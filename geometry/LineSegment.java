package geometry;

public final class LineSegment implements LineSegmentLineSegmentIntersection {
	public Point a;
	public Point b;
	
	public LineSegment(Point a, Point b) {
		this.a = a;
		this.b = b;
	}
	
	public LineSegment(Point a, Vector delta) {
		this.a = a;
		this.b = a.translated(delta);
	}
	
	/**
	* Determines whether any part of this segment is within the passed rectangle.
	* @return true if the shapes intersect, false otherwise.
	*/
	public boolean intersects(SgndAlgndRectangle rect) {
		// First check if an endpoint is within the rectangle.
		// This covers the case where the line is fully within the rectangle.
		if (rect.contains(a)) return true;
		
		// Check collision with left side.
		double left_t = (rect.left() - a.x) / (b.x - a.x);
		if (left_t >= 0 && left_t <= 1) {
			double left_col_y = (b.y - a.y) * left_t + a.y;
			if (left_col_y >= rect.bottom() && left_col_y <= rect.top()) return true; 
		}
		
		// Check collision with right side.
		double right_t = (rect.right() - a.x) / (b.x - a.x);
		if (right_t >= 0 && right_t <= 1) {
			double right_col_y = (b.y - a.y) * right_t;
			if (right_col_y >= rect.bottom() && right_col_y <= rect.top()) return true;
		}
		
		// Check collision with top side.
		double top_t = (rect.top() - a.y) / (b.y - a.y);
		if (top_t >= 0 && top_t <= 1) {
			double top_col_x = (b.x - a.x) * top_t;
			if (top_col_x >= rect.left() && top_col_x <= rect.right()) return true; 
		}
		
		// Check collision with bottom side.
		double bottom_t = (rect.bottom() - a.y) / (b.y - a.y);
		if (bottom_t >= 0 && bottom_t <= 1) {
			double bottom_col_x = (b.x - a.x) * bottom_t;
			if (bottom_col_x >= rect.left() && bottom_col_x <= rect.right()) return true; 
		}
		
		// At least one endpoint is outside the rect.
		// The segment does not intersect any of the rect's sides.
		return false;
	}
}