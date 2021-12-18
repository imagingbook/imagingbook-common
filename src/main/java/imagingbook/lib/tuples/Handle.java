package imagingbook.lib.tuples;

/**
 * An elementary "handle" mechanism to facilitate indirect (two-stage)
 * references to objects.
 * The main purpose is to allow true "calls by reference", as shown in 
 * Example 1 below.
 * The second application is de-referencing of a tuple's elements
 * into named variables, as in Example 2.
 * 
 * @author WB
 *
 * @param <T> the element type of this handle
 */
public class Handle<T> {
	
	private T obj;
		
	public Handle(T obj) {
		this.obj = obj;
	}
	
	public T get() {
		return this.obj;
	}
	
	public void set(T obj) {
		this.obj = obj;
	}
	
	public static <T> Handle<T> of(T obj) {
		return new Handle<T>(obj);
	}
	
	// ---------------------------------------------
	
	private static void foo(Handle<Integer> h) {
		h.set(33);
	}
	
	// returns multiple values as a tuple
	private static Tuple2<Integer, String> bar() {
		return Tuple2.of(99, "bar");
	}

	
	public static void main(String[] args) {
		Handle<Integer> a = Handle.of(10);
		Handle<String>  s = Handle.of("prima!");
		
		System.out.println("a = " + a.get());
		
		// Example 1: call by reference (foo modifies the contents of a)
		foo(a);
		System.out.println("a = " + a.get());
		
		// Example 2: de-referencing the elements of a tuple
		bar().assignto(a, s);
		System.out.println("a = " + a.get());
		System.out.println("s = " + s.get());
		
	}


}
