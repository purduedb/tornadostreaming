package edu.purdue.cs.tornado.experimental.baseline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.QueryType;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.index.GlobalIndexBolt;
import edu.purdue.cs.tornado.messages.CombinedTuple;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.JoinQuery;
import edu.purdue.cs.tornado.messages.KNNQuery;
import edu.purdue.cs.tornado.messages.Query;
import edu.purdue.cs.tornado.storage.AbstractStaticDataSource;
import edu.purdue.cs.tornado.storage.POIHDFSSource;
import edu.purdue.cs.tornado.test.TestPOIsStaticDataSource;

public class BaselineEvaluator extends BaseRichBolt {
	/**
	 * 
	 */
	ArrayList<Query> allQueries;
	Rectangle selfBounds;
	ArrayList<DataObject> staticData;
	AbstractStaticDataSource staticDataSource;
	Integer selfTaskId;
	Integer selfTaskIdIndex;
	Integer totalTaskSize;
	Properties properties;
	Map stormConf;
	TopologyContext context;
	OutputCollector collector;

	Boolean reliable;
	IndexCell singleIndexCell;

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.stormConf = stormConf;
		this.context = context;
		this.collector = collector;
		this.selfTaskId = context.getThisTaskId();
		this.selfTaskIdIndex = context.getThisTaskIndex();
		this.totalTaskSize = context.getComponentTasks(context.getThisComponentId()).size();
		if (stormConf != null)
			this.reliable = ((Long) stormConf.get(Config.TOPOLOGY_ACKER_EXECUTORS)) > 0;
		else
			this.reliable = false;
		allQueries = new ArrayList<Query>();
		staticData = new ArrayList<DataObject>(); //Maybe this needs to be spatially partitioned 
		selfBounds = new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange));
		singleIndexCell = new IndexCell(selfBounds, false, 0);
		//	initMetrics(context);
		//		Long startTime = System.nanoTime();
		//	    readStaticData();
		//	    Long endTime = System.nanoTime();
		//	    System.out.println("StaticData load time in nanoseconds="+(startTime-endTime));
	}

	void initMetrics(TopologyContext context) {
		//		_inputDataCountMetric = new CountMetric();
		//		context.registerMetric("Data_Object_counter", _inputDataCountMetric, 300);
		//		_dataCountMeanMetric = new ReducedMetric(new MeanReducer());
		//		context.registerMetric("Data_Object_count_mean", _dataCountMeanMetric, 300);
		//		_outputCountMetric = new CountMetric();
		//		context.registerMetric("Output_counter", _outputCountMetric, 300);
		//		_outputCountMeanMetric = new ReducedMetric(new MeanReducer());
		//		context.registerMetric("Output_count_mean", _outputCountMeanMetric, 300);
		//		_queriesCountMetric = new CountMetric();
		//		context.registerMetric("Queries_counter", _queriesCountMetric, 300);
		//		_queriesCountMeanMetric = new ReducedMetric(new MeanReducer());
		//		context.registerMetric("Queries_count_mean", _queriesCountMeanMetric, 300);
	}

	@Override
	public void execute(Tuple input) {

		if (input.getSourceComponent().equals("Tweets")) {
			//			_inputDataCountMetric.incr();
			//			_dataCountMeanMetric.update(1);
			DataObject dataObject = readDataObject(input, "Tweets");
			//iterate over all queries and check if the tuple qualifies for this query
			Boolean resend = evaluateDataObjectAgainstQueries(dataObject, false);
			if (resend) {
				//collector.emit("sharedData",input, new Values(dataObject));
				collector.emit("sharedData", new Values(dataObject));
			}
		} else if (input.getSourceComponent().equals("querySource")) {
			//			_queriesCountMetric.incr();
			//			_queriesCountMeanMetric.update(1);
			QueryType queryType =(QueryType) input.getValueByField(SpatioTextualConstants.queryTypeField);
			Query query = GlobalIndexBolt.readQueryByType(input, queryType, "querySource");
			singleIndexCell.addQuery(query);
			//	allQueries.add(query);
		} else if (input.getSourceComponent().equals("BaseLineEvaluator")) {
			if (input.getSourceTask() != selfTaskId) {
				DataObject dataObject = (DataObject) input.getValueByField(SpatioTextualConstants.data);
				evaluateDataObjectAgainstQueries(dataObject, true);
			}

		}
		if (reliable)
			collector.ack(input);
	}

	private Query readQueryByType(Tuple input, QueryType queryType, String source) {
		Query query = new Query();
		query.setQueryId(input.getIntegerByField(SpatioTextualConstants.queryIdField));
		query.setSrcId(source);
		query.setQueryType(queryType);
		query.setTimeStamp(input.getLongByField(SpatioTextualConstants.queryTimeStampField));
		query.setDataSrc(input.getStringByField(SpatioTextualConstants.dataSrc));
		query.setCommand((Command) input.getValueByField(SpatioTextualConstants.queryCommand));
		String text = "", text2 = "";
		if (input.contains(SpatioTextualConstants.textualPredicate)) {
			query.setTextualPredicate((TextualPredicate) input.getValueByField(SpatioTextualConstants.textualPredicate));
		} else {
			query.setTextualPredicate(TextualPredicate.OVERlAPS);
		}
		if (input.contains(SpatioTextualConstants.textualPredicate2)) {
			query.setTextualPredicate((TextualPredicate) input.getValueByField(SpatioTextualConstants.textualPredicate2));
		} else {
			((JoinQuery)query).setTextualPredicate2(TextualPredicate.NONE);
		}
		if (input.contains(SpatioTextualConstants.queryTextField)) {
			text = input.getStringByField(SpatioTextualConstants.queryTextField);
			ArrayList<String> queryText = new ArrayList<String>();
			if (text != null && !"".equals(text)) {
				queryText = TextHelpers.transformIntoSortedArrayListOfString(text);

			} else {
				query.setTextualPredicate(TextualPredicate.NONE);
			}
			query.setQueryText(queryText);

		}
		if (input.contains(SpatioTextualConstants.queryText2Field)) {
			text2 = input.getStringByField(SpatioTextualConstants.queryText2Field);
			ArrayList<String> queryText = new ArrayList<String>();
			if (text2 != null && !"".equals(text2)) {
				queryText = TextHelpers.transformIntoSortedArrayListOfString(text2);

			} else {
				((JoinQuery)query).setTextualPredicate2(TextualPredicate.NONE);
			}
			((JoinQuery)query).setQueryText2(queryText);

		}

		if (input.contains(SpatioTextualConstants.joinTextualPredicate)) {
			((JoinQuery)query).setJoinTextualPredicate((TextualPredicate) input.getValueByField(SpatioTextualConstants.joinTextualPredicate));
		} else {
			((JoinQuery)query).setJoinTextualPredicate(TextualPredicate.NONE);
		}

		if (QueryType.queryTextualKNN.equals(queryType)) {
			((KNNQuery)query).setK(input.getIntegerByField(SpatioTextualConstants.kField));
			((KNNQuery)query).getFocalPoint().setX(input.getDoubleByField(SpatioTextualConstants.focalXCoordField));
			((KNNQuery)query).getFocalPoint().setY(input.getDoubleByField(SpatioTextualConstants.focalYCoordField));
			((KNNQuery)query).setSpatialRange(new Rectangle(((KNNQuery)query).getFocalPoint(), ((KNNQuery)query).getFocalPoint()));
		} else if (QueryType.queryTextualRange.equals(queryType) || (QueryType.queryTextualSpatialJoin.equals(queryType))) {
			Point min = new Point();
			min.setX(input.getDoubleByField(SpatioTextualConstants.queryXMinField));
			min.setY(input.getDoubleByField(SpatioTextualConstants.queryYMinField));
			Point max = new Point();
			max.setX(input.getDoubleByField(SpatioTextualConstants.queryXMaxField));
			max.setY(input.getDoubleByField(SpatioTextualConstants.queryYMaxField));
			query.setSpatialRange(new Rectangle(min, max));
			if (QueryType.queryTextualSpatialJoin.equals(queryType)) {
				((JoinQuery)query).setDataSrc2(input.getStringByField(SpatioTextualConstants.dataSrc2));
				((JoinQuery)query).setDistance(input.getDoubleByField(SpatioTextualConstants.queryDistance));
			}
		}

		return query;
	}

	private Boolean evaluateDataObjectAgainstQueries(DataObject dataObject, Boolean fromNeighbour) {
		Boolean resend = false;
		//HashMap<String, List<Query>> queriesList = singleIndexCell.getTextualOverlappingQueries(dataObject.getObjectText());
		List<Query> queriesList = singleIndexCell.getQueries();
	
			for (Query q : queriesList) {
				if (!fromNeighbour && q.getQueryType().equals(QueryType.queryTextualRange)) {
					if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), q.getSpatialRange()) && TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate()))
						generateOutput(q, dataObject, Command.addCommand);
				} else if (q.getQueryType().equals(QueryType.queryTextualSpatialJoin)) {
					if (processVolatileDataObjectForTextualSpatialJoinQuery(dataObject, ((JoinQuery)q)))
						resend = true;
				}
			}
		
		return resend;
	}

	private Boolean evaluateDataObjectAgainstQueries_old(DataObject dataObject, Boolean fromNeighbour) {
		Boolean resend = false;
		for (Query q : allQueries) {
			if (!fromNeighbour && q.getQueryType().equals(QueryType.queryTextualRange)) {
				if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), q.getSpatialRange()) && TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate()))
					generateOutput(q, dataObject, Command.addCommand);
			} else if (q.getQueryType().equals(QueryType.queryTextualSpatialJoin)) {
				if (processVolatileDataObjectForTextualSpatialJoinQuery(dataObject, (JoinQuery)q))
					resend = true;
			}
		}
		return resend;
	}

	private Boolean processVolatileDataObjectForTextualSpatialJoinQuery(DataObject dataObject, JoinQuery q) {
		Boolean resend = false;
		if (!SpatialHelper.overlapsSpatially(dataObject.getLocation(), q.getSpatialRange()))
			return resend;

		String otherDataSource = "";
		//identify the other data source to join with 
		//check if this data object came from input sources or from data source 
		// verify the textual predicate of the incomming data source and the query 
		if (dataObject.getSrcId().equals(q.getDataSrc())) {
			otherDataSource = q.getDataSrc2();
			if (!TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate()))
				return resend;
			else
				resend = true;
		} else {
			otherDataSource = q.getDataSrc();
			if (!TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText2(), q.getTextualPredicate2()))
				return resend;
			else
				resend = true;
		}

		for (DataObject storedDataObject : staticData) {

			if (TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), storedDataObject.getObjectText(), q.getJoinTextualPredicate())
					&& (otherDataSource.equals(q.getDataSrc2()) && TextHelpers.evaluateTextualPredicate(storedDataObject.getObjectText(), q.getQueryText2(), q.getTextualPredicate2())
							|| otherDataSource.equals(q.getDataSrc()) && TextHelpers.evaluateTextualPredicate(storedDataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate()))

			&& SpatialHelper.getDistanceInBetween(dataObject.getLocation(), storedDataObject.getLocation()) <= q.getDistance() //evaluate distance 
					&& SpatialHelper.overlapsSpatially(storedDataObject.getLocation(), q.getSpatialRange())) {
				generateOutput(q, dataObject, storedDataObject, Command.addCommand, Command.addCommand);
				resend = true;
			}

		}
		return resend;
	}

	private DataObject readDataObject(Tuple input, String source) {
		DataObject dataObject = null;
		if (input.contains(SpatioTextualConstants.dataObject)) {
			dataObject = (DataObject) input.getValueByField(SpatioTextualConstants.dataObject);
			dataObject.setSrcId(source);
		}
		return dataObject;

	}

	private void readStaticData() {
		staticDataSource = getHDFSPOIStaticSource();
		//	staticDataSource = getLFSPOIStaticSource() ;
		while (staticDataSource.hasNext()) {
			DataObject dataObject = staticDataSource.getNext();
			staticData.add(dataObject);
		}
		staticDataSource.close();

	}

	void generateOutput(Query q, DataObject obj, Command command) {
		//		_outputCountMeanMetric.update(1);
		//		_outputCountMetric.incr();
		//	System.out.println("[Output: command: "+command+" query:" + q.toString() + "\n******" + obj.toString() + "]");
		CombinedTuple outputTuple = new CombinedTuple();
		outputTuple.setDataObject(obj);
		Query miniQuery = new Query();
		miniQuery.setQueryId(q.getQueryId());
		miniQuery.setSrcId(q.getSrcId());
		miniQuery.setDataSrc(q.getDataSrc());
		outputTuple.setQuery(miniQuery);
		outputTuple.setDataObjectCommand(command);
		collector.emit("output", new Values(outputTuple));
	}

	void generateOutput(JoinQuery q, DataObject obj, DataObject obj2, Command obj1Command, Command obj2Command) {
		//		System.out.println("[Output: command  "+obj1Command+" query:"  + q.toString() + "\n******" + obj.toString() + "\n******" + obj2.toString() + "]");
		//		_outputCountMeanMetric.update(1);
		//		_outputCountMetric.incr();
		CombinedTuple outputTuple = new CombinedTuple();
		outputTuple.setDataObject(obj);
		outputTuple.setDataObject2(obj2);
		JoinQuery miniQuery = new JoinQuery();
		miniQuery.setQueryId(q.getQueryId());
		miniQuery.setSrcId(q.getSrcId());
		miniQuery.setDataSrc(q.getDataSrc());
		miniQuery.setDataSrc2(q.getDataSrc2());
		outputTuple.setQuery(miniQuery);
		outputTuple.setDataObjectCommand(obj1Command);
		outputTuple.setDataObject2Command(obj2Command);
		collector.emit("output", new Values(outputTuple));
	}

	AbstractStaticDataSource getHDFSPOIStaticSource() {
		Map<String, String> staticSourceConfig = new HashMap<String, String>();
		staticSourceConfig.put(POIHDFSSource.HDFS_POI_FOLDER_PATH, (String) stormConf.get("HDFS_POI_FOLDER_PATH"));
		staticSourceConfig.put(POIHDFSSource.CORE_FILE_PATH, (String) stormConf.get("CORE_FILE_PATH"));

		Rectangle selfBoundsTemp = getBoundsForTaskIndex(selfTaskIdIndex);
		AbstractStaticDataSource staticDataSource = new POIHDFSSource(selfBoundsTemp, staticSourceConfig, "POI_Data", selfTaskId, selfTaskIdIndex);
		return staticDataSource;
	}

	AbstractStaticDataSource getLFSPOIStaticSource() {
		Map<String, String> staticSourceConfig = new HashMap<String, String>();
		//staticSourceConfig.put(POIHDFSSource.HDFS_POI_FOLDER_PATH, (String) stormConf.get("HDFS_POI_FOLDER_PATH"));
		staticSourceConfig.put(TestPOIsStaticDataSource.POIS_PATH, "datasources/pois.csv");

		Rectangle selfBoundsTemp = getBoundsForTaskIndex(selfTaskIdIndex);
		AbstractStaticDataSource staticDataSource = new TestPOIsStaticDataSource(selfBoundsTemp, staticSourceConfig, "POI_Data", selfTaskId, selfTaskIdIndex);
		return staticDataSource;
	}

	public Rectangle getBoundsForTaskIndex(Integer taskIndex) {
		Double xrange;
		Double yrange;
		Double xStep;
		Double yStep;
		Integer xCellsNum;
		Integer yCellsNum;
		xrange = SpatioTextualConstants.xMaxRange;
		yrange = SpatioTextualConstants.yMaxRange;
		yCellsNum = xCellsNum = (int) Math.sqrt(this.totalTaskSize);
		xStep = xrange / xCellsNum;
		yStep = yrange / yCellsNum;
		IndexCellCoordinates globalIndexSelfcoordinates = new IndexCellCoordinates(taskIndex / xCellsNum, taskIndex % xCellsNum);
		Point minPoint = new Point();
		minPoint.setX(xStep * globalIndexSelfcoordinates.getX());
		minPoint.setY(yStep * globalIndexSelfcoordinates.getY());
		Point maxPoint = new Point();
		maxPoint.setX(xStep * (globalIndexSelfcoordinates.getX() + 1));
		maxPoint.setY(yStep * (globalIndexSelfcoordinates.getY() + 1));
		return new Rectangle(minPoint, maxPoint);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("output", new Fields(SpatioTextualConstants.output));
		declarer.declareStream("sharedData", new Fields(SpatioTextualConstants.data));
	}

}
