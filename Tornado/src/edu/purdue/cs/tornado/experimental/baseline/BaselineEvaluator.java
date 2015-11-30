package edu.purdue.cs.tornado.experimental.baseline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.StringHelpers;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.OutputTuple;
import edu.purdue.cs.tornado.messages.Query;
import edu.purdue.cs.tornado.storage.AbstractStaticDataSource;
import edu.purdue.cs.tornado.storage.POIHDFSSource;
import edu.purdue.cs.tornado.test.TestPOIsStaticDataSource;
import info.aduna.concurrent.locks.Properties;

public class BaselineEvaluator extends BaseRichBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5114596912424735584L;
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

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.stormConf = stormConf;
		this.context = context;
		this.collector = collector;
		this.selfTaskId = context.getThisTaskId();
		this.selfTaskIdIndex = context.getThisTaskIndex();
		this.totalTaskSize = context.getComponentTasks(context.getThisComponentId()).size();
		allQueries = new ArrayList<Query>();
		staticData = new ArrayList<DataObject>(); //Maybe this needs to be spatially partitioned 
		selfBounds = new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange));
		Long startTime = System.nanoTime();
	    readStaticData();
	    Long endTime = System.nanoTime();
	    System.out.println("StaticData load time in nanoseconds="+(startTime-endTime));
	}

	@Override
	public void execute(Tuple input) {

		if (input.getSourceComponent().equals("Datasource")) {
			DataObject dataObject = readDataObject(input, "Datasource");
			//iterate over all queries and check if the tuple qualifies for this query
			Boolean resend = evaluateDataObjectAgainstQueries(dataObject,false);
			if(resend){
				collector.emit("sharedData",input, new Values(dataObject));
			}
		} else if (input.getSourceComponent().equals("Querysource")) {
			String queryType = input.getStringByField(SpatioTextualConstants.queryTypeField);
			Query query = readQueryByType(input, queryType, "Querysource");
			allQueries.add(query);
		} else if (input.getSourceComponent().equals("BaseLineEvaluator")) {
			if(input.getSourceTask()!=selfTaskId){
				DataObject dataObject = (DataObject)input.getValueByField(SpatioTextualConstants.data);
				evaluateDataObjectAgainstQueries(dataObject,true);
			}
				
		}
		collector.ack(input);
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

	private Boolean evaluateDataObjectAgainstQueries(DataObject dataObject,Boolean fromNeighbour) {
		Boolean resend = false;
		for (Query q : allQueries) {
			if (!fromNeighbour &&q.getQueryType().equals(SpatioTextualConstants.queryTextualRange)) {
				if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), q.getSpatialRange()) && StringHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate()))
					generateOutput(q, dataObject, SpatioTextualConstants.addCommand);
			} else if (q.getQueryType().equals(SpatioTextualConstants.queryTextualSpatialJoin)) {
				if (processVolatileDataObjectForTextualSpatialJoinQuery(dataObject, q))
					resend = true;
			}
		}
		return resend;
	}

	private Boolean processVolatileDataObjectForTextualSpatialJoinQuery(DataObject dataObject, Query q) {
		Boolean resend = false;
		if (!SpatialHelper.overlapsSpatially(dataObject.getLocation(), q.getSpatialRange()))
			return resend;

		String otherDataSource = "";
		//identify the other data source to join with 
		//check if this data object came from input sources or from data source 
		// verify the textual predicate of the incomming data source and the query 
		if (dataObject.getSrcId().equals(q.getDataSrc())) {
			otherDataSource = q.getDataSrc2();
			if (!StringHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate()))
				return resend;
			else
				resend = true;
		} else {
			otherDataSource = q.getDataSrc();
			if (!StringHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText2(), q.getTextualPredicate2()))
				return resend;
			else
				resend = true;
		}

		for (DataObject storedDataObject : staticData) {

			if (StringHelpers.evaluateTextualPredicate(dataObject.getObjectText(), storedDataObject.getObjectText(), q.getJoinTextualPredicate())
					&& (otherDataSource.equals(q.getDataSrc2()) && StringHelpers.evaluateTextualPredicate(storedDataObject.getObjectText(), q.getQueryText2(), q.getTextualPredicate2())
							|| otherDataSource.equals(q.getDataSrc()) && StringHelpers.evaluateTextualPredicate(storedDataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate()))

			&& SpatialHelper.getDistanceInBetween(dataObject.getLocation(), storedDataObject.getLocation()) <= q.getDistance() //evaluate distance 
					&& SpatialHelper.overlapsSpatially(storedDataObject.getLocation(), q.getSpatialRange())) {
				generateOutput(q, dataObject, storedDataObject, SpatioTextualConstants.addCommand, SpatioTextualConstants.addCommand);
				resend = true;
			}

		}
		return resend;
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

	private void readStaticData() {
		staticDataSource = getHDFSPOIStaticSource();
	//	staticDataSource = getLFSPOIStaticSource() ;
		while (staticDataSource.hasNext()) {
			DataObject dataObject = staticDataSource.getNext();
				staticData.add(dataObject);
		}
		staticDataSource.close();

	}

	void generateOutput(Query q, DataObject obj, String command) {
		//	System.out.println("[Output: command: "+command+" query:" + q.toString() + "\n******" + obj.toString() + "]");
		OutputTuple outputTuple = new OutputTuple();
		outputTuple.setDataObject(obj);
		Query miniQuery = new Query();
		miniQuery.setQueryId(q.getQueryId());
		miniQuery.setSrcId(q.getSrcId());
		miniQuery.setDataSrc(q.getDataSrc());
		outputTuple.setQuery(miniQuery);
		outputTuple.setDataObjectCommand(command);
		collector.emit("output", new Values(outputTuple));
	}

	void generateOutput(Query q, DataObject obj, DataObject obj2, String obj1Command, String obj2Command) {
	//		System.out.println("[Output: command  "+obj1Command+" query:"  + q.toString() + "\n******" + obj.toString() + "\n******" + obj2.toString() + "]");
		OutputTuple outputTuple = new OutputTuple();
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
	public Rectangle getBoundsForTaskIndex(Integer taskIndex){
		 Double xrange;
		 Double yrange;
		 Double xStep;
		 Double yStep;
		 Integer xCellsNum;
		 Integer yCellsNum;
		 xrange = SpatioTextualConstants.xMaxRange;
		yrange = SpatioTextualConstants.yMaxRange;
		yCellsNum = xCellsNum = (int)Math.sqrt(this.totalTaskSize);
		xStep = xrange / xCellsNum;
		yStep = yrange/yCellsNum;
		IndexCellCoordinates globalIndexSelfcoordinates = new IndexCellCoordinates(taskIndex/xCellsNum,taskIndex%xCellsNum);
		Point minPoint = new Point ();
		minPoint.setX(xStep*globalIndexSelfcoordinates.getX());
		minPoint.setY(yStep*globalIndexSelfcoordinates.getY());
		Point maxPoint = new Point ();
		maxPoint.setX(xStep*(globalIndexSelfcoordinates.getX()+1));
		maxPoint.setY(yStep*(globalIndexSelfcoordinates.getY()+1));
		return new Rectangle(minPoint, maxPoint);
	}
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("output", new Fields(SpatioTextualConstants.output));
		declarer.declareStream("sharedData", new Fields(SpatioTextualConstants.data));
	}

}
