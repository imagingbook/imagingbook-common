package imagingbook.lib.filter.examples;

import ij.IJ;
import imagingbook.lib.filter.GenericFilterVector;
import imagingbook.lib.image.access.PixelPack;

public class FilterShowProgressExample extends GenericFilterVector {

	float[] dummy = new float[3];
//	int n;
	int cnt;
	
	@Override
	protected void initFilter(PixelPack source, PixelPack target) {
		//this.n = source.getWidth() * source.getHeight();
		IJ.log("needs passes " + this.passesRequired());
	}
	
	protected void initPass(PixelPack source, PixelPack target) {
		//this.n = source.getWidth() * source.getHeight();
		IJ.log(" ***********************+ pass " + this.getPass());
		cnt = 0;
	}
	
	protected int passesRequired() {
		return 3;	// needs 3 passes
	}
	
	@Override
	protected float[] doPixel(PixelPack source, int u, int v) {
		cnt++;
		if (cnt % 2000 == 0) {
			int n = source.getWidth() * source.getHeight();
			IJ.log("   step " + cnt);
			this.stepProcess((double)cnt/n);
		}
		// here comes the actual filter's work
//		try {
//			//Thread.sleep(1);
//			Thread.sleep(0, 25);	// sleep 
//			
//		} catch (InterruptedException e) {}
		float sum = 0;
		for (int i = 0; i < 1000; i++) {
			sum += (float) Math.sqrt(i);
		}
		dummy[0] = sum;
		return dummy;
	}

}
