/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.nonlinear;

import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.mappings.Mapping;

import java.awt.geom.Point2D;


/**
 * This class represents a bilinear transformation in 2D with 8
 * parameters a0, ..., a3, b0, ..., b3 of the form
 * <pre>
 * x' = a0 * x + a1 * y + a2 * x * y + a3
 * y' = b0 * x + b1 * y + b2 * x * y + b3
 * </pre>
 * Note that this is a non-linear transformation because of the mixed term.
 */
public class BilinearMapping extends Mapping {
	
	private final double a0, a1, a2, a3;
	private final double b0, b1, b2, b3;
	
	public BilinearMapping(
			double a0, double a1, double a2, double a3,
			double b0, double b1, double b2, double b3) {
		this.a0 = a0;   this.a1 = a1;   this.a2 = a2;   this.a3 = a3;
		this.b0 = b0;   this.b1 = b1;   this.b2 = b2;   this.b3 = b3;		
	}
	
	public static BilinearMapping fromQuads(Point2D[] P, Point2D[] Q) {
		return fromQuads(P[0], P[1], P[2], P[3], Q[0], Q[1], Q[2], Q[3]);
	}

	/**
	 * Calculates and returns the bilinear mapping M between two point
	 * sets P, Q, with 4 points each, such that q = M(p).
	 * The inverse mapping can be obtained by simply swapping the two point sets.
	 * @param P1 point P1
	 * @param P2 point P2
	 * @param P3 point P3
	 * @param P4 point P4
	 * @param Q1 point Q1
	 * @param Q2 point Q2
	 * @param Q3 point Q3
	 * @param Q4 point Q4
	 * @return a new bilinear mapping
	 */
	public static BilinearMapping fromQuads(
			Point2D P1, Point2D P2, Point2D P3, Point2D P4,	// source quad
			Point2D Q1, Point2D Q2, Point2D Q3, Point2D Q4)	// target quad
		{	
		//define column vectors x, y
		double[] x = {Q1.getX(), Q2.getX(), Q3.getX(), Q4.getX()};
		double[] y = {Q1.getY(), Q2.getY(), Q3.getY(), Q4.getY()};
		
		//define matrix M
		double[][] M = new double[][]
			{{P1.getX(), P1.getY(), P1.getX() * P1.getY(), 1},
			 {P2.getX(), P2.getY(), P2.getX() * P2.getY(), 1},
			 {P3.getX(), P3.getY(), P3.getX() * P3.getY(), 1},
			 {P4.getX(), P4.getY(), P4.getX() * P4.getY(), 1}};
		double[] a = Matrix.solve(M, x);		// solve x = M * a = x (a is unknown)
		double[] b = Matrix.solve(M, y);		// solve y = M * b = y (b is unknown)		
		double a1 = a[0];		double b1 = b[0];
		double a2 = a[1];		double b2 = b[1];
		double a3 = a[2];		double b3 = b[2];
		double a4 = a[3];		double b4 = b[3];
		return new BilinearMapping(a1, a2, a3, a4, b1, b2, b3, b4);
	}
	
	@Override
	public double[] applyTo (double x, double y) {
		double xx = a0 * x + a1 * y + a2 * x * y + a3;
		double yy = b0 * x + b1 * y + b2 * x * y + b3;
		return new double[] {xx, yy};
	}	
	
	public String toString() {
		return String.format(
				"A = (%.3f, %.3f, %.3f, %.3f) / B = (%.3f, %.3f, %.3f, %.3f)",
				a0, a1, a2, a3, b0, b1, b2, b3);
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * For testing only.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		Point2D[] P = new Point2D[] {
				new Point2D.Double(2,5),
				new Point2D.Double(4,6),
				new Point2D.Double(7,9),
				new Point2D.Double(5,9),
				};
		
		Point2D[] Q = new Point2D[] {
				new Point2D.Double(4,3),
				new Point2D.Double(5,2),
				new Point2D.Double(9,3),
				new Point2D.Double(7,5),
				};
		
		BilinearMapping bm = fromQuads(P, Q);
		System.out.println("\nbilinear mapping = \n" + bm.toString());
		
		for (int i = 0; i < P.length; i++) {
			Point2D Qi = bm.applyTo(P[i]);
			System.out.println(P[i].toString() + " -> " + Qi.toString());
		}
		
	}

}
