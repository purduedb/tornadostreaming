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

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;

public class TestSpatialJoinQueryGenerator extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector collector;
	
	int i;
	private String dataSrc;
	private String dataSrc2;

	public TestSpatialJoinQueryGenerator(String dataSrc,String dataSrc2){
		this.dataSrc = dataSrc;
		this.dataSrc2 = dataSrc2;
	}
	
	
	public void nextTuple() {

		if(i>0){
			try {
				if (SpatioTextualConstants.queryGeneratorDelay != 0)
					Thread.sleep(SpatioTextualConstants.queryGeneratorDelay);
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ArrayList<String > textList = new ArrayList<String>();
		
		textList.add("text2 text3 text4 text5");
	
		
		Date date = new Date();
//		this.collector.emit(new Values(
//				SpatioTextualConstants.queryTextualSpatialJoin, i, textList.get(0),2550.0,5050.0,
//				4500.0, 7400.0,100.0,date.getTime(),dataSrc,dataSrc2,SpatioTextualConstants.queryCommandAdd));
		this.collector.emit(new Values(
				SpatioTextualConstants.queryTextualSpatialJoin, ""+(i++), textList.get(0),4980.0,7400.0,
				5100.0, 7600.0,100.0,date.getTime(),dataSrc,dataSrc2,Command.addCommand));
		this.collector.emit(new Values(
				SpatioTextualConstants.queryTextualSpatialJoin, ""+(i++), textList.get(0),5001.0,7400.0,
				6000.0, 7499.0,100.0,date.getTime(),dataSrc,dataSrc2,Command.addCommand));
		
			
		}
	

	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		i = 0;
		this.collector = collector;
		
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
