/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.color.quantize;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;


/**
 * This class implements color quantization by k-means clustering
 * of image pixels in RGB color space. It uses the Apache Commons
 * Math class {@link KMeansPlusPlusClusterer} to perform the
 * clustering.
 * 
 * @author WB
 * @version 2017/01/04
 */
public class KMeansClusteringQuantizerApache extends ColorQuantizerOld {
	
	private final Parameters params;
	private final int[][] colormap;
	
//	private final Clusterer<DoublePoint> clusterer;
	private final List<CentroidCluster<DoublePoint>> centers;
	
	public enum SamplingMethod {
		Random, Most_Frequent
	};
	
	public static class Parameters {
		/** Maximum number of quantized colors. */
		public int maxColors = 16;
		/** Maximum number of clustering iterations */
		public int maxIterations = 500;
		
		void check() {
			if (maxColors < 2 || maxColors > 256 || maxIterations < 1) {
				throw new IllegalArgumentException();
			}
		}
	}
	
	// --------------------------------------------------------------

	/**
	 * Creates a new quantizer instance from the supplied sequence
	 * of color values (assumed to be ARGB-encoded integers). 
	 * @param pixels Sequence of input color values.
	 * @param params Parameter object.
	 */
	public KMeansClusteringQuantizerApache(int[] pixels, Parameters params) {
		params.check();
		this.params = params;
		centers = cluster(pixels);
		colormap = makeColorMap();
	}
	
	public KMeansClusteringQuantizerApache(int[] pixels) {
		this(pixels, new Parameters());
	}
	
	// --------------------------------------------------------------

	private List<CentroidCluster<DoublePoint>> cluster(int[] pixels) {
		KMeansPlusPlusClusterer<DoublePoint> clusterer = 
				new KMeansPlusPlusClusterer<>(params.maxColors, params.maxIterations);
		
		List<DoublePoint> points = new ArrayList<>();
		for (int i = 0; i < pixels.length; i++) {
			points.add(new DoublePoint(intToRgbDouble(pixels[i])));
		}
		
		return clusterer.cluster(points);
	}
	
	// --------------------------------------------------------------

	private double[] intToRgbDouble(int rgb) {
		int red   = ((rgb >> 16) & 0xFF);
		int grn = ((rgb >> 8) & 0xFF);
		int blu  = (rgb & 0xFF);
		return new double[] {red, grn, blu};
	}

	private int[][] makeColorMap() {
		List<int[]> colList = new LinkedList<>();
		
		for (CentroidCluster<DoublePoint> ctr : centers) {
			double[] c = ctr.getCenter().getPoint();
			int red = (int) Math.round(c[0]);
			int grn = (int) Math.round(c[1]);
			int blu = (int) Math.round(c[2]);
			colList.add(new int[] {red, grn, blu});
		}
		
		return colList.toArray(new int[0][]);
	}
	
	/**
	 * Lists the color clusters to System.out (intended for debugging only).
	 */
//	public void listClusters() {
//		for (Cluster c : clusters) {
//			System.out.println(c.toString());
//		}
//	}
	

	// ------- methods required by abstract super class -----------------------
	
	@Override
	public int[][] getColorMap() {
		return colormap;
	}
	
} 

