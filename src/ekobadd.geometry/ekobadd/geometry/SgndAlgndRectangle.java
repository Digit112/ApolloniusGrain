package ekobadd.geometry;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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
	* Modifies the width and height by multiplying them by the passed factor.
	* The center of the rectangle is fixed in place.
	* @param scale The scale factor. Values greater than 1 grow the rectangle, values between 0 and 1 will shrink it. Negative values will invert it.
	*/
	public void scale(double scale) {
		double half_delta_width  = signedWidth()  * (1 - scale) / 2;
		double half_delta_height = signedHeight() * (1 - scale) / 2;
		
		a = a.translated(new Vector( half_delta_width,  half_delta_height));
		b = b.translated(new Vector(-half_delta_width, -half_delta_height));
	}
	
	/**
	* Same as scale(), but returns a new rectangle instead of modifying the callee.
	* @return A new rectangle scaled by the given factor.
	* @see #scale(double)
	*/
	public SgndAlgndRectangle scaled(double factor) {
		double half_delta_width  = signedWidth()  * (1 - 1 / factor) / 2;
		double half_delta_height = signedHeight() * (1 - 1 / factor) / 2;
		
		return new SgndAlgndRectangle(
			a.translated(new Vector( half_delta_width,  half_delta_height)),
			b.translated(new Vector(-half_delta_width, -half_delta_height))
		);
	}
	
	/**
	* Equivalent to scaling by the reciprocal of the supplied factor.
	* The center of the rectangle is fixed in place.
	* @param factor The zoom factor. Values greater than 1 shrink the rectangle, values between 0 and 1 will grow it. Negative values will also invert it.
	* @see #scale(double)
	*/
	public void zoom(double factor) {
		scale(1 / factor);
	}
	
	/**
	* Same as zoom(), but returns a new rectangle instead of modifying the callee.
	* @return A new rectangle zoomed by the given factor.
	* @see #zoom(double)
	* @see #scale(double)
	*/
	public SgndAlgndRectangle zoomed(double factor) {
		return scaled(1 / factor);
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
	
	/**
	* Returns the unsigned distance of the passed point to the edge of the rectangle.
	*/
	// TODO: Test
	public double distanceToEdge(Point p) {
		Vector to_nearest_point = new Vector(
			Math.min(Math.abs(p.x - a.x), Math.abs(p.x - b.x)),
			Math.min(Math.abs(p.y - a.y), Math.abs(p.y - b.y))
		);
		
		return to_nearest_point.length();
	}
	
	// Draws the outline of a rectangle.
	public void draw(BufferedImage img, SgndAlgndRectangle viewport, Color color, Stroke stroke) {
		Vector scale_vector = new Vector(img.getWidth() / viewport.width(), img.getHeight() / viewport.height());
		
		Vector m = Vector.difference(a, viewport.a).scaled(scale_vector);
		Vector n = Vector.difference(b, viewport.a).scaled(scale_vector);
		
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(color);
		g2d.setStroke(stroke);
		
		g2d.drawRect((int) m.x, (int) m.y, (int) (n.x - m.x), (int) (n.y - m.y));
		
		g2d.dispose();
	}
	
	public String toString() {
		return String.format("[] (%.2f, %.2f), (%.2f, %.2f)", a.x, a.y, b.x, b.y);
	}
}