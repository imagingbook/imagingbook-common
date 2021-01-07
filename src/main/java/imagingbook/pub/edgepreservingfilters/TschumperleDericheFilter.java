/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import static imagingbook.pub.edgepreservingfilters.TschumperleDericheF.kernelDx;
import static imagingbook.pub.edgepreservingfilters.TschumperleDericheF.kernelDy;

import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilter;
import imagingbook.lib.filter.GenericFilterScalar;
import imagingbook.lib.filter.linear.GaussianFilterSeparable;
import imagingbook.lib.filter.linear.LinearFilter;
import imagingbook.lib.image.access.PixelPack;
import imagingbook.lib.image.access.PixelPack.PixelSlice;
import imagingbook.lib.math.Eigensolver2x2;
import imagingbook.lib.math.Matrix;

/**
 * Complete rewrite from scratch
 * 
 * @version 2021/01/06
 */

public class TschumperleDericheFilter extends GenericFilter {

	// ----------------------------------------------------------------------------------
	
	private final TschumperleDericheF.Parameters params;
	private int M;				// image width
	private int N;				// image height
	private int K;				// number of color channels (any, but typ. 1 or 3)
	private PixelPack Dx, Dy;
	private GenericFilterScalar filterDx, filterDy;
	private GenericFilterScalar gradientBlurFilter;
	private GenericFilterScalar structureBlurFilter;
	private PixelPack G;		// structure matrix as (u,v) with 3 elements
	
	private int T;		// number of iterations
	private float alpha;
	
	// constructor - uses only default settings:
	public TschumperleDericheFilter(ImageProcessor ip) {
		this(new TschumperleDericheF.Parameters());
	}
	
	// constructor - use for setting individual parameters:
	public TschumperleDericheFilter(TschumperleDericheF.Parameters params) {
		this.params = params;
	}
	
	@Override
	protected void initFilter(PixelPack sourcePack, PixelPack targetPack) {	// called by {@link GenericFilter}
		this.M = this.getWidth(); 
		this.N = this.getHeight(); 
		this.K = this.getDepth();
		
		this.T = params.iterations;
		this.alpha = params.initialAlpha;
		
		this.Dx = new PixelPack(sourcePack, false);	// container for X/Y-derivatives
		this.Dy = new PixelPack(sourcePack, false);
		
		this.filterDx = new LinearFilter(kernelDx);
		this.filterDy = new LinearFilter(kernelDy);
		
		this.gradientBlurFilter = new GaussianFilterSeparable(params.sigmaG);
		this.structureBlurFilter = new GaussianFilterSeparable(params.sigmaS);
		
		this.G = new PixelPack(M, N, 3, null);	// structure matrix as (u,v) with 3 elements
	}
	
	// ----------------------------------------------------------------------------------
	
	@Override
	protected void doPass(PixelPack sourcePack, PixelPack targetPack) {
		makeGradients();							// Step 1
		makeStructureMatrix();						// Step 2
		float maxVelocity = updateVelocities(); 	// Step 3
		alpha = (float) params.dt / maxVelocity;	// Step 4: re-adjust alpha
	}
	
	// ------------------------------------------------------------
	
	private void makeGradients() {
		source.copyTo(Dx);
		filterDx.applyTo(Dx);
		gradientBlurFilter.applyTo(Dx);
		
		source.copyTo(Dy);
		filterDy.applyTo(Dy);
		gradientBlurFilter.applyTo(Dy);
	}
	
	private void makeStructureMatrix() {	// make G
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				final float[] dx = Dx.getPixel(u, v);
				final float[] dy = Dy.getPixel(u, v);
				float a = 0; float b = 0; float c = 0;
				for (int k = 0; k < K; k++) {
					final float fx = dx[k];
					final float fy = dy[k];
					a += fx * fx;
					b += fx * fy;
					c += fy * fy;
				}
				G.setPixel(u, v, a, b, c);
			}
		}
		structureBlurFilter.applyTo(G);
	}

	private float updateVelocities() {
		float maxV = Float.NEGATIVE_INFINITY;	// maximum velocity
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				// calculate the local geometry matrix A(u,v), which has only 3 distinct elements
				float[] A = getGeometryMatrix(u, v);
				float[] B = new float[K];			// local velocities for K channels			
				for (int k = 0; k < K; k++) {
					float[] H = getHessianMatrix(k, u, v); // local Hessian for channel k at pos u,v (3 elements)
					float vel = A[0] * H[0] + 2 * A[1] * H[1] + A[2] * H[2]; // = trace (A*H)
					B[k] = vel;
					maxV = Math.max(maxV, Math.abs(vel)); // find max absolute velocity for time-step adaptation
				}
				
				// update the image (result goes to target)
				float[] p = source.getPixel(u, v);
				for (int k = 0; k < K; k++) {
					p[k] = p[k] + alpha * B[k];	// we use alpha from the previous pass!
				}
				//source.setPixel(u, v, Inew);
				target.setPixel(u, v, p);
			}
		}
		return maxV;
	}
	
	private float[] getGeometryMatrix(int u, int v) {
		double[] lambda12 = new double[2]; 		// eigenvalues
		double[] evec1 = new double[2];			// eigenvector 1
		double a1 = params.a1;
		double a2 = params.a2;
		
		float[] Guv = G.getPixel(u, v); // 3 elements of local geometry matrix (2x2)
		// calculate the 2 eigenvalues lambda1, lambda2 and the greater eigenvector e1
		getEigenValues2x2(Guv[0], Guv[1], Guv[1], Guv[2], lambda12, evec1);
		normalize(evec1);
		
		double arg = 1.0 + lambda12[0] + lambda12[1];	// 1 + lambda_1 + lambda_2
		float c1 = (float) Math.pow(arg, -a1);
		float c2 = (float) Math.pow(arg, -a2);
		
		// mount geometry matrix:
		float ex = (float) evec1[0];
		float ey = (float) evec1[1];
		float exx = ex * ex;
		float exy = ex * ey;
		float eyy = ey * ey;
		
		float A0 = c1 * eyy + c2 * exx;
		float A1 = (c2 - c1)* exy;
		float A2 = c1 * exx + c2 * eyy;
		return new float[] {A0, A1, A2};
	}

	
	// Calculate the Hessian matrix Hk for channel k at position (u,v)
	private float[] getHessianMatrix(int k, int u, int v) {
		float[] Hk = new float[3];
		PixelSlice Ik = source.getSlice(k);
		float icc = Ik.getVal(u, v);
		Hk[0] = Ik.getVal(u-1,v) + Ik.getVal(u+1,v) - 2 * icc;								// = H_xx(u,v)
		Hk[1] = 0.25f * (Ik.getVal(u-1,v-1) + Ik.getVal(u+1,v+1) - Ik.getVal(u-1,v+1) - Ik.getVal(u+1,v-1));	// = H_xy(u,v)
		Hk[2] = Ik.getVal(u,v+1) + Ik.getVal(u,v-1) - 2 * icc;								// = H_yy(u,v)
		return Hk;
	}
	
	// --------------------------------------------------------------------------
	
	private void getEigenValues2x2 (		// TODO: not optimal, revise!
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
	
	private void normalize(double[] vec) {
		double norm = Matrix.normL2(vec);
		if (norm > 0.000001) {
			for (int i = 0; i < vec.length; i++) {
				vec[i] = vec[i] / norm;
			}
		}
	}
	
	private void copyFromTo(double[] a, double[] b) {
		System.arraycopy(a, 0, b, 0, a.length);
	}
	
	// ----------------------------------------------------------------------
	
	@Override
	protected final boolean finished() {
		return (getPass() >= T);	// this filter needs T passes
	}
	
	@Override
	// do some (probably unnecessary) cleanup
	protected void closeFilter() {
		this.Dx = null;
		this.Dy = null;
		this.G = null;
	}


}

