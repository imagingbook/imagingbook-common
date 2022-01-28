/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.color.image;

public abstract class ChromaticAdaptation {
	
	protected final double[] W1, W2;

	// actual transformation of color coordinates.
	// XYZ1 are interpreted relative to white point W1.
	// Returns a new color adapted to white point W2.
	public abstract float[] apply(float[] XYZ);
	
	/**
	 * Creates a color adaptation which transforms colors 
	 * relative to white point W1 to corresponding colors relative 
	 * to white point W2.
	 * White points are in XYZ color coordinates.
	 * 
	 * @param W1 white point 1
	 * @param W2 white point 2
	 */
	protected ChromaticAdaptation (double[] W1, double[] W2) {
		if (W1.length != 3 || W2.length != 3) {
			throw new IllegalArgumentException("white point coords must be of length 3");
		}
		this.W1 = W1.clone();
		this.W2 = W2.clone();
	}
	
	/**
	 * Returns the source white point (W1).
	 * @return the source white point
	 */
	public double[] getW1() {
		return W1;
	}
	
	/**
	 * Returns the target white point (W2).
	 * @return the target white point
	 */
	public double[] getW2() {
		return W2;
	}

}
