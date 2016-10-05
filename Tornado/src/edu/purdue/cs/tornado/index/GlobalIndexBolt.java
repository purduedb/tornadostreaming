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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.Constants;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.DataSourceType;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.QueryType;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.index.global.GlobalGridIndex;
import edu.purdue.cs.tornado.index.global.GlobalIndex;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.global.GlobalOptimizedPartitionedIndexLowerSpace;
import edu.purdue.cs.tornado.index.global.GlobalOptimizedPartitionedTextAwareIndex;
import edu.purdue.cs.tornado.index.global.RandomTextRouting;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.messages.Control;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.DataObjectList;
import edu.purdue.cs.tornado.messages.JoinQuery;
import edu.purdue.cs.tornado.messages.KNNQuery;
import edu.purdue.cs.tornado.messages.Query;

/**
 * 
 * This class is for build a static global index of streamed data
 * 
 * @author Ahmed Mahmood
 *
 */
public class GlobalIndexBolt extends BaseRichBolt {

	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	/**
	 * here we are assuming point objects every point can go only to a single
	 * query node
	 */
	protected String id;
	protected Boolean debug;
	protected static final String UNDER_SCORE = "_";
	protected StringBuilder stringBuilder;
	protected String selfComponentId;
	protected Integer selfTaskId;
	protected Integer selfTaskIndex;
	// **************** Evaluator bolts attributes **********************
	protected Integer numberOfEvaluatorTasks;
	protected List<Integer> evaluatorBoltTasks; // this keeps track of the evaluator bolts IDs
	protected List<Integer> indexBoltTasks;
	protected Map<String, DataSourceInformation> sourcesInformations; // this keeps track of the type of every input source
	protected Boolean reliable;

	// ******************Index parameters ********************************
	public Rectangle entireSpace;
	public GlobalIndex globalIndex;
	public GlobalIndexType globalIndexType;
	public ArrayList<Cell> partitions;
	Integer fineGridGran;

	// ******************Textual Semantic Attributes ********************************
	//DISCO disco;

	// *******************Storm specific attributes *********************
	protected Map stormConf; // configuration
	protected TopologyContext context; // storm context
	protected OutputCollector collector;

	//************************Statistics attributes *********************
	//transient CountMetric _inputDataCountMetric;
	//transient ReducedMetric _indexingDataTimeMeanMetric;
	int count;

	/**
	 * Construcor
	 * 
	 * @param id
	 */
	public GlobalIndexBolt(String id, ArrayList<Cell> partitions, GlobalIndexType globalIndexType, Integer fineGridGran) {
		this.id = id;
		this.partitions = partitions;
		this.globalIndexType = globalIndexType;
		this.fineGridGran = fineGridGran;
	}

	public GlobalIndexBolt(String id, GlobalIndexType globalIndexType, Integer fineGridGran) {
		this.id = id;
		this.partitions = null;
		this.globalIndexType = globalIndexType;
		this.fineGridGran = fineGridGran;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(SpatioTextualConstants.getIndexBoltQueryStreamId(id), new Fields(SpatioTextualConstants.query));
		declarer.declareStream(SpatioTextualConstants.getIndexBoltDataStreamId(id), new Fields(SpatioTextualConstants.data));
		declarer.declareStream(SpatioTextualConstants.getIndexBoltControlStreamId(id), new Fields(SpatioTextualConstants.control));

		declarer.declareStream(SpatioTextualConstants.getIndexIndexQueryStreamId(id), new Fields(SpatioTextualConstants.query));
		declarer.declareStream(SpatioTextualConstants.getIndexIndexDataStreamId(id), new Fields(SpatioTextualConstants.data));
		declarer.declareStream(SpatioTextualConstants.getIndexIndexControlStreamId(id), new Fields(SpatioTextualConstants.control));

	}

	@Override
	public void execute(Tuple input) {

		// get tuple source information
		try {
			String source = getSourceName(input);
			DataSourceType sourceType = getDataSourceType(source);
			if (DataSourceType.DATA_SOURCE.equals(sourceType)) {
				Boolean ack = handleData(input, source.toString());
				//				if (ack)
				//					collector.ack(input);
			} else if (DataSourceType.QUERY_SOURCE.equals(sourceType)) {
				handleQuery(input, source.toString());
				//		collector.ack(input);
			} else if (DataSourceType.CONTROL.equals(sourceType)) {
				handleControl(input);
				//	collector.ack(input);
			} else if (isTickTuple(input)) {
				handleTickTuple(input);
				//	collector.ack(input);
			}
			//			if (count >= 100000) {
			//				count = 0;
			//				Thread.sleep(10);
			//			}
			//			count++;
		} catch (Exception e) {
			e.printStackTrace(System.err);

		}

	}

	protected DataSourceType getDataSourceType(String source) {
		DataSourceType sourceType = null;
		if (sourcesInformations.containsKey(source))
			sourceType = sourcesInformations.get(source).getDataSourceType();
		else if (SpatioTextualConstants.isControlStreamSource(source))
			sourceType = DataSourceType.CONTROL;
		return sourceType;
	}

	protected String getSourceName(Tuple input) {
		String source = input.getSourceComponent();
		String streamId = input.getSourceStreamId();
		if (!SpatioTextualConstants.Default.equals(streamId)) {
			stringBuilder = new StringBuilder(source).append(UNDER_SCORE).append(streamId);
			source = stringBuilder.toString();
		}
		return source;
	}

	protected void handleControl(Tuple tuple) {
		Control controlMessage = (Control) tuple.getValueByField(SpatioTextualConstants.control);
		if (Control.TEXT_SUMMERY.equals(controlMessage.getControlMessageType())) {
			if (controlMessage.textSummeryTaskIdList != null && controlMessage.textSummery != null) {
				//TODO we need to handle more than one data source in the global index to address the update in text summary
				if (globalIndex.isTextAware()) {
					((GlobalOptimizedPartitionedTextAwareIndex) globalIndex).dropTextFromTaskID(controlMessage.textSummeryTaskIdList, controlMessage.textSummery, controlMessage.getTextSummaryTimeStamp());
				}
			}
		} else if (Control.INDEX_TEXT_SUMMERY.equals(controlMessage.getControlMessageType())) {
			if (this.selfTaskIndex != controlMessage.getForwardTextIndexTaskIndex()) {
				HashSet<String> textSummary = controlMessage.getTextSummery();
				((GlobalOptimizedPartitionedTextAwareIndex) globalIndex).addTextToTaskID(controlMessage.getTextSummeryTaskIdList(), new ArrayList<String>(textSummary), true, false);
			}
		}
	}

	protected static boolean isTickTuple(Tuple tuple) {
		return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID) && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
	}

	protected static String getTickTupleSourceId() {
		return "" + Constants.SYSTEM_COMPONENT_ID + "_" + Constants.SYSTEM_TICK_STREAM_ID;
	}

	protected void handleTickTuple(Tuple tuple) {

	}

	protected boolean handleData(Tuple input, String source) throws Exception {
		// identify the evaluator bolt to submit your tuple to
		// if persistent update the last evaluator bolt information and emit to
		// the previous bolt information
		// to invalidate the previous tuple,
		// the evaluator bolt should detect this and remove the previous tuple
		// and the send to the proper bolt
		DataObject dataObject = readDataObject(input, source);
		//	boolean ack = dataObject.getObjectId() %10 == 0;
		boolean ack = false;
		if (Command.addCommand.equals(dataObject.getCommand()) || Command.updateCommand.equals(dataObject.getCommand()) || dataObject.getCommand() == null //assuming that a null data object command means an add command
		) {
			//the underlying data source may not be aware if the incoming data object is an update to a previous data object 
			//or if it is a new data object
			//			DataObjectList dataObjectList = new DataObjectList();
			//			dataObjectList.addDataObject(dataObject);
			if (globalIndex.isTextOnlyIndex()) {
				ArrayList<Integer> evaluatorsTasks = globalIndex.getTaskIDsContainingKeywordData(dataObject);
				for(Integer evalauatorTask: evaluatorsTasks)
					collector.emitDirect(evalauatorTask, SpatioTextualConstants.getIndexBoltDataStreamId(id), new Values(dataObject));

			} else {
				Integer evalauatorTask = globalIndex.getTaskIDsContainingPoint(dataObject.getLocation());
				if (sourcesInformations.get(source).isCurrent()) {
					Integer previousEvalauatorTask = sourcesInformations.get(source).getDataLastBoltTasKInformation().get(dataObject.getObjectId());
					//	sourcesInformations.get(source).getLastBoltTasKInformation().put(dataObject.getObjectId(), evalauatorTaskList);
					if (!evalauatorTask.equals(previousEvalauatorTask)) {
						//this means there are previous location information about this object and hence 
						//we make sure that the command for this dataobject is an update 
						DataObject removeDataObject = new DataObject();
						removeDataObject.setCommand(Command.dropCommand);
						removeDataObject.setObjectId(dataObject.getObjectId());
						removeDataObject.setSrcId(dataObject.getSrcId());
						DataObjectList removeDataObjectList = new DataObjectList();
						removeDataObjectList.addDataObject(removeDataObject);
						collector.emitDirect(previousEvalauatorTask, SpatioTextualConstants.getIndexBoltDataStreamId(id), new Values(removeDataObjectList));
					}
					sourcesInformations.get(source).getDataLastBoltTasKInformation().put(dataObject.getObjectId(), evalauatorTask);
				}
				//sending the new dataobject information to proper bolt task 
				if (globalIndex.isTextAware()) {
					if (globalIndex.verifyTextOverlap(evalauatorTask, dataObject.getObjectText())) {
						//					if (ack)
						//						collector.emitDirect(evalauatorTask, SpatioTextualConstants.getIndexBoltDataStreamId(id), input, new Values(dataObject));
						//					else
						collector.emitDirect(evalauatorTask, SpatioTextualConstants.getIndexBoltDataStreamId(id), new Values(dataObject));
					} else {
						//		_inputDataCountMetric.incr();
					}
				} else {

					collector.emitDirect(evalauatorTask, SpatioTextualConstants.getIndexBoltDataStreamId(id), new Values(dataObject));
				}
			}
		}
		return ack;

	}

	protected void handleDataTest(Tuple input, String source) throws Exception {
		DataObject dataObject = readDataObject(input, source);
		if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), this.entireSpace)) {
			Integer evalauatorTask = globalIndex.getTaskIDsContainingPoint(dataObject.getLocation());
			collector.emitDirect(evalauatorTask, SpatioTextualConstants.getIndexBoltDataStreamId(id), new Values(dataObject));
		} else {
			System.err.println("Data object is outside the range of data " + dataObject.toString());
		}
	}

	protected void handleQuery(Tuple input, String source) throws Exception {
		// identify the evaluator bolt to submit your tuple to
		//TODO add code handling for the special case of query update and query droping
		QueryType queryType = (QueryType) input.getValueByField(SpatioTextualConstants.queryTypeField);
		//	Query query =(Query)input.getValueByField(SpatioTextualConstants.query); 
		Query query = readQueryByType(input, queryType, source);

		// update the last evaluator bolt information
		//	if (sourcesInformations.get(source).isContinuous()) {
		//	ArrayList<Integer> previousEvalauatorTaskList = sourcesInformations.get(source).getQueryLastBoltTasKInformation().get(query.getQueryId());
		//			if (previousEvalauatorTaskList != null && Command.dropCommand.equals(query.getCommand())) {
		//				for (Integer task : previousEvalauatorTaskList) {
		//					collector.emitDirect(task, id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, new Values(query));
		//				}
		//				sourcesInformations.get(source).getQueryLastBoltTasKInformation().put(query.getQueryId(), null);
		//			} else 
		if (Command.addCommand.equals(query.getCommand())) {
			ArrayList<Integer> evalauatorTaskList = null;

			sourcesInformations.get(source).getQueryLastBoltTasKInformation().put(query.getQueryId(), evalauatorTaskList);
			//				if (previousEvalauatorTaskList != null && previousEvalauatorTaskList.size() != 0) {
			//					for (Integer task : previousEvalauatorTaskList) {
			//						if (!evalauatorTaskList.contains(task)) {
			//							Query removeQuery = new Query();
			//							removeQuery.setCommand(Command.dropCommand);
			//							removeQuery.setQueryId(query.getQueryId());
			//							removeQuery.setSrcId(query.getSrcId());
			//							collector.emitDirect(task, id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, new Values(removeQuery));
			//						}
			//					}
			//				}
			if (globalIndex.isTextOnlyIndex()) {
				evalauatorTaskList = globalIndex.getTaskIDsContainingKeywordQuery(query);
				sourcesInformations.get(source).getQueryLastBoltTasKInformation().put(query.getQueryId(), evalauatorTaskList);
				for (Integer task : evalauatorTaskList)
					collector.emitDirect(task, id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, input, new Values(query));

			} else {
				evalauatorTaskList = mapQueryToPartitions(query);
				sourcesInformations.get(source).getQueryLastBoltTasKInformation().put(query.getQueryId(), evalauatorTaskList);
				if (globalIndex.isTextAware()) {
					HashSet<String> text = globalIndex.addTextToTaskID(evalauatorTaskList, query.getQueryText(), query.getTextualPredicate() == TextualPredicate.OVERlAPS, isForwardGlobalIndex());
					if (isForwardGlobalIndex()) {
						for (Integer task : evalauatorTaskList)
							collector.emitDirect(task, id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, input, new Values(query));
						if (text != null) {
							Control contorlMesage = new Control();
							contorlMesage.setControlMessageType(Control.INDEX_TEXT_SUMMERY);
							contorlMesage.setTextSummeryTaskIdList(evalauatorTaskList);
							contorlMesage.setForwardTextIndexTaskIndex(this.selfTaskIndex);
							contorlMesage.setTextSummery(text);
							contorlMesage.setTextSummeryTaskIdList(evalauatorTaskList);
							collector.emit(SpatioTextualConstants.getIndexIndexControlStreamId(id), new Values(contorlMesage));

						}
					} else {
						if (query.getQueryId() % this.indexBoltTasks.size() == this.selfTaskIndex) //only one evalautor should send the query 
							for (Integer task : evalauatorTaskList)
								collector.emitDirect(task, id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, input, new Values(query));
					}

				} else
					for (Integer task : evalauatorTaskList)
						collector.emitDirect(task, id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, input, new Values(query));
			}
		} else if (Command.updateCommand.equals(query.getCommand())) {
		}
		//		} else {//this query is snapshot
		//				//just submit the query and do not maintain any information about it 
		//			ArrayList<Integer> evalauatorTaskList = mapQueryToPartitions(query);
		//			for (Integer task : evalauatorTaskList)
		//				collector.emitDirect(task, id + SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query, new Values(query));
		//		}
	}

	boolean isForwardGlobalIndex() {
		return globalIndexType == GlobalIndexType.PARTITIONED_TEXT_AWARE_FORWARD;
	}

	void initMetrics(TopologyContext context) {
		//		_inputDataCountMetric = new CountMetric();
		//		context.registerMetric("Unemitted_counter", _inputDataCountMetric, 20);
	}

	protected ArrayList<Integer> mapQueryToPartitions(Query query) throws Exception {
		ArrayList<Integer> tasks = new ArrayList<Integer>();
		if (QueryType.queryTextualKNN.equals(query.getQueryType())) {
			tasks.add(globalIndex.getTaskIDsContainingPoint(((KNNQuery) query).getFocalPoint()));
		} else if (QueryType.queryTextualRange.equals(query.getQueryType()) || QueryType.queryTextualSpatialJoin.equals(query.getQueryType())) {
			tasks = globalIndex.getTaskIDsOverlappingRecangle(query.getSpatialRange());
		}
		return tasks;
	}

	// collecting the topology information
	// prepare the grid index cells
	// set the evaluator bolt task ids
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.count = 0;
		this.debug = (Boolean) stormConf.get(Config.TOPOLOGY_DEBUG);
		this.context = context;
		this.collector = collector;
		this.stormConf = stormConf;
		this.evaluatorBoltTasks = context.getComponentTasks(id);
		this.indexBoltTasks = context.getComponentTasks(SpatioTextualConstants.getIndexId(id));
		this.reliable = ((Long) stormConf.get(Config.TOPOLOGY_ACKER_EXECUTORS)) > 0;
		this.stringBuilder = new StringBuilder();
		entireSpace = new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.xMaxRange));
		numberOfEvaluatorTasks = this.evaluatorBoltTasks.size();
		selfComponentId = context.getThisComponentId();
		selfTaskId = context.getThisTaskId();
		selfTaskIndex = context.getThisTaskIndex();
		buildGlobalIndex();
		initMetrics(context);

		//		if (this.stormConf.get(SpatioTextualConstants.discoDir) != null)
		//			disco = SemanticHelper.getDiskBasedDiscoInstance((String) this.stormConf.get(SpatioTextualConstants.discoDir));
		//		else
		//			disco = null;

		readDataSourcesInformation();
	}

	protected void buildGlobalIndex() {
		if (partitions != null && partitions.size() == numberOfEvaluatorTasks && (globalIndexType == GlobalIndexType.PARTITIONED)) {
			for (Partition p : partitions) {
				System.out.println("parition: " + p.index + " ,cost " + p.getCost() + " ,xmin: " + p.getCoords()[0] + " ,ymin: " + p.getCoords()[1] + " ,xlength: " + p.getDimensions()[0] + " ,ylength: " + p.getDimensions()[1]);
			}
			globalIndex = new GlobalOptimizedPartitionedIndexLowerSpace(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions, fineGridGran);
			//globalIndex = new GlobalOptimizedPartitionedIndex(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions, fineGridGran);
			System.out.println("Starting a partition based global index ");

		} else if (partitions != null && partitions.size() == numberOfEvaluatorTasks && (globalIndexType == GlobalIndexType.PARTITIONED_TEXT_AWARE || globalIndexType == GlobalIndexType.PARTITIONED_TEXT_AWARE_FORWARD)) {
			for (Partition p : partitions) {
				System.out.println("parition: " + p.index + " ,cost " + p.getCost() + " ,xmin: " + p.getCoords()[0] + " ,ymin: " + p.getCoords()[1] + " ,xlength: " + p.getDimensions()[0] + " ,ylength: " + p.getDimensions()[1]);
			}

			globalIndex = new GlobalOptimizedPartitionedTextAwareIndex(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions, fineGridGran);
			System.out.println("Starting a partitioned text aware based global index ");

		} else if (globalIndexType == GlobalIndexType.RANDOM_TEXT) {
			globalIndex = new RandomTextRouting(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions, fineGridGran);
			System.out.println("Starting a random text only  global index ");
		} else {
			globalIndex = new GlobalGridIndex(numberOfEvaluatorTasks, evaluatorBoltTasks);
			System.out.println("Starting a grid based global index ");
		}
	}

	protected DataObject readDataObject(Tuple input, String source) {
		DataObject dataObject = null;
		if (input.contains(SpatioTextualConstants.dataObject)) {
			dataObject = (DataObject) input.getValueByField(SpatioTextualConstants.dataObject);
			dataObject.setSrcId(source);
		}
		return dataObject;

	}

	/**
	 * This function reads the configuration of the data sources and builds
	 * required data structures
	 */
	protected void readDataSourcesInformation() {
		System.out.println("***************************Evaluating bolt configuration*******************");
		Iterator it = stormConf.entrySet().iterator();
		sourcesInformations = new HashMap<String, DataSourceInformation>();
		String sourceId = "";
		DataSourceType sourceType;
		String persistenceState = "";
		//Adding tickTupleSource
		DataSourceInformation dataSourcesInformation = new DataSourceInformation(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), getTickTupleSourceId(),
				DataSourceType.TICK_SOURCE, null, null, false, null, fineGridGran);

		sourcesInformations.put(sourceId, dataSourcesInformation);
		//Adding data and query sources 
		while (it.hasNext()) {
			persistenceState = "";
			Map.Entry pair = (Map.Entry) it.next();

			String key = "" + (String) pair.getKey();

			if (key.startsWith(DataSourceType.DATA_SOURCE.name())) {
				sourceType = DataSourceType.DATA_SOURCE;
				sourceId = key.substring(DataSourceType.DATA_SOURCE.name().length() + 1);
			} else if (key.startsWith(DataSourceType.QUERY_SOURCE.name())) {
				sourceType = DataSourceType.QUERY_SOURCE;
				sourceId = key.substring(DataSourceType.QUERY_SOURCE.name().length() + 1);
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
			dataSourcesInformation = new DataSourceInformation(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), sourceId, sourceType, persistenceState, cleanState, false,
					null, fineGridGran);

			sourcesInformations.put(sourceId, dataSourcesInformation);
			System.out.println(key + " = " + value);

		}

	}

	public static Query readQueryByType(Tuple input, QueryType queryType, String source) {
		Query query = new Query();
		query.setQueryId(input.getIntegerByField(SpatioTextualConstants.queryIdField));
		query.setSrcId(source);
		query.setQueryType(queryType);
		query.setTimeStamp(input.getLongByField(SpatioTextualConstants.queryTimeStampField));
		query.setDataSrc(input.getStringByField(SpatioTextualConstants.dataSrc));
		query.setCommand((Command) input.getValueByField(SpatioTextualConstants.queryCommand));
		query.setComplexQueryText((ArrayList<ArrayList<String>>) input.getValueByField(SpatioTextualConstants.queryComplexTextField));
		String text = "", text2 = "";
		if (input.contains(SpatioTextualConstants.textualPredicate)) {
			query.setTextualPredicate((TextualPredicate) input.getValueByField(SpatioTextualConstants.textualPredicate));
		} else {
			query.setTextualPredicate(TextualPredicate.NONE);
		}
		//		if (input.contains(SpatioTextualConstants.textualPredicate2)) {
		//			query.setTextualPredicate2((TextualPredicate) input.getValueByField(SpatioTextualConstants.textualPredicate2));
		//		} else {
		//query.setTextualPredicate2(TextualPredicate.NONE);
		//		}
		if (input.contains(SpatioTextualConstants.queryTextField)) {
			text = input.getStringByField(SpatioTextualConstants.queryTextField);
			ArrayList<String> queryText = new ArrayList<String>();
			if (text != null && !"".equals(text)) {
				queryText = TextHelpers.transformIntoSortedArrayListOfString(text);
				//				if (TextualPredicate.SEMANTIC.equals(query.getTextualPredicate()) && disco != null) {
				//					ArrayList<String> similarKeyWord = SemanticHelper.getSematicallySimilarKeyWords(disco, queryText);
				//					queryText = TextHelpers.sortTextArrayList(similarKeyWord);
				//
				//					query.setTextualPredicate(TextualPredicate.OVERlAPS);
				//				}
			}
			//			else {
			//				query.setTextualPredicate(TextualPredicate.NONE);
			//			}
			query.setQueryText(queryText);

		}
		if (input.contains(SpatioTextualConstants.removeTime)) {
			query.setRemoveTime(input.getLongByField(SpatioTextualConstants.removeTime));
		}
		//		if (input.contains(SpatioTextualConstants.queryText2Field)) {
		//			text2 = input.getStringByField(SpatioTextualConstants.queryText2Field);
		//			ArrayList<String> queryText = new ArrayList<String>();
		//			if (text2 != null && !"".equals(text2)) {
		//				queryText = TextHelpers.transformIntoSortedArrayListOfString(text2);
		//				//				if (TextualPredicate.SEMANTIC.equals(query.getTextualPredicate()) && disco != null) {
		//				//					ArrayList<String> similarKeyWord = SemanticHelper.getSematicallySimilarKeyWords(disco, queryText);
		//				//					queryText = TextHelpers.sortTextArrayList(similarKeyWord);
		//				//					query.setTextualPredicate2(TextualPredicate.OVERlAPS);
		//				//		}
		//			} else {
		//				query.setTextualPredicate2(TextualPredicate.NONE);
		//			}
		//			query.setQueryText2(queryText);
		//
		//		}

		//		if (input.contains(SpatioTextualConstants.joinTextualPredicate)) {
		//			query.setJoinTextualPredicate((TextualPredicate) input.getValueByField(SpatioTextualConstants.joinTextualPredicate));
		//		} else {
		//		}

		if (QueryType.queryTextualKNN.equals(queryType)) {
			((KNNQuery) query).setK(input.getIntegerByField(SpatioTextualConstants.kField));
			((KNNQuery) query).getFocalPoint().setX(input.getDoubleByField(SpatioTextualConstants.focalXCoordField));
			((KNNQuery) query).getFocalPoint().setY(input.getDoubleByField(SpatioTextualConstants.focalYCoordField));
			((KNNQuery) query).setSpatialRange(new Rectangle(((KNNQuery) query).getFocalPoint(), ((KNNQuery) query).getFocalPoint()));
		} else if (QueryType.queryTextualRange.equals(queryType) || (QueryType.queryTextualSpatialJoin.equals(queryType))) {
			Point min = new Point();
			min.setX(input.getDoubleByField(SpatioTextualConstants.queryXMinField));
			min.setY(input.getDoubleByField(SpatioTextualConstants.queryYMinField));
			Point max = new Point();
			max.setX(input.getDoubleByField(SpatioTextualConstants.queryXMaxField));
			max.setY(input.getDoubleByField(SpatioTextualConstants.queryYMaxField));
			query.setSpatialRange(new Rectangle(min, max));
			if (QueryType.queryTextualSpatialJoin.equals(queryType)) {
				((JoinQuery) query).setDataSrc2(input.getStringByField(SpatioTextualConstants.dataSrc2));
				((JoinQuery) query).setDistance(input.getDoubleByField(SpatioTextualConstants.queryDistance));
			}
		}

		return query;
	}

}
