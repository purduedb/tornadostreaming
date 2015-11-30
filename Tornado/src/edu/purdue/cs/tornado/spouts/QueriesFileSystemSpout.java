package edu.purdue.cs.tornado.spouts;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.StringHelpers;
import edu.purdue.cs.tornado.messages.Query;

public class QueriesFileSystemSpout extends FileSpout {
	public QueriesFileSystemSpout(Map spoutConf) {
		super(spoutConf);
		
	}

	public static String SPATIAL_RANGE = "SPATIAL_RANGE"; //1  [10] 100 500 out of 10000
	public static String KEYWORD_COUNT = "KEYWORD_COUNT"; //1  [5] 10 20  
	public static String TOTAL_QUERY_COUNT = "TOTAL_QUERY_COUNT"; //1000 [100000]  1000000
	public Double spatialRangeVal = 10.0;
	public Integer keywordCountVal = 5;
	public Integer totalQueryCountVal = 100000;
	public Integer k=5;
	public String queryType;
	public String dataSrc1,dataSrc2;
	public String textualPredicate1,textualPredicate2,joinTextualPredicate;
	public Double distance ;
	Integer i = new Integer(0);

	public void ack(Object msgId) {
	}

	public void close() {
	}

	public void fail(Object msgId) {
	}

	@Override
	public void nextTuple() {

		if (i>=this.totalQueryCountVal)
			return;
		String line = "";
		try {

			// Read File Line By Line
			if ((line = br.readLine()) == null) {
				br.close();
				connectToHDFS();

			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return;

		}
		if (line == null || line.isEmpty())
			return;
		//System.out.println(tweet);
		i = i + 1;
		emitQuery(line, i);
		sleep();

	}

	private void emitQuery(String line, Integer msgId) {
		
		Query q = buildQuery(line);
		this.collector.emit(new Values(q.getQueryType(), q.getQueryId(), q.getFocalPoint().getX(), q.getFocalPoint().getY(), q.getSpatialRange().getMin().getX(), q.getSpatialRange().getMin().getY(), q.getSpatialRange().getMax().getX(),
				q.getSpatialRange().getMax().getY(), q.getK(), StringHelpers.convertArrayListOfStringToText(q.getQueryText()), StringHelpers.convertArrayListOfStringToText(q.getQueryText2()), q.getTimeStamp(), q.getDataSrc(),
				q.getDataSrc2(), q.getCommand(), q.getDistance(), q.getTextualPredicate(), q.getTextualPredicate2(), q.getJoinTextualPredicate()

		),i);

	}

	private Query buildQuery(String line) {
		StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
		
		String id = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		Double xCoord = 0.0;
		Double yCoord = 0.0;
		try {
			xCoord = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;

			yCoord = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		String textContent = "";
		while (stringTokenizer.hasMoreTokens())
			textContent = textContent + stringTokenizer.nextToken() + " ";
		String[] keywordsArr = textContent.split(" ");
		ArrayList<String> queryText1 =new ArrayList<String>();
		ArrayList<String> queryText2 =new ArrayList<String>();
		
		for(int i =0;i<keywordCountVal;i++){
			queryText1.add(keywordsArr[i]);
			queryText2.add(keywordsArr[keywordsArr.length-i-1]);
		}
		
				
		Date date = new Date();
		
		
		Query q = new Query();
		q.setQueryId(id);
		q.setCommand(SpatioTextualConstants.addCommand);
		q.setContinousQuery(true);
		q.setDataSrc(dataSrc1);
		q.setDataSrc2(dataSrc2);
		q.setDistance(distance);
		q.setQueryType(queryType);
		q.setTimeStamp(date.getTime());
		if(queryType.equals(SpatioTextualConstants.queryTextualRange)){
			q.setSpatialRange(new Rectangle(new Point(xCoord, yCoord),new Point(xCoord+this.spatialRangeVal,yCoord+this.spatialRangeVal)) );
			q.setTextualPredicate(textualPredicate1);
			q.setQueryText(queryText1);
		}
		else if(queryType.equals(SpatioTextualConstants.queryTextualKNN)){
			q.setFocalPoint(new Point(xCoord, yCoord));
			q.setTextualPredicate(textualPredicate1);
			q.setQueryText(queryText1);
			q.setK(k);
		}
		else if(queryType.equals(SpatioTextualConstants.queryTextualSpatialJoin)){
			q.setSpatialRange(new Rectangle(new Point(xCoord, yCoord),new Point(xCoord+this.spatialRangeVal,yCoord+this.spatialRangeVal)) );
			q.setTextualPredicate(textualPredicate1);
			q.setTextualPredicate2(textualPredicate2);
			q.setQueryText(queryText1);
			q.setQueryText(queryText2);
			q.setDistance(distance);
		}	
		return q;
	}

	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		super.open(conf, context, collector);
		if (spoutConf.containsKey(this.TOTAL_QUERY_COUNT))
			this.totalQueryCountVal = (Integer) spoutConf.get(this.TOTAL_QUERY_COUNT);
		if (spoutConf.containsKey(this.SPATIAL_RANGE))
			this.spatialRangeVal = (Double) spoutConf.get(this.SPATIAL_RANGE);
		if (spoutConf.containsKey(this.KEYWORD_COUNT))
			this.keywordCountVal = (Integer) spoutConf.get(this.KEYWORD_COUNT);
		
		if (spoutConf.containsKey(SpatioTextualConstants.kField))
			this.k = (Integer) spoutConf.get(SpatioTextualConstants.kField);
		
		if (spoutConf.containsKey(SpatioTextualConstants.queryTypeField))
			this.queryType = (String) spoutConf.get(SpatioTextualConstants.queryTypeField);
		else 
			this.queryType = SpatioTextualConstants.queryTextualRange;
		
		
		
		if (spoutConf.containsKey(SpatioTextualConstants.dataSrc))
			this.dataSrc1 = (String) spoutConf.get(SpatioTextualConstants.dataSrc);
		else 
			this.dataSrc1 = null;
		if (spoutConf.containsKey(SpatioTextualConstants.dataSrc2))
			this.dataSrc2 = (String) spoutConf.get(SpatioTextualConstants.dataSrc2);
		else 
			this.dataSrc2 = null;
		if (spoutConf.containsKey(SpatioTextualConstants.textualPredicate))
			this.textualPredicate1 = (String) spoutConf.get(SpatioTextualConstants.textualPredicate);
		else 
			this.textualPredicate1 = SpatioTextualConstants.none;
		if (spoutConf.containsKey(SpatioTextualConstants.textualPredicate2))
			this.textualPredicate2 = (String) spoutConf.get(SpatioTextualConstants.textualPredicate2);
		else 
			this.textualPredicate2 = SpatioTextualConstants.none;
		if (spoutConf.containsKey(SpatioTextualConstants.joinTextualPredicate))
			this.joinTextualPredicate = (String) spoutConf.get(SpatioTextualConstants.joinTextualPredicate);
		else 
			this.joinTextualPredicate = SpatioTextualConstants.none;
		if (spoutConf.containsKey(SpatioTextualConstants.queryDistance))
			this.distance = (Double) spoutConf.get(SpatioTextualConstants.queryDistance);
		else 
			this.distance = null;
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(SpatioTextualConstants.queryTypeField, SpatioTextualConstants.queryIdField, SpatioTextualConstants.focalXCoordField, SpatioTextualConstants.focalYCoordField, SpatioTextualConstants.queryXMinField,
				SpatioTextualConstants.queryYMinField, SpatioTextualConstants.queryXMaxField, SpatioTextualConstants.queryYMaxField, SpatioTextualConstants.kField, SpatioTextualConstants.queryTextField,
				SpatioTextualConstants.queryText2Field, SpatioTextualConstants.queryTimeStampField, SpatioTextualConstants.dataSrc, SpatioTextualConstants.dataSrc2, SpatioTextualConstants.queryCommand, SpatioTextualConstants.queryDistance,
				SpatioTextualConstants.textualPredicate, SpatioTextualConstants.textualPredicate2, SpatioTextualConstants.joinTextualPredicate));
	}

}
