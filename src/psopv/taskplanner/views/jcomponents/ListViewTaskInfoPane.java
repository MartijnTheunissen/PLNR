/**
 * 
 */
package psopv.taskplanner.views.jcomponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.Duration;

import javax.swing.JComponent;
import javax.swing.JDialog;

import net.fortuna.ical4j.model.Dur;
import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.models.Task;
import psopv.taskplanner.util.VEventHelper;
import psopv.taskplanner.views.dialogs.EditTaskDialog;

/**
 * 
 * $Rev:: 135                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-02 19:01:51 +0200 (Mon, 02 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * List view task info pane component
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version<br>
 * 2 - martijn.theunissen: updated translations<br>
 * 3 - <br>
 *
 * @since May 28, 2014 10:30:02 PM
 * @author Martijn Theunissen
 */
public class ListViewTaskInfoPane extends JComponent implements MouseListener {

	private final Dimension	SIZE					= new Dimension(520, 80);
	private Font			m_durationStringFont	= new Font("Verdana", Font.BOLD, 13);
	private Font			m_summaryFont			= new Font("Verdana", Font.BOLD, 15);
	private Font			m_descriptionFont		= new Font("Verdana", Font.BOLD | Font.ITALIC, 11);
	private Font			m_durationFont			= new Font("Verdana", Font.BOLD, 15);
	private Color			m_durationColor			= new Color(54, 135, 224);
	private Task			m_item;
	private boolean			m_hoverActive			= false;
	private EditTaskDialog	m_dialog;

	/**
	 * Create the listview info pane of one calendaritem
	 * @param item the calendaritem to display
	 */
	public ListViewTaskInfoPane(Task item) {
		m_item = item;
		setMinimumSize(SIZE);
		setPreferredSize(SIZE);
		setMaximumSize(SIZE);
		setSize(SIZE);
		this.addMouseListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		drawBackground(g2);

		drawSummary(g2);

		drawDescription(g2);
		drawDurationString(g2);
		if (m_hoverActive) {
			drawModify(g2);
		}
		g2.dispose();
		super.paintComponent(g);
	}

	/**
	 * draw the duratation in the pane (like "2 hours, 1 minute")
	 * @param g2 the graphics to draw with
	 */
	private void drawDurationString(Graphics2D g2) {
		Duration dur = m_item.getDuration();

		String duration = makeDurationString(dur);

		FontMetrics fm = g2.getFontMetrics(m_durationStringFont);

		g2.setFont(m_durationStringFont);
		g2.setColor(m_durationColor);
		g2.drawString(duration, getWidth() - fm.stringWidth(duration) - 10, 25);
	}

	/**
	 * Generate a duration string from a duration variable
	 * @param dur the duration to convert to string
	 * @return the duration
	 */
	private String makeDurationString(Duration dur) {
		Dur d = VEventHelper.durationToICALDur(dur);
		String out = "";
		int days = d.getDays();
		if (days > 0)
			out += days + " " + (days == 1 ? ApplicationSettings.getInstance().getLocalizedMessage("lvip.day") : ApplicationSettings.getInstance().getLocalizedMessage("lvip.days"));

		int hrs = d.getHours();
		if (hrs > 0) {
			if (out.length() > 0)
				out += ", ";
			out += +hrs + " " + (hrs == 1 ? ApplicationSettings.getInstance().getLocalizedMessage("lvip.hr") : ApplicationSettings.getInstance().getLocalizedMessage("lvip.hrs"));
		}

		int mins = d.getMinutes();
		if (mins > 0) {
			if (out.length() > 0)
				out += ", ";
			out += mins + " " + (mins == 1 ? ApplicationSettings.getInstance().getLocalizedMessage("lvip.min") : ApplicationSettings.getInstance().getLocalizedMessage("lvip.mins"));
		}
		return out;
	}

	/**
	 * draw the description in the pane
	 * @param g2 the graphics to draw with
	 */
	private void drawDescription(Graphics2D g2) {
		String description = m_item.getDescription();

		g2.setFont(m_descriptionFont);
		g2.setColor(new Color(165, 165, 165));
		g2.drawString(description, 10, 65);
	}

	/**
	 * draw the summary in the pane
	 * @param g2 the graphics to draw with
	 */
	private void drawSummary(Graphics2D g2) {
		String summary = m_item.getName();
		g2.setFont(m_summaryFont);
		g2.setColor(new Color(77, 77, 77));
		g2.drawString(summary, 10, 25);
	}

	private void drawModify(Graphics2D g2) {
		String modify = ApplicationSettings.getInstance().getLocalizedMessage("lvip.cte");

		Font f = new Font("Verdana", Font.PLAIN, 11);

		g2.setFont(f);
		g2.setColor(Color.green.darker());
		FontMetrics fm = g2.getFontMetrics(f);

		g2.drawString(modify, getWidth() - fm.stringWidth(modify) - 20, 67);

	}

	/**
	 * draw the gradient background and border of the pane
	 * @param g2 the graphics to draw with
	 */
	private void drawBackground(Graphics2D g2) {
		g2.setPaint(new GradientPaint(new Point(0, 0), new Color(250, 250, 250), new Point(0, getHeight()), new Color(240, 240, 240)));
		g2.fillRect(-1, 0, getWidth(), getHeight() - 1);
		g2.setPaint(new GradientPaint(new Point(0, 0), new Color(250, 250, 250).darker(), new Point(0, getHeight()), new Color(240, 240, 240).darker()));
		g2.drawRoundRect(-1, 0, getWidth(), getHeight() - 1, 3, 3);
		g2.drawLine(0, 0, 0, getHeight() - 1);
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
		m_dialog = new EditTaskDialog(m_item);
		m_dialog.setVisible(false);
		m_dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		m_dialog.pack();
		m_dialog.setVisible(true);
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
		m_hoverActive = true;
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		m_hoverActive = false;
		repaint();
	}
}
