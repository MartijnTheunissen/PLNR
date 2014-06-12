package psopv.taskplanner.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.util.CompatibilityHints;
import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.exceptions.CalendarIOException;
import psopv.taskplanner.models.Calendar;

/**
 * This class reads an iCal calendar and makes a Calendar object
 *
 * @since Apr 1, 2014 2:42:25 PM
 * @author Martijn Theunissen
 */
public class ICALCalendarReader implements CalendarReader {

	/**
	 * Parse the ical calendar to a calendar
	 * @param importFile the ical calendar to read and parse
	 */
	public Calendar read(File importFile) throws CalendarIOException {
		FileInputStream fin;
		net.fortuna.ical4j.model.Calendar calendar;
		try {
			fin = new FileInputStream(importFile);
			net.fortuna.ical4j.data.CalendarBuilder builder = new net.fortuna.ical4j.data.CalendarBuilder();
			CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
			calendar = builder.build(fin);
		} catch (FileNotFoundException e) {
			throw new CalendarIOException(ApplicationSettings.getInstance().getLocalizedMessage("log.calin.404"));
		} catch (IOException e) {
			throw new CalendarIOException(ApplicationSettings.getInstance().getLocalizedMessage("log.calin.ioex"));
		} catch (ParserException e) {
			throw new CalendarIOException(ApplicationSettings.getInstance().getLocalizedMessage("log.calin.valex"));
		}
		return new Calendar(importFile.getName(), calendar);

	}

}
