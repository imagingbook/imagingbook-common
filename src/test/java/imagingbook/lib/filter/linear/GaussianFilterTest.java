package imagingbook.lib.filter.linear;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.lib.util.resource.ResourceLocation;
import imagingbook.lib.util.resource.ResourceLocation.Resource;
import imagingbook.testutils.ImageTests;

public class GaussianFilterTest {
	
	static double SIGMA = 3.0;
	static float TOL = 1f;	// deviations +/-1 are possible due to rounding to integer images
	
	ResourceLocation loc = new imagingbook.DATA.images.Resources();
	
	Resource path1A = loc.getResource("monastery-small.png");
	Resource path1B = loc.getResource("monastery-small-gauss3.png");
	
	Resource path2A = loc.getResource("clown.png");
	Resource path2B = loc.getResource("clown-gauss3.png");

	// -----------------------------------------------------------------------
	
	@Test
	public void testGaussianGray() {
		ImageProcessor ipA = path1A.openAsImage().getProcessor();
		ImageProcessor ipB = path1B.openAsImage().getProcessor();
		
		//new GaussianFilter(ipA, SIGMA).apply();
		new GaussianFilter(SIGMA).applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testGaussianRgb() {
		ImageProcessor ipA = path2A.openAsImage().getProcessor();
		ImageProcessor ipB = path2B.openAsImage().getProcessor();
		
		new GaussianFilter(SIGMA).applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, TOL));
	}
	
	// -----------------------------------------------------------------------
	
	@Test
	public void testGaussianSeparableGray() {
		ImageProcessor ipA = path1A.openAsImage().getProcessor();
		ImageProcessor ipB = path1B.openAsImage().getProcessor();
		
		new GaussianFilter(SIGMA).applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, TOL));
	}

	@Test
	public void testGaussianSeparableRgb() {
		ImageProcessor ipA = path2A.openAsImage().getProcessor();
		ImageProcessor ipB = path2B.openAsImage().getProcessor();
		
		new GaussianFilter(SIGMA).applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, TOL));
	}
}
