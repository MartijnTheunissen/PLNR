/**
 * 
 */
package psopv.taskplanner.views.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.config.UnplannablePeriod;
import psopv.taskplanner.config.UnplannablePeriodList;

import com.alee.extended.list.CheckBoxListModel;
import com.alee.extended.list.WebCheckBoxList;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;

/**
 * 
 * $Rev:: 126                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-05-31 00:09:10 +0200 (Sat, 31 May 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * Planning exception dialog for editting when there should not be planned
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version<br>
 * 2 - <br>
 * 3 - <br>
 *
 * @since May 30, 2014 5:18:27 PM
 * @author martijn
 */
public class PlanningExceptionSettingsDialog extends WebDialog implements Observer {

	private ApplicationSettings		m_appSettings;
	private WebPanel				m_contentPanel;
	private WebPanel				panel;
	private WebButton				btnAdd;
	private WebButton				btnRemove;
	private WebCheckBoxList			list;
	private UnplannablePeriodList	m_unplannableList;

	/**
	 * Create the dialog.
	 */
	public PlanningExceptionSettingsDialog() {
		m_appSettings = ApplicationSettings.getInstance();
		m_appSettings.addObserver(this);
		initializeGUIComponents();
	}

	/**
	 * Create the CheckBoxListModel
	 * @return the CheckBoxListModel of the UnplannablePeriodList objects
	 */
	private CheckBoxListModel createExceptionListModel() {
		CheckBoxListModel m_checkBoxListModel = new CheckBoxListModel();

		ArrayList<UnplannablePeriod> periods = m_unplannableList.getList();
		for (UnplannablePeriod period : periods) {
			m_checkBoxListModel.addCheckBoxElement(period, false);
		}
		return m_checkBoxListModel;
	}

	/**
	 * Initialize the GUI with correct values etc.
	 */
	private void initializeGUIComponents() {
		// Instantiate all components first
		this.setTitle(m_appSettings.getLocalizedMessage("pesd.title"));
		m_unplannableList = m_appSettings.getUnplannablePeriods();
		m_contentPanel = new WebPanel();
		getContentPane().setLayout(new BorderLayout());
		m_contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(m_contentPanel, BorderLayout.CENTER);
		m_contentPanel.setLayout(new BorderLayout(0, 0));

		panel = new WebPanel();
		m_contentPanel.add(panel);
		panel.setLayout(new MigLayout("", "[grow][][]", "[grow][]"));

		JScrollPane scrollPane = new JScrollPane();

		list = new WebCheckBoxList(createExceptionListModel());
		scrollPane.setViewportView(list);

		panel.add(scrollPane, "cell 0 0 3 1,grow");

		btnAdd = new WebButton(m_appSettings.getLocalizedMessage("pesd.add"));
		btnAdd.addActionListener(this::clickedAdd);
		panel.add(btnAdd, "cell 1 1");

		btnRemove = new WebButton(m_appSettings.getLocalizedMessage("pesd.remsel"));
		btnRemove.addActionListener(this::clickedRemove);
		panel.add(btnRemove, "cell 2 1");
	}

	/**
	 * invoked when the user clicked add
	 * @param e the ActionEvent that happened
	 */
	private void clickedAdd(ActionEvent e) {

		AddUnplannablePeriodDialog aupd = new AddUnplannablePeriodDialog();
		aupd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		aupd.setModal(true);
		aupd.setVisible(true);
	}

	/**
	 * invoked when the user clicked add
	 * @param e the ActionEvent that happened
	 */
	private void clickedRemove(ActionEvent e) {
		CheckBoxListModel model = list.getCheckBoxListModel();
		UnplannablePeriodList list = ApplicationSettings.getInstance().getUnplannablePeriods();

		for (int i = 0; i < model.getSize(); i++) {
			if (model.isCheckBoxSelected(i)) {
				list.removeUnplannablePeriod((UnplannablePeriod) model.getElementAt(i).getUserObject());
			}
		}
		m_appSettings.setUnplannablePeriodList(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		getContentPane().removeAll();
		initializeGUIComponents();
		this.revalidate();
		this.repaint();
	}
}
