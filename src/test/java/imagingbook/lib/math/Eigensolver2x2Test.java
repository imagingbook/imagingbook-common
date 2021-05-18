package imagingbook.lib.math;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class Eigensolver2x2Test {

	@Test
	public void testEigensolver2x2A() { // λ1 = 5.0000, λ2 = -1.0000 x1 = {4.000, -4.000}, x2 = {-2.000, -4.000}
		double[][] M = {
				{3, -2},
				{-4, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2B() { // <-0,0310, -0,0029, {-0.007, -0.026}, {0.026, -0.007}>
		double[][] M = {
				{-0.004710, -0.006970},
				{-0.006970, -0.029195}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2C() { // <1,0000, 0,0000, {0.000, 1.000}, {-1.000, 0.000}>
		double[][] M = {
				{0, 0},
				{0, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2D() { // <1,0000, 0,0000, {1.000, 0.000}, {0.000, -1.000}>
		double[][] M = {
				{1, 0},
				{0, 0}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2E() { // <1,0000, 1,0000, {-0.000, -2.000}, {0.000, -2.000}>
		double[][] M = {
				{1, 0},
				{-2, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2F() { // <1,0000, 1,0000, {-2.000, -0.000}, {-2.000, 0.000}>
		double[][] M = {
				{1, -2},
				{0, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2G() { // <3,0000, -1,0000, {2.000, 2.000}, {-2.000, 2.000}>
		double[][] M = {
				{1, 2},
				{2, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2H() {	// not real
		double[][] M = {
				{0, -1},
				{2, 0}};
		runTest(M, false);
	}
	
	@Test
	public void testEigensolver2x2I() { // not real
		double[][] M = {
				{4, -1},
				{2, 4}};
		runTest(M, false);
	}
	
	@Test
	public void testRandomMatrix() {
		final int N = 1000;
		int cnt = 0;
		for (int i = 0; i < N; i++) {
			double[][] A = makeRandomMatrix();
			RealEigensolver solver = new Eigensolver2x2(A);
//			RealEigensolver solver = new EigensolverNxN(A);
			if (solver.isReal()) {
				cnt++;
				double[] eigenvals = solver.getEigenvalues();
				for (int k = 0; k < eigenvals.length; k++) {
					double lambda = eigenvals[k];
					double[] x = solver.getEigenvector(k);
					assertArrayEquals(Matrix.multiply(A, x), Matrix.multiply(lambda, x), 1E-6);
				}
			}
		}
		System.out.println("real solutions: " + cnt + " out of " + N);
	}
	
	private Random RG = new Random();
	
	private double[][] makeRandomMatrix() {
		double[][] A = new double[2][2];
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				A[i][j] = 2 * RG.nextDouble() - 1;
			}
		}
		return A;
	}
	
	// -------------------------------------------------------------------------------------------------------
	
	private void runTest(double[][] M) {
		runTest(M, true);
	}
	
	
	private void runTest(double[][] A, boolean shouldBeReal) {
//		System.out.println("\nA = \n" + Matrix.toString(A));
		Eigensolver2x2 solver = new Eigensolver2x2(A);	
		
//		System.out.println(solver.toString());
		
		if (shouldBeReal) {
			assertTrue(solver.isReal());
		}
		else {
			assertFalse(solver.isReal());
			return;
		}
		
		//assertTrue(solver.isReal());
		double[] eigenvals = solver.getEigenvalues();
		
//		System.out.format("λ1 = %.4f, λ2 = %.4f\n", eigenvals[0], eigenvals[1]);
		for (int k = 1; k < eigenvals.length; k++) {
			assertTrue(Math.abs(eigenvals[k-1]) >= Math.abs(eigenvals[k]));		// |λ1| >= |λ2|
		}
		
		assertTrue(Matrix.normL2(eigenvals) > 0.01);	// make sure that not both eigenvalues are zero
		
		for (int k = 0; k < eigenvals.length; k++) {
			double lambda = eigenvals[k];
			double[] x = solver.getEigenvector(k);
//			System.out.format("x%d = %s\n", k+1, Matrix.toString(x));
//			System.out.format("A*x%d = %s\n", k+1, Matrix.toString(Matrix.multiply(A, x)));
//			System.out.format("l%d*x%d = %s\n", k+1, k+1, Matrix.toString(Matrix.multiply(lambda, x)));
			// check: M * x = λ * x
			assertArrayEquals(Matrix.multiply(A, x), Matrix.multiply(lambda, x), 1E-6);
		}
	}

}
