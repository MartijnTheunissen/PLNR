package psopv.taskplanner.views.dialogs;

import java.awt.Dimension;
import java.awt.Font;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;
import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.controllers.TaskPlannerController;
import psopv.taskplanner.models.Task;
import psopv.taskplanner.models.TaskList;
import psopv.taskplanner.models.TaskPlannerModel;
import be.uhasselt.oo2.mvc.Controller;
import be.uhasselt.oo2.mvc.View;

import com.alee.extended.date.WebDateField;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextArea;

/**
 * 
 * JDialog for adding new tasks.
 *
 * @since Apr 8, 2014 12:45:41 AM
 * @author Martijn Theunissen
 * @author Tom Knaepen
 */
public class AddTaskDialog extends JDialog implements View {

	private JTextField			m_nameTextField;
	private WebTextArea			m_descriptionTextArea;
	private JComboBox<String>	m_priorityComboBox;
	private WebDateField		m_datepicker;
	private JSpinner			m_hourDuration;
	private JSpinner			m_minuteDuration;
	private JButton				m_addTaskButton;
	private JSpinner			m_timeSpinner;
	private JSpinner.DateEditor	m_timeEditor;

	JComboBox<TaskList>			m_tasklistComboBox;
	private ApplicationSettings	m_settings	= ApplicationSettings.getInstance();	// for localized strings.

	private Observable			m_model;
	private Controller			m_controller;

	/**
	 * Create a new AddtaskDialog
	 * @param model observable model
	 * @param contr the controller, may be null
	 */
	public AddTaskDialog(Observable model, Controller contr) {
		m_model = model;
		if (contr == null)
			contr = defaultController(model);
		m_controller = contr;

		initializeGUIComponents();
	}

	/**
	 * Automatically generated by window builder
	 */
	private void initializeGUIComponents() {
		this.setTitle(m_settings.getLocalizedMessage("task.create"));
		getContentPane().setPreferredSize(new Dimension(500, 340));
		getContentPane().setLayout(new MigLayout("", "[][grow][]", "[][][][][grow][][][][][][]"));

		JLabel lblTaskList = new JLabel(m_settings.getLocalizedMessage("lblTaskList"));
		getContentPane().add(lblTaskList, "cell 0 1,alignx left");

		m_tasklistComboBox = new JComboBox<TaskList>();
		TaskPlannerModel mod = (TaskPlannerModel) m_model;
		ArrayList<TaskList> tl = mod.getTaskLists();
		TaskList[] list = new TaskList[tl.size()];
		for (int i = 0; i < tl.size(); i++)
			list[i] = tl.get(i);
		m_tasklistComboBox.setModel(new DefaultComboBoxModel<TaskList>(list));
		getContentPane().add(m_tasklistComboBox, "cell 1 1 2 1,growx");

		JLabel lblName = new JLabel(m_settings.getLocalizedMessage("lblName"));
		getContentPane().add(lblName, "cell 0 3,alignx left");

		m_nameTextField = new JTextField();
		getContentPane().add(m_nameTextField, "cell 1 3 2 1,growx");
		m_nameTextField.setColumns(10);

		JLabel lblDescription = new JLabel(m_settings.getLocalizedMessage("lblDescription"));
		getContentPane().add(lblDescription, "cell 0 4,alignx left");

		m_descriptionTextArea = new WebTextArea();
		m_descriptionTextArea.setLineWrap(true);
		m_descriptionTextArea.setWrapStyleWord(true);
		WebScrollPane areaScroll = new WebScrollPane(m_descriptionTextArea);
		areaScroll.setPreferredSize(new Dimension(200, 150));

		getContentPane().add(areaScroll, "cell 1 4 2 1,grow");

		JLabel lblPriority = new JLabel(m_settings.getLocalizedMessage("lblPriority"));
		getContentPane().add(lblPriority, "cell 0 5,alignx left");

		m_priorityComboBox = new JComboBox<String>();
		m_priorityComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		String[] cboxmodel = new String[5];
		cboxmodel[0] = m_settings.getLocalizedMessage("priority.vlow");
		cboxmodel[1] = m_settings.getLocalizedMessage("priority.low");
		cboxmodel[2] = m_settings.getLocalizedMessage("priority.medium");
		cboxmodel[3] = m_settings.getLocalizedMessage("priority.high");
		cboxmodel[4] = m_settings.getLocalizedMessage("priority.vhigh");

		m_priorityComboBox.setModel(new DefaultComboBoxModel<String>(cboxmodel));
		getContentPane().add(m_priorityComboBox, "cell 1 5 2 1,growx");

		JLabel lblDeadline = new JLabel(m_settings.getLocalizedMessage("lblDeadline"));
		getContentPane().add(lblDeadline, "cell 0 6,alignx left");

		m_datepicker = new WebDateField(new Date());
		getContentPane().add(m_datepicker, "cell 1 6,growx");

		m_timeSpinner = new JSpinner(new SpinnerDateModel());
		m_timeEditor = new JSpinner.DateEditor(m_timeSpinner, "HH:mm:ss");
		m_timeSpinner.setEditor(m_timeEditor);
		m_timeSpinner.setValue(new Date());
		getContentPane().add(m_timeSpinner, "cell 2 6,growx");

		JLabel lblDuration = new JLabel(m_settings.getLocalizedMessage("lblDuration"));
		getContentPane().add(lblDuration, "cell 0 7,alignx left");

		m_hourDuration = new JSpinner();
		m_hourDuration.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		getContentPane().add(m_hourDuration, "cell 1 7,growx");

		JLabel lblHours = new JLabel(m_settings.getLocalizedMessage("hrs"));
		lblHours.setFont(new Font("Dialog", Font.PLAIN, 12));
		getContentPane().add(lblHours, "cell 2 7");

		m_minuteDuration = new JSpinner();
		m_minuteDuration.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		getContentPane().add(m_minuteDuration, "cell 1 8,growx");

		JLabel lblMinutes = new JLabel(m_settings.getLocalizedMessage("mins"));
		lblMinutes.setFont(new Font("Dialog", Font.PLAIN, 12));
		getContentPane().add(lblMinutes, "cell 2 8");

		m_addTaskButton = new JButton(m_settings.getLocalizedMessage("addt"));
		m_addTaskButton.addActionListener(ae -> this.addTask());
		m_addTaskButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		getContentPane().add(m_addTaskButton, "cell 2 9");
		this.pack();
	}

	/**
	 * Add a task to the tasklist
	 */
	private void addTask() {
		if (m_nameTextField.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, m_settings.getLocalizedMessage("empty.namefield"), m_settings.getLocalizedMessage("error"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		Duration duration = Duration.ofMinutes(((Integer) m_hourDuration.getValue()) * 60 + (Integer) m_minuteDuration.getValue());
		String time = m_timeEditor.getFormat().format(m_timeSpinner.getValue()); // "HH:mm:ss"
		int hr = Integer.parseInt(time.substring(0, 2));
		int min = Integer.parseInt(time.substring(3, 5));
		int sec = Integer.parseInt(time.substring(6, 8));

		ZonedDateTime zdt;

		LocalDate date = m_datepicker.getDate().toInstant().atZone(ApplicationSettings.getInstance().getTimeZone().toZoneId()).toLocalDate();
		LocalTime timestr = LocalTime.of(hr, min, sec);
		zdt = ZonedDateTime.of(date, timestr, ApplicationSettings.getInstance().getTimeZone().toZoneId());

		Task newTask = new Task("", m_nameTextField.getText().trim(), zdt, duration, getPriority((String) m_priorityComboBox.getSelectedItem()), m_descriptionTextArea.getText().trim());

		((TaskPlannerController) getController()).addTask((TaskList) m_tasklistComboBox.getSelectedItem(), newTask);
		this.clearComponents();
		this.setVisible(false);
	}

	/**
	 * Get the int-priority of the text-rep in the jdialog
	 * @param priority the priority-string of the int-representation
	 * @return the integer-representation of the string-priority
	 */
	private int getPriority(String priority) {

		if (priority.equals(m_settings.getLocalizedMessage("priority.vlow")))
			return 1;
		else if (priority.equals(m_settings.getLocalizedMessage("priority.low")))
			return 2;
		else if (priority.equals(m_settings.getLocalizedMessage("priority.medium")))
			return 3;
		else if (priority.equals(m_settings.getLocalizedMessage("priority.high")))
			return 4;
		else if (priority.equals(m_settings.getLocalizedMessage("priority.vhigh")))
			return 5;
		else
			return 3; // default
	}

	/**
	 * clear the components of every field on the jdialog
	 */
	public void clearComponents() {
		/*
		 * m_nameTextField.setText(""); m_descriptionTextArea.setText("");
		 * m_hourDuration.setValue(0); m_minuteDuration.setValue(0);
		 */
		initializeGUIComponents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		// ignore updates
		m_model = o;
		initializeGUIComponents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.uhasselt.oo2.mvc.View#setController(be.uhasselt.oo2.mvc.Controller)
	 */
	@Override
	public void setController(Controller controller) {
		m_controller = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.uhasselt.oo2.mvc.View#getController()
	 */
	@Override
	public Controller getController() {
		if (m_controller == null)
			return defaultController(getModel());
		else
			return m_controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.uhasselt.oo2.mvc.View#setModel(java.util.Observable)
	 */
	@Override
	public void setModel(Observable model) {
		m_model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.uhasselt.oo2.mvc.View#getModel()
	 */
	@Override
	public Observable getModel() {
		return m_model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.uhasselt.oo2.mvc.View#defaultController(java.util.Observable)
	 */
	@Override
	public Controller defaultController(Observable model) {
		return new TaskPlannerController(model);
	}

	/**
	 * @param list
	 */
	public void setSelectedList(TaskList list) {
		m_tasklistComboBox.setSelectedItem(list);
	}
}
