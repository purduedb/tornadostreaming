package edu.purdue.cs.tornado.examples;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import org.apache.storm.Config;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.InvalidTopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.purdue.cs.tornado.SpatioTextualLocalCluster;
import edu.purdue.cs.tornado.SpatioTextualToplogyBuilder;
import edu.purdue.cs.tornado.SpatioTextualToplogySubmitter;
import edu.purdue.cs.tornado.experimental.DataAndQueriesSources;
import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.spouts.FileSpout;
import edu.purdue.cs.tornado.storage.POILFSDataSource;

public class TornadoTweetCountExample {
	private static final Logger LOGGER = LoggerFactory.getLogger(TornadoTweetCountExample.class);
	static String javaArgs = "-Xmx3g -Xms3g -Dcom.sun.management.jmxremote  -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -javaagent:/home/staticdata/CustomAgent%ID%.jar ";
	//Set the variable names for later use
	static String tweetsSource = "Tweets";
	static String querySource  = "querySource";
	static String topologyName = "Tornado";
			
	public static void main(String[] args) throws NumberFormatException, Exception {
		testTornado();
	}
	
	static void testTornado() throws NumberFormatException, Exception{
		
		/* ------------- SETUP ------------- */
		
		//TODO: can we make a lot of these global? We just need to set up elsewhere which would make testTornado() cleaner.
		
		//Load Properties from a specific file path
		final Properties properties = loadProperties(SpatioTextualConstants.CONFIG_PROPERTIES_FILE);

		//Setting the static source paths 
		HashMap<String, String> staticSourceConf = new HashMap<String, String>();
		//staticSourceConf.put(POILFSDataSource.POI_FOLDER_PATH, properties.getProperty("LFS_POI_FOLDER_PATH"));
		ArrayList<Cell> partitions = PartitionsHelper.readSerializedPartitions("resources/partitions16_1024_prio.ser");
				
		//Initialize our topology builder
		SpatioTextualToplogyBuilder builder = new SpatioTextualToplogyBuilder();
		
		//Initialize and set Config properties
		Config conf = new Config();
		conf.setDebug(false);
		conf.setNumAckers(Integer.parseInt(properties.getProperty("STORM_NUMBER_OF_ACKERS").trim()));
		conf.put(Config.TOPOLOGY_DEBUG, false);
		
		//Setting the nimbus host and port paths
		String nimbusHost = properties.getProperty(SpatioTextualConstants.NIMBUS_HOST);
		Integer nimbusPort = Integer.parseInt(properties.getProperty(SpatioTextualConstants.NIMBUS_THRIFT_PORT).trim());
		
		
		/* ------------- SPOUT AND BOLT CREATION ------------- */
		
		//TODO: What needs to be changed by the user? What if they want to add multiple global index types? More bolts? 

		//Make the dataSpout called tweetsSource, parallelism of 1 and replication of 0.
		addTweetSpout(tweetsSource, builder, properties, 1, 0, 100,1);
		
		//Make the query spout
		addQuerySpout(tweetsSource, querySource, builder, properties, 10, 1000.0, 1000,3, 0, 0, FileSpout.LFS,properties.getProperty("LFS_TWEETS_FILE_PATH"),TextualPredicate.OVERlAPS);
		
		//Set the GlobalIndex and SpatioTextual bolts
		addTornado(builder, partitions, GlobalIndexType.PARTITIONED, LocalIndexType.HYBRID_GRID);
		
		
		/* ------------- TOPOLOGY SUBMISSION ------------- */
		
		//TODO: What needs to be changed by the user?
		
		//Submitting the topology based on the proper submit type (doesn't change)
		String submitType = properties.getProperty(SpatioTextualConstants.stormSubmitType);
		if (submitType == null || "".equals(submitType) || SpatioTextualConstants.localCluster.equals(submitType)) {

			conf.put(Config.STORM_ZOOKEEPER_PORT, Integer.parseInt(properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_PORT)));
			ArrayList<String> zookeeperServers = new ArrayList(Arrays.asList(properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_SERVERS).split(",")));
			conf.put(Config.STORM_ZOOKEEPER_SERVERS, zookeeperServers);
			
			SpatioTextualLocalCluster cluster = new SpatioTextualLocalCluster();
			cluster.submitTopology("Tornado", conf, builder.createTopology());
		} else {
			conf.put(Config.JAVA_LIBRARY_PATH, "/home/tornadojars/:/usr/local/lib:/opt/local/lib:/usr/lib");
			conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS, javaArgs);
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
		//KillTopology.killToplogy(topologyName, nimbusHost, nimbusPort);
		System.out.println("******************************************************************************************************");
	}
	
	/*
	 * Find and load the properties available in the given file path
	 * 
	 * @param filepath the location of properties to be loaded
	 * @return Properties object with the loaded properties at the specified file path
	 */
	static Properties loadProperties(String filepath) {
		//Create and load a new properties object
		final Properties properties = new Properties();
		try {
			LOGGER.info("******************************************************************");
			LOGGER.info("**********************Reading toplogy config******************");
			properties.load(new FileInputStream(filepath));
		} catch (final IOException ioException) {
			//Should not occur. If it does, we can't continue. So we're exiting the program!
			LOGGER.error(ioException.getMessage(), ioException);
			System.exit(1);
		}
		
		return properties;
	}
	
	/*
	 * Add a new tweetSpout with the parameters given
	 * 
	 * @param dataSourceName the data source name associated with the spout
	 * @param builder the SpatioTextualToplogyBuilder associated with the spout
	 * @param properties the Properties object that the spout will refer to
	 * @param parrellism the number of tasks that should be assigned to execute this spout
	 * @param emitSleepDurationInNanoSecond the amount of time in which the emit will sleep (in nanoseconds)
	 * @param initialSleepDuration the amount of time the spout will sleep before starting to emit
	 * @param spoutReplication the amount of replicates of this spout to be made
	 */
	static void addTweetSpout(String dataSourceName, SpatioTextualToplogyBuilder builder, Properties properties, Integer parrellism, 
			Integer emitSleepDurationInNanoSecond, Integer initialSleepDurantion, Integer spoutReplication) {
		
			DataAndQueriesSources.addLFSTweetsSpout(dataSourceName, builder, properties, parrellism, 
				emitSleepDurationInNanoSecond, initialSleepDurantion, spoutReplication);
	}
	
	/*
	 * Add a new querySpout with the parameters given
	 * 
	 * @param dataSourceName the data source name associated with the spout
	 * @param querySourceName the query source name associated with the spout
	 * @param builder the SpatioTextualToplogyBuilder associated with the spout
	 * @param properties the Properties object that the spout will refer to
	 * @param parrellism the number of tasks that should be assigned to execute this spout
	 * @param spatialRange
	 * @param queryCount
	 * @param queryKeywordCount
	 * @param emitSleepDurationInNanoSecond the amount of time in which the emit will sleep (in nanoseconds)
	 * @param initialSleepDuration the amount of time the spout will sleep before starting to emit
	 * @param fileSystem
	 * @param queriesFilePath
	 * @param queryTextualPredicate
	 */
	static void addQuerySpout(String dataSourceName, String querySourceName, SpatioTextualToplogyBuilder builder, Properties properties, Integer parrellism, Double spatialRange, Integer queryCount,
			Integer queryKeywordCount, Integer emitSleepDurationInNanoSecond, Integer initialSleepDuration, String fileSystem, String queriesFilePath, TextualPredicate queryTextualPredicate) {
		
			DataAndQueriesSources.addRangeQueries(dataSourceName, querySourceName, builder, properties, parrellism, spatialRange, queryCount,
				queryKeywordCount, emitSleepDurationInNanoSecond, initialSleepDuration, fileSystem, queriesFilePath, queryTextualPredicate);
	}
	
	/*
	 * Add a new querySpout with the parameters given
	 * 
	 * @param builder the SpatioTextualToplogyBuilder associated with the spout
	 * @param partitions 
	 * @param globalIndexType the GlobalIndexType that we choose to run the bolts on
	 * @param localIndexType the LocalIndexType that we choose to run the bolts on
	 */
	static void addTornado(SpatioTextualToplogyBuilder builder, ArrayList<Cell> partitions, GlobalIndexType globalIndexType, LocalIndexType localIndexType) {
		
		//TODO: in addSpatioTextualProcessor there lies a few TODO statements
		builder.addSpatioTextualProcessor("tornado", 3, 16, 
			partitions, globalIndexType, localIndexType,1024)
			.addVolatileSpatioTextualInput(tweetsSource)
			.addContinuousQuerySource(querySource);
	}
}
