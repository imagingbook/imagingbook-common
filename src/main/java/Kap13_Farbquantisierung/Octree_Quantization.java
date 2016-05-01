/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/

package Kap13_Farbquantisierung;


import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.color.quantize.OctreeQuantizer;


public class Octree_Quantization implements PlugInFilter {
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		ColorProcessor cp = (ColorProcessor) ip;
		IJ.log("creating octree quantizer");
		OctreeQuantizer oq = new OctreeQuantizer(cp, 256);
		IJ.log("leaves created: " + oq.getNumberOfLeaves());
		
		ImageProcessor qi = oq.quantize(cp);
		(new ImagePlus("Quantized", qi)).show();
	}


}
