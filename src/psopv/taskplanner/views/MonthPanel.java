/**
 * 
 */
package psopv.taskplanner.views;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JPanel;

import psopv.taskplanner.models.CalendarItem;
import psopv.taskplanner.views.jcomponents.GridPanel;

/**
 * 
 * $Rev:: 136                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-03 01:03:28 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This class visualizes a calendar month.
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - tom.knaepen: initial version<br>
 * 2 - tom.knaepen: subclassing new GridPanel class, more (placeholder) functionality<br>
 * 3 - <br>
 *
 * @since 1-apr.-2014 18:14:00
 * @author tom.knaepen
 */
public class MonthPanel extends GridPanel {

	// Grid size, 5 rows x 7 cols = 5 weeks x 7 days
	private static final int						NUM_ROWS	= 5;
	private static final int						NUM_COLS	= 7;

	private Collection<Collection<CalendarItem>>	calendars;			// List<CalendarItem> = calendar

	/**
	 * Create a new MonthPanel with current date visible.
	 */
	public MonthPanel() {
		super(NUM_ROWS, NUM_COLS);
		calendars = new ArrayList<Collection<CalendarItem>>();
		fillLabels(LocalDate.now());
	}

	/**
	 * Create a new MonthPanel with date <i>d</i> visible.
	 * @param d the date of the monthpanel
	 */
	public MonthPanel(LocalDate d) {
		super(NUM_ROWS, NUM_COLS);
		calendars = new ArrayList<Collection<CalendarItem>>();
		fillLabels(d);
	}

	/**
	 * Create a new MonthPanel with given month and year visible.
	 * @param month the month of the monthpanel
	 * @param year the year of the monthpanel
	 */
	public MonthPanel(Month month, int year) {
		super(NUM_ROWS, NUM_COLS);
		calendars = new ArrayList<Collection<CalendarItem>>();
		fillLabels(LocalDate.of(1, month, year));
	}

	private void fillLabels(LocalDate d) {
		LocalDate firstDay = d.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate lastDay = d.with(TemporalAdjusters.lastDayOfMonth());
		int lastDayNumber = lastDay.getDayOfMonth();
		int startDay = firstDay.getDayOfWeek().get(ChronoField.DAY_OF_WEEK);
		// Add empty panels for previous month TODO: correct numbers of previous month + perhaps calendar items
		for (int i = 1; i <= startDay; i++)
			addItem(new JPanel());
		// Add panels with numbers for current month TODO: add calendar items
		for (int i = 1; i <= lastDayNumber; i++) {
			JPanel dayPanel = new JPanel();
			dayPanel.add(new JLabel("" + i));
			addItem(dayPanel);
		}
		// fill remainder of grid with empty panels TODO: correct numbers + perhaps calendar items
		for (int i = filled(); i < size(); i++)
			addItem(new JPanel());
	}

	public void addCalendar(Collection<CalendarItem> calendar) {
		//calendars.add(calendar);
	}

	//public void removeCalendar(Collection<CalendarItem> calendar)
	//public void removeCalendar(String calendarName)
	//...

}
