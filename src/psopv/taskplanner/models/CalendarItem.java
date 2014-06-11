/**
 * 
 */
package psopv.taskplanner.models;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Observable;

/**
 * 
 * $Rev:: 139                                                  $:  Revision of last commit<br>
 * $Author:: tom.knaepen                                       $:  Author of last commit<br>
 * $Date:: 2014-06-03 13:25:22 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This class represents an item in the calendar
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version <br>
 * 2 - martijn.theunissen: Timezone support added<br>
 * 3 - martijn.theunissen: Summary/Description instead of name.<br>
 *
 * @since Apr 1, 2014 12:46:36 PM
 * @author Martijn Theunissen
 */
public class CalendarItem extends Observable {

	/**
	 * CalendarItem properties
	 */
	private Duration	m_duration;
	private ZonedDateTime	m_startDate, m_endDate;
	private String			m_summary, m_description;
	private boolean			m_removeMe;

	/**
	 * Create the calendaritem
	 * @param summary the name of the item
	 * @param description the description of the item
	 * @param startDate the start of the item
	 * @param length the length of the item
	 */
	public CalendarItem(String summary, String description, ZonedDateTime startDate, Duration length) {
		m_removeMe = false;
		m_summary = summary;
		m_description = description;
		m_startDate = startDate;
		m_duration = length;
		m_endDate = startDate.plus(length);

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
	 * @return the start datetime of the event
	 */
	public ZonedDateTime getStartDate() {
		return m_startDate;
	}

	/**
	 * @return the end datetime of the event
	 */
	public ZonedDateTime getEndDate() {
		return m_endDate;
	}

	/**
	 * @return the summary of the event
	 */
	public String getSummary() {
		return m_summary;
	}

	/**
	 * @return the description of the event
	 */
	public String getDescription() {
		return m_description;
	}

	public Duration getDuration() {
		return m_duration;
	}

	public void setStartDate(ZonedDateTime startDate) {
		if (m_startDate.isEqual(startDate))
			return;

		m_startDate = startDate;
		m_endDate = m_startDate.plus(m_duration);
		setChanged();
		notifyObservers();
	}

	public void setSummary(String summary) {
		if (summary.equals(m_summary))
			return;

		m_summary = summary;
		setChanged();
		notifyObservers();
	}

	public void setDescription(String description) {
		if (description.equals(m_description))
			return;

		m_description = description;
		setChanged();
		notifyObservers();
	}

	public void setDuration(Duration duration) {
		if (duration.equals(m_duration))
			return;

		m_duration = duration;
		m_endDate = m_startDate.plus(m_duration);
		setChanged();
		notifyObservers();
	}
}
