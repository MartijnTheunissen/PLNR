/**
 * 
 */
package psopv.taskplanner.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.models.Calendar;
import psopv.taskplanner.models.CalendarItem;
import psopv.taskplanner.models.TaskPlannerModel;
import psopv.taskplanner.views.jcomponents.DatePickerButton;
import psopv.taskplanner.views.jcomponents.ListViewCalendarItem;
import be.uhasselt.oo2.mvc.Controller;

import com.alee.extended.date.DateSelectionListener;
import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;

/**
 * 
 * $Rev:: 154                                                  $:  Revision of last commit<br>
 * $Author:: tom.knaepen                                       $:  Author of last commit<br>
 * $Date:: 2014-06-05 17:20:57 +0200 (Thu, 05 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * A list view representation of the taskplanner model
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version <br>
 * 2 - tom.knaepen: minor bug fix: repaint() in updateGUI()<br>
 * 3 - <br>
 *
 * @since May 10, 2014 5:36:16 PM
 * @author martijn
 */
public class ListView extends WebPanel implements Observer {

	private TreeMap<CalendarItem, ListViewCalendarItem>	m_calendarItems;
	private Comparator									m_calendarItemComparator;
	private WebPanel									m_itemContainer;
	private WebScrollPane								m_scrollPane;
	private WebPanel									m_container;

	private TaskPlannerModel							m_model;
	private Controller									m_controller;
	private int											m_dayAmount	= 5;
	private DatePickerButton							m_datePicker;
	private WebToggleButton								today;
	private WebToggleButton								threedays;
	private WebToggleButton								fivedays;
	private WebToggleButton								week;
	private WebButtonGroup								dayButtonGroup;
	private JPanel										panel;
	private LocalDate									m_currentWeek;

	/**
	 * Initialize the listview
	 * @param model the model to view
	 * @param controller the controller to use
	 */
	public ListView(TaskPlannerModel model, Controller controller) {
		m_model = model;
		model.addObserver(this);
		m_controller = controller;
		m_currentWeek = LocalDate.now().minusDays(LocalDate.now().get(ChronoField.DAY_OF_WEEK) - 1);
		m_calendarItemComparator = new Comparator<CalendarItem>() {
			@Override
			public int compare(CalendarItem arg0, CalendarItem arg1) {
				if (arg0.getStartDate().isEqual(arg1.getStartDate()))
					return 0;
				else if (arg0.getStartDate().isBefore(arg1.getStartDate()))
					return -1;
				else
					return 1;
			}
		};
		m_calendarItems = new TreeMap<CalendarItem, ListViewCalendarItem>(m_calendarItemComparator);
		ApplicationSettings.getInstance().addObserver(this);

		initGUI();

		initItems();

		updateGUI();

		this.add(m_container);
	}

	/**
	 * Initialize the gui of the view
	 */
	private void initGUI() {
		m_itemContainer = new WebPanel();
		m_itemContainer.setLayout(new BoxLayout(m_itemContainer, BoxLayout.Y_AXIS));

		m_container = new WebPanel();
		m_container.setLayout(new BorderLayout());

		m_scrollPane = new WebScrollPane(m_itemContainer, false);
		m_scrollPane.setPreferredSize(new Dimension(900, 500));
		m_scrollPane.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		m_scrollPane.setBorder(new EmptyBorder(5, 20, 20, 20));

		panel = new JPanel(new BorderLayout());

		today = new WebToggleButton(ApplicationSettings.getInstance().getLocalizedMessage("lv.today"));
		threedays = new WebToggleButton("3 " + ApplicationSettings.getInstance().getLocalizedMessage("lv.days"));
		fivedays = new WebToggleButton("5 " + ApplicationSettings.getInstance().getLocalizedMessage("lv.days"));
		week = new WebToggleButton(ApplicationSettings.getInstance().getLocalizedMessage("lv.week"));

		today.addActionListener((l) -> {
			showDays(1);
		});
		threedays.addActionListener((l) -> {
			showDays(3);
		});
		fivedays.addActionListener((l) -> {
			showDays(5);
		});
		week.addActionListener((l) -> {
			showDays(7);
		});

		dayButtonGroup = new WebButtonGroup(true, today, threedays, fivedays, week);
		fivedays.setSelected(true);

		dayButtonGroup.setButtonsDrawFocus(false);

		panel.add(dayButtonGroup, BorderLayout.WEST);

		initDatepicker();

		panel.setBorder(new EmptyBorder(0, 20, 0, 20));

		m_container.add(panel, BorderLayout.NORTH);
		m_container.add(m_scrollPane, BorderLayout.CENTER);
	}

	private void initDatepicker() {
		String dateText = m_currentWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		dateText += "   -   " + m_currentWeek.plusDays(m_dayAmount).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		m_datePicker = new DatePickerButton(m_currentWeek, dateText);
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

		m_datePicker.addDateSelectionListener(new DateSelectionListener() {
			@Override
			public void dateSelected(Date arg0) {
				setDate(m_datePicker.getDate());
			}
		});
		WebButtonGroup buttons = new WebButtonGroup(true, previous, m_datePicker, next);
		panel.add(buttons, BorderLayout.EAST);
		buttons.setFocusable(false);
	}

	/**
	 * Change the displayed week
	 * @param date
	 */
	private void setDate(LocalDate date) {
		m_currentWeek = date.minusDays(date.get(ChronoField.DAY_OF_WEEK) - 1);
		initItems();
		updateGUI();
	}

	private void moveDate(int days) {
		m_currentWeek = m_currentWeek.plusDays(days);
		initItems();
		updateGUI();
	}

	/**
	 * Show x amount of days on the list
	 * @param amount
	 */
	private void showDays(int amount) {
		m_dayAmount = amount;
		initItems();
		updateGUI();
		m_container.revalidate();
	}

	/**
	 * init the items of the view
	 */
	private void initItems() {
		TreeMap<CalendarItem, ListViewCalendarItem> newList = new TreeMap<CalendarItem, ListViewCalendarItem>(m_calendarItemComparator);
		ArrayList<Calendar> calendars = m_model.getCalendars();
		ZoneId zone = ApplicationSettings.getInstance().getTimeZone().toZoneId();
		ZonedDateTime now = m_currentWeek.atStartOfDay(zone);

		for (Calendar calendar : calendars) {
			ArrayList<CalendarItem> events = calendar.getCalendarItems(now, Duration.ofDays(m_dayAmount));
			for (CalendarItem event : events) {
				if (m_calendarItems.get(event) == null)
					newList.put(event, new ListViewCalendarItem(event, true, calendar.getColor()));
				else
					newList.put(event, m_calendarItems.get(event));
				newList.get(event).setVisible(calendar.isActive());
			}
		}

		m_calendarItems.clear();
		m_calendarItems.putAll(newList);
	}

	/**
	 * Update the gui of the view
	 */
	private void updateGUI() {
		m_itemContainer.removeAll();

		ZonedDateTime last = null;
		Iterator<CalendarItem> it = m_calendarItems.keySet().iterator();
		while (it.hasNext()) {
			CalendarItem item = it.next();
			if (!m_calendarItems.get(item).isVisible())
				continue;
			ZonedDateTime newer = item.getStartDate();
			if (last == null) // if this is the first visible item
			{
				last = newer;
				m_calendarItems.get(item).setDrawDate(true);
			} else if (last.truncatedTo(ChronoUnit.DAYS).isEqual(newer.truncatedTo(ChronoUnit.DAYS))) // same day, don't repeat the drawing
			{
				m_calendarItems.get(item).setDrawDate(false);
			} else {
				last = newer;
				m_itemContainer.add(Box.createRigidArea(new Dimension(0, 20)));
				m_calendarItems.get(item).setDrawDate(true);
			}
			m_itemContainer.add(m_calendarItems.get(item));
		}

		today.setText(ApplicationSettings.getInstance().getLocalizedMessage("lv.today"));
		threedays.setText("3 " + ApplicationSettings.getInstance().getLocalizedMessage("lv.days"));
		fivedays.setText("5 " + ApplicationSettings.getInstance().getLocalizedMessage("lv.days"));
		week.setText(ApplicationSettings.getInstance().getLocalizedMessage("lv.week"));
		String dateText = m_currentWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		dateText += "   -   " + m_currentWeek.plusDays(m_dayAmount - 1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		m_datePicker.setText(dateText);

		// display a message if no active calendars
		if (m_itemContainer.getComponentCount() == 0)
			if (m_model.getCalendars().size() == 0) {
				m_itemContainer.add(new WebLabel(ApplicationSettings.getInstance().getLocalizedMessage("tv.nocal")));
			} else {
				m_itemContainer.add(new WebLabel(ApplicationSettings.getInstance().getLocalizedMessage("tv.noitem")));
			}

		m_container.revalidate();
		m_container.repaint();
		revalidate();
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof TaskPlannerModel)
			m_model = (TaskPlannerModel) o;
		initItems();
		updateGUI();
	}
}
