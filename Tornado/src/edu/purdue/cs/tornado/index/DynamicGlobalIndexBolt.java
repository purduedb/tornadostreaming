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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import de.linguatools.disco.DISCO;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SemanticHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.helper.TornadoZooKeeperConnection;
import edu.purdue.cs.tornado.index.global.GlobalDynamicIndex;
import edu.purdue.cs.tornado.loadbalance.LoadBalanceMessage;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.loadbalance.SplitMergeInfo;
import edu.purdue.cs.tornado.messages.Control;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.DataObjectList;
import edu.purdue.cs.tornado.messages.Query;

public class DynamicGlobalIndexBolt extends BaseRichBolt {

	public DynamicGlobalIndexBolt(String id) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}

//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	/**
//	 * here we are assuming point objects every point can go only to a single
//	 * query node
//	 */
//	private String id;
//	private String selfComponentId;
//	private Integer selfTaskId;
//	private Integer selfTaskIndex;
//	// **************** Evaluator bolts attributes **********************
//	private Integer numberOfEvaluatorTasks;
//	private List<Integer> executorsTasks; // this keeps track of the evaluator bolts ids
//	private Map<String, DataSourceInformation> sourcesInformations; // this keeps track of the type of every input source
//
//	// ******************DyanmicIndex parameters ********************************
//	GlobalDynamicIndex globalDynamicIndex;
//	TornadoZooKeeperConnection ZK;
//	String myId;
//	List<Integer> indexTasks;
//	private Integer auxiliaryExecutorTask;
//	private Integer numberParallelismExecutors;
//	private Integer numberParallelismIndexBolts;
//	SplitMergeInfo splitMergeInfo;
//	boolean splitFinished = false;
//	boolean mergeFinished = false;
//	int indexBoltTurn = 0;
//	int roundNum = 1; //for debugging
//	List<Integer> newExecutorsTasks;
//	int newAuxiliaryExecutorTask = 0;
//
//	boolean shouldEnterBarrier = false;
//
//	// ******************Textual Semantic Attributes ********************************
//	DISCO disco;
//
//	// *******************Storm specific attributes *********************
//	private Map stormConf; // configuration
//	private TopologyContext context; // storm context
//	private OutputCollector collector;
//	private Rectangle selfBounds;
//	private ArrayList<EvaluatorBoltHistory> mapQueryToPartitions(Query query) {
//		ArrayList<EvaluatorBoltHistory> previousBoltHistory = new ArrayList<EvaluatorBoltHistory>();
//		ArrayList<Integer> tasks = new ArrayList<Integer>();
//		if (SpatioTextualConstants.queryTextualKNN.equals(query.getQueryType())) {
//			tasks .add(globalDynamicIndex.getTaskIDsContainingPoint(query.getFocalPoint()));
//
//		} else if (SpatioTextualConstants.queryTextualRange.equals(query.getQueryType()) || SpatioTextualConstants.queryTextualSpatialJoin.equals(query.getQueryType())) {
//			tasks = globalDynamicIndex.getTaskIDsOverlappingRecangle(query.getSpatialRange());
//		}
//		for (Integer task : tasks)
//			previousBoltHistory.add(new EvaluatorBoltHistory(task, query.getTimeStamp()));
//		return previousBoltHistory;
//	}
//
//	private ArrayList<EvaluatorBoltHistory> mapDataObjectToPartitions(DataObject dataObject) {
//		ArrayList<EvaluatorBoltHistory> previousBoltHistory = new ArrayList<EvaluatorBoltHistory>();
//		Integer task = globalDynamicIndex.getTaskIDsContainingPoint(dataObject.getLocation());
//
//		previousBoltHistory.add(new EvaluatorBoltHistory(task, dataObject.getTimeStamp()));
//		return previousBoltHistory;
//	}
//
//	/**
//	 * Construcor
//	 * 
//	 * @param id
//	 */
//	public DynamicGlobalIndexBolt(String id) {
//		this.id = id;
//
//	}
//
//	/**
//	 * This function reads the configuration of the data sources and builds
//	 * required data structures
//	 */
//	private void readDataSourcesInformation() {
//		System.out.println("***************************Evaluating bolt configuration*******************");
//		Iterator it = stormConf.entrySet().iterator();
//		sourcesInformations = new HashMap<String, DataSourceInformation>();
//		String sourceId = "";
//		String sourceType = "";
//		String persistenceState = "";
//
//		while (it.hasNext()) {
//			persistenceState = "";
//			Map.Entry pair = (Map.Entry) it.next();
//
//			String key = "" + (String) pair.getKey();
//
//			if (key.startsWith(SpatioTextualConstants.Data_Source)) {
//				sourceType = SpatioTextualConstants.Data_Source;
//				sourceId = key.substring(SpatioTextualConstants.Data_Source.length() + 1);
//			} else if (key.startsWith(SpatioTextualConstants.Query_Source)) {
//				sourceType = SpatioTextualConstants.Query_Source;
//				sourceId = key.substring(SpatioTextualConstants.Query_Source.length() + 1);
//			} else
//				continue;
//			String value = "" + (String) pair.getValue();
//			if (value.contains(SpatioTextualConstants.Volatile))
//				persistenceState = SpatioTextualConstants.volatilePersistenceState;
//			else if (value.contains(SpatioTextualConstants.Continuous))
//				persistenceState = SpatioTextualConstants.continuousPersistenceState;
//			else if (value.contains(SpatioTextualConstants.Static))
//				persistenceState = SpatioTextualConstants.staticPersistenceState;
//			else if (value.contains(SpatioTextualConstants.Persistent))
//				persistenceState = SpatioTextualConstants.persistentPersistenceState;
//			else if (value.contains(SpatioTextualConstants.Current))
//				persistenceState = SpatioTextualConstants.currentPersistenceState;
//
//			String cleanState = SpatioTextualConstants.NOTCLEAN;
//			if (stormConf.containsKey(SpatioTextualConstants.getVolatilePropertyKey(sourceId)))
//				cleanState = (String) stormConf.get(SpatioTextualConstants.getVolatilePropertyKey(sourceId));
//			DataSourceInformation dataSourcesInformation = new DataSourceInformation(this.selfBounds,sourceId, sourceType, persistenceState, cleanState);
//
//			sourcesInformations.put(sourceId, dataSourcesInformation);
//			System.out.println(key + " = " + value);
//
//		}
//
//	}
//
//	// collecting the topology information
//	// prepare the grid index cells
//	// set the evaluator bolt task ids
//	@Override
//	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
//
//		this.context = context;
//		this.collector = collector;
//		this.stormConf = stormConf;
//		this.myId = context.getThisComponentId() + "_" + context.getThisTaskId();
//		this.executorsTasks = context.getComponentTasks(id);
//		this.indexTasks = context.getComponentTasks(id + SpatioTextualConstants.IndexIDExtension);
//		this.auxiliaryExecutorTask = executorsTasks.get(this.executorsTasks.size() - 1);
//		this.executorsTasks.remove(this.executorsTasks.size() - 1);
//		this.numberParallelismExecutors = this.executorsTasks.size();
//		this.numberParallelismIndexBolts = this.indexTasks.size();
//		this.ZK = new TornadoZooKeeperConnection(stormConf, myId);
//
//		numberOfEvaluatorTasks = this.executorsTasks.size();
//		selfComponentId = context.getThisComponentId();
//		selfTaskId = context.getThisTaskId();
//		selfTaskIndex = context.getThisTaskIndex();
//		globalDynamicIndex = new GlobalDynamicIndex(numberOfEvaluatorTasks, executorsTasks);
//
//		//TODO maybe move this to a seperate bolt and adjust the topology accordingly
//		if (this.stormConf.get(SpatioTextualConstants.discoDir) != null)
//			disco = SemanticHelper.getDiskBasedDiscoInstance((String) this.stormConf.get(SpatioTextualConstants.discoDir));
//		else
//			disco = null;
//
//		readDataSourcesInformation();
//
//		for (Partition c : this.globalDynamicIndex.currentPartitions) {
//			Control controlMessage = new Control();
//			LoadBalanceMessage loadBalanceMessage = new LoadBalanceMessage();
//			loadBalanceMessage.setParition(c);
//			loadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.PARTITION);
//			controlMessage.setControlMessageType(Control.LOAD_BALANCE);
//			controlMessage.setLeadBalanceMessage(loadBalanceMessage);
//			collector.emitDirect(executorsTasks.get(c.index), SpatioTextualConstants.getIndexBoltControlStreamId(id), new Values(controlMessage));
//		}
//		Partition auxPartition = new Partition(0, 0, 0, 0);
//		auxPartition.index = -1; //old:auxPartition.index=this.auxiliaryExecutorTask; 
//		collector.emitDirect(this.auxiliaryExecutorTask, "Partition", new Values(auxPartition));
//		//System.out.println("??????????????????????????????????????? auxiliaryExecutorTask = "+ auxiliaryExecutorTask);
//
//	}
//
//	@Override
//	public Map<String, Object> getComponentConfiguration() {
//		// configure how often a tick tuple will be sent to the bolt
//		Config conf = new Config();
//		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 10);//in seconds
//		return conf;
//	}
//
//	protected static boolean isTickTuple(Tuple tuple) {
//		return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID) && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
//	}
//
//	@Override
//	public synchronized void execute(Tuple tuple) {
//
//		if (isTickTuple(tuple)) {
//			String bolt = ""; //for debugging
//			int[][] result = new int[numberParallelismExecutors][4];
//			for (int i = 0; i < numberParallelismExecutors; i++) {
//				bolt = "ExecutorBolt_" + executorsTasks.get(i);
//				result[i] = ZK.readDataFrom(bolt);
//				System.out.println(
//						"RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR" + myId + " : Read data #" + i + " from " + bolt + " statData= " + result[i][0] + " statQuery= " + result[i][1] + " coord= " + result[i][2] + " axis= " + result[i][3]);
//			}
//			roundNum++; //for debugging
//
//			splitMergeInfo = globalDynamicIndex.newSplitMerge(result);
//
//			if (splitMergeInfo != null) {
//				if (indexBoltTurn == selfTaskIndex) {
//					System.out.println("NEW_PLAN::NEW_PLAN::NEW_PLAN::NEW_PLAN::NEW_PLAN::NEW_PLAN::NEW_PLAN from: " + myId + " sending Merge and Split commands........ ");
//					//merge messages
//					Control newControl = new Control();
//					newControl.setControlMessageType(Control.LOAD_BALANCE);
//					LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
//					newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.MERGE);
//					newLoadBalanceMessage.setSendTo(executorsTasks.get(splitMergeInfo.mergeParent.index));
//					newControl.setLeadBalanceMessage(newLoadBalanceMessage);
//
//					if (splitMergeInfo.mergeParent.index != splitMergeInfo.mergeChild0.index) {
//						collector.emitDirect((executorsTasks.get(splitMergeInfo.mergeChild0.index)), SpatioTextualConstants.getIndexBoltControlStreamId(id), new Values(newControl));
//					} else {
//						collector.emitDirect((executorsTasks.get(splitMergeInfo.mergeChild1.index)), SpatioTextualConstants.getIndexBoltControlStreamId(id), new Values(newControl));
//					}
//					//split messages
//					newControl = new Control();
//					newControl.setControlMessageType(Control.LOAD_BALANCE);
//					newLoadBalanceMessage = new LoadBalanceMessage();
//					newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.SPLIT);
//					newLoadBalanceMessage.setSendTo(auxiliaryExecutorTask);
//					newLoadBalanceMessage.setNewCell(splitMergeInfo.splitChild0);
//					newLoadBalanceMessage.setAuxCell(splitMergeInfo.splitChild1);
//					newControl.setLeadBalanceMessage(newLoadBalanceMessage);
//					collector.emitDirect(executorsTasks.get(splitMergeInfo.splitParent.index), SpatioTextualConstants.getIndexBoltControlStreamId(id), new Values(newControl));
//				}
//				if (shouldEnterBarrier) {
//					enterBarrier();
//					shouldEnterBarrier = false;
//				}
//			} else {
//				System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ " + myId + " No need for changing the plan");
//			}
//		} else {
//			String sourceType = tuple.getSourceStreamId();
//			LoadBalanceMessage loadBalanceMessage = null;
//			if (SpatioTextualConstants.isControlStreamSource(sourceType)) {
//				Control controlMessage = (Control) tuple.getValueByField(SpatioTextualConstants.control);
//				if (Control.LOAD_BALANCE.equals(controlMessage.getControlMessageType())) {
//					loadBalanceMessage = controlMessage.getLeadBalanceMessage();
//					if (LoadBalanceMessage.FINISHED.equals(loadBalanceMessage.getLoadBalanceMessageType())) {
//						if (LoadBalanceMessage.SPLIT.equals(loadBalanceMessage.getType())) {
//							splitFinished = true;
//							System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSS " + myId + " reseived finished Split message");
//						} else if (LoadBalanceMessage.MERGE.equals(loadBalanceMessage.getType())) {
//							mergeFinished = true;
//							System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMM " + myId + " reseived finished Merge message");
//						} else {
//							System.err.println("Unknown Finished Stream");
//						}
//
//						if (splitFinished && mergeFinished) {
//							System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFF " + myId + " finished Split & Merge, ready to switch to the new plan");
//							try {
//								splitFinished = false;
//								mergeFinished = false;
//								ZK.setBarrier();
//								Control newControl = new Control();
//								newControl.setControlMessageType(Control.LOAD_BALANCE);
//								LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
//								newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.ENTER_BARRIER);
//								newControl.setLeadBalanceMessage(newLoadBalanceMessage);
//								collector.emit(SpatioTextualConstants.getIndexIndexControlStreamId(id), new Values(newControl));
//
//								//for(int i : indexTasks){
//								//	collector.emitDirect(i, "EnterBarrier", new Values(1));
//								//}
//							} catch (Exception e) {
//								System.err.println(myId + " could not set barrier " + e);
//							}
//						}
//					} else if (LoadBalanceMessage.ENTER_BARRIER.equals(loadBalanceMessage.getLoadBalanceMessageType())) {
//						if (splitMergeInfo == null) {
//							shouldEnterBarrier = true;
//							System.out.println(myId + "splitMergeInfo = null --> shouldEnterBarrier = true");
//
//							//collector.emitDirect(myTaskId, "EnterBarrier", new Values(1));
//						} else {
//							enterBarrier();
//						}
//					}
//				}
//			}
//
//			else {
//				// get tuple source information
//				try {
//					String source = tuple.getSourceComponent();
//					String streamId = tuple.getSourceStreamId();
//					if (!SpatioTextualConstants.Default.equals(streamId))
//						source = source + "_" + streamId;
//
//					if (SpatioTextualConstants.Query_Source.equals(sourceType)) {
//						handleQuery(tuple, source);
//					} else if (SpatioTextualConstants.Data_Source.equals(sourceType)) {
//						handleData(tuple, source);
//					}
//				} catch (Exception e) {
//					e.printStackTrace(System.err);
//				}
//			}
//		}
//
//	}
//
//	private void handleQuery(Tuple input, String source) {
//		// identify the evaluator bolt to submit your tuple to
//		//TODO add code handling for the special case of query update and query droping
//		String queryType = input.getStringByField(SpatioTextualConstants.queryTypeField);
//		Query query = readQueryByType(input, queryType, source);
//
//		// update the last evaluator bolt information
//		if (sourcesInformations.get(source).isContinuous()) {
//			ArrayList<Integer> previousEvalauatorTaskList = sourcesInformations.get(source).getLastBoltTasKInformation().get(query.getQueryId());
//
//			if (previousEvalauatorTaskList != null && SpatioTextualConstants.dropCommand.equals(query.getCommand())) {
//				for (EvaluatorBoltHistory task : previousEvalauatorTaskList) {
//					collector.emitDirect(task.getTaskId(), id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, new Values(query));
//				}
//				sourcesInformations.get(source).getLastBoltTasKInformation().put(query.getQueryId(), null);
//			} else if (SpatioTextualConstants.addCommand.equals(query.getCommand())) {
//				ArrayList<EvaluatorBoltHistory> evalauatorTaskList = mapQueryToPartitions(query);
//
//				sourcesInformations.get(source).getLastBoltTasKInformation().put(query.getQueryId(), evalauatorTaskList);
//				if (previousEvalauatorTaskList != null && previousEvalauatorTaskList.size() != 0) {
//					for (EvaluatorBoltHistory task : previousEvalauatorTaskList) {
//						if (!evalauatorTaskList.contains(task)) {
//							Query removeQuery = new Query();
//							removeQuery.setCommand(SpatioTextualConstants.dropCommand);
//							removeQuery.setQueryId(query.getQueryId());
//							removeQuery.setSrcId(query.getSrcId());
//							collector.emitDirect(task.getTaskId(), id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, new Values(removeQuery));
//						}
//					}
//				}
//				for (EvaluatorBoltHistory task : evalauatorTaskList)
//					collector.emitDirect(task.getTaskId(), id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, new Values(query));
//
//			} else if (SpatioTextualConstants.updateCommand.equals(query.getCommand())) {
//				//TODO carefully handle updates
//			}
//		} else {//this query is snapshot
//				//just submit the query and do not maintain any information about it 
//			ArrayList<EvaluatorBoltHistory> evalauatorTaskList = mapQueryToPartitions(query);
//			for (EvaluatorBoltHistory task : evalauatorTaskList)
//				collector.emitDirect(task.getTaskId(), id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, new Values(query));
//		}
//	}
//
//	private Query readQueryByType(Tuple input, String queryType, String source) {
//		Query query = new Query();
//		query.setQueryId(input.getStringByField(SpatioTextualConstants.queryIdField));
//		query.setSrcId(source);
//		query.setQueryType(queryType);
//		query.setTimeStamp(input.getLongByField(SpatioTextualConstants.queryTimeStampField));
//		query.setDataSrc(input.getStringByField(SpatioTextualConstants.dataSrc));
//		query.setCommand(input.getStringByField(SpatioTextualConstants.queryCommand));
//		String text = "", text2 = "";
//		if (input.contains(SpatioTextualConstants.textualPredicate)) {
//			query.setTextualPredicate(input.getStringByField(SpatioTextualConstants.textualPredicate));
//		} else {
//			query.setTextualPredicate(SpatioTextualConstants.none);
//		}
//		if (input.contains(SpatioTextualConstants.textualPredicate2)) {
//			query.setTextualPredicate2(input.getStringByField(SpatioTextualConstants.textualPredicate2));
//		} else {
//			query.setTextualPredicate2(SpatioTextualConstants.none);
//		}
//		if (input.contains(SpatioTextualConstants.queryTextField)) {
//			text = input.getStringByField(SpatioTextualConstants.queryTextField);
//			ArrayList<String> queryText = new ArrayList<String>();
//			if (text != null && !"".equals(text)) {
//				queryText = TextHelpers.transformIntoSortedArrayListOfString(text);
//				if (SpatioTextualConstants.semantic.equals(query.getTextualPredicate()) && disco != null) {
//					ArrayList<String> similarKeyWord = SemanticHelper.getSematicallySimilarKeyWords(disco, queryText);
//					queryText = TextHelpers.sortTextArrayList(similarKeyWord);
//
//					query.setTextualPredicate(SpatioTextualConstants.overlaps);
//				}
//			} else {
//				query.setTextualPredicate(SpatioTextualConstants.none);
//			}
//			query.setQueryText(queryText);
//
//		}
//		if (input.contains(SpatioTextualConstants.queryText2Field)) {
//			text2 = input.getStringByField(SpatioTextualConstants.queryText2Field);
//			ArrayList<String> queryText = new ArrayList<String>();
//			if (text2 != null && !"".equals(text2)) {
//				queryText = TextHelpers.transformIntoSortedArrayListOfString(text2);
//				if (SpatioTextualConstants.semantic.equals(query.getTextualPredicate()) && disco != null) {
//					ArrayList<String> similarKeyWord = SemanticHelper.getSematicallySimilarKeyWords(disco, queryText);
//					queryText = TextHelpers.sortTextArrayList(similarKeyWord);
//					query.setTextualPredicate2(SpatioTextualConstants.overlaps);
//				}
//			} else {
//				query.setTextualPredicate2(SpatioTextualConstants.none);
//			}
//			query.setQueryText2(queryText);
//
//		}
//
//		if (input.contains(SpatioTextualConstants.joinTextualPredicate)) {
//			query.setJoinTextualPredicate(input.getStringByField(SpatioTextualConstants.joinTextualPredicate));
//		} else {
//			query.setJoinTextualPredicate(SpatioTextualConstants.none);
//		}
//
//		if (SpatioTextualConstants.queryTextualKNN.equals(queryType)) {
//			query.setK(input.getIntegerByField(SpatioTextualConstants.kField));
//			query.getFocalPoint().setX(input.getDoubleByField(SpatioTextualConstants.focalXCoordField));
//			query.getFocalPoint().setY(input.getDoubleByField(SpatioTextualConstants.focalYCoordField));
//			query.setSpatialRange(new Rectangle(query.getFocalPoint(), query.getFocalPoint()));
//		} else if (SpatioTextualConstants.queryTextualRange.equals(queryType) || (SpatioTextualConstants.queryTextualSpatialJoin.equals(queryType))) {
//			Point min = new Point();
//			min.setX(input.getDoubleByField(SpatioTextualConstants.queryXMinField));
//			min.setY(input.getDoubleByField(SpatioTextualConstants.queryYMinField));
//			Point max = new Point();
//			max.setX(input.getDoubleByField(SpatioTextualConstants.queryXMaxField));
//			max.setY(input.getDoubleByField(SpatioTextualConstants.queryYMaxField));
//			query.setSpatialRange(new Rectangle(min, max));
//			if (SpatioTextualConstants.queryTextualSpatialJoin.equals(queryType)) {
//				query.setDataSrc2(input.getStringByField(SpatioTextualConstants.dataSrc2));
//				query.setDistance(input.getDoubleByField(SpatioTextualConstants.queryDistance));
//			}
//		}
//
//		return query;
//	}
//
//	private void handleData(Tuple input, String source) {
//		// identify the evaluator bolt to submit your tuple to
//		// if persistent update the last evaluator bolt information and emit to
//		// the previous bolt information
//		// to invalidate the previous tuple,
//		// the evaluator bolt should detect this and remove the previous tuple
//		// and the send to the proper bolt
//		DataObject dataObject = readDataObject(input, source);
//		if (SpatioTextualConstants.dropCommand.equals(dataObject.getCommand())) {
//			//this case is an legitimate drop command from the data source and it is not related to an update drop
//			ArrayList<EvaluatorBoltHistory> previousEvalauatorTaskList = sourcesInformations.get(source).getLastBoltTasKInformation().get(dataObject.getObjectId());
//			if (previousEvalauatorTaskList != null && previousEvalauatorTaskList.size() != 0) {
//				for (EvaluatorBoltHistory task : previousEvalauatorTaskList) {
//					DataObject removeDataObject = new DataObject();
//					removeDataObject.setCommand(SpatioTextualConstants.dropCommand);
//					removeDataObject.setObjectId(dataObject.getObjectId());
//					removeDataObject.setSrcId(dataObject.getSrcId());
//					DataObjectList removeDataObjectList = new DataObjectList();
//					removeDataObjectList.addDataObject(removeDataObject);
//					collector.emitDirect(task.getTaskId(), SpatioTextualConstants.getIndexBoltDataStreamId(id), new Values(removeDataObjectList));
//				}
//			}
//			sourcesInformations.get(source).getLastBoltTasKInformation().remove(dataObject.getObjectId());
//		} else if (SpatioTextualConstants.addCommand.equals(dataObject.getCommand()) || SpatioTextualConstants.updateCommand.equals(dataObject.getCommand()) || dataObject.getCommand() == null //assuming that a null data object command means an add command
//		) {
//			//the underlying datasource may not be aware if the incomming data object is an update to a previous dataobject 
//			//or if it is a new data object
//			DataObjectList dataObjectList = new DataObjectList();
//			dataObjectList.addDataObject(dataObject);
//
//			ArrayList<EvaluatorBoltHistory> evalauatorTaskList = mapDataObjectToPartitions(dataObject);
//			// TODO handle all cases of object, current, persistent with window,
//			// volatile
//			if (sourcesInformations.get(source).isCurrent()) {
//				ArrayList<EvaluatorBoltHistory> previousEvalauatorTaskList = sourcesInformations.get(source).getLastBoltTasKInformation().get(dataObject.getObjectId());
//				sourcesInformations.get(source).getLastBoltTasKInformation().put(dataObject.getObjectId(), evalauatorTaskList);
//
//				if (previousEvalauatorTaskList != null && previousEvalauatorTaskList.size() != 0) {
//					//this means there are previous location information about this object and hence 
//					//we make sure that the command for this dataobject is an update 
//					dataObject.setCommand(SpatioTextualConstants.updateCommand);
//					for (EvaluatorBoltHistory task : previousEvalauatorTaskList)
//						if (!evalauatorTaskList.contains(task)) {
//							DataObject removeDataObject = new DataObject();
//							removeDataObject.setCommand(SpatioTextualConstants.updateDropCommand);
//							removeDataObject.setObjectId(dataObject.getObjectId());
//							removeDataObject.setSrcId(dataObject.getSrcId());
//							DataObjectList removeDataObjectList = new DataObjectList();
//							removeDataObjectList.addDataObject(removeDataObject);
//							collector.emitDirect(task.getTaskId(), SpatioTextualConstants.getIndexBoltDataStreamId(id), new Values(removeDataObjectList));
//						}
//				} else {
//					//this means there are NO previous location information about this object and hence 
//					//we make sure that the command for this dataobject is an add 
//					dataObject.setCommand(SpatioTextualConstants.addCommand);
//				}
//			} else if (sourcesInformations.get(source).isPersistent()) {
//				//TODO handle persient data and sliding window 
//			}
//			//sending the new dataobject information to proper bolt task 
//			for (EvaluatorBoltHistory task : evalauatorTaskList)
//				collector.emitDirect(task.getTaskId(), SpatioTextualConstants.getIndexBoltDataStreamId(id), new Values(dataObjectList));
//		}
//
//	}
//
//	private void removeDataObject(DataObject dataObject) {
//
//	}
//
//	private DataObject readDataObject(Tuple input, String source) {
//		DataObject dataObject = new DataObject();
//		dataObject.setSrcId(source);
//		if (input.contains(SpatioTextualConstants.dataObjectCommand))
//			dataObject.setCommand(input.getStringByField(SpatioTextualConstants.dataObjectCommand));
//		if (input.contains(SpatioTextualConstants.objectIdField))
//			dataObject.setObjectId(input.getStringByField(SpatioTextualConstants.objectIdField));
//		if (input.contains(SpatioTextualConstants.objectXCoordField))
//			dataObject.getLocation().setX(input.getDoubleByField(SpatioTextualConstants.objectXCoordField));
//		if (input.contains(SpatioTextualConstants.objectYCoordField))
//			dataObject.getLocation().setY(input.getDoubleByField(SpatioTextualConstants.objectYCoordField));
//		if (input.contains(SpatioTextualConstants.objectTextField)) {
//			String objectText = input.getStringByField(SpatioTextualConstants.objectTextField);
//			ArrayList<String> objectTextList = TextHelpers.transformIntoSortedArrayListOfString(objectText);
//			dataObject.setOriginalText(objectText);
//			dataObject.setObjectText(objectTextList);
//		}
//		if (input.contains(SpatioTextualConstants.timeStamp))
//			dataObject.setTimeStamp(input.getLongByField(SpatioTextualConstants.timeStamp));
//		return dataObject;
//
//	}
//
//	@Override
//	public void declareOutputFields(OutputFieldsDeclarer declarer) {
//		declarer.declareStream(id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, new Fields(SpatioTextualConstants.query));
//		declarer.declareStream(SpatioTextualConstants.getIndexBoltDataStreamId(id), new Fields(SpatioTextualConstants.data));
//		declarer.declareStream(id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Control, new Fields(SpatioTextualConstants.control));
//
//		declarer.declareStream(id + SpatioTextualConstants.Index_Index_STreamIDExtension_Query, new Fields(SpatioTextualConstants.query));
//		declarer.declareStream(id + SpatioTextualConstants.Index_Index_STreamIDExtension_Data, new Fields(SpatioTextualConstants.data));
//		declarer.declareStream(id + SpatioTextualConstants.Index_Index_STreamIDExtension_Control, new Fields(SpatioTextualConstants.control));
//
//	}
//
//	private void enterBarrier() {
//		newExecutorsTasks = new ArrayList<Integer>(executorsTasks);
//		System.out.println(myId + ": splitMergeInfo.newAuxiliaryIndex = " + splitMergeInfo.newAuxiliaryIndex);
//		System.out.println(myId + ": executorsTasks.get(splitMergeInfo.newAuxiliaryIndex) = " + executorsTasks.get(splitMergeInfo.newAuxiliaryIndex));
//		newAuxiliaryExecutorTask = executorsTasks.get(splitMergeInfo.newAuxiliaryIndex);
//		newExecutorsTasks.set(splitMergeInfo.newAuxiliaryIndex, auxiliaryExecutorTask);
//		LoadBalanceMessage balanceMessage = new LoadBalanceMessage();
//		balanceMessage.setNewExecuterTasks(newExecutorsTasks);
//		balanceMessage.setNewAuxiliaryExecutorTask(newAuxiliaryExecutorTask);
//		balanceMessage.setNewPartitions(globalDynamicIndex.newPartitions);
//		balanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.GOIING_TO_BARRIER);
//		Control contorlMesage = new Control();
//		contorlMesage.setControlMessageType(Control.LOAD_BALANCE);
//		//collector.emitDirect(auxiliaryExecutorTask, "GoingToBarrier", new Values(newExecutorsTasks,newAuxiliaryExecutorTask,LB.newPartitions));
//		collector.emitDirect(auxiliaryExecutorTask, SpatioTextualConstants.getIndexBoltControlStreamId(id), new Values(contorlMesage));
//
//		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA " + myId + " Received enterBarrier Stream");
//		try {
//			ZK.waitOnBarrier();
//		} catch (Exception e) {
//			System.err.println(myId + " could not wait on barrier " + e);
//		}
//
//		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXX " + myId + " : Get out from barrier XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//
//		executorsTasks = new ArrayList<Integer>(newExecutorsTasks);
//		auxiliaryExecutorTask = newAuxiliaryExecutorTask;
//		globalDynamicIndex.switchToNewPartitions();
//
//		int splitChild0index = splitMergeInfo.splitChild0.index;
//		int splitChild1index = splitMergeInfo.splitChild1.index;
//		int mergeParentIndex = splitMergeInfo.mergeParent.index;
//
//		splitMergeInfo = null;
//		splitFinished = false;
//		mergeFinished = false;
//
//		if (indexBoltTurn < numberParallelismIndexBolts - 1)
//			indexBoltTurn++;
//		else
//			indexBoltTurn = 0;
//
//	}

}
