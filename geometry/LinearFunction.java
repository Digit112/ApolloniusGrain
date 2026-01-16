package geometry;

// Represents an equation of the form y = mx + b
public class LinearFunction implements Function<double, double> {
	double slope;
	double offset;
	
	public LinearFunction(double slope, double offset) {
		this.slope = slope;
		this.offset = offset;
	}
	
	public double evaluate(double x) {
		return slope*x + offset;
	}
	
	public String toString() {
		return String.format("%.2fx + %.2f", slope, offset);
	}
}