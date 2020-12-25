package imagingbook.lib.tuples;

/**
 * A tuple with exactly 3 elements of arbitrary types.
 *
 * @param <T0> the type of element 0
 * @param <T1> the type of element 1
 * @param <T2> the type of element 2
 */
public final class Tuple3<T0, T1, T2> implements Tuple {
	
	public final T0 f0;
	public final T1 f1;
	public final T2 f2;
	
	public Tuple3(T0 val0, T1 val1, T2 val2) {
		this.f0 = val0;
		this.f1 = val1;
		this.f2 = val2;
	}
	
	@Override
	public String toString() {
		return String.format("<%s,%s,%s>", f0.toString(), f1.toString(), f2.toString());
	}
	
}
