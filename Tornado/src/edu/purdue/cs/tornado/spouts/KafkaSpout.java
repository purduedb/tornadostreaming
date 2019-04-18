
/**
 * Copyright Jul 6, 2015
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
//THis code has been adopted from code made by thamir qadah
package edu.purdue.cs.tornado.spouts;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import edu.purdue.cs.tornado.helper.JsonHelper;
import edu.purdue.cs.tornado.helper.QueryType;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.messages.Query;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaSpout extends BaseRichSpout {
	Map conf;
	TopologyContext context;
	SpoutOutputCollector collector;
	ConsumerConnector consumer;
	String zookeeper;
	String group;
	String topic;
	ConsumerIterator<byte[], byte[]> it;

	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.conf = conf;
		this.context = context;
		this.collector = collector;

		this.zookeeper = "localhost:2181";

		this.group = "queries";
		this.topic = "queries";

		consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig("localhost:2181", "queries"));
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

		it = streams.get(0).iterator();
	}

	private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", a_zookeeper);
		props.put("group.id", a_groupId);
		props.put("zookeeper.session.timeout.ms", "10000");
		props.put("zookeeper.sync.time.ms", "400");
		props.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(props);
	}

	@Override
	public void nextTuple() {
		try {

			if (it.hasNext()) {
				String queryJson = new String(it.next().message());
				Query q = JsonHelper.convertJsonStringToQuery(queryJson);
				q.setTimeStamp((new Date()).getTime());
				consumer.commitOffsets();
				if (QueryType.queryTextualRange.equals(q.getQueryType())) {
					this.collector.emit(new Values(q.getQueryType(), q.getQueryId(), null, null, q.getSpatialRange().getMin().getX(), q.getSpatialRange().getMin().getY(),
							q.getSpatialRange().getMax().getX(), q.getSpatialRange().getMax().getY(), null, TextHelpers.convertArrayListOfStringToText(q.getQueryText()), null,
							q.getTimeStamp(), q.getDataSrc(),null, null, null, q.getTextualPredicate(), null,null

					));
				}else{
//					this.collector.emit(new Values(q.getQueryType(), q.getQueryId(), q.getFocalPoint().getX(), q.getFocalPoint().getY(), q.getSpatialRange().getMin().getX(), q.getSpatialRange().getMin().getY(),
//							q.getSpatialRange().getMax().getX(), q.getSpatialRange().getMax().getY(), q.getK(), TextHelpers.convertArrayListOfStringToText(q.getQueryText()), TextHelpers.convertArrayListOfStringToText(q.getQueryText2()),
//							q.getTimeStamp(), q.getDataSrc(), q.getDataSrc2(), q.getCommand(), q.getDistance(), q.getTextualPredicate(), q.getTextualPredicate2(), q.getJoinTextualPredicate()
//
//					));
					;
				}

			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(SpatioTextualConstants.queryTypeField, SpatioTextualConstants.queryIdField, SpatioTextualConstants.focalXCoordField, SpatioTextualConstants.focalYCoordField, SpatioTextualConstants.queryXMinField,
				SpatioTextualConstants.queryYMinField, SpatioTextualConstants.queryXMaxField, SpatioTextualConstants.queryYMaxField, SpatioTextualConstants.kField, SpatioTextualConstants.queryTextField,
				SpatioTextualConstants.queryText2Field, SpatioTextualConstants.queryTimeStampField, SpatioTextualConstants.dataSrc, SpatioTextualConstants.dataSrc2, SpatioTextualConstants.queryCommand, SpatioTextualConstants.queryDistance,
				SpatioTextualConstants.textualPredicate, SpatioTextualConstants.textualPredicate2, SpatioTextualConstants.joinTextualPredicate));
	}

}