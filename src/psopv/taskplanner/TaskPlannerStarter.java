package psopv.taskplanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.models.TaskPlannerModel;
import psopv.taskplanner.views.TaskPlannerWindow;

import com.alee.laf.WebLookAndFeel;

/**
 *
 * This class is the main starting point of the program. It initializes the model and creates the user interface.
 * 
 * @since Mar 12, 2014 8:10:08 PM
 * @author Martijn Theunissen
 */
public class TaskPlannerStarter {

	private final static Logger	log	= LogManager.getLogger(TaskPlannerStarter.class);

	/* The model of the taskplanner */
	private TaskPlannerModel	m_model;

	/* The main GUI of the taskplanner */
	private TaskPlannerWindow	m_window;

	/**
	 *	Default constructor of the TPStarter. It will initialize the TPModel
	 */
	public TaskPlannerStarter() {

		m_model = new TaskPlannerModel();
	}

	/**
	 * Create the main GUI of the taskplanner
	 */
	public void createMainWindow() {

		m_window = new TaskPlannerWindow(m_model, null);
		m_model.addObserver(m_window);
	}

	/**
	 * Start the application
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		log.info(ApplicationSettings.getInstance().getLocalizedMessage("log.create"));
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				TaskPlannerStarter starter = new TaskPlannerStarter();
				WebLookAndFeel.install();
				WebLookAndFeel.setDecorateFrames(true);
				WebLookAndFeel.setDecorateDialogs(true);
				/*
				 * try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
				 * catch (Exception e) { log.error(e); }
				 */
				starter.createMainWindow();
			}
		});

	}
}
