package imagingbook.lib.random;

import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class RandomDrawTest {

	@Test
	public void test1() {
		Integer[] numbers = { null, 1, 2, null, 3, 4, 5, 6, 7, null, null, null, 8, 9, 10 , null};
		int N = 1000000;
		
		Random rg = new Random(17);
		RandomDraw<Integer> rd = new RandomDraw<>(2, rg);
		
		for (int i = 0; i < N; i++) {
			Integer[] draw = rd.drawFrom(numbers);
			assertFalse("duplicates found in " + Arrays.toString(draw), rd.hasDuplicates(draw));
		}
		
	}

}
