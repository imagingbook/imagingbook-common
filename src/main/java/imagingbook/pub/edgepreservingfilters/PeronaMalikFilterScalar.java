/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilterScalar;
import imagingbook.lib.image.access.PixelPack.PixelSlice;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.ConductanceFunction;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.Parameters;


/**
 * Scalar version, without gradient array.
 * 
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
 * @version 2021/01/02
 */
public class PeronaMalikFilterScalar extends GenericFilterScalar {
	
//	private final int M;		// image width
//	private final int N;		// image height
	private final int T; 		// number of iterations
	private final float alpha;
	private final ConductanceFunction g;
	
//	private final float[][] Dx;		// Dx[u][v] = I[u+1][v] - I[u][v]
//	private final float[][] Dy;		// Dy[u][v] = I[u][v+1] - I[u][v]
	
	// constructor - using default parameters
	public PeronaMalikFilterScalar (ImageProcessor ip) {
		this(ip, new Parameters());
	}
	
	// constructor - use this version to set all parameters
	public PeronaMalikFilterScalar (ImageProcessor ip, Parameters params) {
		super(ip, params.obs);
//		this.M = ip.getWidth();
//		this.N = ip.getHeight();
		this.T = params.iterations;
		this.alpha = params.alpha;
		this.g = ConductanceFunction.get(params.smoothRegions, params.kappa);
//		this.Dx = new float[M][N];
//		this.Dy = new float[M][N];	
	}
	
	// ------------------------------------------------------
	
//	@Override
//	protected void setupPass(PixelSlice source) {
//		// re-calculate gradients in X and Y direction:
//		for (int u = 0; u < M; u++) {
//			for (int v = 0; v < N; v++) {
//				float I_uv = source.getVal(u,v);
//				Dx[u][v] = source.getVal(u + 1, v) - I_uv;
//				Dy[u][v] = source.getVal(u, v + 1) - I_uv;
//			}
//		}
//	}
	
	@Override
	protected float filterPixel(PixelSlice source, int u, int v) {
//		float d0 = Dx[u][v];
//		float d1 = Dy[u][v];
//		float d2 = (u > 0) ? -Dx[u - 1][v] : 0;
//		float d3 = (v > 0) ? -Dy[u][v - 1] : 0;
		/*   
		 *  NH pixels:      directions:
		 *      I4              d3
		 *   I3 I0 I1        d2 x d0
		 *      I2              d1
		 */
		float[] p = new float[5];
		p[0] = source.getVal(u, v);
		p[1] = source.getVal(u+1, v);
		p[2] = source.getVal(u, v+1);
		p[3] = source.getVal(u-1, v);
		p[4] = source.getVal(u, v-1);
		
		float d0 = p[1] - p[0];		// A:  Dx[u][v] = I[u+1][v] - I[u][v]
		float d1 = p[2] - p[0];		// B:  Dy[u][v] = I[u][v+1] - I[u][v]
		float d2 = p[3] - p[0];		// C: -Dx[u-1][v] = -(I[u][v] - I[u-1][v]) = I[u-1][v] - I[u][v]
		float d3 = p[4] - p[0];		// D: -Dy[u][v-1] = -(I[u][v] - I[u][v-1]) = I[u][v-1] - I[u][v]
		return source.getVal(u, v) +
				alpha * (g.eval(d0) * d0 + g.eval(d1) * d1 + g.eval(d2) * d2 + g.eval(d3) * d3);
	}
	
//	private float getGradientX(PixelSlice source, int u, int v) {
//		return source.getVal(u + 1, v) - source.getVal(u,v);
//	}
//	
//	private float getGradientY(PixelSlice source, int u, int v) {
//		return source.getVal(u, v + 1) - source.getVal(u,v);
//	}
	
	
	@Override
	protected final boolean finished() {
		return (getPass() >= T);	// this filter needs T passes
	}

}
