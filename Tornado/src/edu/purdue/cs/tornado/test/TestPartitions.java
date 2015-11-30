package edu.purdue.cs.tornado.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.loadbalance.Partition;

public class TestPartitions {

	public static void main(String[] args) throws InterruptedException {
		String filePath = "/home/ahmed/Downloads/sample_usa_tweets.csv";
		ArrayList<Point> points = new ArrayList<Point>();
		ArrayList<Partition> partitions = new ArrayList<Partition>();
		FileInputStream fstream;
		BufferedReader br ;
		String tweet="";
		try {
			fstream = new FileInputStream(filePath);
			br = new BufferedReader(new InputStreamReader(fstream));
			int count=0;
			while((tweet = br.readLine()) != null) {
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
				
				if(Double.compare(lat, 0.0)==0||Double.compare(lon, 0)==0) 
					continue;
				if(Double.compare(lat, 0.0)<0||Double.compare(lon, -50)>0) 
					continue;
				Point xy = SpatialHelper.convertFromLatLonToXYPoint(new LatLong(lat, lon));
				points.add(xy);
				
				
			}
			partitions = PartitionsHelper.getKDBasedParitionsFromPoints(1024, points);
			
			FileOutputStream fos = new FileOutputStream("resources/partitions1024.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(partitions);
			oos.close();
			
			
			// read object from file
			FileInputStream fis = new FileInputStream("resources/partitions1024.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			ArrayList<Partition> partitions2= (ArrayList<Partition> ) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
		

	}
}
