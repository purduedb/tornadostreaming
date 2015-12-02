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

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.storage.AbstractStaticDataSource;

public class TestStaticDataSource  extends AbstractStaticDataSource{
	private Integer count;
	private Integer maxCount;
	private ArrayList<DataObject> dataObjects;
	public TestStaticDataSource(Rectangle bounds, Map<String, String> config,
			String sourceId,Integer selfTaskId,Integer selfTaskIdIndex) {
		super(bounds, config, sourceId, selfTaskIdIndex, selfTaskIdIndex);
		
	}

	@Override
	public void prepareData() {
		count=0;
		maxCount =5;
		dataObjects = new ArrayList<DataObject>();
		ArrayList<String > textList = new ArrayList<String>();
		textList.add("text1");
		textList.add("text2");
		textList.add("text3");
//		dataObjects.add(getDataObject(0.0,0.0,textList,1));
//		dataObjects.add(getDataObject(2600.0,5700.0,textList,1));
//		dataObjects.add(getDataObject(3000.0,6000.0,textList,1));
//		dataObjects.add(getDataObject(4000.0,7000.0,textList,1));
//		dataObjects.add(getDataObject(7000.0,5000.0,textList,1));
		dataObjects.add(getDataObject(0.0,0.0,textList,1));
		dataObjects.add(getDataObject(4998.0,7498.0,textList,1));
		dataObjects.add(getDataObject(4998.0,7502.0,textList,1));
		dataObjects.add(getDataObject(5002.0,7498.0,textList,1));
		dataObjects.add(getDataObject(5002.0,7502.0,textList,1));
	}
	DataObject getDataObject(Double x,Double y, ArrayList<String > textList,Integer id){
		Date date = new Date();
		textList = TextHelpers.sortTextArrayList(textList);
		DataObject dataObject = new DataObject();
		dataObject.setLocation(new Point(x, y));
		dataObject.setObjectId(""+id);
		dataObject.setObjectText(textList);
		dataObject.setSrcId(sourceId);
		dataObject.setTimeStamp(date.getTime());
		return dataObject;
	}
	@Override
	public Boolean hasNext() {
	//	DataObject dataObject  =dataObjects.get(count);
//		while(!SpatialHelper.overlapsSpatially(dataObject.getLocation(), bounds)&&count<maxCount)
//			count++;
		if(count<maxCount)
			return true;
		else 
			return false;
	}

	@Override
	public DataObject getNext() {
		if(count<maxCount){
			
			DataObject dataObject  =dataObjects.get(count);
			
			count++;
			return dataObject;		
		}			
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
