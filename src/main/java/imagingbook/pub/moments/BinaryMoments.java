/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/

package imagingbook.pub.moments;
import ij.process.ImageProcessor;

public class BinaryMoments {
	
	static final int BACKGROUND = 0;

	public static double moment(ImageProcessor I, int p, int q) {
		double Mpq = 0.0;
		for (int v = 0; v < I.getHeight(); v++) { 
			for (int u = 0; u < I.getWidth(); u++) { 
				if (I.getPixel(u, v) != BACKGROUND) {
					Mpq+= Math.pow(u, p) * Math.pow(v, q);
				}
			}
		}
		return Mpq;
	}
	
	public static double centralMoment(ImageProcessor I, int p, int q) {
		double m00  = moment(I, 0, 0);	// region area
		double xCtr = moment(I, 1, 0) / m00;
		double yCtr = moment(I, 0, 1) / m00;
		double cMpq = 0.0;
		for (int v = 0; v < I.getHeight(); v++) { 
			for (int u = 0; u < I.getWidth(); u++) {
				if (I.getPixel(u, v) != BACKGROUND) { 
					cMpq+= Math.pow(u - xCtr, p) * Math.pow(v - yCtr, q);
				}
			}
		}
		return cMpq;
	}
	
	@Deprecated // renamed to nCentralMoment
	public static double normalCentralMoment(ImageProcessor I, int p, int q) {
		double m00 = moment(I, 0, 0);
		double norm = Math.pow(m00, 0.5 * (p + q + 2));
		return centralMoment(I, p, q) / norm;
	}
	
	public static double nCentralMoment(ImageProcessor I, int p, int q) {
		double m00 = moment(I, 0, 0);
		double norm = Math.pow(m00, 0.5 * (p + q + 2));
		return centralMoment(I, p, q) / norm;
	}
}

