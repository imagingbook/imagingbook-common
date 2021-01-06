/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.filtersOBSOLETE;

import ij.IJ;
import ij.process.ImageProcessor;
import imagingbook.lib.image.access.ImageAccessor;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.ScalarAccessor;
import imagingbook.lib.image.access.VectorAccessor;

/**
 * This abstract class represents a generic filter that, when applied to 
 * an {@code ImageProcessor} object performs all pixel-level
 * iterations automatically.
 * Concrete sub-classes of this class only need to override a single method:
 * {@link #filterScalar(ScalarAccessor, ScalarAccessor, int, int)},
 * which defines how a scalar image component is filtered.
 * <br>
 * If the image has multiple components (e.g., an RGB image)
 * the same scalar filter is applied to all components by default.
 * To change this behavior, implementing classes should also override
 * the method {@link #filterVector(ImageAccessor, ImageAccessor, int, int)}.
 * <br>
 * 
 * 
 * @author wilbur
 * @version 2020/12/28
 * @deprecated
 * 
 */
public abstract class GenericFilter2D {
	
	/* TODO: PASS THE IMAGE PROCESSOR of the original image and
	 * set up width/height, accessors etc., then use apply without processor argument??
	 * Allow source/target to be of different types?
	 */
	
	@Deprecated
	public static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.NEAREST_BORDER;
	
	private OutOfBoundsStrategy obs = DefaultOutOfBoundsStrategy;
	private boolean showProgress = false;
	
	protected GenericFilter2D() {
	}
	
	/**
	 * Sets the out-of-bounds strategy of this {@link GenericFilter2D}. 
	 * See {@link OutOfBoundsStrategy}.
	 * 
	 * @param obs the out-of-bounds strategy
	 */
	public void setOutOfBoundsStrategy(OutOfBoundsStrategy obs) {
		this.obs = obs;
	}
	
	/**
	 * Turn on/off if filter progress should be displayed in 
	 * ImageJ's progress bar. Turned off by default.
	 * 
	 * @param showProgress set true to turn on
	 */
 	public void setShowProgress(boolean showProgress) {
 		this.showProgress = showProgress;
 	}
 	
	/**
	 * Calculates and returns the filter result for a single pixel
	 * at the given position in a scalar-valued image.
	 * Concrete filters must implement at least this method.
	 * 
	 * @param source the {@link ScalarAccessor} representing the source (scalar-valued) image
	 * @param target TODO
	 * @param u the horizontal pixel position
	 * @param v the vertical pixel position
	 */
 	protected abstract void filterScalar(ScalarAccessor source, ScalarAccessor target, int u, int v);
 	
 	/**
	 * Calculates and returns the filter result for a single pixel
	 * at the given position in a vector-valued image. 
	 * This also works if the image is scalar-valued, i.e., has only a single
	 * component.
	 * The method implements the default behavior, where the same filter
	 * is applied to all component channels.
	 * Concrete filter classes should override this method if a different
	 * behavior is required.
	 * 
	 * @param source the {@link VectorAccessor} representing the source (RGB) image
 	 * @param target TODO
 	 * @param u the horizontal pixel position
 	 * @param v the vertical pixel position
	 */
	protected void filterVector(ImageAccessor source, ImageAccessor target, int u, int v) {
		//float[] result = new float[source.getDepth()];
		// DEFAULT: apply the same filter independently to all scalar-valued components:
		for (int k = 0; k < source.getDepth(); k++) {
			//result[k] = filterScalar(source.getComponentAccessor(k), null, u, v);
			filterScalar(source.getComponentAccessor(k), target.getComponentAccessor(k), u, v);
		}
		//return result;
 	}
 	
 	/**
 	 * Applies this filter to the given image, which is modified
 	 * by this operation.
 	 * 
 	 * @param ip the image this filter is applied to (destructively)
 	 */
	public void applyTo(ImageProcessor ip) {
		final int w = ip.getWidth();
		final int h = ip.getHeight();
 		final ImageProcessor ipCopy = ip.duplicate();
 
		ImageAccessor iaOrig = ImageAccessor.create(ip, obs, null);
		ImageAccessor iaCopy = ImageAccessor.create(ipCopy, obs, null);
		
		if (iaOrig instanceof ScalarAccessor) {
			for (int v = 0; v < h; v++) {
				for (int u = 0; u < w; u++) {
					//float p = 
					filterScalar((ScalarAccessor)iaCopy, (ScalarAccessor)iaOrig, u, v);
					//((ScalarAccessor)iaOrig).setVal(u, v, p);
				}
				if (showProgress) IJ.showProgress(v, h);
			}
		}
		
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				//float[] p = 
				filterVector(iaCopy, iaOrig, u, v);
				//iaOrig.setPix(u, v, p);
			}
			if (showProgress) IJ.showProgress(v, h);
		}
 	}


// 	public void applyTo(ImageProcessor ip) {
//		final int w = ip.getWidth();
//		final int h = ip.getHeight();
// 		final ImageProcessor ipCopy = ip.duplicate();
// 
// 		if (ip instanceof ColorProcessor) {
// 	 		RgbAccessor iaOrig = new RgbAccessor((ColorProcessor)ip, obs, null);
// 	 		RgbAccessor iaCopy = new RgbAccessor((ColorProcessor)ipCopy, obs, null);
//			for (int v = 0; v < h; v++) {
//				for (int u = 0; u < w; u++) {
// 	            	float[] rgb = filterPixel(iaCopy, u, v);
// 	            	iaOrig.setPix(u, v, rgb);
// 	            }
//				if (showProgress) IJ.showProgress(v, h);
// 	        }
// 		}
// 		else {
// 			ScalarAccessor iaOrig = ScalarAccessor.create(ip, obs, null);
// 	 		ScalarAccessor iaCopy = ScalarAccessor.create(ipCopy, obs, null);
//			for (int v = 0; v < h; v++) {
//				for (int u = 0; u < w; u++) {
//					float p = filterPixel(iaCopy, u, v);
//					iaOrig.setVal(u, v, p);
//				}
//				if (showProgress) IJ.showProgress(v, h);
//			}
// 		}
// 	}

}
