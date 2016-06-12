/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.corners;

/**
 * 2013/06/09 Changed to 'final float' coordinates (WB).
 */
public class Corner implements Comparable<Corner> {
	protected final float x, y, q;

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getQ() {
		return q;
	}
	
	public Corner (float x, float y, float q) {
		this.x = x;
		this.y = y;
		this.q = q;
	}
    
	public int compareTo (Corner c2) {
		//used for sorting corners by corner strength q
		if (this.q > c2.q) return -1;
		if (this.q < c2.q) return 1;
		else return 0;
	}
	
	double dist2 (Corner c2){
		//returns the squared distance between this corner and corner c2
		float dx = this.x - c2.x;
		float dy = this.y - c2.y;
		return (dx * dx) + (dy * dy);	
	}
}
