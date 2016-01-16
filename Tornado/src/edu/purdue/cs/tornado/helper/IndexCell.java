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
package edu.purdue.cs.tornado.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class IndexCell {
	public enum IndexCellType{
		Grid, Multi_Level_Grid, Pyramid
	}
	public HashMap<String, DataObject> storedObjects;
	public HashMap<String, Integer> allDataTextInCell; //keyword, count of distinct objects
	public HashMap<String, Integer> allQueryTextInCell; //keyword, count of distinct queries
	public ArrayList<Query> storedQueries;
	public ArrayList<Query> outerEvaluatorQueries;
	
	
	public Rectangle bounds;
	public Integer dataObjectCount;
	public Integer queryCount;
	
	//this is for testing spatial objects only
	public Boolean spatialOnlyFlag;
	IndexCellType indexCellType;
	
	//used by multilevel index
	public Rectangle boundsNoBoundaries;
	public Integer level;
	
	//used by pyramid index
	public IndexCell[] children;
	public IndexCell parent;
	public Integer numberOfChildren;
	
	public Integer coverQueryCount;
	
	public IndexCell(Rectangle bounds, Boolean spatialOnlyFlag,Integer level) {
		storedObjects = null;
		allDataTextInCell =null;
		allQueryTextInCell = null;
		storedQueries = null;
		outerEvaluatorQueries = null;
		
		this.bounds = bounds;
		this.dataObjectCount = 0;
		this.queryCount=0;
		
		this.boundsNoBoundaries = new Rectangle(new Point(bounds.getMin().getX()+.0001, bounds.getMin().getY()+.0001),new Point(bounds.getMax().getX()-.0001, bounds.getMax().getY()-.0001));
		this.spatialOnlyFlag = spatialOnlyFlag;
		
		this.level =level;
		//used by multilevel index
		children = null ;
		parent = null ;
		numberOfChildren=0;
		
		this.coverQueryCount=0;
		
	}

	/**
	 * This function indexes the data object the textual content is assumed to
	 * be sorted increases the count of objects for every distinct keyword
	 * 
	 * @param dataObject
	 */
	public void addDataObject(DataObject dataObject) {
		if(storedObjects==null)storedObjects= new HashMap<String, DataObject>();
		if(allDataTextInCell==null)allDataTextInCell= new HashMap<String , Integer>();
		storedObjects.put(dataObject.getObjectId(), dataObject);
		dataObjectCount++;
		if (!spatialOnlyFlag)
			for (String s : dataObject.getObjectText()) {
				if (allDataTextInCell.containsKey(s))
					allDataTextInCell.put(s, allDataTextInCell.get(s) + 1);
				else
					allDataTextInCell.put(s, 1);

			}
	}
	public IndexCell addDataObjectRecusrive(DataObject dataObject) {
		if(storedObjects==null)storedObjects= new HashMap<String, DataObject>();
		if(allDataTextInCell==null)allDataTextInCell= new HashMap<String , Integer>();
		storedObjects.put(dataObject.getObjectId(), dataObject);
		dataObjectCount++;
		if (!spatialOnlyFlag)
			for (String s : dataObject.getObjectText()) {
				if (allDataTextInCell.containsKey(s))
					allDataTextInCell.put(s, allDataTextInCell.get(s) + 1);
				else
					allDataTextInCell.put(s, 1);

			}
		
		return this;
	}

	public void addDataObjectStatics(DataObject dataObject) {
		if(allDataTextInCell==null)allDataTextInCell= new HashMap<String , Integer>();
		dataObjectCount++;
		if (!spatialOnlyFlag)
			for (String s : dataObject.getObjectText()) {
				if (allDataTextInCell.containsKey(s))
					allDataTextInCell.put(s, allDataTextInCell.get(s) + 1);
				else
					allDataTextInCell.put(s, 1);
			}
	}

	public void addQuery(Query query) {
		if(storedQueries==null)storedQueries= new ArrayList<Query>();
		if (!storedQueries.contains(query)){
			queryCount++;
			storedQueries.add(query);
		}
	}

	public boolean cellOverlapsSpatiall(Rectangle rectangle) {
		return SpatialHelper.overlapsSpatially(bounds, rectangle);
	}

	public boolean cellOverlapsTextually(ArrayList<String> textList) {
		if (spatialOnlyFlag)
			return true;
		if (allDataTextInCell.size() != 0)
			return TextHelpers.overlapsTextually(allDataTextInCell, textList);
		return false;
	}

	public DataObject dropDataObject(String id) {
		if(storedObjects==null) return null;
		if (dataObjectCount > 0)
			dataObjectCount--;
		DataObject obj = storedObjects.get(id);
		storedObjects.remove(id);
		return obj;
	}

	public void dropQuery(Query query) {
		if(storedQueries==null)return ;
		int i = 0;
		boolean found = false;
		for (i = 0; i < storedQueries.size(); i++) {
			if (query.getSrcId().equals(storedQueries.get(i).getSrcId()) && query.getQueryId().equals(storedQueries.get(i).getQueryId())) {
				found = true;
				break;
			}
		}
		if (found){
			storedQueries.remove(i);
			queryCount--;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof IndexCell))
			return false;
		IndexCell c = (IndexCell) o;
		return (c.getBounds().equals(this.getBounds()));
	}

	public Integer estimateDataObjectCountAll(ArrayList<String> keywords) {
		Integer min = Integer.MAX_VALUE;
		for (String keyword : keywords) {
			if (!allDataTextInCell.containsKey(keyword)) {
				min = 0;
				break;
			} else if (allDataTextInCell.get(keyword) < min)
				min = allDataTextInCell.get(keyword);
		}
		return min;
	}

	/**
	 * 
	 * Estimate how many data objects contain Any of the input keywords this is
	 * estimated as the sum of objects per keyword.
	 * 
	 * @param keywords
	 */
	public Integer estimateDataObjectCountAny(ArrayList<String> keywords) {

		Integer sum = 0;
		for (String keyword : keywords) {
			if (!allDataTextInCell.containsKey(keyword)) {
				continue;
			}
			sum += allDataTextInCell.get(keyword);
		}
		return sum;
	}

	public HashMap<String, Integer> getAllDataTextInCell() {
		return allDataTextInCell;
	}

	public HashMap<String, Integer> getAllQueryTextInCell() {
		return allQueryTextInCell;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public DataObject getDataObject(String id) {
		return storedObjects.get(id);
	}

	public Integer getDataObjectCount() {
		return dataObjectCount;
	}

	public Integer getLevel() {
		return level;
	}

	public ArrayList<Query> getOuterEvaluatorQueries() {
		return outerEvaluatorQueries;
	}

	public ArrayList<Query> getQueries() {
		return storedQueries;
	}

	public Boolean getSpatialOnlyFlag() {
		return spatialOnlyFlag;
	}

	public ArrayList<DataObject> getSpatioTextualOverlappingDataObjects(Query query) {
		ArrayList<DataObject> qulifiedObjects = new ArrayList<DataObject>();
		if(storedObjects==null) return qulifiedObjects;
		Iterator<Entry<String, DataObject>> it = storedObjects.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, DataObject> entry = (Map.Entry<String, DataObject>) it.next();
			DataObject dataObject = (DataObject) entry.getValue();
			if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), query.getSpatialRange()) && TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), query.getQueryText(), query.getTextualPredicate()))
				qulifiedObjects.add((DataObject) entry.getValue());
		}
		return qulifiedObjects;
	}

	public HashMap<String, DataObject> getStoredObjects() {
		return storedObjects;
	}
	public ArrayList<DataObject> getStoredObjects(Rectangle rect, ArrayList<String> keywords, String textualPredicate) {
		
		ArrayList<DataObject> resultObjects = new ArrayList<DataObject>() ;
		for (DataObject dataObject : storedObjects.values()) 
		if(SpatialHelper.overlapsSpatially(dataObject.getLocation(),rect) && TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), keywords, textualPredicate)) {
			resultObjects.add(dataObject);
		}
		return resultObjects;
	}

	public ArrayList<Query> getStoredQueries() {
		return storedQueries;
	}

	public void setAllDataTextInCell(HashMap<String, Integer> allDataTextInCell) {
		this.allDataTextInCell = allDataTextInCell;
	}

	public void setAllQueryTextInCell(HashMap<String, Integer> allQueryTextInCell) {
		this.allQueryTextInCell = allQueryTextInCell;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public void setDataObjectCount(Integer dataObjectCount) {
		this.dataObjectCount = dataObjectCount;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public void setOuterEvaluatorQueries(ArrayList<Query> outerEvaluatorQueries) {
		this.outerEvaluatorQueries = outerEvaluatorQueries;
	}

	public void setSpatialOnlyFlag(Boolean spatialOnlyFlag) {
		this.spatialOnlyFlag = spatialOnlyFlag;
	}
	public void setStoredObjects(HashMap<String, DataObject> storedObjects) {
		this.storedObjects = storedObjects;
	}

	public void setStoredQueries(ArrayList<Query> storedQueries) {
		this.storedQueries = storedQueries;
	}
	

}
