/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.filters;

import static imagingbook.lib.math.Arithmetic.sqr;

/**
 * This class implements a 2D Gaussian filter, providing the functionality of
 * {@link GenericFilter} and {@link LinearFilter}.
 * It also defines static methods for creating 1D and 2D Gaussian
 * filter kernels for other uses.
 * 
 * @author wilbur
 * @version 2020/02/26
 */
public class GaussianFilter extends LinearFilter {

	public GaussianFilter(double sigma) {
		super(makeGaussKernel2D(sigma));
	}
	
	public GaussianFilter(double sigmaX, double sigmaY) {
		super(makeGaussKernel2D(sigmaX, sigmaY));
	}
	
	/**
	 * Creates and returns a 1D Gaussian filter kernel large enough
	 * to avoid truncation effects. The resulting array is odd-sized.
	 * The kernel is normalized, i.e., the sum of its elements is 1.
	 * @param sigma the width of the Gaussian
	 * @return a 1D, odd-sized filter kernel
	 */
	public static float[] makeGaussKernel1D(double sigma) {
		if (sigma < 0) {	// TODO: should check for larger sigma
			throw new IllegalArgumentException("Positive sigma required for Gaussian kernel!");
		}
		final int rad = (int) Math.ceil(3.5 * sigma);
		final int size = rad + rad + 1;
		final double[] kernelD = new double[size]; // odd size, center cell = kernel[rad]
		final double sigma2 = sqr(sigma);
		//final double scale = 1.0 / Math.sqrt(2 * Math.PI * sigma2);
		
		double sum = 0.0;
		for (int i = 0; i < kernelD.length; i++) {
//			double x = rad - i;
			kernelD[i] = //scale * 		// we dont's scale here but normalize later
					Math.exp(-0.5 * sqr(rad - i) / sigma2);
			sum = sum + kernelD[i];
		}
		
		// normalize the kernel:
		final float[] kernelF = new float[size];
		for (int i = 0; i < kernelD.length; i++) {
				kernelF[i] = (float) (kernelD[i] / sum);
		}
		
		return kernelF;
	}

	/**
	 * Creates and returns a 2D Gaussian filter kernel large enough
	 * to avoid truncation effects. The resulting array is odd-sized.
	 * The kernel is normalized, i.e., the sum of its elements is 1.
	 * 
	 * @param sigma the width of the Gaussian (in both directions)
	 * @return a 2D, odd-sized filter kernel
	 */
	public static float[][] makeGaussKernel2D(double sigma) {
		return  makeGaussKernel2D(sigma, sigma);
	}
	
	/**
	 * Creates and returns a 2D Gaussian filter kernel large enough
	 * to avoid truncation effects. The resulting array is odd-sized.
	 * The kernel is normalized, i.e., the sum of its elements is 1.
	 * 
	 * @param sigmaX the width of the Gaussian in x-direction
	 * @param sigmaY the width of the Gaussian in y-direction
	 * @return a 2D, odd-sized filter kernel
	 */
	public static float[][] makeGaussKernel2D(double sigmaX, double sigmaY){
		final int radX = (int) Math.ceil(3.5 * sigmaX);
		final int radY = (int) Math.ceil(3.5 * sigmaY);
		final int sizeX = radX + radX + 1;
		final int sizeY = radY + radY + 1;

		final double[][] kernelD = new double[sizeX][sizeY]; //center cell = kernel[rad][rad]
		final double sigmaX2 = (sigmaX > 0.1) ? sqr(sigmaX) : 0.1;
		final double sigmaY2 = (sigmaY > 0.1) ? sqr(sigmaY) : 0.1;
		
		double sum = 0.0;
		for (int i = 0; i < sizeX; i++) {
//			final double x = radX - i;
			final double a = sqr(radX - i) / (2 * sigmaX2);
			for (int j = 0; j < sizeY; j++) {
//				final double y = radY - j;
				final double  b = sqr(radY - j) / (2 * sigmaY2);
//				double g = Math.exp(-(sqr(x) / (2 * sigmaX2) + sqr(y) / (2 * sigmaY2)));
				double g = Math.exp(-(a + b));
				kernelD[i][j] = g;
				sum = sum + g;
			}
		}

		// normalize the kernel to sum 1
		final float[][] kernelF = new float[sizeX][sizeY];
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				kernelF[i][j] = (float) (kernelD[i][j] / sum);
			}
		}
		return kernelF;
	}

}
