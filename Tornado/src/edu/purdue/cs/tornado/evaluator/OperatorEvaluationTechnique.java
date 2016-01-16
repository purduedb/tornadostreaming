package edu.purdue.cs.tornado.evaluator;

public enum OperatorEvaluationTechnique {
	SPATIAL_INDEX_FETCH,
	TEXT_INDEX_FETCH,
	HYBRID_INDEX_FETCH,
	DATA_SCAN,
	GET_NEXT,
	HASH_JOIN,
	NESTED_LOOP_JOIN
}
