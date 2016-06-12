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
 * This class calculates the 1D DFT using tabulated cosine values.
 * This version is considerably faster than the original without tables.
 * Other optimizations are possible.
 * @author W. Burger
 * @version 2015-07-29
 */
public class Dct1d {
	
	static final double CM0 = 1.0 / Math.sqrt(2);

	final private double s; 		// common scale factor
	final private double[] tmp;		// array to hold temporary data
	final private int M;			// size of the input vector
	final private int N;			// = 4M
	
	/**
	 * This table holds the cosine values cos(PI * (m(2u + 1)) / (2M)) = cos(j * PI / (2M))
	 * for all possible values of j = m (2u + 1), i.e., cosTable[j] =  cos[j * PI / (2M)].
	 * The table is of size N = 4M. To retrieve the correct cosine value for a given index 
	 * pair (m, u) use cosTable[(m * (2 * u + 1)) % (4 * M)].
	 */
	final private double[] cosTable;
	
	public Dct1d(int M) {
		this.M = M;
		this.N = 4 * M;
		this.s = Math.sqrt(2.0 / M); 
		this.tmp = new double[M];
		this.cosTable = makeCosineTable(M);
	}
	
	private double[] makeCosineTable(int M) {
		double[] table = new double[N]; 	// we need a table of size 4*M
		for (int j = 0; j < N; j++) {		// j is equivalent to (m * (2 * u + 1)) % 4M
			double phi = j * Math.PI / (2 * M);
			table[j] = Math.cos(phi);
		}
		return table;
	}

	/**
	 * Performs the 1D forward discrete cosine transform.
	 * Destructively applies the forward Discrete Cosine Transform to 
	 * the argument vector.
	 * @param g the data to be transformed
	 */
	public void DCT(double[] g) {
		if (g.length != M)
			throw new IllegalArgumentException();
		double[] G = tmp;
		for (int m = 0; m < M; m++) {
			double cm = (m == 0) ? CM0 : 1.0;
			double sum = 0;
			for (int u = 0; u < M; u++) {
				sum += g[u] * cm * cosTable[(m * (2 * u + 1)) % N];
			}
			G[m] = s * sum;
		}
		System.arraycopy(G, 0, g, 0, M); // copy G -> g
	}
	
	/**
	 * Performs the 1D inverse discrete cosine transform.
	 * Destructively applies the inverse Discrete Cosine Transform to 
	 * the argument vector.
	 * @param G the data to be transformed
	 */
	public void iDCT(double[] G) {
		if (G.length != M)
			throw new IllegalArgumentException();
		double[] g = tmp;
		for (int u = 0; u < M; u++) {
			double sum = 0;
			for (int m = 0; m < M; m++) {
				double cm = (m == 0) ? CM0 : 1.0;
				sum += cm * G[m] * cosTable[(m * (2 * u + 1)) % N];
			}
			g[u] = s * sum;
		}
		System.arraycopy(g, 0, G, 0, M); // copy g -> G
	}
	
	
	// test example
	public static void main(String[] args) {
		
		double[] data = {1,2,3,4,5,3,0};
		System.out.println("Original data:      " + Matrix.toString(data));
		System.out.println();
		
		Dct1d dct = new Dct1d(data.length);
		dct.DCT(data);
		System.out.println("DCT of data:        " + Matrix.toString(data));
		System.out.println();
		
		dct.iDCT(data);
		System.out.println("Reconstructed data: " + Matrix.toString(data));
		System.out.println();
	}
	
//	Original data:      {1.000, 2.000, 3.000, 4.000, 5.000, 3.000, 0.000}
//	DCT of data:        {6.803, -0.361, -3.728, 1.692, -0.888, -0.083, 0.167}
//	Reconstructed data: {1.000, 2.000, 3.000, 4.000, 5.000, 3.000, -0.000}

	
}
