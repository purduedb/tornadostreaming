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
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;

public class TestTextualKNNQueryGenerator extends BaseRichSpout {
	private SpoutOutputCollector collector;
	
	int i;
	private String dataSrcId;
	public TestTextualKNNQueryGenerator(String dataSrcId){
		this.dataSrcId = dataSrcId;
	}
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		i = 0;
		this.collector = collector;
		
	}
	@Override
	public void nextTuple() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(i>0)
			return ;
		Date date = new Date();
		String textList="text2 text1 text4 text5";
		//String textList="coffee restaurant";
		this.collector.emit(new Values(
				SpatioTextualConstants.queryTextualKNN, ""+(i++), 
				1000.0, 6000.0,3, textList, date.getTime(),dataSrcId,
				//SpatioTextualConstants.semantic,
				SpatioTextualConstants.overlaps,
				SpatioTextualConstants.addCommand));
		
	}
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(SpatioTextualConstants.queryTypeField,
				SpatioTextualConstants.queryIdField,
				SpatioTextualConstants.focalXCoordField,
				SpatioTextualConstants.focalYCoordField,
				SpatioTextualConstants.kField,
				SpatioTextualConstants.queryTextField,
				SpatioTextualConstants.queryTimeStampField,
				SpatioTextualConstants.dataSrc,
				SpatioTextualConstants.textualPredicate,
				SpatioTextualConstants.queryCommand));


		
	}
}