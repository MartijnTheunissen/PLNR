package psopv.taskplanner.util;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.config.UnplannablePeriod;
import psopv.taskplanner.models.Calendar;
import psopv.taskplanner.models.CalendarItem;
import psopv.taskplanner.models.FreeTimeContainer;
import psopv.taskplanner.models.TaskList;

/**
 * 
 * Planning utility class
 *
 * @since Jun 1, 2014 4:09:43 PM
 * @author Martijn Theunissen
 */
public class PlanningUtil {

	/**
	 * Get all possible timeslots to plan in
	 * @param calendar the calendar to avoid (get timeslots between events in these calendars)
	 * @param tasklist the tasks to be planned (to know what the last deadline is)
	 * @return a list of free time slots available to plan in
	 */
	public static ArrayList<FreeTimeContainer> getPossibleTimeSlots(Calendar calendar, TaskList tasklist) {
		ZonedDateTime last = tasklist.getLastDeadline();
		ZonedDateTime now = ZonedDateTime.now();
		ArrayList<FreeTimeContainer> freeTime = calendar.getFTC(now, Duration.between(now, last));

		Predicate<FreeTimeContainer> noWeekend = p -> p.getBeginZDT().getDayOfWeek() != DayOfWeek.SATURDAY && p.getBeginZDT().getDayOfWeek() != DayOfWeek.SUNDAY;
		if (!ApplicationSettings.getInstance().isPlannableOnWeekends())
			freeTime = (ArrayList<FreeTimeContainer>) freeTime.stream().filter(noWeekend).collect(Collectors.toList());

		return freeTime;
	}

	/**
	 * Merge the calendars into one calendar
	 * @param calendarName the name of the original export calendar
	 * @param calendars the calendars to merge
	 * @param start from when to merge
	 * @param end end time to merge
	 * @return the merged calendar
	 */
	public static Calendar mergeCalendars(String calendarName, ArrayList<Calendar> calendars, ZonedDateTime start, ZonedDateTime end) {
		Calendar merged = new Calendar(calendarName, null);
		for (Calendar cal : calendars) {
			ArrayList<CalendarItem> items = cal.getCalendarItems();
			for (CalendarItem item : items)
				merged.addCalendarItem(item);
		}

		return merged;
	}

	/**
	 * Manage exception periods where nothing should be planned
	 * @param freeTime the original free time containers
	 * @return the new free time containers
	 */
	public static ArrayList<FreeTimeContainer> manageFTCExceptions(ArrayList<FreeTimeContainer> freeTime) {
		ArrayList<FreeTimeContainer> list = freeTime;
		for (UnplannablePeriod unplannable : ApplicationSettings.getInstance().getUnplannablePeriods().getList()) {
			list = removeUnplannableTime(list, unplannable);
		}

		return list;
	}

	/**
	 * Remove unplannable time from a list of timecontainers
	 * @param freeTime the original containers
	 * @param unplannable the time to remove
	 * @return the new timecontainers
	 */
	private static ArrayList<FreeTimeContainer> removeUnplannableTime(ArrayList<FreeTimeContainer> freeTime, UnplannablePeriod unplannable) {
		ArrayList<FreeTimeContainer> list = new ArrayList<FreeTimeContainer>();
		for (FreeTimeContainer ftc : freeTime) {
			if (unplannable.isDayRepeated(ftc.getBeginZDT().getDayOfWeek().getValue())) {
				ArrayList<FreeTimeContainer> split = ftc.removeTimeSlice(ftc.getBeginZDT().with(unplannable.getStart()), ftc.getEndZDT().with(unplannable.getEnd()));
				for (FreeTimeContainer a : split)
					list.add(a);
			} else
				list.add(ftc);
		}
		return list;
	}

}
