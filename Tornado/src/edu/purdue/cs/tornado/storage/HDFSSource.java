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
package edu.purdue.cs.tornado.storage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.messages.DataObject;


public class HDFSSource extends AbstractStaticDataSource{
	private InputStreamReader HDFSReader;
	BufferedReader reader;
	String str;
	public HDFSSource(Rectangle bounds, HashMap<String, String> config,
			String sourceId,Integer selfTaskId,Integer selfTaskIdIndex) {
		super(bounds, config, sourceId, selfTaskIdIndex, selfTaskIdIndex);
		// TODO Auto-generated constructor stub
	}
	ArrayList<DataObject>storedDataObject;

	@Override
	public void prepareData() {
		/*
		storedDataObject = new ArrayList<DataObject>();
		Configuration HDFSconfig = new Configuration();
		String DataSet = config.get("OSMFilePath").toString();
		String defaultFS = config.get("defaultFS").toString();
		String Resource1 = config.get("Resource1").toString();
		String Resource2= config.get("Resource2").toString();
		
		HDFSconfig.set("fs.defaultFS",defaultFS);
		HDFSconfig.addResource(Resource1);
		HDFSconfig.addResource(Resource2);
		try {
			Path pt=new Path(DataSet);
			FileSystem fs = FileSystem.get(HDFSconfig);
			this.HDFSReader = new InputStreamReader(fs.open(pt));
			this.reader = new BufferedReader(HDFSReader);		
			while ((str = reader.readLine()) != null) {
				
				String[] words = str.split(",");
				int ID = Integer.parseInt(words[1]);
				Double x =  Double.parseDouble(words[2]);
				Double y = Double.parseDouble(words[3]);
				String txt = words[7];
				String []textlist  = txt.split(" ");
				// Samy: You should Apply Range here:
				// Based on Range_x and Range_y you should choose weather to add to Array
				// Samy: Create a spatialObject from record
				DataObject item = new DataObject();
				item.setObjectId(ID);
				item.setObjectText(new ArrayList( Arrays.asList(textlist )));
				item.setLocation(new Point(x,y));
				// Samy: Add to the array list
				storedDataObject.add(item);	
				
			}
		} catch (Exception e) {
			throw new RuntimeException("Error reading typle", e);
		} 
		try {
			this.HDFSReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	@Override
	public Boolean hasNext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataObject getNext() {
		// TODO Auto-generated method stub
		return null;
	}

}
