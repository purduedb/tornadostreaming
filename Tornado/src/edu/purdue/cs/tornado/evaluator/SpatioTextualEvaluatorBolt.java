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
package edu.purdue.cs.tornado.evaluator;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import edu.purdue.cs.tornado.cleaning.Deduplication;
import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.StringHelpers;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.index.GlobalGridIndex;
import edu.purdue.cs.tornado.index.GlobalIndexKNNIterator;
import edu.purdue.cs.tornado.index.LocalGridIndex;
import edu.purdue.cs.tornado.index.LocalIndexKNNIterator;
import edu.purdue.cs.tornado.messages.Control;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.DataObjectList;
import edu.purdue.cs.tornado.messages.OutputTuple;
import edu.purdue.cs.tornado.messages.Query;
import edu.purdue.cs.tornado.messages.ResultSetChange;
import edu.purdue.cs.tornado.storage.AbstractStaticDataSource;

/**
 * This class is for the indexing of streamed data
 * 
 * @author Ahmed Mahmood
 *
 */
public class SpatioTextualEvaluatorBolt extends BaseRichBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String id; //given spatiotextial component id
	private String selfComponentId;
	private Integer selfTaskId;
	private Integer selfTaskIndex;
	//*******************************************************************************
	//local index attributes 
	private Rectangle selfBounds;
	private HashMap<String, LocalGridIndex> localDataSpatioTextualIndex;//source to spatial index cells
	private HashMap<String, HashMap<String, IndexCell>> objectToLocalCellIndex; //source to object id to index cell
	private HashMap<String, HashMap<String, ArrayList<DataObject>>> textToObjectInvertedlist; //source to keyword to list objectIds
	private HashMap<String, HashMap<String, Query>> queryInformationHashMap; //source to query id to index cellqueyr

	//outer KNN queries, these are KNN queries that come from other evaluators and need to 
	//reevaluated for incomming data
	private HashMap<String, HashMap<String, Query>> externalKNNMap; //source to query id to index cellqueyr
	//*******************************************************************************************************
	private HashMap<String,Deduplication> cleaningMap; //source to query id to index cellqueyr

	//**************** Evaluator bolts attributes **********************
	private Integer numberOfEvaluatorTasks;
	private List<Integer> evaluatorBoltTasks; //this keeps track of the evaluator bolts ids 
	private HashMap<String, DataSourceInformation> sourcesInformations; //this keeps track of the type of every input source 

	//******************Global Grid  parameters ********************************
	IndexCellCoordinates globalIndexSelfcoordinates;
	GlobalGridIndex globalGridIndex;

	//*******************Storm specific attributes *********************
	private Map stormConf; //configuration
	private TopologyContext context; //storm context
	private OutputCollector collector;

	public SpatioTextualEvaluatorBolt(String id) {
		this.id = id;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		try {
			this.context = context;
			this.collector = collector;
			this.stormConf = stormConf;
			this.evaluatorBoltTasks = context.getComponentTasks(id);
			//**************************************************************************************
			//preparing local and global indexes
			prepareLocalAndGlobalIndexes();
			//**************************************************************************************
			//preparing data sources information 
			prepareDataAndQuerySourceInfo();
			//**************************************************************************************
			//read static data congfiuration and data 
			prepareStaticData();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

	}

	/**
	 * This function reads
	 */
	private void prepareStaticData() {
		Iterator it = sourcesInformations.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			if (((DataSourceInformation) pair.getValue()).isStatic())
				readStaticData(((DataSourceInformation) pair.getValue()).getDataSourceId());
		}

	}

	/**
	 * This function reads data based on self bounds
	 */
	private void readStaticData(String sourceId) {
		String sourceClassName = (String) this.stormConf.get(SpatioTextualConstants.Static_Source_Class_Name + "_" + sourceId);
		Map<String, String> staticSourceConfig = (Map<String, String>) this.stormConf.get(SpatioTextualConstants.Static_Source_Class_Config + "_" + sourceId);
		try {
			Class<?> dataSourceClass = Class.forName(sourceClassName);
			Constructor<?> constructor = dataSourceClass.getConstructor(Rectangle.class, Map.class, String.class,Integer.class,Integer.class);
			AbstractStaticDataSource staticDataSource = (AbstractStaticDataSource) constructor.newInstance(selfBounds, staticSourceConfig, sourceId,this.selfTaskId,this.selfTaskIndex);

			while (staticDataSource.hasNext()) {
				DataObject dataObject = staticDataSource.getNext();
				if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), selfBounds)) {
					addAndIndexADataObject(dataObject);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	IndexCell addAndIndexADataObject(DataObject dataObject) {
		String sourceId = dataObject.getSrcId();
		IndexCell indexCell = localDataSpatioTextualIndex.get(sourceId).addDataObject(dataObject);
		objectToLocalCellIndex.get(sourceId).put(dataObject.getObjectId(), indexCell);
		for (String text : dataObject.getObjectText()) {
			ArrayList<DataObject> objectList;
			if (!textToObjectInvertedlist.get(sourceId).containsKey(text)) {
				objectList = new ArrayList<DataObject>();
				objectList.add(dataObject);
				textToObjectInvertedlist.get(sourceId).put(text, objectList);
			} else {
				textToObjectInvertedlist.get(sourceId).get(text).add(dataObject);
			}
		}
		return indexCell;

	}

	/**
	 * This function initialized the global and local indexes information
	 */
	private void prepareLocalAndGlobalIndexes() {
		numberOfEvaluatorTasks = this.evaluatorBoltTasks.size();
		selfComponentId = context.getThisComponentId();
		selfTaskId = context.getThisTaskId();
		selfTaskIndex = context.getThisTaskIndex();
		//**************************************************************************************
		//global index information
		globalGridIndex = new GlobalGridIndex(numberOfEvaluatorTasks, evaluatorBoltTasks);
		//**************************************************************************************
		//local index information
		//identifying evaluator cell bounds 
		selfBounds = globalGridIndex.getBoundsForTaskIndex(selfTaskIndex);

	}

	private void prepareDataAndQuerySourceInfo() {
		System.out.println("***************************Printing Bolt configuration*******************");
		Iterator it = stormConf.entrySet().iterator();
		sourcesInformations = new HashMap<String, DataSourceInformation>();
		//**************************************************************************************
		//initializing the local variables
		localDataSpatioTextualIndex = new HashMap<String, LocalGridIndex>();
		objectToLocalCellIndex = new HashMap<String, HashMap<String, IndexCell>>();
		textToObjectInvertedlist = new HashMap<String, HashMap<String, ArrayList<DataObject>>>();
		queryInformationHashMap = new HashMap<String, HashMap<String, Query>>();
		externalKNNMap = new HashMap<String, HashMap<String, Query>>();
		cleaningMap = new HashMap<String, Deduplication>();

		String sourceId = "";
		String sourceType = "";
		String persistenceState = "";
		while (it.hasNext()) {
			persistenceState = "";
			Map.Entry pair = (Map.Entry) it.next();

			String key = "" + (String) pair.getKey();
			if (key.startsWith(SpatioTextualConstants.Data_Source)) {
				sourceType = SpatioTextualConstants.Data_Source;
				sourceId = key.substring(SpatioTextualConstants.Data_Source.length() + 1);

			} else if (key.startsWith(SpatioTextualConstants.Query_Source)) {
				sourceType = SpatioTextualConstants.Query_Source;
				sourceId = key.substring(SpatioTextualConstants.Query_Source.length() + 1);
			} else
				continue;

			String value = "" + (String) pair.getValue();
			if (value.contains(SpatioTextualConstants.Volatile))
				persistenceState = SpatioTextualConstants.volatilePersistenceState;
			else if (value.contains(SpatioTextualConstants.Continuous))
				persistenceState = SpatioTextualConstants.continuousPersistenceState;
			else if (value.contains(SpatioTextualConstants.Static)) {
				persistenceState = SpatioTextualConstants.staticPersistenceState;
			} else if (value.contains(SpatioTextualConstants.Persistent))
				persistenceState = SpatioTextualConstants.persistentPersistenceState;
			else if (value.contains(SpatioTextualConstants.Current))
				persistenceState = SpatioTextualConstants.currentPersistenceState;
			String cleanState=SpatioTextualConstants.NOTCLEAN;
			if(stormConf.containsKey(SpatioTextualConstants.getVolatilePropertyKey(sourceId)))
				cleanState= (String)stormConf.get(SpatioTextualConstants.getVolatilePropertyKey(sourceId));
			DataSourceInformation dataSourcesInformation = new DataSourceInformation(sourceId, sourceType, persistenceState,cleanState);
			addIndexesPerSource(sourceId, sourceType, dataSourcesInformation);
			sourcesInformations.put(sourceId, dataSourcesInformation);
			System.out.println(key + " = " + value);

		}
	}
	
	

	private void addIndexesPerSource(String sourceId, String sourceType, DataSourceInformation dataSourcesInformation) {
		if (sourceType.equals(SpatioTextualConstants.Data_Source)) {
			LocalGridIndex indexPerSource = new LocalGridIndex(selfBounds, dataSourcesInformation);
			localDataSpatioTextualIndex.put(sourceId, indexPerSource);
			objectToLocalCellIndex.put(sourceId, new HashMap<String, IndexCell>());
			textToObjectInvertedlist.put(sourceId, new HashMap<String, ArrayList<DataObject>>());
			externalKNNMap.put(sourceId, new HashMap<String, Query>());
			if(dataSourcesInformation.isClean()){
				cleaningMap.put(sourceId, new Deduplication(SpatioTextualConstants.CACHE_SIZE));
			}
		} else if (sourceType.equals(SpatioTextualConstants.Query_Source)) {
			queryInformationHashMap.put(sourceId, new HashMap<String, Query>());

		}
	}

	@Override
	public synchronized void execute(Tuple input) {
		try {
			String sourceType = input.getSourceStreamId();
			if (sourceType.contains(SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query)) {
				handleQuery(input);
			} else if (SpatioTextualConstants.isDataStreamSource( sourceType)) {
				handleDataObject(input);
			} else if (SpatioTextualConstants.isControlStreamSource( sourceType)) {
				handleControlMessage(input);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * This function processes the query input
	 * 
	 * @param input
	 */
	void handleQuery(Tuple input) {
		//TODO make this decision from the global index 
		Query query = (Query) input.getValueByField(SpatioTextualConstants.query);
		//System.out.println("Evaluator:"+selfTaskId+" receieved query "+query.toString());
		if (sourcesInformations.get(query.getSrcId()).isContinuous()) {
			query.setContinousQuery(new Boolean(true));
			handleContinousQuery(query);
		} else if (sourcesInformations.get(query.getSrcId()).isVolatile()) {
			query.setContinousQuery(new Boolean(false));
			handleSnapShotQuery(query);
		}

	}

	/**
	 * This function processes a snapshot query input
	 * 
	 * @param input
	 */
	void handleSnapShotQuery(Query query) {
		if (query.getQueryType().equals(SpatioTextualConstants.queryTextualRange)) {
			handleSnapShotTextualRangeQuery(query);
		} else if (query.getQueryType().equals(SpatioTextualConstants.queryTextualKNN)) {
			handleSnapShotTextualKNNQuery(query);
		} else if (query.getQueryType().equals(SpatioTextualConstants.queryTextualSpatialJoin)) {
			handleSnapShotTextualSpatialJoinQuery(query);
		}
	}

	/**
	 * This function processePs a persistent query input
	 * 
	 * @param input
	 */
	void handleContinousQuery(Query query) {
		
		if (query.getCommand().equals(SpatioTextualConstants.addCommand)) {
			if(!sourcesInformations.containsKey(query.getDataSrc())){
				System.err.println("Data Source not found: "+query.getDataSrc());
				return;
			
			}
			queryInformationHashMap.get(query.getSrcId()).put(query.getQueryId(), query);
			if (!sourcesInformations.get(query.getDataSrc()).isVolatile() || (query.getDataSrc2() != null && !sourcesInformations.get(query.getDataSrc2()).isVolatile())) {
				//this means that this query works on existing data and hence needs to first perorm a snapshop query 
				//then register itself as a continous query and hence update its result
				handleSnapShotQuery(query);
			}
			//TODO CHeck if some more results are needed from neighbour evaluators
			localDataSpatioTextualIndex.get(query.getDataSrc()).addContinousQuery(query);
			if (query.getDataSrc2() != null)
				localDataSpatioTextualIndex.get(query.getDataSrc2()).addContinousQuery(query);
		} else if (query.getCommand().equals(SpatioTextualConstants.updateCommand)) {
			if(!sourcesInformations.containsKey(query.getDataSrc())){
				System.err.println("Data Source not found: "+query.getDataSrc());
				return;
			
			}
			//delete then update
			Query queryInfo = queryInformationHashMap.get(query.getSrcId()).get(query.getQueryId());
			localDataSpatioTextualIndex.get(query.getDataSrc()).updateContinousQuery(queryInfo, query);
			if (query.getDataSrc2() != null)
				localDataSpatioTextualIndex.get(query.getDataSrc2()).updateContinousQuery(queryInfo, query);
		} else if (query.getCommand().equals(SpatioTextualConstants.dropCommand)) {
			//only getting information from oldStored Query as the new query may not have all information and it may contain source and query ids
			Query oldQuery = queryInformationHashMap.get(query.getSrcId()).get(query.getQueryId());
			localDataSpatioTextualIndex.get(oldQuery.getDataSrc()).dropContinousQuery(oldQuery);
			if (oldQuery.getDataSrc2() != null)
				localDataSpatioTextualIndex.get(oldQuery.getDataSrc2()).dropContinousQuery(oldQuery);
			queryInformationHashMap.get(oldQuery.getSrcId()).remove(oldQuery.getQueryId());
		}

	}

	void handleSnapShotTextualRangeQuery(Query query) {

	}

	/**
	 * This method process a query on an existing dataset of objects
	 * 
	 * @param query
	 */
	void handleSnapShotTextualKNNQuery(Query query) {
		query.resetKNNStructures();
		//TODO consider generating a class for the localIndex and have the query 
		//initiate an iterator on the index 
		LocalIndexKNNIterator it = localDataSpatioTextualIndex.get(query.getDataSrc()).LocalKNNIterator(query.getFocalPoint());
		query.setLocalKnnIterator(it);
		//this is one way of evaluating the textual KNN query 
		//first apply the spatial predicate the locate the query in a cell index 
		//then apply the textual predicate 
		//then evaluate the KNN predicate 
		//TODO consider other alternatives that first consider a textual index first 
		//TODO consider choose the arbitrating the alternatives based on the selectivity of the predicate
		//TODO consider applying the other textual predicates
		expandKNNQueryToAdjustResultLocally(query);
		if (SpatialHelper.checkKNNQueryDoneWithinLocalBounds(query, selfBounds)) {
			ArrayList<DataObject> knnList = query.getKNNList();
			for (DataObject obj : knnList){
				
				generateOutput(query, obj, SpatioTextualConstants.addCommand);
			}

		} else {
			GlobalIndexKNNIterator globalit = globalGridIndex.globalKNNIterator(query);
			query.setGlobalKNNIterator(globalit);
			expandSnapShotKNNPredicateToSurroundingEvaluators(query);
		}

	}

	/**
	 * This method process a query on an existing dataset of objects and
	 * registers the external continous KNN query
	 * 
	 * @param query
	 */
	void handleExternalSnapShotTextualKNNQuery(Query query) {
		Boolean continousQuery = query.getContinousQuery();
		query.resetKNNStructures();
		//TODO consider generating a class for the localIndex and have the query 
		//initiate an iterator on the index 
		LocalIndexKNNIterator it = localDataSpatioTextualIndex.get(query.getDataSrc()).LocalKNNIterator(query.getFocalPoint());
		query.setLocalKnnIterator(it);
		query.setContinousQuery(false);
		//this is one way of evaluating the textual KNN query 
		//first apply the spatial predicate the locate the query in a cell index 
		//then apply the textual predicate 
		//then evaluate the KNN predicate 
		//TODO consider other alternatives that first consider a textual index first 
		//TODO consider choose the arbitrating the alternatives based on the selectivity of the predicate
		//TODO consider applying the other textual predicates
		expandKNNQueryToAdjustResultLocally(query);
		ArrayList<DataObject> knnList = query.getKNNList();
		//Build a controlMessage to respond as result of the KNN query 
		Control controlMessage = new Control();
		ArrayList<Query> queriesList = new ArrayList<Query>();
		queriesList.add(query);
		controlMessage.setQueriesList(queriesList);
		controlMessage.setControlMessageType(Control.ANSWER_SNAPSHOT_KNN_PREDICATE);
		ArrayList<DataObject> resultObject = new ArrayList<DataObject>();
		for (DataObject obj : knnList)
			resultObject.add(obj);
		controlMessage.setDataObjects(resultObject);
		Integer taskId = globalGridIndex.getTaskIDsContainingPoint(query.getFocalPoint()).get(0);
		query.setContinousQuery(continousQuery);
		collector.emitDirect(taskId, SpatioTextualConstants.getBoltBoltControlStreamId( id), new Values(controlMessage));
		//this registers the external continous query if it is originally continous
		if (continousQuery)
			this.externalKNNMap.get(query.getDataSrc()).put(query.getQueryId(), query);

	}

	private void expandSnapShotKNNPredicateToSurroundingEvaluators(Query query) {
		GlobalIndexKNNIterator it = query.getGlobalKNNIterator();
		ArrayList<Integer> surroundingEvaluators = new ArrayList<Integer>();
		if (it.hasNext()) {
			surroundingEvaluators = it.next();
			query.setPendingKNNTaskIds(surroundingEvaluators);
			Control message = new Control();
			message.setControlMessageType(Control.REQUEST_KNN_PREDICATE);
			ArrayList<Query> querieslist = new ArrayList<Query>();
			querieslist.add(query);
			message.setQueriesList(querieslist);
			//TODO make this smarter and restrict your self to only relevant global index cells 
			for (Integer i : surroundingEvaluators) {
				collector.emitDirect(i,SpatioTextualConstants.getBoltBoltControlStreamId( id), new Values(message));
			}
		}

	}

	void handleSnapShotTextualSpatialJoinQuery(Query query) {

	}

	/**
	 * This function progessivly increases the range of the KNN query to find
	 * the complete KNN resultset
	 * 
	 * @param query
	 * @return
	 */
	ArrayList<ResultSetChange> expandKNNQueryToAdjustResultLocally(Query query) {
		LocalIndexKNNIterator it = query.getLocalKnnIterator();
		ArrayList<ResultSetChange> changes = new ArrayList<ResultSetChange>();
		while (it.hasNext()) {
			ArrayList<IndexCell> indexCellList = it.next();
			for (IndexCell indexCell : indexCellList) {
				if (StringHelpers.evaluateTextualPredicate(indexCell.getAllDataTextInCell(), query.getQueryText(), query.getTextualPredicate())){//indexCell.cellOverlapsTextually(query.getQueryText())) {
					HashMap<String, DataObject> indexedDataObjectsMap = indexCell.getStoredObjects();
					Iterator dataObjectIterator = indexedDataObjectsMap.entrySet().iterator();
					while (dataObjectIterator.hasNext()) {
						Map.Entry entry = (Map.Entry) dataObjectIterator.next();
						DataObject dataObject = (DataObject) entry.getValue();
						if (StringHelpers.evaluateTextualPredicate(dataObject.getObjectText(), query.getQueryText(), query.getTextualPredicate())) {
							changes.addAll(query.processDataObject(dataObject));
						}
					}
				}
				if (query.getContinousQuery())
					indexCell.addQuery(query);
			}
			if (query.getKNNlistSize() >= query.getK()) {
				break;
			}

		}
		return changes;
	}

	/**
	 * This function processes a data object
	 * 
	 * @param input
	 */
	void handleDataObject(Tuple input) {

		DataObjectList dataObjectList = (DataObjectList) input.getValueByField(SpatioTextualConstants.data);
		DataObject dataObject;
		String source = "";
		for (int i = 0; i < dataObjectList.getDataObjects().size(); i++) {
			dataObject = dataObjectList.getDataObjects().get(i);

			source = dataObject.getSrcId();
			if (sourcesInformations.get(source).isPersistent())
				handlePersisentDataObject(dataObject);
			if (sourcesInformations.get(source).isCurrent())
				handleCurrentDataObject(dataObject);
			else if (sourcesInformations.get(source).isVolatile())
				handleVolatileDataObject(dataObject);

		}
	}

	/**
	 * This function processes a control message
	 * 
	 * @param input
	 */
	void handleControlMessage(Tuple input) {
		Control controlMessage = (Control) input.getValueByField(SpatioTextualConstants.control);
		if (Control.ANSWER_SNAPSHOT_KNN_PREDICATE.equals(controlMessage.getControlMessageType())) {
			handleExternalSnapshotKNNQueryAnswer(controlMessage, input.getSourceTask());
		} else if (Control.REQUEST_KNN_PREDICATE.equals(controlMessage.getControlMessageType())) {
			handleExternalKNNQueryRequest(controlMessage);
		} else if (Control.DROP_CONTININOUS_KNN_PREDICATE.equals(controlMessage.getControlMessageType())) {
			handleDropُExternalKNNQueryRequest(controlMessage);
		} else if (Control.UPDATE_CONTININOUS_KNN_PREDICATE.equals(controlMessage.getControlMessageType())) {
			//This is an update 
			//TODO 
			//TODO
			//TODO
			handleUpdateExternalKNNQueryRequest(controlMessage);
		} else if (Control.CHANGES_SET_CONTINOUS_KNN_PREDICATE.equals(controlMessage.getControlMessageType())) {
			handleExternalKNNPredicateResultSetChange(controlMessage);
		}
	}

	private void handleExternalKNNPredicateResultSetChange(Control controlMessage) {
		//this object is assumed to qualify for the textual predicate of the KNN query 
		ArrayList<ResultSetChange> resultSetChanges = controlMessage.getResultSetChanges();
		ArrayList<ResultSetChange> internalResultSetChanges = new ArrayList<ResultSetChange>();
		for (ResultSetChange resultSetChange : resultSetChanges) {
			Query externalQuery = resultSetChange.getQuery();
			Query internalQuery = queryInformationHashMap.get(externalQuery.getSrcId()).get(externalQuery.getQueryId());
			DataObject dataObject = resultSetChange.getDataObject();
			String changeType = resultSetChange.getChangeType();
			if (ResultSetChange.Add.equals(changeType)) {
				dataObject.setCommand(SpatioTextualConstants.addCommand);
			} else if (ResultSetChange.Update.equals(changeType)) {
				dataObject.setCommand(SpatioTextualConstants.updateCommand);
			} else if (ResultSetChange.Remove.equals(changeType)) {
				dataObject.setCommand(SpatioTextualConstants.dropCommand);
			}
			internalResultSetChanges.addAll(internalQuery.processDataObject(dataObject));
			if (!checkExternalKNNqueryReusltDone(internalQuery))
				expandSnapShotKNNPredicateToSurroundingEvaluators(internalQuery);
		}
		generateOutputForResultChange(internalResultSetChanges);
	}

	private void handleUpdateExternalKNNQueryRequest(Control controlMessage) {
		//TODO
		//TODO
		//TODO
		//TODO
	}

	private void handleDropُExternalKNNQueryRequest(Control controlMessage) {
		Query outSideQuery = controlMessage.getQueriesList().get(0);
		this.externalKNNMap.get(outSideQuery.getDataSrc()).remove(outSideQuery.getQueryId());
	}

	private void handleExternalKNNQueryRequest(Control controlMessage) {
		ArrayList<Query> queriesList = controlMessage.getQueriesList();
		for (Query q : queriesList) {
			//initilize an iterator for this 
			handleExternalSnapShotTextualKNNQuery(q);
		}
	}

	private void handleExternalSnapshotKNNQueryAnswer(Control controlMessage, Integer taskId) {
		Query outSideQuery = controlMessage.getQueriesList().get(0);

		Query internalQuery = queryInformationHashMap.get(outSideQuery.getSrcId()).get(outSideQuery.getQueryId());
		Integer taskIdIndex = internalQuery.getPendingKNNTaskIds().indexOf(taskId);
		if (taskIdIndex != null)
			internalQuery.getPendingKNNTaskIds().remove(taskId);
		ArrayList<DataObject> responseDataObjects = controlMessage.getDataObjects();
		if (responseDataObjects != null && responseDataObjects.size() != 0)
			for (DataObject obj : responseDataObjects) {
				//initilize an iterator for this 
				internalQuery.processDataObject(obj);
			}
		if (internalQuery.getPendingKNNTaskIds().size() == 0) {
			if (!checkExternalKNNqueryReusltDone(internalQuery))
				expandSnapShotKNNPredicateToSurroundingEvaluators(internalQuery);
		}

	}

	private Boolean checkExternalKNNqueryReusltDone(Query query) {
		Double farthestDistance = query.getFarthestDistance();
		Integer knnSizelist = query.getKNNlistSize();
		if (knnSizelist >= query.getK()) {
			Double minDistOfNextIteration = query.getGlobalKNNIterator().getMinDistOfNextIteration();
			if (minDistOfNextIteration == null || farthestDistance < minDistOfNextIteration)
				return true;
			//TODO we need to consider shrink the iterations of the global iterator

		}
		return false;

	}

	/**
	 * This function processes a persistent data object
	 * 
	 * @param input
	 */
	void handlePersisentDataObject(DataObject dataObject) {

	}

	/**
	 * This function processes a current data object
	 * 
	 * @param input
	 */
	void handleCurrentDataObject(DataObject dataObject) {

		ArrayList<Query> affectedKNNQueries = new ArrayList<Query>();
		ArrayList<ResultSetChange> changes = new ArrayList<ResultSetChange>();
		IndexCell previousCell = objectToLocalCellIndex.get(dataObject.getSrcId()).get(dataObject.getObjectId());
		if (SpatioTextualConstants.updateDropCommand.equals(dataObject.getCommand()) && previousCell != null) {
			DataObject removedDataObject = dropCurrnetDataObject(dataObject);
			processDataObjectForExternalKNNPredicates(removedDataObject);
			changes.addAll(processDataObjectUpdateForContinousQueries(removedDataObject, previousCell.getQueries()));
			affectedKNNQueries.addAll(previousCell.getQueries());
		}
		else if (SpatioTextualConstants.dropCommand.equals(dataObject.getCommand()) && previousCell != null) {
			//This removes the object from this cell all together 
			DataObject removedDataObject = dropCurrnetDataObject(dataObject);
			removedDataObject.setCommand(SpatioTextualConstants.dropCommand);
			processDataObjectForExternalKNNPredicates(removedDataObject);
			changes.addAll(processDataObjectUpdateForContinousQueries(removedDataObject, previousCell.getQueries()));
			affectedKNNQueries.addAll(previousCell.getQueries());

		} else if (previousCell != null && SpatioTextualConstants.updateCommand.equals(dataObject.getCommand())) {
			processDataObjectForExternalKNNPredicates(dataObject);
			//Check if the data object in the same cell
			//if yes update the data object 
			//if no drop the data object from the old cell adjust queries accordingly
			//then add the query to the new index cell and adjust the relevant queries accordingly
			IndexCell newIndexCell = localDataSpatioTextualIndex.get(dataObject.getSrcId()).mapDataObjectToIndexCell(dataObject);
			DataObject previousDataObject = previousCell.getDataObject(dataObject.getObjectId());
			//TODO this function needs optimization by finding out common text between the previous object and the new object 
			//and reflect this change in the textual index within the grid cell
			if (!previousCell.equals(newIndexCell) || !previousDataObject.getOriginalText().equals(dataObject.getOriginalText())) {
				previousDataObject.setCommand(SpatioTextualConstants.updateDropCommand);
				DataObject removedDataObject = dropCurrnetDataObject(previousDataObject);
				dataObject.setCommand(SpatioTextualConstants.updateCommand);
				addAndIndexADataObject(dataObject);
				ArrayList<Query> previousCellQueries = previousCell.getQueries();
				ArrayList<Query> newCellQueries = newIndexCell.getQueries();
				ArrayList<Query> toRemoveFrom = new ArrayList<Query>();
				for (Query q : previousCellQueries) {
					if (!newCellQueries.contains(q)) {
						toRemoveFrom.add(q);
						//the query is contained in both cells 

					}
				}
				removedDataObject.setCommand(SpatioTextualConstants.updateDropCommand);
				changes.addAll(processDataObjectUpdateForContinousQueries(dataObject, toRemoveFrom));
				affectedKNNQueries.addAll(toRemoveFrom);
				dataObject.setCommand(SpatioTextualConstants.updateCommand);
				changes.addAll(processDataObjectUpdateForContinousQueries(dataObject, newCellQueries));
				affectedKNNQueries.addAll(newCellQueries);
			} else {
				//no need to remove the data object just adjust the KNN list of all relevant queries 
				//make sure that the object did not change its text
				previousDataObject.setTimeStamp(dataObject.getTimeStamp());
				if (!dataObject.getLocation().equals(previousDataObject.getLocation())) {
					previousDataObject.setLocation(dataObject.getLocation());
					dataObject.setCommand(SpatioTextualConstants.updateCommand);
					changes.addAll(processDataObjectUpdateForContinousQueries(dataObject, newIndexCell.getQueries()));
					affectedKNNQueries.addAll(newIndexCell.getQueries());
				}
			}
		} else {// if(SpatioTextualConstants.addCommand.equals( dataObject.getCommand())){
			IndexCell indexcell = addAndIndexADataObject(dataObject);
			dataObject.setCommand(SpatioTextualConstants.addCommand);
			processDataObjectForExternalKNNPredicates(dataObject);
			changes.addAll(processDataObjectUpdateForContinousQueries(dataObject, indexcell.getQueries()));
			affectedKNNQueries.addAll(indexcell.getQueries());
		}
		changes.addAll(checkIfKNNQueryRequiresExtensionsOrShriniking(affectedKNNQueries));
		generateOutputForResultChange(changes);
	}
	private ArrayList<ResultSetChange>checkIfKNNQueryRequiresExtensionsOrShriniking(ArrayList<Query> affectedQueries){
		ArrayList<ResultSetChange> changes= new ArrayList<ResultSetChange>();
		for(Query query:affectedQueries){
			if (!SpatialHelper.checkKNNQueryDoneWithinLocalBounds(query, selfBounds)&&query.getGlobalKNNIterator()==null) {	
				GlobalIndexKNNIterator globalit = globalGridIndex.globalKNNIterator(query);
				query.setGlobalKNNIterator(globalit);
				expandSnapShotKNNPredicateToSurroundingEvaluators(query);
			}
			else if (query.getGlobalKNNIterator()!=null&&!checkExternalKNNqueryReusltDone(query))
				expandSnapShotKNNPredicateToSurroundingEvaluators(query);
		}
		return changes;
	}
	private void processDataObjectForExternalKNNPredicates(DataObject obj) {
		String source = obj.getSrcId();
		Map<String, Query> externalPredicates = externalKNNMap.get(source);
		if (externalPredicates != null) {
			Iterator<Entry<String, Query>> queriesIterator = externalPredicates.entrySet().iterator();
			while (queriesIterator.hasNext()) {
				Query externalQuery = queriesIterator.next().getValue();
				ArrayList<ResultSetChange> resultSetChanges = externalQuery.processDataObject(obj);
				if (resultSetChanges != null && !resultSetChanges.isEmpty()) {
					Control controlMessage = new Control();
					controlMessage.setControlMessageType(Control.CHANGES_SET_CONTINOUS_KNN_PREDICATE);
					controlMessage.setResultSetChanges(resultSetChanges);
					Integer taskId = globalGridIndex.getTaskIDsContainingPoint(externalQuery.getFocalPoint()).get(0);
					collector.emitDirect(taskId, SpatioTextualConstants.getBoltBoltControlStreamId( id), new Values(controlMessage));
				}
			}
		}
	}

	private void generateOutputForResultChange(ArrayList<ResultSetChange> changes) {
		for (ResultSetChange change : changes){
			generateOutput(change.getQuery(), change.getDataObject(), change.getChangeType());
		}
	}

	private ArrayList<ResultSetChange> processDataObjectUpdateForContinousQueries(DataObject dataObject, ArrayList<Query> queries) {
		ArrayList<ResultSetChange> changes = new ArrayList<ResultSetChange>();
		//To avoid concurrent modification problem processing a query list and then maybe remove a query from it while iteratoing 
		//create a copy of the arrya list 
		ArrayList<Query> queriesListReplica = new ArrayList<Query>();
		//TODO this is expensive update this 
		queriesListReplica.addAll(queries);
		for (Query q : queriesListReplica) {
			if (SpatioTextualConstants.queryTextualKNN.equals(q.getQueryType())) {
				Double previousLargestDist = q.getFarthestDistance();
				Integer previousKNNListSize = q.getKNNlistSize();
				changes.addAll(q.processDataObject(dataObject));
				if (previousLargestDist > q.getFarthestDistance()) {
					LocalIndexKNNIterator it = q.getLocalKnnIterator();
					Boolean shrink = true;
					while (it.hasPrevious() && shrink) {
						ArrayList<IndexCell> indexCells = it.previous();

						//We are shrinking in terms of an entire round around the focal point 
						for (IndexCell indexCell : indexCells) {
							if (SpatialHelper.getMinDistanceBetween(q.getFocalPoint(), indexCell.getBounds()) <= q.getFarthestDistance()) {
								shrink = false;
								break;
							}
						}
						if (shrink) {
							for (IndexCell indexCell : indexCells) {
								indexCell.dropQuery(q);
							}
						}
					}
				} else if (previousLargestDist < q.getFarthestDistance() || q.getKNNlistSize() < q.getK()) {
					changes.addAll(expandKNNQueryToAdjustResultLocally(q));
				}

			}
		}
		return changes;
	}

	/**
	 * This function data object
	 * 
	 * @param input
	 */
	DataObject dropCurrnetDataObject(DataObject dataObject) {
		ArrayList<ResultSetChange> changes = null;
		if (objectToLocalCellIndex.get(dataObject.getSrcId()).containsKey(dataObject.getObjectId())) {
			IndexCell indexCell = objectToLocalCellIndex.get(dataObject.getSrcId()).get(dataObject.getObjectId());
			DataObject removedDataObject = indexCell.dropDataObject(dataObject.getObjectId());
			objectToLocalCellIndex.get(dataObject.getSrcId()).remove(dataObject.getObjectId());
			for (String text : dataObject.getObjectText()) {
				ArrayList<DataObject> invertedObjectList = textToObjectInvertedlist.get(dataObject.getSrcId()).get(text);
				Integer i = 0;
				Boolean found = false;
				for (DataObject obj : invertedObjectList) {
					if (obj.equals(dataObject)) {
						found = true;
						break;
					}
					i++;
				}
				if (found)
					invertedObjectList.remove(i);
			}
			return removedDataObject;
		} else
			return null;//object not found and hence cannot be removed //TODO maybe throw an exception
	}

	/**
	 * This function processes a volatile data object
	 * 
	 * @param input
	 */
	void handleVolatileDataObject(DataObject dataObject) {
		boolean fromNeighbour = false;
		if (!SpatialHelper.overlapsSpatially(dataObject.getLocation(), selfBounds)) {
			fromNeighbour = true;
		} else {
			fromNeighbour = false;
		}
		//check if the incomming data object is duplicate 
		if(!fromNeighbour
				&&cleaningMap.containsKey(dataObject.getSrcId())
				&& ((Deduplication)cleaningMap.get(dataObject.getSrcId())).isDuplicate(dataObject))
			return; //this object does not need to be processde			
		
		HashMap<String, Query> queriesMap = localDataSpatioTextualIndex.get(dataObject.getSrcId()).getReleventQueries(dataObject, fromNeighbour);

		Iterator queriesIterator = queriesMap.entrySet().iterator();
		while (queriesIterator.hasNext()) {
			Map.Entry entry = (Map.Entry) queriesIterator.next();
			Query q = (Query) entry.getValue();

			if (!fromNeighbour && q.getQueryType().equals(SpatioTextualConstants.queryTextualRange)) {
				//apply spatial predicate then textual predicate 
				if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), q.getSpatialRange()) && StringHelpers.evaluateTextualPredicate( dataObject.getObjectText(),q.getQueryText(), q.getTextualPredicate()))
					generateOutput(q, dataObject, SpatioTextualConstants.addCommand);
			} else if (q.getQueryType().equals(SpatioTextualConstants.queryTextualKNN)) {
				//apply spatial predicate 
				//apply textual predicate 
				//apply KNN predicate 
				processVolatileDataObjectForTextualKNNQuery(dataObject, q, fromNeighbour);

			} else if (q.getQueryType().equals(SpatioTextualConstants.queryTextualSpatialJoin)) {
				processVolatileDataObjectForTextualSpatialJoinQuery(dataObject, q, fromNeighbour);
			}
		}

		//check if the data object needs to be send to other partitions
		DataObjectList dataObjectList = new DataObjectList();
		dataObjectList.addDataObject(dataObject);
		if (!fromNeighbour && dataObject.getRelevantArea() != null) {
			ArrayList<Integer> relevantTaskIds = globalGridIndex.getTaskIDsOverlappingRecangle(dataObject.getRelevantArea());
			for (Integer taskId : relevantTaskIds) {
				if (taskId != selfTaskId)
					collector.emitDirect(taskId, id + SpatioTextualConstants.Bolt_Bolt_STreamIDExtension_Data, new Values(dataObjectList));
			}
		}
	}

	/**
	 * This function performs the spatial distance join
	 * 
	 * @param dataObject
	 * @param q
	 */
	void processVolatileDataObjectForTextualKNNQuery(DataObject dataObject, Query q, Boolean fromNeighbour) {
		if (!StringHelpers.evaluateTextualPredicate(dataObject.getObjectText(),q.getQueryText(),  q.getTextualPredicate())) {
			//this data object does not overlap with the textual predicate of the query and hence cannot affect the result

		}
	}

	/**
	 * This function performs the spatial distance join
	 * 
	 * @param dataObject
	 * @param q
	 */
	void processVolatileDataObjectForTextualSpatialJoinQuery(DataObject dataObject, Query q, Boolean fromNeighbour) {
		if (!SpatialHelper.overlapsSpatially(dataObject.getLocation(), q.getSpatialRange()))
			return;

		String otherDataSource = "";
		//identify the other data source to join with 
		//check if this data object came from input sources or from data source 
		// verify the textual predicate of the incomming data source and the query 
		if (dataObject.getSrcId().equals(q.getDataSrc())){
			otherDataSource = q.getDataSrc2();
			if(!StringHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate())) return ;
		}
		else{
			otherDataSource = q.getDataSrc();
			if(!StringHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText2(), q.getTextualPredicate2()))return ;
		}

		Double distance = q.getDistance();
		Rectangle relevantArea = new Rectangle(new Point(dataObject.getLocation().getX() - distance, dataObject.getLocation().getY() - distance),
				new Point(dataObject.getLocation().getX() + distance, dataObject.getLocation().getY() + distance));

		dataObject.extendRelevantArea(relevantArea);

		ArrayList<IndexCell> relevantIndexCells = localDataSpatioTextualIndex.get(otherDataSource).getOverlappingIndexCells(relevantArea);

		for (IndexCell indexCell : relevantIndexCells) {

			if (true ){//indexCell.cellOverlapsTextually(dataObject.getObjectText())) {
				//this cell contains data that is relevant to the incoming data object 
				//iterate over all data objects to find matching 
				//this is the the theta join operator 
				HashMap<String, DataObject> storedObjects = indexCell.getStoredObjects();
				Iterator it = storedObjects.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					DataObject storedDataObject = (DataObject) entry.getValue();
					if (  	StringHelpers.evaluateTextualPredicate(dataObject.getObjectText(), storedDataObject.getObjectText(), q.getJoinTextualPredicate())&&
							 	(otherDataSource.equals(q.getDataSrc2()) &&   StringHelpers.evaluateTextualPredicate(storedDataObject.getObjectText(), q.getQueryText2(), q.getTextualPredicate2())     ||
							 otherDataSource.equals(q.getDataSrc()) &&   StringHelpers.evaluateTextualPredicate(storedDataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate())     )
						
							&&SpatialHelper.getDistanceInBetween(dataObject.getLocation(), storedDataObject.getLocation()) <= q.getDistance() //evaluate distance 
							
							&& SpatialHelper.overlapsSpatially(storedDataObject.getLocation(), q.getSpatialRange()))
					
						generateOutput(q, dataObject, storedDataObject, SpatioTextualConstants.addCommand, SpatioTextualConstants.addCommand);

				}
			}
		}

	}

	void generateOutput(Query q, DataObject obj, String command) {
		System.out.println("[Output: command: "+command+" query:" + q.toString() + "\n******" + obj.toString() + "]");
		OutputTuple outputTuple = new OutputTuple();
		outputTuple.setDataObject(obj);
		outputTuple.setQuery(q);
		outputTuple.setDataObjectCommand(command);
		collector.emit(SpatioTextualConstants.Bolt_Output_STreamIDExtension, new Values(outputTuple));
	}

	void generateOutput(Query q, DataObject obj, DataObject obj2, String obj1Command, String obj2Command) {
		System.out.println("[Output: command  "+obj1Command+" query:"  + q.toString() + "\n******" + obj.toString() + "\n******" + obj2.toString() + "]");
		OutputTuple outputTuple = new OutputTuple();
		outputTuple.setDataObject(obj);
		outputTuple.setDataObject2(obj2);
		outputTuple.setQuery(q);
		outputTuple.setDataObjectCommand(obj1Command);
		outputTuple.setDataObject2Command(obj2Command);
		collector.emit(SpatioTextualConstants.Bolt_Output_STreamIDExtension, new Values(outputTuple));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		try {
			//These are the streams for interaction with other bolts 
			declarer.declareStream(id + SpatioTextualConstants.Bolt_Bolt_STreamIDExtension_Query, new Fields(SpatioTextualConstants.query));
			declarer.declareStream(id + SpatioTextualConstants.Bolt_Bolt_STreamIDExtension_Data, new Fields(SpatioTextualConstants.data));
			declarer.declareStream(SpatioTextualConstants.getBoltBoltControlStreamId( id), new Fields(SpatioTextualConstants.control));
			declarer.declareStream(id + SpatioTextualConstants.Bolt_Index_STreamIDExtension_Query, new Fields(SpatioTextualConstants.query));
			declarer.declareStream(id + SpatioTextualConstants.Bolt_Index_STreamIDExtension_Data, new Fields(SpatioTextualConstants.data));
			declarer.declareStream(id + SpatioTextualConstants.Bolt_Index_STreamIDExtension_Control, new Fields(SpatioTextualConstants.control));
			//This is the final output 
			declarer.declareStream(SpatioTextualConstants.Bolt_Output_STreamIDExtension, new Fields(SpatioTextualConstants.output));
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
