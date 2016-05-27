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
package edu.purdue.cs.tornado.messages;

import java.util.ArrayList;
import java.util.List;

import edu.purdue.cs.tornado.helper.Command;

public class CombinedTuple {
	public DataObject dataObject;
	public DataObject dataObject2;
	public ArrayList<DataObject> dataObjectList;
	public ArrayList<DataObject> dataObject2List;
	
	public Command dataObjectCommand;
	public Command dataObject2Command;
	public Query query;
	public ArrayList<Integer>  queriesIdList;
	public String queryListSrcId;
	
	public ArrayList<DataObject> getDataObjectList() {
		return dataObjectList;
	}
	public void setDataObjectList(ArrayList<DataObject> dataObjectList) {
		this.dataObjectList = dataObjectList;
	}
	public String getQueryListSrcId() {
		return queryListSrcId;
	}
	public void setQueryListSrcId(String queryListSrcId) {
		this.queryListSrcId = queryListSrcId;
	}
	public ArrayList<Integer> getQueriesIdList() {
		return queriesIdList;
	}
	public void setQueriesIdList(ArrayList<Integer> queriesIdList) {
		this.queriesIdList = queriesIdList;
	}
	public ArrayList<DataObject> getDataObject2List() {
		return dataObject2List;
	}
	public void setDataObject2List(ArrayList<DataObject> dataObject2List) {
		this.dataObject2List = dataObject2List;
	}
	public DataObject getDataObject() {
		return dataObject;
	}
	public void setDataObject(DataObject dataObject) {
		this.dataObject = dataObject;
	}
	public Query getQuery() {
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}
	public DataObject getDataObject2() {
		return dataObject2;
	}
	public void setDataObject2(DataObject dataObject2) {
		this.dataObject2 = dataObject2;
	}
	public Command getDataObjectCommand() {
		return dataObjectCommand;
	}
	public void setDataObjectCommand(Command dataObjectCommand) {
		this.dataObjectCommand = dataObjectCommand;
	}
	public Command getDataObject2Command() {
		return dataObject2Command;
	}
	public void setDataObject2Command(Command dataObject2Command) {
		this.dataObject2Command = dataObject2Command;
	}
	@Override 
	public String toString(){
		String toReturn="";
		if(query!=null &&query.getQueryId()!=null)
			toReturn +="Query id:  "+query.getQueryId();
		if(query!=null &&query.getSrcId()!=null)
			toReturn +="Query source id:  "+query.getSrcId();
		if(dataObject!=null){
			toReturn +="\n"+dataObject.toString();
		}
		if(dataObject2!=null){
			toReturn +="\n"+dataObject2.toString();
		}
		return toReturn;
	}
}
