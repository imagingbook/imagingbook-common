package imagingbook.lib.util.progress2;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.Arrays;

public class ReportsProgressExample {
	
	// the root class
	public static abstract class A implements ReportsProgress {
		int P = 5;
		int i = 2;
		
		abstract void runB();
		
		void runA() {
			for (i = 0; i < P; i++) {
				runB();
			}
		}
		
		public long[] reportProgress() {
			System.out.println("reportProgress A");
			return new long[] {i, P};
		}
		
		@Override
		public String toString() {
			return("class A");
		}
	}
	
	// ---------------------------------------------------------------
	
	public static abstract class B extends A {
		int Q = 3;
		int j = 1;
		abstract void runC();
		
		@Override
		void runB() {
			for (j = 0; j < Q; j++) {
				runC();
			}
		}
		
		public long[] reportProgress() {
			System.out.println("reportProgress B");
			return new long[] {j, Q};
		}
		
		@Override
		public String toString() {
			return("class B");
		}
	}
	
	// ---------------------------------------------------------------
	
	// the concrete terminal class
	public static class C extends B {
		int R = 2;
		int k = 1;
		
		void runC() {
			for (k = 0; k < R; k++) {
			}
		}
		
		public long[] reportProgress() {
			System.out.println("reportProgress C");
			return new long[] {k, R};
		}
		
		@Override
		public String toString() {
			return("class C");
		}
		
		public void foo() {
			try {
				Lookup lu = MethodHandles.lookup();
				MethodHandle h = lu.findSpecial(B.class, "toString", MethodType.methodType(String.class), A.class);
				//h.invoke(this);
			} catch (NoSuchMethodException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	// ---------------------------------------------------------------
	
	
	public static void main(String[] args) throws Throwable {
		C instC = new C();
		B instB = (B) instC;
		
//		System.out.println("progress = " + Arrays.toString(instC.reportProgress()));
//		System.out.println("progress = " + Arrays.toString(instB.reportProgress()));
		
		instC.foo();

	}
	
	

}
