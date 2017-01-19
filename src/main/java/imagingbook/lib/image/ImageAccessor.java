/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.image;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.lib.interpolation.InterpolationMethod;
import imagingbook.lib.interpolation.PixelInterpolator;


/**
 * This class provides unified image access to all 4 types of images available in ImageJ.
 * Byte, Short, Float: get/set values are passed as type float using getVal() and setVal().
 * Byte, Short, Float, Rgb: uses a float[] to pass in values using getPix() and setPix().
 * 
 * getVal() and getPix() interpolate for non-integer coordinates.
 * 
 * @author W. Burger
 * @version 2015/12/20
 */
public abstract class ImageAccessor {
	
	static OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.DefaultValue;
	static InterpolationMethod DefaultInterpolationMethod = InterpolationMethod.Bilinear;
		
	protected final int width;
	protected final int height;
	protected final PixelIndexer indexer;				// implements the specified OutOfBoundsStrategy
	protected final OutOfBoundsStrategy outOfBoundsStrategy;
	protected final InterpolationMethod interpolationMethod;
	
	/**
	 * Creates a new {@code ImageAccessor} instance for the given image,
	 * using the default out-of-bounds strategy and interpolation method.
	 * 
	 * @param ip the source image
	 * @return a new {@code ImageAccessor} instance
	 */
	public static ImageAccessor create(ImageProcessor ip) {
		return create(ip, DefaultOutOfBoundsStrategy, DefaultInterpolationMethod);
	}
	
	/**
	 * Creates a new {@code ImageAccessor} instance for the given image,
	 * using the specified out-of-bounds strategy and interpolation method.
	 * 
	 * @param ip the source image
	 * @param obs the out-of-bounds strategy (use {@code null} for default settings)
	 * @param ipm the interpolation method (use {@code null} for default settings)
	 * @return a new {@code ImageAccessor} instance
	 */
	public static ImageAccessor create(ImageProcessor ip,  OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		if (ip instanceof ColorProcessor) {
			return ImageAccessor.Rgb.create(ip, obs, ipm);
		}
		else {
			return ImageAccessor.Scalar.create(ip, obs, ipm);
		}
	}
	
	// private constructor (used by all subtypes)
	private ImageAccessor(ImageProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		this.width  = ip.getWidth();
		this.height = ip.getHeight();
		this.outOfBoundsStrategy = (obs != null) ? obs : DefaultOutOfBoundsStrategy;
		this.interpolationMethod = (ipm != null) ? ipm : DefaultInterpolationMethod;
		this.indexer = PixelIndexer.create(width, height, outOfBoundsStrategy);
	}
	
	public abstract ImageProcessor getProcessor();
	
	public OutOfBoundsStrategy getOutOfBoundsStrategy() {
		return outOfBoundsStrategy;
	}

	public InterpolationMethod getInterpolationMethod() {
		return interpolationMethod;
	}
	
	// all ImageAccessor's can do this (Gray and Color, get/set complete pixels):
	public abstract float[] getPix(int u, int v);			// returns pixel value at integer position (u, v)
	public abstract float[] getPix(double x, double y);		// returns interpolated pixel value at real position (x, y)
	public abstract void setPix(int u, int v, float[] val);
	
	// ------------------------------------------------------------
	
	public static abstract class Scalar extends ImageAccessor {
		
		public static ImageAccessor.Scalar create(ImageProcessor ip,  OutOfBoundsStrategy obs, InterpolationMethod ipm) {
			if (ip instanceof ByteProcessor)
				return new ImageAccessor.Byte((ByteProcessor) ip, obs, ipm);
			if (ip instanceof ShortProcessor)
				return new ImageAccessor.Short((ShortProcessor) ip, obs, ipm);
			if (ip instanceof FloatProcessor)
				return new ImageAccessor.Float((FloatProcessor) ip, obs, ipm);
			throw new IllegalArgumentException("cannot create ImageAccessor.Gray for this processor");
		}
		
		protected final ImageProcessor ip;
		protected final float pixelDefaultValue = 0.0f;
		
		// only scalar accessors can do this (get/set scalar values):
		public abstract float getVal(int u, int v);				// returns pixel value at integer position (u, v)
		public abstract void setVal(int u, int v, float val);
		
		protected final PixelInterpolator interpolator;	// performs interpolation
			
		private Scalar(ImageProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
			super(ip, obs, ipm);
			this.ip = ip;
			this.interpolator = PixelInterpolator.create(interpolationMethod);
		}
		
		public float getVal(double x, double y) {	// interpolating version
			return interpolator.getInterpolatedValue(this, x, y);
		}
		
		@Override
		public float[] getPix(int u, int v) {
			return new float[] {this.getVal(u, v)};
		}
		
		@Override
		public float[] getPix(double x, double y) {
			return new float[] {this.getVal(x, y)};
		}
		
		@Override
		public void setPix(int u, int v, float[] pix) {
			this.setVal(u, v, pix[0]);
		}
	}
	
	// ------------------------------------------------------------
	
	public static class Byte extends ImageAccessor.Scalar {
		private final ByteProcessor ip;
		private final byte[] pixels;
		
		
		public Byte(ByteProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
			super(ip, obs, ipm);
			this.ip = ip;
			this.pixels = (byte[]) this.ip.getPixels();
		}
		
		@Override
		public ByteProcessor getProcessor() {
			return ip;
		}
		
		@Override
		public float getVal(int u, int v) {
			final int i = indexer.getIndex(u, v);
			if (i < 0) 
				return pixelDefaultValue;
			else {
				return (0xff & pixels[i]);
			}
		}
		
		@Override
		public void setVal(int u, int v, float valf) {
			int val = Math.round(valf);
			if (val < 0)
				val = 0;
			if (val > 255)
				val = 255;
			if (u >= 0 && u < width && v >= 0 && v < height) {
				pixels[width * v + u] = (byte) (0xFF & val);
			}
		}
	}
	
	// ------------------------------------------------------------
	
	public static class Short extends ImageAccessor.Scalar {
		private final ShortProcessor ip;
		private final short[] pixels;
		
		public Short(ShortProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
			super(ip, obs, ipm);
			this.ip = ip;
			this.pixels = (short[]) this.ip.getPixels();
		}
		
		@Override
		public ShortProcessor getProcessor() {
			return ip;
		}
		
		@Override
		public float getVal(int u, int v) {
			int i = indexer.getIndex(u, v);
			if (i < 0) 
				return pixelDefaultValue;
			else
				return (0xFFFF & pixels[i]);
		}
		
		@Override
		public void setVal(int u, int v, float valf) {
			int val = Math.round(valf);
			if (val < 0) val = 0;
            if (val > 65535) val = 65535;
			if (u >= 0 && u < width && v >= 0 && v < height) {
				pixels[width * v + u] = (short) (0xFFFF & val);
			}
		}
	}
	
	// ------------------------------------------------------------
	
	public static class Float extends ImageAccessor.Scalar {
		private final FloatProcessor ip;
		private final float[] pixels;
		
		public Float(FloatProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
			super(ip, obs, ipm);
			this.ip = ip;
			this.pixels = (float[]) ip.getPixels();
		}
		
		@Override
		public FloatProcessor getProcessor() {
			return ip;
		}
		
		@Override
		public float getVal(int u, int v) {
			int i = indexer.getIndex(u, v);
			if (i < 0) 
				return pixelDefaultValue;
			else
				return pixels[i];
		}
		
		@Override
		public void setVal(int u, int v, float val) {	
			if (u >= 0 && u < width && v >= 0 && v < height) {
				pixels[width * v + u] = val;
			}
		}
	}
	
	// ------------------------------------------------------------
	
	public static class Rgb extends ImageAccessor {
		private final ColorProcessor ip;
		private final int[] pixels;
		private final float[] pixelDefaultValue = { 0, 0, 0 };
		private final int[] rgb = new int[3];
		
		private final ImageAccessor.Byte rAcc, gAcc, bAcc;
		
		
		public static ImageAccessor.Rgb create(ImageProcessor ip,  OutOfBoundsStrategy obs, InterpolationMethod ipm) {
			if (ip instanceof ColorProcessor)
				return new ImageAccessor.Rgb((ColorProcessor) ip, obs, ipm);
			throw new IllegalArgumentException("cannot create ImageAccessor.Rgb for this processor");
		}
		
		public Rgb(ColorProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
			super(ip, obs, ipm);
			this.ip = ip;
			this.pixels = (int[]) this.ip.getPixels();
			
			int width  = ip.getWidth();
			int height = ip.getHeight();
			ByteProcessor rp = new ByteProcessor(width, height);
			ByteProcessor gp = new ByteProcessor(width, height);
			ByteProcessor bp = new ByteProcessor(width, height);
			byte[] rpix = (byte[]) rp.getPixels();
			byte[] gpix = (byte[]) gp.getPixels();
			byte[] bpix = (byte[]) bp.getPixels();
			ip.getRGB(rpix, gpix, bpix);	// fill byte arrays
			rAcc = new ImageAccessor.Byte(rp, obs, ipm);
			gAcc = new ImageAccessor.Byte(gp, obs, ipm);
			bAcc = new ImageAccessor.Byte(bp, obs, ipm);
		}
		
		@Override
		public ColorProcessor getProcessor() {
			return ip;
		}
		
		@Override
		public float[] getPix(int u, int v) {	// returns an RGB value packed into a float[]
			int i = indexer.getIndex(u, v);
			if (i < 0) {
				return pixelDefaultValue;
			}
			else {
				int c = pixels[i];
				int red = (c & 0xff0000) >> 16;
				int grn = (c & 0xff00) >> 8;
				int blu = (c & 0xff);
				return new float[] {red, grn, blu};
			}	
		}
	
		@Override
		public void setPix(int u, int v, float[] valf) {
			if (u >= 0 && u < width && v >= 0 && v < height) {
				if (valf.length == 3) {
					rgb[0] = clamp(Math.round(valf[0]));
					rgb[1] = clamp(Math.round(valf[1]));
					rgb[2] = clamp(Math.round(valf[2]));
				}
				else {
					rgb[0] = clamp(Math.round(valf[0]));
					rgb[1] = rgb[0];
					rgb[2] = rgb[0];
				}
				int val = ((rgb[0] & 0xff) << 16) | ((rgb[1] & 0xff) << 8) | rgb[2] & 0xff;
				pixels[width * v + u] = val;
			}
		}

		@Override
		public float[] getPix(double x, double y) {
//			return interpolator.getInterpolatedValue(new Point2D.Double(x, y));
			float red = rAcc.getVal(x, y);
			float grn = gAcc.getVal(x, y);
			float blu = bAcc.getVal(x, y);
			return new float[] { red, grn, blu };
		}

	}
	
	private static final int clamp(int val) {
		if (val < 0) return 0;
		if (val > 255) return 255;
		return val;
	}
	
}
