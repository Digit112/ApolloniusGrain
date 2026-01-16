package geometry;

public class LineSegment implements CollisionResult1D {
	Point a;
	Point b;
	
	public LineSegment(Point a, Point b) {
		this.a = a;
		this.b = b;
	}
	
	public LineSegment(Point a, Vector delta) {
		this.a = a;
		this.b = a.translated(delta);
	}
}