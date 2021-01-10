package imagingbook.lib.filter;

import ij.IJ;

public class ProgressMonitor extends Thread {
	
	private static int DefaultWaitTime = 250; // ms
	
	private final int period;
	private final ReportsProgress target;
	
	private boolean running;
	
	public ProgressMonitor(ReportsProgress target) {
		this(target, DefaultWaitTime);
	}
	
	public ProgressMonitor(ReportsProgress target, int period) {
		if (target == null)
			throw new IllegalArgumentException("target instance is null");
		this.target = target;
		this.period = period;
	}

	@Override
	public void run() {
		running = true;
		while(running) {
			double p = target.getProgress();
			IJ.log(String.format("progress = %.3f", p));
			IJ.showProgress(target.getProgress());	// TODO: allow a lambda expression instead?
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {break;}
		}
	}
	
	public void terminate() {
        running = false;
        this.interrupt();
    }

}
