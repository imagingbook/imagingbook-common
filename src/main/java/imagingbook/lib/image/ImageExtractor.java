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

import ij.process.ImageProcessor;
import imagingbook.pub.geometry.mappings.linear.AffineMapping;
import imagingbook.pub.geometry.mappings.linear.LinearMapping;
import imagingbook.pub.geometry.mappings.linear.ProjectiveMapping;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Use to exctract warped images for testing the Lucas-Kanade matcher.
 * @author wilbur
 *
 */
public class ImageExtractor {
	
	
//	public static ImageProcessor extract(ImageProcessor source, int width, int height, LinearMapping T) {
//		ImageExtractor ie = new ImageExtractor(source);
//		return ie.extractImage(width, height, T);
//	}
	
		
	private int interpolationMethod = ImageProcessor.BILINEAR;
	private final ImageProcessor I;
	
	/**
	 * Creates a new instance of {@link ImageExtractor} for the image {@code I}.
	 * @param I the target image.
	 */
	public ImageExtractor(ImageProcessor I) {
		this.I = I;
	}
	
	public void setInterpolationMethod(int interpolationMethod) {
		this.interpolationMethod = interpolationMethod;
	}
	
	/**
	 * Extracts an image {@code R} of size {@code width} x {@code height} from the source image 
	 * {@code I} (referenced by {@code this} object).
	 * The image {@code R} is extracted from a quadrilateral patch of the source image,
	 * defined by the transformation of the boundary of {@code R} by {@code T(x)}.
	 * @param width the width of the target image {@code R}.
	 * @param height the height of the target image {@code R}.
	 * @param T a {@link LinearMapping} object.
	 * @return the extracted image {@code R}, which is of the same type as the source image.
	 */	

	public ImageProcessor extractImage(int width, int height, LinearMapping T) {
		ImageProcessor R = I.createProcessor(width, height);
		extractImage(R, T);
		return R;
	}
	
	public ImageProcessor extractImage(int width, int height, Point2D[] sourcePnts) {
		ImageProcessor R = I.createProcessor(width, height);
		ProjectiveMapping T = getMapping(width, height, sourcePnts);
		extractImage(R, T);
		return R;
	}
	
	/**
	 * Fills the image {@code R} from the source image 
	 * {@code I} (referenced by {@code this} object).
	 * The image {@code R} is extracted from a quadrilateral patch of the source image,
	 * defined by the transformation of the boundary of {@code R} by {@code T(x)}.
	 * Grayscale and color images my not be mixed (i.e., {@code R} must be of the same type as {@code I}).
	 * @param R the image to be filled.
	 * @param T a {@link LinearMapping} object.
	 */	
	public void extractImage(ImageProcessor R, LinearMapping T) {
		int prevInterpolationMethod = I.getInterpolationMethod();
		// save current interpolation method
		I.setInterpolationMethod(interpolationMethod);
		
		ImageAccessor iaI = ImageAccessor.create(I);
		ImageAccessor iaR = ImageAccessor.create(R);
	
		int wT = R.getWidth();
		int hT = R.getHeight();
		for (int u = 0; u < wT; u++) {
			for (int v = 0; v < hT; v++) {
				Point2D uv = new Point(u, v);
				Point2D xy = T.applyTo(uv);
				float[] val = iaI.getPix(xy.getX(), xy.getY());
				iaR.setPix(u, v, val);
			}
		}
		// restore interpolation method
		I.setInterpolationMethod(prevInterpolationMethod);
	}
	
	/**
	 * Extracts a warped sub-image of the associated target image I,
	 * defined by a sequence of 3 or 4 points. In the case of 3 
	 * points in sourcePnts, an {@link AffineMapping} is used; with 4 points,
	 * a {@link ProjectiveMapping} is used. The 3 or 4 points map clockwise to
	 * the corner points of the target image R, starting with the top-left corner.
	 * @param R the target image;
	 * @param sourcePnts an array of 3 or 4 {@link Point2D} objects.
	 */
	public void extractImage(ImageProcessor R, Point2D[] sourcePnts) {
		ProjectiveMapping T = getMapping(R.getWidth(), R.getHeight(), sourcePnts);
		extractImage(R, T);
	}
	
	private ProjectiveMapping getMapping(int w, int h, Point2D[] sourcePnts) {
		Point2D[] targetPnts = {
				new Point(0, 0), new Point(w - 1, 0),
				new Point(w - 1, h - 1), new Point(0, h - 1)
			};
		ProjectiveMapping T = null;
		switch (sourcePnts.length) {
		case (3) : T = new AffineMapping(targetPnts, sourcePnts); break;
		case (4) : T = new ProjectiveMapping(targetPnts, sourcePnts); break;
		default : throw new IllegalArgumentException("wrong number of source points");
		}
		return T;
	}
	
}
