/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.math;

public abstract class Arithmetic {
	
	// machine accuracy for IEEE 754 float/double;
	public static final float EPSILON_FLOAT 	= 1e-7f;	// 1.19 x 10^-7
	public static final double EPSILON_DOUBLE 	= 2e-16;	// 2.22 x 10^-16
		
	public static int sqr(int x) {
		return x * x;
	}
	
	public static float sqr(float x) {
		return x * x;
	}
	
	public static double sqr(double x) {
		return x * x;
	}
	
	/**
	 * Integer version of modulus operator (as described in the book).
	 * Calculates {@code a mod b}.
	 * * Also see <a href="http://en.wikipedia.org/wiki/Modulo_operation">here</a>.
	 * @param a dividend
	 * @param b divisor
	 * @return {@code a mod b}
	 */
	public static int mod(int a, int b) {
		if (b == 0)
			return a;
		if (a * b >= 0)	// a, b are either both positive or negative
			return a - b * (a / b);	
		else
			return a - b * (a / b - 1);
//		return Math.floorMod(a, a); 	// equivalent, but requires Java 1.8
	}
	
	/**
	 * Non-integer version of modulus operator, with results identical to Mathematica. 
	 * Calulates {@code a mod b}.
	 * Also see <a href="http://en.wikipedia.org/wiki/Modulo_operation">here</a>.
	 * @param a dividend
	 * @param b divisor
	 * @return {@code a mod b}
	 */
	public static double mod(double a, double b) {
		return a - b * Math.floor(a / b);
	}
	
	/**
	 * Test for zero (float version).
	 * @param x quantity to be tested
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(float x) {
		return Math.abs(x) < Arithmetic.EPSILON_FLOAT;
	}
	
	/**
	 * Test for zero (double version).
	 * @param x quantity to be tested
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(double x) {
		return Math.abs(x) < Arithmetic.EPSILON_DOUBLE;
	}

}
