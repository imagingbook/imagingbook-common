package imagingbook.pub.color.filters;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.math.VectorNorm.NormType;
import imagingbook.testutils.ImageTests;

public class ColorMedianFilterTest {

	@Test
	public void testScalarMedianFilter() {
		Path pathA = new imagingbook.DATA.images.Resources().getResourcePath("clown.png");
		Path pathB = new imagingbook.DATA.images.Resources().getResourcePath("clown-median-scalar-3.png");
		ImageProcessor ipA = IjUtils.openImage(pathA).getProcessor();
		ImageProcessor ipB = IjUtils.openImage(pathB).getProcessor();
		
		ScalarMedianFilter.Parameters params = new ScalarMedianFilter.Parameters();
		params.radius = 3.0;
		params.obs = OutOfBoundsStrategy.NEAREST_BORDER;
		
		ScalarMedianFilter filter = new ScalarMedianFilter(params);
		filter.applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, 1E-6));
	}
	
	@Test
	public void testVectorMedianFilter() {
		Path pathA = new imagingbook.DATA.images.Resources().getResourcePath("clown.png");
		Path pathB = new imagingbook.DATA.images.Resources().getResourcePath("clown-median-vector-3-L1.png");
		ImageProcessor ipA = IjUtils.openImage(pathA).getProcessor();
		ImageProcessor ipB = IjUtils.openImage(pathB).getProcessor();
		
		VectorMedianFilter.Parameters params = new VectorMedianFilter.Parameters();
		params.radius = 3.0;
		params.obs = OutOfBoundsStrategy.NEAREST_BORDER;
		params.distanceNorm = NormType.L1;
		
		VectorMedianFilter filter = new VectorMedianFilter(params);
		filter.applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, 1E-6));
	}
	
	@Test
	public void testVectorMedianFilterSharpen() {
		Path pathA = new imagingbook.DATA.images.Resources().getResourcePath("clown.png");
		Path pathB = new imagingbook.DATA.images.Resources().getResourcePath("clown-median-vectorsharpen-3-L1.png");
		ImageProcessor ipA = IjUtils.openImage(pathA).getProcessor();
		ImageProcessor ipB = IjUtils.openImage(pathB).getProcessor();
		
		VectorMedianFilterSharpen.Parameters params = new VectorMedianFilterSharpen.Parameters();
		params.radius = 3.0;
		params.obs = OutOfBoundsStrategy.NEAREST_BORDER;
		params.distanceNorm = NormType.L1;
		params.sharpen = 0.5;
		params.threshold = 0.0;	
		
		VectorMedianFilterSharpen filter = new VectorMedianFilterSharpen(params);
		filter.applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, 1E-6));
	}

}
