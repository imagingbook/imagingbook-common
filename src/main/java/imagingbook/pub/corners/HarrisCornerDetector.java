/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.corners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.image.Filter;
import imagingbook.lib.image.ImageMath;


/**
 * This class implements the Harris corner detector.
 *
 * @author W. Burger
 *	@version 2015/07/05
 */
public class HarrisCornerDetector {
	

	/**
	 * Default parameters; a (usually modified) instance of this class
	 * may be passed to constructor of the main class.
	 */
	public static class Parameters {
		/** Sensitivity parameter */
		public double alpha = 0.05;
		/** Corner response threshold */
		public double tH = 20000;
		/** Min. distance between final corners */
		public double dmin = 10;
		/** Width of border region ignored in corner search */
		public int border = 20;
		/** Set true to perform final corner cleanup */
		public boolean doCleanUp = true;	
	}
	
	//filter kernels (one-dim. part of separable 2D filters)
	private final static float[] hp = {2f/9, 5f/9, 2f/9};
	private final static float[] hd = {0.5f, 0, -0.5f};
	private final static float[] hb = {1f/64, 6f/64, 15f/64, 20f/64, 15f/64, 6f/64, 1f/64};
	
	private final Parameters params;
	private final int M, N;
	
	private FloatProcessor A;							
	private FloatProcessor B;
	private FloatProcessor C;


	public HarrisCornerDetector(ImageProcessor ip){
		this(ip, new Parameters());
	}
	
	public HarrisCornerDetector(ImageProcessor ip, Parameters params) {
		this.M = ip.getWidth();
		this.N = ip.getHeight();
		this.params = params;
		makeDerivatives(ip);
	}
	
	public List<Corner> findCorners() {
		FloatProcessor Q = makeCrf((float)params.alpha);				//corner response function (CRF)
		List<Corner> corners = collectCorners(Q, (float)params.tH, params.border);
		if (params.doCleanUp) {
			corners = cleanupCorners(corners, params.dmin);
		}
		return corners;
	}
	
	private void makeDerivatives(ImageProcessor I) {
		FloatProcessor Ix = I.convertToFloatProcessor(); 
		FloatProcessor Iy = I.convertToFloatProcessor();
		
		Filter.convolveX(Ix, hp);				// pre-filter Ix horizontally
		Filter.convolveX(Ix, hd);				// get horizontal derivative 
		
		Filter.convolveY(Iy, hp);				// pre-filter Iy vertically
		Filter.convolveY(Iy, hd);				// get vertical derivative
		
		A = ImageMath.sqr(Ix);					// A <- Ix^2
		B = ImageMath.sqr(Iy);					// B <- Iy^2
		C = ImageMath.mult(Ix, Iy);				// C <- Ix * Iy
		
		Filter.convolveXY(A, hb);				// blur A in x/y
		Filter.convolveXY(B, hb);				// blur B in x/y
		Filter.convolveXY(C, hb);				// blur C in x/y
	}
	
	private FloatProcessor makeCrf(float alpha) { //corner response function (CRF)
		FloatProcessor Q = new FloatProcessor(M, N);
		final float[] pA = (float[]) A.getPixels();
		final float[] pB = (float[]) B.getPixels();
		final float[] pC = (float[]) C.getPixels();
		final float[] pQ = (float[]) Q.getPixels();
		for (int i = 0; i < M * N; i++) {
			float a = pA[i], b = pB[i], c = pC[i];
			float det = a * b - c * c;
			float trace = a + b;
			pQ[i] = det - alpha * (trace * trace);
		}
		return Q;
	}
	
	private List<Corner> collectCorners(FloatProcessor Q, float tH, int border) {
		List<Corner> C = new ArrayList<Corner>();
		for (int v = border; v < N - border; v++) {
			for (int u = border; u < M - border; u++) {
				float q = Q.getf(u, v);
				if (q > tH && isLocalMax(Q, u, v)) {
					Corner c = new Corner(u, v, q);
					C.add(c);
				}
			}
		}
		return C;
	}
	
	// uses 8-neighborhood
	private boolean isLocalMax (FloatProcessor Q, int u, int v) {
		if (u <= 0 || u >= M - 1 || v <= 0 || v >= N - 1) {
			return false;
		} 
		else {
			final float[] q = (float[]) Q.getPixels();
			final int i0 = (v - 1) * M + u;
			final int i1 = v * M + u;
			final int i2 = (v + 1) * M + u;
			final float q0 = q[i1];
			return	// check 8 neighbors of q0
				q0 >= q[i0 - 1] && q0 >= q[i0] && q0 >= q[i0 + 1] &&
				q0 >= q[i1 - 1] &&                q0 >= q[i1 + 1] && 
				q0 >= q[i2 - 1] && q0 >= q[i2] && q0 >= q[i2 + 1] ;
		}
	}
	
	private List<Corner> cleanupCorners(List<Corner> C, double dmin){
		final double dmin2 = dmin * dmin;
		// sort corners by descending q-value:
		Collections.sort(C);
		// we use an array of corners for efficiency reasons:
		Corner[] Ca = C.toArray(new Corner[C.size()]);
		List<Corner> Cclean = new ArrayList<Corner>(C.size());
		for (int i = 0; i < Ca.length; i++) {
			Corner c0 = Ca[i];		// get next strongest corner
			if (c0 != null) {
				Cclean.add(c0);
				// delete all remaining corners cj too close to c0:
				for (int j = i + 1; j < Ca.length; j++) {
					Corner cj = Ca[j];
					if (cj != null && c0.dist2(cj) < dmin2)
						Ca[j] = null;   //delete corner cj from C
				}
			}
		}
		return Cclean;
	}

}
