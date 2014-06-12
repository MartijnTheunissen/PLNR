package psopv.taskplanner.views.jcomponents;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;

import psopv.taskplanner.config.ApplicationSettings;

import com.alee.extended.date.DateSelectionListener;
import com.alee.extended.date.WebCalendar;
import com.alee.extended.date.WebDateField;
import com.alee.extended.date.WebDateFieldStyle;
import com.alee.laf.StyleConstants;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.rootpane.WebWindow;
import com.alee.utils.CollectionUtils;
import com.alee.utils.SwingUtils;

/*
 * This file is part of WebLookAndFeel library.
 *
 * WebLookAndFeel library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WebLookAndFeel library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WebLookAndFeel library.  If not, see <http://www.gnu.org/licenses/>.
 */


public class DatePickerButton extends WebButton {
	/**
	 * Used icons.
	 */
	public static final ImageIcon			selectDateIcon			= new ImageIcon(WebDateField.class.getResource("icons/date.png"));

	/**
	 * Date selection listeners.
	 */
	protected List<DateSelectionListener>	dateSelectionListeners	= new ArrayList<DateSelectionListener>(1);

	/**
	 * Date display format.
	 */
	protected String						format					= "dd/MM/yyyy";
	protected DateTimeFormatter				dateFormat				= DateTimeFormatter.ofPattern(format);

	/**
	 * Currently selected date.
	 */
	protected LocalDate						date					= null;

	/**
	 * UI components.
	 */
	protected WebWindow						popup;
	protected WebCalendar					calendar;
	protected DateSelectionListener			dateSelectionListener;

	/**
	 * Constructs new date field.
	 */
	public DatePickerButton() {
		this(LocalDate.now(), null);
	}

	public DatePickerButton(String text) {
		this(LocalDate.now(), text);
	}

	/**
	 * Constructs new date field with the specified selected date.
	 *
	 * @param date selected date
	 * @param text the text of the button
	 */
	public DatePickerButton(final LocalDate date, String text) {
		super();

		this.date = date;

		setDrawFocus(WebDateFieldStyle.drawFocus);

		// Popup button
		setFocusable(false);
		setCursor(Cursor.getDefaultCursor());
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				showCalendarPopup();
			}
		});

		// Actions
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				setDateFromField();
			}
		});
		// Initial field date
		updateFieldFromDate();

		// Initial styling settings
		setRound(WebDateFieldStyle.round);
		setShadeWidth(WebDateFieldStyle.shadeWidth);

		setText(text);
	}

	/**
	 * Displays calendar popup.
	 */
	protected void showCalendarPopup() {
		// Updating date from field
		setDateFromField();

		// Create popup if it doesn't exist
		if (popup == null || calendar == null) {
			final Window ancestor = SwingUtils.getWindowAncestor(this);

			// Calendar
			calendar = new WebCalendar(Date.from(date.atStartOfDay().atZone(ApplicationSettings.getInstance().getTimeZone().toZoneId()).toInstant()));
			calendar.setPaintFocus(false);
			calendar.setRound(StyleConstants.smallRound);
			calendar.setShadeWidth(0);

			// Popup window
			popup = new WebWindow(ancestor);
			popup.setLayout(new BorderLayout());
			popup.setCloseOnFocusLoss(true);
			popup.setWindowOpaque(false);
			popup.add(calendar);
			popup.pack();

			// Correct popup positioning
			updatePopupLocation();
			ancestor.addPropertyChangeListener(WebLookAndFeel.ORIENTATION_PROPERTY, new PropertyChangeListener() {
				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					if (popup.isShowing()) {
						updatePopupLocation();
					}
				}
			});

			// Selection listener
			dateSelectionListener = new DateSelectionListener() {
				@Override
				public void dateSelected(final Date date) {
					hideCalendarPopup();
					setDateFromCalendar();
					requestFocusInWindow();
				}
			};
			calendar.addDateSelectionListener(dateSelectionListener);
		} else {
			// Updating window location
			updatePopupLocation();
		}

		// Applying orientation to popup
		SwingUtils.copyOrientation(DatePickerButton.this, popup);

		// Showing popup and changing focus
		popup.setVisible(true);
		calendar.transferFocus();
	}

	/**
	 * Hides calendar popup.
	 */
	protected void hideCalendarPopup() {
		if (popup != null) {
			popup.setVisible(false);
		}
	}

	/**
	 * Updates calendar popup location.
	 */
	protected void updatePopupLocation() {
		final Point los = DatePickerButton.this.getLocationOnScreen();
		final Rectangle gb = popup.getGraphicsConfiguration().getBounds();
		final boolean ltr = DatePickerButton.this.getComponentOrientation().isLeftToRight();
		final int w = DatePickerButton.this.getWidth();
		final int h = DatePickerButton.this.getHeight();

		final int x;
		if (ltr) {
			if (los.x + popup.getWidth() <= gb.x + gb.width) {
				x = los.x;
			} else {
				x = los.x + w - popup.getWidth();
			}
		} else {
			if (los.x + w - popup.getWidth() >= gb.x) {
				x = los.x + w - popup.getWidth();
			} else {
				x = los.x;
			}
		}

		final int y;
		if (los.y + h + popup.getHeight() <= gb.y + gb.height) {
			y = los.y + h;
		} else {
			y = los.y - popup.getHeight();
		}

		popup.setLocation(x, y);
	}

	/**
	 * Returns date specified in text field.
	 *
	 * @return date specified in text field
	 */
	protected LocalDate getDateFromField() {
		try {
			final String text = getText();
			if (text != null && !text.trim().equals("")) {
				return (LocalDate) dateFormat.parse(text.substring(0, format.length()));
			} else {
				return null;
			}
		} catch (final Throwable ex) {
			return date;
		}
	}

	/**
	 * Returns text date representation according to date format.
	 *
	 * @return text date representation according to date format
	 */
	protected String getTextDate() {
		String text = dateFormat.format(date);
		text += "   -   " + dateFormat.format(date.plusDays(6));
		return date != null ? text : "";
	}

	/**
	 * Returns currently selected date.
	 *
	 * @return currently selected date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * Sets currently selected date.
	 *
	 * @param date new selected date
	 */
	public void setDate(final LocalDate date) {
		setDateImpl(date, UpdateSource.other);
	}

	/**
	 * Updates date using the value from field.
	 */
	protected void setDateFromField() {
		setDateImpl(getDateFromField(), UpdateSource.field);
	}

	/**
	 * Updates date using the value from calendar.
	 */
	protected void setDateFromCalendar() {
		LocalDate d = calendar.getDate().toInstant().atZone(ApplicationSettings.getInstance().getTimeZone().toZoneId()).toLocalDate();
		setDateImpl(d.minusDays(d.getDayOfWeek().getValue() - 1), UpdateSource.calendar);
	}

	/**
	 * Sets currently selected date and updates component depending on update source.
	 *
	 * @param date new selected date
	 */
	protected void setDateImpl(final LocalDate date, final UpdateSource source) {
		final boolean changed = !this.date.equals(date);
		this.date = date;

		// Updating field text even if there is no changes
		// Text still might change due to formatting pattern
		updateFieldFromDate();

		if (changed) {
			// Updating calendar date
			if (source != UpdateSource.calendar && calendar != null) {
				updateCalendarFromDate(date);
			}

			// Informing about date selection changes
			fireDateSelected(date);
		}
	}

	/**
	 * Updates text field with currently selected date.
	 */
	protected void updateFieldFromDate() {
		setText(getTextDate());
	}

	/**
	 * Updates date displayed in calendar.
	 *
	 * @param date new displayed date
	 */
	protected void updateCalendarFromDate(final LocalDate date) {
		calendar.removeDateSelectionListener(dateSelectionListener);
		calendar.setDate(Date.from(date.atStartOfDay().atZone(ApplicationSettings.getInstance().getTimeZone().toZoneId()).toInstant()), false);
		calendar.addDateSelectionListener(dateSelectionListener);
	}

	/**
	 * Returns date format.
	 *
	 * @return date format
	 */
	public DateTimeFormatter getDateFormat() {
		return dateFormat;
	}

	/**
	 * Sets date format.
	 *
	 * @param dateFormat date format
	 */
	public void setDateFormat(final DateTimeFormatter dateFormat) {
		this.dateFormat = dateFormat;
		updateFieldFromDate();
	}

	/**
	 * Adds date selection listener.
	 *
	 * @param listener date selection listener to add
	 */
	public void addDateSelectionListener(final DateSelectionListener listener) {
		dateSelectionListeners.add(listener);
	}

	/**
	 * Removes date selection listener.
	 *
	 * @param listener date selection listener to remove
	 */
	public void removeDateSelectionListener(final DateSelectionListener listener) {
		dateSelectionListeners.remove(listener);
	}

	/**
	 * Notifies about date selection change.
	 *
	 * @param date new selected date
	 */
	public void fireDateSelected(final LocalDate date) {
		for (final DateSelectionListener listener : CollectionUtils.copy(dateSelectionListeners)) {
			Date d = Date.from(date.atStartOfDay().atZone(ApplicationSettings.getInstance().getTimeZone().toZoneId()).toInstant());
			listener.dateSelected(d);
		}
	}

	/**
	 * This enumeration represents the type of source that caused view update.
	 */
	protected enum UpdateSource {
		/**
		 * Text field source.
		 */
		field,

		/**
		 * Calendar source.
		 */
		calendar,

		/**
		 * Other source.
		 */
		other
	}
}
