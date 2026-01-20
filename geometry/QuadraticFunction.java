package geometry;

// Represents an equation of the form y = ax^2 + bx + c
public class QuadraticFunction implements Function<Double, Double> {
	public double a;
	public double b;
	public double c;
	
	public QuadraticFunction(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public Double evaluate(Double x) {
		return a*x*x + b*x + c;
	}
	
	// Returns an array of the two roots.
	// Returns [NaN, NaN] if there are no roots.
	// If there is one root, both values in the array will equal it.
	public double[] getRoots() {
		double radicand = b*b - 4*a*c;
		
		if (radicand < 0) {
			return new double[] {Double.NaN, Double.NaN};
		}
		else {
			return new double[] {
				(-b - Math.sqrt(radicand)) / (2*a),
				(-b + Math.sqrt(radicand)) / (2*a)
			};
		}
	}
	
	public String toString() {
		return String.format("%.2fx^2 + %.2fx + %.2f", a, b, c);
	}
}