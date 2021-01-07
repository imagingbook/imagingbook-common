package imagingbook.lib.filter;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.PixelPack;

public abstract class GenericFilter {
	
	private class AbortFilterException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	protected PixelPack source = null;
	private int pass = 0;

	
	public GenericFilter() {
	}

	protected int getPass() {
		return pass;
	}
	
	// -----------------------------------------------------------------------------------
	
	public void applyTo(PixelPack source) {
		this.source = source;
		makeTarget();
		try {
			pass = 0;
			while (!finished()) {
				setupPass();
				doPass();
				pass++;
			}
		} catch (AbortFilterException e) {};
		// the filter's result is to be found in 'source'
		//source.updateImageProcessor();
		closeFilter();
	}
	
	// -----------------------------------------------------------------------------------
	
	public void applyTo(ImageProcessor ip) {
		applyTo(ip, PixelPack.DefaultOutOfBoundsStrategy);
	}
	
	public void applyTo(ImageProcessor ip, OutOfBoundsStrategy obs) {
		PixelPack pp = new PixelPack(ip, obs);
		applyTo(pp);
		pp.copyToImageProcessor(ip);	// copy data back to ip
	}

	// -----------------------------------------------------------------------------------
	
	protected abstract void makeTarget();
	
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
