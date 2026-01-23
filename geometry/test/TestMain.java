package geometry.test;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.lang.AssertionError;
import java.lang.reflect.*;
import java.lang.StackWalker.Option;

import java.util.HashMap;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface Test {}

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface Fixture {}

/**
* Quick & Dirty unit-tester calls all methods with @Test on extending classes.
* <p>
* Errors are caught and printed.
* Use assert to easily throw on a failed condition.
* Use assertThrows to ensure that a certain error is thrown as expected.
* <p>
* Extending classes must annotate all tests with @Test. 
* Methods may be used as generators for test dependencies. Just declare a parameter to a test with the same name as a method annotated @Fixture.
* Fixtures can use other fixtures for their parameters as well.
*/
abstract class TestSet {
	public final void test() {
		Method methods[] = getClass().getDeclaredMethods();
		HashMap<String, Method> fixtures = new HashMap<String, Method>();
		
		// Register fixtures
		for (Method method : methods) {
			if (method.isAnnotationPresent(Fixture.class)) {
				fixtures.add(method.getName().toLowerCase(), method);
			}
		}
		
		for (Method method : methods) {
			if (method.isAnnotationPresent(Test.class)) {
				String test_header = String.format("Testing %s(%s)", method.getName(), "...");
				try {
					System.out.println();
					//method.invoke(this);
					
					System.out.println(String.format("%s... PASS", test_header));
				}
				catch (Throwable err) {
					System.out.println(err.getMessage());
					System.out.println(String.format("%s... FAIL", test_header));
				}
			}
		}
	}
	
	public final void assertThrows(Runnable runnable, Class error_class) {
		try {
			runnable.run();
			throw new AssertionError("Failed to throw.");
		}
		catch (Throwable err) {
			if (!error_class.isInstance(err)) {
				throw err;
			}
		}
	}
	
	private String red(String text) {
		return text;
	}
}

public class TestMain {
	public static void main(String[] args) {
		new TestTriangle().test();
	}
}