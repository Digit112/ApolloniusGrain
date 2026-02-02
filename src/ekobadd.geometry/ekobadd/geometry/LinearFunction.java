package ekobadd.geometry;

// Represents an equation of the form y = mx + b
public class LinearFunction implements Function<Double, Double> {
	public double slope;
	public double offset;
	
	public LinearFunction(double slope, double offset) {
		this.slope = slope;
		this.offset = offset;
	}
	
	public Double evaluate(Double x) {
		return slope*x + offset;
	}
	
	public String toString() {
		return String.format("%.2fx + %.2f", slope, offset);
	}
}