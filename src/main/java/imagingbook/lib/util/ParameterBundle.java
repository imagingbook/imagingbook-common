package imagingbook.lib.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import ij.gui.GenericDialog;

/**
 * Interface to be implemented by local 'Parameters' classes.
 * This is part of the 'simple parameter object' scheme,
 * working with public fields.
 * Only non-static, non-final, public fields are accepted as parameters.
 * 
 * Current features:
 * (a) Makes parameter bundles printable by listing all eligible fields.
 * (b) Parameter bundles can be added/modified as a whole by ImageJ's 
 * {@link GenericDialog}, supported by specific annotations.
 * 
 * Other functionality may be added in the future.
 * 
 * @author WB
 * @version 2022/02/02
 */
public interface ParameterBundle {
	
	public default String printToString() {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		try (PrintStream strm = new PrintStream(bas)) {
			printToStream(strm);
		}
		return bas.toString();
	}

	public default void printToStream(PrintStream strm) {
		Class<? extends ParameterBundle> clazz = this.getClass();
		Field[] fields = clazz.getFields();		// gets only public fields
		strm.println(clazz.getCanonicalName());
		for (Field field : fields) {
			if (!isValidParameterItem(field)) {
				continue;
			}
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
	
	// ---- Dialog-related annotations to be used on individual parameter fields ------
	
	
	/**
	 * Annotation to specify a specific 'label' to be shown for following
	 * parameter fields. Default label is the variable name.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface DialogLabel {
		public String value();
	}
	
	/**
	 * Annotation to specify the number of digits displayed when showing
	 * numeric values in dialogs. Default is {@link DefaultNumberOfDigits}.
	 * This annotation has no effect on non-floating-point fields.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface DialogDigits {
		public int value();
	}
	
	/**
	 * Annotation to always hide the following parameter field in dialogs.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface DialogHide {
	}
	
	// ------------ ImageJ dialog-related (move to another interface?) ------------------
	
	public default void addToDialog(GenericDialog gd) {
		Class<? extends ParameterBundle> clazz = this.getClass();
		Field[] fields = clazz.getFields();		// gets only public fields
		for (Field f : fields) {
			if (!isValidParameterItem(f) || f.isAnnotationPresent(DialogHide.class)) {
				continue;
			}
			try {
				addFieldToDialog(f, gd);
			} catch (IllegalArgumentException | IllegalAccessException e) {	}
		}
	}
	
	public default boolean getFromDialog(GenericDialog gd) {
		Class<? extends ParameterBundle> clazz = this.getClass();
		Field[] fields = clazz.getFields();		// gets only public fields
		int errorCount = 0;
		for (Field f : fields) {
			if (!isValidParameterItem(f) || f.isAnnotationPresent(DialogHide.class)) {
				continue;
			}
			try {
				if (!getFieldFromDialog(f, gd)) {
					errorCount++;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {	}
		}
		return (errorCount == 0);
	}
	
	static boolean isValidParameterItem(Field f) {
		int mod = f.getModifiers();
		if (Modifier.isPrivate(mod) || Modifier.isFinal(mod) || Modifier.isStatic(mod)) {
			return false;
		}
		return true;
	}
	
	static void printModifiers(Field f) {
		int mod = f.getModifiers();
		System.out.println("Modifiers of field " + f.getName());
		System.out.println("abstract     = " + Modifier.isAbstract(mod));
		System.out.println("final        = " + Modifier.isFinal(mod));
		System.out.println("interface    = " + Modifier.isInterface(mod));
		System.out.println("native       = " + Modifier.isNative(mod));
		System.out.println("private      = " + Modifier.isPrivate(mod));
		System.out.println("protected    = " + Modifier.isProtected(mod));
		System.out.println("public       = " + Modifier.isPublic(mod));
		System.out.println("static       = " + Modifier.isStatic(mod));
		System.out.println("strict       = " + Modifier.isStrict(mod));
		System.out.println("synchronized = " + Modifier.isSynchronized(mod));
		System.out.println("transient    = " + Modifier.isTransient(mod));
		System.out.println("volatite     = " + Modifier.isVolatile(mod));
		
		
	}
	
	/**
	 * Adds the specified {@link Field} of this object as new item to 
	 * the {@link GenericDialog} instance.
	 * The name of the field is used as the 'label' of the dialog item.
	 * TODO: this could/should be private!
	 * 
	 * @param field
	 * @param dialog
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	default void addFieldToDialog(Field field, GenericDialog dialog) 
			throws IllegalArgumentException, IllegalAccessException {
		
		String name = field.getName();
		if (field.isAnnotationPresent(DialogLabel.class)) {
			name = field.getAnnotation(DialogLabel.class).value();
		}
		
		int digits = 2; // DefaultDialogDigits;
		if (field.isAnnotationPresent(DialogDigits.class)) {
			digits = field.getAnnotation(DialogDigits.class).value();
			digits = Math.max(0,  digits);
		}
		
		Class<?> clazz = field.getType();
		if  (clazz.equals(boolean.class)) {
			dialog.addCheckbox(name, field.getBoolean(this));
		}
		else if (clazz.equals(int.class)) {
			dialog.addNumericField(name, field.getInt(this), 0);
		}
		else if (clazz.equals(float.class)) {
			dialog.addNumericField(name, field.getFloat(this), digits);
		}
		else if (clazz.equals(double.class)) {
			dialog.addNumericField(name, field.getDouble(this), digits);
		}
		else if (clazz.equals(String.class)) {
			String str = (String) field.get(this);
			dialog.addStringField(name, str);
		}
		else if (clazz.isEnum()) {
			dialog.addEnumChoice(name, (Enum<?>) field.get(this));
		}
		else {
			throw new RuntimeException("cannot handle field of type " + clazz);
		}
	}

	/**
	 * Modifies the specified {@link Field} of this object by reading the next item
	 * from the {@link GenericDialog} instance.
	 * TODO: this could/should be private!
	 * 
	 * @param field	a publicly accessible {@link Field} of this object 
	 * @param dialog a {@link GenericDialog} instance
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	default boolean getFieldFromDialog(Field field, GenericDialog dialog) 
					throws IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = field.getType();
		if  (clazz.equals(boolean.class)) {
			field.setBoolean(this, dialog.getNextBoolean());
		}
		else if (clazz.equals(int.class)) {
			double val = dialog.getNextNumber();
			if (Double.isNaN(val)) {
				return false;
			}
			field.setInt(this, (int) val);
		}
		else if (clazz.equals(float.class)) {
			double val = dialog.getNextNumber();
			if (Double.isNaN(val)) {
				return false;
			}
			field.setFloat(this, (float) val);
		}
		else if (clazz.equals(double.class)) {
			double val = dialog.getNextNumber();
			if (Double.isNaN(val)) {
				return false;
			}
			field.setDouble(this, val);
		}
		else if (clazz.equals(String.class)) {
			String str = dialog.getNextString();
			if (str == null) {
				return false;
			}
			field.set(this, str);
		}
		else if (clazz.isEnum()) {
			Enum en = dialog.getNextEnumChoice((Class<Enum>) clazz);
			if (en == null) {
				return false;
			}
			field.set(this, en);
//			field.set(instance, gd.getNextEnumChoice((Class<? extends Enum>) clazz));	// works			
//			field.set(instance, gd.getNextEnumChoice((Class<Enum>) clazz));	// works	
		}
		else {
			throw new RuntimeException("cannot handle field of type " + clazz);
		}
		return true;
	}
	
	// ----------------------------------------------------------------------
	
	enum MyEnum {
		A, B, Cee
	}

	// Example parameter bundle
	static class DemoParameters implements ParameterBundle {
		public static int staticInt = 44;	// currently static members are listed too!
		
		@DialogLabel("Make a decision:")
		public boolean someBool = true;
		public int someInt = 39;
		public float someFloat = 1.99f;
		
		@DialogLabel("Math.PI")@DialogDigits(10)
		public double someDouble = Math.PI;
		public String someString = "SHOW ME";
		
		@DialogHide
		public String hiddenString = "HIDE ME";
		public MyEnum someEnum = MyEnum.B;
	}
	
	public static void main(String[] args) {
		
		ParameterBundle params = new DemoParameters();
		System.out.println("p1 = \n" + params.printToString());
		
		GenericDialog gd = new GenericDialog(ParameterBundle.class.getSimpleName());
		gd.addNumericField("some single int", 123, 0);
		params.addToDialog(gd);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		
		@SuppressWarnings("unused")
		int singleInt = (int) gd.getNextNumber();
		boolean success = params.getFromDialog(gd);
		System.out.println("success = " + success);
		System.out.println("p2 = \n" + params.printToString());
	}
	
}