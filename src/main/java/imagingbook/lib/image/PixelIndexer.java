/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/

package imagingbook.lib.image;


/*
 * This class implements different schemes for accessing pixel values from
 * a 2D image that is contained in a 1D array.
 * 
 */
public abstract class PixelIndexer {
	
	public static PixelIndexer create(int width, int height, OutOfBoundsStrategy mode) {
		switch (mode) {
		case DefaultValue 	: return new PixelIndexer.DefaultValueIndexer(width, height);
		case NearestBorder	: return new PixelIndexer.NearestBorderIndexer(width, height);
		case MirrorImage	: return new PixelIndexer.MirrorImageIndexer(width, height);
		case Exception		: return new PixelIndexer.ExceptionIndexer(width, height);
		}
		return null;
	}
	
	protected final int width;
	protected final int height;

	private PixelIndexer(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public abstract int getIndex(int u, int v);

	/* 
	 * This indexer returns out of bounds pixel values that
	 * are taken from the closest border pixel. This is the
	 * most common method.
	 */
	public static class NearestBorderIndexer extends PixelIndexer {
		NearestBorderIndexer(int width, int height) {
			super(width, height);
		}

		public int getIndex(int u, int v) {
			if (u < 0)
				u = 0;
			else if (u >= width)
				u = width - 1;
			if (v < 0)
				v = 0;
			else if (v >= height)
				v = height - 1;
			return width * v + u;
		}
	}
	
	/* 
	 * This index returns out of bound pixels taken from
	 * the mirrored image.
	 */
	public static class MirrorImageIndexer extends PixelIndexer {
		MirrorImageIndexer(int width, int height) {
			super(width, height);
		}

		public int getIndex(int u, int v) {
			// this is a fast modulo operation for positive divisors only
			u = u % width;
			if (u < 0) u = u + width; 
			v = v % height;
			if (v < 0) v = v + height; 
			return width * v + u;
		}
	}
	
	/* 
	 * This indexer returns -1 for out of bounds pixels to
	 * indicate that a (predefined) default value should be used.
	 */
	public static class DefaultValueIndexer extends PixelIndexer {
		DefaultValueIndexer(int width, int height) {
			super(width, height);
		}

		public int getIndex(int u, int v) {
			if (u < 0 || u >= width || v < 0 || v >= height)
				return -1;
			else 
				return width * v + u;
		}
	}
	
	/*
	 * This indexer throws an exception if out of bounds pixels
	 * are accessed.
	 */
	public static class ExceptionIndexer extends PixelIndexer {
		ExceptionIndexer(int width, int height) {
			super(width, height);
		}

		public int getIndex(int u, int v) {
			if (u < 0 || u >= width || v < 0 || v >= height) {
				throw new ArrayIndexOutOfBoundsException();
			}
			else 
				return width * v + u;
		}
	}

}


