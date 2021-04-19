package imagingbook.lib.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;

/**
 * @author WB
 *
 */
public class EigensolverNxN implements Eigensolver {

	private final EigenDecomposition ed;
	private final double[] eVals;
	private final int[] permutation;

	public EigensolverNxN(double[][] M) {
		if (!Matrix.isSquare(M)) {
			throw new IllegalArgumentException("matrix M must be square");
		}
		this.ed = new EigenDecomposition(MatrixUtils.createRealMatrix(M));
		this.eVals = ed.getRealEigenvalues();
		this.permutation = sortArray(eVals);
	}

	@Override
	public double[] getEigenvalues() {
		return eVals;
	}

	@Override
	public double getEigenvalue(int k) {
		return eVals[k];
	}

	@Override
	public double[] getEigenvector(int k) {
		return ed.getEigenvector(permutation[k]).toArray();
	}

	@Override
	public boolean isReal() {
		return !ed.hasComplexEigenvalues();
	}

	// Sorts vals (in descending order) and returns the associated permutation
	// (with help from https://stackoverflow.com/a/11998394).
	private int[] sortArray(double[] vals) {
		List<Integer> indices = new ArrayList<>(vals.length);
		for (int i = 0; i < vals.length; i++) {
			indices.add(i);
		}
		Comparator<Integer> comparator = new Comparator<Integer>() {
			@Override
			public int compare(Integer a, Integer b) {
				return Double.compare(vals[b], vals[a]);
			}
		};

		Collections.sort(indices, comparator);
		int[] perm = indices.stream().mapToInt(Integer::intValue).toArray();
		double[] valsC = vals.clone();
		for (int i = 0; i < vals.length; i++) {
			vals[i] = valsC[perm[i]];
		}

		return perm;
	}

}
