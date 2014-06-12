package psopv.taskplanner.models;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;

/**
 * 
 * This class converts a CalendarItem to its respective ICAL4J VEVent counterpart
 * 
 * @since Apr 1, 2014 1:18:35 PM
 * @author Martijn Theunissen
 */
public class CalendarItemToICalConverter {

	private final String	TIME_ZONE_STR	= "Europe/Brussels";	// TODO: Localize

	/**
	 * @param item the CalendarItem to change into a vevent
	 * @return a VEVent component that is a representation of the item
	 */
	public Component convert(CalendarItem item) {
		TimeZone timezone = createTimeZone();
		java.util.Calendar startDate = createStartDate(item);
		java.util.Calendar endDate = createEndDate(item);
		VEvent newEvent = createEvent(item, startDate, endDate, timezone);
		newEvent.getProperties().add(timezone.getVTimeZone().getTimeZoneId());

		return newEvent;
	}

	/**
	 * Create a timezone variable to use for the converter
	 * @return a timezone
	 */
	private TimeZone createTimeZone() {
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timezone = registry.getTimeZone(TIME_ZONE_STR);
		return timezone;
	}

	/**
	 * Create a startdate to use in ical
	 * @param item the calendaritem to base on
	 * @return a startdate
	 */
	private java.util.Calendar createStartDate(CalendarItem item) {
		java.util.Calendar startDate;

		ZonedDateTime start = item.getStartDate();
		startDate = GregorianCalendar.from(start);

		return startDate;
	}

	/**
	 * Create a enddate to use in ical
	 * @param item the calendaritem to base on
	 * @return a enddate
	 */
	private java.util.Calendar createEndDate(CalendarItem item) {
		java.util.Calendar enddate;
		ZonedDateTime end = item.getEndDate();
		enddate = GregorianCalendar.from(end);
		return enddate;
	}

	/**
	 * Create the actual event
	 * @param item the item to base on
	 * @param startDate a startdate of the item
	 * @param endDate a enddate of the item
	 * @param tz a timezone of the item
	 * @return a vevent of the calendaritem
	 */
	private VEvent createEvent(CalendarItem item, Calendar startDate, Calendar endDate, TimeZone tz) {
		String summary = item.getSummary();
		DateTime start = new DateTime(startDate.getTime());
		DateTime end = new DateTime(endDate.getTime());
		VEvent event = new VEvent(start, end, summary);
		event.getProperties().add(tz.getVTimeZone().getTimeZoneId());
		event.getProperties().add(new Description(item.getDescription()));
		return event;
	}

}
