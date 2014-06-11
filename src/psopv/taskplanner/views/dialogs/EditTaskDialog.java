/**
 * 
 */
package psopv.taskplanner.views.dialogs;

import java.awt.Dimension;
import java.awt.Font;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

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
import psopv.taskplanner.models.Task;
import psopv.taskplanner.util.VEventHelper;

import com.alee.extended.date.WebDateField;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextArea;

/**
 * 
 * $Rev:: 141                                                  $:  Revision of last commit<br>
 * $Author:: tom.knaepen                                       $:  Author of last commit<br>
 * $Date:: 2014-06-03 14:59:48 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * Dialog for editing tasks
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - Martijn Theunissen: initial version<br>
 * 2 - <br>
 * 3 - <br>
 *
 * @since May 20, 2014 1:18:39 PM
 * @author Martijn Theunissen
 */
public class EditTaskDialog extends JDialog {

	private JTextField			m_nameTextField;
	private WebTextArea			m_descriptionTextArea;
	private JComboBox<String>	m_priorityComboBox;
	private WebDateField		m_datepicker;
	private JSpinner			m_hourDuration;
	private JSpinner			m_minuteDuration;
	private JButton				m_editTaskButton;
	private JButton				m_removeTaskButton;
	private JSpinner			m_timeSpinner;
	private JSpinner.DateEditor	m_timeEditor;
	private ApplicationSettings	m_settings	= ApplicationSettings.getInstance();	// for localized strings.

	private Task				m_task;

	/**
	 * Create a new AddtaskDialog
	 * @param task the task to edit
	 */
	public EditTaskDialog(Task task) {
		m_task = task;

		initializeGUIComponents();
	}

	/**
	 * Automatically generated by window builder
	 */
	private void initializeGUIComponents() {
		this.setTitle(m_settings.getLocalizedMessage("edit.title"));
		getContentPane().setPreferredSize(new Dimension(500, 340));
		getContentPane().setLayout(new MigLayout("", "[][grow][]", "[-17.00][][grow][][][][][][]"));

		JLabel lblName = new JLabel(m_settings.getLocalizedMessage("lblName"));
		getContentPane().add(lblName, "cell 0 1,alignx left");

		m_nameTextField = new JTextField();
		getContentPane().add(m_nameTextField, "cell 1 1 2 1,growx");
		m_nameTextField.setColumns(10);
		m_nameTextField.setText(m_task.getName());

		JLabel lblDescription = new JLabel(m_settings.getLocalizedMessage("lblDescription"));
		getContentPane().add(lblDescription, "cell 0 2,alignx left");

		m_descriptionTextArea = new WebTextArea();
		m_descriptionTextArea.setLineWrap(true);
		m_descriptionTextArea.setWrapStyleWord(true);
		m_descriptionTextArea.setText(m_task.getDescription());
		WebScrollPane areaScroll = new WebScrollPane(m_descriptionTextArea);
		areaScroll.setPreferredSize(new Dimension(200, 150));

		getContentPane().add(areaScroll, "cell 1 2 2 1,grow");

		JLabel lblPriority = new JLabel(m_settings.getLocalizedMessage("lblPriority"));
		getContentPane().add(lblPriority, "cell 0 3,alignx left");

		m_priorityComboBox = new JComboBox<String>();
		m_priorityComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		String[] cboxmodel = new String[5];
		cboxmodel[0] = m_settings.getLocalizedMessage("priority.vlow");
		cboxmodel[1] = m_settings.getLocalizedMessage("priority.low");
		cboxmodel[2] = m_settings.getLocalizedMessage("priority.medium");
		cboxmodel[3] = m_settings.getLocalizedMessage("priority.high");
		cboxmodel[4] = m_settings.getLocalizedMessage("priority.vhigh");

		m_priorityComboBox.setModel(new DefaultComboBoxModel<String>(cboxmodel));
		m_priorityComboBox.setSelectedItem(cboxmodel[m_task.getPriority() - 1]);
		getContentPane().add(m_priorityComboBox, "cell 1 3 2 1,growx");

		JLabel lblDeadline = new JLabel(m_settings.getLocalizedMessage("lblDeadline"));
		getContentPane().add(lblDeadline, "cell 0 4,alignx left");

		m_datepicker = new WebDateField(new Date());
		m_datepicker.setDate(Date.from(m_task.getDeadline().toInstant()));
		getContentPane().add(m_datepicker, "cell 1 4,growx");

		m_timeSpinner = new JSpinner(new SpinnerDateModel());
		m_timeEditor = new JSpinner.DateEditor(m_timeSpinner, "HH:mm:ss");
		m_timeSpinner.setEditor(m_timeEditor);
		m_timeSpinner.setValue(Date.from(m_task.getDeadline().toInstant()));
		getContentPane().add(m_timeSpinner, "cell 2 4,growx");

		JLabel lblDuration = new JLabel(m_settings.getLocalizedMessage("lblDuration"));
		getContentPane().add(lblDuration, "cell 0 5,alignx left");

		m_hourDuration = new JSpinner();
		m_hourDuration.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		m_hourDuration.setValue(VEventHelper.durationToICALDur(m_task.getDuration()).getHours());
		getContentPane().add(m_hourDuration, "cell 1 5,growx");

		JLabel lblHours = new JLabel(m_settings.getLocalizedMessage("hrs"));
		lblHours.setFont(new Font("Dialog", Font.PLAIN, 12));
		getContentPane().add(lblHours, "cell 2 5");

		m_minuteDuration = new JSpinner();
		m_minuteDuration.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		m_minuteDuration.setValue(VEventHelper.durationToICALDur(m_task.getDuration()).getMinutes());
		getContentPane().add(m_minuteDuration, "cell 1 6,growx");

		JLabel lblMinutes = new JLabel(m_settings.getLocalizedMessage("mins"));
		lblMinutes.setFont(new Font("Dialog", Font.PLAIN, 12));
		getContentPane().add(lblMinutes, "cell 2 6");

		m_editTaskButton = new JButton(m_settings.getLocalizedMessage("savet"));
		m_editTaskButton.addActionListener(ae -> this.replaceTask());
		m_editTaskButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		getContentPane().add(m_editTaskButton, "cell 2 7");
		m_removeTaskButton = new JButton(m_settings.getLocalizedMessage("removet"));
		m_removeTaskButton.addActionListener(ae -> this.removeTask());
		m_removeTaskButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		getContentPane().add(m_removeTaskButton, "cell 1 7, right");
		this.pack();
	}

	/**
	 * Add a task to the tasklist
	 */
	private void replaceTask() {
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

		m_task.copyFrom(newTask);

		this.clearComponents();
		this.setVisible(false);
	}

	private void removeTask() {
		m_task.remove();
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
		initializeGUIComponents();
	}
}