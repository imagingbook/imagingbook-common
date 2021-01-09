package imagingbook.lib.util.progress2;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public interface ReportsProgress {
	
	// implementing classes must have such a method
	public long[] reportProgress();
	
	public default double getProgress() throws Throwable {
		Class<?> clazz = this.getClass();
		do {
			//listMethods(clazz);
			try {
				Lookup lu = MethodHandles.lookup();
				MethodHandle h = lu.  findSpecial(clazz, "toString", MethodType.methodType(String.class), clazz);
				//h.invoke(this);
			} catch (NoSuchMethodException | IllegalAccessException e) {
				e.printStackTrace();
			}
			clazz = clazz.getSuperclass();
		} while (ReportsProgress.class.isAssignableFrom(clazz));
		
//		listMethods(clazz);
//		while (ReportsProgress.class.isAssignableFrom(clazz.getSuperclass())) {
//			clazz = clazz.getSuperclass();
//			listMethods(clazz);
//		}
		
		return 0;
	}
	
	public static void listMethods(Class<?> clazz) {
		System.out.println(clazz.getSimpleName());
		for (Method m : clazz.getDeclaredMethods()) {
			System.out.println("   " + m.toString());
		}
	}
	
	
	

}
