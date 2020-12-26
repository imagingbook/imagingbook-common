package imagingbook.pub.geometry.basic;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.pub.geometry.basic.Pnt2d.PntDouble;
import imagingbook.pub.geometry.basic.Pnt2d.PntInt;

// Testing point duplication
public class Pnt2dDuplicateTest {

	static double DELTA = 1E-6;
	
	@Test
	public void testDuplicateClass() {
		// duplicates are always of the same class as the original
		
		Pnt2d p1 = PntInt.from(3, 8);
		Assert.assertTrue(p1.duplicate() instanceof PntInt);
		Assert.assertTrue(p1.duplicate().getClass() == p1.getClass());
		
		Pnt2d p2 = PntDouble.from(3, 8);
		Assert.assertTrue(p2.duplicate() instanceof PntDouble);
		Assert.assertTrue(p2.duplicate().getClass() == p2.getClass());	
	}

	@Test
	public void testDuplicateEquality() {
		// int + int points
		Pnt2d p1i = PntInt.from(3, 8);
		Pnt2d p1d = PntDouble.from(3, 8);
		Pnt2d p2i = PntInt.from(-2, 7);
		Pnt2d p2d = PntDouble.from(-2, 7);

		Assert.assertTrue(p1i.equals(p1d));
		Assert.assertTrue(p1d.equals(p1i));
		Assert.assertTrue(p1i.equals(p1i.duplicate()));
		Assert.assertTrue(p1d.equals(p1d.duplicate()));
		
		Assert.assertFalse(p1i.equals(p2d));
		Assert.assertFalse(p2d.equals(p1i));
		Assert.assertFalse(p1i.equals(p2i.duplicate()));
		Assert.assertFalse(p2d.equals(p1d.duplicate()));	
	}
}
