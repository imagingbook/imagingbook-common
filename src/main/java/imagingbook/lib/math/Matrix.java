/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.math;

import static imagingbook.lib.math.Arithmetic.isZero;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import imagingbook.lib.math.Arithmetic.DivideByZeroException;
import imagingbook.lib.settings.PrintPrecision;

/**
 * <p>This class defines a set of static methods for calculations
 * with vectors and matrices using native Java arrays without any enclosing 
 * objects structures. 
 * Matrices are simply two-dimensional arrays A[r][c], where r is the row index
 * and c is the column index (as common in linear algebra). This means that
 * matrices are really vectors of row vectors.
 * Only arrays of type {@code float} and {@code double} are supported.
 * All matrices are assumed to be rectangular (i.e., all rows are of equal length).</p>
 * 
 * <p>Methods named with a trailing 'D' (e.g., {@code multiplyD}) operate destructively,
 * i.e., modify one of the passed arguments.</p>
 * 
 * <p>Most methods are self-explanatory and are therefore left undocumented.</p>
 * 
 * @author W. Burger
 * @version 2019/01/05
 */
public abstract class Matrix {
	
	/** Locale used for printing decimal numbers. */
	public static Locale PrintLocale = Locale.US;
	/** Character used to separate successive vector and matrix elements. */
	public static char SeparationChar = ',';
	/** Leading delimiter used for lists of vector and matrix elements. */
	public static char LeftDelimitChar = '{';
	/** Trailing delimiter used for lists of vector and matrix elements. */
	public static char RightDelimitChar = '}';
	
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
	
	// Specific vector/matrix creation:
	
	/**
	 * Creates and returns a new zero-valued vector of the 
	 * specified length.
	 * @param length the length of the vector
	 * @return a vector with zero values
	 */
	public static double[] zeroVector(int length) {
		if (length < 1)
			throw new IllegalArgumentException("vector size cannot be < 1");
		return new double[length];
	}
	
	/**
	 * Creates and returns a new identity matrix of the 
	 * specified size.
	 * @param size the size of the matrix
	 * @return an identity matrix
	 */
	public static double[][] idMatrix(int size) {
		if (size < 1)
			throw new IllegalArgumentException("matrix size cannot be < 1");
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
	
	// Extract rows or columns
	
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
	
	public static boolean isSquare(double[][] A) {
		return A.length > 0 && A.length == A[0].length;
	}
	
	public static boolean isSquare(float[][] A) {
		return A.length > 0 && A.length == A[0].length;
	}
	
	public static boolean sameSize(double[] a, double[] b) {
		return a.length == b.length;
	}
	
	public static boolean sameSize(float[] a, float[] b) {
		return a.length == b.length;
	}
	
	public static boolean sameSize(double[][] A, double[][] B) {
		return (A.length == B.length) && (A[0].length == B[0].length);
	}
	
	public static boolean sameSize(float[][] A, float[][] B) {
		return (A.length == B.length) && (A[0].length == B[0].length);
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
	
	public static float[][] duplicate(final float[][] A) {
		final int m = A.length;
		float[][] B = new float[m][];
		for (int i = 0; i < m; i++) {
			B[i] = A[i].clone();
		}
		return B;
	}
	
	public static float[][] toFloat(final double[][] A) {
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
	
	public static double[][] toDouble(final float[][] A) {
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
	
	public static double[] add(double[] a, double[] b) {
		if (!sameSize(a, b))
			throw new IncompatibleDimensionsException();
		final int n = a.length;
		double[] c = new double[n];
		for (int i = 0; i < n; i++) {
			c[i] = a[i] + b[i];
		}
		return c;
	}

	public static double[][] add(double[][] A, double[][] B) {
		if (!sameSize(A, B))
			throw new IncompatibleDimensionsException();
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
		if (!sameSize(a, b))
			throw new IncompatibleDimensionsException();
		final int n = a.length;
		double[] c = new double[n];
		for (int i = 0; i < n; i++) {
			c[i] = a[i] - b[i];
		}
		return c;
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
		multiplyD(s, B);
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

	/**
	 * Multiplies a vector x with a matrix A from the right, i.e., y = x * A,
	 * where x is treated as a row vector and the result y is also a row vector.
	 * @param x a (row) vector of length m
	 * @param A a matrix of size (m,n)
	 * @return a (row) vector of length n
	 */
	public static double[] multiply(final double[] x, final double[][] A) {
		double[] y = new double[getNumberOfColumns(A)];
		multiplyD(x, A, y);
		return y;
	}
	
	/**
	 * Destructive version of {@link #multiply(double[], double[][])}.
	 * @param x a (row) vector of length m
	 * @param A matrix of size (m,n)
	 * @param y a (row) vector of length n
	 */
	public static void multiplyD(final double[] x, final double[][] A, double[] y) {
		if (x == y) 
			throw new SameSourceTargetException();
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (x.length != m || y.length != n) 
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < n; i++) {
			double s = 0;
			for (int j = 0; j < m; j++) {
				s = s + x[j] * A[j][i];
			}
			y[i] = s;
		}
	}

	/**
	 * Multiplies a matrix A with a vector x from the right, i.e., y = A * x,
	 * where x is treated as a column vector and the result y is also a column vector.
	 * @param x a (column) vector of length n
	 * @param A a matrix of size (m,n)
	 * @return a (column) vector of length m
	 */
	public static double[] multiply(final double[][] A, final double[] x) {
		double[] y = new double[getNumberOfRows(A)];
		multiplyD(A, x, y);
		return y;
	}
	
	/**
	 * Destructive version of {@link #multiply(double[][], double[])}.
	 * @param A matrix of size (m,n)
	 * @param x a (column) vector of length n
	 * @param y a (column) vector of length m
	 */
	public static void multiplyD(final double[][] A, final double[] x, double[] y) {
		if (x == y) 
			throw new SameSourceTargetException();
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (x.length != n || y.length != m) 
			throw new IncompatibleDimensionsException();
		for (int i = 0; i < m; i++) {
			double s = 0;
			for (int j = 0; j < n; j++) {
				s = s + A[i][j] * x[j];
			}
			y[i] = s;
		}
	}
	
	/**
	 * Multiplies a matrix A with a vector x from the right, i.e., y = A * x,
	 * where x is treated as a column vector and the result y is also a column vector.
	 * @param x a (column) vector of length n
	 * @param A a matrix of size (m,n)
	 * @return a (column) vector of length m
	 */
	public static float[] multiply(final float[][] A, final float[] x) {
		float[] y = new float[getNumberOfRows(A)];
		multiplyD(A, x, y);
		return y;
	}
	
	/**
	 * Destructive version of {@link #multiply(float[][], float[])}.
	 * @param A matrix of size (m,n)
	 * @param x a (column) vector of length n
	 * @param y a (column) vector of length m
	 */
	public static void multiplyD(final float[][] A, final float[] x, float[] y) {
		if (x == y) 
			throw new SameSourceTargetException();
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (x.length != n || y.length != m) 
			throw new IncompatibleDimensionsException();
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
		if (A == C || B == C) 
			throw new SameSourceTargetException();
		final int mA = getNumberOfRows(A);
		final int nA = getNumberOfColumns(A);
		final int mB = getNumberOfRows(B);
		final int nB = getNumberOfColumns(B);
		if (nA != mB)
			throw new IncompatibleDimensionsException();
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
		multiplyD(A, B, C);
		return C;
	}

	// A * B -> C (destructive)
	public static void multiplyD(final float[][] A, final float[][] B, final float[][] C) {
		if (A == C || B == C) 
			throw new SameSourceTargetException();
		final int mA = getNumberOfRows(A);
		final int nA = getNumberOfColumns(A);
		final int mB = getNumberOfRows(B);
		final int nB = getNumberOfColumns(B);
		if (nA != mB)
			throw new IncompatibleDimensionsException();
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
	
	/**
	 * Calculates and returns the dot (inner or scalar) product of two vectors,
	 * which must have the same length.
	 * @param x first vector
	 * @param y second vector
	 * @return the dot product
	 */
	public static double dotProduct(final double[] x, final double[] y) {
		if (!sameSize(x, y))
			throw new IncompatibleDimensionsException();
		double sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum = sum + x[i] * y[i];
		}
		return sum;
	}
	
	// A is considered a column vector, B is a row vector, of length m, n, resp.
	// returns a matrix M of size (m,n).
	/**
	 * Calculates and returns the outer product of two vectors, which is a
	 * matrix of size (m,n), where m is the length of the first vector and
	 * m is the length of the second vector.
	 * @param x first vector (of length m)
	 * @param y second vector (of length n)
	 * @return the outer product (matrix)
	 */
	public static double[][] outerProduct(final double[] x, final double[] y) {
		final int m = x.length;
		final int n = y.length;
		final double[][] M = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				M[i][j] = x[i] * y[j];
			}
		}
		return M;
	}

	//  Vector norms ---------------------------------------------------

	/**
	 * Calculates and returns the L1 norm of the given vector.
	 * @param x a vector
	 * @return the L1 norm of the vector
	 */
	public static double normL1(final double[] x) {
		double sum = 0;
		for (double val : x) {
			sum = sum + Math.abs(val);
		}
		return sum;
	}

	/**
	 * Calculates and returns the L2 norm of the given vector.
	 * @param x a vector
	 * @return the L2 norm of the vector
	 */
	public static double normL2(final double[] x) {
		return Math.sqrt(normL2squared(x));
	}

	/**
	 * Calculates and returns the squared L2 norm of the given vector.
	 * The squared norm is less costly to calculate (no square root
	 * needed) than the L2 norm and is thus often used for efficiency. 
	 * @param x a vector
	 * @return the squared L2 norm of the vector
	 */
	public static double normL2squared(final double[] x) {
		double sum = 0;
		for (double val : x) {
			sum = sum + (val * val);
		}
		return sum;
	}
	
	/**
	 * Calculates and returns the L1 norm of the given vector.
	 * @param x a vector
	 * @return the L1 norm of the vector
	 */
	public static float normL1(final float[] x) {
		double sum = 0;
		for (double val : x) {
			sum = sum + Math.abs(val);
		}
		return (float) sum;
	}

	/**
	 * Calculates and returns the L2 norm of the given vector.
	 * @param x a vector
	 * @return the L2 norm of the vector
	 */
	public static float normL2(final float[] x) {
		return (float) Math.sqrt(normL2squared(x));
	}

	/**
	 * Calculates and returns the squared L2 norm of the given vector.
	 * The squared norm is less costly to calculate (no square root
	 * needed) than the L2 norm and is thus often used for efficiency. 
	 * @param x a vector
	 * @return the squared L2 norm of the vector
	 */
	public static float normL2squared(final float[] x) {
		double sum = 0;
		for (double val : x) {
			sum = sum + (val * val);
		}
		return (float) sum;
	}
	
	// Matrix (Froebenius) norm ---------------------------------------
	
	/**
	 * Calculates and returns the Froebenius norm of the given matrix.
	 * @param A a matrix
	 * @return the norm of the matrix
	 */
	public static double norm(final double[][] A) {
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

	// Summation --------------------------------------------------

	public static double sum(final double[] x) {
		double sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum = sum + x[i];
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
	
	public static float sum(final float[] x) {
		double sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum = sum + x[i];
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
	
	// --------------------------------------------------
	
	/**
	 * Calculates the sums of all matrix columns and returns 
	 * them as a vector.
	 * @param A  a matrix
	 * @return a vector containing the sums of all matrix columns
	 */
	public static double[] sumColumns(final double[][] A) {
		double[] sumVec = new double[getNumberOfRows(A)];
		for (int r = 0; r < sumVec.length; r++) {
			double sum = 0;
			for (int c = 0; c < A[r].length; c++) {
				sum = sum + A[r][c];
			}
			sumVec[r] = sum;
		}
		return sumVec;
	}
	
	/**
	 * Calculates the sums of all matrix columns and returns 
	 * them as a vector.
	 * @param A a matrix
	 * @return a vector containing the sums of all matrix columns
	 */
	public static float[] sumColumns(final float[][] A) {
		float[] sumVec = new float[getNumberOfRows(A)];
		for (int r = 0; r < sumVec.length; r++) {
			double sum = 0;
			for (int c = 0; c < A[r].length; c++) {
				sum = sum + A[r][c];
			}
			sumVec[r] = (float) sum;
		}
		return sumVec;
	}
	
	/**
	 * Calculates the sums of all matrix rows.
	 * @param A The input matrix
	 * @return A vector with sum of all matrix rows
	 */
	public static double[] sumRows(final double[][] A) {
		double[] sumVec = new double[getNumberOfColumns(A)];
		for (int c = 0; c < sumVec.length; c++) {
			double sum = 0;
			for (int r = 0; r < A.length; r++) {
				sum = sum + A[r][c];
			}
			sumVec[c] = sum;
		}
		return sumVec;
	}
	
	/**
	 * Calculates the sums of all matrix rows.
	 * @param A The input matrix
	 * @return A vector with sum of all matrix rows
	 */
	public static float[] sumRows(final float[][] A) {
		float[] sumVec = new float[getNumberOfColumns(A)];
		for (int c = 0; c < sumVec.length; c++) {
			double sum = 0;
			for (int r = 0; r < A.length; r++) {
				sum = sum + A[r][c];
			}
			sumVec[c] = (float) sum;
		}
		return sumVec;
	}
	
	// min/max of vectors ------------------------
	
	public static float min(final float[] x) {
		if (x.length == 0)
			throw new ZeroLengthVectorException();
		float minval = Float.POSITIVE_INFINITY;
		for (float val : x) {
			if (val < minval) {
				minval = val;
			}
		}
		return minval;
	}
	
	public static double min(final double[] x) {
		if (x.length == 0)
			throw new ZeroLengthVectorException();
		double minval = Double.POSITIVE_INFINITY;
		for (double val : x) {
			if (val < minval) {
				minval = val;
			}
		}
		return minval;
	}
	
	public static float max(final float[] x) {
		if (x.length == 0)
			throw new ZeroLengthVectorException();
		float maxval = Float.NEGATIVE_INFINITY;
		for (float val : x) {
			if (val > maxval) {
				maxval = val;
			}
		}
		return maxval;
	}
	
	public static double max(final double[] x) {
		if (x.length == 0)
			throw new ZeroLengthVectorException();
		double maxval = Double.NEGATIVE_INFINITY;
		for (double val : x) {
			if (val > maxval) {
				maxval = val;
			}
		}
		return maxval;
	}
	
	// Vector concatenation -----------------------
	
	/**
	 * Joins (concatenates) a sequence of vectors into a single vector.
	 * @param xs a sequence of vectors (at least one vector)
	 * @return a vector containing all elements of the input vectors
	 */
	public static float[] join(float[]... xs) {
		int n = 0;
		for (float[] x : xs) {
			n = n + x.length;
		}
		float[] va = new float[n];
		int j = 0;
		for (float[] x : xs) {
			for (int i = 0; i < x.length; i++) {
				va[j] = x[i];
				j++;
			}
		}		
		return va;
	}

	/**
	 * Joins (concatenates) a sequence of vectors into a single vector.
	 * @param xs a sequence of vectors (at least one vector)
	 * @return a vector containing all elements of the input vectors
	 */
	public static double[] join(double[]... xs) {
		int n = 0;
		for (double[] x : xs) {
			n = n + x.length;
		}
		double[] va = new double[n];	
		int j = 0;
		for (double[] x : xs) {
			for (int i = 0; i < x.length; i++) {
				va[j] = x[i];
				j++;
			}
		}		
		return va;
	}
	
	// Homogeneous coordinates ---------------------------------
	
	/**
	 * Converts a Cartesian vector to an equivalent homogeneous
	 * vector, which contains an additional 1-element.
	 * @param xc a Cartesian vector
	 * @return an equivalent homogeneous vector
	 */
	public static double[] toHomogeneous(double[] xc) {
		double[] xh = new double[xc.length + 1];
		for (int i = 0; i < xc.length; i++) {
			xh[i] = xc[i];
			xh[xh.length - 1] = 1;
		}
		return xh;
	}
	
	/**
	 * Converts a homogeneous vector to its equivalent Cartesian
	 * vector, which is one element shorter.
	 * @param xh a homogeneous vector
	 * @return the equivalent Cartesian vector
	 */
	public static double[] toCartesian(double[] xh) {
		double[] xc = new double[xh.length - 1];
		final double s = 1 / xh[xh.length - 1];
		if (isZero(s))
			throw new DivideByZeroException();
		for (int i = 0; i < xh.length - 1; i++) {
			xc[i] = s * xh[i];
		}
		return xc;
	}
	
	// Determinants --------------------------------------------
	
	public static float determinant2x2(final float[][] A) {
		if (A.length != 2 || A[0].length != 2)
			throw new IncompatibleDimensionsException();
		return A[0][0] * A[1][1] - A[0][1] * A[1][0];
	}
	
	public static double determinant2x2(final double[][] A) {
		if (A.length != 2 || A[0].length != 2)
			throw new IncompatibleDimensionsException();
		return A[0][0] * A[1][1] - A[0][1] * A[1][0];
	}
	
	public static float determinant3x3(final float[][] A) {
		if (A.length != 3 || A[0].length != 3)
			throw new IncompatibleDimensionsException();
		return
				A[0][0] * A[1][1] * A[2][2] +
				A[0][1] * A[1][2] * A[2][0] +
				A[0][2] * A[1][0] * A[2][1] -
				A[2][0] * A[1][1] * A[0][2] -
				A[2][1] * A[1][2] * A[0][0] -
				A[2][2] * A[1][0] * A[0][1] ;
	}

	public static double determinant3x3(final double[][] A) {
		if (A.length != 3 || A[0].length != 3)
			throw new IncompatibleDimensionsException();
		return 
			A[0][0] * A[1][1] * A[2][2] + 
			A[0][1] * A[1][2] * A[2][0]	+ 
			A[0][2] * A[1][0] * A[2][1] - 
			A[2][0] * A[1][1] * A[0][2] - 
			A[2][1] * A[1][2] * A[0][0] - 
			A[2][2] * A[1][0] * A[0][1] ;
	}
	
	public static double determinant(final double[][] A) {
		if (!isSquare(A))
			throw new NonsquareMatrixException();
		RealMatrix M = MatrixUtils.createRealMatrix(A);
		return new LUDecomposition(M).getDeterminant();
	}
	
	// Matrix trace ---------------------------------------
	
	public static double trace(final double[][] A) {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (m != n) 
			throw new NonsquareMatrixException();
		double s = 0;
		for (int i = 0; i < m; i++) {
				s = s + A[i][i];
		}
		return s;
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
	
	// Matrix inversion ---------------------------------------
	
	/**
	 * Calculates and returns the inverse of the given matrix, which
	 * must be square. Exceptions are thrown if the supplied matrix is
	 * not square or ill-conditioned (singular).
	 * @param A a square matrix
	 * @return the inverse matrix
	 */
	public static double[][] inverse(final double[][] A) {
		if (!isSquare(A))
			throw new NonsquareMatrixException();
		RealMatrix M = MatrixUtils.createRealMatrix(A);
		return MatrixUtils.inverse(M).getData();
	}
	
	/**
	 * Calculates and returns the inverse of the given matrix, which
	 * must be square. Exceptions are thrown if the supplied matrix is
	 * not square or ill-conditioned (singular).
	 * @param A a square matrix
	 * @return the inverse matrix
	 */
	public static float[][] inverse(final float[][] A) {
		if (!isSquare(A))
			throw new NonsquareMatrixException();
		double[][] Ad = toDouble(A);
		return toFloat(inverse(Ad));
	}

	// ------------------------------------------------------------------------
	
	/**
	 * Finds the exact solution x for the linear systems of equations
	 * A * x = b. Exceptions are thrown if the supplied matrix is
	 * not square or ill-conditioned (singular).
	 * @param A a square matrix of size n x n
	 * @param b a vector of length n
	 * @return the solution vector of length n
	 */
	public static double[] solve(final double[][] A, double[] b) {
		RealMatrix AA = MatrixUtils.createRealMatrix(A);
		RealVector bb = MatrixUtils.createRealVector(b);
		DecompositionSolver solver = new LUDecomposition(AA).getSolver();
//		double[] x = null;
//		try {
//			x = solver.solve(bb).toArray();
//		} catch (SingularMatrixException e) {}
		return solver.solve(bb).toArray();
	}
	
	// Output to strings and streams ------------------------------------------
	
	public static String toString(double[] x) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(x, strm);
		return bas.toString();
	}
	
	public static String toString(float[] x) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(x, strm);
		return bas.toString();
	}
	
	public static String toString(double[][] A) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	public static String toString(float[][] A) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	// --------------------
	
	public static void printToStream(double[] x, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("%c", LeftDelimitChar);
		for (int i = 0; i < x.length; i++) {
			if (i > 0)
				strm.format("%c ", SeparationChar);
			strm.format(PrintLocale, fStr, x[i]);
		}
		strm.format("%c", RightDelimitChar);
		strm.flush();
	}
	
	public static void printToStream(double[][] A, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("%c", LeftDelimitChar);
		for (int i=0; i< A.length; i++) {
			if (i == 0)
				strm.format("%c", LeftDelimitChar);
			else
				strm.format("%c \n%c", SeparationChar, LeftDelimitChar);
			for (int j=0; j< A[i].length; j++) {
				if (j == 0) 
					strm.format(PrintLocale, fStr, A[i][j]);
				else
					strm.format(PrintLocale, "%c " + fStr, SeparationChar, A[i][j]);
			}
			strm.format("%c", RightDelimitChar);
		}
		strm.format("%c", RightDelimitChar);
		strm.flush();
	}
	
	public static void printToStream(float[] A, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("%c", LeftDelimitChar);
		for (int i = 0; i < A.length; i++) {
			if (i > 0)
				strm.format("%c ", SeparationChar);
			strm.format(PrintLocale, fStr, A[i]);
		}
		strm.format("%c", RightDelimitChar);
		strm.flush();
	}
	
	public static void printToStream(float[][] A, PrintStream strm) {
		String fStr = PrintPrecision.getFormatStringFloat();
		strm.format("%c", LeftDelimitChar);
		for (int i=0; i< A.length; i++) {
			if (i == 0)
				strm.format("%c", LeftDelimitChar);
			else
				strm.format("%c \n%c", SeparationChar, LeftDelimitChar);
			for (int j = 0; j < A[i].length; j++) {
				if (j == 0) 
					strm.format(PrintLocale, fStr, A[i][j]);
				else
					strm.format(PrintLocale, "%c " + fStr, SeparationChar, A[i][j]);
			}
			strm.format("%c", RightDelimitChar);
		}
		strm.format("%c", RightDelimitChar);
		strm.flush();
	}

	// Exceptions ---------------------
	
	public static class IncompatibleDimensionsException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		private static String DefaultMessage = "incompatible matrix-vector dimensions";
		
		public IncompatibleDimensionsException() {
			super(DefaultMessage);
		}
	}
	
	public static class NonsquareMatrixException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		private static String DefaultMessage = "square matrix expected";
		
		public NonsquareMatrixException() {
			super(DefaultMessage);
		}
	}
	
	public static class SameSourceTargetException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		private static String DefaultMessage = "source and target must not be the same";
		
		public SameSourceTargetException() {
			super(DefaultMessage);
		}
	}
	
	public static class ZeroLengthVectorException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		private static String DefaultMessage = "vector length must be greater that 0";
		
		public ZeroLengthVectorException() {
			super(DefaultMessage);
		}
	}
	
	//--------------------------------------------------------------------------
	
	/**
	 * For testing only.
	 * @param args ignored
	 */
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
		float[][] Ai = inverse(A);
		toString(Ai);

		double[][] B = {
				{ -1, 2, 3 }, 
				{ 4, 5, 6 }};
		System.out.println("B = \n" + toString(B));
		System.out.println("B rows = " + getNumberOfRows(B));
		System.out.println("B columns = " + getNumberOfColumns(B));
		
		double[] rowSum = sumRows(B);
		System.out.println("B sum of rows = " + toString(rowSum));
		double[] colSum = sumColumns(B);
		System.out.println("B sum of cols = " + toString(colSum));
		
		PrintPrecision.set(5);

		double[][] C = new double[2][3];
		System.out.println("C rows = " + getNumberOfRows(C));
		System.out.println("C columns = " + getNumberOfColumns(C));

		RealMatrix Ba = MatrixUtils.createRealMatrix(B);
		System.out.println("Ba = " + Ba.toString());
		
		float[] v1 = {1,2,3};
		float[] v2 = {4,5,6,7};
		float[] v3 = {};
		float[] v4 = {8};
		float[] v123 = join(v1, v2, v3, v4);
		System.out.println("v123 = \n" + toString(v123));
		
		System.out.println("mind1 = " + Matrix.min(new double[] {-20,30,60,-40, 0}));
		System.out.println("maxd2 = " + Matrix.max(new double[] {-20,30,60,-40, 0}));
		System.out.println("minf1 = " + Matrix.min(new float[] {-20,30,60,-40, 0}));
		System.out.println("maxf2 = " + Matrix.max(new float[] {-20,30,60,-40, 0}));
	}
	

}
