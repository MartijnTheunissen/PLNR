package psopv.taskplanner.views.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TimeZone;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import psopv.taskplanner.config.ApplicationSettings;

import com.alee.laf.button.WebButton;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * 
 * A dialog to change the application settings.
 *
 * @since 28-apr.-2014 14:32:01
 * @author Tom Knaepen
 */
public class SettingsDialog extends JDialog {

	private ApplicationSettings				m_appSettings;
	private JPanel							m_contentPanel;
	private JPanel							m_minutePanel;
	private LinkedHashMap<String, JLabel>	m_labels;
	private String[]						LABEL_NAMES	= { "lblLocale", "lblLanguage", "lblTimeZone", "lblRegion", "lblPlanWeekends", "lblMinimumBreak", "mins", "lblDontPlanBefore", "lblDontPlanAfter" };
	private JLabel							m_lblPlanningTitle;
	private JComboBox<String>				m_comboLanguage, m_comboTimeZone, m_comboZone;
	private DateEditor						m_planBeforeEditor, m_planAfterEditor;
	private JSeparator						m_separator, m_separator2;
	private JCheckBox						m_checkWeekends;
	private JSpinner						m_spinPlanBefore, m_spinPlanAfter;
	private JButton							m_okButton, m_cancelButton;
	private WebButton						m_extras;

	private Component						m_horizontalStrut;

	/**
	 * Create the dialog.
	 * @param parent the parent of the dialog
	 */
	public SettingsDialog(JFrame parent) {
		super(parent, ApplicationSettings.getInstance().getLocalizedMessage("menu.options.settings"));
		m_appSettings = ApplicationSettings.getInstance();
		m_contentPanel = new JPanel();
		getContentPane().setLayout(new BorderLayout());
		m_contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(m_contentPanel, BorderLayout.CENTER);
		m_contentPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));
		initializeGUIComponents();
		pack();
		setLocationRelativeTo(parent);
	}

	/**
	 * Initialize the GUI with correct values etc.
	 */
	private void initializeGUIComponents() {
		// Instantiate all components first
		m_labels = new LinkedHashMap<String, JLabel>();
		for (String s : LABEL_NAMES)
			m_labels.put(s, new JLabel(m_appSettings.getLocalizedMessage(s)));
		m_separator = new JSeparator();
		m_separator2 = new JSeparator();
		m_comboLanguage = new JComboBox<String>();
		m_comboTimeZone = new JComboBox<String>();
		m_comboZone = new JComboBox<String>();
		m_lblPlanningTitle = new JLabel(m_appSettings.getLocalizedMessage("planning"));
		m_checkWeekends = new JCheckBox("");
		m_minutePanel = new JPanel(new BorderLayout());
		m_spinPlanBefore = new JSpinner(new SpinnerDateModel());
		m_spinPlanAfter = new JSpinner(new SpinnerDateModel());
		m_planBeforeEditor = new JSpinner.DateEditor(m_spinPlanBefore, "HH:mm:ss");
		m_planAfterEditor = new JSpinner.DateEditor(m_spinPlanAfter, "HH:mm:ss");
		JPanel buttonPane = new JPanel();
		m_okButton = new JButton(m_appSettings.getLocalizedMessage("save"));
		m_cancelButton = new JButton(m_appSettings.getLocalizedMessage("cancel"));

		m_labels.get("lblLocale").setVerticalAlignment(SwingConstants.TOP);
		m_labels.get("lblLocale").setFont(new Font("Tahoma", Font.PLAIN, 13));
		m_labels.get("lblLocale").setHorizontalAlignment(SwingConstants.CENTER);
		m_contentPanel.add(m_labels.get("lblLocale"), "2, 2, left, default");

		m_contentPanel.add(m_separator2, "4, 2, 5, 1");

		m_contentPanel.add(m_labels.get("lblLanguage"), "4, 4, left, default");

		m_contentPanel.add(m_comboLanguage, "6, 4, fill, default");
		for (String s : m_appSettings.getAvailableLocales().keySet())
			m_comboLanguage.addItem(s);
		m_comboLanguage.setSelectedItem(m_appSettings.getLocale().getDisplayLanguage(m_appSettings.getLocale()));
		m_comboLanguage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String selectedLanguage = (String) m_comboLanguage.getSelectedItem();
				for (String s : LABEL_NAMES)
					m_labels.get(s).setText(m_appSettings.getLocalizedMessage(s, selectedLanguage));
				m_okButton.setText(m_appSettings.getLocalizedMessage("save", selectedLanguage));
				m_cancelButton.setText(m_appSettings.getLocalizedMessage("cancel", selectedLanguage));
				m_extras.setText(m_appSettings.getLocalizedMessage("exceptions", selectedLanguage));
				setTitle(m_appSettings.getLocalizedMessage("menu.options.settings", selectedLanguage));
				pack();
			}
		});
		m_appSettings.getAvailableLocales().get((String) m_comboLanguage.getSelectedItem());

		m_contentPanel.add(m_labels.get("lblTimeZone"), "4, 6, left, default");

		m_comboTimeZone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_comboZone.removeAllItems();
				String selectedTimeZone = (String) m_comboTimeZone.getSelectedItem();
				ArrayList<String> zones = new ArrayList<String>();
				for (String tz : TimeZone.getAvailableIDs(TimeZone.getTimeZone(selectedTimeZone).getRawOffset())) {
					if (!zones.contains(tz) && tz.matches("(\\w*)/(\\w*)")) {
						zones.add(tz);
						m_comboZone.addItem(tz);
					}
				}
				m_comboZone.setSelectedItem(m_appSettings.getTimeZone().getID());
				pack();
			}
		});
		m_contentPanel.add(m_comboTimeZone, "6, 6, fill, default");
		for (TimeZone tz : m_appSettings.getAvailableTimeZones()) {
			//String s = String.format("%s (%s)", tz.getID(), tz.getDisplayName(Locale.UK));
			//TODO: display "GMT-08:00 (Pacific Standard Time)"
			m_comboTimeZone.addItem(tz.getDisplayName(m_appSettings.getLocale()));
		}

		for (int i = -12; i <= 12; i++) {
			String timeZoneId = "GMT";
			if (i >= 0)
				timeZoneId += "+";
			timeZoneId += i;
			if (TimeZone.getTimeZone(timeZoneId).getRawOffset() == m_appSettings.getTimeZone().getRawOffset()) {
				m_comboTimeZone.setSelectedItem(TimeZone.getTimeZone(timeZoneId).getDisplayName(m_appSettings.getLocale()));
			}
		}

		m_contentPanel.add(m_labels.get("lblRegion"), "4, 8");

		m_contentPanel.add(m_comboZone, "6, 8, left, default");

		m_contentPanel.add(m_comboZone, "6, 8, left, default");

		m_lblPlanningTitle.setVerticalAlignment(SwingConstants.TOP);
		m_lblPlanningTitle.setFont(new Font("Tahoma", Font.PLAIN, 13));
		m_lblPlanningTitle.setHorizontalAlignment(SwingConstants.CENTER);
		m_contentPanel.add(m_lblPlanningTitle, "2, 10, left, default");

		m_contentPanel.add(m_separator, "4, 10, 5, 1");

		m_contentPanel.add(m_labels.get("lblPlanWeekends"), "4, 12, left, default");

		m_checkWeekends.setSelected(m_appSettings.isPlannableOnWeekends());
		m_contentPanel.add(m_checkWeekends, "6, 12, left, default");

		// m_contentPanel.add(m_labels.get("lblMinimumBreak"), "4, 14, left, default");

		//m_contentPanel.add(m_minutePanel, "6, 14, fill, top");
		//m_minutePanel.add(m_spinnerBreak, BorderLayout.CENTER);
		//int minBreakBetweenTasks = (int) m_appSettings.getMinimumBreakBetweenTasks().toMinutes();
		//m_spinnerBreak.setModel(new SpinnerNumberModel(minBreakBetweenTasks, new Integer(0), null, new Integer(1)));
		m_horizontalStrut = Box.createHorizontalStrut(5);
		//JPanel p = new JPanel(new BorderLayout());
		//p.add(m_horizontalStrut, BorderLayout.WEST);
		//p.add(m_labels.get("mins"), BorderLayout.CENTER);
		//m_minutePanel.add(p, BorderLayout.EAST);

		m_contentPanel.add(m_labels.get("lblDontPlanBefore"), "4, 16, left, default");

		m_spinPlanBefore.setValue(Date.from(m_appSettings.getStartPlanningTime().atDate(LocalDate.now()).atZone(m_appSettings.getTimeZone().toZoneId()).toInstant()));
		m_spinPlanAfter.setValue(Date.from(m_appSettings.getEndPlanningTime().atDate(LocalDate.now()).atZone(m_appSettings.getTimeZone().toZoneId()).toInstant()));
		m_planBeforeEditor.getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
		m_planAfterEditor.getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
		m_spinPlanBefore.setEditor(m_planBeforeEditor);
		m_spinPlanAfter.setEditor(m_planAfterEditor);

		m_contentPanel.add(m_spinPlanBefore, "6, 16, fill, default");

		m_contentPanel.add(m_labels.get("lblDontPlanAfter"), "4, 18, left, default");

		m_contentPanel.add(m_spinPlanAfter, "6, 18, fill, default");

		m_extras = new WebButton(m_appSettings.getLocalizedMessage("exceptions"));
		m_extras.addActionListener(this::clickedExtra);
		m_contentPanel.add(m_extras, "4, 20, left, default");

		// fix spinner's width to be equal to the text fields
		//Dimension spinSize = new Dimension(m_spinPlanBefore.getPreferredSize().width, m_spinnerBreak.getPreferredSize().height);
		//m_spinnerBreak.setPreferredSize(spinSize);
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		m_okButton.addActionListener(this::clickedOk);
		buttonPane.add(m_okButton);
		getRootPane().setDefaultButton(m_okButton);
		m_cancelButton.addActionListener(this::clickedCancel);
		buttonPane.add(m_cancelButton);
	}

	private void clickedExtra(ActionEvent e) {
		setVisible(false);
		dispose();
		PlanningExceptionSettingsDialog pesd = new PlanningExceptionSettingsDialog();
		pesd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pesd.setSize(500, 300);
		pesd.setVisible(true);
	}

	private void clickedOk(ActionEvent e) {
		saveChanges();
		setVisible(false);
		dispose();
	}

	private void clickedCancel(ActionEvent e) {
		setVisible(false);
		dispose();
	}

	/**
	 * Save all changes to the application model.
	 */
	private void saveChanges() {
		m_appSettings.setLocale((String) m_comboLanguage.getSelectedItem());
		m_appSettings.setPlannableOnWeekends(m_checkWeekends.isSelected());
		//m_appSettings.setMinimumBreakBetweenTasks(Duration.ofMinutes((int) m_spinnerBreak.getValue()));
		LocalTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(((Date) m_spinPlanBefore.getValue()).getTime()), m_appSettings.getTimeZone().toZoneId()).toLocalTime();
		m_appSettings.setStartPlanningTime(start);
		LocalTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(((Date) m_spinPlanAfter.getValue()).getTime()), m_appSettings.getTimeZone().toZoneId()).toLocalTime();
		m_appSettings.setEndPlanningTime(end);
		m_appSettings.setTimeZone(TimeZone.getTimeZone((String) m_comboZone.getSelectedItem()));
	}
}
