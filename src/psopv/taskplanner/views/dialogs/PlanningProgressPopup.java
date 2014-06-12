package psopv.taskplanner.views.dialogs;

import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;

import psopv.taskplanner.config.ApplicationSettings;
import psopv.taskplanner.models.TaskPlannerModel;

import com.alee.extended.window.WebProgressDialog;
import com.alee.utils.ThreadUtils;

/**
 * 
 * Dialog for showing the planning progress
 *
 * @since May 29, 2014 10:27:43 PM
 * @author Martijn Theunissen
 */
public class PlanningProgressPopup {

	private TaskPlannerModel	m_model;

	public PlanningProgressPopup(TaskPlannerModel model) {
		m_model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// Load dialog
		WebProgressDialog progress = new WebProgressDialog(ApplicationSettings.getInstance().getLocalizedMessage("plan.progtitle"));
		progress.setModalityType(ModalityType.MODELESS);
		progress.setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
		progress.setText(ApplicationSettings.getInstance().getLocalizedMessage("plan.start"));

		// Starting updater thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				int failed = 0;
				String done = ApplicationSettings.getInstance().getLocalizedMessage("plan.done");
				String failedtoplan = ApplicationSettings.getInstance().getLocalizedMessage("plan.ftp");

				while (!m_model.getPlanningStrategy().isDone()) {
					ThreadUtils.sleepSafely(1);
					double prog = m_model.getPlanningStrategy().getProgress();
					int progr = (int) (prog * 100);
					failed = m_model.getPlanningStrategy().getFailedTasks();
					progr %= 101;
					progress.setProgress(progr);

					if (progr > 25) {
						progress.setText("1/4 " + done + " - " + failed + " " + failedtoplan);
					}
					if (progr > 50) {
						progress.setText("1/2 " + done + " - " + failed + " " + failedtoplan);
					}
					if (progr > 75) {
						progress.setText("3/4 " + done + " - " + failed + " " + failedtoplan);
					}

				}
				progress.setText(done + "! - " + failed + " " + failedtoplan);
				ThreadUtils.sleepSafely(2000);
				progress.setVisible(false);
			}
		}).start();

		// view dialog
		progress.setVisible(true);
		progress.requestFocus();
	}

}
