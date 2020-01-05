/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;

import imagingbook.pub.geometry.basic.Point;

/**
 * This class represents a pure 2D translation (as a special case of 
 * affine transformation).
 * It can be assumed that every instance of this class is indeed a translation.
 */
public class Translation2D extends AffineMapping2D {

	/**
	 * Creates a blank (zero) translation.
	 */
	public Translation2D() {
		super();
	}
	
	/**
	 * Creates a new translation mapping.
	 * @param tx translation in x
	 * @param ty translation in y
	 */
	public Translation2D(double tx, double ty){
		super(1, 0, tx, 0, 1, ty);
	}

	/** 
	 * Creates a new translation instance from a given translation.
	 * @param m a translation
	 */
	public Translation2D(Translation2D m) {
		this(m.a02, m.a12);
	}
	
	/**
	 * Creates a new translation from two points.
	 * @param p first point
	 * @param q second point
	 */
	public Translation2D(Point p, Point q) {
		this(q.getX() - p.getX(), q.getY() - p.getY());
	}
	
	// ----------------------------------------------------------

	@Override
	public Translation2D duplicate() {
		return new Translation2D(this);
	}
	
	/**
	 * Concatenates this translation A with another translation B and returns
	 * a new translation C, such that C(x) = B(A(x)).
	 * @param B the second translation
	 * @return the concatenated translations
	 */
	public Translation2D concat(Translation2D B) {
		return new Translation2D(this.a02 + B.a02, this.a12 + B.a12);
	}
	
	@Override
	public Translation2D getInverse() {
		return new Translation2D(-this.a02, -this.a12);
	}


	// Jacobian support -------------------------------------
	
	@Override
	public double[] getParameters() {
		double[] p = new double[] {a02,	a12};
		return p;
	}
	
	@Override
	public Translation2D fromParameters(double[] p) {
		return new Translation2D(p[0], p[1]);
	}
	
	@Override
	public double[][] getJacobian(Point xy) {
		return JT; // this transformation has a constant Jacobian (indep. of xy)
	}
	
	private static final double[][] JT =
		{{1, 0},
		 {0, 1}};
}
