/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.ij;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import ij.io.Opener;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.lib.util.bits.BitMap;


/**
 * TODO: Clarify assumed arrangement of 2D arrays!
 * @author WB
 *
 */
public abstract class IjUtils {
	
	/**
	 * Returns a (possibly empty) array of ImagePlus objects that are
	 * sorted by their titles if the 'sortByTitle' flag is set.
	 * 
	 * @param sortByTitle flag, result is sorted if true.
	 * @return an array of currently open images.
	 */
	public static ImagePlus[] getOpenImages(boolean sortByTitle) {
		return getOpenImages(sortByTitle, null);
	}
	

	/**
	 * Returns an array of strings containing the short titles
	 * of the images supplied.
	 * 
	 * @param images array of images.
	 * @return array of names.
	 */
	public static String[] getImageShortTitles(ImagePlus[] images) {
		String[] imageNames = new String[images.length];
		for (int i = 0; i < images.length; i++) {
			imageNames[i] = images[i].getShortTitle();
		}
		return imageNames;
	}
	
	/**
	 * Opens a dialog to let the user select one of the currently open images.
	 * @param title string to show in the dialog
	 * @return An ImagePlus object, use the getProcessor method to obtain the associated ImageProcessor
	 */
	public static ImagePlus selectOpenImage(String title) {
		ImagePlus[] openImages = getOpenImages(true, null);
		String[] imageNames = getImageShortTitles(openImages);
		if (title == null) {
			title = "image:";
		}
		GenericDialog gd = new GenericDialog("Select image");
		gd.addChoice(title, imageNames, imageNames[0]);
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return null;
		else {
			return openImages[gd.getNextChoiceIndex()];
		}
	}
	
	
	/**
	 * Returns a (possibly empty) array of {@link ImagePlus} objects that are
	 * sorted by their titles if the sortByTitle flag is set.
	 * The image "exclude" (typically the current image) is not included 
	 * in the returned array (pass null to exclude no image).
	 * 
	 * @param sortByTitle set {@code true} to return images sorted by title
	 * @param exclude reference to an image to be excluded (may be {@code null})
	 * @return a (possibly empty) array of {@link ImagePlus} objects
	 */
	public static ImagePlus[] getOpenImages(boolean sortByTitle, ImagePlus exclude) {
		List<ImagePlus> imgList = new LinkedList<ImagePlus>();
		int[] wList = WindowManager.getIDList();
        if (wList != null) {
	    	for (int i : wList) {
	            ImagePlus imp = WindowManager.getImage(i);
	            if (imp != null && imp != exclude) {
	            	imgList.add(imp);
	            }
	    	}
        }
        ImagePlus[] impArr = imgList.toArray(new ImagePlus[0]);
        if (sortByTitle) {
        	Comparator<ImagePlus> cmp = new Comparator<ImagePlus>() {
        		public int compare(ImagePlus impA, ImagePlus impB) {
        			return impA.getTitle().compareTo(impB.getTitle());
        		}
        	};
        	Arrays.sort(impArr, cmp);
        }
		return impArr;
	}
	

	/**
	 *  Queries the user for an arbitrary file to be opened.
	 *  
	 * @param title string to be shown in the interaction window.
	 * @return path of the selected resource.
	 */
	public static String askForOpenPath(String title) {
		OpenDialog od = new OpenDialog(title, "");
		String dir = od.getDirectory();
		String name = od.getFileName();
		if (name == null)
			return null;
		return encodeURL(dir + name);
	}
	
	private static String encodeURL(String url) {
		//url = url.replaceAll(" ","%20");	// this doesn't work with spaces
		url = url.replace('\\','/');
		return url;
	}
	
	//----------------------------------------------------------------------
	

	/**
	 * Creates an ImageJ {@link ImagePlus} image for the matrix {@code M[r][c]} (2D array),
	 * where {@code r} is treated as the row (vertical) coordinate and
	 * {@code c} is treated as the column (horizontal) coordinate.
	 * Use {@code show()} to display the resulting image.
	 * 
	 * @param title image title
	 * @param M 2D array
	 * @return a new {@link ImagePlus} image
	 */
	public static ImagePlus createImage(String title, float[][] M) {
		FloatProcessor fp = new FloatProcessor(M[0].length, M.length);
		for (int u = 0; u < M[0].length; u++) {
			for (int v = 0; v < M.length; v++) {
				fp.setf(u, v, M[v][u]);
			}
		}
		return new ImagePlus(title, fp);
	}
	

	/**
	 * Creates an ImageJ {@link ImagePlus} image for the matrix {@code M[r][c]} (2D array),
	 * where {@code r} is treated as the row (vertical) coordinate and
	 * {@code c} is treated as the column (horizontal) coordinate.
	 * Use {@code show()} to display the resulting image.
	 * 
	 * @param title the image title
	 * @param M a 2D array holding the image data
	 * @return a new {@link ImagePlus} instance
	 */
	public static ImagePlus createImage(String title, double[][] M) {
		FloatProcessor fp = new FloatProcessor(M[0].length, M.length);
		for (int u = 0; u < M[0].length; u++) {
			for (int v = 0; v < M.length; v++) {
				fp.setf(u, v, (float) M[v][u]);
			}
		}
		return new ImagePlus(title, fp);
	}
	
	/**
	 * Sets the weighing factors for the color components used
	 * in RGB-to-grayscale conversion for the specified image {@code ip}.
	 * Note that this method can be applied to any {@link ImageProcessor}
	 * instance but has no effect unless {@code ip} is of type
	 * {@link ColorProcessor}. Applies standard (ITU-709) weights.
	 * 
	 * @param ip The affected image
	 */
	public static void setRgbConversionWeights(ImageProcessor ip) {
		setRgbConversionWeights(ip, 0.299, 0.587, 0.114);
	}
	
	/**
	 * Sets the weighing factors for the color components used
	 * in RGB-to-grayscale conversion for the specified image {@code ip}.
	 * Note that this method can be applied to any {@link ImageProcessor}
	 * instance but has no effect unless {@code ip} is of type
	 * {@link ColorProcessor}.
	 * 
	 * @param ip The affected image
	 * @param wr Red weight
	 * @param wg Green weight
	 * @param wb Blue weight
	 */
	public static void setRgbConversionWeights(ImageProcessor ip, double wr, double wg, double wb) {
		if (ip instanceof ColorProcessor) {
			((ColorProcessor) ip).setRGBWeights(wr, wg, wb);
		}
	}
	
	
	// -------------------------------------------------------------------
	
	/** 
	 * Returns a copy of the pixel data as a 2D double
	 * array with dimensions [x = 0..width-1][y = 0..height-1].
	 * @param fp the image
	 * @return the resulting array
	 */
	public static double[][] toDoubleArray(FloatProcessor fp) {
		final int width = fp.getWidth();
		final int height = fp.getHeight();
		float[] fPixels = (float[]) fp.getPixels();
		double[][] dPixels = new double[width][height];
		int i = 0;
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				dPixels[u][v] = fPixels[i];
				i++;
			}
		}
		return dPixels;
	}
	
	/**
	 * Creates a new {@link FloatProcessor} instance of size width x height
	 * from the given 2D array with dimensions [x = 0..width-1][y = 0..height-1].
	 * 
	 * @param dPixels a 2D {@code double} array
	 * @return a new {@link FloatProcessor} instance
	 */
	public static FloatProcessor toFloatProcessor(double[][] dPixels) {
		final int width = dPixels.length;
		final int height = dPixels[0].length;
		float[] fPixels = new float[width * height];
		int i = 0;
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				fPixels[i] = (float) dPixels[u][v];
				i++;
			}
		}
		return new FloatProcessor(width, height, fPixels);
	}
	
	// Open image from path (used with resources)
	
	/**
	 * Opens the image from the specified path and returns it
	 * as a {@link ImagePlus} instance.
	 * 
	 * @param name The simple name of the image file (including extension)
	 * @return A new {@link ImagePlus} instance or {@code null} if not found
	 */
	public static ImagePlus openImage(Path name) {
		Objects.requireNonNull(name);
		return new Opener().openImage(name.toString());
	}
	
	
	// Methods for checking/comparing images (primarily used for testing)  ---------------------
	
	/**
	 * Checks if two images are of the same type.
	 * 
	 * @param ip1 the first image
	 * @param ip2 the second image
	 * @return true if both images have the same type
	 */
	public static boolean sameType(ImageProcessor ip1, ImageProcessor ip2) {
		return ip1.getClass().equals(ip2.getClass());
	}
	
	/**
	 * Checks if two images have the same size.
	 * 
	 * @param ip1 the first image
	 * @param ip2 the second image
	 * @return true if both images have the same size
	 */
	public static boolean sameSize(ImageProcessor ip1, ImageProcessor ip2) {
		return ip1.getWidth() == ip2.getWidth() && ip1.getHeight() == ip2.getHeight();
	}
	
	/**
	 * Checks if the given image is possibly a binary image. This requires that
	 * the image contains at most two different pixel values, one of which
	 * must be zero. All pixels are checked.
	 * This should work for all image types.
	 * More efficient implementations are certainly possible.
	 * 
	 * @param ip the image ({@link ImageProcessor}) to be checked
	 * @return true if the image is binary
	 */
	public static boolean isBinary(ImageProcessor ip) {
		final int width = ip.getWidth();
		final int height = ip.getHeight();
		int fgVal = 0;
		boolean binary = true;
		
		outer:
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int val = 0x007FFFFF & ip.get(u, v); // = mantissa in case of float
				if (val != 0) {
					if (fgVal == 0) {	// first non-zero value
						fgVal = val;
					}
					else if (val != fgVal) {	// found another non-zero value
						binary = false;
						break outer;
					}
				}
			}
		}
		
		return binary;
	}
	
	public static final double DefaultMatchTolerance = 1E-6;
	
	/**
	 * Checks if two images have the same type, size and content (using 
	 * {@link #DefaultMatchTolerance}).
	 * 
	 * @param ip1 the first image
	 * @param ip2 the second image
	 * @return true if both images have the same type and content
	 */
	public static boolean match(ImageProcessor ip1, ImageProcessor ip2) {
		return match(ip1, ip2, DefaultMatchTolerance);
	}
	
	/**
	 * Checks if two images have the same type, size and content (using 
	 * the specified tolerance). 
	 * 
	 * @param ip1 the first image
	 * @param ip2 the second image
	 * @param tolerance the matching tolerance
	 * @return true if both images have the same type, size and content
	 */
	public static boolean match(ImageProcessor ip1, ImageProcessor ip2, double tolerance) {
		if (!sameType(ip1, ip2)) {
			return false;
		}
		if (!sameSize(ip1, ip2)) {
			return false;
		}
		
		if (ip1 instanceof ByteProcessor) {
			byte[] p1 = (byte[]) ip1.getPixels();
			byte[] p2 = (byte[]) ip2.getPixels();
			return Arrays.equals(p1, p2);
		}
		else if (ip1 instanceof ShortProcessor) {
			short[] p1 = (short[]) ip1.getPixels();
			short[] p2 = (short[]) ip2.getPixels();
			return Arrays.equals(p1, p2);
		}
		else if (ip1 instanceof ColorProcessor) {
			int[] p1 = (int[]) ip1.getPixels();
			int[] p2 = (int[]) ip2.getPixels();
			return Arrays.equals(p1, p2);
		}
		
		else if (ip1 instanceof FloatProcessor) {
			float[] p1 = (float[]) ip1.getPixels();
			float[] p2 = (float[]) ip2.getPixels();
			boolean same = true;
			final float toleranceF = (float) tolerance;
			for (int i = 0; i < p1.length; i++) {
				if (Math.abs(p1[i] - p2[i]) > toleranceF) {
					same = false;
					break;
				}
			}
			return same;
		}

		throw new IllegalArgumentException("unknown processor type " + ip1.getClass().getSimpleName());
	}
	
	// BitMap from/to ByteProcessor conversion
	
	public static BitMap convertToBitMap(ByteProcessor bp) {
		return new BitMap(bp.getWidth(), bp.getHeight(), (byte[]) bp.getPixels());
	}
	
	 public static ByteProcessor convertToByteProcessor(BitMap bitmap) {
		 byte[] pixels = bitmap.getBitVector().toByteArray();
		 return new ByteProcessor(bitmap.getWidth(), bitmap.getHeight(), pixels);
	 }

}
