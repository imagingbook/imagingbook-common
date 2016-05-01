/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap12_Farbbilder;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.image.IndexColorModel;

public class Index_To_Rgb implements PlugInFilter {
	static final int R = 0, G = 1, B = 2;
	
	ImagePlus imp;
	
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8C + NO_CHANGES; // does not alter original image	
	}

	public void run(ImageProcessor ip) {
		int w = ip.getWidth();
		int h = ip.getHeight();
		
		// retrieve the lookup tables (maps) for R,G,B:
		IndexColorModel icm = (IndexColorModel) ip.getColorModel(); 
		int nColors = icm.getMapSize(); 
		
		byte[] Pred = new byte[nColors]; 
		byte[] Pgrn = new byte[nColors]; 
		byte[] Pblu = new byte[nColors];
		
		icm.getReds(Pred); 
		icm.getGreens(Pgrn);
		icm.getBlues(Pblu);
		  
		// create a new 24-bit RGB image:
		ColorProcessor cp = new ColorProcessor(w,h);
		int[] RGB = new int[3];
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int idx = ip.getPixel(u, v);
				RGB[R] = 0xFF & Pred[idx];
				RGB[G] = 0xFF & Pgrn[idx];
				RGB[B] = 0xFF & Pblu[idx];
				cp.putPixel(u, v, RGB); 
			}
		}
		ImagePlus cwin = new ImagePlus(imp.getShortTitle() + " (RGB)", cp);
		cwin.show();
	}
}

