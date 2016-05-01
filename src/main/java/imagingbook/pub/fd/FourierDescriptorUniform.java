package imagingbook.pub.fd;

import imagingbook.lib.math.Arithmetic;
import imagingbook.lib.math.Complex;

import java.awt.geom.Point2D;


/**
 * Subclass of FourierDescriptor whose constructors assume
 * that input polygons are uniformly sampled.
 * 
 * @author W. Burger
 * @version 2015/08/13
 */
public class FourierDescriptorUniform extends FourierDescriptor {
	
	/**
	 * Creates a new Fourier descriptor from a uniformly sampled polygon V
	 * with the maximum number of Fourier coefficient pairs.
	 * 
	 * @param V polygon
	 */
	public FourierDescriptorUniform(Point2D[] V) {
		g = makeComplex(V);
		G = DFT(g);
	}
	
	
	/**
	 * Creates a new Fourier descriptor from a uniformly sampled polygon V
	 * with Mp coefficient pairs.
	 * 
	 * @param V polygon
	 * @param Mp number of coefficient pairs
	 */
	public FourierDescriptorUniform(Point2D[] V, int Mp) {
		g = makeComplex(V);
		G = DFT(g, 2 * Mp + 1);
	}
	
	// -------------------------------------------------------------------
	

	/**
	 * DFT with the resulting spectrum (signal, if inverse) of the same length
	 * as the input vector g. Not using sin/cos tables.
	 * 
	 * @param g signal vector
	 * @return DFT spectrum
	 */
	private Complex[] DFT(Complex[] g) {
		int M = g.length;
//		double[] cosTable = makeCosTable(M);	// cosTable[m] == cos(2*pi*m/M)
//		double[] sinTable = makeSinTable(M);
		Complex[] G = new Complex[M];
		double s = 1.0 / M; //common scale factor (fwd/inverse differ!)
		for (int m = 0; m < M; m++) {
			double Am = 0;
			double Bm = 0;
			for (int k = 0; k < M; k++) {
				double x = g[k].re();
				double y = g[k].im();
				double phi = 2 * Math.PI * m * k / M;
				double cosPhi = Math.cos(phi);
				double sinPhi = Math.sin(phi);
				Am = Am + x * cosPhi + y * sinPhi;
				Bm = Bm - x * sinPhi + y * cosPhi;
			}
			G[m] = new Complex(s * Am, s * Bm);
		}
		return G;
	}
	

	/**
	 * As above, but the length P of the resulting spectrum (signal, if inverse) 
	 * is explicitly specified.
	 * @param g signal vector
	 * @param P length of the resulting  DFT spectrum
	 * @return DFT spectrum
	 */
	private Complex[] DFT(Complex[] g, int P) {
		int M = g.length;
//		double[] cosTable = makeCosTable(M);	// cosTable[m] == cos(2*pi*m/M)
//		double[] sinTable = makeSinTable(M);
		Complex[] G = new Complex[P];
		double s = 1.0/M; //common scale factor (fwd/inverse differ!)
		for (int m = P/2-P+1; m <= P/2; m++) {
			double Am = 0;
			double Bm = 0;
			for (int k = 0; k < M; k++) {
				double x = g[k].re();
				double y = g[k].im();
				//int mk = (m * k) % M; double phi = 2 * Math.PI * mk / M;
				double phi = 2 * Math.PI * m * k / M;	
				double cosPhi = Math.cos(phi);
				double sinPhi = Math.sin(phi);
				Am = Am + x * cosPhi + y * sinPhi;
				Bm = Bm - x * sinPhi + y * cosPhi;
			}
			G[Arithmetic.mod(m, P)] = new Complex(s * Am, s * Bm);
		}
		return G;
	}

//	private double[] makeCosTable(int M) {
//		double[] cosTab = new double[M];
//		for (int m = 0; m < M; m++) {
//			cosTab[m] = Math.cos(2 * Math.PI * m / M);
//		}
//		return cosTab;
//	}

//	private double[] makeSinTable(int M) {
//		double[] sinTab = new double[M];
//		for (int m = 0; m < M; m++) {
//			sinTab[m] = Math.sin(2 * Math.PI * m / M);
//		}
//		return sinTab;
//	}

}
