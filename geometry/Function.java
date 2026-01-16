package geometry;

// Represents a function from some type onto some other.
public interface Function<In, Out> {
	Out evaluate(In in);
}