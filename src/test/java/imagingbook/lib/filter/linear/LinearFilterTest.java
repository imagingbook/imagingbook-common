package imagingbook.lib.filter.linear;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.util.resource.ResourceLocation;
import imagingbook.lib.util.resource.ResourceLocation.Resource;
import imagingbook.testutils.ImageTests;

public class LinearFilterTest {
	
	static OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NEAREST_BORDER;
	
	static float[][] H1 = {
			{0, 0, 0},
			{0, 1, 0},
			{0, 0, 0}};
	
	static float[][] H2 = {
			{1, 2, 1},
			{2, 4, 2},
			{1, 2, 1}};
	
	ResourceLocation loc = new imagingbook.DATA.images.RLOC();
	
	Resource path1A = loc.getResource("monastery-small.png");
	Resource path1B = loc.getResource("monastery-small-filter3x3.png");
	
	Resource path2A = loc.getResource("clown.png");
	Resource path2B = loc.getResource("clown-filter3x3.png");
	
	@Test
	public void testLinearFilterUnitKernel() {
		ImageProcessor ipA = path1A.openAsImage().getProcessor();
		float[][] H = H1;
		ImageProcessor ipAf = ipA.duplicate();
		new LinearFilter(new Kernel2D(H)).applyTo(ipAf, OBS);
		assertTrue(ImageTests.match(ipAf, ipA, 1E-6));
	}
	
	@Test
	public void testLinearFilter3x3gray() {
		ImageProcessor ipA = path1A.openAsImage().getProcessor();
		ImageProcessor ipB = path1B.openAsImage().getProcessor();
		float[][] H = H2;
		new LinearFilter(new Kernel2D(H)).applyTo(ipA, OBS);
		assertTrue(ImageTests.match(ipA, ipB, 1E-6));
	}
	
	@Test
	public void testLinearFilter3x3float() {
		ImageProcessor ipA = path1A.openAsImage().getProcessor();
		ImageProcessor ipB = path1B.openAsImage().getProcessor();
		float[][] H = H2;
		ImageProcessor ipAf = ipA.convertToFloatProcessor();
		new LinearFilter(new Kernel2D(H)).applyTo(ipAf, OBS);
		assertTrue(ImageTests.match(ipAf, ipB.convertToFloatProcessor(), 0.5f));
	}
	
	@Test
	public void testLinearFilter3x3rgb() {
		ImageProcessor ipA = path2A.openAsImage().getProcessor();
		ImageProcessor ipB = path2B.openAsImage().getProcessor();
		float[][] H = H2;
		
		ImageProcessor ipAf = ipA.duplicate();
		new LinearFilter(new Kernel2D(H)).applyTo(ipAf, OBS);
		assertTrue(ImageTests.match(ipAf, ipB, 1E-6));
	}

}
