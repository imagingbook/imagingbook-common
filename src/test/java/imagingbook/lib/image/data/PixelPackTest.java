package imagingbook.lib.image.data;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.lib.util.resource.ResourceLocation;
import imagingbook.lib.util.resource.ResourceLocation.Resource;
import imagingbook.testutils.ImageTests;

public class PixelPackTest {

	ResourceLocation loc = new imagingbook.DATA.images.RLOC();
	private Resource path1 = loc.getResource("monastery-small.png");
	private Resource path2 = loc.getResource("clown.png");

	@Test
	public void testByteImage() {
		ImageProcessor ip1 = path1.openAsImage().getProcessor();
    	PixelPack pack = new PixelPack(ip1);
    	ImageProcessor ip2 = ip1.duplicate();
    	pack.copyToImageProcessor(ip2);
    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
	}
	
	@Test
	public void testShortImage() {
		ImageProcessor ip1 = path1.openAsImage().getProcessor().convertToShortProcessor();
		PixelPack pack = new PixelPack(ip1);
    	ImageProcessor ip2 = ip1.duplicate();
    	pack.copyToImageProcessor(ip2);
    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
	}
	
	@Test
	public void testFloatImage() {
		ImageProcessor ip1 = path1.openAsImage().getProcessor().convertToFloatProcessor();
		PixelPack pack = new PixelPack(ip1);
    	ImageProcessor ip2 = ip1.duplicate();
    	pack.copyToImageProcessor(ip2);
    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
	}
	
	@Test
	public void testColorImage() {
		ImageProcessor ip1 = path2.openAsImage().getProcessor();
		PixelPack pack = new PixelPack(ip1);
    	ImageProcessor ip2 = ip1.duplicate();
    	pack.copyToImageProcessor(ip2);
    	assertTrue(ImageTests.match(ip1, ip2, 1E-6));
	}

}
