/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.color.filters;

import java.awt.Color;
import java.util.Arrays;

import imagingbook.lib.filters.GenericFilter2D;
import imagingbook.lib.image.access.ImageAccessor;
import imagingbook.lib.image.access.ScalarAccessor;
import imagingbook.lib.math.VectorNorm;
import imagingbook.lib.math.VectorNorm.NormType;

/**
 * Sharpening vector median filter for color images implemented
 * by extending the {@link GenericFilter2D} class.
 * @author W. Burger
 * @version 2013/05/30
 */
public class VectorMedianFilterSharpen extends GenericFilter2D {
	
	public static class Parameters {
		/** Filter radius */
		public double radius = 3.0;
		/** Sharpening factor (0 = none, 1 = max.) */
		public double sharpen = 0.5;
		/** Threshold for replacing the current center pixel */
		public double threshold = 0.0;	
		/** Distance norm to use */
		public NormType distanceNorm = NormType.L1;
		/** For testing only */
		public boolean showMask = false;
		/** For testing only */
		public boolean markModifiedPixels = false;
		/** For testing only */
		public Color modifiedColor = Color.black;
	}
	
	private final FilterMask mask;
	private final int[][] supportRegion;		// supportRegion[i][c] with index i, color component c
	private final VectorNorm vNorm;
	private final int a;						// a = 2,...,n
	private final Parameters params;
	
	private int[] modColor;
	
	/** For testing only */
	public int modifiedCount = 0;
	
	/**
	 * Uses default parameters.
	 */
	public VectorMedianFilterSharpen() {	
		this(new Parameters());
	}
	
	public VectorMedianFilterSharpen(Parameters params) {
		this.params = params;
		mask = new FilterMask(params.radius);
		int maskCount = mask.getCount();
		supportRegion = new int[maskCount][3];
		a = (int) Math.round(maskCount - params.sharpen * (maskCount - 2));
		vNorm = params.distanceNorm.create();
		initialize();
	}
	
	private void initialize() {
		modColor = new int[] {params.modifiedColor.getRed(), params.modifiedColor.getGreen(), params.modifiedColor.getBlue()};
		if (params.showMask) 
			mask.show("Mask");
	}
	
	@Override
	protected float filterScalar(ScalarAccessor source, int u, int v) {
		throw new UnsupportedOperationException("no filter for gray images");
	}
	
	/**
	 * Vector median filter for RGB color images
	 */
	@Override
	protected float[] filterVector(ImageAccessor ia, int u, int v) {
		final int[] pCtr = new int[3];		// center pixel
		final float[] pCtrf = ia.getPix(u, v);
		copyRgbTo(pCtrf, pCtr);			// TODO: check, not elegant
		getSupportRegion(ia, u, v);
		double dCtr = trimmedAggregateDistance(pCtr, supportRegion, a); 
		double dMin = Double.MAX_VALUE;
		int jMin = -1;
		for (int j = 0; j < supportRegion.length; j++) {
			int[] p = supportRegion[j];
			double d = trimmedAggregateDistance(p, supportRegion, a);
			if (d < dMin) {
				jMin = j;
				dMin = d;
			}
		}
		int[] pmin = supportRegion[jMin];
		// modify this pixel only if the min aggregate distance of some
		// other pixel in the filter region is smaller
		// than the aggregate distance of the original center pixel:
		final float[] pF = new float[3];			// the returned color tupel
		if (dCtr - dMin > params.threshold * a) {	// modify this pixel
			if (params.markModifiedPixels) {
				copyRgbTo(modColor, pF);
				modifiedCount++;
			}
			else {
				copyRgbTo(pmin, pF);
			}
		}
		else {	// keep the original pixel value
			copyRgbTo(pCtr, pF);
		}
		return pF;
 	}
	
	private int[][] getSupportRegion(ImageAccessor ia, int u, int v) {
		//final int[] p = new int[3];
		// fill 'supportRegion' for current mask position
		int n = 0;
		final int[][] maskArray = mask.getMask();
		final int maskCenter = mask.getCenter();
		for (int i = 0; i < maskArray.length; i++) {
			int ui = u + i - maskCenter;
			for (int j = 0; j < maskArray[0].length; j++) {
				if (maskArray[i][j] > 0) {
					int vj = v + j - maskCenter;
					final float[] p = ia.getPix(ui, vj);
					copyRgbTo(p, supportRegion[n]);
					n = n + 1;
				}
			}
		}
		return supportRegion;
	}
	
//	private void copyRgbTo(int[] source, int[] target) {
//		target[0] = source[0];
//		target[1] = source[1];
//		target[2] = source[2];
//	}
	
	private void copyRgbTo(float[] source, int[] target) {
		target[0] = (int) source[0];
		target[1] = (int) source[1];
		target[2] = (int) source[2];
	}
	
	private void copyRgbTo(int[] source, float[] target) {
		target[0] = source[0];
		target[1] = source[1];
		target[2] = source[2];
	}
	
	// find the color with the smallest summed distance to all others.
	// brute force and thus slow
//	private double aggregateDistance(int[] p, int[][] P) {
//		double d = 0;
//		for (int i = 0; i < P.length; i++) {
//			d = d + vNorm.distance(p, P[i]);
//		}
//		return d;
//	}
	
	private double trimmedAggregateDistance(int[] p, int[][] P, int a) {
		if (a <= 1) {
			return 0;	// aggregate distance of rank 1 is always zero
		}
		int N = P.length;
		final double[] R = new double[N];
		for (int i = 0; i < N; i++) {
			R[i] = vNorm.distance(p, P[i]);
		}
		if (a < N) {
			Arrays.sort(R);	// only sort if the rank is less than N
		}
		double d = 0;
		for (int i = 1; i < a; i++) {
			d = d + R[i];
		}
		return d;
	}
	
//	private final int rgbToInt (int r, int g, int b) {
//		return ((r & 0xFF)<<16) | ((g & 0xFF)<<8) | b & 0xFF;
//	}

}
