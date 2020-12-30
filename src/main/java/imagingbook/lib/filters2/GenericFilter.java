package imagingbook.lib.filters2;

import ij.process.ImageProcessor;

public abstract class GenericFilter implements AutoCloseable {
	
	protected int pass = 0;
	ImageProcessor ip;  // no reference needed?
	
	protected GenericFilter() {
		this.ip = null;
	}
	
	protected GenericFilter(ImageProcessor ip) {
		this.ip = ip;
	}
	
	public ImageProcessor apply() {
		float[][] sources = makeSources(ip); // one pixel array for each component
		pass = 0;
		do {
			applyTo(sources);
			pass++;
		} while (morePasses());
		ImageProcessor result = makeResultIp(sources);
		return result;	
	}
	
	private float[][] makeSources(ImageProcessor ip) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// limits the necessary number of passes, which may not be known at initialization.
	// multi-pass filters must override this method.
	protected boolean morePasses() {
		return false;
	}


	private ImageProcessor makeResultIp(float[][] sources) {
		// TODO create the image processor to return
		return null;
	}
	
	// ----------------------------------------------------

	protected abstract void applyTo(float[][] sources);
	
	@Override
	public void close() throws Exception {
		// TODO close all allocated resources of this filter
		this.ip = null;
	}

}
