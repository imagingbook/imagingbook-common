package imagingbook.lib.math;

public interface RealEigensolver {
	
	/**
	 * Returns {@code true} iff all eigenvalues of the associated matrix are real.
	 * @return as described
	 */
	public boolean isReal();
	
	/**
	 * Returns a vector of eigenvalues, sorted by magnitude (in descending order).
	 * {@code Double.Nan} is inserted for complex eigenvalues.
	 * Note that the method returns a reference to an internal
	 * array and thus results should be used read-only!
	 * @return an array of eigenvalues
	 */
	public double[] getEigenvalues();
	
	/**
	 * Returns the k-th eigenvalue (&lambda;_k, for k = 0,...,N-1).
	 * Eigenvalues are sorted by magnitude (in descending order).
	 * {@code Double.Nan} is returned if the associated eigenvalue
	 * is complex-valued (non-real).
	 * @param k index 
	 * @return the k-th eigenvalue
	 */
	public double getEigenvalue(int k);
	
	/**
	 * Returns the k-th eigenvector.
	 * The ordering of the returned eigenvectors is the same as for the
	 * eigenvalues returned by {@link #getEigenvalues()}.
	 * @param k index
	 * @return the k-th eigenvector
	 */
	public double[] getEigenvector(int k);

}
