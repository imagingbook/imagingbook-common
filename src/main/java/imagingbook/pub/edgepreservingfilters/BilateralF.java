package imagingbook.pub.edgepreservingfilters;

import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.math.VectorNorm.NormType;
import imagingbook.lib.util.ParameterBundle;

public interface BilateralF {
	
	public static class Parameters implements ParameterBundle {
		/** Sigma (width) of domain filter */
		public double sigmaD = 2; 		
		/** Sigma (width) of range filter */
		public double sigmaR = 50;
		/** Distance norm to use between color vectors */
		public NormType colorNormType = NormType.L2;
		/** Out-of-bounds strategy */
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NearestBorder;
	}
	
	// ------------------------------------------------------
	
	public static float gauss(double d, double sigmaR2) {
		return (float) Math.exp(-(d * d) / (2 * sigmaR2));
	}
	
	// This returns the weights for a Gaussian range kernel (scalar version):
//	protected static float similarityGauss(float a, float b, double sigmaR2) {
//		//double dI = a - b;
//		return gauss(a - b, sigmaR2);
//		//return (float) Math.exp(-(dI * dI) / (2 * sigmaR2));
//	}
	
//	@SuppressWarnings("unused")
//	// for better efficiency: pre-tabulated version of the range kernel - CHECK!
//	private float[] makeRangeKernel(double sigma, int K) {
//		int size = K + 1 + K;
//		float[] rangeKernel = new float[size]; //center cell = kernel[K]
//		double sigma2 = sigma * sigma;
//		for (int i = 0; i < size; i++) {
//			double x = K - i;
//			rangeKernel[i] =  (float) Math.exp(-0.5 * (x*x) / sigma2);
//		}
//		return rangeKernel;
//	}

}
