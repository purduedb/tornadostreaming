package edu.purdue.cs.tornado.tutorial;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import org.apache.storm.Config;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;

import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.purdue.cs.tornado.SpatioTextualLocalCluster;
import edu.purdue.cs.tornado.SpatioTextualToplogyBuilder;
import edu.purdue.cs.tornado.SpatioTextualToplogySubmitter;
import edu.purdue.cs.tornado.examples.TweetCountBolt;
import edu.purdue.cs.tornado.experimental.DataAndQueriesSources;
import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.bolts.KafakaProducerBolt;
import edu.purdue.cs.tornado.evaluator.DynamicEvalautorBolt;
import edu.purdue.cs.tornado.evaluator.SpatioTextualEvaluatorBolt;
import edu.purdue.cs.tornado.spouts.AtlasParserSpout;
import edu.purdue.cs.tornado.spouts.FileSpout;
import edu.purdue.cs.tornado.spouts.KafkaSpout;
import edu.purdue.cs.tornado.spouts.TweetsFSSpout;
import edu.purdue.cs.tornado.storage.POILFSDataSource;



public class TornadoUITopology {
	private static final Logger LOGGER = LoggerFactory.getLogger(TornadoUITopology.class);
	static String javaArgs = "-Xmx3g -Xms3g";// -Dcom.sun.management.jmxremote  -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -javaagent:/home/staticdata/CustomAgent%ID%.jar ";
	//Set the variable names for later use
	static String tweetsSource = "Tweets";
	static String querySource  = "querySource";
	static String topologyName = "Tornado";
	public static void main(String[] args) throws InterruptedException{
		
		/* ------------- SETUP ------------- */
		
		//Load Properties from a specific file path (use CONFIG_PROPERTIES_FILE for local cluster)
		//final Properties properties = loadProperties(SpatioTextualConstants.CLUSTER_CONFIG_PROPERTIES_FILE);
		final Properties properties = loadProperties(SpatioTextualConstants.CONFIG_PROPERTIES_FILE);

		//Setting the static source paths 
		ArrayList<Cell> partitions = PartitionsHelper.readSerializedPartitions("resources/partitions16_1024_prio.ser");
				
		//Initialize our topology builder
		SpatioTextualToplogyBuilder builder = new SpatioTextualToplogyBuilder();
		
		//Initialize and set Config properties
		Config conf = new Config();
		conf.setDebug(true);
		
/* ------------- SPOUT AND BOLT CREATION ------------- */
		
		//TODO: What needs to be changed by the user? What if they want to add multiple global index types? More bolts? 

		//Make the dataSpout called tweetsSource, parallelism of 1 and replication of 0.
		addTweetSpout(tweetsSource, builder, properties, 1, 0, 50,1);

		//Make the query spout
		//addQuerySpout(tweetsSource, querySource, builder, properties, 10, 1000.0, 10, 3, 0, 0, FileSpout.LFS,properties.getProperty("LFS_TWEETS_FILE_PATH"),TextualPredicate.OVERlAPS);
		builder.setSpout(querySource, new KafkaSpout());

		//Set the GlobalIndex and SpatioTextual bolts
		addTornado(builder, partitions, GlobalIndexType.PARTITIONED, LocalIndexType.FAST);
		
		//builder.setBolt("kafkaOutputProducer", new KafakaProducerBolt()).shuffleGrouping("spatiotextualcomponent1",
		//		SpatioTextualConstants.Bolt_Output_STreamIDExtension);
		/* ------------- TOPOLOGY SUBMISSION ------------- */
		
		//Submitting the topology based on the proper submit type (change stormSubmitType in clusterconfig or config.properties to submit to local or cluster)
		String submitType = properties.getProperty(SpatioTextualConstants.stormSubmitType);
		if (submitType == null || "".equals(submitType) || SpatioTextualConstants.localCluster.equals(submitType)) {

//			conf.put(Config.STORM_ZOOKEEPER_PORT, Integer.parseInt(properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_PORT)));
//			ArrayList<String> zookeeperServers = new ArrayList(Arrays.asList(properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_SERVERS).split(",")));
//			conf.put(Config.STORM_ZOOKEEPER_SERVERS, zookeeperServers);
//			
		conf = new Config();
		conf.setDebug(true);
		conf.setNumWorkers(2);
		conf.setNumAckers(0);
		conf.put(Config.TOPOLOGY_DEBUG, false);

			conf.put(SpatioTextualConstants.kafkaZookeeper, properties.getProperty(SpatioTextualConstants.kafkaZookeeper));
			conf.put(SpatioTextualConstants.kafkaConsumerGroup, properties.getProperty(SpatioTextualConstants.kafkaConsumerGroup));
			conf.put(SpatioTextualConstants.kafkaConsumerTopic, properties.getProperty(SpatioTextualConstants.kafkaConsumerTopic));
			conf.put(SpatioTextualConstants.kafkaProducerTopic, properties.getProperty(SpatioTextualConstants.kafkaProducerTopic));
			conf.put(SpatioTextualConstants.kafkaBootstrapServerConfig, properties.getProperty(SpatioTextualConstants.kafkaBootstrapServerConfig));
			conf.put(SpatioTextualConstants.discoDir, properties.getProperty(SpatioTextualConstants.discoDir));
			//conf.put(SampleUSATweetGenerator.TWEETS_FILE_PATH, properties.getProperty(SampleUSATweetGenerator.TWEETS_FILE_PATH));
			
			submitType = properties.getProperty(SpatioTextualConstants.stormSubmitType);
			if (submitType == null || "".equals(submitType) || SpatioTextualConstants.localCluster.equals(submitType)) {
				SpatioTextualLocalCluster cluster = new SpatioTextualLocalCluster();
				cluster.submitTopology("test", conf, builder.createTopology());
			}else{
			    conf.put(Config.NIMBUS_HOST, properties.getProperty(SpatioTextualConstants.NIMBUS_HOST));
			    conf.put(Config.NIMBUS_THRIFT_PORT,properties.getProperty(SpatioTextualConstants.NIMBUS_THRIFT_PORT));
			    conf.put(Config.STORM_ZOOKEEPER_PORT,properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_PORT));
			    conf.put(Config.STORM_ZOOKEEPER_SERVERS,properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_SERVERS));
			    conf.setNumWorkers(Integer.parseInt(properties.getProperty(SpatioTextualConstants.STORM_NUMBER_OF_WORKERS)));
			    try {
					SpatioTextualToplogySubmitter.submitTopology("test", conf, builder.createTopology());
				} catch (AlreadyAliveException e) {
					LOGGER.error(e.getMessage(), e);
					e.printStackTrace(System.err);
				} catch (InvalidTopologyException e) {
					LOGGER.error(e.getMessage(), e);
					e.printStackTrace(System.err);
					
				} catch(AuthorizationException e) {
					LOGGER.error(e.getMessage(), e);
			    	e.printStackTrace(System.err);	
			    }
			}
		System.out.println("******************************************************************************************************");

		String[] nimbusInfo = new String[2];
		//nimbusInfo[0] = nimbusHost;
		//nimbusInfo[1] = "" + nimbusPort;

		Integer minutesToStats = Integer.parseInt(properties.getProperty("MINUTS_TO_STATS"));
		Thread.sleep(1000 * 60 * minutesToStats);
		Thread.sleep(1);
		//KillTopology.killToplogy(topologyName, nimbusHost, nimbusPort);
		System.out.println("******************************************************************************************************");
		
		}
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
		
		//builder.setBolt("tweetCount", new TweetCountBolt()).shuffleGrouping("tornado",SpatioTextualConstants.Bolt_Output_STreamIDExtension);
		builder.setBolt("kafkaOutputProducer", new KafakaProducerBolt());
	}
	
}