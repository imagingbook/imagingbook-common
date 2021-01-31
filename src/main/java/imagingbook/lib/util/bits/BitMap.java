package imagingbook.lib.util.bits;

import imagingbook.pub.geometry.basic.Pnt2d.PntInt;

/**
 * This class implements a true 2D bitmap container, i.e., each 0/1 element
 * occupies only a single bit (unlike {@code boolean} arrays, which require at 
 * least 8 bits per element).
 * 
 * @author WB
 * @version 2021/01/30
 */
public class BitMap {
	
	/** the width of this bitmap */
	public final int width;
	/** the height of this bitmap */
	public final int height;
	
	private final BitVector bitvec;
	
	/**
	 * Constructor.
	 * @param width the width of the new bitmap
	 * @param height the height of the new bitmap
	 */
	public BitMap(int width, int height) {
		this.width = width;
		this.height = height;
		this.bitvec = BitVector.create(width * height);
	}
	
	/**
	 * Factory method.
	 * @param width the width of the new bitmap
	 * @param height the height of the new bitmap
	 * @return a new bitmap with the specified size
	 */
	public static BitMap create(int width, int height) {
		return new BitMap(width, height);
	}
	
	/**
	 * Returns {@code true} is the specified element is set (1),
	 * {@code false} otherwise (0).
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return as described
	 */
	public boolean get(int x, int y) {
		return bitvec.get(y * width + x);
	}
	
	public boolean get(PntInt p) {
		return bitvec.get(p.y * width + p.x);
	}
	
	/**
	 * Sets the specified bit-element to the given boolean value
	 * (1 for {@code true}, 0 for {@code false}).
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param val a boolean value
	 */
	public void set(int x, int y, boolean val) {
		if (val) {
			this.set(x, y);
		}
		else {
			this.unset(x, y);
		}
	}
	
	public void set(PntInt p, boolean val) {
		set(p.x, p.y, val);
	}
	
	/**
	 * Sets the specified element (to bit-value 1).
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public void set(int x, int y) {
		bitvec.set(y * width + x);
	}
	
	public void set(PntInt p) {
		bitvec.set(p.y * width + p.x);
	}
	
	/**
	 * Unsets the specified element (to bit-value 0).
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public void unset(int x, int y) {
		bitvec.unset(y * width + x);
	}
	
	public void unset(PntInt p) {
		bitvec.unset(p.y * width + p.x);
	}
	
	/**
	 * Sets all element values to 1.
	 */
	public void setAll() {
		bitvec.setAll();
	}
	
	/**
	 * Sets all element values to 0.
	 */
	public void unsetAll() {
		bitvec.unsetAll();
	}
	
	/**
	 * Returns the underlying 1D {@link BitVector}.
	 * @return the bitvector
	 */
	public BitVector getBitVector() {
		return this.bitvec;
	}

}
