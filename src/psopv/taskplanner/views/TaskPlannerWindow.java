package psopv.taskplanner.views;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.config.ViewPreferences;
import psopv.taskplanner.config.ViewPreferences.CenterView;
import psopv.taskplanner.controllers.TaskPlannerController;
import psopv.taskplanner.models.TaskPlannerModel;
import be.uhasselt.oo2.mvc.AbstractView;
import be.uhasselt.oo2.mvc.Controller;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;

/**
 * 
 * This class represents the main window of the application.
 * 
 * @since Mar 12, 2014 7:15:49 PM
 * @author Martijn Theunissen
 * @author Tom Knaepen
 */
public class TaskPlannerWindow extends AbstractView implements Observer, ComponentListener {

	/**
	 * Window frame.
	 */
	private WebFrame					m_frame;

	/**
	 * The menu bar.
	 */
	private TaskPlannerMenuBar			m_menuBar;

	/* Window parameters */
	private final String				TITLE				= ApplicationSettings.getInstance().getLocalizedMessage("window.title");
	private final int					INIT_WINDOW_HEIGHT	= 500;
	private final int					INIT_WINDOW_WIDTH	= 800;
	private final java.awt.Dimension	INIT_WINDOW_SIZE	= new java.awt.Dimension(INIT_WINDOW_WIDTH, INIT_WINDOW_HEIGHT);
	private final boolean				WINDOW_ISRESIZABLE	= true;

	/**
	 * Different panels
	 */
	private TaskEditorPanel				m_taskEditorPanel;
	private CalendarList				m_calendarListView;
	private ListView					m_listView;
	private WeekView					m_weekView;
	private TaskListView				m_taskListView;
	private ViewToolBar					m_viewToolBar;
	private WebPanel					m_centerPanel;

	/**
	 * Initialize the main TaskPlanner window.
	 * @param model the TPM
	 * @param controller the controller
	 */
	public TaskPlannerWindow(TaskPlannerModel model, Controller controller) {
		super(model, controller);
		initFrame();
		ApplicationSettings.getInstance().addObserver(this);
		m_taskEditorPanel = new TaskEditorPanel(model, controller);
		m_menuBar = new TaskPlannerMenuBar(model, controller);
		m_listView = new ListView(model, controller);
		m_taskListView = new TaskListView(model, controller);
		m_calendarListView = new CalendarList(model, controller);
		m_weekView = new WeekView(model, controller);
		m_viewToolBar = new ViewToolBar(model);
		m_viewToolBar.getUI().setBorder(new EmptyBorder(5, 20, 5, 20));

		m_frame.setLayout(new BorderLayout());

		WebPanel westPanel = new WebPanel();
		westPanel.setLayout(new GridLayout(2, 1));
		westPanel.add(m_calendarListView.getUI());
		westPanel.add(m_taskEditorPanel.getUI());
		m_frame.add(westPanel, BorderLayout.WEST);
		m_frame.addComponentListener(this);

		m_centerPanel = new WebPanel();
		m_centerPanel.setLayout(new BorderLayout());

		m_centerPanel.add(m_weekView, BorderLayout.CENTER);
		m_centerPanel.add(m_viewToolBar.getUI(), BorderLayout.NORTH);
		m_frame.add(m_centerPanel, BorderLayout.CENTER);

		m_frame.setJMenuBar(m_menuBar);

		ViewPreferences.getInstance().addObserver(this);
		m_frame.setShowResizeCorner(true);
		m_frame.packAndCenter(false);
		m_frame.setSize(1200, 650);
		m_frame.setResizable(true);
		m_frame.setLocationRelativeTo(null); // place the window at the center of the screen by default
		m_frame.setVisible(true);
		m_frame.setExtendedState(m_frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	/**
	 * Initialize the frame
	 */
	private void initFrame() {
		m_frame = new WebFrame(TITLE);
		m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//m_frame.setSize(INIT_WINDOW_SIZE);
		m_frame.setResizable(WINDOW_ISRESIZABLE);
	}

	/**
	 * Update the window when the observable model sends a change.
	 * @param o the observed object that is changed.
	 * @param arg1 extra argument provided by the observed object.
	 */
	public void update(Observable o, Object arg1) {
		if (arg1 != null && arg1 instanceof String && ((String) arg1).contains("VS")) // View settings updates
		{
			m_frame.remove(m_centerPanel);
			m_centerPanel.removeAll();

			if (ViewPreferences.getInstance().getCenterView() == CenterView.WEEK)
				m_centerPanel.add(m_weekView, BorderLayout.CENTER);
			else if (ViewPreferences.getInstance().getCenterView() == CenterView.LIST)
				m_centerPanel.add(m_listView, BorderLayout.CENTER);
			else
				m_centerPanel.add(m_taskListView, BorderLayout.CENTER);

			m_centerPanel.add(m_viewToolBar.getUI(), BorderLayout.NORTH);

			m_frame.add(m_centerPanel, BorderLayout.CENTER);
			m_frame.revalidate();
			m_frame.repaint();
		}
		m_frame.setTitle(ApplicationSettings.getInstance().getLocalizedMessage("window.title"));
	}

	/**
	 * Get the default controller used with this window.
	 * @return the default controller: a new TaskPlannerController with the current model.
	 */
	public Controller getDefaultController() {
		return new TaskPlannerController(getModel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		//m_frame.setShowResizeCorner(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub	
		m_frame.setShowResizeCorner(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
	}

}
