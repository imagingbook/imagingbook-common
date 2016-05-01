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
 * This plugin implements a binary morphology filter using an arbitrary
 * structuring element.
 */
public class Bin_Morphology_Free implements PlugInFilter {

	static final int W = 9;	// width and height of the structuring element
	static boolean[] freeStructure = new boolean[W * W];
	static boolean showElement = false;
	static OpType op = OpType.Dilate;

	static { // initially set the center element
		freeStructure[(W * W) / 2] = true;
	}

	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor orig) {
		if (!showDialog())
			return;
		int[][] H = makeStructureElement();
		BinaryMorphologyFilter bmf = new BinaryMorphologyFilter(H);
		bmf.applyTo((ByteProcessor) orig, op);
		if (showElement) {
			bmf.showStructuringElement();
		}
	}

	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("Structuring Element");
		String[] labels = makeFilterLabels();

		gd.addCheckboxGroup(W, W, labels, freeStructure);
		String[] ops = Enums.getEnumNames(OpType.class);
		gd.addChoice("Operation", ops, op.name());
		gd.addCheckbox("Show structuring element", showElement);
		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		for (int i = 0; i < W * W; i++) {
			freeStructure[i] = gd.getNextBoolean();
		}
		
		showElement = gd.getNextBoolean();
		op = OpType.valueOf(gd.getNextChoice());
		return true;
	}

	private int[][] makeStructureElement() {
		int[][] filter = new int[W][W];
		int i = 0;
		for (int v = 0; v < W; v++) {
			for (int u = 0; u < W; u++) {
				if (freeStructure[i])
					filter[v][u] = 1;
				else
					filter[v][u] = 0;
				i++;
			}
		}
		return filter;
	}

	private String[] makeFilterLabels() {
		String[] labels = new String[W * W];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = " ";
		}
		labels[(W * W) / 2] = "x";
		return labels;
	}

}
