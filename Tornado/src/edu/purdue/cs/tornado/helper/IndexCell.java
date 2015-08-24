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
	
	HashMap<String,DataObject> storedObjects ;
	HashMap<String,Integer> allDataTextInCell; //keyword, count of distinct objects
	HashMap<String,Integer> allQueryTextInCell; //keyword, count of distinct queries
	Rectangle bounds ;
	ArrayList<Query> storedQueries;
	
	ArrayList<Query> outerEvaluatorQueries;
	
	public ArrayList<Query> getOuterEvaluatorQueries() {
		return outerEvaluatorQueries;
	}
	public void setOuterEvaluatorQueries(ArrayList<Query> outerEvaluatorQueries) {
		this.outerEvaluatorQueries = outerEvaluatorQueries;
	}
	public IndexCell(Rectangle bounds){
		storedObjects = new HashMap<String, DataObject>();
		allDataTextInCell = new HashMap<String,Integer>(); 
		allQueryTextInCell  = new HashMap<String, Integer>();
		storedQueries = new ArrayList<Query>();
		outerEvaluatorQueries = new ArrayList<Query>();
		this.bounds = bounds;
	}
	public HashMap<String, DataObject> getStoredObjects() {
		return storedObjects;
	}
	public void setStoredObjects(HashMap<String, DataObject> storedObjects) {
		this.storedObjects = storedObjects;
	}
	public HashMap<String, Integer> getAllDataTextInCell() {
		return allDataTextInCell;
	}
	public void setAllDataTextInCell(HashMap<String, Integer> allDataTextInCell) {
		this.allDataTextInCell = allDataTextInCell;
	}
	public HashMap<String, Integer> getAllQueryTextInCell() {
		return allQueryTextInCell;
	}
	public void setAllQueryTextInCell(HashMap<String, Integer> allQueryTextInCell) {
		this.allQueryTextInCell = allQueryTextInCell;
	}
	public Rectangle getBounds() {
		return bounds;
	}
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	public ArrayList<Query> getStoredQueries() {
		return storedQueries;
	}
	public void setStoredQueries(ArrayList<Query> storedQueries) {
		this.storedQueries = storedQueries;
	}
	/**
	 * This function indexes the data object 
	 * the textual content is assumed to be sorted 
	 * increases the count of objects for every distinct keyword 
	 * @param dataObject
	 */
	public void addDataObject (DataObject dataObject){
		storedObjects.put(dataObject.getObjectId(), dataObject);
		String prev="";
		for(String s:dataObject.getObjectText()){
			if(!s.equals(prev)){
				prev=s;
				if(allDataTextInCell.containsKey(s))
					allDataTextInCell.put(s, allDataTextInCell.get(s)+1);
				else
					allDataTextInCell.put(s,1);
			}
		}
		
	}
	public DataObject getDataObject(String id){
		return storedObjects.get(id);
	}
	public DataObject dropDataObject(String id){
		DataObject obj= storedObjects.get(id);
		storedObjects.remove(id);
		return obj;
	}
	public void addQuery(Query query){
		if(!storedQueries.contains(query))
		storedQueries.add(query);
	}
	public void dropQuery(Query query){
		int i=0;
		boolean found = false;
		for(i =0;i<storedQueries.size();i++ ){
			if(query.getSrcId().equals(storedQueries.get(i).getSrcId())&&
					query.getQueryId().equals(storedQueries.get(i).getQueryId())){
				found =true;
				break;
			}
		}
		if(found)
			storedQueries.remove(i);
	}
	public ArrayList<DataObject> getSpatioTextualOverlappingDataObjects (Query query){
		ArrayList<DataObject> qulifiedObjects = new ArrayList<DataObject>();
		Iterator<Entry<String, DataObject>> it = storedObjects.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, DataObject> entry = (Map.Entry<String, DataObject>)it.next();
	        DataObject dataObject = (DataObject)entry.getValue();
	       if(SpatialHelper.overlapsSpatially(dataObject.getLocation(),query.getSpatialRange())&&
	    		   StringHelpers.evaluateTextualPredicate(dataObject.getObjectText(),query.getQueryText(), query.getTextualPredicate()))
	    	   qulifiedObjects.add((DataObject)entry.getValue());
	    }
		return qulifiedObjects;
	}
	public boolean cellOverlapsSpatiall(Rectangle rectangle){
		return SpatialHelper.overlapsSpatially(bounds, rectangle);
	}
	public boolean cellOverlapsTextually(ArrayList<String> textList){
		if(allDataTextInCell.size()!=0)
		return StringHelpers.overlapsTextually(allDataTextInCell, textList);
		return false;
	}
	public ArrayList<Query>getQueries(){
		return storedQueries;
	}
	@Override 
	public boolean equals(Object o){
		  if (o == this) 
	            return true;
	        if (!(o instanceof IndexCell)) 
	            return false;
	        IndexCell c = (IndexCell) o;
	        return (c.getBounds().equals(this.getBounds())  		);
	}
	
}
