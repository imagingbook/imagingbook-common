package imagingbook.pub.geometry.fitting;

import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

public class AffineFit extends LinearFit {
	
	private int m;		// number of samples
	private int n;		// dimension of samples
	
	private RealMatrix A = null;

	@Override
	public void fit(List<double[]> X, List<double[]> Y) {	// fits n-dimensional data sets with affine model
		if (X.size() != Y.size())
			throw new IllegalArgumentException("point sequences X, Y must have same length");
		this.m = X.size();
		this.n = X.get(0).length;
		
		RealMatrix M = MatrixUtils.createRealMatrix(2 * m, 2 * (n + 1));
		RealVector b = new ArrayRealVector(2 * m);
		
		// mount matrix M:
		int row = 0;
		for (double[] x : X) {
			for (int j = 0; j < n; j++) {
				M.setEntry(row, j, x[j]);
				M.setEntry(row, n, 1);
				row++;
			}
			for (int j = 0; j < n; j++) {
				M.setEntry(row, j + n + 1, x[j]);
				M.setEntry(row, 2 * n + 1, 1);
				row++;
			}
		}
		
		// mount vector b
		row = 0;
		for (double[] y : Y) {
			for (int j = 0; j < n; j++) {
				b.setEntry(row, y[j]);
				row++;
			}
		}
		
		SingularValueDecomposition svd = new SingularValueDecomposition(M);
		DecompositionSolver solver = svd.getSolver();
		RealVector a = solver.solve(b);
		A = makeTransformationMatrix(a);
	}

	// creates a n x (n+1) transformation matrix from the elements of a
	private RealMatrix makeTransformationMatrix(RealVector a) {
		RealMatrix A = MatrixUtils.createRealMatrix(n, n + 1);
		int i = 0;
		for (int row = 0; row < n; row++) {
			// get (n+1) elements from a and place in row
			for (int j = 0; j <= n; j++) {
				A.setEntry(row, j, a.getEntry(i));
				i++;
			}
		}
		A.setEntry(n - 1, n, 1);
		return A;
	}

	@Override
	public RealMatrix getTransformationMatrix() {
		return A;
	}

	@Override
	public double getError() {
		// TODO Auto-generated method stub
		return 0;
	}
	


}
