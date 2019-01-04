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
 * Abstract class which represents a general 2D transformation.
 * @version 2019/01/01
 */
public abstract class Mapping implements Cloneable {
	

	/** 
	 * Duplicates this mapping by using {@link Object}'s shallow clone method.
	 * Sub-classes are supposed to override this method.
	 * @return a copy of this mapping
	 */
	public Mapping duplicate() {
		Mapping copy = null;
		try {
			copy = (Mapping) super.clone();
		} 
		catch (CloneNotSupportedException e) {
			throw new UnsupportedOperationException("Cannot duplicate mapping " + this.toString());}
		return copy;
	}
	
	/**
	 * Applies this transformation to the given 2D point.
	 * All sub-classes of {@link Mapping} must implement this method.
	 * 
	 * @param x the x-coordinate of the point to be mapped
	 * @param y the y-coordinate of the point to be mapped
	 * @return the transformed 2D coordinate
	 */
	public abstract double[] applyTo (double x, double y);
	
	/**
	 * Applies this transformation to a 2D point given as
	 * a {@code double} array.
	 * 
	 * @param xy the point to be mapped
	 * @return the transformed 2D coordinate
	 */
	public double[] applyTo (double[] xy) {
		return applyTo(xy[0], xy[1]);
	}

	/**
	 * The inverse of this mapping is calculated (if possible)
	 * and returned.
	 * 
	 * @return the inverse mapping
	 */
	public Mapping getInverse() {
		throw new UnsupportedOperationException("Cannot invert mapping " + this.toString());
	}
	
	/**
	 * Applies this mapping to a single 2D point.
	 * 
	 * @param pnt the original point
	 * @return the transformed point
	 */
	public Point2D applyTo(Point2D pnt) {
		double[] xy = applyTo(new double[] {pnt.getX(), pnt.getY()});
		return new Point2D.Double(xy[0], xy[1]);
	}
	
	/**
	 * Applies this mapping to all points in the array of 2D points
	 * (convenience method).
	 * 
	 * @param pnts the original points
	 * @return the transformed points
	 */
	public Point2D[] applyTo(Point2D[] pnts) {
		Point2D[] outPnts = new Point2D[pnts.length];
		for (int i = 0; i < pnts.length; i++) {
			outPnts[i] = applyTo(pnts[i]);
		}
		return outPnts;
	}
	
//	// image-to-image mappings (needs to be moved!) ----------------------
//
//	/**
//	 * Destructively transforms the passed image using this geometric
//	 * mapping and the specified pixel interpolation method.
//	 * The geometric mapping is supposed to be INVERTED, i.e. transforming
//	 * target to source image coordinates!
//	 * TODO: this should not be here (geometry only)?
//	 * 
//	 * @param ip target image to which this mapping is applied
//	 * @param im interpolation method
//	 * @deprecated
//	 */
//	public void applyTo(ImageProcessor ip, InterpolationMethod im) {
//		// make a temporary copy of the image:
//		ImageProcessor source = ip.duplicate();
//		ImageProcessor target = ip;
//		applyTo(source, target, im);
//		source = null;
//	}

//	/**
//	 * Transforms the source image to the target image using this geometric
//	 * mapping and the specified pixel interpolation method. Note that source
//	 * and target must be different images!
//	 * The geometric mapping is supposed to be INVERTED, i.e. transforming
//	 * target to source image coordinates!
//	 * 
//	 * @param source input image (not modified)
//	 * @param target output image (modified)
//	 * @param im interpolation method
//	 * @deprecated
//	 */
//	public void applyTo(ImageProcessor source, ImageProcessor target, InterpolationMethod im) {
//		if (target == source) {
//			throw new IllegalArgumentException("Source and target image must not be the same!");
//		}
//		ImageAccessor sourceAcc = ImageAccessor.create(source, null, im);
//		ImageAccessor targetAcc = ImageAccessor.create(target);
//		applyTo(sourceAcc, targetAcc);
//	}


//	/**
//	 * Transforms the source image to the target image using this geometric
//	 * mapping and the specified pixel interpolation method.
//	 * The two images are passed as instances of {@link ImageAccessor}.
//	 * Note that source and target reference different images!
//	 * The geometric mapping is supposed to be INVERTED, i.e. transforming
//	 * target to source image coordinates!
//	 * 
//	 * @param sourceAcc accessor to the source image
//	 * @param targetAcc accessor to the target image
//	 * @deprecated
//	 */
//	public void applyTo(ImageAccessor sourceAcc, ImageAccessor targetAcc) {
//		if (targetAcc.getProcessor() == sourceAcc.getProcessor()) {
//			throw new IllegalArgumentException("Source and target image must not be the same!");
//		}
//		Mapping invMap = this; 		// this always IS an inverse mapping!!
//		ImageProcessor target = targetAcc.getProcessor();
//		final int w = target.getWidth();
//		final int h = target.getHeight();
//		for (int v = 0; v < h; v++) {
//			for (int u = 0; u < w; u++) {
//				Point2D sourcePt = invMap.applyTo(new Point(u, v));
//				float[] val = sourceAcc.getPix(sourcePt.getX(), sourcePt.getY());
//				targetAcc.setPix(u, v, val);
//			}
//		}
//	}
}
