package imagingbook.pub.geometry.fitting_OLD;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * Describes a fitter based on a linear transformation model.
 * @author WB
 *
 */
public abstract class LinearFit {
	
	protected final int n;	// dimensionality of samples
	
	protected LinearFit(int n) {
		this.n= n;
	}
	
	/**
	 * Calculates the least-squares fit for the supplied point sequences,
	 * which must have the same length and order (i.e., points must be in 
	 * correspondence).
	 * @param X Sequence of n-dimensional points
	 * @param Y Sequence of n-dimensional points (reference)
	 */
	public abstract void fit(List<double[]> X, List<double[]> Y);

	/**
	 * Retrieves the (n) x (n+1) transformation matrix A, such that
	 * y_i = A * x_i (with x_i in homogeneous coordinates).
	 * 
	 * @return the transformation matrix
	 */
	public abstract RealMatrix getTransformationMatrix();
	
	/**
	 * Retrieves the total (squared) error for the estimated fit.
	 * @return The total error for the estimated fit.
	 */
	public abstract double getError();
	
	
	/**
	 * Calculates the least-squares fit for the supplied point sequences,
	 * which must have the same length and order (i.e., points must be in 
	 * correspondence). Convenience method for fitting 2D points.
	 * @param Xpts Sequence of 2-dimensional points
	 * @param Ypts Sequence of 2-dimensional points (reference)
	 */
	public void fitPoints(List<Point2D> Xpts, List<Point2D> Ypts) {
		fit(toDoubleArrays(Xpts), toDoubleArrays(Ypts));
	}
	
	private List<double[]> toDoubleArrays(List<Point2D> Xpts) {
		List<double[]> X = new ArrayList<>(Xpts.size());
		for (Point2D pt : Xpts) {
			X.add(new double[] {pt.getX(), pt.getY()});
		}
		return X;
	}

}
