package ekobadd.geometry.test;

import ekobadd.geometry.*;
import ekobadd.test.*;

public class TestTriangle extends TestSet {
	@Test
	public static void testPointInclusionWithUnitRightTri() {
		Triangle unitRightTri = new Triangle(new Point(0, 0), new Point(1, 0), new Point(0, 1));
		
		assertEquals(unitRightTri.contains(new Point(0, 0)), true);
		assertEquals(unitRightTri.contains(new Point(0, 1)), true);
		assertEquals(unitRightTri.contains(new Point(1, 0)), true);
		assertEquals(unitRightTri.contains(new Point(0.2, 0.2)), true);
		assertEquals(unitRightTri.contains(new Point(0.5, 0.5)), true);
		
		assertEquals(unitRightTri.contains(new Point(0.5, -1)), false);
		assertEquals(unitRightTri.contains(new Point(-1, 0.5)), false);
		assertEquals(unitRightTri.contains(new Point(1, 1)), false);
		
		assertEquals(unitRightTri.contains(new Point(2, -1)), false);
		assertEquals(unitRightTri.contains(new Point(-1, 2)), false);
		assertEquals(unitRightTri.contains(new Point(-1, -1)), false);
	}
	
	@Test
	public static void testPointInclusionWithOtherTris() {
		Triangle otherTri1 = new Triangle(new Point(4, 3), new Point(2, 2), new Point(3, 4));
		
		assertEquals(otherTri1.contains(new Point(4, 3)), true);
		assertEquals(otherTri1.contains(new Point(2, 2)), true);
		assertEquals(otherTri1.contains(new Point(3, 4)), true);
		
		assertEquals(otherTri1.contains(new Point(3, 3)), true);
		assertEquals(otherTri1.contains(new Point(3, 2.5)), true);
		assertEquals(otherTri1.contains(new Point(3.5, 3.5)), true);
		
		assertEquals(otherTri1.contains(new Point(3, 2)), false);
		assertEquals(otherTri1.contains(new Point(4, 4)), false);
		assertEquals(otherTri1.contains(new Point(2, 3)), false);
		assertEquals(otherTri1.contains(new Point(2, 5)), false);
		assertEquals(otherTri1.contains(new Point(5, 3)), false);
		
		Triangle otherTri2 = new Triangle(new Point(4, 3), new Point(1, 5), new Point(3, 4));
		
		assertEquals(otherTri2.contains(new Point(2, 4.5)), true);
		assertEquals(otherTri2.contains(new Point(3, 3.8)), true);
		
		assertEquals(otherTri2.contains(new Point(3, 4.2)), false);
		assertEquals(otherTri2.contains(new Point(2, 4.2)), false);
		assertEquals(otherTri2.contains(new Point(4, 4)), false);
		
		Triangle otherTri3 = new Triangle(new Point(-4, -3), new Point(-1, -5), new Point(0, -3));
		
		assertEquals(otherTri3.contains(new Point(-1, -4)), true);
		assertEquals(otherTri3.contains(new Point(-3, -3.5)), true);
		assertEquals(otherTri3.contains(new Point(0, -3)), true);
		assertEquals(otherTri3.contains(new Point(0, -3)), true);
		
		assertEquals(otherTri3.contains(new Point(0, -4)), false);
		assertEquals(otherTri3.contains(new Point(1, -0.5)), false);
		assertEquals(otherTri3.contains(new Point(-2, -2)), false);
	}
	
	@Test
	public static void testSgndAlgndRectIntersectionWithUnitRightTri() {
		Triangle unitRightTri = new Triangle(new Point(0, 0), new Point(1, 0), new Point(0, 1));
		
		// Test rectangle sides tangent to triangle vertices.
		assertEquals(unitRightTri.intersects(new SgndAlgndRectangle(new Point(2, 1), new Point(1, -1))), true);
		assertEquals(unitRightTri.intersects(new SgndAlgndRectangle(new Point(1, -1), new Point(2, 1))), true);
		assertEquals(unitRightTri.intersects(new SgndAlgndRectangle(new Point(-1, 2), new Point(1, 1))), true);
		assertEquals(unitRightTri.intersects(new SgndAlgndRectangle(new Point(1, 1), new Point(-1, 2))), true);
		assertEquals(unitRightTri.intersects(new SgndAlgndRectangle(new Point(-1, -1), new Point(0, 0))), true);
		assertEquals(unitRightTri.intersects(new SgndAlgndRectangle(new Point(0, 0), new Point(-1, -1))), true);
		
		// Test rectangle includes the whole triangle.
		assertEquals(unitRightTri.intersects(new SgndAlgndRectangle(new Point(-1, -1), new Point(2, 2))), true);
		assertEquals(unitRightTri.intersects(new SgndAlgndRectangle(new Point(2, -1), new Point(-1, 2))), true);
		
		// Test triangle includes the whole rectangle.
		assertEquals(unitRightTri.intersects(new SgndAlgndRectangle(new Point(0.1, 0.1), new Point(0.4, 0.4))), true);
		
		// Test edge intersection where no vertex is within the other shape.
		assertEquals(unitRightTri.intersects(new SgndAlgndRectangle(new Point(0.2, 1), new Point(0.8, -1))), true);
		assertEquals(unitRightTri.intersects(new SgndAlgndRectangle(new Point(1, 0.2), new Point(-1, 0.8))), true);
	}
	
	@Test
	public static void testSgndAlgndRectIntersectionWithOtherRightTri() {
		Triangle otherTri = new Triangle(new Point(-2, 7), new Point(-1, 1), new Point(-4, 2));
		
		// Test rectangle vertices tangent to triangle edges.
		assertEquals(otherTri.intersects(new SgndAlgndRectangle(new Point(-6, 6), new Point(-3, 4.5))), true);
		assertEquals(otherTri.intersects(new SgndAlgndRectangle(new Point(4, 4), new Point(-1.5, 6))), true);
		assertEquals(otherTri.intersects(new SgndAlgndRectangle(new Point(-2.5, 1.5), new Point(-3, 0))), true);
		
		// Test rectangle includes the whole triangle.
		assertEquals(otherTri.intersects(new SgndAlgndRectangle(new Point(0, 0), new Point(-6, 8))), true);
		assertEquals(otherTri.intersects(new SgndAlgndRectangle(new Point(-6, 0), new Point(0, 8))), true);
		
		// Test triangle includes the whole rectangle.
		assertEquals(otherTri.intersects(new SgndAlgndRectangle(new Point(-2, 2), new Point(-3, 4))), true);
		
		// Test edge intersection where no vertex is within the other shape.
		assertEquals(otherTri.intersects(new SgndAlgndRectangle(new Point(0, 1.5), new Point(-3, 5))), true);
		assertEquals(otherTri.intersects(new SgndAlgndRectangle(new Point(-2.5, 1), new Point(-3.5, 7))), true);
	}
}