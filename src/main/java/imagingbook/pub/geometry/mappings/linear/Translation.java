/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;

/**
 * This class represents a pure 2D translation (as a special case of 
 * affine transformation).
 */
public class Translation extends AffineMapping {

	/**
	 * Creates a new translation mapping.
	 * @param tx translation in x
	 * @param ty translation in y
	 */
	public Translation(double tx, double ty){
		super(1, 0, tx, 0, 1, ty);
	}
	
	/**
	 * Creates a blank (zero) translation.
	 */
	public Translation() {
		super();
	}

	/** 
	 * Creates a new translation instance from a given translation.
	 * @param t an existing translation
	 */
	public Translation(Translation t) {
		this(t.getParameters());
	}
	
	public Translation(double[] p) {
		super(1, 0, p[0], 0, 1, p[1]);  // AffineMapping
	}
	
	// --------------------------------------------------------
	
	public double[] getParameters() {
		return new double[] { a02, a12 };
	}
	
	// ----------------------------------------------------------

	@Override
	public Translation getInverse() {
		return new Translation(-this.a02, -this.a12);
	}
	
	@Override
	public Translation duplicate() {
		return new Translation(this);
	}
	
	// Warp parameter support -------------------------------------
	
	@Override
	public int getWarpParameterCount() {
		return 2;
	}
	
	@Override
	public double[] getWarpParameters() {
		double[] p = new double[] {a02,	a12};
		return p;
	}
	
	public static Translation fromWarpParameters(double[] p) {
		return new Translation(p[0], p[1]);
	}
	
	private static final double[][] JT =	// this transformation has a constant Jacobian
		{{1, 0},
		 {0, 1}};
	
	@Override
	public double[][] getWarpJacobian(double[] X) {
		return JT;
	}

}
