/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap20_DCT;


import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.dct.Dct2d;

/** 
 * Calculates and displays the 2-dimensional DCT after converting the input image to a float image.
 * of arbitrary size. Be patient, this is not optimized and thus slow!
 * @author W. Burger
 * @version 2014-04-13
 */
public class DCT_2D_Demo implements PlugInFilter {
	
	public int setup(String arg, ImagePlus im) {
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		
		FloatProcessor fp = ip.convertToFloatProcessor();
		
		// create a new DCT:
		Dct2d dct = new Dct2d();
		
		// calculate the forward DCT:
		FloatProcessor spectrum = dct.DCT(fp);

		// calculate the inverse DCT:
		FloatProcessor reconstruction = dct.iDCT(spectrum);
		
		// modify the spectrum for viewing and show it:
		spectrum.abs();
		spectrum.add(1.0);
		spectrum.log();
		(new ImagePlus("DCT Spectrum", spectrum)).show();
		
		// show the reconstructed image
		(new ImagePlus("DCT Reconstruction", reconstruction)).show();
	}
	
}
