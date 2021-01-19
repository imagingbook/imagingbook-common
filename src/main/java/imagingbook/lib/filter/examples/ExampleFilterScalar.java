package imagingbook.lib.filter.examples;

import imagingbook.lib.filter.GenericFilterScalar;
import imagingbook.lib.image.data.PixelPack.PixelSlice;

public class ExampleFilterScalar extends GenericFilterScalar //implements ReportsProgress
{
	
	static float[][] H = {
			{1, 2, 1},
			{2, 4, 2},
			{1, 2, 1}};
	
	int width = 3;
	int height = 3;
	int xc = 1;
	int yc = 1;
	
	@Override
	protected float doPixel(PixelSlice plane, int u, int v) {
		float sum = 0;
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < width; i++) {
				int ui = u + i - xc;
				sum = sum + plane.getVal(ui, vj) * H[i][j];
			}
		}
		return sum / 16;
	}

}
