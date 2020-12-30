package imagingbook.pub.color.quantize;

import java.awt.image.IndexColorModel;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import imagingbook.lib.color.Rgb;

public abstract class ColorQuantizer {
	
	protected final static int MAX_RGB = 255;
	
	/**
	 * Retrieves the color map produced by this color quantizer.
	 * The returned array is in the format int[idx][rgb], where
	 * rgb = 0 (red), 1 (green), 2 (blue) and  0 &le; idx &lt; nColors.
	 * This method must be implemented by any derived concrete class.
	 * 
	 * @return The table of quantization colors.
	 */
	public abstract int[][] getColorMap();
	
	// ---------------------------------------------------------------
	
	/**
	 * Performs color quantization on the given full-color RGB image
	 * and creates an indexed color image.
	 * 
	 * @param cp The original full-color RGB image.
	 * @return The quantized (indexed color) image.
	 */
	public ByteProcessor quantize(ColorProcessor cp) {
		int[][] colormap = this.getColorMap();
		if (colormap.length > 256) 
			throw new Error("cannot index to more than 256 colors");
		int w = cp.getWidth();
		int h = cp.getHeight();
		int[]  rgbPixels = (int[]) cp.getPixels();
		byte[] idxPixels = new byte[rgbPixels.length];

		for (int i = 0; i < rgbPixels.length; i++) {
			idxPixels[i] = (byte) this.findColorIndex(rgbPixels[i]);
		}

		IndexColorModel idxCm = makeIndexColorModel(colormap);
		return new ByteProcessor(w, h, idxPixels, idxCm);
	}
	
	private IndexColorModel makeIndexColorModel(int[][] colormap) {
		final int nColors = colormap.length;
		byte[] rMap = new byte[nColors];
		byte[] gMap = new byte[nColors];
		byte[] bMap = new byte[nColors];
		for (int i = 0; i < nColors; i++) {
			rMap[i] = (byte) colormap[i][0];
			gMap[i] = (byte) colormap[i][1];
			bMap[i] = (byte) colormap[i][2];
		}
		return new IndexColorModel(8, nColors, rMap, gMap, bMap);
	}
	
	/**
	 * Performs color quantization on the given sequence of
	 * ARGB-encoded color values and returns a new sequence 
	 * of quantized colors.
	 * 
	 * @param origPixels The original ARGB-encoded color values.
	 * @return The quantized ARGB-encoded color values.
	 */
	public int[] quantize(int[] origPixels) {
		int[] qantPixels = new int[origPixels.length];
		for (int i = 0; i < origPixels.length; i++) {
			qantPixels[i] = quantize(origPixels[i]);
		}
		return qantPixels;
	}
	
	/**
	 * Performs color quantization on the given ARGB-encoded color 
	 * value and returns the associated quantized color. 
	 * @param p The original ARGB-encoded color value.
	 * @return The quantized ARGB-encoded color value.
	 */
	public int quantize(int p) {
		int[][] colormap = getColorMap();
		int idx = findColorIndex(p);
		int red = colormap[idx][0];
		int grn = colormap[idx][1];
		int blu = colormap[idx][2];
		return Rgb.rgbToInt(red, grn, blu);
	}
	
	/**
	 * Finds the color table index of the color that is "closest" to the supplied
	 * RGB color (minimum Euclidean distance in color space). 
	 * This method may be overridden by inheriting classes, for example, to use
	 * quick indexing in the octree method.
	 *  
	 * @param p Original color, encoded as an ARGB integer.
	 * @return The associated color table index.
	 */
	protected int findColorIndex(int p) {
		int[][] colormap = getColorMap();
		int[] rgb = Rgb.intToRgb(p);
		int n = colormap.length;
		int minD2 = Integer.MAX_VALUE;
		int minIdx = -1;
		for (int i = 0; i < n; i++) {
			final int red = colormap[i][0];
			final int grn = colormap[i][1];
			final int blu = colormap[i][2];
			int d2 = sqr(red - rgb[0]) + sqr(grn - rgb[1]) + sqr(blu - rgb[2]);	// dist^2
			if (d2 < minD2) {
				minD2 = d2;
				minIdx = i;
			}
		}
		return minIdx;
	}
	
	public void listColorMap() {
		int[][] colormap = getColorMap();
		int n = colormap.length;
		for (int i = 0; i < n; i++) {
			int red = 0xff & colormap[i][0];
			int grn = 0xff & colormap[i][1];
			int blu = 0xff & colormap[i][2];
			System.out.println(String.format("i=%3d: r=%3d g=%3d b=%3d", i, red, grn, blu));
		}
	}
	
	// -----------------------------------------------------------------------------
	
	protected int log2(int n){
		if(n <= 0) throw new IllegalArgumentException();
		return 31 - Integer.numberOfLeadingZeros(n);
	}
	
	protected int sqr(int k) {
		return k * k;
	}

}
