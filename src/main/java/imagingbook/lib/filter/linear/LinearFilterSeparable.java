package imagingbook.lib.filter.linear;

import imagingbook.lib.filter.GenericFilterScalarSeparable;
import imagingbook.lib.image.data.PixelPack.PixelSlice;

public class LinearFilterSeparable extends GenericFilterScalarSeparable {

	private final float[] hX, hY;			// the horizontal/vertical kernel arrays
	private final int width, height;		// width/height of the kernel
	private final int xc, yc;				// 'hot spot' coordinates
	
	public LinearFilterSeparable(Kernel1D kernelXY) {
		this(kernelXY, kernelXY);
	}
	
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
