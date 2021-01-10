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
	
	public ProgressMonitor(ReportsProgress instanceToBeMonitored, int period) {
		this.target = instanceToBeMonitored;
		this.period = period;
	}

	@Override
	public void run() {
		running = true;
		while(running) {
			IJ.showProgress(target.getProgress());
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
