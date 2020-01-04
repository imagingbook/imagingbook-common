package imagingbook.pub.geometry.fitting_OLD;

import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.lib.math.Matrix;

/**
 * TODO: Need a complete rewrite!
 *
 */
public class AffineFit extends LinearFit {
	
	private int m;			// number of samples
	private double err;		// TODO: this should be changed!
	
	private RealMatrix A = null;
	
	public AffineFit() {
		this(2);	// 2D by default
	}
	
	public AffineFit(int n) {
		super(n);
	}

	@Override
	public void fit(List<double[]> X, List<double[]> Y) {	// fits n-dimensional data sets with affine model
		if (X.size() != Y.size())
			throw new IllegalArgumentException("point sequences X, Y must have same length");
		if (X.get(0).length < n)
			throw new IllegalArgumentException("dimensionality of samples must be >= " + n);
		this.m = X.size();
		
		RealMatrix M = MatrixUtils.createRealMatrix(2 * m, 2 * (n + 1));
		RealVector b = new ArrayRealVector(2 * m);
		
//		IJ.log("M = " + M.getRowDimension() + " x " + M.getColumnDimension());
		
		// mount matrix M:	TODO: needs to be checked for n > 2!
		int row = 0;
		for (double[] x : X) {
			for (int j = 0; j < n; j++) {
				M.setEntry(row, j, x[j]);
				M.setEntry(row, n, 1);
			}
			row++;
			for (int j = 0; j < n; j++) {
				M.setEntry(row, j + n + 1, x[j]);
				M.setEntry(row, 2 * n + 1, 1);
			}
			row++;
		}
		//IJ.log("M = \n" + Matrix.toString(M.getData()));
		
		// mount vector b
		row = 0;
		for (double[] y : Y) {
			for (int j = 0; j < n; j++) {
				b.setEntry(row, y[j]);
				row++;
			}
		}
		
		//IJ.log("b = \n" + Matrix.toString(b.toArray()));
		
		SingularValueDecomposition svd = new SingularValueDecomposition(M);
		DecompositionSolver solver = svd.getSolver();
		RealVector a = solver.solve(b);
		A = makeTransformationMatrix(a);
		err = makeError(X, Y, A);
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
		//A.setEntry(n - 1, n, 1);
		return A;
	}
	
	private double makeError(List<double[]> X, List<double[]> Y, RealMatrix A) {
		double[][] Xa = X.toArray(new double[0][]);
		double[][] Ya = Y.toArray(new double[0][]);
		if (Xa.length != Ya.length)
			throw new IllegalArgumentException("X,Y must have the same length!");
		double errSum = 0;
		for (int i = 0; i < Xa.length; i++) {
			double[] p = Xa[i];
			double[] q = Ya[i];
			double[] p2 = A.operate(Matrix.toHomogeneous(p));	// TODO:
			errSum = errSum + 
					Math.sqrt(sq(q[0] - p2[0]) + sq(q[1] - p2[1]));
		}
		return errSum;
	}
	
	// --------------------------------------------------------
	
	private double sq(double x) {
		return x * x;
	}

	@Override
	public RealMatrix getTransformationMatrix() {
		return A;
	}

	@Override
	public double getError() {
		return err;
	}

}