package sqliteparser;

//package org.apache.storm.starter.spout;
//import for defining spout
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import org.apache.storm.spout.SpoutOutputCollector;
//importing the context
import org.apache.storm.task.TopologyContext;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
//imports for defining tuples
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
//logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StringReaderSpout extends BaseRichSpout {
    private static final Logger LOG = LoggerFactory.getLogger(BaseRichSpout.class);
    private SpoutOutputCollector spoutOutputCollector;
    private TopologyContext context; //what is context here
    
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("line"));
    }
    
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector)
    {
        this.spoutOutputCollector = collector;
        this.context = context;
    }
    
    public void nextTuple()
    {
        try {
           // BufferedReader br = new BufferedReader(new FileReader("/Users/jaideepjuneja/Downloads/apache-storm-1.0.2/examples/storm-starter/src/jvm/org/apache/storm/starter/spout/test.txt"));
            
           URL path = ClassLoader.getSystemResource("test.txt");
           path.toURI();
           
           BufferedReader br = new BufferedReader(new FileReader(new File(path.toURI())));
           String line;
            
             while((line = br.readLine())!=null)
             {
              System.out.println("The line is " + line);
              this.spoutOutputCollector.emit(new Values(line)); //we have to emit tuples
             }
            
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(StringReaderSpout.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
       
    }
    

}