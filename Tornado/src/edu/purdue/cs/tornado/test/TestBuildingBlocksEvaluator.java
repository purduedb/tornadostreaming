package edu.purdue.cs.tornado.test;


import java.io.IOException;
import java.util.ArrayList;

import edu.purdue.cs.tornado.evaluator.Evalautor;
import edu.purdue.cs.tornado.evaluator.Geometry;
import edu.purdue.cs.tornado.evaluator.Operator;
import edu.purdue.cs.tornado.evaluator.OperatorType;
import edu.purdue.cs.tornado.evaluator.Query2;
import edu.purdue.cs.tornado.evaluator.SpatialDistance;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.messages.DataObject;
public class TestBuildingBlocksEvaluator {

	
	public static void main(String[] args) throws IOException {
		Query2 query = new Query2();
		query.querySrcId ="Queries";
		query.queryId ="query1";
		
		Operator op = new Operator();
		op.dataSourceId="Tweets";
		op.operatorType=OperatorType.SPATIAL_INSIDE;
		op.arguments = new ArrayList<Object>();
		op.arguments.add(new Rectangle(new Point(0.0,0.0),new Point(100.0,100.0)));
		
		Operator op2 = new Operator();
		op.dataSourceId="Tweets";
		op.operatorType=OperatorType.SPATIAL_DIST;
		op.arguments = new ArrayList<Object>();
		op.arguments.add(Geometry.POINT);
		op.arguments.add(SpatialDistance.EUCLIDEAN);
		
		op.arguments.add(new Point(100.0,110.0));
		
		DataObject obj = new DataObject();
		obj.setObjectId("1");
		obj.setLocation(new Point(1.1,1.2));
		
		Boolean result = Evalautor.evalauteBooleanOpertor(op, obj);
				
	}
}
