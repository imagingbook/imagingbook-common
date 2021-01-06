package imagingbook.lib.filter;

import imagingbook.lib.image.access.PixelPack;

public abstract class GenericFilterVector extends GenericFilter {
	
	PixelPack target = null;

	public GenericFilterVector(PixelPack pp) {
		super(pp);
	}
	
	@Override
	protected void makeTarget() {
		this.target = source.getEmptyCopy();
	}

	@Override 
	protected void doPass() {
		//PixelPack target = source.getEmptyCopy();
		final int width = source.getWidth();
		final int height = source.getHeight();
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				target.setPixel(u, v, doPixel(u, v)); // single pixel operation
			}
		}
		target.copyTo(source);	// copy targets back to sources
	}
	
	// calculate the result vector for a single pixel
	protected float[] doPixel(int u, int v) {
		throw new UnsupportedOperationException("method 'float[] doPixel(u,v)' not implemented!");
	}
	
	// -----------------------------------------------------------------
	
	// helper method for copying vector pixels
	public void copyPixel(float[] source, float[] target) {
		System.arraycopy(source, 0, target, 0, source.length);
	}

}
