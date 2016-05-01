/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap19_DFT_2D;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.dft.Dft2d;

/** 
 * Computes the 2-dimensional (power-spectrum) DFT on a float image
 * of arbitrary size.
 * TODO: adapt API to the DCT layout.
 */
public class DFT_2D_Demo implements PlugInFilter{

	static boolean center = true;    //center the resulting spectrum?
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		FloatProcessor fp = ip.convertToFloatProcessor();
		Dft2d dft = new Dft2d(fp, center);

		ImageProcessor ipP = dft.makePowerImage();
		ImagePlus win = new ImagePlus("DFT Power Spectrum (byte)", ipP);
		win.show();
	}

}
