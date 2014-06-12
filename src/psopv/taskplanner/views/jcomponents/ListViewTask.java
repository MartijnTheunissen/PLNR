package psopv.taskplanner.views.jcomponents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import psopv.taskplanner.models.Task;
import psopv.taskplanner.views.TaskListView;

import com.alee.laf.panel.WebPanel;

/**
 * 
 * Task in the list view
 *
 * @since May 28, 2014 10:29:00 PM
 * @author Martijn Theunissen
 */
public class ListViewTask extends WebPanel implements Observer {

	private Observable					m_model;
	private TaskListView				m_parent;
	private ListViewTaskDeadlinePane	m_datePane;
	private ListViewTaskInfoPane		m_infoPane;
	private Task						m_item;

	/**
	 * Initialize the calendaritemview
	 * @param taskListView the tasklist view parent
	 * @param item the item to view
	 * @param drawDate if we should draw the date
	 */
	public ListViewTask(TaskListView taskListView, Task item, boolean drawDate) {
		m_model = item;
		m_model.addObserver(this);
		m_parent = taskListView;
		this.setSize(600, 80);
		this.setPreferredSize(new Dimension(600, 80));
		this.setMaximumSize(new Dimension(super.getMaximumSize().width, 80));
		m_item = item;

		this.setLayout(new BorderLayout());
		m_datePane = new ListViewTaskDeadlinePane(item, drawDate);
		m_infoPane = new ListViewTaskInfoPane(item);
		this.add(m_datePane, BorderLayout.WEST);
		this.add(m_infoPane, BorderLayout.CENTER);
	}

	/**
	 * get the task item of the view
	 * @return the task of the view
	 */
	public Task getTask() {
		return m_item;
	}

	/**
	 * set if one should draw the datepane
	 * @param b whether or not it should be drawn
	 */
	public void setDrawDate(boolean b) {
		m_datePane.setDrawDate(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		m_model = arg0;
		m_parent.taskUpdated();
		//m_parent.moveItem((CalendarItem) arg0);
	}

}
