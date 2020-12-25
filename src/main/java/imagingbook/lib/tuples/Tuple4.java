package imagingbook.lib.tuples;

/**
 * A tuple with exactly 4 elements of arbitrary types.
 *
 * @param <T0> the type of element 0
 * @param <T1> the type of element 1
 * @param <T2> the type of element 2
 * @param <T3> the type of element 3
 */
public final class Tuple4<T0, T1, T2, T3> implements Tuple {
	
	public final T0 f0;
	public final T1 f1;
	public final T2 f2;
	public final T3 f3;
	
	public Tuple4(T0 val0, T1 val1, T2 val2, T3 val3) {
		this.f0 = val0;
		this.f1 = val1;
		this.f2 = val2;
		this.f3 = val3;
	}
	
	@Override
	public String toString() {
		return String.format("<%s,%s,%s,%s>", f0.toString(), f1.toString(), f2.toString(), f3.toString());
	}

	public static <T0, T1, T2, T3> Tuple4<T0, T1, T2, T3> of(T0 val0, T1 val1, T2 val2, T3 val3) {
		return new Tuple4<>(val0, val1, val2, val3);
	}
	
}
