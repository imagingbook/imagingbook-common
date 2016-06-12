/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;

import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.mappings.Mapping;



import java.awt.geom.Point2D;

/*
 * 2013-02-02: 
 * 	Changed applyTo(Point2D) to return a new point (no reuse).
 * 	Changed LinearMapping invert() to return new mapping.
 * 	Changed LinearMapping concat() to return new mapping.
 * 2015-12-20:
 * 	Duplicated getInverse() to return a LinearMapping.
 */

public class LinearMapping extends Mapping {
	
	protected double 
		a00 = 1, a01 = 0, a02 = 0,
		a10 = 0, a11 = 1, a12 = 0,
		a20 = 0, a21 = 0, a22 = 1;
		   
	public LinearMapping() {
		// creates the identity mapping
	}
		   
	protected LinearMapping (
			double a11, double a12, double a13, 
			double a21, double a22, double a23,
			double a31, double a32, double a33, boolean inv) {
		this.a00 = a11;  this.a01 = a12;  this.a02 = a13;
		this.a10 = a21;  this.a11 = a22;  this.a12 = a23;
		this.a20 = a31;  this.a21 = a32;  this.a22 = a33;
		isInverseFlag = inv;
	}
	
	protected LinearMapping (LinearMapping lm) {
		this.a00 = lm.a00;  this.a01 = lm.a01;  this.a02 = lm.a02;
		this.a10 = lm.a10;  this.a11 = lm.a11;  this.a12 = lm.a12;
		this.a20 = lm.a20;  this.a21 = lm.a21;  this.a22 = lm.a22;
		this.isInverseFlag = lm.isInverseFlag;
	}
	
	public double[] applyTo (double[] xy) {
		return applyTo(xy[0], xy[1]);
	}
	
	public double[] applyTo (double x, double y) {
		double h =  (a20 * x + a21 * y + a22);
		double x1 = (a00 * x + a01 * y + a02) / h;
		double y1 = (a10 * x + a11 * y + a12) / h;
		// pnt.setLocation(x1, y1);
		return new double[] {x1, y1};
	}
		   
	public Point2D applyTo (Point2D pnt) {
		double x = pnt.getX();
		double y = pnt.getY();
		double h =  (a20 * x + a21 * y + a22);
		double x1 = (a00 * x + a01 * y + a02) / h;
		double y1 = (a10 * x + a11 * y + a12) / h;
		// pnt.setLocation(x1, y1);
		return new Point2D.Double(x1, y1);
	}
	
	public LinearMapping getInverse() {
		if (isInverseFlag)
			return this;
		else {
			return this.invert();
		}
	}
	
	public LinearMapping invert() {
		LinearMapping lm = new LinearMapping(this);
		lm.invertDestructive();
		return lm;
	}
	
	public void invertDestructive() {
		double det = a00*a11*a22 + a01*a12*a20 + a02*a10*a21 - 
					 a00*a12*a21 - a01*a10*a22 - a02*a11*a20;
		double b11 = (a11*a22 - a12*a21) / det; 
		double b12 = (a02*a21 - a01*a22) / det; 
		double b13 = (a01*a12 - a02*a11) / det; 
		double b21 = (a12*a20 - a10*a22) / det; 
		double b22 = (a00*a22 - a02*a20) / det; 
		double b23 = (a02*a10 - a00*a12) / det;
		double b31 = (a10*a21 - a11*a20) / det; 
		double b32 = (a01*a20 - a00*a21) / det; 
		double b33 = (a00*a11 - a01*a10) / det;
		a00 = b11;		a01 = b12;		a02 = b13;
		a10 = b21;		a11 = b22;		a12 = b23;
		a20 = b31;		a21 = b32;		a22 = b33;
		isInverseFlag = !isInverseFlag;
	}
	
	public LinearMapping concat(LinearMapping B) {	// TODO: this is unfinished and not clean!
		LinearMapping A = new LinearMapping(this);
		A.concatDestructive(B);
		return A;
	}
	
	// concatenates THIS transform matrix A with B: A-> B*A
	public void concatDestructive(LinearMapping B) {
		double b11 = B.a00*a00 + B.a01*a10 + B.a02*a20;
		double b12 = B.a00*a01 + B.a01*a11 + B.a02*a21;
		double b13 = B.a00*a02 + B.a01*a12 + B.a02*a22;
		
		double b21 = B.a10*a00 + B.a11*a10 + B.a12*a20;
		double b22 = B.a10*a01 + B.a11*a11 + B.a12*a21;
		double b23 = B.a10*a02 + B.a11*a12 + B.a12*a22;
		
		double b31 = B.a20*a00 + B.a21*a10 + B.a22*a20;
		double b32 = B.a20*a01 + B.a21*a11 + B.a22*a21;
		double b33 = B.a20*a02 + B.a21*a12 + B.a22*a22;
		a00 = b11;		a01 = b12;		a02 = b13;
		a10 = b21;		a11 = b22;		a12 = b23;
		a20 = b31;		a21 = b32;		a22 = b33;
	}
	
	public double[][] getTransformationMatrix () {
		return new double[][]
				{{a00, a01, a02},
				 {a10, a11, a12},
				 {a20, a21, a22}};
	}

	public LinearMapping duplicate() {
		return (LinearMapping) this.clone();
	}
	
	
	public double[][] toArray() {
		double[][] A =
			{{a00, a01, a02},
			 {a10, a11, a12},
			 {a20, a21, a22}};
		return A;
	}
	
	public String toString() {
		return Matrix.toString(toArray());
	}
	
}




