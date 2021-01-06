/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import ij.IJ;
import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilter;
import imagingbook.lib.filter.linear.GaussianFilterSeparable;
import imagingbook.lib.filter.linear.Kernel2D;
import imagingbook.lib.filter.linear.LinearFilter;
import imagingbook.lib.image.access.PixelPack;
import imagingbook.lib.image.access.PixelPack.PixelSlice;
import imagingbook.lib.math.Eigensolver2x2;
import imagingbook.lib.math.Matrix;

// TODO: convert to subclass of GenericFilter using ImageAccessor (see BilateralFilter)

/**
 * Complete rewrite from scratch
 * 
 * @version 2021/01/06
 */

public class TschumperleDericheFilterGeneric extends GenericFilter {
	
	public static class Parameters {
		/** Number of smoothing iterations */
		public int iterations = 20;	
		/** Adapting time step */
		public double dt = 20.0;  		
		/** Gradient smoothing (sigma of Gaussian) */
		public double sigmaG  = 0.5;
		/** Structure tensor smoothing (sigma of Gaussian) */
		public double sigmaS  = 0.5;	
		/** Diff. limiter along minimal var. (small value = strong smoothing) */
		public float a1 = 0.25f;  		
		/** Diff. limiter along maximal var. (small value = strong smoothing) */
		public float a2 = 0.90f;
		/** Set true to apply the filter in linear RGB (assumes sRGB input) */
		public boolean useLinearRgb = false;
	}
	
	// ---------------------------------------------------------------------------------
	
	private static float INITIAL_ALPHA = 0.5f;
	
	private static final float C1 = (float) (2 - Math.sqrt(2.0)) / 4;
	private static final float C2 = (float) (Math.sqrt(2.0) - 1) / 2;
	
	// TODO: move outside
	private static final float[][] Hdx = // gradient kernel X
		{{-C1, 0, C1},
		 {-C2, 0, C2},
		 {-C1, 0, C1}};
	
	private static final float[][] Hdy = // gradient kernel Y
		{{-C1, -C2, -C1},
		 {  0,   0,   0},
		 { C1,  C2,  C1}};
	
	// ----------------------------------------------------------------------------------
	
	private final Parameters params;
	private final int T;			// number of iterations
	private final int M;	// image width
	private final int N;	// image height
	private final int K;	// number of color channels, k = 0,...,K-1
	private final PixelPack Dx, Dy;
	private Kernel2D kernelDx = new Kernel2D(Hdx, 1, 1, false);
	private Kernel2D kernelDy = new Kernel2D(Hdy, 1, 1, false);
	private final PixelPack G;		// structure matrix as (u,v) with 3 elements
	private final PixelPack A;		// geometry matrix at (u,v) with 3 elements
	private final float[][][] B;
	
//	private float initial_max;
//	private float initial_min;
	
	private float alpha = INITIAL_ALPHA;
	
	// constructor - uses only default settings:
	public TschumperleDericheFilterGeneric(ImageProcessor ip) {
		this(ip, new Parameters());
	}
	
	// constructor - use for setting individual parameters:
	public TschumperleDericheFilterGeneric(ImageProcessor ip, Parameters params) {
		super(PixelPack.fromImageProcessor(ip, null));
		this.params = params;
		this.T = params.iterations;
		this.M = source.getWidth(); 
		this.N = source.getHeight(); 
		this.K = source.getDepth();
		this.Dx = this.source.getEmptyCopy();
		this.Dy = this.source.getEmptyCopy();
		this.G = new PixelPack(M, N, 3, null);	// structure matrix as (u,v) with 3 elements
		this.A = new PixelPack(M, N, 3, null);	// geometry matrix at (u,v) with 3 elements
		
		this.B = new float[K][M][N];	// temporary, eliminate!
//		getImageMinMax();
	}
	
	// ----------------------------------------------------------------------------------
	
	private float maxVelocity = 0;
	
	@Override
	protected void doPass() {
		System.out.println("starting pass " + getPass());
		// Step 1+2: Calculate gradients and smooth
		calculateGradients();
		
		// Step 4 + 5: calculateStructureMatrix G
		calculateStructureMatrix();
		
		System.out.format("%d: before update: alpha=%.2f\n", getPass(), alpha);
		// Step 6-7: calculateGeometryMatrix A
		calculateGeometryMatrix(); // includes the rest now
		 
		// Step 8: calculate max velocity and update the image
//		calculateVelocities();	// updates B and maxVelocity
		
//		this.alpha = (float) params.dt / maxVelocity;
		updateImage();	// uses alpha
		
		this.alpha = (float) params.dt / maxVelocity;
		System.out.format("after update: maxVelocity=%.2f, alpha=%.2f\n", maxVelocity, alpha);
		IJ.log("done with iteration " + this.getPass());
	}
	
	@Override
	protected final boolean finished() {
		return (getPass() >= T);	// this filter needs T passes
	}
	
	// ------------------------------------------------------------
	
	private void calculateGradients() {
		source.copyTo(Dx);	// TODO: make new option 'filter.apply(source, target)' to eliminate intermediate copies
		source.copyTo(Dy);
		new LinearFilter(Dx, kernelDx).apply();
		new LinearFilter(Dy, kernelDy).apply();
		new GaussianFilterSeparable(Dx, params.sigmaG).apply();
		new GaussianFilterSeparable(Dy, params.sigmaG).apply();
	}
	
	private void calculateStructureMatrix() {
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				float[] dx = Dx.getPixel(u, v);
				float[] dy = Dy.getPixel(u, v);
				float a = 0; float b = 0; float c = 0;
				for (int k = 0; k < K; k++) {
					//version 0.2 normalization
					float fx = dx[k]; // Dx[k][u][v];
					float fy = dy[k]; // Dy[k][u][v];
					a += fx * fx;
					b += fx * fy;
					c += fy * fy;
				}
				G.setPixel(u, v, a, b, c);
			}
		}
		new GaussianFilterSeparable(G, params.sigmaS).apply();
	}

	// creates array A
	private void calculateGeometryMatrix() {
		double[] lambda12 = new double[2]; 		// eigenvalues
		double[] e1 = new double[2];			// eigenvector x1
		double a1 = params.a1;
		double a2 = params.a2;
		float maxV = Float.NEGATIVE_INFINITY;	// maximum velocity
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				float[] Guv = G.getPixel(u, v); // 3 elements of local geometry matrix (2x2)
				// calculate the 2 eigenvalues lambda1, lambda2 and the greater eigenvector e1
				getEigenValues2x2(Guv[0], Guv[1], Guv[1], Guv[2], lambda12, e1);
				double arg = 1.0 + lambda12[0] + lambda12[1];	// 1 + lambda_1 + lambda_2
				float c1 = (float) Math.pow(arg, -a1);
				float c2 = (float) Math.pow(arg, -a2);
				
				// calculate eigenvectors:
				normalize(e1);
				float ex = (float) e1[0];
				float ey = (float) e1[1];
				float exx = ex * ex;
				float exy = ex * ey;
				float eyy = ey * ey;
				
				float A0 = c1 * eyy + c2 * exx;
				float A1 = (c2 - c1)* exy;
				float A2 = c1 * exx + c2 * eyy;
				A.setPixel(u, v, A0, A1, A2);		// cool! can make local??
				
				// calculateVelocities() -------------------
				
//				float[] AA = A.getPixel(u, v);
//				final float A0 = AA[0];
//				final float A1 = AA[1];
//				final float A2 = AA[2];	
				
				float[] BB = new float[K];	// local B
				
				final float[] H = new float[3];			// 3 elements of the local Hessian
				for (int k = 0; k < K; k++) {
					getHessian(k, u, v, H);	// Hessian for channel k at pos u,v		// PROBLEM: Hessian depends on source! Write to target??
					final float H0 = H[0]; 
					final float H1 = H[1]; 
					final float H2 = H[2];
					final float vel = A0 * H0 + 2 * A1 * H1 + A2 * H2; // = trace (A*H)
					B[k][u][v] = vel;	// unnecessary
					BB[k] = vel; // B[k][u][v] = vel;
					// find max velocity for time-step adaptation
					maxV = Math.max(maxV, Math.abs(vel));
				}
				
				// updateImage(float alpha) ------------------------
//				if (u == 0 && v == 0) 
//					System.out.format("%d: updating with alpha=%.2f\n", getPass(), alpha);
//				float[] I = source.getPixel(u, v);
//				float[] Inew = new float[I.length];
//				for (int k = 0; k < K; k++) {
//					//float inew = I[k] + alpha * B[k][u][v];	// we use the previous value of alpha
//					float inew = I[k] + alpha * BB[k];	// we use the previous value of alpha
//					Inew[k] = inew;	// I[k][u][v] = inew;
//				}
//				source.setPixel(u, v, Inew);
			}
		}
		this.maxVelocity = maxV;
	}
	
//	private void calculateVelocities() {	// I, A --> B
//		float maxV = Float.MIN_VALUE;
//		final float[] H = new float[3];			// 3 elements of the local Hessian
//		for (int u = 0; u < M; u++) {
//			for (int v = 0; v < N; v++) {
//				
//				float[] AA = A.getPixel(u, v);
//				final float A0 = AA[0];
//				final float A1 = AA[1];
//				final float A2 = AA[2];	
//				
//				for (int k = 0; k < K; k++) {
//					getHessian(k, u, v, H);	// Hessian for channel k at pos u,v		
//					final float H0 = H[0]; 
//					final float H1 = H[1]; 
//					final float H2 = H[2];
//					final float vel = A0 * H0 + 2 * A1 * H1 + A2 * H2; // = trace (A*H)
//					B[k][u][v] = vel;
//					// find max velocity for time-step adaptation
//					maxV = Math.max(maxV, Math.abs(vel));
//				}
//			}
//		}
//		this.maxVelocity = maxV; // Math.max(Math.abs(maxV), Math.abs(minV));
//	}
	
	// Calculate the Hessian matrix Hk for channel k at position (u,v)
	void getHessian(int k, int u, int v, float[] Hk) {
		PixelSlice Ik = source.getSlice(k);
		float icc = Ik.getVal(u, v);
		Hk[0] = Ik.getVal(u-1,v) + Ik.getVal(u+1,v) - 2 * icc;								// = H_xx(u,v)
		Hk[1] = 0.25f * (Ik.getVal(u-1,v-1) + Ik.getVal(u+1,v+1) - Ik.getVal(u-1,v+1) - Ik.getVal(u+1,v-1));	// = H_xy(u,v)
		Hk[2] = Ik.getVal(u,v+1) + Ik.getVal(u,v-1) - 2 * icc;								// = H_yy(u,v)
	}
	
	
	void updateImage() {	// float[][][] I, float[][][] B, double alpha
		System.out.format("%d: updating with alpha=%.2f\n", getPass(), alpha);
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				float[] I = source.getPixel(u, v);
				float[] Inew = new float[I.length];
				for (int k = 0; k < K; k++) {
					float inew = I[k] + alpha * B[k][u][v];
					// clamp image to the original range (brute!)
//					if (inew < initial_min) inew = initial_min;
//					if (inew > initial_max) inew = initial_max;
					Inew[k] = inew;	// I[k][u][v] = inew;
				}
				source.setPixel(u, v, Inew);
			}
		}
	}
	// --------------------------------------------------------------------------
	
	//Eigensolver2x2
	
	private void getEigenValues2x2 (
			double A, double B, double C, double D, 
			double[] lam12, double[] x1) {
		Eigensolver2x2 solver = new Eigensolver2x2(A, B, C, D);
		if (solver.isReal()) {
			copyFromTo(solver.getEigenvalues(), lam12);
			double[][] evecs = solver.getEigenvectors();
			copyFromTo(evecs[0], x1);
			//copyFromTo(evecs[1], x2);
		}
		else {
			throw new RuntimeException("eigenvalues undefined in " + 
					this.getClass().getSimpleName());
		}
	}
	
	private void copyFromTo(double[] a, double[] b) {
		System.arraycopy(a, 0, b, 0, a.length);
	}
	
	
	private void normalize(double[] vec) {
		double norm = Matrix.normL2(vec);
		if (norm > 0.000001) {
			for (int i = 0; i < vec.length; i++) {
				vec[i] = vec[i] / norm;
			}
		}
	}
	
//	private void getImageMinMax() {
//		float max = Float.MIN_VALUE;
//		float min = Float.MAX_VALUE;
//		for (int u = 0; u < M; u++) {
//			for (int v = 0; v < N; v++) {
//				float[] I = source.getPixel(u, v);
//				for (int k = 0; k < K; k++) {
//					float p = I[k];
//					if (p > max) max = p;
//					if (p < min) min = p;
//				}
//			}
//		}
//		initial_max = max;
//		initial_min = min;
//	}

	@Override
	protected void makeTarget() {
		// no target needed
	}
}

