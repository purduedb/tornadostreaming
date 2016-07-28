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
import java.util.HashSet;

import com.sun.tools.javac.code.Attribute.Array;

import edu.purdue.cs.tornado.helper.IndexCell;
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
	public static String CORRECTNESS ="CORRECTNESS";
	public static String TEXT_SUMMERY ="textsummery";
	public static String INDEX_TEXT_SUMMERY ="indextextsummery";
	private String controlMessageType;

	//******************************************************
	//DATA related to top-k queries 
	public ArrayList<Query> queriesList;
	public ArrayList<DataObject> dataObjects;
	public ArrayList<ResultSetChange> resultSetChanges;
	public ArrayList<IndexCell> indexCells;

	public Query singleQuery;
	public DataObject singlDataObject;
	public ArrayList<Integer> textSummeryTaskIdList;
	


	public ArrayList<String> forwardTextSummeryFromGlobalIndex;
	public Integer forwardTextIndexTaskIndex;
	public HashSet<String> textSummery;
	public IndexCell indexCell;
	public Long textSummaryTimeStamp;
	public HashSet<String> getTextSummery() {
		return textSummery;
	}

	public Integer getForwardTextIndexTaskIndex() {
		return forwardTextIndexTaskIndex;
	}

	public void setForwardTextIndexTaskIndex(Integer forwardTextIndexTaskIndex) {
		this.forwardTextIndexTaskIndex = forwardTextIndexTaskIndex;
	}

	public void setTextSummery(HashSet<String> textSummery) {
		this.textSummery = textSummery;
	}


	public ArrayList<String> getForwardTextSummeryFromGlobalIndex() {
		return forwardTextSummeryFromGlobalIndex;
	}

	public void setForwardTextSummeryFromGlobalIndex(ArrayList<String> forwardTextSummeryFromGlobalIndex) {
		this.forwardTextSummeryFromGlobalIndex = forwardTextSummeryFromGlobalIndex;
	}


	public Long getTextSummaryTimeStamp() {
		return textSummaryTimeStamp;
	}

	public void setTextSummaryTimeStamp(Long textSummaryTimeStamp) {
		this.textSummaryTimeStamp = textSummaryTimeStamp;
	}

	public ArrayList<Integer> getTextSummeryTaskIdList() {
		return textSummeryTaskIdList;
	}

	public void setTextSummeryTaskIdList(ArrayList<Integer> textSummeryTaskIndexList) {
		this.textSummeryTaskIdList = textSummeryTaskIndexList;
	}

	public Query getSingleQuery() {
		return singleQuery;
	}

	public void setSingleQuery(Query singleQuery) {
		this.singleQuery = singleQuery;
	}

	public DataObject getSinglDataObject() {
		return singlDataObject;
	}

	public void setSinglDataObject(DataObject singlDataObject) {
		this.singlDataObject = singlDataObject;
	}

	public String srcId;
	
	//******************************************************
	//DATA related to load balance
	LoadBalanceMessage leadBalanceMessage;
	
	public LoadBalanceMessage getLeadBalanceMessage() {
		return leadBalanceMessage;
	}

	public void setLeadBalanceMessage(LoadBalanceMessage leadBalanceMessage) {
		this.leadBalanceMessage = leadBalanceMessage;
	}
	public IndexCell getIndexCell() {
		return indexCell;
	}

	public void setIndexCell(IndexCell indexCell) {
		this.indexCell = indexCell;
	}
	public ArrayList<IndexCell> getIndexCells() {
		return indexCells;
	}

	public void setIndexCells(ArrayList<IndexCell> indexCells) {
		this.indexCells = indexCells;
	}

	public String getSrcId() {
		return srcId;
	}

	public void setSrcId(String srcId) {
		this.srcId = srcId;
	}

	public Control (){
		this.controlMessageType=null;
		this.queriesList = null;
		this.dataObjects=null;
		this.resultSetChanges=null;
		this.indexCells=null;
		this.srcId=null;
		this.indexCell =null;
		this.textSummery=null;
		this.textSummeryTaskIdList=null;
		this.singleQuery=null;
		this.singlDataObject=null;
		this.forwardTextIndexTaskIndex=null;
		this.forwardTextSummeryFromGlobalIndex=null;
				
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
