package edu.purdue.cs.tornado.evaluator;

import java.util.ArrayList;

public class Operator {

	public String dataSourceId; //an operator can be on a single data source, such  as filter and top-k, or a group
	public String dataSourceId2;//an  operator can be on multiple  data source, in joins, such as the distance and top-K joins
	public OperatorType operatorType;
	public ArrayList<Object> arguments; //the operator can have zero to many arguments of any type
	
	
	
	
	
	
}
