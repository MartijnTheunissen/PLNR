package psopv.taskplanner.controllers;

import java.util.ArrayList;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.models.Calendar;
import psopv.taskplanner.models.CalendarItem;
import psopv.taskplanner.models.Task;
import psopv.taskplanner.models.TaskList;
import psopv.taskplanner.models.TaskPlannerModel;
import psopv.taskplanner.planningstrategies.PlanningStrategy;
import psopv.taskplanner.views.dialogs.PlanningProgressPopup;
import be.uhasselt.oo2.mvc.AbstractController;

import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;

/**
 * 
 * This class represents the main controller of the taskplanner. 
 *
 *
 * @since Mar 12, 2014 8:07:08 PM
 * @author Martijn Theunissen
 */
public class TaskPlannerController extends AbstractController {

	private final static Logger	log	= LogManager.getLogger(TaskPlannerController.class);

	/**
	 * Initialize the taskplannercontroller
	 * @param model the model.
	 */
	public TaskPlannerController(Observable model) {
		super(model);
	}

	/**
	 * Add a new task to the taskplanner
	 * @param newTask the task to add
	 * @param list the list to add the task to
	 */
	public void addTask(TaskList list, Task newTask) {
		((TaskPlannerModel) getModel()).addTask(list, newTask);
	}

	/**
	 * Remove the task from the model of the tasklist
	 * @param list the list to remove task from
	 * @param task the task to remove
	 */
	public void removeTask(TaskList list, Task task) {
		((TaskPlannerModel) getModel()).removeTask(list, task);
	}

	/**
	 * plan the tasks in the calendar
	 * @param calendarName the name of the calendar that is planned
	 * @param list the list of tasks to be planned
	 * @param avoid the list of calendars to avoid
	 * 
	 */
	public void plan(String calendarName, TaskList list, ArrayList<Calendar> avoid) {
		if (list == null || list.getSize() == 0) {
			String warn = ApplicationSettings.getInstance().getLocalizedMessage("tpc.emptytl");
			log.warn(warn);
			NotificationManager.showNotification(warn, NotificationIcon.warning.getIcon());
		} else {
			PlanningProgressPopup popup = new PlanningProgressPopup((TaskPlannerModel) getModel());
			popup.run();
			((TaskPlannerModel) getModel()).plan(calendarName, list, avoid);
		}

	}

	/**
	 * add a calendaritem to a calendar
	 * @param calendar the calendar to add the item to
	 * @param item the item to add
	 */
	public void addCalendarItem(Calendar calendar, CalendarItem item) {
		((TaskPlannerModel) getModel()).addCalendarItem(calendar, item);
	}

	/**
	 * set the planning strategy
	 * @param strategy the strategy to set
	 */
	public void setPlanningStrategy(PlanningStrategy strategy) {
		((TaskPlannerModel) getModel()).setPlanningStrategy(strategy);
	}

	/**
	 * Replace a task in the model with another one
	 * @param old the old task to replace
	 * @param newTask the replacement task
	 */
	public void replaceTask(Task old, Task newTask) {
		((TaskPlannerModel) getModel()).replaceTask(old, newTask);
	}

	/**
	 * add a task list to the model
	 * @param taskList the list to add
	 */
	public void addTaskList(TaskList taskList) {
		((TaskPlannerModel) getModel()).addTaskList(taskList);
	}

}
