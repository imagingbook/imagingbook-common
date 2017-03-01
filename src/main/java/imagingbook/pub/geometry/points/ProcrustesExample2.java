package imagingbook.pub.geometry.points;

import java.util.ArrayList;
import java.util.List;

import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.mappings.linear.AffineMapping;

class ProcrustesExample2 extends ProcrustesExample {

	@Override
	public void run() {
		
		showHeadline(this.getClass().getSimpleName() + " ( Umeyama-Test)");
		
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
		
		System.out.println("     --- Case 1: rotation NOT enforced (reflection allowed) -------------");
		
		ProcrustesFit solver1 = new ProcrustesFit(lA, lB, true, true, false);
		System.out.println("c = " + solver1.getScale());
		System.out.println("Q = \n" + Matrix.toString(solver1.getQ().getData()));
		System.out.println("t = \n" + Matrix.toString(solver1.getT().toArray()));
		System.out.println("err1 = " + solver1.getError());
		System.out.println("err2 = " + solver1.getEuclideanError(lA, lB));
		AffineMapping map1 = solver1.getAffineMapping2D();
		System.out.println("map = \n" + map1.toString());
		
		System.out.println("     --- Case 2: rotation enforced (NO reflection allowed) -------------");
		
		ProcrustesFit solver2 = new ProcrustesFit(lA, lB, true, true, true);
		System.out.println("c = " + solver2.getScale());
		System.out.println("Q = \n" + Matrix.toString(solver2.getQ().getData()));
		System.out.println("t = \n" + Matrix.toString(solver2.getT().toArray()));
		System.out.println("err1 = " + solver2.getError());
		System.out.println("err2 = " + solver2.getEuclideanError(lA, lB));
		AffineMapping map2 = solver2.getAffineMapping2D();
		System.out.println("map = \n" + map2.toString());
	}

}
