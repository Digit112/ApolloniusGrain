package geometry;

public class Vector {
	double x;
	double y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Matrix m) {
		if (m.numCols() != 1 || m.numRows() != 2) {
			throw new Error("Matrix must be 2x1 (column vector) to be convertable to a vector.");
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
	
	public static double dot(Vector a, Vector b) {
		return a.x*b.x + a.y*b.y;
	}
	
	public Vector scaled(double factor) {
		return new Vector(x*factor, y*factor);
	}
	
	public String toString() {
		return String.format("-> (%.2f, %.2f)", x, y);
	}
}