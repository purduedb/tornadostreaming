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
import edu.purdue.cs.tornado.helper.StringHelpers;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.DataObjectList;
import edu.purdue.cs.tornado.messages.Query;

/**
 * 
 * This class is for build a static global index of streamed data
 * 
 * @author Ahmed Mahmood
 *
 */
public class StaticSpatialIndexBolt extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * here we are assuming point objects every point can go only to a single
	 * query node
	 */
	private String id;
	private String selfComponentId;
	private Integer selfTaskId;
	private Integer selfTaskIndex;
	// **************** Evaluator bolts attributes **********************
	private Integer numberOfEvaluatorTasks;
	private List<Integer> evaluatorBoltTasks; // this keeps track of the evaluator bolts ids
	private Map<String, DataSourceInformation> sourcesInformations; // this keeps track of the type of every input source

	// ******************Grid parameters ********************************
	GlobalGridIndex globalGridIndex;

	// ******************Textual Semantic Attributes ********************************
	DISCO disco;

	// *******************Storm specific attributes *********************
	private Map stormConf; // configuration
	private TopologyContext context; // storm context
	private OutputCollector collector;

	private ArrayList<EvaluatorBoltHistory> mapQueryToPartitions(Query query) {
		ArrayList<EvaluatorBoltHistory> previousBoltHistory = new ArrayList<EvaluatorBoltHistory>();
		ArrayList<Integer> tasks = new ArrayList<Integer>();
		if (SpatioTextualConstants.queryTextualKNN.equals(query.getQueryType())) {
			tasks = globalGridIndex.getTaskIDsContainingPoint(query.getFocalPoint());

		} else if (SpatioTextualConstants.queryTextualRange.equals(query.getQueryType()) || SpatioTextualConstants.queryTextualSpatialJoin.equals(query.getQueryType())) {
			tasks = globalGridIndex.getTaskIDsOverlappingRecangle(query.getSpatialRange());
		}
		for (Integer task : tasks)
			previousBoltHistory.add(new EvaluatorBoltHistory(task, query.getTimeStamp()));
		return previousBoltHistory;
	}

	private ArrayList<EvaluatorBoltHistory> mapDataObjectToPartitions(DataObject dataObject) {
		ArrayList<EvaluatorBoltHistory> previousBoltHistory = new ArrayList<EvaluatorBoltHistory>();
		ArrayList<Integer> tasks = new ArrayList<Integer>();
		tasks = globalGridIndex.getTaskIDsContainingPoint(dataObject.getLocation());
		for (Integer task : tasks)
			previousBoltHistory.add(new EvaluatorBoltHistory(task, dataObject.getTimeStamp()));
		return previousBoltHistory;
	}

	/**
	 * Construcor
	 * 
	 * @param id
	 */
	public StaticSpatialIndexBolt(String id) {
		this.id = id;

	}

	/**
	 * This function reads the configuration of the data sources and builds
	 * required data structures
	 */
	private void readDataSourcesInformation() {
		System.out.println("***************************Evaluating bolt configuration*******************");
		Iterator it = stormConf.entrySet().iterator();
		sourcesInformations = new HashMap<String, DataSourceInformation>();
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
			else if (value.contains(SpatioTextualConstants.Static))
				persistenceState = SpatioTextualConstants.staticPersistenceState;
			else if (value.contains(SpatioTextualConstants.Persistent))
				persistenceState = SpatioTextualConstants.persistentPersistenceState;
			else if (value.contains(SpatioTextualConstants.Current))
				persistenceState = SpatioTextualConstants.currentPersistenceState;

			String cleanState = SpatioTextualConstants.NOTCLEAN;
			if (stormConf.containsKey(SpatioTextualConstants.getVolatilePropertyKey(sourceId)))
				cleanState = (String) stormConf.get(SpatioTextualConstants.getVolatilePropertyKey(sourceId));
			DataSourceInformation dataSourcesInformation = new DataSourceInformation(sourceId, sourceType, persistenceState, cleanState);

			sourcesInformations.put(sourceId, dataSourcesInformation);
			System.out.println(key + " = " + value);

		}

	}

	// collecting the topology information
	// prepare the grid index cells
	// set the evaluator bolt task ids
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {

		this.context = context;
		this.collector = collector;
		this.stormConf = stormConf;
		this.evaluatorBoltTasks = context.getComponentTasks(id);
		numberOfEvaluatorTasks = this.evaluatorBoltTasks.size();
		selfComponentId = context.getThisComponentId();
		selfTaskId = context.getThisTaskId();
		selfTaskIndex = context.getThisTaskIndex();
		globalGridIndex = new GlobalGridIndex(numberOfEvaluatorTasks, evaluatorBoltTasks);

		//TODO maybe move this to a seperate bolt and adjust the topology accordingly
		if (this.stormConf.get(SpatioTextualConstants.discoDir) != null)
			disco = SemanticHelper.getDiskBasedDiscoInstance((String) this.stormConf.get(SpatioTextualConstants.discoDir));
		else
			disco = null;

		readDataSourcesInformation();
	}

	@Override
	public synchronized void execute(Tuple input) {
		// get tuple source information
		try {
			String source = input.getSourceComponent();
			String streamId = input.getSourceStreamId();
			if (!SpatioTextualConstants.Default.equals(streamId))
				source = source + "_" + streamId;

			String sourceType = sourcesInformations.get(source).getDataSourceType();

			if (SpatioTextualConstants.Query_Source.equals(sourceType)) {
				handleQuery(input, source);
			} else if (SpatioTextualConstants.Data_Source.equals(sourceType)) {
				handleData(input, source);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

	}

	private void handleQuery(Tuple input, String source) {
		// identify the evaluator bolt to submit your tuple to
		//TODO add code handling for the special case of query update and query droping
		String queryType = input.getStringByField(SpatioTextualConstants.queryTypeField);
		Query query = readQueryByType(input, queryType, source);

		// update the last evaluator bolt information
		if (sourcesInformations.get(source).isContinuous()) {
			ArrayList<EvaluatorBoltHistory> previousEvalauatorTaskList = sourcesInformations.get(source).getLastBoltTasKInformation().get(query.getQueryId());

			if (previousEvalauatorTaskList != null && SpatioTextualConstants.dropCommand.equals(query.getCommand())) {
				for (EvaluatorBoltHistory task : previousEvalauatorTaskList) {
					collector.emitDirect(task.getTaskId(), id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, new Values(query));
				}
				sourcesInformations.get(source).getLastBoltTasKInformation().put(query.getQueryId(), null);
			} else if (SpatioTextualConstants.addCommand.equals(query.getCommand())) {
				ArrayList<EvaluatorBoltHistory> evalauatorTaskList = mapQueryToPartitions(query);

				sourcesInformations.get(source).getLastBoltTasKInformation().put(query.getQueryId(), evalauatorTaskList);
				if (previousEvalauatorTaskList != null && previousEvalauatorTaskList.size() != 0) {
					for (EvaluatorBoltHistory task : previousEvalauatorTaskList) {
						if (!evalauatorTaskList.contains(task)) {
							Query removeQuery = new Query();
							removeQuery.setCommand(SpatioTextualConstants.dropCommand);
							removeQuery.setQueryId(query.getQueryId());
							removeQuery.setSrcId(query.getSrcId());
							collector.emitDirect(task.getTaskId(), id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, new Values(removeQuery));
						}
					}
				}
				for (EvaluatorBoltHistory task : evalauatorTaskList)
					collector.emitDirect(task.getTaskId(), id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, new Values(query));

			} else if (SpatioTextualConstants.updateCommand.equals(query.getCommand())) {
				//TODO carefully handle updates
			}
		} else {//this query is snapshot
				//just submit the query and do not maintain any information about it 
			ArrayList<EvaluatorBoltHistory> evalauatorTaskList = mapQueryToPartitions(query);
			for (EvaluatorBoltHistory task : evalauatorTaskList)
				collector.emitDirect(task.getTaskId(), id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, new Values(query));
		}
	}

	private Query readQueryByType(Tuple input, String queryType, String source) {
		Query query = new Query();
		query.setQueryId(input.getStringByField(SpatioTextualConstants.queryIdField));
		query.setSrcId(source);
		query.setQueryType(queryType);
		query.setTimeStamp(input.getLongByField(SpatioTextualConstants.queryTimeStampField));
		query.setDataSrc(input.getStringByField(SpatioTextualConstants.dataSrc));
		query.setCommand(input.getStringByField(SpatioTextualConstants.queryCommand));
		String text = "", text2 = "";
		if (input.contains(SpatioTextualConstants.textualPredicate)) {
			query.setTextualPredicate(input.getStringByField(SpatioTextualConstants.textualPredicate));
		} else {
			query.setTextualPredicate(SpatioTextualConstants.none);
		}
		if (input.contains(SpatioTextualConstants.textualPredicate2)) {
			query.setTextualPredicate2(input.getStringByField(SpatioTextualConstants.textualPredicate2));
		} else {
			query.setTextualPredicate2(SpatioTextualConstants.none);
		}
		if (input.contains(SpatioTextualConstants.queryTextField)) {
			text = input.getStringByField(SpatioTextualConstants.queryTextField);
			ArrayList<String> queryText = new ArrayList<String>();
			if (text != null && !"".equals(text)) {
				queryText = StringHelpers.transformIntoSortedArrayListOfString(text);
				if (SpatioTextualConstants.semantic.equals(query.getTextualPredicate()) && disco != null) {
					ArrayList<String> similarKeyWord = SemanticHelper.getSematicallySimilarKeyWords(disco, queryText);
					queryText = StringHelpers.sortTextArrayList(similarKeyWord);

					query.setTextualPredicate(SpatioTextualConstants.semantic);
				}
			} else {
				query.setTextualPredicate(SpatioTextualConstants.none);
			}
			query.setQueryText(queryText);

		}
		if (input.contains(SpatioTextualConstants.queryText2Field)) {
			text2 = input.getStringByField(SpatioTextualConstants.queryText2Field);
			ArrayList<String> queryText = new ArrayList<String>();
			if (text2 != null && !"".equals(text2)) {
				queryText = StringHelpers.transformIntoSortedArrayListOfString(text2);
				if (SpatioTextualConstants.semantic.equals(query.getTextualPredicate()) && disco != null) {
					ArrayList<String> similarKeyWord = SemanticHelper.getSematicallySimilarKeyWords(disco, queryText);
					queryText = StringHelpers.sortTextArrayList(similarKeyWord);
					query.setTextualPredicate2(SpatioTextualConstants.semantic);
				}
			} else {
				query.setTextualPredicate2(SpatioTextualConstants.none);
			}
			query.setQueryText2(queryText);

		}

		if (input.contains(SpatioTextualConstants.joinTextualPredicate)) {
			query.setJoinTextualPredicate(input.getStringByField(SpatioTextualConstants.joinTextualPredicate));
		} else {
			query.setJoinTextualPredicate(SpatioTextualConstants.none);
		}

		if (SpatioTextualConstants.queryTextualKNN.equals(queryType)) {
			query.setK(input.getIntegerByField(SpatioTextualConstants.kField));
			query.getFocalPoint().setX(input.getDoubleByField(SpatioTextualConstants.focalXCoordField));
			query.getFocalPoint().setY(input.getDoubleByField(SpatioTextualConstants.focalYCoordField));
			query.setSpatialRange(new Rectangle(query.getFocalPoint(), query.getFocalPoint()));
		} else if (SpatioTextualConstants.queryTextualRange.equals(queryType) || (SpatioTextualConstants.queryTextualSpatialJoin.equals(queryType))) {
			Point min = new Point();
			min.setX(input.getDoubleByField(SpatioTextualConstants.queryXMinField));
			min.setY(input.getDoubleByField(SpatioTextualConstants.queryYMinField));
			Point max = new Point();
			max.setX(input.getDoubleByField(SpatioTextualConstants.queryXMaxField));
			max.setY(input.getDoubleByField(SpatioTextualConstants.queryYMaxField));
			query.setSpatialRange(new Rectangle(min, max));
			if (SpatioTextualConstants.queryTextualSpatialJoin.equals(queryType)) {
				query.setDataSrc2(input.getStringByField(SpatioTextualConstants.dataSrc2));
				query.setDistance(input.getDoubleByField(SpatioTextualConstants.queryDistance));
			}
		}

		return query;
	}

	private void handleData(Tuple input, String source) {
		// identify the evaluator bolt to submit your tuple to
		// if persistent update the last evaluator bolt information and emit to
		// the previous bolt information
		// to invalidate the previous tuple,
		// the evaluator bolt should detect this and remove the previous tuple
		// and the send to the proper bolt
		DataObject dataObject = readDataObject(input, source);
		if (SpatioTextualConstants.dropCommand.equals(dataObject.getCommand())) {
			ArrayList<EvaluatorBoltHistory> previousEvalauatorTaskList = sourcesInformations.get(source).getLastBoltTasKInformation().get(dataObject.getObjectId());
			if (previousEvalauatorTaskList != null && previousEvalauatorTaskList.size() != 0) {
				for (EvaluatorBoltHistory task : previousEvalauatorTaskList) {
					DataObject removeDataObject = new DataObject();
					removeDataObject.setCommand(SpatioTextualConstants.dropCommand);
					removeDataObject.setObjectId(dataObject.getObjectId());
					removeDataObject.setSrcId(dataObject.getSrcId());
					DataObjectList removeDataObjectList = new DataObjectList();
					removeDataObjectList.addDataObject(removeDataObject);
					collector.emitDirect(task.getTaskId(), id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Data, new Values(removeDataObjectList));
				}
			}
			sourcesInformations.get(source).getLastBoltTasKInformation().remove(dataObject.getObjectId());
		} else if (SpatioTextualConstants.addCommand.equals(dataObject.getCommand()) || SpatioTextualConstants.updateCommand.equals(dataObject.getCommand()) || dataObject.getCommand() == null //assuming that a null data object command means an add command
		) {
			DataObjectList dataObjectList = new DataObjectList();
			dataObjectList.addDataObject(dataObject);

			ArrayList<EvaluatorBoltHistory> evalauatorTaskList = mapDataObjectToPartitions(dataObject);
			// TODO handle all cases of object, current, persistent with window,
			// volatile
			if (sourcesInformations.get(source).isCurrent()) {
				ArrayList<EvaluatorBoltHistory> previousEvalauatorTaskList = sourcesInformations.get(source).getLastBoltTasKInformation().get(dataObject.getObjectId());
				sourcesInformations.get(source).getLastBoltTasKInformation().put(dataObject.getObjectId(), evalauatorTaskList);

				if (previousEvalauatorTaskList != null && previousEvalauatorTaskList.size() != 0) {
					//this means there are previous location information about this object and hence 
					//we make sure that the command for this dataobject is an update 
					dataObject.setCommand(SpatioTextualConstants.updateCommand);
					for (EvaluatorBoltHistory task : previousEvalauatorTaskList)
						if (!evalauatorTaskList.contains(task)) {
							DataObject removeDataObject = new DataObject();
							removeDataObject.setCommand(SpatioTextualConstants.dropCommand);
							removeDataObject.setObjectId(dataObject.getObjectId());
							removeDataObject.setSrcId(dataObject.getSrcId());
							DataObjectList removeDataObjectList = new DataObjectList();
							removeDataObjectList.addDataObject(removeDataObject);
							collector.emitDirect(task.getTaskId(), id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Data, new Values(removeDataObjectList));
						}
				} else {
					//this means there are NO previous location information about this object and hence 
					//we make sure that the command for this dataobject is an add 
					dataObject.setCommand(SpatioTextualConstants.addCommand);
				}
			} else if (sourcesInformations.get(source).isPersistent()) {
				//TODO handle persient data and sliding window 
			}
			//sending the new dataobject information to proper bolt task 
			for (EvaluatorBoltHistory task : evalauatorTaskList)
				collector.emitDirect(task.getTaskId(), id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Data, new Values(dataObjectList));
		}

	}

	private void removeDataObject(DataObject dataObject) {

	}

	private DataObject readDataObject(Tuple input, String source) {
		DataObject dataObject = new DataObject();
		dataObject.setSrcId(source);
		if (input.contains(SpatioTextualConstants.dataObjectCommand))
			dataObject.setCommand(input.getStringByField(SpatioTextualConstants.dataObjectCommand));
		if (input.contains(SpatioTextualConstants.objectIdField))
			dataObject.setObjectId(input.getStringByField(SpatioTextualConstants.objectIdField));
		if (input.contains(SpatioTextualConstants.objectXCoordField))
			dataObject.getLocation().setX(input.getDoubleByField(SpatioTextualConstants.objectXCoordField));
		if (input.contains(SpatioTextualConstants.objectYCoordField))
			dataObject.getLocation().setY(input.getDoubleByField(SpatioTextualConstants.objectYCoordField));
		if (input.contains(SpatioTextualConstants.objectTextField)) {
			String objectText = input.getStringByField(SpatioTextualConstants.objectTextField);
			ArrayList<String> objectTextList = StringHelpers.transformIntoSortedArrayListOfString(objectText);
			dataObject.setOriginalText(objectText);
			dataObject.setObjectText(objectTextList);
		}
		if (input.contains(SpatioTextualConstants.timeStamp))
			dataObject.setTimeStamp(input.getLongByField(SpatioTextualConstants.timeStamp));
		return dataObject;

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, new Fields(SpatioTextualConstants.query));
		declarer.declareStream(id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Data, new Fields(SpatioTextualConstants.data));
		declarer.declareStream(id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Control, new Fields(SpatioTextualConstants.control));

	}

}
