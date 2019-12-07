/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.dft;

import java.util.AbstractMap;
import java.util.Arrays;

import imagingbook.lib.math.Complex;
import imagingbook.lib.math.Matrix;

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
	
	private final double[] GRe;
	private final double[] GIm;
	
	Dft1d(int M) {
		this.M = M;
		this.cosTable = makeCosTable();
		this.sinTable = makeSinTable();
		GRe = new double[M];
		GIm = new double[M];
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
	 * Forward DFT applied to a complex-valued input signal (forward = true)
	 * or inverse DFT applied to a complex-values spectrum (forward = false).
	 * The transformation operates "in place", i.e., the two input arrays
	 * are modified.
	 * 
	 * @param gRe	real part of the signal	(must be of length M)
	 * @param gIm	imaginary part of the signal (must be of length M)
	 * @param forward
	 */
	public void dft(double[] gRe, double[] gIm, boolean forward) {
		if (M != gRe.length || M != gIm.length) {	// TODO: add more checks!
			throw new IllegalArgumentException(String.format("Dft1d: length of input signal g (%d) must be %d", gRe.length, M));
		}
		double s = 1.0 / Math.sqrt(M); //common scale factor
		for (int u = 0; u < M; u++) {
			double sumRe = 0;
			double sumIm = 0;
			for (int m = 0; m < M; m++) {
				double re = gRe[m];
				double im = (gIm == null) ? 0 : gIm[m];
				int k = (u * m) % M;
				double cosPhi = cosTable[k];
				double sinPhi = sinTable[k];
				if (forward)
					sinPhi = -sinPhi;
				//complex multiplication: (gRe + i gIm) * (cosPhi + i sinPhi)
				sumRe += re * cosPhi - im * sinPhi;
				sumIm += re * sinPhi + im * cosPhi;
			}
			GRe[u] = s * sumRe;	
			GIm[u] = s * sumIm;
		}
		// copy the results back the input arrays
		System.arraycopy(GRe, 0, gRe, 0, gRe.length);
		System.arraycopy(GIm, 0, gIm, 0, gIm.length);
	}
	
	
	// ------------------------------------------------------------------------------
	
	/*
	 * Direct implementation of the one-dimensional DFT for arbitrary signal lengths.
	 * This DFT uses the same definition as Mathematica. Example:
	 * Fourier[{1, 2, 3, 4, 5, 6, 7, 8}, FourierParameters = {0, -1}]:
		{12.7279 + 0. i, 
		-1.41421 + 3.41421 i, 
		-1.41421 + 1.41421 i, 
		-1.41421 + 0.585786 i, 
		-1.41421 + 0. i, 
		-1.41421 - 0.585786 i, 
		-1.41421 - 1.41421 i, 
		-1.41421 - 3.41421 i}
	*/
	
	//test example
	public static void main(String[] args) {
		double[] gRe = { 1, 2, 3, 4, 5, 6, 7, 8 };
		double[] gIm = new double[gRe.length];
		
		System.out.println("original signal:");
		System.out.println("gRe = " + Matrix.toString(gRe));
		System.out.println("gIm = " + Matrix.toString(gIm));
		
		Dft1d dft1 = new Dft1d(gRe.length);
		dft1.dft(gRe, gIm, true);
		
		System.out.println("DFT spectrum:");
		System.out.println("GRe = " + Matrix.toString(gRe));
		System.out.println("GIm = " + Matrix.toString(gIm));
		
		dft1.dft(gRe, gIm, false);
		
		System.out.println("reconstructed signal:");
		System.out.println("gRe = " + Matrix.toString(gRe));
		System.out.println("gIm = " + Matrix.toString(gIm));

		
	}
	
	
	
}
