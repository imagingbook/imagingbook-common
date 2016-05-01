/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap11_Automatische_Schwellwerte;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * Demo plugin showing MinMax thresholding.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Threshold_Global_MinMax implements PlugInFilter {

	private int theMin, theMax;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}
	
	public void run(ImageProcessor ip) {
		ByteProcessor bp = (ByteProcessor) ip;
		getMinMax(bp);
		double minVal = theMin; 
		double maxVal = theMax;
		getMinMax(bp);
		
		int q = (int) Math.rint((minVal + maxVal)/2);
	
		if (q < maxVal) {
			IJ.log("threshold = " + q);
			ip.threshold(q);
		}
		else {
			IJ.showMessage("no threshold found");
		}
	}
	
	void getMinMax(ByteProcessor bp) {
		int N = bp.getWidth() * bp.getHeight();
		theMin = bp.get(0);
		theMax = theMin;
		for (int i=1; i<N; i++) {
			int p = bp.get(i);
			if (p < theMin) 
				theMin = p;
			if (p > theMax)
				theMax = p;
		}
	}
}
