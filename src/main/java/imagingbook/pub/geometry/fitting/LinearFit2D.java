package imagingbook.pub.geometry.fitting;

/**
 * Describes a fitter based on a linear transformation model.
 * @author WB
 */
public interface LinearFit2D {
	
	/**
	 * Retrieves the (3 x 3) or (2 x 3) transformation matrix A, such that
	 * {@code y_i ~ A * x_i} (with {@code x_i} in homogeneous coordinates).
	 * 
	 * @return the transformation matrix
	 */
	double[][] getTransformationMatrix();
	
	/**
	 * Retrieves the total (squared) error for the estimated fit.
	 * @return the fitting error
	 */
	double getError();
	
	// -----------------------------------------------
	


}