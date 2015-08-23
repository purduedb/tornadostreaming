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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.Config;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import edu.purdue.cs.tornado.SpatioTextualLocalCluster;
import edu.purdue.cs.tornado.SpatioTextualToplogyBuilder;
import edu.purdue.cs.tornado.SpatioTextualToplogySubmitter;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.output.KafakaProducerBolt;
import edu.purdue.cs.tornado.sentiment.EvalSentimentBolt;
import edu.purdue.cs.tornado.spouts.KafkaSpout;
import edu.purdue.cs.tornado.spouts.SampleUSATweetGenerator;

public class TestMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestMain.class);

	public static void main(String[] args) throws InterruptedException {
		final Properties properties = new Properties();
		try {
			LOGGER.info("******************************************************************");
			LOGGER.info("**********************Reading toplogy config******************");
			properties.load(new FileInputStream(SpatioTextualConstants.CONFIG_PROPERTIES_FILE));
		} catch (final IOException ioException) {
			//Should not occur. If it does, we cant continue. So exiting the program!
			LOGGER.error(ioException.getMessage(), ioException);
			System.exit(1);
		}

		SpatioTextualToplogyBuilder builder = new SpatioTextualToplogyBuilder();
		builder.setSpout("Tweets", new SampleUSATweetGenerator(), 1);
		//		builder.setSpout("movingobjects", new TestMovingObjectWithTextSpout(), 1);
		//		builder.setSpout("TextualKNNQueryGenerator",
		//				new TestTextualKNNQueryGenerator("movingobjects"), 1);
		builder.setSpout("TextualRangeQueryGenerator", new KafkaSpout());

		//		builder.setSpout("TextualDistanceJoinQueryGenerator",
		//				new TestSpatialJoinQueryGenerator("OpenStreetMap",
		//						"TweetsGenerator"), 1);

		//		builder.setSpout("TextualDistanceJoinQueryGenerator",
		//				new TestSpatialJoinQueryGenerator("TweetsGenerator","OpenStreetMap"
		//						), 1);

		HashMap<String, String> staticSourceConf = new HashMap<String, String>();
		staticSourceConf.put(TestPOIsStaticDataSource.POIS_PATH, properties.getProperty(TestPOIsStaticDataSource.POIS_PATH));
		builder.addStaticSpatioTextualProcessor("spatiotextualcomponent1", SpatioTextualConstants.globalGridGranularity * SpatioTextualConstants.globalGridGranularity)
				//.addVolatileSpatioTextualInput("Tweets")
				.addCleanVolatileSpatioTextualInput("Tweets")
				//				.addCurrentSpatioTextualInput("movingobjects")
				//				.addContinuousQuerySource("TextualKNNQueryGenerator")
				.addContinuousQuerySource("TextualRangeQueryGenerator").addStaticDataSource("OSM_Data", "edu.purdue.cs.tornado.test.TestPOIsStaticDataSource", staticSourceConf)
				//				.addStaticDataSource(
				//						"OSM_Data",
				//						"edu.purdue.cs.tornado.storage.POIsStaticDataSource",
				//						staticSourceConf)
				//.addContinuousQuerySource("TextualDistanceJoinQueryGenerator")
				//				.addContinuousQuerySource("test kafka")
		;

		builder.setBolt("SentimentBolt", new EvalSentimentBolt("Tweets")).shuffleGrouping("spatiotextualcomponent1", SpatioTextualConstants.Bolt_Output_STreamIDExtension);
		builder.setBolt("kafkaOutputProducer", new KafakaProducerBolt()).shuffleGrouping("SentimentBolt");

		Config conf = new Config();
		conf.setDebug(true);
		conf.setNumWorkers(2);
		conf.setNumAckers(0);
		conf.put(Config.TOPOLOGY_DEBUG, false);

		conf.put(SpatioTextualConstants.kafkaZookeeper, properties.getProperty(SpatioTextualConstants.kafkaZookeeper));
		conf.put(SpatioTextualConstants.kafkaConsumerGroup, properties.getProperty(SpatioTextualConstants.kafkaConsumerGroup));
		conf.put(SpatioTextualConstants.kafkaConsumerTopic, properties.getProperty(SpatioTextualConstants.kafkaConsumerTopic));
		conf.put(SpatioTextualConstants.kafkaProducerTopic, properties.getProperty(SpatioTextualConstants.kafkaProducerTopic));
		conf.put(SpatioTextualConstants.kafkaBootstrapServerConfig, properties.getProperty(SpatioTextualConstants.kafkaBootstrapServerConfig));
		conf.put(SpatioTextualConstants.discoDir, properties.getProperty(SpatioTextualConstants.discoDir));
		conf.put(SampleUSATweetGenerator.TWEETS_FILE_PATH, properties.getProperty(SampleUSATweetGenerator.TWEETS_FILE_PATH));
		
		String submitType = properties.getProperty(SpatioTextualConstants.stormSubmitType);
		if (submitType == null || "".equals(submitType) || SpatioTextualConstants.localCluster.equals(submitType)) {
			SpatioTextualLocalCluster cluster = new SpatioTextualLocalCluster();
			cluster.submitTopology("test", conf, builder.createTopology());
		}else{
		    conf.put(Config.NIMBUS_HOST, properties.getProperty(SpatioTextualConstants.NIMBUS_HOST));
		    conf.put(Config.NIMBUS_THRIFT_PORT,properties.getProperty(SpatioTextualConstants.NIMBUS_THRIFT_PORT));
		    conf.put(Config.STORM_ZOOKEEPER_PORT,properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_PORT));
		    conf.put(Config.STORM_ZOOKEEPER_SERVERS,properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_SERVERS));
		    conf.setNumWorkers(Integer.parseInt(properties.getProperty(SpatioTextualConstants.STORM_NUMBER_OF_WORKERS)));
		    try {
				SpatioTextualToplogySubmitter.submitTopology("test", conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				LOGGER.error(e.getMessage(), e);
				e.printStackTrace(System.err);
			} catch (InvalidTopologyException e) {
				LOGGER.error(e.getMessage(), e);
				e.printStackTrace(System.err);
				
			}
		}
		// Utils.sleep(10000);
		// cluster.killTopology("test");
		// cluster.shutdown();
	}
}
