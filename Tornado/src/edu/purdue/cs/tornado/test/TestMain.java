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
package edu.purdue.cs.tornado.test;

import java.util.HashMap;

import backtype.storm.Config;
import edu.purdue.cs.tornado.SpatioTextualLocalCluster;
import edu.purdue.cs.tornado.SpatioTextualToplogyBuilder;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.output.KafakaProducerBolt;
import edu.purdue.cs.tornado.sentiment.EvalSentimentBolt;
import edu.purdue.cs.tornado.spouts.KafkaSpout;
import edu.purdue.cs.tornado.spouts.SampleUSATweetGenerator;

public class TestMain {

	public static void main(String[] args) throws InterruptedException {
		SpatioTextualToplogyBuilder builder = new SpatioTextualToplogyBuilder();
	    builder.setSpout("Tweets", new SampleUSATweetGenerator(), 1);
//		builder.setSpout("movingobjects", new TestMovingObjectWithTextSpout(), 1);
//		builder.setSpout("TextualKNNQueryGenerator",
//				new TestTextualKNNQueryGenerator("movingobjects"), 1);
		builder.setSpout("TextualRangeQueryGenerator",new KafkaSpout());
		
		
//		builder.setSpout("TextualDistanceJoinQueryGenerator",
//				new TestSpatialJoinQueryGenerator("OpenStreetMap",
//						"TweetsGenerator"), 1);
		
//		builder.setSpout("TextualDistanceJoinQueryGenerator",
//				new TestSpatialJoinQueryGenerator("TweetsGenerator","OpenStreetMap"
//						), 1);

		HashMap<String, String> staticSourceConf = new HashMap<String, String>();
		
		builder.addStaticSpatioTextualProcessor(
				"spatiotextualcomponent1",
				SpatioTextualConstants.globalGridGranularity
						* SpatioTextualConstants.globalGridGranularity)
		.addVolatileSpatioTextualInput("Tweets")
//				.addCurrentSpatioTextualInput("movingobjects")
//				.addContinuousQuerySource("TextualKNNQueryGenerator")
				.addContinuousQuerySource("TextualRangeQueryGenerator")
				.addStaticDataSource(
						"OSM_Data",
						"edu.purdue.cs.tornado.test.TestPOIsStaticDataSource",
						staticSourceConf)
//				.addStaticDataSource(
//						"OSM_Data",
//						"edu.purdue.cs.tornado.storage.POIsStaticDataSource",
//						staticSourceConf)
				//.addContinuousQuerySource("TextualDistanceJoinQueryGenerator")
//				.addContinuousQuerySource("test kafka")
						;
		
		builder.setBolt("SentimentBolt", new EvalSentimentBolt("Tweets")).shuffleGrouping("spatiotextualcomponent1",SpatioTextualConstants.Bolt_Output_STreamIDExtension);
		builder.setBolt("kafkaOutputProducer", new KafakaProducerBolt()).shuffleGrouping("SentimentBolt");
		
		
		Config conf = new Config();
		conf.setDebug(true);
		conf.setNumWorkers(2);
		conf.setNumAckers(0);
		conf.put(Config.TOPOLOGY_DEBUG, false);
		
		conf.put(SpatioTextualConstants.kafkaZookeeper, "localhost:2181");
		conf.put(SpatioTextualConstants.kafkaConsumerGroup, "queryprocExample");
		conf.put(SpatioTextualConstants.kafkaConsumerTopic, "queries");
		conf.put(SpatioTextualConstants.kafkaProducerTopic, "output");
		conf.put(SpatioTextualConstants.kafkaBootstrapServerConfig, "localhost:9092");
		conf.put(SpatioTextualConstants.discoDir,"/home/ahmed/Downloads/enwiki-20130403-word2vec-lm-mwl-lc-sim/");
		SpatioTextualLocalCluster cluster = new SpatioTextualLocalCluster();
		cluster.submitTopology("test", conf, builder.createTopology());
		// Utils.sleep(10000);
		// cluster.killTopology("test");
		// cluster.shutdown();
	}
}
