package imagingbook.lib.math.eigen;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Interface for classes performing eigenvalue/eigenvector calculations.
 *
 */
public interface RealEigensolver {
	
	/**
	 * Returns the size of the system (N = number of rows and columns).
	 * @return size of the system (N)
	 */
	public int getSize();
	
	/**
	 * Returns {@code true} iff all eigenvalues of the associated matrix are real.
	 * @return as described
	 */
	public boolean isReal();
	
	/**
	 * Returns a vector of real eigenvalues in no specific order.
	 * {@code NaN} is inserted for complex eigenvalues.
	 * @return an array of eigenvalues
	 */
	public double[] getEigenvalues();
	
	/**
	 * Returns the k-th eigenvalue (&lambda;_k, for k = 0,...,N-1).
	 * {@code NaN} is returned if the associated eigenvalue
	 * is complex-valued (non-real).
	 * @param k index 
	 * @return the k-th eigenvalue (&lambda;<sub>k</sub>)
	 */
	public double getEigenvalue(int k);
	
	/**
	 * Returns a matrix whose columns are the eigenvectors of the
	 * solution, arranged in the same order as the eigenvalues
	 * returned by {@link #getEigenvalues()}. 
	 * 
	 * @return a matrix of eigenvectors (column vectors)
	 */
	public double[][] getEigenvectors();
	
	/**
	 * Returns the k-th eigenvector (x_k, for k = 0,...,N-1).
	 * The ordering of the returned eigenvectors is the same as for the
	 * eigenvalues returned by {@link #getEigenvalues()}.
	 * @param k index
	 * @return the k-th eigenvector (x<sub>k</sub>)
	 */
	public double[] getEigenvector(int k);
	
	/**
	 * Returns a solution to the eigenproblem as an instance
	 * of {@link EigenSolution} if real eigenvalues exist, null otherwise. 
	 * 
	 * @return an instance of {@link EigenSolution}
	 */
	public default EigenSolution getSolution() {
		if (this.isReal()) {
			RealVector eVals = MatrixUtils.createRealVector(this.getEigenvalues());
			RealMatrix eVecs = new Array2DRowRealMatrix(this.getEigenvectors(), false);
			return new EigenSolution(eVals, eVecs);
		}
		else {
			return null;
		}
	}

}
