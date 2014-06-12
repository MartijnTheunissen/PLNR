package psopv.taskplanner.views;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.models.Calendar;
import psopv.taskplanner.models.TaskPlannerModel;
import be.uhasselt.oo2.mvc.Controller;

import com.alee.extended.list.CheckBoxCellData;
import com.alee.extended.list.CheckBoxListModel;
import com.alee.extended.list.WebCheckBoxList;
import com.alee.extended.list.WebCheckBoxListCellRenderer;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;

/**
 * 
 * This is a view that represents the calendars in the application.
 *
 * @since May 11, 2014 4:50:21 PM
 * @author Martijn Theunissen
 * @author Tom Knaepen
 */
public class CalendarList implements Observer, MouseListener {

	private WebPanel			m_container;
	private Observable			m_model;
	private Controller			m_controller;
	private ArrayList<Calendar>	m_calendars;
	private WebCheckBoxList		m_list;
	private WebScrollPane		m_scrollPane;
	private CheckBoxListModel	m_checkBoxListModel;

	/**
	 * Initialize the calendarlist
	 * @param model  the model to set
	 * @param controller controller to set
	 */
	public CalendarList(Observable model, Controller controller) {
		m_model = model;
		model.addObserver(this);
		ApplicationSettings.getInstance().addObserver(this);
		m_controller = controller;
		m_calendars = new ArrayList<Calendar>();
		m_container = new WebPanel();
		m_container.setLayout(new BorderLayout());

		initGUI();
		m_scrollPane = new WebScrollPane(m_list);
	}

	/**
	 * initialize the gui
	 */
	private void initGUI() {
		m_list = new WebCheckBoxList(createCalendarData());
		m_list.setCellRenderer(new WebCheckBoxListCellRenderer()); // Possible to display colors, use CustomCheckBoxListCellRenderer
		m_list.getWebListCellRenderer();
		m_list.setSelectedIndex(0);
		m_list.setEditable(false);
		m_list.addMouseListener(this);
		m_list.setUnselectable(true);
	}

	/**
	 * @return the checkboxlist model
	 */
	private CheckBoxListModel createCalendarData() {
		m_checkBoxListModel = new CheckBoxListModel();
		if (m_calendars.isEmpty()) {
			m_checkBoxListModel.addCheckBoxElement(ApplicationSettings.getInstance().getLocalizedMessage("cl.noadd"));

			return m_checkBoxListModel;
		}
		for (int i = 0; i < m_calendars.size(); i++)
			m_checkBoxListModel.addCheckBoxElement(m_calendars.get(i), m_calendars.get(i).isActive());
		return m_checkBoxListModel;
	}

	/**
	 * @return the ui of the view
	 */
	public WebScrollPane getUI() {
		return m_scrollPane;
	}

	/**
	 * Update the gui (change the list model)
	 */
	@SuppressWarnings("unchecked")
	private void updateGUI() {
		m_list.setModel(createCalendarData());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof TaskPlannerModel) {
			m_model = o;
			m_calendars = ((TaskPlannerModel) o).getCalendars();
		}
		updateGUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		for (int i = 0; i < m_checkBoxListModel.size(); i++) {
			boolean selected = m_checkBoxListModel.isCheckBoxSelected(i);
			if (m_checkBoxListModel.getElementAt(i).getUserObject() instanceof Calendar)
				if (selected != ((Calendar) m_checkBoxListModel.getElementAt(i).getUserObject()).isActive())
					((TaskPlannerModel) m_model).setCalendarActive((Calendar) m_checkBoxListModel.getElementAt(i).getUserObject(), selected);
		}
		if (SwingUtilities.isRightMouseButton(e)) {
			int index = m_list.locationToIndex(e.getPoint());
			m_list.setSelectedIndex(index);
			if (index <= m_list.getMaxSelectionIndex() && m_list.getCellBounds(index, index).contains(e.getPoint())) {

				JPopupMenu menu = new JPopupMenu();
				JMenuItem remove = new JMenuItem(ApplicationSettings.getInstance().getLocalizedMessage("removeci"));
				menu.add(remove);
				Object sel = (Object) ((CheckBoxCellData) m_list.getModel().getElementAt(index)).getUserObject();
				Calendar selected;
				if (sel instanceof String)
					return;
				else {
					selected = (Calendar) sel;
					remove.addActionListener((l) -> {
						removeCalendarClicked(selected);
					});
					menu.show(m_scrollPane, (int) e.getX(), (int) e.getY());
				}
			}
		}
	}

	private void removeCalendarClicked(Calendar cal) {
		((TaskPlannerModel) m_model).removeCalendar(cal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}
}
