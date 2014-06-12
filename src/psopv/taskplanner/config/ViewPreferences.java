package psopv.taskplanner.config;

import java.util.Observable;

/**
 * Class that keeps track of visual preferences of the gui.
 *
 * @since May 11, 2014 6:16:35 PM
 * @author Martijn Theunissen
 */
public class ViewPreferences extends Observable {

	private static ViewPreferences	instance;

	public enum CenterView {
		WEEK, LIST, TASK
	};

	private CenterView	m_centerView;

	/**
	 * Set the center view of the application
	 * @param view the centerview
	 */
	public void setCenterView(CenterView view) {
		m_centerView = view;
		this.setChanged();
		this.notifyObservers("VS");
	}

	/**
	 * @return the current centerview setting
	 */
	public CenterView getCenterView() {
		return m_centerView;
	}

	private ViewPreferences() { // initialize the preferences
		m_centerView = CenterView.WEEK;
	}

	/**
	 * Global access point to the instance of this class
	 * @return the ViewPreferences instance
	 */
	public static ViewPreferences getInstance() {
		if (instance == null)
			instance = new ViewPreferences();
		return instance;
	}

}
