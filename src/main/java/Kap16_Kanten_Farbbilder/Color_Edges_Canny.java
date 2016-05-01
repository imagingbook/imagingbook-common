/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap16_Kanten_Farbbilder;


import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.color.edge.CannyEdgeDetector;
import imagingbook.pub.color.edge.CannyEdgeDetector.Parameters;

import java.awt.Point;
import java.util.List;

/**
 * This plugin implements the Canny edge detector for all types of images.
 * @author W. Burger
 * @version 2014/12/03
 */
public class Color_Edges_Canny implements PlugInFilter {
	
	static boolean showEdgeMagnitude = true;
	static boolean showEdgeOrientation = true;
	static boolean showBinaryEdges = true;
	static boolean listEdgeTraces = true;
	
	ImagePlus imp = null;
	
	public int setup(String arg0, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		
		Parameters params = new Parameters();
		if (!setParameters(params)) return;
		
		CannyEdgeDetector detector = new CannyEdgeDetector(ip, params);
		
		if (showEdgeMagnitude) {
			ImageProcessor eMag = detector.getEdgeMagnitude();
			(new ImagePlus("Canny Edge Magnitude sigma=" + params.gSigma, eMag)).show();
		}
		
		if (showEdgeOrientation) {
			ImageProcessor eOrt = detector.getEdgeOrientation();
			(new ImagePlus("Canny Edge Orientation sigma=" + params.gSigma, eOrt)).show();
		}
		
		if (showBinaryEdges) {
			ImageProcessor eBin = detector.getEdgeBinary();
			(new ImagePlus("Canny Binary Edges sigma=" + params.gSigma, eBin)).show();
		}
		
		if(listEdgeTraces) {
			List<List<Point>> edgeTraces = detector.getEdgeTraces();
			IJ.log("number of edge traces: " + edgeTraces.size());
		}
	}

	boolean setParameters(Parameters params) {
		GenericDialog gd = new GenericDialog("Canny Detector");
		// Canny parameters:
		gd.addNumericField("Sigma (0.5 - 20)", params.gSigma, 1);
		gd.addNumericField("Low Threshold", params.loThr, 2);
		gd.addNumericField("High Threshold", params.hiThr, 2);
		gd.addCheckbox("Normalize gradient magnitude", params.normGradMag);
		// plugin parameters:
		gd.addMessage("Plugin parameters:");
		gd.addCheckbox("Show edge magnitude", showEdgeMagnitude);
		gd.addCheckbox("Show edge orientation", showEdgeOrientation); 
		gd.addCheckbox("Show binary edges", showBinaryEdges);
		gd.addCheckbox("List edge traces", listEdgeTraces);
		// display
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}	
		// update Canny parameters:
		params.gSigma = (float) gd.getNextNumber();
		if (params.gSigma < 0.5f) params.gSigma = 0.5f;
		if (params.gSigma > 20) params.gSigma = 20;
		params.loThr = (float) gd.getNextNumber();
		params.hiThr = (float) gd.getNextNumber();
		params.normGradMag = gd.getNextBoolean();
		// update plugin parameters:
		showEdgeMagnitude = gd.getNextBoolean();
		showEdgeOrientation = gd.getNextBoolean();
		showBinaryEdges = gd.getNextBoolean();
		listEdgeTraces = gd.getNextBoolean();
		return true;
	}
	
}
