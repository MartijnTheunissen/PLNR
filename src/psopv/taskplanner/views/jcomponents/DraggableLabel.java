package psopv.taskplanner.views.jcomponents;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;

public class DraggableLabel extends JLabel {

	private boolean			draggable	= true;
	private LayeredDnDPane	m_parent;
	protected Point			anchorPoint;

	public DraggableLabel(LayeredDnDPane parent) {
		m_parent = parent;
		addDragListeners();
	}

	/**
	 * Add Mouse Motion Listener with drag function
	 */
	private void addDragListeners() {
		final DraggableLabel handle = this;
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				anchorPoint = e.getPoint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				int anchorX = anchorPoint.x;
				int anchorY = anchorPoint.y;

				Point parentOnScreen = getParent().getLocationOnScreen();
				Point mouseOnScreen = e.getLocationOnScreen();
				Point position = new Point(mouseOnScreen.x - parentOnScreen.x - anchorX, mouseOnScreen.y - parentOnScreen.y - anchorY);
				setLocation(position);
			}
		});
	}

	/**
	 * Remove all Mouse Motion Listener. Freeze component.
	 */
	private void removeDragListeners() {
		for (MouseMotionListener listener : this.getMouseMotionListeners()) {
			removeMouseMotionListener(listener);
		}
		for (MouseListener listener : this.getMouseListeners())
			removeMouseListener(listener);
		setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * Get the value of draggable
	 *
	 * @return the value of draggable
	 */
	public boolean isDraggable() {
		return draggable;
	}

	/**
	 * Set the value of draggable
	 *
	 * @param draggable new value of draggable
	 */
	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
		if (draggable) {
			addDragListeners();
		} else {
			removeDragListeners();
		}
	}
}
