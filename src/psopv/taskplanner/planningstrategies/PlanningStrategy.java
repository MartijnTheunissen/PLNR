package psopv.taskplanner.planningstrategies;

import java.util.ArrayList;

import psopv.taskplanner.exceptions.PlanningException;
import psopv.taskplanner.models.Calendar;
import psopv.taskplanner.models.TaskList;

/**
 * 
 * This interface defines a PlanningStrategy. 
 *
 * @since Apr 9, 2014 3:34:21 PM
 * @author Martijn Theunissen
 */
public interface PlanningStrategy {

	/**
	 * Plan a list of tasks such that they do not overlap with events on the calendars.
	 * @param calendars the calendars with events to avoid
	 * @param tasklist the tasks to plan in
	 * @param calendarName the name of the calendar made
	 * @return a new Calendar of the tasks
	 * @throws PlanningException when there is a problem planning
	 */
	Calendar plan(ArrayList<Calendar> calendars, TaskList tasklist, String calendarName) throws PlanningException;

	double getProgress();

	int getFailedTasks();

	boolean isDone();

}
