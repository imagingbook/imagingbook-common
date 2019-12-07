/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.dft;

import imagingbook.lib.math.Complex;

/**
 * One-dimensional Discrete Fourier Transform (no FFT but using
 * tabulated sine and cosines values).
 * 
 * @version 2019/12/07
 *
 */
public class Dft1d {

	private final int M;
	private final double[] cosTable;
	private final double[] sinTable;
	
	Dft1d(int M) {
		this.M = M;
		this.cosTable = makeCosTable();
		this.sinTable = makeSinTable();
	}
	
	double[] makeCosTable() {
		double[] cosTable = new double[M];
		for (int i = 0; i < M; i++) {
			cosTable[i] = Math.cos(2 * Math.PI * i / M);
		}
		return cosTable;
	}

	double[] makeSinTable() {
		double[] sinTable = new double[M];
		for (int i = 0; i < M; i++) {
			sinTable[i] = Math.sin(2 * Math.PI * i / M);
		}
		return sinTable;
	}

	/**
	 * Forward/inverse DFT applied to a complex-valued input signal.
	 * @param g complex-valued 1D input signal: g[i][0] = Re, g[i][1] = Im 
	 * @param forward true: forward DFT, false: inverse DFT
	 * @return a complex-valued spectrum (forward) or reconstructed signal (inverse)
	 * @deprecated
	 */
	public Complex[] dft(Complex[] g, boolean forward) {
		if (M != g.length) {
			throw new IllegalArgumentException(String.format("Dft1d: length of input signal g (%d) must be %d", g.length, M));
		}
		Complex[] G = new Complex[M];
		double s = 1.0 / Math.sqrt(M); //common scale factor
		for (int u = 0; u < M; u++) {
			double sumRe = 0;
			double sumIm = 0;
			for (int m = 0; m < M; m++) {
				double gRe = g[m].re;
				double gIm = g[m].im;
				int k = (u * m) % M;
				double cosPhi = cosTable[k];
				double sinPhi = sinTable[k];
				if (forward)
					sinPhi = -sinPhi;
				//complex multiplication: (gRe + i gIm) * (cosPhi + i sinPhi)
				sumRe += gRe * cosPhi - gIm * sinPhi;
				sumIm += gRe * sinPhi + gIm * cosPhi;
			}
			G[u] = new Complex(s * sumRe, s * sumIm);
		}
		return G;
	}
	
	/**
	 * Forward DFT applied to a complex-valued input signal.
	 * @param g	complex-valued 1D input signal: g[i][0] = Re, g[i][1] = Im 
	 * @return a complex-valued spectrum (forward) or reconstructed signal (inverse)
	 */
	public double[][] dft(double[][] g, boolean forward) {
		if (M != g.length) {
			throw new IllegalArgumentException(String.format("Dft1d: length of input signal g (%d) must be %d", g.length, M));
		}
		double[][] G = new double[M][2];
		double s = 1.0 / Math.sqrt(M); //common scale factor
		for (int u = 0; u < M; u++) {
			double sumRe = 0;
			double sumIm = 0;
			for (int m = 0; m < M; m++) {
				double gRe = g[m][0];
				double gIm = g[m][1];
				int k = (u * m) % M;
				double cosPhi = cosTable[k];
				double sinPhi = sinTable[k];
				if (forward)
					sinPhi = -sinPhi;
				//complex multiplication: (gRe + i gIm) * (cosPhi + i sinPhi)
				sumRe += gRe * cosPhi - gIm * sinPhi;
				sumIm += gRe * sinPhi + gIm * cosPhi;
			}
			G[u][0] = s * sumRe;	
			G[u][1] = s * sumIm;
		}
		return G;
	}
	
	/**
	 * Forward/inverse DFT applied to a real-valued input signal.
	 * @param g	real-valued 1D input signal: g[i] = Re, 
	 * @param forward true: forward DFT, false: inverse DFT
	 * @return a complex-valued spectrum (forward) or reconstructed signal (inverse)
	 */
	public double[][] dft(double[] g) {
		if (M != g.length) {
			throw new IllegalArgumentException(String.format("Dft1d: length of input signal g (%d) must be %d", g.length, M));
		}
		final boolean forward = true;
		double[][] G = new double[M][2];
		double s = 1.0 / Math.sqrt(M); //common scale factor
		for (int u = 0; u < M; u++) {
			double sumRe = 0;
			double sumIm = 0;
			for (int m = 0; m < M; m++) {
				double gRe = g[m];
				double gIm = 0;
				int k = (u * m) % M;
				double cosPhi = cosTable[k];
				double sinPhi = sinTable[k];
				if (forward)
					sinPhi = -sinPhi;
				//complex multiplication: (gRe + i gIm) * (cosPhi + i sinPhi)
				sumRe += gRe * cosPhi - gIm * sinPhi;
				sumIm += gRe * sinPhi + gIm * cosPhi;
			}
			G[u][0] = s * sumRe;	
			G[u][1] = s * sumIm;
		}
		return G;
	}
}
