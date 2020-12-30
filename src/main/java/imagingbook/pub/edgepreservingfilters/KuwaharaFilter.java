/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import imagingbook.lib.filters.GenericFilter2D;
import imagingbook.lib.image.access.ImageAccessor;
import imagingbook.lib.image.access.ScalarAccessor;

/**
 * This class implements a Kuwahara-type filter, similar to the filter suggested in 
 * Tomita and Tsuji (1977). It structures the filter region into five overlapping, 
 * square subregions (including a center region) of size (r+1) x (r+1). 
 * See algorithm 5.2 in Utics Vol. 3.
 * 
 * @author W. Burger
 * @version 2020/12/29
 */
public class KuwaharaFilter extends GenericFilter2D {
	
	public static class Parameters {
		/** Radius of the filter (should be even) */
		public int radius = 2;
		/** Threshold on sigma to avoid banding in flat regions */
		public double tsigma = 5.0;
		
		public Parameters() {
		}
		
		public Parameters(int radius, double tsigma) {
			this.radius = radius;
			this.tsigma = tsigma;
		}
	}
	
	private final Parameters params;
	private final int n;			// fixed subregion size 
	private final int dm;			// = d-
	private final int dp;			// = d+


	// constructor using default settings
	public KuwaharaFilter() {
		this(new Parameters());
	}
	
	public KuwaharaFilter(Parameters params) {
		this.params = params;
		//initialize();
		int r = params.radius;
		this.n = (r + 1) * (r + 1);	// size of complete filter
		this.dm = (r/2) - r;			// d- = top/left center coordinate
		this.dp = this.dm + r;			// d+ = bottom/right center coordinate
	}
	
	public KuwaharaFilter(int r, double tsigma) {
		this(new Parameters(r, tsigma));
	}
	
//	private void initialize() {
//		int r = params.radius;
//		n = (r + 1) * (r + 1);	// size of complete filter
//		dm = (r/2) - r;			// d- = top/left center coordinate
//		dp = dm + r;			// d+ = bottom/right center coordinate
//	}
		
//	private static int checkRadius(int radius) {
//		assert radius >= 1 : "filter radius must be >= 1";
//		return radius;
//	}
	
	// ------------------------------------------------------

	// these are used in calculation by several methods:
	private float Smin;		// min. variance
	private float Amin;		
	private float AminR;
	private float AminG;
	private float AminB;
	
	// This method is used for all scalar-values images.
	@Override
	protected void filterScalar(ScalarAccessor ia, ScalarAccessor target, int u, int v) {
		Smin = Float.MAX_VALUE;
		evalSubregionGray(ia, u, v);					// a centered subregion (not in original Kuwahara)
		Smin = Smin - (float)params.tsigma * n;			// tS * n because we use variance scaled by n
		evalSubregionGray(ia, u + dm, v + dm);
		evalSubregionGray(ia, u + dm, v + dp);
		evalSubregionGray(ia, u + dp, v + dm);
		evalSubregionGray(ia, u + dp, v + dp);
		
		target.setVal(u, v, Amin);
 		//return Amin;
 	} 
	
	// sets the member variables Smin, Amin
	private void evalSubregionGray(ScalarAccessor ia, int u, int v) {
		float S1 = 0; 
		float S2 = 0;
		for (int j = dm; j <= dp; j++) {
			for (int i = dm; i <= dp; i++) {
				float a = ia.getVal(u+i, v+j);
				S1 = S1 + a;
				S2 = S2 + a * a;
			}
		}
//		double s = (sum2 - sum1*sum1/nr)/nr;	// actual sigma^2
		float s = S2 - S1 * S1 / n;	// s = n * sigma^2
		if (s < Smin) {
			Smin = s;
			Amin = S1 / n; // mean
		}
	}
	
	// ------------------------------------------------------
	
	private final float[] rgb = {0,0,0};
	
	@Override
	protected void filterVector(ImageAccessor ia, ImageAccessor target, int u, int v) {
		Smin = Float.MAX_VALUE;
		evalSubregion(ia, u, v);						// centered subregion - different to original Kuwahara!
		Smin = Smin - (3 * (float)params.tsigma * n);	// tS * n because we use variance scaled by n
		evalSubregion(ia, u+dm, v+dm);	
		evalSubregion(ia, u+dm, v+dp);	
		evalSubregion(ia, u+dp, v+dm);	
		evalSubregion(ia, u+dp, v+dp);	
		rgb[0] = (int) Math.rint(AminR);
		rgb[1] = (int) Math.rint(AminG);
		rgb[2] = (int) Math.rint(AminB);
		target.setPix(u, v, rgb);
 		//return rgb;
 	}
	
	private void evalSubregion(ImageAccessor ia, int u, int v) {
		// evaluate the subregion centered at (u,v)
		//final int[] cpix = {0,0,0};
		int S1R = 0, S2R = 0;	// TODO: use float throughout?
		int S1G = 0, S2G = 0;
		int S1B = 0, S2B = 0;
		for (int j = dm; j <= dp; j++) {
			for (int i = dm; i <= dp; i++) {		
				float[] cpix = ia.getPix(u + i, v + j);
				int red = (int) cpix[0];
				int grn = (int) cpix[1];
				int blu = (int) cpix[2];
				S1R = S1R + red;
				S1G = S1G + grn;
				S1B = S1B + blu;
				S2R = S2R + red * red;
				S2G = S2G + grn * grn;
				S2B = S2B + blu * blu;
			}
		}
		// calculate the variance for this subregion:
		float nf = n;
		float SR = S2R - S1R * S1R / nf;
		float SG = S2G - S1G * S1G / nf;
		float SB = S2B - S1B * S1B / nf;
		// total variance (scaled by nr):
		float Srgb = SR + SG + SB;
		if (Srgb < Smin) { 
			Smin = Srgb;
			AminR = S1R / n;	
			AminG = S1G / n;
			AminB = S1B / n;
		}
	}
}
