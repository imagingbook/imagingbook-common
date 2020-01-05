package imagingbook.pub.geometry.fitting;

import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.basic.Point;

/**
 * TODO: Need a complete rewrite!
 *
 */
public class AffineFit2D extends LinearFit2D {
	
	private int m;			// number of samples
	private double err;		// TODO: this should be changed!
	
	private RealMatrix A = null;
	
	public AffineFit2D() {
	}

	@Override
	public void fit(List<Point> X, List<Point> Y) {	// fits n-dimensional data sets with affine model
		if (X.size() != Y.size())
			throw new IllegalArgumentException("point sequences X, Y must have same length");
		this.m = X.size();
		
		RealMatrix M = MatrixUtils.createRealMatrix(2 * m, 6);
		RealVector b = new ArrayRealVector(2 * m);
		
		// mount matrix M:	TODO: needs to be checked for n > 2!
		int row = 0;
		for (Point x : X) {
			M.setEntry(row, 0, x.getX());
			M.setEntry(row, 1, x.getY());
			M.setEntry(row, 2, 1);
//			for (int j = 0; j < n; j++) {
//				M.setEntry(row, j, x[j]);
//				M.setEntry(row, n, 1);
//			}
			row++;
			M.setEntry(row, 3, x.getX());
			M.setEntry(row, 4, x.getY());
			M.setEntry(row, 5, 1);
//			for (int j = 0; j < n; j++) {
//				M.setEntry(row, j + n + 1, x[j]);
//				M.setEntry(row, 2 * n + 1, 1);
//			}
			row++;
		}
		//IJ.log("M = \n" + Matrix.toString(M.getData()));
		
		// mount vector b
		row = 0;
		for (Point y : Y) {
			b.setEntry(row, y.getX());
			row++;
			b.setEntry(row, y.getY());
			row++;
//			for (int j = 0; j < n; j++) {
//				b.setEntry(row, y[j]);
//				row++;
//			}
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
		RealMatrix A = MatrixUtils.createRealMatrix(2, 3);
		int i = 0;
		for (int row = 0; row < 2; row++) {
			// get (n+1) elements from a and place in row
			for (int j = 0; j < 3; j++) {
				A.setEntry(row, j, a.getEntry(i));
				i++;
			}
		}
		return A;
	}
	
	private double makeError(List<Point> X, List<Point> Y, RealMatrix A) {
		Point[] Xa = X.toArray(new Point[0]);
		Point[] Ya = Y.toArray(new Point[0]);
		if (Xa.length != Ya.length)
			throw new IllegalArgumentException("X,Y must have the same length!");
		double errSum = 0;
		for (int i = 0; i < Xa.length; i++) {
			Point p = Xa[i];
			Point q = Ya[i];
			double[] p2a = A.operate(Matrix.toHomogeneous(new double[] {p.getX(), p.getY()}));	// TODO:
			Point p2 = Point.create(p2a[0], p2a[1]); // TODO: fix!
			errSum = errSum + 
					Math.sqrt(sq(q.getX() - p2.getX()) + sq(q.getY() - p2.getY()));
		}
		return errSum;
	}
	
	// --------------------------------------------------------
	
	private double sq(double x) {
		return x * x;
	}

	@Override
	public RealMatrix getTransformationMatrix() { // TODO: should return a mapping!?
		return A;
	}

	@Override
	public double getError() {
		return err;
	}

}
