package imagingbook.lib.color;

public abstract class Rgb {

	public static int[] intToRgb(int rgb) {
		int red = ((rgb >> 16) & 0xFF);
		int grn = ((rgb >> 8) & 0xFF);
		int blu  = (rgb & 0xFF);
		return new int[] {red, grn, blu};
	}

	public static int rgbToInt(int red, int grn, int blu) {
		return ((red & 0xff)<<16) | ((grn & 0xff)<<8) | blu & 0xff;
	}

}
