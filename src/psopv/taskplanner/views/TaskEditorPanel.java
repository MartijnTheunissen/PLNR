package psopv.taskplanner.views;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.models.Task;
import psopv.taskplanner.models.TaskList;
import psopv.taskplanner.models.TaskPlannerModel;
import psopv.taskplanner.views.dialogs.AddTaskDialog;
import be.uhasselt.oo2.mvc.AbstractView;
import be.uhasselt.oo2.mvc.Controller;

/**
 * 
 * This class represents the visual component of a taskeditor.
 *
 *
 * @since Mar 19, 2014 8:57:09 PM
 * @author Martijn Theunissen
 */
public class TaskEditorPanel extends AbstractView implements Observer {

	private JTree					m_tree;
	private AddTaskDialog			m_atd;
	private final int				MINIMUM_WIDTH	= 200;
	private final int				MINIMUM_HEIGHT	= 250;
	private JScrollPane				m_scrollbar;
	private Observable				m_model;
	private Controller				m_controller;
	private DefaultMutableTreeNode	m_topNode;
	private ApplicationSettings		m_settings		= ApplicationSettings.getInstance();	// for localized messages

	/**
	 * Create the TaskEditorPanel
	 * @param model the model to set
	 * @param controller the controller to set
	 */
	public TaskEditorPanel(Observable model, Controller controller) {
		super(model, controller);

		m_model = model;
		model.addObserver(this);
		if (controller == null)
			m_controller = defaultController(model);
		else
			m_controller = controller;

		initializeGUIComponents();
		initializePopupDialogs(model, controller);
	}

	/**
	 * Initialize all the graphical components of this view.
	 */
	private void initializeGUIComponents() {
		m_topNode = new DefaultMutableTreeNode(m_settings.getLocalizedMessage("tasklists"));
		createTree((TaskPlannerModel) m_model);

		m_tree = new JTree(m_topNode) {
			@Override
			public Dimension getPreferredScrollableViewportSize() {
				return getPreferredSize();
			}
		};
		m_tree.addMouseListener(new TaskEditorTreeMouseListener(m_tree, m_model, m_controller));

		m_scrollbar = new JScrollPane();
		m_scrollbar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		m_scrollbar.setViewportView(m_tree);
	}

	/**
	 * Initialize the popup dialogs of this panel
	 * @param model model of the dialog
	 * @param controller controller of the dialog
	 */
	private void initializePopupDialogs(Observable model, Controller controller) {
		if (m_atd == null)
			m_atd = new AddTaskDialog(model, controller);

		m_atd.setTitle(m_settings.getLocalizedMessage("addt"));

		m_atd.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				m_atd.setVisible(false);
			}
		});

		m_atd.setSize(450, 350);
		m_atd.setAlwaysOnTop(true);
		m_atd.setAutoRequestFocus(true);
		m_atd.setVisible(false);
	}

	/*
	 * Update the panel when the observable model sends a change.
	 * 
	 * @param o the observed object that is changed.
	 * 
	 * @param arg1 extra argument provided by the observed object.
	 */
	public void update(Observable o, Object arg1) {
		TaskPlannerModel mod = (TaskPlannerModel) o;

		createTree(mod);
		m_tree.repaint();
		m_scrollbar.revalidate();

	}

	/**
	 * Create the tree structure and update the UI of the JTree
	 * @param mod the taskplanner model
	 */
	private void createTree(TaskPlannerModel mod) {
		m_topNode.removeAllChildren();

		ArrayList<TaskList> list = mod.getTaskLists();

		for (TaskList t : list) {
			DefaultMutableTreeNode tlNode = new DefaultMutableTreeNode(new TaskEditorTreeComponent<TaskList>(t, t.getName()));
			for (int i = 0; i < t.getSize(); i++) {
				DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(new TaskEditorTreeComponent<Task>(t.getTask(i), t.getTask(i).getName()));
				/*
				 * DefaultMutableTreeNode taskDescription = new DefaultMutableTreeNode(new
				 * TaskEditorTreeComponent<Task>(t.getTask(i),
				 * m_settings.getLocalizedMessage("lblDescription") +
				 * t.getTask(i).getDescription())); DefaultMutableTreeNode taskPriority = new
				 * DefaultMutableTreeNode(new TaskEditorTreeComponent<Task>(t.getTask(i),
				 * m_settings.getLocalizedMessage("lblPriority") + t.getTask(i).getPriority()));
				 * DefaultMutableTreeNode taskDeadline = new DefaultMutableTreeNode(new
				 * TaskEditorTreeComponent<Task>(t.getTask(i),
				 * m_settings.getLocalizedMessage("lblDeadline") + t.getTask(i).getDeadline()));
				 * DefaultMutableTreeNode taskDuration = new DefaultMutableTreeNode(new
				 * TaskEditorTreeComponent<Task>(t.getTask(i),
				 * m_settings.getLocalizedMessage("lblDuration") + t.getTask(i).getDuration()));
				 */
				tlNode.add(taskNode);
				/*
				 * taskNode.add(taskDescription); taskNode.add(taskPriority);
				 * taskNode.add(taskDeadline); taskNode.add(taskDuration);
				 */
			}
			m_topNode.add(tlNode);
		}
		if (m_tree != null) {
			m_tree.updateUI();
			m_tree.setCellRenderer(new TaskEditorTreeToolTipRenderer());
			ToolTipManager.sharedInstance().registerComponent(m_tree);
		}
	}

	/**
	 * Get the UI of this view.
	 * @return the container UI of this view.
	 */
	public JComponent getUI() {
		return m_scrollbar;
	}
}
