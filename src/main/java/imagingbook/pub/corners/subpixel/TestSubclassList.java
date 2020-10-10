package imagingbook.pub.corners.subpixel;

public class TestSubclassList {

	public static void main(String[] args) {
		Class<?> clazz = MaxLocator.class;
		Class<?>[] subclazzes = clazz.getDeclaredClasses();
		
		for (Class<?> c : subclazzes) {
			System.out.format("%s: local=%b member=%b interface=%b enum=%b enclosedBy=%s\n", c.getName(), c.isLocalClass(), c.isMemberClass(), c.isInterface(), c.isEnum(), c.getEnclosingClass().getSimpleName());
		}
	}
}
