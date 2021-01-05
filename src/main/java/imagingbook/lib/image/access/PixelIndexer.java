/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.image.access;

/**
 * Instances of this class perform the transformation between 2D image coordinates 
 * and indexes into the associated 1D pixel array and vice versa.
 * 
 * The key method {@link #getIndex(int, int)} returns the 1D array index
 * for a pair of image coordinates.
 * It throws an exception when trying to access out-of-image coordinates.
 * 
 * The subclasses {@link ZeroValueIndexer}, {@link MirrorImageIndexer} and
 * {@link NearestBorderIndexer} implement different behaviors for accessing
 * out-of-image coordinates.
 * 
 */
public class PixelIndexer implements Cloneable {
	
	public static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.NEAREST_BORDER;
	
	public static PixelIndexer create(int width, int height, OutOfBoundsStrategy obs) {
		obs = (obs != null) ? obs : DefaultOutOfBoundsStrategy;
		switch (obs) {
		case ZERO_VALUE 			: return new ZeroValueIndexer(width, height);
		case NEAREST_BORDER			: return new NearestBorderIndexer(width, height);
		case MIRROR_IMAGE			: return new MirrorImageIndexer(width, height);
		case THROW_EXCEPTION		: return new PixelIndexer(width, height);
		}
		return null;
	}
	
	protected final int width;
	protected final int height;

	private PixelIndexer(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Returns the 1D array index for a given pair of image coordinates.
	 * Throws an exception when applied to out-of-image coordinates.
	 * Subclasses override this method.
	 * 
	 * @param u x-coordinate
	 * @param v y-coordinate
	 * @return 1D array index
	 */
	public int getIndex(int u, int v) throws OutOfImageException {
		if (u < 0 || u >= width || v < 0 || v >= height) {
			throw new OutOfImageException(
					String.format("out-of-image position [%d,%d]", u, v));
		}
		else 
			return this.insideBoundsIndex(u, v);
	}
	
	private int insideBoundsIndex(int u, int v) {
		return width * v + u;
	}
	
	// ---------------------------------------------------------

	/** 
	 * This indexer returns out-of-bounds pixels that
	 * are taken from the closest border pixel. This is the
	 * most common method.
	 */
	public static class NearestBorderIndexer extends PixelIndexer {
		
		NearestBorderIndexer(int width, int height) {
			super(width, height);
		}

		@Override
		public int getIndex(int u, int v) {
			if (u < 0) {
				u = 0;
			}
			else if (u >= width) {
				u = width - 1;
			}
			if (v < 0) {
				v = 0;
			}
			else if (v >= height) {
				v = height - 1;
			}
			return super.insideBoundsIndex(u, v);
		}
	}
	
	/** 
	 * This indexer returns out-of-bound pixels taken from
	 * the mirrored image.
	 */
	public static class MirrorImageIndexer extends PixelIndexer {
		
		MirrorImageIndexer(int width, int height) {
			super(width, height);
		}

		@Override
		public int getIndex(int u, int v) {
			// fast modulo operation for positive divisors only
			u = u % width;
			if (u < 0) {
				u = u + width; 
			}
			v = v % height;
			if (v < 0) {
				v = v + height; 
			}
			return super.insideBoundsIndex(u, v);
		}
	}
	
	/** 
	 * This indexer returns -1 for out-of-bounds coordinates to
	 * indicate that a (predefined) default value should be used.
	 */
	public static class ZeroValueIndexer extends PixelIndexer {
		
		ZeroValueIndexer(int width, int height) {
			super(width, height);
		}

		@Override
		public int getIndex(int u, int v) {
			if (u < 0 || u >= width || v < 0 || v >= height) {
				return -1;
			}
			else {
				return super.insideBoundsIndex(u, v);
			}
		}
	}
	
//	/**
//	 * This indexer throws an exception if out of bounds pixels
//	 * are accessed.
//	 */
//	public static class ExceptionIndexer extends PixelIndexer {
//		
//		ExceptionIndexer(int width, int height) {
//			super(width, height);
//		}
//
//		@Override
//		public int getIndex(int u, int v) {
//			if (u < 0 || u >= width || v < 0 || v >= height) {
//				throw new ArrayIndexOutOfBoundsException();
//			}
//			else 
//				return super.insideBoundsIndex(u, v);
//		}
//	}
	
	// -----------------------------------------------------------
	
	public static class OutOfImageException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public OutOfImageException(String message) {
			super(message);
		}
	}

}


