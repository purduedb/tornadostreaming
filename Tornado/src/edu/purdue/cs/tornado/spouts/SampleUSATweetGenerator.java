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
package edu.purdue.cs.tornado.spouts;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.RandomGenerator;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;

/**
 * This is a sample of a transient spatio-textual object generator
 * 
 * @author Ahmed Mahmood
 *
 */
public class SampleUSATweetGenerator extends BaseRichSpout {
	public static final String TWEETS_FILE_PATH = "TWEETS_FILE_PATH";
	private RandomGenerator randomGenerator;
	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector collector;
	ArrayList<String> tweets;
	Map conf;
	BufferedReader br;
	int i = 0;
	FileInputStream fstream;
	String filePath;//= "/home/ahmed/Downloads/sample_usa_tweets.csv"; //this is the sample path 

	public void ack(Object msgId) {
		System.out.println("OK:" + msgId);
	}

	public void close() {
	}

	public void fail(Object msgId) {
		System.out.println("FAIL:" + msgId);
	}

	@Override
	public void nextTuple() {

		String tweet = "";
		try {

			// Read File Line By Line
			if ((tweet = br.readLine()) == null) {
				br.close();
				fstream = new FileInputStream(filePath);
				br = new BufferedReader(new InputStreamReader(fstream));

			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			try {
				br.close();
				fstream = new FileInputStream(filePath);
				br = new BufferedReader(new InputStreamReader(fstream));

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace(System.err);
			}

		}
		if (tweet == null || tweet.isEmpty())
			return;
		StringTokenizer stringTokenizer = new StringTokenizer(tweet, ",");

		String id = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";

		String dateString = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		//dateString = stringTokenizer.hasMoreTokens()?stringTokenizer.nextToken():"";

		Double lat = 0.0;
		Double lon = 0.0;

		try {
			lat = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;

			lon = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;
		} catch (Exception e) {
			e.printStackTrace();
		}

		String textContent = "";
		String dummy = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		while (stringTokenizer.hasMoreTokens())
			textContent = textContent + stringTokenizer.nextToken() + " ";

		Point xy = SpatialHelper.convertFromLatLonToXYPoint(new LatLong(lat, lon));

		Double xCoord = xy.getX();
		Double yCoord = xy.getY();

		Date date = new Date();

		this.collector.emit(new Values(id, xCoord, yCoord, textContent, date.getTime(), SpatioTextualConstants.addCommand));
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		randomGenerator = new RandomGenerator(SpatioTextualConstants.generatorSeed);
		this.conf = conf;
		this.filePath = (String) conf.get(TWEETS_FILE_PATH);
		tweets = new ArrayList<String>();
		try {
			//FileInputStream fstream = new FileInputStream("datasources/twitterdata.csv");
			fstream = new FileInputStream(filePath);
			br = new BufferedReader(new InputStreamReader(fstream));
			//			try {
			//				Thread.sleep(30000);
			//			} catch (InterruptedException e) {
			//				e.printStackTrace();
			//			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(SpatioTextualConstants.objectIdField, SpatioTextualConstants.objectXCoordField, SpatioTextualConstants.objectYCoordField, SpatioTextualConstants.objectTextField, SpatioTextualConstants.timeStamp,
				SpatioTextualConstants.dataObjectCommand));
	}

}
