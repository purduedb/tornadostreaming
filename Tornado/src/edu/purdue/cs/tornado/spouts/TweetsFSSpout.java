package edu.purdue.cs.tornado.spouts;

import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.messages.DataObject;

public class TweetsFSSpout extends FileSpout {

	public TweetsFSSpout(Map spoutConf) {
		super(spoutConf);
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

		if (i.equals(Long.MAX_VALUE))
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
		if (tweet == null || tweet.isEmpty())
			return;
		i = i + 1;
		emitTweet(tweet, i);
		sleep();
	}

	private void emitTweet(String tweet, Long msgId) {
		StringTokenizer stringTokenizer = new StringTokenizer(tweet, ",");
		String id = stringTokenizer.hasMoreTokens() ? selfTaskId + "_"+stringTokenizer.nextToken() : "";
		String dateString = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		//dateString = stringTokenizer.hasMoreTokens()?stringTokenizer.nextToken():"";

		Double lat = 0.0;
		Double lon = 0.0;

		try {
			lat = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;

			lon = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}

		String textContent = "";
		String dummy = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		while (stringTokenizer.hasMoreTokens())
			textContent = textContent + stringTokenizer.nextToken() + " ";
		
		Point xy = SpatialHelper.convertFromLatLonToXYPoint(new LatLong(lat, lon));
		Date date = new Date();

		DataObject obj= new DataObject(id,xy,textContent,date.getTime(),SpatioTextualConstants.addCommand);
		if (reliable)
			this.collector.emit(new Values(id,obj), "" + selfTaskId + "_" + (i++));
		else	this.collector.emit(new Values(id,obj));
	}

	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		super.open(conf, context, collector);
		

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(SpatioTextualConstants.objectIdField, SpatioTextualConstants.dataObject));
	}

}
