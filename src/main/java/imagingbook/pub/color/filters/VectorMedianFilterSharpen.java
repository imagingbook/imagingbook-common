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

import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilterVector;
import imagingbook.lib.image.access.FloatPixelPack;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.math.VectorNorm;
import imagingbook.lib.math.VectorNorm.NormType;

/**
 * Sharpening vector median filter for color images implemented
 * by extending the {@link GenericFilterVector} class.
 * @author W. Burger
 * @version 2020/12/31
 */
public class VectorMedianFilterSharpen extends GenericFilterVector {
	
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
		/** Out-of-bounds strategy */
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NEAREST_BORDER;
	}
	
	private final FilterMask mask;
	private final int maskCount;
	private final int[][] maskArray;
	private final int maskCenter;
	private final float[][] supportRegion;		// supportRegion[i][c] with index i, color component c
	private final VectorNorm vNorm;
	private final int a;						// a = 2,...,n
	private final Parameters params;
	private final float[] modColor;
	
	/** For testing only */
	public int modifiedCount = 0;
	
	public VectorMedianFilterSharpen(ImageProcessor ip) {	
		this(ip, new Parameters());
	}
	
	public VectorMedianFilterSharpen(ImageProcessor ip, Parameters params) {
		super(ip, params.obs);
		this.params = params;
		this.mask = new FilterMask(params.radius);
		this.maskCount = mask.getCount();
		this.maskArray = mask.getMask();
		this.maskCenter = mask.getCenter();
		this.supportRegion = new float[maskCount][];
		this.a = (int) Math.round(maskCount - params.sharpen * (maskCount - 2));
		this.vNorm = params.distanceNorm.create();
		this.modColor = new float[] {params.modifiedColor.getRed(), params.modifiedColor.getGreen(), params.modifiedColor.getBlue()};
		
		if (params.showMask) mask.show("Mask");
	}
	
	@Override
	protected float[] filterPixel(FloatPixelPack sources, int u, int v) {
		float[] pCtr = sources.getPixel(u, v);
		getSupportRegion(sources, u, v);
		double dCtr = trimmedAggregateDistance(pCtr, supportRegion, a); 
		double dMin = Double.MAX_VALUE;
		int jMin = -1;
		for (int j = 0; j < supportRegion.length; j++) {
			//float[] p = supportRegion[j];
			double d = trimmedAggregateDistance(supportRegion[j], supportRegion, a);
			if (d < dMin) {
				jMin = j;
				dMin = d;
			}
		}
		float[] pmin = supportRegion[jMin];
		// modify this pixel only if the min aggregate distance of some
		// other pixel in the filter region is smaller
		// than the aggregate distance of the original center pixel:
		float[] pF = new float[3];			// the returned color tupel
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
	
	private float[][] getSupportRegion(FloatPixelPack src, int u, int v) {
		//final int[] p = new int[3];
		// fill 'supportRegion' for current mask position
		int n = 0;
		for (int i = 0; i < maskArray.length; i++) {
			int ui = u + i - maskCenter;
			for (int j = 0; j < maskArray[0].length; j++) {
				if (maskArray[i][j] > 0) {
					int vj = v + j - maskCenter;
//					float[] p = src.getPixel(ui, vj);
//					copyRgbTo(p, supportRegion[n]);
					supportRegion[n] = src.getPixel(ui, vj);	// TODO: check!!
					n = n + 1;
				}
			}
		}
		return supportRegion;
	}

	
	private void copyRgbTo(float[] source, float[] target) {
		target[0] = source[0];
		target[1] = source[1];
		target[2] = source[2];
	}
	
	private double trimmedAggregateDistance(float[] p, float[][] P, int a) {
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

}
