package edu.purdue.cs.tornado.evaluator;

import java.util.ArrayList;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;

public class Operator {

	public String dataSourceId; //an operator can be on a single data source, such  as filter and top-k, or a group
	public String dataSourceId2;//an  operator can be on multiple  data source, in joins, such as the distance and top-K joins
	public OperatorType operatorType;
	public ArrayList<Object> arguments; //the operator can have zero to many arguments of any type
	
	public static Operator getSpatialInsideOperator(String dataSourceId, Rectangle rectangle){
		Operator op = new Operator();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType.SPATIAL_INSIDE;
		op.arguments = new ArrayList<Object>();
		op.arguments.add(rectangle);
		return op;
	}
	public static Operator getSpatialDistOperatorPoint(String dataSourceId, SpatialDistanceType distType , Point point,BooleanOperator booleanOperatorType ,Double threashold){
		Operator op = new Operator();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType.SPATIAL_DIST;
		op.arguments = new ArrayList<Object>();
		op.arguments.add(Geometry.POINT);
		op.arguments.add(distType);
		op.arguments.add(booleanOperatorType);
		op.arguments.add(point);
		op.arguments.add(threashold);
		return op;
	}
	public static Operator getSpatialDistOperatorRectange(String dataSourceId,SpatialDistanceType distType , Rectangle rectangle,BooleanOperator booleanOperatorType ,Double threashold){
		Operator op = new Operator();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType.SPATIAL_DIST;
		op.arguments = new ArrayList<Object>();
		op.arguments.add(Geometry.RECTANGLE);
		op.arguments.add(distType);
		op.arguments.add(booleanOperatorType);
		op.arguments.add(rectangle);
		op.arguments.add(threashold);
		return op;
	}
	
	
	
}
