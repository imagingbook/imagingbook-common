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

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.color.edge.CannyEdgeDetector;

import java.awt.Point;
import java.util.List;


public class Canny_Edge_Demo_Minimal implements PlugInFilter {
	
	public int setup(String arg0, ImagePlus imp) {
		return DOES_ALL + NO_CHANGES;
	}

	@SuppressWarnings("unused")
	public void run(ImageProcessor ip) {
	
		CannyEdgeDetector.Parameters params = 
							new CannyEdgeDetector.Parameters(); 
							
		params.gSigma = 3.0f;	// sigma of Gaussian
		params.hiThr  = 20.0f;	// 20% of max. edge magnitude
		params.loThr  = 5.0f;	// 5% of max. edge magnitude
		
		CannyEdgeDetector detector = new CannyEdgeDetector(ip, params); 
		
		FloatProcessor eMag = detector.getEdgeMagnitude();
		FloatProcessor eOrt = detector.getEdgeOrientation();
		ByteProcessor eBin = detector.getEdgeBinary();
		List<List<Point>> edgeTraces = detector.getEdgeTraces();
		
		(new ImagePlus("Canny Edges", eBin)).show();
		
		// process edge detection results ...
	}
}
