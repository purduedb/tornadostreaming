
package edu.purdue.cs.tornado.experimental.baseline;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.thrift7.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.Config;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.NotAliveException;
import backtype.storm.metric.LoggingMetricsConsumer;
import backtype.storm.topology.TopologyBuilder;
import edu.purdue.cs.tornado.SpatioTextualLocalCluster;
import edu.purdue.cs.tornado.SpatioTextualToplogyBuilder;
import edu.purdue.cs.tornado.SpatioTextualToplogySubmitter;
import edu.purdue.cs.tornado.experimental.DataAndQueriesSources;
import edu.purdue.cs.tornado.experimental.TornadoClusterTest;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.performance.ClusterInformationExtractor;
import edu.purdue.cs.tornado.spouts.DummyTweetGenerator;
import edu.purdue.cs.tornado.spouts.FileSpout;
import edu.purdue.cs.tornado.spouts.QueriesFileSystemSpout;
import edu.purdue.cs.tornado.spouts.TweetsFSSpout;
import edu.purdue.cs.tornado.storage.POIHDFSSource;

public class BaselineTopology {
	private static final Logger LOGGER = LoggerFactory.getLogger(TornadoClusterTest.class);

	public static void main(String[] args) throws InterruptedException, NotAliveException, TException {
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

		String tweetsSource="Datasource";
		String POISource="POI_Data";
		String querySource= "Querysource";
		
	//	DataAndQueriesSources.addHDFSTweetsSpout(tweetsSource, builder, properties, 100, 0,1000,200);
		builder.setSpout(tweetsSource, new DummyTweetGenerator(10),40);
		DataAndQueriesSources.addRangeQueries(tweetsSource, querySource, builder, properties, 1, 50.0, 1000, 5, 0,0,FileSpout.LFS);
	//	DataAndQueriesSources.addJoinQueries(tweetsSource,POISource, querySource, builder, properties, 1, 50.0, 1000, 5, 20.0, 0);
		
		
		
		
		
		HashMap<String, String> staticSourceConf = new HashMap<String, String>();
		staticSourceConf.put(POIHDFSSource.HDFS_POI_FOLDER_PATH, properties.getProperty(POIHDFSSource.HDFS_POI_FOLDER_PATH));
		staticSourceConf.put(POIHDFSSource.CORE_FILE_PATH, properties.getProperty("CORE_FILE_PATH"));
		builder.setBolt("BaseLineEvaluator", new  BaselineEvaluator(), 64).shuffleGrouping(tweetsSource).allGrouping(querySource).allGrouping("BaseLineEvaluator","sharedData");
		
		String topologyName = "TornadoBaseline";
		Config conf = new Config();
		conf.setDebug(false);
		conf.setNumWorkers(4);
		conf.setNumAckers(0);
		conf.put(Config.TOPOLOGY_DEBUG, false);
		conf.put(POIHDFSSource.HDFS_POI_FOLDER_PATH, properties.getProperty(POIHDFSSource.HDFS_POI_FOLDER_PATH));
		conf.put(POIHDFSSource.CORE_FILE_PATH, properties.getProperty("CORE_FILE_PATH"));
		String nimbusHost = properties.getProperty(SpatioTextualConstants.NIMBUS_HOST);
		Integer nimbusPort = Integer.parseInt(properties.getProperty(SpatioTextualConstants.NIMBUS_THRIFT_PORT));

		String submitType = properties.getProperty(SpatioTextualConstants.stormSubmitType);
		if (submitType == null || "".equals(submitType) || SpatioTextualConstants.localCluster.equals(submitType)) {
			SpatioTextualLocalCluster cluster = new SpatioTextualLocalCluster();
			cluster.submitTopology("TornadoBaseline", conf, builder.createTopology());
		} else {
			conf.registerMetricsConsumer(LoggingMetricsConsumer.class, 2);
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
		Integer minutesToStats=Integer.parseInt(properties.getProperty("MINUTS_TO_STATS"));
		Thread.sleep(1000 * 60 * minutesToStats);
		ClusterInformationExtractor.main(nimbusInfo);
		//	KillTopology.killToplogy(topologyName, nimbusHost, nimbusPort);
		System.out.println("******************************************************************************************************");

		// Utils.sleep(10000);
		// cluster.killTopology("test");
		// cluster.shutdown();
		
	}

	

	
}
