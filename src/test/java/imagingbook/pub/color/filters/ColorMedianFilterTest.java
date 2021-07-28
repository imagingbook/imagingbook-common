package imagingbook.pub.color.filters;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.math.VectorNorm.NormType;
import imagingbook.lib.util.resource.ResourceLocation;
import imagingbook.lib.util.resource.ResourceLocation.Resource;
import imagingbook.testutils.ImageTests;

public class ColorMedianFilterTest {
	
	private final ResourceLocation loc = new imagingbook.DATA.images.RLOC();

	@Test
	public void testScalarMedianFilter() {
		
		Resource pathA = loc.getResource("clown.png");
		Resource pathB = loc.getResource("clown-median-scalar-3.png");
		ImageProcessor ipA = pathA.openAsImage().getProcessor();
		ImageProcessor ipB = pathB.openAsImage().getProcessor();
		
		ScalarMedianFilter.Parameters params = new ScalarMedianFilter.Parameters();
		params.radius = 3.0;
		params.obs = OutOfBoundsStrategy.NEAREST_BORDER;
		
		ScalarMedianFilter filter = new ScalarMedianFilter(params);
		filter.applyTo(ipA);
		assertTrue(ImageTests.match(ipA, ipB, 1E-6));
	}
	
	@Test
	public void testVectorMedianFilter() {
		Resource pathA = loc.getResource("clown.png");
		Resource pathB = loc.getResource("clown-median-vector-3-L1.png");
		ImageProcessor ipA = pathA.openAsImage().getProcessor();
		ImageProcessor ipB = pathB.openAsImage().getProcessor();
		
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
		Resource pathA = loc.getResource("clown.png");
		Resource pathB = loc.getResource("clown-median-vectorsharpen-3-L1.png");
		ImageProcessor ipA = pathA.openAsImage().getProcessor();
		ImageProcessor ipB = pathB.openAsImage().getProcessor();
		
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
