package imagingbook.lib.filter.examples;

import ij.IJ;
import imagingbook.lib.filter.GenericFilter;
import imagingbook.lib.filter.GenericFilterVector;
import imagingbook.lib.filter.ProgressMonitor;
import imagingbook.lib.image.access.PixelPack;

public class FilterShowProgressExample extends GenericFilterVector {

	// variables for progress reporting
	int maxCnt = 10000;
	int cnt;
	
	@Override
	protected void initFilter(PixelPack source, PixelPack target) {	
		//this.n = source.getWidth() * source.getHeight();
		IJ.log("needs passes " + this.passesRequired());
	}
	
	protected void initPass(PixelPack source, PixelPack target) {
		//this.n = source.getWidth() * source.getHeight();
//		System.out.println("**** pass " + this.getPass() + " prog = " + this.getProgress());
//		System.out.println("    GenericFilterVector.this.iter = " + super.iter);
	}
	
	protected int passesRequired() {
		return 3;	// needs 3 passes
	}
	
	@Override
	protected float[] doPixel(PixelPack source, int u, int v) {
		float[] dummy = new float[3];
		float sum = 0;
		cnt = 0;
		for (int i = 0; i < maxCnt; i++) {
			this.cnt = i;
//			if (i % 2 == 0) {
//				IJ.log("    i = " + i + " prog = " + this.getProgress());
//			}
			sum += (float) Math.sqrt(i);
		}
		cnt = 0;
		dummy[0] = sum;
		return dummy;
	}
	
	// -------------------------------------------------------------
	
	@Override
	protected double getProgressFinal() {
		double prog = (double) this.cnt / this.maxCnt;
		//System.out.println("FilterShowProgressExample: getProcessFinal() - returning " + prog);
		return prog;
	}
	
	public static void main(String[] args) {
		GenericFilter filter = new FilterShowProgressExample();
		ProgressMonitor mon = new ProgressMonitor(filter, 500);
		//System.out.println("main: getProgress() - returning " + filter.getProgress());
		mon.start();
		PixelPack pp = new PixelPack(400, 300, 3, null);
		filter.applyTo(pp);
		mon.terminate();
	}

}
