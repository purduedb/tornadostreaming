/**
 * Copyright Jul 10, 2015
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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import edu.purdue.cs.tornado.messages.CombinedTuple;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class JsonHelper {
	//****************************************************************************
	//***********************UI constants
	public static final String continousQueryType = "cquery";
	public static final String name = "name";
	public static final String tag = "tag";
	public static final String dropTag = "-";
	public static final String addTag = "+";
	public static final String sourceNames = "sourceNames";
	public static final String plan = "plan";
	public static final String type = "type";
	public static final String attributes = "attributes";
	public static final String join = "join";
	public static final String conditions = "conditions";
	public static final String argc = "argc";
	public static final String jcond = "jcond";
	public static final String op = "op";
	public static final String lhs = "lhs";
	public static final String rhs = "rhs";
	public static final String WITHIN_DISTANCE = "WITHIN_DISTANCE";
	public static final String OVERLAPS = "OVERLAPS";
	public static final String SEMANTIC = "SEMANTIC";
	public static final String sourceName = "sourceName";
	public static final String attributeName = "attributeName";
	public static final String loc = "loc";
	public static final String children = "children";
	public static final String cval = "cval";
	public static final String select = "select";
	public static final String INSIDE = "INSIDE";
	public static final String alias = "alias";
	public static final String source = "source";
	public static final String CONTAINS = "CONTAINS";
	public static final String currentView = "currentView";
	public static final String north = "north";
	public static final String east = "east";
	public static final String south = "south";
	public static final String west = "west";
	public static final String lat = "lat";
	public static final String lng = "lng";
	public static final String text = "text";
	public static final String knn = "knn";
	public static final String kval = "kval";
	public static final String focal = "focal";
	private static Gson gson = new Gson();

	public static DataObject convertJsonStringToDataObject(String dataObjectJson) {
		return null;
	}

	public static Query convertJsonStringToQuery(String queryJson) {
		JsonReader reader = new JsonReader(new StringReader(queryJson));
		reader.setLenient(true);
		Map m = gson.fromJson(reader, Map.class);
		Query q = new Query();
		Map sourceAliasMap = new HashMap<String, String>();
		q.setQueryId((Integer) m.get(JsonHelper.name));
		String queryTag = (String) m.get(JsonHelper.tag);
		if (JsonHelper.dropTag.equals(queryTag)) {
			q.setCommand(Command.dropCommand);
			return q;
		} else {
			q.setCommand(Command.addCommand);
		}

		Map queryMap = (Map) ((List) ((Map) m.get(JsonHelper.plan)).get(JsonHelper.children)).get(0);
		List sourceNames = (List) m.get(JsonHelper.sourceNames);
		if (sourceNames.size() > 1) {//this is a join operator{
			q.setQueryType(SpatioTextualConstants.queryTextualSpatialJoin);

			double latMin = (Double) ((Map) m.get(JsonHelper.currentView)).get(JsonHelper.south);
			double lonMin = (Double) ((Map) m.get(JsonHelper.currentView)).get(JsonHelper.west);
			double latMax = (Double) ((Map) m.get(JsonHelper.currentView)).get(JsonHelper.north);
			double LonMax = (Double) ((Map) m.get(JsonHelper.currentView)).get(JsonHelper.east);

			q.setTextualPredicate(TextualPredicate.NONE);
			q.setTextualPredicate2(TextualPredicate.NONE);
			q.setJoinTextualPredicate(TextualPredicate.NONE);
			String textLHSAlias = null;
			String textRHSAlias = null;
			List<Map> conditions = ((List) queryMap.get(JsonHelper.conditions));
			for (Map condition : conditions) {
				if (condition != null && condition.containsKey(JsonHelper.op) && ((String) condition.get(JsonHelper.op)).equalsIgnoreCase(JsonHelper.WITHIN_DISTANCE))
					q.setDistance((Double) condition.get(JsonHelper.cval));
				else if (condition != null && condition.containsKey(JsonHelper.op)
						&& (((String) condition.get(JsonHelper.op)).equalsIgnoreCase(CONTAINS) || ((String) condition.get(JsonHelper.op)).equalsIgnoreCase(OVERLAPS) || ((String) condition.get(JsonHelper.op)).equalsIgnoreCase(SEMANTIC))) {

					if (OVERLAPS.equalsIgnoreCase(((String) condition.get(JsonHelper.op)).toLowerCase()))
						q.setJoinTextualPredicate(TextualPredicate.OVERlAPS);
					else if (SEMANTIC.equalsIgnoreCase(((String) condition.get(JsonHelper.op)).toLowerCase()))
						q.setJoinTextualPredicate(TextualPredicate.SEMANTIC);
					else if (CONTAINS.equalsIgnoreCase(((String) condition.get(JsonHelper.op)).toLowerCase()))
						q.setJoinTextualPredicate(TextualPredicate.CONTAINS);

					if (condition.containsKey(JsonHelper.lhs))
						textLHSAlias = (String) ((Map) condition.get(JsonHelper.lhs)).get(JsonHelper.sourceName);
					if (condition.containsKey(JsonHelper.rhs))
						textRHSAlias = (String) ((Map) condition.get(JsonHelper.rhs)).get(JsonHelper.sourceName);
				} else if (condition != null && condition.containsKey(JsonHelper.op) && (((String) condition.get(JsonHelper.op)).equalsIgnoreCase(JsonHelper.INSIDE))) {
					Map rhs = (Map) condition.get(JsonHelper.rhs);
					if (rhs.containsKey(JsonHelper.south)) {//location is specified here 
						latMin = (Double) rhs.get(JsonHelper.south);
						lonMin = (Double) rhs.get(JsonHelper.west);
						latMax = (Double) rhs.get(JsonHelper.north);
						LonMax = (Double) rhs.get(JsonHelper.east);
					} //else we use the currentview as query location
				}
			}

			String src1 = "";
			ArrayList<String> text1 = null;
			String src1Alias = "";
			TextualPredicate textualPredicate1 = TextualPredicate.NONE;
			Map map1 = (Map) ((List) queryMap.get(JsonHelper.children)).get(0);
			if (JsonHelper.source.equals((String) (map1).get(JsonHelper.type))) {
				src1 = (String) (map1).get(JsonHelper.name);//
				src1Alias = (String) (map1).get(JsonHelper.alias);//
			} else {
				src1 = (String) ((Map) ((List) map1.get(JsonHelper.children)).get(0)).get(JsonHelper.name);
				src1Alias = (String) ((Map) ((List) map1.get(JsonHelper.children)).get(0)).get(JsonHelper.alias);
				for (int i = 0; i < ((List) map1.get(JsonHelper.conditions)).size(); i++) {
					Map condition = (Map) ((List) map1.get(JsonHelper.conditions)).get(i);
					if (condition.containsKey(JsonHelper.op) && (((String) condition.get(JsonHelper.op)).equalsIgnoreCase(CONTAINS) || ((String) condition.get(JsonHelper.op)).equalsIgnoreCase(OVERLAPS)
							|| ((String) condition.get(JsonHelper.op)).equalsIgnoreCase(SEMANTIC))) {
						{

							text1 = TextHelpers.sortTextArrayList((ArrayList<String>) condition.get(JsonHelper.rhs));

							if (OVERLAPS.equalsIgnoreCase(((String) condition.get(JsonHelper.op)).toLowerCase()))
								textualPredicate1 = (TextualPredicate.OVERlAPS);
							else if (SEMANTIC.equalsIgnoreCase(((String) condition.get(JsonHelper.op)).toLowerCase()))
								textualPredicate1 = (TextualPredicate.SEMANTIC);
							else if (CONTAINS.equalsIgnoreCase(((String) condition.get(JsonHelper.op)).toLowerCase()))
								textualPredicate1 = (TextualPredicate.CONTAINS);

						}
					}
				}
			}

			Map map2 = (Map) ((List) queryMap.get(JsonHelper.children)).get(1);
			String src2 = "";
			String src2Alias = "";
			ArrayList<String> text2 = null;
			TextualPredicate textualPredicate2 = TextualPredicate.NONE;
			if (JsonHelper.source.equals((String) (map2).get(JsonHelper.type))) {
				src2 = (String) (map2).get(JsonHelper.name);
				src2Alias = (String) (map2).get(JsonHelper.alias);//
			} else {
				src2 = (String) ((Map) ((List) map2.get(JsonHelper.children)).get(0)).get(JsonHelper.name);
				src2Alias = (String) ((Map) ((List) map2.get(JsonHelper.children)).get(0)).get(JsonHelper.alias);
				for (int i = 0; i < ((List) map2.get(JsonHelper.conditions)).size(); i++) {
					Map condition = (Map) ((List) map2.get(JsonHelper.conditions)).get(i);
					if (condition.containsKey(JsonHelper.op) && (((String) condition.get(JsonHelper.op)).equalsIgnoreCase(CONTAINS) || ((String) condition.get(JsonHelper.op)).equalsIgnoreCase(OVERLAPS)
							|| ((String) condition.get(JsonHelper.op)).equalsIgnoreCase(SEMANTIC))) {
						text2 = TextHelpers.sortTextArrayList((ArrayList<String>) condition.get(JsonHelper.rhs));

						if (OVERLAPS.equalsIgnoreCase(((String) condition.get(JsonHelper.op)).toLowerCase()))
							textualPredicate2 = (TextualPredicate.OVERlAPS);
						else if (SEMANTIC.equalsIgnoreCase(((String) condition.get(JsonHelper.op)).toLowerCase()))
							textualPredicate2 = (TextualPredicate.SEMANTIC);
						else if (CONTAINS.equalsIgnoreCase(((String) condition.get(JsonHelper.op)).toLowerCase()))
							textualPredicate2 = (TextualPredicate.CONTAINS);
					}
				}

			}
			if (textLHSAlias == null || textLHSAlias.equals(src1Alias)) {
				q.setDataSrc(src1);
				q.setDataSrc2(src2);
				if (!"".equals(text1)) {
					q.setQueryText(text1);
					q.setTextualPredicate(textualPredicate1);
				}
				if (!"".equals(text2)) {
					q.setQueryText2(text2);
					q.setTextualPredicate2(textualPredicate2);
				}
			} else {
				q.setDataSrc2(src1);
				q.setDataSrc(src2);
				if (!"".equals(text1)) {
					q.setQueryText2(text1);
					q.setTextualPredicate2(textualPredicate1);
				}
				if (!"".equals(text2)) {
					q.setQueryText(text2);
					q.setTextualPredicate(textualPredicate2);
				}
			}
			q.setSpatialRange(new Rectangle(SpatialHelper.convertFromLatLonToXYPoint(new LatLong(latMin, lonMin)), SpatialHelper.convertFromLatLonToXYPoint(new LatLong(latMax, LonMax))));
		} else if (queryJson.contains("KNN") || queryJson.contains("knn")) {//either range or KNN
			q.setQueryType(SpatioTextualConstants.queryTextualKNN);
			q.setDataSrc((String) sourceNames.get(0));
			q.setCommand(Command.addCommand);
			q.setTextualPredicate(TextualPredicate.NONE);
			q.setK(0);
			q.setFocalPoint(new Point(0.0, 0.0));
			q.setQueryText(new ArrayList<String>());
			List<Map> conditions = (List<Map>) queryMap.get(JsonHelper.conditions);
			for (Map condition : conditions) {
				String operation = (String) condition.get(JsonHelper.op);
				Map lhs = (Map) condition.get(JsonHelper.lhs);
				//textual predicate
				if (lhs != null && lhs.containsKey(JsonHelper.attributeName) && JsonHelper.text.equals(lhs.get(JsonHelper.attributeName))) {
					if (OVERLAPS.equalsIgnoreCase(operation.toLowerCase()))
						q.setTextualPredicate(TextualPredicate.OVERlAPS);
					else if (SEMANTIC.equalsIgnoreCase(operation.toLowerCase()))
						q.setTextualPredicate(TextualPredicate.SEMANTIC);
					else if (CONTAINS.equalsIgnoreCase(operation.toLowerCase()))
						q.setTextualPredicate(TextualPredicate.CONTAINS);
					ArrayList<String> text = (ArrayList<String>) condition.get(JsonHelper.rhs);
					q.setQueryText(TextHelpers.sortTextArrayList(text));
				} else if (JsonHelper.knn.equals(operation.toLowerCase())) {
					q.setK(((Double) condition.get(JsonHelper.kval)).intValue());
					Double lat = (Double) ((Map) ((Map) ((Map) condition.get(JsonHelper.rhs))).get("val")).get(JsonHelper.lat);
					Double lng = (Double) ((Map) ((Map) ((Map) condition.get(JsonHelper.rhs))).get("val")).get(JsonHelper.lng);
					q.setFocalPoint(SpatialHelper.convertFromLatLonToXYPoint(new LatLong(lat, lng)));
				}

			}

			//leave now till figuring out the exact KNN syntanx

		} else {

			q.setQueryType(SpatioTextualConstants.queryTextualRange);
			q.setDataSrc((String) sourceNames.get(0));
			q.setCommand(Command.addCommand);
			q.setTextualPredicate(TextualPredicate.NONE);
			//current view location 
			double latMin = (Double) ((Map) m.get(JsonHelper.currentView)).get(JsonHelper.south);
			double lonMin = (Double) ((Map) m.get(JsonHelper.currentView)).get(JsonHelper.west);
			double latMax = (Double) ((Map) m.get(JsonHelper.currentView)).get(JsonHelper.north);
			double LonMax = (Double) ((Map) m.get(JsonHelper.currentView)).get(JsonHelper.east);
			//iterate over conditions to update default syntax

			List<Map> conditions = (List<Map>) queryMap.get(JsonHelper.conditions);
			for (Map condition : conditions) {
				String operation = (String) condition.get(JsonHelper.op);
				Map lhs = (Map) condition.get(JsonHelper.lhs);
				//textual predicate
				if (JsonHelper.text.equals(lhs.get(JsonHelper.attributeName))) {
					if (OVERLAPS.equalsIgnoreCase(operation.toLowerCase()))
						q.setTextualPredicate(TextualPredicate.OVERlAPS);
					else if (SEMANTIC.equalsIgnoreCase(operation.toLowerCase()))
						q.setTextualPredicate(TextualPredicate.SEMANTIC);
					else if (CONTAINS.equalsIgnoreCase(operation.toLowerCase()))
						q.setTextualPredicate(TextualPredicate.CONTAINS);
					ArrayList<String> text = (ArrayList<String>) condition.get(JsonHelper.rhs);
					q.setQueryText(TextHelpers.sortTextArrayList(text));
				} else if (JsonHelper.loc.equals(lhs.get(JsonHelper.attributeName))) {
					Map rhs = (Map) condition.get(JsonHelper.rhs);
					if (rhs.containsKey(JsonHelper.south)) {//location is specified here 
						latMin = (Double) rhs.get(JsonHelper.south);
						lonMin = (Double) rhs.get(JsonHelper.west);
						latMax = (Double) rhs.get(JsonHelper.north);
						LonMax = (Double) rhs.get(JsonHelper.east);
					} //else we use the currentview as query location
				}

			}

			q.setSpatialRange(new Rectangle(SpatialHelper.convertFromLatLonToXYPoint(new LatLong(latMin, lonMin)), SpatialHelper.convertFromLatLonToXYPoint(new LatLong(latMax, LonMax))));

		}

		System.out.println("Query " + q.toString());
		return q;

	}

	public static String convertOutputToJsonString(CombinedTuple outputTuple) {
		return null;
	}

	public static String convertMapToJsonString(Map jsonMap) {
		return gson.toJson(jsonMap, Map.class);
	}

}
