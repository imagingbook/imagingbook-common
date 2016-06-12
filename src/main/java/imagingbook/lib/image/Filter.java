/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.lib.image;

import ij.plugin.filter.Convolver;
import ij.process.ImageProcessor;

/**
 * Utility methods for filtering images. 
 * None of the filter methods modifies the kernel, i.e., kernels are 
 * used as supplied and never normalized.
 * @author WB
 *
 */
public abstract class Filter {

	public static void convolveX (ImageProcessor fp, float[] h) {
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(fp, h, h.length, 1);
	}

	public static void convolveY (ImageProcessor fp, float[] h) {
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(fp, h, 1, h.length);
	}

	public static void convolveXY (ImageProcessor fp, float[] h) {
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(fp, h, h.length, 1);
		conv.convolve(fp, h, 1, h.length);
//		convolveX(fp, h);
//		convolveY(fp, h);
	}

}
