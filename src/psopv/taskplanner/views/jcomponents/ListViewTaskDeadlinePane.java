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
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;

import javax.swing.JComponent;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.models.Task;

/**
 * 
 * $Rev:: 136                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-03 01:03:28 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * List view task deadline date block component
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version<br>
 * 2 - <br>
 * 3 - <br>
 *
 * @since May 28, 2014 10:29:13 PM
 * @author Martijn Theunissen
 */
public class ListViewTaskDeadlinePane extends JComponent {

	private final Dimension	SIZE					= new Dimension(80, 80);
	private Task			m_item;
	private Font			m_dateMonthFont			= new Font("Verdana", Font.PLAIN, 18);
	private Color			m_backgroundColorTop	= new Color(97, 198, 236);
	private Color			m_backgroundColorBottom	= new Color(54, 135, 224);
	private Font			m_dateDayFont			= new Font("Verdana", Font.BOLD, 25);
	private boolean			m_draw					= true;

	/**
	 * Create the pane
	 * @param item the calendaritem to draw
	 * @param drawDate if we should draw the date
	 */
	public ListViewTaskDeadlinePane(Task item, boolean drawDate) {
		m_item = item;

		m_draw = drawDate;

		selectBackgroundColors();

		setMinimumSize(SIZE);
		setPreferredSize(SIZE);
		setMaximumSize(SIZE);
		setSize(SIZE);

	}

	private void selectBackgroundColors() {
		if (m_item.getPriority() < 3) // low or very low
		{
			m_backgroundColorTop = new Color(175, 175, 175); // gray
			m_backgroundColorBottom = new Color(147, 147, 147); // darker gray
		} else if (m_item.getPriority() == 3) // medium
		{
			m_backgroundColorTop = new Color(250, 208, 89); // yellow
			m_backgroundColorBottom = new Color(230, 148, 38); // darker yellow
		} else if (m_item.getPriority() == 4)// high
		{
			m_backgroundColorTop = new Color(252, 147, 59); // orangy-red
			m_backgroundColorBottom = new Color(222, 58, 16); // red
		} else // very high and above
		{
			m_backgroundColorTop = new Color(236, 122, 101); //  salmon
			m_backgroundColorBottom = new Color(188, 37, 48); // red
		}

	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		if (m_draw) {
			drawBackground(g2);

			g2.setColor(Color.WHITE);
			drawDateDay(g2);
			drawDateMonth(g2);
		}

		g2.dispose();
		super.paintComponent(g);
	}

	/**
	 * Draw the background month of the date pane
	 * @param g2 the graphics to draw with
	 */
	private void drawDateMonth(Graphics2D g2) {
		String month = m_item.getDeadline().format(DateTimeFormatter.ofPattern("MMM", ApplicationSettings.getInstance().getLocale()));
		String day = m_item.getDeadline().format(DateTimeFormatter.ofPattern("d"));
		FontMetrics fm = g2.getFontMetrics(m_dateMonthFont);

		g2.setFont(m_dateMonthFont);
		g2.drawString(day + " " + month, (getWidth() - fm.stringWidth(day + " " + month)) / 2, 60);
	}

	/**
	 * Draw the day of the date in the pane
	 * @param g2 the graphics to draw with
	 */
	private void drawDateDay(Graphics2D g2) {
		String day = m_item.getDeadline().getDayOfWeek().getDisplayName(TextStyle.SHORT, ApplicationSettings.getInstance().getLocale());
		FontMetrics fm = g2.getFontMetrics(m_dateDayFont);

		g2.setFont(m_dateDayFont);
		g2.drawString(day, (getWidth() - fm.stringWidth(day)) / 2, 40);
	}

	/**
	 * Draw the background gradient of the date pane
	 * @param g2 the graphics to draw with
	 */
	private void drawBackground(Graphics2D g2) {
		g2.setPaint(new GradientPaint(new Point(0, 0), m_backgroundColorTop, new Point(0, getHeight()), m_backgroundColorBottom));
		g2.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		g2.setPaint(new GradientPaint(new Point(0, 0), m_backgroundColorTop.darker(), new Point(0, getHeight()), m_backgroundColorBottom.darker()));
		g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 3, 3);
		g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1);
	}

	/**
	 * set whether we should draw this component
	 * @param b true or false, depending if we should draw
	 */
	public void setDrawDate(boolean b) {
		m_draw = b;
	}
}
