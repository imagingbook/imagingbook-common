package imagingbook.lib.filter;

import ij.process.ImageProcessor;
import imagingbook.lib.filter.kernel.GaussianKernel2D;
import imagingbook.lib.image.access.OutOfBoundsStrategy;

/**
 * This class implements a 2D Gaussian filter by extending
 * {@link LinearFilter}.
 * 
 * @author WB
 * @version 2020/12/29
 */
public class GaussianFilter extends LinearFilter {
	
	public static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.NEAREST_BORDER;

	public GaussianFilter(ImageProcessor ip, double sigma) {
		super(ip, new GaussianKernel2D(sigma), DefaultOutOfBoundsStrategy);
	}

}
