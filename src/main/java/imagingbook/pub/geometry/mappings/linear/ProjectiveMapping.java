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

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.lib.settings.PrintPrecision;
import imagingbook.pub.geometry.mappings.JacobianSupport;
import imagingbook.pub.geometry.mappings.Mapping;


/**
 * This class represents a projective transformation in 2D (also known
 * as a "homography"). It can be specified uniquely by four pairs of corresponding
 * points.
 */
public class ProjectiveMapping extends LinearMapping implements JacobianSupport {
	
	//  static methods -----------------------------------------------------
	
	/**
	 * Creates the most specific linear mapping from two sequences of corresponding
	 * 2D points.
	 * 
	 * @param P first point sequence
	 * @param Q second point sequence
	 * @return a linear mapping derived from point correspondences
	 * @deprecated
	 */
	public static ProjectiveMapping makeMapping(Point2D[] P, Point2D[] Q) { // TODO: Check for a better solution!
		int minLen = Math.min(P.length, Q.length);
		if (minLen < 1) {
			throw new IllegalArgumentException("cannot create a mapping from zero points");
		}
		else if (minLen <= 2) {				// TODO: special case for minLen == 2, rigid transformation??
			return null; //new Translation(P, Q);
		}
		else if (minLen <= 3) {
			return AffineMapping.fromTriangles(P, Q);
		}
		else {
			return ProjectiveMapping.fromQuads(P, Q);
		}
	}
	
	/**
	 * Creates the projective mapping from the unit square S to
	 * the arbitrary quadrilateral P, specified by four points.
	 * 
	 * @param p0 point 0
	 * @param p1 point 1
	 * @param p2 point 2
	 * @param p3 point 3
	 * @return a new projective mapping
	 */
	public static ProjectiveMapping fromUnitSquareToQuad(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
		double x0 = p0.getX(), x1 = p1.getX(), x2 = p2.getX(), x3 = p3.getX();
		double y0 = p0.getY(), y1 = p1.getY(), y2 = p2.getY(), y3 = p3.getY();
		double S = (x1 - x2) * (y3 - y2) - (x3 - x2) * (y1 - y2);
		// TODO: check S for zero value and throw exception
		double a20 = ((x0 - x1 + x2 - x3) * (y3 - y2) - (y0 - y1 + y2 - y3) * (x3 - x2)) / S;
		double a21 = ((y0 - y1 + y2 - y3) * (x1 - x2) - (x0 - x1 + x2 - x3) * (y1 - y2)) / S;
		double a00 = x1 - x0 + a20 * x1;
		double a01 = x3 - x0 + a21 * x3;
		double a02 = x0;
		double a10 = y1 - y0 + a20 * y1;
		double a11 = y3 - y0 + a21 * y3;
		double a12 = y0;
		return new ProjectiveMapping(a00, a01, a02, a10, a11, a12, a20, a21);
	}
	
	/**
	 * Creates a new projective mapping between arbitrary two quadrilaterals P, Q.
	 * @param p0 point 0 of source quad P
	 * @param p1 point 1 of source quad P
	 * @param p2 point 2 of source quad P
	 * @param p3 point 3 of source quad P
	 * @param q0 point 0 of target quad Q
	 * @param q1 point 1 of target quad Q
	 * @param q2 point 2 of target quad Q
	 * @param q3 point 3 of target quad Q
	 * @return a new projective mapping
	 */
	public static ProjectiveMapping fromQuads(
			Point2D p0, Point2D p1, Point2D p2, Point2D p3, 
			Point2D q0, Point2D q1, Point2D q2, Point2D q3)	{
		ProjectiveMapping T1 = ProjectiveMapping.fromUnitSquareToQuad(p0, p1, p2, p3);
		ProjectiveMapping T2 = ProjectiveMapping.fromUnitSquareToQuad(q0, q1, q2, q3);
		ProjectiveMapping T1i = T1.getInverse();
		return T1i.concat(T2);		
	}
	
	/**
	 * Creates a new projective mapping between arbitrary two quadrilaterals P, Q.
	 * @param P source quad
	 * @param Q target quad
	 * @return a new projective mapping
	 */
	public static final ProjectiveMapping fromQuads(Point2D[] P, Point2D[] Q) {
		return ProjectiveMapping.fromQuads(P[0], P[1], P[2], P[3], Q[0], Q[1], Q[2], Q[3]);
	}
	
	/**
	 * Maps between n &gt; 4 point pairs, finds a least-squares solution
	 * for the homography parameters.
	 * TODO: find better name, this is UNFINISHED code!
	 * @param P sequence of points (source)
	 * @param Q sequence of points (target)
	 * @return a new projective mapping
	 */
	public static ProjectiveMapping fromPoints(Point2D[] P, Point2D[] Q) {
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
		double a00 = h.getEntry(0);
		double a01 = h.getEntry(1);
		double a02 = h.getEntry(2);
		double a10 = h.getEntry(3);
		double a11 = h.getEntry(4);
		double a12 = h.getEntry(5);
		double a20 = h.getEntry(6);
		double a21 = h.getEntry(7);
		return new ProjectiveMapping(a00, a01, a02, a10, a11, a12, a20, a21);
	}
	
	//  constructors -----------------------------------------------------
	
	/**
	 * Creates the identity mapping.
	 */
	public ProjectiveMapping() {
		super();
	}
	
	/**
	 * Creates a projective mapping from the specified matrix elements.
	 * @param a00 matrix element A_00
	 * @param a01 matrix element A_01
	 * @param a02 matrix element A_02
	 * @param a10 matrix element A_10
	 * @param a11 matrix element A_11
	 * @param a12 matrix element A_12
	 * @param a20 matrix element A_20
	 * @param a21 matrix element A_21
	 */
	public ProjectiveMapping(
			double a00, double a01, double a02, 
			double a10, double a11, double a12, 
			double a20, double a21) {
		super(a00, a01, a02, a10, a11, a12, a20, a21, 1);
	}
	
	/**
	 * Creates a projective mapping from any linear mapping.
	 * The transformation matrix gets normalized to a22 = 1.
	 * @param m a linear mapping
	 */
	public ProjectiveMapping(LinearMapping m) {
		this(m.normalize());
	}
	
	/**
	 * Creates a projective mapping from an existing instance.
	 * @param m a projective mapping
	 */
	public ProjectiveMapping(ProjectiveMapping m) {
		//this(m.getTransformationMatrix());
		this(m.a00, m.a01, m.a02, m.a10, m.a11, m.a12, m.a20, m.a21);
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Concatenates this mapping A with another linear mapping B and returns
	 * a new mapping C, such that C(x) = B(A(x)).
	 * @param B the second mapping
	 * @return the concatenated mapping
	 */
	public ProjectiveMapping concat(ProjectiveMapping B) {
		LinearMapping C = LinearMapping.concatenate(B, this);
		return new ProjectiveMapping(C);
	}
	
	/**
	 * {@inheritDoc}
	 * @return a new projective mapping
	 */
	public ProjectiveMapping duplicate() {
		return new ProjectiveMapping(this);
	}
	
	/**
	 * {@inheritDoc}
	 * Note that the inverse A' of a projective transformation matrix A is again a linear transformation
	 * but its a'2' element is generally not 1. Scaling A' to A'' = A' / a22' yields a projective transformation
	 * that reverses A. While A * A' = I, the result of A * A'' is a scaled identity matrix.
	 * @return the inverse projective transformation
	 */
	public ProjectiveMapping getInverse() {
		return new ProjectiveMapping(super.getInverse());
	}
	
	// Jacobian support -------------------------------------

	@Override
	public double[] getParameters() {
		return new double[] { a00 - 1, a01, a10, a11 - 1, a20, a21, a02, a12 };
	}
	
	@Override
	public ProjectiveMapping fromParameters(double[] p) {
		return new ProjectiveMapping(
				p[0] + 1,   p[1],        p[6],
				p[2],       p[3] + 1,    p[7],
				p[4],       p[5]             );
	}
	
	public double[][] getJacobian(double[] xy) {
		// see Baker 2003 "20 Years" Part 1, Eq. 99 (p. 46)
		final double x = xy[0];
		final double y = xy[1];
		final double a = a00 * x + a01 * y + a02;	// = alpha
		final double b = a10 * x + a11 * y + a12;	// = beta
		final double c = a20 * x + a21 * y + 1;		// = gamma
		final double cc = c * c;
		// TODO: check c for zero-value and throw exception, make more efficient
		return new double[][]
			{{x/c, y/c, 0,   0,   -(x*a)/cc, -(y*a)/cc, 1/c, 0  },
			 {0,   0,   x/c, y/c, -(x*b)/cc, -(y*b)/cc, 0,   1/c}};
	}
	
	// -----------------------------------------------------------------
	
	/**
	 * For testing only.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		PrintPrecision.set(6);

		// book example:
		Point2D[] A = new Point2D[] {
				new Point2D.Double(2,5),
				new Point2D.Double(4,6),
				new Point2D.Double(7,9),
				new Point2D.Double(5,9),
				//new Point2D.Double(5.2,9.1)	// 5 points, overdetermined!
				};
		
		Point2D[] B = new Point2D[] {
				new Point2D.Double(4,3),
				new Point2D.Double(5,2),
				new Point2D.Double(9,3),
				new Point2D.Double(7,5),
				//new Point2D.Double(7,4.9)	// 5 points, overdetermined!
				};
		
		ProjectiveMapping pm = ProjectiveMapping.fromPoints(A, B);
		
		System.out.println("\nprojective mapping = \n" + pm.toString());
		
		for (int i = 0; i < A.length; i++) {
			Point2D Bi = pm.applyTo(A[i]);
			System.out.println(A[i].toString() + " -> " + Bi.toString());
		}
		
		System.out.println("pm is of class " + pm.getClass().getName());
		ProjectiveMapping pmi = pm.getInverse();
		pmi = pmi.normalize();
		System.out.println("\ninverse projective mapping (normalized) = \n" + pmi.toString());
		
		for (int i = 0; i < B.length; i++) {
			Point2D Ai = pmi.applyTo(B[i]);
			System.out.println(B[i].toString() + " -> " + Ai.toString());
		}
		
		ProjectiveMapping testId = pm.concat(pmi);
		System.out.println("\ntest: should be a scaled identity matrix: = \n" + testId.toString());
		
		
	}
	
	
}
