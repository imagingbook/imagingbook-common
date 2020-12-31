package imagingbook.lib.filter.examples;

import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilterVector;
import imagingbook.lib.image.access.FloatPixelPack;
import imagingbook.lib.image.access.OutOfBoundsStrategy;

public class ExampleFilterVector extends GenericFilterVector {

	public ExampleFilterVector(ImageProcessor ip, OutOfBoundsStrategy obs) {
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
	protected float[] filterPixel(FloatPixelPack sources, int u, int v) {
		int depth = sources.getDepth();
		float[] sum = new float[depth];
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < width; i++) {
				int ui = u + i - xc;
				float[] p = sources.getPixel(ui, vj);
				for (int k = 0; k < depth; k++) {
					sum[k] = sum[k] + (p[k] * H[i][j]) / 16;
				}
			}
		}
		return sum;
	}

}
