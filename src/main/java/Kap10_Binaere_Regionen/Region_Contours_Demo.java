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
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.regions.Contour;
import imagingbook.pub.regions.ContourOverlay;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * This plugin demonstrates the use of the class CombinedContourLabeling
 * to perform both region labeling and contour tracing simultaneously.
 * The resulting contours are displayed as a non-destructive vector overlay.
 * 2010-11-19: initial version.
 * 2012-03-04: updated to use awt.Point2D (instead of Point) class 
 */
public class Region_Contours_Demo implements PlugInFilter {
	
	static boolean listRegions = true;
	static boolean listContourPoints = false;
	static boolean showContours = true;
	
	public int setup(String arg, ImagePlus im) { 
		return DOES_8G + NO_CHANGES; 
	}
	
	public void run(ImageProcessor ip) {
	   	if (!getUserInput())
    		return;
	   	
	   	// Make sure we have a proper byte image:
	   	ByteProcessor bp = (ByteProcessor) ip.convertToByteProcessor();
	   	
	   	// Create the region labeler / contour tracer:
		RegionContourLabeling segmenter = new RegionContourLabeling(bp);
		
		// Get the list of detected regions (sort by size):
		List<BinaryRegion> regions = segmenter.getRegions(true);
		if (regions.isEmpty()) {
			IJ.error("No regions detected!");
			return;
		}

		if (listRegions) {
			IJ.log("Detected regions: " + regions.size());
			for (BinaryRegion r: regions) {
				IJ.log(r.toString());
			}
		}
		
		if (listContourPoints) {
			// Get the outer contour of the largest region:
			BinaryRegion largestRegion = regions.get(0);
			Contour oc =  largestRegion.getOuterContour();
			IJ.log("Points along outer contour of largest region:");
			Point2D[] points = oc.getPointArray();
			for (int i = 0; i < points.length; i++) {
				Point2D p = points[i];
				IJ.log("Point " + i + ": " + p.toString());
			}
			
			// Get all inner contours of the largest region:
			List<Contour> ics = largestRegion.getInnerContours();
			IJ.log("Inner regions (holes): " + ics.size());
		}
		
		
		// Display the contours if desired:
		if (showContours) {
			ImageProcessor lip = segmenter.makeLabelImage(false);
			ImagePlus lim = new ImagePlus("Region labels and contours", lip);
			Overlay oly = new ContourOverlay(segmenter);
			lim.setOverlay(oly);
			lim.show();
		}
		
//		BinaryRegion r = segmenter.getRegions().get(0);
//		for (java.awt.Point p : r) {
//			
//		}
	}
	
	boolean getUserInput() {
		GenericDialog gd = new GenericDialog("Contour Tracer");
		gd.addCheckbox("List regions", listRegions);
		gd.addCheckbox("List contour points", listContourPoints);
		gd.addCheckbox("Show contours", showContours);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		listRegions = gd.getNextBoolean();
		listContourPoints = gd.getNextBoolean();
		showContours = gd.getNextBoolean();
		return true;
	}
}
