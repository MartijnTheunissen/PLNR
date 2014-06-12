package psopv.taskplanner.views.jcomponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import psopv.taskplanner.models.CalendarItem;
import psopv.taskplanner.views.dialogs.EditCalendarItemDialog;
import psopv.taskplanner.views.jcomponents.BackgroundPanel.BackgroundColor;

/**
 * 
 * Represents a drag-and-droppable panel with different layers 
 *
 * @since 10-mei-2014 16:45:46
 * @author Tom Knaepen
 */
public class LayeredDnDPane extends JLayeredPane implements MouseListener, MouseMotionListener, ComponentListener {
	private Dimension						DEFAULT_SIZE	= new Dimension(832, 1000);
	private Point							m_oldItemLocation;
	private JPanel							gridPanel, eventPanel;
	private WeekViewCalendarItem			dragComponent;
	private ArrayList<WeekViewCalendarItem>	m_items;
	private int								xAdjustment;
	private int								yAdjustment;
	private ConcurrentLinkedQueue<Timer>	m_timers;

	/**
	 * Constructor
	 */
	public LayeredDnDPane() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);

		m_timers = new ConcurrentLinkedQueue<Timer>();
		m_items = new ArrayList<WeekViewCalendarItem>();
		//  Add a grid panel to the Layered Pane
		gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(24, 7));
		gridPanel.setOpaque(false);
		gridPanel.setBounds(0, 0, DEFAULT_SIZE.width, DEFAULT_SIZE.height);
		add(gridPanel, JLayeredPane.DEFAULT_LAYER);

		// Add a null-layout panel to the next layer
		eventPanel = new JPanel();
		eventPanel.setLayout(null);
		eventPanel.setOpaque(false);
		gridPanel.setBounds(0, 0, DEFAULT_SIZE.width, DEFAULT_SIZE.height);
		add(eventPanel, JLayeredPane.PALETTE_LAYER);

		Color gridColor = new Color(120, 120, 120);
		//  Build a grid
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 7; j++) {
				BackgroundPanel square = new BackgroundPanel(j + 1, i - 1);
				square.setOpaque(true);
				square.setBorder(BorderFactory.createMatteBorder(1, 1, (i + 1) / 24, (j + 1) / 7, gridColor)); // top left bottom right
				square.setBackground(Color.WHITE);
				gridPanel.add(square);
			}
		}
		setPreferredSize(DEFAULT_SIZE);
	}

	/**
	 * Add item to the event panel
	 * @param item the item to add
	 */
	public void addItem(WeekViewCalendarItem item) {
		eventPanel.add(item);
		m_items.add(item);
		moveItem(item);
		eventPanel.repaint();
	}

	/**
	 * Remove item from the event panel
	 * @param item the item to remove
	 */
	public void removeItem(WeekViewCalendarItem item) {
		eventPanel.remove(item);
		m_items.remove(item);
		eventPanel.repaint();
	}

	@Override
	public void removeAll() {
		eventPanel.removeAll();
		m_items.clear();
		eventPanel.repaint();
	}

	@Override
	public void setPreferredSize(Dimension d) {
		super.setPreferredSize(d);
		gridPanel.setPreferredSize(d);
		eventPanel.setPreferredSize(d);
	}

	/**
	 * Add a striped background to given area
	 * @param c the bg color of the area
	 * @param dayOffset the day offset
	 * @param startHour the start hour
	 * @param startMinutes the start minutes
	 * @param endHour the end hour
	 * @param endMinutes the end minutes
	 */
	public void setColoredArea(BackgroundColor c, int dayOffset, int startHour, int startMinutes, int endHour, int endMinutes) {
		BackgroundPanel p;
		// first and last (partial) areas
		if (startMinutes + endMinutes >= 60 || endHour > startHour) {
			p = (BackgroundPanel) gridPanel.getComponent(startHour * 7 + dayOffset);
			p.setArea(c, startMinutes, 59);
		}
		if (endMinutes > 0 && endHour > startHour) {
			p = (BackgroundPanel) gridPanel.getComponent(endHour * 7 + dayOffset);
			p.setArea(c, 0, endMinutes);
		}
		// full areas
		for (int i = startHour + 1; i < endHour; i++) {
			p = (BackgroundPanel) gridPanel.getComponent(i * 7 + dayOffset);
			p.setArea(c, 0, 59);
		}
	}

	public void addColoredArea(Color c, int dayOffset, int startHour, int startMinutes, int endHour, int endMinutes) {
		BackgroundPanel p;
		// first and last (partial) areas
		if (startMinutes + endMinutes >= 60 || endHour > startHour) {
			p = (BackgroundPanel) gridPanel.getComponent(startHour * 7 + dayOffset);
			p.addColor(c, startMinutes, 59);
		}
		if (endMinutes > 0 && endHour > startHour) {
			p = (BackgroundPanel) gridPanel.getComponent(endHour * 7 + dayOffset);
			p.addColor(c, 0, endMinutes);
		}
		// full areas
		for (int i = startHour + 1; i < endHour; i++) {
			p = (BackgroundPanel) gridPanel.getComponent(i * 7 + dayOffset);
			p.addColor(c, 0, 59);
		}
	}

	public void removeColoredArea(Color c, int dayOffset, int startHour, int endHour) {
		BackgroundPanel p;
		for (int i = startHour; i <= endHour; i++) {
			p = (BackgroundPanel) gridPanel.getComponent(i * 7 + dayOffset);
			p.removeColor(c);
		}
	}

	public void resetColors() {
		for (Component c : gridPanel.getComponents()) {
			BackgroundPanel p = (BackgroundPanel) c;
			p.reset();
		}
	}

	/**
	 * Get the background component at a certain point in the pane
	 */
	public BackgroundPanel getComponentAt(int x, int y) {
		Component c = eventPanel.getComponentAt(x, y);
		if (!c.isValid() || c instanceof WeekViewCalendarItem) // something in front
		{
			return null;
		}

		c = gridPanel.findComponentAt(x, y);

		if (!(c instanceof BackgroundPanel)) {
			return null;
		}
		return (BackgroundPanel) c;
	}

	/*
	 * * Add the selected event to the dragging layer so it can be moved
	 */
	public void mousePressed(MouseEvent e) {
		dragComponent = null;
		Component c = eventPanel.findComponentAt(e.getX(), e.getY());

		if (!(c instanceof WeekViewCalendarItem)) {

			return;
		}

		m_oldItemLocation = c.getLocation();

		if (e.getClickCount() == 2) { // show edit menu
			EditCalendarItemDialog dialog = new EditCalendarItemDialog((CalendarItem) ((WeekViewCalendarItem) c).getModel());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.pack();
			dialog.setVisible(true);
			return;
		}

		Point eventLocation = c.getLocation();
		xAdjustment = e.getX() - eventLocation.x;
		yAdjustment = e.getY() - eventLocation.y;
		dragComponent = (WeekViewCalendarItem) c;
		dragComponent.setLocation(e.getX() - xAdjustment, e.getY() - yAdjustment);

		add(dragComponent, JLayeredPane.DRAG_LAYER);
		setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}

	/*
	 * * Move the event around
	 */
	public void mouseDragged(MouseEvent me) {
		if (dragComponent == null)
			return;

		//  The drag location should be within the bounds of the calendar

		int x = me.getX() - xAdjustment;
		int xMax = getWidth() - dragComponent.getWidth();
		x = Math.min(x, xMax);
		x = Math.max(x, 0);

		int y = me.getY() - yAdjustment;
		int yMax = getHeight() - dragComponent.getHeight();
		y = Math.min(y, yMax);
		y = Math.max(y, 0);

		dragComponent.setLocation(x, y);
	}

	/*
	 * * Drop the event back onto the calendar
	 */
	public void mouseReleased(MouseEvent e) {
		setCursor(null);

		if (dragComponent == null)
			return;

		//  Make sure the event is no longer painted on the layered pane

		dragComponent.setVisible(false);
		remove(dragComponent);
		dragComponent.setVisible(true);

		//  The drop location should be within the bounds of the calendar

		int xMax = getWidth() - dragComponent.getWidth();
		int x = Math.min(e.getX() - xAdjustment, xMax);
		x = Math.max(x, 0);

		int yMax = getHeight() - dragComponent.getHeight();
		int y = Math.min(e.getY() - yAdjustment, yMax);
		y = Math.max(y, 0);

		// Calculate where to drop the event
		int centerX = x + dragComponent.getWidth() / 2; // day where more than half of the event is currently positioned
		int centerY = y + gridPanel.getComponent(0).getHeight() / 2; // hour where more than half of the top hour is currently positioned

		Component c = gridPanel.findComponentAt(centerX, centerY);
		Container parent = (Container) c;

		Point newLocation = parent.getLocation();
		eventPanel.add(dragComponent);
		if (Math.abs(m_oldItemLocation.y - y) > 5 || Math.abs(m_oldItemLocation.x - x) > parent.getWidth() / 2)
			moveItem(dragComponent, newLocation);
		else
			moveItem(dragComponent, m_oldItemLocation);
	}

	/**
	 * Move the item to given point and update it
	 * @param item
	 * @param p
	 */
	private void moveItem(WeekViewCalendarItem item, Point p) {
		// (getX|getY) + (getWidth|getHeight)/2, bug fix to get correct offsets since getX/getY returns top left corner, possible rounding error
		int dayOffset = (p.x + item.getWidth() / 2) * 7 / getSize().width + 1; // day = getX / (getSize / numberOfDays)
		int hourOffset = (p.y + (int) (item.getHeight() / item.getDuration() / 2)) * 24 / getSize().height; // hour = getY / (getSize / numberOfDays)
		item.setLocation(p);
		if (dayOffset != item.getDayOffset() || hourOffset != (int) Math.round(item.getHourOffset()))
			item.setDate(dayOffset, hourOffset);
	}

	/**
	 * Move calendar <i>item</i> according to its fields.
	 * @param item the item to move
	 */
	public void moveItem(WeekViewCalendarItem item) {
		Component closest = gridPanel.getComponent(item.getDayOffset() - 1 + (int) Math.floor(item.getHourOffset()) * 7);
		int minutes = (int) ((item.getHourOffset() - Math.floor(item.getHourOffset())) * (closest.getHeight() + 1));
		item.setPreferredSize(new Dimension(closest.getWidth() + 1, (int) (closest.getHeight() * item.getDuration() + 1)));
		item.setBounds(closest.getX(), closest.getY() + minutes, item.getPreferredSize().width, item.getPreferredSize().height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO window resized, scale all events!
		Dimension size = arg0.getComponent().getSize();
		gridPanel.setSize(size);
		eventPanel.setSize(size);
		for (WeekViewCalendarItem item : m_items) {
			if (item.isVisible()) {
				int day = item.getDayOffset();
				double start = item.getHourOffset();
				int startHour = (int) Math.floor(start);
				int startMinutes = (int) ((start - Math.floor(start)) * 60);
				double end = item.getHourOffset() + item.getDuration();
				int endHour = (int) Math.floor(end);
				int endMinutes = (int) ((end - Math.floor(end)) * 60);
				this.addColoredArea(item.getColor(), day - 1, startHour, startMinutes, endHour, endMinutes);
				item.setVisible(false);
			}
		}
		Timer t = new Timer(70, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Timer t = m_timers.poll();
				if (t != null) {
					t.stop();
					//debug: System.out.println("Removed timer, count: " + m_timers.size());
					if (m_timers.isEmpty())
						resizeEvents();
				}
			}
		});
		m_timers.add(t);
		t.start();
		//debug: System.out.println("Added new timer, count: " + m_timers.size());
		revalidate();
	}

	private void resizeEvents() {
		SwingWorker<ArrayList<WeekViewCalendarItem>, Void> worker = new SwingWorker<ArrayList<WeekViewCalendarItem>, Void>() {

			@Override
			protected ArrayList<WeekViewCalendarItem> doInBackground() throws Exception {
				for (WeekViewCalendarItem item : m_items) {
					moveItem(item);
				}
				return m_items;
			}

			@Override
			protected void done() {
				for (WeekViewCalendarItem item : m_items) {
					moveItem(item);
					item.setVisible(true);
					int day = item.getDayOffset();
					int startHour = (int) Math.floor(item.getHourOffset());
					int endHour = (int) Math.floor(item.getHourOffset() + item.getDuration());
					removeColoredArea(item.getColor(), day - 1, startHour, endHour);
				}
			}
		};
		worker.execute();
		revalidate();
	}

	// Ignore all these events
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentShown(ComponentEvent arg0) {
	}
}
