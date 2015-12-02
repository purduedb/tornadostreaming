package edu.purdue.cs.tornado.evaluator;

import java.util.ArrayList;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.messages.DataObject;

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
		return evalauteBooleanOpertor(operator, dataObject1, null, null, null);
	}

	public static Boolean evalauteBooleanOpertor(Operator operator, DataObject dataObject1, DataObject dataObject2) {
		return evalauteBooleanOpertor(operator, dataObject1, dataObject2, null, null);
	}

	public static Boolean evalauteBooleanOpertor(Operator operator, ArrayList<DataObject> dataObjectList1, ArrayList<DataObject> dataObjectList2) {
		return evalauteBooleanOpertor(operator, null, null, dataObjectList1, dataObjectList2);
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
		if (operator.arguments == null || operator.arguments.get(0) == null)
			return false;
		Rectangle rect = (Rectangle) operator.arguments.get(0);
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
		if (operator.arguments == null || operator.arguments.size() < 5)
			return false;
		Double threashold = (Double) operator.arguments.get(4);
		Geometry geometry = (Geometry) operator.arguments.get(0);
		BooleanOperator booleanOperator = (BooleanOperator) operator.arguments.get(2);
		double dist = 0;
		switch (geometry) {
		case POINT:
			Point p = (Point) operator.arguments.get(3);
			dist = SpatialHelper.getDistanceInBetween(dataObject1.getLocation(), p);
			break;
		case RECTANGLE:
			Rectangle rec = (Rectangle) operator.arguments.get(3);
			SpatialDistanceType distType = (SpatialDistanceType) operator.arguments.get(1);
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
		result = evaluateBooleanExpression(booleanOperator,dist,threashold);
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

	public static ArrayList<DataObject> evalautePlanSnapShot(Plan operator) {
		return null;
	}

	public static ArrayList<DataObject> updateContinuousQuery(DataObject... dataObject) {
		return null;
	}

	public static ArrayList<DataObject> evalauteFetchOperator(Operator operator) {
		return null;
	}

	public static ArrayList<DataObject> evalauteJoinOperator(Operator operator) {
		return null;
	}
}
