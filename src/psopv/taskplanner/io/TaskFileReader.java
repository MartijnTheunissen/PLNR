package psopv.taskplanner.io;

import java.io.File;

import psopv.taskplanner.exceptions.TaskIOException;
import psopv.taskplanner.models.TaskList;

/**
 * 
 * This interfaces defines which methods a certain TaskFileReader should have.
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
