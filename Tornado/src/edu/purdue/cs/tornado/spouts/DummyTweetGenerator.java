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

import org.apache.storm.Config;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.RandomGenerator;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.messages.DataObject;

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
	private Integer selfTaskId;
	private Boolean reliable;
	Integer objectTextualContentLength;
	public void ack(Object msgId) {
		//System.out.println("OK:" + msgId);
	}
	public DummyTweetGenerator(Integer objectTextualContentLength){
		this.objectTextualContentLength=objectTextualContentLength;
	}

	public void close() {
	}

	public void fail(Object msgId) {
		//System.out.println("FAIL:" + msgId);
	}

	@Override
	public void nextTuple() {
		if(i>Integer.MAX_VALUE)
			i=0;
		Integer id = randomGenerator
				.nextInt(SpatioTextualConstants.numMovingObjects);
		Double xCoord = randomGenerator.nextDouble(0,
				SpatioTextualConstants.xMaxRange);
		Double yCoord = randomGenerator.nextDouble(0,
				SpatioTextualConstants.yMaxRange);
		String textContent = "";
		for (int i = 0; i < objectTextualContentLength-1; i++)
			textContent += SampleTextualContent.TextArr[randomGenerator
					.nextInt(SampleTextualContent.TextArr.length - 1)] + SpatioTextualConstants.textDelimiter;
		textContent += SampleTextualContent.TextArr[randomGenerator
		                        					.nextInt(SampleTextualContent.TextArr.length - 1)] ;
		
		
		Date date = new Date();


		DataObject dataObject= new DataObject(id, new Point(xCoord, yCoord), textContent, date
				.getTime(), Command.addCommand);
	///	for(int j=0;j<1000;j++)
		if (reliable)
			this.collector.emit(new Values(id,dataObject), "" + selfTaskId + "_" + (i++));
		else	this.collector.emit(new Values(id,dataObject));
	}

	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
		this.selfTaskId =context.getThisTaskId();
		randomGenerator = new RandomGenerator(
				SpatioTextualConstants.generatorSeed);
		this.reliable =((Long)conf.get(Config.TOPOLOGY_ACKER_EXECUTORS))>0;

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
//		declarer.declare(new Fields(SpatioTextualConstants.objectIdField,
//				SpatioTextualConstants.objectXCoordField,
//				SpatioTextualConstants.objectYCoordField,
//				SpatioTextualConstants.objectTextField,
//				SpatioTextualConstants.timeStamp,
//				SpatioTextualConstants.dataObjectCommand));
		declarer.declare(new Fields(SpatioTextualConstants.objectIdField,SpatioTextualConstants.dataObject));
	}

}