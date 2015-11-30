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

import edu.purdue.cs.tornado.loadbalance.LoadBalanceMessage;

public class Control {
	//**********Constants***********************//
	//**************control messages types related to KKN (TOPK ) predicates
	public static String DROP_CONTININOUS_KNN_PREDICATE ="DROP_CONTININOUS_KNN_PREDICATE";
	public static String UPDATE_CONTININOUS_KNN_PREDICATE ="UPDATE_CONTININOUS_KNN_PREDICATE";
	public static String ANSWER_SNAPSHOT_KNN_PREDICATE ="ANSWER_SNAPSHOT_KNN_PREDICATE";
	public static String REQUEST_KNN_PREDICATE ="REQUEST_KNN_PREDICATE";
	public static String CHANGES_SET_CONTINOUS_KNN_PREDICATE ="CHANGES_SET_CONTINOUS_KNN_PREDICATE";
	public static String LOAD_BALANCE ="LOAD_BALANCE";
	
	private String controlMessageType;

	//******************************************************
	//DATA related to top-k queries 
	ArrayList<Query> queriesList;
	ArrayList<DataObject> dataObjects;
	ArrayList<ResultSetChange> resultSetChanges;
	
	//******************************************************
	//DATA related to load balance
	LoadBalanceMessage leadBalanceMessage;
	
	public LoadBalanceMessage getLeadBalanceMessage() {
		return leadBalanceMessage;
	}

	public void setLeadBalanceMessage(LoadBalanceMessage leadBalanceMessage) {
		this.leadBalanceMessage = leadBalanceMessage;
	}

	public Control (){
		this.controlMessageType=null;
		this.queriesList = null;
		this.dataObjects=null;
		this.resultSetChanges=null;
				
	}

	public String getControlMessageType() {
		return controlMessageType;
	}

	public void setControlMessageType(String controlMessageType) {
		this.controlMessageType = controlMessageType;
	}

	public ArrayList<ResultSetChange> getResultSetChanges() {
		return resultSetChanges;
	}

	public void setResultSetChanges(ArrayList<ResultSetChange> resultSetChanges) {
		this.resultSetChanges = resultSetChanges;
	}

	public ArrayList<Query> getQueriesList() {
		return queriesList;
	}

	public void setQueriesList(ArrayList<Query> queriesList) {
		this.queriesList = queriesList;
	}

	public ArrayList<DataObject> getDataObjects() {
		return dataObjects;
	}

	public void setDataObjects(ArrayList<DataObject> dataObjects) {
		this.dataObjects = dataObjects;
	}
	
	
}
