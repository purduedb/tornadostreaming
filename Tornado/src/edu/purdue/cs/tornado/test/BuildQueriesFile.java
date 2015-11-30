package edu.purdue.cs.tornado.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import edu.purdue.cs.tornado.helper.RandomGenerator;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;

public class BuildQueriesFile {
	static RandomGenerator rg = new RandomGenerator(0);
	static String tweetskeywordsfilePath ="/media/D/googleDrive/walid research/datasets/querykeywordssorted/sortedTweetskeywords.txt"; //popularityCutoff=1600
	static String poikeywordsfilePath="/media/D/googleDrive/walid research/datasets/querykeywordssorted/sortedPOIkeywords.txt";  //popularityCutoff=10000
//	static String outputfilePath="/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
//	static String outputfilePath="/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
	static String outputfilePath="/media/D/googleDrive/walid research/datasets/querykeywordssorted/HybridQueries.csv";
	
	
	static ArrayList<String> keywords = new ArrayList<>();
	public static void main(String[] args) throws IOException {
		PrintWriter writer = new PrintWriter(outputfilePath, "UTF-8");
		
		buildPopularKeywords(tweetskeywordsfilePath, 1600);
		buildPopularKeywords(poikeywordsfilePath, 10000);
		
		//schema, id, x,y, text 
		for(int i=0;i<1000000;i++){
			String text="";
			Double x = rg.nextDouble(0,SpatioTextualConstants.xMaxRange-(SpatioTextualConstants.xMaxRange/10));
			Double y = rg.nextDouble(0,SpatioTextualConstants.yMaxRange-(SpatioTextualConstants.yMaxRange/10));
			for(int j=0;j<50;j++){
				text= text+ keywords.get(rg.nextInt(keywords.size()))+" ";
			}
			writer.println(i+","+x+","+y+","+text+"\n");
			
		}
		writer.close();

	}
	private static void buildPopularKeywords(String fileName, Integer cuttOff) throws IOException{
		FileInputStream fstream = new FileInputStream(fileName);
		BufferedReader 	br = new BufferedReader(new InputStreamReader(fstream));
		String line;
		while ((line = br.readLine()) != null) {
			String [] words = line.split(",");
			String keyword=words[0];
			Integer freq=Integer.parseInt(words[1]);
			if(freq>cuttOff)
				keywords.add(keyword);
				

		}
		br.close();
		fstream.close();
		
	}
}
