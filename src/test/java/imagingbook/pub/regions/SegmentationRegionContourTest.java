package imagingbook.pub.regions;

import static imagingbook.pub.regions.NeighborhoodType.N4;
import static imagingbook.pub.regions.NeighborhoodType.N8;

import java.nio.file.Path;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.DATA.images.Resources;
import imagingbook.lib.ij.IjUtils;
import imagingbook.pub.regions.BinaryRegionSegmentation.BinaryRegion;

public class SegmentationRegionContourTest {
	
	@Test
	public void testSmallN4() {
		// -----------------------------------------------------------------
		String ImgName = "segmentation-small.png";
		NeighborhoodType NHT = N4;
		int RegionCount = 26;
		int LargestRegionSize = 1000;
		int OuterContourLength = 168;
		int FirstRegionWithHole = 0;
		int FirstInnerContourLength = 108;
		// -----------------------------------------------------------------
		
		SegmentationRegionContour segmentation = 
				new SegmentationRegionContour(openImage(ImgName), NHT);
		
		// check region count:
		List<BinaryRegion> regions = segmentation.getRegions(true);
		Assert.assertNotNull(regions);
		Assert.assertFalse(regions.isEmpty());
		Assert.assertEquals(RegionCount, regions.size());
		
		// check largest region's size:
		BinaryRegion r0 = regions.get(0);	// the largest region
		Assert.assertEquals(LargestRegionSize, r0.getSize());
		
		// check largest regions outer contour length:
		Contour.Outer outerContour = r0.getOuterContour();
		Assert.assertNotNull(outerContour);
		Assert.assertEquals(OuterContourLength, outerContour.getLength());
		//System.out.println("length=" + outerContour.getLength());
		
		int k = findFirstRegionWithHole(regions);
		Assert.assertEquals(FirstRegionWithHole, k);
		if (k >= 0) {
			List<Contour.Inner> ics = regions.get(k).getInnerContours();
			Assert.assertFalse(ics.isEmpty());
			int n = ics.get(0).getLength();
			Assert.assertEquals(FirstInnerContourLength, n);
		}
	}
	
	@Test
	public void testSmallN8() {
		// -----------------------------------------------------------------
		String ImgName = "segmentation-small.png";
		NeighborhoodType NHT = N8;
		int RegionCount = 9;
		int LargestRegionSize = 1000;
		int OuterContourLength = 159;
		int FirstRegionWithHole = 0;
		int FirstInnerContourLength = 99;
		// -----------------------------------------------------------------
		
		SegmentationRegionContour segmentation = 
				new SegmentationRegionContour(openImage(ImgName), NHT);
		
		// check region count:
		List<BinaryRegion> regions = segmentation.getRegions(true);
		Assert.assertNotNull(regions);
		Assert.assertFalse(regions.isEmpty());
		Assert.assertEquals(RegionCount, regions.size());
		
		// check largest region's size:
		BinaryRegion r0 = regions.get(0);	// the largest region
		Assert.assertEquals(LargestRegionSize, r0.getSize());
		
		// check largest regions outer contour length:
		Contour.Outer outerContour = r0.getOuterContour();
		Assert.assertNotNull(outerContour);
		Assert.assertEquals(OuterContourLength, outerContour.getLength());
		//System.out.println("length=" + outerContour.getLength());
		
		int k = findFirstRegionWithHole(regions);
		Assert.assertEquals(FirstRegionWithHole, k);
		if (k >= 0) {
			List<Contour.Inner> ics = regions.get(k).getInnerContours();
			Assert.assertFalse(ics.isEmpty());
			int n = ics.get(0).getLength();
			Assert.assertEquals(FirstInnerContourLength, n);
		}
	}
	
	@Test
	public void testMedN4() {
		// -----------------------------------------------------------------
		String ImgName = "segmentation-med.png";
		NeighborhoodType NHT = N4;
		int RegionCount = 88;
		int LargestRegionSize = 16352;
		int OuterContourLength = 686;
		int FirstRegionWithHole = 0;
		int FirstInnerContourLength = 434;
		// -----------------------------------------------------------------
		
		SegmentationRegionContour segmentation = 
				new SegmentationRegionContour(openImage(ImgName), NHT);
		
		// check region count:
		List<BinaryRegion> regions = segmentation.getRegions(true);
		Assert.assertNotNull(regions);
		Assert.assertFalse(regions.isEmpty());
		Assert.assertEquals(RegionCount, regions.size());
		
		// check largest region's size:
		BinaryRegion r0 = regions.get(0);	// the largest region
		Assert.assertEquals(LargestRegionSize, r0.getSize());
		
		// check largest regions outer contour length:
		Contour.Outer outerContour = r0.getOuterContour();
		Assert.assertNotNull(outerContour);
		Assert.assertEquals(OuterContourLength, outerContour.getLength());
		//System.out.println("length=" + outerContour.getLength());
		
		int k = findFirstRegionWithHole(regions);
		Assert.assertEquals(FirstRegionWithHole, k);
		if (k >= 0) {
			List<Contour.Inner> ics = regions.get(k).getInnerContours();
			Assert.assertFalse(ics.isEmpty());
			int n = ics.get(0).getLength();
			Assert.assertEquals(FirstInnerContourLength, n);
		}
	}
	
	@Test
	public void testMedN8() {
		// -----------------------------------------------------------------
		String ImgName = "segmentation-med.png";
		NeighborhoodType NHT = N8;
		int RegionCount = 9;
		int LargestRegionSize = 16352;
		int OuterContourLength = 650;
		int FirstRegionWithHole = 0;
		int FirstInnerContourLength = 395;
		// -----------------------------------------------------------------
		
		SegmentationRegionContour segmentation = 
				new SegmentationRegionContour(openImage(ImgName), NHT);
		
		// check region count:
		List<BinaryRegion> regions = segmentation.getRegions(true);
		Assert.assertNotNull(regions);
		Assert.assertFalse(regions.isEmpty());
		Assert.assertEquals(RegionCount, regions.size());
		
		// check largest region's size:
		BinaryRegion r0 = regions.get(0);	// the largest region
		Assert.assertEquals(LargestRegionSize, r0.getSize());
		
		// check largest regions outer contour length:
		Contour.Outer outerContour = r0.getOuterContour();
		Assert.assertNotNull(outerContour);
		Assert.assertEquals(OuterContourLength, outerContour.getLength());
		//System.out.println("length=" + outerContour.getLength());
		
		int k = findFirstRegionWithHole(regions);
		Assert.assertEquals(FirstRegionWithHole, k);
		if (k >= 0) {
			List<Contour.Inner> ics = regions.get(k).getInnerContours();
			Assert.assertFalse(ics.isEmpty());
			int n = ics.get(0).getLength();
			Assert.assertEquals(FirstInnerContourLength, n);
		}
	}
	
	// --------------------------------------------------------------------------------------------
	
	private static ByteProcessor openImage(String imgName) {
		Path path = new Resources().getResourcePath(imgName);
		return (ByteProcessor) IjUtils.openImage(path).getProcessor();
	}
	
	private static int findFirstRegionWithHole(List<BinaryRegion> regions) {
		int i = 0;
		for (BinaryRegion r : regions) {
			List<Contour.Inner> ics = r.getInnerContours();
			if (ics != null && !ics.isEmpty()) {
				return i;
			}
			i = i + 1;
		}		
		return -1;
	}

}
