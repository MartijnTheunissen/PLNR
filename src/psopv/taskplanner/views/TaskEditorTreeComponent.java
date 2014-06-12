package psopv.taskplanner.views;

/**
 * 
 * A node to put in the taskeditor tree. It holds a component and a custom toString value.
 *
 * @since Apr 29, 2014 7:35:40 PM
 * @author Martijn Theunissen
 */
public class TaskEditorTreeComponent<T> {
	private T		t;
	private String	s;

	/**
	 * Initialize the TETC
	 * @param t type in the taskeditor tree
	 * @param s custom toString value for representing the item
	 */
	public TaskEditorTreeComponent(T t, String s) {
		this.t = t;
		this.s = s;
	}

	/**
	 * Get the component that the taskeditor holds
	 * @return the item from the TETC
	 */
	public T getItem() {
		return t;
	}

	/**
	 * String representation of the component in the node.
	 */
	public String toString() {
		return s;
	}

}