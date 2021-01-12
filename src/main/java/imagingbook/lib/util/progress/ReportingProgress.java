package imagingbook.lib.util.progress;

/**
 * Monitored objects must implement this interface.
 * @author WB
 * 
 */
public interface ReportingProgress {
	
	/**
	 * Returns a value in [0,1) indicating to which degree the
	 * task associated with this object is complete.
	 * @return a value between 0 and 1
	 */
	public double getProgress();

}
