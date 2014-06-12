package psopv.taskplanner.exceptions;

/**
 * 
 * This exception occurs when there is a problem reading or writing a calendar from or to a file.
 *
 * @since Mar 14, 2014 9:13:10 PM
 * @author Martijn Theunissen
 */
public class CalendarIOException extends Exception {

	/**
	 * Generated serial version UID.
	 */
	private static final long	serialVersionUID	= -2320001001246607030L;

	private final String		m_message;

	/**
	 * Initialize the exception.
	 * @param message the error description message
	 */
	public CalendarIOException(String message) {
		m_message = message;
	}

	/**
	 * Get the error description message
	 */
	public String getMessage() {
		return m_message;
	}

}
