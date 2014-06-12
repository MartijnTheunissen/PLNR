package psopv.taskplanner.exceptions;

/**
 * 
 * This exception occurs when there is a problem with building or reading a tasklist.
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
