package imagingbook.lib.image.access;

import java.util.Arrays;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.lib.color.Rgb;

// TODO: Merge with image accessors
public class FloatPixelPack {
	
	@SuppressWarnings("unused")
	private final ImageProcessor ip;
	
	private final float[][] pixels;
	private final int length;
	private final int depth;
	private final PixelIndexer indexer;
	
	public FloatPixelPack(ImageProcessor ip, OutOfBoundsStrategy obs) {
		this.ip = ip;
		this.pixels = fromImageProcessor(ip);
		this.depth = pixels.length;
		this.length = pixels[0].length;
		this.indexer = PixelIndexer.create(ip.getWidth(), ip.getHeight(), obs);
	}
	
	public FloatPixelPack(FloatPixelPack orig) {
		this.ip = null;
		this.pixels = new float[orig.depth][orig.length];
		this.depth = orig.depth;
		this.length = orig.length;
		this.indexer = orig.indexer;
	}
	
	// returns a new pixel array
	public float[] getPixel(int u, int v) {
		return getPixel(u, v, new float[depth]);
	}
	
	// fills in and returns an existing pixels array
	public float[] getPixel(int u, int v, float[] vals) {
		//float[] vals = new float[depth];
		final int i = indexer.getIndex(u, v);
		if (i < 0) {	// i = -1 --> default value (zero)
			Arrays.fill(vals, 0);
		}
		else {	
			for (int k = 0; k < depth; k++) {
				vals[k] = pixels[k][i];
			}
		}
		return vals;
	}
	
	public void setPixel(int u, int v, float[] vals) {
		final int i = indexer.getIndex(u, v);
		if (i >= 0) {
			for (int k = 0; k < depth; k++) {
				pixels[k][i] = vals[k];
			}
		}
	}
	
	public FloatPixelPack getEmptyCopy() {
		return new FloatPixelPack(this);
	}
	
	public void copyTo(FloatPixelPack other) {
		for (int k = 0; k < this.depth; k++) {
			System.arraycopy(this.pixels[k], 0, other.pixels[k], 0, this.length);
		}
	}
	
	public Slice getSlice(int k) {
		if (k < 0 || k >= depth) {
			throw new IllegalArgumentException("illegal slice number " + k);
		}
		return new Slice(k);
	}
	
	public Slice getEmptySlice() {
		return new Slice();
	}
	
	public float[][] getPixels() {
		return pixels;
	}
	
	public int getDepth() {
		return this.depth;
	}
	
	public ImageProcessor toImageProcessor(ImageProcessor ip2) {
		return copyToImageProcessor(this.pixels, ip2);
	}

	
	// -------------------------------------------------------------------
	
	public class Slice {
		final int idx;
		final float[] vals;
		
		Slice(int idx) {
			this.idx = idx;
			this.vals = pixels[idx];
		}
		
		Slice() {
			this.idx = -1;
			this.vals = new float[length];
		}
		
		public float getVal(int u, int v) {
			int i = indexer.getIndex(u, v);
			return (i >= 0) ? vals[i] : 0;
		}
		
		public void setVal(int u, int v, float val) {
			int i = indexer.getIndex(u, v);
			if (i >= 0) {
				vals[i] = val;
			}
		}
		
		public float[] getPixels() {
			return vals;
		}
		
		public void copyTo(Slice other) {
			System.arraycopy(this.vals, 0, other.vals, 0, this.vals.length);
		}
	}
	
	// -------------------------------------------------------------------
	
	public static float[][] fromImageProcessor(ImageProcessor ip) {
		if (ip instanceof ByteProcessor)
			return fromByteProcessor((ByteProcessor)ip);
		if (ip instanceof ShortProcessor)
			return fromShortProcessor((ShortProcessor)ip);
		if (ip instanceof FloatProcessor)
			return fromFloatProcessor((FloatProcessor)ip);
		if (ip instanceof ColorProcessor)
			return fromColorProcessor((ColorProcessor)ip);
		throw new IllegalArgumentException("unknown processor type " + ip.getClass().getSimpleName());
	}
	
	public static float[][] fromByteProcessor(ByteProcessor ip) {
		byte[] pixels = (byte[]) ip.getPixels();
		float[] P = new float[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			P[i] = 0xff & pixels[i];
		}
		return new float[][] {P};
	}
	
	public static float[][] fromShortProcessor(ShortProcessor ip) {
		short[] pixels = (short[]) ip.getPixels();
		float[] P = new float[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			P[i] = 0xffff & pixels[i];
		}
		return new float[][] {P};
	}
	
	public static float[][] fromFloatProcessor(FloatProcessor ip) {
		float[] pixels = (float[]) ip.getPixels();
		float[] P = pixels.clone();
		return new float[][] {P};
	}
	
	public static float[][] fromColorProcessor(ColorProcessor ip) {
		int[] pixels = (int[]) ip.getPixels();
		float[] R = new float[pixels.length];
		float[] G = new float[pixels.length];
		float[] B = new float[pixels.length];
		int[] RGB = new int[3];
		for (int i = 0; i < pixels.length; i++) {
			Rgb.intToRgb(pixels[i], RGB);
			R[i] = RGB[0];
			G[i] = RGB[1];
			B[i] = RGB[2];
		}
		return new float[][] {R, G, B};
	}
	
	// --------------------------------------------------------------------
	
	public static ImageProcessor copyToImageProcessor(float[][] sources, ImageProcessor ip) {
		if (ip instanceof ByteProcessor)
			copyToByteProcessor(sources, (ByteProcessor)ip);
		else if (ip instanceof ShortProcessor)
			copyToShortProcessor(sources, (ShortProcessor)ip);
		else if (ip instanceof FloatProcessor)
			copyToFloatProcessor(sources, (FloatProcessor)ip);
		else if (ip instanceof ColorProcessor)
			copyToColorProcessor(sources, (ColorProcessor)ip);
		else
			throw new IllegalArgumentException("unknown processor type " + ip.getClass().getSimpleName());
		return ip;
	}
	
	public static void copyToByteProcessor(float[][] sources, ByteProcessor ip) {
		byte[] pixels = (byte[]) ip.getPixels();
		float[] P = sources[0];
		for (int i = 0; i < pixels.length; i++) {
			int val = clampByte(Math.round(P[i]));
			pixels[i] = (byte) val;
		}
	}
	
	public static void copyToShortProcessor(float[][] sources, ShortProcessor ip) {
		short[] pixels = (short[]) ip.getPixels();
		float[] P = sources[0];
		for (int i = 0; i < pixels.length; i++) {
			int val = clampShort(Math.round(P[i]));
			pixels[i] = (short) val;
		}
	}
	
	public static void copyToFloatProcessor(float[][] sources, FloatProcessor ip) {
		float[] pixels = (float[]) ip.getPixels();
		float[] P = sources[0];
		System.arraycopy(P, 0, pixels, 0, P.length);
	}
	
	public static void copyToColorProcessor(float[][] sources, ColorProcessor ip) {
		int[] pixels = (int[]) ip.getPixels();
		float[] R = sources[0];
		float[] G = sources[1];
		float[] B = sources[2];
		for (int i = 0; i < pixels.length; i++) {
			int r = clampByte(Math.round(R[i]));
			int g = clampByte(Math.round(G[i]));
			int b = clampByte(Math.round(B[i]));
			pixels[i] = Rgb.rgbToInt(r, g, b);
		}
	}
	
	private static int clampByte(int val) {
		if (val < 0) return 0;
		if (val > 255) return 255;
		return val;
	}
	
	private static int clampShort(int val) {
		if (val < 0) return 0;
		if (val > 65535) return 65535;
		return val;
	}

}
