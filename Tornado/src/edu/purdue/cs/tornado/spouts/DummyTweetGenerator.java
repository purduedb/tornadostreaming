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
package edu.purdue.cs.tornado.spouts;

import java.util.Date;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import edu.purdue.cs.tornado.helper.RandomGenerator;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;

/**
 * This is a sample of a transient spatio-textual object generator
 * 
 * @author Ahmed Mahmood
 *
 */
public class DummyTweetGenerator extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector collector;
	private RandomGenerator randomGenerator;
	int i = 0;

	public void ack(Object msgId) {
		System.out.println("OK:" + msgId);
	}

	public void close() {
	}

	public void fail(Object msgId) {
		System.out.println("FAIL:" + msgId);
	}

	@Override
	public void nextTuple() {

		String id = ""+randomGenerator
				.nextInt(SpatioTextualConstants.numMovingObjects);
		Double xCoord = randomGenerator.nextDouble(0,
				SpatioTextualConstants.xMaxRange);
		Double yCoord = randomGenerator.nextDouble(0,
				SpatioTextualConstants.yMaxRange);
		String textContent = "";
		for (int i = 0; i < SpatioTextualConstants.objectTextualContentLength-1; i++)
			textContent += SampleTextualContent.TextArr[randomGenerator
					.nextInt(SampleTextualContent.TextArr.length - 1)] + SpatioTextualConstants.textDelimiter;
		textContent += SampleTextualContent.TextArr[randomGenerator
		                        					.nextInt(SampleTextualContent.TextArr.length - 1)] ;
		
		
		Date date = new Date();

		this.collector.emit(new Values(id, xCoord, yCoord, textContent, date
				.getTime(),SpatioTextualConstants.addCommand));
		try {
			Thread.sleep(SpatioTextualConstants.dataGeneratorDelay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
		randomGenerator = new RandomGenerator(
				SpatioTextualConstants.generatorSeed);

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(SpatioTextualConstants.objectIdField,
				SpatioTextualConstants.objectXCoordField,
				SpatioTextualConstants.objectYCoordField,
				SpatioTextualConstants.objectTextField,
				SpatioTextualConstants.timeStamp,
				SpatioTextualConstants.dataObjectCommand));
	}

}
