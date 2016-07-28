package edu.purdue.cs.tornado.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import edu.purdue.cs.tornado.evaluator.DynamicEvalautorBolt;
import edu.purdue.cs.tornado.evaluator.SpatioTextualEvaluatorBolt;
import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.DataSourceType;
import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.ObjectSizeCalculator;
import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.QueryType;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.index.global.DynamicGlobalOptimizedIndex;
import edu.purdue.cs.tornado.index.global.GlobalGridIndex;
import edu.purdue.cs.tornado.index.global.GlobalIndex;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.global.GlobalOptimizedPartitionedIndex;
import edu.purdue.cs.tornado.index.global.GlobalOptimizedPartitionedIndexLowerSpace;
import edu.purdue.cs.tornado.index.global.GlobalOptimizedPartitionedTextAwareIndex;
import edu.purdue.cs.tornado.index.global.GlobalPartitionedGridBasedIndex;
import edu.purdue.cs.tornado.index.global.GlobalStaticPartitionedIndex;
import edu.purdue.cs.tornado.index.local.LocalHybridGridIndex;
import edu.purdue.cs.tornado.index.local.LocalHybridIndex;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.index.local.NoLocalIndex;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.JoinQuery;
import edu.purdue.cs.tornado.messages.KNNQuery;
import edu.purdue.cs.tornado.messages.Query;
import edu.purdue.cs.tornado.spouts.QueriesFileSystemSpout;

public class TestPartitionsPerformance {
	static Integer countId = 0;
	static Integer queryCountId = 0;
	static Integer finegGridGran = 64;

	public static void main(String[] args) throws Exception {

		//testGlocalIndexPerformance();
		//testPartitions();
		testLocalIndexPerformance();

	}

	static void testGlocalIndexPerformance() throws Exception {
		String tweetsFile = "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "/media/D/datasets/tweetsForQueries.csv";

		finegGridGran = 1024;

		Integer numberOfPartitions = 64;

		String partitionsPath = "resources/partitions" + numberOfPartitions + "_" + finegGridGran + "_prio.ser";
		ArrayList<Cell> partitions = PartitionsHelper.readSerializedPartitions(partitionsPath);
		ArrayList<Integer> evaluators = new ArrayList<Integer>();
		for (int i = 0; i < numberOfPartitions; i++) {
			evaluators.add(i);
		}
		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, 1);
		System.out.println("Done reading data");
		//ArrayList<Query> queries = readQueriesFromTweetsLocations(queriesFile, 1000000,dataObjects);
		//ArrayList<Query> queries = readQueries(queriesFile, 100000,TextualPredicate.OVERlAPS);
		ArrayList<Query> queries = readQueries(queriesFile, 1000000, TextualPredicate.CONTAINS,5,3);
		System.out.println("Done reading queries");

		//		
		//		System.out.println("Partitioned text aware");
		//		GlobalOptimizedPartitionedTextAwareIndex partitionedIndex1= new GlobalOptimizedPartitionedTextAwareIndex(numberOfPartitions, evaluators, partitions,finegGridGran);
		//		testGlobal(dataObjects, queries, partitionedIndex1, GlobalIndexType.PARTITIONED_TEXT_AWARE, LocalIndexType.HYBRID_GRID, "", numberOfPartitions);
		//		partitionedIndex1=null;
		//		System.gc();
		//		System.gc();
		for (int i = 0; i < 4; i++) {
			System.out.println("********************************************************");
			
			System.out.println("Partitioned  traditional grid");
			GlobalPartitionedGridBasedIndex partitionedIndex4 = new GlobalPartitionedGridBasedIndex(numberOfPartitions, evaluators, partitions, finegGridGran);
			testGlobal(dataObjects, queries, partitionedIndex4, GlobalIndexType.PARTITIONED, LocalIndexType.HYBRID_GRID, "", numberOfPartitions);
			partitionedIndex4 = null;
			System.gc();
			System.gc();
			System.out.println("------------");
			System.out.println("Partitioned augmented grid smaller size");
			GlobalOptimizedPartitionedIndexLowerSpace partitionedIndex0 = new GlobalOptimizedPartitionedIndexLowerSpace(numberOfPartitions, evaluators, partitions, finegGridGran);
			testGlobal(dataObjects, queries, partitionedIndex0, GlobalIndexType.PARTITIONED, LocalIndexType.HYBRID_GRID, "", numberOfPartitions);
			partitionedIndex0 = null;
			System.gc();
			System.gc();
//			System.out.println("------------");
//			System.out.println("Partitioned augmented grid text aware ");
//			GlobalOptimizedPartitionedIndex partitionedIndex1 = new GlobalOptimizedPartitionedTextAwareIndex(numberOfPartitions, evaluators, partitions, finegGridGran);
//			testGlobal(dataObjects, queries, partitionedIndex1, GlobalIndexType.PARTITIONED_TEXT_AWARE, LocalIndexType.HYBRID_GRID, "", numberOfPartitions);
//			partitionedIndex1 = null;
//			System.gc();
//			System.gc();
//			System.out.println("------------");
//			System.out.println("Partitioned augmented grid");
//			GlobalOptimizedPartitionedIndex partitionedIndex2 = new GlobalOptimizedPartitionedIndex(numberOfPartitions, evaluators, partitions, finegGridGran);
//			testGlobal(dataObjects, queries, partitionedIndex2, GlobalIndexType.PARTITIONED, LocalIndexType.HYBRID_GRID, "", numberOfPartitions);
//			partitionedIndex2 = null;
//			System.gc();
//			System.gc();
//			System.out.println("------------");
//			System.out.println("Partitioned  Rtree");
//			GlobalStaticPartitionedIndex partitionedIndex3 = new GlobalStaticPartitionedIndex(numberOfPartitions, evaluators, partitions, finegGridGran);
//			testGlobal(dataObjects, queries, partitionedIndex3, GlobalIndexType.PARTITIONED, LocalIndexType.HYBRID_GRID, "", numberOfPartitions);
//			partitionedIndex3 = null;
//			System.gc();
//			System.gc();
			

		}

	}

	static void testLocalIndexPerformance() throws Exception {
		String tweetsFile = "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "/media/D/datasets/tweetsForQueries.csv";

		finegGridGran = 1024;

	
		Integer numberOfDataObjects = 10000;
		Integer numberOfQueries = 1500000;

		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, numberOfDataObjects);
		System.out.println("Done reading data");
		//ArrayList<Query> queries = readQueriesFromTweetsLocations(queriesFile, 1000000,dataObjects);
		//ArrayList<Query> queries = readQueries(queriesFile, 100000,TextualPredicate.OVERlAPS);
		ArrayList<Query> queries = readQueries(queriesFile, numberOfQueries, TextualPredicate.CONTAINS, 5,3);
		System.out.println("Done reading queries");
		long queriesSize = ObjectSizeCalculator.getObjectSize(queries);
		System.out.println("Queries size =" + queriesSize/1024/1024+" MB");
		System.gc();
		System.gc();
		//		
		//		System.out.println("Partitioned text aware");
		//		GlobalOptimizedPartitionedTextAwareIndex partitionedIndex1= new GlobalOptimizedPartitionedTextAwareIndex(numberOfPartitions, evaluators, partitions,finegGridGran);
		//		testGlobal(dataObjects, queries, partitionedIndex1, GlobalIndexType.PARTITIONED_TEXT_AWARE, LocalIndexType.HYBRID_GRID, "", numberOfPartitions);
		//		partitionedIndex1=null;
		//		System.gc();
		//		System.gc();
		//for (int i = 0; i < 4; i++) {
		System.out.println("********************************************************");
		System.out.println("Hybrid grid");
		LocalHybridGridIndex localHybridIndex = new LocalHybridGridIndex(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null, finegGridGran);
		testLocal(dataObjects, queries, localHybridIndex, LocalIndexType.HYBRID_GRID);
		System.gc();
		System.gc();

		//}

	}

	static void testPartitions() throws Exception {
		String tweetsFile = "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "/media/D/datasets/tweetsForQueries.csv";
		String partitionsPath = "resources/partitionsDataAndQueries64_64.ser";
		Integer numberOfPartitions = 64;
		ArrayList<Cell> partitions = PartitionsHelper.readSerializedPartitions(partitionsPath);
		ArrayList<Integer> evaluators = new ArrayList<Integer>();
		for (int i = 0; i < numberOfPartitions; i++) {
			evaluators.add(i);
		}
		GlobalOptimizedPartitionedIndex partitionedIndex = new GlobalOptimizedPartitionedIndex(numberOfPartitions, evaluators, partitions, finegGridGran);
		GlobalGridIndex gridIndex = new GlobalGridIndex(numberOfPartitions, evaluators);
		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, 100000);
		//ArrayList<Query> queries = readQueriesFromTweetsLocations(queriesFile, 1000000,dataObjects);
		ArrayList<Query> queries = readQueries(queriesFile, 100000, TextualPredicate.OVERlAPS);
		//	testBruteForce(dataObjects, queries, null);
		///	processData(tweetsFile,queriesFile,outputFile,1000000,gridIndex,partitionedIndex,numberOfPartitions);
		//		System.out.println("Global grid");
		//		testGlobalLocal(dataObjects, queries, gridIndex, GlobalIndexType.GRID, LocalIndexType.HYBRID_GRID, "", numberOfPartitions);
		//		System.out.println("Global KD");
		//		testGlobalLocal(dataObjects, queries, partitionedIndex, GlobalIndexType.PARTITIONED, LocalIndexType.HYBRID_GRID, "", numberOfPartitions);
		//		partitions = PartitionsHelper.readSerializedPartitions(partitionsPath2);
		//		partitionedIndex = new GlobalOptimizedPartitionedIndex(numberOfPartitions, evaluators, partitions,finegGridGran);
		//		System.out.println("Global queries KD");
		//		testGlobalLocal(dataObjects, queries, partitionedIndex, GlobalIndexType.PARTITIONED, LocalIndexType.HYBRID_GRID, "", numberOfPartitions);
		//		System.out.println("Global Text aware queries KD");
		//		
		//		
		//		GlobalOptimizedPartitionedTextAwareIndex partitionedIndex2 = new GlobalOptimizedPartitionedTextAwareIndex(numberOfPartitions, evaluators, partitions,finegGridGran);
		//		testGlobalLocal(dataObjects, queries, partitionedIndex2, GlobalIndexType.PARTITIONED_TEXT_AWARE, LocalIndexType.HYBRID_GRID, "", numberOfPartitions);
		System.out.println("Dynamic Optimized");
		evaluators.add(numberOfPartitions);
		DynamicGlobalOptimizedIndex dyamicindex = new DynamicGlobalOptimizedIndex(numberOfPartitions, evaluators, partitions, finegGridGran);
		testDynamicGlobalLocal(dataObjects, queries, dyamicindex, GlobalIndexType.DYNAMIC_OPTIMIZED, LocalIndexType.HYBRID_GRID, "", numberOfPartitions);

	}

	static void testBruteForce(ArrayList<DataObject> dataObjects, ArrayList<Query> queries, String resultsFile) {
		System.out.println("*****************************************************************************************************************************************");
		System.out.println("Test bruteforce");

		ArrayList<DataObject> tweets = dataObjects;

		HashMap<String, DataSourceInformation> dataSourcesInformation = getDataSourcesInformation();

		dataSourcesInformation.get("Tweets").localHybridIndex = new NoLocalIndex(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), dataSourcesInformation.get("Tweets"));
		SpatioTextualEvaluatorBolt localEvaluator = new SpatioTextualEvaluatorBolt("TornadoTest", LocalIndexType.HYBRID_GRID, GlobalIndexType.GRID, null, finegGridGran);
		localEvaluator.setSourcesInformations(dataSourcesInformation);
		HashMap<String, HashMap<Integer, Query>> queryInformationHashMap = new HashMap<String, HashMap<Integer, Query>>();
		queryInformationHashMap.put("querySrc", new HashMap<Integer, Query>());
		localEvaluator.queryInformationHashMap = queryInformationHashMap;
		localEvaluator.selfBounds = dataSourcesInformation.get("Tweets").selfBounds;

		Stopwatch stopwatch = Stopwatch.createStarted();
		Long dataProcessingDuration;

		Long queryRegisterationduration;
		for (Query q : queries) {

			localEvaluator.handleContinousQuery(q);
		}

		stopwatch.stop();
		queryRegisterationduration = stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println(
				"Query register Time per query (nano)= " + queryRegisterationduration / queries.size() + " with qulified tuples:" + localEvaluator.outputTuplesCount + " with visited dataobjects:" + localEvaluator.visitedDataObjectCount);

		stopwatch = Stopwatch.createStarted();

		for (DataObject obj : tweets) {
			if (SpatialHelper.overlapsSpatially(obj.getLocation(), localEvaluator.selfBounds))
				localEvaluator.handleVolatileDataObject(obj);
			else
				System.err.println("Error in passed data objects");
		}

		stopwatch.stop();
		dataProcessingDuration = stopwatch.elapsed(TimeUnit.NANOSECONDS);
		//writer.println(numberOfqueries + "," + numberOfKeywords + "," + spatialRange + "," + fineGrid + "," + queryRegisterationduration / queries.size() + "," + dataProcessingDuration / tweets.size());
		System.out.println(" Brute force DataProcessing Time per object (nanos)= " + dataProcessingDuration / tweets.size() + " with qulified tuples:" + localEvaluator.outputTuplesCount + " with visited dataobjects:"
				+ localEvaluator.visitedDataObjectCount);
		tweets = null;
		queries = null;
		localEvaluator = null;
		dataSourcesInformation = null;
	}

	static void testGlobalLocal(ArrayList<DataObject> dataObjects, ArrayList<Query> queries, GlobalIndex globalIndex, GlobalIndexType globalIndexType, LocalIndexType localIndexType, String resultsFile, Integer numberOfEvaluators)
			throws Exception {
		Integer[] dataPoints = new Integer[numberOfEvaluators];
		Integer[] queriesCount = new Integer[numberOfEvaluators];

		String line = "";
		for (int i = 0; i < numberOfEvaluators; i++) {
			dataPoints[i] = 0;
			queriesCount[i] = 0;

		}

		ArrayList<SpatioTextualEvaluatorBolt> evaluators = new ArrayList<SpatioTextualEvaluatorBolt>();
		for (int i = 0; i < numberOfEvaluators; i++) {
			Rectangle bounds = globalIndex.getBoundsForTaskId(i);
			HashMap<String, DataSourceInformation> dataSourcesInformation = getDataSourcesInformation(bounds);

			SpatioTextualEvaluatorBolt localEvaluator = new SpatioTextualEvaluatorBolt("TornadoTest", localIndexType, globalIndexType, null, finegGridGran);
			localEvaluator.setSourcesInformations(dataSourcesInformation);
			HashMap<String, HashMap<Integer, Query>> queryInformationHashMap = new HashMap<String, HashMap<Integer, Query>>();
			queryInformationHashMap.put("querySrc", new HashMap<Integer, Query>());
			localEvaluator.queryInformationHashMap = queryInformationHashMap;
			localEvaluator.selfBounds = dataSourcesInformation.get("Tweets").selfBounds;
			evaluators.add(localEvaluator);
		}

		Stopwatch stopwatch = Stopwatch.createStarted();
		Long dataProcessingDuration;

		Long queryRegisterationduration;
		for (Query q : queries) {
			ArrayList<Integer> tasks = globalIndex.getTaskIDsOverlappingRecangle(q.getSpatialRange());
			if (GlobalIndexType.PARTITIONED_TEXT_AWARE.equals(globalIndexType))
				globalIndex.addTextToTaskID(tasks, q.getQueryText(), q.getTextualPredicate() == TextualPredicate.OVERlAPS, false);
			for (Integer taskId : tasks) {
				queriesCount[taskId]++;
				evaluators.get(taskId).handleContinousQuery(q);
			}
		}

		stopwatch.stop();
		queryRegisterationduration = stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("Query register Time per query (nanos)= " + queryRegisterationduration / queries.size());

		stopwatch = Stopwatch.createStarted();

		Integer sendTuples = 0;
		for (DataObject obj : dataObjects) {
			Integer taskId = globalIndex.getTaskIDsContainingPoint(obj.getLocation());
			Boolean send = false;
			if (GlobalIndexType.PARTITIONED_TEXT_AWARE.equals(globalIndexType))
				send = globalIndex.verifyTextOverlap(taskId, obj.getObjectText());

			if (SpatialHelper.overlapsSpatially(obj.getLocation(), evaluators.get(taskId).selfBounds)) {
				dataPoints[taskId]++;
				if (GlobalIndexType.PARTITIONED_TEXT_AWARE.equals(globalIndexType)) {
					if (send) {
						sendTuples++;
						evaluators.get(taskId).handleVolatileDataObject(obj);
					}
				} else {
					sendTuples++;
					evaluators.get(taskId).handleVolatileDataObject(obj);
				}
			} else
				System.err.println("Error in passed data objects");
		}

		stopwatch.stop();
		dataProcessingDuration = stopwatch.elapsed(TimeUnit.NANOSECONDS);
		Integer maxData = 0, maxDataIndex = 0, maxQuery = 0, maxQueryIndex = 0, maxEmitted = 0, maxEmittedIndex = 0;
		Integer totalEmiitedCount = 0;
		for (int i = 0; i < numberOfEvaluators; i++) {
			totalEmiitedCount += evaluators.get(i).outputTuplesCount;
			if (evaluators.get(i).outputTuplesCount > maxEmitted) {
				maxEmitted = evaluators.get(i).outputTuplesCount;
				maxEmittedIndex = i;
			}

		}
		System.out.println(" Global:" + globalIndexType.name() + " Local:" + localIndexType.name() + " DataProcessing Time per object (nanos)= " + dataProcessingDuration / dataObjects.size() + " with qulified tuples:" + totalEmiitedCount
				+ " max emitted:" + maxEmitted + " max emitted index:" + maxEmittedIndex + "Send tuples:" + sendTuples);

		for (int i = 0; i < numberOfEvaluators; i++) {
			if (dataPoints[i] > maxData) {
				maxData = dataPoints[i];
				maxDataIndex = i;
			}
			if (queriesCount[i] > maxQuery) {
				maxQuery = queriesCount[i];
				maxQueryIndex = i;
			}
		}
		System.out.println("Max data =" + maxData + ",Max data index=" + maxDataIndex + ",Max query =" + maxQuery + ",Max query index=" + maxQueryIndex);

		dataObjects = null;
		queries = null;
		evaluators = null;

	}

	static void testDynamicGlobalLocal(ArrayList<DataObject> dataObjects, ArrayList<Query> queries, GlobalIndex globalIndex, GlobalIndexType globalIndexType, LocalIndexType localIndexType, String resultsFile, Integer numberOfEvaluators)
			throws Exception {
		Integer[] dataPoints = new Integer[numberOfEvaluators];
		Integer[] queriesCount = new Integer[numberOfEvaluators];

		String line = "";
		for (int i = 0; i < numberOfEvaluators; i++) {
			dataPoints[i] = 0;
			queriesCount[i] = 0;

		}

		ArrayList<DynamicEvalautorBolt> evaluators = new ArrayList<DynamicEvalautorBolt>();
		for (int i = 0; i < numberOfEvaluators; i++) {
			Rectangle bounds = globalIndex.getBoundsForTaskId(i);
			HashMap<String, DataSourceInformation> dataSourcesInformation = getDataSourcesInformation(bounds);

			DynamicEvalautorBolt localEvaluator = new DynamicEvalautorBolt("TornadoTest", localIndexType, globalIndexType, null, finegGridGran);
			localEvaluator.setSourcesInformations(dataSourcesInformation);
			HashMap<String, HashMap<Integer, Query>> queryInformationHashMap = new HashMap<String, HashMap<Integer, Query>>();
			queryInformationHashMap.put("querySrc", new HashMap<Integer, Query>());
			localEvaluator.queryInformationHashMap = queryInformationHashMap;
			localEvaluator.selfBounds = dataSourcesInformation.get("Tweets").selfBounds;
			evaluators.add(localEvaluator);
		}

		Stopwatch stopwatch = Stopwatch.createStarted();
		Long dataProcessingDuration;

		Long queryRegisterationduration;
		for (Query q : queries) {
			ArrayList<Integer> tasks = globalIndex.getTaskIDsOverlappingRecangle(q.getSpatialRange());
			if (GlobalIndexType.PARTITIONED_TEXT_AWARE.equals(globalIndexType))
				globalIndex.addTextToTaskID(tasks, q.getQueryText(), q.getTextualPredicate() == TextualPredicate.OVERlAPS, false);
			for (Integer taskId : tasks) {
				queriesCount[taskId]++;
				evaluators.get(taskId).handleContinousQuery(q);
			}
		}

		stopwatch.stop();
		queryRegisterationduration = stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("Query register Time per query (nanos)= " + queryRegisterationduration / queries.size());

		stopwatch = Stopwatch.createStarted();

		Integer sendTuples = 0;
		for (DataObject obj : dataObjects) {
			Integer taskId = globalIndex.getTaskIDsContainingPoint(obj.getLocation());
			Boolean send = false;
			if (globalIndex.isTextAware())
				send = globalIndex.verifyTextOverlap(taskId, obj.getObjectText());

			if (SpatialHelper.overlapsSpatially(obj.getLocation(), evaluators.get(taskId).selfBounds)) {
				dataPoints[taskId]++;
				if (globalIndex.isTextAware()) {
					if (send) {
						sendTuples++;
						evaluators.get(taskId).handleVolatileDataObject(obj);
					}
				} else {
					sendTuples++;
					evaluators.get(taskId).handleVolatileDataObject(obj);
				}
			} else
				System.err.println("Error in passed data objects");
		}

		stopwatch.stop();
		dataProcessingDuration = stopwatch.elapsed(TimeUnit.NANOSECONDS);
		Integer maxData = 0, maxDataIndex = 0, maxQuery = 0, maxQueryIndex = 0, maxEmitted = 0, maxEmittedIndex = 0;
		Integer totalEmiitedCount = 0;
		for (int i = 0; i < numberOfEvaluators; i++) {
			totalEmiitedCount += evaluators.get(i).outputTuplesCount;
			if (evaluators.get(i).outputTuplesCount > maxEmitted) {
				maxEmitted = evaluators.get(i).outputTuplesCount;
				maxEmittedIndex = i;
			}

		}
		System.out.println(" Global:" + globalIndexType.name() + " Local:" + localIndexType.name() + " DataProcessing Time per object (nanos)= " + dataProcessingDuration / dataObjects.size() + " with qulified tuples:" + totalEmiitedCount
				+ " max emitted:" + maxEmitted + " max emitted index:" + maxEmittedIndex + "Send tuples:" + sendTuples);

		for (int i = 0; i < numberOfEvaluators; i++) {
			if (dataPoints[i] > maxData) {
				maxData = dataPoints[i];
				maxDataIndex = i;
			}
			if (queriesCount[i] > maxQuery) {
				maxQuery = queriesCount[i];
				maxQueryIndex = i;
			}
		}
		System.out.println("Max data =" + maxData + ",Max data index=" + maxDataIndex + ",Max query =" + maxQuery + ",Max query index=" + maxQueryIndex);

		dataObjects = null;
		queries = null;
		evaluators = null;

	}

	static void testLocal(ArrayList<DataObject> dataObjects, ArrayList<Query> queries, LocalHybridIndex localIndex, LocalIndexType localIndexType) throws Exception {

		Stopwatch stopwatch = Stopwatch.createStarted();
		Long dataProcessingDuration;

		Long queryRegisterationduration;
		int queryTasks = 0;
		//	System.out.println();
		for (Query q : queries) {
			localIndex.addContinousQuery(q);
		}

		stopwatch.stop();
		
		
		queryRegisterationduration = stopwatch.elapsed(TimeUnit.NANOSECONDS);
		
		
		long indexMemorySize = ObjectSizeCalculator.getObjectSize(localIndex);
		System.out.println("Local index size =" + indexMemorySize/1024/1024+" MB");

		System.out.println("Query register Time per query (nanos)= " + queryRegisterationduration / queries.size());
		System.out.println("Total query evalautors= " + queryTasks);

		stopwatch = Stopwatch.createStarted();
		Integer sendTuples = 0;
		for (int i = 0; i < 100; i++) {
			for (DataObject obj : dataObjects) {
				localIndex.getReleventSpatialKeywordRangeQueries(obj, false);
			}
		}
		stopwatch.stop();
		dataProcessingDuration = stopwatch.elapsed(TimeUnit.NANOSECONDS);
		Integer maxData = 0, maxDataIndex = 0, maxQuery = 0, maxQueryIndex = 0, maxEmitted = 0, maxEmittedIndex = 0;
		Integer totalEmiitedCount = 0;

		System.out.println(" Local:" + localIndexType.name() + " DataProcessing Time per object (nano)= " + dataProcessingDuration / dataObjects.size() / 100 + " with qulified tuples:" + totalEmiitedCount + " max emitted:" + maxEmitted
				+ " max emitted index:" + maxEmittedIndex + "Send tuples:" + sendTuples);

		dataObjects = null;
		queries = null;

	}

	static void testGlobal(ArrayList<DataObject> dataObjects, ArrayList<Query> queries, GlobalIndex globalIndex, GlobalIndexType globalIndexType, LocalIndexType localIndexType, String resultsFile, Integer numberOfEvaluators)
			throws Exception {
		long indexMemorySize = ObjectSizeCalculator.getObjectSize(globalIndex);
		System.out.println("Global index size =" + indexMemorySize);
		Integer[] dataPoints = new Integer[numberOfEvaluators];
		Integer[] queriesCount = new Integer[numberOfEvaluators];

		for (int i = 0; i < numberOfEvaluators; i++) {
			dataPoints[i] = 0;
			queriesCount[i] = 0;

		}

		//		ArrayList<SpatioTextualEvaluatorBolt> evaluators = new ArrayList<SpatioTextualEvaluatorBolt>();
		//		for (int i = 0; i < numberOfEvaluators; i++) {
		//			Rectangle bounds = globalIndex.getBoundsForTaskId(i);
		//			HashMap<String, DataSourceInformation> dataSourcesInformation = getDataSourcesInformation(bounds);
		//
		//			SpatioTextualEvaluatorBolt localEvaluator = new SpatioTextualEvaluatorBolt("TornadoTest", localIndexType, globalIndexType, null,finegGridGran);
		//			localEvaluator.setSourcesInformations(dataSourcesInformation);
		//			HashMap<String, HashMap<Integer, Query>> queryInformationHashMap = new HashMap<String, HashMap<Integer, Query>>();
		//			queryInformationHashMap.put("querySrc", new HashMap<Integer, Query>());
		//			localEvaluator.queryInformationHashMap = queryInformationHashMap;
		//			localEvaluator.selfBounds = dataSourcesInformation.get("Tweets").selfBounds;
		//			evaluators.add(localEvaluator);
		//		}

		Stopwatch stopwatch = Stopwatch.createStarted();
		Long dataProcessingDuration;

		Long queryRegisterationduration;
		int queryTasks = 0;
		//	System.out.println();
		for (Query q : queries) {
			ArrayList<Integer> tasks = globalIndex.getTaskIDsOverlappingRecangle(q.getSpatialRange());
			queryTasks += tasks.size();
			if (GlobalIndexType.PARTITIONED_TEXT_AWARE.equals(globalIndexType))
				globalIndex.addTextToTaskID(tasks, q.getQueryText(), q.getTextualPredicate() == TextualPredicate.OVERlAPS, false);
			for (Integer taskId : tasks) {
				queriesCount[taskId]++;
				//	System.out.print(taskId+",");
				//evaluators.get(taskId).handleContinousQuery(q);
			}
			//System.out.println();
		}

		stopwatch.stop();

		queryRegisterationduration = stopwatch.elapsed(TimeUnit.NANOSECONDS);

		System.out.println("Query register Time per query (nanos)= " + queryRegisterationduration / queries.size());
		System.out.println("Total query evalautors= " + queryTasks);

		stopwatch = Stopwatch.createStarted();
		Integer sendTuples = 0;
		for (int i = 0; i < 100; i++) {
			for (DataObject obj : dataObjects) {
				Integer taskId = globalIndex.getTaskIDsContainingPoint(obj.getLocation());
				Boolean send = false;
				//if (GlobalIndexType.PARTITIONED_TEXT_AWARE.equals(globalIndexType))
				send = globalIndex.verifyTextOverlap(taskId, obj.getObjectText());
				//
				//			if (SpatialHelper.overlapsSpatially(obj.getLocation(), evaluators.get(taskId).selfBounds)) {
				//				dataPoints[taskId]++;
				//				if (GlobalIndexType.PARTITIONED_TEXT_AWARE.equals(globalIndexType)) {
				//					if (send) {
				//						sendTuples++;
				//						//evaluators.get(taskId).handleVolatileDataObject(obj);
				//					}
				//				} else {
				//					sendTuples++;
				//					//evaluators.get(taskId).handleVolatileDataObject(obj);
				//				}
				//			} else
				//				;
				//	System.err.println("Error in passed data objects");
			}
		}
		stopwatch.stop();
		dataProcessingDuration = stopwatch.elapsed(TimeUnit.NANOSECONDS);
		Integer maxData = 0, maxDataIndex = 0, maxQuery = 0, maxQueryIndex = 0, maxEmitted = 0, maxEmittedIndex = 0;
		Integer totalEmiitedCount = 0;

		System.out.println(" Global:" + globalIndexType.name() + " Local:" + localIndexType.name() + " DataProcessing Time per object (nano)= " + dataProcessingDuration / dataObjects.size() / 100 + " with qulified tuples:"
				+ totalEmiitedCount + " max emitted:" + maxEmitted + " max emitted index:" + maxEmittedIndex + "Send tuples:" + sendTuples);

		for (int i = 0; i < numberOfEvaluators; i++) {
			if (dataPoints[i] > maxData) {
				maxData = dataPoints[i];
				maxDataIndex = i;
			}
			if (queriesCount[i] > maxQuery) {
				maxQuery = queriesCount[i];
				maxQueryIndex = i;
			}
		}
		System.out.println("Max data =" + maxData + ",Max data index=" + maxDataIndex + ",Max query =" + maxQuery + ",Max query index=" + maxQueryIndex);

		dataObjects = null;
		queries = null;

	}

	public static ArrayList<DataObject> readDataObjects(String fileName, int numberOfDataObjects) {
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

	static ArrayList<Query> readQueries(String fileName, int numberOfqueries, TextualPredicate textualPredicate) {
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
				obj = queriesSpout.buildQuery(line, "querySrc", 3, "Tweets", null, null, QueryType.queryTextualRange, 5.0, textualPredicate, null, null);
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

	static ArrayList<Query> readQueries(String fileName, int numberOfqueries, TextualPredicate textualPredicate, double spatialRange, int numberOfKeywords) {
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
				obj = queriesSpout.buildQuery(line, "querySrc", numberOfKeywords, "Tweets", null, null, QueryType.queryTextualRange, spatialRange, textualPredicate, null, null);
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

	static ArrayList<Query> readQueriesFromTweetsLocations(String fileName, int numberOfqueries, ArrayList<DataObject> dataObjects) {
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
				obj = buildQueriesFromUsingOtherLocations(line, "querySrc", 10, "Tweets", null, null, QueryType.queryTextualRange, 100.0, TextualPredicate.NONE, null, null,
						dataObjects.get((i * 17) % dataObjects.size()).getLocation().getX(), dataObjects.get((i * 17) % dataObjects.size()).getLocation().getX());
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

	private static void processData(String dataFileName, String queryFileName, String outputFile, Integer numberOfQueries, GlobalGridIndex gridIndex, GlobalOptimizedPartitionedIndex partitionedIndex, Integer numberOfPartitions)
			throws FileNotFoundException, UnsupportedEncodingException {
		Integer[] gridPointsCounts = new Integer[numberOfPartitions];
		Integer[] partitionedPointsCounts = new Integer[numberOfPartitions];
		Integer[] gridQueriesCounts = new Integer[numberOfPartitions];
		Integer[] partitionedQueriesCounts = new Integer[numberOfPartitions];
		String line = "";
		for (int i = 0; i < numberOfPartitions; i++) {
			gridPointsCounts[i] = 0;
			partitionedPointsCounts[i] = 0;
			gridQueriesCounts[i] = 0;
			partitionedQueriesCounts[i] = 0;
		}

		Point p;
		try {
			FileInputStream fstream = new FileInputStream(dataFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			while ((line = br.readLine()) != null) {
				p = parseTweet(line);
				if (p == null)
					continue;
				gridPointsCounts[gridIndex.getTaskIDsContainingPoint(p)]++;
				partitionedPointsCounts[partitionedIndex.getTaskIDsContainingPoint(p)]++;
			}
			br.close();
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		try {
			FileInputStream fstream = new FileInputStream(queryFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			while ((line = br.readLine()) != null) {
				if ("".equals(line))
					continue;
				Query q = buildQuery(line, QueryType.queryTextualRange, 5, 50.0);
				ArrayList<Integer> tasks;
				tasks = gridIndex.getTaskIDsOverlappingRecangle(q.getSpatialRange());
				for (Integer task : tasks)
					gridQueriesCounts[task]++;
				tasks = partitionedIndex.getTaskIDsOverlappingRecangle(q.getSpatialRange());
				for (Integer task : tasks)
					partitionedQueriesCounts[task]++;

			}
			br.close();
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		Integer maxDataGrid = 0, maxDataPartition = 0, maxQueryGrid = 0, maxQueryPartition = 0;

		PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
		for (int i = 0; i < numberOfPartitions; i++) {
			if (gridPointsCounts[i] > maxDataGrid)
				maxDataGrid = gridPointsCounts[i];
			if (partitionedPointsCounts[i] > maxDataPartition)
				maxDataPartition = partitionedPointsCounts[i];
			if (gridQueriesCounts[i] > maxQueryGrid)
				maxQueryGrid = gridQueriesCounts[i];
			if (partitionedQueriesCounts[i] > maxQueryPartition)
				maxQueryPartition = partitionedQueriesCounts[i];
			writer.println(gridPointsCounts[i] + "," + partitionedPointsCounts[i] + "," + gridQueriesCounts[i] + "," + partitionedQueriesCounts[i]);
		}
		System.out.println("Max data grid=" + maxDataGrid + ",Max data partition=" + maxDataPartition + ",Max query grid=" + maxQueryGrid + ",Max query partition=" + maxQueryPartition);
		writer.close();

	}

	public static void printMap(Map<String, Integer> map, String outputfilePath) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(outputfilePath, "UTF-8");
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			writer.println(entry.getKey() + "," + entry.getValue());
		}
		writer.close();
	}

	private static Query buildQuery(String line, QueryType queryType, Integer keywordCountVal, Double spatialRangeVal) {
		StringTokenizer stringTokenizer = new StringTokenizer(line, ",");

		//String id = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		Integer id = queryCountId++;//tweetParts[0];
		if (queryCountId >= Integer.MAX_VALUE)
			countId = 0;//
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

		//		for(int i =0;i<keywordCountVal;i++){
		//			queryText1.add(keywordsArr[i]);
		//			queryText2.add(keywordsArr[keywordsArr.length-i-1]);
		//		}

		Date date = new Date();

		Query q = new Query();
		q.setQueryId(id);
		q.setCommand(Command.addCommand);
		q.setDataSrc("");
		q.setQueryType(QueryType.queryTextualRange);
		q.setTimeStamp(date.getTime());
		if (queryType.equals(QueryType.queryTextualRange)) {
			q.setSpatialRange(new Rectangle(new Point(xCoord, yCoord), new Point(xCoord + spatialRangeVal, yCoord + spatialRangeVal)));
			q.setTextualPredicate(null);
			q.setQueryText(queryText1);
		} else if (queryType.equals(QueryType.queryTextualKNN)) {
			((KNNQuery)q).setFocalPoint(new Point(xCoord, yCoord));
			((KNNQuery)q).setTextualPredicate(null);
			((KNNQuery)q).setQueryText(queryText1);
			((KNNQuery)q).setK(5);
		} else if (queryType.equals(QueryType.queryTextualSpatialJoin)) {
			((JoinQuery)q).setSpatialRange(new Rectangle(new Point(xCoord, yCoord), new Point(xCoord + spatialRangeVal, yCoord + spatialRangeVal)));
			((JoinQuery)q).setTextualPredicate(null);
			((JoinQuery)q).setTextualPredicate2(null);
			((JoinQuery)q).setQueryText(queryText1);
			((JoinQuery)q).setQueryText(queryText2);
			((JoinQuery)q).setDistance(0.0);
		}
		return q;
	}

	private static Point parseTweet(String tweet) {
		StringTokenizer stringTokenizer = new StringTokenizer(tweet, ",");

		String id = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";

		String dateString = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		//dateString = stringTokenizer.hasMoreTokens()?stringTokenizer.nextToken():"";

		Double lat = 0.0;
		Double lon = 0.0;

		try {
			lat = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;

			lon = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;
			if (Double.compare(lat, 0.0) == 0 && Double.compare(lon, 0.0) == 0)
				return null;
			if (lat < SpatioTextualConstants.minLat || lat > SpatioTextualConstants.maxLat)
				return null;
			if (lon < SpatioTextualConstants.minLong || lon > SpatioTextualConstants.maxLong)
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		//		String textContent = "";
		//		String dummy = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		//		while (stringTokenizer.hasMoreTokens())
		//			textContent = textContent + stringTokenizer.nextToken() + " ";
		//		ArrayList<String> objectTextList = TextHelpers.transformIntoSortedArrayListOfString(textContent);
		Point p = SpatialHelper.convertFromLatLonToXYPoint(new LatLong(lat, lon));
		return p;
	}

	private static DataObject parseTweetToDataObject(String tweet) {
		LatLong latLong = new LatLong();
		String[] tweetParts = tweet.split(",");
		if (tweetParts.length < 5)
			return null;
		//	String id = tweetParts[0];
		Integer id = countId++;//tweetParts[0];
		if (countId >= Integer.MAX_VALUE)
			countId = 0;//
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

	static private Query buildQuery(String line, String srcId, int keywordCountVal, String dataSrc1, String dataSrc2, Double distance, QueryType queryType, Double spatialRangeVal, TextualPredicate textualPredicate1,
			TextualPredicate textualPredicate2, Integer k) {
		StringTokenizer stringTokenizer = new StringTokenizer(line, ",");

		//String id = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		Integer id = queryCountId++;//tweetParts[0];
		if (queryCountId >= Integer.MAX_VALUE)
			countId = 0;//
		Double xCoord = 0.0;
		Double yCoord = 0.0;
		try {
			xCoord = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;

			yCoord = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (xCoord < 0 || xCoord > SpatioTextualConstants.xMaxRange || yCoord < 0 || yCoord > SpatioTextualConstants.yMaxRange)
			return null;
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

	static private Query buildQueriesFromUsingOtherLocations(String line, String srcId, int keywordCountVal, String dataSrc1, String dataSrc2, Double distance, QueryType queryType, Double spatialRangeVal, TextualPredicate textualPredicate1,
			TextualPredicate textualPredicate2, Integer k, Double xCoord, Double yCoord) {
		StringTokenizer stringTokenizer = new StringTokenizer(line, ",");

		//String id = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
		Integer id = queryCountId++;//tweetParts[0];
		if (queryCountId >= Integer.MAX_VALUE)
			countId = 0;//
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

	static HashMap<String, DataSourceInformation> getDataSourcesInformation() {

		DataSourceInformation tweetsDataSourceInformation = new DataSourceInformation(new Rectangle(new Point(0.0, 0.0), new Point(10000.0, 10000.0)), "Tweets", DataSourceType.DATA_SOURCE, SpatioTextualConstants.volatilePersistenceState,
				SpatioTextualConstants.NOTCLEAN, true, LocalIndexType.HYBRID_GRID, finegGridGran);
		DataSourceInformation tweetsWindowDataSourceInformation = new DataSourceInformation(new Rectangle(new Point(0.0, 0.0), new Point(10000.0, 10000.0)), "TweetsW", DataSourceType.DATA_SOURCE,
				SpatioTextualConstants.volatilePersistenceState, SpatioTextualConstants.NOTCLEAN, true, LocalIndexType.HYBRID_GRID, finegGridGran);
		DataSourceInformation POIsDataSourceInformation = new DataSourceInformation(new Rectangle(new Point(0.0, 0.0), new Point(10000.0, 10000.0)), "POIs", DataSourceType.DATA_SOURCE, SpatioTextualConstants.staticPersistenceState,
				SpatioTextualConstants.NOTCLEAN, true, LocalIndexType.HYBRID_GRID, finegGridGran);
		DataSourceInformation movingObjectDataSourceInformation = new DataSourceInformation(new Rectangle(new Point(0.0, 0.0), new Point(10000.0, 10000.0)), "MO", DataSourceType.DATA_SOURCE, SpatioTextualConstants.currentPersistenceState,
				SpatioTextualConstants.NOTCLEAN, true, LocalIndexType.HYBRID_GRID, finegGridGran);

		HashMap<String, DataSourceInformation> dataSourcesInformation = new HashMap<String, DataSourceInformation>();

		dataSourcesInformation.put("Tweets", tweetsDataSourceInformation);
		dataSourcesInformation.put("TweetsW", tweetsWindowDataSourceInformation);
		dataSourcesInformation.put("POIs", POIsDataSourceInformation);
		dataSourcesInformation.put("MO", movingObjectDataSourceInformation);
		return dataSourcesInformation;
	}

	static HashMap<String, DataSourceInformation> getDataSourcesInformation(Rectangle bounds) {

		DataSourceInformation tweetsDataSourceInformation = new DataSourceInformation(bounds, "Tweets", DataSourceType.DATA_SOURCE, SpatioTextualConstants.volatilePersistenceState, SpatioTextualConstants.NOTCLEAN, true,
				LocalIndexType.HYBRID_GRID, finegGridGran);
		DataSourceInformation tweetsWindowDataSourceInformation = new DataSourceInformation(bounds, "TweetsW", DataSourceType.DATA_SOURCE, SpatioTextualConstants.volatilePersistenceState, SpatioTextualConstants.NOTCLEAN, true,
				LocalIndexType.HYBRID_GRID, finegGridGran);
		DataSourceInformation POIsDataSourceInformation = new DataSourceInformation(bounds, "POIs", DataSourceType.DATA_SOURCE, SpatioTextualConstants.staticPersistenceState, SpatioTextualConstants.NOTCLEAN, true,
				LocalIndexType.HYBRID_GRID, finegGridGran);
		DataSourceInformation movingObjectDataSourceInformation = new DataSourceInformation(bounds, "MO", DataSourceType.DATA_SOURCE, SpatioTextualConstants.currentPersistenceState, SpatioTextualConstants.NOTCLEAN, true,
				LocalIndexType.HYBRID_GRID, finegGridGran);

		HashMap<String, DataSourceInformation> dataSourcesInformation = new HashMap<String, DataSourceInformation>();

		dataSourcesInformation.put("Tweets", tweetsDataSourceInformation);
		dataSourcesInformation.put("TweetsW", tweetsWindowDataSourceInformation);
		dataSourcesInformation.put("POIs", POIsDataSourceInformation);
		dataSourcesInformation.put("MO", movingObjectDataSourceInformation);
		return dataSourcesInformation;
	}

}
