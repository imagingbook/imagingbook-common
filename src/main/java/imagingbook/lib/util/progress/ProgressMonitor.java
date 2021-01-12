package imagingbook.lib.util.progress;

public abstract class ProgressMonitor extends Thread implements AutoCloseable {
	
	private static int DefaultWaitTime = 250; // ms
	
	private int waitTime = DefaultWaitTime;
	private final ProgressReporter target;

	private boolean running;
	private long startTime, endTime;
	
	
	public ProgressMonitor(ProgressReporter target) {
		this(target, true);
	}
			
	public ProgressMonitor(ProgressReporter target, boolean autoStart) {
		if (target == null)
			throw new IllegalArgumentException("null target instance!");
		this.target = target;
		if (autoStart) {
			start();
		}
	}
	
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}
	
	// action to be implemented by real classes
	public abstract void handleProgress(double progress, long elapsedNanoTime);
	
	// --------------------------------------------------------

	@Override
	public void run() {
		handleProgress(0, 0);
		startTime = System.nanoTime();
		running = true;
		do {
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {break;}
			handleProgress(target.getProgress(), System.nanoTime() - startTime);
		} while(running);
		endTime = System.nanoTime();
	}
	
	public void terminate() {
        running = false;
        this.interrupt();
    }
	
	@Override
	public void close() {
		this.terminate();
	}
	
	// --------------------------------------------------------
	
	public double getElapsedTime() { // result is in seconds
		if (running) {
			return  (System.nanoTime() - startTime) / 1E9d;
		}
		else {
			return (endTime - startTime) / 1E9d;
		}
	}

}
