/**
 * 
 */
package psopv.taskplanner.views.dialogs;

import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JOptionPane;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.models.Calendar;
import psopv.taskplanner.models.TaskPlannerModel;

/**
 * 
 * 
 * $Rev:: 135                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-02 19:01:51 +0200 (Mon, 02 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * Dialog for selecting the calendar
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version<br>
 * 2 - <br>
 * 3 - <br>
 *
 * @since May 9, 2014 11:54:13 PM
 * @author Martijn Theunissen
 */
public class SelectCalendarDialog {

	TaskPlannerModel			m_model		= null;
	private ApplicationSettings	m_settings	= ApplicationSettings.getInstance();	// for localized strings.

	public SelectCalendarDialog(Observable model) {
		m_model = (TaskPlannerModel) model;
	}

	/**
	 * get the tasklist by opening a popup dialog.
	 * @return the tasklist chosen or null if there are none
	 */
	public Calendar getCalendar() {
		ArrayList<Calendar> cals = m_model.getCalendars();
		if (cals.isEmpty())
			return null;
		Calendar[] list = new Calendar[cals.size()];
		for (int i = 0; i < cals.size(); i++)
			list[i] = cals.get(i);
		Calendar selectedCalendar = (Calendar) JOptionPane.showInputDialog(null, m_settings.getLocalizedMessage("which.calendar"), m_settings.getLocalizedMessage("sel.calendar"), JOptionPane.QUESTION_MESSAGE, null, list, list[0]);
		return selectedCalendar;
	}
}