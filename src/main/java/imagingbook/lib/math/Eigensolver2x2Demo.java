package imagingbook.lib.math;

import imagingbook.lib.math.Eigensolver2x2;
import imagingbook.lib.math.Matrix;
import imagingbook.lib.settings.PrintPrecision;

/**
 * This is a small demo program to show the use of the
 * {@link Eigensolver2x2} class.
 * It takes a 2x2 real-valued matrix and calculates its two
 * eigenvalues and eigenvectors.
 * Results are printed and checked.
 * 
 * @author WB
 *@version 2020/03/25
 */
public class Eigensolver2x2Demo {
	
	public static void main(String[] args) {
		
		PrintPrecision.set(6);
		
		// specify a 2x2 matrix
		double[][] M = {
				{3, -2},
				{-4, 1}};
		
		System.out.format("M =\n%s\n\n", Matrix.toString(M));
		
		Eigensolver2x2 solver = new Eigensolver2x2(M);
		double[]   eigenvalues  = solver.getEigenvalues();
		double[][] eigenvectors = solver.getEigenvectors();
		
		System.out.println("Eigenvalues \u03BB_i / Eigenvectors x_i:\n");
		
		for (int i = 0; i < 2; i++) {
			// get the eigen-pair <lambda_i, x_i>
			double lambda = eigenvalues[i];
			double[] x = eigenvectors[i];
			
			System.out.format("\u03BB_%d  = %.6f\n", i, lambda);
			System.out.format("x_%d = %s\n", i, Matrix.toString(x));
			
			System.out.format("Check: \u03BB_%d * x_%d = %s\n", i, i, Matrix.toString(Matrix.multiply(lambda, x)));
			System.out.format("Check:   M * x_%d = %s\n\n", i, Matrix.toString(Matrix.multiply(M, x)));
		}

	}
}
