package imagingbook.lib.util.progress;

import imagingbook.lib.util.progress.ProgressMonitor.HasProgressMonitor;

public class ProgressMonitorExample {
	
	// the root class
	static abstract class A implements HasProgressMonitor {
		int N = 5;
		private ProgressMonitor mon = new ProgressMonitor(this);
		
		public ProgressMonitor getProcessMonitor() {
			return mon;
		}
		
		abstract void runB();
		
		void runA() {
			getProcessMonitor().setMaxCount(A.class, N);
			getProcessMonitor().reset();
			for (int i = 0; i < N; i++) {
				runB();
				getProcessMonitor().stepCount(A.class);
			}
		}
	}
	
	static abstract class B extends A {
		int N = 3;
		
		abstract void runC();
		
		@Override
		void runB() {
			getProcessMonitor().setMaxCount(B.class, N);
			for (int j = 0; j < N; j++) {
				runC();
				getProcessMonitor().stepCount(B.class);
			}
		}
	}
	
	// the concrete terminal class
	static class C extends B {
		int N = 2;
		
		void runC() {
			getProcessMonitor().setMaxCount(C.class, N);
			for (int k = 0; k < N; k++) {
				getProcessMonitor().stepCount(C.class);
				System.out.println(getProcessMonitor().getProgress() + " " + getProcessMonitor());
			}
		}
	}
	
	
	static void showClass(Object any) {
		System.out.println("show class = " + any.getClass().getSimpleName());
	}
	
	static String prnt(String str) {
		System.out.println(str);
		return str;
	}
	
	// -----------------------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		
		A instC = new C();
		System.out.println(instC.mon);
		instC.runA();
	}



}
