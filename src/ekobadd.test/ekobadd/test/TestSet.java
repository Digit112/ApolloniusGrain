package ekobadd.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* Quick & Dirty unit-tester meant to be extended.
* Enumerates all methods and runs anything marked with @Test. Errors are caught and printed. Sums of passes and fails are displayed after testing is complete.
* <p>
* Fixtures may be supplied and are identified with the @Fixture annotation. Parameters are matched by name to the fixture method which should supply them. Matching is case-insensitive.
* Fixtures may be dependent upon one another.
* <p>
* Parameter names MUST be included in the class file to enable matching, which is not the default due to performance and security concerns. Your extending test set class MUST be compiled with -parameters.
* <p>
* Errors in the construction of the test class and in the resolution of fixtures are reported as "ERROR", as opposed to a test failure, which is an error caught from a test invocation and is reported as "FAIL".
*/
public abstract class TestSet {
	/**
	* Enumerate all methods on this class. All methods marked @Fixture are collected. Then, all methods marked @Test have their dependencies resolved and they are executed.
	* Exceptions in the tests are collated into statistics and displayed. The return values of tests are neither stored nor analyzed.
	* @throws TestConfigurationError When parameter resolution fails. For more details, check the return of getCause().
	*/
	public final void test(int depth_limit) {
		Method methods[] = getClass().getDeclaredMethods();
		HashMap<String, Method> fixtures = new HashMap<String, Method>();
		
		ArrayList<TestResult> tests = new ArrayList<TestResult>();
		int passes = 0;
		int unxpasses = 0;
		int fails = 0;
		int xfails = 0;
		int errors = 0;
		
		// Register fixtures
		for (Method method : methods) {
			if (method.isAnnotationPresent(Fixture.class)) {
				String fixtureName = method.getName().toLowerCase();
				if (fixtures.containsKey(fixtureName))
					throw new IllegalStateException("Two fixtures on one class must not have the same name. Matching is case-insensitive.");
				
				fixtures.put(fixtureName, method);
			}
		}
		
		// Run tests
		for (Method method : methods) {
			if (method.isAnnotationPresent(Test.class)) {
				boolean isExpectedFail = method.isAnnotationPresent(XFail.class);
				TestResult test;
				
				try {  // Catches errors resolving test params
					Object[] parameters = resolveParameters(method, fixtures, depth_limit);
					
					try { // Catches errors thrown by the test itself.
						try { // Catches errors that can be thrown by the invoke() method specifically.
							method.invoke(this, parameters);
						}
						catch (IllegalArgumentException cause) {
							throw new TestConfigurationError(String.format(
								"Unable to run test '%s' because at least one of its dependent fixtures did not return a type which can be unwrapped and/or converted to the specified type of the parameter.", method.getName()), cause);
						}
						catch (IllegalAccessException cause) {
							throw new TestConfigurationError(String.format(
								"Unable to run test '%s' due to the underlying method's access control.", method.getName()), cause);
						}
						catch (NullPointerException cause) {
							throw new Error(String.format( // Should be impossible since we always pass "this"....
								"Unable to run test '%s' because it is an instance method but recieved a null object.", method.getName()), cause);
						}
						catch (InvocationTargetException exc) {
							throw exc.getCause();
						}
						
						if (isExpectedFail) { // Test did not throw.
							test = new TestResult(method, TestResult.Result.UNXPASS, null, null);
							unxpasses++;
						}
						else {
							test = new TestResult(method, TestResult.Result.PASS, null, null);
							passes++;
						}
					}
					catch (TestConfigurationError err) {
						throw err;
					}
					catch (Throwable thr) {
						if (isExpectedFail) { // Test threw
							test = new TestResult(method, TestResult.Result.XFAIL, thr, null);
							xfails++;
						}
						else {
							test = new TestResult(method, TestResult.Result.FAIL, thr, null);
							fails++;
						}
					}
				}
				catch (TestConfigurationError err) {
					throw err;
				}
				catch (Throwable thr) { // Test setup or dependent fixture threw
					test = new TestResult(method, TestResult.Result.ERROR, null, thr);
					errors++;
				}
				
				// Individual test result printout.
				System.out.print(test.getResultString());
				tests.add(test);
			}
		}
		
		// Summary printout.
		System.out.println();
		System.out.println(String.format("Ran %d tests...", tests.size()));
		for (TestResult test : tests) {
			System.out.print(test.getResultLetter());
		}
		System.out.println();
		
		System.out.println(String.format("PASS: %d", passes));
		if (unxpasses > 0)
			System.out.println(String.format("UNX PASS: %d", unxpasses));
		
		System.out.println(String.format("FAIL: %d", fails));
		if (xfails > 0)
			System.out.println(String.format("EXP FAIL: %d", xfails));
		
		if (errors > 0)
			System.out.println(String.format("ERROR: %d", errors));
	}
	
	public final void assertThrows(Runnable runnable, Class exceptionClass) {
		try {
			runnable.run();
			throw new AssertionError("Failed to throw.");
		}
		catch (Throwable thr) {
			if (!exceptionClass.isInstance(thr)) {
				throw thr;
			}
		}
	}
	
	public static final void assertEquals(Object a, Object b) {
		if (!a.equals(b)) {
			throw new AssertionError(String.format("Entities failed equality assertion: '%s' != '%s'", a.toString(), b.toString()));
		}
	}
	
	/**
	* Generates an array of values satisfying the parameters of the supplied method.
	* <p>
	* Performs a case-insensitive lookup using the names of the parameters of the passed method into the supplied Map to obtain methods which produce them.
	* Looked-up fixtures have their own parameters recursively resolving using the same map. A hard depth limit prevents infinite recurse.
	* <p>
	* This function must be able to look up the names of methods and parameters of all the tests and fixtures in the class and therefore these identifies must be present in the compiled .class file.
	* @param method The method to obtain the parameters for.
	* @param fixtures A Map of strings (fixture names) onto methods which return those named values.
	* @param recursesRemaining The depth limit after which to throw.
	* @return An array of objects returned by fixtures with names matching the parameters of the passed method which may be supplied in the course of invoking the passed method.
	* @throws IllegalStateException When a parameter name is not a key into the fixtures dict and a suitable object can therefore not be resolved for that parameter.
	* @throws IllegalAccessException When, in the course of recursively invoking fixtures' dependencies, an invocation fails due to the fixture's access control restrictions.
	* @throws IllegalArgumentException When, in the course of recursively invoking fixtures' dependencies, an invocation fails due to a type mismatch between the parameter type and return type of the fixture with the same name.
	* @throws Throwable When a fixture throws an error, that error is rethrown.
	*/
	private final Object[] resolveParameters(Method method, Map<String, Method> fixtures, int recursesRemaining) {
		if (recursesRemaining == 0)
			throw new TestConfigurationError("Fixture depth limit exceeded. Likely caused by an infinite loop in fixture dependencies.");
		
		Parameter[] parameters = method.getParameters();
		Object objects[] = new Object[parameters.length];
		
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			if (!parameter.isNamePresent())
				throw new TestConfigurationError("It is necessary to compile your test classes with '-parameter' to allow matching parameter names with fixtures. You may need to delete the existing class file(s).");
			
			String name = parameter.getName().toLowerCase();
			
			if (fixtures.containsKey(name)) {
				Method fixture = fixtures.get(name);
				
				try {
					objects[i] = fixture.invoke(this, resolveParameters(fixture, fixtures, recursesRemaining-1));
				}
				catch (IllegalAccessException cause) {
					throw new TestConfigurationError(String.format(
						"Unable to run fixture '%s' due to the underlying method's access control.", name), cause);
				}
				catch (IllegalArgumentException cause) {
					throw new TestConfigurationError(String.format(
						"Unable to run fixture '%s' because at least one of its dependent fixtures did not return a type which can be unwrapped and/or converted to the specified type of the parameter.", name), cause);
				}
				catch (NullPointerException cause) {
					throw new Error(String.format( // Should be impossible since we always pass "this"....
						"Unable to run fixture '%s' because it is an instance method but recieved a null object.", name), cause);
				}
				catch (InvocationTargetException cause) {
					throw new TestConfigurationError(
						String.format("Exception encountered during invocation of fixture '%s'", name), cause.getCause());
				}
			}
			else {
				throw new TestConfigurationError(String.format(
					"No such fixture '%s' matching parameter at index %d of test '%s'. Ensure fixture names and parameter names match.",
					parameter.getName(), i, method.getName()
				));
			}
		}
		
		return objects;
	}
}