package imagingbook.lib.math;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;

/**
 * UNFINSHED!!
 * @author WB
 *
 */
public class EigensolverNxN {
	
	private final EigenDecomposition ed;
	
	public EigensolverNxN(double[][] M) {
		this.ed = new EigenDecomposition(MatrixUtils.createRealMatrix(M));
	}
	
	public double[] getEigenvalues() {
		return ed.getRealEigenvalues();
	}
	
	public double getEigenvalue(int k) {
		return ed.getRealEigenvalue(k);
	}
	
	public double[] getEigenvector(int k) {
		return ed.getEigenvector(k).toArray();
	}

}
