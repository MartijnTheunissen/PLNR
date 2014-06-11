/**
 * 
 */
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
 * 
 * $Rev:: 113                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-05-29 16:59:57 +0200 (Thu, 29 May 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This class reads an ical calendar and makes a Calendar object
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - Martijn Theunissen: initial version<br>
 * 2 - martijn.theunissen: multilanguage errors<br>
 * 3 - <br>
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
