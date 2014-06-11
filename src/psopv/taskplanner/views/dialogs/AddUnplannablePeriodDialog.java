/**
 * 
 */
package psopv.taskplanner.views.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerDateModel;

import net.miginfocom.swing.MigLayout;
import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.config.UnplannablePeriod;
import psopv.taskplanner.config.UnplannablePeriodList;

import com.alee.extended.list.CheckBoxListModel;
import com.alee.extended.list.WebCheckBoxList;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.spinner.WebSpinner;

/**
 * 
 * $Rev:: 126                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-05-31 00:09:10 +0200 (Sat, 31 May 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * Dialog for adding an unplannable period dialog
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version<br>
 * 2 - <br>
 * 3 - <br>
 *
 * @since May 30, 2014 8:34:27 PM
 * @author Martijn Theunissen
 */
public class AddUnplannablePeriodDialog extends WebDialog {

	private ApplicationSettings	m_appSettings;
	private WebCheckBoxList		m_weekList;
	private WebSpinner			m_startTimeSpinner, m_endTimeSpinner;
	private JSpinner.DateEditor	m_startTimeEditor, m_endTimeEdittor;
	private JTabbedPane			m_tabbedPane;

	public AddUnplannablePeriodDialog() {
		setSize(500, 350);
		m_appSettings = ApplicationSettings.getInstance();

		initializeComponents();
	}

	/**
	 * Automatically generated using window builder
	 */
	private void initializeComponents() {
		getContentPane().setLayout(new BorderLayout(0, 0));

		m_tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		getContentPane().add(m_tabbedPane);

		JPanel buttonPane = new JPanel();
		JButton okButton = new JButton(m_appSettings.getLocalizedMessage("save"));
		JButton cancelButton = new JButton(m_appSettings.getLocalizedMessage("cancel"));

		JPanel panel_1 = new JPanel();
		m_tabbedPane.addTab(m_appSettings.getLocalizedMessage("daily"), null, panel_1, null);
		panel_1.setLayout(new MigLayout("", "[grow][][]", "[][][grow]"));

		JLabel lblStartTime_1 = new JLabel(m_appSettings.getLocalizedMessage("starttime"));
		panel_1.add(lblStartTime_1, "cell 0 0");
		m_startTimeSpinner = new WebSpinner();
		SpinnerDateModel model3 = new SpinnerDateModel();
		m_startTimeSpinner.setModel(model3);
		m_startTimeSpinner.setValue(new Date());
		m_startTimeEditor = new JSpinner.DateEditor(m_startTimeSpinner, "HH:mm:ss");
		m_startTimeSpinner.setEditor(m_startTimeEditor);
		panel_1.add(m_startTimeSpinner, "cell 2 0,growx");

		JLabel lblEndTime_1 = new JLabel(m_appSettings.getLocalizedMessage("endtime"));
		panel_1.add(lblEndTime_1, "cell 0 1");
		m_endTimeSpinner = new WebSpinner();
		SpinnerDateModel model4 = new SpinnerDateModel();
		m_endTimeSpinner.setModel(model4);
		m_endTimeSpinner.setValue(new Date());
		m_endTimeEdittor = new JSpinner.DateEditor(m_endTimeSpinner, "HH:mm:ss");
		m_endTimeSpinner.setEditor(m_endTimeEdittor);
		panel_1.add(m_endTimeSpinner, "cell 2 1,growx");

		JScrollPane scrollPane = new JScrollPane();
		m_weekList = new WebCheckBoxList(createCheckboxModel());
		scrollPane.setViewportView(m_weekList);
		panel_1.add(scrollPane, "cell 0 2 3 1,grow");

		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		okButton.addActionListener(this::clickedOk);
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		cancelButton.addActionListener(this::clickedCancel);
		buttonPane.add(cancelButton);
	}

	/**
	 * Create a CheckBox model
	 * @return the CheckBox list model
	 */
	private CheckBoxListModel createCheckboxModel() {
		CheckBoxListModel model = new CheckBoxListModel();
		for (int i = 1; i <= 7; i++) {
			model.addCheckBoxElement(makeCapitalized(DayOfWeek.of(i).getDisplayName(TextStyle.FULL, m_appSettings.getLocale())));
		}
		return model;
	}

	/**
	 * User clicked save/ok
	 * @param e the event that occurred
	 */
	private void clickedOk(ActionEvent e) {
		saveChanges();
		setVisible(false);
		dispose();
	}

	/**
	 * User clicked cancel
	 * @param e the ActionEvent that occurred
	 */
	private void clickedCancel(ActionEvent e) {
		setVisible(false);
		dispose();
	}

	/**
	 * Save all changes
	 */
	private void saveChanges() {
		UnplannablePeriodList upl = m_appSettings.getUnplannablePeriods();
		LocalTime start = getLocalTime(m_startTimeEditor.getFormat().format(m_startTimeSpinner.getValue()));
		LocalTime end = getLocalTime(m_endTimeEdittor.getFormat().format(m_endTimeSpinner.getValue()));
		String repeat = "";
		CheckBoxListModel model = m_weekList.getCheckBoxListModel();
		for (int i = 0; i < model.getSize(); i++) {
			if (model.isCheckBoxSelected(i))
				repeat += (i + 1);
		}
		upl.addUnplannablePeriod(new UnplannablePeriod(start, end, repeat));
		m_appSettings.setUnplannablePeriodList(upl);
	}

	/**
	 * Make a capitalized string from a string
	 * @param other the string to capitalize
	 * @return the capitalized string
	 */
	private String makeCapitalized(String other) {
		if (other.isEmpty())
			return "";
		return ("" + other.charAt(0)).toUpperCase() + other.substring(1);
	}

	/**
	 * Get the local time of a TimeSpinner string
	 * @param time string representing time
	 * @return the time string
	 */
	private LocalTime getLocalTime(String time) {
		int hr = Integer.parseInt(time.substring(0, 2));
		int min = Integer.parseInt(time.substring(3, 5));
		int sec = Integer.parseInt(time.substring(6, 8));

		return LocalTime.of(hr, min, sec);
	}

}
