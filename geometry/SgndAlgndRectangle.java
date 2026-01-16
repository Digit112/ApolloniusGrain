package geometry;

// Signed, Axis-Aligned Rectangle.
// Represents an axis-aligned Rectangle defined by two points.
// Exchanging the x components of the points negates the signed width of the rectangle, likewise with the y components and signed height.
public class SgndAlgndRectangle {
	public Point a;
	public Point b;
	
	public SgndAlgndRectangle(Point a, Point b) {
		this.a = a;
		this.b = b;
	}
	
	public SgndAlgndRectangle(Point a, Vector size) {
		this.a = a;
		this.b = a.translated(size);
	}
	
	public double width() {
		return Math.abs(signedWidth());
	}
	
	public double height() {
		return Math.abs(signedHeight());
	}
	
	public double aspectRatio() {
		return Math.abs(signedAspectRatio());
	}
	
	// Scales the passed vector along the axes of this rectangle and translates according to the rectangle's offset.
	// Passing (0, 0) returns this.a and (1, 1) returns this.b
	// Passing (0, 1) returns (a.x, b.y), passing (1, 0) returns (b.x, a.y), passing (0.5, 0.5) returns the center of the rectangle.
	public Point bilerp(Vector t) {
		return new Point(
			signedWidth() * t.x + a.x,
			signedHeight() * t.y + a.y
		);
	}
	
	// Decreases the width and height by a factor of the reciprocal of the input value.
	// Center of the rectangle is fixed in place.
	public void zoom(double factor) {
		double half_delta_width  = signedWidth()  * (1 - 1 / factor) / 2;
		double half_delta_height = signedHeight() * (1 - 1 / factor) / 2;
		
		a = a.translated(new Vector( half_delta_width,  half_delta_height));
		b = b.translated(new Vector(-half_delta_width, -half_delta_height));
	}
	
	public void translate(Vector offset) {
		a = a.translated(offset);
		b = b.translated(offset);
	}
	
	public double signedWidth() {
		return b.x - a.x;
	}
	
	public double signedHeight() {
		return b.y - a.y;
	}
	
	public double signedAspectRatio() {
		return signedWidth() / signedHeight();
	}
	
	public double left() {
		return Math.min(a.x, b.x);
	}
	
	public double bottom() {
		return Math.min(a.y, b.y);
	}
	
	public double right() {
		return Math.max(a.x, b.x);
	}
	
	public double top() {
		return Math.max(a.y, b.y);
	}
}