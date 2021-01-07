package imagingbook.lib.filter.examples;

import imagingbook.lib.filter.GenericFilterVector;
import imagingbook.lib.image.access.PixelPack;

public class ExampleFilterVector extends GenericFilterVector {

	public ExampleFilterVector() {
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
	protected float[] doPixel(PixelPack pack, int u, int v) {
		int depth = pack.getDepth();
		float[] sum = new float[depth];
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < width; i++) {
				int ui = u + i - xc;
				float[] p = pack.getPixel(ui, vj);
				for (int k = 0; k < depth; k++) {
					sum[k] = sum[k] + (p[k] * H[i][j]) / 16;
				}
			}
		}
		return sum;
	}

}
