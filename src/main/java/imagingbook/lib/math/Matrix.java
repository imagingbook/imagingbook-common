/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/

package imagingbook.lib.math;

import imagingbook.lib.settings.PrintPrecision;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;

/**
 * This class contains a collection of static methods for calculations
 * with vectors and matrices using native Java arrays without any enclosing 
 * objects structures. 
 * Matrices are simply two-dimensional array M[r][c], where r is the row index
 * and c is the column index (as common in linear algebra). This means that
 * matrices are really vectors of row vectors.
 * TODO: need to differentiate functional and side-effect methods!
 * @author WB
 * @version 2014/12/04
 */

public abstract class Matrix {
	
	static {
		Locale.setDefault(Locale.US);
	}
	
	// Vector and matrix creation

	public static double[] createDoubleVector(int length) {
		return new double[length];
	}
	
	public static float[] createFloatVector(int length) {
		return new float[length];
	}
	
	public static double[][] createDoubleMatrix(int rows, int columns) {
		return new double[rows][columns];
	}
	
	public static float[][] createFloatMatrix(int rows, int columns) {
		return new float[rows][columns];
	}
	
	// vector/matrix creation:
	
	public static double[] zeroVector(int size) {
		return new double[size];
	}
	
	public static double[][] idMatrix(int size) {
		double[][] A = new double[size][size];
		for (int i = 0; i < size; i++) {
			A[i][i] = 1;
		}
		return A;
	}
	
	// Matrix properties -------------------------------------

	public static int getNumberOfRows(double[][] A) {
		return A.length;
	}
	
	public static int getNumberOfColumns(double[][] A) {
		return A[0].length;
	}
	
	public static int getNumberOfRows(float[][] A) {
		return A.length;
	}
	
	public static int getNumberOfColumns(float[][] A) {
		return A[0].length;
	}
	
	// extract rows or columns
	
	public static double[] getRow(double[][] A, int r) {
		return A[r].clone();
	}
	
	public static double[] getColumn(double[][] A, int c) {
		final int rows = A.length;
		double[] col = new double[rows];
		for (int r = 0; r < rows; r++) {
			col[r] = A[r][c];
		}
		return col;
	}

	// Matrix and vector duplication ------------------------------

	public static double[] duplicate(final double[] A) {
		return A.clone();
	}
	
	public static float[] duplicate(final float[] A) {
		return A.clone();
	}

	public static double[][] duplicate(final double[][] A) {
		final int m = A.length;
		final double[][] B = new double[m][];
		for (int i = 0; i < m; i++) {
			B[i] = A[i].clone();
		}
		return B;
	}
	
	public static float[][] duplicateToFloat(final double[][] A) {
		final int m = A.length;
		final int n = A[0].length;
		final float[][] B = new float[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				B[i][j] = (float) A[i][j];
			}
		}
		return B;
	}
	
	public static float[][] duplicate(final float[][] A) {
		final int m = A.length;
		float[][] B = new float[m][];
		for (int i = 0; i < m; i++) {
			B[i] = A[i].clone();
		}
		return B;
	}
	
	public static double[][] duplicateToDouble(final float[][] A) {
		final int m = A.length;
		final int n = A[0].length;
		final double[][] B = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				B[i][j] = A[i][j];
			}
		}
		return B;
	}
	
	// Element-wise arithmetic -------------------------------
	
	//TODO: change to multiple args: public static int[] add(int[]... as)
	
	public static int[] add(int[] a, int[] b) {
		int[] c = new int[a.length];
		for (int i = 0; i < a.length; i++) {
			c[i] = c[i] + b[i];
		}
		return c;
	}
	
	public static double[] add(double[] a, double[] b) {
		double[] c = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] + b[i];
		}
		return c;
	}

	public static double[][] add(double[][] A, double[][] B) {
		final int m = A.length;
		final int n = A[0].length;
		double[][] C = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j] + B[i][j];
			}
		}
		return C;
	}
	
	public static double[] subtract(double[] a, double[] b) {
		double[] c = a.clone();
		for (int i = 0; i < a.length; i++) {
			c[i] = c[i] - b[i];
		}
		return c;
	}
	
	public static double[] subtract(double[] a, int[] b) {
		double[] c = a.clone();
		for (int i = 0; i < a.length; i++) {
			c[i] = c[i] - b[i];
		}
		return c;
	}
	
	public static int[] floor(double[] a) {
		int[] b = new int[a.length];
		for (int i = 0; i < a.length; i++) {
			b[i] = (int) Math.floor(a[i]);
		}
		return b;
	}

	// Scalar multiplications -------------------------------

	// non-destructive
	public static double[] multiply(final double s, final double[] a) {
		double[] b = a.clone();
		multiplyD(s, b);
		return b;
	}
	
	// destructive
	public static void multiplyD(final double s, final double[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = a[i] * s;
		}
	}
	

	// non-destructive
	public static double[][] multiply(final double s, final double[][] A) {
		double[][] B = duplicate(A);
		multiplyD(s, B);
		return B;
	}
	
	public static void multiplyD(final double s, final double[][] A) {
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				A[i][j] = A[i][j] * s;
			}
		}
	}
	
	// non-destructive
	public static float[] multiply(final float s, final float[] A) {
		float[] B = duplicate(A);
		multiplyD(s, B);
		return B;
	}
	
	// destructive
	public static void multiplyD(final float s, final float[] A) {
		for (int i = 0; i < A.length; i++) {
			A[i] = A[i] * s;
		}
	}

	// non-destructive
	public static float[][] multiply(final float s, final float[][] A) {
		float[][] B = duplicate(A);
		for (int i = 0; i < B.length; i++) {
			for (int j = 0; j < B[i].length; j++) {
				B[i][j] = B[i][j] * s;
			}
		}
		return B;
	}
	
	// destructive
	public static void multiplyD(final float s, final float[][] A) {
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				A[i][j] = A[i][j] * s;
			}
		}
	}
	
	// matrix-vector multiplications ----------------------------------------

	// non-destructive
	public static double[] multiply(final double[] x, final double[][] A) {
		double[] Y = new double[getNumberOfColumns(A)];
		multiplyD(x, A, Y);
		return Y;
	}
	
	/*  
	 * Implements a right (post-) matrix-vector multiplication: x . A -> y
	 * x is treated as a row vector of length m, matrix A is of size (m,n).
	 * y (a row vector of length n) is modified.
	 * destructive
	 */
	public static void multiplyD(final double[] x, final double[][] A, double[] y) {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (x.length != m || y.length != n) 
			throw new IllegalArgumentException("incompatible vector-matrix dimensions");
		for (int i = 0; i < n; i++) {
			double s = 0;
			for (int j = 0; j < m; j++) {
				s = s + x[j] * A[j][i];
			}
			y[i] = s;
		}
	}
	

	/*  
	 * implements a left (pre-) matrix-vector multiplication:  A . x -> y
	 * Matrix A is of size (m,n), column vector x is of length n.
	 * The result y is a column vector of length m.
	 * non-destructive
	 */
	public static double[] multiply(final double[][] A, final double[] x) {
		double[] y = new double[getNumberOfRows(A)];
		multiplyD(A, x, y);
		return y;
	}
	
	// destructive
	public static void multiplyD(final double[][] A, final double[] x, double[] y) {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (x.length != n || y.length != m) 
			throw new IllegalArgumentException("incompatible matrix-vector dimensions");
		for (int i = 0; i < m; i++) {
			double s = 0;
			for (int j = 0; j < n; j++) {
				s = s + A[i][j] * x[j];
			}
			y[i] = s;
		}
	}
	
	// non-destructive
	public static float[] multiply(final float[][] A, final float[] x) {
		float[] y = new float[getNumberOfRows(A)];
		multiplyD(A, x, y);
		return y;
	}
	
	/**
	 * Matrix-vector product: A . x = y 
	 * All arguments must be appropriately sized. destructive
	 * @param A matrix of size m,n (input)
	 * @param x vector of length n (input)
	 * @param y vector of length m (result)
	 */
	public static void multiplyD(final float[][] A, final float[] x, float[] y) {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (x.length != n || y.length != m) 
			throw new IllegalArgumentException("incompatible matrix-vector dimensions");
		for (int i = 0; i < m; i++) {
			double s = 0;
			for (int j = 0; j < n; j++) {
				s = s + A[i][j] * x[j];
			}
			y[i] = (float) s;
		}
	}
	

	
	// Matrix-matrix products ---------------------------------------
	
	// returns A * B (non-destructive)
	public static double[][] multiply(final double[][] A, final double[][] B) {
		int m = getNumberOfRows(A);
		int q = getNumberOfColumns(B);
		double[][] C = createDoubleMatrix(m, q);
		multiplyD(A, B, C);
		return C;
	}
	
	// A * B -> C (destructive)
	public static void multiplyD(final double[][] A, final double[][] B, final double[][] C) {
		final int mA = getNumberOfRows(A);
		final int nA = getNumberOfColumns(A);
		final int nB = getNumberOfColumns(B);
		for (int i = 0; i < mA; i++) {
			for (int j = 0; j < nB; j++) {
				double s = 0;
				for (int k = 0; k < nA; k++) {
					s = s + A[i][k] * B[k][j];
				}
				C[i][j] = s;
			}
		}
	}
	
	// returns A * B (non-destructive)
	public static float[][] multiply(final float[][] A, final float[][] B) {
		// TODO: also check nA = mB
		final int mA = getNumberOfRows(A);
		final int nB = getNumberOfColumns(B);
		float[][] C = createFloatMatrix(mA, nB);
		multiply(A, B, C);
		return C;
	}

	// A * B -> C (destructive)
	public static void multiply(final float[][] A, final float[][] B, final float[][] C) {
		final int mA = getNumberOfRows(A);
		final int nA = getNumberOfColumns(A);
		final int mB = getNumberOfRows(B);
		final int nB = getNumberOfColumns(B);
		if (nA != mB)
			throw new IllegalArgumentException("Matrix.multiply: wrong row/col dimensions");
		for (int i = 0; i < mA; i++) {
			for (int j = 0; j < nB; j++) {
				float s = 0;
				for (int k = 0; k < nA; k++) {
					s = s + A[i][k] * B[k][j];
				}
				C[i][j] = s;
			}
		}
	}
	
	// Vector-vector products ---------------------------------------
	
	// A is considered a row vector, B is a column vector, both of length n.
	// returns a scalar vale.
	public static double dotProduct(final double[] A, final double[] B) {
		double sum = 0;
		for (int i = 0; i < A.length; i++) {
			sum = sum + A[i] * B[i];
		}
		return sum;
	}
	
	// A is considered a column vector, B is a row vector, of length m, n, resp.
	// returns a matrix M of size (m,n).
	public static double[][] outerProduct(final double[] A, final double[] B) {
		final int m = A.length;
		final int n = B.length;
		final double[][] M = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				M[i][j] = A[i] * B[j];
			}
		}
		return M;
	}

	//  Vector norms ---------------------------------------------------

	public static double normL1(final double[] A) {
		double sum = 0;
		for (double x : A) {
			sum = sum + Math.abs(x);
		}
		return sum;
	}

	public static double normL2(final double[] A) {
		return Math.sqrt(normL2squared(A));
	}

	public static double normL2squared(final double[] A) {
		double sum = 0;
		for (double x : A) {
			sum = sum + (x * x);
		}
		return sum;
	}
	
	public static float normL1(final float[] A) {
		double sum = 0;
		for (double x : A) {
			sum = sum + Math.abs(x);
		}
		return (float) sum;
	}

	public static float normL2(final float[] A) {
		return (float) Math.sqrt(normL2squared(A));
	}

	public static float normL2squared(final float[] A) {
		double sum = 0;
		for (double x : A) {
			sum = sum + (x * x);
		}
		return (float) sum;
	}

	// Summation --------------------------------------------------

	public static double sum(final double[] A) {
		double sum = 0;
		for (int i = 0; i < A.length; i++) {
			sum = sum + A[i];
		}
		return sum;
	}
	
	public static double sum(final double[][] A) {
		double sum = 0;
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				sum = sum + A[i][j];
			}
		}
		return sum;
	}
	
	public static float sum(final float[] A) {
		double sum = 0;
		for (int i = 0; i < A.length; i++) {
			sum = sum + A[i];
		}
		return (float) sum;
	}
	
	public static double sum(final float[][] A) {
		double sum = 0;
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				sum = sum + A[i][j];
			}
		}
		return sum;
	}
	
	// Min/max of vectors ------------------------
	
	public static float min(final float[] A) {
		float minval = Float.POSITIVE_INFINITY;
		for (float val : A) {
			if (val < minval) {
				minval = val;
			}
		}
		return minval;
	}
	
	
	public static double min(final double[] A) {
		double minval = Double.POSITIVE_INFINITY;
		for (double val : A) {
			if (val < minval) {
				minval = val;
			}
		}
		return minval;
	}
	
	public static float max(final float[] A) {
		float maxval = Float.NEGATIVE_INFINITY;
		for (float val : A) {
			if (val > maxval) {
				maxval = val;
			}
		}
		return maxval;
	}
	
	
	public static double max(final double[] A) {
		double maxval = Double.NEGATIVE_INFINITY;
		for (double val : A) {
			if (val > maxval) {
				maxval = val;
			}
		}
		return maxval;
	}
	
	// Vector concatenation -----------------------
	
	public static float[] concatenate(float[]... as) {
		List<Float> vlist = new ArrayList<Float>();
		for (float[] a : as) {
			for (float val : a) {
				vlist.add(val);
			}
		}
		
		float[] va = new float[vlist.size()];
		int i = 0;
		for (float val : vlist) {
			va[i] = val;
			i++;
		}
		return va;
	}
	
	public static double[] concatenate(double[]... as) {
		List<Double> vlist = new ArrayList<Double>();
		for (double[] a : as) {
			for (double val : a) {
				vlist.add(val);
			}
		}
		
		double[] va = new double[vlist.size()];
		int i = 0;
		for (double val : vlist) {
			va[i] = val;
			i++;
		}
		return va;
	}
	
	// Determinants --------------------------------------------
	
	public static float determinant2x2(final float[][] A) {
		return A[0][0] * A[1][1] - A[0][1] * A[1][0];
	}
	
	public static double determinant2x2(final double[][] A) {
		return A[0][0] * A[1][1] - A[0][1] * A[1][0];
	}
	
	public static float determinant3x3(final float[][] A) {
		return
				A[0][0] * A[1][1] * A[2][2] +
				A[0][1] * A[1][2] * A[2][0] +
				A[0][2] * A[1][0] * A[2][1] -
				A[2][0] * A[1][1] * A[0][2] -
				A[2][1] * A[1][2] * A[0][0] -
				A[2][2] * A[1][0] * A[0][1] ;
	}

	public static double determinant3x3(final double[][] A) {
		return 
			A[0][0] * A[1][1] * A[2][2] + 
			A[0][1] * A[1][2] * A[2][0]	+ 
			A[0][2] * A[1][0] * A[2][1] - 
			A[2][0] * A[1][1] * A[0][2] - 
			A[2][1] * A[1][2] * A[0][0] - 
			A[2][2] * A[1][0] * A[0][1] ;
	}

	// Matrix transposition ---------------------------------------
	
	public static float[][] transpose(float[][] A) {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		float[][] At = new float[n][m];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				At[j][i] = A[i][j];
			}
		}
		return At;
	}
	
	public static double[][] transpose(double[][] A) {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		double[][] At = new double[n][m];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				At[j][i] = A[i][j];
			}
		}
		return At;
	}
	
	// Matrix Froebenius norm ---------------------------------------
	
	public static double froebeniusNorm(final double[][] A) {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		double s = 0;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				s = s + A[i][j] * A[i][j];
			}
		}
		return Math.sqrt(s);
	}
	
	// Matrix trace ---------------------------------------
	
	public static double trace(final double[][] A) {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (m != n) 
			throw new IllegalArgumentException("square matrix expected");
		double s = 0;
		for (int i = 0; i < m; i++) {
				s = s + A[i][i];
		}
		return s;
	}
	
	
	// Matrix inversion ---------------------------------------
	
	
	/**
	 * @param A a square matrix.
	 * @return the inverse of A or null if A is non-square or singular.
	 */
	public static double[][] inverse(final double[][] A) {
		RealMatrix M = MatrixUtils.createRealMatrix(A);
		if (!M.isSquare())
			return null;
		else {
			double[][] Ai = null;
			try {
				RealMatrix Mi = MatrixUtils.inverse(M); //new LUDecomposition(M).getSolver().getInverse();
				Ai = Mi.getData();
			} catch (SingularMatrixException e) {}
			return Ai;
		}
	}
	
	public static float[][] inverse(final float[][] A) {
		double[][] B = duplicateToDouble(A);
		double[][] Bi = inverse(B);
		if (Bi == null)
			return null;
		else
			return duplicateToFloat(Bi);
	}
	
	// numerically stable?
	@Deprecated
	public static float[][] inverse2x2(final float[][] A) {
		float[][] B = duplicate(A);
		final double det = determinant2x2(B);
		if (Math.abs(det) < Arithmetic.EPSILON_DOUBLE)
			return null;
		else {
			final double a00 = B[0][0];
			final double a01 = B[0][1];
			final double a10 = B[1][0];
			final double a11 = B[1][1];
			B[0][0] = (float) ( a11 / det);
			B[0][1] = (float) (-a01 / det);
			B[1][0] = (float) (-a10 / det);
			B[1][1] = (float) ( a00 / det);
			return B;
		}
	}

	// numerically stable?
	@Deprecated
	public static double[][] inverse2x2(final double[][] A) {
		double[][] B = duplicate(A);
		final double det = determinant2x2(B);
		if (Math.abs(det) < Arithmetic.EPSILON_DOUBLE)
			return null;
		else {
			final double a00 = B[0][0];
			final double a01 = B[0][1];
			final double a10 = B[1][0];
			final double a11 = B[1][1];
			B[0][0] =  a11 / det;
			B[0][1] = -a01 / det;
			B[1][0] = -a10 / det;
			B[1][1] =  a00 / det;
			return B;
		}
	}

	// use general method, i.e. double[][] inverse(double[][] A)
	@Deprecated
	public static double[][] inverse3x3(final double[][] A) {
		double[][] B = duplicate(A);
		final double det = determinant3x3(B);
		if (Math.abs(det) < Arithmetic.EPSILON_DOUBLE)
			return null;
		else {
			final double a00 = B[0][0];
			final double a01 = B[0][1];
			final double a02 = B[0][2];
			final double a10 = B[1][0];
			final double a11 = B[1][1];
			final double a12 = B[1][2];
			final double a20 = B[2][0];
			final double a21 = B[2][1];
			final double a22 = B[2][2];
			B[0][0] = (a11 * a22 - a12 * a21) / det;
			B[0][1] = (a02 * a21 - a01 * a22) / det;
			B[0][2] = (a01 * a12 - a02 * a11) / det;

			B[1][0] = (a12 * a20 - a10 * a22) / det;
			B[1][1] = (a00 * a22 - a02 * a20) / det;
			B[1][2] = (a02 * a10 - a00 * a12) / det;

			B[2][0] = (a10 * a21 - a11 * a20) / det;
			B[2][1] = (a01 * a20 - a00 * a21) / det;
			B[2][2] = (a00 * a11 - a01 * a10) / det;
			return B;
		}
	}
	
	// numerically stable? should be replaced by standard inversion
	@Deprecated
	public static float[][] inverse3x3(final float[][] A) {
		final float[][] B = duplicate(A);
		final double det = determinant3x3(B);
		// IJ.log("   determinant = " + det);
		if (Math.abs(det) < Arithmetic.EPSILON_DOUBLE)
			return null;
		else {
			final double a00 = B[0][0];
			final double a01 = B[0][1];
			final double a02 = B[0][2];
			final double a10 = B[1][0];
			final double a11 = B[1][1];
			final double a12 = B[1][2];
			final double a20 = B[2][0];
			final double a21 = B[2][1];
			final double a22 = B[2][2];
			B[0][0] = (float) ((a11 * a22 - a12 * a21) / det);
			B[0][1] = (float) ((a02 * a21 - a01 * a22) / det);
			B[0][2] = (float) ((a01 * a12 - a02 * a11) / det);

			B[1][0] = (float) ((a12 * a20 - a10 * a22) / det);
			B[1][1] = (float) ((a00 * a22 - a02 * a20) / det);
			B[1][2] = (float) ((a02 * a10 - a00 * a12) / det);

			B[2][0] = (float) ((a10 * a21 - a11 * a20) / det);
			B[2][1] = (float) ((a01 * a20 - a00 * a21) / det);
			B[2][2] = (float) ((a00 * a11 - a01 * a10) / det);
			return B;
		}
	}
	
	// ------------------------------------------------------------------------
	
	// Finds the EXACT solution x for A.x = b
	public static double[] solve(final double[][] A, double[] b) {
		RealMatrix AA = MatrixUtils.createRealMatrix(A);
		RealVector bb = MatrixUtils.createRealVector(b);
		DecompositionSolver solver = new LUDecomposition(AA).getSolver();
		double[] x = null;
		try {
			x = solver.solve(bb).toArray();
		} catch (SingularMatrixException e) {}
		return x;
	}
	
	// Output to streams and strings ------------------------------------------
	
//	static int defaultPrintPrecision = 3;
//	static int printPrecision = defaultPrintPrecision;
////	static String fStr;
//	static {
//		 resetPrintPrecision();
//	}
//	
//	public static void resetPrintPrecision() {
//		setPrintPrecision(defaultPrintPrecision);
//	}
//	
//	public static void setPrintPrecision(int nDigits) {
//		printPrecision = Math.max(nDigits, 0);
//		if (nDigits > 0) {
//			fStr = "%." + printPrecision + "f"; // e.g. "%.5f"
//		}
//		else {
//			fStr = "%e";	// use scientific format - OK?
//		}
//	}
//	
//	public static int getPrintPrecision() {
//		return printPrecision;
//	}
	
	public static String toString(double[] A) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	public static void printToStream(double[] A, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("{");
		for (int i = 0; i < A.length; i++) {
			if (i > 0)
				strm.format(", ");
			strm.format(fStr, A[i]);
		}
		strm.format("}");
		strm.flush();
	}
	
	public static String toString(double[][] A) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	public static void printToStream(double[][] A, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("{");
		for (int i=0; i< A.length; i++) {
			if (i == 0)
				strm.format("{");
			else
				strm.format(", \n{");
			for (int j=0; j< A[i].length; j++) {
				if (j == 0) 
					strm.format(fStr, A[i][j]);
				else
					strm.format(", " + fStr, A[i][j]);
			}
			strm.format("}");
		}
		strm.format("}");
		strm.flush();
	}
	
	public static String toString(float[] A) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	public static void printToStream(float[] A, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("{");
		for (int i = 0; i < A.length; i++) {
			if (i > 0)
				strm.format(", ");
			strm.format(fStr, A[i]);
		}
		strm.format("}");
		strm.flush();
	}

	public static String toString(float[][] A) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	public static void printToStream(float[][] A, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("{");
		for (int i=0; i< A.length; i++) {
			if (i == 0)
				strm.format("{");
			else
				strm.format(", \n{");
			for (int j=0; j< A[i].length; j++) {
				if (j == 0) 
					strm.format(fStr, A[i][j]);
				else
					strm.format(", " + fStr, A[i][j]);
			}
			strm.format("}");
		}
		strm.format("}");
		strm.flush();
	}

	//--------------------------------------------------------------------------
	

	public static void main(String[] args) {
		float[][] A = {
				{ -1, 2, 3 }, 
				{  4, 5, 6 }, 
				{  7, 8, 9 }};

		System.out.println("A = \n" + toString(A));
		System.out.println("A rows = " + getNumberOfRows(A));
		System.out.println("A columns = " + getNumberOfColumns(A));
		int row = 1;
		int column = 2;
		System.out.println("A[1][2] = " + A[row][column]);

		System.out.println("det=" + determinant3x3(A));
		float[][] Ai = inverse3x3(A);
		toString(Ai);

		double[][] B = {{ -1, 2, 3 }, { 4, 5, 6 }};
		System.out.println("B = \n" + toString(B));
		System.out.println("B rows = " + getNumberOfRows(B));
		System.out.println("B columns = " + getNumberOfColumns(B));
		
		PrintPrecision.set(5);

		double[][] C = new double[2][3];
		System.out.println("C rows = " + getNumberOfRows(C));
		System.out.println("C columns = " + getNumberOfColumns(C));

		RealMatrix Ba = MatrixUtils.createRealMatrix(B);
		System.out.println("Ba = " + Ba.toString());
		
		float[] v1 = {1,2,3};
		float[] v2 = {4,5,6,7};
		float[] v3 = {8};
		float[] v123 = concatenate(v1, v2, v3);
		System.out.println("v123 = \n" + toString(v123));
		
		System.out.println("mind1 = " + Matrix.min(new double[] {-20,30,60,-40, 0}));
		System.out.println("maxd2 = " + Matrix.max(new double[] {-20,30,60,-40, 0}));
		System.out.println("minf1 = " + Matrix.min(new float[] {-20,30,60,-40, 0}));
		System.out.println("maxf2 = " + Matrix.max(new float[] {-20,30,60,-40, 0}));
		
	}
}
