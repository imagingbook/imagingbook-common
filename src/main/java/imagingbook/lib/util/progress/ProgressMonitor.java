package imagingbook.lib.util.progress;

import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Allows monitoring of progress of activities distributed in
 * a class hierarchy.
 * 
 * TODO: add annotation to classes which should be monitored?
 * 
 * @author WB
 * @version 2021/01/09
 */
public class ProgressMonitor {
	
	/**
	 * Classes to be monitored must implement this interface.
	 */
	public interface HasProgressMonitor {
		public ProgressMonitor getProcessMonitor();
		
		// delegating methods ---------------------------
		
		public default void stepCount(Class<?> clazz) {
			getProcessMonitor().stepCount(clazz);
		}
		
	}
	
	// ---------------------------------------------------------------------
	
	private final LinkedHashMap<Class<?>, Integer> classHierarchy;
	private final long[][] progressCounters;
	
	public ProgressMonitor(HasProgressMonitor instance) {
		this.classHierarchy = getClassHierarchy(instance);
		this.progressCounters = new long[2][classHierarchy.size()];	// [0] = max counts, [1] = current counts
	}
	
	// collect all classes in the class hierarchy that implement {@link HasProgressMonitor}
	private static LinkedHashMap<Class<?>, Integer> getClassHierarchy(Object instance) {
		int nc = 0;
		LinkedHashMap<Class<?>, Integer> map = new LinkedHashMap<>();
		Class<?> clazz = instance.getClass();
		while (clazz != null) {
			if (HasProgressMonitor.class.isAssignableFrom(clazz)) { //if (!clazz.equals(Object.class)) {
				map.put(clazz, nc);
			}
			clazz = clazz.getSuperclass();
			nc++;
		}
		return map;
	}
	
	public void listClassHierarchy() {
		for(Class<?> clazz : classHierarchy.keySet()) {
			int nc = classHierarchy.get(clazz);
			System.out.println(nc + " " + clazz);
		}
	}
	
	public int getClassIndex(Class<?> clazz) throws IllegalArgumentException {
		Integer k = classHierarchy.get(clazz);
		if (k == null) 
			throw new IllegalArgumentException("class not monitored: " + clazz.getSimpleName());
		else
			return k;
	}
	
	public void setMaxCount(Class<?> clazz, long val) {
		int k = getClassIndex(clazz);
		progressCounters[0][k] = val;
	}
	
	public void resetCount(Class<?> clazz) {
		resetCount(getClassIndex(clazz));
	}
	
	public void resetCount(int k) {
		progressCounters[1][k] = 0;
	}
	
	public void stepCount(Class<?> clazz) {
		int k = getClassIndex(clazz);
		progressCounters[1][k] += 1;
		// reset all lower counters
		for (int i = k - 1; i >= 0; i--) {
			resetCount(i);
		}
	}
	
	public void reset() {
		for (int k = 0; k < progressCounters[1].length; k++) {
			progressCounters[1][k] = 0;
		}
	}
	
	public long getMaxCount(Class<?> clazz) {
		return progressCounters[0][getClassIndex(clazz)];
	}
	
	public long getCount(Class<?> clazz) {
		return progressCounters[1][getClassIndex(clazz)];
	}
	
	@Override
	public String toString() {
		return String.format("%s cur=%s max=%s", this.getClass().getSimpleName(), 
				Arrays.toString(progressCounters[1]), Arrays.toString(progressCounters[0]));
	}
	
	// -----------------------------------------------------------------------------
	
	public double getProgress(Class<?> clazz) {
		int k = classHierarchy.get(clazz);
		return getProgress(k);
	}
	
	public double getProgress(int k) {
		long maxCount = progressCounters[0][k];
		if (maxCount <= 0) {
			throw new IllegalStateException("invalid max. progress count at index " + k);
		}
		return (double) progressCounters[1][k] / maxCount;
	}
	
	public double getProgress() {
		double progress = 0;
		for (int k = 0; k < progressCounters[0].length; k++) {
			long maxCount = progressCounters[0][k];
			long curCount = progressCounters[1][k];
			if (maxCount > 0) {	// skip if no maxCount is defined
				progress = (curCount + progress) / maxCount;
			}
		}
		return progress;
	}

}
