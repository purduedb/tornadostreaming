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

import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class ResultSetChange {
	
	DataObject dataObject;
	String changeType;
	Query query;
	
	public Query getQuery() {
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}
	public static String Update = SpatioTextualConstants.updateCommand;
	public static String Add = SpatioTextualConstants.addCommand;
	public static String Remove =  SpatioTextualConstants.dropCommand;
	public DataObject getDataObject() {
		return dataObject;
	}
	public void setDataObject(DataObject dataObject) {
		this.dataObject = dataObject;
	}
	public String getChangeType() {
		return changeType;
	}
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
	public ResultSetChange(DataObject dataObject, String changeType,Query q) {
		super();
		this.dataObject = dataObject;
		this.changeType = changeType;
		this.query = q;
	}
	@Override
	public String toString(){
		return"Change type: "+changeType+"  "+ dataObject.toString()+"  "+query.toString(); 
	}

}
