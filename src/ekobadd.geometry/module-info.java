/**
* The Geometry module provides 2D shape calculation logic including intersection tests
*/
module ekobadd.geometry {
	requires ekobadd.test;
	requires java.desktop;
	
	exports ekobadd.geometry;
	
	exports ekobadd.geometry.test to ekobadd.test;
}