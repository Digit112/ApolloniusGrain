package geometry;

// Represents an equation of the form c1*x + c2*y + b = 0;
public final class Line implements LineLineIntersection {
	public double c1;
	public double c2;
	public double b;
	
	// TODO: Test
	public static LineLineIntersection intersect(Line A, Line B) {
		double y_numer = A.c1*B.b - B.c1*A.b;
		double y_denom = A.c2*B.c1-A.c1*B.c2;
		
		// Check for parallel lines.
		if (y_denom == 0) {
			if (y_numer == 0) {
				return new Line(A);
			}
			else {
				return null;
			}
		}
		
		double y_comp = y_numer / y_denom;
		double x_comp = -(B.c2 * y_comp + B.b) / B.c1;
		
		return new Point(x_comp, y_comp);
	}
	
	public Line(double c1, double c2, double b) {
		this.c1 = c1;
		this.c2 = c2;
		this.b = b;
	}
	
	public Line(Line that) {
		this.c1 = that.c1;
		this.c2 = that.c2;
		this.b = that.b;
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
	public double distance(Point p) {
		return Math.abs(signedDistance(p));
	}
	
	// The distance to origin, normal vector and tangent vector will all scale by this factor.
	public Line scaled(double factor) {
		return new Line(c1*factor, c2*factor, b*factor*factor);
	}
	
	public Vector getTangent() {
		return new Vector(c2, -c1);
	}
	
	public Vector getNormal() {
		return new Vector(c1, c2);
	}
	
	// Perpendicular distance along the normal from the passed point to the line.
	public double signedDistance(Point p) {
		return (c1*p.x + c2*p.y + b) / Math.sqrt(c1*c1 + c2*c2);
	}
}