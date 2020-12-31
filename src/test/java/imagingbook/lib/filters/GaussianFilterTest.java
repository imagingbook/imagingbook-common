package imagingbook.lib.filters;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.testutils.ImageTests;

public class GaussianFilterTest {
	
	static double SIGMA = 3.0;
	
	Path path1A = new imagingbook.DATA.images.Resources().getResourcePath("monastery-small.png");
	Path path1B = new imagingbook.DATA.images.Resources().getResourcePath("monastery-small-gauss3.png");
	
	Path path2A = new imagingbook.DATA.images.Resources().getResourcePath("clown.png");
	Path path2B = new imagingbook.DATA.images.Resources().getResourcePath("clown-gauss3.png");

//	@Test
//	public void testGaussianGray() {
//		ImageProcessor ipA = IjUtils.openImage(path1A).getProcessor();
//		ImageProcessor ipB = IjUtils.openImage(path1B).getProcessor();
//		ImageProcessor ipAf = ipA.duplicate();
//		
//		GaussianFilter filter = new GaussianFilter(SIGMA);
//		filter.setOutOfBoundsStrategy(OutOfBoundsStrategy.NEAREST_BORDER);	// default
//		
//		filter.applyTo(ipAf);
//		assertTrue(ImageTests.match(ipAf, ipB, 1E-6));
//	}
//	
//	@Test
//	public void testGaussianRgb() {
//		ImageProcessor ipA = IjUtils.openImage(path2A).getProcessor();
//		ImageProcessor ipB = IjUtils.openImage(path2B).getProcessor();
//		ImageProcessor ipAf = ipA.duplicate();
//		
//		GaussianFilter filter = new GaussianFilter(SIGMA);
//		filter.setOutOfBoundsStrategy(OutOfBoundsStrategy.NEAREST_BORDER);	// default
//		
//		filter.applyTo(ipAf);
//		assertTrue(ImageTests.match(ipAf, ipB, 1E-6));
//	}
	
	// -----------------------------------------------------------------------
	
//	@Test
//	public void testGaussianSeparableGray() {
//		ImageProcessor ipA = IjUtils.openImage(path1A).getProcessor();
//		ImageProcessor ipB = IjUtils.openImage(path1B).getProcessor();
//		ImageProcessor ipAf = ipA.duplicate();
//		
//		GaussianFilterSeparable filter = new GaussianFilterSeparable(SIGMA);
//		filter.setOutOfBoundsStrategy(OutOfBoundsStrategy.NEAREST_BORDER);	// default
//		
//		filter.applyTo(ipAf);
//		assertTrue(ImageTests.match(ipAf, ipB, 1E-6));
//	}
//
//	@Test
//	public void testGaussianSeparableRgb() {
//		ImageProcessor ipA = IjUtils.openImage(path2A).getProcessor();
//		ImageProcessor ipB = IjUtils.openImage(path2B).getProcessor();
//		ImageProcessor ipAf = ipA.duplicate();
//		
//		GaussianFilterSeparable filter = new GaussianFilterSeparable(SIGMA);
//		filter.setOutOfBoundsStrategy(OutOfBoundsStrategy.NEAREST_BORDER);	// default
//		
//		filter.applyTo(ipAf);
//		assertTrue(ImageTests.match(ipAf, ipB, 1E-6));
//	}
}
