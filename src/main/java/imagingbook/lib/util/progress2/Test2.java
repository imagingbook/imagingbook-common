package imagingbook.lib.util.progress2;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * https://stackoverflow.com/a/15674467
 *
 */

class Test2 extends Base2 {
	public void foo() throws Throwable {
//		MethodHandle h0 = MethodHandles.lookup().findSpecial(Test2.class, "toString",
//				MethodType.methodType(String.class), Test2.class);
		
		Class<?> superClazz = this.getClass().getSuperclass();
		Class<?> supersuperClazz = this.getClass().getSuperclass().getSuperclass();
		
		MethodHandle h1 = MethodHandles.lookup().findSpecial(Base2.class, "toString",
				MethodType.methodType(String.class), this.getClass());
		System.out.println(h1.invoke(new Test2()));   // outputs Base
		
		MethodHandle h0 = MethodHandles.lookup().findSpecial(Base1.class, "toString",
				MethodType.methodType(String.class), Test2.class);
		System.out.println(h0.invoke(new Test2()));   // outputs Base
		
		
//		MethodHandle h2 = MethodHandles.lookup().findSpecial(Object.class, "toString",
//				MethodType.methodType(String.class), Test2.class);
		
//		System.out.println(h0.invoke(new Test2()));   // outputs Base
//		System.out.println(h2.invoke(new Test2()));   // outputs Base
		
		System.out.println(new Test2().toString());   // outputs Test
	}

	@Override
	public String toString() {
		return "Test2";
	}
	
	public static void main(String[] args)  {
		//Test2.foo();
		try {
			new Test2().foo();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

}