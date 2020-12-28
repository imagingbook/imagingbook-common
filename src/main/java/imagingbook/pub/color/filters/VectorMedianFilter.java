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

import imagingbook.lib.filters.GenericFilter;
import imagingbook.lib.image.access.ImageAccessor;
import imagingbook.lib.image.access.ScalarAccessor;
import imagingbook.lib.math.VectorNorm;
import imagingbook.lib.math.VectorNorm.NormType;

/**
 * Basic vector median filter for color images implemented
 * by extending the {@link GenericFilter} class.
 * 
 * @author W. Burger
 * @version 2013/05/30
 */
public class VectorMedianFilter extends GenericFilter {
	
	public static class Parameters {
		/** Filter radius */
		public double radius = 3.0;
		/** Distance norm to use */
		public NormType distanceNorm = NormType.L1;
		/** For testing only */
		public boolean markModifiedPixels = false;
		/** For testing only */
		public boolean showMask = false;
	}
	
	final Parameters params;
	
	public static Color modifiedColor = Color.black;
	final int[] modColor = {modifiedColor.getRed(), modifiedColor.getGreen(), modifiedColor.getBlue()};
	public int modifiedCount = 0;
	
	final FilterMask mask;
	final int[][] supportRegion;		// supportRegion[i][c] with index i, color component c
	final VectorNorm vNorm;
	
	//-------------------------------------------------------------------------------------
	
	// uses default parameters:
	public VectorMedianFilter() {	
		this(new Parameters());
	}
	
	// accepts parameter object:
	public VectorMedianFilter(Parameters params) {
		this.params = params;
		mask = new FilterMask(params.radius);
		supportRegion = new int[mask.getCount()][3];
		vNorm = params.distanceNorm.create();
		initialize();
	}
	
	//-------------------------------------------------------------------------------------
	
	void initialize() {
		if (params.showMask) mask.show("Mask");
	}
	
	@Override
	protected float filterScalar(ScalarAccessor source, int u, int v) {
		throw new UnsupportedOperationException("no filter for gray images");
	}
	
	// vector median filter for RGB color image
	@Override
	protected float[] filterVector(ImageAccessor ia, int u, int v) {
		final int[] pCtr = new int[3];		// center pixel
		final float[] pCtrf = ia.getPix(u, v);
		copyRgbTo(pCtrf, pCtr);
		getSupportRegion(ia, u, v);	// TODO: check method!
		double dCtr = aggregateDistance(pCtr, supportRegion); 
		double dMin = Double.MAX_VALUE;
		int jMin = -1;
		for (int j = 0; j < supportRegion.length; j++) {
			int[] p = supportRegion[j];
			double d = aggregateDistance(p, supportRegion);
			if (d < dMin) {
				jMin = j;
				dMin = d;
			}
		}
		int[] pmin = supportRegion[jMin];
		// modify this pixel only if the min aggregate distance of some
		// other pixel in the filter region is smaller
		// than the aggregate distance of the original center pixel:
		final float[] pF = new float[3];	// the returned color tupel
		if (dMin < dCtr) {	// modify this pixel
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
	
	private void copyRgbTo(float[] source, int[] target) {
		target[0] = (int) source[0];
		target[1] = (int) source[1];
		target[2] = (int) source[2];
	}
	
//	private void copyRgbTo(float[] source, float[] target) {
//		target[0] = source[0];
//		target[1] = source[1];
//		target[2] = source[2];
//	}
	
	
	private void copyRgbTo(int[] source, float[] target) {
		target[0] = source[0];
		target[1] = source[1];
		target[2] = source[2];
	}
	
	// find the color with the smallest summed distance to all others.
	// brute force and thus slow
	private double aggregateDistance(int[] p, int[][] P) {
		double d = 0;
		for (int i = 0; i < P.length; i++) {
			d = d + vNorm.distance(p, P[i]);
		}
		return d;
	}

}
