package psopv.taskplanner.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.ValidationException;
import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.exceptions.CalendarIOException;

/**
 * 
 * Write a calendar to file.
 *
 * @since Apr 1, 2014 2:58:43 PM
 * @author Martijn Theunissen
 */
public class ICALCalendarBuilder implements CalendarBuilder {

	/*
	 * (non-Javadoc)
	 * 
	 * @see psopv.taskplanner.io.CalendarBuilder#build(java.io.File)
	 */
	@Override
	public void build(File exportedFile, psopv.taskplanner.models.Calendar cal) throws CalendarIOException {
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(exportedFile);
			CalendarOutputter outputter = new CalendarOutputter();
			outputter.setValidating(false);
			outputter.output(cal.getICAL4JCalendar(), fout);
		} catch (FileNotFoundException e) {
			throw new CalendarIOException(ApplicationSettings.getInstance().getLocalizedMessage("log.calex.404"));
		} catch (IOException e) {
			throw new CalendarIOException(ApplicationSettings.getInstance().getLocalizedMessage("log.calex.ioex"));
		} catch (ValidationException e) {
			throw new CalendarIOException(ApplicationSettings.getInstance().getLocalizedMessage("log.calex.valex") + e.getLocalizedMessage());
		}
	}

}
