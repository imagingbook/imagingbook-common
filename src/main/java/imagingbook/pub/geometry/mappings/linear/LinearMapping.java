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

/**
 * This class represents an arbitrary linear transformation in 2D.
 * TODO: make fields final.
 */
public class LinearMapping extends Mapping {
	
	protected double 
		a00 = 1, a01 = 0, a02 = 0,
		a10 = 0, a11 = 1, a12 = 0,
		a20 = 0, a21 = 0, a22 = 1;

	/**
	 * Creates a new identity mapping.
	 */
	public LinearMapping() {
	}

	/**
	 * Creates an arbitrary linear mapping from the specified matrix elements.
	 * @param a00 matrix element A_00
	 * @param a01 matrix element A_01
	 * @param a02 matrix element A_02
	 * @param a10 matrix element A_10
	 * @param a11 matrix element A_11
	 * @param a12 matrix element A_12
	 * @param a20 matrix element A_20
	 * @param a21 matrix element A_21
	 * @param a22 matrix element A_22
	 * @param inv set true if this mapping represents an inverse transformation
	 */
	public LinearMapping (
			double a00, double a01, double a02, 
			double a10, double a11, double a12,
			double a20, double a21, double a22, boolean inv) {
		this.a00 = a00;  this.a01 = a01;  this.a02 = a02;
		this.a10 = a10;  this.a11 = a11;  this.a12 = a12;
		this.a20 = a20;  this.a21 = a21;  this.a22 = a22;
		isInverseFlag = inv;
	}
	
	/**
	 * Creates a new linear mapping from an existing linear mapping.
	 * @param lmap a given linear mapping
	 */
	public LinearMapping (LinearMapping lmap) {
		this.a00 = lmap.a00;  this.a01 = lmap.a01;  this.a02 = lmap.a02;
		this.a10 = lmap.a10;  this.a11 = lmap.a11;  this.a12 = lmap.a12;
		this.a20 = lmap.a20;  this.a21 = lmap.a21;  this.a22 = lmap.a22;
		this.isInverseFlag = lmap.isInverseFlag;
	}
	
	// TODO: add a constructor that accepts a sequence of linear mappings, make concatenation protected.
	
	// ----------------------------------------------------------
	
	@Override
	public double[] applyTo (double x, double y) {
		double h =  (a20 * x + a21 * y + a22);
		double x1 = (a00 * x + a01 * y + a02) / h;
		double y1 = (a10 * x + a11 * y + a12) / h;
		// pnt.setLocation(x1, y1);
		return new double[] {x1, y1};
	}
	
	// TODO: should be implemented differently (generics)
	public LinearMapping getInverse() {
		return (LinearMapping) super.getInverse();
//		if (isInverseFlag)
//			return this;
//		else {
//			return this.invert();
//		}
	}
	

	/**
	 * Calculates and returns the inverted mapping.
	 */
	public LinearMapping invert() {
		LinearMapping lm = new LinearMapping(this);
		lm.invertDestructive();
		return lm;
	}
	
	/**
	 * Invertes this mapping destructively ("in-place") i.e.,
	 * without creating a new instance.
	 */
	protected void invertDestructive() {
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
	
	/**
	 * Concatenates this mapping A with another linear mapping B and returns
	 * a new mapping C, such that C(x) = B(A(x)).
	 * @param B the second mapping
	 * @return the concatenated mapping
	 */
	public LinearMapping concat(LinearMapping B) {
		LinearMapping A = new LinearMapping(this);
		A.concatDestructive(B);
		return A;
	}
	
	/**
	 * Concatenates this mapping A destructively with another linear mapping B,
	 * such that A <- B * A.
	 * TODO: This should not be public!
	 * @param B the second mapping
	 */
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
	
	/**
	 * Retrieves the transformation matrix for this mapping.
	 * @return the 3x3 transformation matrix
	 */
	public double[][] getTransformationMatrix() {
		return new double[][]
				{{a00, a01, a02},
				 {a10, a11, a12},
				 {a20, a21, a22}};
	}

	/**
	 * Returns a copy of this mapping.
	 * @return a new linear mapping
	 */
	public LinearMapping duplicate() {	// TODO: use generics?
//		return (LinearMapping) this.clone();
		return new LinearMapping(this);
	}

	public String toString() {
		return Matrix.toString(getTransformationMatrix());
	}
	
}




