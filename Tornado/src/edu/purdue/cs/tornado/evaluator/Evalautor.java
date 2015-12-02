package edu.purdue.cs.tornado.evaluator;

import java.util.ArrayList;

import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.messages.DataObject;

public class Evalautor {

	/**
	 * This function evaluates a single predicate or a function to as a qualifying condition
	 * @param operator
	 * @param args
	 * @return
	 */
	public static Boolean evalauteBooleanOpertor(Operator operator,DataObject dataObject1){
		return evalauteBooleanOpertor(operator, dataObject1, null, null, null);
	}
	public static Boolean evalauteBooleanOpertor(Operator operator,DataObject dataObject1,DataObject dataObject2){
		return evalauteBooleanOpertor(operator, dataObject1, dataObject2, null, null);
	}
	public static Boolean evalauteBooleanOpertor(Operator operator,ArrayList<DataObject> dataObjectList1,ArrayList<DataObject> dataObjectList2){
		 return evalauteBooleanOpertor(operator, null, null,dataObjectList1, dataObjectList2);
	}
	/**
	 * Currently what is being support is a list of anded boolean expressions
	 * More sophisticated expressions are to be added 
	 * A boolean expression can either be a boolean function (predicate)  or a 
	 * @param operator
	 * @param dataObject1
	 * @param dataObject2
	 * @param dataObjectList1
	 * @param dataObjectList2
	 * @return
	 */
	public static Boolean evalauteBooleanOpertor(Operator operator,DataObject dataObject1,DataObject dataObject2,ArrayList<DataObject> dataObjectList1,ArrayList<DataObject> dataObjectList2){
		
		Boolean result = false;
		switch (operator.operatorType) {
		case SPATIAL_INSIDE: 
			if(dataObject1==null){result =false;break;}
			result = evaluateBooleanSpatialInside(operator,dataObject1);
			break;	
		case SPATIAL_OVERLAP:
			break;

		default:
			System.err.println("Undefined operator type for boolean evlaution");
			break;
		}	
		
		
		
		
		return result;
	}
	private static Boolean evaluateBooleanSpatialInside(Operator operator,DataObject dataObject1){
		if (operator.arguments==null||operator.arguments.get(0)==null) return false;
		Rectangle rect =(Rectangle ) operator.arguments.get(0);
		return SpatialHelper.overlapsSpatially(dataObject1.getLocation(), rect);
		
	}
	private static Boolean evaluateBooleanSpatialOverlap(){
		return null;
	}
	private static Boolean evaluateBooleanSpatialDistance(){
		return null;
	}
	public static  ArrayList<DataObject> evalautePlanSnapShot(Plan operator){
		return null;
	}
	public static  ArrayList<DataObject> updateContinuousQuery(DataObject... dataObject){
		return null;
	}
}
