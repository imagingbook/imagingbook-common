/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/

package Kap09_Morphologische_Filter;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.morphology.BinaryMorphologyFilter;

/**
 * This ImageJ plugin demonstrates morphological thinning
 * on binary images. Pixels with value 0 are considered
 * background, values &gt; 0 are foreground. The plugin 
 * modifies the supplied image.
 * 
 * @author W. Burger
 * @version 2015/07/08
 *
 */
public class Thinning_Demo implements PlugInFilter {
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		BinaryMorphologyFilter m = new BinaryMorphologyFilter();
		int iterations = m.thin((ByteProcessor) ip);
		IJ.log("Iterations performed: " + iterations);
	}

}




