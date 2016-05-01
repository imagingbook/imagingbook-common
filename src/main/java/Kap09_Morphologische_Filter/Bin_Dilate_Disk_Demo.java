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

import ij.ImagePlus;
import ij.Prefs;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.morphology.BinaryMorphologyFilter;
import imagingbook.pub.morphology.BinaryMorphologyFilter.OpType;

/**
 * This plugin performs a binary dilation using a disk-shaped
 * structuring element with a specified radius.
 */
public class Bin_Dilate_Disk_Demo implements PlugInFilter {

	static double radius = 5.0;
	static OpType op = OpType.Dilate;	// Erode, Open, Close, ...

	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		BinaryMorphologyFilter bmf = new BinaryMorphologyFilter.Disk(radius);
		bmf.applyTo((ByteProcessor) ip, op);
		Prefs.blackBackground = false;
		if (ip.isInvertedLut())
			ip.invertLut();
	}

}
