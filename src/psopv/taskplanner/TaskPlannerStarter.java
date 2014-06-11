package psopv.taskplanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.models.TaskPlannerModel;
import psopv.taskplanner.views.TaskPlannerWindow;

import com.alee.laf.WebLookAndFeel;

/**
 * 
 * 
 * $Rev:: 89                                                   $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-05-11 19:11:30 +0200 (Sun, 11 May 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This class is the main starting point of the program. It initializes the model and creates the user interface.
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial primitive version<br>
 * 2 - tom.knaepen: UIManager.setLookAndFeel<br>
 * 3 - martijn.theunissen: Log uncatched errors<br>
 * 4 - martijn.theunissen: multilanguage logging
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
