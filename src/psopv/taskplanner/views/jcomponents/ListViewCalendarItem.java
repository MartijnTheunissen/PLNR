package psopv.taskplanner.views.jcomponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import psopv.taskplanner.models.CalendarItem;

import com.alee.laf.panel.WebPanel;

/**
 * 
 * Calendar item in the list view to display
 *
 * @since May 10, 2014 12:27:29 PM
 * @author Martijn Theunissen
 */
public class ListViewCalendarItem extends WebPanel implements Observer {
	private Observable			m_model;
	private ListViewDatePane	m_datePane;
	private ListViewInfoPane	m_infoPane;
	private CalendarItem		m_calendarItem;

	/**
	 * Initialize the calendaritemview
	 * @param calendarItem the calendar item to display
	 * @param drawDate if we should draw the date
	 * @param calendarColor the color of the calendar
	 */
	public ListViewCalendarItem(CalendarItem calendarItem, boolean drawDate, Color calendarColor) {
		m_model = calendarItem;
		m_model.addObserver(this);
		this.setSize(600, 80);
		this.setPreferredSize(new Dimension(600, 80));
		this.setMaximumSize(new Dimension(super.getMaximumSize().width, 80));
		m_calendarItem = calendarItem;

		this.setLayout(new BorderLayout());
		m_datePane = new ListViewDatePane(calendarItem, drawDate);
		m_infoPane = new ListViewInfoPane(calendarItem, calendarColor);
		this.add(m_datePane, BorderLayout.WEST);
		this.add(m_infoPane, BorderLayout.CENTER);
	}

	/**
	 * get the calendar item of the listview
	 * @return the calendaritem of the view
	 */
	public CalendarItem getCalendarItem() {
		return m_calendarItem;
	}

	/**
	 * set if one should draw the datepane
	 * @param b wether or not it should be drawn
	 */
	public void setDrawDate(boolean b) {
		m_datePane.setDrawDate(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		m_model = arg0;
	}

}
