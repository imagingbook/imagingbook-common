/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/

package imagingbook.lib.filters;

import ij.IJ;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.image.ImageAccessor;


public abstract class GenericFilter {
	
	// PASS THE IMAGE PROCESSOR of the original image and
	// set up width/height, accessors etc.
	// then use apply without processor argument??
	// Allow source/target to be of different types?
	// Implement using interfaces (for gray/color)?
	
	protected GenericFilter() {
	}
 	
 	public abstract float   filterPixel(ImageAccessor.Scalar source, int u, int v);
 	public abstract float[] filterPixel(ImageAccessor.Rgb source, int u, int v);
 	
 	/* Dispatch work depending on actual (runtime) type of processor.
 	 * This is ugly but we want to avoid generic types (which would
 	 * not be of much help in this case anyway).
 	 */
 	public void applyTo(ImageProcessor ip) {	// check for target == null?
		final int w = ip.getWidth();
		final int h = ip.getHeight();
 		ImageProcessor ipCopy = ip.duplicate();
 
 		if (ip instanceof ColorProcessor) {
 	 		ImageAccessor.Rgb iaOrig = ImageAccessor.Rgb.create(ip, null, null);
 	 		ImageAccessor.Rgb iaCopy = ImageAccessor.Rgb.create(ipCopy, null, null);
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
 			ImageAccessor.Scalar iaOrig = ImageAccessor.Scalar.create(ipCopy, null, null);
 	 		ImageAccessor.Scalar iaCopy = ImageAccessor.Scalar.create(ipCopy, null, null);
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
