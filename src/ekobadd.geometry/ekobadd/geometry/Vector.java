package ekobadd.geometry;

public class Vector {
	public double x;
	public double y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Matrix m) {
		if (m.numCols() != 1 || m.numRows() != 2) {
			throw new Error("Matrix must be 2x1 (column vector) to be convertible to a vector.");
		}
		
		this.x = m.d[0][0];
		this.y = m.d[1][0];
	}
	
	public double squared_length() {
		return x*x + y*y;
	}
	
	public double length() {
		return Math.sqrt(squared_length());
	}
	
	public Vector normalized() {
		double len = length();
		return new Vector(x/len, y/len);
	}
	
	public Vector normalized(double new_len) {
		double len = length();
		return new Vector(x/len*new_len, y/len*new_len);
	}
	
	public static Vector difference(Point a, Point b) {
		return new Vector(a.x - b.x, a.y - b.y);
	}
	
	public static double dot(Vector a, Vector b) {
		return a.x*b.x + a.y*b.y;
	}
	
	public Vector scaled(Vector factor) {
		return new Vector(x*factor.x, y*factor.y);
	}
	
	public Vector scaled(double factor) {
		return new Vector(x*factor, y*factor);
	}
	
	/**
	* Clamps the individual coordinates such that the returned vector always lies within the passed rectangle.
	* If the passed vector is outside the bounds, the returned vector will lay on one of its edges or corners.
	* @param bounds The bounding box that the returned vector must lie on.
	*/
	public Vector clamped(SgndAlgndRectangle bounds) {
		return new Vector(
			Math.max(Math.min(x, bounds.right()), bounds.left()),
			Math.max(Math.min(y, bounds.top()), bounds.bottom())
		);
	}
	
	public String toString() {
		return String.format("-> (%.2f, %.2f)", x, y);
	}
}