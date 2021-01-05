/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.filter.linear;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.PixelPack;

/**
 * This class implements a separable 2D Gaussian filter by extending
 * {@link LinearFilterSeparable}.
 * 
 * @author WB
 * @version 2020/12/29
 */
public class GaussianFilterSeparable extends LinearFilterSeparable {
	
	public static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NEAREST_BORDER;

	public GaussianFilterSeparable(ImageProcessor ip, double sigma) {
		super(PixelPack.fromImageProcessor(ip, OBS), new GaussianKernel1D(sigma));
	}
	
	public GaussianFilterSeparable(ImageProcessor ip, double sigmaX, double sigmaY) {
		super(PixelPack.fromImageProcessor(ip, OBS), new GaussianKernel1D(sigmaX), new GaussianKernel1D(sigmaY));
	}
	
}
