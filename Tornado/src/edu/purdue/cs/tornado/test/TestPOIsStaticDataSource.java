/**
 * Copyright Jul 13, 2015
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.StringHelpers;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.storage.AbstractStaticDataSource;

public class TestPOIsStaticDataSource extends AbstractStaticDataSource{
	public static final String POIS_PATH = "POIS_PATH";
	private Integer count;
	private Integer maxCount;
	private ArrayList<DataObject> pois;
	
	public TestPOIsStaticDataSource(Rectangle bounds,
			Map<String, String> config, String sourceId,Integer selfTaskId,Integer selfTaskIdIndex) {
		super(bounds, config, sourceId,selfTaskId,selfTaskIdIndex);
		prepareData();
	}

	public void  prepareData() {
		pois = new ArrayList<DataObject>();
		count = 0;
		 String filePath=config.get(POIS_PATH);//"datasources/pois.csv";
		try {
			FileInputStream fstream = new FileInputStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fstream));

			String strLine;
			
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				StringTokenizer stringTokenizer = new StringTokenizer(strLine,",") ;
				
			//	String id =stringTokenizer.nextToken();
				
				
				Double lon = Double.parseDouble(stringTokenizer.nextToken()) ;
				Double lat = Double.parseDouble(stringTokenizer.nextToken()) ;
				String text = stringTokenizer.nextToken();
				Point point = SpatialHelper.convertFromLatLonToXYPoint(new LatLong(lat, lon));
				
				
				if(SpatialHelper.overlapsSpatially(point, bounds)){
					DataObject dataObject = new DataObject();
					Date date = new Date();
					// for now just building random data
					dataObject.setOriginalText(text);
					ArrayList<String> textContent = StringHelpers.transformIntoSortedArrayListOfString(text);

					
					dataObject.setLocation(point);
					dataObject.setObjectId(""+(count++));//
					;
					dataObject.setObjectText(textContent);
					dataObject.setSrcId(sourceId);
					dataObject.setTimeStamp(date.getTime());

					pois.add(dataObject);
				}
			}

			// Close the input stream
			br.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		count = 0;
	}

	public Boolean hasNext() {
		if (count < pois.size())
			return true;
		else
			return false;
	}

	public DataObject getNext() {
		if (count <pois.size()) {
			
			
			return pois.get(count++);
		} else
			return null;
	}

}
