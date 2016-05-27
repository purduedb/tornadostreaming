package edu.purdue.cs.tornado.spouts;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.RandomGenerator;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.messages.DataObject;

public class TweetsFSSpoutHotSpots extends FileSpout {

	Integer spoutReplication;
	LatLong latLong;
	DataObject previousObject;
	int countId;
//	ArrayList<Point> hotspotPoints;
	int numberOfHotSpotsLocations;
	int numberOfPointSofar;
	public static RandomGenerator r;
	 double hotSpotRange=50;
	 double hotSpotRatio=0.75;
	static int hotSpotPoint=0;
	
	

	public TweetsFSSpoutHotSpots(Map spoutConf, Integer initialSleepDuration, Integer spoutReplication, Double hotSpotRatio) {
		super(spoutConf, initialSleepDuration);
		this.spoutReplication = spoutReplication;
		previousObject = null;
		countId = 0;

		numberOfHotSpotsLocations = 5;
		numberOfPointSofar=0;
		this.hotSpotRatio = hotSpotRatio;
	//	hotspotPoints = new ArrayList<Point>();

	}

	Long i = new Long(0);

	public void ack(Object msgId) {
	}

	public void close() {
	}

	public void fail(Object msgId) {
	}

	@Override
	public void nextTuple() {

		super.nextTuple();
		if (i >= Long.MAX_VALUE)
			i = (long) 0;
		String tweet = "";
		try {
			if ((tweet = br.readLine()) == null) {
				br.close();
				connectToFS();

			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return;

		}
		if (tweet == null || tweet.isEmpty()) {
			emit_previous();
			return;
		}
		i = i + 1;

		emitTweet(tweet, i);

		sleep();

	}

	private void emitTweet(String tweet, Long msgId) {
		String[] tweetParts = tweet.split(",");
		if (tweetParts.length < 5)
			return;

		double lat = 0.0;
		double lon = 0.0;

		try {
			lat = Double.parseDouble(tweetParts[2]);

			lon = Double.parseDouble(tweetParts[3]);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		if (lat < SpatioTextualConstants.minLat || lat > SpatioTextualConstants.maxLat || lon < SpatioTextualConstants.minLong || lon > SpatioTextualConstants.maxLong || (Double.compare(lat, 0.0) == 0 && Double.compare(lon, 0.0) == 0)) {
			emit_previous();
			return;
		}

		latLong.setLatitude(lat);
		latLong.setLongitude(lon);
		String textContent = "";
		int i = 5;
		while (i < tweetParts.length)
			textContent = textContent + tweetParts[i++] + " ";

		Point xy = SpatialHelper.convertFromLatLonToXYPoint(latLong);

	
		
		Date date = new Date();

		for (int j = 0; j < spoutReplication; j++) {
			//			if (reliable)
			//				this.collector.emit(new Values(id, obj), "" + selfTaskId + "_" + (i++));
			//			else
			countId++;//tweetParts[0];
			if (countId >= Integer.MAX_VALUE)
				countId = 0;
//			if(countId%hotSpotRatio==0){
//				xy=hotspotPoints.get(hotSpotPoint);
//				hotSpotPoint=(hotSpotPoint+1)%hotspotPoints.size();
//				xy.X=Math.min(SpatioTextualConstants.xMaxRange-1,xy.X+r.nextDouble()*hotSpotRange);
//				xy.Y=Math.min(SpatioTextualConstants.yMaxRange-1,xy.Y+r.nextDouble()*hotSpotRange);
//			}
			xy.X*=hotSpotRatio;
			xy.Y*=hotSpotRatio;
	
			DataObject obj = new DataObject(new Integer(countId), xy, textContent, date.getTime(), Command.addCommand);
			previousObject = obj;
			//			if (countId % 10 == 0)
			//				this.collector.emit(new Values(countId, obj), "" + selfTaskId + "_" + (countId));
			//			else
			this.collector.emit(new Values(new Integer(countId), obj));

		}
	}

	private void emit_previous() {
		if (previousObject != null)
			for (int j = 0; j < spoutReplication; j++) {
				//				if (reliable)
				//					this.collector.emit(new Values(previousObject.getObjectId(), previousObject), "" + selfTaskId + "_" + (i++));
				//				else
				countId++;//tweetParts[0];
				if (countId >= Integer.MAX_VALUE)
					countId = 0;
				DataObject obj = new DataObject(countId, previousObject.getLocation(), previousObject.getOriginalText(), (new Date()).getTime(), Command.addCommand);
				//				if (countId % 10 == 0)
				//					this.collector.emit(new Values(countId, obj), "" + selfTaskId + "_" + (countId));
				//				else
				this.collector.emit(new Values(previousObject.getObjectId(), obj));
			}
	}

	private void emitTweet_old(String tweet, Long msgId) {
		StringTokenizer stringTokenizer = new StringTokenizer(tweet, ",");
		//Integer id = stringTokenizer.hasMoreTokens() ? selfTaskId + "_" + stringTokenizer.nextToken() : "";
		Integer id = countId++;//tweetParts[0];
		if (countId >= Integer.MAX_VALUE)
			countId = 0;
		String dateString = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		//dateString = stringTokenizer.hasMoreTokens()?stringTokenizer.nextToken():"";

		double lat = 0.0;
		double lon = 0.0;

		try {
			lat = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;

			lon = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		latLong.setLatitude(lat);
		latLong.setLongitude(lon);
		String textContent = "";
		String dummy = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		while (stringTokenizer.hasMoreTokens())
			textContent = textContent + stringTokenizer.nextToken() + " ";

		Point xy = SpatialHelper.convertFromLatLonToXYPoint(latLong);
		Date date = new Date();

		DataObject obj = new DataObject(id, xy, textContent, date.getTime(), Command.addCommand);
		for (int j = 0; j < spoutReplication; j++) {
			if (reliable)
				this.collector.emit(new Values(id, obj), "" + selfTaskId + "_" + (i++));
			else
				this.collector.emit(new Values(id, obj));
		}
	}

	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		super.open(conf, context, collector);
		latLong = new LatLong();
//		hotspotPoints = new ArrayList<Point>();
//		hotspotPoints.add(new Point(100,100));
//		hotspotPoints.add(new Point(500,500));
//		hotspotPoints.add(new Point(800,800));
		r = new RandomGenerator(0);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(SpatioTextualConstants.objectIdField, SpatioTextualConstants.dataObject));
	}

}
