/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.math;


/**
 * </p> 
 * Implements an efficient, closed form algorithm for calculating the real 
 * eigenvalues (&lambda;) and eigenvectors (x) of a 2x2 matrix M of the form
 * <pre>
 *   | A B |
 *   | C D | </pre>
 * There are typically (but not in general) two pairs of real-valued 
 * solutions 
 * &lang;&lambda;<sub>1</sub>, x<sub>1</sub>&rang;,
 * &lang;&lambda;<sub>2</sub>, x<sub>2</sub>&rang;
 * such that M&middot;x<sub>k</sub> = &lambda;<sub>k</sub>&middot;x<sub>k</sub>.
 * The resulting eigensystems are ordered such that
 * |&lambda;<sub>1</sub> &geq; |&lambda;<sub>2</sub>|.
 * Eigenvectors are not normalized (i.e., no unit vectors).
 * </p>
 * <p>
 * This implementation is inspired by Blinn, Jim: "Jim Blinn's Corner: 
 * Notation, Notation, Notation", Morgan Kaufmann (2002) -
 * Ch. 5 ("Consider the Lowly 2x2 Matrix").
 * Note that Blinn uses the notation 
 * x&middot;M = &lambda;&middot;x (common computer graphics),
 * while this implementation adopts the notation 
 * M&middot;x = &lambda;&middot;x.
 * Thus matrix M is transposed (elements B/C are exchanged).
 * </p>
 * This implementation is considerably (ca. factor 5) faster than the general solution
 * in {@link EigensolverNxN} (based on Apache Commons Math) for 2x2 matrices.
 * 
 * @author W. Burger
 * @version 2021-05-18
 */
public class Eigensolver2x2 implements RealEigensolver { // to check: http://www.akiti.ca/Eig2Solv.html
	private final boolean isReal;
	private final double[] eVals = {Double.NaN, Double.NaN};
	private final double[][] eVecs = new double[2][];
	
	/**
	 * Constructor, takes a 2x2 matrix.
	 * @param M a 2x2 matrix
	 */
	public Eigensolver2x2(double[][] M) {
		this(M[0][0], M[0][1], M[1][0], M[1][1]);
	}
	
	/**
	 * Constructor, takes the individual elements of a 2x2 matrix:
	 * <pre>
	 *   A B
	 *   C D</pre>
	 * @param A matrix element M[0,0]
	 * @param B matrix element M[0,1]
	 * @param C matrix element M[1,0]
	 * @param D matrix element M[1,1]
	 */
	public Eigensolver2x2(double A, double B, double C, double D) {
		this.isReal = this.solve(A, B, C, D);
	}
	
	@Override
	public int getSize() {
		return 2;
	}
	
	private boolean solve(double A, double B, double C, double D) {
		final double R = (A + D) / 2;
		final double S = (A - D) / 2;
		final double rho = S * S + B * C;
		
//		System.out.format("S^2 = %.4f\n", S * S);
//		System.out.format("BC = %.4f\n", B * C);
//		System.out.format("rho = %.4f\n", rho);
		
		if (rho < 0) {	// no real-valued eigenvalues
//			System.out.println("Case 0");
			return false;
		}
		
		final double T = Math.sqrt(rho);
		this.eVals[0] = R + T;
		this.eVals[1] = R - T;
		if (A - D > 0) {
//			System.out.println("Case 1");
			this.eVecs[0] = new double[] { S + T, C };
			this.eVecs[1] = new double[] { B, -S - T };
		}
		else if (A - D < 0) {
//			System.out.println("Case 2");  
			this.eVecs[0] = new double[] { B, -S + T};
			this.eVecs[1] = new double[] { S - T, C};
		}
		else {		// (A - D) == 0
//			System.out.println("Case 3");  
			final double aB = Math.abs(B);
			final double aC = Math.abs(C);
			final double sBC = Math.sqrt(B * C);
			if (aB < aC) {							// |B| < |C|
//				System.out.println("Case 3A"); 
				this.eVecs[0] = new double[] { sBC, C};
				this.eVecs[1] = new double[] {-sBC, C};
			}
			else if (aB > aC) { 					// |B| > |C|
//				System.out.println("Case 3B");
				this.eVecs[0] = new double[] { B,  sBC};
				this.eVecs[1] = new double[] { B, -sBC};
			}
			else {
//				System.out.println("Case 3C");// |B| == |C|
				this.eVecs[0] = new double[] { C, C};
				this.eVecs[1] = new double[] {-C, C};
			}
		}
		
		if (Math.abs(eVals[0]) < Math.abs(eVals[1])) {	// reorder eigenvalues by absolute value
			swap();
		}
		return true;
	}
	
	/**
	 * Reorders eigenvalues and eigenvectors such that
	 * |&lambda;<sub>1</sub> &geq; |&lambda;<sub>2</sub>|.
	 */
	private void swap() {
		double l = eVals[0];
		eVals[0] = eVals[1];
		eVals[1] = l;
		double[] x = eVecs[0];
		eVecs[0] = eVecs[1];
		eVecs[1] = x;
	}
	
	@Override
	public boolean isReal() {
		return isReal;
	}
	
	/**
	 * Returns a vector holding the two real eigenvalues. Both are
	 * {@code NaN} if the associated matrix has no real eigenvalues.
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
		return this.eVals[0];
	}
	
	/**
	 * Obsolete, use {@link #getEigenvalue(int)} instead.
	 * @return the second eigenvalue (&lambda;_2)
	 * @deprecated
	 */
	public double getEigenvalue2() {
		return this.eVals[1];
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
		return this.eVecs;
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
		return this.eVecs[k];
	}
	
	@Deprecated
	public double[] getEigenvector1() {
		return this.eVecs[0];
	}
	
	@Deprecated
	public double[] getEigenvector2() {
		return this.eVecs[1];
	}
	
	@Override
	public String toString() {
		if (this.isReal) {
			return String.format("<%.4f, %.4f, %s, %s>", 
				eVals[0], eVals[1], Matrix.toString(eVecs[0]),  Matrix.toString(eVecs[1]));
		}
		else {
			return "not real";
		}
	}
		
}
