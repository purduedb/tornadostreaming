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

import edu.purdue.cs.tornado.helper.RandomGenerator;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class TextualSpatialJoinGenerator extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector collector;
	private RandomGenerator randomGenerator;
	int i;
	private String dataSrc;
	private String dataSrc2;

	public TextualSpatialJoinGenerator(String dataSrc,String dataSrc2){
		this.dataSrc = dataSrc;
		this.dataSrc2 = dataSrc2;
	}
	
	
	public void nextTuple() {

		if (i < SpatioTextualConstants.numQueries) { // i will be the query id.
			i++;
			Double xMin = randomGenerator.nextDouble(0,
					SpatioTextualConstants.xMaxRange);
			Double yMin = randomGenerator.nextDouble(0,
					SpatioTextualConstants.yMaxRange);

			Double width = randomGenerator.nextDouble(0,
					SpatioTextualConstants.queryMaxWidth);
			Double xMax = xMin + width;
			if (xMax > SpatioTextualConstants.xMaxRange) {
				xMax = SpatioTextualConstants.xMaxRange;
			}

			Double height = randomGenerator.nextDouble(0,
					SpatioTextualConstants.queryMaxHeight);
			
			Double distance = SpatioTextualConstants.queryDistanceJoin;
			
			
			Double yMax = yMin + height;
			if (yMax > SpatioTextualConstants.yMaxRange) {
				yMax = SpatioTextualConstants.yMaxRange;
			}
			String textContent = "";
			for (int i = 0; i < SpatioTextualConstants.queryTextualContentLength-1; i++)
				textContent += SampleTextualContent.TextArr[randomGenerator
						.nextInt(SampleTextualContent.TextArr.length - 1)]
						+ SpatioTextualConstants.textDelimiter;
			textContent += SampleTextualContent.TextArr[randomGenerator
					.nextInt(SampleTextualContent.TextArr.length - 1)];
             		
			Date date = new Date();
			this.collector.emit(new Values(
					SpatioTextualConstants.queryTextualSpatialJoin, i, textContent,xMin,
					yMin, xMax, yMax,distance,date.getTime(),dataSrc,dataSrc2,SpatioTextualConstants.addCommand));

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
		i = 0;
		this.collector = collector;
		randomGenerator = new RandomGenerator(
				SpatioTextualConstants.generatorSeed);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(SpatioTextualConstants.queryTypeField,
				SpatioTextualConstants.queryIdField,
				SpatioTextualConstants.queryTextField,
				SpatioTextualConstants.queryXMinField,
				SpatioTextualConstants.queryYMinField,
				SpatioTextualConstants.queryXMaxField,
				SpatioTextualConstants.queryYMaxField,
				SpatioTextualConstants.queryDistance,
				SpatioTextualConstants.queryTimeStampField,
				SpatioTextualConstants.dataSrc,
				SpatioTextualConstants.dataSrc2,	
				SpatioTextualConstants.queryCommand));
	}
}
