package imagingbook.lib.filter.linear;

import imagingbook.lib.image.access.OutOfBoundsStrategy;

/**
 * This class implements a 2D Gaussian filter by extending
 * {@link LinearFilter}.
 * 
 * @author WB
 * @version 2020/12/29
 */
public class GaussianFilter extends LinearFilter {
	
	public static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NEAREST_BORDER;
	
	public GaussianFilter(double sigma) {
		super(new GaussianKernel2D(sigma));
	}

}
