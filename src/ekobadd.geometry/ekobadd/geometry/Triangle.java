package ekobadd.geometry;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

// Three-sided polygon.
public class Triangle {
	public Point a;
	public Point b;
	public Point c;

	public Triangle(Point a, Point b, Point c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public boolean contains(Point p) {
		Vector base = Point.difference(b, a);
		Vector left_arm = Point.difference(c, a);
		Vector right_arm = Point.difference(c, b);
		
		Vector rel_pos = Point.difference(p, a);
		
		double base_sqr_len = base.squared_length();
		double base_len = Math.sqrt(base_sqr_len);
		
		double proj_len = Vector.dot(left_arm, base) / base.length();
		double proj_sqr_len = proj_len*proj_len;
		double rel_proj_len = proj_len / base_len;
		
		// Intersection of the base with a line dropped from the shared vertex of the arms down perpendicular to the base.
		Vector drop = new Vector(rel_proj_len * base.x + a.x, rel_proj_len * base.y + a.y);
		
		double signed_height = Vector.dot(left_arm, new Vector(-base.y, base.x).normalized());
		
		double right_ratio = proj_len / base_sqr_len / signed_height;
		double left_ratio = 1 / base_sqr_len;
		double lower_ratio = 1 / signed_height / base_len;
		
		// This transformation matrix transforms the triangle A, B, C to (0, 0), (1, 0), (0, 1).
		Matrix triangle_transform = new Matrix(new double[][] {
			{base.x * left_ratio + base.y * right_ratio, base.y * left_ratio - base.x * right_ratio},
			{-base.y * lower_ratio, base.x * lower_ratio},
		});
		
		Vector new_point = Matrix.multiply(triangle_transform, rel_pos);
		
		// if (do_debug) {
			// System.out.println(String.format("Base Length: %.2f Proj Length: %.2f (Rel: %.2f) Signed Height: %.2f Ratios: %.2f %.2f %.2f",
				// base_len, proj_len, rel_proj_len, signed_height, right_ratio, left_ratio, lower_ratio));
			
			// System.out.println(String.format("%s within %s",
				// p.toString(), this.toString()));
			
			// System.out.println(String.format("Arms: %s %s %s Drop Point: %s Rel Point: %s Transformed Point: %s",
				// base.toString(), left_arm.toString(), right_arm.toString(), drop.toString(), rel_pos.toString(), new_point.toString()));
		// }
		
		return new_point.x >= 0 && new_point.y >= 0 && new_point.x + new_point.y <= 1;
	}
	
	/**
	* Determines whether any part of this segment is within the passed rectangle.
	* @return true if the shapes intersect, false otherwise.
	*/
	public boolean intersects(SgndAlgndRectangle rect) {
		if (new LineSegment(a, b).intersects(rect)) return true;
		if (new LineSegment(c, b).intersects(rect)) return true;
		if (new LineSegment(a, c).intersects(rect)) return true;
		
		// Cover the case where the rectangle is entirely within the triangle.
		if (contains(rect.a)) return true;
		
		// The edges of the triangle do not intersect the rectangle.
		// At least one point of the rectangle is outside the triangle.
		return false;
	}
	
	// Draws the outline of a triangle.
	public void draw(BufferedImage img, SgndAlgndRectangle viewport, Color color, Stroke stroke) {
		Vector scale_vector = new Vector(img.getWidth() / viewport.width(), img.getHeight() / viewport.height());
		
		Vector m = Vector.difference(a, viewport.a).scaled(scale_vector);
		Vector n = Vector.difference(b, viewport.a).scaled(scale_vector);
		Vector o = Vector.difference(c, viewport.a).scaled(scale_vector);
		
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(color);
		
		g2d.drawPolygon(new Polygon(
			new int[] {(int) m.x, (int) n.x, (int) o.x},
			new int[] {(int) m.y, (int) n.y, (int) o.y},
			3
		));
		
		g2d.dispose();
	}
	
	public String toString() {
		return String.format("/\\ (%.2f, %.2f) (%.2f, %.2f) (%.2f, %.2f)", a.x, a.y, b.x, b.y, c.x, c.y);
	}
}