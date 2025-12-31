import java.util.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class Point {
	double x;
	double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return String.format("(%.2f, %.2f)", x, y);
	}
}

class Circle {
	Point origin;
	double radius;
	
	public Circle(Point origin, double radius) {
		this.origin = origin;
		this.radius = radius;
	}
	
	public boolean contains(Point p) {
		double dx = p.x - origin.x;
		double dy = p.y - origin.y;
		
		// TODO: Uncomment
		// if (Math.abs(dx) > radius || Math.abs(dy) > radius) {
			// return false;
		// }
		
		return dx*dx + dy*dy < radius*radius;
	}
	
	public String toString() {
		return String.format("(%.2f, %.2f) -- %.2f", origin.x, origin.y, radius);
	}
}

class QuadraticFunction {
	double a;
	double b;
	double c;
	
	public QuadraticFunction(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
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

class LinearFunction {
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

// This class represents an individual circle in the apollonius grain fractal, called a "grain".
// Each is the result of finding a circle tangent to three others, its parent plus two contributors. Each grain has three children and is the parent of all three.
// A node's two contributors are chosen from among the node's parent's parent and parent's contributors. Since order doesn't matter, there are three possibilites, and thus three children.
// The contributor relationships can be extremely complex. Other than those, it is just a ternary tree.
// In the final fractal, a node's parent and contributors are the three circles it touches which are bigger than it. The smallest of them is the parent.
class ApolloniusGrain {
	ApolloniusGrain parent;
	ApolloniusGrain contributor_a;
	ApolloniusGrain contributor_b;
	
	Circle circle;
	
	ApolloniusGrain child_a;
	ApolloniusGrain child_b;
	ApolloniusGrain child_c;
	
	public ApolloniusGrain(ApolloniusGrain parent, ApolloniusGrain contributor_a, ApolloniusGrain contributor_b) {
		this.parent = parent;
		this.contributor_a = contributor_a;
		this.contributor_b = contributor_b;
		
		this.circle = Apollonius.getSmallerSolutionCircle(parent.circle, contributor_a.circle, contributor_b.circle);
		if (this.circle == null)
			throw new Error("There must be a solution."); // TODO: Could be handled more gracefully...
		
		this.child_a = null;
		this.child_b = null;
		this.child_c = null;
	}
	
	// Constructs the "parents" of the root. Unlike actual nodes, they have only one child: The real root.
	// Such grains are called scaffolding. Three are needed to find the root.
	public ApolloniusGrain(Circle circle) {
		this.circle = circle;
	}
	
	// Constructs the root.
	public ApolloniusGrain(Circle A, Circle B, Circle C) {
		this.parent = new ApolloniusGrain(A);
		this.contributor_a = new ApolloniusGrain(B);
		this.contributor_b = new ApolloniusGrain(C);
		
		this.circle = Apollonius.getSmallerSolutionCircle(A, B, C);
		if (this.circle == null)
			throw new Error("There must be a solution."); // TODO: Could be handled more gracefully...
		
		this.child_a = null;
		this.child_b = null;
		this.child_c = null;
	}
	
	public void calculateChildren() {
		this.child_a = new ApolloniusGrain(this, this.parent, this.contributor_a);
		this.child_b = new ApolloniusGrain(this, this.parent, this.contributor_b);
		this.child_c = new ApolloniusGrain(this, this.contributor_a, this.contributor_b);
	}
	
	public void calculateChildrenToDepth(int depth) {
		if (depth == 0) return;
		
		this.calculateChildren();
		
		this.child_a.calculateChildrenToDepth(depth-1);
		this.child_b.calculateChildrenToDepth(depth-1);
		this.child_c.calculateChildrenToDepth(depth-1);
	}
	
	// Returns the depth level at which the passed point is contained by the fractal generated so far.
	// That is, by one of the circles. This metric is used in coloring.
	// If the point is not contained, returns the max depth plus one.
	// When called on the root, if the point is contained by scaffold circles, returns 0. If contained by the root, returns 1.
	public int getContainmentDepth(Point p) {
		if (isRoot()) {
			if (this.parent.contains(p) || this.contributor_a.contains(p) || this.contributor_b.contains(p)) {
				return 0;
			}
		}
		
		return getContainmentDepth(p, 1);
	}
	
	private int getContainmentDepth(Point p, int current_depth) {
		if (this.circle.contains(p)) {
			return current_depth;
		}
		else {
			if (isLeaf()) {
				return current_depth+1;
			}
			else {
				return Math.min(
					this.child_a.getContainmentDepth(p, current_depth+1),
					Math.min(
						this.child_b.getContainmentDepth(p, current_depth+1),
						this.child_c.getContainmentDepth(p, current_depth+1)
					)
				);
			}
		}
	}
	
	private boolean contains(Point p) {
		return circle.contains(p);
	}
	
	private boolean isLeaf() {
		return this.child_a == null;
	}
	
	private boolean isScaffold() {
		return this.parent == null;
	}
	
	private boolean isRoot() {
		return this.parent != null && this.parent.isScaffold();
	}
}

public class Apollonius {
	// Returns up to two circles which are tangent to the passed circles.
	// This solution DOES NOT WORK if the circles form a straight line, even if there is a solution, because in this special case the solution circle radii are equal.
	// This breaks the algebra being used.
	// If no solution exists, return an empty array.
	public static Circle[] solve(Circle A, Circle B, Circle C) {
		// System.out.println(String.format("Solving: %s | %s | %s", A.toString(), B.toString(), C.toString()));
		
		// Constant factors obtained by expanding perimiter equations.
		double constant_a = A.origin.x*A.origin.x + A.origin.y*A.origin.y - A.radius*A.radius;
		double constant_b = B.origin.x*B.origin.x + B.origin.y*B.origin.y - B.radius*B.radius;
		double constant_c = C.origin.x*C.origin.x + C.origin.y*C.origin.y - C.radius*C.radius;
		
		// Coefficients for B - A = a1x + b1y + c1r + d1
		double a1 = 2*(A.origin.x - B.origin.x);
		double b1 = 2*(A.origin.y - B.origin.y);
		double c1 = 2*(A.radius - B.radius);
		double d1 = constant_a - constant_b;
		
		// Coefficients for C - A = a2x + b2y + c2r + d2
		double a2 = 2*(A.origin.x - C.origin.x);
		double b2 = 2*(A.origin.y - C.origin.y);
		double c2 = 2*(A.radius - C.radius);
		double d2 = constant_a - constant_c;
		
		// System.out.println(String.format(
			// "%.2fx + %.2fy + %.2fr + %.2f = 0 | %.2fx + %.2fy + %.2fr + %.2f = 0",
			// a1, b1, c1, d1, a2, b2, c2, d2
		// ));
		
		// Solved for x and y in terms of the radius of solution circle
		LinearFunction r2x = new LinearFunction(
			b1*c2 - b2*c1,
			b2*d1 - b1*d2
		);
		
		LinearFunction r2y = new LinearFunction(
			a2*c1 - a1*c2,
			a1*d2 - a2*d1
		);
		
		// System.out.println(String.format("%s | %s", r2x.toString(), r2y.toString()));
		
		// This is the common denominator of the linear functions which is excluded because it can be zero and therefore cause issues,
		// specifically when the solution circles' radii are equal, which can happend if input circles' origins are colinear.
		double cd = a1*b2 - b1*a2;
		
		double r2x_sqr_slope = r2x.slope*r2x.slope;
		double r2y_sqr_slope = r2y.slope*r2y.slope;
		
		double r2x_sqr_offset = r2x.offset*r2x.offset;
		double r2y_sqr_offset = r2y.offset*r2y.offset;
		
		double A_sqr_x = A.origin.x*A.origin.x;
		double A_sqr_y = A.origin.y*A.origin.y;
		double A_sqr_r = A.radius*A.radius;
		
		// Substituting into one of the perimiter equations gives a quadratic whose root(s) are solution radii.
		QuadraticFunction r_eq_0 = new QuadraticFunction(
			r2x_sqr_slope + r2y_sqr_slope - cd*cd,
			2*((r2x.slope*r2x.offset + r2y.slope*r2y.offset) - (A.origin.x*r2x.slope + A.origin.y*r2y.slope)*cd - A.radius*cd*cd),
			(r2y_sqr_offset + r2x_sqr_offset) - (2*A.origin.x*r2x.offset + 2*A.origin.y*r2y.offset)*cd + (A_sqr_x + A_sqr_y - A_sqr_r)*cd*cd
		);
		
		double[] roots = r_eq_0.getRoots();
		
		// System.out.println(String.format("%s has roots at x = %.2f and x = %.2f", r_eq_0.toString(), roots[0], roots[1]));
		
		// No solution if the roots don't exist.
		// If they're both negative, internal solutions exist, but we only want external tangencies.
		if (Double.isNaN(roots[0]) || (roots[0] < 0 && roots[1] < 0)) {
			return new Circle[] {};
		}
		
		Circle sol1 = null;
		Circle sol2 = null;
		
		// Check each roots and generate solutions if possible.
		// Quadratic may give negative radius meaning the solution circle is imaginary.
		if (roots[0] >= 0) {
			sol1 = new Circle(
				new Point(r2x.evaluate(roots[0]) / cd, r2y.evaluate(roots[0]) / cd),
				roots[0]
			);
		}
		
		if (roots[1] >= 0) {
			sol2 = new Circle(
				new Point(r2x.evaluate(roots[1]) / cd, r2y.evaluate(roots[1]) / cd),
				roots[1]
			);
		}
		
		// Return solutions
		if (sol1 != null && sol2 != null) {
			return new Circle[] {sol1, sol2};
		}
		else {
			if (sol1 != null) {
				return new Circle[] {sol1};
			}
			else if (sol2 != null) {
				return new Circle[] {sol2};
			}
			else {
				throw new Error("Impossible state.");
			}
		}
	}
	
	// Obtains the external tangent solution circles to the passed circles, and returns the smaller.
	// Returns null if none exists.
	public static Circle getSmallerSolutionCircle(Circle A, Circle B, Circle C) {
		Circle[] sols = solve(A, B, C);
		
		if (sols.length == 0)
			return null;
		if (sols.length == 1 || sols[0].radius < sols[1].radius)
			return sols[0];
		else
			return sols[1];
	}
	
    public static void main(String[] args) throws IOException {
        System.out.println("Hello, World");
		
		// Form of an equilateral triangle.
		Circle A = new Circle(new Point( 0,  2.0/3*Math.sqrt(3)), 1);
		Circle B = new Circle(new Point(-1, -1.0/3*Math.sqrt(3)), 1);
		Circle C = new Circle(new Point( 1, -1.0/3*Math.sqrt(3)), 1);
		
		// Generate fractal.
		ApolloniusGrain root = new ApolloniusGrain(A, B, C);
		root.calculateChildrenToDepth(8);
		
		float left = -0.5f;
		float right = 0.5f;
		float top = -0.5f;
		float bottom = 0.5f;
		
		int width = 1024*2;
		int height = 1024*2;
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Point sample = new Point(
					((double) x) / width  * (right - left) + left,
					((double) y) / height * (bottom - top) + top
				);
				
				int depth = root.getContainmentDepth(sample);
				
				int val = (int) (255 * (1 - Math.pow(3, -(float) depth / 10)));
				int pixel = (val << 16) | (val << 8) | val;

                // Set the pixel at the specific coordinates
                image.setRGB(x, y, pixel);
			}
		}
		
		File fout = new File("out.png");
		ImageIO.write(image, "png", fout);
    }
}