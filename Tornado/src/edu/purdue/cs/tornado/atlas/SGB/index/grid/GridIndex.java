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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import edu.purdue.cs.tornado.atlas.SGB.global.Tuple;
import edu.purdue.cs.tornado.atlas.SGB.index.SpatialIndex;
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
import gnu.trove.TIntProcedure;

public class GridIndex implements SpatialIndex {
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

	public GridIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer fineGridGran) {
		this(selfBounds, dataSourcesInformation, fineGridGran, fineGridGran, false, 0);
	}

	public GridIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer xGridGranularity, Integer yGridGranularity) {
		this(selfBounds, dataSourcesInformation, xGridGranularity, yGridGranularity, false, 0);
	}

	public GridIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer xGridGranularity, Integer yGridGranularity, Boolean spatialOnlyFlag, Integer level) {
		super();
		initindex(selfBounds, dataSourcesInformation, xGridGranularity, yGridGranularity, spatialOnlyFlag, level);
	}

	public GridIndex() {
		init(null);
	}
	public GridIndex(double cellRange) {
		int cellCount = (int)(SpatioTextualConstants.xMaxRange/cellRange);
		//if(cellCount<64)
			cellCount = 64;
//		if(cellCount>1000)
//			cellCount =1000;
		initindex(new Rectangle(new Point(0, 0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.xMaxRange)), null, cellCount, cellCount, true, 0);
	}

	public void initindex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer xGridGranularity, Integer yGridGranularity, Boolean spatialOnlyFlag, Integer level) {
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
			if (!indexCell.transmitted) {
				indexCell.addQuery(query);
				for (String s : query.getQueryText()) {
					if (!overallQueryTextSummery.containsKey(s))
						overallQueryTextSummery.put(s, 1);
					else
						overallQueryTextSummery.put(s, overallQueryTextSummery.get(s) + 1);
				}
			} else
				completed = false;
		}

		return completed;
	}

	public Boolean addSpatialRange(Query query) {
		Boolean completed = true;
		ArrayList<IndexCellCoordinates> indexCells = mapRecToIndexCells(query.getSpatialRange());
		for (IndexCellCoordinates indexCellCoordinate : indexCells) {

			IndexCell indexCell = getIndexCellFromCoordinates(indexCellCoordinate);//index.get(cellCoordinates.getX()).get(cellCoordinates.getY());
			if (indexCell == null) {
				indexCell = new IndexCell(getBoundForIndexCell(indexCellCoordinate), spatialOnlyFlag, level, indexCellCoordinate);
				addIndexCellFromCoordinates(indexCellCoordinate, indexCell);
			}
			indexCell.addQuery(query);
		}

		return completed;
	}

	public void addDataObject(DataObject dataObject) {
		IndexCellCoordinates cellCoordinates = mapDataPointToPartition(dataObject.getLocation());
		IndexCell indexCell = getIndexCellFromCoordinates(cellCoordinates);//index.get(cellCoordinates.getX()).get(cellCoordinates.getY());
		if (indexCell == null) {
			indexCell = new IndexCell(getBoundForIndexCell(cellCoordinates), spatialOnlyFlag, level, cellCoordinates);

			addIndexCellFromCoordinates(cellCoordinates, indexCell);

		}
		indexCell.addDataObject(dataObject);
		allDataCount++;

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

	public Boolean dropSpatialRange(Query query) {
		ArrayList<IndexCellCoordinates> indexCells = mapRecToIndexCells(query.getSpatialRange());
		for (IndexCellCoordinates indexCell : indexCells) {
			if (indexCell == null)
				continue;
			index.get(indexCell.getX()).get(indexCell.getY()).dropQuery(query);

		}
		return true;
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
				else if (remainingCount <= 0) {
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

	public ArrayList<IndexCell> getOverlappingIndexCellWithData(ArrayList<String> keywords) {
		return getOverlappingIndexCellWithData(selfBounds, keywords);
	}

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

		int xMinCell = (int) (rectangle.getMin().getX() / localXstep);
		int yMinCell = (int) (rectangle.getMin().getY() / localYstep);
		int xMaxCell = (int) (rectangle.getMax().getX() / localXstep);
		int yMaxCell = (int) (rectangle.getMax().getY() / localYstep);

		for (Integer xCell = xMinCell; xCell <= xMaxCell; xCell++)
			for (Integer yCell = yMinCell; yCell <= yMaxCell; yCell++) {
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

	public HashSet<String> getUpdatedTextSummery() {
		if (textUpdated) {
			textUpdated = false;
			HashSet<String> s = new HashSet<String>();
			s.addAll(overallQueryTextSummery.keySet());

			return s;
		}
		return null;
	}

	public void removeTextSummeryFromIndexCell(IndexCell cell) {
		for (Entry<String, HashMap<String, ArrayList<Query>>> e : cell.queriesInvertedList.entrySet()) {
			Iterator<Entry<String, ArrayList<Query>>> itr = e.getValue().entrySet().iterator();
			while (itr.hasNext()) {
				Entry<String, ArrayList<Query>> entry = itr.next();
				if (overallQueryTextSummery.containsKey(entry.getKey())) {
					int remainingCount = overallQueryTextSummery.get(entry.getKey()) - entry.getValue().size();
					if (remainingCount > 0)
						overallQueryTextSummery.put(entry.getKey(), remainingCount);
					else if (remainingCount <= 0) {
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
					if (entry.getValue().size() != 0)
						overallQueryTextSummery.put(entry.getKey(), entry.getValue().size());
				} else {
					if ((overallQueryTextSummery.get(entry.getKey()) + entry.getValue().size()) != 0)
						overallQueryTextSummery.put(entry.getKey(), overallQueryTextSummery.get(entry.getKey()) + entry.getValue().size());

				}

			}
		}

	}

	@Override
	public void init(Properties props) {
		//initindex(new Rectangle(new Point(0, 0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.xMaxRange)), null, 1000, 1000, true, 0);

	}

	@Override
	public void add(edu.purdue.cs.tornado.atlas.SGB.index.Rectangle r, int id) {
		Query q = new Query();
		q.setQueryId(id);
		q.setSpatialRange(new Rectangle(new Point(r.minX, r.minY), new Point(r.maxX, r.maxY)));
		this.addSpatialRange(q);

	}

	@Override
	public boolean delete(edu.purdue.cs.tornado.atlas.SGB.index.Rectangle r, int id) {
		Query q = new Query();
		q.setQueryId(id);
		q.setSpatialRange(new Rectangle(new Point(r.minX, r.minY), new Point(r.maxX, r.maxY)));
		dropSpatialRange(q);
		return true;
	}

	@Override
	public void nearest(edu.purdue.cs.tornado.atlas.SGB.index.Point p, TIntProcedure v, float furthestDistance) {
		System.out.println("Not implemented now");

	}

	@Override
	public void nearestN(edu.purdue.cs.tornado.atlas.SGB.index.Point p, TIntProcedure v, int n, float distance) {
		System.out.println(" nearestN Not implemented now");

	}

	@Override
	public void nearestNUnsorted(edu.purdue.cs.tornado.atlas.SGB.index.Point p, TIntProcedure v, int n, float distance) {
		System.out.println(" nearestNUnsorted Not implemented now");

	}

	@Override
	public void intersects(edu.purdue.cs.tornado.atlas.SGB.index.Rectangle r, TIntProcedure ip) {
		System.out.println(" intersects Not implemented now");

	}

	@Override
	public void contains(edu.purdue.cs.tornado.atlas.SGB.index.Rectangle r, TIntProcedure ip) {
		System.out.println(" contains Not implemented now");

	}

	@Override
	public ArrayList<Integer> containPoint(edu.purdue.cs.tornado.atlas.SGB.index.Rectangle r, TIntProcedure v) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		HashMap<Integer, Query> queries = null;
		Point p = new Point(r.minX, r.minY);
		IndexCell indexCell = getOverlappingIndexCells(p);
		if (indexCell != null && indexCell.storedQueries != null) {
			queries = indexCell.storedQueries;
			for (Query q : queries.values()) {
				if (overlapsSpatially(p, q.getSpatialRange()))
					list.add(q.getQueryId());

				//					if (!v.execute(q.getQueryId())) {
				//						return;
				//					}
			}
		}
		return list;
	}

	public static Boolean overlapsSpatially(Point point, Rectangle rectangle) {
		if (point.getX() >= rectangle.getMin().getX() && point.getX() <= rectangle.getMax().getX() && point.getY() >= rectangle.getMin().getY() && point.getY() <= rectangle.getMax().getY())
			return true;
		return false;
	}

	@Override
	public int size() {
		System.out.println("size Not implemented now");
		return 0;
	}

	@Override
	public edu.purdue.cs.tornado.atlas.SGB.index.Rectangle getBounds() {
		System.out.println("getBounds Not implemented now");
		return null;
	}

	@Override
	public String getVersion() {
		System.out.println("getVersion Not implemented now");
		return null;
	}

	@Override
	public boolean update(edu.purdue.cs.tornado.atlas.SGB.index.Rectangle oldRect, edu.purdue.cs.tornado.atlas.SGB.index.Rectangle newRect, int id) {

		ArrayList<IndexCellCoordinates> partitions = new ArrayList<IndexCellCoordinates>();

		int xoldMinCell = (int) (oldRect.minX / localXstep);
		int yoldMinCell = (int) (oldRect.minY / localYstep);
		int xoldMaxCell = (int) (oldRect.maxX / localXstep);
		int yoldMaxCell = (int) (oldRect.maxY / localYstep);

		int xnewMinCell = (int) (newRect.minX / localXstep);
		int ynewMinCell = (int) (newRect.minY / localYstep);
		int xnewMaxCell = (int) (newRect.maxX / localXstep);
		int ynewMaxCell = (int) (newRect.maxY / localYstep);
		if (xoldMinCell == xnewMinCell && yoldMinCell == ynewMinCell && xoldMaxCell == xnewMaxCell && yoldMaxCell == ynewMaxCell)
			return true;

		Query q = new Query();
		q.setQueryId(id);
		q.setSpatialRange(new Rectangle(new Point(oldRect.minX, oldRect.minY), new Point(oldRect.maxX, oldRect.maxY)));
		if (xoldMinCell <= xnewMinCell && xoldMaxCell >= xnewMaxCell && yoldMinCell <= ynewMinCell && yoldMaxCell >= ynewMaxCell) {
			//shrink

			for (int j = yoldMinCell; j <= yoldMaxCell; j++) {
				for (int i = xoldMinCell; i < xnewMinCell; i++) {
					IndexCell cell = getIndexCellFromCoordinates(new IndexCellCoordinates(i, j));
					//if (cell != null)
						cell.dropQuery(q);
				}
				for (int i = xnewMaxCell + 1; i <= xoldMaxCell; i++) {
					IndexCell cell = getIndexCellFromCoordinates(new IndexCellCoordinates(i, j));
					//if (cell != null)
						cell.dropQuery(q);
				}
			}
			for (int i = xnewMinCell; i <= xnewMaxCell; i++) {
				for (int j = yoldMinCell; j < ynewMinCell; j++) {
					IndexCell cell = getIndexCellFromCoordinates(new IndexCellCoordinates(i, j));
					//if (cell != null)
						cell.dropQuery(q);
				}
				for (int j = ynewMaxCell + 1; j <= yoldMaxCell; j++) {
					IndexCell cell = getIndexCellFromCoordinates(new IndexCellCoordinates(i, j));
					//if (cell != null)
						cell.dropQuery(q);
				}
			}
		} else if (xoldMinCell >= xnewMinCell && xoldMaxCell <= xnewMaxCell && yoldMinCell >= ynewMinCell && yoldMaxCell <= ynewMaxCell) {

			for (int j = ynewMinCell; j <= ynewMaxCell; j++) {
				for (int i = xnewMinCell; i < xoldMinCell; i++) {
					IndexCell cell = getIndexCellCreateIfNull(new IndexCellCoordinates(i, j),false);
					if (cell != null)
						cell.addQuery(q);
				}
				for (int i = xoldMaxCell + 1; i <= xnewMaxCell; i++) {
					IndexCell cell = getIndexCellCreateIfNull(new IndexCellCoordinates(i, j),false);
					if (cell != null)
						cell.addQuery(q);
				}
			}
			for (int i = xoldMinCell; i <= xoldMaxCell; i++) {
				for (int j = ynewMinCell; j < yoldMinCell; j++) {
					IndexCell cell = getIndexCellCreateIfNull(new IndexCellCoordinates(i, j),false);
					if (cell != null)
						cell.addQuery(q);
				}
				for (int j = yoldMaxCell + 1; j <= ynewMaxCell; j++) {
					IndexCell cell = getIndexCellCreateIfNull(new IndexCellCoordinates(i, j),false);
					if (cell != null)
						cell.addQuery(q);
				}
			}
		} else {
			delete(oldRect, id);
			add(newRect, id);
		}
		return true;
	}

	@Override
	public ArrayList<DataObject> getDataObjects(edu.purdue.cs.tornado.atlas.SGB.index.Rectangle r) {
		Rectangle range = new Rectangle(new Point(r.minX, r.minY), new Point(r.maxX, r.maxY));
		ArrayList<IndexCell> cells = getOverlappingIndexCellWithData(range);

		ArrayList<DataObject> result = new ArrayList<DataObject>();
		for (IndexCell cell : cells) {
			result.addAll(cell.getSpatialOverlappingDataObjects(range));
		}
		return result;
	}

	@Override
	public ArrayList<Tuple> getSpatialOverlapTuples(edu.purdue.cs.tornado.atlas.SGB.index.Rectangle r) {
		Rectangle range = new Rectangle(new Point(r.minX, r.minY), new Point(r.maxX, r.maxY));
		ArrayList<IndexCell> cells = getOverlappingIndexCellWithData(range);

		ArrayList<Tuple> result = new ArrayList<Tuple>();
		for (IndexCell cell : cells) {
			result.addAll(cell.getSpatialOverlappingTuples(range));
		}
		return result;
	}

	@Override
	public void addTuple(Tuple tuple) {
		IndexCellCoordinates cellCoordinates = mapDataPointToPartition(((DataObject) tuple.getFiled(3)).getLocation());
		IndexCell indexCell = getIndexCellFromCoordinates(cellCoordinates);//index.get(cellCoordinates.getX()).get(cellCoordinates.getY());
		if (indexCell == null) {
			indexCell = new IndexCell(getBoundForIndexCell(cellCoordinates), spatialOnlyFlag, level, cellCoordinates);
			addIndexCellFromCoordinates(cellCoordinates, indexCell);
		}
		indexCell.addTuple(tuple);
		allDataCount++;

	}
}
