/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap04_Punktoperationen;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.Blitter;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;

/**
 * This plugin demonstrates alpha blending between two images:
 * bg: the background image (passed to and modified by the run method),
 * fg: the foreground image (selected in a user dialog).
 * New (simpler) version using the {@code imagingbook.lib.ij.IjUtils.getOpenImages} library method.
 * @author WB
 */
public class Linear_Blending implements PlugInFilter {
	
	static double alpha = 0.5;	// transparency of foreground image
	ImagePlus fgIm;				// foreground image (to be selected)
	
	public int setup(String arg, ImagePlus im) {
		return DOES_8G;
	}	
	
	public void run(ImageProcessor ipBG) {	// iBG = background image
		if(runDialog()) {
			ImageProcessor ipFG = fgIm.getProcessor().convertToByte(false);
			ipFG = ipFG.duplicate();
			ipFG.multiply(1 - alpha);
			ipBG.multiply(alpha);
			ipBG.copyBits(ipFG, 0, 0, Blitter.ADD);
		}
	}	

	boolean runDialog() {
		// get list of open images and their titles:
		ImagePlus[] openImages = IjUtils.getOpenImages(true);
		String[] imageTitles = new String[openImages.length];
		for (int i = 0; i < openImages.length; i++) {
			imageTitles[i] = openImages[i].getShortTitle();
		}
		// create the dialog and show:
		GenericDialog gd = new GenericDialog("Alpha Blending");
		gd.addChoice("Foreground image:", imageTitles, imageTitles[0]);
		gd.addNumericField("Alpha value [0..1]:", alpha, 2);
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		else {
			fgIm = openImages[gd.getNextChoiceIndex()];
			alpha = gd.getNextNumber();
			return true;
		}
	}
}
