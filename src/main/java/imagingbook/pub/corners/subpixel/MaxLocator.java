package imagingbook.pub.corners.subpixel;

/**
 * The common interface for all sub-pixel locators in this package.
 * @see MaxLocatorParabolic
 * @see MaxLocatorQuartic
 * @see MaxLocatorTaylor
 * @author WB
 * @version 2020/10/03
 */
public interface MaxLocator {
	
	/**
	 * Tries to locate the sub-pixel maximum from the 9 discrete sample values
	 * (s0,...,s8) taken from a 3x3 neigborhood. 
	 * The center value (s0) is assumed to be at position (0,0).
	 * Samples values are assumed to be arranged 
	 * in the following order:
	 * <pre>
	 * s4 s3 s2
	 * s5 s0 s1
	 * s6 s7 s8
	 * </pre>
	 * @param s a vector containing 9 sample values in the order described above
	 * @return a 3-element array [x,y,z], with the estimated maximum position (x,y) and the associated max. value (z). 
	 * The position is relative to the center coordinate (0,0).
	 * {@code null} is returned if the maximum position could not be located.
	 */
	public float[] locateMaximum(float[] s);
	
	/**
	 * Enumeration of keys for {@link MaxLocator} methods.
	 */
	public enum Method {
		None		{@Override MaxLocator getInstance() {return null;}},
		Parabolic 	{@Override MaxLocator getInstance() {return new MaxLocatorParabolic();}}, 
		Quartic		{@Override MaxLocator getInstance() {return new MaxLocatorQuartic();}},  
		Taylor		{@Override MaxLocator getInstance() {return new MaxLocatorTaylor();}};

		abstract MaxLocator getInstance();
	}
	
	/**
	 * Creates a specific {@link MaxLocator} instance based on the supplied
	 * {@link Method} key.
	 * @param m
	 * @return
	 */
	public static MaxLocator create(Method m) {
		if (m == null) {
			return null;
		}
		else {
			return m.getInstance();
		}
	}

}
