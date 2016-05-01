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
import imagingbook.pub.threshold.global.OtsuThresholder;

/**
 * Demo plugin showing the use of the OtsuThresholder class.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Threshold_Global_Otsu implements PlugInFilter {
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}	

	public void run(ImageProcessor ip) {
		ByteProcessor bp = (ByteProcessor) ip;
		
		OtsuThresholder thr = new OtsuThresholder();
		int q = thr.getThreshold(bp);
		if (q >= 0) {
			IJ.log("threshold = " + q);
			bp.threshold(q);
		}
		else {
			IJ.showMessage("no threshold found");
		}
	}

}
