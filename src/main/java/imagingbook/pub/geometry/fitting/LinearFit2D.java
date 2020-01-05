package imagingbook.pub.geometry.fitting;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * Describes a fitter based on a linear transformation model.
 * @author WB
 *
 */
public interface LinearFit2D {
	
//	/**
//	 * Calculates the least-squares fit for the supplied point sequences,
//	 * which must have the same length and order (i.e., points must be in 
//	 * correspondence).
//	 * @param P Sequence of n-dimensional points
//	 * @param Q Sequence of n-dimensional points (reference)
//	 */
//	public abstract void fit(List<Point> P, List<Point> Q);

	/**
	 * Retrieves the (n) x (n+1) transformation matrix A, such that
	 * y_i = A * x_i (with x_i in homogeneous coordinates).
	 * 
	 * @return the transformation matrix
	 */
	RealMatrix getTransformationMatrix();
	
	/**
	 * Retrieves the total (squared) error for the estimated fit.
	 * @return The total error for the estimated fit.
	 */
	double getError();
	
	// -----------------------------------------------
	


}