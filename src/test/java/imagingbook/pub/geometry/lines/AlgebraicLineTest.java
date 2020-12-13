package imagingbook.pub.geometry.lines;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.pub.geometry.basic.Point;

public class AlgebraicLineTest {

	@Test
	public void test() {
		Point p1 = Point.create(30, 10);
		Point p2 = Point.create(200, 100);
		
		AlgebraicLine l12 = AlgebraicLine.create(p1, p2);		
		AlgebraicLine l21 = AlgebraicLine.create(p2, p1);
		
		Assert.assertEquals(0.0, l12.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, l12.getDistance(p2), 1E-6);
		
		Assert.assertEquals(0.0, l21.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, l21.getDistance(p2), 1E-6);
	}

}
