/**
 * 
 */
package psopv.taskplanner.io;

import java.io.File;

import psopv.taskplanner.exceptions.CalendarIOException;
import psopv.taskplanner.models.Calendar;

/**
 * 
 * $Rev:: 43                                                   $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-03-20 22:45:37 +0100 (Thu, 20 Mar 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This interface defines what a CalendarReader should do.
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: Initial version<br>
 * 2 - martijn.theunissen: Use files instead of filename/path/extension<br>
 * 3 - <br>
 *
 * @since Mar 13, 2014 12:29:15 PM
 * @author Martijn Theunissen
 */
public interface CalendarReader {

	/**
	 * Read a calendar from a file.
	 * @param importFile the file to import
	 * @return a calendar which is parsed from the file.
	 * @throws CalendarIOException when there is a problem reading the calendar or invalid data is passed.
	 */
	Calendar read(File importFile) throws CalendarIOException;

}
