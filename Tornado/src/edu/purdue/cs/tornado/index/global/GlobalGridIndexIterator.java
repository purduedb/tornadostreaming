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
import java.util.NoSuchElementException;

import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.SpatialHelper;

public class GlobalGridIndexIterator extends GlobalIndexIterator{
	private GlobalGridIndex gridIndex;
	Integer totalNumberofGridCells;
	Integer visitedNumberofGridCells;
	IndexCellCoordinates minIndexCellCoordinates, maxIndexCellCoordinates;
	Boolean[][] visitedCells;
	ArrayList<ArrayList<Integer>> orderedIndexList;
	Integer orderedListIndex;
	Point focalPoint;

	public GlobalGridIndexIterator(GlobalGridIndex gridIndex, Point focalPoint) {
		this.gridIndex = gridIndex;
		this.focalPoint = focalPoint;
		initStructures(gridIndex, focalPoint);
		
	}
	private void initStructures(GlobalGridIndex gridIndex, Point focalPoint){
		orderedIndexList = new ArrayList<ArrayList<Integer>>();
		
		totalNumberofGridCells = gridIndex.getxCellsNum() * gridIndex.getyCellsNum();
		visitedNumberofGridCells = 0;
		
		minIndexCellCoordinates = gridIndex.mapDataPointToIndexCellCoordinates(focalPoint).get(0);
		maxIndexCellCoordinates = gridIndex.mapDataPointToIndexCellCoordinates(focalPoint).get(0);
		
		visitedCells = new Boolean[gridIndex.getxCellsNum()][gridIndex.getyCellsNum()];
		for (Integer i = 0; i < gridIndex.getxCellsNum(); i++)
			for (Integer j = 0; j < gridIndex.getyCellsNum(); j++)
				visitedCells[i][j] = false;
		while(visitedNumberofGridCells<totalNumberofGridCells)
			orderedIndexList.add(getNextRound());
		orderedListIndex=1; //initlize this to 1 to avoid retriving the current evluator when initiating the evaluation
	}


	@Override
	public boolean hasNext() {
		return (orderedListIndex<orderedIndexList.size()-1);
	}
	@Override
	public boolean hasPrevious() {
		return (orderedListIndex>0);
	}
	@Override
	public void reset(){
		initStructures(this.gridIndex, this.focalPoint);
	}
	@Override
	public ArrayList<Integer> next() {
		if (!this.hasNext()) {
			throw new NoSuchElementException("Nothing Next ");
		}
		return orderedIndexList.get(orderedListIndex++);	
	}
	@Override
	public  ArrayList<Integer> current(){
		if(orderedListIndex>0)
			return orderedIndexList.get(orderedListIndex-1);
		else{
			throw new NoSuchElementException("Nothing Previous");
		}
	
	}
	@Override
	public Double getMinDistOfNextIteration(){
		Double dist=Double.MAX_VALUE; 
		if (hasNext()){
			for(Integer taskId :orderedIndexList.get(orderedListIndex) ){
				Double newDist =SpatialHelper.getMinDistanceBetween(focalPoint, gridIndex.getBoundsForTaskId(taskId));
				if(newDist<dist) dist= newDist;
			}
			return dist;
		}
		else 
			return null;
	}
	@Override
	public ArrayList<Integer> previous() {
		if (!this.hasPrevious()) {
			throw new NoSuchElementException("Nothing Previous");
		}
		return orderedIndexList.get(--orderedListIndex);	
	}
	private ArrayList<Integer> getNextRound(){
		ArrayList<Integer> indexCellList = new ArrayList<Integer>();
		for (Integer i = Math.max(minIndexCellCoordinates.getX(), 0) ; i <= Math.min(maxIndexCellCoordinates.getX(),gridIndex.getxCellsNum()-1); i++) {
			if (!visitedCells[i][minIndexCellCoordinates.getY()]) {
				visitedCells[i][minIndexCellCoordinates.getY()]=true;
				//indexCellList.add(gridIndex.getIndex().get(i).get(minIndexCellCoordinates.getY()));
				indexCellList.add(gridIndex.mapIndexCellCoordinatedToTaskId(new IndexCellCoordinates(i, minIndexCellCoordinates.getY())));
				visitedNumberofGridCells++;
			}
			if (!visitedCells[i][maxIndexCellCoordinates.getY()]) {
				visitedCells[i][maxIndexCellCoordinates.getY()]=true;
				//indexCellList.add(gridIndex.getIndex().get(i).get(maxIndexCellCoordinates.getY()));
				indexCellList.add(gridIndex.mapIndexCellCoordinatedToTaskId(new IndexCellCoordinates(i, maxIndexCellCoordinates.getY())));
				visitedNumberofGridCells++;
			}
		}
	
		for (Integer i =  Math.max(minIndexCellCoordinates.getY()+1,0); i <= Math.min(maxIndexCellCoordinates.getY()-1,gridIndex.getyCellsNum()-1); i++) {
			if (!visitedCells[minIndexCellCoordinates.getX()][i]) {
				visitedCells[minIndexCellCoordinates.getX()][i]=true;
				//indexCellList.add(gridIndex.getIndex().get(minIndexCellCoordinates.getX()).get(i));
				indexCellList.add(gridIndex.mapIndexCellCoordinatedToTaskId(new IndexCellCoordinates(minIndexCellCoordinates.getX(), i)));
				visitedNumberofGridCells++;
			}
			if (!visitedCells[maxIndexCellCoordinates.getX()][i]) {
				visitedCells[maxIndexCellCoordinates.getX()][i]=true;
			//	indexCellList.add(gridIndex.getIndex().get(maxIndexCellCoordinates.getX()).get(i));
				indexCellList.add(gridIndex.mapIndexCellCoordinatedToTaskId(new IndexCellCoordinates(maxIndexCellCoordinates.getX(), i)));
				visitedNumberofGridCells++;
			}
		}
		minIndexCellCoordinates.setX(Math.max(minIndexCellCoordinates.getX() - 1,0));
		minIndexCellCoordinates.setY(Math.max(minIndexCellCoordinates.getY() - 1,0));
		maxIndexCellCoordinates.setX(Math.min(maxIndexCellCoordinates.getX() + 1,gridIndex.getxCellsNum()-1));
		maxIndexCellCoordinates.setY(Math.min(maxIndexCellCoordinates.getY() + 1,gridIndex.getyCellsNum()-1));
		return indexCellList;
	}
	@Override
	public void remove() {
		throw new UnsupportedOperationException("It is read-only");

	}
	
}
