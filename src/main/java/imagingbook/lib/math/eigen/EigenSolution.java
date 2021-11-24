package imagingbook.lib.math.eigen;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

public class EigenSolution extends Pair<RealVector, RealMatrix> {

	public EigenSolution(RealVector eVals, RealMatrix eVecs) {
		super(eVals, eVecs);
	}
	
	public RealVector getEigenValues() {
		return this.getFirst();
	}
	
	public RealMatrix getEigenVectors() {
		return this.getSecond();
	}

}
