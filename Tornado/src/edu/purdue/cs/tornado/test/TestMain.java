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

import org.apache.storm.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.purdue.cs.tornado.SpatioTextualLocalCluster;
import edu.purdue.cs.tornado.SpatioTextualToplogyBuilder;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.spouts.FileSpout;
import edu.purdue.cs.tornado.spouts.QueriesFileSystemSpout;
import edu.purdue.cs.tornado.spouts.TweetsFSSpout;

public class TestMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestMain.class);

	public static void main(String[] args) throws Exception {
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
		
		Map<String, Object> tweetsSpoutConf = new HashMap<String, Object>();
		tweetsSpoutConf.put(FileSpout.FILE_PATH,properties.getProperty("TWEETS_FILE_PATH"));
		tweetsSpoutConf.put(FileSpout.FILE_SYS_TYPE,FileSpout.LFS);
		tweetsSpoutConf.put(FileSpout.EMIT_SLEEP_DURATION_NANOSEC,new Integer (1000));
		builder.setSpout("Tweets", new TweetsFSSpout(tweetsSpoutConf,100,100), 1);
				
		Map<String, Object> queriesSpoutConf = new HashMap<String, Object>();
		queriesSpoutConf.put(FileSpout.FILE_PATH,properties.getProperty("QUERIES_FILE_PATH"));
		queriesSpoutConf.put(FileSpout.FILE_SYS_TYPE,FileSpout.LFS);
		queriesSpoutConf.put(QueriesFileSystemSpout.SPATIAL_RANGE,new Double(10));
		queriesSpoutConf.put(QueriesFileSystemSpout.TOTAL_QUERY_COUNT,new Integer(1000));
		queriesSpoutConf.put(QueriesFileSystemSpout.KEYWORD_COUNT,new Integer(5));
		queriesSpoutConf.put(SpatioTextualConstants.dataSrc,"Tweets");
		queriesSpoutConf.put(SpatioTextualConstants.queryTypeField,SpatioTextualConstants.queryTextualRange);
		queriesSpoutConf.put(SpatioTextualConstants.textualPredicate,TextualPredicate.OVERlAPS);
		queriesSpoutConf.put(FileSpout.EMIT_SLEEP_DURATION_NANOSEC,new Integer (1000));
		builder.setSpout("TextualRangeQueryGenerator", new QueriesFileSystemSpout(queriesSpoutConf,100), 1);
		//builder.setSpout("Tweets", new TweetsHDFSSpout(), 1);
		//builder.setSpout("MovingObjects", new BrinkhoffSpout(), 1);
		//		builder.setSpout("movingobjects", new TestMovingObjectWithTextSpout(), 1);
		//		builder.setSpout("TextualKNNQueryGenerator",
		//				new TestTextualKNNQueryGenerator("movingobjects"), 1);
	    //	builder.setSpout("TextualRangeQueryGenerator", new KafkaSpout());

		//		builder.setSpout("TextualDistanceJoinQueryGenerator",
		//				new TestSpatialJoinQueryGenerator("OpenStreetMap",
		//						"TweetsGenerator"), 1);

		//		builder.setSpout("TextualDistanceJoinQueryGenerator",
		//				new TestSpatialJoinQueryGenerator("TweetsGenerator","OpenStreetMap"
		//						), 1);

		HashMap<String, String> staticSourceConf = new HashMap<String, String>();
		staticSourceConf.put(TestPOIsStaticDataSource.POIS_PATH, properties.getProperty(TestPOIsStaticDataSource.POIS_PATH));
	//	staticSourceConf.put(POILFSDataSource.POI_FOLDER_PATH, properties.getProperty(POILFSDataSource.POI_FOLDER_PATH));
		builder.addSpatioTextualProcessor("spatiotextualcomponent1", 36,36,GlobalIndexType.GRID,LocalIndexType.HYBRID_GRID,64)
				.addVolatileSpatioTextualInput("Tweets")
				//.addCurrentSpatioTextualInput("MovingObjects")
				//.addCleanVolatileSpatioTextualInput("Tweets")
				//				.addCurrentSpatioTextualInput("movingobjects")
				//				.addContinuousQuerySource("TextualKNNQueryGenerator")
				.addContinuousQuerySource("TextualRangeQueryGenerator")
				.addStaticDataSource("POI_Data", "edu.purdue.cs.tornado.test.TestPOIsStaticDataSource", staticSourceConf)
				//.addStaticDataSource("POI_Data", "edu.purdue.cs.tornado.storage.POILFSDataSource", staticSourceConf)
				//				.addStaticDataSource(
				//						"OSM_Data",
				//						"edu.purdue.cs.tornado.storage.POIsStaticDataSource",
				//						staticSourceConf)
				//.addContinuousQuerySource("TextualDistanceJoinQueryGenerator")
				//				.addContinuousQuerySource("test kafka")
		;

		//builder.setBolt("SentimentBolt", new EvalSentimentBolt("Tweets")).shuffleGrouping("spatiotextualcomponent1", SpatioTextualConstants.Bolt_Output_STreamIDExtension);
		//builder.setBolt("kafkaOutputProducer", new KafakaProducerBolt()).shuffleGrouping("SentimentBolt");

		
		
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
		
		
		
		SpatioTextualLocalCluster cluster = new SpatioTextualLocalCluster();
		cluster.submitTopology("test", conf, builder.createTopology());
		
		// Utils.sleep(10000);
		// cluster.killTopology("test");
		// cluster.shutdown();
	}
}
