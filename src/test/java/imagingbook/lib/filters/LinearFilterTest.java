package imagingbook.lib.filters;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.lib.filter.kernel.Kernel2D;
import imagingbook.lib.filtersOBSOLETE.LinearFilter2D;
import imagingbook.lib.ij.IjUtils;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.testutils.ImageTests;

public class LinearFilterTest {
	
	static float[][] H1 = {
			{0, 0, 0},
			{0, 1, 0},
			{0, 0, 0}};
	
	static float[][] H2 = {
			{1, 2, 1},
			{2, 4, 2},
			{1, 2, 1}};
	
	Path path1A = new imagingbook.DATA.images.Resources().getResourcePath("monastery-small.png");
	Path path1B = new imagingbook.DATA.images.Resources().getResourcePath("monastery-small-filter3x3.png");
	
	Path path2A = new imagingbook.DATA.images.Resources().getResourcePath("clown.png");
	Path path2B = new imagingbook.DATA.images.Resources().getResourcePath("clown-filter3x3.png");
	
	@Test
	public void testLinearFilterUnitKernel() {
		ImageProcessor ipA = IjUtils.openImage(path1A).getProcessor();
		float[][] H = H1;
		ImageProcessor ipAf = ipA.duplicate();
		LinearFilter2D lf = new LinearFilter2D(new Kernel2D(H));
		lf.applyTo(ipAf);
		assertTrue(ImageTests.match(ipAf, ipA, 1E-6));
	}
	
	@Test
	public void testLinearFilter3x3gray() {
		ImageProcessor ipA = IjUtils.openImage(path1A).getProcessor();
		ImageProcessor ipB = IjUtils.openImage(path1B).getProcessor();
		float[][] H = H2;
		ImageProcessor ipAf = ipA.duplicate();
		LinearFilter2D lf = new LinearFilter2D(new Kernel2D(H));
		lf.setOutOfBoundsStrategy(OutOfBoundsStrategy.NEAREST_BORDER);
		lf.applyTo(ipAf);
		assertTrue(ImageTests.match(ipAf, ipB, 1E-6));
	}
	
	@Test
	public void testLinearFilter3x3float() {
		ImageProcessor ipA = IjUtils.openImage(path1A).getProcessor();
		ImageProcessor ipB = IjUtils.openImage(path1B).getProcessor();
		float[][] H = H2;
		ImageProcessor ipAf = ipA.convertToFloatProcessor();
		LinearFilter2D lf = new LinearFilter2D(new Kernel2D(H));
		lf.setOutOfBoundsStrategy(OutOfBoundsStrategy.NEAREST_BORDER);
		lf.applyTo(ipAf);
		assertTrue(ImageTests.match(ipAf, ipB.convertToFloatProcessor(), 0.5f));
	}
	
	@Test
	public void testLinearFilter3x3rgb() {
		ImageProcessor ipA = IjUtils.openImage(path2A).getProcessor();
		ImageProcessor ipB = IjUtils.openImage(path2B).getProcessor();
		float[][] H = H2;
		
		ImageProcessor ipAf = ipA.duplicate();
		LinearFilter2D lf = new LinearFilter2D(new Kernel2D(H));
		lf.setOutOfBoundsStrategy(OutOfBoundsStrategy.NEAREST_BORDER);
		lf.applyTo(ipAf);
		assertTrue(ImageTests.match(ipAf, ipB, 1E-6));
	}

}