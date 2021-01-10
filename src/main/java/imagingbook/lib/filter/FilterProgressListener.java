package imagingbook.lib.filter;

import java.util.EventListener;

import ij.IJ;

@Deprecated
public interface FilterProgressListener extends EventListener {
	
	// override to get different behavior
	public default void updateProgress(double progress) {
		if (IJ.getInstance() != null)
			IJ.showProgress(progress);
	}

}
