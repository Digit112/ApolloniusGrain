
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

import geometry.*;

// This class represents an individual circle in the apollonius grain fractal, called a "grain".
// Each is the result of finding a circle tangent to three others, its parent plus two contributors. Each grain has three children and is the parent of all three.
// A node's two contributors are chosen from among the node's parent's parent and parent's contributors. Since order doesn't matter, there are three possibilities, and thus three children.
// The contributor relationships can be extremely complex. Other than those, it is just a ternary tree.
// In the final fractal, a node's parent and contributors are the three circles it touches which are bigger than it. The smallest of them is the parent.
class ApolloniusGrain {
	ApolloniusGrain parent;
	ApolloniusGrain contributor_a;
	ApolloniusGrain contributor_b;
	
	Circle circle;
	int datum;
	int depth;
	
	ApolloniusGrain child_a;
	ApolloniusGrain child_b;
	ApolloniusGrain child_c;
	
	public ApolloniusGrain(ApolloniusGrain parent, ApolloniusGrain contributor_a, ApolloniusGrain contributor_b, Random random) {
		this.parent = parent;
		this.contributor_a = contributor_a;
		this.contributor_b = contributor_b;
		
		this.circle = Apollonius.getSmallerSolutionCircle(parent.circle, contributor_a.circle, contributor_b.circle);
		if (this.circle == null)
			throw new Error("There must be a solution."); // TODO: Could be handled more gracefully...
		
		this.child_a = null;
		this.child_b = null;
		this.child_c = null;
		
		this.datum = random.nextInt();
		this.depth = parent.depth + 1;
	}
	
	// Constructs the "parents" of the root. Unlike actual nodes, they have only one child: The real root.
	// Such grains are called scaffolding. Three are needed to find the root.
	public ApolloniusGrain(Circle circle, Random random) {
		this.circle = circle;
		
		this.datum = random.nextInt();
		this.depth = -1;
	}
	
	// Constructs the root.
	public ApolloniusGrain(Circle A, Circle B, Circle C, Random random) {
		this.parent = new ApolloniusGrain(A, random);
		this.contributor_a = new ApolloniusGrain(B, random);
		this.contributor_b = new ApolloniusGrain(C, random);
		
		this.circle = Apollonius.getSmallerSolutionCircle(A, B, C);
		if (this.circle == null)
			throw new Error("There must be a solution."); // TODO: Could be handled more gracefully...
		
		this.child_a = null;
		this.child_b = null;
		this.child_c = null;
		
		this.datum = random.nextInt();
		this.depth = 0;
	}
	
	// Calculates children based on self, parent, and contributors.
	// Pass a random number generator to generate the datum.
	public void calculateChildren(Random random) {
		this.child_a = new ApolloniusGrain(this, this.parent, this.contributor_a, random);
		this.child_b = new ApolloniusGrain(this, this.parent, this.contributor_b, random);
		this.child_c = new ApolloniusGrain(this, this.contributor_a, this.contributor_b, random);
	}
	
	// Pass a random number generator to generate the datum.
	public void calculateChildrenToDepth(int depth, Random random) {
		if (depth == 0) return;
		
		if (this.isLeaf()) {
			this.calculateChildren(random);
		}
		
		this.child_a.calculateChildrenToDepth(depth-1, random);
		this.child_b.calculateChildrenToDepth(depth-1, random);
		this.child_c.calculateChildrenToDepth(depth-1, random);
	}
	
	// Recursively generate children until all leaf grains have no greater diameter than the passed threshold.
	// Pass a random number generator to generate the datum.
	public void calculateChildrenToGranularity(double min_diameter, Random random) {
		if (this.isLeaf()) {
			this.calculateChildren(random);
		}
		
		if (diameter() > min_diameter) {
			this.child_a.calculateChildrenToGranularity(min_diameter, random);
			this.child_b.calculateChildrenToGranularity(min_diameter, random);
			this.child_c.calculateChildrenToGranularity(min_diameter, random);
		}
	}
	
	// Returns the depth level at which the passed point is contained by the fractal generated so far.
	// That is, by one of the circles. This metric is used in coloring.
	// If the point is not contained, returns the max depth plus one.
	// When called on the root, if the point is contained by scaffold circles, returns 0. If contained by the root, returns 1.
	public ApolloniusGrain getContainmentCircle(Point p, boolean do_debug) {
		if (isRoot()) {
			if (parent.contains(p)       ) return parent;
			if (contributor_a.contains(p)) return contributor_a;
			if (contributor_b.contains(p)) return contributor_b;
		}
		
		return getContainmentCircleRecurse(p, 1, do_debug);
	}
	
	// This function assumes that the parent circles of this grain are tangent to each other.
	// In that case, this grain and all its children are contained in the triangle whose vertices are the points of tangency.
	public Triangle getDartBounds() {
		// For each pair of circles, translate one origin in the direction of the other circle out to its radius, at the point of tangency.
		return new Triangle(
			parent.origin().translated(Point.difference(contributor_a.origin(), parent.origin()).normalized(parent.radius())),
			parent.origin().translated(Point.difference(contributor_b.origin(), parent.origin()).normalized(parent.radius())),
			contributor_a.origin().translated(Point.difference(contributor_b.origin(), contributor_a.origin()).normalized(contributor_a.radius()))
		);
	}
	
	// Deletes portions of the fractal which would not appear on a render of the passed rectangle.
	// Returns a grain which should be taken as the new root of the fractal.
	// Its parent, contributors, and all descendents remaibn. All other circles are disconnected in such a manor as to allow garbage collection.
	public ApolloniusGrain pruneByExtrication(SgndAlgndRectangle rect) {
		if (!getDartBounds().intersects(rect)) {
			throw new Error("Invalid state. Passed rectangle is not within the tree at all!");
		}
		
		if (!isRoot()) {
			throw new Error("Invalid state. pruneByExtrication must only be called on root.");
		}
		
		// Basically descend as long as only one gap of the current root intersects the rect.
		ApolloniusGrain root = this;
		
		while (true) {
			// No children to descend to.
			if (root.isLeaf()) {
				root.extricate();
				return root;
			}
			
			boolean child_a_intersects = root.child_a.getDartBounds().intersects(rect);
			boolean child_b_intersects = root.child_b.getDartBounds().intersects(rect);
			
			// Multiple child gaps intersect; stop descent.
			if (child_a_intersects && child_b_intersects) {
				root.extricate();
				return root;
			}
			
			boolean child_c_intersects = root.child_c.getDartBounds().intersects(rect);
			
			// Multiple child gaps intersect; stop descent.
			if (child_c_intersects && (child_a_intersects || child_b_intersects)) {
				root.extricate();
				return root;
			}
			
			// At this point, there are either 0 or 1 intersecting child gaps.
			if (child_a_intersects) root = root.child_a;
			else if (child_b_intersects) root = root.child_b;
			else if (child_c_intersects) root = root.child_c;
			else {
				// No intersections. Rect is fully enclosed by a circle, outside of any gaps.
				// Uh... Stop, I guess...
				root.extricate();
				return root;
			}
		}
	}
	
	// Recursively removes children whose descendents cannot influence a render of the passed region.
	public void pruneByExcision(SgndAlgndRectangle rect) {
		if (isLeaf()) return;
		
		boolean child_a_intersects = child_a.getDartBounds().intersects(rect);
		if (!child_a_intersects) child_a.pruneByExcision(rect);
		else child_a.excise();
			
		boolean child_b_intersects = child_b.getDartBounds().intersects(rect);
		if (!child_b_intersects) child_b.pruneByExcision(rect);
		else child_b.excise();
		
		boolean child_c_intersects = child_c.getDartBounds().intersects(rect);
		if (!child_c_intersects) child_c.pruneByExcision(rect);
		else child_c.excise();
	}
	
	// Deletes all internal relations among ancestors, allowing them to be garbage-collected, except this node's parent and contributors.
	private void extricate() {
		if (!isRoot() && !isScaffold()) {
			ApolloniusGrain[] parents = new ApolloniusGrain[] {parent, contributor_a, contributor_b};
			for (int parent_i = 0; parent_i < 3; parent_i++) {
				parents[parent_i].extricate();
				
				parents[parent_i].parent = null;
				parents[parent_i].contributor_a = null;
				parents[parent_i].contributor_b = null;
			}
		}
	}
	
	// Deletes all internal relations among descendents, allowing them to be garbage-collected.
	private void excise() {
		if (!isLeaf()) {
			ApolloniusGrain[] children = new ApolloniusGrain[] {child_a, child_b, child_c};
			for (int child_i = 0; child_i < 3; child_i++) {
				children[child_i].excise();
				
				children[child_i].parent = null;
				children[child_i].contributor_a = null;
				children[child_i].contributor_b = null;
			}
			
			child_a = null;
			child_b = null;
			child_c = null;
		}
	}
	
	private ApolloniusGrain getContainmentCircleRecurse(Point p, int current_depth, boolean do_debug) {
		if (this.circle.contains(p)) {
			return this;
		}
		else {
			if (isLeaf()) {
				return null;
			}
			else {
				ApolloniusGrain[] children = new ApolloniusGrain[] {child_a, child_b, child_c};
				for (int i = 0; i < 3; i++) {
					Triangle dart_bounds = children[i].getDartBounds();
					if (dart_bounds.contains(p)) {
						return children[i].getContainmentCircleRecurse(p, current_depth+1, do_debug);
					}
				}
			}
		}
		
		return null;
	}
	
	// Returns five arrays giving, for each layer:
	// - circle quantity
	// - Total covered area
	// - minimum radius
	// - average radius
	// - maximum radius
	public double[][] getStats() {
		if (isLeaf()) {
			return new double[][] {{1}, {area()}, {radius()}, {radius()}, {radius()}};
		}
		
		double[][][] child_stats = new double[][][] {
			this.child_a.getStats(),
			this.child_b.getStats(),
			this.child_c.getStats()
		};
		
		// Find which child has the greatest depth.
		int max_depth = 0;
		for (int child = 0; child < 3; child++) {
			if (child_stats[child][0].length > max_depth) {
				max_depth = child_stats[child][0].length;
			}
		}
		
		// Pad child arrays.
		for (int child_i = 0; child_i < 3; child_i++) {
			for (int metric_i = 0; metric_i < child_stats[child_i].length; metric_i++) {
				int cur_metric_array_len = child_stats[child_i][metric_i].length;
				
				if (cur_metric_array_len != max_depth) {
					// Create longer array and paste in values.
					double[] new_metric_array = new double[max_depth];
					
					for (int layer_i = 0; layer_i < cur_metric_array_len; layer_i++) {
						new_metric_array[layer_i + max_depth - cur_metric_array_len] = child_stats[child_i][metric_i][layer_i];
					}
					
					child_stats[child_i][metric_i] = new_metric_array;
				}
			}
		}
		
		// Combine child stats.
		double[][] my_stats = new double[5][max_depth+1];
		
		for (int layer = 0; layer < max_depth; layer++) {
			// Calculate circle quantity and area.
			double total_child_circles = 0;
			double total_circles_area = 0;
			for (int child = 0; child < 3; child++) {
				total_child_circles += child_stats[child][0][layer];
				total_circles_area += child_stats[child][1][layer];
			}
			
			my_stats[0][layer] = total_child_circles;
			my_stats[1][layer] = total_circles_area;
			
			// Adjust average.
			for (int child = 0; child < 3; child++) {
				double child_weight = child_stats[child][0][layer] / total_child_circles;
				my_stats[3][layer] += child_stats[child][3][layer] * child_weight;
			}
			
			// Set min and max.
			my_stats[2][layer] = Math.min(
				child_stats[0][2][layer],
				Math.min(child_stats[1][2][layer],
				child_stats[2][2][layer])
			);
			
			my_stats[4][layer] = Math.max(
				child_stats[0][4][layer],
				Math.max(child_stats[1][4][layer],
				child_stats[2][4][layer])
			);
		}
		
		// Add self to stats
		my_stats[0][max_depth] = 1;
		my_stats[1][max_depth] = area();
		my_stats[2][max_depth] = radius();
		my_stats[3][max_depth] = radius();
		my_stats[4][max_depth] = radius();
		
		return my_stats;
			
	}
	
	private int getMaxDepth() {
		if (isLeaf()) {
			return 0;
		}
		else {
			return child_a.getMaxDepth() + 1;
		}
	}
	
	// Obtains stats on this tree and prints them.
	public void debug() {
		String[] names = {" AREA", "MIN R", "AVG R", "MAX R"};
		double[][] stats = getStats();
		
		// Display layer numbers.
		System.out.print(String.format("LY ID: layer %2d", stats[0].length));
		for (int j = 1; j < stats[0].length; j++) {
			System.out.print(String.format(", layer %2d", stats[0].length - j));
		}
		System.out.println("");
		
		// Display total counts of circles.
		System.out.print(String.format("COUNT: %8d", (int) stats[0][0]));
		for (int j = 1; j < stats[0].length; j++) {
			System.out.print(String.format(", %8d", (int) stats[0][j]));
		}
		System.out.println("");
		
		// Display other stats - areas as well as min, avg, max radius.
		for (int i = 1; i < stats.length; i++) {
			System.out.print(String.format("%s: %.2e", names[i-1], stats[i][0]));
			for (int j = 1; j < stats[i].length; j++) {
				System.out.print(String.format(", %.2e", stats[i][j]));
			}
			System.out.println("");
		}
		
		int total_circles = 0;
		double total_area = 0;
		for (int layer = 0; layer < stats[1].length; layer++) {
			total_circles += (int) stats[0][layer];
			total_area += stats[1][layer];
		}
			
		// This is the area of the gap between three circles of radius one arranged to be cotangent and in the shape of an equilateral triangle.
		// It is calculated as the unit equilateral triangle minus the three segments of the unit circle.
		// Each segment is a 60 degree slice of a circle minus a unit equilateral triangle spanning from a chord to the origin of its circle.
		double max_area = Math.sqrt(3)/4 - 3*(Math.PI/6 - Math.sqrt(3)/4);
		System.out.println(String.format("Total area: %.4f (%.2f%%) Total Circles: %d Max Depth: %d", total_area, total_area / max_area * 100, total_circles, stats[0].length));
	}
	
	public boolean contains(Point p) {
		return circle.contains(p);
	}
	
	public Point origin() {
		return circle.origin;
	}
	
	public double radius() {
		return circle.radius;
	}
	
	public double diameter() {
		return circle.diameter();
	}
	
	public double area() {
		return circle.area();
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
	
	public String toString() {
		return this.circle.toString();
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
	
	public static BufferedImage render(ApolloniusGrain root, int width, int height, SgndAlgndRectangle viewport) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Point sample = viewport.bilerp(new Vector(
					x / (double) width, y / (double) height
				));
				
				ApolloniusGrain grain = root.getContainmentCircle(sample, false);
				int pixel = 0;
				
				if (grain != null) {
					pixel = grain.datum & 0xFFFFFF;
				}
				// //int val = (int) (255 * (1 - Math.pow(3, -(float) grain.depth / 10)));
				// int val = grain.depth % 2 * 255;
				// int pixel = (val << 16) | (val << 8) | val;
				

                // Set the pixel at the specific coordinates
                image.setRGB(x, y, pixel);
			}
		}
		
		return image;
	}
	
    public static void main(String[] args) throws IOException {
        System.out.println("Hello, World");
		
		Random random = new Random();
		
		SgndAlgndRectangle viewport = new SgndAlgndRectangle(
			new Point(-0.5, -1f/3 * Math.sqrt(3)),
			new Point( 0.5,  1f/6 * Math.sqrt(3))
		);
		
		// Camera and image params.
		// SgndAlgndRectangle viewport = new SgndAlgndRectangle(
			// new Point(-0.5, -0.5),
			// new Point( 0.5,  0.5)
		// );
		float aspect_ratio = (float) viewport.aspectRatio();
		
		int width = 1024;
		int height = (int) (width / aspect_ratio);
		
		// viewport.translate(new Vector(0.054, 0.154));
		// viewport.zoom(0.5);
		
		//SgndAlgndRectangle pruningBounds = viewport.zoomed(10).translated(new Vector(0.054, -0.064));
		
		double final_zoom = 1;
		int num_frames = 1;
		
		/* ---- END PARAMETERS ---- */
			
		// Form of an equilateral triangle.
		Circle A = new Circle(new Point( 0,  2.0/3*Math.sqrt(3)), 1);
		Circle B = new Circle(new Point(-1, -1.0/3*Math.sqrt(3)), 1);
		Circle C = new Circle(new Point( 1, -1.0/3*Math.sqrt(3)), 1);
		ApolloniusGrain root = new ApolloniusGrain(A, B, C, random);
		
		root = root.pruneByExtrication(pruningBounds);
		root.pruneByExcision(pruningBounds);
		
		double zoom_per_frame = Math.pow(final_zoom, 1f / num_frames);
		for (int frame_i = 0; frame_i < num_frames; frame_i++) {
			double pixel_width = viewport.width() / width;
			
			//for (int i = 0; i < 
			// Generate fractal.
			double gen_start_time = System.nanoTime();
			//root.calculateChildrenToDepth(9, random);
			root.calculateChildrenToGranularity(pixel_width*8, random);
			double gen_end_time = System.nanoTime();
			
			// Print statistics.
			root.debug();
			
			// Create image.
			long render_start_time = System.nanoTime();
			BufferedImage image = render(root, width, height, viewport);
			long render_end_time = System.nanoTime();
			
			pruningBounds.draw(image, viewport, 0xFF << 16);
			
			File fout = new File(String.format("out/%03d.png", frame_i));
			ImageIO.write(image, "png", fout);
			
			System.out.println(String.format("FRAME %d: GEN: %.4fs, RENDER: %.4fs PX WIDTH: %.8f.",
				frame_i, (float) (gen_end_time - gen_start_time) / 1E9, (float) (render_end_time - render_start_time) / 1E9, pixel_width));
			
			// Zoom in.
			viewport.zoom(zoom_per_frame);
		}
    }
}