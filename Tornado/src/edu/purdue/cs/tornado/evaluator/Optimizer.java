package edu.purdue.cs.tornado.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.index.DataSourceInformation;

//Every query will have a spatial filter predicate no matter what , and if does not have there should be a filter operator with the entire spatial range
public class Optimizer {
	public static final String JOIN_OPERATOR_SPLITTER = ",";

	public static Operator buildPlan(Query2 query, HashMap<String, DataSourceInformation> dataSourcesInformation) {

		ArrayList<Operator> alloperators = query.dataSrcOperators;

		HashMap<String, ArrayList<Operator>> spatialFilterOperator = new HashMap<String, ArrayList<Operator>>();
		HashMap<String, ArrayList<Operator>> textFilterOperator = new HashMap<String, ArrayList<Operator>>();
		HashMap<String, ArrayList<Operator>> topKOperator = new HashMap<String, ArrayList<Operator>>();
		HashMap<String, ArrayList<Operator>> joinOperator = new HashMap<String, ArrayList<Operator>>();
		HashMap<String, ArrayList<Operator>> groupOperator = new HashMap<String, ArrayList<Operator>>();
		HashMap<String, ArrayList<Operator>> aggregateOperator = new HashMap<String, ArrayList<Operator>>();
		HashMap<String, ArrayList<Operator>> hybridOperator = new HashMap<String, ArrayList<Operator>>();

		Set<String> sourcesSet = new HashSet<String>();

		for (Operator op : alloperators) {
			sourcesSet.add(op.dataSourceId);
			if (op.isSpatialFilterOperator()) {
				estimateCostِAndSelectivityOfFilterOperator(op, dataSourcesInformation);
				addOperatorToList(op.dataSourceId, spatialFilterOperator, op);
			} else if (op.isTextFilterOperator()) {
				estimateCostِAndSelectivityOfFilterOperator(op, dataSourcesInformation);
				addOperatorToList(op.dataSourceId, textFilterOperator, op);
			} else if (op.isJoinOperator()) {
				String joinId = getJoinStringId(op.dataSourceId, op.dataSourceId2);
				sourcesSet.add(op.dataSourceId2);
				addOperatorToList(joinId, joinOperator, op);
			} else if (op.isTopKOperator()) {
				addOperatorToList(op.dataSourceId, topKOperator, op);
			} else if (op.isAggregateOperator()) {
				addOperatorToList(op.dataSourceId, aggregateOperator, op);
			} else if (op.isGroupOperator()) {
				addOperatorToList(op.dataSourceId, groupOperator, op);
			}
		}

		Operator planOperator = null;

		for (String srcId : sourcesSet) {

			if (spatialFilterOperator.containsKey(srcId) && textFilterOperator.containsKey(srcId)) {
				for (Operator spatialOp : spatialFilterOperator.get(srcId))
					for (Operator textOp : spatialFilterOperator.get(srcId)) {
						Operator hybrid = Operator.getHybridOperator(spatialOp, textOp);
						estimateCostِAndSelectivityOfFilterOperator(hybrid, dataSourcesInformation);
						addOperatorToList(hybrid.dataSourceId, hybridOperator, hybrid);
					}
			}

			//in the case for the filter operator try to use the 
			//****************STEP 1 find the least cost data read operator *********************/
			Operator fetchOperator = spatialFilterOperator.get(srcId).get(0);
			for (Operator spatialOp : spatialFilterOperator.get(srcId)) {
				if (spatialOp.selectivity < fetchOperator.selectivity)
					fetchOperator = spatialOp;
			}
			for (Operator textOp : textFilterOperator.get(srcId)) {
				if (textOp.selectivity < fetchOperator.selectivity)
					fetchOperator = textOp;
			}
			for (Operator hybridOp : hybridOperator.get(srcId)) {
				if (hybridOp.selectivity < fetchOperator.selectivity)
					fetchOperator = hybridOp;
			}
			
			//****************STEP 2 Add the remaining filter operators at any arbitrary order *********************/
			planOperator = fetchOperator;
			if (fetchOperator.isHybridOperator()) {
				for (Operator spatialOp : spatialFilterOperator.get(srcId)) {
					if (!spatialOp.operatorId.equals(fetchOperator.dependantOperator1.operatorId)) {
						spatialOp.dependantOperator1 = planOperator;
						planOperator = spatialOp;
					}
				}
				for (Operator textOp : textFilterOperator.get(srcId)) {
					if (!textOp.operatorId.equals(fetchOperator.dependantOperator2.operatorId)) {
						textOp.dependantOperator1 = planOperator;
						planOperator = textOp;
					}
				}
			} else {
				for (Operator spatialOp : spatialFilterOperator.get(srcId)) {
					if (!spatialOp.operatorId.equals(fetchOperator.operatorId)) {
						spatialOp.dependantOperator1 = planOperator;
						planOperator = spatialOp;
					}
				}
				for (Operator textOp : textFilterOperator.get(srcId)) {
					if (!textOp.operatorId.equals(fetchOperator.dependantOperator1.operatorId)) {
						textOp.dependantOperator1 = planOperator;
						planOperator = textOp;
					}
				}
			}

			//****************STEP 3 Then check the order by *********************/

			//****************STEP 4 then check the join operators *********************/

		}

		return planOperator;
	}

	public static void estimateCostِAndSelectivityOfFilterOperator(Operator op, HashMap<String, DataSourceInformation> dataSourcesInformation) {

		DataSourceInformation sourceInfo = dataSourcesInformation.get(op.dataSourceId);
		double selectivity = sourceInfo.allDataCount;
		//First check the selectivity of individual operators
		ArrayList<OperatorCost> evaluationTechniques = new ArrayList<OperatorCost>();
		if (op.operatorType == OperatorType.SPATIAL_DIST) {
			if (op.booleanOperator.equals(BooleanOperator.LESS_THAN) || op.booleanOperator.equals(BooleanOperator.LESS_EQUAL)) {
				if (op.geometryType.equals(Geometry.POINT)) {
					//for now we assume all distances are euculdian, other wise assume other types 
					Rectangle rect = new Rectangle(new Point(op.focalPoint.X - op.threashold, op.focalPoint.Y - op.threashold), new Point(op.focalPoint.X + op.threashold, op.focalPoint.Y + op.threashold));
					if (sourceInfo.localSpatialIndex != null || sourceInfo.localHybridIndex1 != null) {
						if (sourceInfo.localSpatialIndex != null) {
							selectivity = sourceInfo.localSpatialIndex.getCountPerRec(rect);
							evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.SPATIAL_INDEX_FETCH));

						}
						if (sourceInfo.localHybridIndex1 != null) {
							if (sourceInfo.localHybridIndex1.getCountPerRec(rect) < selectivity) {
								selectivity = sourceInfo.localHybridIndex1.getCountPerRec(rect);
								evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.HYBRID_INDEX_FETCH));
							}
						}
					} else {
						selectivity = SpatialHelper.getArea(rect) / SpatialHelper.getArea(sourceInfo.selfBounds) * sourceInfo.allDataCount;
						evaluationTechniques.add(new OperatorCost(sourceInfo.allDataCount, OperatorEvaluationTechnique.DATA_SCAN));
					}
				} else {
					//TODO handle other types of geometry such as rectangle 
				}
			} else {
				//TODO handle distances greater and greater than or equal
			}

		}
		if (op.operatorType == OperatorType.SPATIAL_INSIDE) {
			if (sourceInfo.localSpatialIndex != null || sourceInfo.localHybridIndex1 != null) {
				if (sourceInfo.localSpatialIndex != null) {
					selectivity = sourceInfo.localSpatialIndex.getCountPerRec(op.bounds);
					evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.SPATIAL_INDEX_FETCH));

				}
				if (sourceInfo.localHybridIndex1 != null) {
					if (sourceInfo.localHybridIndex1.getCountPerRec(op.bounds) < selectivity) {
						selectivity = sourceInfo.localHybridIndex1.getCountPerRec(op.bounds);
						evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.HYBRID_INDEX_FETCH));
					}
				}
			} else {
				selectivity = SpatialHelper.getArea(op.bounds) / SpatialHelper.getArea(sourceInfo.selfBounds) * sourceInfo.allDataCount;
				evaluationTechniques.add(new OperatorCost(sourceInfo.allDataCount, OperatorEvaluationTechnique.DATA_SCAN));
			}
		}
		if (op.operatorType == OperatorType.SPATIAL_OVERLAP) {
			//TODO handle this type of operator later 
		}
		if (op.operatorType == OperatorType.TEXT_OVERLAP) {
			if (sourceInfo.localTextIndex != null || sourceInfo.localHybridIndex1 != null) {
				if (sourceInfo.localTextIndex != null) {
					selectivity = sourceInfo.localTextIndex.estimateDataObjectCountAny(op.keywords);
					evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.TEXT_INDEX_FETCH));

				}
				if (sourceInfo.localHybridIndex1 != null) {
					if (sourceInfo.localHybridIndex1.getCountPerKeywrodsAny(op.keywords, sourceInfo.selfBounds) < selectivity) {
						selectivity = sourceInfo.localHybridIndex1.getCountPerKeywrodsAny(op.keywords, sourceInfo.selfBounds);
						evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.HYBRID_INDEX_FETCH));
					}
				}
			} else {
				selectivity = sourceInfo.allDataCount;
				evaluationTechniques.add(new OperatorCost(sourceInfo.allDataCount, OperatorEvaluationTechnique.DATA_SCAN));
			}
		}
		if (op.operatorType == OperatorType.TEXT_CONTAINS) {
			if (sourceInfo.localTextIndex != null || sourceInfo.localHybridIndex1 != null) {
				if (sourceInfo.localTextIndex != null) {
					selectivity = sourceInfo.localTextIndex.estimateDataObjectCountAll(op.keywords);
					evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.TEXT_INDEX_FETCH));

				}
				if (sourceInfo.localHybridIndex1 != null) {
					if (sourceInfo.localHybridIndex1.getCountPerKeywrodsAll(op.keywords, sourceInfo.selfBounds) < selectivity) {
						selectivity = sourceInfo.localHybridIndex1.getCountPerKeywrodsAll(op.keywords, sourceInfo.selfBounds);
						evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.HYBRID_INDEX_FETCH));
					}
				}
			} else {
				selectivity = sourceInfo.allDataCount;
				evaluationTechniques.add(new OperatorCost(sourceInfo.allDataCount, OperatorEvaluationTechnique.DATA_SCAN));
			}
		}
		if (op.operatorType == OperatorType.TEXT_DIST) {
			//TODO more accurate approximations are needed 
			if (sourceInfo.localTextIndex != null || sourceInfo.localHybridIndex1 != null) {
				if (sourceInfo.localTextIndex != null) {
					selectivity = sourceInfo.localTextIndex.estimateDataObjectCountAny(op.keywords);
					evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.TEXT_INDEX_FETCH));

				}
				if (sourceInfo.localHybridIndex1 != null) {
					if (sourceInfo.localHybridIndex1.getCountPerKeywrodsAny(op.keywords, sourceInfo.selfBounds) < selectivity) {
						selectivity = sourceInfo.localHybridIndex1.getCountPerKeywrodsAny(op.keywords, sourceInfo.selfBounds);
						evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.HYBRID_INDEX_FETCH));
					}
				}
			} else {
				selectivity = sourceInfo.allDataCount;
				evaluationTechniques.add(new OperatorCost(sourceInfo.allDataCount, OperatorEvaluationTechnique.DATA_SCAN));
			}

		}
		if (op.operatorType == OperatorType.TEXT_DIST_ANY) {
			//TODO more accurate approximations are needed 
			if (sourceInfo.localTextIndex != null || sourceInfo.localHybridIndex1 != null) {
				if (sourceInfo.localTextIndex != null) {
					selectivity = sourceInfo.localTextIndex.estimateDataObjectCountAny(op.keywords);
					evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.TEXT_INDEX_FETCH));

				}
				if (sourceInfo.localHybridIndex1 != null) {
					if (sourceInfo.localHybridIndex1.getCountPerKeywrodsAny(op.keywords, sourceInfo.selfBounds) < selectivity) {
						selectivity = sourceInfo.localHybridIndex1.getCountPerKeywrodsAny(op.keywords, sourceInfo.selfBounds);
						evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.HYBRID_INDEX_FETCH));
					}
				}
			} else {
				selectivity = sourceInfo.allDataCount;
				evaluationTechniques.add(new OperatorCost(sourceInfo.allDataCount, OperatorEvaluationTechnique.DATA_SCAN));
			}
		}
		if (op.operatorType == OperatorType.TEXT_DIST_ALL) {
			//TODO more accurate approximations are needed 
			if (sourceInfo.localTextIndex != null || sourceInfo.localHybridIndex1 != null) {
				if (sourceInfo.localTextIndex != null) {
					selectivity = sourceInfo.localTextIndex.estimateDataObjectCountAny(op.keywords);
					evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.TEXT_INDEX_FETCH));

				}
				if (sourceInfo.localHybridIndex1 != null) {
					if (sourceInfo.localHybridIndex1.getCountPerKeywrodsAny(op.keywords, sourceInfo.selfBounds) < selectivity) {
						selectivity = sourceInfo.localHybridIndex1.getCountPerKeywrodsAny(op.keywords, sourceInfo.selfBounds);
						evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.HYBRID_INDEX_FETCH));
					}
				}
			} else {
				selectivity = sourceInfo.allDataCount;
				evaluationTechniques.add(new OperatorCost(sourceInfo.allDataCount, OperatorEvaluationTechnique.DATA_SCAN));
			}
		}
		if (op.operatorType == OperatorType.TEXT_SEMANTIC) {
			//text semantic should be similar to the text overlap with semantic extension and hence we can use text overlap selectivity estimation
			if (sourceInfo.localTextIndex != null || sourceInfo.localHybridIndex1 != null) {
				if (sourceInfo.localTextIndex != null) {
					selectivity = sourceInfo.localTextIndex.estimateDataObjectCountAny(op.keywords);
					evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.TEXT_INDEX_FETCH));

				}
				if (sourceInfo.localHybridIndex1 != null) {
					if (sourceInfo.localHybridIndex1.getCountPerKeywrodsAny(op.keywords, sourceInfo.selfBounds) < selectivity) {
						selectivity = sourceInfo.localHybridIndex1.getCountPerKeywrodsAny(op.keywords, sourceInfo.selfBounds);
						evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.HYBRID_INDEX_FETCH));
					}
				}
			} else {
				selectivity = sourceInfo.allDataCount;
				evaluationTechniques.add(new OperatorCost(sourceInfo.allDataCount, OperatorEvaluationTechnique.DATA_SCAN));
			}
		}
		if (op.operatorType == OperatorType.TEXT_SENTIMENT) {//cannot perform indexing operations
			selectivity = sourceInfo.allDataCount;
			evaluationTechniques.add(new OperatorCost(sourceInfo.allDataCount * 10000, OperatorEvaluationTechnique.DATA_SCAN));
		}
		if (op.operatorType == OperatorType.HYBRIBD_FILTER) {

			if (sourceInfo.localHybridIndex1 != null) {
				selectivity = sourceInfo.localHybridIndex1.getCountPerKeywrodsAny(op.dependantOperator2.keywords, op.dependantOperator1.bounds);
				evaluationTechniques.add(new OperatorCost(selectivity, OperatorEvaluationTechnique.HYBRID_INDEX_FETCH));
			} else {
				selectivity = sourceInfo.allDataCount;
				evaluationTechniques.add(new OperatorCost(sourceInfo.allDataCount, OperatorEvaluationTechnique.DATA_SCAN));
			}
		}
		//then check the selectivity of both spatial and textual information if there is a spatial index  
		op.selectivity = selectivity;
		op.evaluationAleternatives = evaluationTechniques;
	}

	public static void addOperatorToList(String id, HashMap<String, ArrayList<Operator>> operatorsMap, Operator operator) {
		if (!operatorsMap.containsKey(id))
			operatorsMap.put(id, new ArrayList<Operator>());
		operatorsMap.get(id).add(operator);
	}

	public static Operator updatePlan(Query2 query, Operator plan) {
		return null;
	}

	/**
	 * This function gives the id for a join operator in the plan,
	 * 
	 * @param src1
	 * @param scr2
	 * @return
	 */
	public static String getJoinStringId(String src1, String src2) {
		if (src1.compareTo(src2) <= 0)
			return src1 + JOIN_OPERATOR_SPLITTER + src2;
		else
			return src2 + JOIN_OPERATOR_SPLITTER + src1;
	}

	public static void getStringSourceIdsFromJoinOperator(String joinStringId, StringBuilder src1, StringBuilder src2) {
		String[] ids = joinStringId.split(JOIN_OPERATOR_SPLITTER);
		src1.append(ids[0]);
		src2.append(ids[1]);

	}

}
