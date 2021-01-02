package imagingbook.lib.filter;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.PixelPack.PixelSlice;
import imagingbook.lib.filter.kernel.Kernel1D;
import imagingbook.lib.image.access.OutOfBoundsStrategy;

public class LinearFilterSeparable extends GenericFilterScalar {

	private final float[] hX, hY;			// the horizontal/vertical kernel arrays
	private final int width, height;		// width/height of the kernel
	private final int xc, yc;				// 'hot spot' coordinates
	
	public LinearFilterSeparable(ImageProcessor ip, Kernel1D kernelXY, OutOfBoundsStrategy obs) {
		this(ip, kernelXY, kernelXY, obs);
	}
	
	public LinearFilterSeparable(ImageProcessor ip, Kernel1D kernelX, Kernel1D kernelY, OutOfBoundsStrategy obs) {
		super(ip, obs);
		this.hX = kernelX.getH();
		this.hY = kernelY.getH();
		this.width = kernelX.getWidth();
		this.height = kernelY.getWidth();
		this.xc = kernelX.getXc();
		this.yc = kernelY.getXc();
	}

	@Override
	protected float filterPixel(PixelSlice source, int u, int v) {
		switch (getPass()) {
		case 0: return filterPixelX(source, u, v);
		case 1: return filterPixelY(source, u, v);
		default: throw new RuntimeException("invalid pass number " + getPass());
		}
	}
	
	@Override
	protected int passesNeeded() {
		return 2;	// this filter needs 2 passes
	}
	
	// ------------------------------------------------------------------------

	// 1D convolution in x-direction
	private float filterPixelX(PixelSlice source, int u, int v) {
		final int vj = v; // - yc;
		double sum = 0;
		for (int i = 0; i < width; i++) {
			int ui = u + i - xc;
			sum = sum + source.getVal(ui, vj) * hX[i];
		}
		return (float)sum;
	}
	
	// 1D convolution in y-direction
	private float filterPixelY(PixelSlice source, int u, int v) {
		final int ui = u; // - xc;
		double sum = 0;
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			sum = sum + source.getVal(ui, vj) * hY[j];
		}
		return (float) sum;
	}
	


}
