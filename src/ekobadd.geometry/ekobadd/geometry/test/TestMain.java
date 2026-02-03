package ekobadd.geometry.test;

public class TestMain {
	public static void main(String[] args) {
		System.out.println(new TestTriangle().test().formatSummary(true));
		System.out.println(new TestLineSegment().test().formatSummary(true));
	}
}