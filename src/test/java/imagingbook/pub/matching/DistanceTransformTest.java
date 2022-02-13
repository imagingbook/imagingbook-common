package imagingbook.pub.matching;

import java.util.Arrays;

import org.junit.Test;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

// TODO: UNFINISHED!
public class DistanceTransformTest {
	
	static int W = 12;
	static int H = 10;

	static byte[] pixels = {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	
	static byte[] distManhattan = { 
			4, 3, 3, 2, 2, 2, 3, 4, 5, 6, 7, 7, 
			3, 2, 2, 1, 1, 1, 2, 3, 4, 5, 6, 6, 
			2, 1, 1, 1, 0, 1, 2, 3, 4, 4, 5, 5, 
			2, 1, 0, 1, 1, 1, 2, 3, 3, 3, 4, 4, 
			2, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 4, 
			3, 2, 2, 2, 3, 2, 1, 1, 1, 1, 2, 3, 
			4, 3, 3, 3, 3, 2, 1, 0, 0, 1, 2, 3, 
			5, 4, 4, 4, 3, 2, 1, 0, 1, 1, 2, 3, 
			6, 5, 5, 4, 3, 2, 1, 1, 1, 2, 3, 4, 
			7, 6, 6, 5, 4, 3, 2, 2, 2, 3, 4, 4
	};


	@Test
	public void test() {
		ImageProcessor ip = new ByteProcessor(W, H, pixels);
		DistanceTransform dt = new DistanceTransform(ip);
		float[][] dmap = dt.getDistanceMap();
		FloatProcessor fp = new FloatProcessor(dmap);
		ByteProcessor bp = fp.convertToByteProcessor(false);
		new ImagePlus("Test", fp).show();
	}

	public static void main(String[] args) {
		ImageProcessor ip = new ByteProcessor(W, H, pixels);
		DistanceTransform dt = new DistanceTransform(ip);
		float[][] dmap = dt.getDistanceMap();
		FloatProcessor fp = new FloatProcessor(dmap);
		new ImagePlus("Test", fp).show();
		
		ByteProcessor bp = fp.convertToByteProcessor(false);
		byte[] pixels = (byte[]) bp.getPixels();
		int[] ints = toIntArray(pixels);
		System.out.println("pixels = \n" + Arrays.toString(ints));
	}
	
	private static int[] toIntArray(byte[] bytes) {
		int n = bytes.length;
		int[]  ints = new int[n];
		for (int i=0; i < n; i++) {
			ints[i] = 0xFF & bytes[i];
		}
		return ints;
	}
	
}
