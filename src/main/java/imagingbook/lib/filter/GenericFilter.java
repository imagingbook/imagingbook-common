package imagingbook.lib.filter;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.FloatPixelPack;
import imagingbook.lib.image.access.OutOfBoundsStrategy;


public abstract class GenericFilter {
	
	public static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.NEAREST_BORDER;
	
	protected int pass = 0;
	protected ImageProcessor ip;  // we need a reference to create the result processor
	protected final int imageWidth, imageHeight;
	protected final OutOfBoundsStrategy obs;

	
	protected GenericFilter(ImageProcessor ip, OutOfBoundsStrategy obs) {
		this.ip = ip;
		this.imageWidth = ip.getWidth();
		this.imageHeight = ip.getHeight();
		this.obs = (obs != null) ? obs : DefaultOutOfBoundsStrategy;
	}
	
	// -----------------------------------------------------------------------------------
	
	public ImageProcessor apply() {
		return apply(false);
	}
	
	public ImageProcessor apply(boolean createNew) {
		FloatPixelPack source = new FloatPixelPack(ip, obs);
		if (pass > 0) {
			throw new IllegalStateException("filter has already been applied");
		}
		do {
			filterAll(source);
			pass++;
		} while (doMorePasses());
		return source.toImageProcessor((createNew) ? ip.duplicate() : ip);
	}
	
	// limits the necessary number of passes, which may not be known at initialization.
	// multi-pass filters must override this method.
	protected boolean doMorePasses() {
		return false;
	}	

	protected abstract void filterAll(FloatPixelPack source);
	

}
