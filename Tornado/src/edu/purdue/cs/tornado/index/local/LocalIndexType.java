package edu.purdue.cs.tornado.index.local;

public enum LocalIndexType {
	NO_LOCAL_INDEX,
	SPATIAL_GRID,
	HYBRID_GRID,
	SPATIAL_MULTI_LEVEL_GRID,
	HYBRID_PYRAMID, //Add FAST as a Type here
	INVERTED_LIST,
	FAST
}
