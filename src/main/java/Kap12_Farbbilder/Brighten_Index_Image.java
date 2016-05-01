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
import ij.process.ImageProcessor;
import java.awt.image.IndexColorModel;

public class Brighten_Index_Image implements PlugInFilter {
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G + DOES_8C;	// this plugin works on indexed color images 
	}

	public void run(ImageProcessor ip) {
		IndexColorModel icm = (IndexColorModel) ip.getColorModel(); 
		//IJ.write("Color Model=" + ip.getColorModel() + " " + ip.isColorLut());
	
		int pixBits = icm.getPixelSize(); 
		int nColors = icm.getMapSize(); 
		
		//retrieve the current lookup tables (maps) for R,G,B
		byte[] Pred = new byte[nColors]; icm.getReds(Pred);  
		byte[] Pgrn = new byte[nColors]; icm.getGreens(Pgrn);  
		byte[] Pblu = new byte[nColors]; icm.getBlues(Pblu);  
		
		//modify the lookup tables	
		for (int idx = 0; idx < nColors; idx++){ 
			int r = 0xff & Pred[idx];	//mask to treat as unsigned byte 
			int g = 0xff & Pgrn[idx];
			int b = 0xff & Pblu[idx];   
			Pred[idx] = (byte) Math.min(r + 10, 255); 
			Pgrn[idx] = (byte) Math.min(g + 10, 255);
			Pblu[idx] = (byte) Math.min(b + 10, 255); 
		}
		
		//create a new color model and apply to the image
		IndexColorModel icm2 = new IndexColorModel(pixBits, nColors, Pred, Pgrn, Pblu);  
		ip.setColorModel(icm2);
	}

}

