package psopv.taskplanner.views;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * 
 * Tooltip renderer for the tree
 *
 * @since May 11, 2014 9:02:08 PM
 * @author Martijn Theunissen
 */

public class TaskEditorTreeToolTipRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		final Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		this.setToolTipText(value.toString());
		return comp;
	}
}
