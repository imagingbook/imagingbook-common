package imagingbook.lib.filters;

import static imagingbook.lib.ij.IjUtils.match;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.lib.image.access.OutOfBoundsStrategy;

public class LinearFilterTest {
	
	Path path1 = new imagingbook.DATA.images.Resources().getResourcePath("monastery-small.png");
	ImageProcessor ip1 = IjUtils.openImage(path1).getProcessor();
	
	Path path2 = new imagingbook.DATA.images.Resources().getResourcePath("monastery-small-filter3x3.png");
	ImageProcessor ip2 = IjUtils.openImage(path2).getProcessor();
	

	@Test
	public void testLinearFilterUnitKernel() {
		float[][] H = {
				{0, 0, 0},
				{0, 1, 0},
				{0, 0, 0}};
		ImageProcessor ip1f = ip1.duplicate();
		LinearFilter2D lf = new LinearFilter2D(new Kernel2D(H));
		lf.applyTo(ip1f);
		assertTrue(match(ip1f, ip1));

	}
	
	@Test
	public void testLinearFilter3x3() {
		float[][] H = {
				{1, 2, 1},
				{2, 4, 2},
				{1, 2, 1}};
		
		ImageProcessor ip1f = ip1.duplicate();
		LinearFilter2D lf = new LinearFilter2D(new Kernel2D(H));
		lf.setOutOfBoundsStrategy(OutOfBoundsStrategy.NEAREST_BORDER);
		lf.applyTo(ip1f);
		assertTrue(match(ip1f, ip2));
	}

}
