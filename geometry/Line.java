package geometry;

// Represents an equation of the form c1*x + c2*y + b = 0;
public class Line {
	public double c1;
	public double c2;
	public double b;
	
	public static LineLineIntersection intersect(Line A, Line B) {
		double y_numer = A.c1*B.b - B.c1*A.b;
		double y_denom = A.c2*
	}
	
	public Line(double c1, double c2, double b) {
		this.c1 = c1;
		this.c2 = c2;
		this.b = b;
	}
	
	public Line(LinearFunction func) {
		this.c1 = func.slope;
		this.c2 = -1;
		this.b = fun.offset;
	}
	
	// The length of the normal and tangent vectors will be equal to the distance between the passed points.
	// Swapping the order of the points will negate the normal and tangent vectors.
	public Line(Point a, Point b) {
		this.c1 = a.y - b.y;
		this.c2 = b.x - a.x;
		this.b = a.x*b.y - a.y*b.x;
	}
	
	// Minimum distance from this line to the point.
	public distance(Point p) {
		return Math.abs(signedDistance(p));
	}
	
	// The distance to origin, normal vector and tangent vector will all scale by this factor.
	public scaled(double factor) {
		return new Line(c1*factor, c2*factor, b*factor*factor);
	}
	
	public Vector getTangent() {
		return new Vector(c2, -c1);
	}
	
	public Vector getNormal() {
		return new Vector(c1, c2);
	}
	
	// Perpendicular distance along the normal from the passed point to the line.
	public signedDistance(Point p) {
		return (c1*p.x + c2*p.y + b) / Math.sqrt(c1*c1 + c2*c2);
	}
}