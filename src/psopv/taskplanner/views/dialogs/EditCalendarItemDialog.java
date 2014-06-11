/**
 * 
 */
package psopv.taskplanner.views.dialogs;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

import net.miginfocom.swing.MigLayout;
import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.models.CalendarItem;
import psopv.taskplanner.util.VEventHelper;

import com.alee.extended.date.WebDateField;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextArea;

/**
 * 
 * $Rev:: 139                                                  $:  Revision of last commit<br>
 * $Author:: tom.knaepen                                       $:  Author of last commit<br>
 * $Date:: 2014-06-03 13:25:22 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * Dialog for editing a calendar item
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version<br>
 * 2 - <br>
 * 3 - <br>
 *
 * @since May 26, 2014 2:44:15 PM
 * @author Martijn Theunissen
 */
public class EditCalendarItemDialog extends JDialog implements ActionListener {

	/** Automatically generated GUI components */
	private WebDateField		m_datepicker;

	private JTextField			m_textField;
	private JSpinner			m_timeSpinner;
	private DateEditor			m_timeEditor;
	private JButton				m_btnSave, m_btnRemove;
	private JLabel				m_lblName;
	private JLabel				m_lblDescription;
	private WebTextArea			m_textArea;
	private JLabel				m_lblStartDate;
	private JLabel				m_lblDuration;
	private JSpinner			m_spinner;
	private JLabel				m_lblHours;
	private JSpinner			m_spinner_1;
	private JLabel				m_lblMinutes;
	private ApplicationSettings	m_settings	= ApplicationSettings.getInstance();	// for localized strings.
	private CalendarItem		m_calendarItem;

	/**
	 * Create a new AddtaskDialog
	 * @param item the calendaritem to edit
	 */
	public EditCalendarItemDialog(CalendarItem item) {
		m_calendarItem = item;
		initializeGUIComponents();

		getContentPane().setPreferredSize(new Dimension(500, 300));

		m_btnSave = new JButton(m_settings.getLocalizedMessage("save"));
		m_btnSave.setFont(new Font("Dialog", Font.PLAIN, 12));
		m_btnSave.addActionListener(this);
		m_btnRemove = new JButton(m_settings.getLocalizedMessage("removeci"));
		m_btnRemove.setSize(m_btnSave.getSize());
		m_btnRemove.setFont(new Font("Dialog", Font.PLAIN, 12));
		m_btnRemove.addActionListener(this);
		getContentPane().add(m_btnSave, "cell 1 8,right");
		getContentPane().add(m_btnRemove, "cell 2 8,growx");
		this.setTitle(m_settings.getLocalizedMessage("editcalitem.title"));
		pack();
		this.setLocationRelativeTo(null);
	}

	/**
	 * Automatically generated by window builder
	 */
	private void initializeGUIComponents() {
		getContentPane().removeAll();
		getContentPane().setLayout(new MigLayout("", "[][grow][]", "[][][][][grow][][][][]"));

		m_lblName = new JLabel(m_settings.getLocalizedMessage("lblName"));
		getContentPane().add(m_lblName, "cell 0 3,alignx left");

		m_textField = new JTextField();
		getContentPane().add(m_textField, "cell 1 3 2 1,growx");
		m_textField.setColumns(10);
		m_textField.setText(m_calendarItem.getSummary());

		m_lblDescription = new JLabel(m_settings.getLocalizedMessage("lblDescription"));
		getContentPane().add(m_lblDescription, "cell 0 4,alignx left");

		m_textArea = new WebTextArea();
		m_textArea.setLineWrap(true);
		m_textArea.setText(m_calendarItem.getDescription());
		m_textArea.setWrapStyleWord(true);
		WebScrollPane areaScroll = new WebScrollPane(m_textArea);
		areaScroll.setPreferredSize(new Dimension(200, 150));
		getContentPane().add(areaScroll, "cell 1 4 2 1,grow");

		m_lblStartDate = new JLabel(m_settings.getLocalizedMessage("lblStartDate"));
		getContentPane().add(m_lblStartDate, "cell 0 5,alignx left");

		m_lblDuration = new JLabel(m_settings.getLocalizedMessage("lblDuration"));
		getContentPane().add(m_lblDuration, "cell 0 6,alignx left");

		m_spinner = new JSpinner();
		getContentPane().add(m_spinner, "cell 1 6,growx");
		m_spinner.setValue(VEventHelper.durationToICALDur(m_calendarItem.getDuration()).getHours());

		m_lblHours = new JLabel(m_settings.getLocalizedMessage("hrs"));
		m_lblHours.setFont(new Font("Dialog", Font.PLAIN, 12));
		getContentPane().add(m_lblHours, "cell 2 6");

		m_datepicker = new WebDateField(new Date());
		getContentPane().add(m_datepicker, "cell 1 5,growx");
		m_datepicker.setDate(Date.from(m_calendarItem.getStartDate().toInstant()));

		m_timeSpinner = new JSpinner(new SpinnerDateModel());
		m_timeEditor = new JSpinner.DateEditor(m_timeSpinner, "HH:mm:ss");
		m_timeSpinner.setValue(Date.from(m_calendarItem.getStartDate().toInstant()));

		m_timeSpinner.setEditor(m_timeEditor);
		getContentPane().add(m_timeSpinner, "cell 2 5");

		m_spinner_1 = new JSpinner();
		getContentPane().add(m_spinner_1, "cell 1 7,growx");
		m_spinner_1.setValue(VEventHelper.durationToICALDur(m_calendarItem.getDuration()).getMinutes());

		m_lblMinutes = new JLabel(m_settings.getLocalizedMessage("mins"));
		m_lblMinutes.setFont(new Font("Dialog", Font.PLAIN, 12));
		getContentPane().add(m_lblMinutes, "cell 2 7");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == m_btnSave) {
			String summary = m_textField.getText();
			String desc = m_textArea.getText();

			Duration duration = Duration.ofMinutes(((Integer) m_spinner.getValue()) * 60 + (Integer) m_spinner_1.getValue());
			String time = m_timeEditor.getFormat().format(m_timeSpinner.getValue()); // "HH:mm:ss"
			int hr = Integer.parseInt(time.substring(0, 2));
			int min = Integer.parseInt(time.substring(3, 5));
			int sec = Integer.parseInt(time.substring(6, 8));

			ZonedDateTime zdt = ZonedDateTime.now();
			LocalDate date = m_datepicker.getDate().toInstant().atZone(ApplicationSettings.getInstance().getTimeZone().toZoneId()).toLocalDate();
			LocalTime timestr = LocalTime.of(hr, min, sec);
			zdt = ZonedDateTime.of(date, timestr, ApplicationSettings.getInstance().getTimeZone().toZoneId());

			m_calendarItem.setDescription(desc);
			m_calendarItem.setDuration(duration);
			m_calendarItem.setSummary(summary);
			m_calendarItem.setStartDate(zdt);

			this.dispose();
		} else if (e.getSource() == m_btnRemove) {
			m_calendarItem.remove();
			this.dispose();
		}
	}
}
