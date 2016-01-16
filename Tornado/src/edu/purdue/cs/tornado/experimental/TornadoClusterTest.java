package edu.purdue.cs.tornado.experimental;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import org.apache.thrift7.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.Config;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.NotAliveException;
import backtype.storm.metric.LoggingMetricsConsumer;
import edu.purdue.cs.tornado.SpatioTextualLocalCluster;
import edu.purdue.cs.tornado.SpatioTextualToplogyBuilder;
import edu.purdue.cs.tornado.SpatioTextualToplogySubmitter;
import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.performance.ClusterInformationExtractor;
import edu.purdue.cs.tornado.spouts.DummyTweetGenerator;
import edu.purdue.cs.tornado.spouts.FileSpout;
import edu.purdue.cs.tornado.storage.POIHDFSSource;
import edu.purdue.cs.tornado.storage.POILFSDataSource;

public class TornadoClusterTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TornadoClusterTest.class);

	public static void main(String[] args) throws NumberFormatException, Exception {
		final Properties properties = new Properties();
		try {
			LOGGER.info("******************************************************************");
			LOGGER.info("**********************Reading toplogy config******************");
			properties.load(new FileInputStream(SpatioTextualConstants.CLUSTER_CONFIG_PROPERTIES_FILE));
		} catch (final IOException ioException) {
			//Should not occur. If it does, we cant continue. So exiting the program!
			LOGGER.error(ioException.getMessage(), ioException);
			System.exit(1);
		}

		SpatioTextualToplogyBuilder builder = new SpatioTextualToplogyBuilder();
		String tweetsSource = "Tweets";
		String POISource = "POI_Data";
		String querySource = "querySource";
		String querySource2 = "querySource2";
		String querySource3 = "querySource3";

		//builder.setSpout(tweetsSource, new DummyTweetGenerator(5), Integer.parseInt(properties.getProperty("SPOUT_PARALLEISM").trim()));
		DataAndQueriesSources.addLFSTweetsSpout(tweetsSource, builder, properties, Integer.parseInt(properties.getProperty("SPOUT_PARALLEISM").trim()), 0,10000,1);
	//	builder.setSpout(tweetsSource, new DummyTweetGenerator(10), Integer.parseInt(properties.getProperty("SPOUT_PARALLEISM").trim()));
		DataAndQueriesSources.addRangeQueries(tweetsSource, querySource, builder, properties, 1, 50.0, 100000, 5, 0,100,FileSpout.LFS);
	//	DataAndQueriesSources.addJoinQueries(tweetsSource, POISource, querySource2, builder, properties, 1, 50.0, 1000, 5, 20.0, 0);
	//	DataAndQueriesSources.addTopKQueries(tweetsSource, querySource3, builder, properties, 1, 10, 1000, 5, 0);

		HashMap<String, String> staticSourceConf = new HashMap<String, String>();
//	staticSourceConf.put(POIHDFSSource.HDFS_POI_FOLDER_PATH, properties.getProperty(POIHDFSSource.HDFS_POI_FOLDER_PATH));
//		staticSourceConf.put(POIHDFSSource.CORE_FILE_PATH, properties.getProperty("CORE_FILE_PATH"));
		staticSourceConf.put(POILFSDataSource.POI_FOLDER_PATH, properties.getProperty("LFS_POI_FOLDER_PATH"));
		ArrayList<Partition> partitions = PartitionsHelper.readSerializedPartitions(properties.getProperty("PARTITIONS_PATH").trim());
		//builder.addSpatioTextualProcessor("tornado", Integer.parseInt(properties.getProperty("ROUTING_PARALLEISM").trim()), Integer.parseInt(properties.getProperty("EVALUATOR_PARALLEISM").trim()))
		//	builder.addDynamicSpatioTextualProcessor("tornado", Integer.parseInt(properties.getProperty("ROUTING_PARALLEISM").trim()), Integer.parseInt(properties.getProperty("EVALUATOR_PARALLEISM").trim()), null, null)
		builder.addSpatioTextualProcessor("tornado", Integer.parseInt(properties.getProperty("ROUTING_PARALLEISM").trim()), Integer.parseInt(properties.getProperty("EVALUATOR_PARALLEISM").trim()), partitions, GlobalIndexType.PARTITIONED,
				LocalIndexType.HYBRID_GRID).addVolatileSpatioTextualInput(tweetsSource)
		//			.addStaticDataSource(POISource, "edu.purdue.cs.tornado.storage.POIHDFSSource", staticSourceConf)
	//	.addStaticDataSource(POISource, "edu.purdue.cs.tornado.storage.POILFSDataSource", staticSourceConf)
					.addContinuousQuerySource(querySource)
		;

		String topologyName = "Tornado";
		Config conf = new Config();
		conf.setDebug(false);

		conf.setNumAckers(Integer.parseInt(properties.getProperty("STORM_NUMBER_OF_ACKERS").trim()));
		conf.put(Config.TOPOLOGY_DEBUG, false);

		conf.put(SpatioTextualConstants.discoDir, properties.getProperty(SpatioTextualConstants.discoDir));
		String nimbusHost = properties.getProperty(SpatioTextualConstants.NIMBUS_HOST);
		Integer nimbusPort = Integer.parseInt(properties.getProperty(SpatioTextualConstants.NIMBUS_THRIFT_PORT).trim());

		String submitType = properties.getProperty(SpatioTextualConstants.stormSubmitType);
		if (submitType == null || "".equals(submitType) || SpatioTextualConstants.localCluster.equals(submitType)) {
			SpatioTextualLocalCluster cluster = new SpatioTextualLocalCluster();
			cluster.submitTopology("Tornado", conf, builder.createTopology());
		} else {
			conf.registerMetricsConsumer(LoggingMetricsConsumer.class, 3);
			conf.put(Config.JAVA_LIBRARY_PATH, "/home/tornadojars/:/usr/local/lib:/opt/local/lib:/usr/lib");
			conf.put(Config.WORKER_CHILDOPTS, "-Xmx6g");
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

}
