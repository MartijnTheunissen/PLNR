/**
 * 
 */
package psopv.taskplanner.models;

/**
 * 
 * $Rev:: 96                                                   $:  Revision of last commit<br>
 * $Author:: tom.knaepen                                       $:  Author of last commit<br>
 * $Date:: 2014-05-18 15:43:10 +0200 (Sun, 18 May 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * Represents an update in the task planner model
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - tom.knaepen: initial version<br>
 * 2 - tom.knaepen: changed boolean to enum, specialized updates<br>
 * 3 - <br>
 *
 * @since 11-mei-2014 14:44:53
 * @author tom.knaepen
 */
public class TaskPlannerUpdate {

	private Object	m_updatedItem;

	public enum TaskPlannerChange {
		ADDED, REMOVED, UPDATED, VISIBLE, INVISIBLE
	}

	private TaskPlannerChange	m_status;	// true if the item was added, false if not

	public TaskPlannerUpdate(Object item, TaskPlannerChange status) {
		m_updatedItem = item;
		m_status = status;
	}

	public TaskPlannerChange getStatus() {
		return m_status;
	}

	public Object getItem() {
		return m_updatedItem;
	}
}
