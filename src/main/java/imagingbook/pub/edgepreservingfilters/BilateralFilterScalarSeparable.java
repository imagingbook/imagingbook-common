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
import imagingbook.lib.filter.kernel.GaussianKernel1D;
import imagingbook.lib.image.access.PixelPack.PixelSlice;
import imagingbook.pub.edgepreservingfilters.BilateralFilterScalar.Parameters;

/**
 * Scalar version, applicable to all image types.
 * On color images, this filter is applied separately to each color component.
 * This class implements a bilateral filter as proposed in
 * C. Tomasi and R. Manduchi, "Bilateral Filtering for Gray and Color Images",
 * Proceedings of the 1998 IEEE International Conference on Computer Vision,
 * Bombay, India.
 * The filter uses Gaussian domain and range kernels and can be applied to all 
 * image types.
 * Accepts the same parameters as {@link BilateralFilterScalar}.
 * 
 * @author W. Burger
 * @version 2021/01/01
 */
public class BilateralFilterScalarSeparable extends GenericFilterScalar {
	
	private final float[] Hd;	// the 1D domain kernel
	private final int K;		// the domain kernel size [-K,...,K]
	private final double sigmaR2;
	
	public BilateralFilterScalarSeparable(ImageProcessor ip) {
		this(ip, new Parameters());
	}
	
	public BilateralFilterScalarSeparable(ImageProcessor ip, Parameters params) {
		super(ip, params.obs);
		GaussianKernel1D kernel = new GaussianKernel1D(params.sigmaD);
		this.Hd = kernel.getH();
		this.K = kernel.getXc();
		this.sigmaR2 = params.sigmaR * params.sigmaR;
	}
	
	@Override
	protected float filterPixel(PixelSlice source, int u, int v) {
		switch (getPass()) {
		case 0: return filterPixelX(source, u, v);
		case 1: return filterPixelY(source, u, v);
		default: throw new RuntimeException("invalid pass number " + getPass());
		}
	}
	
	@Override
	protected int passesNeeded() {
		return 2;	// this filter needs 2 passes
	}
	
	// ------------------------------------------------------
	
	// 1D filter in x-direction
	private float filterPixelX(PixelSlice source, int u, int v) {
		float a = source.getVal(u, v);
		float S = 0;
		float W = 0;
		for (int m = -K; m <= K; m++) {
			float b = source.getVal(u + m, v);
			float wd = Hd[m + K];				// domain weight
			float wr = similarityGauss(a, b);	// range weight
			float w = wd * wr;
			S = S + w * b;
			W = W + w;
		}
		return S / W;
	}
	
	// 1D filter in y-direction
	private float filterPixelY(PixelSlice source, int u, int v) {
		float a = source.getVal(u, v);
		float S = 0;
		float W = 0;
		for (int n = -K; n <= K; n++) {
			float b = source.getVal(u, v + n);
			float wd = Hd[n + K];				// domain weight
			float wr = similarityGauss(a, b);	// range weight
			float w = wd * wr;
			S = S + w * b;
			W = W + w;
		}
		return S / W;
	}
	
	// ------------------------------------------------------
	
	// TODO: use common method
	// This returns the weights for a Gaussian range kernel (scalar version):
	private float similarityGauss(float a, float b) {
		double dI = a - b;
		return (float) Math.exp(-(dI * dI) / (2 * sigmaR2));
	}

}
