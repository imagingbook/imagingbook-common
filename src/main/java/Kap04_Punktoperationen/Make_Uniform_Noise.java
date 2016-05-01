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
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Make_Uniform_Noise implements PlugInFilter { 
	
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G;
	}
	
	public void run(ImageProcessor ip) {
		int w = ip.getWidth();
		int h = ip.getHeight();
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int p = (int) (Math.random() * 256);
				ip.putPixel(u, v, p);
			}
		}
	}


}

