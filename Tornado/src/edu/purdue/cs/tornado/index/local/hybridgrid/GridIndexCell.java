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
package edu.purdue.cs.tornado.index.local.hybridgrid;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class GridIndexCell extends IndexCell {
	private ArrayList<Query> storedQueries;
	private HashMap<String, HashMap<String, ArrayList<Query>>> queriesInvertedList;//src,keyword,Query
	private Rectangle bounds;
	private IndexCellCoordinates globalCoordinates;
	private Integer indexCellCost;
	private boolean transmitted;
	private Long minExpireTime;

	
	public GridIndexCell() {
		super();
		storedQueries = null;
		queriesInvertedList = null;
		this.bounds = null;
		indexCellCost = 0;
		transmitted = false;
		minExpireTime = Long.MAX_VALUE;
	}

	public GridIndexCell(Rectangle bounds, Boolean spatialOnlyFlag, Integer level) {
		super();
		storedQueries = null;
		queriesInvertedList = null;
		this.bounds = bounds;
		indexCellCost = 0;
		//used by multilevel index
		indexCellCost = 0;
		this.transmitted = false;
		this.minExpireTime = Long.MAX_VALUE;

	}

	public GridIndexCell(Rectangle bounds, Boolean spatialOnlyFlag, Integer level, IndexCellCoordinates globalCoordinates) {
		storedQueries = null;
		queriesInvertedList = null;
		this.bounds = bounds;
		//used by multilevel index
		this.transmitted = false;
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

	public void addQuery(Query query) {
		query.added[0] = false;
		query.visitied = 0;
		if (storedQueries == null)
			storedQueries = new ArrayList<Query>();

		if (!storedQueries.contains(query)) {
			storedQueries.add(query);
		}
		if (query.getRemoveTime() < minExpireTime)
			minExpireTime = query.getRemoveTime();

		if (queriesInvertedList == null) {
			queriesInvertedList = new HashMap<String, HashMap<String, ArrayList<Query>>>();
			queriesInvertedList.put(query.getSrcId(), new HashMap<String, ArrayList<Query>>());
		} else if (!queriesInvertedList.containsKey(query.getSrcId()))
			queriesInvertedList.put(query.getSrcId(), new HashMap<String, ArrayList<Query>>());
		for (String keyword : query.getQueryText()) {
			if (!queriesInvertedList.get(query.getSrcId()).containsKey(keyword))
				queriesInvertedList.get(query.getSrcId()).put(keyword, new ArrayList<Query>());
			queriesInvertedList.get(query.getSrcId()).get(keyword).add(query);
		}

	}

	public boolean cellOverlapsSpatiall(Rectangle rectangle) {
		return SpatialHelper.overlapsSpatially(bounds, rectangle);
	}

	public boolean cellOverlapsTextually(ArrayList<String> textList) {
		return true;
	}

	public void dropQuery(Query query) {
		if (storedQueries == null)
			return;
		int i = 0;
		boolean found = false;
		for (i = 0; i < storedQueries.size(); i++) {
			if (query.getSrcId().equals(storedQueries.get(i).getSrcId()) && query.getQueryId().equals(storedQueries.get(i).getQueryId())) {
				found = true;
				break;
			}
		}

		if (found) {
			Query q = storedQueries.remove(i);
			removeQueryFromInvertedList(q);
		}
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof GridIndexCell))
			return false;
		GridIndexCell c = (GridIndexCell) o;
		return (c.getBounds().equals(this.getBounds()));
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

		return sum;
	}

	public ArrayList<Query> findandRemoveExpriedQueries() {
		Long nowTime = (new Date()).getTime();
		if (nowTime < minExpireTime || storedQueries == null || storedQueries.size() == 0)
			return null;
		minExpireTime = Long.MAX_VALUE;
		Iterator<Query> itr = storedQueries.iterator();
		ArrayList<Query> expriedList = new ArrayList<Query>();
		while (itr.hasNext()) {
			Query q = itr.next();
			if (q.getRemoveTime() < nowTime) {
				removeQueryFromInvertedList(q);
				expriedList.add(q);
				itr.remove();

			} else if (q.getRemoveTime() < minExpireTime)
				minExpireTime = q.getRemoveTime();

		}
		return expriedList;
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
									if (q.added[0] != true) {
										q.added[0] = true;
										finalQueries.add(q);
									}
								} else if (TextualPredicate.CONTAINS.equals(q.getTextualPredicate())) {
									if (q.visitied == 0)
										tempQueries.add(q);
									q.visitied++;
									if (q.visitied >= q.getQueryText().size()) {
										if (q.added[0] != true && TextHelpers.containsTextually(keywords, q.getQueryText())) {
											q.added[0] = true;
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
				q.added[0] = false;
			}
			for (Query q : tempQueries) {
				q.visitied = 0;
			}
			result.add(finalQueries);
		}
		return result;
	}

	public Rectangle getBounds() {
		return bounds;
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

	public void removeQueryFromInvertedList(Query query) {

		for (String keyword : query.getQueryText()) {
			List<Query> queries = queriesInvertedList.get(query.getSrcId()).get(keyword);
			int j = 0;
			Iterator<Query> itr = queries.iterator();
			while (itr.hasNext()) {
				Query q = itr.next();
				if (query.getQueryId().equals(q.getQueryId())) {
					itr.remove();
					break;
				}
			}

		}

	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public void setStoredQueries(ArrayList<Query> storedQueries) {
		this.storedQueries = storedQueries;
	}

	public List<Query> getQueries() {
		return storedQueries;
	}

	public HashMap<String, HashMap<String, ArrayList<Query>>> getQueriesInvertedList() {
		return queriesInvertedList;
	}

	public void setQueriesInvertedList(HashMap<String, HashMap<String, ArrayList<Query>>> queriesInvertedList) {
		this.queriesInvertedList = queriesInvertedList;
	}

	public IndexCellCoordinates getGlobalCoordinates() {
		return globalCoordinates;
	}

	public void setGlobalCoordinates(IndexCellCoordinates globalCoordinates) {
		this.globalCoordinates = globalCoordinates;
	}

	public Integer getIndexCellCost() {
		return indexCellCost;
	}

	public void setIndexCellCost(Integer indexCellCost) {
		this.indexCellCost = indexCellCost;
	}

	public boolean isTransmitted() {
		return transmitted;
	}

	public void setTransmitted(boolean transmitted) {
		this.transmitted = transmitted;
	}

	public Long getMinExpireTime() {
		return minExpireTime;
	}

	public void setMinExpireTime(Long minExpireTime) {
		this.minExpireTime = minExpireTime;
	}

	public ArrayList<Query> getStoredQueries() {
		return storedQueries;
	}

	public com.sun.tools.javac.util.List<DataObject> getStoredObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStoredObjects(com.sun.tools.javac.util.List<DataObject> objects) {
		// TODO Auto-generated method stub

	}

	public Integer getDataObjectCount() {
		// TODO Auto-generated method stub
		return null;
	}

	public DataObject dropDataObject(Integer id) {
		return null;

	}

	public HashMap<String, Integer> getAllDataTextInCell() {
		// TODO Auto-generated method stub
		return null;
	}

	public com.sun.tools.javac.util.List<DataObject> getStoredObjects(Rectangle range, ArrayList<String> keywords, TextualPredicate predicate) {
		// TODO Auto-generated method stub
		return null;
	}

	public DataObject getDataObject(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addDataObject(DataObject id) {
		// TODO Auto-generated method stub

	}

	public int estimateDataObjectCountAll(ArrayList<String> keywords) {
		// TODO Auto-generated method stub
		return 0;
	}

}
