/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.filters;

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
	 * @param sigma the width of the Gaussian
	 * @return a 1D, odd-sized filter kernel
	 */
	public static float[] makeGaussKernel1D(double sigma) {
		if (sigma < 0) {	// TODO: should check for larger sigma
			throw new IllegalArgumentException("Positive sigma required for Gaussian kernel!");
		}
		final int rad = (int) Math.ceil(3.5 * sigma);
		int size = rad + rad + 1;
		final float[] kernel = new float[size]; // odd size, center cell = kernel[rad]
		final double sigma2 = sigma * sigma;
		final double scale = 1.0 / Math.sqrt(2 * Math.PI * sigma2);
		for (int i = 0; i < kernel.length; i++) {
			double x = rad - i;
			kernel[i] =  (float) (scale * Math.exp(-0.5 * (x * x) / sigma2));
		}
		return kernel;
	}

	/**
	 * Creates and returns a 2D Gaussian filter kernel large enough
	 * to avoid truncation effects. The resulting array is odd-sized.
	 * @param sigma the width of the Gaussian (in both directions)
	 * @return a 2D, odd-sized filter kernel
	 */
	public static float[][] makeGaussKernel2D(double sigma) {
		return  makeGaussKernel2D(sigma, sigma);
//		int rad = (int) Math.ceil(3.5 * sigma);
//		int size = rad + rad + 1;
//		final float[][] kernel = new float[size][size]; //center cell = kernel[rad][rad]
//		final double sigma2 = sigma * sigma;
//		final double scale = 1.0 / (2 * Math.PI * sigma2);
//		double sum = 0;
//		for (int i = 0; i < size; i++) {
//			double x = rad - i;
//			for (int j = 0; j < size; j++) {
//				double y = rad - j;
//				kernel[i][j] = (float) (scale * Math.exp(-0.5 * (x * x + y * y) / sigma2));
//				sum = sum + kernel[i][j];
//			}
//		}
//		
//		// normalize the kernel:
//		for (int i = 0; i < size; i++) {
//			for (int j = 0; j < size; j++) {
//				kernel[i][j] = (float) (kernel[i][j] / sum);
//			}
//		}
//		
//		return kernel;
	}
	
	/**
	 * Creates and returns a 2D Gaussian filter kernel large enough
	 * to avoid truncation effects. The resulting array is odd-sized.
	 * @param sigmaX the width of the Gaussian in x-direction
	 * @param sigmaY the width of the Gaussian in y-direction
	 * @return a 2D, odd-sized filter kernel
	 */
	public static float[][] makeGaussKernel2D(double sigmaX, double sigmaY){
		final int radX = (int) Math.ceil(3.5 * sigmaX);
		final int radY = (int) Math.ceil(3.5 * sigmaY);
		final int sizeX = radX + radX + 1;
		final int sizeY = radY + radY + 1;

		final float[][] kernel = new float[sizeX][sizeY]; //center cell = kernel[rad][rad]
		final double sigmaX2 = (sigmaX > 0.1) ? sigmaX * sigmaX : 0.1;
		final double sigmaY2 = (sigmaY > 0.1) ? sigmaY * sigmaY : 0.1;
		
		double sum = 0;
		for (int i = 0; i < sizeX; i++) {
			double x = radX - i;
			for (int j = 0; j < sizeY; j++) {
				double y = radY - j;
				// IJ.log("x = " + x + " / " + "y = " + y);
				double g = (float) Math.exp(-((x * x) / (2 * sigmaX2) + (y * y) / (2 * sigmaY2)));
				// IJ.log("g = " + g);
				kernel[i][j] = (float) g;
				sum = sum + g;
			}
		}

		// normalize the kernel to sum 1
		double scale = 1.0 / sum;
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				kernel[i][j] = (float) (kernel[i][j] * scale);
			}
		}
		return kernel;
	}

}
