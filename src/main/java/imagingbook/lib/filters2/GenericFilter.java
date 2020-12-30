package imagingbook.lib.filters2;

import ij.process.ImageProcessor;

public abstract class GenericFilter {
	
	int passes = 5;	// defined by the concrete filter type
	
	protected GenericFilter() {
	}
	
	public void applyTo(ImageProcessor ip) {
		
		float[][] sources = makeSources(ip); // one pixel array for each component
		float[][] targets = new float[sources.length][sources[0].length]; 
		
		for (int pass = 0; pass < passes; pass++) {	// use something like 'morePassesNeeded()'
			applyTo(sources, targets, 0);	
			copyBack(sources, ip);
		}
	}
	
	private float[][] makeSources(ImageProcessor ip) {
		// TODO Auto-generated method stub
		return null;
	}

	private void copyBack(float[][] sources, ImageProcessor ip) {
		// TODO Auto-generated method stub
	}
	
	// ----------------------------------------------------

	protected abstract void applyTo(float[][] sources, float[][] targets, int pass);

}
