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
import imagingbook.lib.filter.kernel.GaussianKernel2D;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.PixelPack.PixelSlice;

/**
 * Scalar version, applicable to all image types.
 * On color images, this filter is applied separately to each color component.
 * This class implements a bilateral filter as proposed in
 * C. Tomasi and R. Manduchi, "Bilateral Filtering for Gray and Color Images",
 * Proceedings of the 1998 IEEE International Conference on Computer Vision,
 * Bombay, India.
 * The filter uses Gaussian domain and range kernels and can be applied to all 
 * image types.
 * 
 * @author W. Burger
 * @version 2021/01/01
 */
public class BilateralFilterScalar extends GenericFilterScalar {
	
	public static class Parameters {
		/** Sigma (width) of domain filter */
		public double sigmaD = 2; 		
		/** Sigma (width) of range filter */
		public double sigmaR = 50;
		/** Out-of-bounds strategy */
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NEAREST_BORDER;
	}
	
	private final float[][] Hd;	// the domain kernel
	private final int K;		// the domain kernel size [-K,...,K]
	private final double sigmaR2;
	
	
	public BilateralFilterScalar(ImageProcessor ip) {
		this(ip, new Parameters());
	}
	
	public BilateralFilterScalar(ImageProcessor ip, Parameters params) {
		super(ip, params.obs);
		GaussianKernel2D kernel = new GaussianKernel2D(params.sigmaD);
		this.Hd = kernel.getH();
		this.K = kernel.getXc();
		this.sigmaR2 = params.sigmaR * params.sigmaR;
	}
	
	@Override
	protected float filterPixel(PixelSlice source, int u, int v) {
		float S = 0;			// sum of weighted pixel values
		float W = 0;			// sum of weights		
		float a = source.getVal(u, v); // value of the current center pixel
		
		for (int m = -K; m <= K; m++) {
			for (int n = -K; n <= K; n++) {
				float b = source.getVal(u + m, v + n);
				float wd = Hd[m + K][n + K];		// domain weight
				float wr = similarityGauss(a, b);	// range weight
				float w = wd * wr;
				S = S + w * b;
				W = W + w;
			}
		}
		return S / W;
	}
	
	// ------------------------------------------------------
	// This returns the weights for a Gaussian range kernel (scalar version):
	private float similarityGauss(float a, float b) {
		double dI = a - b;
		return (float) Math.exp(-(dI * dI) / (2 * sigmaR2));
	}
	
//	@SuppressWarnings("unused")
//	// for better efficiency: pre-tabulated version of the range kernel - CHECK!
//	private float[] makeRangeKernel(double sigma, int K) {
//		int size = K + 1 + K;
//		float[] rangeKernel = new float[size]; //center cell = kernel[K]
//		double sigma2 = sigma * sigma;
//		for (int i = 0; i < size; i++) {
//			double x = K - i;
//			rangeKernel[i] =  (float) Math.exp(-0.5 * (x*x) / sigma2);
//		}
//		return rangeKernel;
//	}

}