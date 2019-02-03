package edu.purdue.cs.tornado.spouts;

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
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.messages.DataObject;

public class TweetsFSSpoutSlow extends FileSpout {

	Integer spoutReplication;
	LatLong latLong;
	DataObject previousObject;
	int countId;

	public TweetsFSSpoutSlow(Map spoutConf, Integer initialSleepDuration, Integer spoutReplication) {
		super(spoutConf, initialSleepDuration);
		this.spoutReplication = spoutReplication;
		previousObject = null;
		countId = 0;
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

	//	sleep();

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
			countId++;
			if (countId >= Integer.MAX_VALUE)
				countId = 0;
			
			DataObject obj = getDataObject(new Integer(countId), xy, textContent, date.getTime(), Command.addCommand);
			previousObject = obj;
			if(i%100<sleepDurationMicroSec)//this variable is used to control the flow 
				this.collector.emit(new Values(new Integer(countId), getDataObject(obj)));

		}
	}

	private void emit_previous() {
		if (previousObject != null)
			for (int j = 0; j < spoutReplication; j++) {
				if (countId >= Integer.MAX_VALUE)
					countId = 0;
				
				DataObject obj = getDataObject(countId, previousObject.getLocation(), previousObject.getOriginalText(), (new Date()).getTime(), Command.addCommand);
				if(i%100<sleepDurationMicroSec)//this variable is used to control the flow 
					this.collector.emit(new Values(previousObject.getObjectId(), getDataObject(obj)));
			}
	}

	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		super.open(conf, context, collector);
		latLong = new LatLong();
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(SpatioTextualConstants.objectIdField, SpatioTextualConstants.dataObject));
	}
	
	//Functions below are used to make a new data object to emit
	@Override
	public DataObject getDataObject() {
		return new DataObject();
	}
	@Override
	public DataObject getDataObject(DataObject other) {
		return new DataObject(other);
	}
	@Override
	public DataObject getDataObject(Integer objectId, Point location, String originalText, Long timeStamp,
			Command command) {
		return new DataObject(objectId, location, originalText,timeStamp,command);
	}

}
