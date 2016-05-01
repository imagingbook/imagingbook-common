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
import imagingbook.pub.edgepreservingfilters.TschumperleDericheFilter;
import imagingbook.pub.edgepreservingfilters.TschumperleDericheFilter.Parameters;

/**
 * This ImageJ plugin demonstrates the use of the Anisotropic Diffusion filter proposed 
 * by David Tschumperle in D. Tschumperle and R. Deriche, Rachid, "Diffusion PDEs on 
 * vector-valued images}", IEEE Signal Processing Magazine, vol. 19, no. 5, pp. 16-25 
 * (Sep. 2002). This plugin works for all types of images and stacks.
 * @author W. Burger
 * @version 2014/03/16
 */

public class Tschumperle_Deriche_Filter implements PlugInFilter {
	
	private Parameters params = new Parameters();

	public int setup(String arg0, ImagePlus imp) {
		if (!getParameters(imp))
			return DONE;
		else
			return DOES_ALL + DOES_STACKS;
	}
	
	public void run(ImageProcessor ip) {
		TschumperleDericheFilter filter = new TschumperleDericheFilter(params);	
		filter.applyTo(ip);
	}

	private boolean getParameters(ImagePlus imp) {
    	boolean isColor = (imp.getType() == ImagePlus.COLOR_RGB);
		GenericDialog gd = new GenericDialog("2D Anisotropic Diffusion Tschumperle-Deriche");
		gd.addNumericField("Number of iterations", params.iterations, 0);
		gd.addNumericField("dt (Time step)", params.dt, 1);
		gd.addNumericField("Gradient smoothing (sigma_g)", params.sigmaG, 2);
		gd.addNumericField("Structure tensor smoothing (sigma_s)", params.sigmaS, 2);
		gd.addNumericField("a1 (Diffusion limiter along minimal variations)", params.a1, 2);
		gd.addNumericField("a2 (Diffusion limiter along maximal variations)", params.a2, 2);
		if (isColor) {
			gd.addCheckbox("Use linear RGB", params.useLinearRgb);
		}
		gd.addMessage("Incorrect values are replaced by defaults.");
		
		gd.showDialog();
		if (gd.wasCanceled()) return false;

		params.iterations = Math.max(1, (int)gd.getNextNumber());
		params.dt = (double) gd.getNextNumber();
		params.sigmaG = gd.getNextNumber();
		params.sigmaS = gd.getNextNumber();
		params.a1 = (float) gd.getNextNumber();
		params.a2 = (float) gd.getNextNumber();
		if (isColor) {
			params.useLinearRgb = gd.getNextBoolean();
		}
		return true;
	}
    
}



