package imagingbook.lib.filter;

import ij.process.ImageProcessor;
import imagingbook.lib.filter.kernel.Kernel2D;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.PixelPack.PixelSlice;

public class LinearFilter extends GenericFilterScalar {

	private final float[][] H;			// the kernel matrix
	private final int width, height;	// width/height of the kernel
	private final int xc, yc;			// 'hot spot' coordinates
	
	public LinearFilter(ImageProcessor ip, Kernel2D kernel, OutOfBoundsStrategy obs) {
		super(ip, obs);
		this.H = kernel.getH();
		this.width = kernel.getWidth();
		this.height = kernel.getHeight();
		this.xc = kernel.getXc();
		this.yc = kernel.getYc();
	}

	@Override
	protected float filterPixel(PixelSlice source, int u, int v) {
		double sum = 0;
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < width; i++) {
				int ui = u + i - xc;
				sum = sum + source.getVal(ui, vj) * H[i][j];
			}
		}
 		return (float)sum;
	}

}
