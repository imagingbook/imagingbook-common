package imagingbook.lib.filter;

@Deprecated
public class ProgressMonitorTest {

	public static void main(String[] args) throws InterruptedException {

		ProgressMonitor mon = new ProgressMonitor(null, 1000);
		mon.start();
		Thread.sleep(5500);
		System.out.println("finish call: " + System.currentTimeMillis());
		mon.terminate();
	}

}
