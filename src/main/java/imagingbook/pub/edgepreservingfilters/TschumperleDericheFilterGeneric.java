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
import ij.process.ColorProcessor;
import imagingbook.lib.filter.GenericFilter;
import imagingbook.lib.filter.linear.GaussianFilterSeparable;
import imagingbook.lib.filter.linear.Kernel2D;
import imagingbook.lib.filter.linear.LinearFilter;
import imagingbook.lib.image.access.PixelPack;
import imagingbook.lib.image.access.PixelPack.PixelSlice;

// TODO: convert to subclass of GenericFilter using ImageAccessor (see BilateralFilter)

/**
 * Complete rewrite from scratch
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
	private final PixelPack G;
	private final PixelPack A;
	private final float[][][] B;
	
	private float initial_max;
	private float initial_min;
	
	
	
	// constructor - uses only default settings:
	public TschumperleDericheFilterGeneric(ColorProcessor ip) {
		this(ip, new Parameters());
	}
	
	// constructor - use for setting individual parameters:
	public TschumperleDericheFilterGeneric(ColorProcessor ip, Parameters params) {
		super(PixelPack.fromImageProcessor(ip, null));
		this.params = params;
		this.T = params.iterations;
		this.M = source.getWidth(); 
		this.N = source.getHeight(); 
		this.K = source.getDepth();
		this.Dx = this.source.getEmptyCopy();
		this.Dy = this.source.getEmptyCopy();
		this.G = new PixelPack(M, N, 3, null);
		this.A = new PixelPack(M, N, 3, null);
		
		this.B = new float[K][M][N];	// temporary, eliminate!
		getImageMinMax();
	}
	
	// ----------------------------------------------------------------------------------
	
	@Override
	protected void doPass() {
		System.out.println("starting pass " + getPass());
		// Step 1+2: Calculate gradients and smooth
		calculateGradients();
		
		// Step 4 + 5: calculateStructureMatrix G
		calculateStructureMatrix();
		
		// Step 6-7: calculateGeometryMatrix A
		calculateGeometryMatrix();
		 
		// Step 8: calculate max velocity and update the image
		float maxVelocity = calculateVelocities();
		double alpha = params.dt / maxVelocity;
		IJ.log("NEW: alpha = " + alpha);
		
		updateImage(alpha);
		IJ.log("done with iteratation " + this.getPass());
	}
	
	@Override
	protected final boolean finished() {
		return (getPass() >= T);	// this filter needs T passes
	}
	
	// ------------------------------------------------------------
	
	private void calculateGradients() {
		source.copyTo(Dx);
		source.copyTo(Dy);
		new LinearFilter(Dx, kernelDx).apply();
		new LinearFilter(Dy, kernelDy).apply();
		new GaussianFilterSeparable(Dx, params.sigmaG).apply();
		new GaussianFilterSeparable(Dy, params.sigmaG).apply();
	}
	
	private void calculateStructureMatrix() {
		G.zero();
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				float[] DDx = Dx.getPixel(u, v);
				float[] DDy = Dy.getPixel(u, v);
				float[] g = G.getPixel(u, v);
				for (int k = 0; k < K; k++) {
					//version 0.2 normalization
					float fx = DDx[k]; // Dx[k][u][v];
					float fy = DDy[k]; // Dy[k][u][v];
					g[0] += fx * fx;
					g[1] += fx * fy;
					g[2] += fy * fy;
				}
				G.setPixel(u, v, g);
			}
		}
		new GaussianFilterSeparable(G, params.sigmaS).apply();
	}

	private void calculateGeometryMatrix() {
		double[] lambda12 = new double[2]; 	// eigenvalues
		double[] e1 = new double[2];			// eigenvectors
		double[] e2 = new double[2];
		double a1 = params.a1;
		double a2 = params.a2;
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				float[] Guv = G.getPixel(u, v);
				final double G0 = Guv[0];	// elements of local geometry matrix (2x2)
				final double G1 = Guv[1];
				final double G2 = Guv[2];
				// calculate eigenvalues:
				if (!realEigenValues2x2(G0, G1, G1, G2, lambda12, e1, e2)) {
					throw new RuntimeException("eigenvalues undefined in " + 
								TschumperleDericheFilter_tmp2.class.getSimpleName());
				}
				final double val1 = lambda12[0];
				final double val2 = lambda12[1];
				final double arg = 1.0 + val1 + val2;
				final float c1 = (float) Math.pow(arg, -a1);
				final float c2 = (float) Math.pow(arg, -a2);
				
				// calculate eigenvectors:
				normalize(e1);
				final float ex = (float) e1[0];
				final float ey = (float) e1[1];
				final float exx = ex * ex;
				final float exy = ex * ey;
				final float eyy = ey * ey;
				
				float A0 = c1 * eyy + c2 * exx;
				float A1 = (c2 - c1)* exy;
				float A2 = c1 * exx + c2 * eyy;
				A.setPixel(u, v, A0, A1, A2);		// cool! can make local??
			}
		}
	}
	
	private float calculateVelocities() {	// I, A --> B
		float maxV = Float.MIN_VALUE;
		float minV = Float.MAX_VALUE;
		final float[] Hkuv = new float[3];
		for (int k = 0; k < K; k++) {
			for (int u = 0; u < M; u++) {
				for (int v = 0; v < N; v++) {
					//calculateHessianMatrix(I[k], u, v, Hkuv);	// TODO!
					getHessian(k, u, v, Hkuv);
					float[] Auv = A.getPixel(u, v);
					final float a = Auv[0];
					final float b = Auv[1];
					final float c = Auv[2];					
					final float ixx = Hkuv[0]; 
					final float ixy = Hkuv[1]; 
					final float iyy = Hkuv[2];
					final float vel = a * ixx + 2 * b * ixy + c * iyy; 
					// find min/max velocity for time-step adaptation
					if (vel > maxV) maxV = vel;
					if (vel < minV) minV = vel;
					B[k][u][v] = vel;
				}
			}
		}
		return Math.max(Math.abs(maxV), Math.abs(minV));
	}
	
	// Calculate the Hessian matrix Hk for a single position (u,v) in image Ik.
	void getHessian(int k, int u, int v, float[] Hk) {
		PixelSlice Ik = source.getSlice(k);
		float icc = Ik.getVal(u, v);
		Hk[0] = Ik.getVal(u-1,v) + Ik.getVal(u+1,v) - 2 * icc;								// = H_xx(u,v)
		Hk[1] = 0.25f * (Ik.getVal(u-1,v-1) + Ik.getVal(u+1,v+1) - Ik.getVal(u-1,v+1) - Ik.getVal(u+1,v-1));	// = H_xy(u,v)
		Hk[2] = Ik.getVal(u,v+1) + Ik.getVal(u,v-1) - 2 * icc;								// = H_yy(u,v)
	}
	
	
	void updateImage(double alpha) {	// float[][][] I, float[][][] B, double alpha
		final float alphaF = (float) alpha;
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				float[] Iuv = source.getPixel(u, v);
				float[] Inew = new float[Iuv.length];
				for (int k = 0; k < K; k++) {
					float inew = Iuv[k] + alphaF * B[k][u][v];
					// clamp image to the original range (brute!)
					if (inew < initial_min) inew = initial_min;
					if (inew > initial_max) inew = initial_max;
					Inew[k] = inew;	// I[k][u][v] = inew;
				}
				source.setPixel(u, v, Inew);
			}
		}
	}
	// --------------------------------------------------------------------------
	
	
	
	private boolean realEigenValues2x2 (
			double A, double B, double C, double D, 
			double[] lam12, double[] x1, double[] x2) {
		final double R = (A + D) / 2;
		final double S = (A - D) / 2;
		final double V = S * S + B * C;
		if (V < 0) 
			return false; // matrix has no real eigenvalues
		else {
			double T = Math.sqrt(V);
			lam12[0] = R + T;	// lambda_1
			lam12[1] = R - T;	// lambda_2
			if ((A - D) >= 0) {
				x1[0] = S + T;	//e_1x
				x1[1] = C;		//e_1y			
				x2[0] = B;		//e_2x
				x2[1] = -S - T;	//e_2y		
			} 
			else {
				x1[0] = B;		//e_1x
				x1[1] = -S + T;	//e_1y	
				x2[0] = S - T;	//e_2x
				x2[1] = C;		//e_2y	
			}
			return true;
		}
	}
	
	private void normalize(double[] vec) {
		double sum = 0;
		for (double v : vec) {
			sum = sum + v * v;
		}
		if (sum > 0.000001) {
			double s = 1 / Math.sqrt(sum);
			for (int i = 0; i < vec.length; i++) {
				vec[i] = vec[i] * s;
			}
		}
	}
	
	private void getImageMinMax() {
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				float[] I = source.getPixel(u, v);
				for (int k = 0; k < K; k++) {
					float p = I[k];
					if (p > max) max = p;
					if (p < min) min = p;
				}
			}
		}
		initial_max = max;
		initial_min = min;
	}

	@Override
	protected void makeTarget() {
		// no target needed
	}
}

