package imagingbook.lib.math;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArithmeticTest {

	@Test
	public void testModDoubleDouble() {
		double D = 1E-9;
		//System.out.println(Arithmetic.mod(-3.7, -2));
		assertEquals( 1.7, Arithmetic.mod( 3.7,  2), D);
		assertEquals( 0.3, Arithmetic.mod(-3.7,  2), D);
		assertEquals(-0.3, Arithmetic.mod( 3.7, -2), D);
		assertEquals(-1.7, Arithmetic.mod(-3.7, -2), D);
	}

	@Test
	public void testEqualsDoubleDouble() {
		assertFalse(Arithmetic.equals(-0.7, 0.8));
		assertTrue(Arithmetic.equals(-0.7, -0.7));
		assertTrue(Arithmetic.equals(1.0, 1.0 + Arithmetic.EPSILON_DOUBLE / 2));
		assertTrue(Arithmetic.equals(1.0, 1.0 - Arithmetic.EPSILON_DOUBLE / 2));
	}

	@Test
	public void testEqualsFloatFloat() {
		assertFalse(Arithmetic.equals(-0.7f, 0.8f));
		assertTrue(Arithmetic.equals(-0.7f, -0.7f));
		assertTrue(Arithmetic.equals(1.0f, 1.0f + Arithmetic.EPSILON_FLOAT / 2));
		assertTrue(Arithmetic.equals(1.0f, 1.0f - Arithmetic.EPSILON_FLOAT / 2));
	}

}
