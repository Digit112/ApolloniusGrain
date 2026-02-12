package ekobadd.geometry;

public final class Point implements LineSegmentLineSegmentIntersection, LineLineIntersection {
	public double x;
	public double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector position() {
		return new Vector(x, y);
	}
	
	public void scale(double factor) {
		x *= factor;
		y *= factor;
	}
	
	public static Vector difference(Point a, Point b) {
		return new Vector(a.x - b.x, a.y - b.y);
	}
	
	public void translate(Vector offset) {
		x += offset.x;
		y += offset.y;
	}
	
	public Point translated(Vector a) {
		return new Point(x + a.x, y + a.y);
	}
	
	public String toString() {
		return String.format(">< (%.2f, %.2f)", x, y);
	}
}