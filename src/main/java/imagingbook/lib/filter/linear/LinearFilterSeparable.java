package imagingbook.lib.filter.linear;

import imagingbook.lib.filter.GenericFilter;
import imagingbook.lib.filter.GenericFilterScalarSeparable;
import imagingbook.lib.image.data.PixelPack.PixelSlice;

/**
 * This class represents a 2D linear filter that is x/y-separable and
 * specified by two 1D-kernels.
 * It is based on {@link GenericFilter} and {@link GenericFilterScalarSeparable},
 * which take care of all data copying and filter mechanics.
 * Since it is a "scalar" filter, pixel values are treated as scalars.
 * If the processed image has more than one component 
 * (e.g., a RGB color image), this filter is automatically 
 * and independently applied to all (scalar-valued) components.
 * To apply to an image, use the {@link #applyTo(ij.process.ImageProcessor)}
 * method, for example.
 */
public class LinearFilterSeparable extends GenericFilterScalarSeparable {

	private final float[] hX, hY;			// the horizontal/vertical kernel arrays
	private final int width, height;		// width/height of the kernel
	private final int xc, yc;				// 'hot spot' coordinates
	
	/**
	 * Constructor, takes a 1D convolution kernel to be applied both
	 * in x- and y-direction. 
	 * 
	 * @param kernelXY a 1D convolution kernel
	 */
	public LinearFilterSeparable(Kernel1D kernelXY) {
		this(kernelXY, kernelXY);
	}
	
	/**
	 * Constructor, takes two 1D convolution kernels to be applied
	 * in x- and y-direction, respectively
	 * 
	 * @param kernelX a 1D convolution kernel for the x-direction
	 * @param kernelY a 1D convolution kernel for the y-direction
	 */
	public LinearFilterSeparable(Kernel1D kernelX, Kernel1D kernelY) {
		super();
		this.hX = kernelX.getH();
		this.hY = kernelY.getH();
		this.width = kernelX.getWidth();
		this.height = kernelY.getWidth();
		this.xc = kernelX.getXc();
		this.yc = kernelY.getXc();
	}

	// ------------------------------------------------------------------------

	// 1D convolution in x-direction
	@Override
	protected float doPixelX(PixelSlice source, int u, int v) {
		final int vj = v; // - yc;
		double sum = 0;
		for (int i = 0; i < width; i++) {
			int ui = u + i - xc;
			sum = sum + source.getVal(ui, vj) * hX[i];
		}
		return (float)sum;
	}
	
	@Override
	// 1D convolution in y-direction
	protected float doPixelY(PixelSlice source, int u, int v) {
		final int ui = u; // - xc;
		double sum = 0;
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			sum = sum + source.getVal(ui, vj) * hY[j];
		}
		return (float) sum;
	}

}
