package edu.purdue.cs.tornado.helper;

import org.apache.storm.generated.KillOptions;
import org.apache.storm.generated.Nimbus.Client;
import org.apache.storm.generated.NotAliveException;
import org.apache.storm.thrift.TException;
import org.apache.storm.thrift.protocol.TBinaryProtocol;
import org.apache.storm.thrift.transport.TFramedTransport;
import org.apache.storm.thrift.transport.TSocket;

public class KillTopology {
	public static void killToplogy(String topologyName,String nibmusHost, Integer nimbusPort) throws NotAliveException, TException{
		
		
		TSocket socket = new TSocket(nibmusHost, nimbusPort);
		TFramedTransport transport = new TFramedTransport(socket);
		TBinaryProtocol protocol = new TBinaryProtocol(transport);
		Client client = new Client(protocol);
		try {
			transport.open();
			KillOptions options = new KillOptions();
			options.set_wait_secs(0);
			client.killTopologyWithOpts(topologyName, options);
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		
//		
//		Map storm_conf = Utils.readStormConfig();
//		storm_conf.put(Config.NIMBUS_HOST, nibmusHost);
//		storm_conf.put(Config.NIMBUS_THRIFT_PORT, nimbusPort);
//		Client client = NimbusClient.getConfiguredClient(storm_conf)
//				.getClient();
//		Iterator<TopologySummary> topologyList = client.getClusterInfo()
//				.get_topologies_iterator();
//
//	
//		KillOptions options = new KillOptions();
//		options.set_wait_secs(0);
//		client.killTopologyWithOpts(topologyName, options);
	}
}
