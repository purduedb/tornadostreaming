package edu.purdue.cs.tornado.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import edu.purdue.cs.tornado.evaluator.DynamicEvalautorBolt;
import edu.purdue.cs.tornado.evaluator.SpatioTextualEvaluatorBolt;
import edu.purdue.cs.tornado.experimental.TornadoExperimentsSequence;
import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.DataSourceType;
import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.ObjectSizeCalculator;
import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.QueryType;
import edu.purdue.cs.tornado.helper.RandomGenerator;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.index.global.DynamicGlobalOptimizedIndex;
import edu.purdue.cs.tornado.index.global.GlobalGridIndex;
import edu.purdue.cs.tornado.index.global.GlobalIndex;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.global.GlobalOptimizedPartitionedIndex;
import edu.purdue.cs.tornado.index.global.GlobalOptimizedPartitionedIndexLowerSpace;
import edu.purdue.cs.tornado.index.global.GlobalPartitionedGridBasedIndex;
import edu.purdue.cs.tornado.index.global.GlobalStaticPartitionedIndex;
import edu.purdue.cs.tornado.index.local.LocalHybridIndex;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.index.local.NoLocalIndex;
import edu.purdue.cs.tornado.index.local.hybridgrid.LocalHybridGridIndex;
import edu.purdue.cs.tornado.index.local.hybridpyramidminimal.KeyWordTrieIndexMinimal;
import edu.purdue.cs.tornado.index.local.hybridpyramidminimal.KeywordFrequencyStats;
import edu.purdue.cs.tornado.index.local.hybridpyramidminimal.LocalHybridPyramidGridIndexOptimized;
import edu.purdue.cs.tornado.index.local.hybridpyramidminimal.LocalHybridPyramidGridIndexOptimizedExperiment;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.JoinQuery;
import edu.purdue.cs.tornado.messages.KNNQuery;
import edu.purdue.cs.tornado.messages.MinimalRangeQuery;
import edu.purdue.cs.tornado.messages.Query;
import edu.purdue.cs.tornado.spouts.QueriesFileSystemSpout;

/**
 * This class tests the global and local index performance and test the value of
 * partitioning
 * 
 * @author ahmed
 *
 */
public class TestPartitionsPerformance {
	static Integer countId = 0;
	static Integer queryCountId = 0;
	static Integer finegGridGran = 64;
	public static RandomGenerator randomGenerator = new RandomGenerator(0);

	public static void main(String[] args) throws Exception {

		String opFile = null, qFile = null, dataFile = null;
		Integer numOfQueries = null, numOfObjs = null;
		Boolean getMemSize = null;
		Integer experiment = 0;
		Integer numberOfKeywords = null;
		Double spatialRange = null;
		Integer expansionThreshold = null;
		Integer degredationThreashold = null;
		Integer gridGran = null;
		Integer maxLevel =null;
		if (args.length > 0) {
			experiment = Integer.parseInt(args[0]);
			opFile = args[1];
			qFile = args[2];
			dataFile = args[3];
			numOfQueries = Integer.parseInt(args[4]);
			numOfObjs = Integer.parseInt(args[5]);
			int getMemSizeInt = Integer.parseInt(args[6]);
			expansionThreshold = Integer.parseInt(args[7]);
			degredationThreashold = Integer.parseInt(args[8]);
			spatialRange = Double.parseDouble(args[9]);
			numberOfKeywords = Integer.parseInt(args[10]);
			gridGran = Integer.parseInt(args[11]);
			maxLevel = Integer.parseInt(args[12]);
			getMemSize = false;
			if (getMemSizeInt == 1)
				getMemSize = true;
		}
		testGlocalIndexPerformance();
		//testPartitions();
		//testLocalIndexPerformance();
		//testlocalIndexPerformanceSpatialRange();
		//testlocalIndexPerformanceNumberOfKeywords();
		//testlocalIndexPerformanceThreasholdKDTreeVsTrie();
		//testROTPerf();
		//testINVPerf();
//		if (experiment == 0)
//			testPTPPerf(opFile, qFile, dataFile, numOfQueries, numOfObjs, getMemSize,  expansionThreshold,  degredationThreashold,  spatialRange,  numberOfKeywords,  gridGran,  maxLevel);

		//testTimeDecay();
		//testlocalIndexPerformanceNumberOfQueries();
		//experiment1EstimatethePostingListThreashold();

	}

	static void testlocalIndexPerformanceSpatialRange() throws Exception {
		String outputFile = "results/pyramidSpatialRangeContains.csv";
		String tweetsFile = "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "/media/D/datasets/tweetsForQueries.csv";

		finegGridGran = 1024;
		ArrayList<Double> spatialRanges = new ArrayList<Double>();
		spatialRanges.add(1.0);
		spatialRanges.add(5.0);
		spatialRanges.add(10.0);
		spatialRanges.add(50.0);
		spatialRanges.add(100.0);
		spatialRanges.add(500.0);
		spatialRanges.add(1000.0);
		spatialRanges.add(5000.0);

		Integer numberOfDataObjects = 1000000;
		Integer numberOfQueries = 2500000;
		Integer numberOfKeywords = 3;

		Integer maxLevel = 11;
		TextualPredicate txtPredicate = TextualPredicate.CONTAINS;
		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, numberOfDataObjects);
		TornadoExperimentsSequence.appendToFile(outputFile,
				"Number of queries," + numberOfQueries + ",Number of objects," + numberOfDataObjects + ",Number of keywords," + numberOfKeywords + ",Max grid granualitry," + finegGridGran + "text perdicate," + txtPredicate.toString());
		System.out.println("Done reading data");
		TornadoExperimentsSequence.appendToFile(outputFile, "Spatial range, query insert time, object time, index size");
		ArrayList<MinimalRangeQuery> queries = readMinimalQueries(queriesFile, numberOfQueries, txtPredicate, 0, numberOfKeywords, false, false);
		System.out.println("Done reading queries");
		double previousRange = 0;
		long queriesSize = ObjectSizeCalculator.getObjectSize(queries);
		System.out.println("Queries size =" + queriesSize / 1024 / 1024 + " MB");

		for (Double spatialRange : spatialRanges) {
			System.out.println("********************************************************");
			System.out.println("Hybrid Pyramid grid spatial range" + spatialRange);
			String result = "" + spatialRange + ",";
			for (MinimalRangeQuery q : queries) {
				q.setSpatialRange(new Rectangle(q.getSpatialRange().getMin(), new Point(q.getSpatialRange().getMax().getX() - previousRange + spatialRange, q.getSpatialRange().getMax().getY() - previousRange + spatialRange)));
				//	System.out.println(q.toString());
			}
			previousRange = spatialRange;
			System.out.println("Done reading queries");

			System.gc();
			System.gc();

			LocalHybridPyramidGridIndexOptimized localHybridPyramidIndex = new LocalHybridPyramidGridIndexOptimized(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null,
					finegGridGran, maxLevel);
			result += testLocalPyramid(dataObjects, queries, localHybridPyramidIndex, LocalIndexType.HYBRID_GRID, false, false);
			TornadoExperimentsSequence.appendToFile(outputFile, result);
			localHybridPyramidIndex = null;
			System.gc();
			System.gc();
		}
		//		outputFile = "results/pyramidSpatialRangeOverlaps.csv";
		//		txtPredicate = TextualPredicate.OVERlAPS;
		//		TornadoExperimentsSequence.appendToFile(outputFile,
		//				"Number of queries," + numberOfQueries + ",Number of objects," + numberOfDataObjects + ",Number of keywords," + numberOfKeywords + ",Max grid granualitry," + finegGridGran + "text perdicate," + txtPredicate.toString());
		//
		//		TornadoExperimentsSequence.appendToFile(outputFile, "Spatial range, query insert time, object time, index size");
		//
		//		System.out.println("Done reading data");
		//		for (Double spatialRange : spatialRanges) {
		//			String result = "" + spatialRange + ",";
		//			ArrayList<MinimalRangeQuery> queries = readMinimalQueries(queriesFile, numberOfQueries, txtPredicate, spatialRange, numberOfKeywords);
		//			System.out.println("Done reading queries");
		//			long queriesSize = ObjectSizeCalculator.getObjectSize(queries);
		//			System.out.println("Queries size =" + queriesSize / 1024 / 1024 + " MB");
		//			System.gc();
		//			System.gc();
		//			System.out.println("********************************************************");
		//			System.out.println("Hybrid Pyramid grid");
		//			LocalHybridPyramidGridIndexOptimized localHybridPyramidIndex = new LocalHybridPyramidGridIndexOptimized(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null,
		//					finegGridGran);
		//			result += testLocal(dataObjects, queries, localHybridPyramidIndex, LocalIndexType.HYBRID_GRID);
		//			TornadoExperimentsSequence.appendToFile(outputFile, result);
		//			localHybridPyramidIndex = null;
		//			System.gc();
		//			System.gc();
		//		}
	}

	static void testlocalIndexPerformanceThreasholdKDTreeVsTrie() throws Exception {
		String outputFile = "results/pyramidThreasholdContains.csv";
		String outputFileExperiment = "results/pyramidThreasholdContainsExperiment.csv";
		String tweetsFile = "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "/media/D/datasets/tweetsForQueries.csv";

		finegGridGran = 1024;
		ArrayList<Integer> thresholds = new ArrayList<Integer>();
		thresholds.add(1);
		//		thresholds.add(2);
		//		thresholds.add(3);
		//		thresholds.add(4);
		thresholds.add(5);
		//		thresholds.add(6);
		//		thresholds.add(7);
		//		thresholds.add(8);
		//		thresholds.add(9);
		thresholds.add(10);
		thresholds.add(50);
		//		thresholds.add(100);
		//		thresholds.add(500);

		Integer numberOfDataObjects = 10;//00000;
		Integer numberOfQueries = 2500000;
		Integer numberOfKeywords = 5;//3;
		Double spatialRange = 100.0;

		Integer maxLevel = 11;
		TextualPredicate txtPredicate = TextualPredicate.CONTAINS;
		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, numberOfDataObjects);
		TornadoExperimentsSequence.appendToFile(outputFile,
				"Number of queries," + numberOfQueries + ",Number of objects," + numberOfDataObjects + ",Number of keywords," + numberOfKeywords + ",Max grid granualitry," + finegGridGran + "text perdicate," + txtPredicate.toString());
		System.out.println("Done reading data");
		TornadoExperimentsSequence.appendToFile(outputFile, "Threashold, query insert time, object time, index size");
		ArrayList<MinimalRangeQuery> queries = readMinimalQueries(queriesFile, numberOfQueries, txtPredicate, spatialRange, numberOfKeywords, true, true);
		System.out.println("Done reading queries");
		//		long queriesSize = ObjectSizeCalculator.getObjectSize(queries);
		//		System.out.println("Queries size =" + queriesSize / 1024 / 1024 + " MB");

		for (Integer threshold : thresholds) {
			System.out.println("********************************************************");
			System.out.println("Main Hybrid Pyramid grid threshold" + threshold);
			String result = "" + threshold + ",";
			System.gc();
			System.gc();
			KeyWordTrieIndexMinimal.SPLIT_THRESHOLD_FREQ = threshold;
			LocalHybridPyramidGridIndexOptimized localHybridPyramidIndex = new LocalHybridPyramidGridIndexOptimized(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null,
					finegGridGran, maxLevel);
			result += testLocalPyramid(dataObjects, queries, localHybridPyramidIndex, LocalIndexType.HYBRID_GRID, true, false);
			TornadoExperimentsSequence.appendToFile(outputFile, result);
			localHybridPyramidIndex = null;
			System.gc();
			System.gc();
			System.out.println("********************************************************");
			System.out.println("Experiment Hybrid Pyramid grid threshold" + threshold);
			result = "" + threshold + ",";
			System.gc();
			System.gc();
			LocalHybridPyramidGridIndexOptimizedExperiment.Trie_SPLIT_THRESHOLD = threshold;//nMath.max(threshold/2,1);

			LocalHybridPyramidGridIndexOptimizedExperiment localHybridPyramidIndexExperiment = new LocalHybridPyramidGridIndexOptimizedExperiment(
					new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null, finegGridGran, maxLevel);
			result += testLocalPyramid(dataObjects, queries, localHybridPyramidIndexExperiment, LocalIndexType.HYBRID_GRID, true, false);
			TornadoExperimentsSequence.appendToFile(outputFileExperiment, result);
			localHybridPyramidIndexExperiment = null;
			System.gc();
			System.gc();
		}
	}

	static void testROTPerf() throws Exception {
		String outputFile = "results/ROTPERF.csv";
		String tweetsFile = "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "/media/D/datasets/tweetsForQueries.csv";

		finegGridGran = 1024;
		ArrayList<Double> spatialRanges = new ArrayList<Double>();
		spatialRanges.add(1.0);
		spatialRanges.add(5.0);
		spatialRanges.add(10.0);
		spatialRanges.add(50.0);
		spatialRanges.add(100.0);
		spatialRanges.add(500.0);
		spatialRanges.add(1000.0);
		spatialRanges.add(5000.0);

		Integer numberOfDataObjects = 100000;
		Integer numberOfQueries = 2500000;
		Integer numberOfKeywords = 3;

		Integer maxLevel = 11;
		TextualPredicate txtPredicate = TextualPredicate.CONTAINS;
		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, numberOfDataObjects);
		TornadoExperimentsSequence.appendToFile(outputFile,
				"Number of queries," + numberOfQueries + ",Number of objects," + numberOfDataObjects + ",Number of keywords," + numberOfKeywords + ",Max grid granualitry," + finegGridGran + "text perdicate," + txtPredicate.toString());
		System.out.println("Done reading data");
		TornadoExperimentsSequence.appendToFile(outputFile, "Spatial range, query insert time, object time, index size");
		ArrayList<MinimalRangeQuery> queries = readMinimalQueries(queriesFile, numberOfQueries, txtPredicate, 0, numberOfKeywords, false, false);
		System.out.println("Done reading queries");
		double previousRange = 0;
		long queriesSize = ObjectSizeCalculator.getObjectSize(queries);
		System.out.println("Queries size =" + queriesSize / 1024 / 1024 + " MB");

		for (Double spatialRange : spatialRanges) {
			System.out.println("********************************************************");
			System.out.println("Hybrid Pyramid grid spatial range" + spatialRange);
			String result = "" + spatialRange + ",";
			for (MinimalRangeQuery q : queries) {
				q.setSpatialRange(new Rectangle(q.getSpatialRange().getMin(), new Point(q.getSpatialRange().getMax().getX() - previousRange + spatialRange, q.getSpatialRange().getMax().getY() - previousRange + spatialRange)));
				//	System.out.println(q.toString());
			}
			previousRange = spatialRange;
			System.out.println("Done reading queries");

			System.gc();
			System.gc();
			//Testing the trie, this only allows insertions into the trie
			System.out.println("********************************************************");
			KeyWordTrieIndexMinimal.SPLIT_THRESHOLD_FREQ = 0;
			KeyWordTrieIndexMinimal.SPLIT_THRESHOLD = 1;
			System.out.println("Testing the trie only, the number of queries is " + queries.size());
			System.gc();
			System.gc();

			LocalHybridPyramidGridIndexOptimized localHybridPyramidIndex = new LocalHybridPyramidGridIndexOptimized(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null,
					finegGridGran, maxLevel);
			result += testLocalPyramid(dataObjects, queries, localHybridPyramidIndex, LocalIndexType.HYBRID_GRID, true, false);
			TornadoExperimentsSequence.appendToFile(outputFile, result);
			localHybridPyramidIndex = null;
			System.gc();
			System.gc();
		}

	}

	static void testINVPerf() throws Exception {
		String outputFile = "results/RILperf.csv";
		String tweetsFile = "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "/media/D/datasets/tweetsForQueries.csv";

		finegGridGran = 1024;
		ArrayList<Double> spatialRanges = new ArrayList<Double>();
		spatialRanges.add(1.0);
		spatialRanges.add(5.0);
		spatialRanges.add(10.0);
		spatialRanges.add(50.0);
		spatialRanges.add(100.0);
		spatialRanges.add(500.0);
		spatialRanges.add(1000.0);
		spatialRanges.add(5000.0);

		Integer numberOfDataObjects = 100000;
		Integer numberOfQueries = 2500000;
		Integer numberOfKeywords = 3;

		Integer maxLevel = 11;
		TextualPredicate txtPredicate = TextualPredicate.CONTAINS;
		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, numberOfDataObjects);
		TornadoExperimentsSequence.appendToFile(outputFile,
				"Number of queries," + numberOfQueries + ",Number of objects," + numberOfDataObjects + ",Number of keywords," + numberOfKeywords + ",Max grid granualitry," + finegGridGran + "text perdicate," + txtPredicate.toString());
		System.out.println("Done reading data");
		TornadoExperimentsSequence.appendToFile(outputFile, "Spatial range, query insert time, object time, index size");
		ArrayList<MinimalRangeQuery> queries = readMinimalQueries(queriesFile, numberOfQueries, txtPredicate, 0, numberOfKeywords, false, false);
		System.out.println("Done reading queries");
		double previousRange = 0;

		for (Double spatialRange : spatialRanges) {
			System.out.println("********************************************************");
			System.out.println("Hybrid Pyramid grid spatial range" + spatialRange);
			String result = "" + spatialRange + ",";
			for (MinimalRangeQuery q : queries) {
				q.setSpatialRange(new Rectangle(q.getSpatialRange().getMin(), new Point(q.getSpatialRange().getMax().getX() - previousRange + spatialRange, q.getSpatialRange().getMax().getY() - previousRange + spatialRange)));
				//	System.out.println(q.toString());
			}
			previousRange = spatialRange;
			System.out.println("Done reading queries");

			System.gc();
			System.gc();
			//Testing the trie, this only allows insertions into the trie
			System.out.println("********************************************************");
			KeyWordTrieIndexMinimal.SPLIT_THRESHOLD_FREQ = 0;
			KeyWordTrieIndexMinimal.SPLIT_THRESHOLD = 1;
			System.out.println("Testing the trie only, the number of queries is " + queries.size());
			System.gc();
			System.gc();
			System.out.println("Done reading queries");
			//Testing the trie, this only allows insertions into the trie
			System.out.println("********************************************************");
			KeyWordTrieIndexMinimal.SPLIT_THRESHOLD_FREQ = 1000000000;
			KeyWordTrieIndexMinimal.SPLIT_THRESHOLD = 1000000000;
			LocalHybridPyramidGridIndexOptimizedExperiment.Trie_SPLIT_THRESHOLD = 1000000000;//nMath.max(threshold/2,1);
			LocalHybridPyramidGridIndexOptimized localHybridPyramidIndex = new LocalHybridPyramidGridIndexOptimized(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null,
					finegGridGran, maxLevel);

			result += testLocalPyramid(dataObjects, queries, localHybridPyramidIndex, LocalIndexType.HYBRID_GRID, true, false);
			TornadoExperimentsSequence.appendToFile(outputFile, result);
			localHybridPyramidIndex = null;
			System.gc();
			System.gc();
		}

	}

	static void testTimeDecay() throws Exception {

		String tweetsFile = "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "/media/D/datasets/tweetsForQueries.csv";
		String queryScoreFile = "results/timedecaydata/querydecayperformance.csv";
		String objectScoreFile = "results/timedecaydata/objectdecayperformance.csv";
		String recencyScoreFile = "results/timedecaydata/recencydecayperformance.csv";

		finegGridGran = 1024;

		Integer numberOfDataObjects = 100000;
		//Integer numberOfQueries = 1000000;
		Integer numberOfKeywords = 3;

		Integer maxLevel = 11;
		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, numberOfDataObjects);
		System.out.println("Done reading data");
		//		ArrayList<MinimalRangeQuery> queries = readMinimalQueries(queriesFile, numberOfQueries, TextualPredicate.CONTAINS, 100, numberOfKeywords, false, false);
		//		System.out.println("Done reading queries");

		LocalHybridPyramidGridIndexOptimizedExperiment localHybridPyramidIndexExperiment = new LocalHybridPyramidGridIndexOptimizedExperiment(
				new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null, finegGridGran, maxLevel);

		//		for (MinimalRangeQuery q : queries)
		//			localHybridPyramidIndexExperiment.updateKeywordStatsForQueryAdd(q);
		//
		for (DataObject obj : dataObjects)
			for (String keyword : obj.getObjectText()) {
				if (!localHybridPyramidIndexExperiment.overallQueryTextSummery.containsKey(keyword))
					localHybridPyramidIndexExperiment.overallQueryTextSummery.put(keyword, new KeywordFrequencyStats(1, 0, 1));
				localHybridPyramidIndexExperiment.overallQueryTextSummery.get(keyword).objectVisitCount++;
			}

		ArrayList<Entry<String, KeywordFrequencyStats>> statsArr = new ArrayList(localHybridPyramidIndexExperiment.overallQueryTextSummery.entrySet());
		//		statsArr.sort(new QueryRankAwareStatsComparator());
		//
		//		int i = 0;
		//		for (Entry<String, KeywordFrequencyStats> e : statsArr)
		//			TornadoExperimentsSequence.appendToFile(queryScoreFile, "" + (i++) + "," + e.getKey() + "," + e.getValue().queryCount);
		//		System.out.println("Done 1");
		statsArr.sort(new ObjectCountCompartor());
		int i = 0;
		for (Entry<String, KeywordFrequencyStats> e : statsArr)
			TornadoExperimentsSequence.appendToFile(objectScoreFile, "" + (i++) + "," + e.getKey() + "," + e.getValue().objectVisitCount);
		System.out.println("Done 2");

	}

	static void testPTPPerf(String opFile, String qFile, String tweetFile, Integer numOfQueries, Integer numOfObjs, Boolean getMemSize, Integer expansionThreshold, Integer degThreshold, Double spaRange, Integer numOfKeywords, Integer gridGan, Integer maxLevl)
			throws Exception {
		String outputFile = "results/ptpperformance.csv";
		if (opFile != null)
			outputFile = opFile;
		String tweetsFile = "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
		if (tweetFile != null)
			tweetsFile = tweetFile;
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "/media/D/datasets/tweetsForQueries.csv";
		if (qFile != null)
			queriesFile = qFile;
		finegGridGran = 1024;
		ArrayList<Integer> thresholds = new ArrayList<Integer>();
		//thresholds.add(1);
		////		//thresholds.add(2);
		//thresholds.add(3);
		////		//thresholds.add(4);
		thresholds.add(5);
		//		//		thresholds.add(6);
		//		//		thresholds.add(7);
		//		//		thresholds.add(8);
		//		//		thresholds.add(9);
		thresholds.add(10);
		thresholds.add(25);
		thresholds.add(50);

		finegGridGran = 512;
		if(gridGan!=null)
			finegGridGran = gridGan;
		ArrayList<Double> spatialRanges = new ArrayList<Double>();
		//spatialRanges.add(1.0);
		//spatialRanges.add(5.0);
		//spatialRanges.add(10.0);
		//spatialRanges.add(50.0);
		spatialRanges.add(100.0);
		//spatialRanges.add(500.0);
		//spatialRanges.add(1000.0);
		//spatialRanges.add(5000.0);

		Integer numberOfDataObjects = 1000000;
		if (numOfObjs != null)
			numberOfDataObjects = numOfObjs;

		Integer numberOfQueries = 5000000;
		if (numOfQueries != null)
			numberOfQueries = numOfQueries;
		Integer numberOfKeywords = 3;
		if (numOfKeywords != null)
			numberOfKeywords = numOfKeywords;
		boolean randomSpatialRange = true;
		boolean randomNumberOfKeywords = false;
		boolean getSize = false;
		if (getMemSize != null)
			getSize = getMemSize;
		boolean verifyCorrectness = false;

		Double spatialRange = 100.0;
		if (spaRange != null)
			spatialRange = spaRange;
		Integer threshold = 5;
		if (expansionThreshold != null)
			threshold = expansionThreshold;
		Integer degerationThreshold = 2;
		if (degThreshold != null)
			degerationThreshold = degThreshold;
		Integer maxLevel = 8;
		if(maxLevl!=null)
			maxLevel = maxLevl;
		TextualPredicate txtPredicate = TextualPredicate.CONTAINS;
		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, numberOfDataObjects);
		TornadoExperimentsSequence.appendToFile(outputFile,
				"Number of queries," + numberOfQueries + ",Number of objects," + numberOfDataObjects + ",Number of keywords," + numberOfKeywords + ",Max grid granualitry," + finegGridGran + "text perdicate," + txtPredicate.toString());
		System.out.println("Done reading data");
		TornadoExperimentsSequence.appendToFile(outputFile, "Spatial range, query insert time, object time, index size");

		//testing the performance of the ptp under various threasholds

		//for (Double spatialRange : spatialRanges) {
		System.out.println("********************************************************");
		System.out.println("Hybrid Pyramid grid spatial range" + spatialRange);
		ArrayList<MinimalRangeQuery> queries = readMinimalQueries(queriesFile, numberOfQueries, txtPredicate, spatialRange, numberOfKeywords, randomSpatialRange, randomNumberOfKeywords);
		System.out.println("Done reading queries");
		//previousRange = spatialRange;
		//	for (Integer threshold : thresholds) {
		String result = "\n PTP Threashold" + threshold + ",\n";
		System.out.println("Hybrid Pyramid grid threashold" + threshold);
		System.gc();
		System.gc();
		System.gc();
		System.gc();

		LocalHybridPyramidGridIndexOptimizedExperiment.Trie_SPLIT_THRESHOLD = threshold;
		LocalHybridPyramidGridIndexOptimizedExperiment.Degredation_Ratio = degerationThreshold;

		LocalHybridPyramidGridIndexOptimizedExperiment localHybridPyramidIndexExperiment = new LocalHybridPyramidGridIndexOptimizedExperiment(
				new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null, finegGridGran, maxLevel);
		result += testLocalPyramid(dataObjects, queries, localHybridPyramidIndexExperiment, LocalIndexType.HYBRID_GRID, getSize, verifyCorrectness);

		localHybridPyramidIndexExperiment = null;
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		localHybridPyramidIndexExperiment = new LocalHybridPyramidGridIndexOptimizedExperiment(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null, finegGridGran,
				maxLevel);
		result += testLocalPyramid(dataObjects, queries, localHybridPyramidIndexExperiment, LocalIndexType.HYBRID_GRID, getSize, verifyCorrectness);

		localHybridPyramidIndexExperiment = null;
		System.gc();
		System.gc();
		//				TornadoExperimentsSequence.appendToFile(outputFile, result);
	}

	static public void testBruteForce(ArrayList<DataObject> dataObjects, ArrayList<MinimalRangeQuery> queries) {
		int totalResult = 0;
		for (DataObject obj : dataObjects) {
			for (MinimalRangeQuery q : queries) {
				if (SpatialHelper.overlapsSpatially(obj.getLocation(), q.spatialRange) && TextHelpers.containsTextually(obj.getObjectText(), q.queryText))
					totalResult++;
			}
		}
		System.out.println(totalResult);
	}

	static public void testTrie(ArrayList<DataObject> dataObjects, ArrayList<MinimalRangeQuery> queries, ArrayList<Double> executionResults) {
		int totalResult = 0;
		KeyWordTrieIndexMinimal trieIndex = new KeyWordTrieIndexMinimal();
		double sumQueryKeyCount = 0;
		double maxQueryKeyCount = 0;
		double sumObjKeyCount = 0;
		double maxObjKeyCount = 0;
		double sumTrieAccessCount = 0;
		double maxTrieAccessCount = 0;
		for (MinimalRangeQuery q : queries) {
			trieIndex.insert(q.queryText, q);
			sumQueryKeyCount += q.queryText.size();
			if (q.queryText.size() > maxQueryKeyCount)
				maxQueryKeyCount = q.queryText.size();
		}
		for (DataObject obj : dataObjects) {
			sumObjKeyCount += obj.getObjectText().size();
			if (obj.getObjectText().size() > maxObjKeyCount)
				maxObjKeyCount = obj.getObjectText().size();
			Integer[] opCount = new Integer[1];
			opCount[0] = 0;
			LinkedList<MinimalRangeQuery> result = trieIndex.find(obj.getObjectText(), opCount);
			sumTrieAccessCount += opCount[0];
			if (opCount[0] > maxTrieAccessCount)
				maxTrieAccessCount = opCount[0];

			for (MinimalRangeQuery q : result) {
				if (SpatialHelper.overlapsSpatially(obj.getLocation(), q.spatialRange) && TextHelpers.containsTextually(obj.getObjectText(), q.queryText))
					totalResult++;
			}
		}
		executionResults.add(sumObjKeyCount / dataObjects.size());
		executionResults.add(maxObjKeyCount);
		executionResults.add(sumQueryKeyCount / queries.size());
		executionResults.add(maxQueryKeyCount);
		executionResults.add(sumTrieAccessCount / dataObjects.size());
		executionResults.add(maxTrieAccessCount);
		System.out.println(totalResult);
	}

	static void testlocalIndexPerformanceNumberOfKeywords() throws Exception {
		String outputFile = "results/pyramidNumberOfKeywordsContains.csv";
		String tweetsFile = "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "/media/D/datasets/tweetsForQueries.csv";

		finegGridGran = 1024;
		ArrayList<Integer> numberOfKeywords = new ArrayList<Integer>();
		numberOfKeywords.add(1);
		numberOfKeywords.add(2);
		numberOfKeywords.add(3);
		numberOfKeywords.add(4);
		numberOfKeywords.add(5);
		//		numberOfKeywords.add(6);
		//		numberOfKeywords.add(7);

		Integer numberOfDataObjects = 1000000;
		Integer numberOfQueries = 2500000;
		Double spatialRange = 10.0;
		Integer maxLevel = 10;
		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, numberOfDataObjects);

		TextualPredicate txtPredicate = TextualPredicate.CONTAINS;
		TornadoExperimentsSequence.appendToFile(outputFile,
				"Number of queries," + numberOfQueries + ",Number of objects," + numberOfDataObjects + ",Number of keywords," + numberOfKeywords + ",Max grid granualitry," + finegGridGran + "text perdicate," + txtPredicate.toString());
		TornadoExperimentsSequence.appendToFile(outputFile, "NumberOfKeywords, query insert time, object time, index size");

		System.out.println("Done reading data");
		for (Integer numberofkeyword : numberOfKeywords) {
			String result = "" + numberofkeyword + ",";
			ArrayList<MinimalRangeQuery> queries = readMinimalQueries(queriesFile, numberOfQueries, txtPredicate, spatialRange, numberofkeyword, false, false);
			System.out.println("Done reading queries");
			long queriesSize = ObjectSizeCalculator.getObjectSize(queries);
			System.out.println("Queries size =" + queriesSize / 1024 / 1024 + " MB");
			System.gc();
			System.gc();
			System.out.println("********************************************************");
			System.out.println("Hybrid Pyramid grid");
			LocalHybridPyramidGridIndexOptimized localHybridPyramidIndex = new LocalHybridPyramidGridIndexOptimized(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null,
					finegGridGran, maxLevel);
			result += testLocalPyramid(dataObjects, queries, localHybridPyramidIndex, LocalIndexType.HYBRID_GRID, false, false);
			TornadoExperimentsSequence.appendToFile(outputFile, result);
			localHybridPyramidIndex = null;
			System.gc();
			System.gc();
		}
		outputFile = "results/pyramidNumberOfKeywordsOverlaps.csv";
		txtPredicate = TextualPredicate.OVERlAPS;
		TornadoExperimentsSequence.appendToFile(outputFile,
				"Number of queries," + numberOfQueries + ",Number of objects," + numberOfDataObjects + ",Number of keywords," + numberOfKeywords + ",Max grid granualitry," + finegGridGran + "text perdicate," + txtPredicate.toString());
		TornadoExperimentsSequence.appendToFile(outputFile, "NumberOfKeywords, query insert time, object time, index size");

		System.out.println("Done reading data");
		for (Integer numberofkeyword : numberOfKeywords) {
			String result = "" + numberofkeyword + ",";
			ArrayList<MinimalRangeQuery> queries = readMinimalQueries(queriesFile, numberOfQueries, txtPredicate, spatialRange, numberofkeyword, false, false);
			System.out.println("Done reading queries");
			long queriesSize = ObjectSizeCalculator.getObjectSize(queries);
			System.out.println("Queries size =" + queriesSize / 1024 / 1024 + " MB");
			System.gc();
			System.gc();
			System.out.println("********************************************************");
			System.out.println("Hybrid Pyramid grid");
			LocalHybridPyramidGridIndexOptimized localHybridPyramidIndex = new LocalHybridPyramidGridIndexOptimized(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null,
					finegGridGran, maxLevel);
			result += testLocalPyramid(dataObjects, queries, localHybridPyramidIndex, LocalIndexType.HYBRID_GRID, false, false);
			TornadoExperimentsSequence.appendToFile(outputFile, result);
			localHybridPyramidIndex = null;
			System.gc();
			System.gc();
		}
	}

	static void testlocalIndexPerformanceNumberOfQueries() throws Exception {
		String outputFile = "results/pyramidNumberOfQueriesOverlaps.csv";
		String tweetsFile = "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "/media/D/datasets/tweetsForQueries.csv";

		finegGridGran = 1024;
		ArrayList<Integer> numberofqueries = new ArrayList<Integer>();
		numberofqueries.add(500000);
		numberofqueries.add(1000000);
		numberofqueries.add(2000000);
		numberofqueries.add(3000000);

		Integer numberOfDataObjects = 1000000;
		Double spatialRange = 5.0;
		int numberOfKeywords = 3;
		Integer maxLevel = 10;
		TextualPredicate txtPredicate = TextualPredicate.OVERlAPS;
		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, numberOfDataObjects);
		TornadoExperimentsSequence.appendToFile(outputFile, ",Number of objects," + numberOfDataObjects + ",Number of keywords," + numberOfKeywords + ",Max grid granualitry," + finegGridGran + "text perdicate," + txtPredicate.toString());

		TornadoExperimentsSequence.appendToFile(outputFile, "NumberOfKeywords, query insert time, object time, index size");

		System.out.println("Done reading data");
		for (Integer queriesNum : numberofqueries) {
			String result = "" + queriesNum + ",";
			ArrayList<MinimalRangeQuery> queries = readMinimalQueries(queriesFile, queriesNum, txtPredicate, spatialRange, numberOfKeywords, false, false);
			System.out.println("Done reading queries");
			long queriesSize = ObjectSizeCalculator.getObjectSize(queries);
			System.out.println("Queries size =" + queriesSize / 1024 / 1024 + " MB");
			System.gc();
			System.gc();
			System.out.println("********************************************************");
			System.out.println("Hybrid Pyramid grid");
			LocalHybridPyramidGridIndexOptimized localHybridPyramidIndex = new LocalHybridPyramidGridIndexOptimized(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null,
					finegGridGran, maxLevel);
			result += testLocalPyramid(dataObjects, queries, localHybridPyramidIndex, LocalIndexType.HYBRID_GRID, false, false);
			TornadoExperimentsSequence.appendToFile(outputFile, result);
			localHybridPyramidIndex = null;
			System.gc();
			System.gc();
		}
	}

	static void testGlocalIndexPerformance() throws Exception {
		String tweetsFile = "C:\\datasets\\tweets\\sampletweets.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "C:\\datasets\\tweets\\tweetsForQueries.csv";

		finegGridGran = 1024;

		Integer numberOfPartitions = 500;

		String partitionsPath = "resources/partitions" + numberOfPartitions + "_" + finegGridGran + "_prio.ser";
		ArrayList<Cell> partitions = PartitionsHelper.readSerializedPartitions(partitionsPath);
		ArrayList<Integer> evaluators = new ArrayList<Integer>();
		for (int i = 0; i < numberOfPartitions; i++) {
			evaluators.add(i);
		}
		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, 1000000);
		System.out.println("Done reading data");
		//ArrayList<Query> queries = readQueriesFromTweetsLocations(queriesFile, 1000000,dataObjects);
		//ArrayList<Query> queries = readQueries(queriesFile, 100000,TextualPredicate.OVERlAPS);
		ArrayList<Query> queries = readQueries(queriesFile, 2000000, TextualPredicate.CONTAINS, 10, 3);

		System.out.println("Done reading queries");

		//		
		//		System.out.println("Partitioned text aware");
		//		GlobalOptimizedPartitionedTextAwareIndex partitionedIndex1= new GlobalOptimizedPartitionedTextAwareIndex(numberOfPartitions, evaluators, partitions,finegGridGran);
		//		testGlobal(dataObjects, queries, partitionedIndex1, GlobalIndexType.PARTITIONED_TEXT_AWARE, LocalIndexType.HYBRID_GRID, "", numberOfPartitions);
		//		partitionedIndex1=null;
		//		System.gc();
		//		System.gc();
		for (int i = 0; i < 1; i++) {
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
			System.out.println("------------");
			System.out.println("Partitioned  Rtree");
			GlobalStaticPartitionedIndex partitionedIndex3 = new GlobalStaticPartitionedIndex(numberOfPartitions,
					evaluators, partitions, finegGridGran);
			testGlobal(dataObjects, queries, partitionedIndex3, GlobalIndexType.PARTITIONED, LocalIndexType.HYBRID_GRID,
					"", numberOfPartitions);
			partitionedIndex3 = null;
			System.gc();
			System.gc();

		}

	}

	static void testLocalIndexPerformance() throws Exception {
		String tweetsFile = "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "/media/D/datasets/tweetsForQueries.csv";

		finegGridGran = 1024;

		Integer numberOfDataObjects = 100000;
		Integer numberOfQueries = 2500000;

		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, numberOfDataObjects);
		System.out.println("Done reading data");
		//ArrayList<Query> queries = readQueriesFromTweetsLocations(queriesFile, 1000000,dataObjects);
		ArrayList<Query> queries = readQueries(queriesFile, numberOfQueries, TextualPredicate.CONTAINS, 10, 3);
		//ArrayList<MinimalRangeQuery> minimalqueries = readMinimalQueries(queriesFile, numberOfQueries, TextualPredicate.OVERlAPS, 5, 1);
		//ArrayList<Query> queries = readRandomQueries(queriesFile, numberOfQueries, TextualPredicate.CONTAINS,100, 10);
		System.out.println("Done reading queries");
		//long queriesSize = ObjectSizeCalculator.getObjectSize(minimalqueries);
		//System.out.println("Queries size =" + queriesSize / 1024 / 1024 + " MB");
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

		//		System.out.println("********************************************************");
		//		System.out.println("Hybrid Pyramid grid");
		//		LocalHybridPyramidGridIndexOptimized localHybridPyramidIndex = new LocalHybridPyramidGridIndexOptimized(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null,
		//				finegGridGran);
		//		testLocal(dataObjects, minimalqueries, localHybridPyramidIndex, LocalIndexType.HYBRID_GRID);
		//		localHybridPyramidIndex = null;
		//		System.gc();
		//		System.gc();
		//		System.out.println("********************************************************");
		//		System.out.println("Test keyword trie");
		//		testKeywordTrie(dataObjects,queries);
		//		System.gc();
		//		System.gc();
		//		System.out.println("********************************************************");
		//		System.out.println("Optimized Hybrid grid");
		//		LocalHybridGridIndexOptimized localHybridIndexOpt = new LocalHybridGridIndexOptimized(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null, finegGridGran);
		//		testLocal(dataObjects, queries, localHybridIndexOpt, LocalIndexType.HYBRID_GRID);
		//		localHybridIndexOpt = null;
		//		System.gc();
		//		System.gc();
		System.out.println("********************************************************");
		System.out.println("Hybrid grid");
		LocalHybridGridIndex localHybridIndex = new LocalHybridGridIndex(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange)), null, finegGridGran);
		testLocal(dataObjects, queries, localHybridIndex, LocalIndexType.HYBRID_GRID);
		localHybridIndex = null;
		System.gc();
		System.gc();

		//}

	}

	public static void testKeywordTrie(ArrayList<DataObject> dataObjects, ArrayList<MinimalRangeQuery> queries) {
		KeyWordTrieIndexMinimal localIndex = new KeyWordTrieIndexMinimal();
		Stopwatch stopwatch = Stopwatch.createStarted();
		Long dataProcessingDuration;

		Long queryRegisterationduration;
		int queryTasks = 0;
		//	System.out.println();
		for (MinimalRangeQuery q : queries) {
			localIndex.insertOld1(q.getQueryText(), q);
		}

		stopwatch.stop();

		queryRegisterationduration = stopwatch.elapsed(TimeUnit.NANOSECONDS);

		long indexMemorySize = ObjectSizeCalculator.getObjectSize(localIndex);
		System.out.println("Local index size =" + indexMemorySize / 1024 / 1024 + " MB");

		System.out.println("Query register Time per query (nanos)= " + queryRegisterationduration / queries.size());
		System.out.println("Total query evalautors= " + queryTasks);

		stopwatch = Stopwatch.createStarted();
		int querycount = 0;
		Integer sendTuples = 0;
		int i = 0;
		//for (int i = 0; i < 100; i++) {
		for (DataObject obj : dataObjects) {
			LinkedList<MinimalRangeQuery> result = localIndex.find(obj.getObjectText());

			if (result != null && result.size() > 0) {
				querycount += result.size();
				if (result.size() > 0) {
					System.out.println("-----------------------------------");
					System.out.println(obj.toString());
					for (MinimalRangeQuery q : result)
						System.out.println(q.toString());
				}
			}
			i++;

		}
		//}
		stopwatch.stop();
		System.gc();
		System.gc();

		dataProcessingDuration = stopwatch.elapsed(TimeUnit.NANOSECONDS);
		Integer maxData = 0, maxDataIndex = 0, maxQuery = 0, maxQueryIndex = 0, maxEmitted = 0, maxEmittedIndex = 0;
		Integer totalEmiitedCount = 0;

		System.out.println(" DataProcessing Time per object (nano)= " + dataProcessingDuration / dataObjects.size() + " with qulified tuples:" + totalEmiitedCount + " max emitted:" + maxEmitted + "total query count = " + querycount);

		dataObjects = null;
		queries = null;
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
		System.out.println("Local index size =" + indexMemorySize / 1024 / 1024 + " MB");

		System.out.println("Query register Time per query (nanos)= " + queryRegisterationduration / queries.size());
		System.out.println("Total query evalautors= " + queryTasks);

		stopwatch = Stopwatch.createStarted();
		int querycount = 0;
		for (DataObject obj : dataObjects) {
			ArrayList<List<Query>> result = localIndex.getReleventSpatialKeywordRangeQueries(obj, false);
			if (result != null)
				querycount += result.get(0).size();
		}
		stopwatch.stop();
		dataProcessingDuration = stopwatch.elapsed(TimeUnit.NANOSECONDS);
		Integer maxData = 0, maxDataIndex = 0, maxQuery = 0, maxQueryIndex = 0, maxEmitted = 0, maxEmittedIndex = 0;
		Integer totalEmiitedCount = 0;

		System.out.println(" Local:" + localIndexType.name() + " DataProcessing Time per object (nano)= " + dataProcessingDuration / dataObjects.size() + " with qulified tuples:" + totalEmiitedCount + " max emitted:" + maxEmitted
				+ "total query count = " + querycount);

		dataObjects = null;
		queries = null;

	}

	static String testLocalPyramid(ArrayList<DataObject> dataObjects, ArrayList<MinimalRangeQuery> queries, LocalHybridPyramidGridIndexOptimized localIndex, LocalIndexType localIndexType, boolean findSize, boolean verifyCorrectness)
			throws Exception {
		String metadata = "";
		String toReturn = "";
		Long dataProcessingDuration;
		Long queryRegisterationduration;
		LocalHybridPyramidGridIndexOptimized.totalVisited = 0;
		LocalHybridPyramidGridIndexOptimized.spatialOverlappingQuries = 0;
		int queryTasks = 0;
		double sumQueryKeywords = 0;
		double sumOfObjectsKeywords = 0;
		Stopwatch stopwatch = Stopwatch.createStarted();

		for (MinimalRangeQuery q : queries) {
			//						if(q.queryId==11508){
			//							System.out.println("");
			//						}
			localIndex.addContinousQuery(q);
			sumQueryKeywords += q.queryText.size();

		}

		stopwatch.stop();

		KeyWordTrieIndexMinimal trieIndex = new KeyWordTrieIndexMinimal();
		if (verifyCorrectness)
			for (MinimalRangeQuery q : queries) {
				trieIndex.insert(q.queryText, q);

			}

		queryRegisterationduration = stopwatch.elapsed(TimeUnit.NANOSECONDS);

		metadata += "\nQuery register Time per query (nanos)    = " + queryRegisterationduration / queries.size();
		metadata += "\nTotal query evalautors                   = " + queryTasks;
		metadata += "\nqueryInsertInvListNodeCounter            = " + LocalHybridPyramidGridIndexOptimized.queryInsertInvListNodeCounter;
		metadata += "\nqueryInsertTrieNodeCounter               = " + LocalHybridPyramidGridIndexOptimized.queryInsertTrieNodeCounter;
		metadata += "\nTotal node insertions                    = " + (LocalHybridPyramidGridIndexOptimized.queryInsertTrieNodeCounter + LocalHybridPyramidGridIndexOptimized.queryInsertInvListNodeCounter);
		metadata += "\ntotalQueryInsertionsIncludingReplications= " + LocalHybridPyramidGridIndexOptimized.totalQueryInsertionsIncludingReplications;
		metadata += "\nAverage query replications               = " + LocalHybridPyramidGridIndexOptimized.totalQueryInsertionsIncludingReplications / (double) queries.size();
		metadata += "\nnumberOfHashEntries                      = " + LocalHybridPyramidGridIndexOptimized.numberOfHashEntries;
		metadata += "\nnumberOfTrieNodes                        = " + LocalHybridPyramidGridIndexOptimized.numberOfTrieNodes;
		metadata += "\nAverage ranked inv list length           = " + localIndex.getAverageRankedInvListSize();
		metadata += "\nAverage query keywords size              = " + sumQueryKeywords / queries.size();

		stopwatch = Stopwatch.createStarted();
		int querycount = 0;
		for (int i = 0; i < 5; i++) {

			for (DataObject obj : dataObjects) {

				sumOfObjectsKeywords += obj.getObjectText().size();
				List<MinimalRangeQuery> result = localIndex.getReleventSpatialKeywordRangeQueries(obj, false);
				if (verifyCorrectness)
					if (result.size() > 0) {
						verifyCorrectness(trieIndex, obj, result, localIndex);
					}
				if (result != null && result.size() > 0) {
					querycount += result.size();
				}
			}
		}
		stopwatch.stop();
		dataProcessingDuration = stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.gc();
		System.gc();
		long queriesSize = 0;
		long indexMemorySize = 0;
		if (findSize) {
			queriesSize = ObjectSizeCalculator.getObjectSize(queries);
			System.out.println("Queries size =" + queriesSize / 1024 / 1024 + " MB");
			indexMemorySize = ObjectSizeCalculator.getObjectSize(localIndex) - queriesSize;
			System.out.println("Local index size =" + indexMemorySize / 1024 / 1024 + " MB");
			System.gc();
			System.gc();
		}

		Integer maxData = 0, maxDataIndex = 0, maxQuery = 0, maxQueryIndex = 0, maxEmitted = 0, maxEmittedIndex = 0;
		Integer totalEmiitedCount = 0;

		System.out.println(" Local:" + localIndexType.name() + " DataProcessing Time per object (nano)= " + (dataProcessingDuration / dataObjects.size() / 5) + " with qulified tuples:" + totalEmiitedCount + " max emitted:" + maxEmitted
				+ "total query count = " + querycount + "total visted  = " + LocalHybridPyramidGridIndexOptimized.totalVisited + "totalspatial overlapping  = " + LocalHybridPyramidGridIndexOptimized.spatialOverlappingQuries);

		metadata += "\nAverage object keywords        = " + sumOfObjectsKeywords / dataObjects.size() / 5;
		metadata += "\nobjectSearchInvListNodeCounter = " + LocalHybridPyramidGridIndexOptimized.objectSearchInvListNodeCounter / 5;
		metadata += "\nobjectSearchTrieNodeCounter    = " + LocalHybridPyramidGridIndexOptimized.objectSearchTrieNodeCounter / 5;
		metadata += "\nTotal search node access       = " + ((LocalHybridPyramidGridIndexOptimized.objectSearchTrieNodeCounter + LocalHybridPyramidGridIndexOptimized.objectSearchInvListNodeCounter)) / 5;
		metadata += "\nobjectSearchInvListHashAccess  = " + LocalHybridPyramidGridIndexOptimized.objectSearchInvListHashAccess / 5;
		metadata += "\nobjectSearchTrieHashAccess     = " + LocalHybridPyramidGridIndexOptimized.objectSearchTrieHashAccess / 5;
		metadata += "\nTotoal trie access             = " + LocalHybridPyramidGridIndexOptimized.totalTrieAccess / 5;
		metadata += "\nAverage operations per trie    = "
				+ (LocalHybridPyramidGridIndexOptimized.objectSearchTrieNodeCounter + LocalHybridPyramidGridIndexOptimized.objectSearchTrieHashAccess) / (LocalHybridPyramidGridIndexOptimized.totalTrieAccess + 1);
		metadata += "\nTotal hash aceesses            = " + ((LocalHybridPyramidGridIndexOptimized.objectSearchTrieHashAccess + LocalHybridPyramidGridIndexOptimized.objectSearchInvListHashAccess)) / 5;
		metadata += "\nTotal operations               = " + (((LocalHybridPyramidGridIndexOptimized.objectSearchTrieHashAccess + LocalHybridPyramidGridIndexOptimized.objectSearchInvListHashAccess)
				+ (LocalHybridPyramidGridIndexOptimized.objectSearchTrieNodeCounter + LocalHybridPyramidGridIndexOptimized.objectSearchInvListNodeCounter))) / 5;

		toReturn = toReturn + (queryRegisterationduration / queries.size()) + "," + (dataProcessingDuration / dataObjects.size() / 5) + "," + (indexMemorySize / 1024 / 1024) + "," + (queriesSize / 1024 / 1024) + "," + querycount + ","
				+ LocalHybridPyramidGridIndexOptimized.totalVisited + "," + LocalHybridPyramidGridIndexOptimized.spatialOverlappingQuries + "\n";//+ metadata + "\n";

		System.out.println(metadata);
		dataObjects = null;

		queries = null;
		return toReturn;
	}

	public static void verifyCorrectness(KeyWordTrieIndexMinimal trieIndex, DataObject obj, List<MinimalRangeQuery> result, LocalHybridPyramidGridIndexOptimized localIndex) {
		LinkedList<MinimalRangeQuery> trieResult = trieIndex.find(obj.getObjectText());
		int totalResult = 0;
		HashSet<MinimalRangeQuery> resultSet = new HashSet<MinimalRangeQuery>();
		HashSet<MinimalRangeQuery> trieResultHashSet = new HashSet<MinimalRangeQuery>();
		for (MinimalRangeQuery q : trieResult) {
			if (SpatialHelper.overlapsSpatially(obj.getLocation(), q.spatialRange) && TextHelpers.containsTextually(obj.getObjectText(), q.queryText)) {
				totalResult++;
				trieResultHashSet.add(q);
			}
		}
		for (MinimalRangeQuery q : result) {
			resultSet.add(q);
		}
		if (totalResult < result.size()) {
			System.out.println("Object:" + result.size() + ":" + obj.toString());
			for (MinimalRangeQuery q : result) {
				if (!trieResultHashSet.contains(q)) {
					System.err.println("Error extra query");

				}

				if (!SpatialHelper.overlapsSpatially(obj.getLocation(), q.spatialRange) || !TextHelpers.containsTextually(obj.getObjectText(), q.queryText)) {
					System.err.println("Error should repeat");

				}

			}
			localIndex.getReleventSpatialKeywordRangeQueries(obj, false);
			;

		} else if (totalResult > result.size()) {
			System.out.println("Object:" + result.size() + ":" + obj.toString());
			for (MinimalRangeQuery q : trieResultHashSet) {
				if (!resultSet.contains(q)) {
					if (q.queryId == 657746)
						System.out.println("missing");
					System.err.println("Error missing query" + q.toString());
					localIndex.getReleventSpatialKeywordRangeQueries(obj, true);
				}
			}
		}
	}

	public static void experiment1EstimatethePostingListThreashold() {
		String outputFile = "results/expansionthreshold/expansionthreshold.csv";
		String tweetsFile = "/media/D/googleDrive/walid research/datasets/twittersample/sampletweets.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/tweetsQueries.csv";
		//	String queriesFile = "/media/D/googleDrive/walid research/datasets/querykeywordssorted/POIQueries.csv";
		String queriesFile = "/media/D/datasets/tweetsForQueries.csv";

		Integer numberOfDataObjects = 100000;
		Integer numberOfQueries = 10000000;
		Integer numberOfKeywords = 3;

		TextualPredicate txtPredicate = TextualPredicate.CONTAINS;
		ArrayList<DataObject> dataObjects = readDataObjects(tweetsFile, numberOfDataObjects);
		TornadoExperimentsSequence.appendToFile(outputFile,
				"Number of queries," + numberOfQueries + ",Number of objects," + numberOfDataObjects + ",Number of keywords," + numberOfKeywords + ",Max grid granualitry," + finegGridGran + "text perdicate," + txtPredicate.toString());
		System.out.println("Done reading data");
		TornadoExperimentsSequence.appendToFile(outputFile, "Spatial range, query insert time, object time, index size");
		ArrayList<MinimalRangeQuery> queries = readMinimalQueries(queriesFile, numberOfQueries, txtPredicate, 0, numberOfKeywords, false, false);
		System.out.println("Done reading queries");
		//		long queriesSize = ObjectSizeCalculator.getObjectSize(queries);
		//		System.out.println("Queries size =" + queriesSize / 1024 / 1024 + " MB");

		System.out.println("********************************************************");

		for (MinimalRangeQuery q : queries) {
			q.setSpatialRange(new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.xMaxRange)));
			//	System.out.println(q.toString());
		}

		System.out.println("Done reading queries");

		System.out.println("Testing the trie only, the number of queries is " + queries.size());
		System.gc();
		System.gc();

		ArrayList<Double> expecutionResults = new ArrayList<Double>();
		testTrie(dataObjects, queries, expecutionResults);
		String result = "averageNumberOfKeywordsInAnObject," + expecutionResults.get(0) + "\n";
		result = result + "maxNumberOfKeywordsInAnObject," + expecutionResults.get(1) + "\n";
		result = result + "avgNumberOfKeywordsInAnQuery," + expecutionResults.get(2) + "\n";
		result = result + "maxNumberOfKeywordsInAnQuery," + expecutionResults.get(3) + "\n";
		result = result + "averageNumberOfTrieSearchOpertaions," + expecutionResults.get(4) + "\n";
		result = result + "maxNumberOfTrieSearchOpertaions," + expecutionResults.get(5) + "\n";
		result = result + "avgexpansionThreasholdEstimate," + expecutionResults.get(4) / (expecutionResults.get(0) + expecutionResults.get(2) - 1) + "\n";

		System.out.println(result);
		TornadoExperimentsSequence.appendToFile(outputFile, result);
		System.gc();
		System.gc();

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
		for (int i = 0; i < 1; i++) {
			for (DataObject obj : dataObjects) {
				Integer taskId = globalIndex.getTaskIDsContainingPoint(obj.getLocation());
				dataPoints[taskId]++;
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
		Integer maxData = 0, maxDataIndex = 0, minData = Integer.MAX_VALUE, minDataIndex = 0, maxQuery = 0, maxQueryIndex = 0, maxEmitted = 0, maxEmittedIndex = 0;
		Integer totalEmiitedCount = 0;

		System.out.println(" Global:" + globalIndexType.name() + " Local:" + localIndexType.name() + " DataProcessing Time per object (nano)= " + dataProcessingDuration / dataObjects.size() / 100 + " with qulified tuples:"
				+ totalEmiitedCount + " max emitted:" + maxEmitted + " max emitted index:" + maxEmittedIndex + "Send tuples:" + sendTuples);

		for (int i = 0; i < numberOfEvaluators; i++) {
			//System.out.println(dataPoints[i]);
			if (dataPoints[i] > maxData) {
				maxData = dataPoints[i];
				maxDataIndex = i;

			}
			if (dataPoints[i] < minData) {
				minData = dataPoints[i];
				minDataIndex = i;

			}
			if (queriesCount[i] > maxQuery) {
				maxQuery = queriesCount[i];
				maxQueryIndex = i;
			}
		}

		System.out.println("  Max data =" + maxData + "  Min data =" + minData + ",Max data index=" + maxDataIndex + ",Max query =" + maxQuery + ",Max query index=" + maxQueryIndex);

		dataObjects = null;
		queries = null;

	}

	public static double stdev(Double a[], int n) {

		if (n == 0)
			return 0.0;
		double sum = 0;
		double sq_sum = 0;
		for (int i = 0; i < n; ++i) {
			sum += a[i];
			sq_sum += a[i] * a[i];
		}
		double mean = sum / n;
		double variance = sq_sum / n - mean * mean;
		return Math.sqrt(variance);
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
				obj.hashedText = new HashSet<>(obj.getObjectText());
				//	obj.setObjectText(null);
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
			Query query;
			int i = 0;
			while ((line = br.readLine()) != null && i < numberOfqueries) {
				if ("".equals(line))
					continue;
				query = queriesSpout.buildQuery(line, "querySrc", numberOfKeywords, "Tweets", null, null, QueryType.queryTextualRange, spatialRange, textualPredicate, null, null);
				if (query == null)
					continue;
				allQueiries.add(query);
				i++;

			}
			br.close();
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return allQueiries;
	}

	static ArrayList<MinimalRangeQuery> readMinimalQueries(String fileName, int numberOfqueries, TextualPredicate textualPredicate, double spatialRange, int numberOfKeywords, boolean randomSpatialRange, boolean radomNumberOfKeywords) {
		ArrayList<MinimalRangeQuery> allQueiries = new ArrayList<MinimalRangeQuery>();
		QueriesFileSystemSpout queriesSpout = new QueriesFileSystemSpout(null, 0);
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line;
			Query query;
			MinimalRangeQuery minimalRangeQuery;
			Double queryspatialRange = spatialRange;
			int querynumberOfKeywords = numberOfKeywords;
			int i = 0;
			randomGenerator = new RandomGenerator(0);
			while ((line = br.readLine()) != null && i < numberOfqueries) {
				if (randomSpatialRange)
					queryspatialRange = randomGenerator.nextDouble(0, spatialRange);
				if (radomNumberOfKeywords)
					querynumberOfKeywords = randomGenerator.nextInt(numberOfKeywords);
				if ("".equals(line))
					continue;
				query = queriesSpout.buildQuery(line, "querySrc", querynumberOfKeywords, "Tweets", null, null, QueryType.queryTextualRange, queryspatialRange, textualPredicate, null, null);
				if (query == null)
					continue;
				minimalRangeQuery = new MinimalRangeQuery();
				minimalRangeQuery.queryId = i;
				minimalRangeQuery.textualPredicate = textualPredicate;
				minimalRangeQuery.spatialRange = query.getSpatialRange();
				minimalRangeQuery.queryText = query.getQueryText();
				minimalRangeQuery.deleted = false;
				minimalRangeQuery.expireTime = Integer.MAX_VALUE;
				allQueiries.add(minimalRangeQuery);

				i++;

			}
			br.close();
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return allQueiries;
	}

	static ArrayList<Query> readRandomQueries(String fileName, int numberOfqueries, TextualPredicate textualPredicate, double spatialRange, int numberOfKeywords) {
		ArrayList<Query> allQueiries = new ArrayList<Query>();
		QueriesFileSystemSpout queriesSpout = new QueriesFileSystemSpout(null, 0);
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line;
			Query query;
			int i = 0;
			spatialRange = randomGenerator.nextDouble(0, spatialRange);
			numberOfKeywords = randomGenerator.nextInt(numberOfKeywords);
			while ((line = br.readLine()) != null && i < numberOfqueries) {
				if ("".equals(line))
					continue;
				query = queriesSpout.buildQuery(line, "querySrc", numberOfKeywords, "Tweets", null, null, QueryType.queryTextualRange, spatialRange, textualPredicate, null, null);
				if (query == null)
					continue;
				allQueiries.add(query);
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
			((KNNQuery) q).setFocalPoint(new Point(xCoord, yCoord));
			((KNNQuery) q).setTextualPredicate(null);
			((KNNQuery) q).setQueryText(queryText1);
			((KNNQuery) q).setK(5);
		} else if (queryType.equals(QueryType.queryTextualSpatialJoin)) {
			((JoinQuery) q).setSpatialRange(new Rectangle(new Point(xCoord, yCoord), new Point(xCoord + spatialRangeVal, yCoord + spatialRangeVal)));
			((JoinQuery) q).setTextualPredicate(null);
			((JoinQuery) q).setTextualPredicate2(null);
			((JoinQuery) q).setQueryText(queryText1);
			((JoinQuery) q).setQueryText(queryText2);
			((JoinQuery) q).setDistance(0.0);
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

	public static DataObject parseTweetToDataObject(String tweet) {
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
			((KNNQuery) q).setFocalPoint(new Point(xCoord, yCoord));
			((KNNQuery) q).setTextualPredicate(textualPredicate1);
			((KNNQuery) q).setQueryText(queryText1);
			((KNNQuery) q).setK(k);
		} else if (queryType.equals(QueryType.queryTextualSpatialJoin)) {
			((JoinQuery) q).setSpatialRange(new Rectangle(new Point(xCoord, yCoord), new Point(xCoord + spatialRangeVal, yCoord + spatialRangeVal)));
			((JoinQuery) q).setTextualPredicate(textualPredicate1);
			((JoinQuery) q).setTextualPredicate2(textualPredicate2);
			((JoinQuery) q).setQueryText(queryText1);
			((JoinQuery) q).setQueryText(queryText2);
			((JoinQuery) q).setDistance(distance);
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
			((KNNQuery) q).setFocalPoint(new Point(xCoord, yCoord));
			((KNNQuery) q).setTextualPredicate(textualPredicate1);
			((KNNQuery) q).setQueryText(queryText1);
			((KNNQuery) q).setK(k);
		} else if (queryType.equals(QueryType.queryTextualSpatialJoin)) {
			((JoinQuery) q).setSpatialRange(new Rectangle(new Point(xCoord, yCoord), new Point(xCoord + spatialRangeVal, yCoord + spatialRangeVal)));
			((JoinQuery) q).setTextualPredicate(textualPredicate1);
			((JoinQuery) q).setTextualPredicate2(textualPredicate2);
			((JoinQuery) q).setQueryText(queryText1);
			((JoinQuery) q).setQueryText(queryText2);
			((JoinQuery) q).setDistance(distance);
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

class QueryRankAwareStatsComparator implements Comparator<Entry<String, KeywordFrequencyStats>> {
	@Override
	public int compare(Entry<String, KeywordFrequencyStats> e1, Entry<String, KeywordFrequencyStats> e2) {
		int val1 = 0, val2 = 0;

		val1 = e1.getValue().queryCount;

		val2 = e2.getValue().queryCount;
		if (val1 < val2) {
			return 1;
		} else if (val1 == val2)
			return 0;
		else {
			return -1;
		}
	}
}

class ObjectCountCompartor implements Comparator<Entry<String, KeywordFrequencyStats>> {
	@Override
	public int compare(Entry<String, KeywordFrequencyStats> e1, Entry<String, KeywordFrequencyStats> e2) {
		int val1 = 0, val2 = 0;

		val1 = e1.getValue().objectVisitCount;

		val2 = e2.getValue().objectVisitCount;
		if (val1 < val2) {
			return 1;
		} else if (val1 == val2)
			return 0;
		else {
			return -1;
		}
	}
}

class RecencyScoreCompartor implements Comparator<Entry<String, KeywordFrequencyStats>> {
	@Override
	public int compare(Entry<String, KeywordFrequencyStats> e1, Entry<String, KeywordFrequencyStats> e2) {
		int val1 = 0, val2 = 0;

		val1 = e1.getValue().objectVisitCount * e1.getValue().queryCount;

		val2 = e2.getValue().objectVisitCount * e2.getValue().queryCount;
		if (val1 < val2) {
			return 1;
		} else if (val1 == val2)
			return 0;
		else {
			return -1;
		}
	}
}
