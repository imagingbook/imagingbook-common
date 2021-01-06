package imagingbook.lib.filters;

import static imagingbook.lib.image.access.PixelPack.pack;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.lib.filter.linear.GaussianFilter;
import imagingbook.lib.ij.IjUtils;
import imagingbook.testutils.ImageTests;

public class GaussianFilterTest {
	
	static double SIGMA = 3.0;
	static float TOL = 1f;	// deviations +/-1 are possible due to rounding to integer images
	
	Path path1A = new imagingbook.DATA.images.Resources().getResourcePath("monastery-small.png");
	Path path1B = new imagingbook.DATA.images.Resources().getResourcePath("monastery-small-gauss3.png");
	
	Path path2A = new imagingbook.DATA.images.Resources().getResourcePath("clown.png");
	Path path2B = new imagingbook.DATA.images.Resources().getResourcePath("clown-gauss3.png");

	// -----------------------------------------------------------------------
	
	@Test
	public void testGaussianGray() {
		ImageProcessor ipA = IjUtils.openImage(path1A).getProcessor();
		ImageProcessor ipB = IjUtils.openImage(path1B).getProcessor();
		
		//new GaussianFilter(ipA, SIGMA).apply();
		new GaussianFilter(SIGMA).applyTo(pack(ipA));
		assertTrue(ImageTests.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testGaussianRgb() {
		ImageProcessor ipA = IjUtils.openImage(path2A).getProcessor();
		ImageProcessor ipB = IjUtils.openImage(path2B).getProcessor();
		
		new GaussianFilter(SIGMA).applyTo(pack(ipA));
		assertTrue(ImageTests.match(ipA, ipB, TOL));
	}
	
	// -----------------------------------------------------------------------
	
	@Test
	public void testGaussianSeparableGray() {
		ImageProcessor ipA = IjUtils.openImage(path1A).getProcessor();
		ImageProcessor ipB = IjUtils.openImage(path1B).getProcessor();
		
		new GaussianFilter(SIGMA).applyTo(pack(ipA));
		assertTrue(ImageTests.match(ipA, ipB, TOL));
	}

	@Test
	public void testGaussianSeparableRgb() {
		ImageProcessor ipA = IjUtils.openImage(path2A).getProcessor();
		ImageProcessor ipB = IjUtils.openImage(path2B).getProcessor();
		
		new GaussianFilter(SIGMA).applyTo(pack(ipA));
		assertTrue(ImageTests.match(ipA, ipB, TOL));
	}
}
