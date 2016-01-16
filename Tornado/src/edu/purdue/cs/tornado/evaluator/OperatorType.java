package edu.purdue.cs.tornado.evaluator;

public enum OperatorType {
	//GENERAL
	HYBRIBD_FILTER,//this hybrid operator evluates both a spatial and textual filter using a hybrid index	

	//SPATIAL
	SPATIAL_INSIDE,//arguments  true /false
	SPATIAL_DIST, //
	SPATIAL_OVERLAP,
	//Textual
	TEXT_DIST,
	TEXT_CONTAINS,
	TEXT_OVERLAP,
	TEXT_UNION,
	TEXT_INTERSECT,
	TEXT_DIST_ANY,
	TEXT_DIST_ALL,
	TEXT_SEMANTIC,
	TEXT_SENTIMENT,
	//Hybrid
	ORDER_BY,
	//GROUP
	SPATIAL_WITHIN_DIST,
	SPATIAL_PARTITIONS,
	
	//AGGREGATE
	SPATIAL_CENTROID,
	SPATIAL_DIAMETER,
	TEXT_FREQUENT,
	
	//JOIN
	JOIN
	
}
