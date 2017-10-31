package edu.purdue.cs.tornado.index.global;

public enum GlobalIndexType {
	GRID,
	PARTITIONED,
	RANDOM_TEXT,
	
	PARTITIONED_TEXT_AWARE, //DO Not send data when not needed 
	PARTITIONED_TEXT_AWARE_FORWARD, //DO Not send data when not needed and forward text summary
	
	
	DYNAMIC_OPTIMIZED
	
}
