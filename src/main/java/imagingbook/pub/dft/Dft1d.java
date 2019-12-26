package imagingbook.pub.dft;

/**
 * Interface specifying all one-dimensional DFT/FFT implementations.
 */
public interface Dft1d {
	
	public interface Float extends Dft1d {
		/**
		 * Performs an "in-place" 1D DFT forward transformation on the supplied data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * @param gRe real part of the signal (modified)
		 * @param gIm imaginary part of the signal (modified)
		 */
		void forward(float[] gRe, float[] gIm);
		
		/**
		 * Performs an "in-place" 1D DFT inverse transformation on the supplied spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * @param GRe real part of the spectrum (modified)
		 * @param GIm imaginary part of the spectrum (modified)
		 */
		void inverse(float[] GRe, float[] GIm);
		
		/**
		 * Transforms the given 1D arrays 'in-place'. Separate arrays of identical size
		 * must be supplied for the real and imaginary parts of the signal (forward) 
		 * or spectrum (inverse), neither of which may be {@code null}.
		 * 
		 * @param inRe real part of the input signal or spectrum (modified)
		 * @param inIm imaginary part of the input signal or spectrum (modified)
		 * @param forward forward transformation if {@code true}, inverse transformation if {@code false}
		 */
		void transform(float[] inRe, float[] inIm, boolean forward);
		
		default void checkSize(float[] re, float[] im, int n) {
			if (re.length != n || im.length != n)
				throw new IllegalArgumentException("arrays for real/imagingary parts must be of size " + n);
		}
		
	}
	
	public interface Double extends Dft1d {
		
		/**
		 * Performs an "in-place" 1D DFT forward transformation on the supplied data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * @param gRe real part of the signal (modified)
		 * @param gIm imaginary part of the signal (modified)
		 */
		void forward(double[] gRe, double[] gIm);
		
		/**
		 * Performs an "in-place" 1D DFT inverse transformation on the supplied spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * @param GRe real part of the spectrum (modified)
		 * @param GIm imaginary part of the spectrum (modified)
		 */
		void inverse(double[] GRe, double[] GIm);
		
		/**
		 * Transforms the given 1D arrays 'in-place'. Separate arrays of identical size
		 * must be supplied for the real and imaginary parts of the signal (forward) 
		 * or spectrum (inverse), neither of which may be {@code null}.
		 * 
		 * @param inRe real part of the input signal or spectrum (modified)
		 * @param inIm imaginary part of the input signal or spectrum (modified)
		 * @param forward forward transformation if {@code true}, inverse transformation if {@code false}
		 */
		void transform(double[] inRe, double[] inIm, boolean forward);
		
		default void checkSize(double[] re, double[] im, int n) {
			if (re.length != n || im.length != n)
				throw new IllegalArgumentException("arrays for real/imagingary parts must be of size " + n);
		}
	}
	
}
