package imagingbook.lib.filter;

public abstract class GenericFilterVector extends GenericFilter {

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
		//target.copyTo(source);	// this is done in super-class
	}
	
	// calculate the result vector for a single pixel
	protected float[] doPixel(int u, int v) {
		throw new UnsupportedOperationException("method 'float[] doPixel(u,v)' not implemented!");
	}
	
	// -----------------------------------------------------------------
	
	// helper method for copying vector pixels, TODO: move to Utils or so
	public void copyPixel(float[] source, float[] target) {
		System.arraycopy(source, 0, target, 0, source.length);
	}

}
