/**
 * 
 */
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
 * $Rev:: 136                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-03 01:03:28 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * 
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - tom.knaepen: <br>
 * 2 - <br>
 * 3 - <br>
 *
 * @since 2-jun.-2014 15:34:46
 * @author tom.knaepen
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
