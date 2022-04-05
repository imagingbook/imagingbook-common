package imagingbook.lib.color;

import java.awt.Color;
import java.util.EnumSet;
import java.util.Iterator;

/**
 * Enum-types for the pre-defined AWT colors.
 * 
 * @author WB
 *
 */
public enum BasicAwtColor implements ColorEnumeration<BasicAwtColor> {
	Black(Color.black),
	Blue(Color.blue),
	Cyan(Color.cyan),
	DarkGray(Color.darkGray),
	Gray(Color.gray),
	Green(Color.green),
	LightGray(Color.lightGray),
	Magenta(Color.magenta),
	Orange(Color.orange),
	Pink(Color.pink),
	Red(Color.red),
	White(Color.white),
	Yellow(Color.yellow);
	
	private final Color color;
	
	BasicAwtColor(int r, int g, int b) {
		this(new Color(r, g, b));
	}
	
	BasicAwtColor(Color col) {
		this.color = col;
	}
	
	@Override
	public Color getColor() {
		return this.color;
	}

	@Override
	public Iterator<BasicAwtColor> getIterator() {
		return EnumSet.allOf(BasicAwtColor.class).iterator();
	}
}
