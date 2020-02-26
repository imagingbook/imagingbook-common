/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.corners;

import static imagingbook.lib.math.Arithmetic.sqr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.filters.GaussianFilter;
import imagingbook.lib.image.Filter;
import imagingbook.lib.image.ImageMath;

/**
 * Abstract super class for all corner detectors based on local 
 * structure tensor.
 * 
 * @author W. Burger
 * @version 2020/02/25
 */
public abstract class AbstractGradientCornerDetector implements PointFeatureDetector {
	
	/** For testing/example calculations only! */
	public static boolean RETAIN_TEMPORARY_DATA = false;
	
	protected static class Parameters {
		/** Sigma of Gaussian filter used for smoothing gradient maps */
		public double sigma = 1.275;
		/** Corner response threshold */
		public double tH = 20000;
		/** Width of border region ignored in corner search */
		public int border = 20;
		/** Set true to perform final corner cleanup */
		public boolean doCleanUp = true;
		/** Min. distance between final corners */
		public double dmin = 10;
	}
	
	//filter kernels (one-dim. part of separable 2D filters)
	private final static float[] hp = {2f/9, 5f/9, 2f/9};	// pre-smoothing filter kernel
	private final static float[] hd = {0.5f, 0, -0.5f};		// first derivative kernel
//	private final static float[] hb = {1f/64, 6f/64, 15f/64, 20f/64, 15f/64, 6f/64, 1f/64};	// original gradient blur filter kernel
		
	protected final int M, N;
	protected final Parameters params;
	protected final FloatProcessor Q;
	
	// for testing only - REMOVE!
	public FloatProcessor Ixx = null;
	public FloatProcessor Iyy = null;
	public FloatProcessor Ixy = null;
	
	// ---------------------------------------------------------------------------
	
	protected AbstractGradientCornerDetector(ImageProcessor ip, Parameters params) {
		this.M = ip.getWidth();
		this.N = ip.getHeight();
		this.params = params;
		init();
		Q = makeCornerScores(ip);
	}
	
	/**
	 * To perform any necessary initializations in subclasses.
	 * Called by the constructor.
	 */
	protected abstract void init();	
	
	/**
	 * Calculates the corner response score for a single image position (u,v)
	 * from the elements A, B, C of the local structure matrix.
	 * To be implemented by concrete sub-classes.
	 * @param A = Ixx(u,v)
	 * @param B = Iyy(u,v)
	 * @param C = Iyy(u,v)
	 * @return the corner score
	 */
	public abstract float computeScore(float A, float B, float C);
	
	private FloatProcessor makeCornerScores(ImageProcessor I) {
		FloatProcessor Ix = I.convertToFloatProcessor(); 
		FloatProcessor Iy = I.convertToFloatProcessor();
		
		// nothing but a Sobel gradient estimate:
		Filter.convolveY(Ix, hp);				// pre-filter Ix vertically
		Filter.convolveX(Iy, hp);				// pre-filter Iy horizontally
		Filter.convolveX(Ix, hd);				// get horizontal derivative 
		Filter.convolveY(Iy, hd);				// get vertical derivative
		
		// gradient products:
		FloatProcessor Ixx = ImageMath.sqr(Ix);				// Ixx = Ix^2
		FloatProcessor Iyy = ImageMath.sqr(Iy);				// Iyy = Iy^2
		FloatProcessor Ixy = ImageMath.mult(Ix, Iy);		// Ixy = Ix * Iy
		
		// blur the gradient components with a small Gaussian:
		float[] gaussKernel = GaussianFilter.makeGaussKernel1D(params.sigma);
		Filter.convolveXY(Ixx, gaussKernel);
		Filter.convolveXY(Iyy, gaussKernel);
		Filter.convolveXY(Ixy, gaussKernel);
		
		FloatProcessor Q = new FloatProcessor(M, N);
		
		final float[] A = (float[]) Ixx.getPixels();
		final float[] B = (float[]) Iyy.getPixels();
		final float[] C = (float[]) Ixy.getPixels();
		final float[] q = (float[]) Q.getPixels();
		
		for (int i = 0; i < M * N; i++) {
			q[i] = computeScore(A[i], B[i], C[i]);
		}

		// for demo examples only - REMOVE!
		if (RETAIN_TEMPORARY_DATA) {
			this.Ixx = Ixx;
			this.Iyy = Iyy;
			this.Ixy = Ixy;
		}
		
		return Q;
	}
	
	/**
	 * Returns the corner score function as a {@link FloatProcessor} object.
	 * @return the score function
	 */
	public FloatProcessor getQ() {
		return Q;
	}
	
	@Override
	public List<Corner> getPoints() {	
		List<Corner> corners = collectCorners();
		if (params.doCleanUp) {
			corners = cleanupCorners(corners);
		}
		return corners;
	}
	
	public List<Corner> getCorners() {
		return getPoints();
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
	
	private List<Corner> cleanupCorners(List<Corner> C) {
		final double dmin2 = sqr(params.dmin);
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
					if (cj != null && c0.distance2(cj) < dmin2)
						Ca[j] = null;   //delete corner cj from C
				}
			}
		}
		return Cclean;
	}
	
	private List<Corner> collectCorners() {
		final float tH = (float) params.tH;
		final int border = params.border;
		List<Corner> C = new ArrayList<Corner>();
		for (int v = border; v < N - border; v++) {
			for (int u = border; u < M - border; u++) {
				float q = Q.getf(u, v);
				if (q > tH && isLocalMax(Q, u, v)) {	// 
					C.add(new Corner(u, v, q));
				}
			}
		}
		return C;
	}
	
}
