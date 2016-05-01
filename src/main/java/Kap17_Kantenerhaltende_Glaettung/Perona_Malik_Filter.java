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
import imagingbook.lib.util.Enums;
import imagingbook.pub.edgepreservingfilters.PeronaMalikFilter;
import imagingbook.pub.edgepreservingfilters.PeronaMalikFilter.ColorMode;
import imagingbook.pub.edgepreservingfilters.PeronaMalikFilter.Parameters;

/**
 * This plugin demonstrates the use of the PeronaMalikFilter class.
 * This plugin works for all types of images and stacks.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Perona_Malik_Filter implements PlugInFilter {

	private Parameters params = new Parameters();

	public int setup(String arg0, ImagePlus imp) {
		if (!getParameters(imp))
			return DONE;
		else
			return DOES_ALL + DOES_STACKS;
	}

	public void run(ImageProcessor ip) {
		PeronaMalikFilter filter = new PeronaMalikFilter(params);
		filter.applyTo(ip);
	}
	
	private boolean getParameters(ImagePlus imp) {
    	boolean isColor = (imp.getType() == ImagePlus.COLOR_RGB);
		GenericDialog gd = new GenericDialog("Anisotropic Diffusion Filter");
		gd.addNumericField("Number of iterations", params.iterations, 0);
		gd.addNumericField("Alpha (0,..,0.25)", params.alpha, 2);
		gd.addNumericField("K", params.kappa, 0);
		gd.addCheckbox("Smoother regions", params.smoothRegions);
		if (isColor) {
			gd.addChoice("Color method", Enums.getEnumNames(ColorMode.class), params.colorMode.name());
			gd.addCheckbox("Use linear RGB", params.useLinearRgb);
		}
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		params.iterations = (int) Math.max(gd.getNextNumber(), 1);
		params.alpha = (float) gd.getNextNumber();
		params.kappa = (float) gd.getNextNumber();
		params.smoothRegions = gd.getNextBoolean();
		if (isColor) {
			params.colorMode = ColorMode.valueOf(gd.getNextChoice());
			params.useLinearRgb = gd.getNextBoolean();
		}
		return true;
	}
}



