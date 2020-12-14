package imagingbook.pub.hough.lines;


import org.junit.Assert;
import org.junit.Test;

import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.geometry.lines.AlgebraicLine;

public class HoughLineTest {
	
	static Point p1 = Point.create(30, 10);
	static Point p2 = Point.create(200, 100);
	static Point p3 = Point.create(90, 40);
	
	static Point pRef = Point.create(70, 50);

	@Test
	public void test1() {
		HoughLine L12 = HoughLine.fromPoints(p1, p2, pRef, 2);
		HoughLine L21 = HoughLine.fromPoints(p2, p1, pRef, 2);
		
		Assert.assertEquals(0.0, L12.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L12.getDistance(p2), 1E-6);
		
		Assert.assertEquals(0.0, L21.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L21.getDistance(p2), 1E-6);
	}
	
	@Test
	public void test2() {
		HoughLine l12 = HoughLine.fromPoints(p1, p2, pRef, 2);
		Point x0 = l12.getClosestLinePoint(p3);
		Assert.assertEquals(0.0, l12.getDistance(x0), 1E-6);						// x0 is actually ON the line
		Assert.assertEquals(p3.distance(x0), Math.abs(l12.getDistance(p3)), 1E-6);	// distance (p3,x0) is shortest 
	}
	
	@Test
	public void test3() {
		// lA, lH1 lH2 must be the same lines:
		AlgebraicLine LA = AlgebraicLine.fromPoints(p1, p2);
		HoughLine L1 = HoughLine.fromPoints(p1, p2, pRef, 2);
		HoughLine L2 = new HoughLine(LA, pRef.getX(), pRef.getY(), 2);
		
		Assert.assertEquals(0.0, LA.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, LA.getDistance(p2), 1E-6);
		
		Assert.assertEquals(0.0, L1.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L1.getDistance(p2), 1E-6);
		
		Assert.assertEquals(0.0, L2.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L2.getDistance(p2), 1E-6);
	}
	
	@Test
	public void test4() {
		// create a HoughLine from two points
		HoughLine L1 = HoughLine.fromPoints(p1, p2, pRef, 2);
		// check if both points are on the line
		Assert.assertEquals(0.0, L1.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L1.getDistance(p2), 1E-6);
		
		// create a duplicate HoughLine from L1's angle and radius
		HoughLine L2 = new HoughLine(L1.getAngle(), L1.getRadius(), pRef.getX(), pRef.getY(), 2);
		// check if the two points are on this line too
		Assert.assertEquals(0.0, L2.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L2.getDistance(p2), 1E-6);
		
		// create a duplicate HoughLine directly from L1 (using the same reference point)
		HoughLine L3 = new HoughLine(L1, pRef.getX(), pRef.getY(), 2);
		// check if the two points are on this line too
		Assert.assertEquals(0.0, L3.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L3.getDistance(p2), 1E-6);
		
		// create a duplicate HoughLine directly from L1 (using a different reference point)
		HoughLine L4 = new HoughLine(L1, pRef.getX() - 10, pRef.getY() + 15, 2);
		// check if the two points are on this line too
		Assert.assertEquals(0.0, L4.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L4.getDistance(p2), 1E-6);
	}

}
