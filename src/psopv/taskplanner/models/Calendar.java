/**
 * 
 */
package psopv.taskplanner.models;

import java.awt.Color;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.util.VEventHelper;

/**
 * 
 * $Rev:: 152                                                  $:  Revision of last commit<br>
 * $Author:: tom.knaepen                                       $:  Author of last commit<br>
 * $Date:: 2014-06-05 16:17:17 +0200 (Thu, 05 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This class represents a calendar in the application.
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: Initial placeholder version<br>
 * 2 - martijn.theunissen: Added getEvents() between starttime and duration<br>
 * 3 - martijn.theunissen: getEvents() duration bugfix<br>
 * 4 - tom.knaepen: now a list of CalendarItems instead of VEvents
 *
 * @since Mar 14, 2014 8:57:37 PM
 * @author Martijn Theunissen
 */
public class Calendar extends Observable implements Observer {

	private ApplicationSettings					m_appSettings;
	private net.fortuna.ical4j.model.Calendar	m_icalCalendar;
	private ArrayList<CalendarItem>				m_items;
	private String								m_name;
	private boolean								m_active	= true;
	private final static Logger					log			= LogManager.getLogger(Calendar.class);
	private Color								m_color;
	private final Color							m_colorMix	= new Color(255, 255, 255);

	/**
	 * Create the calendar.
	 * @param name the name of the calendar
	 * @param ical the ical calendar to hold
	 */
	public Calendar(String name, net.fortuna.ical4j.model.Calendar ical) {
		m_items = new ArrayList<CalendarItem>();
		if (ical != null) {
			m_icalCalendar = ical;
			ComponentList clist = m_icalCalendar.getComponents(Component.VEVENT);
			for (Object comp : clist) {
				VEvent event = (VEvent) comp;
				CalendarItem item = VEventHelper.makeCalendarItem(event);
				m_items.add(item);
				item.addObserver(this);
			}
		} else {
			m_icalCalendar = new net.fortuna.ical4j.model.Calendar();
		}
		m_name = name;
		m_appSettings = ApplicationSettings.getInstance();
		m_color = getRandomColor(this.toString());
	}

	/**
	 * @return if the calendar is active 
	 */
	public boolean isActive() {
		return m_active;
	}

	/**
	 * set the calendar to an active or inactive state
	 * @param active  active/inactive state
	 */
	public void setActive(boolean active) {
		if (active == m_active)
			return;
		m_active = active;
	}

	public net.fortuna.ical4j.model.Calendar getICAL4JCalendar() {
		return m_icalCalendar;
	}

	/**
	 * add A calendarItem to the calendar.
	 * @param item the item to add.
	 */
	public void addCalendarItem(CalendarItem item) {
		CalendarItemToICalConverter citicc = new CalendarItemToICalConverter();
		m_items.add(item);
		item.addObserver(this);
		m_icalCalendar.getComponents().add(citicc.convert(item));
	}

	/**
	 * TODO: why bother removing from m_icalCalendar, never used
	 * @param item the item to remove
	 */
	public void removeCalendarItem(CalendarItem item) {
		CalendarItemToICalConverter citicc = new CalendarItemToICalConverter();
		m_items.remove(item);
		m_icalCalendar.getComponents().remove(citicc.convert(item));
		ComponentList events = m_icalCalendar.getComponents();
		@SuppressWarnings("unchecked")
		Iterator<VEvent> it = events.iterator();
		while (it.hasNext()) {
			VEvent event = it.next();
			String summary = VEventHelper.getEventSummary(event).trim();
			String description = VEventHelper.getEventSummary(event).trim();
			if (item.getSummary().trim().equals(summary) && item.getDescription().trim().equals(description)) // match found.
				m_icalCalendar.getComponents().remove(event);
		}
	}

	/**
	 * Get a list of events on the calendar between a certain timeslot
	 * @param start the zoned start time of the timeslot
	 * @param duration the length/duration of the timeslot
	 * @return a list of vevents
	 */
	@Deprecated
	public ArrayList<VEvent> getEvents(ZonedDateTime start, Duration duration) {
		GregorianCalendar day = GregorianCalendar.from(start);

		long days = duration.toDays();
		long hrs = days * 24 - (int) duration.toHours();
		long mins = +days * 60 * 24 - hrs * 60 - duration.toMinutes();

		Period period = new Period(new DateTime(day.getTime()), VEventHelper.durationToICALDur(duration));
		Filter filter = new Filter(new PeriodRule(period));

		ArrayList<VEvent> events = (ArrayList<VEvent>) filter.filter(m_icalCalendar.getComponents(Component.VEVENT));
		return events;
	}

	/**
	 * Returns a list of items in this calendar that (at least partially) fall within the given period.
	 * @param start start zdt 
	 * @param duration length of period
	 * @return all calendar items within given period
	 */
	public ArrayList<CalendarItem> getCalendarItems(ZonedDateTime start, Duration duration) {
		ArrayList<CalendarItem> results = new ArrayList<CalendarItem>();
		ZonedDateTime end = start.plus(duration);
		for (CalendarItem item : m_items)
			if (item.getStartDate().isBefore(end) && item.getEndDate().isAfter(start))
				results.add(item);
		return results;
	}

	public ArrayList<CalendarItem> getCalendarItems(LocalDate start, long days) {
		ZonedDateTime startzdt = start.atStartOfDay(m_appSettings.getTimeZone().toZoneId());
		Duration dur = Duration.ofDays(days);
		return getCalendarItems(startzdt, dur);
	}

	public ArrayList<CalendarItem> getCalendarItems() {
		return m_items;
	}

	/**
	 * Replace event in the calendar based on summary and description (trimmed)
	 * @param old the item to replace
	 * @param item the calendar item to reinsert
	 * @return true if it was found and replaced, false otherwise
	 */
	@Deprecated
	public boolean replaceEvent(CalendarItem old, CalendarItem item) {
		ArrayList<VEvent> events = getEvents(old.getStartDate().minusHours(2), old.getDuration().plusHours(4));
		for (VEvent event : events) {
			String summary = VEventHelper.getEventSummary(event).trim();
			String description = VEventHelper.getEventSummary(event).trim();
			CalendarItemToICalConverter conv = new CalendarItemToICalConverter();
			if (old.getSummary().trim().equals(summary) && old.getDescription().trim().equals(description)) // match found.
			{
				m_icalCalendar.getComponents().remove(event);
				m_icalCalendar.getComponents().add(conv.convert(item));
				return true;
			}

		}
		return false;
	}

	/**
	 * Get the containers of free time where there could be planned in between the calendar.
	 * @param start the start ZDT to check
	 * @param duration how long the period is to check
	 * @return a list of calendarevents
	 */
	@Deprecated
	public ArrayList<FreeTimeContainer> getFreeTimeContainersBetween(ZonedDateTime start, Duration duration) {
		// truncatedTo() to compare ZonedDateTimes properly
		ArrayList<FreeTimeContainer> freeTime = new ArrayList<FreeTimeContainer>();
		ArrayList<VEvent> occupied = getEvents(start, duration);
		ZoneId timezone = ApplicationSettings.getInstance().getTimeZone().toZoneId();
		LocalTime startDayTime = m_appSettings.getStartPlanningTime().truncatedTo(ChronoUnit.MINUTES);
		LocalTime endDayTime = m_appSettings.getEndPlanningTime().truncatedTo(ChronoUnit.MINUTES);
		ZonedDateTime endTime = start.plus(duration).truncatedTo(ChronoUnit.MINUTES);
		if (start.isBefore(start.with(startDayTime))) // before the start of the day
			start = start.with(startDayTime);

		if (start.isAfter(start.with(endDayTime))) // if we start planning after end of day, start planning the next day
			start = start.plusDays(1).with(startDayTime);
		for (int i = 0; i < occupied.size(); i++) {
			ZonedDateTime eventStart = VEventHelper.getStartZonedDateTime(occupied.get(i)).truncatedTo(ChronoUnit.MINUTES);
			ZonedDateTime eventEnd = VEventHelper.getEndZonedDateTime(occupied.get(i)).truncatedTo(ChronoUnit.MINUTES);

			if (eventStart.isAfter(start)) // free time between start and event !
				// but event starts later than end of day
				if (eventStart.isAfter(ZonedDateTime.of(start.toLocalDate(), endDayTime, timezone))) {

					// so we create a free time container from start until end of day
					freeTime.add(new FreeTimeContainer(start, ZonedDateTime.of(start.toLocalDate(), endDayTime, timezone)));
					// and set start to the start of next day
					start = ZonedDateTime.of(start.toLocalDate().plusDays(1), startDayTime, timezone);
					// and continue because we just set start manually anyway
					i--;
					continue;
				} else {
					freeTime.add(new FreeTimeContainer(start, eventStart));
					start = eventEnd;
				}

			if (eventStart.isEqual(start)) {
				start = eventEnd;
				continue;
			}

			// if event ends after end of day, new potential FTC starts at the next day
			if (eventEnd.isBefore(ZonedDateTime.of(eventEnd.toLocalDate(), startDayTime, timezone)))
				start = eventEnd;
			else {
				FreeTimeContainer ftc = new FreeTimeContainer(eventEnd, ZonedDateTime.of(eventEnd.toLocalDate(), endDayTime, timezone));
				if (!ftc.getDurationLeft().isZero() && !ftc.getDurationLeft().isNegative())
					freeTime.add(ftc);
				start = ZonedDateTime.of(eventStart.toLocalDate(), startDayTime, timezone).plusDays(1);
			}
		}

		// no more events, but might still be some days left
		while (start.isBefore(endTime)) {
			ZonedDateTime endOfDay = ZonedDateTime.of(start.toLocalDate(), endDayTime, timezone);
			if (endOfDay.isAfter(endTime)) {
				freeTime.add(new FreeTimeContainer(start, endTime));
				start = endTime;
			} else {
				freeTime.add(new FreeTimeContainer(start, endOfDay));
				start = endOfDay.plusDays(1).with(startDayTime);
			}
		}

		return freeTime;
	}

	/**
	 * Private helper class, represents a start or end time of an event. Used for retrieving FreeTimeContainers with getFTC().
	 */
	private class ZDT {
		boolean			isStart;
		ZonedDateTime	zdt;

		public ZDT(ZonedDateTime zdt, boolean isStart) {
			this.isStart = isStart;
			this.zdt = zdt;
		}

		public String toString() {
			return zdt.toString() + " - " + isStart;
		}
	}

	/**
	 * Alternative method of retrieving <i>FreeTimeContainer</i>s
	 * @param startTime start time
	 * @param duration length
	 * @return all the free time containers
	 */
	public ArrayList<FreeTimeContainer> getFTC(ZonedDateTime startTime, Duration duration) {
		// init
		startTime = startTime.truncatedTo(ChronoUnit.MINUTES); // get rid of unnecessary seconds/millis
		ArrayList<FreeTimeContainer> freeTime = new ArrayList<FreeTimeContainer>();
		ArrayList<ZDT> markers = new ArrayList<ZDT>();
		ArrayList<CalendarItem> events = getCalendarItems(startTime, duration);
		ZonedDateTime startOfDay = startTime.with(m_appSettings.getStartPlanningTime().truncatedTo(ChronoUnit.MINUTES));
		ZonedDateTime endOfDay = startTime.with(m_appSettings.getEndPlanningTime().truncatedTo(ChronoUnit.MINUTES));
		ZonedDateTime endOfPeriod = startTime.plus(duration);

		// add all event times
		for (CalendarItem event : events) {
			ZonedDateTime eventStart = event.getStartDate();
			ZonedDateTime eventEnd = event.getEndDate();
			markers.add(new ZDT(eventStart, true));
			markers.add(new ZDT(eventEnd, false));
		}

		// add all options - planning on weekends, planning before/after hours
		while (startOfDay.isBefore(endOfPeriod)) {
			if (startOfDay.isAfter(startTime))
				markers.add(new ZDT(startOfDay, false));
			else
				markers.add(new ZDT(startTime, false));

			if (endOfDay.isAfter(startTime) && endOfDay.isBefore(endOfPeriod))
				markers.add(new ZDT(endOfDay, true));
			else if (endOfDay.isAfter(startTime))
				markers.add(new ZDT(endOfPeriod, true));

			// increment
			startOfDay = startOfDay.plusDays(1);
			endOfDay = endOfDay.plusDays(1);
		}
		//while(startTime.isBefore(endOfPeriod)) {
		//add(weekend.at(startOfDay));
		//add(weekend.at(endOfDay));
		//}

		// sort all data
		markers.sort(new Comparator<ZDT>() {
			@Override
			public int compare(ZDT arg0, ZDT arg1) {
				if (arg0.zdt.isEqual(arg1.zdt) && arg0.isStart == arg1.isStart)
					return 0;

				if (arg0.zdt.isBefore(arg1.zdt) || (arg0.zdt.isEqual(arg1.zdt) && !arg0.isStart))
					return -1;
				else
					return 1;
			}
		});

		// DEBUG
		//		for (ZDT marker : markers) {
		//			log.info(marker.toString());
		//		}

		// match "brackets"
		// eg every '(' is start of an event, every ')' an end of one
		// since they're chronologic/sorted, every outer ')(' represents a FTC
		// example: ) () (()) (()()) ((
		//           ^  ^    ^      ^   
		ZonedDateTime start = null, end = null;
		for (int i = 0; i < markers.size(); i++) {
			ZDT current = markers.get(i);
			if (start == null) {
				if (!current.isStart)
					start = current.zdt;
			} else {
				if (!current.isStart)
					start = current.zdt;
				else {
					freeTime.add(new FreeTimeContainer(start, current.zdt));
					start = null;
					end = null;
				}
			}
		}

		// DEBUG
		//		for (FreeTimeContainer free : freeTime) {
		//			log.info(free.getBeginZDT().toString() + "\t" + free.getEndZDT().toString());
		//		}

		Iterator<FreeTimeContainer> it = freeTime.iterator();
		while (it.hasNext()) { // remove 0-duration FTCs
			FreeTimeContainer free = it.next();
			if (free.getBeginZDT().truncatedTo(ChronoUnit.MINUTES).isEqual(free.getEndZDT().truncatedTo(ChronoUnit.MINUTES)))
				it.remove();
		}

		return freeTime;
	}

	/**
	 * @return the name of the calendar
	 */
	public String getName() {
		return m_name;
	}

	public String toString() {
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		// An item updated, notify TaskPlannerModel
		setChanged();
		notifyObservers(arg0);
	}

	/**
	 * @return the color of the calendar
	 */
	public Color getColor() {
		return m_color;
	}

	/**
	 * Set the color of this calendar
	 * @param color the color of the calendar
	 */
	public void setColor(Color color) {
		setChanged();
		notifyObservers();
	}

	/**
	 * @return a random color that will go well with other random colors by this function
	 */
	private Color getRandomColor(String seed) {
		java.util.Random rand = new java.util.Random(seed.toUpperCase().hashCode());
		int r = rand.nextInt(256);
		int g = rand.nextInt(256);
		int b = rand.nextInt(256);

		if (m_colorMix != null) {
			r = (r + m_colorMix.getRed()) / 2;
			g = (g + m_colorMix.getGreen()) / 2;
			b = (b + m_colorMix.getBlue()) / 2;
		}

		return new Color(r, g, b);
	}

	/**
	 * @param item
	 * @return
	 */
	public boolean contains(CalendarItem item) {
		return m_items.contains(item);
	}

}
