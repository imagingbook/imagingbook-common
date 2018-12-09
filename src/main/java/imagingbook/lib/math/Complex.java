/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2018 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.math;

/**
 * This class represents complex numbers. Instances are immutable.
 * Methods are mostly defined to be compatible with 
 * org.apache.commons.math3.complex.Complex and (newer)
 * org.apache.commons.numbers.complex.Complex.
 * Arithmetic operations are generally more accurate than with the
 * Apache implementation.
 * 
 * @author W. Burger
 * @version 2018/03/18
 */
public class Complex {
	
	public static final Complex ONE = new Complex(1, 0);
	public static final Complex ZERO = new Complex(0, 0);
	public static final Complex I = new Complex(0, 1);

	/** The real part of this complex number */
	public final double re;
	/** The imaginary part of this complex number */
	public final double im;

	/**
	 * Constructor.
	 * @param re real part
	 * @param im imaginary part
	 */
	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}

	/**
	 * Constructor
	 * @param z complex quantity, which is duplicated
	 */
	public Complex(Complex z) {
		this.re = z.re;
		this.im = z.im;
	}


	/**
	 * Create a complex quantity on the unit circle with angle 'phi':
	 * e^{\i \phi} = \cos(\phi) + \i \cdot \sin(\phi)
	 * @param phi angle
	 */
	public Complex(double phi) {
		this.re = Math.cos(phi);
		this.im = Math.sin(phi);
	}

	/**
	 * Returns the absolute value of this complex number, i.e.,
	 * its radius or distance from the origin.
	 * @return the absolute value
	 */
	public double abs() {
		return Math.sqrt(this.abs2());
	}

	/**
	 * Returns the squared absolute value of this complex number, i.e.,
	 * its squared radius or distance from the origin.
	 * @return the squared absolute value
	 */
	public double abs2() {
		return re * re + im * im;
	}

	/**
	 * Returns the 'argument' of this complex number, i.e.,
	 * its angle relative to the real axis. 
	 * @return the argument (in radians)
	 */
	public double arg() {
		return Math.atan2(im, re);
	}

	/**
     * Returns the conjugate {@code z*} of this complex number, i.e,
     * if {@code z = a + i b} then {@code z* = a - i b}.
     *
     * @return the complex conjugate
     */
	public Complex conjugate() {
		return new Complex(this.re, -this.im);
	}

	/**
	 * Adds a complex quantity to this complex number and returns
	 * a new complex number.
	 * @param z complex value
	 * @return the sum of this complex number and {@code z}
	 */
	public Complex add(Complex z) {
		return new Complex(this.re + z.re,  this.im + z.im);
	}

	/**
	 * Rotates this complex number by the angle {@code phi} and returns
	 * a new complex number.
	 * @param phi the angle
	 * @return the rotated complex value
	 */
	public Complex rotate(double phi) {
		return this.multiply(new Complex(phi));
	}

	@Override
    public String toString() {
        return "(" + this.re + ", " + this.im + ")";
    }
	
    /**
     * Returns true if the real or imaginary component of this complex number
     * is {@code NaN}.
     * @return true if {@code NaN}, otherwise false
     */
	public boolean isNaN() {
		return Double.isNaN(this.getReal()) || Double.isNaN(this.getImaginary());
	}
	
	/**
	 * Returns the real part of this complex number.
	 * @return the real part
	 */
	public double getReal() {
		return this.re;
	}
	
	/**
	 * Returns the imaginary part of this complex number.
	 * @return the imaginary part
	 */
	public double getImaginary() {
		return this.im;
	}

	/**
	 * Multiplies this complex number with another complex quantity {@code z2} and returns
	 * a new complex number.
	 * @param z2 a complex quantity
	 * @return this complex number multiplied by {@code z2}
	 */
	public Complex multiply(Complex z2) {
		// (x1 + i y1)(x2 + i y2) = (x1 x2 + y1 y2) + i (x1 y2 + y1 x2)
		Complex z1 = this;
		double x = z1.re * z2.re - z1.im * z2.im;
		double y = z1.re * z2.im + z1.im * z2.re;
		return new Complex(x, y);
	}
	
	/**
	 * Multiplies this complex number with the scalar factor {@code s} and returns
	 * a new complex number.
	 * @param s a scalar factor
	 * @return this complex number multiplied by {@code s}
	 */
	public Complex multiply(double s) {
		return new Complex(this.re * s, this.im * s);
	}
	
	 /**
     * Returns of value of this complex number raised to the power of {@code k}.
     * @param k the exponent
     * @return {@code z^k}
     */
	public Complex pow(int k) {
		if (k < 0) throw new IllegalArgumentException("exponent k >= 0 expected");
		Complex prod = new Complex(1, 0);
		for (int i = 0; i < k; i++) {
			prod = prod.multiply(this);
		}
		return prod;
	}

	//------------ TESTING only ------------------------------

	public static void main(String[] args) {
		Complex z1 = new Complex(0.3, 0.6);
		Complex z2 = new Complex(-1, 0.2);
		System.out.println("z1 = " + z1);
		System.out.println("z2 = " + z2);
		Complex z3 = z1.multiply(z2);
		System.out.println("z3 = " + z3);
		Complex z4 = z2.multiply(z1);
		System.out.println("z4 = " + z4);
	}

}
