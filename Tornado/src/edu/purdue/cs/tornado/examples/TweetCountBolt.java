package edu.purdue.cs.tornado.examples;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.messages.CombinedTuple;
import edu.purdue.cs.tornado.messages.DataObject;

/**
 * A bolt that counts the dataObjects associated to the CombinedTuples that it receives
 */
public class TweetCountBolt extends BaseRichBolt
{

  // To output tuples from this bolt to the next stage bolts, if any
  private OutputCollector collector;

  // Map to store the count of the queryIds
  private Map<Integer, Integer> countMap;

  /**
   * Set up the current bolt
   * 
   * @param map unused in this context
   * @param topologyContext unused in this context
   * @param outputCollecter used to collect and emit tuples from this bolt
   */
  public void prepare(
      Map                     map,
      TopologyContext         topologyContext,
      OutputCollector         outputCollector)
  {

    // save the collector for emitting tuples
    collector = outputCollector;

    // create and initialize the map
    countMap = new HashMap<Integer, Integer>();
  }

  /**
   * Run the current bolt
   * 
   * @param tuple this is the tuple that was emitted from the attached source for us to process
   */
  @Override
  public void execute(Tuple tuple)
  {
	ArrayList<Integer> queryIdList = null;
	CombinedTuple combinedTuple = null;
	
	if (tuple.getValue(0) instanceof CombinedTuple) {
		combinedTuple = (CombinedTuple) tuple.getValue(0);
	    queryIdList = combinedTuple.getQueriesIdList();
	    
	    for (Integer integer: queryIdList) {
	
		    // check if the queryId is present in the map
		    if (countMap.get(integer) == null) {
		
		      // not present, add the queryId with a count of 1
		      countMap.put(integer, 1);
		    } else {
		
		      // already there, hence get the count
		      int val = countMap.get(integer);
		
		      // increment the count and save it to the map
		      countMap.put(integer, ++val);
		    }
	    
		    //output for testing
		    System.out.println("QUERY ID: " + integer + ", COUNT: " + countMap.get(integer));
		    
		    // emit the word and count
		    collector.emit("queryCount", new Values(integer, countMap.get(integer)));
		    //collector.ack();
	    }
	} else {
		System.out.println("Input Error: Please input CombinedTuples for processing.");
	}
  }

  /**
   * Declare the output format for the current bolt
   * 
   * @param outputFieldsDeclarer to declare the necessary output fields 
   */
  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
  {
    // declare the first column 'queryId', second column 'count'
    outputFieldsDeclarer.declare(new Fields("queryId","count"));
  }
}
