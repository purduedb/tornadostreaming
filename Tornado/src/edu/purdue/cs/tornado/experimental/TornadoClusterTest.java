package edu.purdue.cs.tornado.experimental;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.storm.Config;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.metric.LoggingMetricsConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endgame.storm.metrics.statsd.StatsdMetricConsumer;

import edu.purdue.cs.tornado.SpatioTextualLocalCluster;
import edu.purdue.cs.tornado.SpatioTextualToplogyBuilder;
import edu.purdue.cs.tornado.SpatioTextualToplogySubmitter;
import edu.purdue.cs.tornado.experimental.baseline.BaselineEvaluator;
import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.performance.ClusterInformationExtractor;
import edu.purdue.cs.tornado.spouts.FileSpout;
import edu.purdue.cs.tornado.storage.POILFSDataSource;

public class TornadoClusterTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TornadoClusterTest.class);
	static String javaArgs = "-Xmx3g -Xms3g -Dcom.sun.management.jmxremote  -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -javaagent:/home/staticdata/CustomAgent%ID%.jar ";

	public static void main(String[] args) throws NumberFormatException, Exception {
	//testBaseline();
		testTornado();
	}
	//conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS, "-XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:+PrintGCDetails -verbose:gc -Xloggc:/home/apache-storm-0.10.0/logs/gc-storm-worker-%ID%-"+(new Date()).getTime()+".log -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:GCLogFileSize=10M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/apache-storm-0.10.0/logs/heapdump ");
	//builder.setSpout("Tweets", new SampleUSATweetGenerator(), 1);
	//builder.setSpout("MovingObjects", new BrinkhoffSpout(), 1);
	//builder.setSpout("movingobjects", new TestMovingObjectWithTextSpout(), 1);
	//builder.setSpout("TextualKNNQueryGenerator",new TestTextualKNNQueryGenerator("movingobjects"), 1);

	//addRangeQueries("Tweets","TextualRangeQueryGenerator", builder , properties,1);
	//builder.setSpout("TextualRangeQueryGenerator", new TestTextualRangeQueryGenerator("Tweets"));
	//builder.setSpout("TextualRangeQueryGenerator", new KafkaSpout());

	//		builder.setSpout("TextualDistanceJoinQueryGenerator",
	//				new TestSpatialJoinQueryGenerator("OpenStreetMap",
	//						"TweetsGenerator"), 1);

	//		builder.setSpout("TextualDistanceJoinQueryGenerator",
	//				new TestSpatialJoinQueryGenerator("TweetsGenerator","OpenStreetMap"
	//						), 1);
	//builder.addStaticSpatioTextualProcessor("spatiotextualcomponent1", 10 * 10)
	//staticSourceConf.put(TestPOIsStaticDataSource.POIS_PATH, properties.getProperty(TestPOIsStaticDataSource.POIS_PATH));

	//		conf.put(SpatioTextualConstants.kafkaZookeeper, properties.getProperty(SpatioTextualConstants.kafkaZookeeper));
	//		conf.put(SpatioTextualConstants.kafkaConsumerGroup, properties.getProperty(SpatioTextualConstants.kafkaConsumerGroup));
	//		conf.put(SpatioTextualConstants.kafkaConsumerTopic, properties.getProperty(SpatioTextualConstants.kafkaConsumerTopic));
	//		conf.put(SpatioTextualConstants.kafkaProducerTopic, properties.getProperty(SpatioTextualConstants.kafkaProducerTopic));
	//		conf.put(SpatioTextualConstants.kafkaBootstrapServerConfig, properties.getProperty(SpatioTextualConstants.kafkaBootstrapServerConfig));
	//			conf.put(FileSpout.FILE_PATH, properties.getProperty("TWEETS_FILE_PATH"));
	//			conf.put(FileSpout.CORE_FILE_PATH, properties.getProperty(FileSpout.CORE_FILE_PATH));
	//	.addCurrentSpatioTextualInput("MovingObjects")
	//.addCleanVolatileSpatioTextualInput("Tweets")
	//				.addCurrentSpatioTextualInput("movingobjects")
	//		.addContinuousQuerySource("TextualRangeQueryGenerator")
	//.addContinuousQuerySource("TextualRangeQueryGenerator")
	//.addStaticDataSource("POI_Data", "edu.purdue.cs.tornado.test.TestPOIsStaticDataSource", staticSourceConf)
	//	.addCurrentSpatioTextualInput("MovingObjects")
	//.addCleanVolatileSpatioTextualInput("Tweets")
	//				.addCurrentSpatioTextualInput("movingobjects")
	//		.addContinuousQuerySource("TextualRangeQueryGenerator")
	//.addContinuousQuerySource("TextualRangeQueryGenerator")
	//.addStaticDataSource("POI_Data", "edu.purdue.cs.tornado.test.TestPOIsStaticDataSource", staticSourceConf)
	//	.addCurrentSpatioTextualInput("MovingObjects")
	//.addCleanVolatileSpatioTextualInput("Tweets")
	//				.addCurrentSpatioTextualInput("movingobjects")
	//		.addContinuousQuerySource("TextualRangeQueryGenerator")
	//.addContinuousQuerySource("TextualRangeQueryGenerator")
	//.addStaticDataSource("POI_Data", "edu.purdue.cs.tornado.test.TestPOIsStaticDataSource", staticSourceConf)
	static void testBaseline() throws NumberFormatException, Exception{
		final Properties properties = new Properties();
		try {
			LOGGER.info("******************************************************************");
			LOGGER.info("**********************Reading toplogy config******************");
			properties.load(new FileInputStream(SpatioTextualConstants.CONFIG_PROPERTIES_FILE));
		} catch (final IOException ioException) {
			//Should not occur. If it does, we cant continue. So exiting the program!
			LOGGER.error(ioException.getMessage(), ioException);
			System.exit(1);
		}

		SpatioTextualToplogyBuilder builder = new SpatioTextualToplogyBuilder();
		String tweetsSource = "Tweets";
		String querySource  = "querySource";


	

		//DataAndQueriesSources.addLFSTweetsSpout(tweetsSource, builder, properties, Integer.parseInt(properties.getProperty("SPOUT_PARALLEISM").trim()), 0, 0,1);
//		DataAndQueriesSources.addHotSpotLFSTweetsSpout(tweetsSource, builder, properties, Integer.parseInt(properties.getProperty("SPOUT_PARALLEISM").trim()), 0, 0,1);
//		DataAndQueriesSources.addHotSpotRangeQueries(tweetsSource, querySource, builder, properties, 1, 100.0, 1000, 5, 0, 0, FileSpout.LFS);

		builder.setBolt("BaseLineEvaluator", new BaselineEvaluator(), 15).shuffleGrouping(tweetsSource).allGrouping(querySource);//.allGrouping("BaseLineEvaluator", "sharedData");

		String topologyName = "Tornado";
		Config conf = new Config();
		conf.setDebug(false);

		conf.setNumAckers(Integer.parseInt(properties.getProperty("STORM_NUMBER_OF_ACKERS").trim()));
		conf.put(Config.TOPOLOGY_DEBUG, false);

		conf.put(SpatioTextualConstants.discoDir, properties.getProperty(SpatioTextualConstants.discoDir));
		String nimbusHost = properties.getProperty(SpatioTextualConstants.NIMBUS_HOST);
		Integer nimbusPort = Integer.parseInt(properties.getProperty(SpatioTextualConstants.NIMBUS_THRIFT_PORT).trim());
		//conf.put(Config.TOPOLOGY_TESTING_ALWAYS_TRY_SERIALIZE,true);
		String submitType = properties.getProperty(SpatioTextualConstants.stormSubmitType);
		if (submitType == null || "".equals(submitType) || SpatioTextualConstants.localCluster.equals(submitType)) {
			conf.registerMetricsConsumer(LoggingMetricsConsumer.class, 1);
			

			SpatioTextualLocalCluster cluster = new SpatioTextualLocalCluster();
			cluster.submitTopology("Tornado", conf, builder.createTopology());
		} else {
			conf.put(Config.JAVA_LIBRARY_PATH, "/home/tornadojars/:/usr/local/lib:/opt/local/lib:/usr/lib");
			conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS, javaArgs);//"-XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:+PrintGCDetails -verbose:gc -Xloggc:/home/apache-storm-0.10.0/logs/gc-storm-worker-%ID%-"
			//					+ (new Date()).getTime() + ".log -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:GCLogFileSize=10M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/apache-storm-0.10.0/logs/heapdump ");
			//			conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS,
			//					"-XX:+UseConcMarkSweepGC   -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:+PrintGCDetails -verbose:gc -Xloggc:/home/apache-storm-0.10.0/logs/gc-storm-worker-%ID%-" + (new Date())
			//							.getTime()
			//					+ ".log -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:GCLogFileSize=50M -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintTenuringDistribution -XX:+PrintGCApplicationStoppedTime -XX:HeapDumpPath=/home/apache-storm-0.10.0/logs/heapdump ");
			//conf.registerMetricsConsumer(LoggingMetricsConsumer.class, 1);
			//conf.registerMetricsConsumer(StatsdMetricConsumer.class, statsdConfig, 2);
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
				SpatioTextualToplogySubmitter.submitTopology(topologyName, conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				LOGGER.error(e.getMessage(), e);
				e.printStackTrace(System.err);
			} catch (InvalidTopologyException e) {
				LOGGER.error(e.getMessage(), e);
				e.printStackTrace(System.err);

			}
		}
		System.out.println("******************************************************************************************************");

		String[] nimbusInfo = new String[2];
		nimbusInfo[0] = nimbusHost;
		nimbusInfo[1] = "" + nimbusPort;

		Integer minutesToStats = Integer.parseInt(properties.getProperty("MINUTS_TO_STATS"));
		Thread.sleep(1000 * 60 * minutesToStats);
		ClusterInformationExtractor.main(nimbusInfo);
		//	KillTopology.killToplogy(topologyName, nimbusHost, nimbusPort);
		System.out.println("******************************************************************************************************");

		// Utils.sleep(10000);
		// cluster.killTopology("test");
		// cluster.shutdown();
	

 }
 static void testTornado() throws NumberFormatException, Exception{
		final Properties properties = new Properties();
		try {
			LOGGER.info("******************************************************************");
			LOGGER.info("**********************Reading toplogy config******************");
			properties.load(new FileInputStream(SpatioTextualConstants.CONFIG_PROPERTIES_FILE));
		} catch (final IOException ioException) {
			//Should not occur. If it does, we cant continue. So exiting the program!
			LOGGER.error(ioException.getMessage(), ioException);
			System.exit(1);
		}

		SpatioTextualToplogyBuilder builder = new SpatioTextualToplogyBuilder();
		String tweetsSource = "Tweets";
		String POISource    = "POI_Data";
		String querySource  = "querySource";
		String querySource2 = "querySource2";
		String querySource3 = "querySource3";

//		Map statsdConfig = new HashMap();
//		statsdConfig.put(StatsdMetricConsumer.STATSD_HOST, "128.211.254.131");
//		statsdConfig.put(StatsdMetricConsumer.STATSD_PORT, 8125);
//		statsdConfig.put(StatsdMetricConsumer.STATSD_PREFIX, "cluster.counter.");
		
		//*************************************************************************************
		//Adaptivity test
//		Double hotSpotRatio = .25;
//		DataAndQueriesSources.addHotSpotLFSTweetsSpout(tweetsSource, builder, properties, 1, 0, 0,1,hotSpotRatio);
//		DataAndQueriesSources.addHotSpotRangeQueries(tweetsSource, querySource, builder, properties, 1, 100.0, 100000, 5, 0, 0, FileSpout.LFS,hotSpotRatio);
//		HashMap<String, String> staticSourceConf = new HashMap<String, String>();
//		staticSourceConf.put(POILFSDataSource.POI_FOLDER_PATH, properties.getProperty("LFS_POI_FOLDER_PATH"));
//		ArrayList<Cell> partitions = PartitionsHelper.readSerializedPartitions("resources/partitions16_64_prio.ser");
//		builder.addDynamicSpatioTextualProcessor("tornado", 3, 17, 
//				partitions,GlobalIndexType.DYNAMIC_OPTIMIZED, LocalIndexType.HYBRID_GRID,64)
//		.addVolatileSpatioTextualInput(tweetsSource)
//				//			.addStaticDataSource(POISource, "edu.purdue.cs.tornado.storage.POIHDFSSource", staticSourceConf)
//				//	.addStaticDataSource(POISource, "edu.purdue.cs.tornado.storage.POILFSDataSource", staticSourceConf)
//				.addContinuousQuerySource(querySource);
		//*************************************************************************************
		
	
		DataAndQueriesSources.addLFSTweetsSpout(tweetsSource, builder, properties, 1, 0, 100000,1);
		DataAndQueriesSources.addRangeQueries(tweetsSource, querySource, builder, properties, 1, 100.0, 1000, 5, 0, 0, FileSpout.LFS);
		HashMap<String, String> staticSourceConf = new HashMap<String, String>();
		staticSourceConf.put(POILFSDataSource.POI_FOLDER_PATH, properties.getProperty("LFS_POI_FOLDER_PATH"));
		ArrayList<Cell> partitions = PartitionsHelper.readSerializedPartitions("resources/partitions16_1024_prio.ser");
		builder.addSpatioTextualProcessor("tornado", 3, 16, 
				partitions,GlobalIndexType.PARTITIONED, LocalIndexType.HYBRID_PYRAMID,1024)
		.addVolatileSpatioTextualInput(tweetsSource)
				//			.addStaticDataSource(POISource, "edu.purdue.cs.tornado.storage.POIHDFSSource", staticSourceConf)
				//	.addStaticDataSource(POISource, "edu.purdue.cs.tornado.storage.POILFSDataSource", staticSourceConf)
				.addContinuousQuerySource(querySource);
		
	//	DataAndQueriesSources.addLFSTweetsSpout(tweetsSource, builder, properties, Integer.parseInt(properties.getProperty("SPOUT_PARALLEISM").trim()), 0, 0,1);
	//	builder.setSpout(tweetsSource, new DummyTweetGenerator(5), Integer.parseInt(properties.getProperty("SPOUT_PARALLEISM").trim()));
		//DataAndQueriesSources.addLFSTweetsSpout(tweetsSource, builder, properties, Integer.parseInt(properties.getProperty("SPOUT_PARALLEISM").trim()), 3000, 10000, 1);
		//	builder.setSpout(tweetsSource, new DummyTweetGenerator(10), Integer.parseInt(properties.getProperty("SPOUT_PARALLEISM").trim()));
		//	DataAndQueriesSources.addJoinQueries(tweetsSource, POISource, querySource2, builder, properties, 1, 50.0, 1000, 5, 20.0, 0);
		//	DataAndQueriesSources.addTopKQueries(tweetsSource, querySource3, builder, properties, 1, 10, 1000, 5, 0);
		
		//	staticSourceConf.put(POIHDFSSource.HDFS_POI_FOLDER_PATH, properties.getProperty(POIHDFSSource.HDFS_POI_FOLDER_PATH));
		//		staticSourceConf.put(POIHDFSSource.CORE_FILE_PATH, properties.getProperty("CORE_FILE_PATH"));
		
	//	builder.addSpatioTextualProcessor("tornado", Integer.parseInt(properties.getProperty("ROUTING_PARALLEISM").trim()), Integer.parseInt(properties.getProperty("EVALUATOR_PARALLEISM").trim()))
		//	builder.addDynamicSpatioTextualProcessor("tornado", Integer.parseInt(properties.getProperty("ROUTING_PARALLEISM").trim()), Integer.parseInt(properties.getProperty("EVALUATOR_PARALLEISM").trim()), null, null)
//		builder.addSpatioTextualProcessor("tornado", Integer.parseInt(properties.getProperty("ROUTING_PARALLEISM").trim()), Integer.parseInt(properties.getProperty("EVALUATOR_PARALLEISM").trim()), 
//				partitions,GlobalIndexType.GRID, LocalIndexType.HYBRID_GRID,64).addVolatileSpatioTextualInput(tweetsSource);
				//			.addStaticDataSource(POISource, "edu.purdue.cs.tornado.storage.POIHDFSSource", staticSourceConf)
				//	.addStaticDataSource(POISource, "edu.purdue.cs.tornado.storage.POILFSDataSource", staticSourceConf)
				//.addContinuousQuerySource(querySource);

		
		String topologyName = "Tornado";
		Config conf = new Config();
		conf.setDebug(false);

		conf.setNumAckers(Integer.parseInt(properties.getProperty("STORM_NUMBER_OF_ACKERS").trim()));
		conf.put(Config.TOPOLOGY_DEBUG, false);

		conf.put(SpatioTextualConstants.discoDir, properties.getProperty(SpatioTextualConstants.discoDir));
		String nimbusHost = properties.getProperty(SpatioTextualConstants.NIMBUS_HOST);
		Integer nimbusPort = Integer.parseInt(properties.getProperty(SpatioTextualConstants.NIMBUS_THRIFT_PORT).trim());
	//	conf.put(Config.TOPOLOGY_TESTING_ALWAYS_TRY_SERIALIZE,true);
		String submitType = properties.getProperty(SpatioTextualConstants.stormSubmitType);
		if (submitType == null || "".equals(submitType) || SpatioTextualConstants.localCluster.equals(submitType)) {
		//	conf.registerMetricsConsumer(LoggingMetricsConsumer.class, 1);
		//	conf.registerMetricsConsumer(StatsdMetricConsumer.class, statsdConfig, 2);
			conf.put(Config.STORM_ZOOKEEPER_PORT, Integer.parseInt(properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_PORT)));
			ArrayList<String> zookeeperServers = new ArrayList(Arrays.asList(properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_SERVERS).split(",")));
			conf.put(Config.STORM_ZOOKEEPER_SERVERS, zookeeperServers);
			
			SpatioTextualLocalCluster cluster = new SpatioTextualLocalCluster();
			cluster.submitTopology("Tornado", conf, builder.createTopology());
		} else {
			conf.put(Config.JAVA_LIBRARY_PATH, "/home/tornadojars/:/usr/local/lib:/opt/local/lib:/usr/lib");
			conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS, javaArgs);//"-XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:+PrintGCDetails -verbose:gc -Xloggc:/home/apache-storm-0.10.0/logs/gc-storm-worker-%ID%-"
			//					+ (new Date()).getTime() + ".log -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:GCLogFileSize=10M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/apache-storm-0.10.0/logs/heapdump ");
			//			conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS,
			//					"-XX:+UseConcMarkSweepGC   -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:+PrintGCDetails -verbose:gc -Xloggc:/home/apache-storm-0.10.0/logs/gc-storm-worker-%ID%-" + (new Date())
			//							.getTime()
			//					+ ".log -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:GCLogFileSize=50M -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintTenuringDistribution -XX:+PrintGCApplicationStoppedTime -XX:HeapDumpPath=/home/apache-storm-0.10.0/logs/heapdump ");
			//conf.registerMetricsConsumer(LoggingMetricsConsumer.class, 1);
			//conf.registerMetricsConsumer(StatsdMetricConsumer.class, statsdConfig, 2);
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
				SpatioTextualToplogySubmitter.submitTopology(topologyName, conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				LOGGER.error(e.getMessage(), e);
				e.printStackTrace(System.err);
			} catch (InvalidTopologyException e) {
				LOGGER.error(e.getMessage(), e);
				e.printStackTrace(System.err);

			}
		}
		System.out.println("******************************************************************************************************");

		String[] nimbusInfo = new String[2];
		nimbusInfo[0] = nimbusHost;
		nimbusInfo[1] = "" + nimbusPort;

		Integer minutesToStats = Integer.parseInt(properties.getProperty("MINUTS_TO_STATS"));
		Thread.sleep(1000 * 60 * minutesToStats);
		ClusterInformationExtractor.main(nimbusInfo);
		//	KillTopology.killToplogy(topologyName, nimbusHost, nimbusPort);
		System.out.println("******************************************************************************************************");

		// Utils.sleep(10000);
		// cluster.killTopology("test");
		// cluster.shutdown();
	

 }
}
