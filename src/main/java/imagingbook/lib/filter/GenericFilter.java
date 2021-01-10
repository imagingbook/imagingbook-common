package imagingbook.lib.filter;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.PixelPack;

public abstract class GenericFilter implements ReportsProgress {
	
	private class AbortFilterException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	private PixelPack source = null;
	private PixelPack target = null;
//	private FilterProgressListener listener = null;
	private int pass = -1;

//	private double progressMinor = 0;	// 0,..,1
	
	public GenericFilter() {
	}
	
	// --------------------------------------------------------------------------------
	
//	public void setProgressListener(FilterProgressListener listener) {
//		this.listener = listener;
//	}

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
	
	// 
//	protected void setProgress() {
//		double progress = (pass + progressMinor) / passesRequired();
//		setProgress(progress);
//	}
	
//	protected void setProgress(double progress) {
//		if (listener == null) 
//			return;
//		if (progress < 0) progress = 0;
//		if (progress > 1) progress = 1;
//		IJ.log(String.format("  pass=%d, progress=%.3f  major=%.3f  minor=%.3f", 
//				getPass(), progress, (double)pass/passesRequired(), progressMinor));
//		listener.updateProgress(progress);
//	}
	
	// -----------------------------------------------------------------------------------
	
	/**
	 * The key method.
	 * @param source
	 */
	public void applyTo(PixelPack source) {
		this.source = source;
		this.target = new PixelPack(source);	// empty copy of same dimensions as source
		doFilter(source, target);
	}
	
	private void doFilter(PixelPack source, PixelPack target) {	// do we always want to copy back??
		try {
			pass = 0;
			initFilter(source, target);
			while (pass < passesRequired()) {
//				progressMinor = 0;
				initPass(source, target);
				doPass(source, target);
				target.copyTo(this.source); // copy target back to sources
				pass++;
//				setProgress();
			}
		} catch (AbortFilterException e) {};
		// the filter's result is to be found in 'source'
//		setProgress(1);
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
	
//	protected final void stepProcess(double progressMinor) {
//		if (progressMinor < 0) progressMinor = 0;
//		if (progressMinor > 1) progressMinor = 1;
////		this.progressMinor = progressMinor;
////		this.setProgress();
//	}
	
	// call this method to abort the filter
	protected final void abort() {
		throw new AbortFilterException();
	}

	protected abstract void doPass(PixelPack sourcePack, PixelPack targetPack);
	
	// experimental ----------------------------------------------------------------
	
	// this is the method called from outside (not to be overridden)
	public final double getProgress() {
		double fp = getProgressFinal();
		return this.reportProgress(fp);
	}
	
	// this method is for default and may be overridden by the terminal class if does extended work
	protected double getProgressFinal() {
		//System.out.println("GenericFilter: getProcessFinal() - returning 0");
		return 0;
	}
	
	// this method should be implemented by every class in the hierarchy except the terminal class
	public double reportProgress(double subProgress) {
		int pass = Math.max(getPass(), 0);	// getPass() returns -1 if loop is not started 
		double localProgress = (pass + subProgress) / passesRequired();
		//System.out.println("GenericFilter: reportProgress() - returning " + localProgress);
		return localProgress;
	}
	
	/*
	 * Progress reporting works like this:
	 * 
	 * double getProgress() is called from outside, result in [0,1]
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	 
	
}
