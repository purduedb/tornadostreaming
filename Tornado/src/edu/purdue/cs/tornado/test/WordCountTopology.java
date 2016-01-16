/**
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.GlobalStreamId;
import backtype.storm.generated.Grouping;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.grouping.CustomStreamGrouping;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.task.WorkerTopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import edu.purdue.cs.tornado.SpatioTextualLocalCluster;
import edu.purdue.cs.tornado.SpatioTextualToplogySubmitter;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;

/**
 * This topology demonstrates Storm's stream groupings and multilang
 * capabilities.
 */
public class WordCountTopology {
	public static class CustomGroup implements CustomStreamGrouping,Serializable{
		/**
		 * 
		 */
		
		private static final long serialVersionUID = 1L;
		List<Integer> targetTasks;
		Integer numTasks;
		Random _rand;
		List<Integer> boltIds;

		@Override
		public void prepare(WorkerTopologyContext context, GlobalStreamId stream, List<Integer> targetTasks) {

			this.targetTasks = targetTasks;
			this.numTasks = targetTasks.size();
			_rand = new Random();
			boltIds= new ArrayList<Integer>();

		}

		@Override
		public List<Integer> chooseTasks(int taskId, List<Object> values) {
			boltIds.clear();
			boltIds.add(targetTasks.get(_rand.nextInt(numTasks)));
			return boltIds;
		}

	}

	public static class SplitSentence extends BaseRichBolt {
		Random _rand;
		Boolean reliable;
		OutputCollector collector;

		public SplitSentence() {
			// super("python", "splitsentence.py");
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}

		@Override
		public Map<String, Object> getComponentConfiguration() {
			return null;
		}

		@Override
		public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
			this.reliable = ((Long) stormConf.get(Config.TOPOLOGY_ACKER_EXECUTORS)) > 0;
			this.collector = collector;
			_rand = new Random();

		}

		@Override
		public void execute(Tuple input) {
			String sen = input.getString(0);
			String[] words = sen.split(" ");
			String word = words[_rand.nextInt(words.length)];
			if (reliable) {
				collector.emit(input, new Values(word));
				collector.ack(input);
			} else {
				collector.emit(new Values(word));
			}

		}
	}

	public static class RandomSentenceSpout extends BaseRichSpout {
		SpoutOutputCollector _collector;
		Random _rand;
		Boolean reliable;
		String[] sentences ;
		@Override
		public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
			_collector = collector;
			_rand = new Random();
			 sentences = new String[] { "the cow jumped over the moon", "an apple a day keeps the doctor away", "four score and seven years ago", "snow white and the seven dwarfs", "i am at two with nature" };
			
			this.reliable = ((Long) conf.get(Config.TOPOLOGY_ACKER_EXECUTORS)) > 0;
		}

		@Override
		public void nextTuple() {
			//	Utils.sleep(100);
			String sentence = sentences[_rand.nextInt(sentences.length)];
			if (reliable) {
				_collector.emit(new Values(sentence), 5);

			} else {
				_collector.emit(new Values(sentence));
			}
		}

		@Override
		public void ack(Object id) {
		}

		@Override
		public void fail(Object id) {
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}

	}

	public static class WordCount extends BaseRichBolt {
		Map<String, Integer> counts = new HashMap<String, Integer>();
		OutputCollector collector;
		Random _rand;
		Boolean reliable;

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word", "count"));
		}

		@Override
		public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
			this.collector = collector;
			this.reliable = ((Long) stormConf.get(Config.TOPOLOGY_ACKER_EXECUTORS)) > 0;
		}

		@Override
		public void execute(Tuple input) {
			String word = input.getString(0);
			Integer count = counts.get(word);
			if (count == null)
				count = 0;
			count++;
			counts.put(word, count);
			collector.emit(new Values(word, count));
			if (reliable)
				collector.ack(input);

		}
	}

	public static void main(String[] args) throws Exception {
		final Logger LOGGER = LoggerFactory.getLogger(WordCountTopology.class);
		final Properties properties = new Properties();
		try {
			LOGGER.info("******************************************************************");
			LOGGER.info("**********************Reading toplogy config******************");
			properties.load(new FileInputStream(SpatioTextualConstants.CLUSTER_CONFIG_PROPERTIES_FILE));
		} catch (final IOException ioException) {
			//Should not occur. If it does, we cant continue. So exiting the program!
			LOGGER.error(ioException.getMessage(), ioException);
			System.exit(1);
		}

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout", new RandomSentenceSpout(), Integer.parseInt(properties.getProperty("SPOUT_PARALLEISM").trim()));
		//builder.setBolt("split", new SplitSentence(), Integer.parseInt(properties.getProperty("ROUTING_PARALLEISM").trim())).shuffleGrouping("spout");
		builder.setBolt("split", new SplitSentence(), Integer.parseInt(properties.getProperty("ROUTING_PARALLEISM").trim())).customGrouping("spout", new CustomGroup());
		//		builder.setBolt("count", new WordCount(), Integer.parseInt(properties.getProperty("EVALUATOR_PARALLEISM").trim())).shuffleGrouping("split");

		Config conf = new Config();
		conf.setDebug(false);
		String nimbusHost = properties.getProperty(SpatioTextualConstants.NIMBUS_HOST);
		Integer nimbusPort = Integer.parseInt(properties.getProperty(SpatioTextualConstants.NIMBUS_THRIFT_PORT).trim());
		conf.setNumAckers(Integer.parseInt(properties.getProperty("STORM_NUMBER_OF_ACKERS").trim()));
		String submitType = properties.getProperty(SpatioTextualConstants.stormSubmitType);
		if (submitType == null || "".equals(submitType) || SpatioTextualConstants.localCluster.equals(submitType)) {
			LocalCluster cluster = new SpatioTextualLocalCluster();
			cluster.submitTopology("wordcount", conf, builder.createTopology());
		} else {

			conf.put(Config.JAVA_LIBRARY_PATH, "/home/tornadojars/:/usr/local/lib:/opt/local/lib:/usr/lib");
			conf.put(Config.WORKER_CHILDOPTS, "-Xmx6g");
			conf.put(Config.NIMBUS_HOST, nimbusHost);

			conf.put(Config.NIMBUS_THRIFT_PORT, nimbusPort);
			conf.put(Config.STORM_ZOOKEEPER_PORT, Integer.parseInt(properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_PORT)));
			ArrayList<String> zookeeperServers = new ArrayList(Arrays.asList(properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_SERVERS).split(",")));
			conf.put(Config.STORM_ZOOKEEPER_SERVERS, zookeeperServers);
			conf.setNumWorkers(Integer.parseInt(properties.getProperty(SpatioTextualConstants.STORM_NUMBER_OF_WORKERS).trim()));
			System.setProperty("storm.jar", properties.getProperty(SpatioTextualConstants.STORM_JAR_PATH));
			try {
				SpatioTextualToplogySubmitter.submitTopology("wordcount", conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				LOGGER.error(e.getMessage(), e);
				e.printStackTrace(System.err);
			} catch (InvalidTopologyException e) {
				LOGGER.error(e.getMessage(), e);
				e.printStackTrace(System.err);

			}
		}
	}

}