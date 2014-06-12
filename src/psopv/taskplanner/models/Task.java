package psopv.taskplanner.models;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Observable;

/**
 * 
 * This class represents a task that needs to be done.
 *
 * @since Mar 12, 2014 8:22:18 PM
 * @author Martijn Theunissen
 */
public class Task extends Observable {

	private ZonedDateTime	m_deadline;
	private Duration		m_duration;
	private String			m_id;
	private String			m_name;
	private String			m_description;
	private int				m_priority;
	private static int		m_idcounter	= 0;
	private boolean			m_removeMe;

	/**
	 * Constructor for creating a task.
	 * @param id the id of a task.
	 * @param name the name of the task
	 * @param deadline the deadline of the task.
	 * @param duration how long the task takes
	 * @param priority the priority of the task
	 * @param description string about the task
	 */
	public Task(String id, String name, ZonedDateTime deadline, Duration duration, int priority, String description) {
		if (id == null || id.isEmpty())
			id = "" + ++m_idcounter;
		m_deadline = deadline;
		m_duration = duration;
		m_id = id;
		m_name = name;
		m_description = description;
		m_priority = priority;
		m_removeMe = false;
	}

	/**
	 * Copy constructor for copying a task.
	 * @param copy the task to copy over.
	 */
	public Task(Task copy) {
		m_deadline = copy.getDeadline();
		m_duration = copy.getDuration();
		m_id = copy.getId();
		m_description = copy.getDescription();
		m_priority = copy.getPriority();
		m_name = copy.getName();
	}

	/**
	 * Copy tasks from another task
	 * @param copy the task to copy over
	 */
	public void copyFrom(final Task copy) {
		m_deadline = copy.getDeadline();
		m_duration = copy.getDuration();
		m_id = copy.getId();
		m_description = copy.getDescription();
		m_priority = copy.getPriority();
		m_name = copy.getName();
		setChanged();
		notifyObservers();

	}

	public void remove() {
		m_removeMe = true;
		setChanged();
		notifyObservers();
	}

	public boolean isRemoved() {
		return m_removeMe;
	}

	/**
	 * Default constructor with default values. (+ 3 days, 2hr duration, priority = 3)
	 */
	public Task() {

		m_deadline = ZonedDateTime.now().plusDays(3);
		m_duration = Duration.ofDays(2);
		m_id = ++m_idcounter + "";
		m_description = "";
		m_priority = 3;
		m_name = "";
	}

	/**
	 * Set the deadline of a task by string representation.
	 * @param datetime the date in string representation.
	 * @param format the format of the datetime string. A pattern like "MMM dd, yyyy - HH:mm"
	 * @throws DateTimeParseException when the string cannot be parsed correctly.
	 */
	public void setDeadline(String datetime, String format) throws DateTimeParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		m_deadline = ZonedDateTime.parse(datetime, formatter);
		setChanged();
		notifyObservers();
	}

	/**
	 * Set the deadline of the task.
	 * @param deadline the deadline to set.
	 */
	public void setDeadline(ZonedDateTime deadline) {
		m_deadline = deadline;
		setChanged();
		notifyObservers();
	}

	/**
	 * Set the priority of a task.
	 * @param priority taskpriority
	 */
	public void setPriority(int priority) {
		m_priority = priority;
		setChanged();
		notifyObservers();
	}

	/**
	 * Get the priority of the task
	 * @return the priority of the task
	 */
	public int getPriority() {
		return m_priority;
	}

	/**
	 * Set the id of the task
	 * @param id the id to set
	 */
	public void setId(String id) {
		m_id = id;
		setChanged();
		notifyObservers();
	}

	/**
	 * Get the id of the task
	 * @return the id in string representation
	 */
	public String getId() {
		return m_id;
	}

	/**
	 * Set the task description
	 * @param desc the description in string format
	 */
	public void setDescription(String desc) {
		m_description = desc;
		setChanged();
		notifyObservers();
	}

	/**
	 * Get the description of the task
	 * @return the description in string format
	 */
	public String getDescription() {
		return m_description;
	}

	/**
	 * Set the duration of a task
	 * @param duration how long the task takes.
	 */
	public void setDuration(Duration duration) {
		m_duration = duration;
		setChanged();
		notifyObservers();
	}

	/**
	 * Get the duration of a task.
	 * @return how long it takes for the task to complete.
	 */
	public Duration getDuration() {
		return m_duration;
	}

	/**
	 * Get the deadline of the task.
	 * @return the deadline.
	 */
	public ZonedDateTime getDeadline() {
		return m_deadline;
	}

	/**
	 * Get the name of the task.
	 * @return the name of the task.
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * Set the name of the task
	 * @param name the new name.
	 */
	public void setName(String name) {
		m_name = name;
		setChanged();
		notifyObservers();
	}

}
