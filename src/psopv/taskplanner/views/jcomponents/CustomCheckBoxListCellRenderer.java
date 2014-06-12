package psopv.taskplanner.views.jcomponents;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JList;

import psopv.taskplanner.models.Calendar;

import com.alee.extended.list.CheckBoxCellData;
import com.alee.extended.list.WebCheckBoxListCellRenderer;

/**
 * 
 * Custom check box list cell renderer.
 *
 * @since 2-jun.-2014 15:34:46
 * @author Tom Knaepen
 */
public class CustomCheckBoxListCellRenderer extends WebCheckBoxListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		final CheckBoxCellData data = (CheckBoxCellData) value;
		if (data.getUserObject() instanceof Calendar) {
			Color c = ((Calendar) data.getUserObject()).getColor();
			Color bg = new Color(c.getRed(), c.getBlue(), c.getGreen(), 60);
			comp.setBackground(bg);
			((JComponent) comp).setOpaque(true);
		}
		return comp;
	}
}
