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

import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.index.local.LocalHybridGridIndex;
import edu.purdue.cs.tornado.index.local.LocalHybridIndex;
import edu.purdue.cs.tornado.index.local.LocalHybridMultiGridIndex;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.index.local.LocalSpatialGridIndex;
import edu.purdue.cs.tornado.index.local.LocalSpatialIndex;
import edu.purdue.cs.tornado.index.local.LocalTextIndex;
import edu.purdue.cs.tornado.index.local.LocalTextInvertedListIndex;
import edu.purdue.cs.tornado.index.local.NoLocalIndex;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

/**
 * This class keeps track of the data source information
 * 
 * @author Ahmed Mahmood
 *
 */
public class DataSourceInformation {
	public String dataSourceId; // component id of the data source
	public String dataSourceType; // Query, data source
	public String persisteneceState; // data source
	public Double timeSlidingWindow; // data source
	
	public Double dropTime;

	public String cleanState; // data source
	public double allDataCount;

	public LocalTextIndex localTextIndex;//text inverted list
	public HashMap<String, IndexCell> objectToLocalCellIndex;//source to object id to index cell	
	public LocalHybridIndex localHybridIndex1;
	public LocalHybridIndex localHybridIndex2;
	public LocalHybridIndex queryIndex;
	public LocalSpatialIndex localSpatialIndex;
	public Map<String, Integer> dataLastBoltTasKInformation;
	public Map<String, ArrayList<Integer>> queryLastBoltTasKInformation;

	public Rectangle selfBounds;
	LocalIndexType localIndexType;
	public DataSourceInformation(Rectangle selfBounds, String dataSourceId, String dataSourceType, String persisteneceState, String cleanState, Boolean local,LocalIndexType localIndexType) {

		this.dataSourceId = dataSourceId;
		this.dataSourceType = dataSourceType;
		this.persisteneceState = persisteneceState;
		this.cleanState = cleanState;
		this.selfBounds = selfBounds;
		this.localIndexType=localIndexType;

		if (local) {
			switch (localIndexType) {
			case NO_LOCAL_INDEX:
				this.localHybridIndex1 = new NoLocalIndex(selfBounds, this);
				this.localHybridIndex2 = new NoLocalIndex(selfBounds, this);
				this.queryIndex 	   = new NoLocalIndex(selfBounds, this);
				break;
			case HYBRID_GRID:
				this.localHybridIndex1 = new LocalHybridGridIndex(selfBounds, this);
				this.localHybridIndex2 = new LocalHybridGridIndex(selfBounds, this);
				this.queryIndex 	   = new LocalHybridGridIndex(selfBounds, this);
				break;
			case HYBRID_MULTI_LEVEL_GRID:
				this.localHybridIndex1 = new LocalHybridMultiGridIndex(selfBounds, this);
				this.localHybridIndex2 = new LocalHybridMultiGridIndex(selfBounds, this);
				this.queryIndex 	   = new LocalHybridMultiGridIndex(selfBounds, this);
				
				break;
			case SPATIAL_GRID:
				this.localHybridIndex1 = new LocalHybridGridIndex(selfBounds, this,SpatioTextualConstants.fineGridGranularityX,SpatioTextualConstants.fineGridGranularityY,true,0);
				this.localHybridIndex2 = new LocalHybridGridIndex(selfBounds, this,SpatioTextualConstants.fineGridGranularityX,SpatioTextualConstants.fineGridGranularityY,true,0);
				this.queryIndex 	   = new LocalHybridGridIndex(selfBounds, this,SpatioTextualConstants.fineGridGranularityX,SpatioTextualConstants.fineGridGranularityY,true,0);
				
				break;
			case SPATIAL_MULTI_LEVEL_GRID:
				this.localHybridIndex1 = new LocalHybridMultiGridIndex(selfBounds, this,SpatioTextualConstants.fineGridGranularityX,SpatioTextualConstants.fineGridGranularityY,true);
				this.localHybridIndex2 = new LocalHybridMultiGridIndex(selfBounds, this,SpatioTextualConstants.fineGridGranularityX,SpatioTextualConstants.fineGridGranularityY,true);
				this.queryIndex 	   = new LocalHybridMultiGridIndex(selfBounds, this,SpatioTextualConstants.fineGridGranularityX,SpatioTextualConstants.fineGridGranularityY,true);
				
				break;
			default:
				break;
			}
			
			this.localTextIndex = new LocalTextInvertedListIndex();
			this.localSpatialIndex = new LocalSpatialGridIndex();
			this.objectToLocalCellIndex = new HashMap<String, IndexCell>();
			this.dataLastBoltTasKInformation =null;
			this.queryLastBoltTasKInformation =null;

		} else {
			this.localHybridIndex1 =null;
			this.localTextIndex = null;
			this.localSpatialIndex = null;
			this.objectToLocalCellIndex =null;
			this.dataLastBoltTasKInformation = new HashMap<String, Integer>();
			this.queryLastBoltTasKInformation = new HashMap<String, ArrayList<Integer>>();
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

	public Map<String, Integer> getDataLastBoltTasKInformation() {
		return dataLastBoltTasKInformation;
	}

	public String getDataSourceId() {
		return dataSourceId;
	}

	public String getDataSourceType() {
		return dataSourceType;
	}

	public LocalHybridIndex getLocalHybridIndex() {
		return localHybridIndex1;
	}
	public void addDataObject(DataObject dataObject){
		checkWindowStats(dataObject);
		localHybridIndex1.addDataObject(dataObject);
		
	}
	public void addContinousQuery(Query query){
		queryIndex.addContinousQuery(query);
	}
	/**
	 * This function checks if the window of an index has expired, if yes,
	 * remove and index and add a new one
	 * 
	 * @param dataObject
	 * @return
	 */
	public Boolean checkWindowStats(DataObject dataObject) {
		if(dataObject.getTimeStamp()>=this.dropTime){
			this.dropTime+=timeSlidingWindow;
			this.localHybridIndex2 = this.localHybridIndex1;
			switch (localIndexType) {
			case NO_LOCAL_INDEX:
				this.localHybridIndex1 = new NoLocalIndex(selfBounds, this);
				break;
			case HYBRID_GRID:
				this.localHybridIndex1 = new LocalHybridGridIndex(selfBounds, this);
				break;
			case HYBRID_MULTI_LEVEL_GRID:
				this.localHybridIndex1 = new LocalHybridMultiGridIndex(selfBounds, this);
				break;
			case SPATIAL_GRID:
				this.localHybridIndex1 = new LocalHybridGridIndex(selfBounds, this,SpatioTextualConstants.fineGridGranularityX,SpatioTextualConstants.fineGridGranularityY,true,0);
				break;
			case SPATIAL_MULTI_LEVEL_GRID:
				this.localHybridIndex1 = new LocalHybridMultiGridIndex(selfBounds, this,SpatioTextualConstants.fineGridGranularityX,SpatioTextualConstants.fineGridGranularityY,true);
				break;
			default:
				break;
			}
			return true;
		}
		return false;
	}
	public LocalSpatialIndex getLocalSpatialIndex() {
		return localSpatialIndex;
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

	public Map<String, ArrayList<Integer>> getQueryLastBoltTasKInformation() {
		return queryLastBoltTasKInformation;
	}

	public Rectangle getSelfBounds() {
		return selfBounds;
	}

	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle,ArrayList<String> keywords){
		return localHybridIndex1.getOverlappingIndexCellWithData(rectangle,keywords);
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
	public void setDataEvaluatorBoltTaskID(String oid, Integer evaluatorBoltList) {
		dataLastBoltTasKInformation.put(oid, evaluatorBoltList);
	}

	public void setDataLastBoltTasKInformation(Map<String, Integer> dataLastBoltTasKInformation) {
		this.dataLastBoltTasKInformation = dataLastBoltTasKInformation;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public void setLocalHybridIndex(LocalHybridIndex localHybridIndex) {
		this.localHybridIndex1 = localHybridIndex;
	}

	public void setLocalSpatialIndex(LocalSpatialIndex localSpatialIndex) {
		this.localSpatialIndex = localSpatialIndex;
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

	public void setQueryLastBoltTasKInformation(Map<String, ArrayList<Integer>> queryLastBoltTasKInformation) {
		this.queryLastBoltTasKInformation = queryLastBoltTasKInformation;
	}

	public void setSelfBounds(Rectangle selfBounds) {
		this.selfBounds = selfBounds;
	}

}
