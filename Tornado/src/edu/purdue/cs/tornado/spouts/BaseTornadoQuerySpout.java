package edu.purdue.cs.tornado.spouts;

import org.apache.storm.topology.OutputFieldsDeclarer;

import edu.purdue.cs.tornado.messages.Query;

public interface BaseTornadoQuerySpout{

	public void nextTuple();
	
	public abstract Query getQuery();
	public abstract Query getQuery(Query q);
	public abstract Query getQuery(String line);
	
	//To declare the different output fields to be referenced later on
	public void declareOutputFields(OutputFieldsDeclarer declarer);
	
}
