package psopv.taskplanner.util;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;

/**
 * 
 * Utility class for doing a number of drawing operations such as drawing triangles, Strings in a box, ...
 *
 * @since Apr 9, 2014 4:26:32 PM
 * @author Martijn Theunissen
 */
public class Graphics2DHelper {

	/**
	 * Draw a String (splitted over spaces) over several lines, such that it can fit within the lines.
	 * @param g2d the graphics of the jcomponent
	 * @param text the text it should blit on the screen
	 * @param lineWidth the max-linewidth a line should occupy.
	 * @param x the x-position of the text to start
	 * @param y the y-position of the text to start.
	 * @return the amount of pixels downwards it took to draw
	 */
	public static int drawMultiLineString(Graphics2D g2d, String text, int lineWidth, int x, int y) {
		FontMetrics m = g2d.getFontMetrics();
		int originalY = y;

		if (m.stringWidth(text) < lineWidth) {
			g2d.drawString(text, x, y);
		} else {
			String[] words = text.split(" ");
			String currentLine = words[0];
			for (int i = 1; i < words.length; i++) {
				if (m.stringWidth(currentLine + words[i]) < lineWidth) {
					currentLine += " " + words[i];
				} else {
					g2d.drawString(currentLine, x, y);
					y += m.getHeight();
					currentLine = words[i];
				}
			}
			if (currentLine.trim().length() > 0) {
				g2d.drawString(currentLine, x, y);
			}
		}
		return y - originalY + m.getHeight();
	}

	/**
	 *  Draw a String (splitted over spaces) over several lines, such that it can fit within the lines and under a certain height. 
	 *  If that can not be done, it will append "..."
	 * @param g2d the graphics of the jcomponent
	 * @param text the text it should blit on the screen
	 * @param maxWidth the max-linewidth a line should occupy in pixels
	 * @param maxHeight the max-height the text should occupy.
	 * @param x the x-position of the text to start
	 * @param y the y-position of the text to start.
	 * @return the amount of pixels in downwards it took to draw
	 */
	public static int drawMultiLineStringBoxed(Graphics2D g2d, String text, int maxWidth, int maxHeight, int x, int y) {
		FontMetrics m = g2d.getFontMetrics();
		int fontheight = m.getHeight();
		int originalY = y;

		if (m.stringWidth(text) < maxWidth) {
			if (y < maxHeight)
				g2d.drawString(text, x, y);
			else {
				g2d.drawString("...", x, y);
				return 0;
			}

		} else {
			String[] words = text.split(" ");
			String currentLine = words[0];
			for (int i = 1; i < words.length; i++) {
				if (m.stringWidth(currentLine + words[i] + "...") < maxWidth) {
					currentLine += " " + words[i];
				} else {
					if (y + m.getHeight() > maxHeight) {
						g2d.drawString(currentLine + "...", x, y);
						return y - originalY + m.getHeight();
					} else {
						if (m.stringWidth(currentLine) <= maxWidth) {
							g2d.drawString(currentLine, x, y);
							y += m.getHeight();
							currentLine = words[i];
						} else {
							int cutpoint = m.stringWidth(currentLine) - maxWidth;
							int avgcharwidth = 0;

							for (int ch = 0; ch < currentLine.length(); ch++)
								avgcharwidth += m.charWidth(currentLine.charAt(ch));
							avgcharwidth /= currentLine.length();

							int cutpointEstimate = cutpoint / avgcharwidth + 1;

							if (cutpointEstimate > 1 && cutpointEstimate < currentLine.length()) {
								g2d.drawString(currentLine.substring(0, cutpointEstimate) + "-", x, y);
								currentLine = currentLine.substring(cutpointEstimate);
								y += m.getHeight();
								i--;
								continue;
							}

						}
					}
				}
			}
			if (currentLine.trim().length() > 0) {
				if (y + m.getHeight() > maxHeight) {
					g2d.drawString(currentLine + "...", x, y);
					return y - originalY + m.getHeight();
				} else
					g2d.drawString(currentLine, x, y);
			}
		}
		return y - originalY + m.getHeight();
	}

	/**
	 * Draw a triangle lat a certain position, going right.
	 * @param g2d the graphics of the jcomponent
	 * @param x x-position where to draw
	 * @param y y-position where to draw
	 */
	public static void drawRightTriangle(Graphics2D g2d, int x, int y) {
		Polygon triangle = new Polygon();
		triangle.addPoint(x + 0, y - 10);
		triangle.addPoint(x + 10, y - 5);
		triangle.addPoint(x + 0, y + 0);
		g2d.fillPolygon(triangle);
	}

	/**
	 * Draw a triangle lat a certain position, going down.
	 * @param g2d the graphics of the jcomponent
	 * @param x x-position where to draw
	 * @param y y-position where to draw
	 */
	public static void drawDownTriangle(Graphics2D g2d, int x, int y) {
		Polygon triangle = new Polygon();
		triangle.addPoint(x + 0, y - 10);
		triangle.addPoint(x + 10, y - 10);
		triangle.addPoint(x + 5, y + 0);
		g2d.fillPolygon(triangle);
	}
}
