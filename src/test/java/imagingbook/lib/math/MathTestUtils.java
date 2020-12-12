package imagingbook.lib.math;

import org.junit.Assert;

public class MathTestUtils {
	
	// utility methods ---------------------------------------------------------------

	static void assertArrayEquals(double[][] expecteds, double[][] actuals, double delta) {
		Assert.assertEquals(expecteds.length, actuals.length);
		for (int i = 0; i < expecteds.length; i++) {
			Assert.assertArrayEquals(expecteds[i], actuals[i], delta);
		}
	}

	static void assertArrayEquals(float[][] expecteds, float[][] actuals, double delta) {
		Assert.assertEquals(expecteds.length, actuals.length);
		for (int i = 0; i < expecteds.length; i++) {
			Assert.assertArrayEquals(expecteds[i], actuals[i], (float) delta);
		}
	}

	static float TOLERANCE = 1E-6f;

}
