package imagingbook.pub.geometry.icp.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import imagingbook.pub.geometry.icp.IterativeClosestPointMatcher_OLD;
import imagingbook.pub.geometry.mappings.linear.AffineMapping;
import imagingbook.pub.geometry.mappings.linear.Rotation;
import imagingbook.pub.geometry.mappings.linear.Translation;

public class IcpTest1 {
	
	static int m = 150;
	static int size = 100;
	static double theta = 1.5;
	static double dx = 4;
	static double dy = -6;
	
	static double sigma = 2.5;

	
	static double tau = 0.1;
	static int kMax = 20;
	
	AffineMapping A = null;
	List<double[]> X, Y;
	
	Random rnd = new Random(11);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new IcpTest1().run();
	}
	
	void run() {
		A = makeTransformation();
		makeSamplePointsX();
		makeSamplePointsY();
		IterativeClosestPointMatcher_OLD icp = new IterativeClosestPointMatcher_OLD(X, Y, tau, kMax);
		
		System.out.println("ICP has converged: " + icp.hasConverged());
	}

	private AffineMapping makeTransformation() {
		double ctr = 0.5 * size;
		AffineMapping am = new AffineMapping();
		am.concatDestructive(new Translation(-ctr, -ctr));	// TODO: rename to concatD
		am.concatDestructive(new Rotation(theta));
		am.concatDestructive(new Translation(ctr, ctr));
		am.concatDestructive(new Translation(dx, dy));
		return am;
	}

	private void  makeSamplePointsX() {
		X = new ArrayList<double[]>(m);
		for (int i = 0; i < m; i++) {
			double x = rnd.nextInt(size);
			double y = rnd.nextInt(size);
			X.add(new double[] {x, y});
		}
	}

	private void makeSamplePointsY() {
		Y = new ArrayList<double[]>(m);
		for (double[] xi : X) {
			double[] xiT = A.applyTo(xi);
			xiT[0] = xiT[0] + sigma * rnd.nextGaussian();
			xiT[1] = xiT[1] + sigma * rnd.nextGaussian();
			Y.add(xiT);
		}
		
	}
}
