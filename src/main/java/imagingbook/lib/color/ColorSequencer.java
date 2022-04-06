package imagingbook.lib.color;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

/**
 * This class defines methods for iterating over an ordered set of AWT colors.
 * The color set can be constructed from individual AWT colors
 * (see {@link #ColorSequencer(Color...)}),
 * a given collection of colors
 * (see {@link #ColorSequencer(Collection)}),
 * or an enum class implementing {@link ColorEnumeration}
 * (see  {@link #ColorSequencer(Class)}).
 * 
 * @author WB
 * @version 2022/04/06
 * 
 * @see Color
 */
public class ColorSequencer implements Iterator<Color> {
	
	private final Color[] colorArray;
	private final Random rg = new Random();
	
	private int next = 0;
	
	public ColorSequencer(Color... colors) {
		if (colors.length == 0) {
			throw new IllegalArgumentException("color set may not be empty!");
	}
		this.colorArray = colors;
	}
	
	public ColorSequencer(Collection<Color> colors) {
		this(colors.toArray(new Color[0]));
	}
	
	public ColorSequencer(Class<? extends ColorEnumeration> clazz) {
		this.colorArray = ColorEnumeration.getColors(clazz);
	}
	
	// -------------------------------------------------------
	
	public int size() {
		return colorArray.length;
	}
	
	public Color getColor(int idx) {
		return colorArray[idx];
	}
	
	public Color[] getColors() {
		return colorArray;
	}
	
	// --- iteration stuff -----------------------------------
	
	/**
	 * Reset the iterator, such that the next color returned by {@link #next()}
	 * has index 0.
	 */
	public void reset() {
		reset(0);
	}
	
	/**
	 * Reset the iterator such that the index of the item returned by the following
	 * call to {@link #next()} has the specified start index.
	 * 
	 * @param start the new start index
	 */
	public void reset(int start) {
		this.next = Math.floorMod(start - 1, colorArray.length);
	}
	
	@Override
	public boolean hasNext() {
		return true;
	}
	
	@Override
	public Color next() {
		next = (next + 1) % colorArray.length;
		return colorArray[next];
	}
	
	/** 
	 * Returns a color with some random index.
	 * Two successive calls should never give the same color
	 * (except if the color set has only one color).
	 * 
	 * @return the next random color
	 */
	public Color nextRandom() {
		int step = rg.nextInt(Math.max(1, colorArray.length - 1));	// works for single color too
		next = (next + 1 + step) % colorArray.length;
		return colorArray[next];
	}
	
	// ---------------------------------------------------------
	
	public static void main(String[] args) {
		
//		ColorSequencer iter = new ColorSequencer(Color.blue, Color.green, Color.red);
		ColorSequencer iter = new ColorSequencer(BasicAwtColor.class);
		iter.reset();
		for (int k = 0; k < 10; k++) {
			Color c = iter.nextRandom();
			System.out.println(k + ": " + c.toString());
		}
		
	}

}


