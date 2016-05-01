/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap08_Hough_Transformation;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.hough.HoughTransformLines;
import imagingbook.pub.hough.HoughTransformLines.HoughLine;
import imagingbook.pub.hough.HoughTransformLines.Parameters;

/** 
 * This plugin implements a simple Hough transform for straight lines.
 * It expects a binary (8-bit) image, with background = 0 and foreground (contour) 
 * pixels with values &gt; 0.
 * 
 * @author WB
 * @version 2013/08/25
*/
public class Find_Straight_Lines_Simple implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		Parameters params = new Parameters();
		params.nAng = 256;		// = m
		params.nRad = 256;		// = n

		// compute the Hough Transform:
		HoughTransformLines ht = new HoughTransformLines(ip, params);
		// retrieve the 5 strongest lines with min. 50 points
		HoughLine[] lines = ht.getLines(50, 5);
		
		if (lines.length > 0) {
			IJ.log("Lines found:");
			for (HoughLine L : lines) {
				IJ.log(L.toString());	// list the resulting lines
			}
		} 
		else 
			IJ.log("No lines found!");
	}

}





	
	
