/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import ij.process.ColorProcessor;
import imagingbook.lib.filter.GenericFilterVector;
import imagingbook.lib.image.access.PixelPack;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.ConductanceFunction;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.Parameters;


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
 * @version 2021/01/02
 */	
public class PeronaMalikFilterVectorBrightGrad_OLD extends GenericFilterVector {
	
	private final float alpha;
	private final int T; 		// number of iterations
	private final ConductanceFunction g;
	
	private final int M;		// image width
	private final int N;		// image height

	private final float[][] B;		// B[u][v] (brightness image)
	
	private float[][][] Ix;			// Ix[c][u][v] = I[c][u+1][v] - I[c][u][v]
	private float[][][] Iy;			// Iy[c][u][v] = I[c][u][v+1] - I[c][u][v]
	
	private float[][] Bx = null;	// color gradient
	private float[][] By = null;
	
	// constructor - using default parameters
	public PeronaMalikFilterVectorBrightGrad_OLD (ColorProcessor ip) {
		this(ip, new Parameters());
	}
	
	// constructor - use this version to set all parameters
	public PeronaMalikFilterVectorBrightGrad_OLD (ColorProcessor ip, Parameters params) {
		super(ip, params.obs);
		this.M = ip.getWidth();
		this.N = ip.getHeight();
		this.T = params.iterations;
		this.alpha = params.alpha;
		this.g = ConductanceFunction.get(params.smoothRegions, params.kappa);
		
		this.B  = new float[M][N];		// brightness
		this.Ix = new float[3][M][N];	// local differences in R,G,B (x-direction)
		this.Iy = new float[3][M][N]; 	// local differences in R,G,B (y-direction)
		this.Bx = new float[M][N];		// local differences in brightness  (x-direction)
		this.By = new float[M][N];		// local differences in brightness  (y-direction)
	}
	
	// ------------------------------------------------------
	
	@Override
	protected void setupPass(PixelPack source) {
		// re-calculate local brightness:
		for (int v = 0; v < N; v++) {	
			for (int u = 0; u < M; u++) {
				float[] p = source.getPixel(u, v);
				B[u][v] = getBrightness(p);
			}
		}		
		// re-calculate local color differences and brightness gradient in X and Y direction:
		for (int v = 0; v < N; v++) {	
			for (int u = 0; u < M; u++) {
				float[] pA = source.getPixel(u+1, v);
				float[] pB = source.getPixel(u, v);
				Ix[0][u][v] = pA[0] - pB[0];	// do these need to be recalculated in every pass?
				Ix[1][u][v] = pA[1] - pB[1];
				Ix[2][u][v] = pA[2] - pB[2];
    			Bx[u][v] = (u < M-1) ? B[u+1][v] - B[u][v] : 0;
    			
    			pA = source.getPixel(u, v+1);
    			Iy[0][u][v] = pA[0] - pB[0];
				Iy[1][u][v] = pA[1] - pB[1];
				Iy[2][u][v] = pA[2] - pB[2];   			
    			By[u][v] = (v < N-1) ? B[u][v+1] - B[u][v] : 0;
			}
		}	

	}
	
	@Override
	protected float[] filterPixel(PixelPack sources, int u, int v) {
		// brightness gradients:
		float dw = (u > 0) ? -Bx[u - 1][v] : 0;
		float de = Bx[u][v];
		float dn = (v > 0) ? -By[u][v - 1] : 0;
		float ds = By[u][v];
		// update all color channels
		float[] I = sources.getPixel(u, v);
		float[] result = new float[3];
		for (int k = 0; k < 3; k++) {
			float dWrgb = (u > 0) ? -Ix[k][u - 1][v] : 0;
			float dErgb = Ix[k][u][v];
			float dNrgb = (v > 0) ? -Iy[k][u][v - 1] : 0;
			float dSrgb = Iy[k][u][v];
			result[k] = I[k] +
					alpha * (g.eval(dn) * dNrgb + g.eval(ds) * dSrgb + g.eval(de) * dErgb + g.eval(dw) * dWrgb);
		}
		return result;
	}

	@Override
	protected final boolean finished() {
		return (getPass() >= T);	// this filter needs T passes
	}
	
	private final float getBrightness(float[] p) {
		return 0.299f * p[0] + 0.587f * p[1] + 0.114f * p[2];
	}
	
}
