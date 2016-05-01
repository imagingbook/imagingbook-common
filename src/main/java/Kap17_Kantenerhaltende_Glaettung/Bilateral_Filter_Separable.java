/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap17_Kantenerhaltende_Glaettung;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.filters.GenericFilter;
import imagingbook.lib.math.VectorNorm.NormType;
import imagingbook.lib.util.Enums;
import imagingbook.pub.edgepreservingfilters.BilateralFilterSeparable;
import imagingbook.pub.edgepreservingfilters.BilateralFilter.Parameters;

/**
 * Plugin demonstrating the use of the x/y-separated Bilateral filter.
 * This plugin works for all types of images and stacks.
 * @author W. Burger
 * @version 2014-03-16
 */
public class Bilateral_Filter_Separable implements PlugInFilter {
	
	private Parameters params = new Parameters();

	public int setup(String arg0, ImagePlus imp) {
		if (!getParameters(imp))
			return DONE;
		else
			return DOES_ALL + DOES_STACKS;
	}
	
	public void run(ImageProcessor ip) {
		GenericFilter filter = new BilateralFilterSeparable(params);
		filter.applyTo(ip);
	}

	private boolean getParameters(ImagePlus imp) {
    	boolean isColor = (imp.getType() == ImagePlus.COLOR_RGB);   //(ip instanceof ColorProcessor);
		GenericDialog gd = new GenericDialog("Bilateral Filter");
		gd.addNumericField("Sigma_domain", params.sigmaD, 1);
		gd.addNumericField("Sigma_range", params.sigmaR, 1);
		if (isColor) {
			gd.addChoice("Color norm", Enums.getEnumNames(NormType.class), params.colorNormType.name());
		}
		gd.showDialog();
		if (gd.wasCanceled()) return false;
		params.sigmaD = Math.max(gd.getNextNumber(), 0.5);
		params.sigmaR = Math.max(gd.getNextNumber(), 1);
		if (isColor) {
			params.colorNormType = NormType.valueOf(gd.getNextChoice());
		}
		return true;
    }
    
}



