package imagingbook.lib.filter.examples;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.PixelPack.PixelSlice;
import imagingbook.lib.filter.GenericFilterScalar;
import imagingbook.lib.image.access.OutOfBoundsStrategy;

public class ExampleFilterScalar extends GenericFilterScalar {
	
	public ExampleFilterScalar(ImageProcessor ip, OutOfBoundsStrategy obs) {
		super(ip, obs);
	}

	int width = 3;
	int height = 3;
	int xc = 1;
	int yc = 1;
	
	static float[][] H = {
			{1, 2, 1},
			{2, 4, 2},
			{1, 2, 1}};
	
	@Override
	protected float filterPixel(PixelSlice source, int u, int v) {
		float sum = 0;
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < width; i++) {
				int ui = u + i - xc;
				sum = sum + source.getVal(ui, vj) * H[i][j];
			}
		}
		return sum / 16;
	}

}
