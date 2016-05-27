package edu.purdue.cs.tornado.experimental.baseline;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import org.apache.storm.Config;

import edu.purdue.cs.tornado.SpatioTextualToplogyBuilder;
import edu.purdue.cs.tornado.SpatioTextualToplogySubmitter;
import edu.purdue.cs.tornado.experimental.DataAndQueriesSources;
import edu.purdue.cs.tornado.helper.KillTopology;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.performance.ClusterInformationExtractor;
import edu.purdue.cs.tornado.spouts.FileSpout;
import edu.purdue.cs.tornado.storage.POIHDFSSource;

public class BaseLineExperimentsSequenece {
	static String javaArgs = "-Xmx4g -Xms4g ";

	public static void main(String[] args) {


	}

	public static void testSpoutReplicationUnRelianble(String fileName) {

		String result = "";
		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 1, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 2, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 3, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 4, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 5, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 6, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 7, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 8, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 9, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);

	}

	public static void testSpoutReplicationRelianble(String fileName) {

		String result = "";
		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 1, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 2, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 3, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 4, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 5, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 6, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 7, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 8, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 9, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 10, 6000, 0, 5, 50.0, 12);
		appendToFile(fileName, result);

	}

	public static void testQueryCountUnReliable(String fileName) {
		appendToFile(fileName, "-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,");
		String result = "";
//		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 3, 100000, 1000, 3, 50.0, 13);
//		appendToFile(fileName, result);
//		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 5, 300000, 5000, 5, 50.0, 17);
//		appendToFile(fileName, result);
//		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 3, 120000, 10000, 5, 50.0, 15 );
//		appendToFile(fileName, result);
//		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 5, 300000, 50000, 5, 50.0, 17);
//		appendToFile(fileName, result);
//		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 3, 360000, 100000, 5, 50.0, 17);
//		appendToFile(fileName, result);
//		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16, 5, 300000, 500000, 5, 50.0, 17);
//		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 0, 16,1, 600000, 1000000, 5, 50.0, 25);
		appendToFile(fileName, result);
	}

	public static void testQueryCountReliable(String fileName) {
		appendToFile(fileName, "-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,");

		String result = "";
//		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 3, 360000, 1000, 5, 50.0, 17);
//		appendToFile(fileName, result);
//		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 3, 360000, 10000, 5, 50.0, 17);
//		appendToFile(fileName, result);
//		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 3, 360000, 100000, 5, 50.0, 17);
//		appendToFile(fileName, result);
		result = runBaseLineToplogySpatialRange(javaArgs, 10, 16, 1, 600000, 1000000, 5, 50.0, 25);
		appendToFile(fileName, result);
	}

	static void appendToFile(String fileName, String data) {
		BufferedWriter bw = null;

		try {
			// APPEND MODE SET HERE
			bw = new BufferedWriter(new FileWriter(fileName, true));
			bw.write(data);
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally { // always close the file
			if (bw != null)
				try {
					bw.close();
				} catch (IOException ioe2) {
					ioe2.printStackTrace();
				}
		}
	}

	public static String runBaseLineToplogySpatialRange(String javaArgs, Integer numberOfackers, Integer numberOfEvaluators, Integer spoutParallesim, Integer initialSpoutSleepDuration, Integer numberOfQueries, Integer numberOfQueryKeywords,
			Double spatialRange, Integer minutesToStats) {
		String toplogyName = "BaselineTornado";
		String toRetun = "Numberof ackers," + numberOfackers + ",numberOfEvaluators," + numberOfEvaluators + ",spoutParallesim," + spoutParallesim + ",initialSpoutSleepDuration," + initialSpoutSleepDuration + ",numberOfQueries,"
				+ numberOfQueries + ",spatialRange," + spatialRange + ",numberOfQueryKeywords," + numberOfQueryKeywords + ",";
		final Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(SpatioTextualConstants.CLUSTER_CONFIG_PROPERTIES_FILE));
		} catch (final IOException ioException) {
			ioException.printStackTrace();
		}

		SpatioTextualToplogyBuilder builder = new SpatioTextualToplogyBuilder();

		String tweetsSource = "Datasource";
		String POISource = "POI_Data";
		String querySource = "Querysource";

		//	DataAndQueriesSources.addHDFSTweetsSpout(tweetsSource, builder, properties, 100, 0,1000,200);
		//		builder.setSpout(tweetsSource, new DummyTweetGenerator(10),1);
		DataAndQueriesSources.addLFSTweetsSpout(tweetsSource, builder, properties, spoutParallesim, 0, initialSpoutSleepDuration, 1);
		DataAndQueriesSources.addRangeQueries(tweetsSource, querySource, builder, properties, 1, spatialRange, numberOfQueries, numberOfQueryKeywords, 0, 0, FileSpout.LFS);
		//	DataAndQueriesSources.addJoinQueries(tweetsSource,POISource, querySource, builder, properties, 1, 50.0, 1000, 5, 20.0, 0);

		HashMap<String, String> staticSourceConf = new HashMap<String, String>();
		staticSourceConf.put(POIHDFSSource.HDFS_POI_FOLDER_PATH, properties.getProperty(POIHDFSSource.HDFS_POI_FOLDER_PATH));
		staticSourceConf.put(POIHDFSSource.CORE_FILE_PATH, properties.getProperty("CORE_FILE_PATH"));
		builder.setBolt("BaseLineEvaluator", new BaselineEvaluator(), numberOfEvaluators).shuffleGrouping(tweetsSource).allGrouping(querySource).allGrouping("BaseLineEvaluator", "sharedData");

		Config conf = new Config();
		conf.setDebug(false);
		String nimbusHost = properties.getProperty(SpatioTextualConstants.NIMBUS_HOST);
		Integer nimbusPort = Integer.parseInt(properties.getProperty(SpatioTextualConstants.NIMBUS_THRIFT_PORT).trim());
		conf.setNumAckers(numberOfackers);
		String submitType = properties.getProperty(SpatioTextualConstants.stormSubmitType);
		conf.put(Config.JAVA_LIBRARY_PATH, "/home/tornadojars/:/usr/local/lib:/opt/local/lib:/usr/lib");
		//			conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS, "-XX:+UseConcMarkSweepGC   -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:+PrintGCDetails -verbose:gc -Xloggc:/home/apache-storm-0.10.0/logs/gc-storm-worker-%ID%-"
		//					+ (new Date()).getTime() + ".log -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:GCLogFileSize=50M -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintTenuringDistribution -XX:+PrintGCApplicationStoppedTime -XX:HeapDumpPath=/home/apache-storm-0.10.0/logs/heapdump ");
		//	conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS, "-XX:+PrintGCDetails -verbose:gc -Xloggc:/home/apache-storm-0.10.0/logs/gc-storm-worker-%ID%-"+(new Date()).getTime()+".log -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:GCLogFileSize=10M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/apache-storm-0.10.0/logs/heapdump ");
		//	conf.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, new Integer(16384));
		//	conf.put(Config.TOPOLOGY_EXECUTOR_SEND_BUFFER_SIZE, new Integer(16384));
		//	conf.put(Config.TOPOLOGY_TRANSFER_BUFFER_SIZE, new Integer(2048));
		conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS, javaArgs);//-XX:+UseG1GC");
		conf.put(Config.STORM_ZOOKEEPER_SESSION_TIMEOUT, 300000);
		conf.put(Config.STORM_ZOOKEEPER_CONNECTION_TIMEOUT, 300000);

		conf.put(Config.NIMBUS_HOST, nimbusHost);
		conf.put(Config.NIMBUS_THRIFT_PORT, nimbusPort);
		conf.put(Config.STORM_ZOOKEEPER_PORT, Integer.parseInt(properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_PORT)));
		ArrayList<String> zookeeperServers = new ArrayList(Arrays.asList(properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_SERVERS).split(",")));
		conf.put(Config.STORM_ZOOKEEPER_SERVERS, zookeeperServers);
		conf.setNumWorkers(Integer.parseInt(properties.getProperty(SpatioTextualConstants.STORM_NUMBER_OF_WORKERS).trim()));
		System.setProperty("storm.jar", properties.getProperty(SpatioTextualConstants.STORM_JAR_PATH));
		try {
			SpatioTextualToplogySubmitter.submitTopology(toplogyName, conf, builder.createTopology());
			String[] nimbusInfo = new String[2];
			nimbusInfo[0] = nimbusHost;
			nimbusInfo[1] = "" + nimbusPort;
			Thread.sleep(1000 * 60 * minutesToStats);
			//		Thread.sleep(1000 *  minutesToStats);
			toRetun += ClusterInformationExtractor.getStats(nimbusInfo);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				KillTopology.killToplogy(toplogyName, nimbusHost, nimbusPort);
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return toRetun;
	}
}