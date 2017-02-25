package imagingbook.lib.math;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.pub.geometry.mappings.linear.AffineMapping;

public class ProcrustesExample2 extends ProcrustesExample {

	@Override
	public void run() {
		
		List<double[]> lA = new ArrayList<double[]>();
		List<double[]> lB = new ArrayList<double[]>();
		
		double[] a = {0, 0};
		double[] b = {1, 0};
		double[] c = {0, 2};
		
		double[] A = {0, 0};
		double[] B = {-1, 0};
		double[] C = {0, 2};
		
		lA.add(a);
		lA.add(b);
		lA.add(c);
		
		lB.add(A);
		lB.add(B);
		lB.add(C);
		
		ProcrustesFit solver = new ProcrustesFit(lA, lB, true, true, false);
		
		System.out.println("s = " + solver.getS());
		System.out.println("Q = \n" + Matrix.toString(solver.getQ().getData()));
		System.out.println("t = \n" + Matrix.toString(solver.getT().toArray()));
		System.out.println("err1 = " + solver.getError());
		System.out.println("err2 = " + solver.getError2());
		
		AffineMapping map = solver.getAffineMapping();
		System.out.println("map = \n" + map.toString());
	}

}
