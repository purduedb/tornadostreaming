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

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.RandomGenerator;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;

public class TextualKNNQueryGenerator extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector collector;
	private RandomGenerator randomGenerator;
	int i ;
	private String dataSrcId;
	private Integer maxK;
	Integer queryTextualContentLength;
	public TextualKNNQueryGenerator(String dataSrcId, Integer maxK,Integer queryTextualContentLength) {
		this.queryTextualContentLength=queryTextualContentLength;
		this.maxK =maxK;
		this.dataSrcId = dataSrcId;
	}
	public void ack(Object msgId) {
		System.out.println("OK:" + msgId);
	}

	public void close() {
	}

	public void fail(Object msgId) {
		System.out.println("FAIL:" + msgId);
	}

	public void nextTuple() {
		// Generate queries at random.
		if (i < SpatioTextualConstants.numQueries) { // i will be the query
			// id.
			i++;
			Double xCoord = randomGenerator.nextDouble(0,SpatioTextualConstants.xMaxRange);
			Double yCoord = randomGenerator.nextDouble(0,SpatioTextualConstants.yMaxRange);

			int k = randomGenerator.nextInt(maxK) + 1; 
			String textContent = "";
			for (int i = 0; i < queryTextualContentLength-1; i++)
				textContent += SampleTextualContent.TextArr[randomGenerator
						.nextInt(SampleTextualContent.TextArr.length - 1)]
						+ SpatioTextualConstants.textDelimiter;
			textContent += SampleTextualContent.TextArr[randomGenerator
					.nextInt(SampleTextualContent.TextArr.length - 1)];
                   		
			Date date = new Date();
			this.collector.emit(new Values(SpatioTextualConstants.queryTextualKNN,""+i, xCoord, yCoord, k,textContent,date.getTime(),dataSrcId,Command.addCommand));

			try {
				if (SpatioTextualConstants.queryGeneratorDelay != 0)
					Thread.sleep(SpatioTextualConstants.queryGeneratorDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		i=0;
		this.collector = collector;
		randomGenerator = new RandomGenerator(SpatioTextualConstants.generatorSeed);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(
				SpatioTextualConstants.queryTypeField,
				SpatioTextualConstants.queryIdField,
				SpatioTextualConstants.focalXCoordField,
				SpatioTextualConstants.focalYCoordField,
				SpatioTextualConstants.kField,
				SpatioTextualConstants.queryTextField,
				SpatioTextualConstants.timeStamp,
				SpatioTextualConstants.dataSrc,
				SpatioTextualConstants.queryCommand));
	}
}
