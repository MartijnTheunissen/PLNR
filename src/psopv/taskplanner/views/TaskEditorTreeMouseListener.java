package psopv.taskplanner.views;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Observable;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.controllers.TaskPlannerController;
import psopv.taskplanner.controllers.TaskPlannerIOController;
import psopv.taskplanner.models.Task;
import psopv.taskplanner.models.TaskList;
import psopv.taskplanner.models.TaskPlannerModel;
import psopv.taskplanner.views.dialogs.AddTaskDialog;
import psopv.taskplanner.views.dialogs.EditTaskDialog;
import be.uhasselt.oo2.mvc.Controller;

/**
 * 
 * Mouse listener that handles tree related stuff: 
 * Add/remove a node (tasklist/task), 
 * import/export tasklists, ...
 *
 * @since Apr 29, 2014 3:50:04 PM
 * @author Martijn Theunissen
 * @author Tom Knaepen
 */
public class TaskEditorTreeMouseListener extends MouseAdapter {

	private JTree					m_tree;
	private Observable				m_model;
	private Controller				m_controller;
	private DefaultMutableTreeNode	m_lastSelectedTreeNode			= null;
	private DefaultMutableTreeNode	m_lastParentSelectedTreeNode	= null;
	private ApplicationSettings		m_settings						= ApplicationSettings.getInstance();	// for localized messages.

	// initialize
	public TaskEditorTreeMouseListener(JTree tree, Observable model, Controller contr) {
		m_tree = tree;
		m_model = model;
		m_controller = contr;
	}

	/**
	 *	Invoked when the user clicked on the mouse on the taskeditor tree.
	 *  @param e the mouseevent that occured
	 */
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);

		int selectedRow = m_tree.getRowForLocation(e.getX(), e.getY());
		TreePath selectedPath = m_tree.getPathForLocation(e.getX(), e.getY());
		if (selectedRow != -1) {
			if (e.getClickCount() == 1) {
				if (SwingUtilities.isRightMouseButton(e))
					showContextMenu(selectedRow, selectedPath, e);
			} else if (e.getClickCount() == 2) {
				if (SwingUtilities.isLeftMouseButton(e))
					showDoubleClickMenu(selectedRow, selectedPath, e);
			}
		}
	}

	/**
	 * Show a double click popup menu appropriate to node clicked.
	 * @param selectedRow the row of the tree selected
	 * @param selectedPath the path of the tree selected by the mouse
	 */
	private void showDoubleClickMenu(int selectedRow, TreePath selectedPath, MouseEvent e) {
		m_lastSelectedTreeNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
		if (!m_lastSelectedTreeNode.isRoot())
			m_lastParentSelectedTreeNode = (DefaultMutableTreeNode) selectedPath.getParentPath().getLastPathComponent();

		if (m_lastSelectedTreeNode.getLevel() == 2)
			editTask(null);
	}

	/**
	 * Show a context popup menu appropriate to node clicked.
	 * @param selectedRow the row of the tree selected
	 * @param selectedPath the path of the tree selected by the mouse
	 */
	private void showContextMenu(int selectedRow, TreePath selectedPath, MouseEvent e) {
		m_lastSelectedTreeNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
		if (!m_lastSelectedTreeNode.isRoot())
			m_lastParentSelectedTreeNode = (DefaultMutableTreeNode) selectedPath.getParentPath().getLastPathComponent();

		if (m_lastSelectedTreeNode.isRoot()) // root node.
			rootActionMenu(e);
		else if (m_lastSelectedTreeNode.getLevel() == 1) // Task list
			taskListMenu(e);
		else if (m_lastSelectedTreeNode.getLevel() == 2) // Task name
			taskMenu(e);
	}

	/**
	 * Show the action menu for the root node.
	 * @param e the mouseevent that occured
	 */
	private void rootActionMenu(MouseEvent e) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem addTaskList = new JMenuItem(m_settings.getLocalizedMessage("mi.addTaskList"));
		JMenuItem openTaskList = new JMenuItem(m_settings.getLocalizedMessage("menu.file.openTL"));

		popup.add(openTaskList);
		popup.add(addTaskList);
		openTaskList.addActionListener(this::openTaskList);
		addTaskList.addActionListener(this::addTaskList);

		popup.show(e.getComponent(), e.getX(), e.getY());
	}

	/**
	 * Invoked when clicked on the add tasklist context menu
	 * @param e the actionevent that occured
	 */
	private void addTaskList(ActionEvent e) {
		String name = JOptionPane.showInputDialog(m_settings.getLocalizedMessage("nameprompt"));
		if (name != null && name.isEmpty() && !name.trim().equals(""))
			((TaskPlannerModel) m_model).addTaskList(new TaskList(name));
	}

	/**
	 * Invoked when clicked on the open tasklist context menu
	 * @param e the actionevent that occured
	 */
	private void openTaskList(ActionEvent e) {
		JFileChooser openFileChooser = new JFileChooser();
		openFileChooser.setFileFilter(new FileNameExtensionFilter(ApplicationSettings.getInstance().getLocalizedMessage("xmlfile"), "xml"));
		openFileChooser.setFileHidingEnabled(true);
		openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (openFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			new TaskPlannerIOController(m_model).importTaskList(openFileChooser.getSelectedFile());
		}
	}

	/**
	 * show the context menu when clicked on the tasklist in the tree
	 * @param e the mouseevent that occured
	 */
	private void taskListMenu(MouseEvent e) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem addTask = new JMenuItem(m_settings.getLocalizedMessage("mi.addTask"));
		JMenuItem export = new JMenuItem(m_settings.getLocalizedMessage("mi.exporttl"));
		JMenuItem remove = new JMenuItem(m_settings.getLocalizedMessage("mi.removeTaskList"));
		popup.add(addTask);
		popup.add(export);
		popup.add(remove);
		addTask.addActionListener(this::addTask);
		export.addActionListener(this::exportTaskList);
		remove.addActionListener(this::removeTaskList);

		popup.show(e.getComponent(), e.getX(), e.getY());
	}

	/**
	 * Invoked when clicked on the add task context menu
	 * @param e the actionevent that occured
	 */
	private void addTask(ActionEvent e) {
		AddTaskDialog dialog = new AddTaskDialog(m_model, m_controller);
		TaskList list = ((TaskEditorTreeComponent<TaskList>) m_lastSelectedTreeNode.getUserObject()).getItem();
		dialog.setSelectedList(list);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	/**
	 * Invoked when clicked on the export task list context menu
	 * @param e the actionevent that occured
	 */
	private void exportTaskList(ActionEvent e) {
		JFileChooser saveFileChooser = new JFileChooser();
		saveFileChooser.setFileFilter(new FileNameExtensionFilter(m_settings.getLocalizedMessage("xmlfile"), "xml"));
		saveFileChooser.setFileHidingEnabled(true);
		saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		saveFileChooser.setSelectedFile(new File(m_lastSelectedTreeNode.getUserObject().toString().replaceAll(" ", "").toLowerCase() + ".xml"));
		saveFileChooser.setApproveButtonText("Save");
		if (saveFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File fileToBeSaved = saveFileChooser.getSelectedFile();
			if (!saveFileChooser.getSelectedFile().getAbsolutePath().endsWith(".xml")) {
				fileToBeSaved = new File(saveFileChooser.getSelectedFile() + ".xml");
			}
			@SuppressWarnings("unchecked")
			TaskList list = ((TaskEditorTreeComponent<TaskList>) m_lastSelectedTreeNode.getUserObject()).getItem();
			new TaskPlannerIOController(m_model).exportTaskList(list, fileToBeSaved);
		}
	}

	/**
	 * Invoked when clicked on the remove task list context menu.
	 * @param e the actionevent that occured
	 */
	@SuppressWarnings("unchecked")
	private void removeTaskList(ActionEvent e) {
		int selectedOption = JOptionPane.showConfirmDialog(null, m_settings.getLocalizedMessage("deleteprompt"), m_settings.getLocalizedMessage("areyousure"), JOptionPane.YES_NO_OPTION);
		if (selectedOption == JOptionPane.YES_OPTION) {
			((TaskPlannerModel) m_model).removeTaskList(((TaskEditorTreeComponent<TaskList>) m_lastSelectedTreeNode.getUserObject()).getItem());
		}
	}

	/**
	 * show a popup menu when the user clicks on the task node in the tree
	 * @param e the mouseevent that occurd
	 */
	private void taskMenu(MouseEvent e) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem editTask = new JMenuItem(m_settings.getLocalizedMessage("mi.editTask"));
		JMenuItem removeTask = new JMenuItem(m_settings.getLocalizedMessage("mi.removeTask"));
		JMenuItem dupe = new JMenuItem(m_settings.getLocalizedMessage("mi.dupeTask"));

		popup.add(editTask);
		popup.add(removeTask);
		popup.add(dupe);
		editTask.addActionListener(this::editTask);
		removeTask.addActionListener(this::removeTask);
		dupe.addActionListener(this::dupeTask);

		popup.show(e.getComponent(), e.getX(), e.getY());
	}

	@SuppressWarnings("unchecked")
	private void editTask(ActionEvent e) {
		Task selected = ((TaskEditorTreeComponent<Task>) m_lastSelectedTreeNode.getUserObject()).getItem();
		//TaskList list = ((TaskEditorTreeComponent<TaskList>) m_lastParentSelectedTreeNode.getUserObject()).getItem();

		EditTaskDialog dialog = new EditTaskDialog(selected);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	/**
	 * Invoked when the user clicked on the remove task menuitem in the context menu
	 * @param e the actionevent that occured
	 */
	@SuppressWarnings("unchecked")
	private void removeTask(ActionEvent e) {
		Task selected = ((TaskEditorTreeComponent<Task>) m_lastSelectedTreeNode.getUserObject()).getItem();
		TaskList list = ((TaskEditorTreeComponent<TaskList>) m_lastParentSelectedTreeNode.getUserObject()).getItem();

		new TaskPlannerController(m_model).removeTask(list, selected);
	}

	/**
	 * Invoked when the user clicked on the duplicate task menuitem in the context menu
	 * @param e the actionevent that occured
	 */
	@SuppressWarnings("unchecked")
	private void dupeTask(ActionEvent e) {
		Task selected = ((TaskEditorTreeComponent<Task>) m_lastSelectedTreeNode.getUserObject()).getItem();
		TaskList list = ((TaskEditorTreeComponent<TaskList>) m_lastParentSelectedTreeNode.getUserObject()).getItem();

		new TaskPlannerController(m_model).addTask(list, new Task(selected));
	}

}
