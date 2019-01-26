package edu.purdue.cs.tornado.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.performance.ClusterInformationExtractor;

public class ClusterSummeryPrinter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClusterSummeryPrinter.class);
	public static void main(String[] args) {
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
		String nimbusHost = properties.getProperty(SpatioTextualConstants.NIMBUS_HOST);
		Integer nimbusPort = Integer.parseInt(properties.getProperty(SpatioTextualConstants.NIMBUS_THRIFT_PORT).trim());
		String[] nimbusInfo = new String[2];
		nimbusInfo[0] = nimbusHost;
		nimbusInfo[1] = "" + nimbusPort;
		Integer minutesToStats = Integer.parseInt(properties.getProperty("MINUTS_TO_STATS"));
		
		System.out.println(ClusterInformationExtractor.getStats(nimbusInfo));
	}
}
