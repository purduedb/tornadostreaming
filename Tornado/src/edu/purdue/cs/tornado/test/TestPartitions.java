package edu.purdue.cs.tornado.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.QueryType;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;
import edu.stanford.nlp.patterns.Data;

public class TestPartitions {

	public static void main(String[] args) throws InterruptedException {
//		String filePath = "/media/D/googleDrive/walid research/datasets/sample_usa_tweets.csv";
//		String queriesFilePath = "/media/D/datasets/tweetsForQueries.csv";
//		ArrayList<Query> queries = DataReader.readQueries(queriesFilePath, 100000, "scr",3,"scr11", null, null, SpatioTextualConstants.queryTextualRange, 5.0, TextualPredicate.OVERlAPS, null, null);
		test1();

	}
	static void test1(){
		String filePath = "C:\\datasets\\tweets\\sampletweets.csv";
		String queriesFilePath = "C:\\datasets\\tweets\\tweetsForQueries.csv";
		ArrayList<Point> points = new ArrayList<Point>();
		ArrayList<Partition> partitions = new ArrayList<Partition>();
		FileInputStream fstream;
		BufferedReader br ;
		String tweet="";
		try {
			ArrayList<DataObject>tweets = DataReader.readTweets(filePath, 100000);
				
			for(DataObject obj:tweets)
				points.add(obj.getLocation());
			System.out.println("Done reading data");
			ArrayList<Query> queries = DataReader.readQueries(queriesFilePath, 100000, "scr",3,"scr11", null, null, QueryType.queryTextualRange, 5.0, TextualPredicate.OVERlAPS, null, null);
			System.out.println("Done reading Queries");
			//	partitions = PartitionsHelper.getKDBasedParitionsFromPoints(64, points);
			
			
	//		writePartitionsToFile(4,points,queries,1024);
//			writePartitionsToFile(9,points,queries,1024);
//			writePartitionsToFile(16,points,queries,1024);
//			writePartitionsToFile(25,points,queries,1024);
////			writePartitionsToFile(36,points,queries,1024);
		  //  writePartitionsToFile(64,points,queries,64);
			//writePartitionsToFile(64,points,queries,128);
		//	writePartitionsToFile(64,points,queries,256);
		//	writePartitionsToFile(64,points,queries,512);
		//	writePartitionsToFile(64,points,queries,1024);
			writePartitionsToFile(25,points,queries,512);
	//		writePartitionsToFile(100,points,queries,1024);
		//	writePartitionsToFile(250,points,queries,1024);
	//		writePartitionsToFile(500,points,queries,1024);
		//	writePartitionsToFile(1000,points,queries,1024);
	//		writePartitionsToFile(16,points,queries,64);
			
			// read object from file
			//			FileInputStream fis = new FileInputStream("resources/partitionsDataAndQueries36_1024.ser");
			//			ObjectInputStream ois = new ObjectInputStream(fis);
			//			ArrayList<Partition> partitions2= (ArrayList<Partition> ) ois.readObject();
			//			ois.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	static void writePartitionsToFile(int numOfPartitions, ArrayList<Point> points, ArrayList<Query> queries ,int gridGran) throws Exception{
		//ArrayList<Partition>partitions = PartitionsHelper.getKDBasedParitionsFromPointsAndQueries(numOfPartitions, points, queries,gridGran);
		ArrayList<Partition>partitions = PartitionsHelper.getKDBasedParitionsFromPointsAndQueriesPriority(numOfPartitions, points, queries,gridGran);
		System.out.println("Done building partitions");
		FileOutputStream fos = new FileOutputStream("resources/partitions"+numOfPartitions+"_"+gridGran+"_prio.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(partitions);
		oos.close();
	}
	static void test2(){
		
		try {
	
			ArrayList<Cell> partitions = PartitionsHelper.readSerializedPartitions("resources/partitionsDataAndQueries16.ser");
			System.out.print(partitions.toString());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	

}
