package psopv.taskplanner.io;

import java.io.File;

import psopv.taskplanner.exceptions.CalendarIOException;

/**
 * 
 * This interface defines how a certain CalendarBuilder should operate.
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
