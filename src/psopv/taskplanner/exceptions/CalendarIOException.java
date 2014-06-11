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
 * This exception occurs when there is a problem reading or writing a calendar from or to a file
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: Initial version<br>
 * 2 - <br>
 * 3 - <br>
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
