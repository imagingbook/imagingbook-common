package imagingbook.lib.ij;

import java.util.Arrays;
import ij.gui.GenericDialog;


public class GenericDialogWithEnums extends GenericDialog {
	private static final long serialVersionUID = 1L;
	
	public GenericDialogWithEnums(String title) {
		super(title);
	}
	
	// -----------------------------------------------------------------------------------------------
	// Two methods below to be added to ij.gui.GenericDialog
	// -----------------------------------------------------------------------------------------------
	
	/**
	 * Adds a sequence of choices to the dialog with menu items taken from the given 
	 * <code>enum</code> class. Calls the original
	 * {@link GenericDialog#addChoice(String, String[], String)} method.
	 * Note that the menu items are retrieved from from the defaultItem's
	 * enum type.
	 * 
	 * @param <E> the enum type containing the items to chose from
	 * @param label the label shown on top of the dialog window
	 * @param defaultItem the menu item initially selected
	 */
	public <E extends Enum<E>> void addEnumChoice(String label, Enum<E> defaultItem) {
		Class<E> clazz = defaultItem.getDeclaringClass();	
		String[] items = 
				Arrays.stream(clazz.getEnumConstants()).map(Enum::name).toArray(String[]::new);
		this.addChoice(label, items, defaultItem.name());
	}
	
	/**
	 * Returns the selected item in the next enum choice menu.
	 * Note that 'enumClass' must be supplied since there is no other way to infer 
	 * the proper enum type.
	 * 
	 * @param <E> the enum type
	 * @param enumClass the enum type
	 * @return the selected item
	 */
	public <E extends Enum<E>> E getNextEnumChoice(Class<E> enumClass) {
		//enumClass.getEnumConstants();
		String choiceString = this.getNextChoice();
		System.out.println(enumClass);
		return Enum.valueOf(enumClass, choiceString);
	}
	
	// -----------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------
}

/* Usage EXAMPLE:
 
import ij.IJ;
import ij.plugin.PlugIn;
import imagingbook.lib.ij.GenericDialogWithEnums;

public class GenericDialogWithEnums_Example implements PlugIn {
	
	private enum MyEnum {
		Alpha, Beta, Gamma;
	}

	@Override
	public void run(String arg) {
		
		GenericDialogWithEnums gd = new GenericDialogWithEnums("Testing enums");
		gd.addEnumChoice("Choose from here", MyEnum.Beta);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		MyEnum m = gd.getNextEnumChoice(MyEnum.class);
		IJ.log("Your choice was " + m);
	}

}
*/
