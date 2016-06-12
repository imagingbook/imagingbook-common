/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.pub.morphology;

import ij.IJ;
import ij.ImagePlus;
import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;


/**
 * This class implements binary morphological filters.
 * Original version 2007.
 * TODO: Make a subclass for thinning.
 * @author W. Burger
 * @version 2015-07-08 
 */
public class BinaryMorphologyFilter {
	
	private final int[][] H; // structuring element

	public static enum OpType {
		Dilate, Erode, Open, Close, Outline, Thin;
	}

	// constructor methods

	public BinaryMorphologyFilter() {
		H = makeBoxKernel3x3();
	}

	public BinaryMorphologyFilter(int[][] H) {
		this.H = H;
	}
	
	// public methods

	public void applyTo(ByteProcessor I, OpType op) {
		switch(op) {
			case Dilate:	this.dilate(I, H);	break;
			case Erode: 	this.erode(I, H);	break;
			case Open: 		this.open(I, H);	break;
			case Close: 	this.close(I, H);	break;
			case Outline:	this.outline(I);	break;
			case Thin:		this.thin(I);		break;
			default: throw new Error("BinMorpher: unknown operation " + op);
		}
	}
	
	public void dilate(ByteProcessor ip) {
		dilate(ip, H);
	}
	
	public void erode(ByteProcessor ip) {
		erode(ip, H);
	}
	
	public void open(ByteProcessor ip) {
		open(ip, H);
	}
	
	public void close(ByteProcessor ip) {
		close(ip, H);
	}

	// internal morphology methods 
	
	private void dilate(ByteProcessor ip, int[][] H) {
		if (H == null) {
			IJ.error("no structuring element");
			return;
		}

		//assume that the hot spot of se is at its center (ic,jc)
		int ic = (H[0].length - 1) / 2;
		int jc = (H.length - 1) / 2;
		int N = H.length * H[0].length;
		
		ImageProcessor tmp = ip.createProcessor(ip.getWidth(), ip.getHeight());
		
		int k = 0;
		IJ.showProgress(k, N);
		for (int j = 0; j < H.length; j++) {
			for (int i = 0; i < H[j].length; i++) {
				if (H[j][i] > 0) { // this element is set
					// copy image into position (u-ch,v-cv)
					tmp.copyBits(ip, i - ic, j - jc, Blitter.MAX);
				}
				IJ.showProgress(k++, N);
			}
		}
		ip.copyBits(tmp, 0, 0, Blitter.COPY);
		
	}
	
	private void erode(ByteProcessor ip, int[][] H) {
		// dilates the background
		ip.invert();
		dilate(ip, reflect(H));
		ip.invert();
	}

	private void open(ByteProcessor ip, int[][] H) {
		erode(ip, H);
		dilate(ip, H);
	}

	private void close(ByteProcessor ip, int[][] H) {
		dilate(ip, H);
		erode(ip, H);
	}

	public void outline(ByteProcessor ip) {
		int[][] H = { 
				{ 0, 1, 0 }, 
				{ 1, 1, 1 }, 
				{ 0, 1, 0 } };
		ByteProcessor foreground = (ByteProcessor) ip.duplicate();
		erode(foreground, H);
		ip.copyBits(foreground, 0, 0, Blitter.DIFFERENCE);
	}

	private int[][] reflect(int[][] se) {
		// mirrors the structuring element around the center (hot spot)
		// used to implement erosion by a dilation
		int N = se.length; // number of rows
		int M = se[0].length; // number of columns
		int[][] fse = new int[N][M];
		for (int j = 0; j < N; j++) {
			for (int i = 0; i < M; i++) {
				fse[j][i] = se[N - j - 1][M - i - 1];
			}
		}
		return fse;
	}
	
	/*
	 * Fast parallel thinning. Returns the number of iterations performed.
	 */
	public int thin(ByteProcessor ip) {
		return thin(ip, 1500);
	}
	
	public int thin(ByteProcessor ip, int iMax) {
		int M = ip.getWidth();
		int N = ip.getHeight();
		byte[][] D = new byte[M][N];
		int n;
		int iter = 0;
		do {
			n = thinOnce(ip, D);
			iter++;
		} while (n > 0 && iter <  iMax);
		return iter;
	}
	
	// Single thinning interation. Returns the number of deletions performed.
	public int thinOnce(ByteProcessor ip) {
		int M = ip.getWidth();
		int N = ip.getHeight();
		byte[][]  D = new byte[M][N];
		return thinOnce(ip, D);
	}
	
	// Single thinning interation. Returns the number of deletions performed.
	private int thinOnce(ByteProcessor ip, byte[][] D) {
		int M = ip.getWidth();
		int N = ip.getHeight();
		int nd = 0;
		for (byte p = 1; p <= 2; p++) {	// make 2 passes
			int n = 0;
			for (int u = 0; u < M; u++) {
				for (int v = 0; v < N; v++) {
					D[u][v] = 0;
					if (ip.get(u, v) > 0) {
						int c = getNeighborhoodIndex(ip, u, v);
						byte q = Q[c];
						// if (deleteCode == 1 || deleteCode == 3)
						if ((p & q) != 0) {
							D[u][v] = 1;
							n = n + 1;
						}
					}
				}
			}			
			if (n > 0) {
				deleteMarked(ip, D);
				nd = nd + n;
			}
		}
		return nd;
	}
	
	// neighborhood deletion code table
	private static final byte[] Q = {
		0, 0, 0, 3, 0, 0, 3, 3, 0, 0, 0, 0, 3, 0, 3, 3, 
		0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 3, 1, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 3, 1, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 1, 0, 1, 0, 
		0, 3, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 3, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		3, 3, 0, 3, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		3, 3, 0, 3, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 
		3, 2, 0, 2, 0, 0, 0, 0, 3, 2, 0, 0, 1, 0, 0, 0 
	};
	
	private int getNeighborhoodIndex(ImageProcessor ip, int u, int v) {
		int c = 0;
		if (ip.getPixel(u+1, v) > 0) 	c+= 1;		// NH[0]
		if (ip.getPixel(u+1, v-1) > 0) 	c+= 2;		// NH[1]
		if (ip.getPixel(u, v-1) > 0) 	c+= 4; 		// NH[2]
		if (ip.getPixel(u-1, v-1) > 0) 	c+= 8;		// NH[3]
		if (ip.getPixel(u-1, v) > 0) 	c+= 16;		// NH[4]
		if (ip.getPixel(u-1, v+1) > 0) 	c+= 32;		// NH[5]
		if (ip.getPixel(u, v+1) > 0) 	c+= 64;		// NH[6]
		if (ip.getPixel(u+1, v+1) > 0) 	c+= 128;	// NH[7]
		return c;	// c = 0,...,255
	}
	
	private void deleteMarked (ByteProcessor ip, byte[][] D) {
		int w = ip.getWidth();
		int h = ip.getHeight();
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				if (D[u][v] > 0) {
					ip.putPixel(u, v, 0);
				}
			}
		}
	}
	
	//-------------------------------------------------------
	
	/*
	 * 'Slow' parallel thinning. Returns the number of iterations performed.
	 */
	public int thinSlow(ByteProcessor ip) {
		return thinSlow(ip, 1500);
	}
	
	public int thinSlow(ByteProcessor ip, int iMax) {
		int M = ip.getWidth();
		int N = ip.getHeight();
		byte[][] D = new byte[M][N];
		int n;
		int iter = 0;
		do {
			n = thinOnceSlow(ip, D);
			iter++;
		} while (n > 0 && iter <  iMax);
		return iter;
	}
	
	// Single thinning interation. Returns the number of deletions performed.
	private int thinOnceSlow(ByteProcessor ip, byte[][] D) {
		int M = ip.getWidth();
		int N = ip.getHeight();

		// PASS 1 --------------------------------------
		int n1 = 0;
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				D[u][v] = 0;
				if (ip.get(u, v) > 0) {
					byte[] NH = getNeighborhood(ip, u, v);
					if (R1(NH)) {
						D[u][v] = 1;
						n1 = n1 + 1;
					}
				}
			}
		}
		if (n1 > 0)
			deleteMarked(ip, D);

		// PASS 2 --------------------------------------
		int n2 = 0;
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				D[u][v] = 0;
				if (ip.get(u, v) > 0) {
					byte[] NH = getNeighborhood(ip, u, v);
					if (R2(NH)) {
						D[u][v] = 1;
						n2 = n2 + 1;
					}
				}
			}
		}
		if (n2 > 0)
			deleteMarked(ip, D);
		return n1 + n2;
	}
	
	private byte[] getNeighborhood(ImageProcessor ip, int u, int v) {
		byte[] NH = new byte[8];
		NH[0] =  binarize(ip.getPixel(u+1, v));
		NH[1] =  binarize(ip.getPixel(u+1, v-1));
		NH[2] =  binarize(ip.getPixel(u, v-1));
		NH[3] =  binarize(ip.getPixel(u-1, v-1));
		NH[4] =  binarize(ip.getPixel(u-1, v));
		NH[5] =  binarize(ip.getPixel(u-1, v+1));
		NH[6] =  binarize(ip.getPixel(u, v+1));
		NH[7] =  binarize(ip.getPixel(u+1, v+1));
		return NH;
	}
	
	private byte binarize(int i) {
		final byte b0 = (byte) 0;
		final byte b1 = (byte) 1;
		return (i == 0) ? b0 : b1;
	}

	private boolean R1(byte[] NH) {
		final int b = B(NH);
		final int c = C(NH);
		return 
				//NH[0] == 1 &&
				2 <= b && b <= 6 &&
				c == 1 &&
				NH[6] * NH[0] * NH[2] == 0 &&
				NH[4] * NH[6] * NH[0] == 0;
	}
	
	private boolean R2(byte[] NH) {
		final int b = B(NH);
		final int c = C(NH);
		return 
				//NH[0] == 1 &&
				2 <= b && b <= 6 &&
				c == 1 &&
				NH[0] * NH[2] * NH[4] == 0 &&
				NH[2] * NH[4] * NH[6] == 0;
	}
	
	private int B(byte[] NH) {
		return NH[0] + NH[1] + NH[2] + NH[3] + NH[4] + NH[5] + NH[6] + NH[7];
	}
	
	private int C(byte[] NH) { // NH = (N0, N1, N2, N3, N4, N5, N6, N7)
		int c = 0;
		for (int i = 0; i < 8; i++) {
			c = c + NH[i] * (NH[i] - NH[(i+1) % 8]);
		}
		return c;
	}
	
// ------------------------------------------------------------------

	/*
	 * Inner class: use as 'BinaryMorphologyFilter.Box'
	 */
	public static class Box extends BinaryMorphologyFilter {

//		public Box() {
//			super(makeBoxKernel3x3());
//		}
		
		public Box(int rad) {
			super(makeBoxElement(rad));
		}
	}
	
	/*
	 * Inner class: use as 'BinaryMorphologyFilter.Disk'
	 */
	public static class Disk extends BinaryMorphologyFilter {

//		public Disk() {
//			super(makeDiskElement(1));
//		}
		
		public Disk(double rad) {
			super(makeDiskElement(rad));
		}
	}
	
	// ------------------------------------------------------------------

	private static int[][] makeBoxKernel3x3() {
		return  makeBoxElement(1);
	}

	private static int[][] makeBoxElement(int radius) {
		int r = (int) Math.rint(radius);
		if (r <= 1)
			r = 1;
		int size = r + r + 1;
		int[][] strElem = new int[size][size];
		for (int v = 0; v < strElem.length; v++) {
			for (int u = 0; u < strElem[v].length; u++) {
				strElem[v][u] = 1;
			}
		}
		return strElem;
	}
	
	private static int[][] makeDiskElement(double radius){
		int r = (int) Math.rint(radius);
		if (r <= 1) r = 1;
		int size = r + r + 1;
		int[][] strElem = new int[size][size];
		double r2 = radius * radius;

		for (int v = -r; v <= r; v++) {
			for (int u = -r; u <= r; u++) {
				if (u * u + v * v <= r2)
					strElem[v + r][u + r] = 1;
			}
		}
		return strElem;
	}
	
	// ----------------------------------------------------------------
	
	// utility methods (migrate to ...)?
	
	public void showStructuringElement() {
		show(H, "Structuring Element");
	}

	public void show(int[][] matrix, String title) {
		int w = matrix[0].length;
		int h = matrix.length;
		ImageProcessor ip = new ByteProcessor(w, h);
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				if (matrix[v][u] == 1)
					ip.putPixel(u, v, 255);
				else
					ip.putPixel(u, v, 0);
			}
		}
		ip.invertLut();
		(new ImagePlus(title, ip)).show();
	}
}

