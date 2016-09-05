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
package edu.purdue.cs.tornado.index.local.hybridpyramidminimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.MinimalRangeQuery;
import edu.purdue.cs.tornado.messages.Query;

public class IndexCellOptimized {
	public HashMap<String, ArrayList<MinimalRangeQuery>> rareAndOverlapKeywords;//queries that fall only within the cells range
	public KeyWordTrieIndexMinimal trieIndex;

	public IndexCellOptimized() {
		rareAndOverlapKeywords = null;
	}

	public IndexCellOptimized(Rectangle bounds) {
		rareAndOverlapKeywords = null;
	}

	public IndexCellOptimized(Rectangle bounds, Integer globalCoordinates) {
		rareAndOverlapKeywords = null;
	}

	public IndexCellOptimized addDataObjectRecusrive(DataObject dataObject) {

		return this;
	}

	public void addDataObjectStatics(DataObject dataObject) {

	}

	

	public void addInternalQuery(MinimalRangeQuery query) {
		query.added = false;
		if (rareAndOverlapKeywords == null) {
			rareAndOverlapKeywords = new HashMap<String, ArrayList<MinimalRangeQuery>>();

		}
		for (String keyword : query.getQueryText()) {
			if (!rareAndOverlapKeywords.containsKey(keyword))
				rareAndOverlapKeywords.put(keyword, new ArrayList<MinimalRangeQuery>());
			rareAndOverlapKeywords.get(keyword).add(query);
		}
	}

	public void addInternalQuery(String keyword, MinimalRangeQuery query) {
		if (rareAndOverlapKeywords == null) {
			rareAndOverlapKeywords = new HashMap<String, ArrayList<MinimalRangeQuery>>();
		}

		if (!rareAndOverlapKeywords.containsKey(keyword)) {
			rareAndOverlapKeywords.put(keyword, new ArrayList<MinimalRangeQuery>());
			rareAndOverlapKeywords.get(keyword).add(query);
		} else if (rareAndOverlapKeywords.get(keyword).size() < KeyWordTrieIndexMinimal.SPLIT_THRESHOLD_FREQ || query.getQueryText().size() == 1) {
			rareAndOverlapKeywords.get(keyword).add(query);
		} else {
			if (trieIndex == null) {
				trieIndex = new KeyWordTrieIndexMinimal();
			}
			trieIndex.insert(query.getQueryText(), query);
			//	trieIndex.insert(new ArrayList<String>(query.getQueryText().subList(0, 2)), query);
		}

		//	trieIndex.insert( keyword,query);
	}



	public boolean cellOverlapsTextually(ArrayList<String> textList) {
		return TextHelpers.overlapsTextually(rareAndOverlapKeywords.keySet(), textList);
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

		HashMap<String, ArrayList<MinimalRangeQuery>> srcQueryList = rareAndOverlapKeywords;
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
							finalQueries.add(q);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		for (MinimalRangeQuery q : finalQueries) {
			q.added = false;

		}
		result.add(finalQueries);
		return result;
	}

}
