package psopv.taskplanner.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Observable;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.config.UnplannablePeriod;
import psopv.taskplanner.controllers.TaskPlannerController;
import psopv.taskplanner.models.Calendar;
import psopv.taskplanner.models.CalendarItem;
import psopv.taskplanner.models.TaskPlannerUpdate;
import psopv.taskplanner.models.TaskPlannerUpdate.TaskPlannerChange;
import psopv.taskplanner.views.dialogs.AddCalendarItemDialog;
import psopv.taskplanner.views.jcomponents.BackgroundPanel;
import psopv.taskplanner.views.jcomponents.BackgroundPanel.BackgroundColor;
import psopv.taskplanner.views.jcomponents.DatePickerButton;
import psopv.taskplanner.views.jcomponents.LayeredDnDPane;
import psopv.taskplanner.views.jcomponents.WeekViewCalendarItem;
import be.uhasselt.oo2.mvc.Controller;
import be.uhasselt.oo2.mvc.View;

import com.alee.extended.date.DateSelectionListener;
import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.button.WebButton;

/**
 * 
 * Represents a calendar week (GUI)
 *
 * @since 10-mei-2014 20:47:13
 * @author tom.knaepen
 */
public class WeekView extends JPanel implements View {

	private ApplicationSettings									m_appSettings;
	private LayeredDnDPane										m_layeredPane;
	private Observable											m_model;
	private Controller											m_controller;
	private LocalDate											m_currentWeek;
	private LinkedHashMap<CalendarItem, WeekViewCalendarItem>	m_calendarItems;
	private JPanel												m_days, m_hours, m_calendarPane, m_dayPadding, m_dayWrapper;
	private DatePickerButton									m_datePicker;
	private JScrollPane											m_scrollPane;
	private Set<Calendar>										m_calendars;
	private String												HTML_LABEL_STYLE	= "<html><body style='padding:5px'>";

	/**
	 * Constructor, sets week view to current date
	 * @param model the model to set
	 * @param controller the controller to set
	 */
	public WeekView(Observable model, Controller controller) {
		setModel(model);
		model.addObserver(this);
		setController(controller);
		m_appSettings = ApplicationSettings.getInstance();
		m_appSettings.addObserver(this);
		m_calendars = new HashSet<Calendar>();
		m_calendarItems = new LinkedHashMap<CalendarItem, WeekViewCalendarItem>();
		m_currentWeek = LocalDate.now().minusDays(LocalDate.now().get(ChronoField.DAY_OF_WEEK) - 1);
		initGUI();
	}

	private void initGUI() {
		m_layeredPane = new LayeredDnDPane();
		m_layeredPane.setPreferredSize(new Dimension(400, 1000));
		m_layeredPane.addMouseListener(new DNDMouseListener());
		colorGrid();
		m_calendarPane = new JPanel(new BorderLayout());
		m_calendarPane.add(m_layeredPane, BorderLayout.CENTER);
		m_scrollPane = new JScrollPane(m_calendarPane);
		m_scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		m_scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		m_scrollPane.setPreferredSize(new Dimension(400, 600));

		m_hours = new JPanel(new GridLayout(24, 1));
		Dimension hourSize = new Dimension(40, 1);
		for (int i = 0; i < 24; i++) {
			JLabel hour = new JLabel(HTML_LABEL_STYLE + String.format("%02d", i) + ":00");
			hour.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.DARK_GRAY));
			hour.setVerticalAlignment(SwingConstants.NORTH);
			m_hours.add(hour);
			hourSize = hour.getPreferredSize();
		}
		m_hours.setPreferredSize(new Dimension(hourSize.width, 600));

		m_dayWrapper = new JPanel(new BorderLayout());
		m_dayPadding = new JPanel();
		m_dayPadding.setPreferredSize(hourSize);
		m_dayWrapper.add(m_dayPadding, BorderLayout.WEST);
		m_days = new JPanel();
		m_dayWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
		m_days.setLayout(new GridLayout(1, 7));
		for (int i = 0; i < 7; i++) {
			LocalDate date = m_currentWeek.plusDays(i);
			JPanel day = new JPanel(new GridLayout(2, 1));
			JLabel lbl1 = new JLabel(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, m_appSettings.getLocale()).toUpperCase());
			JLabel lbl2 = new JLabel(String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue()));
			lbl1.setHorizontalAlignment(SwingConstants.CENTER);
			lbl2.setHorizontalAlignment(SwingConstants.CENTER);
			day.add(lbl1);
			day.add(lbl2);
			m_days.add(day);
		}
		m_dayWrapper.add(m_days, BorderLayout.CENTER);
		m_scrollPane.setColumnHeaderView(m_dayWrapper);

		m_scrollPane.getViewport().setViewPosition(new Point(0, m_hours.getPreferredSize().height / 2));
		m_calendarPane.add(m_hours, BorderLayout.WEST);
		setLayout(new BorderLayout());
		add(m_scrollPane, BorderLayout.CENTER);

		WebButton previous = new WebButton("<<");
		previous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				moveDate(-1);
			}
		});
		WebButton next = new WebButton(">>");
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				moveDate(1);
			}
		});
		String dateText = m_currentWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		dateText += "   -   " + m_currentWeek.plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		m_datePicker = new DatePickerButton(m_currentWeek, dateText);
		m_datePicker.addDateSelectionListener(new DateSelectionListener() {
			@Override
			public void dateSelected(Date arg0) {
				setDate(m_datePicker.getDate());
			}
		});
		WebButtonGroup buttons = new WebButtonGroup(true, previous, m_datePicker, next);
		buttons.setFocusable(false);
		JPanel buttonWrapper = new JPanel();
		buttonWrapper.add(buttons);
		add(buttonWrapper, BorderLayout.NORTH);
	}

	/**
	 * Add colors to indicate periods where no tasks will be planned
	 */
	private void colorGrid() {
		m_layeredPane.resetColors();
		int day = m_currentWeek.getDayOfWeek().getValue(); // 1-7
		// plan before / after
		int startHour = m_appSettings.getStartPlanningTime().getHour();
		int endHour = m_appSettings.getEndPlanningTime().getHour();
		int startMinutes = m_appSettings.getStartPlanningTime().getMinute();
		int endMinutes = m_appSettings.getEndPlanningTime().getMinute();

		for (int i = 0; i < 7; i++) {
			m_layeredPane.setColoredArea(BackgroundColor.RED, i, 0, 0, startHour, startMinutes);
			m_layeredPane.setColoredArea(BackgroundColor.RED, i, endHour, endMinutes, 23, 59);
			if (!m_appSettings.isPlannableOnWeekends() && (day + i - 1) % 7 >= 5) // if weekend and no planning on weekends
				m_layeredPane.setColoredArea(BackgroundColor.GRAY, i, 0, 0, 23, 59);
		}
		// plan exceptions
		for (UnplannablePeriod period : m_appSettings.getUnplannablePeriods().getList()) {
			for (int i = 0; i < 7; i++)
				if (period.isDayRepeated((day + i) % 7)) {
					int startPHour = period.getStart().getHour();
					int startPMinute = period.getStart().getMinute();
					int endPHour = period.getEnd().getHour();
					int endPMinute = period.getEnd().getMinute();
					m_layeredPane.setColoredArea(BackgroundColor.RED, i, startPHour, startPMinute, endPHour, endPMinute);
				}
		}
	}

	/**
	 * Add a calendar with given color to the week view
	 * @param cal the calendar to add
	 */
	public void addCalendar(Calendar cal) {
		m_calendars.add(cal);
		for (CalendarItem event : cal.getCalendarItems(m_currentWeek, 7)) {
			addCalendarItem(event, cal.getColor(), cal.isActive());
		}
	}

	/**
	 * Remove the calendar
	 * @param cal the calendar to remove
	 */
	public void removeCalendar(Calendar cal) {
		m_calendars.remove(cal);
		for (CalendarItem event : cal.getCalendarItems(m_currentWeek, 7))
			removeCalendarItem(event);
	}

	/**
	 * Add a calendar item with given color to the week view
	 * @param cal
	 * @param c
	 * @param active 
	 */
	private void addCalendarItem(CalendarItem item, Color c, boolean active) {
		WeekViewCalendarItem p = new WeekViewCalendarItem(this, item, c);
		p.setVisible(active);
		m_layeredPane.addItem(p);
		m_calendarItems.put(item, p);
	}

	/**
	 * Add a calendar item to the week view if it exists in one of the calendars.
	 * @param item
	 */
	private void addCalendarItem(CalendarItem item) {
		for (Calendar c : m_calendars) {
			if (c.contains(item)) {
				WeekViewCalendarItem p = new WeekViewCalendarItem(this, item, c.getColor());
				p.setVisible(c.isActive());
				m_layeredPane.addItem(p);
				m_calendarItems.put(item, p);
			}
		}
	}

	/**
	 * Remove a calendar item
	 * @param item the item to remove
	 */
	public void removeCalendarItem(CalendarItem item) {
		// remove from list
		WeekViewCalendarItem calItem = m_calendarItems.remove(item);
		// remove from gui
		m_layeredPane.removeItem(calItem);
	}

	/**
	 * Change the displayed week
	 * @param date
	 */
	private void setDate(LocalDate date) {
		m_currentWeek = date.minusDays(date.get(ChronoField.DAY_OF_WEEK) - 1);
		updateItems();
		updateLabels();
		colorGrid();
	}

	private void moveDate(int days) {
		m_currentWeek = m_currentWeek.plusDays(days);
		updateItems();
		updateLabels();
		colorGrid();
	}

	private void updateItems() {
		m_calendarItems.clear();
		m_layeredPane.removeAll();
		for (Calendar cal : m_calendars) {
			addCalendar(cal);
		}
	}

	private void updateLabels() {
		for (int i = 0; i < 7; i++) {
			LocalDate date = m_currentWeek.plusDays(i);
			JPanel day = (JPanel) m_days.getComponent(i);
			((JLabel) day.getComponent(0)).setText(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, m_appSettings.getLocale()).toUpperCase());
			((JLabel) day.getComponent(1)).setText(String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue()));
		}
		String dateText = m_currentWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		dateText += "   -   " + m_currentWeek.plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		m_datePicker.setText(dateText);
	}

	private void updateCalendar(Calendar cal) {
		for (CalendarItem item : cal.getCalendarItems(m_currentWeek, 7)) {
			WeekViewCalendarItem comp = m_calendarItems.remove(item);
			if (comp != null)
				m_layeredPane.removeItem(comp);
		}
		addCalendar(cal);
	}

	/**
	 * Return the date
	 * @return the date
	 */
	public LocalDate getDate() {
		return m_currentWeek;
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

	/*
	 * (non-Javadoc) Update view if an item/calendar was added/removed
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 != null && arg1 instanceof TaskPlannerUpdate) { // Update from TaskPlannerModel
			TaskPlannerUpdate update = (TaskPlannerUpdate) arg1;
			TaskPlannerChange status = update.getStatus();
			if (status == TaskPlannerChange.ADDED) {// item was added
				if (update.getItem() instanceof Calendar) {
					addCalendar((Calendar) update.getItem());
				} else if (update.getItem() instanceof CalendarItem) {
					addCalendarItem((CalendarItem) update.getItem());
				}
			} else if (status == TaskPlannerChange.VISIBLE || status == TaskPlannerChange.INVISIBLE)
				if (update.getItem() instanceof Calendar) {
					boolean visible = (status == TaskPlannerChange.VISIBLE) ? true : false;
					makeVisible((Calendar) update.getItem(), visible);
				} else {
				}
			else if (status == TaskPlannerChange.REMOVED) {
				if (update.getItem() instanceof Calendar) {
					removeCalendar((Calendar) update.getItem());
				} else {
					removeCalendarItem((CalendarItem) update.getItem());
				}
			} else if (status == TaskPlannerChange.UPDATED) {
				if (update.getItem() instanceof Calendar) {
					Calendar cal = (Calendar) update.getItem();
					updateCalendar(cal);
				} else if (update.getItem() instanceof CalendarItem) {
					WeekViewCalendarItem i = m_calendarItems.get((CalendarItem) update.getItem());
					if (i != null)
						m_layeredPane.moveItem(i);
				}
			}
		} else if (arg0 instanceof ApplicationSettings) { // Update from Settings
			updateItems();
			updateLabels();
			colorGrid();
		}
	}

	/**
	 * @param item
	 * @param b
	 */
	private void makeVisible(Calendar cal, boolean visible) {
		ZoneId currentZone = ApplicationSettings.getInstance().getTimeZone().toZoneId();
		for (CalendarItem event : cal.getCalendarItems(m_currentWeek.atStartOfDay(currentZone), Duration.ofDays(7))) {
			m_calendarItems.get(event).setVisible(visible);
		}
	}

	public ComponentListener getLayeredPane() {
		return m_layeredPane;
	}

	/**
	 * @return a random pastel color
	 */
	private Color getRandomColor() {
		int rgb[] = new int[3];
		for (int i = 0; i < rgb.length; i++)
			rgb[i] = (((int) (Math.random() * 256)) + 0xCC) / 2;
		return new Color(rgb[0], rgb[1], rgb[2]);
	}

	private class DNDMouseListener implements MouseListener {

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
			BackgroundPanel panel = m_layeredPane.getComponentAt(e.getX(), e.getY());
			if (panel != null && e.getClickCount() > 1) {
				AddCalendarItemDialog dialog = new AddCalendarItemDialog(m_model, null);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.pack();
				ZonedDateTime start = m_currentWeek.plusDays(panel.getDay() - 1).atTime(panel.getHour() + 1, 0).atZone(ApplicationSettings.getInstance().getTimeZone().toZoneId());
				dialog.setDate(start);
				dialog.setVisible(true);
			}
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
}
