/**
 * 
 */
package psopv.taskplanner.io;

import java.io.File;

import psopv.taskplanner.exceptions.TaskIOException;
import psopv.taskplanner.models.TaskList;

/**
 * 
 * $Rev:: 136                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-03 01:03:28 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This interfaces defines which methods a certain TaskFileReader should have.
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: Initial version<br>
 * 2 - martijn.theunissen: use File instead of filename/path/extension<br>
 * 3 - <br>
 *
 * @since Mar 13, 2014 12:28:46 PM
 * @author Martijn Theunissen
 */
public interface TaskFileReader {

	/**
	 * Read a tasklist from a file.
	 * @param importFile the file to import
	 * @return a tasklist which is parsed from the file.
	 * @throws TaskIOException the task IO problem
	 */
	TaskList read(File importFile) throws TaskIOException;

}
