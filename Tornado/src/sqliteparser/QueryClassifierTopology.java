package sqliteparser;

//package org.apache.storm.starter;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
//import org.apache.storm.starter.bolt.StringClassifierBolt;
//import org.apache.storm.starter.spout.StringReaderSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;


public class QueryClassifierTopology {
    
    public static void main (String args[]) throws Exception{
        Config config = new Config();
        config.setDebug(true);
        
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("String-Reader-Spout", new StringReaderSpout());
        builder.setBolt("String-Classifier-Bolt", new StringClassifierBolt()).fieldsGrouping("String-Reader-Spout",new Fields("line"));
        
        
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("QueryClassifierTopology", config, builder.createTopology());
        
         Thread.sleep(10000);
        cluster.killTopology("QueryClassifierTopology");

        cluster.shutdown();
    }
    
    
}