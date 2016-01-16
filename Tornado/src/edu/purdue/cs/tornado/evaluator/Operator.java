package edu.purdue.cs.tornado.evaluator;

import java.util.ArrayList;

import edu.purdue.cs.tornado.helper.GenerateUUID;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.messages.DataObject;

public class Operator {

	public String operatorId;
	public String dataSourceId; //an operator can be on a single data source, such  as filter and top-k, or a group
	public String dataSourceId2;//an  operator can be on multiple  data source, in joins, such as the distance and top-K joins
	public OperatorType operatorType;
	
	
	
	public Rectangle bounds;
	public Point focalPoint;
	public LogicalOperator logicalOperator;
	public BooleanOperator booleanOperator;
	public SpatialDistanceType spatialDistType;
	public Double threashold;
	public Geometry geometryType;
	public ArrayList<String> keywords;
	
	
	public double selectivity;
	// Optimizer set parameters 
	ArrayList<OperatorCost> evaluationAleternatives;
	public OperatorEvaluationTechnique selectedEvaluationTechnique;
	public Operator dependantOperator1; //this defines the source of data 
	public Operator dependantOperator2; //this defines the second source of data in case of join

	// Evaluation parameters 
	private Boolean hasNext;
	public void init(){
		
	}
	public Boolean hasNext(){
		return false;
	}
	public DataObject getNext(){
		return null;
	}
	public ArrayList<DataObject> getNextPatch(){
		return null;
	}
	public ArrayList<DataObject> getAll(){
		return null;
	}
	
	
	public static Operator getHybridOperator(Operator spatialOperator,Operator textOperator){
		Operator op = new Operator();
		op.operatorId = GenerateUUID.getUUID();
		op.dataSourceId = spatialOperator.dataSourceId;
		op.operatorType = OperatorType.HYBRIBD_FILTER;
		op.dependantOperator1 = spatialOperator;
		op.dependantOperator2 = textOperator;
		return op;
	}
	
	
	
	public static Operator getSpatialInsideOperator(String dataSourceId, Rectangle rectangle){
		Operator op = new Operator();
		op.operatorId = GenerateUUID.getUUID();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType.SPATIAL_INSIDE;
		op.bounds=rectangle;
		return op;
	}
	public static Operator getSpatialDistOperatorPoint(String dataSourceId, SpatialDistanceType distType , Point point,BooleanOperator booleanOperatorType ,Double threashold){
		Operator op = new Operator();
		op.operatorId = GenerateUUID.getUUID();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType.SPATIAL_DIST;
		op.geometryType=Geometry.POINT;
		op.spatialDistType=distType;
		op.booleanOperator=booleanOperatorType;
		op.focalPoint=point;
		op.threashold=threashold;
		return op;
	}
	public static Operator getSpatialDistOperatorRectange(String dataSourceId,SpatialDistanceType distType , Rectangle rectangle,BooleanOperator booleanOperatorType ,Double threashold){
		Operator op = new Operator();
		op.operatorId = GenerateUUID.getUUID();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType.SPATIAL_DIST;
		op.geometryType=Geometry.RECTANGLE;
		op.spatialDistType=distType;
		op.booleanOperator=booleanOperatorType;
		op.bounds=rectangle;
		op.threashold=threashold;
		return op;
	}
	public static Operator getTextOverlapOperator(String dataSourceId, ArrayList<String> keywords,BooleanOperator booleanOperatorType ,Double threashold){
		Operator op = new Operator();
		op.operatorId = GenerateUUID.getUUID();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType.TEXT_OVERLAP;
		op.booleanOperator=booleanOperatorType;
		op.keywords=keywords;
		op.threashold=threashold;
		return op;
	}
	public static Operator getTextContainOperator(String dataSourceId, ArrayList<String> keywords){
		Operator op = new Operator();
		op.operatorId = GenerateUUID.getUUID();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType.TEXT_CONTAINS;
		op.keywords=keywords;
		return op;
	}
	public static Operator getTextSemanticOperator(String dataSourceId, ArrayList<String> keywords,BooleanOperator booleanOperatorType ,Double threashold){
		Operator op = new Operator();
		op.operatorId = GenerateUUID.getUUID();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType.TEXT_SEMANTIC;
		op.booleanOperator=booleanOperatorType;
		op.keywords=keywords;
		op.threashold=threashold;
		return op;
	}
	public static Operator getTextSentimentOperator(String dataSourceId, ArrayList<String> keywords,BooleanOperator booleanOperatorType ,Double threashold){
		Operator op = new Operator();
		op.operatorId = GenerateUUID.getUUID();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType. TEXT_SENTIMENT;
		op.booleanOperator=booleanOperatorType;
		op.keywords=keywords;
		op.threashold=threashold;
		return op;
	}
	public static Operator getTextDistAnyOperator(String dataSourceId, ArrayList<String> keywords,BooleanOperator booleanOperatorType ,Double threashold){
		Operator op = new Operator();
		op.operatorId = GenerateUUID.getUUID();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType.TEXT_DIST_ANY;
		op.booleanOperator=booleanOperatorType;
		op.keywords=keywords;
		op.threashold=threashold;
		return op;
	}
	public static Operator getTextDistAllOperator(String dataSourceId, ArrayList<String> keywords,BooleanOperator booleanOperatorType ,Double threashold){
		Operator op = new Operator();
		op.operatorId = GenerateUUID.getUUID();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType.TEXT_DIST_ALL;
		op.booleanOperator=booleanOperatorType;
		op.keywords=keywords;
		op.threashold=threashold;
		return op;
	}
	public static Operator getUnionOperator(String dataSourceId){
		Operator op = new Operator();
		op.operatorId = GenerateUUID.getUUID();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType.TEXT_UNION;
		return op;
	}
	public static Operator getIntersectOperator(String dataSourceId){
		Operator op = new Operator();
		op.operatorId = GenerateUUID.getUUID();
		op.dataSourceId=dataSourceId;
		op.operatorType=OperatorType.TEXT_INTERSECT;
		return op;
	}
	public Boolean isSpatialFilterOperator(){
		return(operatorType==OperatorType.SPATIAL_INSIDE||
				operatorType==OperatorType.SPATIAL_DIST||
				operatorType==OperatorType.SPATIAL_OVERLAP);
		
	}
	public Boolean isTextFilterOperator(){
		return(
				operatorType==OperatorType.TEXT_DIST||
				operatorType==OperatorType.TEXT_CONTAINS||
				operatorType==OperatorType.TEXT_OVERLAP||
				operatorType==OperatorType.TEXT_UNION||
				operatorType==OperatorType.TEXT_INTERSECT||
				operatorType==OperatorType.TEXT_DIST_ANY||
				operatorType==OperatorType.TEXT_DIST_ALL||
				operatorType==OperatorType.TEXT_SEMANTIC||
				operatorType==OperatorType.TEXT_SENTIMENT);
		
	}
	
	public Boolean isTopKOperator(){
		return(operatorType==OperatorType.ORDER_BY);
	}
	public Boolean isHybridOperator(){
		return(operatorType==OperatorType.HYBRIBD_FILTER);
	}
	public Boolean isJoinOperator(){
		return(operatorType==OperatorType.JOIN);
	}
	public Boolean isGroupOperator(){
		return(operatorType==OperatorType.SPATIAL_WITHIN_DIST||
				operatorType==OperatorType.SPATIAL_PARTITIONS);
	}
	public Boolean isAggregateOperator(){
		return(operatorType==OperatorType.SPATIAL_CENTROID||
				operatorType==OperatorType.SPATIAL_DIAMETER||
				operatorType==OperatorType.TEXT_FREQUENT);
	}
	
}
