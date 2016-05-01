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
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.util.Enums;
import imagingbook.pub.morphology.BinaryMorphologyFilter;
import imagingbook.pub.morphology.BinaryMorphologyFilter.OpType;

/**
 * This plugin implements a binary morphology filter using a disk-shaped
 * structuring element whose radius can be specified.
 */
public class Bin_Morphology_Disk implements PlugInFilter {

	static double radius = 1.0;
	static boolean showElement = false;
	static OpType op = OpType.Dilate;

	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor orig) {
		if (!showDialog())
			return;
		BinaryMorphologyFilter bmf = new BinaryMorphologyFilter.Disk(radius);
		bmf.applyTo((ByteProcessor) orig, op);
		if (showElement) {
			bmf.showStructuringElement();
		}
	}

	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("Structuring Element (Disk)");
		gd.addNumericField("Radius", 1.0, 1, 5, "pixels");
		String[] ops = Enums.getEnumNames(OpType.class);
		gd.addChoice("Operation", ops, op.name());
		gd.addCheckbox("Show structuring element", showElement);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		radius = gd.getNextNumber();
		showElement = gd.getNextBoolean();
		op = OpType.valueOf(gd.getNextChoice());
		return true;
	}

}
