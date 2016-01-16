package edu.purdue.cs.tornado.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

public class Query2 {
	public String querySrcId;
	public String queryId;
	public Boolean continousQuery;
	public ArrayList<Operator> dataSrcOperators;
	public ArrayList<Operator> joinOperators;
	//this parameter is set by optimizer 
	public Operator plan;
	
	public Query2( String querySrcId,String queryId, Boolean continousQuery){
		this.querySrcId = querySrcId;
		this.queryId = queryId;
		this.continousQuery = continousQuery;
		dataSrcOperators = new ArrayList<Operator>();
		joinOperators = new ArrayList<Operator>();
		
	}
	
	public void addDataSrcOperator(Operator operator){
		dataSrcOperators.add(operator);
	}
	public void addJoinOperator(Operator operator){
		dataSrcOperators.add(operator);
	}
	
}
