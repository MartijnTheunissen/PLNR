/**
 * 
 */
package psopv.taskplanner.io;

import java.io.File;

import psopv.taskplanner.exceptions.CalendarIOException;

/**
 * 
 * $Rev:: 46                                                   $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-04-01 15:30:28 +0200 (Tue, 01 Apr 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This interface defines how a certain CalendarBuilder should operate.
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: Initial version<br>
 * 2 - martijn.theunissen: Using File instead of filename/filepath, void returntype, calendar as param<br>
 * 3 - <br>
 *
 * @since Mar 13, 2014 12:31:20 PM
 * @author Martijn Theunissen
 */
public interface CalendarBuilder {

	/**
	 * write the tasks to a file with a specific format defined by the implementing class
	 * @param exportedFile where it should be exported.
	 * @param calendar the calendar to build
	 * @throws CalendarIOException when invalid data is passed or an IOException occurs.
	 */
	void build(File exportedFile, psopv.taskplanner.models.Calendar calendar) throws CalendarIOException;
}
