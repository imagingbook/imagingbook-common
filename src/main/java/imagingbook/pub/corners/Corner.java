/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.corners;

import java.util.Locale;

import imagingbook.pub.geometry.basic.Pnt2d;


/**
 * This class represents a 2D corner.
 * A corner is essentially a {@link Pnt2d} plus a scalar quantity
 * {@link #q} for the corner strength.
 * 
 * @version 2020/10/02
 */
public class Corner implements Pnt2d, Comparable<Corner> {
	protected final float x, y, q;

	public Corner (float x, float y, float q) {
		this.x = x;
		this.y = y;
		this.q = q;
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getQ() {
		return q;
	}

	// used for sorting corners by corner strength q
	@Override
	public int compareTo (Corner c2) {
		return java.lang.Double.compare(c2.q, this.q);
	}
	
	// ----------------------------------------------------------------
	
	@Override
	public String toString() {
		return String.format(Locale.US, "Corner <%.3f, %.3f, %.3f>", x, y, q);
	}
	
}

