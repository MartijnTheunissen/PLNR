package psopv.taskplanner.models;

/**
 * 
 * Represents an update in the task planner model
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
