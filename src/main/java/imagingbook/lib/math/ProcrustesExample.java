package imagingbook.lib.math;

import java.util.List;

import imagingbook.lib.settings.PrintPrecision;

public abstract class ProcrustesExample {
	
	public abstract void run();
	
	protected double roundToDigits(double x, int ndigits) {
		int d = (int) Math.pow(10, ndigits);
		return Math.rint(x * d) / d;
	}
	
	
	protected void showHeadline(String title) {
		System.out.println("\n**************** " + title + " ****************");
	}
	
	protected void print(List<double[]> lX) {
		for (double[] x : lX) {
			System.out.println("   " + Matrix.toString(x));
		}
	}
	
	
	public static void main(String[] args) {
		PrintPrecision.set(10);
		new ProcrustesExample1().run();
		new ProcrustesExample2().run();
	}


}
