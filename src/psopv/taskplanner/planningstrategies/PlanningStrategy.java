/**
 * 
 */
package psopv.taskplanner.planningstrategies;

import java.util.ArrayList;

import psopv.taskplanner.exceptions.PlanningException;
import psopv.taskplanner.models.Calendar;
import psopv.taskplanner.models.TaskList;

/**
 * 
 * $Rev:: 136                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-03 01:03:28 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This interface defines a PlanningStrategy. 
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version<br>
 * 2 - martijn.theunissen: modified interface to multiple calendars<br>
 * 3 - <br>
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
