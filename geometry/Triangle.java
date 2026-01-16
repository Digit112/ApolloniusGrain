package geometry;

// Three-sided polygon.
public class Triangle {
	Point a;
	Point b;
	Point c;

	public Triangle(Point a, Point b, Point c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public boolean contains(Point p, boolean do_debug) {
		Vector base = Point.difference(b, a);
		Vector left_arm = Point.difference(c, a);
		Vector right_arm = Point.difference(c, b);
		
		Vector rel_pos = Point.difference(p, a);
		
		double base_sqr_len = base.squared_length();
		double base_len = Math.sqrt(base_sqr_len);
		
		double proj_len = Vector.dot(left_arm, base) / base.length();
		double proj_sqr_len = proj_len*proj_len;
		double rel_proj_len = proj_len / base_len;
		
		// Intersection of the base with a line dropped from the shared vertex of the armss down perpendicular to the base.
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
		
		if (do_debug) {
			System.out.println(String.format("Base Length: %.2f Proj Length: %.2f (Rel: %.2f) Signed Height: %.2f Ratios: %.2f %.2f %.2f",
				base_len, proj_len, rel_proj_len, signed_height, right_ratio, left_ratio, lower_ratio));
			
			System.out.println(String.format("%s within %s",
				p.toString(), this.toString()));
			
			System.out.println(String.format("Arms: %s %s %s Drop Point: %s Rel Point: %s Transformed Point: %s",
				base.toString(), left_arm.toString(), right_arm.toString(), drop.toString(), rel_pos.toString(), new_point.toString()));
		}
		
		return new_point.x >= 0 && new_point.y >= 0 && new_point.x + new_point.y <= 1;
	}
	
	public String toString() {
		return String.format("/\\ %s - %s - %s", a.toString(), b.toString(), c.toString());
	}
}