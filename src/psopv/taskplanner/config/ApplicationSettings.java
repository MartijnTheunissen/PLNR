package psopv.taskplanner.config;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.prefs.Preferences;

/**
 * 
 * The main settings of the application will be stored in here.
 *
 * @since Apr 10, 2014 9:30:04 PM
 * @author Martijn Theunissen
 * @author Tom Knaepen
 */
public class ApplicationSettings extends Observable {

	private static ApplicationSettings		instance						= null;

	private final String					LANGUAGE_RESOURCE_BUNDLE_NAME	= "LanguageSettings";
	private Preferences						m_prefs;

	private TimeZone						m_timeZone;
	private boolean							m_planOnWeekend;
	private Duration						m_minimumBreakBetweenTasks;
	private Locale							m_locale;
	private LocalTime						m_startPlanningTime;
	private LocalTime						m_endPlanningTime;
	private LinkedHashMap<String, Locale>	m_availableLocales;
	private ArrayList<TimeZone>				m_availableTimeZones;
	private UnplannablePeriodList			m_unplannablePeriodList;

	/**
	 * Private constructor (singleton pattern).
	 * Gets the stored preferences via the Preferences Java API.
	 */
	private ApplicationSettings() {
		m_availableLocales = new LinkedHashMap<String, Locale>();
		m_availableTimeZones = new ArrayList<TimeZone>();

		//supported languages
		m_availableLocales.put("Nederlands", new Locale("nl"));
		m_availableLocales.put("English", new Locale("en"));

		//supported timezones
		for (int i = -12; i <= 12; i++) {
			String timeZoneId = "GMT";
			if (i >= 0)
				timeZoneId += "+";
			timeZoneId += i;
			m_availableTimeZones.add(TimeZone.getTimeZone(timeZoneId));
		}
		m_prefs = Preferences.userNodeForPackage(this.getClass());
		// Get the stored setting or defaults if no setting is stored
		m_timeZone = TimeZone.getTimeZone(m_prefs.get("TimeZone", TimeZone.getDefault().getID()));

		m_planOnWeekend = m_prefs.getBoolean("PlanOnWeekend", false);
		m_minimumBreakBetweenTasks = Duration.parse(m_prefs.get("MinimumBreakBetweenTasks", Duration.ofMinutes(0).toString()));
		m_locale = Locale.forLanguageTag(m_prefs.get("Locale", Locale.getDefault().toLanguageTag()));
		m_startPlanningTime = LocalTime.parse(m_prefs.get("StartPlanningTime", LocalTime.of(9, 0, 0, 0).toString())); // 9AM
		m_endPlanningTime = LocalTime.parse(m_prefs.get("EndPlanningTime", LocalTime.of(17, 0, 0, 0).toString())); // 5 PM
		m_unplannablePeriodList = UnplannablePeriodList.parse(m_prefs.get("UnplannablePeriods", ""));

	} // stop instantiation

	/**
	 * Global access point to the instance of this class
	 * @return the ApplicationSettings instance
	 */
	public static ApplicationSettings getInstance() {
		if (instance == null)
			instance = new ApplicationSettings();
		return instance;
	}

	/**
	 * Get the start planning time
	 * @return the start planning local time
	 */
	public LocalTime getStartPlanningTime() {
		return m_startPlanningTime;
	}

	/**
	 * Get the end planning time
	 * @return the end planning local time
	 */
	public LocalTime getEndPlanningTime() {
		return m_endPlanningTime;
	}

	/**
	 * Set the start planning time
	 * @param start the start planning local time
	 */
	public void setStartPlanningTime(LocalTime start) {
		m_startPlanningTime = start;
		m_prefs.put("StartPlanningTime", m_startPlanningTime.toString());
		setChanged();
		notifyObservers();
	}

	/**
	 * Set the end planning local time
	 * @param end the end planning local time
	 */
	public void setEndPlanningTime(LocalTime end) {
		m_endPlanningTime = end;
		m_prefs.put("EndPlanningTime", m_endPlanningTime.toString());
		setChanged();
		notifyObservers();
	}

	/**
	 * Get the timezone setting
	 * @return the currently used timezone
	 */
	public TimeZone getTimeZone() {
		return m_timeZone;
	}

	/**
	 * Set the timezone
	 * @param timezone the timezone to set
	 */
	public void setTimeZone(TimeZone timezone) {
		m_timeZone = timezone;
		m_prefs.put("TimeZone", m_timeZone.getID());
		setChanged();
		notifyObservers();
	}

	/**
	 * @return true if one can plan tasks during the weekend, false otherwise
	 */
	public boolean isPlannableOnWeekends() {
		return m_planOnWeekend;
	}

	/**
	 * Set a boolean whether tasks can be planned during the weekend
	 * @param plannable if it is plannable or not
	 */
	public void setPlannableOnWeekends(boolean plannable) {
		m_planOnWeekend = plannable;
		m_prefs.putBoolean("PlanOnWeekend", m_planOnWeekend);
		setChanged();
		notifyObservers();
	}

	/**
	 * Set the minimum break between tasks
	 * @param duration the minimum break length between tasks planned
	 */
	public void setMinimumBreakBetweenTasks(Duration duration) {
		m_minimumBreakBetweenTasks = duration;
		m_prefs.put("MinimumBreakBetweenTasks", m_minimumBreakBetweenTasks.toString());
		setChanged();
		notifyObservers();
	}

	/**
	 * @return the minimum break duration between tasks planned
	 */
	public Duration getMinimumBreakBetweenTasks() {
		return m_minimumBreakBetweenTasks;
	}

	/**
	 * Set the locale the application will use
	 * @param l the locale to set
	 */
	public void setLocale(Locale l) {
		m_locale = l;
		m_prefs.put("Locale", m_locale.toLanguageTag());
		setChanged();
		notifyObservers();
	}

	/**
	 * Set the locale the application will use
	 * @param s the string representation of the locale to set
	 */
	public void setLocale(String s) {
		m_locale = m_availableLocales.get(s);
		m_prefs.put("Locale", m_locale.toLanguageTag());
		setChanged();
		notifyObservers();
	}

	/**
	 * Set the locale the application will use.
	 * If incorrect parameters are passed the default locale will be used.
	 * @param language the language of the locale (example: "en" | "nl")
	 * @param country the country of the locale (example: "US", "BE")
	 */
	public void setLocale(String language, String country) {
		m_locale = new Locale(language, country);
		m_prefs.put("Locale", m_locale.toLanguageTag());
		setChanged();
		notifyObservers();
	}

	/**
	 * @return the locale currently used
	 */
	public Locale getLocale() {
		return m_locale;
	}

	public LinkedHashMap<String, Locale> getAvailableLocales() {
		return m_availableLocales;
	}

	public ArrayList<TimeZone> getAvailableTimeZones() {
		return m_availableTimeZones;
	}

	/**
	 * @return periods of time where the taskplanner may not plan
	 */
	public UnplannablePeriodList getUnplannablePeriods() {
		return m_unplannablePeriodList;
	}

	/**
	 * Set the list of unplannable periods
	 * @param list the list to be set
	 */
	public void setUnplannablePeriodList(UnplannablePeriodList list) {
		m_unplannablePeriodList = list;
		m_prefs.put("UnplannablePeriods", list.toString());
		setChanged();
		notifyObservers();
	}

	/**
	 * Get the localized translated message
	 * @param messageCode the messagecode the message represents
	 * @return the localized translated message
	 */
	public String getLocalizedMessage(String messageCode) {
		ResourceBundle bundle = ResourceBundle.getBundle(LANGUAGE_RESOURCE_BUNDLE_NAME, m_locale);

		return bundle.getString(messageCode);
	}

	public String getLocalizedMessage(String messageCode, String locale) {
		Locale l = m_availableLocales.get(locale);
		ResourceBundle bundle = ResourceBundle.getBundle(LANGUAGE_RESOURCE_BUNDLE_NAME, l);
		return bundle.getString(messageCode);
	}
}