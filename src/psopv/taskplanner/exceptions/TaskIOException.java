/**
 * 
 */
package psopv.taskplanner.exceptions;

/**
 * 
 * $Rev:: 41                                                   $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-03-14 20:21:38 +0100 (Fri, 14 Mar 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This exception occurs when there is a problem with building or reading a tasklist.
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: Initial version<br>
 * 2 - <br>
 * 3 - <br>
 *
 * @since Mar 13, 2014 12:36:43 PM
 * @author Martijn Theunissen
 */
public class TaskIOException extends Exception {

	/**
	 * Generated serial version UID
	 */
	private static final long	serialVersionUID	= -764795042462232992L;

	/**
	 * Error description message
	 */
	private final String		m_message;

	/**
	 * Initialize the exception.
	 * @param message the error description message
	 */
	public TaskIOException(String message) {
		m_message = message;
	}

	/**
	 * Get the error description message
	 */
	public String getMessage() {
		return m_message;
	}

}
