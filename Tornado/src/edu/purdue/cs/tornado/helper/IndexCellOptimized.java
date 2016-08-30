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
package edu.purdue.cs.tornado.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import clojure.lang.Obj;
import edu.purdue.cs.tornado.index.local.LocalHybridPyramidGridIndexOptimized;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.MinimalRangeQuery;
import edu.purdue.cs.tornado.messages.Query;

public class IndexCellOptimized {
	public HashMap<String, ArrayList<MinimalRangeQuery>> internalQueriesInvertedList;//queries that fall only within the cells range
	public KeyWordTrieIndex trieIndex;
	public HashSet<Integer> masterCells;

	public IndexCellOptimized() {
		internalQueriesInvertedList = null;
	}

	public IndexCellOptimized(Rectangle bounds) {
		internalQueriesInvertedList = null;
	}

	public IndexCellOptimized(Rectangle bounds, Integer globalCoordinates) {
		internalQueriesInvertedList = null;
	}

	public IndexCellOptimized addDataObjectRecusrive(DataObject dataObject) {

		return this;
	}

	public void addDataObjectStatics(DataObject dataObject) {

	}

	public void addInternalQuery(MinimalRangeQuery query) {
		query.added = false;
		//query.visitied = 0;

		//		if (query.getTextualPredicate().equals(TextualPredicate.CONTAINS)) {
		//			keywordTrie.insert(query.getQueryText(), query);
		//		} else if (query.getTextualPredicate().equals(TextualPredicate.OVERlAPS)) 
		//		{
		if (internalQueriesInvertedList == null) {
			internalQueriesInvertedList = new HashMap<String, ArrayList<MinimalRangeQuery>>();

		}
		for (String keyword : query.getQueryText()) {
			if (!internalQueriesInvertedList.containsKey(keyword))
				internalQueriesInvertedList.put(keyword, new ArrayList<MinimalRangeQuery>());
			internalQueriesInvertedList.get(keyword).add(query);
		}

		//	}
	}

	public void addInternalQuery(String keyword, MinimalRangeQuery query) {
		//query.visitied = 0;

		if (internalQueriesInvertedList == null) {
			internalQueriesInvertedList = new HashMap<String, ArrayList<MinimalRangeQuery>>();

		}

		if (!internalQueriesInvertedList.containsKey(keyword)) {
			internalQueriesInvertedList.put(keyword, new ArrayList<MinimalRangeQuery>());
			internalQueriesInvertedList.get(keyword).add(query);
		} else if (internalQueriesInvertedList.get(keyword).size() < KeyWordTrieIndex.SPLIT_THRESHOLD_FREQ || query.getQueryText().size() == 1) {
			internalQueriesInvertedList.get(keyword).add(query);
		} else {
			if (trieIndex == null) {
				trieIndex = new KeyWordTrieIndex();
			}
			trieIndex.insert(query.getQueryText(), query);
		//	trieIndex.insert(new ArrayList<String>(query.getQueryText().subList(0, 2)), query);
		}

		//	trieIndex.insert( keyword,query);
	}

	public boolean cellOverlapsTextually(ArrayList<String> textList) {
		return TextHelpers.overlapsTextually(internalQueriesInvertedList.keySet(), textList);
	}

	public ArrayList<Query> findandRemoveExpriedQueries() {
		//TODO
		return null;
	}

	public void dropQuery(Query query) {

	}

	public ArrayList<List<MinimalRangeQuery>> getInternalSpatiotTextualOverlappingQueries(Point p, ArrayList<String> keywords) {
		ArrayList<List<MinimalRangeQuery>> result = new ArrayList<List<MinimalRangeQuery>>();
		ArrayList<MinimalRangeQuery> finalQueries = new ArrayList<MinimalRangeQuery>();

		HashMap<String, ArrayList<MinimalRangeQuery>> srcQueryList = internalQueriesInvertedList;
		if (srcQueryList != null)
			for (String keyword : keywords) {
				List<MinimalRangeQuery> validQueries = srcQueryList.get(keyword);
				if (validQueries != null)
					try {
						for (MinimalRangeQuery q : validQueries) {
							LocalHybridPyramidGridIndexOptimized.totalVisited++;
							if (SpatialHelper.overlapsSpatially(p, q.getSpatialRange())) {
								LocalHybridPyramidGridIndexOptimized.spatialOverlappingQuries++;
								if (TextualPredicate.OVERlAPS.equals(q.getTextualPredicate())) {
									if (q.added != true) {
										q.added = true;
										finalQueries.add(q);
									}
								} else if (TextualPredicate.CONTAINS.equals(q.getTextualPredicate())) {
									if (TextHelpers.containsTextually(keywords, q.getQueryText()))
										finalQueries.add(q);
								}
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}

			}
		if (trieIndex != null) {
			List<MinimalRangeQuery> validQueries = trieIndex.find(keywords);
			if (validQueries != null)
				try {
					for (MinimalRangeQuery q : validQueries) {
						LocalHybridPyramidGridIndexOptimized.totalVisited++;
						if (SpatialHelper.overlapsSpatially(p, q.getSpatialRange())) {
							LocalHybridPyramidGridIndexOptimized.spatialOverlappingQuries++;
						//	if (TextHelpers.containsTextually(keywords, q.getQueryText()))
								finalQueries.add(q);

						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		for (MinimalRangeQuery q : finalQueries) {
			q.added = false;
			//	q.visitied = 0;
		}
		//		for (Query q : tempQueries) {
		//			q.visitied = 0;
		//		}
		result.add(finalQueries);
		return result;
	}

	public synchronized ArrayList<List<MinimalRangeQuery>> getInternalSpatiotTextualOverlappingQueriesOld(Point p, ArrayList<String> keywords) {
		ArrayList<List<MinimalRangeQuery>> result = new ArrayList<List<MinimalRangeQuery>>();
		ArrayList<MinimalRangeQuery> finalQueries = new ArrayList<MinimalRangeQuery>();
		ArrayList<MinimalRangeQuery> tempQueries = new ArrayList<MinimalRangeQuery>();

		if (internalQueriesInvertedList != null) {
			Iterator itr = internalQueriesInvertedList.entrySet().iterator();
			while (itr.hasNext()) {
				//HashSet<Integer> queries = new HashSet<Integer>();
				String queryScrId;
				Map.Entry entry = (Map.Entry) itr.next();
				HashMap<String, ArrayList<MinimalRangeQuery>> srcQueryList = (HashMap<String, ArrayList<MinimalRangeQuery>>) entry.getValue();
				queryScrId = (String) entry.getKey();
				for (String keyword : keywords) {
					List<MinimalRangeQuery> validQueries = srcQueryList.get(keyword);
					if (validQueries != null)
						try {
							for (MinimalRangeQuery q : validQueries) {

								if (SpatialHelper.overlapsSpatially(p, q.getSpatialRange())) {
									if (TextualPredicate.OVERlAPS.equals(q.getTextualPredicate())) {
										if (q.added != true) {
											q.added = true;
											finalQueries.add(q);
										}
									} else if (TextualPredicate.CONTAINS.equals(q.getTextualPredicate())) {
										//										if (q.visitied == 0)
										//											tempQueries.add(q);
										//										q.visitied++;
										//										if (q.visitied >= q.getQueryText().size()) {
										//											if (q.added != true) {
										//												q.added = true;
										//												finalQueries.add(q);
										//											}
										//										}
									}
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			}
		}
		for (MinimalRangeQuery q : finalQueries) {
			q.added = false;
			//	q.visitied = 0;
		}
		for (MinimalRangeQuery q : tempQueries) {
			//	q.visitied = 0;
		}
		result.add(finalQueries);
		return result;
	}

	public HashMap<String, List<Query>> getTextualOverlappingQueries(ArrayList<String> keywords) {
		HashMap<String, List<Query>> result = new HashMap<String, List<Query>>();
		Iterator itr = internalQueriesInvertedList.entrySet().iterator();
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

}
