package imagingbook.pub.edgepreservingfilters;

import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.util.SimpleParameters;

public interface KuwaharaF {

	public static class Parameters implements SimpleParameters {
		/** Radius of the filter (should be even) */
		public int radius = 2;
		/** Threshold on sigma to avoid banding in flat regions */
		public double tsigma = 5.0;
		/** Out-of-bounds strategy */
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NEAREST_BORDER;
	}

}
