/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.color.quantize;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import ij.IJ;
import ij.process.ColorProcessor;
import imagingbook.lib.color.Rgb;
import imagingbook.pub.color.statistics.ColorHistogram;

/**
 * This is an implementation of Heckbert's median-cut color quantization algorithm 
 * (Heckbert P., "Color Image Quantization for Frame Buffer Display", ACM Transactions
 * on Computer Graphics (SIGGRAPH), pp. 297-307, 1982).
 * Unlike in the original algorithm, no initial uniform (scalar) quantization is used to
 * for reducing the number of image colors. Instead, all colors contained in the original
 * image are considered in the quantization process. After the set of representative
 * colors has been found, each image color is mapped to the closest representative
 * in RGB color space using the Euclidean distance.
 * The quantization process has two steps: first a ColorQuantizer object is created from
 * a given image using one of the constructor methods provided. Then this ColorQuantizer
 * can be used to quantize the original image or any other image using the same set of 
 * representative colors (color table).
 * 
 * @author WB
 * @version 2017/01/03
 * 
 * TODO: needs revision!
 */
public class MedianCutQuantizer extends ColorQuantizer {
	
	private ColorNode[] imageColors = null;
	
//	private final int maxColors;
	private final int[][] colormap;
	
//	private final Parameters params;
	
//	public static class Parameters {
//		/** Maximum number of quantized colors. */
//		public int maxColors = 16;
//		
//		void check() {
//			if (maxColors < 2 || maxColors > 256) {
//				throw new IllegalArgumentException();
//			}
//		}
//	}

	// quick fix, better use a lambda expression?
//	@Deprecated
//	private static Parameters makeParameters(int Kmax) {
//		Parameters p = new Parameters();
//		p.maxColors = Kmax;
//		return p;
//	}
	
//	@Deprecated
//	public MedianCutQuantizer(ColorProcessor ip, int Kmax) {
//		this((int[]) ip.getPixels(), makeParameters(Kmax));
//	}
	
//	@Deprecated
//	public MedianCutQuantizer(int[] pixels, int Kmax) {
//		this(pixels, makeParameters(Kmax));
//	}
	
	 
	public MedianCutQuantizer(int[] pixels, int maxColors) {
//		this.maxColors = maxColors;
//		this.params = params;
//		System.out.println("Kmax = " + this.params.maxColors);
		makeImageColors(pixels);
//		listColors(imageColors);
		ColorNode[] quantColors = findRepresentativeColors(maxColors);
//		listColors(quantColors);
		this.colormap = makeColorMap(quantColors);
		this.imageColors = null;
	}
	
//	void listColors(ColorNode[] colors) {
//		for (ColorNode nd : colors) {
//			IJ.log(nd.toString());
//		}
//	}
	
	private void makeImageColors(int[] pixels) {
		ColorHistogram colorHist = new ColorHistogram(pixels);
		final int K = colorHist.getNumberOfColors();
		this.imageColors = new ColorNode[K];
		for (int i = 0; i < K; i++) {
			int rgb = colorHist.getColor(i);
			int cnt = colorHist.getCount(i);
			imageColors[i] = new ColorNode(rgb, cnt);
		}
		//return imgColors;
	}

	ColorNode[] findRepresentativeColors(int Kmax) {
		final int n = imageColors.length;
		if (n <= Kmax) {// image has fewer colors than Kmax
			return imageColors;
		}
		else {
			System.out.println("splitting  " + n);
			ColorBox initialBox = new ColorBox(0, n - 1, 0);
			List<ColorBox> colorSet = new ArrayList<ColorBox>();
			colorSet.add(initialBox);
			int k = 1;
			boolean done = false;
			while (k < Kmax && !done) {
				ColorBox nextBox = findBoxToSplit(colorSet);
				if (nextBox != null) {
					ColorBox newBox = nextBox.splitBox();
					colorSet.add(newBox);
					k = k + 1;
				} else {
					done = true;
				}
			}
			return getAvgColors(colorSet);
		}
	}
	
	private int[][] makeColorMap(ColorNode[] quantColors) {
		int[][] map = new int[quantColors.length][3];
		for (int i = 0; i < quantColors.length; i++) {
			map[i][0] = quantColors[i].red;
			map[i][1] = quantColors[i].grn;
			map[i][2] = quantColors[i].blu;
		}
		return map;
	}
	
	private ColorBox findBoxToSplit(List<ColorBox> colorBoxes) {
		ColorBox boxToSplit = null;
		// from the set of splitable color boxes
		// select the one with the minimum level
		int minLevel = Integer.MAX_VALUE;
		for (ColorBox box : colorBoxes) {
			if (box.colorCount() >= 2) {	// box can be split
				if (box.level < minLevel) {
					boxToSplit = box;
					minLevel = box.level;
				}
			}
		}
		return boxToSplit;
	}
	
	private ColorNode[] getAvgColors(List<ColorBox> colorBoxes) {
		int n = colorBoxes.size();
		ColorNode[] avgColors = new ColorNode[n];
		int i = 0;
		for (ColorBox box : colorBoxes) {
			avgColors[i] = box.getAvgColor();
			i = i + 1;
		}
		return avgColors;
	}
	

	
	// ------- methods required by abstract super class -----------------------
	
	@Override
	public int[][] getColorMap() {
		return colormap;
	}

	// -------------- class ColorNode -------------------------------------------

	private class ColorNode {
		private final int rgb;
		private final int red, grn, blu;
		private final int cnt;
		
		ColorNode (int rgb, int cnt) {
			this.rgb = (rgb & 0xFFFFFF);
			int[] c = Rgb.intToRgb(rgb);
			this.red = c[0];
			this.grn = c[1];
			this.blu = c[2];
			this.cnt = cnt;
		}
		
		ColorNode (int red, int grn, int blu, int cnt) {
			this.rgb = Rgb.rgbToInt(red, grn, blu);
			this.red = red;
			this.grn = grn;
			this.blu = blu;
			this.cnt = cnt;
		}
		
		public String toString() {
			String s = ColorNode.class.getSimpleName();
			s = s + " red=" + red + " green=" + grn + " blue=" + blu + " rgb=" + rgb + " count=" + cnt;
			return s;
		}
	}
	
	// -------------- inner class ColorBox -------------------------------------------

	private class ColorBox { 
		int lower; 		// lower index into 'imageColors'
		int upper; 		// upper index into 'imageColors'
		int level; 		// split level of this color box
		int count = 0; 	// number of pixels represented by this color box
		int rmin, rmax;	// range of contained colors in red dimension
		int gmin, gmax;	// range of contained colors in green dimension
		int bmin, bmax;	// range of contained colors in blue dimension
		
		ColorBox(int lower, int upper, int level) {
			this.lower = lower;
			this.upper = upper;
			this.level = level;
			this.trim();
		}
		
		int colorCount() {
			return upper - lower;
		}
		
		/**
		 * Recalculates the boundaries and population of this color box.
		 */
		void trim() {
			count = 0;	
			rmin = gmin = bmin = MAX_RGB;	
			rmax = gmax = bmax = 0;
			for (int i = lower; i <= upper; i++) {
				ColorNode color = imageColors[i];
				count = count + color.cnt;
				rmax = Math.max(color.red, rmax);
				rmin = Math.min(color.red, rmin);
				gmax = Math.max(color.grn, gmax);
				gmin = Math.min(color.grn, gmin);
				bmax = Math.max(color.blu, bmax);
				bmin = Math.min(color.blu, bmin);
			}
		}
		
		/**
		 * Splits this color box at the median point along its 
		 * longest color dimension. Modifies the original color
		 * box and creates a new one, which is returned.
		 * @return A new box.
		 */
		ColorBox splitBox() {	
			if (this.colorCount() < 2)	// this box cannot be split
				return null;
			else {
				// find longest dimension of this box:
				ColorDimension dim = getLongestColorDimension();
				// find median along dim
				int med = findMedian(dim);
				// now split this box at the median return the resulting new box
				int nextLevel = level + 1;
				ColorBox newBox = new ColorBox(med + 1, upper, nextLevel);
				this.upper = med;
				this.level = nextLevel;
				this.trim();
				return newBox;
			}
		}
		
		/**
		 * Finds the longest dimension of this color box (RED, GREEN, or BLUE)
		 * @return The color dimension of the longest box side.
		 */
		ColorDimension getLongestColorDimension() {
			final int rLength = rmax - rmin;
			final int gLength = gmax - gmin;
			final int bLength = bmax - bmin;
			if (bLength >= rLength && bLength >= gLength)
				return ColorDimension.BLUE;
			else if (gLength >= rLength && gLength >= bLength)
				return ColorDimension.GREEN;
			else 
				return ColorDimension.RED;
		}
				
		/**
		 * Finds the position of the median of this color box in RGB space along
		 * the red, green or blue dimension, respectively.
		 * @param dim Color dimension.
		 * @return The median value.
		 */
		int findMedian(ColorDimension dim) {
			// sort color in this box along dimension dim:
			Arrays.sort(imageColors, lower, upper + 1, dim.comparator);
			// find the median point:
			int nPixels, median;
			for (median = lower, nPixels = 0; median < upper; median++) {
				nPixels = nPixels + imageColors[median].cnt;
				if (nPixels >= count / 2)
					break;
			}			
			return median;
		}
		
		ColorNode getAvgColor() {
			int rSum = 0;
			int gSum = 0;
			int bSum = 0;
			int n = 0;
			for (int i = lower; i <= upper; i++) {
				ColorNode cn = imageColors[i];
				int cnt = cn.cnt;
				rSum = rSum + cnt * cn.red;
				gSum = gSum + cnt * cn.grn;
				bSum = bSum + cnt * cn.blu;
				n = n + cnt;
			}
			int avgRed = (rSum + (n / 2)) / n;
			int avgGrn = (gSum + (n / 2)) / n;
			int avgBlu = (bSum + (n / 2)) / n;
			return new ColorNode(avgRed, avgGrn, avgBlu, n);
		}

		@Override
		public String toString() {
			String s = this.getClass().getSimpleName();
			s = s + " lower=" + lower + " upper=" + upper;
			s = s + " count=" + count + " level=" + level;
			s = s + " rmin=" + rmin + " rmax=" + rmax;
			s = s + " gmin=" + gmin + " gmax=" + gmax;
			s = s + " bmin=" + bmin + " bmax=" + bmax;
			s = s + " bmin=" + bmin + " bmax=" + bmax;
			return s;
		}
	}
		
	/**
	 * The main purpose of this inner enumeration class is to associate the
	 * color dimensions RED, GREEN, BLUE with the corresponding comparators.
	 * Implementation uses anonymous classes.
	 */
	private enum ColorDimension {
		RED (new Comparator<ColorNode>() {
			@Override
			public int compare(ColorNode colA, ColorNode colB) {
				return colA.red - colB.red;
			}}), 
		GREEN (new Comparator<ColorNode>() {
			@Override
			public int compare(ColorNode colA, ColorNode colB) {
				return colA.grn - colB.grn;
			}}), 
		BLUE (new Comparator<ColorNode>() {
			@Override
			public int compare(ColorNode colA, ColorNode colB) {
				return colA.blu - colB.blu;
			}});

		final Comparator<ColorNode> comparator;

		ColorDimension(Comparator<ColorNode> cmp) {
			this.comparator = cmp;
		}
	}
	
} 

