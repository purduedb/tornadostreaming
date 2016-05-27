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
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;

/**
 * This is a sample of a transient spatio-textual object generator
 * 
 * @author Ahmed Mahmood
 *
 */
public class BrinkhoffSpout extends BaseRichSpout {
	public static final String BRINKHOFF_FILE_PATH = "BRINKHOFF_FILE_PATH";
	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector collector;
	Map conf;
	BufferedReader br;
	int i = 0;
	FileInputStream fstream;
	String filePath;

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

		String line = "";
		try {

			// Read File Line By Line
			if ((line = br.readLine()) == null) {
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
		if (line == null || line.isEmpty())
			return;
		StringTokenizer stringTokenizer = new StringTokenizer(line, " ");

		String id = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		id =""+Integer.parseInt(id)%100;
		String dateString = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		String command = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		//dateString = stringTokenizer.hasMoreTokens()?stringTokenizer.nextToken():"";

		Double lat = 0.0;
		Double lon = 0.0;

		try {
			lat = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;

			lon = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;
		} catch (Exception e) {
			e.printStackTrace();
		}

		String textContent = ""+id;
//		String dummy = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
//		while (stringTokenizer.hasMoreTokens())
//			textContent = textContent + stringTokenizer.nextToken() + " ";
		
	//	LatLong latlongOrinal= SpatialHelper.convertFromXYToLatLonTo(new Point(lat,lon), 39.4425565320774, -85.7208251953125, 40.1641823503742, -86.7837524414063);
		
		Point xy = SpatialHelper.convertFromLatLonToXYPoint(new LatLong(lat,lon));

		Double xCoord = xy.getX();
		Double yCoord = xy.getY();

		Date date = new Date();

		this.collector.emit(new Values(id, xCoord, yCoord, textContent, date.getTime(), Command.addCommand));
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
	
		this.conf = conf;
		this.filePath = "/media/E/work/purdue/database/research/datasets/trajecotry/brinkhoffdataset/907.txt";//(String) conf.get(TWEETS_FILE_PATH);
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
