package imagingbook.lib.color;

import java.awt.Color;
import java.util.Iterator;

/**
 * Used for color enum types. This means that every enum class (item) implements this interface.
 * @author WB
 *
 */
public interface ColorEnumeration<T extends Enum<T>> {
	
	public Color getColor();
	
	public default int getRGB() {
		return getColor().getRGB();
	}
	
	// -----------------------------------
	
	@Deprecated // experimental!
	public Iterator<T> getIterator();
	
}
