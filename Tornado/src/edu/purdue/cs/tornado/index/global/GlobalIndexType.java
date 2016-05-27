package edu.purdue.cs.tornado.index.global;

public enum GlobalIndexType {
	GRID,
	PARTITIONED,
	PARTITIONED_TEXT_AWARE, //DO Not send data when not needed 
	DYNAMIC_AQWA,
	DYNAMIC_OPTIMIZED,
}
