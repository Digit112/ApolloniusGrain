package ekobadd.test;

/**
* Thrown in response to certain errors and exceptions encountered in the course of test execution and dependency resolution.
*/
public class TestConfigurationError extends Error {
	public TestConfigurationError(String message) {super(message);}
	public TestConfigurationError(String message, Throwable cause) {super(message, cause);}
};