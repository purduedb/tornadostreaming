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
import java.util.Map;
import java.util.Map.Entry;

import edu.purdue.cs.tornado.atlas.SGB.global.Tuple;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class IndexCell {
	public HashMap<Integer, DataObject> storedObjects;
	public HashMap<Integer, Tuple> storedTuples;
	public HashMap<String, Integer> allDataTextInCell; //keyword, count of distinct objects
	public HashMap<String, Integer> allQueryTextInCell; //keyword, count of distinct queries
	public HashMap<Integer, Query> storedQueries;
	public ArrayList<Query> outerEvaluatorQueries;
	public HashMap<String, HashMap<String,ArrayList<Query>>> queriesInvertedList;//src,keyword,Query

	public Rectangle bounds;

	public Integer dataObjectCount;
	public Integer queryCount;
	//this is for testing spatial objects only
	public Boolean spatialOnlyFlag;

	public IndexCellType indexCellType;
	//used by multilevel index
	public Rectangle boundsNoBoundaries;

	public Integer level;
	public IndexCellCoordinates globalCoordinates;
	//used by pyramid index
	public IndexCell[] children;

	public IndexCell parent;
	public Integer numberOfChildren;
	public Integer coverQueryCount;

	public Integer indexCellCost;
	public boolean transmitted;
	public Long minExpireTime;
	public enum IndexCellType {
		Grid, Multi_Level_Grid, Pyramid
	}

	public IndexCell() {
		storedTuples = null;
		storedObjects = null;
		allDataTextInCell = null;
		allQueryTextInCell = null;
		storedQueries = null;
		outerEvaluatorQueries = null;
		queriesInvertedList = null;
		this.bounds = null;
		this.dataObjectCount = 0;
		this.queryCount = 0;
		this.boundsNoBoundaries = null;
		this.spatialOnlyFlag = null;
		this.level = null;
		children = null;
		parent = null;
		numberOfChildren = 0;
		this.coverQueryCount = 0;
		indexCellCost = 0;
		transmitted = false;
		minExpireTime = Long.MAX_VALUE;
	}

	public IndexCell(Rectangle bounds, Boolean spatialOnlyFlag, Integer level) {
		storedTuples = null;
		storedObjects = null;
		allDataTextInCell = null;
		allQueryTextInCell = null;
		storedQueries = null;
		outerEvaluatorQueries = null;
		queriesInvertedList = null;
		this.bounds = bounds;
		this.dataObjectCount = 0;
		this.queryCount = 0;
		indexCellCost = 0;
		this.boundsNoBoundaries = new Rectangle(new Point(bounds.getMin().getX() + .0001, bounds.getMin().getY() + .0001), new Point(bounds.getMax().getX() - .0001, bounds.getMax().getY() - .0001));
		this.spatialOnlyFlag = spatialOnlyFlag;
		this.level = level;
		//used by multilevel index
		children = null;
		parent = null;
		numberOfChildren = 0;
		indexCellCost = 0;
		this.transmitted = false;
		this.coverQueryCount = 0;
		this.minExpireTime = Long.MAX_VALUE;

	}

	public IndexCell(Rectangle bounds, Boolean spatialOnlyFlag, Integer level, IndexCellCoordinates globalCoordinates) {
		storedTuples = null;
		storedObjects = null;
		allDataTextInCell = null;
		allQueryTextInCell = null;
		storedQueries = null;
		outerEvaluatorQueries = null;
		queriesInvertedList = null;
		this.bounds = bounds;
		this.dataObjectCount = 0;
		this.queryCount = 0;
		this.boundsNoBoundaries = new Rectangle(new Point(bounds.getMin().getX() + .0001, bounds.getMin().getY() + .0001), new Point(bounds.getMax().getX() - .0001, bounds.getMax().getY() - .0001));
		this.spatialOnlyFlag = spatialOnlyFlag;
		this.level = level;
		//used by multilevel index
		this.transmitted = false;
		children = null;
		parent = null;
		numberOfChildren = 0;
		this.coverQueryCount = 0;
		this.globalCoordinates = globalCoordinates;
		indexCellCost = 0;
		this.minExpireTime = Long.MAX_VALUE;
	}

	/**
	 * This function indexes the data object the textual content is assumed to
	 * be sorted increases the count of objects for every distinct keyword
	 * 
	 * @param dataObject
	 */
	public void addDataObject(DataObject dataObject) {
		if (storedObjects == null)
			storedObjects = new HashMap<Integer, DataObject>();
		if (allDataTextInCell == null)
			allDataTextInCell = new HashMap<String, Integer>();
		storedObjects.put(dataObject.getObjectId(), dataObject);
		dataObjectCount++;
		if (!spatialOnlyFlag)
			for (String s : dataObject.getObjectText()) {
				if (allDataTextInCell.containsKey(s))
					allDataTextInCell.put(s, allDataTextInCell.get(s) + 1);
				else
					allDataTextInCell.put(s, 1);
			}
	}

	public void addTuple(Tuple t) {
		if (storedTuples == null)
			storedTuples = new HashMap<Integer, Tuple>();
		storedTuples.put((Integer)t.getFiled(0), t);
		dataObjectCount++;
	}
	public IndexCell addDataObjectRecusrive(DataObject dataObject) {
		if (storedObjects == null)
			storedObjects = new HashMap<Integer, DataObject>();
		if (allDataTextInCell == null)
			allDataTextInCell = new HashMap<String, Integer>();
		storedObjects.put(dataObject.getObjectId(), dataObject);
		dataObjectCount++;
		if (!spatialOnlyFlag)
			for (String s : dataObject.getObjectText()) {
				if (allDataTextInCell.containsKey(s))
					allDataTextInCell.put(s, allDataTextInCell.get(s) + 1);
				else
					allDataTextInCell.put(s, 1);
			}
		return this;
	}

	public void addDataObjectStatics(DataObject dataObject) {
		if (allDataTextInCell == null)
			allDataTextInCell = new HashMap<String, Integer>();
		dataObjectCount++;
		if (!spatialOnlyFlag)
			for (String s : dataObject.getObjectText()) {
				if (allDataTextInCell.containsKey(s))
					allDataTextInCell.put(s, allDataTextInCell.get(s) + 1);
				else
					allDataTextInCell.put(s, 1);
			}
	}

	public void addQuery(Query query) {
		query.added = false;
		query.visitied = 0;
		if (storedQueries == null)
			storedQueries = new HashMap<Integer, Query>();

		if (!storedQueries.containsKey(query.getQueryId())) {
			queryCount++;
			storedQueries.put(query.getQueryId(),query);
		}

//		if (!spatialOnlyFlag) {
//			if (queriesInvertedList == null) {
//				queriesInvertedList = new HashMap<String, HashMap<String, ArrayList<Query>>>();
//				queriesInvertedList.put(query.getSrcId(), new HashMap<String, ArrayList<Query>>());
//			} else if (!queriesInvertedList.containsKey(query.getSrcId()))
//				queriesInvertedList.put(query.getSrcId(), new HashMap<String, ArrayList<Query>>());
//			for (String keyword : query.getQueryText()) {
//				if (!queriesInvertedList.get(query.getSrcId()).containsKey(keyword))
//					queriesInvertedList.get(query.getSrcId()).put(keyword, new ArrayList<Query>());
//				queriesInvertedList.get(query.getSrcId()).get(keyword).add(query);
//			}
//		}

	}

	public boolean cellOverlapsSpatiall(Rectangle rectangle) {
		return SpatialHelper.overlapsSpatially(bounds, rectangle);
	}

	public boolean cellOverlapsTextually(ArrayList<String> textList) {
		if (spatialOnlyFlag)
			return true;
		if (allDataTextInCell.size() != 0)
			return TextHelpers.overlapsTextually(allDataTextInCell, textList);
		return false;
	}

	public DataObject dropDataObject(Integer id) {
		if (storedObjects == null)
			return null;
		if (dataObjectCount > 0)
			dataObjectCount--;
		DataObject obj = storedObjects.get(id);
		storedObjects.remove(id);
		return obj;
	}

	public void dropQuery(Query query) {
		if (storedQueries == null)
			return;
		storedQueries.remove(query.getQueryId());
		
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof IndexCell))
			return false;
		IndexCell c = (IndexCell) o;
		return (c.getBounds().equals(this.getBounds()));
	}

	public Integer estimateDataObjectCountAll(ArrayList<String> keywords) {
		Integer min = Integer.MAX_VALUE;
		for (String keyword : keywords) {
			if (!allDataTextInCell.containsKey(keyword)) {
				min = 0;
				break;
			} else if (allDataTextInCell.get(keyword) < min)
				min = allDataTextInCell.get(keyword);
		}
		return min;
	}
	

	/**
	 * 
	 * Estimate how many data objects contain Any of the input keywords this is
	 * estimated as the sum of objects per keyword.
	 * 
	 * @param keywords
	 */
	public Integer estimateDataObjectCountAny(ArrayList<String> keywords) {

		Integer sum = 0;
		for (String keyword : keywords) {
			if (!allDataTextInCell.containsKey(keyword)) {
				continue;
			}
			sum += allDataTextInCell.get(keyword);
		}
		return sum;
	}

	

	public synchronized ArrayList<List<Query>> geSpatiotTextualOverlappingQueries(Point p, ArrayList<String> keywords) {
		ArrayList<List<Query>> result = new ArrayList<List<Query>>();
		if (queriesInvertedList == null)
			return result;
		Iterator itr = queriesInvertedList.entrySet().iterator();
		while (itr.hasNext()) {
			//HashSet<Integer> queries = new HashSet<Integer>();
			ArrayList<Query> finalQueries = new ArrayList<Query>();
			ArrayList<Query> tempQueries = new ArrayList<Query>();
			String queryScrId;
			Map.Entry entry = (Map.Entry) itr.next();
			HashMap<String, ArrayList<Query>> srcQueryList = (HashMap<String, ArrayList<Query>>) entry.getValue();
			queryScrId = (String) entry.getKey();
			for (String keyword : keywords) {
				List<Query> validQueries = srcQueryList.get(keyword);
				if (validQueries != null)
					try {
						for (Query q : validQueries) {

							if (SpatialHelper.overlapsSpatially(p, q.getSpatialRange())) {
								if (TextualPredicate.OVERlAPS.equals(q.getTextualPredicate())) {
									if (q.added != true) {
										q.added = true;
										finalQueries.add(q);
									}
								} else if (TextualPredicate.CONTAINS.equals(q.getTextualPredicate())) {
									if (q.visitied == 0)
										tempQueries.add(q);
									q.visitied++;
									if (q.visitied >= q.getQueryText().size()) {
										if (q.added != true) {
											q.added = true;
											finalQueries.add(q);
										}
									}
								}
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
			for (Query q : finalQueries) {
				q.added = false;
			}
			for (Query q : tempQueries) {
				q.visitied = 0;
			}
			result.add(finalQueries);
		}
		return result;
	}

	public HashMap<String, Integer> getAllDataTextInCell() {
		return allDataTextInCell;
	}

	public HashMap<String, Integer> getAllQueryTextInCell() {
		return allQueryTextInCell;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public DataObject getDataObject(Integer id) {
		return storedObjects.get(id);
	}

	public Integer getDataObjectCount() {
		return dataObjectCount;
	}

	public Integer getLevel() {
		return level;
	}

	public ArrayList<Query> getOuterEvaluatorQueries() {
		return outerEvaluatorQueries;
	}

	public ArrayList<Query> getQueries() {
		return null;
	}

	public Boolean getSpatialOnlyFlag() {
		return spatialOnlyFlag;
	}

	public ArrayList<DataObject> getSpatioTextualOverlappingDataObjects(Query query) {
		ArrayList<DataObject> qulifiedObjects = new ArrayList<DataObject>();
		if (storedObjects == null)
			return qulifiedObjects;
		Iterator<Entry<Integer, DataObject>> it = storedObjects.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, DataObject> entry = (Map.Entry<Integer, DataObject>) it.next();
			DataObject dataObject = (DataObject) entry.getValue();
			if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), query.getSpatialRange()) && TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), query.getQueryText(), query.getTextualPredicate()))
				qulifiedObjects.add((DataObject) entry.getValue());
		}
		return qulifiedObjects;
	}
	public ArrayList<DataObject> getSpatialOverlappingDataObjects(Rectangle range) {
		ArrayList<DataObject> qulifiedObjects = new ArrayList<DataObject>();
		if (storedObjects == null)
			return qulifiedObjects;
		Iterator<Entry<Integer, DataObject>> it = storedObjects.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, DataObject> entry = (Map.Entry<Integer, DataObject>) it.next();
			DataObject dataObject = (DataObject) entry.getValue();
			if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), range) )
				qulifiedObjects.add((DataObject) entry.getValue());
		}
		return qulifiedObjects;
	}
	public ArrayList<Tuple> getSpatialOverlappingTuples(Rectangle range) {
		ArrayList<Tuple> qulifiedObjects = new ArrayList<Tuple>();
		if (storedTuples == null)
			return qulifiedObjects;
		Iterator<Entry<Integer, Tuple>> it = storedTuples.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Tuple> entry = (Map.Entry<Integer, Tuple>) it.next();
			Tuple tuple = (Tuple) entry.getValue();
			if (SpatialHelper.overlapsSpatially(((DataObject)tuple.getFiled(3)).getLocation(), range) )
				qulifiedObjects.add(tuple);
		}
		return qulifiedObjects;
	}

	public HashMap<Integer, DataObject> getStoredObjects() {
		return storedObjects;
	}

	public ArrayList<DataObject> getStoredObjects(Rectangle rect, ArrayList<String> keywords, TextualPredicate textualPredicate) {

		ArrayList<DataObject> resultObjects = new ArrayList<DataObject>();
		for (DataObject dataObject : storedObjects.values())
			if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), rect) && TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), keywords, textualPredicate)) {
				resultObjects.add(dataObject);
			}
		return resultObjects;
	}

	public List<Query> getStoredQueries() {
		return null;
	}

	public HashMap<String, List<Query>> getTextualOverlappingQueries(ArrayList<String> keywords) {
		HashMap<String, List<Query>> result = new HashMap<String, List<Query>>();
		Iterator itr = queriesInvertedList.entrySet().iterator();
		while (itr.hasNext()) {
			HashSet<Query> queries = new HashSet<Query>();
			String queryScrId;
			Map.Entry entry = (Map.Entry) itr.next();
			HashMap<String, ArrayList<Query>> srcQueryList = (HashMap<String, ArrayList<Query>>) entry.getValue();
			queryScrId = (String) entry.getKey();
			for (String keyword : keywords) {
				ArrayList<Query> validQueries = srcQueryList.get(keyword);
				if (validQueries != null)
					for (Query q : validQueries)
						queries.add(q);
			}

			ArrayList<Query> finalQueries = new ArrayList<Query>(queries);
			result.put(queryScrId, finalQueries);
		}
		return result;
	}
	public ArrayList<DataObject> getTextualOverlappingDataObjects(ArrayList<String> keywords) {
		ArrayList<DataObject> result = new ArrayList<DataObject>();
		Iterator< DataObject>itr = storedObjects.values().iterator();
		while(itr.hasNext()){
			DataObject obj= itr.next();
			if(TextHelpers.overlapsTextually(obj.hashedText, keywords))
				result.add(obj);
		}
		return result;
	}

	public void removeQueryFromInvertedList(Query query) {
		if (!spatialOnlyFlag) {
			for (String keyword : query.getQueryText()) {
				List<Query> queries = queriesInvertedList.get(query.getSrcId()).get(keyword);
				int j = 0;
				Iterator<Query> itr = queries.iterator();
				while(itr.hasNext()){
					Query q = itr.next();
					if (query.getQueryId().equals(q.getQueryId())){
						itr.remove();
						break;
					}
				}
				

			}
		}
	}

	public void setAllDataTextInCell(HashMap<String, Integer> allDataTextInCell) {
		this.allDataTextInCell = allDataTextInCell;
	}

	public void setAllQueryTextInCell(HashMap<String, Integer> allQueryTextInCell) {
		this.allQueryTextInCell = allQueryTextInCell;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public void setDataObjectCount(Integer dataObjectCount) {
		this.dataObjectCount = dataObjectCount;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public void setOuterEvaluatorQueries(ArrayList<Query> outerEvaluatorQueries) {
		this.outerEvaluatorQueries = outerEvaluatorQueries;
	}

	public void setSpatialOnlyFlag(Boolean spatialOnlyFlag) {
		this.spatialOnlyFlag = spatialOnlyFlag;
	}

	public void setStoredObjects(HashMap<Integer, DataObject> storedObjects) {
		this.storedObjects = storedObjects;
	}

	
}
