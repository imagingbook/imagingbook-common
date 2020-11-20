package imagingbook.lib.math;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Eigensolver2x2Test {

	@Test
	public void testEigensolver2x2() {
		double[][] M = {
				{3, -2},
				{-4, 1}};
		
		Eigensolver2x2 solver = new Eigensolver2x2(M);
		
		assertTrue(solver.isReal());
		
		double[]   eigenvals = solver.getEigenvalues();
		double[][] eigenvecs = solver.getEigenvectors();
		
		double lam0 = eigenvals[0];
		double lam1 = eigenvals[1];
		
		double[] x0 = eigenvecs[0];
		double[] x1 = eigenvecs[1];
		
		assertEquals( 5.0, lam0, 1E-6);
		assertArrayEquals(new double[] {4.0, -4.0}, x0, 1E-6);
		
		assertEquals(-1.0, lam1, 1E-6);
		assertArrayEquals(new double[] {-2.0, -4.0}, x1, 1E-6);
		
		// check: M * x_0 = λ_0 * x_0
		assertArrayEquals(Matrix.multiply(M, x0), Matrix.multiply(lam0, x0), 1E-6);
		// check: M * x_1 = λ_1 * x_1
		assertArrayEquals(Matrix.multiply(M, x1), Matrix.multiply(lam1, x1), 1E-6);
	}

}
