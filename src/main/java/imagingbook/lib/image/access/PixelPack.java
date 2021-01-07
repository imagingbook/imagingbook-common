package imagingbook.lib.image.access;

import java.util.Arrays;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.lib.color.Rgb;


public class PixelPack {
	
	public static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.NEAREST_BORDER;

	private ImageProcessor ip = null;
	private final int depth;
	private final float[][] pixels;
	private final int length;
//	private OutOfBoundsStrategy obs;
	private final GridIndexer2D indexer;
	
	// --------------------------------------------------------------------
	public PixelPack(int width, int height, int depth, OutOfBoundsStrategy obs) {
//		this.width = width;
//		this.height = height;
		this.depth = depth;
		this.length = width * height;
		this.pixels = new float[depth][width * height];
		this.indexer = GridIndexer2D.create(width, height, obs);
	}
	
	public PixelPack(ImageProcessor ip) {
		this(ip, DefaultOutOfBoundsStrategy);
	}
			
	public PixelPack(ImageProcessor ip, OutOfBoundsStrategy obs) {
		this(ip.getWidth(), ip.getHeight(), getDepth(ip), obs);
		copyFromImageProcessor(ip, this.pixels);
		this.ip = ip;
	}
	
	public PixelPack(PixelPack orig) {
		this(orig, false);
	}
		
	public PixelPack(PixelPack orig, boolean copyData) {
		this(orig.getWidth(), orig.getHeight(), orig.getDepth(), orig.indexer.obs);
		if (copyData) {
			orig.copyTo(this);
		}
	}
	
	// --------------------------------------------------------------------

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
	
//	public void setPixel(int u, int v, float[] vals) {
//		final int i = indexer.getIndex(u, v);
//		if (i >= 0) {
//			for (int k = 0; k < depth; k++) {
//				pixels[k][i] = vals[k];
//			}
//		}
//	}
	
	public void setPixel(int u, int v, float ... vals) {
		final int i = indexer.getIndex(u, v);
		if (i >= 0) {
			for (int k = 0; k < depth && k < vals.length; k++) {
				pixels[k][i] = vals[k];
			}
		}
	}
	
	public PixelPack getEmptyCopy() {
		return new PixelPack(this);
	}
	
	public void copyTo(PixelPack other) {
		for (int k = 0; k < this.depth; k++) {
			System.arraycopy(this.pixels[k], 0, other.pixels[k], 0, this.length);
		}
	}
	
	public PixelSlice getSlice(int k) {
		if (k < 0 || k >= depth) {
			throw new IllegalArgumentException("illegal slice number " + k);
		}
		return new PixelSlice(k);
	}
	
	public PixelSlice getEmptySlice() {
		return new PixelSlice();
	}
	
	public float[][] getPixels() {
		return pixels;
	}
	
	public int getWidth() {
		return this.indexer.width;
	}
	
	public int getHeight() {
		return this.indexer.height;
	}
	
	public int getDepth() {
		return this.depth;
	}
	
	public ImageProcessor getIp() {
		return this.ip;
	}
	
	public OutOfBoundsStrategy getOutOfBoundsStrategy() {
		return this.indexer.obs;
	}
	
	// returns a new pixel array
	public float[] getPixel(int u, int v) {
		return getPixel(u, v, new float[depth]);
	}
	
	public void zero() {
		for (int k = 0; k < depth; k++) {
			getSlice(k).zero();
		}
	}
	
	// returns nh[x][y][k]
	public float[][][] get3x3Neighborhood(int uc, int vc, float[][][] nh) {
		if (nh == null) 
			nh = new float[3][3][depth];
		for (int i = 0; i < 3; i++) {
			int u = uc - 1 + i;
			for (int j = 0; j < 3; j++) {
				int v = vc - 1 + j;
				nh[i][j] = getPixel(u, v);
			}
		}
		return nh;
	}
	
	public void copyToImageProcessor(ImageProcessor ip) {
		// TODO: check compatibility
		copyToImageProcessor(this.pixels, ip);
	}
	
	// write contents back to source ImageProcessor (if exists)
	@Deprecated
	public void updateImageProcessor() {
		if (this.ip != null) {
			copyToImageProcessor(this.pixels, this.ip);
		}
	}

	// -------------------------------------------------------------------
	
	public class PixelSlice {
		private final int idx;
		private final float[] vals;
		
		PixelSlice(int idx) {
			this.idx = idx;
			this.vals = pixels[idx];
		}
		
		PixelSlice() {
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
		
		public int getIndex() {
			return idx;
		}
		
		public float[] getPixels() {
			return vals;
		}
		
		public int getLength() {
			return vals.length;
		}
		
		public int getWidth() {
			return PixelPack.this.getWidth();
		}
		
		public int getHeight() {
			return PixelPack.this.getHeight();
		}
		
		public void zero() {
			Arrays.fill(this.vals, 0);
		}
		
		public void copyTo(PixelSlice other) {
			System.arraycopy(this.vals, 0, other.vals, 0, this.vals.length);
		}
		
		// returns nh[x][y]
		public float[][] get3x3Neighborhood(int uc, int vc, float[][] nh) {
			if (nh == null) 
				nh = new float[3][3];
			for (int i = 0; i < 3; i++) {
				int u = uc - 1 + i;
				for (int j = 0; j < 3; j++) {
					int v = vc - 1 + j;
					nh[i][j] = getVal(u, v);
				}
			}
			return nh;
		}
	}
	
	// -------------------------------------------------------------------
	
	public static void copyFromImageProcessor(ImageProcessor ip, float[][] P) {
		if (ip instanceof ByteProcessor)
			copyFromByteProcessor((ByteProcessor)ip, P);
		else if (ip instanceof ShortProcessor)
			copyFromShortProcessor((ShortProcessor)ip, P);
		else if (ip instanceof FloatProcessor)
			copyFromFloatProcessor((FloatProcessor)ip, P);
		else if (ip instanceof ColorProcessor)
			copyFromColorProcessor((ColorProcessor)ip, P);
		else 
			throw new IllegalArgumentException("unknown processor type " + ip.getClass().getSimpleName());
	}
	
	public static void copyFromByteProcessor(ByteProcessor ip, float[][] P) {
		byte[] pixels = (byte[]) ip.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			P[0][i] = 0xff & pixels[i];
		}
	}
	
	public static void copyFromShortProcessor(ShortProcessor ip, float[][] P) {
		short[] pixels = (short[]) ip.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			P[0][i] = 0xffff & pixels[i];
		}
	}
	
	public static void copyFromFloatProcessor(FloatProcessor ip, float[][] P) {
		float[] pixels = (float[]) ip.getPixels();
		System.arraycopy(pixels, 0, P[0], 0, pixels.length);
	}
	
	public static void copyFromColorProcessor(ColorProcessor ip, float[][] P) {
		final int[] pixels = (int[]) ip.getPixels();
		final int[] rgb = new int[3];
		for (int i = 0; i < pixels.length; i++) {
			Rgb.intToRgb(pixels[i], rgb);
			P[0][i] = rgb[0];
			P[1][i] = rgb[1];
			P[2][i] = rgb[2];
		}
	}
	
	// -------------------------------------------------------------------
	
	
	public static PixelPack pack(ImageProcessor ip) {
		return pack(ip, DefaultOutOfBoundsStrategy);
	}
			
	public static PixelPack pack(ImageProcessor ip, OutOfBoundsStrategy obs) {
		PixelPack pack = new PixelPack(ip.getWidth(), ip.getHeight(), getDepth(ip), obs);
		pack.ip = ip;
		copyFromImageProcessor(ip, pack.pixels);
		return pack;
	}
	
	
	// -------------------------------------------------------------------
	
//	public static float[][] fromImageProcessor(ImageProcessor ip) {
//		if (ip instanceof ByteProcessor)
//			return fromByteProcessor((ByteProcessor)ip);
//		if (ip instanceof ShortProcessor)
//			return fromShortProcessor((ShortProcessor)ip);
//		if (ip instanceof FloatProcessor)
//			return fromFloatProcessor((FloatProcessor)ip);
//		if (ip instanceof ColorProcessor)
//			return fromColorProcessor((ColorProcessor)ip);
//		throw new IllegalArgumentException("unknown processor type " + ip.getClass().getSimpleName());
//	}
//	
//	public static float[][] fromByteProcessor(ByteProcessor ip) {
//		byte[] pixels = (byte[]) ip.getPixels();
//		float[] P = new float[pixels.length];
//		for (int i = 0; i < pixels.length; i++) {
//			P[i] = 0xff & pixels[i];
//		}
//		return new float[][] {P};
//	}
//	
//	public static float[][] fromShortProcessor(ShortProcessor ip) {
//		short[] pixels = (short[]) ip.getPixels();
//		float[] P = new float[pixels.length];
//		for (int i = 0; i < pixels.length; i++) {
//			P[i] = 0xffff & pixels[i];
//		}
//		return new float[][] {P};
//	}
//	
//	public static float[][] fromFloatProcessor(FloatProcessor ip) {
//		float[] pixels = (float[]) ip.getPixels();
//		float[] P = pixels.clone();
//		return new float[][] {P};
//	}
//	
//	public static float[][] fromColorProcessor(ColorProcessor ip) {
//		int[] pixels = (int[]) ip.getPixels();
//		float[] R = new float[pixels.length];
//		float[] G = new float[pixels.length];
//		float[] B = new float[pixels.length];
//		int[] RGB = new int[3];
//		for (int i = 0; i < pixels.length; i++) {
//			Rgb.intToRgb(pixels[i], RGB);
//			R[i] = RGB[0];
//			G[i] = RGB[1];
//			B[i] = RGB[2];
//		}
//		return new float[][] {R, G, B};
//	}
	
	public static int getDepth(ImageProcessor ip) {
		return (ip instanceof ColorProcessor) ? 3 : 1;
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
