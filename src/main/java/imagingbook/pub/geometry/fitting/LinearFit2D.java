package imagingbook.pub.geometry.fitting;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * Describes a fitter based on a linear transformation model.
 * @author WB
 *
 */
public interface LinearFit2D {
	
	/**
	 * Retrieves the (2 x 3) transformation matrix A, such that
	 * {@code y_i = A * x_i} (with {@code x_i} in homogeneous coordinates).
	 * 
	 * @return the transformation matrix
	 */
	RealMatrix getTransformationMatrix();
	
	/**
	 * Retrieves the total (squared) error for the estimated fit.
	 * @return the fitting error
	 */
	double getError();
	
	// -----------------------------------------------
	


}