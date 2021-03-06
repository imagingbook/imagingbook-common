package imagingbook.lib.math.eigen;

import static imagingbook.lib.math.Arithmetic.sqr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;

import imagingbook.lib.math.Arithmetic;
import imagingbook.lib.math.Matrix;

/**
 * Calculates eigenvalues and eigenvectors of square matrices of arbitrary size.
 * Eigenvalues are sorted by magnitude (in descending order). A matrix may have
 * complex eigenvalues but only real eigenvalues (and their associated
 * eigenvectors) are considered. Uses class {@link EigenDecomposition} of the
 * Apache Commons Math (3) library.
 * <br>
 * Usage example (retrieving all eigenvalues and associated eigenvectors):
 * <pre>
 * double[][] A = {
 * 	{ 5, 2, 0, 1 },
 * 	{ 2, 5, 0, 7 },
 * 	{ -3, 4, 6, 0 },
 * 	{ 1, 2, 3, 4 }};
 * 
 * RealEigensolver solver = new EigensolverNxN(A);
 * 
 * double[] eigenvals = solver.getEigenvalues();
 * for (int k = 0; k &lt; solver.getSize(); k++) {
 * 	double lambda = solver.getEigenvalue(k);
 * 	if (!Double.isNaN(lambda)) {
 * 		double[] x = solver.getEigenvector(k);
 * 		...
 * 	}
 * }</pre>
 * 
 * @author WB
 * @version 2021/04/20
 */
public class EigensolverNxN implements RealEigensolver {

	private final EigenDecomposition ed;
	private final int n;
	private final double[] eVals;
	private final int[] permutation;

	/**
	 * Constructor, takes a NxN (square) matrix.
	 * @param A a NxN matrix
	 */
	public EigensolverNxN(double[][] A) {
		if (!Matrix.isSquare(A)) {
			throw new IllegalArgumentException("matrix A must be square");
		}
		this.n = A.length;
		this.ed = new EigenDecomposition(MatrixUtils.createRealMatrix(A));
		this.eVals = new double[A.length];
		this.permutation = sortEigenvalues(ed.getRealEigenvalues(), ed.getImagEigenvalues());
		//print();
	}
	
	@Override
	public int getSize() {
		return n;
	}
	
	@Override
	public boolean isReal() {
		return !ed.hasComplexEigenvalues();
	}

	@Override
	public double[] getEigenvalues() {
		return eVals;
	}

	@Override
	public double getEigenvalue(int k) {
		return eVals[k];
	}

	@Override
	public double[] getEigenvector(int k) {
		return ed.getEigenvector(permutation[k]).toArray();
	}
	
	// -----------------------------------------------------
	
	/**
	 * Sorts eigenvalues by magnitude (in descending order, including complex eigenvalues),
	 * inserts sorted values into {@link #eVals} (NaN for complex eigenvalues),
	 * and returns the associated permutation array.
	 * @param re real parts of all eigenvalues
	 * @param im imaginary parts of all eigenvalues
	 * @return the permutation array for sorting by magnitude
	 */
	private int[] sortEigenvalues(double[] re, double[] im) {
		final int n = re.length;
		double[] mag = new double[n];
		for (int k = 0; k < n; k++) {
			mag[k] = Math.sqrt(sqr(re[k]) + sqr(im[k]));
		}
		
		// inspired by https://stackoverflow.com/a/11998394
		List<Integer> indices = new ArrayList<>(mag.length);
		for (int i = 0; i < mag.length; i++) {
			indices.add(i);
		}
		
		Comparator<Integer> comparator = new Comparator<Integer>() {
			@Override
			public int compare(Integer a, Integer b) {
				return Double.compare(mag[b], mag[a]);
			}
		};

		Collections.sort(indices, comparator);
		int[] perm = indices.stream().mapToInt(Integer::intValue).toArray();
		
		// Rearrange 'eVals' and replace complex eigenvalues by NaN:
		for (int k = 0; k < n; k++) {
			int j = perm[k];
			eVals[k] = (Arithmetic.isZero(im[j])) ? re[j] : Double.NaN;
		}
		return perm;
	}
	

	@SuppressWarnings("unused")
	private void print() {
		System.out.println("hasComplexEigenvalues = " + ed.hasComplexEigenvalues());
		double[] evalsR = ed.getRealEigenvalues();
		double[] evalsI = ed.getImagEigenvalues();
		System.out.format("evalsR = %s\n", Arrays.toString(evalsR));
		System.out.format("evalsI = %s\n", Arrays.toString(evalsI));
		
		for (int k = 0; k < evalsR.length; k++) {
			double[] evecR = ed.getEigenvector(k).toArray();
			System.out.format("%d: eval=%f | evec=%s\n", k, getEigenvalue(k), Arrays.toString(getEigenvector(k)));
		}
	}

	// -------------------------------------------------------------------------
	
	/**
	 * @param args args
	 * @hidden
	 */
	public static void main(String[] args) {
//		double[][] A = {
//				{3, -2},
//				{-4, 1}};
//		double[][] A = {	// no real eigenvalues
//				{0, -1},
//				{2, 0}};
//		double[][] A = {	// no real eigenvalues
//				{4, -1},
//				{2, 4}};
//		double[][] A = {
//				{5, 2, 0},
//				{2, 5, 0},
//				{-3, 4, 6}};
		double[][] A = {	// 2 real eigenvalues
				{5, 2, 0, 1},
				{2, 5, 0, 7},
				{-3, 4, 6, 0},
				{1 , 2, 3, 4}};
		
		
		System.out.println("A = \n" + Matrix.toString(A));
		RealEigensolver solver = new EigensolverNxN(A);	

		System.out.println("isReal = " + solver.isReal());
		double[] eigenvals = solver.getEigenvalues();
		System.out.println("evals = " + Matrix.toString(eigenvals));
		for (int k = 0; k < solver.getSize(); k++) {
			double lambda = solver.getEigenvalue(k);
			if (!Double.isNaN(lambda)) {
				double[] x = solver.getEigenvector(k);
				System.out.format("λ_%d = %.6f\n", k, lambda);
				System.out.format("x_%d = %s\n", k, Matrix.toString(x));
				System.out.format("   M*x = %s\n", Matrix.toString(Matrix.multiply(A, x)));
				System.out.format("   λ*M = %s\n", Matrix.toString(Matrix.multiply(lambda, x)));
			}
		}
	}
}
