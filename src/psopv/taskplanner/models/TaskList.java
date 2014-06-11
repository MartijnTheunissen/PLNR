package psopv.taskplanner.models;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 
 * 
 * $Rev:: 140                                                  $:  Revision of last commit<br>
 * $Author:: tom.knaepen                                       $:  Author of last commit<br>
 * $Date:: 2014-06-03 14:53:39 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This class represents a list of tasks.
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: Initial version<br>
 * 2 - martijn.theunissen: adding getSize() and change getTask() return type.<br>
 * 3 - martijn.theunissen: Added a filter method()<br>
 * 4 - martijn.theunissen: Added a contains() method<br>
 * 5 - martijn.theunissen: private name variable added
 *
 * @since Mar 12, 2014 8:23:03 PM
 * @author Martijn Theunissen
 */
public class TaskList extends Observable implements Observer {

	private java.util.ArrayList<Task>	m_taskList;
	private String						m_name;

	/**
	 * Initialize the list.
	 * @param name of the tasklist
	 */
	public TaskList(String name) {
		m_taskList = new java.util.ArrayList<Task>();
		m_name = name;
	}

	/**
	 * Get all the tasks in the list.
	 * 
	 * @return every task in the list.
	 */
	public java.util.ArrayList<Task> getAllTasks() {
		return m_taskList;
	}

	/**
	 * Add a new task to the list
	 * @param newTask the task to add
	 */
	public void addTask(Task newTask) {
		m_taskList.add(newTask);
		newTask.addObserver(this);
		setChanged();
		notifyObservers();
	}

	/**
	 * Remove every task in the list.
	 */
	public void clear() {
		m_taskList.clear();
		setChanged();
		notifyObservers();
	}

	/**
	 * Remove a task from the list.
	 * @param toRemove the task to delete
	 */
	public void removeTask(Task toRemove) {
		m_taskList.remove(toRemove);
		setChanged();
		notifyObservers();
	}

	/**
	 * Get a task from the list at a certain index
	 * @param index the index of the task.
	 * @return the task
	 */
	public Task getTask(int index) {
		return m_taskList.get(index);
	}

	/**
	 * Check wether a task is in a tasklist
	 * @param task the task to check
	 * @return true if the tasklist contains the task, false otherwise
	 */
	public boolean contains(Task task) {
		return m_taskList.contains(task);
	}

	/**
	 * Get the number of tasks in the list
	 * @return the size of the list
	 */
	public int getSize() {
		return m_taskList.size();
	}

	/**
	 * Get the last possible deadline in the tasklist
	 * @return the last possible deadline in the tasklist. If no tasks are available, return the time right now.
	 */
	public ZonedDateTime getLastDeadline() {
		if (m_taskList.size() == 0)
			return ZonedDateTime.now();
		ZonedDateTime last = m_taskList.get(0).getDeadline();
		for (int i = 1; i < m_taskList.size(); i++) {
			if (m_taskList.get(i).getDeadline().isAfter(last))
				last = m_taskList.get(i).getDeadline();
		}
		return last;
	}

	/**
	 * Add a filter method for the tasks. Provide a predicate (Task t) to boolean Expr to filter the tasks.
	 * @param predicate the filter predicate
	 * @return a list of tasks without the ones filtered.
	 */
	public ArrayList<Task> filterTasks(Predicate<Task> predicate) {
		return m_taskList.parallelStream().filter(predicate).map(Task::new).collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * @return the name of the tasklist
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * String representation of the object: its name.
	 */
	public String toString() {
		return m_name;
	}

	/**
	 * Set the name of the tasklist
	 * @param name the name to set
	 */
	public void setName(String name) {
		m_name = name;
	}

	/**
	 * sort the tasklist
	 * @param c the comparator
	 */
	public void sort(Comparator<? super Task> c) {
		m_taskList.sort(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Task && ((Task) arg0).isRemoved()) {
			removeTask((Task) arg0);
		}
	}

	/**
	 * Get the total duration of all the tasks in the list
	 * @return the total duration
	 */
	public Duration getTotalDuration() {
		Duration total = Duration.ZERO;
		for (Task t : m_taskList)
			total = total.plus(t.getDuration());
		return total;
	}
}
