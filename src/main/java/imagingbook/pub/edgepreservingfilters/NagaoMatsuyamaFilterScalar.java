/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilterScalar;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.PixelPack.PixelSlice;

/**
 * Scalar version.
 * This class implements a 5x5 Nagao-Matsuyama filter, as described in
 * NagaoMatsuyama (1979).
 * 
 * @author W. Burger
 * @version 2021/01/02
 */

public class NagaoMatsuyamaFilterScalar extends GenericFilterScalar {
	
	public static class Parameters {
		/** Variance threshold */
		public double varThreshold = 0.0;	// 0,...,10
		/** Out-of-bounds strategy */
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NEAREST_BORDER;
	}
	
	protected static final int[][] R0 =
	{{-1,-1}, {0,-1}, {1,-1},
	 {-1, 0}, {0, 0}, {1, 0},
	 {-1, 1}, {0, 1}, {1, 1}};
	
	// -------------------
	
	private static final int[][] R1 =
	{{-2,-1}, {-1,-1},
	 {-2, 0}, {-1, 0}, {0, 0},
	 {-2, 1}, {-1, 1}};
	
	private static final int[][] R2 =
	{{-2,-2}, {-1,-2},
	 {-2,-1}, {-1,-1}, {0,-1},
	          {-1, 0}, {0, 0}};
	
	private static final int[][] R3 =
	{{-1,-2}, {0,-2}, {1,-2}, 
	 {-1,-1}, {0,-1}, {1,-1},
	          {0, 0}};

	private static final int[][] R4 =
	{        {1,-2}, {2,-2},
	 {0,-1}, {1,-1}, {2,-1},
	 {0, 0}, {1, 0}};
	
	private static final int[][] R5 =
	{        {1,-1}, {2,-1},
	 {0, 0}, {1, 0}, {2, 0},
	         {1, 1}, {2, 1}};
	
	private static final int[][] R6 =
	{{0,0}, {1,0},
	 {0,1}, {1,1}, {2,1},
	        {1,2}, {2,2}};

	private static final int[][] R7 =
	{        {0,0},
	 {-1,1}, {0,1}, {1,1},
	 {-1,2}, {0,2}, {1,2}};
	
	private static final int[][] R8 =
	{        {-1,0}, {0,0},
	 {-2,1}, {-1,1}, {0,1},
	 {-2,2}, {-1,2}};
	
	// -------------------
	
	protected static final int[][][] subRegions = {R1, R2, R3, R4, R5, R6, R7, R8};
	
	// ------------------------------------------------------
	
	private final float varThreshold;
	private float minVariance;
	private float minMean;
	
	public NagaoMatsuyamaFilterScalar(ImageProcessor ip) {
		this(ip, new Parameters());
	}
	
	public NagaoMatsuyamaFilterScalar(ImageProcessor ip, Parameters params) {
		super(ip, params.obs);
		this.varThreshold = (float) params.varThreshold;
	}
	
	// ------------------------------------------------------

	@Override
	protected float filterPixel(PixelSlice source, int u, int v) {
		minVariance = Float.MAX_VALUE;
		evalSubregion(source, R0, u, v);
		minVariance = minVariance - varThreshold;
		for (int[][] Rk : subRegions) {
			evalSubregion(source, Rk, u, v);
		}
 		return minMean;
 	}
	
	void evalSubregion(PixelSlice source, int[][] R, int u, int v) {
		float sum1 = 0; 
		float sum2 = 0;
		int n = 0;
		for (int[] p : R) {
			float a = source.getVal(u+p[0], v+p[1]);
			sum1 = sum1 + a;
			sum2 = sum2 + a * a;
			n = n + 1;
		}
		float nr = n;
		float var = (sum2 - sum1 * sum1 / nr) / nr;	// = sigma^2
		if (var < minVariance) {
			minVariance = var;
			minMean = sum1 / nr; // mean of subregion with min. variance so far
		}
	}
	
}
