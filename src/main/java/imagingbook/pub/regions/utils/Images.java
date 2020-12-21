package imagingbook.pub.regions.utils;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.lib.color.RandomColorGenerator;
import imagingbook.pub.regions.BinaryRegionSegmentation;

public abstract class Images {
	
	/**
	 * Utility method that creates an image of the internal
	 * label array.
	 * 
	 * @param color set {@code false} to get a 16-bit grayscale image,
	 * {@code true} for an RGB (24-bit) color image.
	 * @return an image of the label array.
	 */
	public static ImageProcessor makeLabelImage(BinaryRegionSegmentation labeling, boolean color) {
		return (color) ?  makeLabelImageColor(labeling) : makeLabelImageGray(labeling);
	}

	private static ColorProcessor makeLabelImageColor(BinaryRegionSegmentation labeling) {
		int maxLabel = labeling.getMaxLabel();
		int[] colorLUT = new int[maxLabel+1];
		RandomColorGenerator rcg = new RandomColorGenerator();
		
		for (int i = BinaryRegionSegmentation.START_LABEL; i <= maxLabel; i++) {
			colorLUT[i] = rcg.nextColor().getRGB(); //makeRandomColor();
		}
		
		int width = labeling.getWidth();
		int height = labeling.getHeight();
		
		ColorProcessor cp = new ColorProcessor(width, height);
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int lb = labeling.getLabel(u, v);
				if (lb >= 0 && lb < colorLUT.length) {
					cp.putPixel(u, v, colorLUT[lb]);
				}
			}
		}
		return cp;
	}
	
	private static ShortProcessor makeLabelImageGray(BinaryRegionSegmentation labeling) {
		int width = labeling.getWidth();
		int height = labeling.getHeight();
		ShortProcessor sp = new ShortProcessor(width, height);
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int lb = labeling.getLabel(u, v);
				sp.set(u, v, (lb >= 0) ? lb : 0);
			}
		}
		sp.resetMinAndMax();
		return sp;
	}
	
//	private static int makeRandomColor() {
//		double saturation = 0.2;
//		double brightness = 0.2;
//		float h = (float) Math.random();
//		float s = (float) (saturation * Math.random() + 1 - saturation);
//		float b = (float) (brightness * Math.random() + 1 - brightness);
//		return Color.HSBtoRGB(h, s, b);
//	}

}
