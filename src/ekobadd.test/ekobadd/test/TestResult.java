package ekobadd.test;

import java.lang.reflect.Method;
import java.lang.StringBuilder;

/**
* Represents a test's method and result, including thrown errors.
*/
class TestResult {
	public enum Result {
		PASS, UNXPASS, FAIL, XFAIL
	}
	
	Method method;
	Result result;
	
	Throwable threw; // Thrown by test function.
	Throwable error; // Thrown by test suite; test could not be run...
	
	public TestResult(Method method, Result result, Throwable threw) {
		this.method = method;
		this.result = result;
		
		this.threw = threw;
	}
	
	/**
	* Get a string containing the test name and result.
	*/
	public String formatResultHeader() {
		return String.format("Test %s... %s", method.getName(), result.name());
	}
	
	/**
	* Get a string containing the test name, result, and brief description of any caught error.
	*/
	public String formatOneLineDetailedResult() {
		StringBuilder str = new StringBuilder();
		
		str.append(String.format("Test %s (%s)", method.getName(), result.name()));
		if (threw != null) {
			StackTraceElement[] stacktrace = threw.getStackTrace();
			str.append(String.format(" (caught %s at %s.%d)", threw.getClass().getName(), stacktrace[0].getFileName(), stacktrace[0].getLineNumber()));
		}
		
		return str.toString();
	}
	
	/**
	* Get a string consisting of a test header and the error encountered during the test, if any.
	*/
	public String formatDetailedResult() {
		StringBuilder str = new StringBuilder();
		
		str.append(formatResultHeader() + '\n');
		
		if (threw != null) {
			str.append(String.format("Caught %s\n", threw.toString()));
			
			StackTraceElement[] stackTrace = threw.getStackTrace();
			for (StackTraceElement element : stackTrace) {
				str.append("\tat " + element.toString() + "\n");
			}
		}
		
		return str.toString();
	}
	
	/**
	* Returns a single character representing the outcome of this test.
	*/
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
		}
		
		throw new Error(String.format("No result letter for result '%s'", result.name()));
	}
	
	
	/**
	* Returns an ANSI escape sequence which can be inserted into text to color results for clarity.
	*/
	public String getResultColorAnsiCode() {
		switch (result) {
			case Result.PASS:
				return "\033[32m";
			case Result.UNXPASS:
				return "\033[33m";
			case Result.FAIL:
				return "\033[31m";
			case Result.XFAIL:
				return "\033[33m";
		}
		
		throw new Error(String.format("No result color for result '%s'", result.name()));
	}
}