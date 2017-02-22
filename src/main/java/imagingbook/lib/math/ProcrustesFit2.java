package imagingbook.lib.math;

import java.util.List;

import org.apache.commons.math3.linear.RealVector;

public abstract class ProcrustesFit2 {
	
	private final int m, n;
	
	private ProcrustesFit2(int m, int n) {
		this.m = m;
		this.n = n;
		System.out.println("constr ProcrustesFit2");
	}
	
	public RealVector getT() {
		return null;
	}
	
	public static class Orthogonal extends ProcrustesFit2 {
		public Orthogonal(List<double[]> xA, List<double[]> xB) {
			super(xA.size(), xA.get(0).length);
			System.out.println("constr Orthogonal");
		}
	}
	
	public static class Rigid extends ProcrustesFit2 {
		public Rigid(List<double[]> xA, List<double[]> xB) {
			super(xA.size(), xA.get(0).length);
			System.out.println("constr Rigid");
		}
	}
	
	public static class Similarity extends ProcrustesFit2{
		public Similarity(List<double[]> xA, List<double[]> xB) {
			super(xA.size(), xA.get(0).length);
			System.out.println("constr Similarity");
		}
	}
	
	// --------------------------------
	
	public static void main(String[] arges) {
		ProcrustesFit2 pf = new ProcrustesFit2.Similarity(null, null);
	}
	

}
