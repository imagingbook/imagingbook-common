/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap04_Punktoperationen;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.histogram.HistogramMatcher;
import imagingbook.pub.histogram.HistogramPlot;
import imagingbook.pub.histogram.Util;

public class Match_To_Gaussian_Histogram implements PlugInFilter { 
	
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G;
	}
	
	public void run(ImageProcessor ipA) {
		
		// get histograms
		int[] hA = ipA.getHistogram();
		int[] hB = Util.makeGaussianHistogram(128,50);
				
		(new HistogramPlot(hA, "Histogram A")).show();
		(new HistogramPlot(hB, "Cumulative Histogram A")).show();
		
		double[] nhB = Util.normalizeHistogram(hB);
		(new HistogramPlot(nhB, "Gauss")).show();
		
		double[] chB = Util.Cdf(hB);
    	(new HistogramPlot(chB, "Gauss cumulative")).show();
		
		HistogramMatcher m = new HistogramMatcher();
		int[] F = m.matchHistograms(hA, hB);
		
//		for (int i = 0; i < F.length; i++) {
//			IJ.log(i + " -> " + F[i]);
//		}
		
		ipA.applyTable(F);
		int[] hAm = ipA.getHistogram();
		(new HistogramPlot(hAm, "Histogram A (mod)")).show();
		(new HistogramPlot(Util.Cdf(hAm), "Cumulative Histogram A (mod)")).show();
	}

}

