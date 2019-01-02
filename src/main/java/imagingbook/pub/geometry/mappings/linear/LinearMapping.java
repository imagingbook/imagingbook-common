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
	
	protected final double 
		a00, a01, a02,
		a10, a11, a12,
		a20, a21, a22;

	/**
	 * Creates a new identity mapping.
	 */
	public LinearMapping() {
		a00 = 1; a01 = 0; a02 = 0;
		a10 = 0; a11 = 1; a12 = 0;
		a20 = 0; a21 = 0; a22 = 1;
	}
	
	public LinearMapping(double[][] A) {
		a00 = A[0][0]; a01 = A[0][1]; a02 = A[0][2];
		a10 = A[1][0]; a11 = A[1][1]; a12 = A[1][2];
		a20 = A[2][0]; a21 = A[2][1]; a22 = A[2][2];
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
	 */
	public LinearMapping (
			double a00, double a01, double a02, 
			double a10, double a11, double a12,
			double a20, double a21, double a22) {
		this.a00 = a00;  this.a01 = a01;  this.a02 = a02;
		this.a10 = a10;  this.a11 = a11;  this.a12 = a12;
		this.a20 = a20;  this.a21 = a21;  this.a22 = a22;
//		isInverseFlag = inv;
	}
	
	/**
	 * Creates a new linear mapping from an existing linear mapping.
	 * @param lmap a given linear mapping
	 */
	public LinearMapping (LinearMapping lmap) {
		this.a00 = lmap.a00;  this.a01 = lmap.a01;  this.a02 = lmap.a02;
		this.a10 = lmap.a10;  this.a11 = lmap.a11;  this.a12 = lmap.a12;
		this.a20 = lmap.a20;  this.a21 = lmap.a21;  this.a22 = lmap.a22;
//		this.isInverseFlag = lmap.isInverseFlag;
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Scales the transformation matrix such that a22 becomes 1.
	 * Any linear mapping can be normalized and thereby be converted to
	 * a projective mapping (see {@link ProjectiveMapping}.
	 * @return the normalized linear mapping (i.e., a projective mapping)
	 */
	public ProjectiveMapping normalize() {
		// TODO: check a22 for zero value and throw exception, move to ProjectiveMapping, make constructor?
		double b00 = a00/a22;	double b01 = a01/a22;	double b02 = a02/a22;
		double b10 = a10/a22;	double b11 = a11/a22;	double b12 = a12/a22;
		double b20 = a20/a22;	double b21 = a21/a22;
		return new ProjectiveMapping(b00, b01, b02, b10, b11, b12, b20, b21);
	}
	
	// ----------------------------------------------------------
	
	@Override
	public double[] applyTo (double x, double y) {
		double h =  (a20 * x + a21 * y + a22);
		double x1 = (a00 * x + a01 * y + a02) / h;
		double y1 = (a10 * x + a11 * y + a12) / h;
		// pnt.setLocation(x1, y1);
		return new double[] {x1, y1};
	}
	
//	public LinearMapping getInverse() {	// TODO: implement with Apache Commons Math
//		System.out.println("LinearMapping getInverse()");
//		double[][] Ai = Matrix.inverse(this.getTransformationMatrix());
//		return new LinearMapping(Ai);
//	}
	
	
	public LinearMapping getInverse() {	// TODO: implement with Apache Commons Math
		System.out.println("LinearMapping getInverse() - closed solution");
		double det = a00*a11*a22 + a01*a12*a20 + a02*a10*a21 - 
				     a00*a12*a21 - a01*a10*a22 - a02*a11*a20;
		double b00 = (a11*a22 - a12*a21) / det; 
		double b01 = (a02*a21 - a01*a22) / det; 
		double b02 = (a01*a12 - a02*a11) / det; 
		double b10 = (a12*a20 - a10*a22) / det; 
		double b11 = (a00*a22 - a02*a20) / det; 
		double b12 = (a02*a10 - a00*a12) / det;
		double b20 = (a10*a21 - a11*a20) / det; 
		double b21 = (a01*a20 - a00*a21) / det; 
		double b22 = (a00*a11 - a01*a10) / det;
		return new LinearMapping(b00, b01, b02, b10, b11, b12, b20, b21, b22);
	}
	
	/**
	 * Calculates and returns the inverted mapping.
	 */
//	public LinearMapping invert() {
//		LinearMapping lm = new LinearMapping(this);
//		lm.invertDestructive();
//		return lm;
//	}
	
//	/**
//	 * Invertes this mapping destructively ("in-place") i.e.,
//	 * without creating a new instance.
//	 */
//	protected void invertDestructive() {
//		double det = a00*a11*a22 + a01*a12*a20 + a02*a10*a21 - 
//					 a00*a12*a21 - a01*a10*a22 - a02*a11*a20;
//		double b00 = (a11*a22 - a12*a21) / det; 
//		double b01 = (a02*a21 - a01*a22) / det; 
//		double b02 = (a01*a12 - a02*a11) / det; 
//		double b10 = (a12*a20 - a10*a22) / det; 
//		double b11 = (a00*a22 - a02*a20) / det; 
//		double b12 = (a02*a10 - a00*a12) / det;
//		double b20 = (a10*a21 - a11*a20) / det; 
//		double b21 = (a01*a20 - a00*a21) / det; 
//		double b22 = (a00*a11 - a01*a10) / det;
//		a00 = b00;		a01 = b01;		a02 = b02;
//		a10 = b10;		a11 = b11;		a12 = b12;
//		a20 = b20;		a21 = b21;		a22 = b22;
////		isInverseFlag = !isInverseFlag;
//	}
	
	/**
	 * Concatenates this mapping A with another linear mapping B and returns
	 * a new mapping C, such that C(x) = B(A(x)).
	 * @param B the second mapping
	 * @return the concatenated mapping
	 */
	public LinearMapping concat(LinearMapping B) {
		double b00 = B.a00*a00 + B.a01*a10 + B.a02*a20;
		double b01 = B.a00*a01 + B.a01*a11 + B.a02*a21;
		double b02 = B.a00*a02 + B.a01*a12 + B.a02*a22;
		
		double b10 = B.a10*a00 + B.a11*a10 + B.a12*a20;
		double b11 = B.a10*a01 + B.a11*a11 + B.a12*a21;
		double b12 = B.a10*a02 + B.a11*a12 + B.a12*a22;
		
		double b20 = B.a20*a00 + B.a21*a10 + B.a22*a20;
		double b21 = B.a20*a01 + B.a21*a11 + B.a22*a21;
		double b22 = B.a20*a02 + B.a21*a12 + B.a22*a22;
		return new LinearMapping(b00, b01, b02, b10, b11, b12, b20, b21, b22);
	}
	
	/**
	 * Concatenates this mapping A destructively with another linear mapping B,
	 * such that A(x) becomes B(A(x)).
	 * TODO: This should not be public!
	 * @param B the second mapping
	 */
//	@Deprecated
//	public void concatDestructive(LinearMapping B) {
//		double b00 = B.a00*a00 + B.a01*a10 + B.a02*a20;
//		double b01 = B.a00*a01 + B.a01*a11 + B.a02*a21;
//		double b02 = B.a00*a02 + B.a01*a12 + B.a02*a22;
//		
//		double b10 = B.a10*a00 + B.a11*a10 + B.a12*a20;
//		double b11 = B.a10*a01 + B.a11*a11 + B.a12*a21;
//		double b12 = B.a10*a02 + B.a11*a12 + B.a12*a22;
//		
//		double b20 = B.a20*a00 + B.a21*a10 + B.a22*a20;
//		double b21 = B.a20*a01 + B.a21*a11 + B.a22*a21;
//		double b22 = B.a20*a02 + B.a21*a12 + B.a22*a22;
//		a00 = b00;		a01 = b01;		a02 = b02;
//		a10 = b10;		a11 = b11;		a12 = b12;
//		a20 = b20;		a21 = b21;		a22 = b22;
//	}
	
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
	 * @return a new mapping of the same type
	 */
	public LinearMapping duplicate() {
		return new LinearMapping(this);
	}

	public String toString() {
		return Matrix.toString(getTransformationMatrix());
	}
	
	// -----------------------------------------------------------
	
	public static void main(String[] args) {
		double[][] A = 
				{{-1.230769, 2.076923, -1.769231}, 
				{-2.461538, 2.615385, -3.538462}, 
				{-0.307692, 0.230769, 1.000000}};
		
		System.out.println("A = " + Matrix.toString(A));
		double[][] Ai = Matrix.inverse(A);
		System.out.println("Ai = " + Matrix.toString(Ai));
		
	}
	
}




