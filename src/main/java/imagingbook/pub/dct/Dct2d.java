/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.dct;
import ij.IJ;
import ij.process.FloatProcessor;

/**
 * This class provides the functionality for calculating the DCT in 2D.
 * @author W. Burger
 * @version 2014-04-13 (changed massively)
 */

public class Dct2d {
	
	public Dct2d() {
	}
	
	public FloatProcessor DCT(FloatProcessor g) {
		float[][] data = g.getFloatArray();	// this is always a duplicate array!
		DCT(data);
		return new FloatProcessor(data);
	}
	
	public void DCT(float[][] data) {
		applyTo(data, true);
	}
	
	public FloatProcessor iDCT(FloatProcessor G) {
		float[][] data = G.getFloatArray();	
		iDCT(data);
		return new FloatProcessor(data);
	}
	
	public void iDCT(float[][] data) {
		applyTo(data, false);
	}
	
	// in-place 2D DCT
	private void applyTo(final float[][] data, final boolean forward) {
		final int width = data.length;
		final int height = data[0].length;
		
		// do the rows:
		double[] row = new double[width];
		Dct1d dct1R = new Dct1d(width);
		for (int v = 0; v < height; v++) {
			IJ.showProgress(v, height);
			getRow(data, v, row);
			if (forward)
				dct1R.DCT(row);
			else
				dct1R.iDCT(row);
			setRow(data, v, row);
		}

		// do the columns:
		double[] col = new double[height];
		Dct1d dct1C = new Dct1d(height);
		for (int u = 0; u < width; u++) {
			IJ.showProgress(u, width);
			getCol(data, u, col);
			if (forward)
				dct1C.DCT(col);
			else
				dct1C.iDCT(col);
			setCol(data, u, col);
		}
	}

	
	private void getRow(float[][] data, int v, double[] row) {
		final int width = data.length;
		for (int u = 0; u < width; u++) {
			row[u] = data[u][v];
		}
	}
	
	private void setRow(float[][] data, int v, double[] row) {
		final int width = data.length;
		for (int u = 0; u < width; u++) {
			data[u][v] = (float) row[u];
		}
	}
	
	private void getCol(float[][] data, int u, double[] column) {
		final int height = data[0].length;
		for (int v = 0; v < height; v++) {
			column[v] = data[u][v];
		}
	}
	
	private void setCol(float[][] data, int u, double[] column) {
		final int height = data[0].length;
		for (int v = 0; v < height; v++) {
			data[u][v] = (float) column[v];
		}
	}

}


