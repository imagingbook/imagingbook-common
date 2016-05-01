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

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.color.quantize.ColorQuantizer;
import imagingbook.pub.color.quantize.MedianCutQuantizer;

public class Median_Cut_Quantization implements PlugInFilter {
	static int NCOLORS = 32;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}
	
	public void run(ImageProcessor ip) {
		ColorProcessor cp = ip.convertToColorProcessor();
		int w = ip.getWidth();
		int h = ip.getHeight();
		
		// create a quantizer object
		ColorQuantizer q = new MedianCutQuantizer(cp, NCOLORS);
		int nCols = q.countQuantizedColors();
		
		// quantize to an indexed image
		ByteProcessor idxIp = q.quantize(cp);
		(new ImagePlus("Quantized Index Image (" + nCols + " colors)", idxIp)).show();
		
		// quantize to an RGB image
		int[] rgbPix = q.quantize((int[]) cp.getPixels());
		ImageProcessor rgbIp = new ColorProcessor(w, h, rgbPix);
		(new ImagePlus("Quantized RGB Image (" + nCols + " colors)" , rgbIp)).show();
		
	}
}
