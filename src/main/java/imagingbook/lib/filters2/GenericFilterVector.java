package imagingbook.lib.filters2;

public abstract class GenericFilterVector extends GenericFilter {
	
	int w = 300;
	int h = 200;
	
	float[][] targets = null;	// sucks, needs cleanup!

	@Override 
	protected void applyTo(float[][] sources) {
		if (targets == null)	
			targets = new float[sources.length][sources[0].length]; 
		
		int depth = sources.length;
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int i = 0; // the pixel index for (u,v)
				float[] p = filterPixel(sources, u, v);	// single pixel operation
				for (int k = 0; k < depth; k++) {
					targets[k][i] = p[k]; // copy result vector to target
				}
			}
		}
		
		// copy targets back to sources
		for (int k = 0; k < depth; k++) {
			System.arraycopy(targets[k], 0, sources[k], 0, targets[k].length);
		}
	}

	// calculate the result vector for a single pixel
	protected abstract float[] filterPixel(float[][] sources, int u, int v);

}
