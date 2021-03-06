package psopv.taskplanner.views.dialogs;

import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JOptionPane;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.models.TaskList;
import psopv.taskplanner.models.TaskPlannerModel;

/**
 * 
 * Dialog for selecting a tasklist.
 *
 * @since Apr 30, 2014 2:00:31 PM
 * @author Martijn Theunissen
 * @author Tom Knaepen
 */
public class SelectTaskListDialog {

	TaskPlannerModel			m_model		= null;
	private ApplicationSettings	m_settings	= ApplicationSettings.getInstance();	// for localized strings.

	public SelectTaskListDialog(Observable model) {
		m_model = (TaskPlannerModel) model;
	}

	/**
	 * get the tasklist by opening a popup dialog.
	 * @return the tasklist chosen or null if there are none
	 */
	public TaskList getTaskList() {
		ArrayList<TaskList> tl = m_model.getTaskLists();
		if (tl.isEmpty())
			return null;
		TaskList[] list = new TaskList[tl.size()];
		for (int i = 0; i < tl.size(); i++)
			list[i] = tl.get(i);
		TaskList selectedTask = (TaskList) JOptionPane.showInputDialog(null, m_settings.getLocalizedMessage("which.task"), m_settings.getLocalizedMessage("sel.task"), JOptionPane.QUESTION_MESSAGE, null, list, list[0]);
		return selectedTask;
	}
}
