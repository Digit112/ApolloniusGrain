package ekobadd.geometry;

/** Represents an equation of the form y = mx + b */
public class LinearFunction implements Function<Double, Double> {
	public double slope;
	public double offset;
	
	/** Constructs a linear function with the given slope and offset. */
	public LinearFunction(double slope, double offset) {
		this.slope = slope;
		this.offset = offset;
	}
	
	/** Get the output of this function given the passed input. */
	public Double evaluate(Double x) {
		return slope*x + offset;
	}
	
	public String toString() {
		return String.format("%.2fx + %.2f", slope, offset);
	}
}