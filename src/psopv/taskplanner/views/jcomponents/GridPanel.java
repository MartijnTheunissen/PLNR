package psopv.taskplanner.views.jcomponents;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * 
 * This class represents a clean, basic panel with a GridLayout and actions to interact with its children/elements.
 *
 * @since 5-apr.-2014 13:28:01
 * @author Tom Knaepen
 */
public class GridPanel {

	private JPanel					m_panel;
	private ArrayList<JComponent>	m_components;
	private int						m_rows, m_cols;

	public GridPanel(int rows, int cols) {
		m_panel = new JPanel();
		m_rows = rows;
		m_cols = cols;
		m_panel.setLayout(new GridLayout(rows, cols));
		m_components = new ArrayList<JComponent>();
	}

	protected void fillGrid() {
		int filled = m_components.size();
		while (filled < m_rows * m_cols) {
			JComponent comp = new JPanel();
			addItem(comp);
		}
	}

	protected int filled() {
		return m_components.size();
	}

	protected int size() {
		return m_rows * m_cols;
	}

	public void addItem(JComponent item) {
		m_panel.add(item);
		m_components.add(item);
	}

	public void removeItem(JComponent item) {
		m_panel.remove(item);
		m_components.remove(item);
	}

	// 0-indexed
	public JComponent getItem(int row, int col) {
		return m_components.get(row * m_cols + col);
	}

	public void setItem(JComponent comp, int row, int col) {
		int position = row * m_cols + col;
		m_components.set(position, comp);
	}

	public JPanel getUI() {
		return m_panel;
	}
}
