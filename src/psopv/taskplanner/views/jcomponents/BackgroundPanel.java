/**
 * 
 */
package psopv.taskplanner.views.jcomponents;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JPanel;

/**
 * 
 * $Rev:: 139                                                  $:  Revision of last commit<br>
 * $Author:: tom.knaepen                                       $:  Author of last commit<br>
 * $Date:: 2014-06-03 13:25:22 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * Background panel for weekview
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - tom.knaepen: init<br>
 * 2 - martijn.theunissen: added day/hour for adding items<br>
 * 3 - tom.knaepen: working with areas instead of the whole panel<br>
 *
 * @since 23-mei-2014 15:54:46
 * @author tom.knaepen
 */
public class BackgroundPanel extends JPanel {
	public enum BackgroundColor {
		RED, GRAY/* , BLUE */;
	}

	// keeps track of which areas are colored red resp. gray
	private boolean[][]						m_hasColor;
	private LinkedHashMap<Color, boolean[]>	m_colors;

	private int								m_day, m_hour;
	private static final Color				m_red	= new Color(255, 50, 50, 60);
	private static final Color				m_gray	= new Color(50, 50, 50, 60);

	//private static final Color				m_blue	= new Color(50, 255, 50, 60);

	public BackgroundPanel(int day, int hour) {
		m_hasColor = new boolean[3][60];
		m_colors = new LinkedHashMap<Color, boolean[]>();
		m_day = day;
		m_hour = hour;
	}

	public void setArea(BackgroundColor c, int start, int end) {
		int color = c.ordinal();
		if (start < 0 || start >= 60 || end < 0 || end >= 60)
			return;
		for (int i = start; i < end; i++)
			m_hasColor[color][i] = true;

		repaint();
	}

	public void addColor(Color c, int start, int end) {
		boolean[] b = new boolean[60];
		for (int i = start; i < end; i++)
			b[i] = true;
		m_colors.put(c, b);
	}

	public void removeColor(Color c) {
		m_colors.remove(c);
	}

	/**
	 * Returns day this panel represents
	 * @return the day this panel represents
	 */
	public int getDay() {
		return m_day;
	}

	/**
	 * Returns hour this panel represents
	 * @return hour this panel represents
	 */
	public int getHour() {
		return m_hour;
	}

	private ArrayList<Integer> getAreas(BackgroundColor c) {
		ArrayList<Integer> results = new ArrayList<Integer>();
		int color = c.ordinal();
		int start = -1;
		for (int i = 0; i < 60; i++) {
			if (start < 0 && m_hasColor[color][i])
				start = i;
			else if (start >= 0 && !m_hasColor[color][i]) {
				results.add(start);
				results.add(i);
				start = -1;
			}
		}
		if (start >= 0) {
			results.add(start);
			results.add(59);
		}
		return results;
	}

	private ArrayList<Integer> getAreas(Color c) {
		ArrayList<Integer> results = new ArrayList<Integer>();
		boolean[] a = m_colors.get(c);
		if (a == null)
			return results;
		int start = -1;
		for (int i = 0; i < 60; i++) {
			if (start < 0 && a[i])
				start = i;
			else if (start >= 0 && !a[i]) {
				results.add(start);
				results.add(i);
				start = -1;
			}
		}
		if (start >= 0) {
			results.add(start);
			results.add(59);
		}
		return results;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int width = getWidth();
		int height = getHeight();
		g.setColor(Color.white);
		g.drawRect(0, 0, width, height);

		ArrayList<Integer> areas = getAreas(BackgroundColor.RED);
		g.setColor(m_red);
		for (int i = 0; i < areas.size(); i += 2) {
			int start = areas.get(i) * height / 59;
			int end = areas.get(i + 1) * height / 59;
			int areaHeight = end - start;
			for (int j = 0; j < width + height; j += 3)
				g.drawLine(j, start, j - areaHeight, end);
		}

		areas = getAreas(BackgroundColor.GRAY);
		g.setColor(m_gray);
		for (int j = 0; j < areas.size(); j += 2) {
			int start = areas.get(j) * height / 59;
			int end = areas.get(j + 1) * height / 59;
			int areaHeight = end - start;
			for (int i = -height; i < width; i += 3)
				g.drawLine(i, start, i + areaHeight, end);
		}

		for (Color c : m_colors.keySet()) {
			areas = getAreas(c);
			for (int j = 0; j < areas.size(); j += 2) {
				int start = areas.get(j) * height / 59;
				int end = areas.get(j + 1) * height / 59;
				g.setColor(c);
				g.fillRect(0, start, width, end);
			}
		}

		//		areas = getAreas(BackgroundColor.BLUE);
		//		g.setColor(m_blue);
		//		for (int j = 0; j < areas.size(); j += 2)
		//			for (int i = 0; i < width; i += 2)
		//				g.drawLine(i, areas.get(j) * height / 59, i, areas.get(j + 1) * height / 59);
	}

	/**
	 * Reset/remove all colors from the panel
	 */
	public void reset() {
		for (int i = 0; i < m_hasColor.length; i++)
			for (int j = 0; j < m_hasColor[i].length; j++)
				m_hasColor[i][j] = false;
		m_colors.clear();
	}
}
