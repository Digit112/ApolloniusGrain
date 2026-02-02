import java.lang.StringBuilder;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.stream.Stream;

/**
* Returned by TestSet.test()
* Records the results of tests to be formatted for output.
*/
public class TestSetResult {
	private ArrayList<TestResult> results;
	
	public TestSetResult() {
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
		
		for (TestResult result : results) {
			int currentCount = countsPerResultType.get(result.result);
			countsPerResultType.put(result.result, currentCount+1);
		}
		
		return countsPerResultType;
	}
	
	/**
	* Returns a string of characters for every result in this test.
	*/
	public String formatOneCharResults() {
		StringBuilder str = new StringBuilder();
		for (TestResult result : results) {
			str.append(result.getResultLetter());
		}
		
		return str.toString();
	}
	
	/**
	* Returns a nicely-formatted summary of all the results of the test.
	* Does not show the details of any failures.
	*/
	public String formatSummary() {
		// Obtain the length of the longest enum's name.
		int maxEnumNameLength = Stream.of(TestResult.Result.values()).max(
			(TestResult.Result a, TestResult.Result b) -> a.name().length() - b.name().length()
		).get().name().length();
		
		// Create a format string for padding enum names to equal length.
		String padToLengthFmtStr = String.format("%% %d%%s", maxEnumNameLength);
		
		// Obtain counts of each type of result.
		EnumMap<TestResult.Result, Integer> countsPerResultType = getCountsPerResultType();
		
		StringBuilder summary = new StringBuilder();
		summary.append(formatOneCharResults() + "\n");
		
		for (TestResult.Result resultType : TestResult.Result.values()) {
			String enumNameWithConsistentLength = String.format(padToLengthFmtStr, resultType.name());
			int count = countsPerResultType.get(resultType);
			
			// We show the pass count, fail count, and count of any other result which occurred.
			if (count > 0 || resultType == TestResult.Result.PASS || resultType == TestResult.Result.FAIL) {
				summary.append(String.format("%s: %d\n", enumNameWithConsistentLength, count));
			}
		}
		
		return summary.toString();
	}
}