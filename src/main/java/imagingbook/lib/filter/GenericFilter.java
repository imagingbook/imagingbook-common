package imagingbook.lib.filter;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.data.PixelPack;
import imagingbook.lib.util.progress.ProgressReporter;

public abstract class GenericFilter implements ProgressReporter {
	
	private class AbortFilterException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	private PixelPack source = null;
	private PixelPack target = null;
	private int pass = -1;
	
	public GenericFilter() {
	}
	
	// --------------------------------------------------------------------------------

	protected int getPass() {
		return pass;
	}
	
	protected int getWidth() {
		if (source == null) {
			throw new IllegalStateException("width is unknown unless filter is invoked");
		}
		return source.getWidth();
	}
	
	protected int getHeight() {
		if (source == null) {
			throw new IllegalStateException("height is unknown unless filter is invoked");
		}
		return source.getHeight();
	}
	
	protected int getDepth() {
		if (source == null) {
			throw new IllegalStateException("depth is unknown unless filter is invoked");
		}
		return source.getDepth();
	}
	
	// -----------------------------------------------------------------------------------
	
	/**
	 * The key public method.
	 * @param source
	 */
	public void applyTo(PixelPack source) {
		this.source = source;
		this.target = new PixelPack(source);	// empty copy of same dimensions as source
		runFilter(source, target);
	}
	
	private void runFilter(PixelPack source, PixelPack target) {	// do we always want to copy back??
		initFilter(source, target);
		pass = 0;
		try {
			while (pass < passesRequired()) {	// TODO: check return value of passesRequired()!
				//IJ.log("****** starting pass " + pass);
				initPass(source, target);
				runPass(source, target);
				target.copyTo(this.source); // copy target back to sources
				pass++;
			}
		} catch (AbortFilterException e) {};
		// the filter's result is to be found in 'source'
		closeFilter();
		this.source = null;
		this.target = null;
	}
	
	// -----------------------------------------------------------------------------------
	
	// bridge method to ImageProcessor
	public void applyTo(ImageProcessor ip) {
		applyTo(ip, PixelPack.DefaultOutOfBoundsStrategy);
	}
	
	// bridge method to ImageProcessor
	public void applyTo(ImageProcessor ip, OutOfBoundsStrategy obs) {
		PixelPack pp = new PixelPack(ip, obs);
		applyTo(pp);
		pp.copyToImageProcessor(ip);	// copy data back to ip
	}
	
	// -----------------------------------------------------------------------------------
	
	// called once before the filter operations starts.
	// to be used to set up temporary data structures
	// that depend on the image size
	protected void initFilter(PixelPack source, PixelPack target) {
		// does nothing by default
	}
	
	// called before every pass. override to perform
	protected void initPass(PixelPack source, PixelPack target) {
		// does nothing by default
	}
	
	// concrete sub-classes should override to purge 
	// specific data structures if needed
	protected void closeFilter() {
	}
	
	// limits the necessary number of passes, which may not be known at initialization.
	// multi-pass filters must override this method.
	protected int passesRequired() {
		return 1;	// do exactly 1 pass
	}
	
	// call this method to abort the filter prematurely
	protected final void abort() {
		throw new AbortFilterException();
	}

	protected abstract void runPass(PixelPack sourcePack, PixelPack targetPack);
	
	// progress reporting ----------------------------------------------------------------

	// this is the (only) method called from outside (not to be overridden)
	@Override
	public final double getProgress() {
		double fp = reportProgress();
		return this.reportProgress(fp);
	}
	
	// this method is for default and may be overridden by the terminal class if does extended work
	protected double reportProgress() {
		return 0;
	}
	
	// this method should be implemented by every class in the hierarchy except the terminal class
	protected double reportProgress(double subProgress) {
		int pass = Math.max(getPass(), 0);	// getPass() returns -1 if loop is not started 
		double localProgress = (pass + subProgress) / passesRequired();
		return localProgress; // this is the final value returned to the monitor
	}
}
