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
import imagingbook.pub.edgepreservingfilters.NagaoMatsuyamaFilter;
import imagingbook.pub.edgepreservingfilters.NagaoMatsuyamaFilter.Parameters;


/**
 * This plugin demonstrates the 5x5 Nagao-Matsuyama filter, as described in
 * NagaoMatsuyama (1979). This plugin works for all types of images and stacks.
 * @author W. Burger
 * @version 2014/03/16
 */
public class Nagao_Matsuyama_Filter implements PlugInFilter {
	
	private Parameters params = new Parameters();

	public int setup(String arg0, ImagePlus imp) {
		if (!getParameters(imp))
			return DONE;
		else
			return DOES_ALL + DOES_STACKS;
	}
	
    public void run(ImageProcessor ip) {
    	GenericFilter filter = new NagaoMatsuyamaFilter(params);
    	filter.applyTo(ip);
    }
    
    private boolean getParameters(ImagePlus imp) {
		GenericDialog gd = new GenericDialog("5x5 Nagao-Matsuyama Filter");
		gd.addNumericField("Variance threshold", params.varThreshold, 0);
		gd.showDialog();
		if (gd.wasCanceled()) return false;
		params.varThreshold = Math.max(gd.getNextNumber(),0);
		return true;
    }
    
}

