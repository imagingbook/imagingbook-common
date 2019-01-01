/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;

public class Translation extends AffineMapping {

	/**
	 * Creates a new translation mapping.
	 * @param tx translation in x
	 * @param ty translation in y
	 */
	public Translation(double tx, double ty){
		super();
		a02 = tx;
		a12 = ty;
	}

	public Translation(Translation t) {
		super();
		this.a02 = t.a02;
		this.a12 = t.a12;
	}

	public Translation invert() {
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

	@Override
	public void setWarpParameters(double[] p) {
		a02 = p[0];
		a12 = p[1];
	}
	
	private final double[][] J =	// this transformation has a constant Jacobian
		{{1, 0},
		 {0, 1}};
	
	@Override
	public double[][] getWarpJacobian(double[] X) {
		return J;
	}

}
