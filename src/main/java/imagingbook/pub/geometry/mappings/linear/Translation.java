/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;

import java.awt.geom.Point2D;


public class Translation extends AffineMapping {
	
	public Translation() {
		super();
	}

	public Translation(double dx, double dy){
		super();
		a02 = dx;
		a12 = dy;
	}
	
	public Translation(Point2D p1, Point2D p2) {
		this(p2.getX() - p1.getX(), p2.getY() - p1.getY());
	}
	
	// for consistency:
	public Translation(Point2D[] A, Point2D[] B) {
		this(A[0], B[0]);
	}

	public Translation(LinearMapping t) {
		super();
		this.a02 = t.a02;
		this.a12 = t.a12;
	}

	public Translation invert() {
		Translation t2 = new Translation();
		t2.a02 = -this.a02;
		t2.a12 = -this.a12;
		//return (Translation) super.invert();
		return t2;
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
