/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings;

import ij.process.ImageProcessor;
import imagingbook.lib.image.ImageAccessor;
import imagingbook.lib.interpolation.InterpolationMethod;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * 2013-02-02: changed to use the returned new point of applyTo(Point2D),
 * not relying on side effect.
 * 2014-04-19: modified to work with ImageAccessor.
 * 2015-12-10: added isInverse() method.
 */
public abstract class Mapping implements Cloneable {
	
	protected boolean isInverseFlag = false;
	
	// all subclasses must implement this method
	public abstract double[] applyTo(double[] pnt);

	
	public boolean isInverse() {
		return isInverseFlag;
	}

	public Mapping getInverse() {
		if (isInverseFlag)
			return this;
		else {
			return this.invert(); // only linear mappings invert
		}
	}
	
	protected Mapping invert() {
		throw new UnsupportedOperationException("mapping cannot be inverted");
	}
	
	public Point2D applyTo(Point2D pnt) {
		double[] xy = applyTo(new double[] {pnt.getX(), pnt.getY()});
		return new Point2D.Double(xy[0], xy[1]);
	}
	
	/**
	 * Applies this mapping to all points in the pnts array.
	 * @param pnts array of original points.
	 * @return an array of modified points.
	 */
	public Point2D[] applyTo(Point2D[] pnts) {
		Point2D[] outPnts = new Point2D[pnts.length];
		for (int i = 0; i < pnts.length; i++) {
			outPnts[i] = applyTo(pnts[i]);
		}
		return outPnts;
	}

	/**
	 * Destructively transforms the image in "ip" using this geometric
	 * mapping and the specified pixel interpolation method.
	 * TODO: this should not be here (geometry only)?
	 * 
	 * @param ip target image to which THIS mapping is applied.
	 * @param im interpolation method.
	 */
	public void applyTo(ImageProcessor ip, InterpolationMethod im) {
		// make a temporary copy of the image:
		ImageProcessor source = ip.duplicate();
		ImageProcessor target = ip;
		applyTo(source, target, im);
		source = null;
	}

	/**
	 * Transforms the "source" image to the "target" image using this geometric
	 * mapping and the specified pixel interpolation method. Source and target
	 * must be different images!
	 * 
	 * @param source input image (not modified).
	 * @param target output image (modified).
	 * @param im interpolation method.
	 */
	public void applyTo(ImageProcessor source, ImageProcessor target, InterpolationMethod im) {
		if (target == source) {
			throw new IllegalArgumentException("source and target image must not be the same");
		}
		ImageAccessor sourceAcc = ImageAccessor.create(source, null, im);
		ImageAccessor targetAcc = ImageAccessor.create(target);
		applyTo(sourceAcc, targetAcc);
	}


	/**
	 * Transforms the source image (contained in "srcInterpol") to the "target"
	 * image using this geometric mapping and the specified pixel interpolator.
	 * 
	 * @param sourceAcc accessor to the source image
	 * @param targetAcc accessor to the target image
	 */
	public void applyTo(ImageAccessor sourceAcc, ImageAccessor targetAcc) {
		Mapping invMap = this.getInverse(); // get inverse mapping
		ImageProcessor target = targetAcc.getProcessor();
		final int w = target.getWidth();
		final int h = target.getHeight();
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				Point2D sourcePt = invMap.applyTo(new Point(u, v));
				float[] val = sourceAcc.getPix(sourcePt.getX(),sourcePt.getY());
				targetAcc.setPix(u, v, val);
			}
		}
	}

	public Mapping duplicate() { // duplicates any mapping, overwrite
		return this.clone();
	}
	
	protected Mapping clone() {
		Mapping copy = null;
		try {
			copy = (Mapping) super.clone();
		} 
		catch (CloneNotSupportedException e) { }
		return copy;
	}

}
