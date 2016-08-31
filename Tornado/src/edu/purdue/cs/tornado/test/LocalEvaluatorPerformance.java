package edu.purdue.cs.tornado.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import edu.purdue.cs.tornado.evaluator.SpatioTextualEvaluatorBolt;
import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.DataSourceType;
import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.QueryType;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.index.local.hybridgrid.LocalHybridGridIndex;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.JoinQuery;
import edu.purdue.cs.tornado.messages.KNNQuery;
import edu.purdue.cs.tornado.messages.Query;
import edu.purdue.cs.tornado.spouts.FileSpout;
import edu.purdue.cs.tornado.spouts.TweetsFSSpout;

public class LocalEvaluatorPerformance {
	public static String queriesFilePath="/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
	//public static String tweetsFilePath= "/home/ahmed/Downloads/sample_usa_tweets.csv";
	public static String tweetsFilePath= "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
	public static Integer maxTweetSize=3000000;
	public static Integer fineGridGran=64;
	public static void main(String[] args) throws IOException {
//		PrintWriter writer = new PrintWriter("test");
//		System.gc();
//		System.gc();
//		testVolatileRangeQuad(100000, 500.0, 10,100 , 100,writer);
//		writer.close();
//		testQuadTree();
//		testGrid();
		//testMultiGrid();
		testSpatialRangeEffect();
//		
//		countValidObjects (100, 150.0, 25);
	}
	static void testQuadTree() throws FileNotFoundException{
//		PrintWriter writer = new PrintWriter("/media/D/googleDrive/walid research/results/tornado/volatiledataquadtreeparams_splitthresholdeffect.csv");
//		writer.println("Test the effect of split threshold on the quad tree performance: volatile data objects on continuous queries 100000");
//		writer.println("Query Max level:"+100+"  spatial range:"+50.0+", number of keywords:"+10+"*********************************************");
//		writer.println("number of queries,number of keywords,spatial range,Split Threashold, max level, Query time, Data object time");
//		testVolatileRangeQuad(100000, 50.0, 10,20 , 100,writer);
//		System.gc();
//		System.gc();
//		testVolatileRangeQuad(100000, 50.0, 10,100 , 100,writer);
//		System.gc();
//		System.gc();
//		testVolatileRangeQuad(100000, 50.0, 10,500 , 100,writer);
//		System.gc();
//		System.gc();
//		testVolatileRangeQuad(100000, 50.0, 10,1000 , 100,writer);
//		System.gc();
//		System.gc();
//		writer.close();
//		
//		writer = new PrintWriter("/media/D/googleDrive/walid research/results/tornado/volatiledataquadtreeparams_maxleveleffect.csv");
//		writer.println("Test the effect of split threshold on the quad tree performance: volatile data objects on continuous queries 100000");
//		writer.println("split threashold:"+100+"  spatial range:"+50.0+", number of keywords:"+10+"*********************************************");
//		writer.println("number of queries,number of keywords,spatial range,Split Threashold, max level, Query time, Data object time");
//		testVolatileRangeQuad(100000, 50.0, 10,100 , 5,writer);
//		System.gc();
//		System.gc();
//		testVolatileRangeQuad(100000, 50.0, 10,100 , 10,writer);
//		System.gc();
//		System.gc();
//		testVolatileRangeQuad(100000, 50.0, 10,100 , 20,writer);
//		System.gc();
//		System.gc();
//		testVolatileRangeQuad(100000, 50.0, 10,100 , 100,writer);
//		System.gc();
//		System.gc();
//		testVolatileRangeQuad(100000, 50.0, 10,100 , 500,writer);
//		System.gc();
//		System.gc();
//		testVolatileRangeQuad(100000, 50.0, 10,100 , 1000,writer);
//		System.gc();
//		System.gc();
//		writer.close();
//		
		
		PrintWriter writer2 = new PrintWriter("/media/D/googleDrive/walid research/results/tornado/persistancedataquadtreeparams_splitthresholdeffect.csv");
		writer2.println("Test the effect of split threshold on the quad tree performance: persistance data objects on snapshot queries 100000");
		writer2.println("Query Max level:"+100+"  spatial range:"+50.0+", number of keywords:"+10+"*********************************************");
		writer2.println("number of queries,number of keywords,spatial range,Split Threashold, max level, Query time, Data object time");
//		testVolatileRangeQuad(100000, 50.0, 10,5 , 100,writer);
//		System.gc();
//		System.gc();
//		testVolatileRangeQuad(100000, 50.0, 10,10 , 100,writer);
//		System.gc();
//		System.gc();
//		testPersisentRangeHybridQuad(100000, 50.0, 10,20 , 100,writer2);
		System.gc();
		System.gc();
//		testPersisentRangeHybridQuad(100000, 50.0, 10,100 , 100,writer2);
		System.gc();
		System.gc();
//		testPersisentRangeHybridQuad(100000, 50.0, 10,500 , 100,writer2);
		System.gc();
		System.gc();
//		testPersisentRangeHybridQuad(100000, 50.0, 10,1000 , 100,writer2);
		System.gc();
		System.gc();
		writer2.close();
		
		writer2 = new PrintWriter("/media/D/googleDrive/walid research/results/tornado/persistancedataquadtreeparams_maxleveleffect.csv");
		writer2.println("Test the effect of split threshold on the quad tree performance: persistance data objects on snapshot queries 100000");
		writer2.println("split threashold:"+100+"  spatial range:"+50.0+", number of keywords:"+10+"*********************************************");
		writer2.println("number of queries,number of keywords,spatial range,Split Threashold, max level, Query time, Data object time");
	//	testPersisentRangeHybridQuad(100000, 50.0, 10,100 , 5,writer2);
		System.gc();
		System.gc();
	//	testPersisentRangeHybridQuad(100000, 50.0, 10,100 , 10,writer2);
		System.gc();
		System.gc();
	//	testPersisentRangeHybridQuad(100000, 50.0, 10,100 , 20,writer2);
		System.gc();
		System.gc();
	//	testPersisentRangeHybridQuad(100000, 50.0, 10,100 , 100,writer2);
		System.gc();
		System.gc();
	//	testPersisentRangeHybridQuad(100000, 50.0, 10,100 , 500,writer2);
		System.gc();
		System.gc();
	//	testPersisentRangeHybridQuad(100000, 50.0, 10,100 , 1000,writer2);
		System.gc();
		System.gc();
		writer2.close();
	}
	static void testGrid() throws FileNotFoundException{
		PrintWriter writer = new PrintWriter("/media/D/googleDrive/walid research/results/tornado/volatiledatagridparams_grid gran.csv");
		writer.println("Test the effect of split threshold on the grid performance: volatile data objects on continuous queries 100000");
		writer.println(" spatial range:"+50.0+", number of keywords:"+10+"*********************************************");
		writer.println("number of queries,number of keywords,spatial range,granulartiy, Query time, Data object time");

//		testVolatileRangeGrid(4,100000, 50.0, 10,writer);
//		System.gc();
//		System.gc();
		testVolatileRangeGrid(16,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeGrid(64,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeGrid(128,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeGrid(512,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeGrid(1024,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		
		writer.close();	
		
		writer = new PrintWriter("/media/D/googleDrive/walid research/results/tornado/persisteancedatagridparams_grid gran.csv");
		writer.println("Test the effect of split threshold on the grid performance: persitenet data objects on snapshot queries 100000");
		writer.println(" spatial range:"+50.0+", number of keywords:"+10+"*********************************************");
		writer.println("number of queries,number of keywords,spatial range,granulartiy, Query time, Data object time");

//		testPersisentRangeGrid(4,100000, 50.0, 10,writer);
//		System.gc();
//		System.gc();
		testPersisentRangeGrid(16,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeGrid(64,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeGrid(128,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeGrid(512,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeGrid(1024,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		writer.close();	
		
	}
	static void testMultiGrid() throws FileNotFoundException{
		PrintWriter writer = new PrintWriter("/media/D/googleDrive/walid research/results/tornado/volatilemultigridparams_grid gran.csv");
		writer.println("Test the effect of split threshold on the multi grid performance: volatile data objects on continuous queries 100000");
		writer.println(" spatial range:"+50.0+", number of keywords:"+10+"*********************************************");
		writer.println("number of queries,number of keywords,spatial range,granulartiy, Query time, Data object time");

		
		testVolatileRangeMultiGrid(16,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeMultiGrid(64,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeMultiGrid(128,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeMultiGrid(512,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeMultiGrid(1024,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		writer.close();	
		
		writer = new PrintWriter("/media/D/googleDrive/walid research/results/tornado/persisteancemultigridparams_grid gran.csv");
		writer.println("Test the effect of split threshold on the multi grid performance: persitenet data objects on snapshot queries 100000");
		writer.println(" spatial range:"+50.0+", number of keywords:"+10+"*********************************************");
		writer.println("number of queries,number of keywords,spatial range,granulartiy, Query time, Data object time");

	
		testPersisentRangeHybridMultiLevelIndex(16,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeHybridMultiLevelIndex(64,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeHybridMultiLevelIndex(128,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeHybridMultiLevelIndex(512,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeHybridMultiLevelIndex(1024,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		writer.close();	
		
	}
	static void testSpatialRangeEffect() throws FileNotFoundException{
		PrintWriter writer;
		writer = new PrintWriter("/media/D/googleDrive/walid research/results/tornado/persistance_spatial_range_effect_large.csv");
		writer.println("********Quad tree***********");	
//		System.gc();
//		System.gc();
//		testPersisentRangeHybridQuad(100000, 10.0, 10,100 , 100,writer);
//		System.gc();
//		System.gc();
//		testPersisentRangeHybridQuad(100000, 50.0, 10,100 , 100,writer);
//		System.gc();
//		System.gc();
//		testPersisentRangeHybridQuad(100000, 100.0, 10,100 , 100,writer);
//		System.gc();
//		System.gc();
//		testPersisentRangeHybridQuad(100000, 500.0, 10,100 , 100,writer);
//		System.gc();
//		System.gc();
//		testPersisentRangeHybridQuad(100000, 1000.0, 10,100 , 100,writer);
		writer.println("********Multilevel grid***********");	
		System.gc();
		System.gc();
		testPersisentRangeHybridMultiLevelIndex(512,100000,10.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeHybridMultiLevelIndex(512,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeHybridMultiLevelIndex(512,100000, 100.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeHybridMultiLevelIndex(512,100000, 500.0, 10,writer);
//		System.gc();
//		System.gc();
//		testPersisentRangeHybridMultiLevelIndex(512,100000, 1000.0, 10,writer);
		writer.println("********Grid***********");	
		System.gc();
		System.gc();
		testPersisentRangeGrid(512,100000, 10.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeGrid(512,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeGrid(512,100000, 100.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeGrid(512,100000, 500.0, 10,writer);
		System.gc();
		System.gc();
		testPersisentRangeGrid(512,100000, 1000.0, 10,writer);
		writer.close();	
		writer = new PrintWriter("/media/D/googleDrive/walid research/results/tornado/volatile_spatial_range_effect_large.csv");
		writer.println("********Quad tree***********");	
		System.gc();
		System.gc();
	//	testVolatileRangeQuad(100000, 10.0, 10,100 , 100,writer);
		System.gc();
		System.gc();
	//	testVolatileRangeQuad(100000, 50.0, 10,100 , 100,writer);
		System.gc();
		System.gc();
	//	testVolatileRangeQuad(100000, 100.0, 10,100 , 100,writer);
		System.gc();
		System.gc();
	//	testVolatileRangeQuad(100000, 500.0, 10,100 , 100,writer);
//		System.gc();
//		System.gc();
//		testVolatileRangeQuad(100000, 1000.0, 10,100 , 100,writer);
		writer.println("********Multilevel grid***********");	
		System.gc();
		System.gc();
		testVolatileRangeMultiGrid(512,100000,10.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeMultiGrid(512,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeMultiGrid(512,100000, 100.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeMultiGrid(512,100000, 500.0, 10,writer);
//		System.gc();
//		System.gc();
//		testVolatileRangeMultiGrid(512,100000, 1000.0, 10,writer);
		writer.println("********Grid***********");	
		System.gc();
		System.gc();
		testVolatileRangeGrid(512,100000, 10.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeGrid(512,100000, 50.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeGrid(512,100000, 100.0, 10,writer);
		System.gc();
		System.gc();
		testVolatileRangeGrid(512,100000, 500.0, 10,writer);
//		System.gc();
//		System.gc();
//		testVolatileRangeGrid(512,100000, 1000.0, 10,writer);
		writer.close();	
	}
	static void countValidObjects(Integer numberOfqueries, Double spatialRange, Integer numberOfKeywords){
		System.out.println("*****************************************************************************************************************************************");
		System.out.println("***********************Brute force to find qualifiing objects**********************************************");
		System.out.println("Begin reading data objects");
		ArrayList<DataObject> tweets =readTweets("Tweets");
		System.out.println("Done reading data objects");
		System.out.println("begin reading queries");
		ArrayList<Query> queries =addQueries("/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv","querySrc",numberOfqueries,numberOfKeywords,
				"Tweets", null, null, QueryType.queryTextualRange,null,TextualPredicate.OVERlAPS,null,spatialRange) ;
		System.out.println("Done reading queries");
		int count=0;
		for(DataObject obj:tweets){
			for(Query query:queries){
				if (SpatialHelper.overlapsSpatially(obj.getLocation(), query.getSpatialRange())&&TextHelpers.evaluateTextualPredicate(obj.getObjectText(), query.getQueryText(),TextualPredicate.OVERlAPS))
					
				count++;
			}
		}
		System.out.println("Total query count="+count);
	}
	static HashMap<String , DataSourceInformation> getDataSourcesInformation(){

		DataSourceInformation tweetsDataSourceInformation  = new DataSourceInformation(new Rectangle(new Point(0.0,0.0), new Point(10000.0,10000.0)),"Tweets", DataSourceType.DATA_SOURCE, SpatioTextualConstants.volatilePersistenceState, SpatioTextualConstants.NOTCLEAN,true ,LocalIndexType.HYBRID_GRID,fineGridGran);
		DataSourceInformation tweetsWindowDataSourceInformation  = new DataSourceInformation(new Rectangle(new Point(0.0,0.0), new Point(10000.0,10000.0)),"TweetsW", DataSourceType.DATA_SOURCE, SpatioTextualConstants.volatilePersistenceState, SpatioTextualConstants.NOTCLEAN,true,LocalIndexType.HYBRID_GRID,fineGridGran );
		DataSourceInformation POIsDataSourceInformation  = new DataSourceInformation(new Rectangle(new Point(0.0,0.0), new Point(10000.0,10000.0)),"POIs", DataSourceType.DATA_SOURCE, SpatioTextualConstants.staticPersistenceState, SpatioTextualConstants.NOTCLEAN,true ,LocalIndexType.HYBRID_GRID,fineGridGran);
		DataSourceInformation movingObjectDataSourceInformation  = new DataSourceInformation(new Rectangle(new Point(0.0,0.0), new Point(10000.0,10000.0)),"MO", DataSourceType.DATA_SOURCE, SpatioTextualConstants.currentPersistenceState, SpatioTextualConstants.NOTCLEAN,true ,LocalIndexType.HYBRID_GRID,fineGridGran);
		
		
		HashMap<String , DataSourceInformation> dataSourcesInformation = new HashMap<String , DataSourceInformation>();
		
		dataSourcesInformation.put("Tweets", tweetsDataSourceInformation);
		dataSourcesInformation.put("TweetsW",tweetsWindowDataSourceInformation);
		dataSourcesInformation.put("POIs", POIsDataSourceInformation);
		dataSourcesInformation.put("MO", movingObjectDataSourceInformation);
		return dataSourcesInformation;
	}
	static void testVolatileRangeGrid(Integer fineGrid,Integer numberOfqueries, Double spatialRange, Integer numberOfKeywords,PrintWriter writer){
		System.out.println("*****************************************************************************************************************************************");
		System.out.println("***********************Testing the performance of volatile objects on a hybrid grid**********************************************");
		System.out.println("***********************Snapshot filter query fine grid:"+fineGrid+", spatial range:"+spatialRange+", number of keywords:"+numberOfKeywords+"*********************************************");
		System.out.println("Begin reading data objects");
		ArrayList<DataObject> tweets =readTweets("Tweets");
		System.out.println("Done reading data objects");
		System.out.println("begin reading queries");
		ArrayList<Query> queries =addQueries("/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv","querySrc",numberOfqueries,numberOfKeywords,
				"Tweets", null, null, QueryType.queryTextualRange,null,TextualPredicate.OVERlAPS,null,spatialRange) ;
		System.out.println("Done reading queries");
		HashMap<String , DataSourceInformation> dataSourcesInformation = getDataSourcesInformation(); 
		
		
		dataSourcesInformation.get("Tweets").localHybridIndex = new LocalHybridGridIndex(dataSourcesInformation.get("Tweets").getSelfBounds(), dataSourcesInformation.get("Tweets"), fineGrid, fineGrid, false, 0);
		SpatioTextualEvaluatorBolt localEvaluator = new SpatioTextualEvaluatorBolt("TornadoTest",LocalIndexType.HYBRID_GRID,GlobalIndexType.GRID,null,fineGridGran);
		localEvaluator.setSourcesInformations(dataSourcesInformation);
		HashMap<String, HashMap<Integer, Query>>queryInformationHashMap = new HashMap<String, HashMap<Integer, Query>>();
		queryInformationHashMap.put("querySrc", new HashMap<Integer, Query>());
		localEvaluator.queryInformationHashMap=queryInformationHashMap;
		localEvaluator.selfBounds=dataSourcesInformation.get("Tweets").selfBounds;
		Long queryRegisterationduration;
		Stopwatch stopwatch = Stopwatch.createStarted();
		for(Query  q: queries){
			
			localEvaluator.handleContinousQuery(q);
		}
		stopwatch.stop();
		queryRegisterationduration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("Query register Time per query (micros)= "+queryRegisterationduration/queries.size());
		Long dataProcessingDuration;
		stopwatch = Stopwatch.createStarted();
		for(DataObject obj:tweets){
			
			localEvaluator.handleVolatileDataObject(obj);
		}
		stopwatch.stop();
		dataProcessingDuration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("DataProcessing Time per object (micros)= "+dataProcessingDuration/tweets.size()+" with qulified tuples:"+localEvaluator.outputTuplesCount);

		writer.println(numberOfqueries+","+numberOfKeywords+","+spatialRange+","+fineGrid+","+queryRegisterationduration/queries.size()+","+dataProcessingDuration/tweets.size());
		
		tweets=null;
		queries=null;
		localEvaluator=null;
		dataSourcesInformation=null;
	}
	static void testVolatileRangeMultiGrid(Integer fineGrid,Integer numberOfqueries, Double spatialRange, Integer numberOfKeywords,PrintWriter writer){
		System.out.println("*****************************************************************************************************************************************");
		System.out.println("***********************Testing the performance of volatile objects on a hybrid Multilevel grid**********************************************");
		System.out.println("***********************Snapshot filter query fine grid:"+fineGrid+", spatial range:"+spatialRange+", number of keywords:"+numberOfKeywords+"*********************************************");
		System.out.println("Begin reading data objects");
		ArrayList<DataObject> tweets =readTweets("Tweets");
		System.out.println("Done reading data objects");
		System.out.println("begin reading queries");
		ArrayList<Query> queries =addQueries("/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv","querySrc",numberOfqueries,numberOfKeywords,
				"Tweets", null, null, QueryType.queryTextualRange,null,TextualPredicate.OVERlAPS,null,spatialRange) ;
		System.out.println("Done reading queries");
		HashMap<String , DataSourceInformation> dataSourcesInformation = getDataSourcesInformation(); 
		
		
		dataSourcesInformation.get("Tweets").localHybridIndex = null;//new LocalHybridMultiGridIndex(dataSourcesInformation.get("Tweets").getSelfBounds(), dataSourcesInformation.get("Tweets"), fineGrid, fineGrid, false);
		SpatioTextualEvaluatorBolt localEvaluator = new SpatioTextualEvaluatorBolt("TornadoTest",LocalIndexType.HYBRID_GRID,GlobalIndexType.GRID,null,fineGridGran);
		localEvaluator.setSourcesInformations(dataSourcesInformation);
		HashMap<String, HashMap<Integer, Query>>queryInformationHashMap = new HashMap<String, HashMap<Integer, Query>>();
		queryInformationHashMap.put("querySrc", new HashMap<Integer, Query>());
		localEvaluator.queryInformationHashMap=queryInformationHashMap;
		localEvaluator.selfBounds=dataSourcesInformation.get("Tweets").selfBounds;
		Long queryRegisterationduration;
		Stopwatch stopwatch = Stopwatch.createStarted();
		for(Query  q: queries){
			
			localEvaluator.handleContinousQuery(q);
		}
		stopwatch.stop();
		queryRegisterationduration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("Query register Time per query (micros)= "+queryRegisterationduration/queries.size());
		Long dataProcessingDuration;
		stopwatch = Stopwatch.createStarted();
		for(DataObject obj:tweets){
			
			localEvaluator.handleVolatileDataObject(obj);
		}
		stopwatch.stop();
		dataProcessingDuration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("DataProcessing Time per object (micros)= "+dataProcessingDuration/tweets.size()+" with qulified tuples:"+localEvaluator.outputTuplesCount);
		writer.println(numberOfqueries+","+numberOfKeywords+","+spatialRange+","+fineGrid+","+queryRegisterationduration/queries.size()+","+dataProcessingDuration/tweets.size());
		
		tweets=null;
		queries=null;
		localEvaluator=null;
		dataSourcesInformation=null;
	}
	/**
	static void testVolatileRangeQuad(Integer numberOfqueries, Double spatialRange, Integer numberOfKeywords,Integer splitThreshold, Integer maxLevel,PrintWriter writer ){
		System.out.println("*****************************************************************************************************************************************");
		System.out.println("***********************Testing the performance of volatile objects on a hybrid Quad tree**********************************************");
		System.out.println("***********************Snapshot filter query Max level:"+maxLevel+" split threshold"+splitThreshold+", spatial range:"+spatialRange+", number of keywords:"+numberOfKeywords+"*********************************************");
		System.out.println("Begin reading data objects");
		ArrayList<DataObject> tweets =readTweets("Tweets");
		System.out.println("Done reading data objects");
		System.out.println("begin reading queries");
		ArrayList<Query> queries =addQueries("/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv","querySrc",numberOfqueries,numberOfKeywords,
				"Tweets", null, null, QueryType.queryTextualRange,null,TextualPredicate.OVERlAPS,null,spatialRange) ;
		System.out.println("Done reading queries");
		HashMap<String , DataSourceInformation> dataSourcesInformation = getDataSourcesInformation(); 
		
		
		dataSourcesInformation.get("Tweets").localHybridIndex = new LocalQuadTree(dataSourcesInformation.get("Tweets").getSelfBounds(), dataSourcesInformation.get("Tweets"),false ,maxLevel,splitThreshold);
		SpatioTextualEvaluatorBolt localEvaluator = new SpatioTextualEvaluatorBolt("TornadoTest",LocalIndexType.HYBRID_GRID,GlobalIndexType.GRID,null,fineGridGran);
		localEvaluator.setSourcesInformations(dataSourcesInformation);
		HashMap<String, HashMap<Integer, Query>>queryInformationHashMap = new HashMap<String, HashMap<Integer, Query>>();
		queryInformationHashMap.put("querySrc", new HashMap<Integer, Query>());
		localEvaluator.queryInformationHashMap=queryInformationHashMap;
		localEvaluator.selfBounds=dataSourcesInformation.get("Tweets").selfBounds;
		Long queryRegisterationduration;
		Stopwatch stopwatch = Stopwatch.createStarted();
		for(Query  q: queries){
			
			localEvaluator.handleContinousQuery(q);
		}
		stopwatch.stop();
		queryRegisterationduration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("Query register Time per query (micros)= "+queryRegisterationduration/queries.size());
		Long dataProcessingDuration;
		stopwatch = Stopwatch.createStarted();
		for(DataObject obj:tweets){
			
			localEvaluator.handleVolatileDataObject(obj);
		}
		stopwatch.stop();
		dataProcessingDuration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("DataProcessing Time per object (micros)= "+dataProcessingDuration/tweets.size()+" with qulified tuples:"+localEvaluator.outputTuplesCount);
		writer.println(numberOfqueries+","+numberOfKeywords+","+spatialRange+","+splitThreshold+","+maxLevel+","+queryRegisterationduration/queries.size()+","+dataProcessingDuration/tweets.size());
		tweets=null;
		queries=null;
		localEvaluator=null;
		dataSourcesInformation=null;
	}*/
	static void testPersisentRangeGrid(Integer fineGrid,Integer numberOfqueries, Double spatialRange, Integer numberOfKeywords,PrintWriter writer){
		System.out.println("*****************************************************************************************************************************************");
		System.out.println("***********************Testing the performance of persistent objects on a hybrid grid**********************************************");
		System.out.println("***********************Snapshot filter query fine grid:"+fineGrid+", spatial range:"+spatialRange+", number of keywords:"+numberOfKeywords+"*********************************************");
		System.out.println("Begin reading data objects");
		ArrayList<DataObject> tweets =readTweets("Tweets");
		System.out.println("Done reading data objects");
		System.out.println("begin reading queries");
		ArrayList<Query> queries =addQueries("/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv","querySrc",numberOfqueries,numberOfKeywords,
				"Tweets", null, null, QueryType.queryTextualRange,null,TextualPredicate.OVERlAPS,null,spatialRange) ;
		System.out.println("Done reading queries");
		HashMap<String , DataSourceInformation> dataSourcesInformation = getDataSourcesInformation(); 
		
		
		dataSourcesInformation.get("Tweets").localHybridIndex = new LocalHybridGridIndex(dataSourcesInformation.get("Tweets").getSelfBounds(), dataSourcesInformation.get("Tweets"), fineGrid, fineGrid, false, 0);
		SpatioTextualEvaluatorBolt localEvaluator = new SpatioTextualEvaluatorBolt("TornadoTest",LocalIndexType.HYBRID_GRID,GlobalIndexType.GRID,null,fineGridGran);
		localEvaluator.setSourcesInformations(dataSourcesInformation);
		HashMap<String, HashMap<Integer, Query>>queryInformationHashMap = new HashMap<String, HashMap<Integer, Query>>();
		queryInformationHashMap.put("querySrc", new HashMap<Integer, Query>());
		localEvaluator.queryInformationHashMap=queryInformationHashMap;
		localEvaluator.selfBounds=dataSourcesInformation.get("Tweets").selfBounds;
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		Long dataProcessingDuration;
		
		for(DataObject obj:tweets){
			if(!SpatialHelper.overlapsSpatially(obj.getLocation(), localEvaluator.selfBounds)) continue;
			localEvaluator.handlePersisentDataObject(obj);
		}
		stopwatch.stop();
		dataProcessingDuration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("DataProcessing Time per object (micros)= "+dataProcessingDuration/tweets.size());
		
		
		Long queryRegisterationduration;
		stopwatch = Stopwatch.createStarted();
	
		for(Query  q: queries){
			
			localEvaluator.handleSnapShotQuery(q);
		}
		stopwatch.stop();
		queryRegisterationduration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("Query register Time per query (micros)= "+queryRegisterationduration/queries.size()+" with qulified tuples:"+localEvaluator.outputTuplesCount+" with visited dataobjects:"+localEvaluator.visitedDataObjectCount);
		
		writer.println(numberOfqueries+","+numberOfKeywords+","+spatialRange+","+fineGrid+","+queryRegisterationduration/queries.size()+","+dataProcessingDuration/tweets.size());
		
		
		tweets=null;
		queries=null;
		localEvaluator=null;
		dataSourcesInformation=null;
	}
	static void testPersisentRangeHybridMultiLevelIndex(Integer fineGrid,Integer numberOfqueries, Double spatialRange, Integer numberOfKeywords,PrintWriter writer){
		System.out.println("*****************************************************************************************************************************************");
		System.out.println("***********************Testing the performance of persistent objects on a hybrid multilevel index**********************************************");
		System.out.println("***********************Snapshot filter query fine grid:"+fineGrid+", spatial range:"+spatialRange+", number of keywords:"+numberOfKeywords+"*********************************************");
		System.out.println("Begin reading data objects");
		
		ArrayList<DataObject> tweets =readTweets("Tweets");
		System.out.println("Done reading data objects");
		System.out.println("begin reading queries");
		ArrayList<Query> queries =addQueries("/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv","querySrc",numberOfqueries,numberOfKeywords,
				"Tweets", null, null, QueryType.queryTextualRange,null,TextualPredicate.OVERlAPS,null,spatialRange) ;
		System.out.println("Done reading queries");
		HashMap<String , DataSourceInformation> dataSourcesInformation = getDataSourcesInformation(); 
		
		
		dataSourcesInformation.get("Tweets").localHybridIndex = null;//new LocalHybridMultiGridIndex(dataSourcesInformation.get("Tweets").getSelfBounds(), dataSourcesInformation.get("Tweets"), fineGrid, fineGrid, false);
		SpatioTextualEvaluatorBolt localEvaluator = new SpatioTextualEvaluatorBolt("TornadoTest",LocalIndexType.HYBRID_GRID,GlobalIndexType.GRID,null,fineGridGran);
		localEvaluator.setSourcesInformations(dataSourcesInformation);
		HashMap<String, HashMap<Integer, Query>>queryInformationHashMap = new HashMap<String, HashMap<Integer, Query>>();
		queryInformationHashMap.put("querySrc", new HashMap<Integer, Query>());
		localEvaluator.queryInformationHashMap=queryInformationHashMap;
		localEvaluator.selfBounds=dataSourcesInformation.get("Tweets").selfBounds;
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		Long dataProcessingDuration;
		
		for(DataObject obj:tweets){
			if(!SpatialHelper.overlapsSpatially(obj.getLocation(), localEvaluator.selfBounds)) continue;
			localEvaluator.handlePersisentDataObject(obj);
		}
		stopwatch.stop();
		dataProcessingDuration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("DataProcessing Time per object (micros)= "+dataProcessingDuration/tweets.size()+" with qulified tuples:"+localEvaluator.outputTuplesCount+" with visited dataobjects:"+localEvaluator.visitedDataObjectCount);
		
		
		Long queryRegisterationduration;
		stopwatch = Stopwatch.createStarted();
		
		for(Query  q: queries){
			
			localEvaluator.handleSnapShotQuery(q);
		}
		stopwatch.stop();
		queryRegisterationduration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("Query register Time per query (micros)= "+queryRegisterationduration/queries.size()+" with qulified tuples:"+localEvaluator.outputTuplesCount+" with visited dataobjects:"+localEvaluator.visitedDataObjectCount);
		writer.println(numberOfqueries+","+numberOfKeywords+","+spatialRange+","+fineGrid+","+queryRegisterationduration/queries.size()+","+dataProcessingDuration/tweets.size());
		
		
		
		tweets=null;
		queries=null;
		localEvaluator=null;
		dataSourcesInformation=null;
	}
	/*static void testPersisentRangeHybridQuad(Integer numberOfqueries, Double spatialRange, Integer numberOfKeywords,Integer splitThreshold, Integer maxLevel,PrintWriter writer){
		System.out.println("*****************************************************************************************************************************************");
		System.out.println("***********************Testing the performance of persistent objects on a hybrid Quad tree**********************************************");
		System.out.println("***********************Snapshot filter query Max level:"+maxLevel+" split threshold"+splitThreshold+", spatial range:"+spatialRange+", number of keywords:"+numberOfKeywords+"*********************************************");
		System.out.println("Begin reading data objects");
		
		ArrayList<DataObject> tweets =readTweets("Tweets");
		System.out.println("Done reading data objects");
		System.out.println("begin reading queries");
		ArrayList<Query> queries =addQueries("/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv","querySrc",numberOfqueries,numberOfKeywords,
				"Tweets", null, null, QueryType.queryTextualRange,null,TextualPredicate.OVERlAPS,null,spatialRange) ;
		System.out.println("Done reading queries");
		HashMap<String , DataSourceInformation> dataSourcesInformation = getDataSourcesInformation(); 
		
		
		dataSourcesInformation.get("Tweets").localHybridIndex = new LocalQuadTree(dataSourcesInformation.get("Tweets").getSelfBounds(), dataSourcesInformation.get("Tweets"),false ,maxLevel,splitThreshold);
		SpatioTextualEvaluatorBolt localEvaluator = new SpatioTextualEvaluatorBolt("TornadoTest",LocalIndexType.HYBRID_GRID,GlobalIndexType.GRID,null,64);
		localEvaluator.setSourcesInformations(dataSourcesInformation);
		HashMap<String, HashMap<Integer, Query>>queryInformationHashMap = new HashMap<String, HashMap<Integer, Query>>();
		queryInformationHashMap.put("querySrc", new HashMap<Integer, Query>());
		localEvaluator.queryInformationHashMap=queryInformationHashMap;
		localEvaluator.selfBounds=dataSourcesInformation.get("Tweets").selfBounds;
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		Long dataProcessingDuration;
		
		for(DataObject obj:tweets){
			if(!SpatialHelper.overlapsSpatially(obj.getLocation(), localEvaluator.selfBounds)) continue;
			localEvaluator.handlePersisentDataObject(obj);
		}
		stopwatch.stop();
		dataProcessingDuration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("DataProcessing Time per object (micros)= "+dataProcessingDuration/tweets.size()+" with qulified tuples:"+localEvaluator.outputTuplesCount+" with visited dataobjects:"+localEvaluator.visitedDataObjectCount);
		
		
		Long queryRegisterationduration;
		stopwatch = Stopwatch.createStarted();
		
		for(Query  q: queries){
			
			localEvaluator.handleSnapShotQuery(q);
		}
		stopwatch.stop();
		queryRegisterationduration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("Query register Time per query (micros)= "+queryRegisterationduration/queries.size()+" with qulified tuples:"+localEvaluator.outputTuplesCount+" with visited dataobjects:"+localEvaluator.visitedDataObjectCount);
		
		writer.println(numberOfqueries+","+numberOfKeywords+","+spatialRange+","+splitThreshold+","+maxLevel+","+queryRegisterationduration/queries.size()+","+dataProcessingDuration/tweets.size());
		
		tweets=null;
		queries=null;
		localEvaluator=null;
		dataSourcesInformation=null;
	}
	*/
	static void testVolatileJoinGrid(){
		ArrayList<DataObject> tweets =readTweets("Tweets");
		ArrayList<Query> queries =addQueries("/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv","querySrc",10000,5,
				"Tweets", "POIs",20.0 , QueryType.queryTextualSpatialJoin,null,TextualPredicate.OVERlAPS,TextualPredicate.OVERlAPS,50.0) ;
		//this will be tested on tweets and tweets queries  , range, join
	}
	static void testCurrentObjectGrid(){
		ArrayList<DataObject> MO =readMovingObject(1500000,5);
		//this will be tested on this is test on Moving objects and hybrid queries  range, topk "/media/D/googleDrive/walid research/datasets/querykeywordssorted/HybridQueries.csv"
	}
	static void testWindowGrid(){
		ArrayList<DataObject> tweets =readTweets("Tweets");
		//this will be tested on this is test on tweets and hybrid queries  range, topk 
	}
	static ArrayList<Query> addQueries(String queriesFilePath,String querySrc,Integer numberOfQueries, Integer keywordCountVal, String dataSrc1, String dataSrc2, Double distance, QueryType queryType,Integer k,TextualPredicate textualPredicate1,TextualPredicate textualPredicate2,Double spatialRange) {
		
		ArrayList<Query> queries = new ArrayList<Query>();
		String filePath = queriesFilePath;
		FileInputStream fstream;
		BufferedReader br ;
		String line="";
		Integer i=0;
		try {
			fstream = new FileInputStream(filePath);
			br = new BufferedReader(new InputStreamReader(fstream));
			while ((line = br.readLine()) != null&&i<numberOfQueries) {
				if(line==null||"".equals(line)) continue;
				Query q = buildQuery(line,querySrc, keywordCountVal, dataSrc1, dataSrc2, distance, queryType, k, textualPredicate1, textualPredicate2, spatialRange);
				queries.add(q);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return queries;
	}
	static Query buildQuery(String line,String querySrc,Integer keywordCountVal, String dataSrc1, String dataSrc2, Double distance, QueryType queryType,Integer k,TextualPredicate textualPredicate1,TextualPredicate textualPredicate2,Double spatialRange) {
		StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
		
		Integer id = stringTokenizer.hasMoreTokens() ? Integer.parseInt(stringTokenizer.nextToken()) : 0;
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
		ArrayList<String> queryText1 =new ArrayList<String>();
		ArrayList<String> queryText2 =new ArrayList<String>();
		
		for(int i =0;i<keywordCountVal;i++){
			queryText1.add(keywordsArr[i]);
			queryText2.add(keywordsArr[keywordsArr.length-i-1]);
		}
		
				
		Date date = new Date();
		
		
		Query q = new Query();
		q.setSrcId(querySrc);
		q.setQueryId(id);
		q.setCommand(Command.addCommand);
		q.setDataSrc(dataSrc1);
		q.setQueryType(queryType);
		q.setTimeStamp(date.getTime());
		if(queryType.equals(QueryType.queryTextualRange)){
			q.setSpatialRange(new Rectangle(new Point(xCoord, yCoord),new Point(xCoord+spatialRange,yCoord+spatialRange)) );
			q.setTextualPredicate(textualPredicate1);
			q.setQueryText(queryText1);
		}
		else if(queryType.equals(QueryType.queryTextualKNN)){
			((KNNQuery)q).setFocalPoint(new Point(xCoord, yCoord));
			((KNNQuery)q).setTextualPredicate(textualPredicate1);
			((KNNQuery)q).setQueryText(queryText1);
			((KNNQuery)q).setK(k);
		}
		else if(queryType.equals(QueryType.queryTextualSpatialJoin)){
			((JoinQuery)q).setSpatialRange(new Rectangle(new Point(xCoord, yCoord),new Point(xCoord+spatialRange,yCoord+spatialRange)) );
			((JoinQuery)q).setTextualPredicate(textualPredicate1);
			((JoinQuery)q).setTextualPredicate2(textualPredicate2);
			((JoinQuery)q).setQueryText(queryText1);
			((JoinQuery)q).setQueryText(queryText2);
			((JoinQuery)q).setDistance(distance);
		}	
		return q;
	}
	
	static ArrayList<DataObject>  readMovingObject(Integer numberOfUpdates,Integer objectKeywordCount){
		String filePath = "/media/E/work/purdue/database/research/datasets/trajecotry/brinkhoffvariabledurationdataset/brinkhoffdataset.txt";
		String keywordsPath = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/HybridQueries.csv";
		
		ArrayList<DataObject> dataObjects = new ArrayList<DataObject>();
		FileInputStream fstream;
		BufferedReader br ;
		String line="";
		Integer i=0;
		ArrayList<String > keywords = new ArrayList<String>();
		try {
			fstream = new FileInputStream(keywordsPath);
			br = new BufferedReader(new InputStreamReader(fstream));
			while((line = br.readLine()) != null&&i<numberOfUpdates) {
				StringTokenizer stringTokenizer = new StringTokenizer(line, ",");

				String id = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";

				String x = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
				String y = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
				String text = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
				if(text.equals(""))
					continue;
				keywords.add(text);
				i++;
			}
		}catch(Exception e ){
			e.printStackTrace();
		}
		System.out.println("Done reading words ");
		line="";
		i=0;
		try {
			fstream = new FileInputStream(filePath);
			br = new BufferedReader(new InputStreamReader(fstream));
			int count=0;
			i=0;
			while((line = br.readLine()) != null&&i<numberOfUpdates) {
				StringTokenizer stringTokenizer = new StringTokenizer(line, " ");

				Integer id = i;

				String dateString = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
				//dateString = stringTokenizer.hasMoreTokens()?stringTokenizer.nextToken():"";

				Double x = 0.0;
				Double y = 0.0;

				try {
					x = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;

					y = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				Point xy = new Point(x, y);
				String text = "";
				String textContent="";
				int index ;
				index = i%keywords.size();
				text= keywords.get(index);
				String[] keyword=text.split(" ");
				for(int j=0;j<objectKeywordCount;j++){
					textContent=textContent+keyword[keyword.length-1-j]+" ";
				}
				DataObject obj = new DataObject(id, xy, textContent,( new Date()).getTime(), Command.addCommand);
				dataObjects.add(obj);
				
				i++;
			}
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		System.out.println("Done building moving objects ");
		return dataObjects;

	}
	static ArrayList<DataObject>  readTweets(String srcId){
		String filePath = tweetsFilePath;//"/home/ahmed/Downloads/sample_usa_tweets.csv";
		ArrayList<DataObject> dataObjects = new ArrayList<DataObject>();
		ArrayList<Partition> partitions = new ArrayList<Partition>();
		FileInputStream fstream;
		BufferedReader br ;
		String tweet="";
		Integer i=0;
		try {
			fstream = new FileInputStream(filePath);
			br = new BufferedReader(new InputStreamReader(fstream));
			int count=0;
			while((tweet = br.readLine()) != null&&i<maxTweetSize) {
				i++;
				StringTokenizer stringTokenizer = new StringTokenizer(tweet, ",");

				Integer id =i;// stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";

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
				//Point xy = SpatialHelper.convertFromLatLonToXYPoint(new LatLong(lat, lon),SpatioTextualConstants.usaMinLat,SpatioTextualConstants.usaMinLong,SpatioTextualConstants.usaMaxLat,SpatioTextualConstants.usaMaxLong);
				Point xy = SpatialHelper.convertFromLatLonToXYPoint(new LatLong(lat, lon));//,SpatioTextualConstants.usaMinLat,SpatioTextualConstants.usaMinLong,SpatioTextualConstants.usaMaxLat,SpatioTextualConstants.usaMaxLong);
				String textContent = "";
				String dummy = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
				while (stringTokenizer.hasMoreTokens())
					textContent = textContent + stringTokenizer.nextToken() + " ";
				
				DataObject obj = new DataObject(id, xy, textContent,( new Date()).getTime(), Command.addCommand);
				obj.setSrcId(srcId);
				dataObjects.add(obj);
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return dataObjects;
		
	}

	static TweetsFSSpout  readTweetsHDFS(String srcId){
		Map<String, Object> tweetsSpoutConf = new HashMap<String, Object>();
		tweetsSpoutConf.put(FileSpout.FILE_PATH, "hdfs://172.18.11.143:8020/twitterdataset/tweet_us_2015_1_3.csv");
		tweetsSpoutConf.put(FileSpout.CORE_FILE_PATH,"/home/staticdata/core-site.xml");
		tweetsSpoutConf.put(FileSpout.FILE_SYS_TYPE, FileSpout.HDFS);
		tweetsSpoutConf.put(FileSpout.EMIT_SLEEP_DURATION_NANOSEC, 0);
		TweetsFSSpout tweetsFSSpout= new TweetsFSSpout(tweetsSpoutConf,10,100);
		tweetsFSSpout.open(null, null, null);
		return tweetsFSSpout;
	}
}
