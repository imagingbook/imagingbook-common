/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap10_Binaere_Regionen;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.regions.BreadthFirstLabeling;
import imagingbook.pub.regions.DepthFirstLabeling;
import imagingbook.pub.regions.RecursiveLabeling;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;
import imagingbook.pub.regions.SequentialLabeling;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * This ImageJ plugin is an example for how to use the region
 * labeling classes in the "regions" package.
 * One of four labeling types can be selected (see run() method).
 * They should all return the same result.
 * 2009-11-15: Cleanup, added mu_11() example.
 * 2010-11-19: Added user dialog, selection menu, combined region+contour 
 * segmenter.
*/
public class Region_Labeling_Demo implements PlugInFilter {
	
	public enum LabelingMethod {
			BreadthFirst, DepthFirst, Recursive, Sequential, RegionAndContours
		};

	static LabelingMethod method = LabelingMethod.BreadthFirst;
	static boolean recolor = false;
	static boolean listRegions = true;
	
    public int setup(String arg, ImagePlus im) {
		return DOES_8G + NO_CHANGES;
    }
	
    public void run(ImageProcessor ip) {
    	if (!getUserInput())
    		return;
    	if (method == LabelingMethod.Recursive && 
    			!IJ.showMessageWithCancel("Recursive labeling", "This may run out of stack memory!\n" + "Continue?")) {
			return;
    	}
    	
    	// Copy the original to a new byte image:
    	ByteProcessor bp = ip.convertToByteProcessor(false);
    
		// select one of 4 different labeling methods:
		RegionLabeling segmenter = null;
		switch (method) {
			case BreadthFirst:		segmenter = new BreadthFirstLabeling(bp); break;
			case DepthFirst:		segmenter = new DepthFirstLabeling(bp); break;
			case Recursive:			segmenter = new RecursiveLabeling(bp); break; 
			case Sequential:		segmenter = new SequentialLabeling(bp); break;
			case RegionAndContours:	segmenter = new RegionContourLabeling(bp); break;
		}

		// Retrieve the list of detected regions:
		List<BinaryRegion> regions = segmenter.getRegions(true);	// regions are sorted by size
		if (listRegions) {
			IJ.log("Detected regions (sorted by size): " + regions.size());
			for (BinaryRegion r: regions) {
				IJ.log(r.toString());
			}
		}
		
		// Show the resulting labeling as a random color image
		ImageProcessor labelIp = segmenter.makeLabelImage(recolor);
		(new ImagePlus("Label Image", labelIp)).show();
		
		// Example for processing all regions:
		for (BinaryRegion r : regions) {
			double mu11 = mu_11(r);	// example for calculating region statistics (see below)
			IJ.log("Region " + r.getLabel() + ": mu11=" + mu11);
		}
    }
    
	boolean getUserInput() {
		GenericDialog gd = new GenericDialog("Binary Region Labeling");
		LabelingMethod[] methods = LabelingMethod.values();
		String[] mNames = new String[methods.length];
		for (int i=0; i<methods.length; i++) {
			mNames[i] = methods[i].name();
		}
		gd.addChoice("Labeling method", mNames, mNames[0]);
		gd.addCheckbox("Color result", recolor);
		gd.addCheckbox("List regions", listRegions);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		String mName = gd.getNextChoice();
		method = LabelingMethod.valueOf(mName);
		recolor = gd.getNextBoolean();
		listRegions = gd.getNextBoolean();
		return true;
	}
    
    /*
     * This method demonstrates how a particular region's central moment
     * mu_11 could be calculated from the finished region labeling.
     */
    double mu_11 (BinaryRegion r) {
    	Point2D ctr = r.getCenterPoint();
    	final double xc = ctr.getX();	// centroid of this region
    	final double yc = ctr.getY();
    	double mu11 = 0;					// moment
    	
    	// iterate through all pixels of regions r:
    	for (Point p : r) {
    		mu11 = mu11 + (p.x - xc) * (p.y - yc);
    	}
    	return mu11;
    }
    
}



