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

public class TestTextualRangeQueryGenerator extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector collector;
	private RandomGenerator randomGenerator;
	int i;
	private String dataSrcId;

	public TestTextualRangeQueryGenerator(String dataSrcId){
		this.dataSrcId = dataSrcId;
	}
	public void nextTuple() {
		if(i>0){
			try {
				if (SpatioTextualConstants.dataGeneratorDelay != 0)
					Thread.sleep(SpatioTextualConstants.dataGeneratorDelay);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else if (i>20)
			return;
		
		String textList="sale food love";
		
                     		
		Date date = new Date();
		this.collector.emit(new Values(
				SpatioTextualConstants.queryTextualRange,
				""+(i++),
				null,
				null, 
				0.0, 0.0,
				10000.0,
				10000.0,
				null,
				textList, 
				null,
				date.getTime(),
				dataSrcId,
				null,
				SpatioTextualConstants.addCommand,
				null,
				SpatioTextualConstants.overlaps,
				null,
				null
				
				));
//		this.collector.emit(new Values(
//				SpatioTextualConstants.queryTextualRange, ""+(i++), 2400.0, 2400.0,
//				2500.0, 2500.0, textList, date.getTime(),dataSrcId,SpatioTextualConstants.addCommand));

			
		
	}

	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		i = 0;
		this.collector = collector;
		randomGenerator = new RandomGenerator(
				SpatioTextualConstants.generatorSeed);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(
				SpatioTextualConstants.queryTypeField,
				SpatioTextualConstants.queryIdField,
				SpatioTextualConstants.focalXCoordField,
				SpatioTextualConstants.focalYCoordField,
				SpatioTextualConstants.queryXMinField,
				SpatioTextualConstants.queryYMinField,
				SpatioTextualConstants.queryXMaxField,
				SpatioTextualConstants.queryYMaxField,
				SpatioTextualConstants.kField,
				SpatioTextualConstants.queryTextField,
				SpatioTextualConstants.queryText2Field,
				SpatioTextualConstants.queryTimeStampField,
				SpatioTextualConstants.dataSrc,
				SpatioTextualConstants.dataSrc2,
				SpatioTextualConstants.queryCommand,
				SpatioTextualConstants.queryDistance,
				SpatioTextualConstants.textualPredicate,
				SpatioTextualConstants.textualPredicate2,
				SpatioTextualConstants.joinTextualPredicate));
	}
}
