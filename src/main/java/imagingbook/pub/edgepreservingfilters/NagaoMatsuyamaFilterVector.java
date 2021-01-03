/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import static imagingbook.lib.math.Arithmetic.sqr;
import static imagingbook.pub.edgepreservingfilters.NagaoMatsuyamaF.R0;
import static imagingbook.pub.edgepreservingfilters.NagaoMatsuyamaF.SubRegions;

import ij.process.ColorProcessor;
import imagingbook.lib.filter.GenericFilterVector;
import imagingbook.lib.image.access.PixelPack;
import imagingbook.pub.edgepreservingfilters.NagaoMatsuyamaF.Parameters;

/**
 * Vector version.
 * This class implements a 5x5 Nagao-Matsuyama filter, as described in
 * NagaoMatsuyama (1979).
 * 
 * @author W. Burger
 * @version 2021/01/02
 */

public class NagaoMatsuyamaFilterVector extends GenericFilterVector {

	// uses the same subregions as defined in {@link NagaoMatsuyamaFilterScalar}
	
	private final float varThreshold;
	private float minVariance;
	private final float[] rgb = new float[3];
	private final float[] minMean = new float[3];
	
	public NagaoMatsuyamaFilterVector(ColorProcessor ip) {
		this(ip, new Parameters());
	}
	
	public NagaoMatsuyamaFilterVector(ColorProcessor ip, Parameters params) {
		super(ip, params.obs);
		this.varThreshold = (float) params.varThreshold;
	}
	
	// ------------------------------------------------------
	
	@Override
	protected float[] filterPixel(PixelPack ia, int u, int v) {
		minVariance = Float.MAX_VALUE;
		evalSubregionColor(ia, R0, u, v);
		minVariance = minVariance - 3 * varThreshold;
		for (int[][] Rk : SubRegions) {
			evalSubregionColor(ia, Rk, u, v);
		}
		this.copyPixel(minMean, rgb);
 		return rgb;
 	}
	
	void evalSubregionColor(PixelPack ia, int[][] R, int u, int v) {
		final float[] S1 = new float[3];	// sum of pixel values
		final float[] S2 = new float[3];	// sum of squared pixel values
		int n = 0;
		for (int[] p : R) {
			final float[] cp = ia.getPixel(u + p[0], v + p[1]);
			
			S1[0] = S1[0] + cp[0];
			S1[1] = S1[1] + cp[1];
			S1[2] = S1[2] + cp[2];
			
			S2[0] = S2[0] + sqr(cp[0]);
			S2[1] = S2[1] + sqr(cp[1]);
			S2[2] = S2[2] + sqr(cp[2]);
			
			n = n + 1;		// pixel count in subregion
		}
		// calculate variance for this subregion:
		float var0 = (S2[0] - sqr(S1[0]) / n) / n;	// variance red
		float var1 = (S2[1] - sqr(S1[1]) / n) / n;	// variance green
		float var2 = (S2[2] - sqr(S1[2]) / n) / n;	// variance blue
		// total variance:
		float totalVar = var0 + var1 + var2;	
		if (totalVar < minVariance) {
			minVariance = totalVar;
			minMean[0] = S1[0] / n;
			minMean[1] = S1[1] / n;
			minMean[2] = S1[2] / n;
		}
	}

}
