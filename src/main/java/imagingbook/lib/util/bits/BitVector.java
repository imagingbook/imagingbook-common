package imagingbook.lib.util.bits;

public interface BitVector {
	
	/**
	 * Returns {@code true} is the specified element is set (1),
	 * {@code false} otherwise (0).
	 * @param i the element index
	 * @return as described
	 */
	public boolean get(int i);
	
	/**
	 * Sets the specified bit-element to the given boolean value
	 * (1 for {@code true}, 0 for {@code false}).
	 * @param i the element index
	 * @param val a boolean value
	 */
	public default void set(int i, boolean val) {
		if (val) 
			this.set(i);
		else
			this.unset(i);
	}
	
	/**
	 * Sets the specified element (to bit-value 1).
	 * @param i the element index
	 */
	public void set(int i);
	
	/**
	 * Unsets the specified element (to bit-value 0).
	 * @param i the element index
	 */
	public void unset(int i);
	
	/**
	 * Sets all element values to 1.
	 */
	public void setAll();
	
	/**
	 * Sets all element values to 0.
	 */
	public void unsetAll();
	
	/**
	 * Returns the length of this bit vector.
	 * @return the length of this bit vector
	 */
	public int getLength();
	
	/**
	 * Factory method. Creates and returns a new bitvector of type
	 * {@link BitVector64}.
	 * @param length the length of the bit vector
	 * @return a new bit vector
	 */
	public static BitVector create(int length) {
		return new BitVector64(length);
	}

}
