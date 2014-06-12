package psopv.taskplanner.config;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;

/**
 * Represents a time period where no events can be planned.
 *
 * @since May 30, 2014 6:20:13 PM
 * @author Martijn Theunissen
 */
public class UnplannablePeriod {

	private LocalTime	m_start;
	private LocalTime	m_end;
	private String		m_repeat	= "";	// format '1234567', '3215', '235471' each number representing a day of the week

	/**
	 * create an unplannable period where no events should be planned
	 * @param start the start of the period
	 * @param end the end of the period
	 * @param repeat which days to repeat. A string of ints (1 up to 7 inclusive) representing the day of the week. (eg '12467')
	 */
	public UnplannablePeriod(LocalTime start, LocalTime end, String repeat) {
		m_start = start;
		m_end = end;
		m_repeat = repeat;
	}

	/**
	 * @return the duration of the period
	 */
	public Duration getDuration() {
		return Duration.between(m_start, m_end);
	}

	/**
	 * @return the start local time
	 */
	public LocalTime getStart() {
		return m_start;
	}

	/**
	 * @return the end local time
	 */
	public LocalTime getEnd() {
		return m_end;
	}

	/**
	 * Calculate if a day is repeated
	 * @param day number representing WeekDay (range 1 to 7)
	 * @return true if the day is repeated, false otherwide
	 */
	public boolean isDayRepeated(int day) {
		return (m_repeat.indexOf(("" + day)) != -1);
	}

	/**
	 * String representation of this object
	 */
	public String toString() {
		String print = ApplicationSettings.getInstance().getLocalizedMessage("from") + " " + m_start.format(DateTimeFormatter.ISO_LOCAL_TIME) + " " + ApplicationSettings.getInstance().getLocalizedMessage("to") + " " + m_end.format(DateTimeFormatter.ISO_LOCAL_TIME);
		print += "    " + ApplicationSettings.getInstance().getLocalizedMessage("repeating") + " ";
		for (int i = 1; i <= 7; i++) {
			if (m_repeat.indexOf(("" + i)) != -1) // contains day
			{
				print += DayOfWeek.of(i).getDisplayName(TextStyle.SHORT, ApplicationSettings.getInstance().getLocale()).toLowerCase() + " ";
			}
		}
		return print;
	}

	/**
	 * @return Encoded string for parsing
	 */
	public String encode() {
		return m_start.format(DateTimeFormatter.ISO_LOCAL_TIME) + "|" + m_end.format(DateTimeFormatter.ISO_LOCAL_TIME) + "|" + m_repeat;
	}

	/**
	 * Parse an encoded string 
	 * @param period the representation of an UnplannablePeriod object
	 * @return a parsed unplannableperiod object
	 */
	public static UnplannablePeriod parse(String period) {
		int divider = period.indexOf('|');
		String start = period.substring(0, divider);
		String other = period.substring(divider + 1);
		String end = other.substring(0, other.indexOf('|')); // leave 2 characters behind
		String repeat = other.substring(other.indexOf('|') + 1);

		return new UnplannablePeriod(LocalTime.parse(start, DateTimeFormatter.ISO_LOCAL_TIME), LocalTime.parse(end, DateTimeFormatter.ISO_LOCAL_TIME), repeat);
	}
}
