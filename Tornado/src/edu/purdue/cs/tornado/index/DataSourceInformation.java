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
package edu.purdue.cs.tornado.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.purdue.cs.tornado.helper.SpatioTextualConstants;

/**
 * This class keeps track of the data source information
 * 
 * @author Ahmed Mahmood
 *
 */
public class DataSourceInformation {
	private String dataSourceId; // component id of the data source
	private String dataSourceType; // Query, data source
	private String persisteneceState; // data source
	private Map<String, ArrayList<EvaluatorBoltHistory>> lastBoltTasKInformation;
	
	public DataSourceInformation() {
		lastBoltTasKInformation = new HashMap<String, ArrayList<EvaluatorBoltHistory>>();
	}

	public DataSourceInformation(String dataSourceId,
			 String dataSourceType,
			String  persisteneceState) {
		this.dataSourceId = dataSourceId;
		this.dataSourceType = dataSourceType;
		this.persisteneceState = persisteneceState;
		lastBoltTasKInformation = new HashMap<String, ArrayList<EvaluatorBoltHistory>>();
	}

	// **************** Getters and setters *******************
	public void setEvaluatorBoltTaskID(String oid, ArrayList<EvaluatorBoltHistory> evaluatorBoltList) {
		lastBoltTasKInformation.put(oid, evaluatorBoltList);
	}

	public ArrayList<EvaluatorBoltHistory> getEvaluatorBoltTaskID(Integer oid) {
		if (lastBoltTasKInformation==null) return null;
		if (lastBoltTasKInformation.containsKey(oid))
			return lastBoltTasKInformation.get(oid);
		else
			return null;
	}

	

	public String getPersisteneceState() {
		return persisteneceState;
	}

	public void setPersisteneceState(String persisteneceState) {
		this.persisteneceState = persisteneceState;
	}

	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}


	public String getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}
	public boolean isPersistent(){
		return SpatioTextualConstants.persistentPersistenceState.equals(persisteneceState);
	}
	public boolean isContinuous(){
		return SpatioTextualConstants.continuousPersistenceState.equals(persisteneceState);
	}
	public boolean isStatic(){
		return SpatioTextualConstants.staticPersistenceState.equals(persisteneceState);
	}
	public boolean isVolatile(){
		return SpatioTextualConstants.volatilePersistenceState.equals(persisteneceState);
	}
	public boolean isCurrent(){
		return SpatioTextualConstants.currentPersistenceState.equals(persisteneceState);
	}
	public Map<String, ArrayList<EvaluatorBoltHistory>> getLastBoltTasKInformation() {
		
		return lastBoltTasKInformation;
	}

	public void setLastBoltTasKInformation(
			Map<String, ArrayList<EvaluatorBoltHistory>> lastBoltTasKInformation) {
		this.lastBoltTasKInformation = lastBoltTasKInformation;
	}

}
