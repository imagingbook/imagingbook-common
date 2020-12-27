/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
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
 * This abstract class provides unified image access to all 4 types of images available in ImageJ:
 * 8-bit, 16-bit, float, and color images.
 * It is used as a wrapper around an instance of ImageJ's {@link ImageProcessor} type.
 * <br>
 * A generic {@link ImageAccessor} is created, e.g., by {@link #create(ImageProcessor)}, which
 * returns an instance of {@link Byte}, {@link Short}, {@link Float} or
 * {@link Rgb}.
 * {@link ImageAccessor} itself can access any ImageJ image using 
 * the methods {@link #getPix(int, int)}, {@link #getPix(double, double)}
 * for retrieving pixel values and {@link #setPix(int, int, float[])}
 * to modify pixel values.
 * All pixel values are of type {@code float[]}, either containing a single element (for
 * scalar-valued images) or three elements (for color images).
 * <br>
 * In addition, the accessors for scalar-valued images ({@link Byte}, {@link Short},
 * {@link Float}) provide the methods
 * {@link Scalar#getVal(int, int)}, {@link Scalar#getVal(double, double)} and 
 * {@link Scalar#setVal(int, int, float)}
 * to read and write scalar-valued pixels passed as single {@code float} values.
 * <br>
 * The methods {@link #getPix(double, double)} and {@link Scalar#getVal(double, double)} perform interpolation at non-integer coordinates
 * using the specified {@link InterpolationMethod}.
 * <pre>
 * 
 * </pre>
 * <br>
 * See {@link imagingbook.lib.filters.GenericFilter} 
 * and {@code Pixel_Interpolation.Interpolator_Demo} (in {@code imagingbook-plugins-all}) for a concrete usage examples.
 * 
 * @author W. Burger
 * @version 2020/12/27
 */
public abstract class ImageAccessor {
	
	static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.DefaultValue;
	static final InterpolationMethod DefaultInterpolationMethod = InterpolationMethod.Bilinear;
		
	protected final int width;
	protected final int height;
	protected final PixelIndexer indexer;		// implements the specified OutOfBoundsStrategy
	protected final OutOfBoundsStrategy outOfBoundsStrategy;
	protected final InterpolationMethod interpolationMethod;
	
	/**
	 * Creates a new {@code ImageAccessor} instance for the given image,
	 * using the default out-of-bounds strategy and interpolation method.
	 * The conrete type of the returned instance depends on the specified image
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
	 * The conrete type of the returned instance depends on the specified image
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
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Returns the {@link ImageProcessor} associated with this
	 * {@link ImageAccessor}.
	 * @return the image processor
	 */
	public abstract ImageProcessor getProcessor();
	
	/**
	 * Returns the {@link OutOfBoundsStrategy} specified for this
	 * {@link ImageAccessor}.
	 * @return the out-of-bounds strategy
	 */
	public OutOfBoundsStrategy getOutOfBoundsStrategy() {
		return outOfBoundsStrategy;
	}

	/**
	 * Returns the {@link InterpolationMethod} specified for this
	 * {@link ImageAccessor}.
	 * @return the interpolation method
	 */
	public InterpolationMethod getInterpolationMethod() {
		return interpolationMethod;
	}
	
	/**
	 * Returns pixel value at the specified integer position as a
	 * {@code float[]} with either 1 element for scalar-valued images
	 * or 3 elements for RGB images.
	 * @param u the x-coordinate
	 * @param v the y-coordinate
	 * @return the pixel value ({@code float[]})
	 */
	public abstract float[] getPix(int u, int v);
	
	/**
	 * Returns pixel value at the specified floating-point position as a
	 * {@code float[]} with either 1 for scalar-valued images
	 * and or 3 elements for RGB images.
	 * Interpolation is used non-integer coordinates.
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the pixel value ({@code float[]})
	 */
	public abstract float[] getPix(double x, double y);		// returns interpolated pixel value at real position (x, y)
	
	/**
	 * Sets the pixel value at the specified integer position.
	 * The new value must be provided as {@code float[]} with 
	 * 1 element for scalar-valued images or 3 elements for RGB images.
	 * @param u the x-coordinate
	 * @param v the y-coordinate
	 * @param val the new pixel value ({@code float[]})
	 */
	public abstract void setPix(int u, int v, float[] val);
	
	// ------------------------------------------------------------
	// ------------------------------------------------------------
	
	/**
	 * The common (abstract) super-class for all image accessors to scalar-valued images.
	 * It inherits all methods from {@link ImageAccessor} but
	 * adds the methods {@link #getVal(int, int)}, {@link #getVal(double, double)}
	 * and {@link #setVal(int, int, float)} for reading and writing scalar-valued 
	 * pixel data.
	 */
	public static abstract class Scalar extends ImageAccessor {
		
		/**
		 * Creates a new image accessor of general type {@link ImageAccessor.Scalar}.
		 * The conrete type of the returned instance depends on the specified image, i.e.,
		 * {@link Byte} for {@link ByteProcessor},
		 * {@link Short} for {@link ShortProcessor},
		 * {@link Float} for {@link FloatProcessor}.
		 * 
		 * @param ip the image to be accessed
		 * @param obs the out-of-bounds strategy to be used (use {@code null} for default settings)
		 * @param ipm the interpolation method to be used (use {@code null} for default settings)
		 * @return a new image accessor
		 */
		public static ImageAccessor.Scalar create(ImageProcessor ip,  OutOfBoundsStrategy obs, InterpolationMethod ipm) {
			if (ip instanceof ByteProcessor)
				return new ImageAccessor.Byte((ByteProcessor) ip, obs, ipm);
			if (ip instanceof ShortProcessor)
				return new ImageAccessor.Short((ShortProcessor) ip, obs, ipm);
			if (ip instanceof FloatProcessor)
				return new ImageAccessor.Float((FloatProcessor) ip, obs, ipm);
			throw new IllegalArgumentException("cannot create " + 
				ImageAccessor.Scalar.class.getSimpleName() + " for " +
				ip.getClass().getSimpleName());
		}
		
		protected final ImageProcessor ip;
		protected final float pixelDefaultValue = 0.0f;
		
		/**
		 * Reads and returns the scalar pixel value for the given image position.
		 * The value returned for coordinates outside the image boundaries depends
		 * on the {@link OutOfBoundsStrategy} specified for this {@link ImageAccessor}.
		 * @param u the x-coordinate
		 * @param v the y-coordinate
		 * @return the pixel value ({@code float})
		 */
		public abstract float getVal(int u, int v);				// returns pixel value at integer position (u, v)
		
		/**
		 * Reads and returns the interpolated scalar pixel value for the given image position.
		 * The value returned for coordinates outside the image boundaries depends
		 * on the {@link OutOfBoundsStrategy} specified for this {@link ImageAccessor}.
		 * 
		 * @param x the x-coordinate
		 * @param y the y-coordinate
		 * @return the pixel value ({@code float})
		 */
		public float getVal(double x, double y) {	// interpolating version
			return interpolator.getInterpolatedValue(this, x, y);
		}
		
		/**
		 * Writes a scalar pixel value to the given image position.
		 * What happens for coordinates outside the image boundaries depends
		 * on the {@link OutOfBoundsStrategy} specified for this {@link ImageAccessor}.
		 * @param u the x-coordinate
		 * @param v the y-coordinate
		 * @param val the new pixel value ({@code float})
		 */
		public abstract void setVal(int u, int v, float val);
		
		protected final PixelInterpolator interpolator;	// performs interpolation
			
		private Scalar(ImageProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
			super(ip, obs, ipm);
			this.ip = ip;
			this.interpolator = PixelInterpolator.create(interpolationMethod);
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
	
	/**
	 * Image accessor for scalar images with 8-bit (byte) values.
	 */
	public static class Byte extends ImageAccessor.Scalar {
		private final ByteProcessor ip;
		private final byte[] pixels;
		
		/**
		 * Constructor. Creates a new image accessor of type {@link Byte}.
		 * See also the generic factory method 
		 * {@link Scalar#create(ImageProcessor, OutOfBoundsStrategy, InterpolationMethod)}.
		 * 
		 * @param ip an instance of {@link ByteProcessor}
		 * @param obs the out-of-bounds strategy to be used (use {@code null} for default settings)
		 * @param ipm the interpolation method to be used (use {@code null} for default settings)
		 */
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
		public void setVal(int u, int v, float val) {
			int vali = Math.round(val);
			if (vali < 0) vali = 0;
			if (vali > 255) vali = 255;
			if (u >= 0 && u < width && v >= 0 && v < height) {
				pixels[width * v + u] = (byte) (0xFF & vali);
			}
		}
	}
	
	// ------------------------------------------------------------
	
	/**
	 * Image accessor for scalar images with 16-bit (short) values.
	 */
	public static class Short extends ImageAccessor.Scalar {
		private final ShortProcessor ip;
		private final short[] pixels;
		
		/**
		 * Constructor. Creates a new image accessor of type {@link Short}.
		 * See also the generic factory method 
		 * {@link Scalar#create(ImageProcessor, OutOfBoundsStrategy, InterpolationMethod)}.
		 * 
		 * @param ip an instance of {@link ShortProcessor}
		 * @param obs the out-of-bounds strategy to be used (use {@code null} for default settings)
		 * @param ipm the interpolation method to be used (use {@code null} for default settings)
		 */
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
		public void setVal(int u, int v, float val) {
			int vali = Math.round(val);
			if (vali < 0) vali = 0;
            if (vali > 65535) vali = 65535;
			if (u >= 0 && u < width && v >= 0 && v < height) {
				pixels[width * v + u] = (short) (0xFFFF & vali);
			}
		}
	}
	
	// ------------------------------------------------------------
	
	/**
	 * Image accessor for scalar images with 32-bit (float) values.
	 */
	public static class Float extends ImageAccessor.Scalar {
		private final FloatProcessor ip;
		private final float[] pixels;
		
		/**
		 * Constructor. Creates a new image accessor of type {@link Float}.
		 * See also the generic factory method 
		 * {@link Scalar#create(ImageProcessor, OutOfBoundsStrategy, InterpolationMethod)}.
		 * 
		 * @param ip an instance of {@link FloatProcessor}
		 * @param obs the out-of-bounds strategy to be used (use {@code null} for default settings)
		 * @param ipm the interpolation method to be used (use {@code null} for default settings)
		 */
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
	
	/**
	 * Image accessor for vector-valued RGB images.
	 */
	public static class Rgb extends ImageAccessor {
		private final ColorProcessor ip;
		private final int[] pixels;
		private final float[] pixelDefaultValue = { 0, 0, 0 };
		private final int[] rgb = new int[3];
		
		private final ImageAccessor.Byte rAcc, gAcc, bAcc;

		
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
	
	// -------------------------------------------------------------------------
	
	public static void main(String[] args) {
		int width = 300;
		int height = 200;
		
		ImageProcessor ip = new ByteProcessor(width, height);
		ImageAccessor.Scalar ia = ImageAccessor.Scalar.create(ip, null, null);
		
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				float val = ia.getVal(u, v);
				ia.setVal(u, v, val  + 1);
			}
		}
		
	}
	
}
