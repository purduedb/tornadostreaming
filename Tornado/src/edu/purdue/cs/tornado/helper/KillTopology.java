package edu.purdue.cs.tornado.helper;

import java.util.Iterator;
import java.util.Map;

import org.apache.thrift7.TException;

import backtype.storm.Config;
import backtype.storm.generated.KillOptions;
import backtype.storm.generated.NotAliveException;
import backtype.storm.generated.TopologySummary;
import backtype.storm.generated.Nimbus.Client;
import backtype.storm.utils.NimbusClient;
import backtype.storm.utils.Utils;

public class KillTopology {
	public static void killToplogy(String topologyName,String nibmusHost, Integer nimbusPort) throws NotAliveException, TException{
		
		Map storm_conf = Utils.readStormConfig();
		storm_conf.put(Config.NIMBUS_HOST, nibmusHost);
		storm_conf.put(Config.NIMBUS_THRIFT_PORT, nimbusPort);
		Client client = NimbusClient.getConfiguredClient(storm_conf)
				.getClient();
		Iterator<TopologySummary> topologyList = client.getClusterInfo()
				.get_topologies_iterator();

	
		KillOptions options = new KillOptions();
		options.set_wait_secs(0);
		client.killTopologyWithOpts(topologyName, options);
	}
}
