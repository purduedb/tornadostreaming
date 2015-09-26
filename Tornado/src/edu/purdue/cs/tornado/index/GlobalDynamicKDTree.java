package edu.purdue.cs.tornado.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;

public class GlobalDynamicKDTree {//responsible of load balance;
	KDTree kdTree;
	private Double xrange;
	private Double yrange;
	private HashMap<Integer, Integer> taskIdToIndex ;
	private Integer numberOfEvaluatorTasks;
	private List<Integer> evaluatorBoltTasks; //this keeps track of the evaluator bolts ids 
	public Integer getNumberOfEvaluatorTasks() {
		return numberOfEvaluatorTasks;
	}
	public void setNumberOfEvaluatorTasks(Integer numberOfEvaluatorTasks) {
		this.numberOfEvaluatorTasks = numberOfEvaluatorTasks;
	}
	public List<Integer> getEvaluatorBoltTasks() {
		return evaluatorBoltTasks;
	}
	public void setEvaluatorBoltTasks(List<Integer> evaluatorBoltTasks) {
		this.evaluatorBoltTasks = evaluatorBoltTasks;
	}
	public HashMap<Integer, Integer> getTaskIdToIndex() {
		return taskIdToIndex;
	}
	public void setTaskIdToIndex(HashMap<Integer, Integer> taskIdToIndex) {
		this.taskIdToIndex = taskIdToIndex;
	}
	public GlobalDynamicKDTree(Integer numberOfEvaluatorTasks,List<Integer> evaluatorBoltTasks){
		this.numberOfEvaluatorTasks = numberOfEvaluatorTasks;
		this.evaluatorBoltTasks = evaluatorBoltTasks;
		taskIdToIndex = new HashMap<Integer, Integer>();
		for(Integer i =0;i<numberOfEvaluatorTasks;i++){
			taskIdToIndex.put(evaluatorBoltTasks.get(i), i);
		}
		
		xrange = SpatioTextualConstants.xMaxRange;
		yrange = SpatioTextualConstants.yMaxRange;
		
	}
	//*******************************************************************
	public ArrayList<Integer> getTaskIDsOverlappingRecangle(Rectangle rectangle){
		//kdtree.getTaskIDsOverlappingRecangle
		return null;
	
	}
	public ArrayList<Integer> getTaskIDsContainingPoint(Point point){
		return null;
	}
	//*******************************************************************
	public Double getXrange() {
		return xrange;
	}
	public void setXrange(Double xrange) {
		this.xrange = xrange;
	}
	public Double getYrange() {
		return yrange;
	}
	public void setYrange(Double yrange) {
		this.yrange = yrange;
	}
}
