/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.math.eigen;

import imagingbook.lib.math.Matrix;


/**
 * Implements an efficient, closed form algorithm for calculating the real 
 * eigenvalues (&lambda;) and eigenvectors (x) of a 2x2 matrix of the form
 * <pre>
 *   | a b |
 *   | c d | </pre>
 * There are typically (but not always) two pairs of real-valued 
 * solutions 
 * &lang;&lambda;<sub>1</sub>, x<sub>0</sub>&rang;,
 * &lang;&lambda;<sub>2</sub>, x<sub>1</sub>&rang;
 * such that A&middot;x<sub>k</sub> = &lambda;<sub>k</sub>&middot;x<sub>k</sub>.
 * The resulting eigensystems are ordered such that
 * |&lambda;<sub>0</sub> &ge; |&lambda;<sub>1</sub>|.
 * Eigenvectors are not normalized, i.e., no unit vectors
 * (any scalar multiple of an Eigenvector is an Eigenvector too).
 * Non-real eigenvalues are not handled.
 * Clients should call method {@link #isReal()} to check if the resulting eigenvalues
 * are all real or not.
 * <p>
 * This implementation is inspired by Blinn, Jim: "Jim Blinn's Corner: 
 * Notation, Notation, Notation", Morgan Kaufmann (2002) -
 * Ch. 5 ("Consider the Lowly 2x2 Matrix").
 * Note that Blinn uses the notation 
 * x&middot;A = &lambda;&middot;x for the matrix-vector product (as common in computer graphics),
 * while this implementation uses 
 * A&middot;x = &lambda;&middot;x.
 * Thus x is treated as a column vector and matrix A is transposed (elements b/c are exchanged).
 * </p>
 * This implementation is considerably (ca. factor 5) faster than the general solution
 * available in {@link EigensolverNxN} (based on Apache Commons Math) for 2x2 matrices.
 * 
 * @author W. Burger
 * @version 2021-05-19
 */
public class Eigensolver2x2 implements RealEigensolver { // to check: http://www.akiti.ca/Eig2Solv.html
	private final boolean isReal;
	private final double[] eVals = {Double.NaN, Double.NaN};
	private final double[][] eVecs = new double[2][];
	
	/**
	 * Constructor, takes a 2x2 matrix.
	 * @param A a 2x2 matrix
	 */
	public Eigensolver2x2(double[][] A) {
		this(A[0][0], A[0][1], A[1][0], A[1][1]);
		if (Matrix.getNumberOfRows(A) != 2 || Matrix.getNumberOfColumns(A) != 2) {
			throw new IllegalArgumentException("matrix not of size 2x2");
		}
	}
	
	/**
	 * Constructor, takes the individual elements of a 2x2 matrix A:
	 * <pre>
	 *   | a b |
	 *   | c d | </pre>
	 * @param a matrix element A[0,0]
	 * @param b matrix element A[0,1]
	 * @param c matrix element A[1,0]
	 * @param d matrix element A[1,1]
	 */
	public Eigensolver2x2(double a, double b, double c, double d) {
		isReal = solve(a, b, c, d);
	}
	
	@Override
	public int getSize() {
		return 2;
	}
	
	private boolean solve(final double a, final double b, final double c, final double d) {
		final double r = (a + d) / 2;
		final double s = (a - d) / 2;
		final double rho = s * s + b * c;
		
		if (rho < 0) {	
			return false;		// no real-valued eigenvalues
		}
		
		final double t = Math.sqrt(rho);
		final double lambda0 = r + t;	// eigenvalue 0
		final double lambda1 = r - t;	// eigenvalue 1
		final double[] x0, x1;			// eigenvectors 0, 1
		
		if (a - d > 0) {
			x0 = new double[] {s + t, c};
			x1 = new double[] {b, -s - t};
		}
		else if (a - d < 0) {
			x0 = new double[] {b, -s + t};
			x1 = new double[] {s - t, c};
		}
		else {		// (A - D) == 0
			final double bA = Math.abs(b);
			final double cA = Math.abs(c);
			final double bcR = Math.sqrt(b * c);
			if (bA < cA) {							// |b| < |c|
				x0 = new double[] { bcR, c};
				x1 = new double[] {-bcR, c};
			}
			else if (bA > cA) { 					// |b| > |c|
				x0 = new double[] {b,  bcR};
				x1 = new double[] {b, -bcR};
			}
			else { 				// |B| == |C|
				x0 = new double[] { c, c};
				x1 = new double[] {-c, c};
			}
		}
		
		if (Math.abs(lambda0) >= Math.abs(lambda1)) {	// order eigenvalues by magnitude
			eVals[0] = lambda0;
			eVals[1] = lambda1;
			eVecs[0] = x0;
			eVecs[1] = x1;
		}
		else {
			eVals[0] = lambda1;
			eVals[1] = lambda0;
			eVecs[0] = x1;
			eVecs[1] = x0;
		}
		return true;	// real eigenvalues
	}
	
	@Override
	public boolean isReal() {
		return isReal;
	}
	
	/**
	 * Returns a vector holding the two real eigenvalues. Both are
	 * {@code NaN} if the input matrix has any non-real (complex) eigenvalues.
	 * Otherwise the first eigenvalue has greater magnitude than the second.
	 * Note that the method returns a reference to an internal
	 * array and thus results should be used read-only!
	 * @return a 2-element array of eigenvalues
	 */
	@Override
	public double[] getEigenvalues() {
		return this.eVals;
	}
	
	/**
	 * Returns the k-th eigenvalue (&lambda;_k, k = 0, 1).
	 * {@code NaN} is returned if the the eigenvalue is not real.
	 * 
	 * @param k index (0 or 1)
	 * @return the k-th eigenvalue
	 */
	@Override
	public double getEigenvalue(int k) {
		return eVals[k];
	}
	
	/**
	 * Obsolete, use {@link #getEigenvalue(int)} instead.
	 * @return the first eigenvalue (&lambda;_1)
	 * @deprecated
	 */
	public double getEigenvalue1() {
		return eVals[0];
	}
	
	/**
	 * Obsolete, use {@link #getEigenvalue(int)} instead.
	 * @return the second eigenvalue (&lambda;_2)
	 * @deprecated
	 */
	public double getEigenvalue2() {
		return eVals[1];
	}
	
	/**
	 * Returns a 2x2 array holding the two real eigenvectors.
	 * When used in the form
	 * <pre>double[][] X = getEigenvalues();</pre>
	 * then {@code X[0]} and {@code X[1]} contain the 1st and 2nd eigenvector,
	 * respectively. If the associated matrix has no real eigenvalues,
	 * then {@code X[0]} and {@code X[1]} are both {@code null}.
	 * Otherwise {@code X[k][0]} and {@code X[k][1]} are the x/y components
	 * of the k-th eigenvector.
	 * The ordering of the returned eigenvectors is the same as for the
	 * eigenvalues returned by {@link #getEigenvalues()}.
	 * Note that the method returns a reference to an internal
	 * array and thus results should be used read-only!
	 * Obsolete, use {@link #getEigenvector(int)} instead.
	 * @return a 2-element array of 2D eigenvectors
	 * @deprecated
	 */
	public double[][] getEigenvectors() {
		return eVecs;
	}
	
	/**
	 * Returns the k-th eigenvector, with k = 0 or 1.
	 * The ordering of the returned eigenvectors is the same as for the
	 * eigenvalues returned by {@link #getEigenvalues()}.
	 * Note that the method returns a reference to an internal
	 * array and thus results should be used read-only!
	 * @param k index (0 or 1)
	 * @return the k-th eigenvector
	 */
	@Override
	public double[] getEigenvector(int k) {
		return eVecs[k];
	}
	
	@Deprecated
	public double[] getEigenvector1() {
		return eVecs[0];
	}
	
	@Deprecated
	public double[] getEigenvector2() {
		return eVecs[1];
	}
	
	@Override
	public String toString() {
		if (this.isReal) {
			return String.format("<%.4f, %.4f, %s, %s>", 
				eVals[0], eVals[1], Matrix.toString(eVecs[0]), Matrix.toString(eVecs[1]));
		}
		else {
			return "<not real>";
		}
	}
		
}
