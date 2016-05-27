package edu.purdue.cs.tornado.experimental;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.purdue.cs.tornado.SpatioTextualToplogyBuilder;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.spouts.FileSpout;
import edu.purdue.cs.tornado.spouts.QueriesFileSystemSpout;
import edu.purdue.cs.tornado.spouts.QueriesFileSystemSpoutHotSpots;
import edu.purdue.cs.tornado.spouts.TweetsFSSpout;
import edu.purdue.cs.tornado.spouts.TweetsFSSpoutHotSpots;
import edu.purdue.cs.tornado.spouts.TweetsFSSpoutSlow;

public class DataAndQueriesSources {
	public static void addHDFSTweetsSpout(String dataSourceName, SpatioTextualToplogyBuilder builder, Properties properties, Integer parrellism, Integer emitSleepDurationInNanoSecond, Integer initialSleepDurantion,
			Integer spoutReplication) {
		Map<String, Object> tweetsSpoutConf = new HashMap<String, Object>();
		tweetsSpoutConf.put(FileSpout.FILE_PATH, properties.getProperty("TWEETS_FILE_PATH"));
		tweetsSpoutConf.put(FileSpout.CORE_FILE_PATH, properties.getProperty("CORE_FILE_PATH"));
		tweetsSpoutConf.put(FileSpout.FILE_SYS_TYPE, FileSpout.HDFS);
		tweetsSpoutConf.put(FileSpout.EMIT_SLEEP_DURATION_NANOSEC, new Integer(emitSleepDurationInNanoSecond));
		builder.setSpout(dataSourceName, new TweetsFSSpout(tweetsSpoutConf, initialSleepDurantion, spoutReplication), parrellism);
	}

	public static void addLFSTweetsSpout(String dataSourceName, SpatioTextualToplogyBuilder builder, Properties properties, Integer parrellism, Integer emitSleepDurationInNanoSecond, Integer initialSleepDurantion,
			Integer spoutReplication) {
		Map<String, Object> tweetsSpoutConf = new HashMap<String, Object>();
		tweetsSpoutConf.put(FileSpout.FILE_PATH, properties.getProperty("LFS_TWEETS_FILE_PATH"));
		tweetsSpoutConf.put(FileSpout.FILE_SYS_TYPE, FileSpout.LFS);
		tweetsSpoutConf.put(FileSpout.EMIT_SLEEP_DURATION_NANOSEC, new Integer(emitSleepDurationInNanoSecond));
		if (emitSleepDurationInNanoSecond == 0) {
			builder.setSpout(dataSourceName, new TweetsFSSpout(tweetsSpoutConf, initialSleepDurantion, spoutReplication), parrellism);
		} else
			builder.setSpout(dataSourceName, new TweetsFSSpoutSlow(tweetsSpoutConf, initialSleepDurantion, spoutReplication), parrellism);

	}
	public static void addHotSpotLFSTweetsSpout(String dataSourceName, SpatioTextualToplogyBuilder builder, Properties properties, Integer parrellism, Integer emitSleepDurationInNanoSecond, Integer initialSleepDurantion,
			Integer spoutReplication,Double hotSpotRatio) {
		Map<String, Object> tweetsSpoutConf = new HashMap<String, Object>();
		tweetsSpoutConf.put(FileSpout.FILE_PATH, properties.getProperty("LFS_TWEETS_FILE_PATH"));
		tweetsSpoutConf.put(FileSpout.FILE_SYS_TYPE, FileSpout.LFS);
		tweetsSpoutConf.put(FileSpout.EMIT_SLEEP_DURATION_NANOSEC, new Integer(emitSleepDurationInNanoSecond));
		if (emitSleepDurationInNanoSecond == 0) {
			builder.setSpout(dataSourceName, new TweetsFSSpoutHotSpots(tweetsSpoutConf, initialSleepDurantion, spoutReplication,hotSpotRatio), parrellism);
		} else
			builder.setSpout(dataSourceName, new TweetsFSSpoutSlow(tweetsSpoutConf, initialSleepDurantion, spoutReplication), parrellism);

	}

	public static SpatioTextualToplogyBuilder addJoinQueries(String dataSourceName1, String dataSourceName2, String querySourceName, SpatioTextualToplogyBuilder builder, Properties properties, Integer parrellism, Double spatialRange,
			Integer queryCount, Integer queryKeywordCount, Double querySpatialDistance, Integer emitSleepDurationInNanoSecond, Integer initialSleepDuration, String fileSystem) {
		Map<String, Object> queriesSpoutConf = new HashMap<String, Object>();
		queriesSpoutConf.put(FileSpout.FILE_PATH, properties.getProperty("QUERIES_FILE_PATH"));
		queriesSpoutConf.put(FileSpout.FILE_SYS_TYPE, fileSystem);
		queriesSpoutConf.put(FileSpout.CORE_FILE_PATH, properties.getProperty("CORE_FILE_PATH"));
		queriesSpoutConf.put(QueriesFileSystemSpout.SPATIAL_RANGE, spatialRange);
		queriesSpoutConf.put(QueriesFileSystemSpout.TOTAL_QUERY_COUNT, queryCount);
		queriesSpoutConf.put(QueriesFileSystemSpout.KEYWORD_COUNT, queryKeywordCount);
		queriesSpoutConf.put(SpatioTextualConstants.dataSrc, dataSourceName1);
		queriesSpoutConf.put(SpatioTextualConstants.dataSrc2, dataSourceName2);
		queriesSpoutConf.put(SpatioTextualConstants.queryDistance, querySpatialDistance);
		queriesSpoutConf.put(SpatioTextualConstants.queryTypeField, SpatioTextualConstants.queryTextualSpatialJoin);
		queriesSpoutConf.put(SpatioTextualConstants.textualPredicate, TextualPredicate.OVERlAPS);
		queriesSpoutConf.put(SpatioTextualConstants.textualPredicate2, TextualPredicate.OVERlAPS);
		queriesSpoutConf.put(FileSpout.EMIT_SLEEP_DURATION_NANOSEC, emitSleepDurationInNanoSecond);
		builder.setSpout(querySourceName, new QueriesFileSystemSpout(queriesSpoutConf, initialSleepDuration), parrellism);
		return builder;
	}

	public static SpatioTextualToplogyBuilder addRangeQueries(String dataSourceName, String querySourceName, SpatioTextualToplogyBuilder builder, Properties properties, Integer parrellism, Double spatialRange, Integer queryCount,
			Integer queryKeywordCount, Integer emitSleepDurationInNanoSecond, Integer initialSleepDuration, String fileSystem) {
		Map<String, Object> queriesSpoutConf = new HashMap<String, Object>();
		queriesSpoutConf.put(FileSpout.FILE_PATH, properties.getProperty("QUERIES_FILE_PATH"));
		queriesSpoutConf.put(FileSpout.FILE_SYS_TYPE, fileSystem);
		queriesSpoutConf.put(FileSpout.CORE_FILE_PATH, properties.getProperty("CORE_FILE_PATH"));
		queriesSpoutConf.put(QueriesFileSystemSpout.SPATIAL_RANGE, spatialRange);
		queriesSpoutConf.put(QueriesFileSystemSpout.TOTAL_QUERY_COUNT, queryCount);
		queriesSpoutConf.put(QueriesFileSystemSpout.KEYWORD_COUNT, queryKeywordCount);
		queriesSpoutConf.put(SpatioTextualConstants.dataSrc, dataSourceName);
		queriesSpoutConf.put(SpatioTextualConstants.queryTypeField, SpatioTextualConstants.queryTextualRange);
		queriesSpoutConf.put(SpatioTextualConstants.textualPredicate, TextualPredicate.OVERlAPS);
		queriesSpoutConf.put(FileSpout.EMIT_SLEEP_DURATION_NANOSEC, emitSleepDurationInNanoSecond);
		builder.setSpout(querySourceName, new QueriesFileSystemSpout(queriesSpoutConf, initialSleepDuration), parrellism);
		return builder;
	}
	public static SpatioTextualToplogyBuilder addHotSpotRangeQueries(String dataSourceName, String querySourceName, SpatioTextualToplogyBuilder builder, Properties properties, Integer parrellism, Double spatialRange, Integer queryCount,
			Integer queryKeywordCount, Integer emitSleepDurationInNanoSecond, Integer initialSleepDuration, String fileSystem,Double hotSpotRatio) {
		Map<String, Object> queriesSpoutConf = new HashMap<String, Object>();
		queriesSpoutConf.put(FileSpout.FILE_PATH, properties.getProperty("QUERIES_FILE_PATH"));
		queriesSpoutConf.put(FileSpout.FILE_SYS_TYPE, fileSystem);
		queriesSpoutConf.put(FileSpout.CORE_FILE_PATH, properties.getProperty("CORE_FILE_PATH"));
		queriesSpoutConf.put(QueriesFileSystemSpout.SPATIAL_RANGE, spatialRange);
		queriesSpoutConf.put(QueriesFileSystemSpout.TOTAL_QUERY_COUNT, queryCount);
		queriesSpoutConf.put(QueriesFileSystemSpout.KEYWORD_COUNT, queryKeywordCount);
		queriesSpoutConf.put(SpatioTextualConstants.dataSrc, dataSourceName);
		queriesSpoutConf.put(SpatioTextualConstants.queryTypeField, SpatioTextualConstants.queryTextualRange);
		queriesSpoutConf.put(SpatioTextualConstants.textualPredicate, TextualPredicate.OVERlAPS);
		queriesSpoutConf.put(FileSpout.EMIT_SLEEP_DURATION_NANOSEC, emitSleepDurationInNanoSecond);
		builder.setSpout(querySourceName, new QueriesFileSystemSpoutHotSpots(queriesSpoutConf, initialSleepDuration,hotSpotRatio), parrellism);
		return builder;
	}
	public static SpatioTextualToplogyBuilder addRangeQueries(String dataSourceName, String querySourceName, SpatioTextualToplogyBuilder builder, Properties properties, Integer parrellism, Double spatialRange, Integer queryCount,
			Integer queryKeywordCount, Integer emitSleepDurationInNanoSecond, Integer initialSleepDuration, String fileSystem, String queriesFilePath, TextualPredicate queryTextualPredicate) {
		if (queriesFilePath == null)
			return addRangeQueries(dataSourceName, querySourceName, builder, properties, parrellism, spatialRange, queryCount, queryKeywordCount, emitSleepDurationInNanoSecond, initialSleepDuration, fileSystem);
		Map<String, Object> queriesSpoutConf = new HashMap<String, Object>();
		queriesSpoutConf.put(FileSpout.FILE_PATH, queriesFilePath);
		queriesSpoutConf.put(FileSpout.FILE_SYS_TYPE, fileSystem);
		queriesSpoutConf.put(FileSpout.CORE_FILE_PATH, properties.getProperty("CORE_FILE_PATH"));
		queriesSpoutConf.put(QueriesFileSystemSpout.SPATIAL_RANGE, spatialRange);
		queriesSpoutConf.put(QueriesFileSystemSpout.TOTAL_QUERY_COUNT, queryCount);
		queriesSpoutConf.put(QueriesFileSystemSpout.KEYWORD_COUNT, queryKeywordCount);
		queriesSpoutConf.put(SpatioTextualConstants.dataSrc, dataSourceName);
		queriesSpoutConf.put(SpatioTextualConstants.queryTypeField, SpatioTextualConstants.queryTextualRange);
		if (queryTextualPredicate == null)
			queriesSpoutConf.put(SpatioTextualConstants.textualPredicate, TextualPredicate.OVERlAPS);
		else
			queriesSpoutConf.put(SpatioTextualConstants.textualPredicate, queryTextualPredicate);
		queriesSpoutConf.put(FileSpout.EMIT_SLEEP_DURATION_NANOSEC, emitSleepDurationInNanoSecond);
		builder.setSpout(querySourceName, new QueriesFileSystemSpout(queriesSpoutConf, initialSleepDuration), parrellism);
		return builder;
	}
	public static SpatioTextualToplogyBuilder addHotSpotRangeQueries(String dataSourceName, String querySourceName, SpatioTextualToplogyBuilder builder, Properties properties, Integer parrellism, Double spatialRange, Integer queryCount,
			Integer queryKeywordCount, Integer emitSleepDurationInNanoSecond, Integer initialSleepDuration, String fileSystem, String queriesFilePath, TextualPredicate queryTextualPredicate,Double hotSpotRatio) {
		if (queriesFilePath == null)
			return addHotSpotRangeQueries(dataSourceName, querySourceName, builder, properties, parrellism, spatialRange, queryCount, queryKeywordCount, emitSleepDurationInNanoSecond, initialSleepDuration, fileSystem,hotSpotRatio);
		Map<String, Object> queriesSpoutConf = new HashMap<String, Object>();
		queriesSpoutConf.put(FileSpout.FILE_PATH, queriesFilePath);
		queriesSpoutConf.put(FileSpout.FILE_SYS_TYPE, fileSystem);
		queriesSpoutConf.put(FileSpout.CORE_FILE_PATH, properties.getProperty("CORE_FILE_PATH"));
		queriesSpoutConf.put(QueriesFileSystemSpout.SPATIAL_RANGE, spatialRange);
		queriesSpoutConf.put(QueriesFileSystemSpout.TOTAL_QUERY_COUNT, queryCount);
		queriesSpoutConf.put(QueriesFileSystemSpout.KEYWORD_COUNT, queryKeywordCount);
		queriesSpoutConf.put(SpatioTextualConstants.dataSrc, dataSourceName);
		queriesSpoutConf.put(SpatioTextualConstants.queryTypeField, SpatioTextualConstants.queryTextualRange);
		if (queryTextualPredicate == null)
			queriesSpoutConf.put(SpatioTextualConstants.textualPredicate, TextualPredicate.OVERlAPS);
		else
			queriesSpoutConf.put(SpatioTextualConstants.textualPredicate, queryTextualPredicate);
		queriesSpoutConf.put(FileSpout.EMIT_SLEEP_DURATION_NANOSEC, emitSleepDurationInNanoSecond);
		builder.setSpout(querySourceName, new QueriesFileSystemSpoutHotSpots(queriesSpoutConf, initialSleepDuration,hotSpotRatio), parrellism);
		return builder;
	}

	public static SpatioTextualToplogyBuilder addTopKQueries(String dataSourceName, String querySourceName, SpatioTextualToplogyBuilder builder, Properties properties, Integer parrellism, Integer k, Integer queryCount,
			Integer queryKeywordCount, Integer emitSleepDurationInNanoSecond, Integer initialSleepDuration, String fileSystem) {
		Map<String, Object> queriesSpoutConf = new HashMap<String, Object>();
		queriesSpoutConf.put(FileSpout.FILE_PATH, properties.getProperty("QUERIES_FILE_PATH"));
		queriesSpoutConf.put(FileSpout.FILE_SYS_TYPE, fileSystem);
		queriesSpoutConf.put(FileSpout.CORE_FILE_PATH, properties.getProperty("CORE_FILE_PATH"));
		queriesSpoutConf.put(SpatioTextualConstants.kField, k);
		queriesSpoutConf.put(QueriesFileSystemSpout.TOTAL_QUERY_COUNT, queryCount);
		queriesSpoutConf.put(QueriesFileSystemSpout.KEYWORD_COUNT, queryKeywordCount);
		queriesSpoutConf.put(SpatioTextualConstants.dataSrc, dataSourceName);
		queriesSpoutConf.put(SpatioTextualConstants.queryTypeField, SpatioTextualConstants.queryTextualKNN);
		queriesSpoutConf.put(SpatioTextualConstants.textualPredicate, TextualPredicate.OVERlAPS);
		queriesSpoutConf.put(FileSpout.EMIT_SLEEP_DURATION_NANOSEC, emitSleepDurationInNanoSecond);
		builder.setSpout(querySourceName, new QueriesFileSystemSpout(queriesSpoutConf, initialSleepDuration), parrellism);
		return builder;
	}
}
