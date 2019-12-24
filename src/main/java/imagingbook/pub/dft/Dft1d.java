package imagingbook.pub.dft;

/**
 * Interface specifying all one-dimensional DFT/FFT implementations.
 */
public interface Dft1d {
	
	public interface Float extends Dft1d {
		public void forward(float[] gRe, float[] gIm);
		public void inverse(float[] GRe, float[] GIm);
		public void transform(float[] inRe, float[] inIm, boolean forward);
	}
	
	public interface Double extends Dft1d {
		public void forward(double[] gRe, double[] gIm);
		public void inverse(double[] GRe, double[] GIm);
		public void transform(double[] inRe, double[] inIm, boolean forward);
	}
	
}
