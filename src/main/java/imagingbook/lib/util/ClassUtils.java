package imagingbook.lib.util;

import java.lang.reflect.Field;

public abstract class ClassUtils {
	
	/**
	 * Lists all accessible public fields of the given object and 
	 * returns the result as a string.
	 * @param obj a (non-null) object 
	 * @return a string listing the names and values of the object's fields
	 */
	public static String listFields(Object obj) {
		Class<?> clz = obj.getClass();
		StringBuilder buf = new StringBuilder(clz.getName() + ":\n");
		Field[] fields = clz.getFields();
		for (Field f : fields) {
			String name = f.getName();
			String value = null;
			try {
				value = f.get(obj).toString();
			} catch (IllegalArgumentException | IllegalAccessException e1) {			}
			buf.append(name + ": " + value + "\n");
		}
		return buf.toString();
	}

}
