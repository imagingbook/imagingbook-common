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
public abstract class GridIndexer2D implements Cloneable {
	
	public static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.NEAREST_BORDER;
	
	public static GridIndexer2D create(int width, int height, OutOfBoundsStrategy obs) {
		obs = (obs != null) ? obs : DefaultOutOfBoundsStrategy;
		switch (obs) {
		case ZERO_VALUE 		: return new ZeroValueIndexer(width, height);
		case NEAREST_BORDER		: return new NearestBorderIndexer(width, height);
		case MIRROR_IMAGE		: return new MirrorImageIndexer(width, height);
		case THROW_EXCEPTION	: return new ExceptionIndexer(width, height);
		}
		return null;
	}
	
	protected final int width;
	protected final int height;
	protected final OutOfBoundsStrategy obs;

	private GridIndexer2D(int width, int height, OutOfBoundsStrategy obs) {
		this.width = width;
		this.height = height;
		this.obs = obs;
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
	public abstract int getIndex(int u, int v);
	
	private int getWithinBoundsIndex(int u, int v) {
		return width * v + u;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public OutOfBoundsStrategy getOutOfBoundsStrategy() {
		return this.obs;
	}
	
	// ---------------------------------------------------------

	/** 
	 * This indexer returns out-of-bounds pixels that
	 * are taken from the closest border pixel. This is the
	 * most common method.
	 */
	public static class NearestBorderIndexer extends GridIndexer2D {
		
		NearestBorderIndexer(int width, int height) {
			super(width, height, OutOfBoundsStrategy.NEAREST_BORDER);
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
			return super.getWithinBoundsIndex(u, v);
		}
	}
	
	/** 
	 * This indexer returns out-of-bound pixels taken from
	 * the mirrored image.
	 */
	public static class MirrorImageIndexer extends GridIndexer2D {
		
		MirrorImageIndexer(int width, int height) {
			super(width, height, OutOfBoundsStrategy.MIRROR_IMAGE);
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
			return super.getWithinBoundsIndex(u, v);
		}
	}
	
	/** 
	 * This indexer returns -1 for out-of-bounds coordinates to
	 * indicate that a (predefined) default value should be used.
	 */
	public static class ZeroValueIndexer extends GridIndexer2D {
		
		ZeroValueIndexer(int width, int height) {
			super(width, height, OutOfBoundsStrategy.ZERO_VALUE);
		}

		@Override
		public int getIndex(int u, int v) {
			if (u < 0 || u >= width || v < 0 || v >= height) {
				return -1;
			}
			else {
				return super.getWithinBoundsIndex(u, v);
			}
		}
	}
	
	/**
	 * This indexer throws an exception if out of bounds pixels
	 * are accessed.
	 */
	public static class ExceptionIndexer extends GridIndexer2D {
		
		ExceptionIndexer(int width, int height) {
			super(width, height, OutOfBoundsStrategy.THROW_EXCEPTION);
		}

		@Override
		public int getIndex(int u, int v) throws OutOfImageException {
			if (u < 0 || u >= width || v < 0 || v >= height) {
				throw new OutOfImageException(
						String.format("out-of-image position [%d,%d]", u, v));
			}
			else 
				return super.getWithinBoundsIndex(u, v);
		}
	}
	
	// -----------------------------------------------------------
	
	public static class OutOfImageException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public OutOfImageException(String message) {
			super(message);
		}
	}

}


