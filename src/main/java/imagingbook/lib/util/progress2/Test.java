package imagingbook.lib.util.progress2;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * https://stackoverflow.com/a/15674467
 *
 */

public class Test extends Base {
	public static void main(String[] args) throws Throwable {
		
		MethodHandle h0 = MethodHandles.lookup().findSpecial(Test.class, "toString",
				MethodType.methodType(String.class), Test.class);
		
		MethodHandle h1 = MethodHandles.lookup().findSpecial(Base.class, "toString",
				MethodType.methodType(String.class), Test.class);
		
		MethodHandle h2 = MethodHandles.lookup().findSpecial(Object.class, "toString",
				MethodType.methodType(String.class), Test.class);
		
		System.out.println(h0.invoke(new Test()));   // outputs Base
		System.out.println(h1.invoke(new Test()));   // outputs Base
		System.out.println(h2.invoke(new Test()));   // outputs Base
		
		System.out.println(new Test().toString());   // outputs Base
	}

	@Override
	public String toString() {
		return "Test";
	}

}

class Base {
	@Override
	public String toString() {
		return "Base";
	}
}