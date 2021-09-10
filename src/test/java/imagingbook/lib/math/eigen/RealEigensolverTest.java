package imagingbook.lib.math.eigen;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import imagingbook.lib.math.Matrix;

/**
 * Contains common methods for testing eigen-solvers.
 *
 */
public abstract class RealEigensolverTest {

	static void run(RealEigensolver solver, double[][] M, boolean shouldBeReal) {
		
		if (shouldBeReal) {
			assertTrue(solver.isReal());
		}
		else {
			assertFalse(solver.isReal());
			return;
		}
		
		double[] eigenvals = solver.getEigenvalues();
		
//		System.out.format("λ0 = %.4f, λ1 = %.4f\n", eigenvals[0], eigenvals[1]);
		for (int k = 1; k < eigenvals.length; k++) {
			assertTrue(Math.abs(eigenvals[k-1]) >= Math.abs(eigenvals[k]));		// |λ_k-1| >= |λ_k|
		}
		
		for (int k = 0; k < eigenvals.length; k++) {
			if (Double.isNaN(eigenvals[k])) {
				continue;
			}
			//System.out.println("testing " + eigenvals[k]);
			double lambda = eigenvals[k];
			double[] x = solver.getEigenvector(k);
			// check: M * x_k = λ_k * x_k
			assertArrayEquals(Matrix.multiply(M, x), Matrix.multiply(lambda, x), 1E-6);
		}
	}

	
	
	static double[][] makeRandomMatrix2x2(Random RG) {
		double[][] A = new double[2][2];
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				A[i][j] = 2 * RG.nextDouble() - 1;
			}
		}
		return A;
	}


}
