/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.dct;

import imagingbook.lib.math.Matrix;

/**
 * This class calculates the 1D DFT.
 * Explicit (slow) version for that does not use pre-calculated cosine tables.
 * 
 * @author W. Burger
 * @version 2014-04-13 (changed method signatures to operate destructively on the input arrays)
 *
 */
public class Dct1d_Slow {
	
	static final double CM0 = 1.0 / Math.sqrt(2);

	final private double[] tmp;
	final private int M;
	
	public Dct1d_Slow(int M) {
		this.M = M;
		this.tmp = new double[M];
	}
	
	/**
	 * Destructively applies the forward Discrete Cosine Transform to argument vector.
	 * @param g the data to be transformed
	 */
	public void DCT(double[] g) {
		if (g.length != M)
			throw new IllegalArgumentException();
		final double s = Math.sqrt(2.0 / M); 
		double[] G = tmp;
		for (int m = 0; m < M; m++) {
			double cm = (m == 0) ? CM0 : 1.0;
			double sum = 0;
			for (int u = 0; u < M; u++) {
				double Phi = Math.PI * m * (2 * u + 1) / (2 * M);
				sum += g[u] * cm * Math.cos(Phi);
			}
			G[m] = s * sum;
		}
		System.arraycopy(G, 0, g, 0, M); // copy G -> g
	}
	
	/**
	 * Destructively applies the inverse Discrete Cosine Transform to argument vector.
	 * @param G the data to be transformed
	 */
	public void iDCT(double[] G) {
		if (G.length != M)
			throw new IllegalArgumentException();
		final double s = Math.sqrt(2.0 / M); //common scale factor
		double[] g = tmp;
		for (int u = 0; u < M; u++) {
			double sum = 0;
			for (int m = 0; m < M; m++) {
				double cm = (m == 0) ? CM0 : 1.0;
				double Phi = Math.PI * m * (2 * u + 1) / (2 * M);
				sum += G[m] * cm * Math.cos(Phi);
			}
			g[u] = s * sum;
		}
		System.arraycopy(g, 0, G, 0, M); // copy g -> G
	}
	
	
	// test example
	public static void main(String[] args) {
		
		double[] data = {1,2,3,4,5,3,0};
		System.out.println("Original data:      " + Matrix.toString(data));
		
		Dct1d_Slow dct = new Dct1d_Slow(data.length);
		dct.DCT(data);
		System.out.println("DCT of data:        " + Matrix.toString(data));
		
		dct.iDCT(data);
		System.out.println("Reconstructed data: " + Matrix.toString(data));
	}
	
//	Original data:      {1.000, 2.000, 3.000, 4.000, 5.000, 3.000, 0.000}
//	DCT of data:        {6.803, -0.361, -3.728, 1.692, -0.888, -0.083, 0.167}
//	Reconstructed data: {1.000, 2.000, 3.000, 4.000, 5.000, 3.000, -0.000}

}
