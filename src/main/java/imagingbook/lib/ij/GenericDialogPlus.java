package imagingbook.lib.ij;

import ij.gui.GenericDialog;
import imagingbook.lib.util.Enums;

/**
 * An extension to ImageJ's {@link GenericDialog} class which adds
 * simplified choice menus defined by enum types.
 * Submitted for inclusion in ImageJ.
 * Usage example:
 * <pre>
 * import ij.IJ;
 * import ij.plugin.PlugIn;
 * 
 * public class GenericDialogWithEnums_Example implements PlugIn {
 * 
 * 	private enum MyEnum {
 * 		Alpha, Beta, Gamma;
 * 	}
 *
 *	public void run(String arg) {
 *		GenericDialogPlus gd = new GenericDialogPlus("Testing enums");
 *		gd.addEnumChoice("Choose from here", MyEnum.Beta);
 *		gd.showDialog();
 *		if (gd.wasCanceled())
 *			return;
 *		MyEnum m = gd.getNextEnumChoice(MyEnum.class);
 *		IJ.log("Your choice was " + m);
 *	}
 * }
 * </pre>
 * 
 * @author WB
 * @version 2020/10/11
 */
@Deprecated    // enum-choice is now built into ImageJ
public class GenericDialogPlus extends GenericDialog {
	private static final long serialVersionUID = 1L;
	
	public GenericDialogPlus(String title) {
		super(title);
	}
	
	// ------------------------------------------------------------------------------------
	
	/**
	 * Convenience method for {@link #addEnumChoice(String, Enum, boolean)},
	 * set up to show enum descriptions by default.
	 * 
	 * @param <E> the enum type containing the items to chose from
	 * @param label the label displayed for this choice group
	 * @param defaultItem the menu item initially selected
	 */
	public <E extends Enum<E>> void addEnumChoice(String label, Enum<E> defaultItem) {
		addEnumChoice(label, defaultItem, true);
	}
	
	/**
	 * Adds a sequence of choices to the dialog with menu items taken from the
	 * <code>enum</code> class of the specified default item (enum constant).
	 * The default item is automatically set.
	 * Optionally the descriptions of the enum constants are displayed
	 * (if defined); see {@link Enums.Description} and {@link Enums#getEnumDescriptions(Class)}.
	 * 
	 * This method calls the original string-based
	 * {@link GenericDialog#addChoice(String, String[], String)} method.
	 * 
	 * @param <E> the enum type containing the items to chose from
	 * @param label the label displayed for this choice group
	 * @param defaultItem the menu item initially selected
	 * @param showEnumDescriptions if true, the descriptions of the enum constants are shown 
	 */
	public <E extends Enum<E>> void addEnumChoice(String label, Enum<E> defaultItem, boolean showEnumDescriptions) {
		Class<E> enumClass = defaultItem.getDeclaringClass();
		String[] items = showEnumDescriptions ?
				Enums.getEnumDescriptions(enumClass) :
				Enums.getEnumNames(enumClass); //Arrays.stream(clazz.getEnumConstants()).map(Enum::name).toArray(String[]::new);
		String defaultDesc = items[defaultItem.ordinal()]; // defaultItem.name()
		this.addChoice(label, items, defaultDesc);
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
		int k = this.getNextChoiceIndex();
		return enumClass.getEnumConstants()[k];
	}
}

