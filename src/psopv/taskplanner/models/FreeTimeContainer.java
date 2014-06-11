/**
 * 
 */
package psopv.taskplanner.models;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * 
 * $Rev:: 127                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-05-31 22:55:10 +0200 (Sat, 31 May 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This class represents a container of time where nothing is planned in a calendar
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version<br>
 * 2 - martijn.theunissen: tasklist gets a name<br>
 * 3 - <br>
 *
 * @since Apr 14, 2014 1:17:04 PM
 * @author Martijn Theunissen
 */
public class FreeTimeContainer {

	private ZonedDateTime	m_begin;
	private ZonedDateTime	m_end;
	private TaskList		m_tasksPlanned;
	private Duration		m_durationLeft;

	/**
	 * Create a FreeTimeContainer
	 * @param begin the begin of the ftc
	 * @param end the end zdt of the ftc
	 */
	public FreeTimeContainer(ZonedDateTime begin, ZonedDateTime end) {
		m_begin = begin;
		m_end = end;
		m_tasksPlanned = new TaskList("FreeTimeContainer List");
		m_durationLeft = Duration.between(begin, end);
	}

	/**
	 * Add a task to the ftc
	 * @param newTask the task to add
	 * @return wehter or not it was succesfull
	 */
	public boolean addTask(Task newTask) {
		if (m_durationLeft.minus(newTask.getDuration()).isNegative()) // can not plan
		{
			return false;
		} else {
			m_durationLeft = m_durationLeft.minus(newTask.getDuration());
			m_tasksPlanned.addTask(newTask);
			return true;
		}
	}

	/**
	 * Get the duration left of the ftc
	 * @return the duration left of the ftc
	 */
	public Duration getDurationLeft() {
		return m_durationLeft;
	}

	/**
	 * @return the tasks planned in the ftc
	 */
	public TaskList getTasksPlanned() {
		return m_tasksPlanned;
	}

	/**
	 * @return the begin ZonedDateTime of the ftc
	 */
	public ZonedDateTime getBeginZDT() {
		return m_begin;
	}

	/**
	 * @return the end ZonedDateTime of the ftc
	 */
	public ZonedDateTime getEndZDT() {
		return m_end;
	}

	/**
	 * delete a timeslice if possible
	 * @param start the begin of the timeslice
	 * @param end the end of the timeslice
	 * @return a list of new freetimecontainer computated
	 */
	public ArrayList<FreeTimeContainer> removeTimeSlice(ZonedDateTime start, ZonedDateTime end) {
		ArrayList<FreeTimeContainer> comp = new ArrayList<FreeTimeContainer>();
		if (end.isBefore(m_begin) || end.isEqual(m_begin))
			comp.add(this); // Not overlapping, before timeslice, don't split
		else if (start.isAfter(m_end) || start.isEqual(m_end))
			comp.add(this); // not overlapping, after timeslice, don't split
		else if (start.isEqual(m_begin) && end.isEqual(m_end)) // same slot, remove all!
			; // nothin left
		else if (start.isBefore(m_begin) && end.isAfter(m_begin) && end.isBefore(m_end)) // starts before event, ends in between
		{
			m_begin = end;
			comp.add(this); // cut off the start, split unnecessary
		} else if (start.isAfter(m_begin) && start.isBefore(m_end) && end.isAfter(m_end)) {
			m_end = start;
			comp.add(this);
		} else if (start.isAfter(m_begin) && start.isBefore(m_end) && end.isAfter(m_begin) && end.isBefore(m_end)) {
			comp.add(new FreeTimeContainer(m_begin, start));
			comp.add(new FreeTimeContainer(end, m_end));
		}

		return comp;
	}

	/**
	 * String representation to facilitate debugging
	 */
	@Override
	public String toString() {
		return m_begin.toString() + " - " + m_end.toString();
	}
}
