/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.filters;

import ij.IJ;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.image.ImageAccessor;
import imagingbook.lib.image.OutOfBoundsStrategy;

/**
 * This abstract class represents a generic filter that, when applied to 
 * an {@code ImageProcessor} object performs all pixel-level
 * iterations automatically.
 * Concrete implementations of this class need to define only two methods:<br>
 * {@code float filterPixel(ImageAccessor.Scalar, int, int)} and<br>
 * {@code float[] filterPixel(ImageAccessor.Rgb, int, int)}.<br>
 * See {@link LinearFilter} for a sample implementation.
 * 
 * <br>
 * Note that this is experimental code!
 * 
 * @author wilbur
 * @version 2016/11/01
 * 
 */
public abstract class GenericFilter {
	
	/* TODO: PASS THE IMAGE PROCESSOR of the original image and
	 * set up width/height, accessors etc., then use apply without processor argument??
	 * Allow source/target to be of different types?
	 * Implement using interfaces (for gray/color)?
	 * 
	 */
	
	private OutOfBoundsStrategy obs = OutOfBoundsStrategy.NearestBorder;
	
	/**
	 * Set the out-of-bounds strategy of this {@link GenericFilter}. See {@link OutOfBoundsStrategy}.
	 * @param obs the out-of-bounds strategy
	 */
	public void setOutOfBoundsStrategy(OutOfBoundsStrategy obs) {
		this.obs = obs;
	}
	
	protected GenericFilter() {
	}
 	
	/**
	 * Calculates and returns the filter result for a single pixel
	 * at the given position.
	 * 
	 * @param source the {@link ImageAccessor.Scalar} representing the source (scalar-valued) image
	 * @param u the horizontal pixel position
	 * @param v the vertical pixel position
	 * @return the resulting (scalar) pixel value for the specified image position
	 */
 	public abstract float filterPixel(ImageAccessor.Scalar source, int u, int v);
 	
 	/**
	 * Calculates and returns the filter result for a single pixel
	 * at the given position.
	 * 
	 * @param source the {@link ImageAccessor.Rgb} representing the source (RGB) image
	 * @param u the horizontal pixel position
	 * @param v the vertical pixel position
	 * @return the resulting (RGB) pixel value for the specified image position
	 */
 	public abstract float[] filterPixel(ImageAccessor.Rgb source, int u, int v);
 	
 	/**
 	 * Dispatch work depending on actual (runtime) type of processor.
 	 * This is ugly but we want to avoid generic types (which would
 	 * not be of much help in this case anyway).
 	 * 
 	 * @param ip the image this filter is applied to (destructively)
 	 */
 	public void applyTo(ImageProcessor ip) {	// TODO: check for target == null?
		final int w = ip.getWidth();
		final int h = ip.getHeight();
 		ImageProcessor ipCopy = ip.duplicate();
 
 		if (ip instanceof ColorProcessor) {
 	 		ImageAccessor.Rgb iaOrig = new ImageAccessor.Rgb((ColorProcessor)ip, obs, null);
 	 		ImageAccessor.Rgb iaCopy = new ImageAccessor.Rgb((ColorProcessor)ipCopy, obs, null);
			for (int v = 0; v < h; v++) {
				for (int u = 0; u < w; u++) {
 	            	//int p = (int) filterPixel(iaCopy, u, v);
 	            	float[] rgb = filterPixel(iaCopy, u, v);
 	            	iaOrig.setPix(u, v, rgb);
 	            }
 	            IJ.showProgress(v, h);
 	        }
 		}
 		else {
 			ImageAccessor.Scalar iaOrig = ImageAccessor.Scalar.create(ip, obs, null);
 	 		ImageAccessor.Scalar iaCopy = ImageAccessor.Scalar.create(ipCopy, obs, null);
			for (int v = 0; v < h; v++) {
				for (int u = 0; u < w; u++) {
					float p = filterPixel(iaCopy, u, v);
					iaOrig.setVal(u, v, p);
				}
				IJ.showProgress(v, h);
			}
 		}
 	}

}
