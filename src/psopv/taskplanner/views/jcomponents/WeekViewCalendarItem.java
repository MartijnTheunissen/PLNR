/**
 * 
 */
package psopv.taskplanner.views.jcomponents;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Observable;

import javax.swing.JLabel;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.controllers.WeekViewCalendarItemController;
import psopv.taskplanner.models.CalendarItem;
import psopv.taskplanner.views.WeekView;
import be.uhasselt.oo2.mvc.Controller;
import be.uhasselt.oo2.mvc.View;

/**
 * 
 * $Rev:: 139                                                  $:  Revision of last commit<br>
 * $Author:: tom.knaepen                                       $:  Author of last commit<br>
 * $Date:: 2014-06-03 13:25:22 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * A CalendarItem graphical representation
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - tom.knaepen: placeholder <br>
 * 2 - tom.knaepen: changed to label, added style, observer<br>
 * 3 - <br>
 *
 * @since 11-mei-2014 00:50:50
 * @author tom.knaepen
 */
public class WeekViewCalendarItem extends JLabel implements View {

	//TODO: listener, change position when date/time/title/... changes
	private ApplicationSettings	m_appSettings;
	private Observable			m_model;
	private WeekView			m_parent;
	private Controller			m_controller;
	private String				m_htmlStyle		= "<html><body style='padding:5px'>";
	private String				m_htmlStyleEnd	= "</body></html>";
	private int					m_dayOffset;											// 1-7
	private double				m_hourOffset, m_duration;								// 0 - 23
	private Color				m_backgroundColor;

	public WeekViewCalendarItem(WeekView parent, CalendarItem item, Color color) {
		m_appSettings = ApplicationSettings.getInstance();
		m_backgroundColor = color;

		setOpaque(true);
		m_parent = parent;
		//setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, color.darker()));
		item.addObserver(this);
		update(item, null);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (!isOpaque()) {
			super.paintComponent(g);
			return;
		}
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(m_backgroundColor);

		GradientPaint gp = new GradientPaint(0, 0, m_backgroundColor, 0, 1500, m_backgroundColor.darker());
		GradientPaint border = new GradientPaint(0, 0, m_backgroundColor.darker(), 0, 1500, m_backgroundColor.darker().darker());
		g2.setPaint(gp);
		g2.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		g2.setPaint(border);
		g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

		setOpaque(false);
		super.paintComponent(g);
		setOpaque(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		setModel(arg0);
		CalendarItem model = (CalendarItem) m_model;
		Duration dur = Duration.between(model.getStartDate(), m_parent.getDate().atStartOfDay(m_appSettings.getTimeZone().toZoneId())).abs();
		m_dayOffset = (int) dur.toDays() + 1;
		m_hourOffset = model.getStartDate().getHour() + (model.getStartDate().getMinute() / 60.0);
		m_duration = model.getDuration().getSeconds() / 3600.0; // seconds / (3600seconds/hour) 

		String displayString = model.getSummary();//+ "<br>" + model.getDescription();
		if (displayString.indexOf(';') != -1) // UHasselt kalender verfraaien
			setText(m_htmlStyle + displayString.replaceAll(";", "<br>") + m_htmlStyleEnd);
		//setText(m_htmlStyle + displayString.substring(0, displayString.indexOf(';')));
		else
			setText(m_htmlStyle + displayString + m_htmlStyleEnd);

		repaint();
	}

	@Override
	public void setText(String text) {
		int numberOfLines = (int) Math.floor(m_duration * 2);
		String[] lines = text.split("<br>");
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < numberOfLines && i < lines.length; i++)
			result.append(lines[i]).append("<br>");
		super.setText(result.toString());
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

	/**
	 * Returns the color associated with this item('s calendar).
	 * @return
	 */
	public Color getColor() {
		return m_backgroundColor;
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
		return new WeekViewCalendarItemController(model);
	}

	public int getDayOffset() {
		return m_dayOffset;
	}

	public double getHourOffset() {
		return m_hourOffset;
	}

	public double getDuration() {
		return m_duration;
	}

	/**
	 * @param dayOffset the day offset
	 * @param hourOffset the hour offset
	 */
	public void setDate(int dayOffset, int hourOffset) {
		m_dayOffset = dayOffset;
		m_hourOffset = hourOffset;
		LocalDate week = m_parent.getDate();
		ZonedDateTime day = week.atStartOfDay(m_appSettings.getTimeZone().toZoneId()).plusDays(dayOffset - 1);
		long startHour = (long) Math.floor(m_hourOffset);
		ZonedDateTime startDate = day.plusHours(startHour);
		((CalendarItem) getModel()).setStartDate(startDate);
	}
}
