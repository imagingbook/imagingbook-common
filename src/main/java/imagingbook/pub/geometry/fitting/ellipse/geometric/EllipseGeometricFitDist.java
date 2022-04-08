package imagingbook.pub.geometry.fitting.ellipse.geometric;

import static imagingbook.lib.math.Arithmetic.isZero;
import static imagingbook.lib.math.Arithmetic.sqr;
import static imagingbook.lib.math.Matrix.makeRealMatrix;
import static imagingbook.lib.math.Matrix.subtract;
import static java.lang.Math.cos;
import static java.lang.Math.signum;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory.evaluationChecker;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;
import org.apache.commons.math3.util.Pair;

import imagingbook.lib.math.Matrix;
import imagingbook.lib.settings.PrintPrecision;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.ellipse.GeometricEllipse;
import imagingbook.pub.geometry.fitting.ellipse.algebraic.EllipseFitAlgebraic;
import imagingbook.pub.geometry.fitting.ellipse.algebraic.EllipseFitFitzgibbonStable;

/**
 * "Distance-based" geometric ellipse fitter using iterative minimization with
 * the Levenberg-Marquart method.
 * 
 * @author WB
 *
 */
public class EllipseGeometricFitDist extends EllipseFitGeometric {
	
	private final Pnt2d[] pts;
	private final double tolerance;
	private final int maxEvaluations;
	private final int maxIterations;
	
	private final double[] V;
	private final double[][] J;
	
	private final MultivariateJacobianFunction model;
	private final Optimum solution;
	private final List<double[]> history = new ArrayList<>();
	
	public EllipseGeometricFitDist(Pnt2d[] pts, GeometricEllipse initEllipse, int maxEvaluations, 
			int maxIterations, double tolerance, boolean syntheticDeriv) {
		this.pts = pts;
		int n = pts.length;
		this.V = new double[n];
		this.J = new double[n][5];
		this.maxEvaluations = maxEvaluations;
		this.maxIterations = maxIterations;
		this.tolerance = tolerance;
		this.model = (syntheticDeriv) ? new SyntheticModel() : new AnalyticModel();
		this.solution = solveLM(initEllipse.getParameters());
	}
	
	public EllipseGeometricFitDist(Pnt2d[] pts, GeometricEllipse initEllipse) {
		this(pts, initEllipse, DefaultMaxIterations, DefaultMaxIterations, DefaultTolerance, false);
	}
	
	// --------------------------------------------------------------------------
	
	@Override
	public double[] getParameters() {
		return solution.getPoint().toArray();
	}
	
	@Override
	public int getIterations() {
		return solution.getIterations();
	}
	
	@Override
	public List<double[]> getHistory() {
		return this.history;
	}
	
	// --------------------------------------------------------------------------

	private Optimum solveLM(double[] p0) {
		LeastSquaresProblem problem = LeastSquaresFactory.create(
				this.model,
				new ArrayRealVector(pts.length), 	// zero vector
				new ArrayRealVector(p0),
				evaluationChecker(new SimpleVectorValueChecker(tolerance, tolerance)), 
				maxEvaluations, maxIterations);

		LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
		Optimum sol = optimizer.optimize(problem);
		return sol;
	}
	
	// --------------------------------------------------------------------------
	
	class AnalyticModel implements MultivariateJacobianFunction {
	
		@Override
		public Pair<RealVector, RealMatrix> value(RealVector point) {
			final double[] p = point.toArray();
			if (RecordHistory) {
				history.add(p.clone());
			}
			final GeometricEllipse ellipse = new GeometricEllipse(p);
			final double ra = ellipse.ra;
			final double rb = ellipse.rb;
			final double xc = ellipse.xc;
			final double yc = ellipse.yc;
			final double theta = ellipse.theta;
			final double ra2 = sqr(ra);
			final double rb2 = sqr(rb);
			final double[] xyc = new double[] {xc, yc};
			
			RealMatrix R = makeRealMatrix(2, 2,
					cos(theta), -sin(theta), 
					sin(theta),  cos(theta));
			RealMatrix Rt = R.transpose();
			
			// calculate values and Jacobian
			for (int i = 0; i < pts.length; i++) {
				Pnt2d XY = pts[i];
				Pnt2d XYp = ellipse.getClosestPoint(XY);
				double d = XYp.distance(XY);
				V[i] = d;
				
				double[] xy = XY.toDoubleArray();
				double x = xy[0], y = xy[1];
				double[] uv = Rt.operate(subtract(xy, xyc));					// target point in canon. coordinates
				double u = uv[0],  v = uv[1];									// = (u_i, v_i)

				double[] xyp = XYp.toDoubleArray();								// ellipse point closest to Xi
				double xp = xyp[0], yp = xyp[1];								// = (\breve{x}_i, \breve{y}_i)

				double[] uvp = Rt.operate(subtract(xyp, xyc));					// closest point in canon. coordinates
				double up = uvp[0], vp = uvp[1];								// = (\breve{u}_i, \breve{u}_i)	
				
				double gA = signum((u - up) * up / ra2 + (v - vp) * vp / rb2);
				double gB = sqrt(sqr(up / ra2) + sqr(vp / rb2));
				if (isZero(gB)) {
					throw new RuntimeException("gB is zero!");
				}
				double g = gA / gB;
				
				J[i][0] = -g * sqr(up) / (ra2 * ra);
				J[i][1] = -g * sqr(vp) / (rb2 * rb);
				if (!isZero(d)) {
					J[i][2] = (xp - x) / d;
					J[i][3] = (yp - y) / d;
					J[i][4] = ((y - yp) * (xc - xp) - (x - xp) * (yc - yp)) / d ;
				}
				else {
					J[i][2] = J[i][3] = J[i][4] = 0;
				}
			}
			
			RealVector VV = new ArrayRealVector(V, false);
			RealMatrix JJ = new Array2DRowRealMatrix(J, false);
			return new Pair<>(VV, JJ);
		}
	}
	
	// --------------------------------
	
	class SyntheticModel implements MultivariateJacobianFunction {
		
		final double delta = 0.00001;

		@Override
		public Pair<RealVector, RealMatrix> value(RealVector point) {
			double[] p = point.toArray();
			if (RecordHistory) {
				history.add(p.clone());
			}
			GeometricEllipse ellipse = new GeometricEllipse(p);		// ellipse for current parameter point
			GeometricEllipse[] dEllipses = new GeometricEllipse[5];	// differentially modified ellipses
			for (int j = 0; j < 5; j++) {
				double[] pd = p.clone();
				pd[j] = p[j] + delta;
				dEllipses[j] = new GeometricEllipse(pd);
			}
			
			// calculate values and Jacobian
			for (int i = 0; i < pts.length; i++) {
				Pnt2d XYi = pts[i];
				Pnt2d XYp = ellipse.getClosestPoint(XYi);
				double di = XYp.distance(XYi);
				V[i] = di;								// value = distance of point to ellipse
				
				for (int j = 0; j < 5; j++) {
					Pnt2d XYpj = dEllipses[j].getClosestPoint(XYi);
					J[i][j] = (XYpj.distance(XYi) - di) / delta;	// estimated partial derivative
				}
			}
			
			RealVector VV = new ArrayRealVector(V, false);
			RealMatrix JJ = new Array2DRowRealMatrix(J, false);
			return new Pair<>(VV, JJ);
		}
	}
    
    // -------------------------------------------------------------------
    
    public static void main(String[] args) {
    	PrintPrecision.set(9);
    	Pnt2d[] points = null;
    	//Pnt2d[] points = CircleMaker.makeTestCircle(XC, YC, R, 100, Angle0, Angle1, SigmaNoise);
    	//Pnt2d[] points = CircleMaker.makeTestCircle(XC, YC, R, 4, Angle0, Angle1, 0.1);
    	//Pnt2d[] points = CircleSampler.makeTestGander(30);
    	//Pnt2d[] points = CircleSampler.make3Points(30);
    		
    	EllipseFitAlgebraic fitA = new EllipseFitFitzgibbonStable(points);
    	
    	GeometricEllipse ellipseA = GeometricEllipse.from(fitA.getEllipse());
    	System.out.println("ellipseA = " + ellipseA);
    	System.out.println("errorA = " + ellipseA.getMeanSquareError(points));
		
    	EllipseGeometricFitDist fitG = new EllipseGeometricFitDist(points, ellipseA);
    	GeometricEllipse ellipseG = fitG.getEllipse();
    	System.out.println("ellipseG = " + ellipseG);
    	System.out.println("errorG = " + ellipseG.getMeanSquareError(points));
    	System.out.println("iterations = " + fitG.getIterations());
    	
    	for (double[] p : fitG.getHistory()) {
    		System.out.println("   " + Matrix.toString(p));
    	}
    	
    	// check analytic/synthetic Jacobians:
    	GeometricEllipse eTest = new GeometricEllipse (50, -30, 100, 120, 0.25);
    	Pnt2d xi = Pnt2d.from(110, -70);
    	Pnt2d[] pts = {xi};
    	
    	{
    		EllipseGeometricFitDist fit = new EllipseGeometricFitDist(pts, eTest);
    		MultivariateJacobianFunction model = fit.new AnalyticModel();
	    	Pair<RealVector, RealMatrix> result = model.value(Matrix.makeRealVector(eTest.getParameters()));
	    	RealMatrix J = result.getSecond();
	    	System.out.println("J analytic = \n" + Matrix.toString(J.getData()));
    	}
    	
    	{
    		EllipseGeometricFitDist fit = new EllipseGeometricFitDist(pts, eTest);
    		MultivariateJacobianFunction model = fit.new SyntheticModel();
	    	Pair<RealVector, RealMatrix> result = model.value(Matrix.makeRealVector(eTest.getParameters()));
	    	RealMatrix J = result.getSecond();
	    	System.out.println("J synthetic = \n" + Matrix.toString(J.getData()));
    	}
    }

}
