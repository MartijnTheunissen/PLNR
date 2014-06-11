/**
 * 
 */
package psopv.taskplanner.io;

import java.io.File;

import psopv.taskplanner.exceptions.TaskIOException;
import psopv.taskplanner.models.Task;
import psopv.taskplanner.models.TaskList;

/**
 * 
 * $Rev:: 136                                                  $:  Revision of last commit<br>
 * $Author:: martijn.theunissen                                $:  Author of last commit<br>
 * $Date:: 2014-06-03 01:03:28 +0200 (Tue, 03 Jun 2014)        $:  Date of last commit<br>
 *
 * Description:	<br>
 * ------------<br>
 * This interface defines what a TaskFileBuilder should do.
 * 
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: initial version<br>
 * 2 - martijn.theunissen: use File instead of filename/path/extension<br>
 * 3 - <br>
 *
 * @since Mar 13, 2014 12:31:03 PM
 * @author Martijn Theunissen
 */
public interface TaskFileBuilder {

	/**
	 * Add a task to the list of tasks to be generated.
	 * @param newTask the task to add
	 */
	void addTask(Task newTask);

	/**
	 * Set the tasklist which will be built into a specific format.
	 * @param taskList the tasklist to be built.
	 */
	void setTaskList(TaskList taskList);

	/**
	 * write the tasks to a file with a specific format defined by the implementing class
	 * @param exportFile the file to export
	 * @throws TaskIOException when invalid data is passed or an IOException occurs.
	 */
	void build(File exportFile) throws TaskIOException;

}
