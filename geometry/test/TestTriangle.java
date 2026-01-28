package geometry.test;

import geometry.*;

public class TestTriangle extends TestSet {
	@Fixture
	public static SgndAlgndRectangle unitSgndAlgndRect() {
		return new SgndAlgndRectangle(new Point(0, 0), new Point(1, 1));
	}
	
	@Test
	public static void testRectangleCollision(Circle unitSgndAlgndRect) {
	}
}