/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;

import imagingbook.lib.settings.PrintPrecision;
import imagingbook.pub.geometry.mappings.WarpParameters;

import java.awt.geom.Point2D;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;


/**
 * 2015-02-16: Added preliminary constructor for more than 4 point pairs 
 * (overdetermined, least-squares).
 * 
 * @author WB
 *
 */
public class ProjectiveMapping extends LinearMapping implements WarpParameters {
	
	
	public static ProjectiveMapping makeMapping (Point2D[] P, Point2D[] Q) {
		int minLen = Math.min(P.length, Q.length);
		if (minLen < 1) {
			throw new IllegalArgumentException("cannot create a mapping from zero points");
		}
		else if (minLen <= 2) {				// TODO: special case for minLen == 2?
			return new Translation(P, Q);
		}
		else if (minLen <= 3) {
			return new AffineMapping(P, Q);
		}
		else {
			return new ProjectiveMapping(P, Q);	
		}
	}
	
	// creates the identity mapping:
	public ProjectiveMapping() {
		super();
	}
	
	public ProjectiveMapping(
			double a00, double a01, double a02, 
			double a10, double a11, double a12, 
			double a20, double a21, 
			boolean inv) {
		super(a00, a01, a02, a10, a11, a12, a20, a21, 1, inv);
	}
	
	public ProjectiveMapping(LinearMapping lm) {
		super(lm);
		//this.normalize();	// needed??
	}
	
	// creates the projective mapping from the unit square S to
	// the arbitrary quadrilateral P given by points p0,...,p3:
	public ProjectiveMapping(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
		super();
		double x0 = p0.getX(), x1 = p1.getX(), x2 = p2.getX(), x3 = p3.getX(); 
		double y0 = p0.getY(), y1 = p1.getY(), y2 = p2.getY(), y3 = p3.getY();
		double S = (x1-x2)*(y3-y2) - (x3-x2)*(y1-y2);
		// TODO: check S for zero value and throw exception
		a20 = ((x0-x1+x2-x3)*(y3-y2)-(y0-y1+y2-y3)*(x3-x2)) / S;
		a21 = ((y0-y1+y2-y3)*(x1-x2)-(x0-x1+x2-x3)*(y1-y2)) / S;
		a00 = x1 - x0 + a20*x1;
		a01 = x3 - x0 + a21*x3;
		a02 = x0;
		a10 = y1 - y0 + a20*y1;
		a11 = y3 - y0 + a21*y3;
		a12 = y0;
	}
	
	
	/**
	 * Creates a new {@link ProjectiveMapping} between arbitrary quadrilaterals P, Q.
	 * @param p0 point 1 of source quad P.
	 * @param p1 point 2 of source quad P.
	 * @param p2 point 3 of source quad P.
	 * @param p3 point 4 of source quad P.
	 * @param q0 point 1 of target quad Q.
	 * @param q1 point 2 of target quad Q.
	 * @param q2 point 3 of target quad Q.
	 * @param q3 point 4 of target quad Q.
	 */
	public ProjectiveMapping(
			Point2D p0, Point2D p1, Point2D p2, Point2D p3, 
			Point2D q0, Point2D q1, Point2D q2, Point2D q3)	{
		super();	// initialized to identity
		ProjectiveMapping T1 = new ProjectiveMapping(p0, p1, p2, p3);
		ProjectiveMapping T2 = new ProjectiveMapping(q0, q1, q2, q3);
		ProjectiveMapping T1i = T1.invert();
		ProjectiveMapping T12 = T1i.concat(T2);		
		this.concatDestructive(T12);	// transfer T12 -> this
	}
	
	/**
	 * Creates a new {@link ProjectiveMapping} between arbitrary quadrilaterals P, Q.
	 * @param P source quad.
	 * @param Q target quad.
	 */
	public ProjectiveMapping(Point2D[] P, Point2D[] Q) {
		this(P[0], P[1], P[2], P[3], Q[0], Q[1], Q[2], Q[3]);
	}
	
	/**
	 * Constructor for more than 4 point pairs, finds a least-squares solution
	 * for the homography parameters.
	 * NOTE: this is UNFINISHED code!
	 * @param P sequence of points (source)
	 * @param Q sequence of points (target)
	 * @param dummy unused (only to avoid duplicate signature)
	 */
	public ProjectiveMapping(Point2D[] P, Point2D[] Q, boolean dummy) {
		final int n = P.length;
		double[] ba = new double[2 * n];
		double[][] Ma = new double[2 * n][];
		for (int i = 0; i < n; i++) {
			double x = P[i].getX();
			double y = P[i].getY();
			double u = Q[i].getX();
			double v = Q[i].getY();
			ba[2 * i + 0] = u;
			ba[2 * i + 1] = v;
			Ma[2 * i + 0] = new double[] { x, y, 1, 0, 0, 0, -u * x, -u * y };
			Ma[2 * i + 1] = new double[] { 0, 0, 0, x, y, 1, -v * x, -v * y };
		}
		
		RealMatrix M = MatrixUtils.createRealMatrix(Ma);
		RealVector b = MatrixUtils.createRealVector(ba);
		DecompositionSolver solver = new SingularValueDecomposition(M).getSolver();
		RealVector h = solver.solve(b);
		a00 = h.getEntry(0);
		a01 = h.getEntry(1);
		a02 = h.getEntry(2);
		a10 = h.getEntry(3);
		a11 = h.getEntry(4);
		a12 = h.getEntry(5);
		a20 = h.getEntry(6);
		a21 = h.getEntry(7);
		a22 = 1;
	}
	
	// -----------------------------------------------------------
	
	
	public ProjectiveMapping concat(ProjectiveMapping B) {
		ProjectiveMapping A = new ProjectiveMapping(this);
		A.concatDestructive(B);
		return A;
	}
	
	public ProjectiveMapping invert() {
		ProjectiveMapping pm = new ProjectiveMapping(this);
		pm.invertDestructive();
		return pm;
	}
	
	void normalize() {
		// scales the matrix such that a22 becomes 1
		// TODO: check a22 for zero value and throw exception
		a00 = a00/a22;		a01 = a01/a22;		a02 = a02/a22;
		a10 = a10/a22;		a11 = a11/a22;		a12 = a12/a22;
		a20 = a20/a22;		a21 = a21/a22;		a22 = 1;
	}
	
	@Override
	public ProjectiveMapping duplicate() {
		return (ProjectiveMapping) this.clone();
	}
	
	// Warp parameter support -------------------------------------
	
	public int getWarpParameterCount() {
		return 8;
	}
	
	public double[] getWarpParameters() {
		double[] p = new double[] {
			a00 - 1, a01, 
			a10, a11 - 1, 
			a20, a21,
			a02, a12,
			};
		return p;
	}
	
//	p[0] = M3x3[0][0] - 1;	// = a
//	p[1] = M3x3[0][1];		// = b
//	p[2] = M3x3[1][0];		// = c
//	p[3] = M3x3[1][1] - 1;	// = d
//	p[4] = M3x3[2][0];		// = e
//	p[5] = M3x3[2][1];		// = f
//	p[6] = M3x3[0][2];		// = tx
//	p[7] = M3x3[1][2];		// = ty


	public void setWarpParameters(double[] p) {
		a00 = p[0] + 1;   a01 = p[1];        a02 = p[6];
		a10 = p[2];       a11 = p[3] + 1;    a12 = p[7];
		a20 = p[4];       a21 = p[5];        a22 = 1;
	}
	

	public double[][] getWarpJacobian(double[] xy) {
		// see Baker 2003 "20 Years" Part 1, Eq. 99 (p. 46)
		final double x = xy[0];
		final double y = xy[1];
		double a = a00 * x + a01 * y + a02;	// = alpha
		double b = a10 * x + a11 * y + a12;	// = beta
		double c = a20 * x + a21 * y + 1;	// = gamma
		double cc = c * c;
		// TODO: check c for zero-value and throw exception, make more efficient
		return new double[][]
			{{x/c, y/c, 0,   0,   -(x*a)/cc, -(y*a)/cc, 1/c, 0  },
			 {0,   0,   x/c, y/c, -(x*b)/cc, -(y*b)/cc, 0,   1/c}};
	}
	
	// for testing only -----------------------------------------------------------------
	
	public static void main(String[] args) {
		PrintPrecision.set(6);

		// book example:
		Point2D[] A = new Point2D[] {
				new Point2D.Double(2,5),
				new Point2D.Double(4,6),
				new Point2D.Double(7,9),
				new Point2D.Double(5,9),
				new Point2D.Double(5.2,9.1)	// 5 points, overdetermined!
				};
		
		Point2D[] B = new Point2D[] {
				new Point2D.Double(4,3),
				new Point2D.Double(5,2),
				new Point2D.Double(9,3),
				new Point2D.Double(7,5),
				new Point2D.Double(7,4.9)	// 5 points, overdetermined!
				};
		
		ProjectiveMapping pm = new ProjectiveMapping(A, B, true);
		
		System.out.println("\nprojective mapping = " + pm.toString());
		
		for (int i = 0; i < A.length; i++) {
			Point2D Bi = pm.applyTo(A[i]);
			System.out.println(A[i].toString() + " -> " + Bi.toString());
		}
		
		System.out.println();
		ProjectiveMapping pmi = pm.invert();
		System.out.println("\ninverse projective mapping = " + pmi.toString());
		for (int i = 0; i < B.length; i++) {
			Point2D Ai = pmi.applyTo(B[i]);
			System.out.println(B[i].toString() + " -> " + Ai.toString());
		}
	}
	
	
}
