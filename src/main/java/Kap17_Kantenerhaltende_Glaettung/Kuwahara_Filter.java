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
import imagingbook.pub.edgepreservingfilters.KuwaharaFilter;
import imagingbook.pub.edgepreservingfilters.KuwaharaFilter.Parameters;

/**
 * This plugin demonstrates the use of the Kuwahara filter, similar to the filter suggested in 
 * Tomita and Tsuji (1977). It structures the filter region into  five overlapping, 
 * square subregions of size (r+1) x (r+1). Unlike the original Kuwahara filter,
 * it includes a centered subregion. This plugin works for all types of images and stacks.
 * @author W. Burger
 * @version 2014/03/16
 */
public class Kuwahara_Filter implements PlugInFilter {
	
	private Parameters params = new Parameters();
	
    public int setup(String arg, ImagePlus imp) {
		if (!getParameters(imp))
			return DONE;
		else
			return DOES_ALL + DOES_STACKS;
	}

    public void run(ImageProcessor ip) {
    	KuwaharaFilter filter = new KuwaharaFilter(params);
    	filter.applyTo(ip);
    }
    
    private boolean getParameters(ImagePlus imp) {
		GenericDialog gd = new GenericDialog("Median Filter");
		gd.addNumericField("Radius (>1)", params.radius, 0);
		gd.addNumericField("Variance threshold", params.tsigma, 0);
		gd.showDialog();
		if(gd.wasCanceled()) 
			return false;
		params.radius = (int) Math.max(gd.getNextNumber(), 1);
		params.tsigma = Math.max(gd.getNextNumber(), 0);
		return true;
    }
}

