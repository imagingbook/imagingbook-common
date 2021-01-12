package imagingbook.lib.util.progress.ij;

import ij.IJ;
import imagingbook.lib.util.progress.ProgressMonitor;
import imagingbook.lib.util.progress.ProgressReporter;

/**
 * An instance of this class monitors the progress of another object (task)
 * by querying its status periodically.
 * TODO: Make the associated action more generic (currently progress information is 
 * used to update ImageJ's peogress bar.
 * @author WB
 *
 */
public class ProgressBarMonitor extends ProgressMonitor {
	
	private final boolean ijRunning;
	private boolean ijUpdateProgressBar = true;
	
	public ProgressBarMonitor(ProgressReporter target) {
		super(target);
		this.ijRunning = (IJ.getInstance() != null);
	}

	@Override
	public void handleProgress(double progress, long nanoTime) {
		if (ijRunning && ijUpdateProgressBar) {
			IJ.showProgress(progress);
		}
		
	}

}
