package edu.purdue.cs.tornado.tutorial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import org.apache.storm.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.purdue.cs.tornado.SpatioTextualToplogyBuilder;
import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.spouts.KafkaSpout;


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
			
	public static void main(String[] args) {
		System.out.println("Project Directory : " + PROJECT_DIR);
		System.out.println("Datasources Directory : " + DATASOURCES_DIR);
		
		final Properties properties = new Properties();
		try {
			LOGGER.info("**********************Tornado UI Topology************************");
			LOGGER.info("**********************Reading toplogy config******************");
			properties.load(new FileInputStream(SpatioTextualConstants.CONFIG_PROPERTIES_FILE));
		} catch (final IOException ioException) {
			// Should not occur. If it does, we can't continue. So exiting the program!
			LOGGER.error(ioException.getMessage(), ioException);
			System.exit(1);
		}
	
		
		/*
		 * 
		 * //Load Properties from a specific file path (use CONFIG_PROPERTIES_FILE for local cluster)
		//final Properties properties = loadProperties(SpatioTextualConstants.CLUSTER_CONFIG_PROPERTIES_FILE);
		final Properties properties = loadProperties(SpatioTextualConstants.CONFIG_PROPERTIES_FILE);

		//Setting the static source paths 
		ArrayList<Cell> partitions = PartitionsHelper.readSerializedPartitions("resources/partitions16_1024_prio.ser");
				
		//Initialize our topology builder
		SpatioTextualToplogyBuilder builder = new SpatioTextualToplogyBuilder();
		
		//Initialize and set Config properties
		Config conf = new Config();
		conf.setDebug(false);
		 */
		
	}
	
	/* FROM TORNADOTWEETCOUNTEXAMPLE.JAVA
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

	
	public static void setupKafkaSpout() {
		
		//SpatioTextualToplogyBuilder builder = new SpatioTextualToplogyBuilder();
		//builder.setSpout("TextualRangeQueryGenerator", new KafkaSpout());
		
	}
	
	public static void setupTweetSpout() {
		
	}
	
	public static void setupQuerySpout() {
		
	}
	
	public static void setupProducer() {
		
	}
	
	
	
	public static void readSampleTweetFiles(String filename) {
		
	}
	
	public static void readQueriesFromUI() {
		
	}
	
	public static void processUIQueries() {
		
	}
}
