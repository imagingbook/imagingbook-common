package imagingbook.test;

import org.apache.commons.math3.ml.distance.EarthMoversDistance;

public class EarthMoversDistanceTest {
	
	static double[] A = {0,0,1,2,3,4};
	static double[] B = {0,1,2,3,4,0};

	public static void main(String[] args) {
		EarthMoversDistance emd = new EarthMoversDistance();
		double dist = emd.compute(A, B);
		System.out.println("dist = " + dist);
	}

}
