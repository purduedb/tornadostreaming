package edu.purdue.cs.tornado.evaluator;

import java.util.ArrayList;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.messages.CombinedTuple;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.stanford.nlp.patterns.Data;

public class Evalautor {

	
	
	
	
	/**
	 * This function evaluates a single predicate or a function to as a
	 * qualifying condition
	 * 
	 * @param operator
	 * @param args
	 * @return
	 */
	//TODO handle more complex expression than just anded boolean predicates and functions 
	public static Boolean evalauteBooleanOpertor(Operator operator, DataObject dataObject1) {
		Boolean result = false;
		switch (operator.operatorType) {
		case SPATIAL_INSIDE:
			if (dataObject1 == null) {
				result = false;
				break;
			}
			result = evaluateBooleanSpatialInside(operator, dataObject1);
			break;
		case SPATIAL_OVERLAP:
			//TODO
			break;
		case SPATIAL_DIST:
			result = evaluateBooleanSpatialDistance(operator, dataObject1);
			break;
		case TEXT_OVERLAP:
			result = evaluateBooleanTextOverlap(operator, dataObject1);
			break;
		case TEXT_CONTAINS:
			result = evaluateBooleanTextContainment(operator, dataObject1);
			break;
		default:
			System.err.println("Undefined operator type for boolean evlaution");
			break;
		}

		return result;
	}

	public static ArrayList<DataObject> evalauteBooleanOpertor(Operator operator, ArrayList<DataObject> dataObjectList1) {
		//TODO this can be more efficient
		ArrayList<DataObject> toReturn = new ArrayList<DataObject>();
		for (DataObject obj : dataObjectList1)
			if (evalauteBooleanOpertor(operator, obj))
				toReturn.add(obj);
		return toReturn;
	}

	public static Boolean evalauteBooleanJoinOpertor(Operator operator, DataObject dataObject1, DataObject dataObject2) {
		return null;
	}

	public static CombinedTuple evalauteJoinOpertor(Operator operator, DataObject dataObject1, ArrayList<DataObject> dataObjectList2) {
		CombinedTuple combinedTuple = new CombinedTuple();
		combinedTuple.setDataObject(dataObject1);
		ArrayList<DataObject> otherObjectList = new ArrayList<DataObject>();
		Boolean joined = false;
		for (DataObject dataObject2 : dataObjectList2) {
			if (evalauteBooleanJoinOpertor(operator, dataObject1, dataObject2)) {
				otherObjectList.add(dataObject2);
				joined = true;
			}
		}
		if (joined) {
			combinedTuple.setDataObject2List(otherObjectList);
			return combinedTuple;
		} else
			return null;
	}

	/**
	 * This function will be depeneding on the plan to set what is the first source and what is the second source 
	 * @param operator
	 * @param dataObjectList1
	 * @param dataObjectList2
	 * @return
	 */
	//TODO is there a smarter way to perform this join maybe using nested loop joins or something else
	public static ArrayList<CombinedTuple> evalauteBooleanOpertor(Operator operator, ArrayList<DataObject> dataObjectList1, ArrayList<DataObject> dataObjectList2) {
		ArrayList<CombinedTuple> combinedTuples= new ArrayList<CombinedTuple>();
		for(DataObject object1:dataObjectList1){
			CombinedTuple combinedTuple = evalauteJoinOpertor(operator, object1, dataObjectList2);
			if(combinedTuple!=null)
				combinedTuples.add(combinedTuple);
		}
		return combinedTuples;
	}

	/**
	 * Currently what is being support is a list of anded boolean expressions
	 * More sophisticated expressions are to be added A boolean expression can
	 * either be a boolean function (predicate) or a
	 * 
	 * @param operator
	 * @param dataObject1
	 * @param dataObject2
	 * @param dataObjectList1
	 * @param dataObjectList2
	 * @return
	 */
	public static Boolean evalauteBooleanOpertor(Operator operator, DataObject dataObject1, DataObject dataObject2, ArrayList<DataObject> dataObjectList1, ArrayList<DataObject> dataObjectList2) {

		Boolean result = false;
		switch (operator.operatorType) {
		case SPATIAL_INSIDE:
			if (dataObject1 == null) {
				result = false;
				break;
			}
			result = evaluateBooleanSpatialInside(operator, dataObject1);
			break;
		case SPATIAL_OVERLAP:
			//TODO
			break;
		case SPATIAL_DIST:
			result = evaluateBooleanSpatialDistance(operator, dataObject1);
			break;
		case TEXT_OVERLAP:
			result = evaluateBooleanTextOverlap(operator, dataObject1);
			break;
		case TEXT_CONTAINS:
			result = evaluateBooleanTextContainment(operator, dataObject1);
			break;
		default:
			System.err.println("Undefined operator type for boolean evlaution");
			break;
		}

		return result;
	}

	/**
	 * This function evalautes if a point is inside an rectange or not
	 * 
	 * @param operator
	 * @param dataObject1
	 * @return
	 */
	private static Boolean evaluateBooleanSpatialInside(Operator operator, DataObject dataObject1) {
		
		Rectangle rect = (Rectangle) operator.bounds;
		return SpatialHelper.overlapsSpatially(dataObject1.getLocation(), rect);

	}

	/**
	 * This function measures the degree of overlap between two rectnagles and
	 * compares to a threshold
	 * 
	 * @param operator
	 * @param dataObject1
	 * @return
	 */
	private static Boolean evaluateBooleanSpatialOverlap(Operator operator, DataObject dataObject1) {
		//TODO Handle spatial overlap for rectangles
		return null;
	}

	/**
	 * This function compares the distance between two objects to a threshold
	 * 
	 * @return
	 */
	private static Boolean evaluateBooleanSpatialDistance(Operator operator, DataObject dataObject1) {
		//TODO support all distance types, currently, euclidian, min dist, max dist
		Boolean result = false;
		
		Double threashold = operator.threashold;
		Geometry geometry = operator.geometryType;
		BooleanOperator booleanOperator = operator.booleanOperator;
		double dist = 0;
		switch (geometry) {
		case POINT:
			Point p = (Point) operator.focalPoint;
			dist = SpatialHelper.getDistanceInBetween(dataObject1.getLocation(), p);
			break;
		case RECTANGLE:
			Rectangle rec = (Rectangle) operator.bounds;
			SpatialDistanceType distType = operator.spatialDistType;
			switch (distType) {
			case MAX_DIST:
				dist = SpatialHelper.getMaxDistanceBetween(dataObject1.getLocation(), rec);
				break;
			case MIN_DIST:
				dist = SpatialHelper.getMinDistanceBetween(dataObject1.getLocation(), rec);
				break;
			default:
				System.err.println("Undefined distance type for boolean evlaution");
				break;
			}
			break;
		case POLYGON:
			//TODO handle the polygon geometry 
			dist = 0;
			break;
		default:
			break;
		}
		result = evaluateBooleanExpression(booleanOperator, dist, threashold);
		return result;
	}

	/**
	 * This function compares the distance between two objects to a threshold
	 * 
	 * @return
	 */
	private static Boolean evaluateBooleanTextOverlap(Operator operator, DataObject dataObject1) {
		Boolean result = false;
		
		BooleanOperator booleanOperator =  operator.booleanOperator;
		ArrayList<String> keywords =  operator.keywords;
		Double threashold = operator.threashold;

		Integer overlapCount = TextHelpers.getTextOverlapCount(keywords, dataObject1.getObjectText());
		result = evaluateBooleanExpression(booleanOperator, overlapCount.doubleValue(), threashold);
		return result;
	}

	/**
	 * This function compares the distance between two objects to a threshold
	 * 
	 * @return
	 */
	private static Boolean evaluateBooleanTextContainment(Operator operator, DataObject dataObject1) {
		Boolean result = false;
		ArrayList<String> keywords = operator.keywords;
		result = TextHelpers.containsTextually(dataObject1.getObjectText(), keywords);

		return result;
	}

	/**
	 * This function evalautes a boolean expression based on type
	 * 
	 * @param booleanOperator
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	private static Boolean evaluateBooleanExpression(BooleanOperator booleanOperator, Double arg1, Double arg2) {
		Boolean result = false;
		switch (booleanOperator) {
		case EQUAL:
			result = Double.compare(arg1, arg2) == 0;
			break;
		case LESS_THAN:
			result = Double.compare(arg1, arg2) < 0;
			break;
		case GREATER_THAN:
			result = Double.compare(arg1, arg2) > 0;
			break;
		default:
			System.err.println("Undefined boolean  type for boolean evlaution");
			break;
		}
		return result;
	}

	

	public static ArrayList<DataObject> updateContinuousQuery(DataObject... dataObject) {
		return null;
	}

	public static ArrayList<DataObject> evalauteFetchOperator(Operator operator) {
		return null;
	}

}
