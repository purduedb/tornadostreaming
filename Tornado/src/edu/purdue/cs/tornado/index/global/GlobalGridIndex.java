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
import java.util.HashSet;
import java.util.List;

import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class GlobalGridIndex extends GlobalIndex{
	protected Double xStep;
	protected Double yStep;
	protected Integer xCellsNum;
	protected Integer yCellsNum;
	public GlobalGridIndex(Integer numberOfEvaluatorTasks,List<Integer> evaluatorBoltTasks){
		super(numberOfEvaluatorTasks, evaluatorBoltTasks);
		yCellsNum = xCellsNum = (int)Math.sqrt(numberOfEvaluatorTasks); //this is a grid index use it to build the tree 
		xStep = xrange/xCellsNum;
		yStep = yrange/yCellsNum;
	}
	public Rectangle getBoundsForTaskId(Integer taskId){
		Integer taskIndex = taskIdToIndex.get(taskId);
		return getBoundsForTaskIndex(taskIndex);
	}
	public Rectangle getBoundsForTaskIndex(Integer taskIndex){
		IndexCellCoordinates globalIndexSelfcoordinates = new IndexCellCoordinates(taskIndex/xCellsNum,taskIndex%xCellsNum);
		Point minPoint = new Point ();
		minPoint.setX(xStep*globalIndexSelfcoordinates.getX());
		minPoint.setY(yStep*globalIndexSelfcoordinates.getY());
		Point maxPoint = new Point ();
		maxPoint.setX(xStep*(globalIndexSelfcoordinates.getX()+1));
		maxPoint.setY(yStep*(globalIndexSelfcoordinates.getY()+1));
		return new Rectangle(minPoint, maxPoint);
	}
	public ArrayList<Integer> getTaskIDsOverlappingRecangle(Rectangle rectangle){
		return mapRecangleToEvaluatorTask(rectangle);
	}
	public Integer getTaskIDsContainingPoint(Point point){
		return  mapDataPointToEvaluatorTask(point.getX(), point.getY());
	}
	public Double getxStep() {
		return xStep;
	}
	public void setxStep(Double xStep) {
		this.xStep = xStep;
	}
	public Double getyStep() {
		return yStep;
	}
	public void setyStep(Double yStep) {
		this.yStep = yStep;
	}
	public Integer getxCellsNum() {
		return xCellsNum;
	}
	public void setxCellsNum(Integer xCellsNum) {
		this.xCellsNum = xCellsNum;
	}
	public Integer getyCellsNum() {
		return yCellsNum;
	}
	public void setyCellsNum(Integer yCellsNum) {
		this.yCellsNum = yCellsNum;
	}
	public ArrayList<IndexCellCoordinates> mapDataPointToIndexCellCoordinates(Point point){
		ArrayList<IndexCellCoordinates> partitions = new ArrayList<IndexCellCoordinates>();
		Integer xCell = (int) (point.getX() / xStep);
		Integer yCell = (int) (point.getY() / yStep);
		if(xCell>=SpatioTextualConstants.xMaxRange/xStep)
			xCell=(int) ((SpatioTextualConstants.xMaxRange/xStep)-1);
		if(yCell>=SpatioTextualConstants.yMaxRange/xStep)
			yCell=(int) ((SpatioTextualConstants.yMaxRange/yStep)-1);
		if(xCell<0)
			xCell=0;
		if(yCell<0)
			yCell=0;
		partitions.add(new IndexCellCoordinates(xCell, yCell));
		return partitions;
	}
	public Integer mapIndexCellCoordinatedToTaskId(IndexCellCoordinates indexCellCoordinates){
		return evaluatorBoltTasks.get( indexCellCoordinates.getX() * yCellsNum + indexCellCoordinates.getY() );
	}
	private Integer mapDataPointToEvaluatorTask(Double x, Double y) {
		
		Integer xCell = (int) (x / xStep);
		Integer yCell = (int) (y / yStep);
		if(xCell>=SpatioTextualConstants.xMaxRange/xStep)
			xCell=(int) ((SpatioTextualConstants.xMaxRange/xStep)-1);
		if(yCell>=SpatioTextualConstants.yMaxRange/xStep)
			yCell=(int) ((SpatioTextualConstants.yMaxRange/yStep)-1);
		if(xCell<0)
			xCell=0;
		if(yCell<0)
			yCell=0;
		
		Integer partitionNum = xCell * yCellsNum + yCell;
		
		if (partitionNum >= evaluatorBoltTasks.size()) {
			System.out.println("error in data " + x + " , " + y + "  index is "
					+ partitionNum + " while partitions "
					+ evaluatorBoltTasks.size());
			return null;
		
		} else {
			// System.out.println("Point "+x+" , "+y+" is mapped to xcell:"+xCell+"ycell:"+yCell+" index is "+			 partitionNum+" to partitions "+evaluatorBoltTasks.get(partitionNum));
			 return  evaluatorBoltTasks.get(partitionNum);
		}
		
	}
	private ArrayList<Integer> mapRecangleToEvaluatorTask(Rectangle rectangle) {
		ArrayList<Integer> partitions = new ArrayList<Integer>();
		int xMinCell = (int) (rectangle.getMin().getX() / xStep);
		int yMinCell = (int) (rectangle.getMin().getY() / yStep);
		int xMaxCell = (int) (rectangle.getMax().getX() / xStep);
		int yMaxCell = (int) (rectangle.getMax().getY() / yStep);
		//to handle the case where data is outside the range of the bolts 
		if (xMaxCell >= SpatioTextualConstants.xMaxRange / xStep)
			xMaxCell = (int) ((SpatioTextualConstants.xMaxRange / xStep) - 1);
		if (yMaxCell >= SpatioTextualConstants.yMaxRange / xStep)
			yMaxCell = (int) ((SpatioTextualConstants.yMaxRange / yStep) - 1);
		if (xMinCell >= SpatioTextualConstants.xMaxRange / xStep)
			xMinCell = (int) ((SpatioTextualConstants.xMaxRange / xStep) - 1);
		else if (xMinCell < 0)
				xMinCell = 0;
		if (yMinCell >= SpatioTextualConstants.yMaxRange / xStep)
			yMinCell = (int) ((SpatioTextualConstants.yMaxRange / yStep) - 1);
		else if (yMinCell < 0)
			yMinCell = 0;
		for (Integer xCell = xMinCell; xCell <= xMaxCell; xCell++)
			for (Integer yCell = yMinCell; yCell <= yMaxCell; yCell++) {
			//	if(xCell==0&&yCell==0) continue;
				Integer partitionNum = xCell * yCellsNum + yCell;
				if (partitionNum >= evaluatorBoltTasks.size())
					System.out.println("error in rectangle " + rectangle.getMin().getX() + " , " + rectangle.getMin().getY()
							+ " , " + rectangle.getMax().getX() + " , " + rectangle.getMax().getY() + "  index is "
							+ partitionNum + " while partitions "
							+ evaluatorBoltTasks.size());
				else {
					// System.out.println("Query "+xmin+" , "+ymin+" , "+xmax+" , "+ymax+" is mapped to xcell:"+xCell+"ycell:"+yCell+" index is "+
					// partitionNum+" to partitions "+_targets.get(partitionNum));
					partitions.add(evaluatorBoltTasks.get(partitionNum));
				}

			}
		
		return partitions;
	}
	public GlobalIndexIterator globalKNNIterator(Point p){
		return new GlobalGridIndexIterator(this,p);
	}
	@Override
	public HashSet<String>  addTextToTaskID(ArrayList<Integer> tasks, ArrayList<String> text,boolean all,boolean forward) {
		return null;
	}
	@Override
	public void dropTextFromTaskID(ArrayList<Integer> tasks, ArrayList<String> text) {
		
	}
	@Override
	public Boolean verifyTextOverlap(Integer task, ArrayList<String> text) {
		
		return true;
	}
	@Override
	public Boolean isTextAware() {
		return false;
	}
	@Override
	public Boolean isTextOnlyIndex() {
		return false;
	}
	@Override
	public ArrayList<Integer> getTaskIDsContainingKeywordData(DataObject object) {
		return null;
	}

	@Override
	public ArrayList<Integer> getTaskIDsContainingKeywordQuery(Query query) {
		return null;
	}
}
