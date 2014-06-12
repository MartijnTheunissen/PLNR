package psopv.taskplanner.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.TzId;
import psopv.taskplanner.models.CalendarItem;

/**
 * 
 * This class helps getting basic data out of a VEvent class.
 *
 * @since Apr 7, 2014 8:52:00 PM
 * @author Martijn Theunissen
 */
public class VEventHelper {

	/**
	 * Prevent instantiation
	 */
	private VEventHelper() {
	}

	/**
	 * Get the StartZonedDateTime of an event
	 * @param event the event to get the start-ZonedDateTime of
	 * @return the start-ZonedDateTime of the event
	 */
	public static ZonedDateTime getStartZonedDateTime(VEvent event) {
		DtStart datestart = (DtStart) event.getProperty(Property.DTSTART);

		if (datestart.isUtc()) {
			return ZonedDateTime.parse(datestart.getValue(), DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX")).withZoneSameInstant(ZoneId.systemDefault());
		}
		LocalDateTime localdt;
		TzId id = (TzId) event.getProperties().getProperty(Property.TZID);

		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
			localdt = LocalDateTime.parse(datestart.getValue(), dtf);
		} catch (DateTimeParseException dtpe) {
			return ZonedDateTime.parse(datestart.getDate().toInstant().toString());
			//return ZonedDateTime.ofInstant(datestart.getDate().toInstant(), ZoneId.of(id.getValue())).withZoneSameInstant(ZoneId.systemDefault());
		}

		return localdt.atZone(ZoneId.of(id.getValue())).withZoneSameInstant(ZoneId.systemDefault());
	}

	/**
	 * Get the End-ZonedDateTime of an event
	 * @param event the event to get the end-ZonedDateTime of
	 * @return the end-ZonedDateTime
	 */
	public static ZonedDateTime getEndZonedDateTime(VEvent event) {
		DtEnd dateEnd = (DtEnd) event.getProperty(Property.DTEND);

		if (dateEnd.isUtc()) {
			return ZonedDateTime.parse(dateEnd.getValue(), DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX")).withZoneSameInstant(ZoneId.systemDefault());
		}

		LocalDateTime localdt;
		TzId id = (TzId) event.getProperties().getProperty(Property.TZID);

		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
			localdt = LocalDateTime.parse(dateEnd.getValue(), dtf);
		} catch (DateTimeParseException dtpe) {
			return ZonedDateTime.parse(dateEnd.getDate().toInstant().toString());
		}

		return localdt.atZone(ZoneId.of(id.getValue())).withZoneSameInstant(ZoneId.systemDefault());
	}

	/**
	 * Get the summary of the event
	 * @param event the event to get the summary of
	 * @return the summary string of the event
	 */
	public static String getEventSummary(VEvent event) {
		return event.getSummary().toString().replaceFirst("SUMMARY:", "").replaceAll("\\\\", "").trim();
	}

	/**
	 * Get the event description string of the event
	 * @param event the event to get the description of
	 * @return the description of the vevent
	 */
	public static String getEventDescription(VEvent event) {
		if (event.getDescription() == null)
			return "";
		return event.getDescription().toString().replaceFirst("DESCRIPTION:", "").replaceAll("\\\\", "").trim();
	}

	/**
	 * Converta  a duration from java.time format to ical4j format
	 * @param duration the duration in java.time format
	 * @return the duration in ical4j dur format
	 */
	public static Dur durationToICALDur(Duration duration) {
		long days = duration.toDays(), hrs, mins;
		if (days > 0) {
			hrs = (int) duration.toHours() - days * 24;
			if (hrs > 0)
				mins = duration.toMinutes() - (days * 60 * 24 + hrs * 60);
			else
				mins = duration.toMinutes() - (days * 60 * 24);
		} else {
			hrs = (int) duration.toHours();
			if (hrs > 0)
				mins = duration.toMinutes() - hrs * 60;
			else
				mins = duration.toMinutes();
		}
		return new Dur((int) days, (int) hrs, (int) mins, 0);
	}

	/**
	 * Make a calendar item from a vevent
	 * @param event the VEvent to convert
	 * @return the converted calendaritem
	 */
	public static CalendarItem makeCalendarItem(VEvent event) {
		String summary = getEventSummary(event);
		String desc = getEventDescription(event);
		ZonedDateTime start = getStartZonedDateTime(event);
		ZonedDateTime end = getEndZonedDateTime(event);
		return new CalendarItem(summary, desc, start, Duration.between(start, end));
	}
}
