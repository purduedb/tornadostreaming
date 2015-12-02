package edu.purdue.cs.tornado.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.messages.DataObject;

public class BuildFrequenceySortedWordList {
	static HashMap<String, Integer> wordsMap = new HashMap<String, Integer>();
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		String tweetsFile = "/home/ahmed/Downloads/sample_usa_tweets.csv";
		String poiFIle = "/home/ahmed/Downloads/output.txt";
		String outputfilePath="/home/ahmed/Downloads/soredTweetskeywords.txt";
		readTweets(tweetsFile);
	//	readPOIs(poiFIle);
		
		Map<String, Integer> treeMap = sortByComparator(wordsMap);
		printMap(treeMap,outputfilePath);
	}
	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Integer>> list = 
			new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
                                           Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	public static void printMap(Map<String, Integer> map,String outputfilePath) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(outputfilePath, "UTF-8");
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			writer.println(entry.getKey()+"," + entry.getValue());
		}
		writer.close();
	}
	private static void readTweets(String fileName) {
		
		String tweet="";
		ArrayList<String> keywords;
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader 	br = new BufferedReader(new InputStreamReader(fstream));
			while ((tweet = br.readLine()) != null) {
				keywords = parseTweet( tweet);
				addFrequentWords(keywords);

			}
			br.close();
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

	}
	private static void readPOIs(String fileName) {
		
		String tweet="";
		ArrayList<String> keywords;
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader 	br = new BufferedReader(new InputStreamReader(fstream));
			while ((tweet = br.readLine()) != null) {
				keywords = parsePOILine( tweet);
				addFrequentWords(keywords);

			}
			br.close();
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

	}
	private static void addFrequentWords (ArrayList<String> keywords){
		if(keywords==null) return;
		for(String keyword:keywords){
			if(!wordsMap.containsKey(keyword)){
				wordsMap.put(keyword, new Integer(1));
			}else{
				Integer count = wordsMap.get(keyword);
				wordsMap.put(keyword, count+1);
			}
		}
	}
	private static ArrayList<String> parseTweet(String tweet) {
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
		ArrayList<String> objectTextList = TextHelpers.transformIntoSortedArrayListOfString(textContent);
		return objectTextList;
	}

	private static ArrayList<String> parsePOILine(String line) {
		StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
		String id = "", text = "";
		Double lat, lon;
		if (stringTokenizer.hasMoreTokens()) {
			id = stringTokenizer.nextToken();
		} else
			return null;
		if (stringTokenizer.hasMoreTokens()) {
			lat = Double.parseDouble(stringTokenizer.nextToken());
		} else
			return null;
		if (stringTokenizer.hasMoreTokens()) {
			lon = Double.parseDouble(stringTokenizer.nextToken());
		} else
			return null;
		if (stringTokenizer.hasMoreTokens()) {
			text = stringTokenizer.nextToken();
		} else
			text = "";
		Point point = SpatialHelper.convertFromLatLonToXYPoint(new LatLong(lat, lon));
		DataObject dataObject = new DataObject();
		Date date = new Date();
		// for now just building random data
		dataObject.setOriginalText(text);
		ArrayList<String> textContent = TextHelpers.transformIntoSortedArrayListOfString(text);
		return textContent;
	}
}
