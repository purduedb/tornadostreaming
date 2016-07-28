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
package edu.purdue.cs.tornado.atlas.SGB.index.grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.SpatialHelper;

public class KNNGridIndexIterator implements Iterator<ArrayList<IndexCell>>{
	private GridIndex gridIndex;
	Integer totalNumberofGridCells;
	Integer visitedNumberofGridCells;
	IndexCellCoordinates minIndexCellCoordinates, maxIndexCellCoordinates;
	Boolean[][] visitedCells;
	ArrayList<ArrayList<IndexCell>> orderedIndexList;
	Integer orderedListIndex;

	public KNNGridIndexIterator(GridIndex gridIndex, Point focalPoint) {
		initStructures(gridIndex, focalPoint);
		
	}
	private void initStructures(GridIndex gridIndex, Point focalPoint){
		orderedIndexList = new ArrayList<ArrayList<IndexCell>>();
		this.gridIndex = gridIndex;
		totalNumberofGridCells = gridIndex.getLocalXcellCount() * gridIndex.getLocalYcellCount();
		visitedNumberofGridCells = 0;
		if(SpatialHelper.overlapsSpatially(focalPoint, gridIndex.getSelfBounds())){
			minIndexCellCoordinates = gridIndex.mapDataPointToPartition(focalPoint);
			maxIndexCellCoordinates = gridIndex.mapDataPointToPartition(focalPoint);
		}
		else{
			int x =0,y=0;
			if(focalPoint.getX()<gridIndex.getSelfBounds().getMin().getX())
				x=0;
			if(focalPoint.getX()>gridIndex.getSelfBounds().getMax().getX())
				x=gridIndex.getLocalXcellCount()-1;
			if(focalPoint.getY()<gridIndex.getSelfBounds().getMin().getY())
				y=0;
			if(focalPoint.getY()>gridIndex.getSelfBounds().getMax().getY())
				y=gridIndex.getLocalYcellCount()-1;
			minIndexCellCoordinates = new IndexCellCoordinates(x,y);
			maxIndexCellCoordinates = new IndexCellCoordinates(x,y);
		}
		visitedCells = new Boolean[gridIndex.getLocalXcellCount()][gridIndex.getLocalYcellCount()];
		for (Integer i = 0; i < gridIndex.getLocalXcellCount(); i++)
			for (Integer j = 0; j < gridIndex.getLocalYcellCount(); j++)
				visitedCells[i][j] = false;
		while(visitedNumberofGridCells<totalNumberofGridCells)
			orderedIndexList.add(getNextRound());
		orderedListIndex=0;
	}

	public KNNGridIndexIterator(GridIndex gridIndex, Point focalPoint, Double stratingDistance) {
		this(gridIndex, focalPoint);

		ArrayList<IndexCell> currentRoundIndexCells;
		Boolean stop = false;
		while (hasNext() && !stop) {
			currentRoundIndexCells = next();
			for (IndexCell indexCell : currentRoundIndexCells) {
				if (SpatialHelper.getMaxDistanceBetween(focalPoint, indexCell.getBounds()) > stratingDistance) {
					{
						stop = true;
						break;
					}

				}
			}
			if (stop == true) {
				orderedListIndex = Math.max(0, orderedListIndex - 1);
			}
		}

	}

	@Override
	public boolean hasNext() {
		return (orderedListIndex<orderedIndexList.size()-1);
	}
	
	public boolean hasPrevious() {
		return (orderedListIndex>0);
	}

	@Override
	public ArrayList<IndexCell> next() {
		if (!this.hasNext()) {
			throw new NoSuchElementException("Nothing Next ");
		}
		return orderedIndexList.get(orderedListIndex++);	
	}
	
	public ArrayList<IndexCell> previous() {
		if (!this.hasPrevious()) {
			throw new NoSuchElementException("Nothing Previous");
		}
		return orderedIndexList.get(--orderedListIndex);	
	}
	private ArrayList<IndexCell> getNextRound(){
		ArrayList<IndexCell> indexCellList = new ArrayList<>();
		for (Integer i = Math.max(minIndexCellCoordinates.getX(), 0) ; i <= Math.min(maxIndexCellCoordinates.getX(),gridIndex.getLocalXcellCount()-1); i++) {
			if (!visitedCells[i][minIndexCellCoordinates.getY()]) {
				visitedCells[i][minIndexCellCoordinates.getY()]=true;
				if(gridIndex.getIndex().get(i)!=null&&gridIndex.getIndex().get(i).get(minIndexCellCoordinates.getY())!=null)
					indexCellList.add(gridIndex.getIndex().get(i).get(minIndexCellCoordinates.getY()));
				visitedNumberofGridCells++;
			}
			if (!visitedCells[i][maxIndexCellCoordinates.getY()]) {
				visitedCells[i][maxIndexCellCoordinates.getY()]=true;
				if(gridIndex.getIndex().get(i)!=null&&gridIndex.getIndex().get(i).get(maxIndexCellCoordinates.getY())!=null)	
					indexCellList.add(gridIndex.getIndex().get(i).get(maxIndexCellCoordinates.getY()));
				visitedNumberofGridCells++;
			}
		}
	
		for (Integer i =  Math.max(minIndexCellCoordinates.getY()+1,0); i <= Math.min(maxIndexCellCoordinates.getY()-1,gridIndex.getLocalYcellCount()-1); i++) {
			if (!visitedCells[minIndexCellCoordinates.getX()][i]) {
				visitedCells[minIndexCellCoordinates.getX()][i]=true;
				if(gridIndex.getIndex().get(minIndexCellCoordinates.getX())!=null&&gridIndex.getIndex().get(minIndexCellCoordinates.getX()).get(i)!=null)
					indexCellList.add(gridIndex.getIndex().get(minIndexCellCoordinates.getX()).get(i));
				visitedNumberofGridCells++;
			}
			try{
			if (!visitedCells[maxIndexCellCoordinates.getX()][i]) {
				visitedCells[maxIndexCellCoordinates.getX()][i]=true;
				if(gridIndex.getIndex().get(maxIndexCellCoordinates.getX())!=null&&gridIndex.getIndex().get(maxIndexCellCoordinates.getX()).get(i)!=null)	
					indexCellList.add(gridIndex.getIndex().get(maxIndexCellCoordinates.getX()).get(i));
				visitedNumberofGridCells++;
			}}catch(Exception e){
				e.printStackTrace();
			}
		}
		minIndexCellCoordinates.setX(Math.max(minIndexCellCoordinates.getX() - 1,0));
		minIndexCellCoordinates.setY(Math.max(minIndexCellCoordinates.getY() - 1,0));
		maxIndexCellCoordinates.setX(Math.min(maxIndexCellCoordinates.getX() + 1,gridIndex.getLocalXcellCount()-1));
		maxIndexCellCoordinates.setY(Math.min(maxIndexCellCoordinates.getY() + 1,gridIndex.getLocalYcellCount()-1));
		return indexCellList;
	}
	@Override
	public void remove() {
		throw new UnsupportedOperationException("It is read-only");

	}
	
}
