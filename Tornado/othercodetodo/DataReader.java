package edu.purdue.cs.tornado.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.QueryType;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.JoinQuery;
import edu.purdue.cs.tornado.messages.KNNQuery;
import edu.purdue.cs.tornado.messages.Query;
import edu.purdue.cs.tornado.spouts.QueriesFileSystemSpout;

public class DataReader {
	public static ArrayList<DataObject> readTweets(String filePath, int numberOfDataObjects){
		ArrayList<DataObject> allObjects = new ArrayList<DataObject>();
		try {
			FileInputStream fstream = new FileInputStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line;
			DataObject obj;
			int i = 0;
			while (i < numberOfDataObjects && (line = br.readLine()) != null) {
				obj = parseTweetToDataObject(line,i);
				if (obj == null)
					continue;
				allObjects.add(obj);
				i++;

			}
			br.close();
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return allObjects;
	}
	public static DataObject parseTweetToDataObject(String tweet,Integer id) {
		LatLong latLong = new LatLong();
		String[] tweetParts = tweet.split(",");
		if (tweetParts.length < 5)
			return null;
	//	String id = tweetParts[0];
	
		double lat = 0.0;
		double lon = 0.0;

		try {
			lat = Double.parseDouble(tweetParts[2]);

			lon = Double.parseDouble(tweetParts[3]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (Double.compare(lat, 0.0) == 0 && Double.compare(lon, 0.0) == 0)
			return null;
		if (lat < SpatioTextualConstants.minLat || lat > SpatioTextualConstants.maxLat)
			return null;
		if (lon < SpatioTextualConstants.minLong || lon > SpatioTextualConstants.maxLong)
			return null;
		latLong.setLatitude(lat);
		latLong.setLongitude(lon);
		String textContent = "";
		int i = 5;
		while (i < tweetParts.length)
			textContent = textContent + tweetParts[i++] + " ";

		Point xy = SpatialHelper.convertFromLatLonToXYPoint(latLong);
		Date date = new Date();

		DataObject obj = new DataObject(id, xy, textContent, date.getTime(), Command.addCommand);
		obj.setSrcId("Tweets");
		return obj;
	}
	static public  Query buildQueriesFromUsingOtherLocations(String line, String srcId, int keywordCountVal, String dataSrc1, String dataSrc2, Double distance, QueryType queryType, Double spatialRangeVal, TextualPredicate textualPredicate1, TextualPredicate textualPredicate2,
			Integer k, Double xCoord, Double yCoord,Integer id) {
		StringTokenizer stringTokenizer = new StringTokenizer(line, ",");

		//String id = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		
		String textContent = "";
		while (stringTokenizer.hasMoreTokens())
			textContent = textContent + stringTokenizer.nextToken() + " ";
		String[] keywordsArr = textContent.split(" ");
		ArrayList<String> queryText1 = new ArrayList<String>();
		ArrayList<String> queryText2 = new ArrayList<String>();

		for (int i = 0; i < keywordCountVal; i++) {
			queryText1.add(keywordsArr[i]);
			queryText2.add(keywordsArr[keywordsArr.length - i - 1]);
		}

		Date date = new Date();

		Query q = new Query();
		q.setSrcId(srcId);
		q.setQueryId(id);
		q.setCommand(Command.addCommand);
		q.setDataSrc(dataSrc1);
		
		q.setQueryType(queryType);
		q.setTimeStamp(date.getTime());
		if (queryType.equals(QueryType.queryTextualRange)) {
			q.setSpatialRange(new Rectangle(new Point(xCoord, yCoord), new Point(xCoord + spatialRangeVal, yCoord + spatialRangeVal)));
			q.setTextualPredicate(textualPredicate1);
			q.setQueryText(queryText1);
		} else if (queryType.equals(QueryType.queryTextualKNN)) {
			((KNNQuery)q).setFocalPoint(new Point(xCoord, yCoord));
			((KNNQuery)q).setTextualPredicate(textualPredicate1);
			((KNNQuery)q).setQueryText(queryText1);
			((KNNQuery)q).setK(k);
		} else if (queryType.equals(QueryType.queryTextualSpatialJoin)) {
			((JoinQuery)q).setSpatialRange(new Rectangle(new Point(xCoord, yCoord), new Point(xCoord + spatialRangeVal, yCoord + spatialRangeVal)));
			((JoinQuery)q).setTextualPredicate(textualPredicate1);
			((JoinQuery)q).setTextualPredicate2(textualPredicate2);
			((JoinQuery)q).setQueryText(queryText1);
			((JoinQuery)q).setQueryText(queryText2);
			((JoinQuery)q).setDistance(distance);
		}
		return q;
	}
	
	static public Query buildQuery(String line, String srcId, int keywordCountVal, String dataSrc1, String dataSrc2, Double distance, QueryType queryType, Double spatialRangeVal, TextualPredicate textualPredicate1, TextualPredicate textualPredicate2,
			Integer k,Integer id) {
		StringTokenizer stringTokenizer = new StringTokenizer(line, ",");

		
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
		ArrayList<String> queryText1 = new ArrayList<String>();
		ArrayList<String> queryText2 = new ArrayList<String>();

		for (int i = 0; i < keywordCountVal; i++) {
			queryText1.add(keywordsArr[i]);
			queryText2.add(keywordsArr[keywordsArr.length - i - 1]);
		}

		Date date = new Date();

		Query q = new Query();
		q.setSrcId(srcId);
		q.setQueryId(id);
		q.setCommand(Command.addCommand);
		q.setDataSrc(dataSrc1);
		q.setQueryType(queryType);
		q.setTimeStamp(date.getTime());
		if (queryType.equals(QueryType.queryTextualRange)) {
			q.setSpatialRange(new Rectangle(new Point(xCoord, yCoord), new Point(xCoord + spatialRangeVal, yCoord + spatialRangeVal)));
			q.setTextualPredicate(textualPredicate1);
			q.setQueryText(queryText1);
		} else if (queryType.equals(QueryType.queryTextualKNN)) {
			((KNNQuery)q).setFocalPoint(new Point(xCoord, yCoord));
			((KNNQuery)q).setTextualPredicate(textualPredicate1);
			((KNNQuery)q).setQueryText(queryText1);
			((KNNQuery)q).setK(k);
		} else if (queryType.equals(QueryType.queryTextualSpatialJoin)) {
			((JoinQuery)q).setSpatialRange(new Rectangle(new Point(xCoord, yCoord), new Point(xCoord + spatialRangeVal, yCoord + spatialRangeVal)));
			((JoinQuery)q).setTextualPredicate(textualPredicate1);
			((JoinQuery)q).setTextualPredicate2(textualPredicate2);
			((JoinQuery)q).setQueryText(queryText1);
			((JoinQuery)q).setQueryText(queryText2);
			((JoinQuery)q).setDistance(distance);
		}
		return q;
	}
	static public ArrayList<Query> readQueries(String fileName, int numberOfqueries,String srcId, int keywordCountVal, String dataSrc1, String dataSrc2, Double distance, QueryType queryType, Double spatialRangeVal, TextualPredicate textualPredicate1, TextualPredicate textualPredicate2,
			Integer k) {
		ArrayList<Query> allQueiries = new ArrayList<Query>();
		QueriesFileSystemSpout queriesSpout = new QueriesFileSystemSpout(null, 0);
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line;
			Query obj;
			int i = 0;
			while ((line = br.readLine()) != null && i < numberOfqueries) {
				if ("".equals(line))
					continue;
				obj = queriesSpout.buildQuery(line, srcId, keywordCountVal,dataSrc1, dataSrc2, distance, queryType, spatialRangeVal, textualPredicate1, textualPredicate2, k);
				if (obj == null)
					continue;
				allQueiries.add(obj);
				i++;

			}
			br.close();
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return allQueiries;
	}
	static public ArrayList<Query> readQueriesFromTweets(String fileName, int numberOfqueries,String srcId, int keywordCountVal, String dataSrc1, String dataSrc2, Double distance, QueryType queryType, Double spatialRangeVal, TextualPredicate textualPredicate1, TextualPredicate textualPredicate2,
			Integer k) {
		ArrayList<Query> allQueiries = new ArrayList<Query>();
		QueriesFileSystemSpout queriesSpout = new QueriesFileSystemSpout(null, 0);
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line;
			Query obj;
			int i = 0;
			while ((line = br.readLine()) != null && i < numberOfqueries) {
				if ("".equals(line))
					continue;
				obj = queriesSpout.buildQuery(line, srcId, keywordCountVal,dataSrc1, dataSrc2, distance, queryType, spatialRangeVal, textualPredicate1, textualPredicate2,k);
				if (obj == null)
					continue;
				allQueiries.add(obj);
				i++;

			}
			br.close();
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return allQueiries;
	}
	static public ArrayList<Query> readQueriesFromTweetsLocations(String fileName, int numberOfqueries,String srcId, int keywordCountVal, String dataSrc1, String dataSrc2, Double distance, QueryType queryType, Double spatialRangeVal, TextualPredicate textualPredicate1, TextualPredicate textualPredicate2,
			Integer k, ArrayList<Point> points) {
		ArrayList<Query> allQueiries = new ArrayList<Query>();
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line;
			Query obj;
			int i = 0;
			while ((line = br.readLine()) != null && i < numberOfqueries) {
				if ("".equals(line))
					continue;
				obj = buildQueriesFromUsingOtherLocations(line, srcId, keywordCountVal,dataSrc1, dataSrc2, distance, queryType, spatialRangeVal, textualPredicate1, textualPredicate2, k, points.get((i*17)%points.size()).getX(),points.get((i*17)%points.size()).getX(),i);
				if (obj == null)
					continue;
				allQueiries.add(obj);
				i++;

			}
			br.close();
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return allQueiries;
	}
}
