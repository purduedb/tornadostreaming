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
package edu.purdue.cs.tornado;

import java.util.HashMap;

import backtype.storm.Config;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.spouts.TextualRangeQueryGenerator;
import edu.purdue.cs.tornado.spouts.TextualSpatialJoinGenerator;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		SpatioTextualToplogyBuilder builder = new SpatioTextualToplogyBuilder();
		//builder.setSpout("TweetsGenerator", new DummyTweetGenerator(), 1);
		builder.setSpout("TextualRangeQueryGenerator",
				new TextualRangeQueryGenerator("TweetsGenerator"), 1);
		builder.setSpout("TextualDistanceJoinQueryGenerator",
				new TextualSpatialJoinGenerator("OpenStreetMap",
						"TweetsGenerator"), 1);

		HashMap<String, String> staticSourceConf = new HashMap<String, String>();
		staticSourceConf.put("key1", "value1");
		staticSourceConf.put("key2", "value2");
		builder.addStaticSpatioTextualProcessor(
				"spatiotextualcomponent1",
				SpatioTextualConstants.globalGridGranularity
						* SpatioTextualConstants.globalGridGranularity)
				//.addVolatileSpatioTextualInput("TweetsGenerator")
				.addContinuousQuerySource("TextualRangeQueryGenerator")
				.addStaticDataSource(
						"OpenStreetMap",
						"edu.purdue.cs.tornado.storage.RandomStaticDataGenerator",
						staticSourceConf)
				.addContinuousQuerySource("TextualDistanceJoinQueryGenerator");

		Config conf = new Config();
		conf.setDebug(true);
		conf.setNumWorkers(2);
		conf.setNumAckers(0);
		conf.put(Config.TOPOLOGY_DEBUG, false);

		SpatioTextualLocalCluster cluster = new SpatioTextualLocalCluster();
		cluster.submitTopology("test", conf, builder.createTopology());
		// Utils.sleep(10000);
		// cluster.killTopology("test");
		// cluster.shutdown();
	}
}
