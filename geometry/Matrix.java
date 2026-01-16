package geometry;

public class Matrix {
	double[][] d;
	
	public Matrix(int r, int c) {
		this.d = new double[r][c];
	}
	
	public Matrix(double[][] d) {
		this.d = d;
		
		for (int i = 1; i < d.length; i++) {
			if (d[i].length != d[0].length) {
				throw new Error("Matrix must be rectangular.");
			}
		}
	}
	
	public Matrix(Vector v) {
		this.d = new double[][] {{v.x}, {v.y}};
	}
	
	public int numRows() {
		return d.length;
	}
	
	public int numCols() {
		return d[0].length;
	}
	
	public static Matrix multiply(Matrix a, Matrix b) {
		if (a.numCols() != b.numRows()) {
			throw new Error("The left matrix's width must equal the right matrix's height.");
		}
		
		Matrix ret = new Matrix(a.numRows(), b.numCols());
		
		for (int r = 0; r < a.numRows(); r++) {
			for (int c = 0; c < b.numCols(); c++) {
				for (int i = 0; i < a.numCols(); i++) {
					double product = a.d[r][i] * b.d[i][c];
					ret.d[r][c] += product;
				}
			}
		}
		
		return ret;
	}
	
	public static Vector multiply(Matrix a, Vector b) {
		return new Vector(Matrix.multiply(a, new Matrix(b)));
	}
}