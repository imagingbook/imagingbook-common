package imagingbook.lib.random;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 * Randomly draws a set of k unique, non-null elements from a given array
 * with elements of type T, which may contain null elements.
 * The size of the draw (k) is fixed and must be specified at construction.
 * The resulting sets contain no duplicate elements.
 * 
 * @author WB
 *
 * @param <T> element type
 */
public class RandomDraw<T> {
	
	private final int k;
	private final Random rand;
	private final int[] idx;	// index array
	
	// -------------------------------------------------------------
	
	public RandomDraw(int k, Random rand) {
		this.k = k;
		this.rand = rand;
		this.idx = new int[k];
	}
	
	public RandomDraw(int k) {
		this(k, new Random());
	}
	
	// -------------------------------------------------------------
	
	public T[] drawFrom(T[] items) {	// WORKS
		drawRandomIndex(items);
		T[] draw = Arrays.copyOf(items, k);	// trick to create an array of generic type T
		for (int i = 0; i < k; i++) {
			draw[i] = items[idx[i]];
		}
		return draw;
	}
	
//	public T[] drawFrom(T[] items) {	// DOES NOT WORK!
//	drawRandomIndex(items);
//	@SuppressWarnings("unchecked")
//	T[] draw = (T[]) new Object[k];		// this creates an incompatible array!
//	for (int i = 0; i < k; i++) {
//		draw[i] = items[idx[i]];
//	}
//	return draw;
//}
	
//	@SuppressWarnings("unchecked")	
//	public T[] drawFrom(T[] items) { 	// WORKS TOO
//		drawRandomIndex(items);
//		List<T> theList = new ArrayList<>(items.length);
//		for (int i = 0; i < k; i++) {
//			theList.add(items[idx[i]]);
//		}
//		
//		return (T[]) theList.toArray();
//	}
	
//	@SuppressWarnings("unchecked")	
//	public T[] drawFrom(T[] items) { 	// WORKS TOO
//		drawRandomIndex(items);
//		List<T> theList = new ArrayList<>(items.length);
//		for (int i = 0; i < k; i++) {
//			theList.add(items[idx[i]]);
//		}
//		
//		return theList.toArray(Arrays.copyOf(items, k));
//	}
		
	
	private void drawRandomIndex(T[] items) {
//		Arrays.fill(idx, -1);	// clear idx array
		// perform k unique draws (all indexes must be different):
		for (int d = 0; d < k; d++) {
			int i = rand.nextInt(items.length);	// next index
			while (items[i] == null || wasPickedBefore(d, i)) {
				i = rand.nextInt(items.length);
			}
			
			idx[d] = i;
		}
	}

	// Checks if idx[0],...,idx[d-1] contains i.
	private boolean wasPickedBefore(int d, int i) {
		for (int j = 0; j < d; j++) {
			if (i == idx[j]) {
				return true;
			}
		}	
		return false;
	}
	
	/**
	 * Checks for duplicate ("equals") elements in the result (for testing only).
	 * @param arr array of objects
	 * @return true if any object is null or contained more than once
	 */
	public boolean hasDuplicates(T[] arr) {
		HashSet<T> set = new HashSet<>();
		for (T elem : arr) {
			if (elem == null) {
//				throw new RuntimeException("null found in " + Arrays.toString(arr));
				return true;
			}
			if (set.contains(elem)) {
				return true;
			}
			set.add(elem);
		}
		
		return false;
	}
	
	// --------------------------------------------------------------------
	
//	public static void main(String[] args) {
//		Integer[] numbers = { null, 1, 2, null, 3, 4, 5, 6, 7, null, null, null, 8, 9, 10 , null};
////		Integer[] numbers = { 1,2,3,4,5,6,7,8};
//		int N = 1000000;
//		RandomDraw<Integer> rd = new RandomDraw<>(2);
//		
////		Object[] x = rd.drawFrom(numbers);
////		System.out.println("x = " + x.getClass());
////		for(Object o : x) {
////			System.out.println(o.getClass());
////		}
//		
//		for (int i = 0; i < N; i++) {
//			Integer[] draw = rd.drawFrom(numbers);
////			System.out.println(Arrays.toString(draw));
//			if (rd.hasDuplicates(draw)) {
//				throw new RuntimeException("duplicates found in " + Arrays.toString(draw));
//			}
//		}
//		System.out.println("done " + N);
//	}
	
}
