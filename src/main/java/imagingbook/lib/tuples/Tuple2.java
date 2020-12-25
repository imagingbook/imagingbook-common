package imagingbook.lib.tuples;

import imagingbook.lib.handles.Handle;

/**
 * A tuple with exactly 2 elements of arbitrary types.
 *
 * @param <T0> the type of element 0
 * @param <T1> the type of element 1
 */
public final class Tuple2<T0, T1> implements Tuple {
	
	public final T0 f0;
	public final T1 f1;
	
	public Tuple2(T0 val0, T1 val1) {
		this.f0 = val0;
		this.f1 = val1;
	}
	
	@Override
	public String toString() {
		return String.format("<%s,%s>", f0.toString(), f1.toString());
	}

	public static <T0, T1> Tuple2<T0, T1> of(T0 val0, T1 val1) {
		return new Tuple2<>(val0, val1);
	}
	
	// experimental: 
	
	public void assignto(Handle<T0> h0, Handle<T1> h1) {
		h0.set(f0);
		h1.set(f1);
	}
	
}
