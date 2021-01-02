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

import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilterVector;
import imagingbook.lib.image.access.PixelPack;
import imagingbook.pub.edgepreservingfilters.PeronaMalikFilterScalar.ConductanceFunction;

/**
 * Vector version.
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
public class PeronaMalikFilterVectorColorGrad extends GenericFilterVector {
	
	public static class Parameters extends PeronaMalikFilterScalar.Parameters {
//		/** Specify the color mode */
//		public ColorMode colorMode = ColorMode.SeparateChannels;
		/** Set true to apply the filter in linear RGB (assumes sRGB input) */
		public boolean useLinearRgb = false;
	}
	
	private final Parameters params;
//	private final float alpha;
	private final int T; 		// number of iterations
	private final ConductanceFunction g;
	
	private final int M;		// image width
	private final int N;		// image height

	private float[][][] Ix;			// Ix[c][u][v] = I[c][u+1][v] - I[c][u][v]
	private float[][][] Iy;			// Iy[c][u][v] = I[c][u][v+1] - I[c][u][v]

	private final float[][] Sx;	// color gradient
	private final float[][] Sy;
	
	
	// constructor - using default parameters
	public PeronaMalikFilterVectorColorGrad (ImageProcessor ip) {
		this(ip, new Parameters());
	}
	
	// constructor - use this version to set all parameters
	public PeronaMalikFilterVectorColorGrad (ImageProcessor ip, Parameters params) {
		super(ip, params.obs);
		this.params = params;
//		this.alpha = params.alpha;
		this.M = ip.getWidth();
		this.N = ip.getHeight();
		this.T = params.iterations;
		this.g = (params.smoothRegions) ? g2 : g1;
		
		this.Ix = new float[3][M][N];	// local differences in R,G,B (x-direction)
		this.Iy = new float[3][M][N]; 	// local differences in R,G,B (y-direction)
		
		this.Sx = new float[M][N];		// color gradients
		this.Sy = new float[M][N];
		
	}
	
	// ------------------------------------------------------
	
	@Override
	protected void setupPass(PixelPack source) {
		// recalculate gradients:
		for (int v = 0; v < N; v++) {	
			for (int u = 0; u < M; u++) {
				float Rx = 0, Gx = 0, Bx = 0;	// cleanup!
    			float Ry = 0, Gy = 0, By = 0;
    			float[] pA = source.getPixel(u+1, v);
    			float[] pB = source.getPixel(u, v);
    			if (u < M) {	// remove!
    				Rx = pA[0] - pB[0];
    				Gx = pA[1] - pB[1];
    				Bx = pA[2] - pB[2];
    			}
    			pA = source.getPixel(u, v+1);
    			if (v < N) {	// remove!
    				Ry = pA[0] - pB[0];
    				Gy = pA[1] - pB[1];
    				By = pA[2] - pB[2];
    			}    			
    			Ix[0][u][v] = Rx; Ix[1][u][v] = Gx; Ix[2][u][v] = Bx;
    			Iy[0][u][v] = Ry; Iy[1][u][v] = Gy; Iy[2][u][v] = By;
    			// Di Zenzo color contrast along X/Y-axes
				Sx[u][v] = (float) Math.sqrt(sqr(Rx) + sqr(Gx) + sqr(Bx));
				Sy[u][v] = (float) Math.sqrt(sqr(Ry) + sqr(Gy) + sqr(By));
			}
		}
	}
	
	@Override
	protected float[] filterPixel(PixelPack sources, int u, int v) {
		// color gradients:
		float s0 = Sx[u][v];  
		float s1 = Sy[u][v];  
		float s2 = (u > 0) ? Sx[u - 1][v] : 0;
		float s3 = (v > 0) ? Sy[u][v - 1] : 0;
		// calculate neighborhood conductance
		float c0 = g.eval(s0);
		float c1 = g.eval(s1);
		float c2 = g.eval(s2);
		float c3 = g.eval(s3);
		// update all color channels using the same neighborhood conductance
		float[] I = sources.getPixel(u, v);
		float[] result = new float[3];
		for (int i = 0; i < 3; i++) {
			// differences in color channel i
			float d0 = Ix[i][u][v];
			float d1 = Iy[i][u][v];
			float d2 = (u>0) ? -Ix[i][u-1][v] : 0;			
			float d3 = (v>0) ? -Iy[i][u][v-1] : 0;				
			result[i] = I[i] + params.alpha * (c0*d0 + c1*d1 + c2*d2 + c3*d3);
		}
		return result;
	}

	@Override
	protected final boolean finished() {
		return (getPass() >= T);	// this filter needs T passes
	}
	
	// --------------------------------------------------------------------------------
	
	// ConductanceFunction objects g1, g2 implemented with anonymous classes:
	// TODO: This should go away!!
	
	// = g_K^{(1)} (d)	// for not so smooth regions
	private final ConductanceFunction g1 = new ConductanceFunction() {
		public float eval(float d) {
			float gK = d/params.kappa;
			return (float) Math.exp(-gK*gK);
		}
	};
	
	// = g_K^{(2)} (d)	// for smoother regions
	private final ConductanceFunction g2 = new ConductanceFunction() {
		public float eval(float d) {
			float gK = d / params.kappa;
			return (1.0f / (1.0f + gK*gK));
		}
	};
	
}
