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
package edu.purdue.cs.tornado.test;

import edu.purdue.cs.tornado.helper.JsonHelper;

public class TestJson {
	public static void main(String[] args) throws InterruptedException {
		String q1="{\"type\":\"cquery\","
				+ "\"name\":\"q3\","
				+ "\"sourceNames\":[\"Tweets\"]"
				+ ",\"plan\":{\"type\":\"project\","
				+ "\"attributes\":[\"*\"],\"children\":[{\"type\":\"select\",\"conditions\":[{\"argc\":2,\"jcond\":false,\"op\":\"CONTAINS\",\"lhs\":{\"sourceName\":\"T\",\"attributeName\":\"tags\"},\"rhs\":\"coffee\"},{\"argc\":2,\"jcond\":false,\"op\":\"INSIDE\",\"lhs\":{\"sourceName\":\"T\",\"attributeName\":\"loc\"},\"rhs\":null}],\"children\":[{\"type\":\"source\",\"name\":\"Tweets\",\"alias\":\"T\"}]}]},\"sqltext\":\"REGISTER QUERY q3 AS\nSELECT * FROM Tweets AS T\nWHERE CONTAINS(T.tags,\\\"coffee\\\")\nand INSIDE(T.loc,currentView);\",\"currentView\":{\"north\":41.894261440739655,\"east\":-87.59520145366213,\"south\":41.86301133887374,\"west\":-87.67656894633791}}";
		String q2="{\"type\":\"cquery\",\"name\":\"q1\",\"sourceNames\":[\"OSM_Data\",\"Tweets\"],\"plan\":{\"type\":\"project\",\"attributes\":[\"*\"],\"children\":[{\"type\":\"join\",\"conditions\":[{\"argc\":3,\"jcond\":true,\"op\":\"WITHIN_DISTANCE\",\"lhs\":{\"sourceName\":\"O\",\"attributeName\":\"loc\"},\"rhs\":{\"sourceName\":\"T\",\"attributeName\":\"loc\"},\"cval\":1}],\"children\":[{\"type\":\"select\",\"conditions\":[{\"argc\":2,\"jcond\":false,\"op\":\"INSIDE\",\"lhs\":{\"sourceName\":\"T\",\"attributeName\":\"loc\"},\"rhs\":null}],\"children\":[{\"type\":\"source\",\"name\":\"Tweets\",\"alias\":\"T\"}]},{\"type\":\"select\",\"conditions\":[{\"argc\":2,\"jcond\":false,\"op\":\"OVERLAPS\",\"lhs\":{\"sourceName\":\"O\",\"attributeName\":\"tags\"},\"rhs\":null},{\"argc\":2,\"jcond\":false,\"op\":\"CONTAINS\",\"lhs\":{\"sourceName\":\"O\",\"attributeName\":\"tags\"},\"rhs\":\"attraction\"}],\"children\":[{\"type\":\"source\",\"name\":\"OSM_Data\",\"alias\":\"O\"}]}]}]},\"sqltext\":\"REGISTER QUERY q1 AS\nSELECT * FROM OSM_Data AS O, Tweets AS T\nWHERE WITHIN_DISTANCE(O.loc,T.loc,1)\nand CONTAINS(O.tags,\\\"attraction\\\")\nand INSIDE(T.loc,currentView)\nand OVERLAPS(O.tags, T.text);\",\"currentView\":{\"north\":41.894261440739655,\"east\":-87.59520145366213,\"south\":41.86301133887374,\"west\":-87.67656894633791}}";

		JsonHelper.convertJsonStringToQuery(q1);
	}
}
