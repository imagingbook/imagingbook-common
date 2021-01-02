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
import imagingbook.lib.filter.GenericFilterScalar;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.PixelPack.PixelSlice;

/**
 * Scalar version.
 * This code is based on the Anisotropic Diffusion filter proposed by Perona and Malik,
 * as proposed in Pietro Perona and Jitendra Malik, "Scale-space and edge detection 
 * using anisotropic diffusion", IEEE Transactions on Pattern Analysis 
 * and Machine Intelligence, vol. 12, no. 4, pp. 629-639 (July 1990).
 * 
 * The filter operates on all types of grayscale (scalar) and RGB color images.
 * This class is based on the ImageJ API and intended to be used in ImageJ plugins.
 * How to use: consult the source code of the related ImageJ plugins for examples.
 * 
 * @author W. Burger
 * @version 2020/01/02
 */
public class PeronaMalikFilterScalar extends GenericFilterScalar {
	
	public static class Parameters {
		/** Number of iterations to perform */
		public int iterations = 10;
		/** Update rate (alpha) */
		public float alpha = 0.20f; 			
		/** Smoothness parameter (kappa) */
		public float kappa = 25;
		/** Specify the type of conductivity function c() */
		public boolean smoothRegions = true;
		/** Out-of-bounds strategy */
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NEAREST_BORDER;
	}
	
	private final Parameters params;
	private final int M;		// image width
	private final int N;		// image height
//	private final float alpha;
	private final int T; 		// number of iterations
	private final ConductanceFunction g;
	
	private final float[][] Dx;		// Dx[u][v] = I[u+1][v] - I[u][v]
	private final float[][] Dy;		// Dy[u][v] = I[u][v+1] - I[u][v]
	
	// constructor - using default parameters
	public PeronaMalikFilterScalar (ImageProcessor ip) {
		this(ip, new Parameters());
	}
	
	// constructor - use this version to set all parameters
	public PeronaMalikFilterScalar (ImageProcessor ip, Parameters params) {
		super(ip, params.obs);
		this.M = ip.getWidth();
		this.N = ip.getHeight();
		this.params = params;
//		this.alpha = params.alpha;
		this.T = params.iterations;
		this.g = (params.smoothRegions) ? g2 : g1;
		this.Dx = new float[M][N];
		this.Dy = new float[M][N];	
	}
	
	// ------------------------------------------------------
	
	@Override
	protected void setupPass(PixelSlice source) {
		IJ.log("setupPass " + getPass());
		// re-calculate local gradients in X and Y direction:
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				float v_uv = source.getVal(u,v);
				Dx[u][v] = source.getVal(u + 1, v) - v_uv;
				Dy[u][v] = source.getVal(u, v + 1) - v_uv;
			}
		}
	}
	
	@Override
	protected float filterPixel(PixelSlice source, int u, int v) {
		float d0 = Dx[u][v];
		float d1 = Dy[u][v];
		float d2 = (u > 0) ? -Dx[u - 1][v] : 0;
		float d3 = (v > 0) ? -Dy[u][v - 1] : 0;
		return source.getVal(u, v) +
			params.alpha * (g.eval(d0)*d0 + g.eval(d1)*d1 + g.eval(d2)*d2 + g.eval(d3)*d3);
	}

	@Override
	protected final boolean finished() {
		return (getPass() >= T);	// this filter needs T passes
	}
	
	// ------------------------------------------------------
	
	protected interface ConductanceFunction {
		float eval(float d);
	}
	
	// ConductanceFunction objects g1, g2 implemented with anonymous classes:
	
	// = g_K^{(1)} (d)	// for not so smooth regions
	protected final ConductanceFunction g1 = new ConductanceFunction() {
		public float eval(float d) {
			float gK = d/params.kappa;
			return (float) Math.exp(-gK * gK);
		}
	};
	
	// = g_K^{(2)} (d)	// for smoother regions
	protected final ConductanceFunction g2 = new ConductanceFunction() {
		public float eval(float d) {
			float gK = d / params.kappa;
			return (1.0f / (1.0f + gK * gK));
		}
	};



}
