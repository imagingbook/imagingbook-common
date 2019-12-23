/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.dft;

import java.util.Arrays;

import ij.process.ImageProcessor;

/**
 * Two-dimensional Discrete Fourier Transform (no FFT, but
 * using tabulated sine and cosine values).
 * TODO: needs a complete rewrite!
 * 
 * @version 2019/12/07
 * @deprecated
 */
public abstract class DFT2 {

	//	private boolean swapQu = true;
	//	private int scaleValue = 255;	
	//	private boolean forward = true;

	private DFT2() {
	}

	//------------------------------------------------

	public static class Double {

		/**
		 * 
		 * @param g	2D complex-valued input signal: g[u][v][0] = re, g[u][v][1] = im
		 * @return
		 */
		public void applyTo(double[][] gRe, double[][] gIm, boolean forward) {
			final int width = gRe.length;
			final int height = gRe[0].length;

			// transform each row (in place):
			final double[] rowRe = new double[width];
			final double[] rowIm = new double[width];
			DFT1.Double dft1Row = new DFT1.Double(width);
			for (int v = 0; v < height; v++) {
				getRow(gRe, v, rowRe);
				getRow(gIm, v, rowIm);
				dft1Row.applyTo(rowRe, rowIm, forward);
				putRow(gRe, v, rowRe);
				putRow(gIm, v, rowIm);
			}

			// transform each column (in place):
			final double[] colRe = new double[height];
			final double[] colIm = new double[height];
			DFT1.Double dftCol = new DFT1.Double(height);
			for (int u = 0; u < width; u++) {
				getCol(gRe, u, colRe);
				getCol(gIm, u, colIm);
				dftCol.applyTo(colRe, colIm, forward);
				putCol(gRe, u, colRe);
				putCol(gIm, u, colIm);
			}
		}

		//------------------------------------------------

		// copy the values of row 'v' of 'g' into 'row'
		private void getRow(double[][] g, final int v, double[] row) {
			if (g == null) {
				Arrays.fill(row, 0);
			}
			else {
				for (int u = 0; u < row.length; u++) {
					row[u] = g[u][v];
				}
			}
		}

		void putRow(double[][] g, int v, double[] row) {
			for (int u = 0; u < row.length; u++) {
				g[u][v] = row[u];
			}
		}

		// copy the values of column 'u' of 'g' into 'cols'
		private void getCol(double[][] g, final int u, double[] col) {
			if (g == null) {
				Arrays.fill(col, 0);
			}
			else {
				for (int v = 0; v < col.length; v++) {
					col[v] = g[u][v];
				}
			}
		}

		private void putCol(double[][] g, final int u, double[] col) {
			for (int v = 0; v < col.length; v++) {
				g[u][v] = col[v];
			}
		}
		
		public static double[][] getMagnitude(double[][] re, double[][] im) {
			final int width = re.length;
			final int height = re[0].length;
			double[][] mag = new double[width][height];
			for (int u = 0; u < width; u++) {
				for (int v = 0; v < height; v++) {
					double a = re[u][v];
					double b = im[u][v];
					mag[u][v] = Math.sqrt(a * a + b * b);
				}
			}
			return mag;
		}
	}
	
	//------------------------------------------------

	public static class Float {

		
		public void applyTo(float[][] gRe, float[][] gIm, boolean forward) {
			final int width = gRe.length;
			final int height = gRe[0].length;

			// transform each row (in place):
			final float[] rowRe = new float[width];
			final float[] rowIm = new float[width];
			DFT1.Float dft1Row = new DFT1.Float(width);
			for (int v = 0; v < height; v++) {
				getRow(gRe, v, rowRe);
				getRow(gIm, v, rowIm);
				dft1Row.applyTo(rowRe, rowIm, forward);
				putRow(gRe, v, rowRe);
				putRow(gIm, v, rowIm);
			}

			// transform each column (in place):
			final float[] colRe = new float[height];
			final float[] colIm = new float[height];
			DFT1.Float dftCol = new DFT1.Float(height);
			for (int u = 0; u < width; u++) {
				getCol(gRe, u, colRe);
				getCol(gIm, u, colIm);
				dftCol.applyTo(colRe, colIm, forward);
				putCol(gRe, u, colRe);
				putCol(gIm, u, colIm);
			}
		}

		//------------------------------------------------

		// copy the values of row 'v' of 'g' into 'row'
		private void getRow(float[][] g, final int v, float[] row) {
			if (g == null) {
				Arrays.fill(row, 0);
			}
			else {
				for (int u = 0; u < row.length; u++) {
					row[u] = g[u][v];
				}
			}
		}

		void putRow(float[][] g, int v, float[] row) {
			for (int u = 0; u < row.length; u++) {
				g[u][v] = row[u];
			}
		}

		// copy the values of column 'u' of 'g' into 'cols'
		private void getCol(float[][] g, final int u, float[] col) {
			if (g == null) {
				Arrays.fill(col, 0);
			}
			else {
				for (int v = 0; v < col.length; v++) {
					col[v] = g[u][v];
				}
			}
		}

		private void putCol(float[][] g, final int u, float[] col) {
			for (int v = 0; v < col.length; v++) {
				g[u][v] = col[v];
			}
		}
		
		public static float[][] getMagnitude(float[][] re, float[][] im) {
			final int width = re.length;
			final int height = re[0].length;
			float[][] mag = new float[width][height];
			for (int u = 0; u < width; u++) {
				for (int v = 0; v < height; v++) {
					float a = re[u][v];
					float b = im[u][v];
					mag[u][v] = (float) Math.sqrt(a * a + b * b);
				}
			}
			return mag;
		}
	}
	


	//	----------------------------------------------------


}


