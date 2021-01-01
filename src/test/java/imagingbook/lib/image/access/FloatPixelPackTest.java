package imagingbook.lib.image.access;

import static org.junit.Assert.*;

import java.nio.file.Path;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.testutils.ImageTests;

/**
 * Test the conversion to/from float arrays.
 * @author WB
 *
 */
public class FloatPixelPackTest {	// TODO: needs to be adapted to FloatPixelPack!
	
	Path path1 = new imagingbook.DATA.images.Resources().getResourcePath("monastery-small.png");
	Path path2 = new imagingbook.DATA.images.Resources().getResourcePath("clown.png");

//	@Test
//	public void testByteImage() {
//		ImageProcessor ip1 = IjUtils.openImage(path1).getProcessor();
//    	float[][] sources = FloatArrays.fromImageProcessor(ip1);
//    	ImageProcessor ip2 = ip1.duplicate();
//    	FloatArrays.copyToImageProcessor(sources, ip2);
//    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
//	}
	
//	@Test
//	public void testShortImage() {
//		ImageProcessor ip1 = IjUtils.openImage(path1).getProcessor().convertToShortProcessor();
//    	float[][] sources = FloatArrays.fromImageProcessor(ip1);
//    	ImageProcessor ip2 = ip1.duplicate();
//    	FloatArrays.copyToImageProcessor(sources, ip2);
//    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
//	}
//	
//	@Test
//	public void testFloatImage() {
//		ImageProcessor ip1 = IjUtils.openImage(path1).getProcessor().convertToFloatProcessor();
//    	float[][] sources = FloatArrays.fromImageProcessor(ip1);
//    	ImageProcessor ip2 = ip1.duplicate();
//    	FloatArrays.copyToImageProcessor(sources, ip2);
//    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
//	}
//	
//	@Test
//	public void testColorImage() {
//		ImageProcessor ip1 = IjUtils.openImage(path2).getProcessor();
//    	float[][] sources = FloatArrays.fromImageProcessor(ip1);
//    	ImageProcessor ip2 = ip1.duplicate();
//    	FloatArrays.copyToImageProcessor(sources, ip2);
//    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
//	}

}
