package imagingbook.lib.tuples;

/**
 * A tuple with exactly 2 elements of arbitrary types.
 *
 * @param <T0> the type of element 0
 * @param <T1> the type of element 1
 */
public final class Tuple2<T0, T1> implements Tuple {
	
	public final T0 item0;	// anyone can access items but not change
	public final T1 item1;
	
	public Tuple2(T0 item0, T1 item1) {
		this.item0 = item0;
		this.item1 = item1;
	}
	
	// needed??
	public Tuple2(Tuple2<? extends T0, ? extends T1> entry) {
        this(entry.item0, entry.item1);
    }
	
	@Override
	public String toString() {
		return String.format("<%s,%s>", item0.toString(), item1.toString());
	}

	public static <T0, T1> Tuple2<T0, T1> of(T0 val0, T1 val1) {
		return new Tuple2<>(val0, val1);
	}
	
	// experimental: 
	
	public void assignto(Handle<T0> h0, Handle<T1> h1) {
		h0.set(item0);
		h1.set(item1);
	}
	
}
