package edu.purdue.cs.tornado.tutorial;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.storm.Config;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.InvalidTopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;

import edu.purdue.cs.tornado.SpatioTextualLocalCluster;
import edu.purdue.cs.tornado.SpatioTextualToplogyBuilder;
import edu.purdue.cs.tornado.SpatioTextualToplogySubmitter;
import edu.purdue.cs.tornado.bolts.KafakaProducerBolt;
import edu.purdue.cs.tornado.examples.TweetCountBolt;
import edu.purdue.cs.tornado.experimental.DataAndQueriesSources;
import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.messages.Query;
//import edu.purdue.cs.tornado.spouts.KafkaSpout;
import edu.purdue.cs.tornado.helper.JsonHelper;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.local.LocalIndexType;


public class TornadoUITopology {
	private static final Logger LOGGER = LoggerFactory.getLogger(TornadoUITopology.class);
	// Edit path files as needed
	private static final String PROJECT_DIR = System.getProperty("user.dir");	
	private static final String DATASOURCES_DIR = System.getProperty("user.dir") + "/datasources/";
	private static final String SAMPLE_TWEETS_FILE_PATH = DATASOURCES_DIR + "sample_tweets.csv";
	private static final String TWEETS_FILE_PATH = DATASOURCES_DIR + "twitterdata.csv";
	private static final String TEMP_FILE_PATH = DATASOURCES_DIR + "temp.csv";
	
	public static String javaArgs = "-Xmx3g -Xms3g"; // -Dcom.sun.management.jmxremote  -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -javaagent:/home/staticdata/CustomAgent%ID%.jar ";
	public static String tweetsSource = "Tweets";
	public static String querySource  = "querySource";
	public static String topologyName = "TornadoUI";
	
	private static KafkaConsumer<String, String> consumer; //static ConsumerConnector consumer;  
	private static Producer<String, String> producer;
	private static String zookeeper;
	private static String topic;
	private static ArrayList<String> subscriptionTopics = new ArrayList<String>();
	
	private static Properties properties;
	private static SpatioTextualToplogyBuilder builder;
	private static ArrayList<Cell> partitions;
			
	public static void main(String[] args) throws Exception {
		System.out.println("Project Directory : " + PROJECT_DIR);
		System.out.println("Datasources Directory : " + DATASOURCES_DIR);
		
		properties = new Properties();
		try {
			LOGGER.info("**********************Tornado UI Topology************************");
			LOGGER.info("**********************Reading toplogy config******************");
			properties.load(new FileInputStream(SpatioTextualConstants.CONFIG_PROPERTIES_FILE));
		} catch (final IOException ioException) {
			// Should not occur. If it does, we can't continue. So exiting the program!
			LOGGER.error(ioException.getMessage(), ioException);
			System.exit(1);
		}
		
		// Setting the static source paths 
		partitions = PartitionsHelper.readSerializedPartitions("resources/partitions16_1024_prio.ser");
				
		// Initialize our topology builder
		builder = new SpatioTextualToplogyBuilder();
		
		// Initialize and set configuration properties
		Config conf = new Config();
		conf.setDebug(false);
		
		/* Need if using consumers and producers
		setupConsumer();
		setupProducer();
		closeProducer();*/
		
		
		ZkHosts zkHosts = new ZkHosts("127.0.0.1:2181");
		SpoutConfig kafkaConfig = new SpoutConfig(zkHosts, "queries", "", "storm");
		//KafkaConfig kafkaConfig = new KafkaConfig(zkHosts, "queries");
		// KafkaSpout kafkaSpout = new KafkaSpout(kafkaConfig);
		addTweetSpout("Tweets", builder, properties, 1, 0, 50,1);
		builder.setSpout("TweetSource", new KafkaSpout(kafkaConfig));
		builder.setSpout("TornadoUITopology", new KafkaSpout(kafkaConfig));
		
		builder.addSpatioTextualProcessor("tornadouitopologydemo", 3, 16, 
				partitions, GlobalIndexType.PARTITIONED, LocalIndexType.FAST, 1024)
				.addVolatileSpatioTextualInput(tweetsSource)
				.addContinuousQuerySource(querySource);
	
		//builder.setBolt("kafkaOutputProducer", new KafkaBolt()).shuffleGrouping("tornadouitopologydemo");
		builder.setBolt("kafkaOutputProducer", new KafakaProducerBolt()).shuffleGrouping("tornadouitopologydemo");
		//KafakaProducerBolt
		
		//System.out.println("End");
		
		/* ------------- TOPOLOGY SUBMISSION: FROM TORNADOTWEETCOUNTEXAMPLE.JAVA ------------- */
		
		//Submitting the topology based on the proper submit type (change stormSubmitType in clusterconfig or config.properties to submit to local or cluster)
		String submitType = properties.getProperty(SpatioTextualConstants.stormSubmitType);
		if (submitType == null || "".equals(submitType) || SpatioTextualConstants.localCluster.equals(submitType)) {

//			conf.put(Config.STORM_ZOOKEEPER_PORT, Integer.parseInt(properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_PORT)));
//			ArrayList<String> zookeeperServers = new ArrayList(Arrays.asList(properties.getProperty(SpatioTextualConstants.STORM_ZOOKEEPER_SERVERS).split(",")));
//			conf.put(Config.STORM_ZOOKEEPER_SERVERS, zookeeperServers);
//			
			SpatioTextualLocalCluster cluster = new SpatioTextualLocalCluster();
			cluster.submitTopology("TornadoUITopology", conf, builder.createTopology());
			// TODO: declareOuputFields https://stackoverflow.com/questions/49470109/apache-storm-topology-submission-exception-x-subscribes-from-non-existent-st
		} else {
			conf.setNumAckers(Integer.parseInt(properties.getProperty("STORM_NUMBER_OF_ACKERS").trim()));
			conf.put(Config.TOPOLOGY_DEBUG, false);

			// Setting the nimbus host and port paths
			String nimbusHost = properties.getProperty(SpatioTextualConstants.NIMBUS_HOST);
			Integer nimbusPort = Integer
					.parseInt(properties.getProperty(SpatioTextualConstants.NIMBUS_THRIFT_PORT).trim());

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
				System.out.println("TOPOLOGY SUBMITTED!");
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
		//nimbusInfo[0] = nimbusHost;
		//nimbusInfo[1] = "" + nimbusPort;

		Integer minutesToStats = Integer.parseInt(properties.getProperty("MINUTS_TO_STATS"));
		Thread.sleep(1000 * 60 * minutesToStats);
		//KillTopology.killToplogy(topologyName, nimbusHost, nimbusPort);
		System.out.println("******************************************************************************************************");
		
	}
	
	/* FROM: TORNADOTWEETCOUNTEXAMPLE.JAVA
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
	
	/* FROM: TORNADOTWEETCOUNTEXAMPLE.JAVA
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

	/**
	 * 
	 */
	public static void setupConsumer() {
		consumer = new KafkaConsumer<>(createConsumerConfigProps(zookeeper, topic));
		subscriptionTopics.add("queries");
		//subscriptionTopics.add("output");
		consumer.subscribe(subscriptionTopics);	// consume from topics: queries, output, etc
	}
	
	/**
	 * 
	 */
	public static void setupProducer() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<>(props);
	}
	
	/**
	 * 
	 * @param filename
	 */
	public static void readSampleTweetFiles(String filename) {
		
	}
	
	/**
	 * Using consumers/producers to consume records from topics: specified in setupConsumer
	 */
	public static void consumeQueriesFromUI() {
		// Print the records that are consumed from the topics denoted above
		int consumerCount = 0;
	    while (true) {
	         ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
	         for (ConsumerRecord<String, String> record : records) {
	             System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
	             consumerCount++;
	             Query inputQ = new JsonHelper().convertJsonStringToQuery(record.value());
	             inputQ.toString();
	             sendToProducer("output", record.key(), "Testing.... returned query: " + record.key());
	         }
	     }
	}
	
	public static void processUIQueries(SpatioTextualToplogyBuilder builder, ArrayList<Cell> partitions, GlobalIndexType globalIndexType, LocalIndexType localIndexType) {
		/*
		 * Add a new querySpout with the parameters given
		 * 
		 * @param builder the SpatioTextualToplogyBuilder associated with the spout
		 * @param partitions 
		 * @param globalIndexType the GlobalIndexType that we choose to run the bolts on
		 * @param localIndexType the LocalIndexType that we choose to run the bolts on
		 */
		
		builder.addSpatioTextualProcessor("tornadouitopology", 3, 16, 
				partitions, globalIndexType, localIndexType,1024)
				.addVolatileSpatioTextualInput(tweetsSource)
				.addContinuousQuerySource(querySource);
			
		KafkaBolt<String, String> kBolt = new KafkaBolt<String, String>()
		        .withTopicSelector(new DefaultTopicSelector("queries"))
		        .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());
		
		// seems like whenever submitting more than one query gives error using kBolt: Bolt has already been declared for id tornadouitopology_index
		builder.setBolt("TornadoUITopology", kBolt);
		//builder.setBolt("TornadoUITopology", new TweetCountBolt()).shuffleGrouping("tornado",SpatioTextualConstants.Bolt_Output_STreamIDExtension);
	}
	
	public static void sendToProducer() {
		
		// Test text to send to producer
		for (int i = 0; i < 10; i++) {
			String topic = "output";
			String key = "key";
			String value = "TornadoUITopology Test value = " + Integer.toString(i + 1);
			producer.send(new ProducerRecord<String, String>(topic, key, value));
		}
		
		//producer.close();
	}
	
	/**
	 * 
	 * @param topic
	 * @param key
	 * @param value
	 */
	public static void sendToProducer(String topic, String key, String value) {
		// Test text to send to producer
		producer.send(new ProducerRecord<String, String>(topic, key, value));
	}
	
	/**
	 * 
	 */
	public static void closeProducer() {
		producer.close();
	}
	
	/**
	 * 3/30/2019 - Slightly modified function similar to createConsumerConfig(String, String)
	 * 
	 * @param a_zookeeper
	 * @param a_groupId
	 * @return Properties object 
	 */
	public static Properties createConsumerConfigProps(String a_zookeeper, String a_groupId) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "queries");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
	}
}
