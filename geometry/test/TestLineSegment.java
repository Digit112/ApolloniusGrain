package geometry.test;

import geometry.*;
import test.TestSet;

public class TestLineSegment extends TestSet {
	@Test
	public static void testIntersectsSgndAlgndRect() {
		SgndAlgndRectangle rect = new SgndAlgndRectangle(new Point(-1, -1), new Point(1, 1));
		
		// Test collisions tangent to side.
		assertEquals(new LineSegment(new Point(-2,  0), new Point(-1,  0)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 2,  0), new Point( 1,  0)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 0, -2), new Point( 0, -1)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 0,  2), new Point( 0,  1)).intersects(rect), true);
		
		assertEquals(new LineSegment(new Point(-1,  0), new Point(-2,  0)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 1,  0), new Point( 2,  0)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 0, -1), new Point( 0, -2)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 0,  1), new Point( 0,  2)).intersects(rect), true);
		
		// Test coinciding lines
		assertEquals(new LineSegment(new Point(-1, -2), new Point(-1,  2)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 1, -2), new Point( 1,  2)).intersects(rect), true);
		assertEquals(new LineSegment(new Point(-2, -1), new Point( 2, -1)).intersects(rect), true);
		assertEquals(new LineSegment(new Point(-2,  1), new Point( 2,  1)).intersects(rect), true);
		
		assertEquals(new LineSegment(new Point(-1, -0.8), new Point(-1, 0.8)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 1, -0.8), new Point( 1, 0.8)).intersects(rect), true);
		assertEquals(new LineSegment(new Point(-0.8, -1), new Point(0.8, -1)).intersects(rect), true);
		assertEquals(new LineSegment(new Point(-0.8,  1), new Point(0.8,  1)).intersects(rect), true);
		
		// Test intersections with no vertices in the other shape.
		assertEquals(new LineSegment(new Point(-2,  0), new Point( 2,  0)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 2,  0), new Point(-2,  0)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 0, -2), new Point( 0,  2)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 0,  2), new Point( 0, -2)).intersects(rect), true);
		
		// Segments tangent to corners of rect
		assertEquals(new LineSegment(new Point( 0,  2), new Point( 2,  0)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 0,  2), new Point(-2,  0)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 0, -2), new Point( 2,  0)).intersects(rect), true);
		assertEquals(new LineSegment(new Point( 0, -2), new Point(-2,  0)).intersects(rect), true);
	}
}