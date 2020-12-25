package imagingbook.lib.tuples;

/**
 * Elementary implementation of tuples, which are ordered sequences
 * of elements of arbitrary types.
 * Tuples are generally immutable.
 * Their main use is to get methods return multiple values
 * with maximum compile-time safety.
 * A concrete tuple class is defined for
 * each "arity", e.g., {@link Tuple2} for 2 elements.
 * Currently tuple classes for 2, 3 and 4 elements are defined.
 * A tuple may be instantiated using either the class constructor 
 * <pre>new Tuple2&lt;Integer, String&gt;(10, "Foo")</pre>
 * or the associated static method 
 * <pre>Tuple2.of(10, "Foo")</pre>
 * Tuple elements can be accessed (read-only) only by the associated
 * field names, e.g. {@code f0}, {@code f1} for {@link Tuple2}.
 * There are no getter methods.
 * @author WB
 *
 */
public interface Tuple {
	
	public static <T0, T1> Tuple2<T0, T1> of(T0 val0, T1 val1) {
		return new Tuple2<>(val0, val1);
	}
	
	public static <T0, T1, T2> Tuple3<T0, T1, T2> of(T0 val0, T1 val1, T2 val2) {
		return new Tuple3<>(val0, val1, val2);
	}
	
	public static <T0, T1, T2, T3> Tuple4<T0, T1, T2, T3> of(T0 val0, T1 val1, T2 val2, T3 val3) {
		return new Tuple4<>(val0, val1, val2, val3);
	}
	
	// ------------------------------------------------------------------------
	
	public static void main(String[] args) {
		Tuple2<Integer, String> tA = new Tuple2<>(10, "Foo");
		Tuple tB = new Tuple2<Integer, String>(-3, "Bar");
		Tuple tC = Tuple.of(17, "Kaputnik");
		System.out.println("tA = " + tA.toString());
		System.out.println("tB = " + tB.toString());
		System.out.println("tC = " + tC.toString());
		System.out.println("tA.f0 = " + tA.f0.toString());
		System.out.println("tA.f1 = " + tA.f1.toString());
	}
}


