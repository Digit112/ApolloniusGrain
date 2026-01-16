package geometry;

public class Point implements LineSegmentLineSegmentIntersection, LineLineIntersection {
	double x;
	double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public static Vector difference(Point a, Point b) {
		return new Vector(a.x - b.x, a.y - b.y);
	}
	
	public Point translated(Vector a) {
		return new Point(x + a.x, y + a.y);
	}
	
	public String toString() {
		return String.format(">< (%.2f, %.2f)", x, y);
	}
}