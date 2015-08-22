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
package edu.purdue.cs.tornado.index;

import java.util.ArrayList;
import java.util.HashMap;

import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class LocalGridIndex {
	private Integer localXcellCount;
	private Integer localYcellCount;
	private Double localXstep;
	private Double localYstep;
	ArrayList<ArrayList<IndexCell>> index ;
	private Rectangle selfBounds;
	DataSourceInformation dataSourcesInformation;
	private ArrayList<Query> globalKNNQueries;
	
	
	
	public LocalGridIndex(Rectangle selfBounds,DataSourceInformation dataSourcesInformation) {
		this.dataSourcesInformation = dataSourcesInformation;
		this.selfBounds = selfBounds;
		Double globalXrange = SpatioTextualConstants.xMaxRange;
		Double globalYrange = SpatioTextualConstants.yMaxRange;
		localXstep=(globalXrange/SpatioTextualConstants.fineGridGranularity);
		localYstep=(globalYrange/SpatioTextualConstants.fineGridGranularity);;
		localXcellCount = (int) ((selfBounds.getMax().getX()-selfBounds.getMin().getX())/localXstep) ;
		localYcellCount =  (int) ((selfBounds.getMax().getY()-selfBounds.getMin().getY())/localYstep) ;
		index = new ArrayList<ArrayList<IndexCell>>();
		globalKNNQueries = new ArrayList<Query>();
		for(int i =0;i<localXcellCount;i++){
			ArrayList<IndexCell> ycellsList = new ArrayList<IndexCell>();
			for(int j =0;j<localYcellCount;j++){
				Rectangle bounds = new Rectangle(new Point(i*localXstep+selfBounds.getMin().getX() ,  j*localYstep +selfBounds.getMin().getY()  ), new Point((i+1)*localXstep +selfBounds.getMin().getX(),  (j+1)*localYstep  +selfBounds.getMin().getY()));
				ycellsList.add(new IndexCell(bounds));
			}
			index.add(ycellsList);
		}
		
	}
	public Rectangle getSelfBounds() {
		return selfBounds;
	}
	public void setSelfBounds(Rectangle selfBounds) {
		this.selfBounds = selfBounds;
	}
	public Integer getLocalXcellCount() {
		return localXcellCount;
	}
	public void setLocalXcellCount(Integer localXcellCount) {
		this.localXcellCount = localXcellCount;
	}
	public Integer getLocalYcellCount() {
		return localYcellCount;
	}
	public void setLocalYcellCount(Integer localYcellCount) {
		this.localYcellCount = localYcellCount;
	}
	public Double getLocalXstep() {
		return localXstep;
	}
	public void setLocalXstep(Double localXstep) {
		this.localXstep = localXstep;
	}
	public Double getLocalYstep() {
		return localYstep;
	}
	public void setLocalYstep(Double localYstep) {
		this.localYstep = localYstep;
	}
	public ArrayList<ArrayList<IndexCell>> getIndex() {
		return index;
	}
	public void setIndex(ArrayList<ArrayList<IndexCell>> index) {
		this.index = index;
	}
	public IndexCell addDataObject(DataObject dataObject){
		IndexCellCoordinates cellCoordinates =mapDataPointToPartition(dataObject.getLocation()).get(0);
		IndexCell  indexCell = index.get(cellCoordinates.getX()).get(cellCoordinates.getY());
		indexCell.addDataObject(dataObject);
		return indexCell;
	}
	public IndexCell mapDataObjectToIndexCell(DataObject dataObject){
		IndexCellCoordinates cellCoordinates =mapDataPointToPartition(dataObject.getLocation()).get(0);
		IndexCell  indexCell = index.get(cellCoordinates.getX()).get(cellCoordinates.getY());
		return indexCell;
	}
	public Boolean addContinousQuery(Query query){
		
		if (query.getQueryType().equals(SpatioTextualConstants.queryTextualKNN)) {

			// initially assume that the incomming KKN query for a
			// volatile data object will go to global data entry for this bolt
			if (dataSourcesInformation.isVolatile()) {
				query.resetKNNStructures();
				globalKNNQueries.add(query);
			}
		}

		ArrayList<IndexCellCoordinates> indexCells = mapQueryToPartitions(query);
		for (IndexCellCoordinates indexCell : indexCells) {
			index.get(indexCell.getX()).get(indexCell.getY()).addQuery(query);

		}
		return true;
	}
	//TODO we need to find a better way to update a query 
	public Boolean updateContinousQuery(Query oldQuery, Query query){
		dropContinousQuery(oldQuery);
		addContinousQuery(query);
		return true;
	}
	/**
	 * This function retrtives all relevant queries that maybe affected by the addition of a new 
	 * @param dataObject
	 * @param fromNeighbour
	 * @return
	 */
	public HashMap<String, Query> getReleventQueries(DataObject dataObject,Boolean fromNeighbour){
		
		ArrayList<IndexCell> relevantIndexCells = new ArrayList<IndexCell>();
		if(fromNeighbour){
			relevantIndexCells =  getOverlappingIndexCells(dataObject.getRelevantArea());	
		}
		else{
			relevantIndexCells = getOverlappingIndexCells(dataObject.getLocation());
		}
		//this hashmap is based on query source id , query id itself
		HashMap<String, Query>  queriesMap = new HashMap<String, Query> ();
		//first consider all global KNN queries 
		for(Query q : globalKNNQueries){
			String unqQueryId = q.getUniqueIDFromQuerySourceAndQueryId();
			if(!queriesMap.containsKey(unqQueryId))
				queriesMap.put(unqQueryId, q);
		}
		for(IndexCell indexCell:relevantIndexCells){
			ArrayList<Query>queries  =indexCell.getQueries();
			for(Query q : queries){
				String unqQueryId = q.getUniqueIDFromQuerySourceAndQueryId();
				if(!queriesMap.containsKey(unqQueryId))
					queriesMap.put(unqQueryId, q);
			}
		}
		return queriesMap;
	}
	public ArrayList<IndexCell> getOverlappingIndexCells(Rectangle   rectangle){
		ArrayList<IndexCellCoordinates> relevantIndexCellCorredinates = mapRecToIndexCells(rectangle);
		ArrayList<IndexCell> relevantIndexCells = new ArrayList<IndexCell>();
		for (IndexCellCoordinates indexCellCoordinate:relevantIndexCellCorredinates)
			relevantIndexCells.add(index.get(indexCellCoordinate.getX()).get(indexCellCoordinate.getY()));
		return relevantIndexCells;
		
	}
	public ArrayList<IndexCell> getOverlappingIndexCells(Point   point){
		ArrayList<IndexCellCoordinates> relevantIndexCellCorredinates = mapDataPointToPartition(point);
		ArrayList<IndexCell> relevantIndexCells = new ArrayList<IndexCell>();
		for (IndexCellCoordinates indexCellCoordinate:relevantIndexCellCorredinates)
			relevantIndexCells.add(index.get(indexCellCoordinate.getX()).get(indexCellCoordinate.getY()));
		return relevantIndexCells;
		
	}
	public Boolean dropContinousQuery(Query query){
		//first check inside the globalNKK list and if found return 
		if(query.getQueryType().equals(SpatioTextualConstants.queryTextualKNN)){
			int i=0;
			boolean found = false;
			for(i =0;i<globalKNNQueries.size();i++ ){
				if(query.getSrcId().equals(globalKNNQueries.get(i).getSrcId())&&
						query.getQueryId().equals(globalKNNQueries.get(i).getQueryId())){
					found =true;
					break;
				}
			}
			if(found){
				globalKNNQueries.remove(i);
				return true;
			}
		}
		ArrayList<IndexCellCoordinates> indexCells = mapQueryToPartitions(query);
		for(IndexCellCoordinates indexCell :indexCells){
			index.get(indexCell.getX()).get(indexCell.getY()).dropQuery(query);
			if(query.getDataSrc2()!=null)
				index.get(indexCell.getX()).get(indexCell.getY()).dropQuery(query);
		}
		return true;
	}
	
	
	/**
	 * This function maps incoming data point to a grid cell inside the current evaluator bolt
	 * @param x
	 * @param y
	 * @return
	 */
	public ArrayList<IndexCellCoordinates> mapDataPointToPartition(Point point) {
		ArrayList<IndexCellCoordinates> partitions = new ArrayList<IndexCellCoordinates>();
		Double x = point.getX();
		Double y = point.getY();
		
		
		if(
				  (x<selfBounds.getMin().getX()&&!(Math.abs(x-selfBounds.getMin().getX())<.000001))
				||(y<selfBounds.getMin().getY()&&!(Math.abs(y-selfBounds.getMin().getY())<.000001))
				||(x>selfBounds.getMax().getX()&&!(Math.abs(x-selfBounds.getMax().getX())<.000001))
				||(y>selfBounds.getMax().getY()&&!(Math.abs(y-selfBounds.getMax().getY())<.000001))
						){
			System.err.println("Error Point: "+x+" , "+y+" is outside the range of this bolt ");
		}
		else{
			Integer xCell = (int) ((x-selfBounds.getMin().getX()) / localXstep);
			Integer yCell = (int) ((y-selfBounds.getMin().getY()) / localYstep);
			if(xCell>=localXcellCount)
				xCell=localXcellCount-1;
			if(yCell>=localYcellCount)
				yCell=localYcellCount-1;
			
			IndexCellCoordinates indexCellCoordinate = new IndexCellCoordinates(xCell,yCell);
			partitions.add(indexCellCoordinate);
			
		}
		return partitions;
	}
	private ArrayList<IndexCellCoordinates> mapRecToIndexCells(Rectangle rectangle){
		ArrayList<IndexCellCoordinates> partitions = new ArrayList<IndexCellCoordinates>();
		double xmin,ymin,xmax,ymax;
		if(rectangle==null ||selfBounds==null)
			return partitions;
		if(rectangle.getMin().getX()<selfBounds.getMin().getX())
			xmin = selfBounds.getMin().getX();
		else 
			xmin = rectangle.getMin().getX();
		if(rectangle.getMin().getY()<selfBounds.getMin().getY())
			ymin = selfBounds.getMin().getY();
		else 
			ymin = rectangle.getMin().getY();
		if(rectangle.getMax().getX()>selfBounds.getMax().getX())
			xmax = selfBounds.getMax().getX();//to prevent exceeding index range
		else
			xmax = rectangle.getMax().getX();
		if(rectangle.getMax().getY()>selfBounds.getMax().getY())
			ymax = selfBounds.getMax().getY();//to prevent exceeding index range
		else
			ymax = rectangle.getMax().getY();
		if(xmax == selfBounds.getMax().getX())
			xmax = selfBounds.getMax().getX()-1;
		if(ymax == selfBounds.getMax().getY())
			ymax = selfBounds.getMax().getY()-1;
		
		
		xmin-=selfBounds.getMin().getX();
		ymin-=selfBounds.getMin().getY();
		xmax-=selfBounds.getMin().getX();
		ymax-=selfBounds.getMin().getY();
		
		
		int xMinCell = (int) (xmin / localXstep);
		int yMinCell = (int) (ymin / localYstep);
		int xMaxCell = (int) (xmax / localXstep);
		int yMaxCell = (int) (ymax / localYstep);
		
		for (Integer xCell = xMinCell; xCell <= xMaxCell; xCell++)
			for (Integer yCell = yMinCell; yCell <= yMaxCell; yCell++) {
				IndexCellCoordinates indexCell = new IndexCellCoordinates(xCell,yCell);
				partitions.add(indexCell);
			}
		

		return partitions;
	}
	/**
	 * This function maps a query to a set of index cells that overlap the query's range
	 * check if the query does not overlap with the bolts rectangular bounds
	 * maps the query to the bounds of the current bolt if it exceeds it 
	 * @param query
	 * @return
	 */
	private ArrayList<IndexCellCoordinates> mapQueryToPartitions(Query query) {
		ArrayList<IndexCellCoordinates> partitions = new ArrayList<IndexCellCoordinates>();
		double xmin,ymin,xmax,ymax;
		if( query.getSpatialRange().getMin().getX()>selfBounds.getMax().getX()||
			query.getSpatialRange().getMin().getY()>selfBounds.getMax().getY()||
			query.getSpatialRange().getMax().getX()<selfBounds.getMin().getX()||
			query.getSpatialRange().getMax().getY()<selfBounds.getMin().getY()){
			System.err.println("Error query:"+query.getSrcId()+"  is outside the range of this bolt ");
		}
		else{
		   partitions =mapRecToIndexCells(query.getSpatialRange());
		}
		
		return partitions;
	}

	public LocalKNNGridIndexIterator LocalKNNIterator(Point focalPoint){
		return new LocalKNNGridIndexIterator(this, focalPoint);
	}
	public LocalKNNGridIndexIterator KNNIterator(Point focalPoint, Double distance){
		return new LocalKNNGridIndexIterator(this, focalPoint,distance);
	}
	
	
}
