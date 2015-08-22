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
import edu.purdue.cs.tornado.messages.DataObject;

public class TestMovingObjectWithTextSpout extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector collector;
	
	int i;
	int j;
	ArrayList<ArrayList<DataObject>> dataObjects;

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
		if(i>=dataObjects.get(j).size()){
			try {
					Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i=0;
			j=(j+1)%dataObjects.size();
		}	
		Date date = new Date();
		this.collector.emit(new Values(dataObjects.get(j).get(i).getObjectId(), 
				dataObjects.get(j).get(i).getLocation().getX(),
				dataObjects.get(j).get(i).getLocation().getY(),
				dataObjects.get(j).get(i).getOriginalText(), 
				date.getTime(),
				SpatioTextualConstants.addCommand));
		i++;
		

	}

	@Override
	public void open(Map arg0, TopologyContext arg1, SpoutOutputCollector collector) {
		this.collector = collector;
		i=0;
		

		dataObjects = new ArrayList<ArrayList<DataObject>>();
		ArrayList<DataObject> dataObjects1 = new ArrayList<DataObject>();
		ArrayList<DataObject> dataObjects2 = new ArrayList<DataObject>();
		ArrayList<DataObject> dataObjects3 = new ArrayList<DataObject>();
		ArrayList<DataObject> dataObjects4 = new ArrayList<DataObject>();
		
		String text1 = ("none");
		String text2 = ("text1 text2 text5");
		String text3 = ("text2 text4 text5");
		String text4 = ("text4 text4 text5");
		String text5 = ("text5 text4 text5");
		String text6 = ("text2 text4 text5");
		String text7 = ("text2 text4 text5");
		String text8 = ("text1 text4 text5");
		/**********************************************************************************/
		/************Test case1, get KNN for snapshot queries *****************************/
		
		dataObjects1.add(getDataObject(1000.0,6000.0,text1,0,SpatioTextualConstants.addCommand));
		dataObjects1.add(getDataObject(3001.0,7501.0,text2,1,SpatioTextualConstants.addCommand));
		dataObjects1.add(getDataObject(200.0,1502.0,text3,2,SpatioTextualConstants.addCommand));
		dataObjects1.add(getDataObject(8503.0,103.0,text4,3,SpatioTextualConstants.addCommand));
		dataObjects1.add(getDataObject(4.0,504.0,text5,4,SpatioTextualConstants.addCommand));
		/**********************************************************************************/
		/************Test case2, checkKNN expansion *****************************/
		dataObjects2.add(getDataObject(1050.0,6050.0,text2,0,SpatioTextualConstants.updateCommand));
		dataObjects2.add(getDataObject(3601.0,6501.0,text2,1,SpatioTextualConstants.updateCommand));
		dataObjects2.add(getDataObject(300.0,2502.0,text3,2,SpatioTextualConstants.updateCommand));
		dataObjects2.add(getDataObject(8403.0,123.0,text1,3,SpatioTextualConstants.updateCommand));
		dataObjects2.add(getDataObject(10.0,1004.0,text5,4,SpatioTextualConstants.updateCommand));
	
		/**********************************************************************************/
		/************Test case3, checkKNN shrinking *****************************/
		dataObjects3.add(getDataObject(1001.0,5502.0,text1,7,SpatioTextualConstants.updateCommand));

		dataObjects.add(dataObjects1);
		dataObjects.add(dataObjects2);
		dataObjects.add(dataObjects3);
		//dataObjects.add(dataObjects4);
		try {
			if (SpatioTextualConstants.dataGeneratorDelay != 0)
				Thread.sleep(SpatioTextualConstants.dataGeneratorDelay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	DataObject getDataObject(Double x,Double y, String textList,Integer id,String command){
		Date date = new Date();
		
		DataObject dataObject = new DataObject();
		dataObject.setLocation(new Point(x, y));
		dataObject.setObjectId(""+id);
		dataObject.setOriginalText(textList);
		dataObject.setTimeStamp(date.getTime());
		dataObject.setCommand(command);
		return dataObject;
	}
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(SpatioTextualConstants.objectIdField,
				SpatioTextualConstants.objectXCoordField,
				SpatioTextualConstants.objectYCoordField,
				SpatioTextualConstants.objectTextField,
				SpatioTextualConstants.timeStamp,
				SpatioTextualConstants.dataObjectCommand));
	}
}
