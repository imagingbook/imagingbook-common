/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.corners;

import imagingbook.pub.geometry.basic.Point;

/**
 * This class represents a 2D corner.
 * A corner is essentially a {@link Point} plus a scalar quantity
 * {@link #q} for the corner strength.
 * 
 * @version 2020/10/02
 */
public class Corner implements Point, Comparable<Corner> {
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

	// not needed, since already implemented by a static method in 'Point' 
//	double dist2 (Corner c2){
//		//returns the squared distance between this corner and corner c2
//		float dx = this.x - c2.x;
//		float dy = this.y - c2.y;
//		return (dx * dx) + (dy * dy);	
//	}

	// used for sorting corners by corner strength q
	@Override
	public int compareTo (Corner c2) {
		return Double.compare(c2.q, this.q);
	}
	
	// ----------------------------------------------------------------
	
	// Moved to plugins to make class independent of ImageJ
//	public void draw(ImageProcessor ip, int size) {
//		int x = (int) Math.round(this.getX());
//		int y = (int) Math.round(this.getY());
//		ip.drawLine(x - size, y, x + size, y);
//		ip.drawLine(x, y - size, x, y + size);
//	}
	
}

