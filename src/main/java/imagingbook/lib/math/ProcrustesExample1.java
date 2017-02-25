package imagingbook.lib.math;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.lib.settings.PrintPrecision;
import imagingbook.pub.geometry.mappings.linear.AffineMapping;

public class ProcrustesExample1 extends ProcrustesExample {

	@Override
	public void run() {
		int NDIGITS = 1;
		PrintPrecision.set(10);
		
		double scale = 1; //11.5;
		System.out.println("original scale = " + scale);
		
		double alpha = 0.6;
		double[][] Q0 =
			{{ Math.cos(alpha), -Math.sin(alpha) },
			 { Math.sin(alpha),  Math.cos(alpha) }};
		
		RealMatrix R = MatrixUtils.createRealMatrix(Q0);
		System.out.println("original R = \n" + Matrix.toString(R.getData()));
		
		double[] t0 = {4, -3};
		System.out.println("original t = " + Matrix.toString(t0));
		
		List<double[]> lA = new ArrayList<double[]>();
		List<double[]> lB = new ArrayList<double[]>();
		
		lA.add(new double[] {2, 5});
		lA.add(new double[] {7, 3});
		lA.add(new double[] {0, 9});
		lA.add(new double[] {5, 4});
		
		for (double[] a : lA) {
			double[] b = R.operate(a);
			b[0] = roundToDigits(scale * b[0] + t0[0], NDIGITS);
			b[1] = roundToDigits(scale * b[1] + t0[1], NDIGITS);
			lB.add(b);
		}
		
		ProcrustesFit solver = new ProcrustesFit(lA, lB, true, false, false);
		
		System.out.println("s = " + solver.getS());
		System.out.println("Q = \n" + Matrix.toString(solver.getQ().getData()));
		System.out.println("t = \n" + Matrix.toString(solver.getT().toArray()));
		System.out.println("err1 = " + solver.getError());
		System.out.println("err2 = " + solver.getError2());
		
		AffineMapping map = solver.getAffineMapping();
		System.out.println("map = \n" + map.toString());
	}

}
