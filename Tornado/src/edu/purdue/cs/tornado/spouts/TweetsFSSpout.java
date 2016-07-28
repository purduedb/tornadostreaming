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

public class TweetsFSSpout extends FileSpout {

	Integer spoutReplication;
	LatLong latLong;
	DataObject previousObject;
	int countId;

	public TweetsFSSpout(Map spoutConf, Integer initialSleepDuration, Integer spoutReplication) {
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
			i = (long) 1;
		String tweet = "";
		try {
			if ((tweet = br.readLine()) == null) {
				System.out.println("Done file and reopen emitteed"+i);
				i = (long)1;
				br.close();
				connectToFS();

			}
			if (tweet == null || tweet.isEmpty()) {
				emit_previous();
				return;
			}
			i = i + 1;

			emitTweet(tweet, i);

			sleep();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return;

		}
		

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
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(SpatioTextualConstants.objectIdField, SpatioTextualConstants.dataObject));
	}

}
