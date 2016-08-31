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

import edu.purdue.cs.tornado.helper.DataSourceType;
import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.index.local.LocalHybridIndex;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.index.local.LocalTextIndex;
import edu.purdue.cs.tornado.index.local.LocalTextInvertedListIndex;
import edu.purdue.cs.tornado.index.local.NoLocalIndex;
import edu.purdue.cs.tornado.index.local.hybridgrid.LocalHybridGridIndex;
import edu.purdue.cs.tornado.messages.DataObject;

/**
 * This class keeps track of the data source information
 * 
 * @author Ahmed Mahmood
 *
 */
public class DataSourceInformation {
	public String dataSourceId; // component id of the data source
	public DataSourceType dataSourceType; // Query, data source
	public String persisteneceState; // data source
	public Double timeSlidingWindow; // data source
	

	public String cleanState; // data source
	public double allDataCount;

	public LocalTextIndex localTextIndex;//text inverted list
	public HashMap<String, IndexCell> objectToLocalCellIndex;//source to object id to index cell	
	public LocalHybridIndex localHybridIndex;
	public Map<Integer, Integer> dataLastBoltTasKInformation;
	public Map<Integer, ArrayList<Integer>> queryLastBoltTasKInformation;

	public Rectangle selfBounds;
	LocalIndexType localIndexType;
	public Integer fineGridGran ;
	public DataSourceInformation(Rectangle selfBounds, String dataSourceId, DataSourceType dataSourceType, String persisteneceState, String cleanState, Boolean local,LocalIndexType localIndexType,Integer fineGridGran) {

		this.dataSourceId = dataSourceId;
		this.dataSourceType = dataSourceType;
		this.persisteneceState = persisteneceState;
		this.cleanState = cleanState;
		this.selfBounds = selfBounds;
		this.localIndexType=localIndexType;
		this.fineGridGran = fineGridGran;

		if (local) {
			switch (localIndexType) {
			case NO_LOCAL_INDEX:
				this.localHybridIndex = new NoLocalIndex(selfBounds, this);
				break;
			case HYBRID_GRID:
				this.localHybridIndex = new LocalHybridGridIndex(selfBounds, this,fineGridGran);
				break;
			
			case SPATIAL_GRID:
				this.localHybridIndex = new LocalHybridGridIndex(selfBounds, this,fineGridGran,fineGridGran,true,0);
				
				break;
			
			default:
				break;
			}
			
			this.localTextIndex = new LocalTextInvertedListIndex();
			this.objectToLocalCellIndex = new HashMap<String, IndexCell>();
			this.dataLastBoltTasKInformation =null;
			this.queryLastBoltTasKInformation =null;

		} else {
			this.localHybridIndex =null;
			this.localTextIndex = null;
			this.objectToLocalCellIndex =null;
			this.dataLastBoltTasKInformation = new HashMap<Integer, Integer>();
			this.queryLastBoltTasKInformation = new HashMap<Integer, ArrayList<Integer>>();
		}

		this.allDataCount = 0;
	}

	public double getAllDataCount() {
		return allDataCount;
	}

	public String getCleanState() {
		return cleanState;
	}

	public Integer getDataEvaluatorBoltTaskID(Integer oid) {
		if (dataLastBoltTasKInformation == null)
			return null;
		if (dataLastBoltTasKInformation.containsKey(oid))
			return dataLastBoltTasKInformation.get(oid);
		else
			return null;
	}

	public Map<Integer, Integer> getDataLastBoltTasKInformation() {
		return dataLastBoltTasKInformation;
	}

	public String getDataSourceId() {
		return dataSourceId;
	}

	public DataSourceType getDataSourceType() {
		return dataSourceType;
	}

	public LocalHybridIndex getLocalHybridIndex() {
		return localHybridIndex;
	}
	public void addDataObject(DataObject dataObject){
		localHybridIndex.addDataObject(dataObject);
	}
	
	


	public LocalTextIndex getLocalTextIndex() {
		return localTextIndex;
	}

	public HashMap<String, IndexCell> getObjectToLocalCellIndex() {
		return objectToLocalCellIndex;
	}

	public String getPersisteneceState() {
		return persisteneceState;
	}

	public Map<Integer, ArrayList<Integer>> getQueryLastBoltTasKInformation() {
		return queryLastBoltTasKInformation;
	}

	public Rectangle getSelfBounds() {
		return selfBounds;
	}

	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle,ArrayList<String> keywords){
		return localHybridIndex.getOverlappingIndexCellWithData(rectangle,keywords);
	}
	public boolean isClean() {
		return SpatioTextualConstants.CLEAN.equals(cleanState);
	}

	public boolean isContinuous() {
		return SpatioTextualConstants.continuousPersistenceState.equals(persisteneceState);
	}

	public boolean isCurrent() {
		return SpatioTextualConstants.currentPersistenceState.equals(persisteneceState);
	}

	public boolean isPersistent() {
		return SpatioTextualConstants.persistentPersistenceState.equals(persisteneceState);
	}

	public boolean isStatic() {
		return SpatioTextualConstants.staticPersistenceState.equals(persisteneceState);
	}

	public boolean isVolatile() {
		return SpatioTextualConstants.volatilePersistenceState.equals(persisteneceState);
	}

	public void setAllDataCount(double allDataCount) {
		this.allDataCount = allDataCount;
	}

	public void setCleanState(String cleanState) {
		this.cleanState = cleanState;
	}

	// **************** Getters and setters *******************
	public void setDataEvaluatorBoltTaskID(Integer oid, Integer evaluatorBoltList) {
		dataLastBoltTasKInformation.put(oid, evaluatorBoltList);
	}

	public void setDataLastBoltTasKInformation(Map<Integer, Integer> dataLastBoltTasKInformation) {
		this.dataLastBoltTasKInformation = dataLastBoltTasKInformation;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public void setDataSourceType(DataSourceType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public void setLocalHybridIndex(LocalHybridIndex localHybridIndex) {
		this.localHybridIndex = localHybridIndex;
	}


	public void setLocalTextIndex(LocalTextIndex localInvertedList) {
		this.localTextIndex = localInvertedList;
	}

	public void setObjectToLocalCellIndex(HashMap<String, IndexCell> objectToLocalCellIndex) {
		this.objectToLocalCellIndex = objectToLocalCellIndex;
	}

	public void setPersisteneceState(String persisteneceState) {
		this.persisteneceState = persisteneceState;
	}

	public void setQueryLastBoltTasKInformation(Map<Integer, ArrayList<Integer>> queryLastBoltTasKInformation) {
		this.queryLastBoltTasKInformation = queryLastBoltTasKInformation;
	}

	public void setSelfBounds(Rectangle selfBounds) {
		this.selfBounds = selfBounds;
	}
	

}
