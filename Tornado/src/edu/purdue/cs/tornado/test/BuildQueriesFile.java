package edu.purdue.cs.tornado.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.RandomGenerator;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.messages.DataObject;

public class BuildQueriesFile {
	static RandomGenerator rg = new RandomGenerator(0);
	static String tweetskeywordsfilePath = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/sortedTweetskeywords.txt"; //popularityCutoff=1600
	static String poikeywordsfilePath = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/sortedPOIkeywords.txt"; //popularityCutoff=10000
	static String outputfilePath_1 = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetskeywrod_firstQuantile.csv";
	static String outputfilePath_2 = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetskeywrod_secondQuantile.csv";
	static String outputfilePath_3 = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetskeywrod_thirdQuantile.csv";
	//	static String outputfilePath="/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
	//	static String outputfilePath="/media/D/googleDrive/walid research/datasets/querykeywordssorted/HybridQueries.csv";
	static int totalFreq = 0;
	static int freq1limitIndex = 0;
	static int freq2limitIndex = 0;
	static String tweetsFile = "/media/D/datasets/tweetsForQueries.csv";
	static ArrayList<String> keywords = new ArrayList<>();
	static EnumeratedDistribution<String> distribution;
	static List<Pair<String, Double>> keyWordFreq = new ArrayList<Pair<String, Double>>();

	public static void main(String[] args) throws IOException {
		PrintWriter writer1 = new PrintWriter(outputfilePath_1, "UTF-8");
		PrintWriter writer2 = new PrintWriter(outputfilePath_2, "UTF-8");
		PrintWriter writer3 = new PrintWriter(outputfilePath_3, "UTF-8");
		buildPopularKeywords(tweetskeywordsfilePath);
		int numberOfDataObjects = 5000000;
		//buildPopularKeywords(poikeywordsfilePath, 10000);
		//	ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, 5000000);
		RandomGenerator g = new RandomGenerator(0);
		//schema, id, x,y, text 
		try {
			FileInputStream fstream = new FileInputStream(tweetsFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line;
			DataObject obj;
			int i = 0;
			while (i < numberOfDataObjects && (line = br.readLine()) != null) {
				obj = parseTweetToDataObject(line);
				if (obj == null)
					continue;
				String text1 = "", text2 = "", text3 = "";
				for (int j = 0; j < 5; j++) {
					text1 = text1 + keywords.get(rg.nextInt(freq1limitIndex)) + " ";
					text2 = text2 + keywords.get(rg.nextInt(freq2limitIndex-freq1limitIndex) + freq1limitIndex) + " ";
					text3 = text3 + keywords.get(rg.nextInt(keywords.size()-freq2limitIndex) + freq2limitIndex) + " ";
					//text = text +distribution.sample() + " ";

				}
				writer1.append(i + "," + i + "," + obj.getLocation().getX() + "," + obj.getLocation().getY() + "," + i + "," + text1 + "\n");
				writer2.append(i + "," + i + "," + obj.getLocation().getX() + "," + obj.getLocation().getY() + "," + i + "," + text2 + "\n");
				writer3.append(i + "," + i + "," + obj.getLocation().getX() + "," + obj.getLocation().getY() + "," + i + "," + text3 + "\n");
				i++;

			}
			br.close();
			fstream.close();
			writer1.close();
			writer2.close();
			writer3.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

	}

	static ArrayList<DataObject> readDataObjects(String fileName, int numberOfDataObjects) {
		ArrayList<DataObject> allObjects = new ArrayList<DataObject>();
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line;
			DataObject obj;
			int i = 0;
			while (i < numberOfDataObjects && (line = br.readLine()) != null) {
				obj = parseTweetToDataObject(line);
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

	private static DataObject parseTweetToDataObject(String tweet) {
		LatLong latLong = new LatLong();
		String[] tweetParts = tweet.split(",");
		if (tweetParts.length < 5)
			return null;
		String id = tweetParts[0];

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

		//	Point xy = SpatialHe0lper.convertFromLatLonToXYPoint(latLong);
		Point xy = new Point(lat, lon);
		Date date = new Date();

		DataObject obj = new DataObject(1, xy, textContent, date.getTime(), Command.addCommand);
		obj.setSrcId("" + 1);
		return obj;
	}

	private static void buildPopularKeywords(String fileName) throws IOException {
		FileInputStream fstream = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String line;
		while ((line = br.readLine()) != null) {
			String[] words = line.split(",");
			String keyword = words[0];
			double freq = Integer.parseInt(words[1]);
			keywords.add(keyword);
			totalFreq += freq;
			Pair<String, Double> p = new Pair<String, Double>(keyword, freq);
			keyWordFreq.add(p);
		}
		///	distribution = new EnumeratedDistribution<String>(keyWordFreq);
		br.close();
		fstream.close();
		int freqSum=0;
		for (int i =0;i<keyWordFreq.size();i++) {
			freqSum+=keyWordFreq.get(i).getValue();
			if(freqSum>totalFreq/3){
				freq1limitIndex =i;
				break;
			}
		}
		freqSum=0;
		for (int j =0;j<keyWordFreq.size();j++) {
			freqSum+=keyWordFreq.get(j).getValue();
			if(freqSum>2*totalFreq/3){
				freq2limitIndex =j;
				break;
			}
		}
	}
}
