package imagingbook.pub.geometry.fitting;

import java.awt.geom.Point2D;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * Describes a fitter based on a linear transformation model.
 * @author WB
 *
 */
public interface LinearFit {
	
	/**
	 * Calculates the least-squares fit for the supplied point sequences,
	 * which must have the same length and order (i.e., points must be in 
	 * correspondence).
	 * @param X Sequence of n-dimensional points
	 * @param Y Sequence of n-dimensional points (reference)
	 */
	public void fit(List<double[]> X, List<double[]> Y);
	
	/**
	 * Calculates the least-squares fit for the supplied point sequences,
	 * which must have the same length and order (i.e., points must be in 
	 * correspondence). Convenience method for fitting 2D points.
	 * @param X Sequence of 2-dimensional points
	 * @param Y Sequence of 2-dimensional points (reference)
	 */
	public void fitPoints(List<Point2D> X, List<Point2D> Y);
	
	/**
	 * Retrieves the (n) x (n+1) transformation matrix A, such that
	 * y_i = A * x_i (with x_i in homogeneous coordinates)
	 */
	public RealMatrix getTransformationMatrix();
	
	/**
	 * Retrieves the total (squared) error for the estimated fit.
	 * @return The total error for the estimated fit.
	 */
	public double getError();

}
