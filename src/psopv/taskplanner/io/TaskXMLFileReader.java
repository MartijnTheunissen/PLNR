package psopv.taskplanner.io;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.exceptions.TaskIOException;
import psopv.taskplanner.models.Task;
import psopv.taskplanner.models.TaskList;

/**
 * 
 * $Rev:: 130                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-01 17:17:44 +0200 (Sun, 01 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This class will read an XML file, parse it, and create a tasklist.
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: Initial version<br>
 * 2 - martijn.theunissen: Using ISO_ZONED_DATE_TIME as datetime format<br>
 * 3 - martijn.theunissen: name the tasklist<br>
 * 4 - martijn.theunissen: updated translations<br>
 *
 * @since Mar 20, 2014 4:13:51 PM
 * @author Martijn Theunissen
 */
public class TaskXMLFileReader implements TaskFileReader {

	private final static Logger	log	= LogManager.getLogger(TaskXMLFileReader.class);
	private TaskList			m_taskList;

	public TaskList read(File importFile) throws TaskIOException {

		if (importFile == null) {
			log.warn(ApplicationSettings.getInstance().getLocalizedMessage("log.err.file"));
			return new TaskList("Empty");
		}

		m_taskList = new TaskList(importFile.getName());
		log.info(ApplicationSettings.getInstance().getLocalizedMessage("log.read.file") + " " + importFile.getName());

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp;

		try {
			sp = spf.newSAXParser();
			SAXHandler sh = new SAXHandler();
			sp.parse(importFile, sh);
		} catch (ParserConfigurationException e) {
			String error = ApplicationSettings.getInstance().getLocalizedMessage("txmlfr.createsax") + ": " + e.getLocalizedMessage();
			log.error(error);
			throw new TaskIOException(error);
		} catch (SAXException e) {
			String error = ApplicationSettings.getInstance().getLocalizedMessage("txmlfr.createsax") + ": " + e.getLocalizedMessage();
			log.error(error);
			throw new TaskIOException(error);
		} catch (IOException e) {
			String error = ApplicationSettings.getInstance().getLocalizedMessage("txmlfr.io") + ": " + e.getLocalizedMessage();
			log.error(error);
			throw new TaskIOException(error);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw new TaskIOException(ApplicationSettings.getInstance().getLocalizedMessage("log.err.tlparse"));
		}

		return m_taskList;
	}

	private class SAXHandler extends DefaultHandler {
		private Task		m_task;
		private String		m_elementContent;
		private Duration	m_duration;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equalsIgnoreCase("Task")) {
				m_task = new Task();
				m_task.setId(attributes.getValue("id"));
			} else if (qName.equalsIgnoreCase("Duration"))
				m_duration = Duration.ZERO;

		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equalsIgnoreCase("Task"))
				m_taskList.addTask(m_task);
			else if (qName.equalsIgnoreCase("Name"))
				m_task.setName(m_elementContent);
			else if (qName.equalsIgnoreCase("Description"))
				m_task.setDescription(m_elementContent);
			else if (qName.equalsIgnoreCase("Priority"))
				m_task.setPriority(parsePriority(m_elementContent));
			else if (qName.equalsIgnoreCase("Minutes"))
				m_duration = m_duration.plusMinutes(Integer.parseInt(m_elementContent));
			else if (qName.equalsIgnoreCase("Hours"))
				m_duration = m_duration.plusHours(Integer.parseInt(m_elementContent));
			else if (qName.equalsIgnoreCase("Duration"))
				m_task.setDuration(m_duration);
			else if (qName.equalsIgnoreCase("Deadline")) {
				m_task.setDeadline(ZonedDateTime.parse(m_elementContent, DateTimeFormatter.ISO_ZONED_DATE_TIME));
			}
		}

		/**
		 * Parse a priority string and give the value of that string back.
		 * @param priority a priority-string (a number or a description)
		 * @return the priority in integer form
		 */
		private int parsePriority(String priority) {
			int priorityValue = 0;
			if (priority.equalsIgnoreCase("Very Low"))
				priorityValue = 1;
			else if (priority.equalsIgnoreCase("Low"))
				priorityValue = 2;
			else if (priority.equalsIgnoreCase("Medium"))
				priorityValue = 3;
			else if (priority.equalsIgnoreCase("High"))
				priorityValue = 4;
			else if (priority.equalsIgnoreCase("Very High"))
				priorityValue = 5;
			else {
				try {
					priorityValue = Integer.parseInt(priority);
				} catch (NumberFormatException nfe) {
					log.warn(ApplicationSettings.getInstance().getLocalizedMessage("log.warn.priorityparse"));
					priorityValue = 3;
				}
			}
			return priorityValue;
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			m_elementContent = new String(ch, start, length);
		}

	}
}
