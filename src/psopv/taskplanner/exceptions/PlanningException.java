package psopv.taskplanner.exceptions;

import psopv.taskplanner.models.Calendar;
import psopv.taskplanner.models.TaskList;

/**
 * 
 * This error gets thrown when a task could not be planned by the taskplanner
 *
 * @since Apr 14, 2014 8:02:04 PM
 * @author Martijn Theunissen
 */
public class PlanningException extends Exception {

	/**
	 * Error description message
	 */
	private final String	m_message;

	/**
	 * The failed task to plan
	 */
	private final TaskList	m_tasks;

	/**
	 * The calendar with planned items
	 */
	private final Calendar	m_plannedCalendar;

	/**
	 * initialize the exception
	 * @param plannedCalendar the calendar of tasks that are planned
	 * @param tasks the tasks that failed to plan
	 * @param message error message
	 */
	public PlanningException(Calendar plannedCalendar, TaskList tasks, String message) {
		m_plannedCalendar = plannedCalendar;
		m_tasks = tasks;
		m_message = message;
	}

	/**
	 * @return the calendar with items that are planned
	 */
	public Calendar getPlannedCalendar() {
		return m_plannedCalendar;
	}

	/**
	 * @return the error description message
	 */
	public String getMessage() {
		return m_message;
	}

	/**
	 * @return the failed task
	 */
	public TaskList getFailedTasks() {
		return m_tasks;
	}

}
