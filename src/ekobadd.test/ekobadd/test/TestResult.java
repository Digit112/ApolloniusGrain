package ekobadd.test;

import java.lang.reflect.Method;
import java.lang.StringBuilder;

/**
* Represents a test's method and result, including thrown errors.
*/
class TestResult {
	public enum Result {
		PASS, UNXPASS, FAIL, XFAIL, ERROR
	}
	
	Method method;
	Result result;
	
	Throwable threw; // Thrown by test function.
	Throwable error; // Thrown by test suite; test could not be run...
	
	public TestResult(Method method, Result result, Throwable threw, Throwable error) {
		this.method = method;
		this.result = result;
		
		this.threw = threw;
		this.error = error;
	}
	
	/**
	* 
	*/
	public String getResultString() {
		StringBuilder string = new StringBuilder();
		
		string.append(String.format("Method %s... %s\n", method.getName(), result.name()));
		
		if (threw != null) {
			string.append(String.format("During test, caught %s\n", threw.toString()));
			
			StackTraceElement[] stackTrace = threw.getStackTrace();
			for (StackTraceElement element : stackTrace) {
				string.append("\tat " + element.toString() + "\n");
			}
		}
		
		if (error != null) {
			string.append(String.format("During fixture resolution, caught %s\n", error.toString()));
			
			StackTraceElement[] stackTrace = error.getStackTrace();
			for (StackTraceElement element : stackTrace) {
				string.append("\tat " + element.toString() + "\n");
			}
		}
		
		return string.toString();
	}
	
	public String getResultLetter() {
		switch (result) {
			case Result.PASS:
				return ".";
			case Result.UNXPASS:
				return "U";
			case Result.FAIL:
				return "F";
			case Result.XFAIL:
				return "X";
			case Result.ERROR:
				return "E";
		}
		
		throw new Error(String.format("No result letter for result '%s'", result.name()));
	}
}