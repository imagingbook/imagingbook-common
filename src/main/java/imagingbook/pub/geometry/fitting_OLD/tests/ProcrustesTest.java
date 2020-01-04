package imagingbook.pub.geometry.fitting_OLD.tests;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.fitting_OLD.ProcrustesFit;
import imagingbook.pub.geometry.mappings.linear.AffineMapping;


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
		
		ProcrustesFit pf = new ProcrustesFit(2, true, true, false);
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
		
		ProcrustesFit pf = new ProcrustesFit(2, true, true, true);
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

/*
 * ProcrustesTest ( Umeyama-Test)
lA = 
   {0.000, 0.000}
   {1.000, 0.000}
   {0.000, 2.000}
lB = 
   {0.000, 0.000}
   {-1.000, 0.000}
   {0.000, 2.000}
     --- Case 1: rotation NOT enforced (reflection allowed) -------------
c = 0.9999999999999999
Q = 
{{-1.000, -0.000}, 
{-0.000, 1.000}}
t = 
{0.000, 0.000}
err1 = 1.3322676295501878E-15
err2 = 7.395570986446985E-32
affine map = 
{{-1.000, -0.000, 0.000}, 
{-0.000, 1.000, 0.000}, 
{0.000, 0.000, 1.000}}
general map = 
{{-1.000, -0.000, 0.000}, 
{-0.000, 1.000, 0.000}}
     --- Case 2: rotation enforced (NO reflection allowed) -------------
c = 0.7211102550927977
Q = 
{{0.832, 0.555}, 
{-0.555, 0.832}}
t = 
{-0.800, 0.400}
err1 = 1.6000000000000005
err2 = 1.5999999999999999
affine map = 
{{0.600, 0.400, -0.800}, 
{-0.400, 0.600, 0.400}, 
{0.000, 0.000, 1.000}}
general map = 
{{0.600, 0.400, -0.800}, 
{-0.400, 0.600, 0.400}}
*/
