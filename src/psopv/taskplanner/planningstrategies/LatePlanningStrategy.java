/**
 * 
 */
package psopv.taskplanner.planningstrategies;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.exceptions.PlanningException;
import psopv.taskplanner.models.Calendar;
import psopv.taskplanner.models.CalendarItem;
import psopv.taskplanner.models.FreeTimeContainer;
import psopv.taskplanner.models.Task;
import psopv.taskplanner.models.TaskList;
import psopv.taskplanner.util.PlanningUtil;

/**
 * 
 * $Rev:: 138                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-03 13:16:15 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * Planning strategy for planning tasks as late as possible
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version<br>
 * 2 - martijn.theunissen: using PlanningUtil class<br>
 * 3 - <br>
 *
 * @since May 30, 2014 3:16:40 PM
 * @author martijn
 */
public class LatePlanningStrategy implements PlanningStrategy {

	private final static Logger	log				= LogManager.getLogger(ASAPPlanningStrategy.class);
	private int					totalFinished	= 0;
	private int					totalTasks		= 1;
	private int					totalFailed		= 0;
	private boolean				isDone			= false;
	private boolean				errorOccured	= false;
	private String				failmessage		= "";

	/**
	 * Initialize the statistic bookkeeping of the planning
	 * @param list the tasks to plan
	 */
	private void initStats(TaskList list) {
		failmessage = "";
		totalFinished = 0;
		totalTasks = list.getSize();
		totalFailed = 0;
		isDone = false;
		errorOccured = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see psopv.taskplanner.planningstrategies.PlanningStrategy#plan(java.util.ArrayList,
	 * psopv.taskplanner.models.TaskList)
	 */
	@Override
	public Calendar plan(ArrayList<Calendar> calendars, TaskList tasklist, String calendarName) throws PlanningException {

		ZonedDateTime last = tasklist.getLastDeadline();
		ZonedDateTime now = ZonedDateTime.now();
		Calendar merged = PlanningUtil.mergeCalendars(ApplicationSettings.getInstance().getLocalizedMessage("strat.merge") + ": " + calendarName, calendars, now, last);
		Calendar plannedCalendar = new Calendar(calendarName, null);
		TaskList fail = new TaskList(calendarName + ApplicationSettings.getInstance().getLocalizedMessage("strat.fail"));
		ArrayList<FreeTimeContainer> freeTime = PlanningUtil.manageFTCExceptions(PlanningUtil.getPossibleTimeSlots(merged, tasklist));

		initStats(tasklist);

		Comparator<Task> comp = new Comparator<Task>() {
			public int compare(Task t, Task s) {
				int c = s.getDuration().compareTo(t.getDuration());

				if (c != 0) {
					return c;
				} else {
					Integer tp = t.getPriority();
					Integer sp = s.getPriority();
					return tp.compareTo(sp);
				}

			}
		};

		tasklist.sort(comp);

		freeTime.sort((a, b) -> {
			return a.getBeginZDT().compareTo(b.getBeginZDT());
		});

		for (int i = 0; i < tasklist.getSize(); i++) {
			Task toPlan = tasklist.getTask(i);
			Predicate<FreeTimeContainer> deadlineOK = p -> p.getEndZDT().isBefore(toPlan.getDeadline()) && p.getBeginZDT().isBefore(toPlan.getDeadline());

			// Filter the ones where the deadline is not satisfied
			ArrayList<FreeTimeContainer> filteredCopy = (ArrayList<FreeTimeContainer>) freeTime.stream().filter(deadlineOK).sorted((l1, l2) -> {
				return l2.getBeginZDT().compareTo(l1.getBeginZDT());
			}).collect(Collectors.toList());

			try {
				FreeTimeContainer best = getBestContainer(toPlan, filteredCopy);
				best.addTask(toPlan);
				log.info("Planned " + best);
			} catch (PlanningException pe) {
				fail.addTask(toPlan);
				errorOccured = true;
				totalFailed++;
				failmessage = pe.getMessage();
			}
			totalFinished++;
		}

		if (errorOccured) {
			planAllTimeSlots(plannedCalendar, freeTime);
			isDone = true;
			throw new PlanningException(plannedCalendar, fail, failmessage);
		}

		planAllTimeSlots(plannedCalendar, freeTime);
		isDone = true;
		return plannedCalendar;
	}

	/**
	 * Get the best container possible to plan a task in
	 * @param toPlan the task to plan
	 * @param possibilities the timecontainers to be planned in
	 * @return the best container possible
	 * @throws PlanningException when there is no best container to be found
	 */
	private FreeTimeContainer getBestContainer(Task toPlan, ArrayList<FreeTimeContainer> possibilities) throws PlanningException {
		FreeTimeContainer best = null;
		int index = -1;

		if (possibilities != null && possibilities.size() > 0) {
			index = getFirstPlannableContainerIndex(toPlan, possibilities);
			best = possibilities.get(index);
		} else
			throw new PlanningException(null, null, ApplicationSettings.getInstance().getLocalizedMessage("strat.noftc"));

		return best; // First is best :D
	}

	/**
	 * Get the first plannable container index
	 * @param toPlan the task to plan
	 * @param possibilities the possibilities to plan in
	 * @return the first plannable container index
	 * @throws PlanningException if there are no plannable containers
	 */
	private int getFirstPlannableContainerIndex(Task toPlan, ArrayList<FreeTimeContainer> possibilities) throws PlanningException {
		for (int i = 0; i < possibilities.size(); i++) {
			Duration newDuration = possibilities.get(i).getDurationLeft().minus(toPlan.getDuration());
			if (!newDuration.isNegative()) // found a better match
			{
				return i;
			}
		}
		throw new PlanningException(null, null, ApplicationSettings.getInstance().getLocalizedMessage("strat.noftc"));
	}

	/**
	 * Plan every timeslot calculated in the calendar
	 * @param calendar the calendar to plan in
	 * @param plannedContainers the planned containers
	 */
	private void planAllTimeSlots(Calendar calendar, ArrayList<FreeTimeContainer> plannedContainers) {
		for (FreeTimeContainer plan : plannedContainers) {
			// TODO: equally divide tasks over timeslot
			TaskList tasks = plan.getTasksPlanned();
			Duration buildUp = Duration.ZERO;
			for (int i = 0; i < tasks.getSize(); i++) {
				Task t = tasks.getTask(i);
				CalendarItem item = new CalendarItem(t.getName(), t.getDescription(), plan.getBeginZDT().plus(buildUp), t.getDuration());
				calendar.addCalendarItem(item);
				buildUp = buildUp.plus(t.getDuration());
			}
		}
	}

	/**
	 * Get the progress of the planning
	 */
	public synchronized double getProgress() {
		return (double) totalFinished / (double) totalTasks;
	}

	/**
	 * @return true if the planning is done.
	 */
	public synchronized boolean isDone() {
		return isDone;
	}

	/**
	 * @return the amount of tasks failed in currently
	 */
	public synchronized int getFailedTasks() {
		return totalFailed;
	}

	public String toString() {
		return ApplicationSettings.getInstance().getLocalizedMessage("plan.late");
	}

}
