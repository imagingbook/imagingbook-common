/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.dft;

import imagingbook.lib.math.Matrix;

/**
 * One-dimensional Discrete Fourier Transform (no FFT but using
 * tabulated sine and cosines values).
 * 
 * @version 2019/12/07
 * @deprecated
 */
public abstract class DFT1 {

	final int M;
	final double[] cosTable;
	final double[] sinTable;
	final double s; 			// common scale factor

	private DFT1(int M) {
		this.M = M;
		this.cosTable = makeCosTable();
		this.sinTable = makeSinTable();
		this.s = 1.0 / Math.sqrt(M);
	}

	private double[] makeCosTable() {
		double[] cosTable = new double[M];
		for (int i = 0; i < M; i++) {
			cosTable[i] = Math.cos(2 * Math.PI * i / M);
		}
		return cosTable;
	}

	private double[] makeSinTable() {
		double[] sinTable = new double[M];
		for (int i = 0; i < M; i++) {
			sinTable[i] = Math.sin(2 * Math.PI * i / M);
		}
		return sinTable;
	}

	// -----------------------------------------------------

	public static class Double extends DFT1 {

		private final double[] GRe;
		private final double[] GIm;

		public Double(int M) {
			super(M);
			GRe = new double[M];
			GIm = new double[M];
		}

		/**
		 * Forward DFT applied to a complex-valued input signal (forward = true)
		 * or inverse DFT applied to a complex-values spectrum (forward = false).
		 * The transformation operates "in place", i.e., the two input arrays
		 * are modified.
		 * 
		 * @param gRe	real part of the signal	(must be of length M)
		 * @param gIm	imaginary part of the signal (must be of length M)
		 * @param forward set true for forward transform, false for inverse transform
		 */
		public void applyTo(double[] gRe, double[] gIm, boolean forward) {
			if (M != gRe.length || M != gIm.length) {	// TODO: add more checks!
				throw new IllegalArgumentException(String.format("Dft1d: length of input signal g (%d) must be %d", gRe.length, M));
			}
			for (int u = 0; u < M; u++) {
				double sumRe = 0;
				double sumIm = 0;
				for (int m = 0; m < M; m++) {
					final double re = gRe[m];
					final double im = (gIm == null) ? 0 : gIm[m];
					final int k = (u * m) % M;
					final double cosPhi = cosTable[k];
					final double sinPhi = (forward) ? -sinTable[k] : sinTable[k];
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
	}

	// -----------------------------------------------------

	public static class Float extends DFT1 {

		private final float[] GRe;
		private final float[] GIm;

		public Float(int M) {
			super(M);
			GRe = new float[M];
			GIm = new float[M];
		}

		/**
		 * Forward DFT applied to a complex-valued input signal (forward = true)
		 * or inverse DFT applied to a complex-valued spectrum (forward = false).
		 * The transformation operates "in place", i.e., the two input arrays
		 * are modified.
		 * 
		 * @param gRe	real part of the signal	(must be of length M)
		 * @param gIm	imaginary part of the signal (must be of length M)
		 * @param forward set true for forward transform, false for inverse transform
		 */
		public void applyTo(float[] gRe, float[] gIm, boolean forward) {
			if (M != gRe.length || M != gIm.length) {	// TODO: add more checks!
				throw new IllegalArgumentException(String.format("Dft1d: length of input signal g (%d) must be %d", gRe.length, M));
			}
			for (int u = 0; u < M; u++) {
				double sumRe = 0;
				double sumIm = 0;
				for (int m = 0; m < M; m++) {
					final double re = gRe[m];
					final double im = (gIm == null) ? 0 : gIm[m];
					final int k = (u * m) % M;
					final double cosPhi = cosTable[k];
					final double sinPhi = (forward) ? -sinTable[k] : sinTable[k];
					//complex multiplication: (gRe + i gIm) * (cosPhi + i sinPhi)
					sumRe += re * cosPhi - im * sinPhi;
					sumIm += re * sinPhi + im * cosPhi;
				}
				GRe[u] = (float) (s * sumRe);	
				GIm[u] = (float) (s * sumIm);
			}
			// copy the results back the input arrays
			System.arraycopy(GRe, 0, gRe, 0, gRe.length);
			System.arraycopy(GIm, 0, gIm, 0, gIm.length);
		}
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

		System.out.println("*** Double test *****");

		{
			double[] gRe = { 1, 2, 3, 4, 5, 6, 7, 8 };
			double[] gIm = new double[gRe.length];

			System.out.println("original signal:");
			System.out.println("gRe = " + Matrix.toString(gRe));
			System.out.println("gIm = " + Matrix.toString(gIm));

			DFT1.Double dft1 = new DFT1.Double(gRe.length);
			dft1.applyTo(gRe, gIm, true);

			System.out.println("DFT spectrum:");
			System.out.println("GRe = " + Matrix.toString(gRe));
			System.out.println("GIm = " + Matrix.toString(gIm));

			dft1.applyTo(gRe, gIm, false);

			System.out.println("reconstructed signal:");
			System.out.println("gRe = " + Matrix.toString(gRe));
			System.out.println("gIm = " + Matrix.toString(gIm));
		}

		System.out.println();

		System.out.println("*** Float test *****");
		{
			float[] gRe = { 1, 2, 3, 4, 5, 6, 7, 8 };
			float[] gIm = new float[gRe.length];

			System.out.println("original signal:");
			System.out.println("gRe = " + Matrix.toString(gRe));
			System.out.println("gIm = " + Matrix.toString(gIm));

			DFT1.Float dft1 = new DFT1.Float(gRe.length);
			dft1.applyTo(gRe, gIm, true);

			System.out.println("DFT spectrum:");
			System.out.println("GRe = " + Matrix.toString(gRe));
			System.out.println("GIm = " + Matrix.toString(gIm));

			dft1.applyTo(gRe, gIm, false);

			System.out.println("reconstructed signal:");
			System.out.println("gRe = " + Matrix.toString(gRe));
			System.out.println("gIm = " + Matrix.toString(gIm));
		}
	}

}
