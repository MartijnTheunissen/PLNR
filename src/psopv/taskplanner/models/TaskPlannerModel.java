package psopv.taskplanner.models;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.exceptions.PlanningException;
import psopv.taskplanner.models.TaskPlannerUpdate.TaskPlannerChange;
import psopv.taskplanner.planningstrategies.ASAPPlanningStrategy;
import psopv.taskplanner.planningstrategies.LatePlanningStrategy;
import psopv.taskplanner.planningstrategies.PlanningStrategy;
import psopv.taskplanner.planningstrategies.SpreadedPlanningStrategy;

import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;

/**
 * 
 * 
 * $Rev:: 152                                                  $:  Revision of last commit<br>
 * $Author:: tom.knaepen                                       $:  Author of last commit<br>
 * $Date:: 2014-06-05 16:17:17 +0200 (Thu, 05 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This class represents the model of the taskplanner. 
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: very early initial placeholder version.<br>
 * 2 - martijn.theunissen: support for tasklist added <br>
 * 3 - martijn.theunissen: support for calendar added<br>
 * 4 - martijn.theunissen: support for adding and removing individual task<br>
 * 4 - martijn.theunissen: support for multiple calendars<br>
 * 5 - martijn.theunissen: support for multiple tasklist.<br>
 * 6 - tom.knaepen: notifying with a TaskPlannerUpdate to determine what changed<br>
 * 7 - tom.knaepen: minor change to TaskPlannerUpdates<br>
 * 8 - martijn.theunissen: support for replacing a task<br>
 * 9  - martijn.theunissen: updated translations<br>
 *
 * @since Mar 12, 2014 8:23:37 PM
 * @author Martijn Theunissen
 */
public class TaskPlannerModel extends Observable implements Observer {

	private ArrayList<TaskList>			m_taskLists;
	private ArrayList<Calendar>			m_calendars;
	private ArrayList<PlanningStrategy>	m_planningStrategies;
	private final static Logger			log			= LogManager.getLogger(TaskPlannerModel.class);
	private PlanningStrategy			m_planningStrategy;
	private ApplicationSettings			m_settings	= ApplicationSettings.getInstance();

	public TaskPlannerModel() {
		m_taskLists = new ArrayList<TaskList>();
		m_taskLists.add(new TaskList(m_settings.getLocalizedMessage("tpm.defaulttl")));
		m_calendars = new ArrayList<Calendar>();
		m_planningStrategy = new SpreadedPlanningStrategy();
		m_planningStrategies = new ArrayList<PlanningStrategy>();
		m_planningStrategies.add(new SpreadedPlanningStrategy());
		m_planningStrategies.add(new ASAPPlanningStrategy());
		m_planningStrategies.add(new LatePlanningStrategy());
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * add a task list
	 * @param list the list to add
	 */
	public void addTaskList(TaskList list) {
		m_taskLists.add(list);
		list.addObserver(this);
		this.setChanged();
		this.notifyObservers(new TaskPlannerUpdate(list, TaskPlannerChange.ADDED));
		log.info(m_settings.getLocalizedMessage("tpm.notifytl") + m_settings.getLocalizedMessage("tpm.added"));
	}

	/**
	 * Remove a task list from the model
	 * @param list the list to remove
	 */
	public void removeTaskList(TaskList list) {
		if (list != null && m_taskLists.remove(list)) {
			this.setChanged();
			this.notifyObservers(new TaskPlannerUpdate(list, TaskPlannerChange.REMOVED));
			log.info(m_settings.getLocalizedMessage("tpm.notifytl") + list + " " + m_settings.getLocalizedMessage("tpm.removed"));
		} else {
			String warning = m_settings.getLocalizedMessage("tpm.tl") + " " + (list != null ? list : "") + " " + m_settings.getLocalizedMessage("tpm.notremoved");
			log.warn(warning);
			NotificationManager.showNotification(warning, NotificationIcon.warning.getIcon());
		}
	}

	/**
	 * Set the calendar of the task planner
	 * @param cal the calendar to add
	 */
	public void addCalendar(Calendar cal) {
		m_calendars.add(cal);
		cal.addObserver(this);
		this.setChanged();
		this.notifyObservers(new TaskPlannerUpdate(cal, TaskPlannerChange.ADDED));

		log.info(m_settings.getLocalizedMessage("tpm.notifycal") + cal + " " + m_settings.getLocalizedMessage("tpm.added"));
	}

	/**
	 * Remove a calendar from the model
	 * @param cal the calendar to remove
	 */
	public void removeCalendar(Calendar cal) {
		if (cal != null && m_calendars.remove(cal)) {
			this.setChanged();
			this.notifyObservers(new TaskPlannerUpdate(cal, TaskPlannerChange.REMOVED));
			log.info(m_settings.getLocalizedMessage("tpm.cal") + " " + cal + m_settings.getLocalizedMessage("tpm.removed"));
		} else {
			String warning = m_settings.getLocalizedMessage("tpm.cal") + " " + (cal != null ? cal : "") + m_settings.getLocalizedMessage("tpm.notremoved");
			log.warn(warning);
			NotificationManager.showNotification(warning, NotificationIcon.warning.getIcon());
		}
	}

	/**
	 * @return the calendar of the taskplannermodel
	 */
	public ArrayList<Calendar> getCalendars() {
		return m_calendars;
	}

	/**
	 * Get the current task list
	 * @return the task list
	 */
	public ArrayList<TaskList> getTaskLists() {
		return m_taskLists;
	}

	/**
	 * Add a new task to the taskplannermodel
	 * @param list the list to add
	 * @param newTask the task to add
	 */
	public void addTask(TaskList list, Task newTask) {
		if (list != null && m_taskLists.contains(list)) {
			list.addTask(newTask);
			log.info(m_settings.getLocalizedMessage("tpm.notifytask") + " " + newTask.getName() + " " + m_settings.getLocalizedMessage("tpm.added"));
			this.setChanged();
			this.notifyObservers();
		} else {
			String warning = m_settings.getLocalizedMessage("tpm.addtaskfail");
			log.warn(warning);
			NotificationManager.showNotification(warning, NotificationIcon.warning.getIcon());
		}
	}

	/**
	 * Set the planning strategy to use
	 * @param strategy the planning strategy to use
	 */
	public void setPlanningStrategy(PlanningStrategy strategy) {
		m_planningStrategy = strategy;
	}

	/**
	 * @return the planning strategy that will be used
	 */
	public PlanningStrategy getPlanningStrategy() {
		return m_planningStrategy;
	}

	/**
	 * Set a calendar to active or inactive
	 * @param cal the calendar to modify
	 * @param active the state to give
	 */
	public void setCalendarActive(Calendar cal, boolean active) {
		if (cal != null && m_calendars.contains(cal)) {
			m_calendars.get(m_calendars.indexOf(cal)).setActive(active);
			this.setChanged();
			this.notifyObservers(new TaskPlannerUpdate(cal, (active ? TaskPlannerChange.VISIBLE : TaskPlannerChange.INVISIBLE)));
			log.info(m_settings.getLocalizedMessage("tpm.cal") + " " + cal + " " + m_settings.getLocalizedMessage("tpm.setto") + (active ? m_settings.getLocalizedMessage("tpm.active") : m_settings.getLocalizedMessage("tpm.inactive")));
		} else
			log.warn(m_settings.getLocalizedMessage("tpm.cal") + " " + (cal != null ? cal : "") + m_settings.getLocalizedMessage("tpm.wasnotfound"));
	}

	/**
	 * Remove a task from the task list of the taskplannermodel
	 * @param list the list to remove from
	 * @param task the task to remove
	 */
	public void removeTask(TaskList list, Task task) {
		if (list != null && m_taskLists.contains(list) && list.contains(task)) {
			list.removeTask(task);
			this.setChanged();
			this.notifyObservers();
		} else {
			// Trying to remove task that does not exist
			String warning = m_settings.getLocalizedMessage("tpm.task") + " " + task.getName() + " " + m_settings.getLocalizedMessage("tpm.notintl");
			log.warn(warning);
			NotificationManager.showNotification(warning, NotificationIcon.warning.getIcon());
			return;
		}

	}

	/**
	 * Plan the task list in the calendars and add the result
	 * @param calendarName the name of the calendar to add
	 * @param list the task list to add
	 * @param avoid the calendars to avoid planning over
	 */
	public void plan(String calendarName, TaskList list, ArrayList<Calendar> avoid) {
		// run planning on another thread.
		new Thread(new Runnable() {
			public void run() {
				try {
					addCalendar(m_planningStrategy.plan(avoid, list, calendarName));
					log.info(m_settings.getLocalizedMessage("tpm.plansucc"));
				} catch (PlanningException e) {
					addCalendar(e.getPlannedCalendar());
					addTaskList(e.getFailedTasks());
					log.error(e);
				}
			}
		}).start();

	}

	/**
	 * add a calendaritem to a calendar.
	 * @param calendar the calendar to add the item to
	 * @param item the item to add to the calendar
	 */
	public void addCalendarItem(Calendar calendar, CalendarItem item) {
		if (calendar != null && m_calendars.contains(calendar))
			calendar.addCalendarItem(item);
		this.setChanged();
		this.notifyObservers(new TaskPlannerUpdate(item, TaskPlannerChange.ADDED));
	}

	/**
	 * Remove a calendar item.
	 * @param item calendar item to remove
	 */
	public void removeCalendarItem(CalendarItem item) {
		for (Calendar cal : m_calendars)
			if (cal.getCalendarItems().contains(item)) {
				cal.removeCalendarItem(item);
			}
		this.setChanged();
		this.notifyObservers(new TaskPlannerUpdate(item, TaskPlannerChange.REMOVED));
	}

	/**
	 * Check if a tasklist is held by the model
	 * @param list the tasklist to check
	 * @return true if it is held, false otherwise or null
	 */
	public boolean containsTaskList(TaskList list) {
		if (list == null)
			return false;
		return m_taskLists.contains(list);
	}

	/**
	 * @return all the possible planning strategies
	 */
	public ArrayList<PlanningStrategy> getPlanningStrategies() {
		return m_planningStrategies;
	}

	/**
	 * Replace a task with another task
	 * @param old the task to replace
	 * @param newTask the task to replace with
	 */
	public void replaceTask(Task old, Task newTask) {
		for (TaskList list : m_taskLists) {
			if (list.contains(old)) {
				list.removeTask(old);
				list.addTask(newTask);
			}
		}
		this.setChanged();
		this.notifyObservers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		// A calendar updated, notify views
		if (arg1 != null && arg1 instanceof CalendarItem) {
			CalendarItem item = (CalendarItem) arg1;
			if (item.isRemoved())
				removeCalendarItem(item);
			else {
				this.setChanged();
				this.notifyObservers(new TaskPlannerUpdate(arg1, TaskPlannerChange.UPDATED));
			}
		} else {
			this.setChanged();
			this.notifyObservers(new TaskPlannerUpdate(arg0, TaskPlannerChange.UPDATED));
		}
	}

}
