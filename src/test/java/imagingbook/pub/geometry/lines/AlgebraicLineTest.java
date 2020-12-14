package imagingbook.pub.geometry.lines;

import org.junit.Assert;
import org.junit.Test;

import ij.ImagePlus;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.basic.Point;

public class AlgebraicLineTest {
	
	static Point p1 = Point.create(30, 10);
	static Point p2 = Point.create(200, 100);
	static Point p3 = Point.create(90, 40);

	@Test
	public void test1() {
		AlgebraicLine l12 = AlgebraicLine.fromPoints(p1, p2);		
		AlgebraicLine l21 = AlgebraicLine.fromPoints(p2, p1);
		
		Assert.assertEquals(0.0, l12.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, l12.getDistance(p2), 1E-6);
		
		Assert.assertEquals(0.0, l21.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, l21.getDistance(p2), 1E-6);
	}
	
	@Test
	public void test2() {
		AlgebraicLine l12 = AlgebraicLine.fromPoints(p1, p2);
		Point x0 = l12.getClosestLinePoint(p3);
		Assert.assertEquals(0.0, l12.getDistance(x0), 1E-6);						// x0 is actually ON the line
		Assert.assertEquals(p3.distance(x0), Math.abs(l12.getDistance(p3)), 1E-6);	// distance (p3,x0) is shortest 
	}

	@Test
	public void test3() {
		AlgebraicLine l12 = AlgebraicLine.fromPoints(p1, p2);
		Point x0 = l12.getClosestLinePoint(p3);
		
		ImageProcessor ip = new ColorProcessor(250, 200);
		new ImagePlus("test3", ip).show();

	}

}
