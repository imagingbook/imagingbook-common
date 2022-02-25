package imagingbook.lib.color;

public abstract class RgbUtils {

	public static int[] intToRgb(int p) {
		int[] RGB = new int[3];
		intToRgb(p, RGB);
		return RGB;
	}
	
	public static void intToRgb(int p, int[] RGB) {
		RGB[0] = ((p >> 16) & 0xFF);
		RGB[1] = ((p >> 8) & 0xFF);
		RGB[2] = (p & 0xFF);
	}

	public static int rgbToInt(int red, int grn, int blu) {
		return ((red & 0xff)<<16) | ((grn & 0xff)<<8) | blu & 0xff;
	}

}
