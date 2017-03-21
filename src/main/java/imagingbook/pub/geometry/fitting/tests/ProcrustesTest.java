package imagingbook.pub.geometry.fitting.tests;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.mappings.linear.AffineMapping;
import imagingbook.pub.geometry.fitting.ProcrustesFit;


class ProcrustesTest {

	public static void main(String[] args) {
		
		System.out.println(ProcrustesTest.class.getSimpleName() + " ( Umeyama-Test)");
		
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
		
		System.out.println("lA = "); print(lA);
		System.out.println("lB = "); print(lB);
		
		{
		System.out.println("     --- Case 1: rotation NOT enforced (reflection allowed) -------------");
		
		ProcrustesFit pf = new ProcrustesFit(true, true, false);
		pf.fit(lA, lB);
		System.out.println("c = " + pf.getScale());
		System.out.println("Q = \n" + Matrix.toString(pf.getR().getData()));
		System.out.println("t = \n" + Matrix.toString(pf.getT().toArray()));
		System.out.println("err1 = " + pf.getError());
		System.out.println("err2 = " + pf.getEuclideanError(lA, lB));

		AffineMapping amap = pf.getAffineMapping2D();
		System.out.println("affine map = \n" + amap.toString());
		
		RealMatrix map = pf.getTransformationMatrix();
		System.out.println("general map = \n" + Matrix.toString(map.getData()));
		}
		
		{
		System.out.println("     --- Case 2: rotation enforced (NO reflection allowed) -------------");
		
		ProcrustesFit pf = new ProcrustesFit(true, true, true);
		pf.fit(lA, lB);
		System.out.println("c = " + pf.getScale());
		System.out.println("Q = \n" + Matrix.toString(pf.getR().getData()));
		System.out.println("t = \n" + Matrix.toString(pf.getT().toArray()));
		System.out.println("err1 = " + pf.getError());
		System.out.println("err2 = " + pf.getEuclideanError(lA, lB));

		AffineMapping amap = pf.getAffineMapping2D();
		System.out.println("affine map = \n" + amap.toString());
		
		RealMatrix map = pf.getTransformationMatrix();
		System.out.println("general map = \n" + Matrix.toString(map.getData()));
		}
	}

	static void print(List<double[]> lX) {
		for (double[] x : lX) {
			System.out.println("   " + Matrix.toString(x));
		}
	}
}
