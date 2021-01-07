package imagingbook.lib.math;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Eigensolver2x2Test {

	@Test
	public void testEigensolver2x2A() {
		double[][] M = {
				{3, -2},
				{-4, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2B() {
		double[][] M = {
				{-0.004710, -0.006970},
				{-0.006970, -0.029195}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2C() {
		double[][] M = {
				{0, 0},
				{0, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2D() {
		double[][] M = {
				{1, 0},
				{0, 0}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2E() {
		double[][] M = {
				{1, 0},
				{-2, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2F() {
		double[][] M = {
				{1, -2},
				{0, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolver2x2G() {
		double[][] M = {
				{1, 2},
				{2, 1}};
		runTest(M);
	}
	
	
	private void runTest(double[][] M) {
		Eigensolver2x2 solver = new Eigensolver2x2(M);	
		assertTrue(solver.isReal());
		double[]   eigenvals = solver.getEigenvalues();
		double[][] eigenvecs = solver.getEigenvectors();
		double lam1 = eigenvals[0];
		double lam2 = eigenvals[1];
		double[] x1 = eigenvecs[0];
		double[] x2 = eigenvecs[1];
		
		assertTrue(lam1 >= lam2);
		assertTrue(Matrix.normL2(eigenvals) > 0.01);
		
		// check: M * x_0 = λ_0 * x_0
		assertArrayEquals(Matrix.multiply(M, x1), Matrix.multiply(lam1, x1), 1E-6);
		// check: M * x_1 = λ_1 * x_1
		assertArrayEquals(Matrix.multiply(M, x2), Matrix.multiply(lam2, x2), 1E-6);
	}

}
