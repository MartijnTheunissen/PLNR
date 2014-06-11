/**
 * 
 */
package psopv.taskplanner.io;

import java.io.File;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
 * This builder makes a XML file from a TaskList.
 * <br>
 * Changes:<br>
 * ------------<br>
 * 1 - martijn.theunissen: Initial version<br>
 * 2 - martijn.theunissen: Using ISO_ZONED_DATE_TIME as datetime format<br>
 * 3 - martijn.theunissen: name the tasklist<br>
 * 4 - martijn.theunissen: updated translations<br>
 *
 * @since Mar 20, 2014 7:28:27 PM
 * @author Martijn Theunissen
 */
public class TaskXMLFileBuilder implements TaskFileBuilder {

	private final static Logger	log	= LogManager.getLogger(TaskXMLFileBuilder.class);
	private TaskList			m_taskList;

	/**
	 * Create the xmlfile builder.
	 */
	public TaskXMLFileBuilder() {
		m_taskList = new TaskList("buildfile");
	}

	/**
	 * Add a task to the builder
	 */
	public void addTask(Task newTask) {
		m_taskList.addTask(newTask);
	}

	/**
	 * Set the tasklist of the builder
	 */
	public void setTaskList(TaskList taskList) {
		m_taskList = taskList;
	}

	/**
	 * Build the actual file. 
	 * @param exportFile where to export
	 */
	public void build(File exportFile) throws TaskIOException {
		if (exportFile == null) {
			throw new TaskIOException(ApplicationSettings.getInstance().getLocalizedMessage("log.err.file"));
		}

		log.info(ApplicationSettings.getInstance().getLocalizedMessage("log.create.file"));

		m_taskList.setName(exportFile.getName());
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();

			Document doc = builder.newDocument();

			Element root = doc.createElement("TASKLIST");
			doc.appendChild(root);

			createXML(doc, root);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(exportFile);

			transformer.transform(source, result);
		} catch (ParserConfigurationException pce) {
			String err = ApplicationSettings.getInstance().getLocalizedMessage("txmlfb.pce") + ": " + pce.getLocalizedMessage();
			log.error(err);
			throw new TaskIOException(err);
		} catch (TransformerConfigurationException e) {
			String err = ApplicationSettings.getInstance().getLocalizedMessage("txmlfb.tce") + ": " + e.getLocalizedMessage();
			log.error(err);
			throw new TaskIOException(err);
		} catch (TransformerException e) {
			String err = ApplicationSettings.getInstance().getLocalizedMessage("txmlfb.ioe") + ": " + e.getLocalizedMessage();
			log.error(err);
			throw new TaskIOException(err);
		}
		log.info(ApplicationSettings.getInstance().getLocalizedMessage("txmlfb.succ"));
	}

	/**
	 * Create the actual XML structure and add it to the document.
	 * @param doc the xml document
	 * @param root the root element of the document
	 */
	private void createXML(Document doc, Element root) {
		ArrayList<Task> tasklist = m_taskList.getAllTasks();
		for (Task t : tasklist) {
			root.appendChild(getTaskXMLFormat(doc, t));
		}
	}

	/**
	 * Get the XML-format of an individual task.
	 * @param doc the xml document
	 * @param t the task to create the structure of
	 * @return xml element with the right structure
	 */
	private Element getTaskXMLFormat(Document doc, Task t) {
		Element task = doc.createElement("Task");
		task.setAttribute("id", t.getId());

		Element name = doc.createElement("Name");
		name.setTextContent(t.getName());
		task.appendChild(name);

		Element priority = doc.createElement("Priority");
		priority.setTextContent("" + t.getPriority());
		task.appendChild(priority);

		task.appendChild(getDurationXML(doc, t));
		task.appendChild(getDeadlineXML(doc, t));

		Element description = doc.createElement("Description");
		description.setTextContent(t.getDescription());
		task.appendChild(description);

		return task;
	}

	/**
	 * Get the duration of a task in the right format.
	 * @param doc the xml document
	 * @param t the task which holds the duration info
	 * @return the right structure for duration of a task
	 */
	private Element getDurationXML(Document doc, Task t) {
		Element duration_elem = doc.createElement("DURATION");

		Element durminutes = doc.createElement("MINUTES");
		Element durhours = doc.createElement("HOURS");

		Duration duration = t.getDuration();

		long days = duration.toDays(), hrs, mins;
		if (days > 0) {
			hrs = days * 24 - (int) duration.toHours();
			if (hrs > 0)
				mins = duration.toMinutes() - days * 60 * 24 - hrs * 60;
			else
				mins = duration.toMinutes() - days * 60 * 24;
		} else {
			hrs = (int) duration.toHours();
			if (hrs > 0)
				mins = duration.toMinutes() - hrs * 60;
			else
				mins = duration.toMinutes();
		}

		durminutes.setTextContent("" + mins);
		durhours.setTextContent("" + hrs);

		duration_elem.appendChild(durhours);
		duration_elem.appendChild(durminutes);

		return duration_elem;
	}

	/**
	 * Get the deadline of a task in the right format.
	 * @param doc the xml document
	 * @param t the task which holds the deadline info
	 * @return the right structure for the deadline of a task
	 */
	private Element getDeadlineXML(Document doc, Task t) {
		Element deadline = doc.createElement("DEADLINE");

		DateTimeFormatter dtf = DateTimeFormatter.ISO_ZONED_DATE_TIME;

		deadline.setTextContent(t.getDeadline().format(dtf));

		return deadline;
	}

}
