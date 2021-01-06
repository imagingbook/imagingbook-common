package imagingbook.lib.filter;

import imagingbook.lib.image.access.PixelPack;
import imagingbook.lib.image.access.PixelPack.PixelSlice;

public abstract class GenericFilterScalar extends GenericFilter {
	
	protected PixelSlice source = null;	// TODO: rethink, has the same name as super, use getSource() method instead!
	protected PixelSlice target = null;
	
	protected GenericFilterScalar(PixelPack pp) {
		super(pp);
	}
	
//	protected <T extends PixelPack> PixelPack getSource() {
//		return this.source;
//	}
	
	@Override
	protected void makeTarget() {
		this.target = super.source.getEmptySlice();
	}
	
	// apply filter to a stack of pixel planes (1 pass)
	protected void doPass() {
		int depth = super.source.getDepth();
		for (int k = 0; k < depth; k++) {
			this.source = super.source.getSlice(k);
			doSlice();	// default behavior: apply filter to each plane
			target.copyTo(this.source); // copy target back to sources
		}
	}
	
	protected void doSlice() {
		final int width = source.getWidth();
		final int height = source.getHeight();
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				this.target.setVal(u, v, doPixel(u, v));
			}
		}
	}

	// this method every scalar filter must implement
	// calculate the result vector for a single pixel
	protected float doPixel(int u, int v) {
		throw new UnsupportedOperationException("method 'float doPixel(u,v)' not implemented!");
	}
}
