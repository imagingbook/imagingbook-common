package imagingbook.lib.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

/**
 * Interface to be implemented by local 'Parameters' classes.
 * This is part of the 'simple parameter object' scheme,
 * working with public fields.
 * Makes parameter objects printable by listing all public 
 * fields.
 * Other functionality may be added in the future.
 * 
 * @author WB
 *
 */
public interface SimpleParameters {

	public default String printToString() {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		try (PrintStream strm = new PrintStream(bas)) {
			printToStream(strm);
		}
		return bas.toString();
	}

	public default void printToStream(PrintStream strm) {
		Class<? extends SimpleParameters> clazz = this.getClass();
		Field[] fields = clazz.getFields();		// gets only public fields
		strm.println(clazz.getCanonicalName());
		for (Field field : fields) {
			strm.print(field.getType().getSimpleName() + " ");
			strm.print(field.getName() + " = ");
			try {
				strm.print(field.get(this).toString());
			} catch (IllegalArgumentException | IllegalAccessException e) {	}	
			strm.println();
//			int modifiers = field.getModifiers();
//			strm.println("Field is public = " + Modifier.isPublic(modifiers));
//			strm.println("Field is final = " + Modifier.isFinal(modifiers));
		}
	}

}
