package ekobadd.test;

import java.lang.StringBuilder;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.stream.Stream;

/**
* Records the results of all the tests on a single TestSet to be formatted for output.
*/
public class TestSetResult {
	private String testSetName;
	private ArrayList<TestResult> results;
	
	public TestSetResult(String testSetName) {
		this.testSetName = testSetName;
		this.results = new ArrayList<TestResult>();
	}
	
	public TestResult[] getResults() {
		return results.toArray(new TestResult[results.size()]);
	}
	
	public void addResult(TestResult result) {
		results.add(result);
	}
	
	/**
	* Returns a map from the possible TestResult Result values onto the number of times that occurred in the test set.
	*/
	public EnumMap<TestResult.Result, Integer> getCountsPerResultType() {
		EnumMap<TestResult.Result, Integer> countsPerResultType = new EnumMap<TestResult.Result, Integer>(TestResult.Result.class);
		
		for (TestResult.Result resultType : TestResult.Result.values()) {
			countsPerResultType.put(resultType, 0);
		}
		
		for (TestResult result : results) {
			int currentCount = countsPerResultType.get(result.result);
			countsPerResultType.put(result.result, currentCount+1);
		}
		
		return countsPerResultType;
	}
	
	/**
	* Returns a string of characters for every result in this test set.
	*/
	public String formatOneCharResults(boolean useColor) {
		StringBuilder str = new StringBuilder();
		String currentAnsiCode = "";
		
		for (TestResult result : results) {
			if (useColor) {
				// When color changes, insert the new ANSI code.
				String nextAnsiCode = result.getResultColorAnsiCode();
				if (!currentAnsiCode.equals(nextAnsiCode)) {
					currentAnsiCode = nextAnsiCode;
					str.append(currentAnsiCode);
				}
			}
			
			str.append(result.getResultLetter());
		}
		
		if (useColor) { // Reset formatting to default.
			str.append("\033[0m");
		}
		
		return str.toString();
	}
	
	/**
	* Returns a nicely-formatted summary of all the results of the test set.
	* Does not show the details of any failures.
	*/
	public String formatSummary(boolean useColor) {
		// Obtain the length of the longest enum's name.
		int maxEnumNameLength = Stream.of(TestResult.Result.values()).max(
			(TestResult.Result a, TestResult.Result b) -> a.name().length() - b.name().length()
		).get().name().length();
		
		// Create a format string for padding enum names to equal length.
		String padToLengthFmtStr = String.format("%%%ds", maxEnumNameLength);
		
		// Obtain counts of each type of result.
		EnumMap<TestResult.Result, Integer> countsPerResultType = getCountsPerResultType();
		
		StringBuilder summary = new StringBuilder();
		summary.append(String.format("==== TestSet %s Results ====\n", testSetName));
		summary.append(formatOneCharResults(useColor) + "\n");
		
		int numPassed = countsPerResultType.get(TestResult.Result.PASS);
		int numTotal = results.size();
		
		summary.append(String.format("(%d / %d PASSED)", numPassed, numTotal));
		if (numPassed < numTotal) {
			boolean havePrintedFirst = false;
			summary.append(" (");
			for (TestResult.Result resultType : TestResult.Result.values()) {
				String enumNameWithConsistentLength = String.format(padToLengthFmtStr, resultType.name());
				int count = countsPerResultType.get(resultType);
				
				// We show the pass count, fail count, and count of any other results which occurred.
				if (count > 0 && resultType != TestResult.Result.PASS) {
					if (havePrintedFirst) summary.append(", ");
					else havePrintedFirst = true;
					
					summary.append(String.format("%d %s\n", enumNameWithConsistentLength, count));
				}
			}
			summary.append(")");
		}
		
		summary.append("\n");
		
		return summary.toString();
	}
}