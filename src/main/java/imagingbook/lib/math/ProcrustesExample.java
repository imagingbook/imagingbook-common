package imagingbook.lib.math;

public abstract class ProcrustesExample {
	
	public abstract void run();
	
	protected double roundToDigits(double x, int ndigits) {
		int d = (int) Math.pow(10, ndigits);
		return Math.rint(x * d) / d;
	}
	
	
	
	public static void main(String[] args) {
//		new ProcrustesExample1().run();
		new ProcrustesExample2().run();
	}


}
