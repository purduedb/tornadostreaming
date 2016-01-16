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
import java.util.Collection;
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
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.index.global.GlobalGridIndex;
import edu.purdue.cs.tornado.index.global.GlobalIndex;
import edu.purdue.cs.tornado.index.global.GlobalIndexIterator;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.global.GlobalOptimizedPartitionedIndex;
import edu.purdue.cs.tornado.index.global.GlobalStaticPartitionedIndex;
import edu.purdue.cs.tornado.index.local.LocalIndexKNNIterator;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.messages.CombinedTuple;
import edu.purdue.cs.tornado.messages.Control;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.DataObjectList;
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
	public static final long serialVersionUID = 1L;
	String id; //given spatio-textual component id
	public String selfComponentId;
	public Integer selfTaskId;
	public Integer selfTaskIndex;
	public Integer outputTuplesCount;
	public Integer visitedDataObjectCount;
	//*******************************************************************************
	//local index attributes 
	public Rectangle selfBounds;
	//public  HashMap<String, LocalHybridIndex> localDataSpatioTextualIndex;//source to spatial index cells

	public HashMap<String, HashMap<String, Query>> queryInformationHashMap; //source to query id to index cellqueyr

	//outer TOP-K queries, these are Top-K queries that come from other evaluators and need to 
	//reevaluated for incoming data
	public HashMap<String, HashMap<String, Query>> externalTopKMap; //source to query id to index cellqueyr
	//*******************************************************************************************************
	public HashMap<String, Deduplication> cleaningMap; //source to query id to index cellqueyr

	//**************** Evaluator bolts attributes **********************
	public List<Integer> evaluatorBoltTasks; //this keeps track of the evaluator bolts ids 
	public HashMap<String, DataSourceInformation> sourcesInformations; //this keeps track of the type of every input source 

	//******************Global Grid  parameters ********************************
	IndexCellCoordinates globalIndexSelfcoordinates;
	GlobalIndex globalIndex;

	//*******************Storm specific attributes *********************
	public Map stormConf; //configuration
	public TopologyContext context; //storm context
	public OutputCollector collector;
	public LocalIndexType localIndexType;
	public GlobalIndexType globalIndexType;
	public ArrayList<Partition> partitions;

	public SpatioTextualEvaluatorBolt(String id, LocalIndexType localIndexType, GlobalIndexType globalIndexType, ArrayList<Partition> partitions) {
		this.id = id;
		this.outputTuplesCount = 0;
		this.visitedDataObjectCount = 0;
		this.localIndexType = localIndexType;
		this.partitions = partitions;
		this.globalIndexType = globalIndexType;
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
			//read static data congfiguration and data 
			prepareStaticData();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

	}

	/**
	 * This function reads
	 */
	public void prepareStaticData() {
		Iterator it = sourcesInformations.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			//We should not read entire static data at once, we should use the a cache based method
			if (((DataSourceInformation) pair.getValue()).isStatic())
				readStaticData(((DataSourceInformation) pair.getValue()).getDataSourceId());
		}

	}

	/**
	 * This function reads data based on self bounds
	 */
	public void readStaticData(String sourceId) {
		String sourceClassName = (String) this.stormConf.get(SpatioTextualConstants.Static_Source_Class_Name + "_" + sourceId);
		Map<String, String> staticSourceConfig = (Map<String, String>) this.stormConf.get(SpatioTextualConstants.Static_Source_Class_Config + "_" + sourceId);
		try {
			Class<?> dataSourceClass = Class.forName(sourceClassName);
			Constructor<?> constructor = dataSourceClass.getConstructor(Rectangle.class, Map.class, String.class, Integer.class, Integer.class);
			AbstractStaticDataSource staticDataSource = (AbstractStaticDataSource) constructor.newInstance(selfBounds, staticSourceConfig, sourceId, this.selfTaskId, this.selfTaskIndex);

			while (staticDataSource.hasNext()) {
				DataObject dataObject = staticDataSource.getNext();
				if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), selfBounds)) {
					addAndIndexADataObject(dataObject);
				} else {
					System.err.println("Data object is outside the scope this evluator :" + dataObject.toString() + " : bounds " + selfBounds.toString());
				}
			}
			staticDataSource.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fetchStaticDataPortion(String sourceId, Rectangle bounds) {
		String sourceClassName = (String) this.stormConf.get(SpatioTextualConstants.Static_Source_Class_Name + "_" + sourceId);
		Map<String, String> staticSourceConfig = (Map<String, String>) this.stormConf.get(SpatioTextualConstants.Static_Source_Class_Config + "_" + sourceId);
		try {
			Class<?> dataSourceClass = Class.forName(sourceClassName);
			Constructor<?> constructor = dataSourceClass.getConstructor(Rectangle.class, Map.class, String.class, Integer.class, Integer.class);
			AbstractStaticDataSource staticDataSource = (AbstractStaticDataSource) constructor.newInstance(bounds, staticSourceConfig, sourceId, this.selfTaskId, this.selfTaskIndex);

			while (staticDataSource.hasNext()) {
				DataObject dataObject = staticDataSource.getNext();
				if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), bounds)) {
					addAndIndexADataObject(dataObject);
				} else {
					System.err.println("Data object is outside the scope this evluator :" + dataObject.toString() + " : bounds " + bounds.toString());
				}
			}
			staticDataSource.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IndexCell addAndIndexADataObject(DataObject dataObject) {
		String sourceId = dataObject.getSrcId();
		IndexCell indexCell = sourcesInformations.get(sourceId).getLocalHybridIndex().addDataObject(dataObject);
		//	sourcesInformations.get(sourceId).getLocalTextIndex().addDataObject(dataObject);
		return indexCell;

	}

	public void addAndIndexPersistentWindowDataObject(DataObject dataObject) {
		String sourceId = dataObject.getSrcId();

		sourcesInformations.get(sourceId).addDataObject(dataObject);

	}

	/**
	 * This function initialized the global and local indexes information
	 */
	public void prepareLocalAndGlobalIndexes() {
		numberOfEvaluatorTasks = this.evaluatorBoltTasks.size();
		selfComponentId = context.getThisComponentId();
		selfTaskId = context.getThisTaskId();
		selfTaskIndex = context.getThisTaskIndex();
		//**************************************************************************************
		//global index information
		if (globalIndexType == GlobalIndexType.PARTITIONED)
			//globalIndex = new GlobalStaticPartitionedIndex(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions);
			globalIndex = new GlobalOptimizedPartitionedIndex(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions);
		else
			globalIndex = new GlobalGridIndex(numberOfEvaluatorTasks, evaluatorBoltTasks);
		//**************************************************************************************
		//local index information
		//identifying evaluator cell bounds 
		selfBounds = globalIndex.getBoundsForTaskIndex(selfTaskIndex);

	}

	public void prepareDataAndQuerySourceInfo() {
		System.out.println("***************************Printing Bolt configuration*******************");
		Iterator it = stormConf.entrySet().iterator();
		sourcesInformations = new HashMap<String, DataSourceInformation>();
		//**************************************************************************************
		//initializing the local variables
		//localDataSpatioTextualIndex = new HashMap<String, LocalHybridIndex>();
		queryInformationHashMap = new HashMap<String, HashMap<String, Query>>();
		externalTopKMap = new HashMap<String, HashMap<String, Query>>();
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

			String cleanState = SpatioTextualConstants.NOTCLEAN;
			if (stormConf.containsKey(SpatioTextualConstants.getVolatilePropertyKey(sourceId)))
				cleanState = (String) stormConf.get(SpatioTextualConstants.getVolatilePropertyKey(sourceId));
			DataSourceInformation dataSourcesInformation = new DataSourceInformation(this.selfBounds, sourceId, sourceType, persistenceState, cleanState, true, localIndexType);
			addIndexesPerSource(sourceId, sourceType, dataSourcesInformation);
			sourcesInformations.put(sourceId, dataSourcesInformation);
			System.out.println(key + " = " + value);

		}
	}

	public void addIndexesPerSource(String sourceId, String sourceType, DataSourceInformation dataSourcesInformation) {
		if (sourceType.equals(SpatioTextualConstants.Data_Source)) {
			externalTopKMap.put(sourceId, new HashMap<String, Query>());
			if (dataSourcesInformation.isClean()) {
				cleaningMap.put(sourceId, new Deduplication());
			}
		} else if (sourceType.equals(SpatioTextualConstants.Query_Source)) {
			queryInformationHashMap.put(sourceId, new HashMap<String, Query>());

		}
	}

	@Override
	public void execute(Tuple input) {
		//	collector.ack(input);
		try {
			String sourceType = input.getSourceStreamId();
			if (sourceType.contains(SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query)) {
				handleQuery(input);
			} else if (SpatioTextualConstants.isDataStreamSource(sourceType)) {
				handleDataObject(input);
			} else if (SpatioTextualConstants.isControlStreamSource(sourceType)) {
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
	public void handleQuery(Tuple input) {
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
	public void handleSnapShotQuery(Query query) {
		if (query.getQueryType().equals(SpatioTextualConstants.queryTextualRange)) {
			handleSnapShotTextualRangeQuery(query);
		} else if (query.getQueryType().equals(SpatioTextualConstants.queryTextualKNN)) {
			handleSnapShotTextualKNNQuery(query);
		} else if (query.getQueryType().equals(SpatioTextualConstants.queryTextualSpatialJoin)) {
			handleSnapShotTextualJoinQuery(query);
		}
	}

	/**
	 * This function processePs a persistent query input
	 * 
	 * @param input
	 */
	public void handleContinousQuery(Query query) {

		if (query.getCommand().equals(SpatioTextualConstants.addCommand)) {
			if (!sourcesInformations.containsKey(query.getDataSrc())) {
				System.err.println("Data Source not found: " + query.getDataSrc());
				return; 

			}
			queryInformationHashMap.get(query.getSrcId()).put(query.getQueryId(), query);
			if (!sourcesInformations.get(query.getDataSrc()).isVolatile() || (query.getDataSrc2() != null && !sourcesInformations.get(query.getDataSrc2()).isVolatile())) {
				//this means that this query works on existing data and hence needs to first perorm a snapshop query 
				//then register itself as a continous query and hence update its result
				handleSnapShotQuery(query);
			}
			//TODO CHeck if some more results are needed from neighbour evaluators
			sourcesInformations.get(query.getDataSrc()).getLocalHybridIndex().addContinousQuery(query);
			if (query.getDataSrc2() != null)
				sourcesInformations.get(query.getDataSrc2()).getLocalHybridIndex().addContinousQuery(query);
		} else if (query.getCommand().equals(SpatioTextualConstants.updateCommand)) {
			if (!sourcesInformations.containsKey(query.getDataSrc())) {
				System.err.println("Data Source not found: " + query.getDataSrc());
				return;

			}
			//delete then update
			Query queryInfo = queryInformationHashMap.get(query.getSrcId()).get(query.getQueryId());
			sourcesInformations.get(query.getDataSrc()).getLocalHybridIndex().updateContinousQuery(queryInfo, query);
			if (query.getDataSrc2() != null)
				sourcesInformations.get(query.getDataSrc2()).getLocalHybridIndex().updateContinousQuery(queryInfo, query);
		} else if (query.getCommand().equals(SpatioTextualConstants.dropCommand)) {
			//only getting information from oldStored Query as the new query may not have all information and it may contain source and query ids
			Query oldQuery = queryInformationHashMap.get(query.getSrcId()).get(query.getQueryId());
			sourcesInformations.get(oldQuery.getDataSrc()).getLocalHybridIndex().dropContinousQuery(oldQuery);
			if (oldQuery.getDataSrc2() != null)
				sourcesInformations.get(oldQuery.getDataSrc2()).getLocalHybridIndex().dropContinousQuery(oldQuery);
			queryInformationHashMap.get(oldQuery.getSrcId()).remove(oldQuery.getQueryId());
		}

	}

	public void handleSnapShotTextualRangeQuery(Query query) {
		ArrayList<DataObject> outputObjects = new ArrayList<DataObject>();
		ArrayList<IndexCell> relevantIndexCells = sourcesInformations.get(query.getDataSrc()).getLocalHybridIndex().getOverlappingIndexCellWithData(query.getSpatialRange(), query.getQueryText());
		if (relevantIndexCells != null)
			for (IndexCell indexCell : relevantIndexCells) {
				Collection<DataObject> allIndexCellDataObjects = indexCell.getStoredObjects().values();
				for (DataObject dataObject : allIndexCellDataObjects) {
					this.visitedDataObjectCount++;
					if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), query.getSpatialRange()) && TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), query.getQueryText(), query.getTextualPredicate()))
						outputObjects.add(dataObject);

				}
			}
		if (!outputObjects.isEmpty())
			generateOutput(query, outputObjects, SpatioTextualConstants.addCommand);
	}

	/**
	 * This method process a query on an existing dataset of objects
	 * 
	 * @param query
	 */
	public void handleSnapShotTextualKNNQuery(Query query) {
		query.resetKNNStructures();
		//TODO consider generating a class for the localIndex and have the query 
		//initiate an iterator on the index 
		LocalIndexKNNIterator it = sourcesInformations.get(query.getDataSrc()).getLocalHybridIndex().LocalKNNIterator(query.getFocalPoint());
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
			for (DataObject obj : knnList) {

				generateOutput(query, obj, SpatioTextualConstants.addCommand);
			}

		} else {
			GlobalIndexIterator globalit = globalIndex.globalKNNIterator(query.getFocalPoint());
			query.setGlobalKNNIterator(globalit);
			expandSnapShotKNNPredicateToSurroundingEvaluators(query);
		}

	}

	/**
	 * This method process a query on an existing dataset of objects and
	 * registers the external continous KNN query
	 * 
	 * @param query
	 * @throws Exception
	 */
	public void handleExternalSnapShotTextualKNNQuery(Query query) throws Exception {
		Boolean continousQuery = query.getContinousQuery();
		query.resetKNNStructures();
		//TODO consider generating a class for the localIndex and have the query 
		//initiate an iterator on the index 
		LocalIndexKNNIterator it = sourcesInformations.get(query.getDataSrc()).getLocalHybridIndex().LocalKNNIterator(query.getFocalPoint());
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
		Integer taskId = globalIndex.getTaskIDsContainingPoint(query.getFocalPoint());
		query.setContinousQuery(continousQuery);
		collector.emitDirect(taskId, SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(controlMessage));
		//this registers the external continous query if it is originally continous
		if (continousQuery)
			this.externalTopKMap.get(query.getDataSrc()).put(query.getQueryId(), query);

	}

	public void expandSnapShotKNNPredicateToSurroundingEvaluators(Query query) {
		GlobalIndexIterator it = query.getGlobalKNNIterator();
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
				collector.emitDirect(i, SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(message));
			}
		}

	}

	/**
	 * This function evaluates a snapshot join query both sources are assumed to
	 * be non volatile for the snapshot join to work
	 * 
	 * @param query
	 */
	public void handleSnapShotTextualJoinQuery(Query query) {

		if (sourcesInformations.get(query.getDataSrc()).isVolatile() || sourcesInformations.get(query.getDataSrc2()).isVolatile())
			return;

		ArrayList<IndexCell> relevantIndexCells1 = sourcesInformations.get(query.getDataSrc()).getOverlappingIndexCellWithData(query.getSpatialRange(), query.getQueryText());
		ArrayList<DataObject> joinedTuples;
		if (relevantIndexCells1 != null) {
			for (IndexCell indexCell : relevantIndexCells1) {
				Collection<DataObject> allIndexCellDataObjects = indexCell.getStoredObjects(query.getSpatialRange(), query.getQueryText(), query.getTextualPredicate());
				if (allIndexCellDataObjects != null && allIndexCellDataObjects.size() != 0) {
					Rectangle relevantRect = SpatialHelper.expand(indexCell.bounds, query.getDistance());
					relevantRect = SpatialHelper.spatialIntersect(relevantRect, query.getSpatialRange());
					ArrayList<IndexCell> relevantIndexCells2 = sourcesInformations.get(query.getDataSrc2()).getOverlappingIndexCellWithData(relevantRect, query.getQueryText2());
					ArrayList<DataObject> allIndexCellDataObjects2 = new ArrayList<DataObject>();
					for (IndexCell indexCell2 : relevantIndexCells2) {
						allIndexCellDataObjects2.addAll(indexCell2.getStoredObjects(relevantRect, query.getQueryText2(), query.getTextualPredicate()));
					}
					if (allIndexCellDataObjects2 != null && allIndexCellDataObjects2.size() != 0) {
						joinedTuples = new ArrayList<DataObject>();
						for (DataObject dataObject : allIndexCellDataObjects) {
							for (DataObject dataObject2 : allIndexCellDataObjects2) {
								if(SpatialHelper.getDistanceInBetween(dataObject.getLocation(), dataObject2.getLocation())<=query.getDistance()&&
										TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), dataObject2.getObjectText(), query.getJoinTextualPredicate()))
									joinedTuples.add(dataObject2);
							}
							if(!joinedTuples.isEmpty())
								generateOutput(query, dataObject, joinedTuples,SpatioTextualConstants.addCommand, SpatioTextualConstants.addCommand);
						}
					}
				}
			}
		}
		//Now we need to send the data to the proper task ID if the join spans multiple evalautors 
	}

	/**
	 * This function progessivly increases the range of the KNN query to find
	 * the complete KNN resultset
	 * 
	 * @param query
	 * @return
	 */
	public ArrayList<ResultSetChange> expandKNNQueryToAdjustResultLocally(Query query) {
		LocalIndexKNNIterator it = query.getLocalKnnIterator();
		ArrayList<ResultSetChange> changes = new ArrayList<ResultSetChange>();
		while (it.hasNext()) {
			ArrayList<IndexCell> indexCellList = it.next();
			for (IndexCell indexCell : indexCellList) {
				if (TextHelpers.evaluateTextualPredicate(indexCell.getAllDataTextInCell(), query.getQueryText(), query.getTextualPredicate())) {//indexCell.cellOverlapsTextually(query.getQueryText())) {
					HashMap<String, DataObject> indexedDataObjectsMap = indexCell.getStoredObjects();
					Iterator dataObjectIterator = indexedDataObjectsMap.entrySet().iterator();
					while (dataObjectIterator.hasNext()) {
						Map.Entry entry = (Map.Entry) dataObjectIterator.next();
						DataObject dataObject = (DataObject) entry.getValue();
						if (TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), query.getQueryText(), query.getTextualPredicate())) {
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
	 * @throws Exception
	 */
	void handleDataObject(Tuple input) throws Exception {

		//	DataObjectList dataObjectList = (DataObjectList) input.getValueByField(SpatioTextualConstants.data);
		DataObject dataObject = (DataObject) input.getValueByField(SpatioTextualConstants.data);
		if (!SpatialHelper.overlapsSpatially(dataObject.getLocation(), selfBounds))
			return;
		String source = "";
		//for (int i = 0; i < dataObjectList.getDataObjects().size(); i++) {
		//dataObject = dataObjectList.getDataObjects().get(i);

		source = dataObject.getSrcId();
		if (sourcesInformations.get(source).isPersistent())
			handlePersisentDataObject(dataObject);
		if (sourcesInformations.get(source).isCurrent())
			handleCurrentDataObject(dataObject);
		else if (sourcesInformations.get(source).isVolatile())
			handleVolatileDataObject(dataObject);

		//}
	}

	/**
	 * This function processes a control message
	 * 
	 * @param input
	 * @throws Exception
	 */
	void handleControlMessage(Tuple input) throws Exception {
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

	public void handleExternalKNNPredicateResultSetChange(Control controlMessage) {
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

	public void handleUpdateExternalKNNQueryRequest(Control controlMessage) {
		//TODO
		//TODO
		//TODO
		//TODO
	}

	public void handleDropُExternalKNNQueryRequest(Control controlMessage) {
		Query outSideQuery = controlMessage.getQueriesList().get(0);
		this.externalTopKMap.get(outSideQuery.getDataSrc()).remove(outSideQuery.getQueryId());
	}

	public void handleExternalKNNQueryRequest(Control controlMessage) throws Exception {
		ArrayList<Query> queriesList = controlMessage.getQueriesList();
		for (Query q : queriesList) {
			//initilize an iterator for this 
			handleExternalSnapShotTextualKNNQuery(q);
		}
	}

	public void handleExternalSnapshotKNNQueryAnswer(Control controlMessage, Integer taskId) {
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

	public Boolean checkExternalKNNqueryReusltDone(Query query) {
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
	public void handlePersisentDataObject(DataObject dataObject) {

		addAndIndexPersistentWindowDataObject(dataObject);
		handleVolatileDataObject(dataObject);
	}

	/**
	 * This function processes a current data object
	 * 
	 * @param input
	 * @throws Exception
	 */
	public void handleCurrentDataObject(DataObject dataObject) throws Exception {

		ArrayList<Query> affectedKNNQueries = new ArrayList<Query>();
		ArrayList<ResultSetChange> changes = new ArrayList<ResultSetChange>();
		IndexCell previousCell = sourcesInformations.get(dataObject.getSrcId()).getObjectToLocalCellIndex().get(dataObject.getObjectId());
		if (SpatioTextualConstants.dropCommand.equals(dataObject.getCommand()) && previousCell != null) {
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
			IndexCell newIndexCell = sourcesInformations.get(dataObject.getSrcId()).getLocalHybridIndex().mapDataObjectToIndexCell(dataObject);
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

	public ArrayList<ResultSetChange> checkIfKNNQueryRequiresExtensionsOrShriniking(ArrayList<Query> affectedQueries) {
		ArrayList<ResultSetChange> changes = new ArrayList<ResultSetChange>();
		for (Query query : affectedQueries) {
			if (!SpatialHelper.checkKNNQueryDoneWithinLocalBounds(query, selfBounds) && query.getGlobalKNNIterator() == null) {
				GlobalIndexIterator globalit = globalIndex.globalKNNIterator(query.getFocalPoint());
				query.setGlobalKNNIterator(globalit);
				expandSnapShotKNNPredicateToSurroundingEvaluators(query);
			} else if (query.getGlobalKNNIterator() != null && !checkExternalKNNqueryReusltDone(query))
				expandSnapShotKNNPredicateToSurroundingEvaluators(query);
		}
		return changes;
	}

	public void processDataObjectForExternalKNNPredicates(DataObject obj) throws Exception {
		String source = obj.getSrcId();
		Map<String, Query> externalPredicates = externalTopKMap.get(source);
		if (externalPredicates != null) {
			Iterator<Entry<String, Query>> queriesIterator = externalPredicates.entrySet().iterator();
			while (queriesIterator.hasNext()) {
				Query externalQuery = queriesIterator.next().getValue();
				ArrayList<ResultSetChange> resultSetChanges = externalQuery.processDataObject(obj);
				if (resultSetChanges != null && !resultSetChanges.isEmpty()) {
					Control controlMessage = new Control();
					controlMessage.setControlMessageType(Control.CHANGES_SET_CONTINOUS_KNN_PREDICATE);
					controlMessage.setResultSetChanges(resultSetChanges);
					Integer taskId = globalIndex.getTaskIDsContainingPoint(externalQuery.getFocalPoint());
					collector.emitDirect(taskId, SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(controlMessage));
				}
			}
		}
	}

	public void generateOutputForResultChange(ArrayList<ResultSetChange> changes) {
		for (ResultSetChange change : changes) {
			generateOutput(change.getQuery(), change.getDataObject(), change.getChangeType());
		}
	}

	public ArrayList<ResultSetChange> processDataObjectUpdateForContinousQueries(DataObject dataObject, ArrayList<Query> queries) {
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
		if (sourcesInformations.get(dataObject.getSrcId()).getObjectToLocalCellIndex().containsKey(dataObject.getObjectId())) {
			IndexCell indexCell = sourcesInformations.get(dataObject.getSrcId()).getObjectToLocalCellIndex().get(dataObject.getObjectId());
			DataObject removedDataObject = indexCell.dropDataObject(dataObject.getObjectId());
			sourcesInformations.get(dataObject.getSrcId()).getObjectToLocalCellIndex().remove(dataObject.getObjectId());
			sourcesInformations.get(dataObject.getSrcId()).getLocalTextIndex().dropDataObject(dataObject);
			return removedDataObject;
		} else
			return null;//object not found and hence cannot be removed //TODO maybe throw an exception
	}

	/**
	 * This function processes a volatile data object
	 * 
	 * @param input
	 */
	public void handleVolatileDataObject(DataObject dataObject) {
		boolean fromNeighbour = false;
		if (!SpatialHelper.overlapsSpatially(dataObject.getLocation(), selfBounds)) {
			fromNeighbour = true;
		} else {
			fromNeighbour = false;
		}
		//check if the incomming data object is duplicate 
		//		if (!fromNeighbour && cleaningMap.containsKey(dataObject.getSrcId()) && ((Deduplication) cleaningMap.get(dataObject.getSrcId())).isDuplicate(dataObject))
		//			return; //this object does not need to be processed			

		HashMap<String, Query> queriesMap = sourcesInformations.get(dataObject.getSrcId()).getLocalHybridIndex().getReleventQueries(dataObject, fromNeighbour);

		Iterator queriesIterator = queriesMap.entrySet().iterator();
		while (queriesIterator.hasNext()) {
			Map.Entry entry = (Map.Entry) queriesIterator.next();
			Query q = (Query) entry.getValue();

			if (!fromNeighbour && q.getQueryType().equals(SpatioTextualConstants.queryTextualRange)) {
				//apply spatial predicate then textual predicate 
				if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), q.getSpatialRange()) && TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate()))
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
			ArrayList<Integer> relevantTaskIds = globalIndex.getTaskIDsOverlappingRecangle(dataObject.getRelevantArea());
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
		if (!TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate())) {
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
		if (dataObject.getSrcId().equals(q.getDataSrc())) {
			otherDataSource = q.getDataSrc2();
			if (!TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate()))
				return;
		} else {
			otherDataSource = q.getDataSrc();
			if (!TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText2(), q.getTextualPredicate2()))
				return;
		}

		Double distance = q.getDistance();
		Rectangle relevantArea = new Rectangle(new Point(dataObject.getLocation().getX() - distance, dataObject.getLocation().getY() - distance),
				new Point(dataObject.getLocation().getX() + distance, dataObject.getLocation().getY() + distance));

		dataObject.extendRelevantArea(relevantArea);

		ArrayList<IndexCell> relevantIndexCells = sourcesInformations.get(otherDataSource).getLocalHybridIndex().getOverlappingIndexCells(relevantArea);

		for (IndexCell indexCell : relevantIndexCells) {

			if (true) {//indexCell.cellOverlapsTextually(dataObject.getObjectText())) {
				//this cell contains data that is relevant to the incoming data object 
				//iterate over all data objects to find matching 
				//this is the the theta join operator 
				HashMap<String, DataObject> storedObjects = indexCell.getStoredObjects();
				Iterator it = storedObjects.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					DataObject storedDataObject = (DataObject) entry.getValue();
					if (TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), storedDataObject.getObjectText(), q.getJoinTextualPredicate())
							&& (otherDataSource.equals(q.getDataSrc2()) && TextHelpers.evaluateTextualPredicate(storedDataObject.getObjectText(), q.getQueryText2(), q.getTextualPredicate2())
									|| otherDataSource.equals(q.getDataSrc()) && TextHelpers.evaluateTextualPredicate(storedDataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate()))

					&& SpatialHelper.getDistanceInBetween(dataObject.getLocation(), storedDataObject.getLocation()) <= q.getDistance() //evaluate distance 

					&& SpatialHelper.overlapsSpatially(storedDataObject.getLocation(), q.getSpatialRange()))

						generateOutput(q, dataObject, storedDataObject, SpatioTextualConstants.addCommand, SpatioTextualConstants.addCommand);

				}
			}
		}

	}

	void generateOutput(Query q, DataObject obj, String command) {
		//	System.out.println("[Output: command: "+command+" query:" + q.toString() + "\n******" + obj.toString() + "]");
		this.outputTuplesCount++;
		CombinedTuple outputTuple = new CombinedTuple();
		outputTuple.setDataObject(obj);
		Query miniQuery = new Query();
		miniQuery.setQueryId(q.getQueryId());
		miniQuery.setSrcId(q.getSrcId());
		miniQuery.setDataSrc(q.getDataSrc());
		outputTuple.setQuery(miniQuery);
		outputTuple.setDataObjectCommand(command);
		collector.emit(SpatioTextualConstants.Bolt_Output_STreamIDExtension, new Values(outputTuple));
	}

	void generateOutput(Query q, ArrayList<DataObject> objList, String command) {
		//	System.out.println("[Output: command: "+command+" query:" + q.toString() + "\n******" + obj.toString() + "]");
		this.outputTuplesCount += objList.size();
		CombinedTuple outputTuple = new CombinedTuple();
		outputTuple.dataObjectList = objList;
		Query miniQuery = new Query();
		miniQuery.setQueryId(q.getQueryId());
		miniQuery.setSrcId(q.getSrcId());
		miniQuery.setDataSrc(q.getDataSrc());
		outputTuple.setQuery(miniQuery);
		outputTuple.setDataObjectCommand(command);
		collector.emit(SpatioTextualConstants.Bolt_Output_STreamIDExtension, new Values(outputTuple));
	}

	void generateOutput(Query q, DataObject obj, DataObject obj2, String obj1Command, String obj2Command) {
		//	System.out.println("[Output: command  "+obj1Command+" query:"  + q.toString() + "\n******" + obj.toString() + "\n******" + obj2.toString() + "]");
		this.outputTuplesCount++;
		CombinedTuple outputTuple = new CombinedTuple();
		outputTuple.setDataObject(obj);
		outputTuple.setDataObject2(obj2);
		Query miniQuery = new Query();
		miniQuery.setQueryId(q.getQueryId());
		miniQuery.setSrcId(q.getSrcId());
		miniQuery.setDataSrc(q.getDataSrc());
		miniQuery.setDataSrc2(q.getDataSrc2());
		outputTuple.setQuery(miniQuery);
		outputTuple.setDataObjectCommand(obj1Command);
		outputTuple.setDataObject2Command(obj2Command);
		collector.emit(SpatioTextualConstants.Bolt_Output_STreamIDExtension, new Values(outputTuple));
	}

	void generateOutput(Query q, DataObject obj, ArrayList<DataObject> obj2List, String obj1Command, String obj2Command) {
		//	System.out.println("[Output: command  "+obj1Command+" query:"  + q.toString() + "\n******" + obj.toString() + "\n******" + obj2.toString() + "]");
		this.outputTuplesCount++;
		CombinedTuple outputTuple = new CombinedTuple();
		outputTuple.setDataObject(obj);
		outputTuple.setDataObject2List(obj2List);
		Query miniQuery = new Query();
		miniQuery.setQueryId(q.getQueryId());
		miniQuery.setSrcId(q.getSrcId());
		miniQuery.setDataSrc(q.getDataSrc());
		miniQuery.setDataSrc2(q.getDataSrc2());
		outputTuple.setQuery(miniQuery);
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
			declarer.declareStream(SpatioTextualConstants.getBoltBoltControlStreamId(id), new Fields(SpatioTextualConstants.control));
			declarer.declareStream(id + SpatioTextualConstants.Bolt_Index_STreamIDExtension_Query, new Fields(SpatioTextualConstants.query));
			declarer.declareStream(id + SpatioTextualConstants.Bolt_Index_STreamIDExtension_Data, new Fields(SpatioTextualConstants.data));
			declarer.declareStream(id + SpatioTextualConstants.Bolt_Index_STreamIDExtension_Control, new Fields(SpatioTextualConstants.control));
			//This is the final output 
			declarer.declareStream(SpatioTextualConstants.Bolt_Output_STreamIDExtension, new Fields(SpatioTextualConstants.output));
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	//***********************************************************************************************
	//************getters and setters

	public Integer numberOfEvaluatorTasks;

	public HashMap<String, DataSourceInformation> getSourcesInformations() {
		return sourcesInformations;
	}

	public void setSourcesInformations(HashMap<String, DataSourceInformation> sourcesInformations) {
		this.sourcesInformations = sourcesInformations;
	}
}
