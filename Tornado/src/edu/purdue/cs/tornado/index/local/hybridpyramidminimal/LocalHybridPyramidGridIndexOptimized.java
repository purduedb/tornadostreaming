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
 * 
 * 
 * This version is meant for the fair comparison of with the state of the art
 *  index that does not have any other cluster and tornado related attributes
 */
package edu.purdue.cs.tornado.index.local.hybridpyramidminimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.MinimalRangeQuery;

public class LocalHybridPyramidGridIndexOptimized {
	public double localXstep;
	public double localYstep;
	public HashMap<Integer, IndexCellOptimized>[] index;
	public Rectangle selfBounds;
	public int gridGranularity;
	public int maxLevel;
	
	public HashMap<String, Integer> overallQueryTextSummery;
	public static int totalVisited = 0;
	public static int spatialOverlappingQuries = 0;
	public int minInsertedLevel;
	public int maxInsertedLevel;

	public LocalHybridPyramidGridIndexOptimized(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer xGridGranularity, Integer maxLevel) {
		super();
		this.selfBounds = selfBounds;
		Double globalXrange = SpatioTextualConstants.xMaxRange;
		Double globalYrange = SpatioTextualConstants.yMaxRange;
		this.gridGranularity = xGridGranularity;
		this.localXstep = (globalXrange / this.gridGranularity);
		this.localYstep = (globalYrange / this.gridGranularity);
		this.maxLevel = Math.min((int) (Math.log(gridGranularity) / Math.log(2)), maxLevel);
		this.index = new HashMap[maxLevel + 1];
		this.minInsertedLevel = -1;
		this.maxInsertedLevel = -1;
		for (int i = 0; i <= maxLevel; i++)
			index[i] = new HashMap<Integer, IndexCellOptimized>();
		overallQueryTextSummery = new HashMap<String, Integer>();
	}

	public Integer mapToRawMajor(int x, int y) {
		return y * gridGranularity + x;
	}

	public Integer mapToRawMajor(int x, int y, int gridGranularity) {
		return y * gridGranularity + x;
	}

	public Boolean addContinousQuery(MinimalRangeQuery query) {
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
		}

		HashMap<Integer, IndexCellOptimized> levelIndex = index[level];
		Integer coodinate = 0;
		for (Integer i = levelxMinCell; i <= levelxMaxCell; i++) {
			for (Integer j = levelyMinCell; j <= levelyMaxCell; j++) {
				coodinate = mapToRawMajor(i, j, levelGranuality);
				if (!levelIndex.containsKey(coodinate))
					levelIndex.put(coodinate, new IndexCellOptimized(getBoundForIndexCell(coodinate, levelGranuality, levelStep), coodinate));
				if (query.getTextualPredicate().equals(TextualPredicate.OVERlAPS))
					levelIndex.get(coodinate).addInternalQuery(query);
				else if (query.getTextualPredicate().equals(TextualPredicate.CONTAINS)) {
					levelIndex.get(coodinate).addInternalQuery(minkeyword, query);

				}
			}

		}

		return completed;
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

	public Rectangle getSelfBounds() {
		return selfBounds;
	}

	public Integer mapDataPointToPartition(Point point, double step, int granularity) {
		Double x = point.getX();
		Double y = point.getY();
		Integer xCell = (int) ((x) / step);
		Integer yCell = (int) ((y) / step);
		Integer indexCellCoordinate = mapToRawMajor(xCell, yCell, granularity);
		return indexCellCoordinate;
	}


	public void setSelfBounds(Rectangle selfBounds) {
		this.selfBounds = selfBounds;
	}

	public ArrayList<List<MinimalRangeQuery>> getReleventSpatialKeywordRangeQueries(DataObject dataObject, Boolean fromNeighbour) {

		double step = localXstep;
		ArrayList<List<MinimalRangeQuery>> result = new ArrayList<List<MinimalRangeQuery>>();
		result.add(new ArrayList<MinimalRangeQuery>());
		int granualrity = this.gridGranularity;
		if (minInsertedLevel == -1)
			return result;
		for (int level = 0; level <= maxLevel; level++) {
			if (level >= minInsertedLevel && level <= maxInsertedLevel) {
				HashMap<Integer, IndexCellOptimized> levelIndex = index[level];
				Integer cellCoordinates = mapDataPointToPartition(dataObject.getLocation(), step, granualrity);
				IndexCellOptimized indexCellOptimized = levelIndex.get(cellCoordinates);
				if (indexCellOptimized != null) {
					ArrayList<List<MinimalRangeQuery>> results2 = indexCellOptimized.getInternalSpatiotTextualOverlappingQueries(dataObject.getLocation(), dataObject.getObjectText());
					result.get(0).addAll(results2.get(0));
					
				}
			}
			step *= 2;
			granualrity /= 2;
		}

		return result;
	}

}
