package imagingbook.lib.util.progress;

import ij.IJ;

/**
 * An instance of this class monitors the progress of another object (task)
 * by querying its status periodically.
 * TODO: Make the associated action more generic (currently progress information is 
 * used to update ImageJ's peogress bar.
 * @author WB
 *
 */
public class ProgressMonitor extends Thread {
	
	private static int DefaultWaitTime = 250; // ms
	
	private final int period;
	private final ReportingProgress target;
	private final boolean ijRunning;
	private boolean ijUpdateProgressBar = true;
	
	private boolean running;
	private long startTime, endTime;
	
	
	public ProgressMonitor(ReportingProgress target) {
		this(target, DefaultWaitTime);
	}
	
	public ProgressMonitor(ReportingProgress target, int period) {
		if (target == null)
			throw new IllegalArgumentException("target instance is null");
		this.target = target;
		this.period = period;
		this.ijRunning = (IJ.getInstance() != null);
	}
	
	// --------------------------------------------------------

	@Override
	public void run() {
		startTime = System.nanoTime();
		running = true;
		if (ijRunning && ijUpdateProgressBar) {
			IJ.showProgress(0);
		}
		do {
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {break;}
			double p = target.getProgress();
			//IJ.log(String.format("progress = %.3f", p));
			if (ijRunning && ijUpdateProgressBar) {
				IJ.showProgress(p);
			}
		} while(running);
		endTime = System.nanoTime();
	}
	
	public void terminate() {
        running = false;
        this.interrupt();
    }
	
	// --------------------------------------------------------
	
	public void setIjUpdateProgressBar(boolean onOff) {
		this.ijUpdateProgressBar = onOff;
	}
	
	public double getElapsedTime() { // result is in seconds
		if (running) {
			return  (System.nanoTime() - startTime) / 1E9d;
		}
		else {
			return (endTime - startTime) / 1E9d;
		}
	}

}
