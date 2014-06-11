/**
 * 
 */
package psopv.taskplanner.controllers;

import java.io.File;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.exceptions.CalendarIOException;
import psopv.taskplanner.exceptions.TaskIOException;
import psopv.taskplanner.io.CalendarBuilder;
import psopv.taskplanner.io.CalendarReader;
import psopv.taskplanner.io.ICALCalendarBuilder;
import psopv.taskplanner.io.ICALCalendarReader;
import psopv.taskplanner.io.TaskFileBuilder;
import psopv.taskplanner.io.TaskFileReader;
import psopv.taskplanner.io.TaskXMLFileBuilder;
import psopv.taskplanner.io.TaskXMLFileReader;
import psopv.taskplanner.models.Calendar;
import psopv.taskplanner.models.TaskList;
import psopv.taskplanner.models.TaskPlannerModel;
import be.uhasselt.oo2.mvc.AbstractController;

import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;

/**
 * 
 * $Rev:: 136                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-03 01:03:28 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This controller manages the every IO aspect like import and export a tasklist or a calendar.,
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version<br>
 * 2 - martijn.theunissen: ICAL Calendar IO support<br>
 * 3 - martijn.theunissen: mutliple calendar support<br>
 * 4 - martijn.theunissen: multiple task list support<br>
 * 5 - martijn.theunissen: error messages are multilanguage<br>
 * 6  - martijn.theunissen: notification support<br>
 *
 * @since Mar 20, 2014 3:32:50 PM
 * @author Martijn Theunissen
 */
public class TaskPlannerIOController extends AbstractController {

	private final static Logger	log			= LogManager.getLogger(TaskPlannerIOController.class);
	private ApplicationSettings	m_settings	= ApplicationSettings.getInstance();

	/**
	 * Initialize the IOController
	 * @param model the model
	 */
	public TaskPlannerIOController(Observable model) {
		super(model);
	}

	/**
	 * Import a taskfile into a tasklist and update the model
	 * @param importFile the file to import
	 */
	public void importTaskList(File importFile) {
		TaskFileReader reader = new TaskXMLFileReader();
		TaskList list;
		try {
			list = reader.read(importFile);
		} catch (TaskIOException e) {
			String error = m_settings.getLocalizedMessage("log.err.tlimp") + " - " + e.getLocalizedMessage();
			log.error(error);
			NotificationManager.showNotification(error, NotificationIcon.error.getIcon());
			return;
		}

		((TaskPlannerModel) getModel()).addTaskList(list);
	}

	/**
	 * Export a tasklist to a file.
	 * @param list the tasklist to export
	 * @param exportFile the file to export to.
	 */
	public void exportTaskList(TaskList list, File exportFile) {
		TaskFileBuilder builder = new TaskXMLFileBuilder();
		if (((TaskPlannerModel) getModel()).containsTaskList(list))
			builder.setTaskList(list);
		else {
			log.warn(m_settings.getLocalizedMessage("log.warn.tlnomod"));
			builder.setTaskList(list);
		}

		try {
			builder.build(exportFile);
		} catch (TaskIOException e) {
			String error = m_settings.getLocalizedMessage("log.err.tlexp") + " - " + e.getLocalizedMessage();
			log.error(error);
			NotificationManager.showNotification(error, NotificationIcon.error.getIcon());
		}
	}

	/**
	 * Import an ICal Calendar from a file
	 * @param selectedFile the ical calendar to import
	 */
	public void importICalendar(File selectedFile) {
		CalendarReader reader = new ICALCalendarReader();
		Calendar importedCalendar;

		try {
			importedCalendar = reader.read(selectedFile);
		} catch (CalendarIOException e) {
			String error = m_settings.getLocalizedMessage("log.err.calimp") + " - " + e.getLocalizedMessage();
			log.error(error);
			NotificationManager.showNotification(error, NotificationIcon.error.getIcon());
			return;
		}

		((TaskPlannerModel) getModel()).addCalendar(importedCalendar);
	}

	/**
	 * Export an ICal Calendar to a file
	 * @param fileToBeSaved the file to export to.
	 * @param calendar the calendar to export
	 */
	public void exportCalendar(File fileToBeSaved, Calendar calendar) {
		CalendarBuilder builder = new ICALCalendarBuilder();

		try {
			builder.build(fileToBeSaved, calendar);
		} catch (CalendarIOException e) {
			String error = m_settings.getLocalizedMessage("log.err.calexp") + " - " + e.getLocalizedMessage();
			log.error(error);
			NotificationManager.showNotification(error, NotificationIcon.error.getIcon());
		}
	}

}
