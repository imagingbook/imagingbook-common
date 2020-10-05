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
import imagingbook.pub.corners.subpixel.MaxLocator;
import imagingbook.pub.corners.subpixel.MaxLocator.Method;

/**
 * Abstract super class for all corner detectors based on local 
 * structure tensor.
 * 
 * @author W. Burger
 * @version 2020/10/04
 */
public abstract class GradientCornerDetector {
	
	/** For testing/example calculations only! */
	public static boolean RETAIN_TEMPORARY_DATA = false;
	
	public static class Parameters {
		/** Set true to perform pre-filering on the input image (before gradient calc.) */
		public boolean doPreFilter = true;
		/** Sigma of Gaussian filter used for smoothing gradient maps */
		public double sigma = 1.275;
		/** Width of border region ignored in corner search */
		public int border = 20;
		/** Set true to perform final corner cleanup */
		public boolean doCleanUp = true;
		/** Min. distance between final corners */
		public double dmin = 10;
		/** If/how to perform subpixel localization */
		public Method maxLocatorMethod = Method.None;
	}
	
	protected static final float UndefinedScoreValue = 0;	// to be returned when corner score is undefined
	
	//filter kernels (one-dim. part of separable 2D filters)
	private final static float[] hp = {2f/9, 5f/9, 2f/9};		// pre-smoothing filter kernel
	private final static float[] hd = {0.5f, 0, -0.5f};			// first-derivative kernel
//	private final static float[] hb = {1f/64, 6f/64, 15f/64, 20f/64, 15f/64, 6f/64, 1f/64};	// original gradient blur filter kernel
	
	protected final int M, N;
	protected final Parameters params;
	protected final FloatProcessor Q;
	
	private final MaxLocator maxLocator;
	private final List<Corner> corners;
	
	// retained mainly for debugging
	private FloatProcessor Ixx = null;
	private FloatProcessor Iyy = null;
	private FloatProcessor Ixy = null;
	
	// ---------------------------------------------------------------------------
	
	protected GradientCornerDetector(ImageProcessor ip, Parameters params) {
		this.M = ip.getWidth();
		this.N = ip.getHeight();
		this.params = params;
		this.maxLocator = MaxLocator.create(params.maxLocatorMethod);
		this.Q = makeCornerScores(ip);
		this.corners = makeCorners();
	}
	
	/**
	 * Calculates the corner response score for a single image position (u,v)
	 * from the elements A, B, C of the local structure matrix.
	 * To be implemented by concrete sub-classes.
	 * @see HarrisCornerDetector
	 * @see ShiTomasiDetector
	 * @param A = Ixx(u,v)
	 * @param B = Iyy(u,v)
	 * @param C = Ixy(u,v)
	 * @return the corner score
	 */
	protected abstract float computeCornerScore(float A, float B, float C);
	protected abstract boolean acceptScore(float score);
	
	// -------------------------------------------------------------
	
	private FloatProcessor makeCornerScores(ImageProcessor I) {
		FloatProcessor Ix = I.convertToFloatProcessor(); 
		FloatProcessor Iy = I.convertToFloatProcessor();
		
		// nothing really but a Sobel-like gradient:
		if (params.doPreFilter) {
			Filter.convolveY(Ix, hp);				// pre-filter Ix vertically
			Filter.convolveX(Iy, hp);				// pre-filter Iy horizontally
		}
		
		Filter.convolveX(Ix, hd);				// get first derivative in x
		Filter.convolveY(Iy, hd);				// get first derivative in y
		
		// gradient products:
		Ixx = ImageMath.sqr(Ix);				// Ixx = Ix^2
		Iyy = ImageMath.sqr(Iy);				// Iyy = Iy^2
		Ixy = ImageMath.mult(Ix, Iy);			// Ixy = Ix * Iy
		
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
			q[i] = computeCornerScore(A[i], B[i], C[i]);
		}
		
//		(new ImagePlus("Ixx", Ixx)).show();
//		(new ImagePlus("Iyy", Iyy)).show();
//		(new ImagePlus("Ixy", Ixy)).show();
//		(new ImagePlus("Q", Q)).show();
		
		return Q;
	}
	

	/**
	 * Returns the corner score function as a {@link FloatProcessor} object.
	 * @return the score function
	 */
	public FloatProcessor getQ() {
		return this.Q;
	}
	
	public FloatProcessor getIxx() {
		return this.Ixx;
	}
	
	public FloatProcessor getIyy() {
		return this.Iyy;
	}
	
	public FloatProcessor getIxy() {
		return this.Ixy;
	}
	
	public List<Corner> getCorners() {	
		return Collections.unmodifiableList(corners);
	}
	
	// ----------------------------------------------------------
	
	/*
	 * Returned samples are arranged as follows:
	 * 	s4 s3 s2
	 *  s5 s0 s1
	 *  s6 s7 s8
	 */
	private float[] getNeighborhood(FloatProcessor Q, int u, int v) {
		if (u <= 0 || u >= M - 1 || v <= 0 || v >= N - 1) {
			return null;
		} 
		else {
			final float[] q = (float[]) Q.getPixels();
			float[] s = new float[9];
			final int i0 = (v - 1) * M + u;
			final int i1 = v * M + u;
			final int i2 = (v + 1) * M + u;
			s[0] = q[i1];
			s[1] = q[i1 + 1];
			s[2] = q[i0 + 1];
			s[3] = q[i0];
			s[4] = q[i0 - 1];
			s[5] = q[i1 - 1];
			s[6] = q[i2 - 1];
			s[7] = q[i2];
			s[8] = q[i2 + 1];
			return s;
		}
	}
	
	private boolean isLocalMax(float[] s) {
		if (s == null) {
			return false;
		}
		else {
			final float s0 = s[0];
			return	// check 8 neighbors of q0
					s0 > s[4] && s0 > s[3] && s0 > s[2] &&
					s0 > s[5] &&              s0 > s[1] && 
					s0 > s[6] && s0 > s[7] && s0 > s[8] ;
		}
	}
	
	private List<Corner> makeCorners() {	
		List<Corner> cl = collectCorners();
		if (params.doCleanUp) {
			cl = cleanupCorners(cl);
		}
		return cl;
	}
	
	private List<Corner> collectCorners() {
//		final float tH = (float) params.tH;
		final int border = params.border;
		List<Corner> C = new ArrayList<Corner>();
		for (int v = border; v < N - border; v++) {
			for (int u = border; u < M - border; u++) {
				float[] s = getNeighborhood(Q, u, v);
				if (s != null && acceptScore(s[0]) && isLocalMax(s)) {
					Corner c = makeCorner(u, v, s);
					if (c != null) {
						C.add(makeCorner(u, v, s));
					}
				}
			}
		}
		return C;
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
	
	/**
	 * Creates a new {@link Corner} instance. Performs sub-pixel
	 * position refinement if a {@link #maxLocator} is defined.
	 * @param u the corner's horizontal position (int)
	 * @param v the corner's vertical position (int)
	 * @param s the corner scores in the 3x3 neighborhood
	 * @return a new {@link Corner} instance
	 */
	private Corner makeCorner(int u, int v, float[] s) {
		if (maxLocator == null) {
			// no sub-pixel refinement, use original integer coordinates
			return new Corner(u, v, s[0]);
		}
		else {
			// do sub-pixel refinement
			float[] xyz = maxLocator.locateMaximum(s);
			return (xyz == null) ? null : new Corner(u + xyz[0], v + xyz[1], xyz[2]);
		}
	}
	
}
