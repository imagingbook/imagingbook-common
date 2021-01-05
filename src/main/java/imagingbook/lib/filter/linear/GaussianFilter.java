package imagingbook.lib.filter.linear;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.PixelPack;

/**
 * This class implements a 2D Gaussian filter by extending
 * {@link LinearFilter}.
 * 
 * @author WB
 * @version 2020/12/29
 */
public class GaussianFilter extends LinearFilter {
	
	public static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NEAREST_BORDER;

	public GaussianFilter(ImageProcessor ip, double sigma) {
		super(PixelPack.fromImageProcessor(ip, OBS), new GaussianKernel2D(sigma));
	}

}
