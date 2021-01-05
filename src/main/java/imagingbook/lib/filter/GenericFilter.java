package imagingbook.lib.filter;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.PixelPack;

public abstract class GenericFilter {
	
	protected final PixelPack source;
	private int pass = 0;
	
	protected GenericFilter(PixelPack source) {
		this.source = source;
	}
	
	protected int getPass() {
		return pass;
	}
	
	// needed?
	protected  ImageProcessor getIp() {
		return this.source.getIp();
	}
	
	// -----------------------------------------------------------------------------------
	
	public void apply() {
		setupFilter(source);	// need to pass?
		if (pass > 0) {
			throw new IllegalStateException("filter has already been applied");
		}
		pass = 0;
		while (!finished()) {
			filterAll(source);
			pass++;
		}
		
		ImageProcessor ip = source.getIp();
		if (ip != null) {	// source has an IP attached, so we need to copy back
			source.toImageProcessor(ip);
		}
		closeFilter();
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
