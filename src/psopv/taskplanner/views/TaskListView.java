package psopv.taskplanner.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.models.TaskList;
import psopv.taskplanner.models.TaskPlannerModel;
import psopv.taskplanner.views.jcomponents.ListViewTask;
import be.uhasselt.oo2.mvc.Controller;

import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.combobox.WebComboBoxCellRenderer;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;

/**
 * 
 * Task list view/editor that looks like the calendar listview.
 * 
 * @since May 28, 2014 10:07:19 PM
 * @author Martijn Theunissen
 */
public class TaskListView extends WebPanel implements Observer {

	private WebPanel			m_itemContainer;
	private WebPanel			m_container;
	private WebScrollPane		m_scrollPane;
	private WebButtonGroup		m_menuButtonGroup;
	private WebComboBox			m_taskSelector;
	private Observable			m_model;
	private Controller			m_controller;
	private ArrayList<TaskList>	m_taskLists;
	private int					m_lastSelectedIndex	= 0;

	/**
	 * Initialize the task editor list view
	 * @param model the model to view
	 * @param controller the controller to use
	 */
	public TaskListView(TaskPlannerModel model, Controller controller) {
		m_model = model;
		model.addObserver(this);
		m_controller = controller;
		m_taskLists = model.getTaskLists();

		ApplicationSettings.getInstance().addObserver(this);
		this.setPreferredSize(getSize());

		initGUI();

		updateGUI();

		this.add(m_container);
	}

	/**
	 * Update the gui
	 */
	private void updateGUI() {

		m_itemContainer.removeAll();
		TaskList list = (TaskList) m_taskSelector.getSelectedItem();
		list.sort((a, b) -> {
			if (a.getDeadline().isEqual(b.getDeadline()))
				return 0;
			else if (a.getDeadline().isBefore(b.getDeadline()))
				return -1;
			else
				return 1; // must be after
		});
		for (int i = 0; i < list.getSize(); i++) {
			m_itemContainer.add(new ListViewTask(this, list.getTask(i), true));

			m_itemContainer.add(Box.createRigidArea(new Dimension(0, 20)));
		}

		revalidate();
		repaint();

	}

	/**
	 * Initialize the gui of the view
	 */
	private void initGUI() {
		m_itemContainer = new WebPanel();
		m_itemContainer.setLayout(new BoxLayout(m_itemContainer, BoxLayout.Y_AXIS));

		m_container = new WebPanel();
		m_container.setLayout(new BorderLayout());

		m_scrollPane = new WebScrollPane(m_itemContainer, false);
		m_scrollPane.setPreferredSize(new Dimension(900, 500));
		m_scrollPane.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		m_scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));

		WebPanel panel = new WebPanel(new GridLayout(1, 3));

		panel.setBorder(new EmptyBorder(5, 20, 0, 20));

		m_taskSelector = new WebComboBox(((TaskPlannerModel) m_model).getTaskLists().toArray());
		((WebComboBoxCellRenderer) m_taskSelector.getRenderer()).getBoxRenderer().setHorizontalAlignment(SwingConstants.LEFT);
		((WebComboBoxCellRenderer) m_taskSelector.getRenderer()).getElementRenderer().setHorizontalAlignment(SwingConstants.LEFT);
		m_taskSelector.addActionListener(this::selectionChanged);
		panel.add(new WebPanel());
		panel.add(m_taskSelector);
		panel.add(new WebPanel());

		m_container.add(panel, BorderLayout.NORTH);
		m_container.add(m_scrollPane, BorderLayout.CENTER);

	}

	/**
	 * update the gui when the selection is changed of the tasklist combobox selector
	 * @param e the event that happened
	 */
	private void selectionChanged(ActionEvent e) {
		m_lastSelectedIndex = m_taskSelector.getSelectedIndex();
		updateGUI();
	}

	public void taskUpdated() {
		updateGUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof TaskPlannerModel) {
			m_model = (TaskPlannerModel) o;
			final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(((TaskPlannerModel) m_model).getTaskLists().toArray());
			m_taskSelector.setModel(model);
			m_taskSelector.setSelectedIndex(m_lastSelectedIndex);
			updateGUI();
		}
	}

}
