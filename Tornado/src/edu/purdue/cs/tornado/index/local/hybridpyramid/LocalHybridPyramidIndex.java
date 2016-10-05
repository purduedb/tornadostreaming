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
package edu.purdue.cs.tornado.index.local.hybridpyramid;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import clojure.string__init;
import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.index.local.LocalHybridIndex;
import edu.purdue.cs.tornado.index.local.hybridgrid.LocalKNNGridIndexIterator;
import edu.purdue.cs.tornado.index.local.hybridpyramidminimal.IndexCellOptimized;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.MinimalRangeQuery;
import edu.purdue.cs.tornado.messages.Query;

public class LocalHybridPyramidIndex extends LocalHybridIndex {
	public double localXstep;
	public double localYstep;

	public HashMap<Integer, PyramidIndexCell>[] index;
	public Rectangle selfBounds;

	public int gridGranularity;
	public int maxLevel;
	public HashMap<String, Integer> overallQueryTextSummery;
	public DataSourceInformation dataSourcesInformation;
	public Cell myPartition;
	public Integer fineGridGran;
	public int minInsertedLevel;
	public int maxInsertedLevel;
	
	public Rectangle partitionToSend;

	public boolean textUpdated;

	public LocalHybridPyramidIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer fineGridGran) {
		this(selfBounds, dataSourcesInformation, fineGridGran, fineGridGran, false, null);
	}

	public LocalHybridPyramidIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer xGridGranularity, Integer yGridGranularity) {
		this(selfBounds, dataSourcesInformation, xGridGranularity, yGridGranularity, false, null);
	}

	public LocalHybridPyramidIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer xGridGranularity, Integer yGridGranularity, Boolean spatialOnlyFlag, Integer mLevel) {
		super();
		this.dataSourcesInformation = dataSourcesInformation;
		this.selfBounds = selfBounds;
		Double globalXrange = SpatioTextualConstants.xMaxRange;
		Double globalYrange = SpatioTextualConstants.yMaxRange;
		this.gridGranularity = xGridGranularity;
		this.localXstep = (globalXrange / this.gridGranularity);
		this.localYstep = (globalYrange / this.gridGranularity);
		if (mLevel != null) {
			this.maxLevel = Math.min((int) (Math.log(gridGranularity) / Math.log(2)), mLevel);
		} else {
			this.maxLevel = (int) (Math.log(gridGranularity) / Math.log(2));
		}
		this.index = new HashMap[maxLevel + 1];
		this.minInsertedLevel = -1;
		this.maxInsertedLevel = -1;
		for (int i = 0; i <= maxLevel; i++)
			index[i] = new HashMap<Integer, PyramidIndexCell>();
		overallQueryTextSummery = new HashMap<String, Integer>();
		this.partitionToSend = null;

		this.myPartition = new Cell((int) (selfBounds.getMin().getY() / localYstep), (int) (selfBounds.getMax().getY() / localYstep), (int) (selfBounds.getMin().getX() / localXstep), (int) (selfBounds.getMax().getX() / localXstep));
	}

	public Integer mapToRawMajor(int x, int y) {
		return y * gridGranularity + x;
	}

	public Integer mapToRawMajor(int x, int y, int gridGranularity) {
		return y * gridGranularity + x;
	}

	public Boolean addContinousQuery(Query query) {
		Boolean completed = true;
		int xMinCell = (int) (query.getSpatialRange().getMin().getX() / localXstep);
		int yMinCell = (int) (query.getSpatialRange().getMin().getY() / localYstep);
		int xMaxCell = (int) (query.getSpatialRange().getMax().getX() / localXstep);
		int yMaxCell = (int) (query.getSpatialRange().getMax().getY() / localYstep);

		int maxCellSpan = Math.max(xMaxCell - xMinCell, yMaxCell - yMinCell);

		int level = maxCellSpan == 0 ? 0 : (int) (Math.log(maxCellSpan) / Math.log(2));
		level = Math.min(level, maxLevel);

		int levelGranuality = (int) (gridGranularity / Math.pow(2, level));
		double levelStep = (SpatioTextualConstants.xMaxRange / levelGranuality);

		int levelxMinCell = (int) (query.getSpatialRange().getMin().getX() / levelStep);
		int levelyMinCell = (int) (query.getSpatialRange().getMin().getY() / levelStep);
		int levelxMaxCell = (int) (query.getSpatialRange().getMax().getX() / levelStep);
		int levelyMaxCell = (int) (query.getSpatialRange().getMax().getY() / levelStep);

		if ((levelxMaxCell - levelxMinCell >= 1 || levelyMaxCell - levelyMinCell >= 1) && level < maxLevel) {
			level++;
			levelGranuality = (int) (gridGranularity / Math.pow(2, level));
			levelStep = (SpatioTextualConstants.xMaxRange / levelGranuality);

			levelxMinCell = (int) (query.getSpatialRange().getMin().getX() / levelStep);
			levelyMinCell = (int) (query.getSpatialRange().getMin().getY() / levelStep);
			levelxMaxCell = (int) (query.getSpatialRange().getMax().getX() / levelStep);
			levelyMaxCell = (int) (query.getSpatialRange().getMax().getY() / levelStep);
		}
		if (minInsertedLevel == -1)
			minInsertedLevel = maxInsertedLevel = level;
		if (level < minInsertedLevel)
			minInsertedLevel = level;
		if (level > maxInsertedLevel)
			maxInsertedLevel = level;
		String minkeyword = null;
		int minCount = Integer.MAX_VALUE;
		ArrayList<String> minKeywords = new ArrayList<String>();
		if (query.getTextualPredicate().equals(TextualPredicate.OVERlAPS)) {
			for (String keyword : query.getQueryText()) {
				Integer count = overallQueryTextSummery.get(keyword);
				if (count == null)
					count = 1;
				else
					count++;

			}
		} else if (query.getTextualPredicate().equals(TextualPredicate.CONTAINS)) {
			for (String keyword : query.getQueryText()) {

				Integer count = overallQueryTextSummery.get(keyword);
				if (count == null) {
					count = 0;
					overallQueryTextSummery.put(keyword, count);
					minkeyword = keyword;
					minCount = 0;
					break;

				} else if (count < minCount) {
					minCount = count;
					minkeyword = keyword;
				}

			}
			minCount++;
			overallQueryTextSummery.put(minkeyword, minCount);
		} else if (query.getTextualPredicate().equals(TextualPredicate.BOOLEAN_EXPR)) {
			for (ArrayList<String> keywords : query.getComplexQueryText()) {
				minCount = Integer.MAX_VALUE;
				for (String keyword : keywords) {
					Integer count = overallQueryTextSummery.get(keyword);
					if (count == null) {
						count = 0;
						overallQueryTextSummery.put(keyword, count);
						minkeyword = keyword;
						minCount = 0;
						break;

					} else if (count < minCount) {
						minCount = count;
						minkeyword = keyword;
					}

				}
				minCount++;
				overallQueryTextSummery.put(minkeyword, minCount);
				minKeywords.add(minkeyword);
			}
		}

		HashMap<Integer, PyramidIndexCell> levelIndex = index[level];
		Integer coodinate = 0;
		for (Integer i = levelxMinCell; i <= levelxMaxCell; i++) {
			for (Integer j = levelyMinCell; j <= levelyMaxCell; j++) {
				coodinate = mapToRawMajor(i, j, levelGranuality);
				if (!levelIndex.containsKey(coodinate))
					levelIndex.put(coodinate, new PyramidIndexCell(getBoundForIndexCell(coodinate, levelGranuality, levelStep), coodinate, level, levelGranuality));
				if (query.getTextualPredicate().equals(TextualPredicate.OVERlAPS)){
					levelIndex.get(coodinate).addQuery(query);
				}
				else if (query.getTextualPredicate().equals(TextualPredicate.CONTAINS)) {
					levelIndex.get(coodinate).addQuery(minkeyword, query);
				} else if (query.getTextualPredicate().equals(TextualPredicate.BOOLEAN_EXPR)) {
					for (int l = 0; l < minKeywords.size(); l++) {
						Query subQ = new Query(query);
						subQ.setQueryText(query.getComplexQueryText().get(l));
						subQ.added = query.added; //ensures that all subqueries share the same flag, to prevent duplicates 

						subQ.setComplexQueryText(null);
						subQ.setTextualPredicate(TextualPredicate.CONTAINS);
						levelIndex.get(coodinate).addQuery(minKeywords.get(l), subQ);

					}
				}
			}

		}

		return completed;

	}

	boolean checkCompleted(Query query, Rectangle toSendRange,PyramidIndexCell indexCell){
		if(toSendRange!=null){
			if(SpatialHelper.overlapsSpatially(query.getSpatialRange(), toSendRange)))
		}
	}
	public IndexCell addDataObject(DataObject dataObject) {
		return null;
	}

	public Boolean dropContinousQuery(Query query) {
		return true;
	}

	Rectangle getBoundForIndexCell(int indexCellCoordinates, int gridGranulaity, double step) {
		Integer i = indexCellCoordinates % gridGranulaity;
		Integer j = indexCellCoordinates / gridGranulaity;
		Rectangle bounds = new Rectangle(new Point(i * step, j * step), new Point((i + 1) * step, (j + 1) * step));
		return bounds;
	}

	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords) {
		return 0;
	}

	@Override
	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords, Rectangle rect) {
		Integer sum = 0;
		return sum;
	}

	@Override
	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords, Rectangle rect) {
		Integer sum = 0;

		return sum;
	}

	public Integer getCountPerRec(Rectangle rec) {
		Integer sum = 0;
		return sum;

	}


	public IndexCell getIndexCellFromCoordinates(IndexCellCoordinates indexCell) {
		return null;
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
		//TODO 
		return null;

	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(ArrayList<String> keywords) {
		return getOverlappingIndexCellWithData(selfBounds, keywords);
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle) {
		return null;
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle, ArrayList<String> keywords) {
		return null;
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
		return null;
	}

	public LocalKNNGridIndexIterator LocalKNNIterator(Point focalPoint) {
		return null;
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

	/**
	 * This function maps a query to a set of index cells that overlap the
	 * query's range check if the query does not overlap with the bolts
	 * rectangular bounds maps the query to the bounds of the current bolt if it
	 * exceeds it
	 * 
	 * @param query
	 * @return
	 */

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
		return true;
	}

	public Integer mapDataPointToPartition(Point point, double step, int granularity) {
		Double x = point.getX();
		Double y = point.getY();
		Integer xCell = (int) ((x) / step);
		Integer yCell = (int) ((y) / step);
		Integer indexCellCoordinate = mapToRawMajor(xCell, yCell, granularity);
		return indexCellCoordinate;
	}

	@Override
	public ArrayList<List<Query>> getReleventSpatialKeywordRangeQueries(DataObject dataObject, Boolean fromNeighbour) {

		double step = localXstep;
		ArrayList<List<Query>> result = new ArrayList<List<Query>>();
		result.add(new ArrayList<Query>());
		int granualrity = this.gridGranularity;
		if (minInsertedLevel == -1)
			return result;
		for (int level = 0; level <= maxLevel; level++) {
			if (level >= minInsertedLevel && level <= maxInsertedLevel) {
				HashMap<Integer, PyramidIndexCell> levelIndex = index[level];
				Integer cellCoordinates = mapDataPointToPartition(dataObject.getLocation(), step, granualrity);
				PyramidIndexCell indexCellOptimized = levelIndex.get(cellCoordinates);
				if (indexCellOptimized != null) {
					ArrayList<List<Query>> results2 = indexCellOptimized.geSpatiotTextualOverlappingQueries(dataObject.getLocation(), dataObject.getObjectText());
					result.get(0).addAll(results2.get(0));
				}
			}
			step *= 2;
			granualrity /= 2;
		}

		return result;
	}

	public ArrayList<IndexCell> getIndexCellsFromPartition(Cell partition) {
		return null;
	}

	public void removeIndexCellsFromPartition(Cell partition, boolean textAware) {

	}

	public void addIndexCellsFromPartition(ArrayList<IndexCell> indexCells, boolean textAware) {

	}

	public void addIndexCellsFromPartition(IndexCell indexCell, boolean textAware) {

	}

	@Override
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
		for (Entry<String, HashMap<String, ArrayList<Query>>> e : cell.getQueriesInvertedList().entrySet()) {
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
		if (cell.getQueriesInvertedList() == null)
			return;
		for (Entry<String, HashMap<String, ArrayList<Query>>> e : cell.getQueriesInvertedList().entrySet()) {
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
	public void cleanUp() {
		//TODO

	}

	@Override
	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IndexCellCoordinates mapDataPointToPartition(Point point) {
		// TODO Auto-generated method stub
		return null;
	}

}
