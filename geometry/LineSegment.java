package geometry;

public final class LineSegment implements LineSegmentLineSegmentIntersection {
	public Point a;
	public Point b;
	
	public LineSegment(Point a, Point b) {
		this.a = a;
		this.b = b;
	}
	
	public LineSegment(Point a, Vector delta) {
		this.a = a;
		this.b = a.translated(delta);
	}
}