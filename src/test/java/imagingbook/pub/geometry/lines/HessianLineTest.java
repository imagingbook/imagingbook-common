package imagingbook.pub.geometry.lines;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.pub.geometry.basic.Point;

public class HessianLineTest {

	@Test
	public void test1() {
		Point p1 = Point.create(30, 10);
		Point p2 = Point.create(200, 100);
		
		HessianLine l12 = HessianLine.create(p1, p2);		
		HessianLine l21 = HessianLine.create(p2, p1);
		
		Assert.assertEquals(0.0, l12.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, l12.getDistance(p2), 1E-6);
		
		Assert.assertEquals(0.0, l21.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, l21.getDistance(p2), 1E-6);
	}
	
	@Test
	public void test2() {
		double angle = 0.2;
		double radius = 80;
		HessianLine hl1 = HessianLine.create(angle, radius);
		HessianLine hl2 = new HessianLine(hl1);
		Assert.assertEquals(angle, hl2.getAngle(), 1E-6);
		Assert.assertEquals(radius, hl2.getRadius(), 1E-6);
	}

}
