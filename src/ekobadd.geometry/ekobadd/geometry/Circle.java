package ekobadd.geometry;

public class Circle {
	public Point origin;
	public double radius;
	
	public Circle(Point origin, double radius) {
		this.origin = origin;
		this.radius = radius;
	}
	
	public void scale(double factor) {
		radius *= factor;
		origin.scale(factor);
	}
	
	public void translate(Vector offset) {
		origin.translate(offset);
	}
	
	public boolean contains(Point p) {
		double dx = p.x - origin.x;
		double dy = p.y - origin.y;
		
		// TODO: Uncomment
		// if (Math.abs(dx) > radius || Math.abs(dy) > radius) {
			// return false;
		// }
		
		return dx*dx + dy*dy < radius*radius;
	}
	
	public double diameter() {
		return radius*2;
	}
	
	public double area() {
		return Math.PI * radius * radius;
	}
	
	public String toString() {
		return String.format("() (%.2f, %.2f) %.2f", origin.x, origin.y, radius);
	}
}