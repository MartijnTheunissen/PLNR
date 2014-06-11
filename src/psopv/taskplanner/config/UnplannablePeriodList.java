/**
 * 
 */
package psopv.taskplanner.config;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * $Rev:: 136                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-03 01:03:28 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * list of unplannable periods
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version<br>
 * 2 - <br>
 * 3 - <br>
 *
 * @since May 30, 2014 6:47:03 PM
 * @author Martijn Theunissen
 */
public class UnplannablePeriodList implements Serializable {

	private ArrayList<UnplannablePeriod>	m_unplannableperiods;

	/**
	 * Create an unplannableperiodlist
	 */
	public UnplannablePeriodList() {
		m_unplannableperiods = new ArrayList<UnplannablePeriod>();
	}

	/**
	 * @return ArrayList representation
	 */
	public ArrayList<UnplannablePeriod> getList() {
		return m_unplannableperiods;
	}

	/**
	 * update the list
	 * @param list the list to set
	 */
	public void setList(ArrayList<UnplannablePeriod> list) {
		m_unplannableperiods = list;
	}

	/**
	 * Add an unplannable period
	 * @param period the period to add
	 */
	public void addUnplannablePeriod(UnplannablePeriod period) {
		m_unplannableperiods.add(period);
	}

	/**
	 * @return the size of the list
	 */
	public int getSize() {
		return m_unplannableperiods.size();
	}

	/**
	 * Remove an unplannable period
	 * @param period the period to remove
	 */
	public void removeUnplannablePeriod(UnplannablePeriod period) {
		m_unplannableperiods.remove(period);
	}

	/**
	 * String representation of the list
	 */
	public String toString() {
		if (m_unplannableperiods.size() == 0)
			return "";
		String out = "";
		for (UnplannablePeriod period : m_unplannableperiods)
			out += period.encode() + ",";
		out.substring(0, out.length() - 1); // remove last ','
		return out;
	}

	/**
	 * Parse a string to a list
	 * @param in the list to parse
	 * @return the parsed object (toString objects should be parsed correctly)
	 */
	public static UnplannablePeriodList parse(String in) {
		if (in.isEmpty())
			return new UnplannablePeriodList();
		String read = in;
		UnplannablePeriodList list = new UnplannablePeriodList();
		int nextsplit = read.indexOf(',');
		while (nextsplit != -1 && read.length() > 1) {
			list.addUnplannablePeriod(UnplannablePeriod.parse(read.substring(0, nextsplit)));
			read = read.substring(nextsplit + 1);
			nextsplit = read.indexOf(',');
		}
		if (read.length() > 1)
			list.addUnplannablePeriod(UnplannablePeriod.parse(read));

		return list;
	}

}
