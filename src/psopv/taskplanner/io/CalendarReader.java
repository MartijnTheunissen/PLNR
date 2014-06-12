package psopv.taskplanner.io;

import java.io.File;

import psopv.taskplanner.exceptions.CalendarIOException;
import psopv.taskplanner.models.Calendar;

/**
 * 
 * This interface defines what a CalendarReader should do.
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
