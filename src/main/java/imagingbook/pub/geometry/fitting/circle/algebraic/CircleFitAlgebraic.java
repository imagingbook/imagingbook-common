package imagingbook.pub.geometry.fitting.circle.algebraic;

import static imagingbook.lib.math.Arithmetic.isZero;

import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.circle.AlgebraicCircle;
import imagingbook.pub.geometry.circle.GeometricCircle;

public abstract class CircleFitAlgebraic {
	
	public enum FitType {
		KasaOrig,
		KasaA,
		KasaB,
		Pratt,
		Hyper,
//		Taubin	// TODO
	}
	
	public static CircleFitAlgebraic getFit(FitType type, Pnt2d[] points) {
		switch (type) {
		case Hyper: return new CircleFitHyper(points);
		case KasaA: return new CircleFitKasaA(points);
		case KasaB: return new CircleFitKasaB(points);
		case KasaOrig: return new CircleFitKasa(points);
		case Pratt: return new CircleFitPratt(points);
//		case Taubin: return new CircleFitTaubin(points);
		}
		throw new RuntimeException("unknown algebraic fit type: " + type);
	}
	
	/**
	 * Returns the parameters (A, B, C, D) of the algebraic circle
	 * A (x^2 + y^2) + B x + C y + D = 0.
	 * @return the algebraic circle parameters (A, B, C, D)
	 */
	public abstract double[] getParameters();
	
	public GeometricCircle getGeometricCircle() {
		double[] p = this.getParameters();	// assumed to be (A, B, C, D)
		if (p == null || isZero(p[0])) {			// (abs(2 * A / s) < (1 / Rmax))
			return null;			// return a straight line
		}
		else {
			return GeometricCircle.from(new AlgebraicCircle(p));
		}
	}
	
}
