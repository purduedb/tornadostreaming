package edu.purdue.cs.tornado.test;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import edu.purdue.cs.tornado.evaluator.Operator;
import edu.purdue.cs.tornado.evaluator.Query2;
import edu.purdue.cs.tornado.evaluator.SpatioTextualEvaluatorBolt;
import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.DataSourceType;
import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.local.LocalHybridIndex;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.index.local.LocalTextIndex;
import edu.purdue.cs.tornado.index.local.LocalTextInvertedListIndex;
import edu.purdue.cs.tornado.index.local.hybridgrid.LocalHybridGridIndex;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.messages.DataObject;
public class TestBuildingBlocksEvaluator {
	static Integer fineGridGran=64;
	
	public static void main(String[] args) throws IOException {
		Query2 query = new Query2("Queries","query1",true);
		
		
	
		
		query.addDataSrcOperator(Operator.getSpatialInsideOperator("Tweets", new Rectangle(new Point(0.0,0.0),new Point(100.0,100.0))));
		
		ArrayList<String >keywords = new ArrayList<String>();
		keywords.add("love");
		keywords.add("sale");
		query.addDataSrcOperator(Operator.getTextContainOperator("Tweets", keywords));
		
		SpatioTextualEvaluatorBolt textualEvaluatorBolt = new SpatioTextualEvaluatorBolt("evalautor",LocalIndexType.HYBRID_GRID,GlobalIndexType.GRID,null,SpatioTextualConstants.defaultFineGridGranularityX);
		DataSourceInformation tweetsSataSourceInformation  = new DataSourceInformation(new Rectangle(new Point(0.0,0.0), new Point(10000.0,10000.0)),"Tweets", DataSourceType.DATA_SOURCE, SpatioTextualConstants.persistentPersistenceState, SpatioTextualConstants.NOTCLEAN,true ,null,SpatioTextualConstants.defaultFineGridGranularityX);
		
		LocalHybridIndex  localGridIndex = new LocalHybridGridIndex(new Rectangle(new Point(0.0,0.0), new Point(10000.0,10000.0)), tweetsSataSourceInformation,fineGridGran); 
		HashMap<String, LocalHybridIndex> localDataSpatioTextualIndex = new HashMap<String, LocalHybridIndex>();
		localDataSpatioTextualIndex.put("Tweets", localGridIndex) ;
		
		LocalTextInvertedListIndex localTextInvertedListIndex = new LocalTextInvertedListIndex();
		HashMap<String, LocalTextInvertedListIndex> localInvertedLists = new HashMap<String, LocalTextInvertedListIndex>();
		localInvertedLists.put("Tweets", localTextInvertedListIndex);
		
		
		
		
		HashMap<String , DataSourceInformation> dataSourcesInformation = new HashMap<String , DataSourceInformation>();
		dataSourcesInformation.put("Tweets", tweetsSataSourceInformation);
		textualEvaluatorBolt.setSourcesInformations(dataSourcesInformation);
		
		ArrayList<DataObject> allDataObjects= readTweets();
		
		testIndexingTime(allDataObjects, tweetsSataSourceInformation);
	}
	
	static void testIndexingTime(ArrayList<DataObject> allObjects,DataSourceInformation dataSourcesInformation){
		LocalTextInvertedListIndex localTextInvertedListIndex = new LocalTextInvertedListIndex();
		Rectangle entireSpace = new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange));
		LocalHybridGridIndex spatialGridIndex = new LocalHybridGridIndex(entireSpace , dataSourcesInformation, SpatioTextualConstants.defaultFineGridGranularityX, SpatioTextualConstants.defaultFineGridGranularityY, true, 0);
		LocalHybridGridIndex hybridGridIndex = new LocalHybridGridIndex(entireSpace , dataSourcesInformation, SpatioTextualConstants.defaultFineGridGranularityX, SpatioTextualConstants.defaultFineGridGranularityY, false, 0);
		LocalHybridGridIndex spatialMultiLevelGridIndex = null;//new LocalHybridMultiGridIndex(entireSpace , dataSourcesInformation, SpatioTextualConstants.defaultFineGridGranularityX, SpatioTextualConstants.defaultFineGridGranularityY, true);
		LocalHybridGridIndex hybridMultiLevelGridIndex = null;// new LocalHybridMultiGridIndex(entireSpace , dataSourcesInformation, SpatioTextualConstants.defaultFineGridGranularityX, SpatioTextualConstants.defaultFineGridGranularityY, false);
		System.gc();
		System.gc();
		System.out.println("-------------Begin indexing files----------------");
		Long hybridMultiLevelGridIndexTime = new Long(0);//insertInTextHybridIndex(hybridMultiLevelGridIndex,allObjects);
		System.out.println("Hybrid multi-level time:"+hybridMultiLevelGridIndexTime/allObjects.size());
		hybridMultiLevelGridIndex=null;
		System.gc();
		System.gc();
		Long localTextInvertedListIndexTime = insertInTextIndex(localTextInvertedListIndex,allObjects);
		System.out.println("Inverted list time:"+localTextInvertedListIndexTime/allObjects.size());
		localTextInvertedListIndex=null;
		System.gc();
		System.gc();
		Long spatialGridIndexTime = insertInTextHybridIndex(spatialGridIndex,allObjects);
		System.out.println("Spatial grid time:"+spatialGridIndexTime/allObjects.size());
		spatialGridIndex=null;
		System.gc();
		System.gc();
		Long hybridGridIndexTIme = insertInTextHybridIndex(hybridGridIndex,allObjects);
		System.out.println("hybrid grid time:"+hybridGridIndexTIme/allObjects.size());
		hybridGridIndex=null;
		System.gc();
		System.gc();
		Long spatialMultiLevelGridIndexTime =new Long(0);// insertInTextHybridIndex(spatialMultiLevelGridIndex,allObjects);
		System.out.println("Spatial multi-level time:"+spatialMultiLevelGridIndexTime/allObjects.size());
		spatialMultiLevelGridIndex=null;
		System.gc();
		System.gc();
		
	}
	private static Long insertInTextIndex(LocalTextIndex textIndex,ArrayList<DataObject> dataObjects){
		Long duration;
		Stopwatch stopwatch = Stopwatch.createStarted();
		for(DataObject obj:dataObjects){
			textIndex.addDataObject(obj);
		}
		stopwatch.stop();
		duration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		return duration;
	}
	private static Long insertInTextHybridIndex(LocalHybridIndex hybridIndex,ArrayList<DataObject> dataObjects){
		Long duration;
		Stopwatch stopwatch = Stopwatch.createStarted();
		for(DataObject obj:dataObjects){
			hybridIndex.addDataObject(obj);
		}
		stopwatch.stop();
		duration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		return duration;
	}
	
	static ArrayList<DataObject>  readTweets(){
		String filePath = "/home/ahmed/Downloads/sample_usa_tweets.csv";
		ArrayList<DataObject> dataObjects = new ArrayList<DataObject>();
		ArrayList<Partition> partitions = new ArrayList<Partition>();
		FileInputStream fstream;
		BufferedReader br ;
		String tweet="";
		Integer i=0;
		Integer countId=0;
		try {
			fstream = new FileInputStream(filePath);
			br = new BufferedReader(new InputStreamReader(fstream));
			int count=0;
			while((tweet = br.readLine()) != null) {
				StringTokenizer stringTokenizer = new StringTokenizer(tweet, ",");

				//String id = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
				Integer id = countId++;//tweetParts[0];
				if(countId>=Integer.MAX_VALUE)countId=0;
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
				String textContent = "";
				String dummy = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
				while (stringTokenizer.hasMoreTokens())
					textContent = textContent + stringTokenizer.nextToken() + " ";
				
				DataObject obj = new DataObject(id, xy, textContent,( new Date()).getTime(), Command.addCommand);
				dataObjects.add(obj);
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return dataObjects;
		
	}
}
