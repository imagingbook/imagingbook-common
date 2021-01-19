package imagingbook.lib.image.data;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.testutils.ImageTests;

public class PixelPackTest {

	private Path path1 = new imagingbook.DATA.images.Resources().getResourcePath("monastery-small.png");
	private Path path2 = new imagingbook.DATA.images.Resources().getResourcePath("clown.png");

	@Test
	public void testByteImage() {
		ImageProcessor ip1 = IjUtils.openImage(path1).getProcessor();
    	PixelPack pack = new PixelPack(ip1);
    	ImageProcessor ip2 = ip1.duplicate();
    	pack.copyToImageProcessor(ip2);
    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
	}
	
	@Test
	public void testShortImage() {
		ImageProcessor ip1 = IjUtils.openImage(path1).getProcessor().convertToShortProcessor();
		PixelPack pack = new PixelPack(ip1);
    	ImageProcessor ip2 = ip1.duplicate();
    	pack.copyToImageProcessor(ip2);
    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
	}
	
	@Test
	public void testFloatImage() {
		ImageProcessor ip1 = IjUtils.openImage(path1).getProcessor().convertToFloatProcessor();
		PixelPack pack = new PixelPack(ip1);
    	ImageProcessor ip2 = ip1.duplicate();
    	pack.copyToImageProcessor(ip2);
    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
	}
	
	@Test
	public void testColorImage() {
		ImageProcessor ip1 = IjUtils.openImage(path2).getProcessor();
		PixelPack pack = new PixelPack(ip1);
    	ImageProcessor ip2 = ip1.duplicate();
    	pack.copyToImageProcessor(ip2);
    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
	}

}
