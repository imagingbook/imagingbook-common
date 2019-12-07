/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.dft;

import ij.process.*;
import imagingbook.lib.math.Complex;
import imagingbook.pub.dft.Dft1d;

//TODO: clean up creation of G (made instances of Complex immutable)!

/**
 * Two-dimensional Discrete Fourier Transform (no FFT, but
 * using tabulated sine and cosine values).
 * TODO: needs a complete rewrite!
 * 
 * @version 2019/12/07
 *
 */
public class Dft2d {
	
	private final int width, height;
	
	private final double[][] row;	// complex-valued image row
	private final double[][] col;	// complex-valued image column
	
	private final Dft1d dftR;
	private final Dft1d dftC;
	
	private float[] Real;	//original image data
	private float[] Imag;
	private float[] Power;
	private float PowerMax;
	private boolean swapQu = true;
	private int scaleValue = 255;	
	private boolean forward = true;
	
	public Dft2d(int width, int height) {
		this.width = width;
		this.height = height;
		this.row = new double[width][2];
		this.col = new double[height][2];
		this.dftR = new Dft1d(width);
		this.dftC = new Dft1d(height);
	}
	
	//------------------------------------------------
	
	/**
	 * 
	 * @param g	2D complex-valued input signal: g[u][v][0] = re, g[u][v][1] = im
	 * @return
	 */
	public double[][][] dft(double[][][] g) {
		if (width != g.length || height != g[0].length) {
			throw new IllegalArgumentException(String.format("Dft2d: size of input signal g (%d,%d) must be (%d,%d)", 
					g.length, g[0].length, width, height));
		}
		// do the rows:
		//Complex[] row = makeComplexVector(width);
		//Dft1d dftR = new Dft1d(width);
		for (int v = 0; v < height; v++) {
			getRow(g, v, row);
			dftR.dft(row, forward);
			putRow(g, v, row);
		}
		// do the columns:
		//Complex[] col = makeComplexVector(height);
		//Dft1d dftC = new Dft1d(height);
		for (int u = 0; u < width; u++) {
			getCol(g, u, col);
			dftR.dft(col, forward);
			putCol(g, u, col);
		}
		return null;
	}
	
	// copy the values of row 'v' of 'g' into 'row'
	private void getRow(double[][][] g, final int v, double[][] row) {
		for (int u = 0; u < width; u++) {
			row[u][0] = g[u][v][0];
			row[u][1] = g[u][v][1];
		}
	}
	
	void putRow(double[][][] g, int v, double[][] row) {
		for (int u = 0; u < width; u++) {
			g[u][v][0] = row[u][0];
			g[u][v][1] = row[u][1];
		}
	}
	
	// copy the values of column 'u' of 'g' into 'cols'
	private void getCol(double[][][] g, final int u, double[][] col) {
		for (int v = 0; v < height; v++) {
			col[v][0] = g[u][v][0];
			col[v][1] = g[u][v][1];
		}
	}
	
	private void putCol(double[][][] g, final int u, double[][] col) {
		for (int v = 0; v < height; v++) {
			g[u][v][0] = col[v][0];
			g[u][v][1] = col[v][1];
		}
	}
	
	
	//------------------------------------------------
	//------------------------------------------------
	
	@Deprecated
	public Dft2d(FloatProcessor ip) {
		this(ip.getWidth(), ip.getHeight());
		Real = (float[]) ip.getPixels();
		Imag = new float[width*height];  // values are zero
		doDft2d();
		makePowerSpectrum();
	}
	
	@Deprecated
	public Dft2d(FloatProcessor ip, boolean center){
		this(ip);
		swapQu = center;
	}
	
	//------------------------------------------------
	
	@Deprecated
	public float[] getReal() {
		return Real;
	}
	
	@Deprecated
	public float[] getImag(){
		return Imag;
	}
	
	@Deprecated
	public float[] getPower(){
		return Power;
	}
	
	//------------------------------------------------
	
	@Deprecated
	public void doDft2d() { // in-place 2D Dft
		// do the rows:
		Complex[] row = makeComplexVector(width);
		//Dft1d dftR = new Dft1d(width);
		for (int v = 0; v < height; v++) {
			getRow(v, row);
			Complex[] rowDft = dftR.dft(row, forward);
			putRow(v, rowDft);
		}
		// do the columns:
		Complex[] col = makeComplexVector(height);
		//Dft1d dftC = new Dft1d(height);
		for (int u = 0; u < width; u++) {
			getCol(u, col);
			Complex[] colDft = dftC.dft(col, forward);
			putCol(u, colDft);
		}
	}
	
	//------------------------------------------------
	
	@Deprecated
	private void getRow(int v, Complex[] rowC) {
		int i = v * width; // start index of row v
		for (int u = 0; u < width; u++) {
//			rowC[u].re = Real[i+u];
//			rowC[u].im = Imag[i+u];
			rowC[u] = new Complex(Real[i + u], Imag[i + u]);
		}
	}
	
	@Deprecated
	void putRow(int v, Complex[] rowC) {
		int i = v * width; // start index of row v
		for (int u = 0; u < width; u++) {
			Real[i + u] = (float) rowC[u].re;
			Imag[i + u] = (float) rowC[u].im;
		}
	}
	
	@Deprecated
	void getCol(int u, Complex[] rowC) {
		for (int v = 0; v < height; v++) {
//			rowC[v].re = Real[v*width+u];
//			rowC[v].im = Imag[v*width+u];
			rowC[v] = new Complex(Real[v * width + u], Imag[v * width + u]);
		}
	}
	
	@Deprecated
	void putCol(int u, Complex[] rowC) {
		for (int v = 0; v < height; v++) {
			Real[v * width + u] = (float) rowC[v].re;
			Imag[v * width + u] = (float) rowC[v].im;
		}
	}
	
	Complex[] makeComplexVector(int M) {
		Complex[] g = new Complex[M];
		for (int i = 0; i < M; i++) {
			g[i] = new Complex(0, 0);
		}
		return g;
	}

	
	//----------------------------------------------------
	
	void makePowerSpectrum() {
		// computes the power spectrum
		Power = new float[Real.length];
		PowerMax = 0.0f;
		for (int i = 0; i < Real.length; i++) {
			double a = Real[i];
			double b = Imag[i];
			float p = (float) Math.sqrt(a * a + b * b);
			Power[i] = p;
			if (p > PowerMax)
				PowerMax = p;
		}
	}
	
	public ByteProcessor makePowerImage() {
		ByteProcessor ip = new ByteProcessor(width, height);
		byte[] pixels = (byte[]) ip.getPixels();
		// PowerMax must be set
		double max = Math.log(PowerMax + 1.0);
		double scale = 1.0;
		if (scaleValue > 0)
			scale = scaleValue / max;
		for (int i = 0; i < pixels.length; i++) {
			double p = Power[i];
			if (p < 0)
				p = -p;
			double plog = Math.log(p + 1.0);
			int pint = (int) (plog * scale);
			pixels[i] = (byte) (0xFF & pint);
		}
		if (swapQu)
			swapQuadrants(ip);
		return ip;
	}

	//	----------------------------------------------------

	public void swapQuadrants(ImageProcessor ip) {
		// swap quadrants Q1 <-> Q3, Q2 <-> Q4
		// Q2 Q1
		// Q3 Q4
		ImageProcessor t1, t2;
		int w = ip.getWidth();
		int h = ip.getHeight();
		int w2 = w / 2;
		int h2 = h / 2;

		ip.setRoi(w2, 0, w - w2, h2); // Q1
		t1 = ip.crop();

		ip.setRoi(0, h2, w2, h - h2); // Q3
		t2 = ip.crop();

		ip.insert(t1, 0, h2); // swap Q1 <-> Q3
		ip.insert(t2, w2, 0);

		ip.setRoi(0, 0, w2, h2); // Q2
		t1 = ip.crop();

		ip.setRoi(w2, h2, w - w2, h - h2); // Q4
		t2 = ip.crop();

		ip.insert(t1, w2, h2);
		ip.insert(t2, 0, 0);
	}

}


