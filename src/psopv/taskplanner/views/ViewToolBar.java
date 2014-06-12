package psopv.taskplanner.views;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.config.ViewPreferences;
import psopv.taskplanner.config.ViewPreferences.CenterView;
import psopv.taskplanner.controllers.TaskPlannerController;
import psopv.taskplanner.controllers.TaskPlannerIOController;
import psopv.taskplanner.models.Calendar;
import psopv.taskplanner.models.TaskList;
import psopv.taskplanner.models.TaskPlannerModel;
import psopv.taskplanner.views.dialogs.AddCalendarItemDialog;
import psopv.taskplanner.views.dialogs.AddTaskDialog;

import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;

/**
 * 
 * Toolbar that lets you change views.
 *
 * @since May 11, 2014 5:58:34 PM
 * @author Martijn Theunissen
 * @author Tom Knaepen
 */
public class ViewToolBar implements Observer {

	private TaskPlannerModel	m_model;
	private WebPanel			m_container;
	private WebToggleButton		weekView;
	private WebToggleButton		listView;
	private WebToggleButton		taskView;
	private WebButton			addTask, addTasklist, importTasklist, addItem, addCalendar, importCalendar;
	private WebButtonGroup		calendarButtonGroup, taskButtonGroup, activeGroup;

	public ViewToolBar(TaskPlannerModel model) {
		m_model = model;
		m_container = new WebPanel();
		m_container.setLayout(new BorderLayout());
		createGUI();
		ApplicationSettings.getInstance().addObserver(this);
		ViewPreferences.getInstance().addObserver(this);
	}

	/**
	 * Create the GUI.
	 */
	private void createGUI() {
		weekView = new WebToggleButton(ApplicationSettings.getInstance().getLocalizedMessage("vtb.wv"));
		listView = new WebToggleButton(ApplicationSettings.getInstance().getLocalizedMessage("vtb.lv"));
		taskView = new WebToggleButton(ApplicationSettings.getInstance().getLocalizedMessage("vtb.tv"));
		weekView.setSelected(true);
		ViewPreferences.getInstance().setCenterView(CenterView.WEEK);

		addItem = new WebButton(ApplicationSettings.getInstance().getLocalizedMessage("menu.add.calitem"));
		addCalendar = new WebButton(ApplicationSettings.getInstance().getLocalizedMessage("menu.add.calendar"));
		importCalendar = new WebButton(ApplicationSettings.getInstance().getLocalizedMessage("menu.imp.calendar"));
		addItem.addActionListener(this::addCalendarItem);
		addCalendar.addActionListener(this::addCalendar);
		importCalendar.addActionListener(this::openICalCalendar);
		addTask = new WebButton(ApplicationSettings.getInstance().getLocalizedMessage("menu.add.task"));
		addTasklist = new WebButton(ApplicationSettings.getInstance().getLocalizedMessage("menu.add.tasklist"));
		importTasklist = new WebButton(ApplicationSettings.getInstance().getLocalizedMessage("menu.imp.tasklist"));
		addTask.addActionListener(this::addTask);
		addTasklist.addActionListener(this::addTasklist);
		importTasklist.addActionListener(this::importTasklist);

		taskButtonGroup = new WebButtonGroup(false, addTask, addTasklist, importTasklist);
		taskButtonGroup.setButtonsDrawFocus(false);
		calendarButtonGroup = new WebButtonGroup(false, addItem, addCalendar, importCalendar);
		calendarButtonGroup.setButtonsDrawFocus(false);
		activeGroup = calendarButtonGroup;
		m_container.add(activeGroup, BorderLayout.EAST);

		weekView.addActionListener((l) -> {
			ViewPreferences.getInstance().setCenterView(CenterView.WEEK);
		});
		listView.addActionListener((l) -> {
			ViewPreferences.getInstance().setCenterView(CenterView.LIST);
		});

		taskView.addActionListener((l) -> {
			ViewPreferences.getInstance().setCenterView(CenterView.TASK);
		});

		WebButtonGroup viewGroup = new WebButtonGroup(true, weekView, listView, taskView);
		viewGroup.setButtonsDrawFocus(false);
		m_container.add(viewGroup, BorderLayout.WEST);
	}

	/**
	 * @return the toolbar UI
	 */
	public WebPanel getUI() {
		return m_container;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (arg != null && arg instanceof String && ((String) arg).contains("VS")) // View settings updates
		{
			if (activeGroup != null)
				m_container.remove(activeGroup);
			if (ViewPreferences.getInstance().getCenterView() == CenterView.TASK) {
				activeGroup = taskButtonGroup;
				m_container.add(activeGroup, BorderLayout.EAST);
			} else {
				activeGroup = calendarButtonGroup;
				m_container.add(activeGroup, BorderLayout.EAST);
			}
			m_container.validate(); // force to recalculate size of active ButtonGroup
		} else // application settings update
		{
			ApplicationSettings a = ApplicationSettings.getInstance();
			weekView.setText(a.getLocalizedMessage("vtb.wv"));
			listView.setText(a.getLocalizedMessage("vtb.lv"));
			taskView.setText(a.getLocalizedMessage("vtb.tv"));
			addItem.setText(a.getLocalizedMessage("menu.add.calitem"));
			addCalendar.setText(a.getLocalizedMessage("menu.add.calendar"));
			importCalendar.setText(a.getLocalizedMessage("menu.imp.calendar"));
			addTask.setText(a.getLocalizedMessage("menu.add.task"));
			addTasklist.setText(a.getLocalizedMessage("menu.add.tasklist"));
			importTasklist.setText(a.getLocalizedMessage("menu.imp.tasklist"));
		}
	}

	/**
	 * add a calendar item, response to buttonclick
	 * @param e the actionevent
	 */
	private void addCalendarItem(ActionEvent e) {
		AddCalendarItemDialog addCalendarItemDialog = new AddCalendarItemDialog(m_model, null);
		addCalendarItemDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		addCalendarItemDialog.pack();
		addCalendarItemDialog.clearComponents();
		addCalendarItemDialog.setVisible(true);
	}

	/**
	 * Import an ICAL calendar. Choose the file from a filepicker first
	 * @param e the event that chose to import
	 */
	private void openICalCalendar(ActionEvent e) {
		JFileChooser openFileChooser = new JFileChooser();
		openFileChooser.setFileFilter(new FileNameExtensionFilter(ApplicationSettings.getInstance().getLocalizedMessage("icalfile"), "ical", "ics", "ifb"));
		openFileChooser.setFileHidingEnabled(true);
		openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (openFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			new TaskPlannerIOController(m_model).importICalendar(openFileChooser.getSelectedFile());
		}
	}

	/**
	 * add a calendar, response to buttonclick
	 * @param e the actionevent
	 */
	private void addCalendar(ActionEvent e) {
		String s = WebOptionPane.showInputDialog(null, ApplicationSettings.getInstance().getLocalizedMessage("tv.addcal"), ApplicationSettings.getInstance().getLocalizedMessage("tv.inp"), JOptionPane.QUESTION_MESSAGE);
		if (s != null && !s.isEmpty())
			m_model.addCalendar(new Calendar(s, new net.fortuna.ical4j.model.Calendar()));
	}

	/**
	 * add a task, response to buttonclick
	 * @param e the actionevent
	 */
	private void addTask(ActionEvent e) {
		AddTaskDialog atd = new AddTaskDialog(m_model, null);
		atd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		atd.pack();
		atd.setVisible(true);
	}

	/**
	 * add a task, response to buttonclick
	 * @param e the actionevent
	 */
	private void addTasklist(ActionEvent e) {
		String s = WebOptionPane.showInputDialog(null, ApplicationSettings.getInstance().getLocalizedMessage("tv.addtl"), ApplicationSettings.getInstance().getLocalizedMessage("tv.inpl"), JOptionPane.QUESTION_MESSAGE);
		if (s != null && !s.isEmpty())
			new TaskPlannerController(m_model).addTaskList(new TaskList(s));
	}

	/**
	 * add a task, response to buttonclick
	 * @param e the actionevent
	 */
	private void importTasklist(ActionEvent e) {
		JFileChooser openFileChooser = new JFileChooser();
		openFileChooser.setFileFilter(new FileNameExtensionFilter(ApplicationSettings.getInstance().getLocalizedMessage("xmlfile"), "xml"));
		openFileChooser.setFileHidingEnabled(true);
		openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (openFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			new TaskPlannerIOController(m_model).importTaskList(openFileChooser.getSelectedFile());
		}
	}

}
