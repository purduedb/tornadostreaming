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

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.messages.DataObject;

public class TestVolatileDataSource extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector collector;
	
	int i;
	int maxCount;
	ArrayList<DataObject> dataObjects;

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
		try {
			Thread.sleep(SpatioTextualConstants.dataGeneratorDelay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(i>=maxCount)
			return ;
		Date date = new Date();
		this.collector.emit(new Values(dataObjects.get(i).getObjectId(), dataObjects.get(i).getLocation().getX(), dataObjects.get(i).getLocation().getY(), dataObjects.get(i).getObjectText().get(0), date
				.getTime(),SpatioTextualConstants.addCommand));
		i++;
		

	}

	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
		i=0;
		//maxCount =5;
		maxCount =1;
		dataObjects = new ArrayList<DataObject>();
		ArrayList<String > textList = new ArrayList<String>();
		textList.add("text1 text2 text3");
		
//		dataObjects.add(getDataObject(1000.0,5000.0,textList,1));
//		dataObjects.add(getDataObject(2601.0,5702.0,textList,1));
//		dataObjects.add(getDataObject(7505.0,6000.0,textList,1));
//		dataObjects.add(getDataObject(4040.0,7020.0,textList,1));
//		dataObjects.add(getDataObject(7001.0,5001.0,textList,1));
		dataObjects.add(getDataObject(4990.0,7490.0,textList,1));

		try {
			if (SpatioTextualConstants.dataGeneratorDelay != 0)
				Thread.sleep(SpatioTextualConstants.dataGeneratorDelay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	DataObject getDataObject(Double x,Double y, ArrayList<String > textList,Integer id){
		Date date = new Date();
		textList = TextHelpers.sortTextArrayList(textList);
		DataObject dataObject = new DataObject();
		dataObject.setLocation(new Point(x, y));
		dataObject.setObjectId(""+id);
		dataObject.setObjectText(textList);
		dataObject.setTimeStamp(date.getTime());
		return dataObject;
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



