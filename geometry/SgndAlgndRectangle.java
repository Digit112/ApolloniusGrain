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
	
	/** Determines whether the rectangle contains the given point. */
	public boolean contains(Point p) {
		return left() <= p.x && right() >= p.x && bottom() <= p.y && top() >= p.y;
	}
	
	/** Determines whether the passed triangle lies entirely within this rect. */
	public boolean contains(Triangle t) {
		return contains(t.a) && contains(t.b) && contains(t.c);
	}
	
	/**
	* Decreases the width and height by dividing them by the passed factor.
	* Then center of the rectangle is fixed in place.
	* @param factor The zoom factor. Values greater than 1 shrink the rectangle, values between 0 and 1 will grow it. Negative values will also invert it.
	*/
	public void zoom(double factor) {
		double half_delta_width  = signedWidth()  * (1 - 1 / factor) / 2;
		double half_delta_height = signedHeight() * (1 - 1 / factor) / 2;
		
		a = a.translated(new Vector( half_delta_width,  half_delta_height));
		b = b.translated(new Vector(-half_delta_width, -half_delta_height));
	}
	
	/**
	* Same as zoom(), but returns a new rectangle instead of modifying the callee.
	* @return A new rectangle zoomed by the given factor.
	* @see #zoom(double)
	*/
	public SgndAlgndRectangle zoomed(double factor) {
		double half_delta_width  = signedWidth()  * (1 - 1 / factor) / 2;
		double half_delta_height = signedHeight() * (1 - 1 / factor) / 2;
		
		return new SgndAlgndRectangle(
			a.translated(new Vector( half_delta_width,  half_delta_height)),
			b.translated(new Vector(-half_delta_width, -half_delta_height))
		);
	}
	
	public void translate(Vector offset) {
		a = a.translated(offset);
		b = b.translated(offset);
	}
	
	public SgndAlgndRectangle translated(Vector offset) {
		return new SgndAlgndRectangle(
			a.translated(offset),
			b.translated(offset)
		);
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