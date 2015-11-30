/**
 * Copyright Jul 5, 2015
 * Author : Ahmed Mahmood
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.purdue.cs.tornado.index.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;

public abstract class GlobalIndex {
	public static String routingType ;
	public static String STATIC_GRID="STATIC_GRID" ;
	public static String STATIC_KD="STATIC_KD";
	public static String DYANAMIC_KD="DYANAMIC_KD";
	protected Double xrange;
	protected Double yrange;

	protected HashMap<Integer, Integer> taskIdToIndex ;
	protected Integer numberOfEvaluatorTasks;
	protected List<Integer> evaluatorBoltTasks; //this keeps track of the evaluator bolts ids 
	public GlobalIndex(Integer numberOfEvaluatorTasks,List<Integer> evaluatorBoltTasks){
		this.numberOfEvaluatorTasks = numberOfEvaluatorTasks;
		this.evaluatorBoltTasks = evaluatorBoltTasks;
		taskIdToIndex = new HashMap<Integer, Integer>();
		for(Integer i =0;i<numberOfEvaluatorTasks;i++){
			taskIdToIndex.put(evaluatorBoltTasks.get(i), i);
		}
		
		xrange = SpatioTextualConstants.xMaxRange;
		yrange = SpatioTextualConstants.yMaxRange;
	}
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
	public abstract ArrayList<Integer> getTaskIDsOverlappingRecangle(Rectangle rectangle);
	public abstract Integer getTaskIDsContainingPoint(Point point) throws Exception;
	public abstract Rectangle getBoundsForTaskIndex(Integer taskIndex);
	public abstract Rectangle getBoundsForTaskId(Integer taskId);
	public abstract GlobalIndexIterator globalKNNIterator(Point p);
}
