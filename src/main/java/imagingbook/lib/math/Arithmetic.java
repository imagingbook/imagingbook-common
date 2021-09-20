/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.math;

public abstract class Arithmetic {
	
	// machine accuracy for IEEE 754 float/double;
	/** Tolerance used for comparing {@code float} quantities. */
	public static final float EPSILON_FLOAT 	= 1e-7f;	// 1.19 x 10^-7
	/** Tolerance used for comparing {@code double} quantities. */
	public static final double EPSILON_DOUBLE 	= 2e-16;	// 2.22 x 10^-16

	/**
	 * Returns the square of its argument.
	 * @param x argument
	 * @return square of argument
	 */
	public static int sqr(int x) {
		return x * x;
	}
	
	/**
	 * Returns the square of its argument.
	 * @param x argument
	 * @return square of argument
	 */
	public static long sqr(long x) {
		return x * x;
	}
	
	/**
	 * Returns the square of its argument.
	 * @param x argument
	 * @return square of argument
	 */
	public static float sqr(float x) {
		return x * x;
	}
	
	/**
	 * Returns the square of its argument.
	 * @param x argument
	 * @return square of argument
	 */
	public static double sqr(double x) {
		return x * x;
	}
	
	/**
	 * Integer version of the modulus operator ({@code a mod b}).
	 * Also see <a href="http://en.wikipedia.org/wiki/Modulo_operation">here</a>.
	 * Calls {@code Math.floorMod(a,b)} (available in Java 8 and higher).
	 * @param a dividend
	 * @param b divisor (modulus), must be nonzero
	 * @return {@code a mod b}
	 */
	public static int mod(int a, int b) {
		return Math.floorMod(a, b);
	}
	
	// original implementation (obsolete)
//	public static int mod(int a, int b) {
//		if (b == 0)
//			return a;
//		// a, b have the same sign OR the remainder is zero
//        if ((long)a * b >= 0 || a % b == 0)	// or (a / b) * b == a
//        	return a - b * (a / b);
//        else
//        	return a - b * (a / b - 1);
//	}

	
	/**
	 * Non-integer version of modulus operator using floored division
	 * (see <a href="http://en.wikipedia.org/wiki/Modulo_operation">here</a>),
	 * with results identical to Mathematica. 
	 * Calculates {@code a mod b} for floating-point arguments.
	 * An exception is thrown if  {@code b} is zero.
	 * Examples:
	 * <pre>
	 * mod( 3.5, 2.1) ->  1.4
	 * mod(-3.5, 2.1) ->  0.7
	 * mod( 3.5,-2.1) -> -0.7
	 * mod(-3.5,-2.1) -> -1.4</pre>
	 * @param a dividend
	 * @param b divisor (modulus), must be nonzero
	 * @return {@code a mod b}
	 */
	public static double mod(double a, double b) {
		if (isZero(b))
				throw new IllegalArgumentException("zero modulus in mod");
		return a - b * Math.floor(a / b);
	}
	
	/**
	 * Test for zero (float version) using a predefined tolerance.
	 * Returns true if the argument's absolute value
	 * is less than {@link EPSILON_FLOAT}.
	 * @param x quantity to be tested
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(float x) {
		return Math.abs(x) < EPSILON_FLOAT;
	}
	
	/**
	 * Test for zero (float version) using a specified tolerance.
	 * Returns true if the argument's absolute value
	 * is less than the specified tolerance.
	 * @param x quantity to be tested
	 * @param tolerance the tolerance to be used
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(float x, float tolerance) {
		return Math.abs(x) < tolerance;
	}
	
	/**
	 * Test for zero (double version) using a predefined tolerance.
	 * Returns true if the argument's absolute value
	 * is less than {@link EPSILON_DOUBLE}.
	 * @param x quantity to be tested
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(double x) {
		return Math.abs(x) < EPSILON_DOUBLE;
	}
	
	/**
	 * Test for zero (double version) using a specified tolerance.
	 * Returns true if the argument's absolute value
	 * is less than the specified tolerance.
	 * @param x quantity to be tested
	 * @param tolerance the tolerance to be used
	 * @return true if argument is close to zero
	 */
	public static boolean isZero(double x, double tolerance) {
		return Math.abs(x) < tolerance;
	}
	
	/**
	 * Test for numerical equality (double version) using a predefined tolerance.
	 * Returns true if the absolute difference of the arguments
	 * is less than {@link EPSILON_DOUBLE}.
	 * @param x first argument
	 * @param y second argument
	 * @return true if both arguments are numerically the same.
	 */
	public static boolean equals(double x, double y) {
		return Arithmetic.isZero(x - y);
	}
	
	/**
	 * Test for numerical equality (float version) using a predefined tolerance.
	 * Returns true if the absolute difference of the arguments
	 * is less than {@link EPSILON_FLOAT}.
	 * @param x first argument
	 * @param y second argument
	 * @return true if both arguments are numerically the same.
	 */
	public static boolean equals(float x, float y) {
		return Arithmetic.isZero(x - y);
	}
	
	//--------------------------------------------------------------------------
	
	public static class DivideByZeroException extends ArithmeticException {
		private static final long serialVersionUID = 1L;
		private static String DefaultMessage = "zero denominator in division";
		
		public DivideByZeroException() {
			super(DefaultMessage);
		}
	}
	
	// -------------------------------------
	
	public static void main(String[] args) {
		System.out.println(Arithmetic.mod(13, 4));
		System.out.println(Arithmetic.mod(13, -4));
		System.out.println(Arithmetic.mod(-13, 4));
		System.out.println(Arithmetic.mod(-13, -4));
		
		int b = 7;
		System.out.format("   i  -> floor |  mod\n");
		for (int i = -25; i < 25; i++) {
			System.out.format("%4d  -> %4d  | %4d \n", i, Math.floorMod(i, b), Arithmetic.mod(i, b));
		}
	}

}
