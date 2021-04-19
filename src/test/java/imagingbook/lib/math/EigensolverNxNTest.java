package imagingbook.lib.math;

import static org.junit.Assert.*;

import org.junit.Test;

public class EigensolverNxNTest {

	@Test
	public void testEigensolverNxNA() {
		double[][] M = {
				{3, -2},
				{-4, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxNB() {
		double[][] M = {
				{-0.004710, -0.006970},
				{-0.006970, -0.029195}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxNC() {
		double[][] M = {
				{0, 0},
				{0, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxND() {
		double[][] M = {
				{1, 0},
				{0, 0}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxNE() {
		double[][] M = {
				{1, 0},
				{-2, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxNF() {
		double[][] M = {
				{1, -2},
				{0, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxNG() {
		double[][] M = {
				{1, 2},
				{2, 1}};
		runTest(M);
	}
	
	@Test
	public void testEigensolverNxNH() {
		double[][] M = {
				{5, 2, 0},
				{2, 5, 0},
				{-3, 4, 6}};
		runTest(M);
	}

	
	private void runTest(double[][] M) {
		EigensolverNxN solver = new EigensolverNxN(M);	
		assertTrue(solver.isReal());
		double[] eigenvals = solver.getEigenvalues();
		
		//System.out.println("eigenvals = " + Matrix.toString(eigenvals));
		
		for (int k = 1; k < eigenvals.length; k++) {
			assertTrue(eigenvals[k-1] >= eigenvals[k]);
		}
		
		assertTrue(Matrix.normL2(eigenvals) > 0.01);
		
		for (int k = 0; k < eigenvals.length; k++) {
			double lambda = eigenvals[k];
			double[] x = solver.getEigenvector(k);
			// check: M * x = λ * x
			assertArrayEquals(Matrix.multiply(M, x), Matrix.multiply(lambda, x), 1E-6);
		}
	}

}
