/**
 * 
 */
package psopv.taskplanner.views;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Observable;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.controllers.TaskPlannerIOController;
import psopv.taskplanner.models.Calendar;
import psopv.taskplanner.models.TaskList;
import psopv.taskplanner.models.TaskPlannerModel;
import psopv.taskplanner.views.dialogs.AddCalendarItemDialog;
import psopv.taskplanner.views.dialogs.AddTaskDialog;
import psopv.taskplanner.views.dialogs.PlanningDialog;
import psopv.taskplanner.views.dialogs.SelectCalendarDialog;
import psopv.taskplanner.views.dialogs.SelectTaskListDialog;
import psopv.taskplanner.views.dialogs.SettingsDialog;
import be.uhasselt.oo2.mvc.Controller;
import be.uhasselt.oo2.mvc.View;

import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotificationPopup;

/**
 * 
 * $Rev:: 150                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-05 14:59:58 +0200 (Thu, 05 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This class represents the main menubar for the taskplanner window.
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: Initial version<br>
 * 2 - martijn.theunissen: calendar import/export support<br>
 * 3 - martijn.theunissen: add calendaritem + planning support<br>
 * 4 - martijn.theunissen: multilanguage support<br>
 *
 * @since Mar 20, 2014 1:49:53 PM
 * @author Martijn Theunissen
 */
public class TaskPlannerMenuBar extends JMenuBar implements View {

	/**
	 * Generated serial version UID.
	 */
	private static final long		serialVersionUID	= -6534234541166719946L;

	private JMenu					m_file, m_plan, m_add, m_options;
	private JMenuItem				m_openTasks, m_exportTasks, m_openICal, m_exportICal, m_addCalendarItem, m_addTask, m_makePlanning, m_settings;

	private Observable				m_model;
	private Controller				m_controller;
	private AddCalendarItemDialog	m_addCalendarItemDialog;
	private ApplicationSettings		m_appSettings;

	/**
	 * Create the menubar
	 * @param model the model to store
	 * @param controller the controller to use, can be null.
	 */
	public TaskPlannerMenuBar(Observable model, Controller controller) {
		m_appSettings = ApplicationSettings.getInstance();
		m_appSettings.addObserver(this); // menu language updates (et al?)
		m_model = model;
		model.addObserver(this);
		m_controller = controller;
		initializeMenu();
		initializeEvents();
		initializeHiddenDialog(model, controller);
	}

	private void initializeHiddenDialog(Observable model, Controller contr) {
		m_addCalendarItemDialog = new AddCalendarItemDialog(model, contr);
		m_addCalendarItemDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		m_addCalendarItemDialog.setVisible(false);
	}

	/**
	 * Initialize the menubar with all its components.
	 */
	private void initializeMenu() {
		m_file = new JMenu(m_appSettings.getLocalizedMessage("menu.file"));
		m_add = new JMenu(m_appSettings.getLocalizedMessage("menu.add"));
		m_plan = new JMenu(m_appSettings.getLocalizedMessage("menu.plan"));
		m_options = new JMenu(m_appSettings.getLocalizedMessage("menu.options"));
		this.add(m_file);
		this.add(m_add);
		this.add(m_plan);
		this.add(m_options);

		m_openTasks = new JMenuItem(m_appSettings.getLocalizedMessage("menu.file.openTL"));
		m_openTasks.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK + java.awt.event.KeyEvent.SHIFT_MASK));
		m_file.add(m_openTasks);

		m_exportTasks = new JMenuItem(m_appSettings.getLocalizedMessage("menu.file.saveTL"));
		m_exportTasks.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK + java.awt.event.KeyEvent.SHIFT_MASK));
		m_file.add(m_exportTasks);

		m_openICal = new JMenuItem(m_appSettings.getLocalizedMessage("menu.file.openCal"));
		m_openICal.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK));
		m_file.add(m_openICal);

		m_exportICal = new JMenuItem(m_appSettings.getLocalizedMessage("menu.file.saveCal"));
		m_exportICal.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
		m_file.add(m_exportICal);

		m_addCalendarItem = new JMenuItem(m_appSettings.getLocalizedMessage("menu.add.calitem"));
		m_addCalendarItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.Event.CTRL_MASK));
		m_add.add(m_addCalendarItem);

		m_addTask = new JMenuItem(m_appSettings.getLocalizedMessage("menu.add.task"));
		m_addTask.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.Event.CTRL_MASK + java.awt.event.KeyEvent.SHIFT_MASK));
		m_add.add(m_addTask);

		m_makePlanning = new JMenuItem(m_appSettings.getLocalizedMessage("menu.plan.make"));
		m_makePlanning.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.Event.CTRL_MASK));
		m_plan.add(m_makePlanning);

		m_settings = new JMenuItem(m_appSettings.getLocalizedMessage("menu.options.settings"));
		m_settings.setAccelerator(KeyStroke.getKeyStroke("F1"));

		m_options.add(m_settings);
	}

	/**
	 * Initialize the events of the menubar.
	 */
	private void initializeEvents() {
		m_openTasks.addActionListener(this::openTaskFile);
		m_exportTasks.addActionListener(this::exportTaskFile);
		m_openICal.addActionListener(this::openICalCalendar);
		m_exportICal.addActionListener(this::exportICalCalendar);

		m_addCalendarItem.addActionListener(this::addCalendarItem);
		m_addTask.addActionListener(this::addTask);

		m_makePlanning.addActionListener(this::makePlanning);

		m_settings.addActionListener(this::openSettingsDialog);
	}

	/**
	 * Make a planning, response to buttonclick
	 * @param e the actionevent
	 */
	private void makePlanning(ActionEvent e) {

		if (((TaskPlannerModel) m_model).getTaskLists().size() == 0) {
			WebNotificationPopup notification = new WebNotificationPopup();
			notification.setContent(m_appSettings.getLocalizedMessage("plan.notl"));
			notification.setIcon(NotificationIcon.error);
			NotificationManager.showNotification(notification);
			return;
		}
		if (((TaskPlannerModel) m_model).getCalendars().size() == 0) {
			WebNotificationPopup notification = new WebNotificationPopup();
			notification.setContent(m_appSettings.getLocalizedMessage("plan.nocal"));
			notification.setIcon(NotificationIcon.error);
			NotificationManager.showNotification(notification);
			return;
		}

		PlanningDialog dialog = new PlanningDialog(m_model, null);
		dialog.setVisible(true);
		dialog.pack();
	}

	/**
	 * add a calendar item, response to buttonclick
	 * @param e the actionevent
	 */
	private void addCalendarItem(ActionEvent e) {
		m_addCalendarItemDialog.setVisible(true);
		m_addCalendarItemDialog.pack();
		m_addCalendarItemDialog.clearComponents();
	}

	private void openSettingsDialog(ActionEvent e) {
		SettingsDialog settingsDialog = new SettingsDialog(null);
		settingsDialog.setVisible(true);
		settingsDialog.pack();
	}

	/**
	 * Invoked when clicked on the add task menu
	 * @param e the actionevent that occured
	 */
	private void addTask(ActionEvent e) {
		AddTaskDialog dialog = new AddTaskDialog(m_model, m_controller);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	/**
	 * Import a taskfile. Choose the file from a filepicker first
	 * @param e the event that chose to import
	 */
	private void openTaskFile(ActionEvent e) {
		JFileChooser openFileChooser = new JFileChooser();
		openFileChooser.setFileFilter(new FileNameExtensionFilter(m_appSettings.getLocalizedMessage("xmlfile"), "xml"));
		openFileChooser.setFileHidingEnabled(true);
		openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (openFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			((TaskPlannerIOController) defaultController(getModel())).importTaskList(openFileChooser.getSelectedFile());
		}
	}

	/**
	 * Export a taskfile. Choose the file from a filepicker first
	 * @param e the event that chose to export
	 */
	private void exportTaskFile(ActionEvent e) {
		SelectTaskListDialog dialog = new SelectTaskListDialog(m_model);
		TaskList selected = dialog.getTaskList();
		if (selected == null) {
			JOptionPane.showMessageDialog(null, m_appSettings.getLocalizedMessage("save.notl"));
			return;
		}
		WebFileChooser saveFileChooser = new WebFileChooser();
		saveFileChooser.setFileFilter(new FileNameExtensionFilter(m_appSettings.getLocalizedMessage("xmlfile"), "xml"));
		saveFileChooser.setFileHidingEnabled(true);
		saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		saveFileChooser.setApproveButtonText("Save");

		if (saveFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File fileToBeSaved = saveFileChooser.getSelectedFile();
			if (!saveFileChooser.getSelectedFile().getAbsolutePath().endsWith(".xml")) {
				fileToBeSaved = new File(saveFileChooser.getSelectedFile() + ".xml");
			}
			((TaskPlannerIOController) defaultController(getModel())).exportTaskList(selected, fileToBeSaved);
		}
	}

	/**
	 * Import an ICAL calendar. Choose the file from a filepicker first
	 * @param e the event that chose to import
	 */
	private void openICalCalendar(ActionEvent e) {
		JFileChooser openFileChooser = new JFileChooser();
		openFileChooser.setFileFilter(new FileNameExtensionFilter(m_appSettings.getLocalizedMessage("icalfile"), "ical", "ics", "ifb"));
		openFileChooser.setFileHidingEnabled(true);
		openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (openFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			((TaskPlannerIOController) defaultController(getModel())).importICalendar(openFileChooser.getSelectedFile());
		}
	}

	/**
	 * Export an ICAL calendar. Choose the file from a filepicker first
	 * TODO: Select calendar to export
	 * @param e the event that chose to export
	 */
	private void exportICalCalendar(ActionEvent e) {
		JFileChooser saveFileChooser = new JFileChooser();
		saveFileChooser.setFileFilter(new FileNameExtensionFilter(m_appSettings.getLocalizedMessage("icalfile"), "ical", "ics", "ifb"));
		saveFileChooser.setFileHidingEnabled(true);
		saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		saveFileChooser.setApproveButtonText("Save");

		SelectCalendarDialog selectCalendar = new SelectCalendarDialog(m_model);
		Calendar toExport = selectCalendar.getCalendar();
		if (toExport == null) {
			WebOptionPane.showMessageDialog(this, m_appSettings.getLocalizedMessage("save.nocal"), m_appSettings.getLocalizedMessage("error"), WebOptionPane.ERROR_MESSAGE);
			return;
		}
		saveFileChooser.setSelectedFile(new File(toExport.getName()));

		if (saveFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File fileToBeSaved = saveFileChooser.getSelectedFile();
			String abspath = saveFileChooser.getSelectedFile().getAbsolutePath();
			if (!abspath.endsWith(".ics") && !abspath.endsWith(".ifb") && !abspath.endsWith(".ical")) {
				fileToBeSaved = new File(saveFileChooser.getSelectedFile() + ".ics");
			}
			((TaskPlannerIOController) defaultController(getModel())).exportCalendar(fileToBeSaved, toExport);
		}
	}

	/**
	 * Set the controller.
	 */
	public void setController(Controller controller) {
		m_controller = controller;
	}

	/**
	 * Get the current controller
	 */
	public Controller getController() {
		if (m_controller != null)
			return m_controller;
		else
			return defaultController(m_model);
	}

	/**
	 * Set the model
	 */
	public void setModel(Observable model) {
		m_model = model;
	}

	/**
	 * Get the model
	 */
	public Observable getModel() {
		return m_model;
	}

	/**
	 * Get the default controller.
	 */
	public Controller defaultController(Observable model) {
		return new TaskPlannerIOController(model);
	}

	/**
	 * Process updates from ApplicationSettings.
	 */
	public void update(Observable o, Object arg) {
		if (o.getClass().equals(ApplicationSettings.class)) {
			m_file.setText(m_appSettings.getLocalizedMessage("menu.file"));
			m_add.setText(m_appSettings.getLocalizedMessage("menu.add"));
			m_plan.setText(m_appSettings.getLocalizedMessage("menu.plan"));
			m_options.setText(m_appSettings.getLocalizedMessage("menu.options"));
			m_openTasks.setText(m_appSettings.getLocalizedMessage("menu.file.openTL"));
			m_exportTasks.setText(m_appSettings.getLocalizedMessage("menu.file.saveTL"));
			m_openICal.setText(m_appSettings.getLocalizedMessage("menu.file.openCal"));
			m_exportICal.setText(m_appSettings.getLocalizedMessage("menu.file.saveCal"));
			m_addCalendarItem.setText(m_appSettings.getLocalizedMessage("menu.add.calitem"));
			m_makePlanning.setText(m_appSettings.getLocalizedMessage("menu.plan.make"));
			m_settings.setText(m_appSettings.getLocalizedMessage("menu.options.settings"));
		}
	}

}
