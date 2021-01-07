package imagingbook.lib.filter;

import ij.IJ;
import ij.process.ImageProcessor;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.PixelPack;

public abstract class GenericFilter {
	
	private class AbortFilterException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	protected PixelPack source = null;
	protected PixelPack target = null;
	
	private int pass = 0;

	
	public GenericFilter() {
	}

	protected int getPass() {
		return pass;
	}
	
	// -----------------------------------------------------------------------------------
	
	/**
	 * The key method.
	 * @param source
	 */
	public void applyTo(PixelPack source) {
		this.source = source;
		IJ.log("GenericFilter: making target");
		this.target = new PixelPack(source);	// empty copy of same dimensions as source
		doFilter(source, target);
	}
	
	private void doFilter(PixelPack source, PixelPack target) {	// do we always want to copy back??
		try {
			pass = 0;
			while (!finished()) {
				setupPass();
				doPass();
				pass++;
				IJ.log("GenericFilter: copy target -> source");
				target.copyTo(this.source); // copy target back to sources
			}
		} catch (AbortFilterException e) {};
		// the filter's result is to be found in 'source'
		closeFilter();
	}
	
	// -----------------------------------------------------------------------------------
	
	// bridge method to ImageProcessor
	public void applyTo(ImageProcessor ip) {
		applyTo(ip, PixelPack.DefaultOutOfBoundsStrategy);
	}
	
	// bridge method to ImageProcessor
	public void applyTo(ImageProcessor ip, OutOfBoundsStrategy obs) {
		PixelPack pp = new PixelPack(ip, obs);
		IJ.log("GenericFilter: running applyTo");
		applyTo(pp);
		IJ.log("GenericFilter: running copy-back to ip");
		pp.copyToImageProcessor(ip);	// copy data back to ip
	}

	// -----------------------------------------------------------------------------------
	
//	protected abstract void makeTarget();
	
	// called before every pass. override to perform
	// operations at the beginning of tasks.
	protected void setupPass() {
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
	
	// call this method to abort the filter
	protected final void abort() {
		throw new AbortFilterException();
	}

	protected abstract void doPass();
	

}
