package imagingbook.lib.filter;

import static imagingbook.lib.image.access.PixelPack.getDepth;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.PixelPack;
import imagingbook.lib.image.access.OutOfBoundsStrategy;

public abstract class GenericFilter {
	
	public static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.NEAREST_BORDER;
	
	protected ImageProcessor ip;  // we need a reference to create the result processor
	protected final int imgWidth;
	protected final int imgHeight;
	protected final int imgDepth;
	protected final OutOfBoundsStrategy obs;

	private int pass = 0;
	
	protected GenericFilter(ImageProcessor ip, OutOfBoundsStrategy obs) {
		this.ip = ip;
		this.imgWidth = ip.getWidth();
		this.imgHeight = ip.getHeight(); 
		this.imgDepth = getDepth(ip);
		this.obs = (obs != null) ? obs : DefaultOutOfBoundsStrategy;
	}
	
	protected int getPass() {
		return pass;
		
	}
	// -----------------------------------------------------------------------------------
	
	public ImageProcessor apply() {
		return apply(false);
	}
	
	public ImageProcessor apply(boolean createNew) {
		PixelPack source = new PixelPack(ip, obs);
		setupFilter(source);
		if (pass > 0) {
			throw new IllegalStateException("filter has already been applied");
		}
		pass = 0;
		while (!finished()) {
			filterAll(source);
			pass++;
		}
		closeFilter();
		return source.toImageProcessor((createNew) ? ip.duplicate() : ip);
	}
	
	// concrete sub-classes should override to setup or
	// pre-calculate specific data structures once at the beginning
	protected void setupFilter(PixelPack source) {
		// does nothing by default
	}
	
	// concrete sub-classes should override to purge 
	// specific data structures if needed
	protected void closeFilter() {
		// does nothing by default
	}
	
	// limits the necessary number of passes, which may not be known at initialization.
	// multi-pass filters must override this method.
	protected boolean finished() {
		return (getPass() >= 1);	// do exactly 1 pass
	}

	protected abstract void filterAll(PixelPack source);
	

}
