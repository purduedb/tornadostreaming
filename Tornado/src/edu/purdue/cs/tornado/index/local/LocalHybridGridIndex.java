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
package edu.purdue.cs.tornado.index.local;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.QueryType;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.KNNQuery;
import edu.purdue.cs.tornado.messages.Query;

public class LocalHybridGridIndex extends LocalHybridIndex {
	//	private Integer localXcellCount;
	//	private Integer localYcellCount;
	public Double localXstep;
	public Double localYstep;

	public HashMap<Integer, HashMap<Integer, IndexCell>> index;
	public Rectangle selfBounds;
	public DataSourceInformation dataSourcesInformation;
	public ArrayList<Query> globalKNNQueries;
	public Integer xGridGranularity;
	public Integer yGridGranularity;
	public Boolean spatialOnlyFlag;
	public Integer allDataCount;
	public Integer level;
	public Cell myPartition;
	public Integer fineGridGran;

	public HashMap<String, Integer> overallQueryTextSummery;
	public boolean textUpdated;

	public LocalHybridGridIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer fineGridGran) {
		this(selfBounds, dataSourcesInformation, fineGridGran, fineGridGran, false, 0);
	}

	public LocalHybridGridIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer xGridGranularity, Integer yGridGranularity) {
		this(selfBounds, dataSourcesInformation, xGridGranularity, yGridGranularity, false, 0);
	}

	public LocalHybridGridIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer xGridGranularity, Integer yGridGranularity, Boolean spatialOnlyFlag, Integer level) {
		super();
		this.spatialOnlyFlag = spatialOnlyFlag;
		this.allDataCount = 0;
		this.dataSourcesInformation = dataSourcesInformation;
		this.selfBounds = selfBounds;
		Double globalXrange = SpatioTextualConstants.xMaxRange;
		Double globalYrange = SpatioTextualConstants.yMaxRange;
		this.xGridGranularity = xGridGranularity;
		this.yGridGranularity = yGridGranularity;
		this.localXstep = (globalXrange / this.xGridGranularity);
		this.localYstep = (globalYrange / this.yGridGranularity);
		this.textUpdated = false;
		//		this.localXcellCount = (int) ((selfBounds.getMax().getX() - selfBounds.getMin().getX()) / localXstep);
		//		this.localYcellCount = (int) ((selfBounds.getMax().getY() - selfBounds.getMin().getY()) / localYstep);
		this.index = new HashMap<Integer, HashMap<Integer, IndexCell>>();
		this.globalKNNQueries = new ArrayList<Query>();
		this.level = level;
		overallQueryTextSummery = new HashMap<String, Integer>();
		this.myPartition = new Cell((int) (selfBounds.getMin().getY() / localYstep), (int) (selfBounds.getMax().getY() / localYstep), (int) (selfBounds.getMin().getX() / localXstep), (int) (selfBounds.getMax().getX() / localXstep));
	}

	public Boolean addContinousQuery(Query query) {
		Boolean completed = true;
		if (query.getQueryType().equals(QueryType.queryTextualKNN)) {

			// initially assume that the incomming KKN query for a
			// volatile data object will go to global data entry for this bolt
			if (dataSourcesInformation.isVolatile()) {
				((KNNQuery)query).resetKNNStructures();
				globalKNNQueries.add(query);
			}
		}

		ArrayList<IndexCellCoordinates> indexCells = mapQueryToPartitions(query);
		for (IndexCellCoordinates indexCellCoordinate : indexCells) {

			IndexCell indexCell = getIndexCellFromCoordinates(indexCellCoordinate);//index.get(cellCoordinates.getX()).get(cellCoordinates.getY());
			if (indexCell == null) {
				indexCell = new IndexCell(getBoundForIndexCell(indexCellCoordinate), spatialOnlyFlag, level, indexCellCoordinate);
				addIndexCellFromCoordinates(indexCellCoordinate, indexCell);

			}
			if (!indexCell.transmitted){
				indexCell.addQuery(query);
				for (String s : query.getQueryText()) {
					if (!overallQueryTextSummery.containsKey(s))
						overallQueryTextSummery.put(s, 1);
					else
						overallQueryTextSummery.put(s, overallQueryTextSummery.get(s) + 1);
				}
			}else

				completed = false;
		}
		
		return completed;
	}

	public IndexCell addDataObject(DataObject dataObject) {
		IndexCellCoordinates cellCoordinates = mapDataPointToPartition(dataObject.getLocation());
		IndexCell indexCell = getIndexCellFromCoordinates(cellCoordinates);//index.get(cellCoordinates.getX()).get(cellCoordinates.getY());
		if (indexCell == null) {
			indexCell = new IndexCell(getBoundForIndexCell(cellCoordinates), spatialOnlyFlag, level, cellCoordinates);

			addIndexCellFromCoordinates(cellCoordinates, indexCell);

		}
		indexCell.addDataObject(dataObject);
		allDataCount++;
		return indexCell;
	}

	public IndexCell addDataObjectStatics(DataObject dataObject) {
		IndexCellCoordinates cellCoordinates = mapDataPointToPartition(dataObject.getLocation());
		IndexCell indexCell = getIndexCellFromCoordinates(cellCoordinates);//index.get(cellCoordinates.getX()).get(cellCoordinates.getY());
		if (indexCell == null) {
			indexCell = new IndexCell(getBoundForIndexCell(cellCoordinates), spatialOnlyFlag, level);
			addIndexCellFromCoordinates(cellCoordinates, indexCell);

		}
		indexCell.addDataObjectStatics(dataObject);
		allDataCount++;
		return indexCell;
	}

	private void addIndexCellFromCoordinates(IndexCellCoordinates indexCellCoordinate, IndexCell indexCell) {
		if (!index.containsKey(indexCellCoordinate.getX())) {
			HashMap<Integer, IndexCell> yCellList = new HashMap<Integer, IndexCell>();
			index.put(indexCellCoordinate.getX(), yCellList);
		}
		index.get(indexCellCoordinate.getX()).put(indexCellCoordinate.getY(), indexCell);
	}

	public Boolean dropContinousQuery(Query query) {
		//first check inside the globalNKK list and if found return 
		if (query.getQueryType().equals(QueryType.queryTextualKNN)) {
			int i = 0;
			boolean found = false;
			for (i = 0; i < globalKNNQueries.size(); i++) {
				if (query.getSrcId().equals(globalKNNQueries.get(i).getSrcId()) && query.getQueryId().equals(globalKNNQueries.get(i).getQueryId())) {
					found = true;
					break;
				}
			}
			if (found) {
				globalKNNQueries.remove(i);
				return true;
			}
		}
		ArrayList<IndexCellCoordinates> indexCells = mapQueryToPartitions(query);
		for (IndexCellCoordinates indexCell : indexCells) {
			if (indexCell == null)
				continue;
			index.get(indexCell.getX()).get(indexCell.getY()).dropQuery(query);

		}
		for (String s : query.getQueryText()) {
			if (overallQueryTextSummery.containsKey(s)) {
				int remainingCount = overallQueryTextSummery.get(s) - indexCells.size();
				if (remainingCount > 0)
					overallQueryTextSummery.put(s, remainingCount);
				else if (remainingCount <= 0){
					overallQueryTextSummery.remove(s);
					textUpdated = true;
				}

			}

		}
		return true;
	}

	Rectangle getBoundForIndexCell(IndexCellCoordinates indexCellCoordinates) {
		Integer i = indexCellCoordinates.getX();
		Integer j = indexCellCoordinates.getY();
		Rectangle bounds = new Rectangle(new Point(i * localXstep + selfBounds.getMin().getX(), j * localYstep + selfBounds.getMin().getY()),
				new Point((i + 1) * localXstep + selfBounds.getMin().getX(), (j + 1) * localYstep + selfBounds.getMin().getY()));
		return bounds;
	}

	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords) {
		if (spatialOnlyFlag)
			return allDataCount;
		Integer sum = 0;
		ArrayList<IndexCellCoordinates> indexCells = mapRecToIndexCells(selfBounds);
		for (IndexCellCoordinates indexCoordinate : indexCells) {
			IndexCell indexCell = getIndexCellFromCoordinates(indexCoordinate);
			if (indexCell == null)
				continue;
			sum += indexCell.estimateDataObjectCountAll(keywords);

		}
		return sum;
	}

	@Override
	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords, Rectangle rect) {
		Integer sum = 0;
		ArrayList<IndexCellCoordinates> indexCells = mapRecToIndexCells(rect);
		for (IndexCellCoordinates indexCoordinate : indexCells) {
			IndexCell indexCell = getIndexCellFromCoordinates(indexCoordinate);
			if (indexCell == null)
				continue;
			sum += indexCell.estimateDataObjectCountAll(keywords);
		}
		return sum;
	}

	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords) {
		if (spatialOnlyFlag)
			return allDataCount;
		Integer sum = 0;
		ArrayList<IndexCellCoordinates> indexCells = mapRecToIndexCells(selfBounds);
		for (IndexCellCoordinates indexCoordinate : indexCells) {
			IndexCell indexCell = getIndexCellFromCoordinates(indexCoordinate);
			if (indexCell == null)
				continue;
			sum += indexCell.estimateDataObjectCountAny(keywords);
		}
		return sum;
	}

	@Override
	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords, Rectangle rect) {
		Integer sum = 0;
		ArrayList<IndexCellCoordinates> indexCells = mapRecToIndexCells(rect);
		for (IndexCellCoordinates indexCoordinate : indexCells) {
			IndexCell indexCell = getIndexCellFromCoordinates(indexCoordinate);
			if (indexCell == null)
				continue;
			sum += indexCell.estimateDataObjectCountAny(keywords);
		}
		return sum;
	}

	public Integer getCountPerRec(Rectangle rec) {
		Integer sum = 0;
		ArrayList<IndexCellCoordinates> indexCells = mapRecToIndexCells(rec);
		for (IndexCellCoordinates indexCoordinate : indexCells) {
			IndexCell indexCell = getIndexCellFromCoordinates(indexCoordinate);
			if (indexCell == null)
				continue;
			sum += indexCell.getDataObjectCount();
		}
		return sum;

	}

	public HashMap<Integer, HashMap<Integer, IndexCell>> getIndex() {
		return index;
	}

	public IndexCell getIndexCellCreateIfNull(IndexCellCoordinates indexCellCoordinates, Boolean withChildren) {

		IndexCell indexCell = getIndexCellFromCoordinates(indexCellCoordinates);//index.get(cellCoordinates.getX()).get(cellCoordinates.getY());
		if (indexCell == null) {

			indexCell = new IndexCell(getBoundForIndexCell(indexCellCoordinates), spatialOnlyFlag, level, indexCellCoordinates);
			if (withChildren) {
				indexCell.children = new IndexCell[4];
				indexCell.children[0] = indexCell.children[1] = indexCell.children[2] = indexCell.children[3] = null;
			}
			addIndexCellFromCoordinates(indexCellCoordinates, indexCell);

		}
		return indexCell;
	}

	public IndexCell getIndexCellCreateIfNull(Point point, Boolean withChildren) {
		IndexCellCoordinates indexCellCoordinates = mapDataPointToPartition(point);
		return getIndexCellCreateIfNull(indexCellCoordinates, withChildren);

	}

	public IndexCell getIndexCellFromCoordinates(IndexCellCoordinates indexCell) {
		if (index.get(indexCell.getX()) == null) {
			return null;
		}
		return index.get(indexCell.getX()).get(indexCell.getY());
	}

	public Double getLocalXstep() {
		return localXstep;
	}

	public Double getLocalYstep() {
		return localYstep;
	}

	public IndexCell getOverlappingIndexCells(Point point) {
		IndexCellCoordinates relevantIndexCellCorredinates = mapDataPointToPartition(point);
		return getIndexCellFromCoordinates(relevantIndexCellCorredinates);

	}

	public ArrayList<IndexCell> getOverlappingIndexCells(Rectangle rectangle) {
		ArrayList<IndexCellCoordinates> relevantIndexCellCorredinates = mapRecToIndexCells(rectangle);
		ArrayList<IndexCell> relevantIndexCells = new ArrayList<IndexCell>();
		for (IndexCellCoordinates indexCellCoordinate : relevantIndexCellCorredinates)
			relevantIndexCells.add(index.get(indexCellCoordinate.getX()).get(indexCellCoordinate.getY()));
		return relevantIndexCells;

	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(ArrayList<String> keywords) {
		return getOverlappingIndexCellWithData(selfBounds, keywords);
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle) {
		ArrayList<IndexCellCoordinates> relevantIndexCellCorredinates = mapRecToIndexCells(rectangle);
		ArrayList<IndexCell> relevantIndexCells = new ArrayList<IndexCell>();
		for (IndexCellCoordinates indexCellCoordinate : relevantIndexCellCorredinates) {
			IndexCell indexCell = getIndexCellFromCoordinates(indexCellCoordinate);
			if (indexCell != null && indexCell.getDataObjectCount() > 0)
				relevantIndexCells.add(indexCell);
		}
		return relevantIndexCells;
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle, ArrayList<String> keywords) {
		if (spatialOnlyFlag)
			return getOverlappingIndexCellWithData(rectangle);
		else {
			ArrayList<IndexCellCoordinates> relevantIndexCellCorredinates = mapRecToIndexCells(rectangle);
			ArrayList<IndexCell> relevantIndexCells = new ArrayList<IndexCell>();
			for (IndexCellCoordinates indexCellCoordinate : relevantIndexCellCorredinates) {
				IndexCell indexCell = getIndexCellFromCoordinates(indexCellCoordinate);
				if (indexCell != null && indexCell.getDataObjectCount() > 0 && TextHelpers.overlapsTextually(indexCell.getAllDataTextInCell(), keywords))
					relevantIndexCells.add(indexCell);
			}
			return relevantIndexCells;
		}

	}

	/**
	 * This function retrtives all relevant queries that maybe affected by the
	 * addition of a new
	 * 
	 * @param dataObject
	 * @param fromNeighbour
	 * @return
	 */
	public HashMap<String, Query> getReleventQueries(DataObject dataObject, Boolean fromNeighbour) {

		//this hashmap is based on query source id , query id itself
		HashMap<String, Query> queriesMap = new HashMap<String, Query>();

		//first consider all global KNN queries 
		for (Query q : globalKNNQueries) {
			String unqQueryId = q.getUniqueIDFromQuerySourceAndQueryId();
			if (!queriesMap.containsKey(unqQueryId))
				queriesMap.put(unqQueryId, q);
		}
		ArrayList<IndexCell> relevantIndexCells;
		if (fromNeighbour) {
			relevantIndexCells = getOverlappingIndexCells(dataObject.getRelevantArea());
			for (IndexCell indexCell : relevantIndexCells) {
				if (indexCell == null || indexCell.getQueries() == null)
					continue;
			    List<Query> queries = indexCell.getQueries();
				for (Query q : queries) {
					String unqQueryId = q.getUniqueIDFromQuerySourceAndQueryId();
					if (!queriesMap.containsKey(unqQueryId))
						queriesMap.put(unqQueryId, q);
				}
			}
		} else {
			IndexCell indexCell = getOverlappingIndexCells(dataObject.getLocation());
			if (indexCell != null && indexCell.getQueries() != null) {
				List<Query> queries = indexCell.getQueries();
				for (Query q : queries) {
					String unqQueryId = q.getUniqueIDFromQuerySourceAndQueryId();
					if (!queriesMap.containsKey(unqQueryId))
						queriesMap.put(unqQueryId, q);
				}
			}
		}

		return queriesMap;
	}

	public Rectangle getSelfBounds() {
		return selfBounds;
	}

	public LocalKNNGridIndexIterator KNNIterator(Point focalPoint, Double distance) {
		return new LocalKNNGridIndexIterator(this, focalPoint, distance);
	}

	public LocalKNNGridIndexIterator LocalKNNIterator(Point focalPoint) {
		return new LocalKNNGridIndexIterator(this, focalPoint);
	}

	public IndexCell mapDataObjectToIndexCell(DataObject dataObject) {
		IndexCellCoordinates cellCoordinates = mapDataPointToPartition(dataObject.getLocation());
		IndexCell indexCell = getIndexCellFromCoordinates(cellCoordinates);
		return indexCell;
	}

	/**
	 * This function maps incoming data point to a grid cell inside the current
	 * evaluator bolt
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public IndexCellCoordinates mapDataPointToPartition(Point point) {
		Double x = point.getX();
		Double y = point.getY();

		//		Integer xCell = (int) ((x - selfBounds.getMin().getX()) / localXstep);
		//		Integer yCell = (int) ((y - selfBounds.getMin().getY()) / localYstep);
		Integer xCell = (int) ((x) / localXstep);
		Integer yCell = (int) ((y) / localYstep);
		//		if (xCell >= localXcellCount)
		//			xCell = localXcellCount - 1;
		//		if (yCell >= localYcellCount)
		//			yCell = localYcellCount - 1;

		IndexCellCoordinates indexCellCoordinate = new IndexCellCoordinates(xCell, yCell);

		return indexCellCoordinate;
	}

	/**
	 * This function maps a query to a set of index cells that overlap the
	 * query's range check if the query does not overlap with the bolts
	 * rectangular bounds maps the query to the bounds of the current bolt if it
	 * exceeds it
	 * 
	 * @param query
	 * @return
	 */
	private ArrayList<IndexCellCoordinates> mapQueryToPartitions(Query query) {
		ArrayList<IndexCellCoordinates> partitions = new ArrayList<IndexCellCoordinates>();
		if (QueryType.queryTextualRange.equals(query.getQueryType()) || QueryType.queryTextualSpatialJoin.equals(query.getQueryType())) {
			//			if (query.getSpatialRange().getMin().getX() > selfBounds.getMax().getX() || query.getSpatialRange().getMin().getY() > selfBounds.getMax().getY() || query.getSpatialRange().getMax().getX() < selfBounds.getMin().getX()
			//					|| query.getSpatialRange().getMax().getY() < selfBounds.getMin().getY()) {
			//				System.err.println("Error query:" + query.getSrcId() + "  is outside the range of this bolt ");
			//			} else {
			partitions = mapRecToIndexCells(query.getSpatialRange());
			//			}
		} else if (QueryType.queryTextualKNN.equals(query.getQueryType())) {
			partitions.add(mapDataPointToPartition(((KNNQuery)query).getFocalPoint()));
		}
		return partitions;
	}

	private ArrayList<IndexCellCoordinates> mapRecToIndexCells(Rectangle rectangle) {
		ArrayList<IndexCellCoordinates> partitions = new ArrayList<IndexCellCoordinates>();
		double xmin, ymin, xmax, ymax;

		//		if (rectangle == null || selfBounds == null)
		//			return partitions;
		//
		//		if (rectangle.getMin().getX() < selfBounds.getMin().getX())
		//			xmin = selfBounds.getMin().getX();
		//		else
		//			xmin = rectangle.getMin().getX();
		//
		//		if (rectangle.getMin().getY() < selfBounds.getMin().getY())
		//			ymin = selfBounds.getMin().getY();
		//		else
		//			ymin = rectangle.getMin().getY();
		//
		//		if (rectangle.getMax().getX() > selfBounds.getMax().getX())
		//			xmax = selfBounds.getMax().getX();//to prevent exceeding index range
		//		else
		//			xmax = rectangle.getMax().getX();
		//
		//		if (rectangle.getMax().getY() > selfBounds.getMax().getY())
		//			ymax = selfBounds.getMax().getY();//to prevent exceeding index range
		//		else
		//			ymax = rectangle.getMax().getY();
		//
		//		if (xmax == selfBounds.getMax().getX())
		//			xmax = selfBounds.getMax().getX() - 1;
		//		if (ymax == selfBounds.getMax().getY())
		//			ymax = selfBounds.getMax().getY() - 1;

		//		xmin -= selfBounds.getMin().getX();
		//		ymin -= selfBounds.getMin().getY();
		//		xmax -= selfBounds.getMin().getX();
		//		ymax -= selfBounds.getMin().getY();

		int xMinCell = (int) (rectangle.getMin().getX() / localXstep);
		int yMinCell = (int) (rectangle.getMin().getY() / localYstep);
		int xMaxCell = (int) (rectangle.getMax().getX() / localXstep);
		int yMaxCell = (int) (rectangle.getMax().getY() / localYstep);

//		for (Integer xCell = Math.max(xMinCell, myPartition.getLeft());  xCell < Math.min(xMaxCell,myPartition.getRight()); xCell++)
//			for (Integer yCell = Math.max(yMinCell,myPartition.getBottom()); yCell < Math.min(yMaxCell,myPartition.getTop()); yCell++) {
//				IndexCellCoordinates indexCell = new IndexCellCoordinates(xCell, yCell);
//				partitions.add(indexCell);
//			}

		for (Integer xCell = xMinCell;  xCell <= xMaxCell; xCell++)
		for (Integer yCell = yMinCell ;yCell <= yMaxCell; yCell++) {
			IndexCellCoordinates indexCell = new IndexCellCoordinates(xCell, yCell);
			partitions.add(indexCell);
		}
		
		return partitions;
	}

	public Integer getLocalXcellCount() {
		return (int) this.myPartition.dimensions[0];
	}

	public Integer getLocalYcellCount() {
		return (int) this.myPartition.dimensions[1];
	}

	public void setSelfBounds(Rectangle selfBounds) {
		this.selfBounds = selfBounds;
	}

	//TODO we need to find a better way to update a query 
	public Boolean updateContinousQuery(Query oldQuery, Query query) {
		dropContinousQuery(oldQuery);
		addContinousQuery(query);
		return true;
	}

	@Override
	public ArrayList<List<Query>> getReleventSpatialKeywordRangeQueries(DataObject dataObject, Boolean fromNeighbour) {

		IndexCellCoordinates cellCoordinates = mapDataPointToPartition(dataObject.getLocation());
		IndexCell indexCell = getIndexCellFromCoordinates(cellCoordinates);
		if (indexCell == null)
			return null;
		ArrayList<List<Query>> result = indexCell.geSpatiotTextualOverlappingQueries(dataObject.getLocation(), dataObject.getObjectText());

		return result;
	}

	public ArrayList<IndexCell> getIndexCellsFromPartition(Cell partition) {
		ArrayList<IndexCell> result = new ArrayList<IndexCell>();
		for (int i = partition.getLeft(); i < partition.getRight(); i++)
			if (index.containsKey(i)) {
				for (int j = partition.getBottom(); j < partition.getTop(); j++)
					if (index.get(i).containsKey(j))
						result.add(index.get(i).get(j));
			}
		return result;
	}

	public void removeIndexCellsFromPartition(Cell partition, boolean textAware) {
		for (int i = partition.getLeft(); i < partition.getRight(); i++)
			if (index.containsKey(i)) {
				for (int j = partition.getBottom(); j < partition.getTop(); j++)
					if (index.get(i).containsKey(j)) {
						IndexCell cell = index.get(i).remove(j);
						if (textAware)
							removeTextSummeryFromIndexCell(cell);
					}
				index.remove(i);
			}
	}

	public void addIndexCellsFromPartition(ArrayList<IndexCell> indexCells, boolean textAware) {
		for (IndexCell indexCell : indexCells) {
			if (!index.containsKey(indexCell.globalCoordinates.getX()))
				index.put(indexCell.globalCoordinates.getX(), new HashMap<Integer, IndexCell>());
			index.get(indexCell.globalCoordinates.getX()).put(indexCell.globalCoordinates.getY(), indexCell);
			if (textAware)
				addTextSummeryFromIndexCell(indexCell);
		}
	}

	public void addIndexCellsFromPartition(IndexCell indexCell, boolean textAware) {

		if (!index.containsKey(indexCell.globalCoordinates.getX()))
			index.put(indexCell.globalCoordinates.getX(), new HashMap<Integer, IndexCell>());
		index.get(indexCell.globalCoordinates.getX()).put(indexCell.globalCoordinates.getY(), indexCell);
		if (textAware)
			addTextSummeryFromIndexCell(indexCell);

	}

	@Override
	public HashSet<String> getUpdatedTextSummery() {
		if (textUpdated) {
			textUpdated = false;
			HashSet<String> s = new HashSet<String>();
			 s .addAll(overallQueryTextSummery.keySet());

			 return s;
		}
		return null;
	}

	public void removeTextSummeryFromIndexCell(IndexCell cell) {
		for (Entry<String, HashMap<String,ArrayList<Query>>> e : cell.queriesInvertedList.entrySet()) {
			Iterator<Entry<String, ArrayList<Query>>> itr = e.getValue().entrySet().iterator();
			while (itr.hasNext()) {
				Entry<String, ArrayList<Query>> entry = itr.next();
				if (overallQueryTextSummery.containsKey(entry.getKey())) {
					int remainingCount = overallQueryTextSummery.get(entry.getKey()) - entry.getValue().size();
					if (remainingCount > 0)
						overallQueryTextSummery.put(entry.getKey(), remainingCount);
					else  if (remainingCount<= 0){
						overallQueryTextSummery.remove(entry.getKey());
						textUpdated = true;
					}

				}
			}
		}

	}

	public void addTextSummeryFromIndexCell(IndexCell cell) {
		if (cell.queriesInvertedList == null)
			return;
		for (Entry<String, HashMap<String, ArrayList<Query>>> e : cell.queriesInvertedList.entrySet()) {
			Iterator<Entry<String, ArrayList<Query>>> itr = e.getValue().entrySet().iterator();
			while (itr.hasNext()) {
				Entry<String, ArrayList<Query>> entry = itr.next();
				if (!overallQueryTextSummery.containsKey(entry.getKey())) {
					if( entry.getValue().size()!=0)
					overallQueryTextSummery.put(entry.getKey(), entry.getValue().size());
				} else {
					if(( overallQueryTextSummery.get(entry.getKey()) + entry.getValue().size())!=0)
					overallQueryTextSummery.put(entry.getKey(), overallQueryTextSummery.get(entry.getKey()) + entry.getValue().size());

				}

			}
		}

	}

	@Override
	public void cleanUp() {
		beginCleanUpTime = (new Date()).getTime();
		Boolean hasQueries= false;
		for (int i = myPartition.getLeft(); i < myPartition.getRight(); i++)
			if (index.containsKey(i)) {
				for (int j = myPartition.getBottom(); j < myPartition.getTop(); j++)
					if (index.get(i).containsKey(j)) {
						IndexCell cell = index.get(i).get(j);
						ArrayList<Query> expiredQueries = cell.findandRemoveExpriedQueries();
						if(cell.storedQueries.size()!=0)
							hasQueries=true;
						if (expiredQueries != null) {
							for (Query query : expiredQueries) {
								for (String s : query.getQueryText()) {
									if (overallQueryTextSummery.containsKey(s)) {
										int remainingCount = overallQueryTextSummery.get(s) - 1;
										if (remainingCount > 0)
											overallQueryTextSummery.put(s, remainingCount);
										else if (remainingCount <= 0){
											overallQueryTextSummery.remove(s);
											textUpdated = true;
										}

									}

								}
							}
						}
					}

			}
//		if(hasQueries==false &&!overallQueryTextSummery.isEmpty())
//			System.err.println("There is an error in cleaning");
		
	}

}
