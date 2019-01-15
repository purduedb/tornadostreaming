package edu.purdue.cs.tornado.spouts;

import org.apache.storm.topology.OutputFieldsDeclarer;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.messages.DataObject;

public interface BaseTornadoDataSpout{
	
	public void nextTuple();
	
	/**
	 * Returns an empty DataObject.
	 */
	public abstract DataObject getDataObject();
	
	/**
	 * Returns a new DataObject with same variables as the DataObject in the arguments.
	 *
	 * @param  	other 	an existing DataObject
	 */
	public abstract DataObject getDataObject(DataObject other);
	
	/**
	 * Returns a new DataObject with the specified arguments
	 *
	 * @param  	objectID 		the object identification number associated with the new DataObject
	 * @param  	location 		the location of the new DataObject
	 * @param  	originalText 	the original text message associated with the new DataObject
	 * @param  	timeStamp 		the time at which the DataObject was instantiated
	 * @param  	commmand 		the type of Command associated with the new DataObject
	 */
	public abstract DataObject getDataObject(Integer objectId, Point location, String originalText, Long timeStamp, Command command);
	
	//To declare the different output fields to be referenced later on
	public void declareOutputFields(OutputFieldsDeclarer declarer);
}
