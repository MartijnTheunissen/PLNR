/**
 * 
 */
package psopv.taskplanner.views;

/**
 * 
 * $Rev:: 136                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-03 01:03:28 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * A node to put in the taskeditor tree. It holds a component and a custom toString value.
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version<br>
 * 2 - <br>
 * 3 - <br>
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