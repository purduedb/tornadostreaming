package edu.purdue.cs.tornado.evaluator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TornadoZooKeeperConnection;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.LoadBalanceMessage;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.messages.Control;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class DynamicEvalautorBolt extends BaseRichBolt {
	//*******************Storm specific attributes *********************
	private Map stormConf; //configuration
	private TopologyContext context; //storm context
	private OutputCollector collector;
	private String id;

	private ArrayList<Query >toSendQueriesList; //queries to be send 
	private ArrayList<DataObject> toSendDataObjects;//data objects to be send 
	private ArrayList<DataObject> incommingDataObjects;
	private ArrayList<Query> incommingQueries;
	public DynamicEvalautorBolt(String id) {
		this.id = id;
	}

	String addedString = "+++"; //for debugging
	int roundNum = 1; //for debugging
	int statData = 0; //should add a mechanism to reset this counter for old stat
	int statQuery = 0;
	String myId;

	TornadoZooKeeperConnection ZK;
	Partition myPartition;

	int numberParallelismExecutors;
	int numberParallelismIndexBolts;

	int numberOfReceivedReply;
	int numberOfGoingToBarrier;
	int numberOfTickTuples;
	//Barrier b;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.stormConf = stormConf;
		this.collector = collector;
		this.myId = context.getThisComponentId() + "_" + context.getThisTaskId();
		this.ZK = new TornadoZooKeeperConnection(stormConf, this.myId);
		this.context = context;
		this.numberParallelismExecutors = context.getComponentTasks("ExecutorBolt").size() - 1;
		this.numberParallelismIndexBolts = context.getComponentTasks("IndexBolt").size();
		this.numberOfReceivedReply = 0;
		this.numberOfGoingToBarrier = 0;
		this.numberOfTickTuples = 0;
		toSendQueriesList = new ArrayList<Query >(); //queries to be send 
		toSendDataObjects = new ArrayList<DataObject> ();//data objects to be send 
		Long time=(new Date()).getTime();
		for(int i=0;i<100;i++){
			toSendDataObjects.add(new DataObject(myId+"_"+i,new Point((double)i,(double)i),"test text", time, SpatioTextualConstants.addCommand));
			Query q = new Query();
			q.setQueryId(myId+"_"+i);
			toSendQueriesList.add(q);
		}
		incommingDataObjects= new ArrayList<DataObject> ();
		incommingQueries= new ArrayList<Query> ();
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// configure how often a tick tuple will be sent to the bolt
		Config conf = new Config();
		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 2);
		return conf;
	}

	protected static boolean isTickTuple(Tuple tuple) {
		return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID) && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
	}

	@Override
	public void execute(Tuple tuple) {
		if (isTickTuple(tuple)) {
			numberOfTickTuples++;
			System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT" + myId + " : TickTuple number:  " + numberOfTickTuples);

			if (numberOfTickTuples == 4) {
				numberOfTickTuples = -1; //skip one so that it will not update while IndexBolts get stat
				Random rand = new Random();
				//statQuery = rand.nextInt(201); //dummy query load
				statQuery = 1;
				//coord, axis: 0 is x-coord 1 is y-coord
				PointOnAxis myBestSplitPoint = bestSplitPosition();
				System.out.println(
						"+++++++++++++++++++++++++++++++++++++++++++++" + myId + "__Write to my Znode: " + " statData= " + statData + " statQuery= " + statQuery + " coord= " + myBestSplitPoint.coord + " axis= " + myBestSplitPoint.axis);
				ZK.writeData(statData, statQuery, myBestSplitPoint.coord, myBestSplitPoint.axis);
				roundNum++; //for debugging
				System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" + myId + " : Change roundNum to " + roundNum);
			}
		} else
			try {
				String sourceType = tuple.getSourceStreamId();
				if (sourceType.contains(SpatioTextualConstants.Index_Bolt_STreamIDExtension_Query)) {
					;
					;//	handleQuery(input);
				} else if (SpatioTextualConstants.isDataStreamSource(sourceType)) {
					;
					;//	handleDataObject(input);
				} else if (SpatioTextualConstants.isControlStreamSource(sourceType)) {
					handleControlMessage(tuple);
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}

	}

	private void handleControlMessage(Tuple tuple) {
		Control controlMessage = (Control) tuple.getValueByField(SpatioTextualConstants.control);
		if (Control.LOAD_BALANCE.equals(controlMessage.getControlMessageType())) {
			LoadBalanceMessage balanceMessage = controlMessage.getLeadBalanceMessage();
			if (LoadBalanceMessage.PARTITION.equals(balanceMessage.getLoadBalanceMessageType())) {
				myPartition = (Partition) tuple.getValue(0);
				System.out.println("///////////////////////////////////////// " + myId + " : recived Partition info. of index = " + myPartition.index);
			} else if (LoadBalanceMessage.MERGE.equals(balanceMessage.getLoadBalanceMessageType())) {
				Integer receipinetTaskId  = balanceMessage.getSendTo();
				Integer sourceIndexingBoltId = tuple.getSourceTask();
				//send all the data to the ExecutorBolt in the tuple
				Control newControl = new Control();
				newControl.setControlMessageType(Control.LOAD_BALANCE);
				newControl.setDataObjects(toSendDataObjects);
				newControl.setQueriesList(toSendQueriesList);
				LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
				newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.MERGE_DATA);
				newControl.setLeadBalanceMessage(newLoadBalanceMessage);
	
				collector.emitDirect(receipinetTaskId,SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(newControl));
				//finish sending data ...... notify IndexBolts
				newControl = new Control();
				newControl.setControlMessageType(Control.LOAD_BALANCE);
				newLoadBalanceMessage = new LoadBalanceMessage();
				newLoadBalanceMessage.setType(LoadBalanceMessage.MERGE);
				newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.FINISHED);
				newControl.setLeadBalanceMessage(newLoadBalanceMessage);
				collector.emitDirect(sourceIndexingBoltId,SpatioTextualConstants.getBoltIndexControlStreamId(id), new Values(newControl));
			} else if (LoadBalanceMessage.SPLIT.equals(balanceMessage.getLoadBalanceMessageType())) {
				//send the data out of the newCell in the tuple to the ExecutorBolt in the tuple (auxiliaryExecutor)
				Cell newCell = ( Cell) tuple.getValue(1);//new self boundaries 
				Cell auxCell = (Cell) tuple.getValue(2);// boundaries of data and queries to send 
				Integer receipinetTaskId  = balanceMessage.getSendTo();
				Integer sourceIndexingBoltId = tuple.getSourceTask();
				//send all the data to the ExecutorBolt in the tuple
				Control newControl = new Control();
				newControl.setControlMessageType(Control.LOAD_BALANCE);
				newControl.setDataObjects(toSendDataObjects);
				newControl.setQueriesList(toSendQueriesList);
				LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
				newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.SPLIT_DATA);
				newControl.setLeadBalanceMessage(newLoadBalanceMessage);
	
				collector.emitDirect(receipinetTaskId,SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(newControl));
				//finish sending data ...... notify IndexBolt
				newControl = new Control();
				newControl.setControlMessageType(Control.LOAD_BALANCE);
				newLoadBalanceMessage = new LoadBalanceMessage();
				newLoadBalanceMessage.setType(LoadBalanceMessage.SPLIT);
				newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.FINISHED);
				newControl.setLeadBalanceMessage(newLoadBalanceMessage);
				collector.emitDirect(sourceIndexingBoltId,SpatioTextualConstants.getBoltIndexControlStreamId(id), new Values(newControl));
			} else if (LoadBalanceMessage.MERGE_DATA.equals(balanceMessage.getLoadBalanceMessageType() )){//tuple.getSourceStreamId() == "MergeData") {//TODO
				//store data
				incommingDataObjects.addAll(controlMessage.getDataObjects());
				incommingQueries.addAll(controlMessage.getQueriesList());
				System.out.println("............................... " + myId + " : recived MergeData ");
			} else if (LoadBalanceMessage.SPLIT_DATA.equals(balanceMessage.getLoadBalanceMessageType() )) {//TODO
				//store data 
				incommingDataObjects.addAll(controlMessage.getDataObjects());
				incommingQueries.addAll(controlMessage.getQueriesList());
				System.out.println("............................... " + myId + " : recived SplitData ");
			} else if (LoadBalanceMessage.GOIING_TO_BARRIER.equals(balanceMessage.getLoadBalanceMessageType())) {
				//Send to all executorBolts and wait their responses
				System.out.println("............................... " + myId + " : recived GoingToBarrier from task: " + tuple.getSourceTask());
				numberOfGoingToBarrier++;
				if (numberOfGoingToBarrier >= numberParallelismIndexBolts) {
					System.out.println("............................... " + myId + " : All indexBolts entered barrier, send new partations to ExecutorBols ");
					numberOfGoingToBarrier = 0;
					List<Integer> newExecutorsTasks = (ArrayList<Integer>) tuple.getValue(0);
					int newAuxiliaryExecutorTask = tuple.getInteger(1);
					ArrayList<Cell> newPartitions = (ArrayList<Cell>) tuple.getValue(2);
					
					Control newControl = new Control();
					newControl.setControlMessageType(Control.LOAD_BALANCE);
					
					for (Partition c : newPartitions) {
						LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
						newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.ReplyWhenReceived);
						newLoadBalanceMessage.setParition(c);
						newControl.setLeadBalanceMessage(newLoadBalanceMessage);
						collector.emitDirect(newExecutorsTasks.get(c.index),SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(newControl));
						
					}
					Partition auxPartition = new Partition(0, 0, 0, 0);
					auxPartition.index = -1;
					

					LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
					newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.ReplyWhenReceived);
					newLoadBalanceMessage.setParition(auxPartition);
					newControl.setLeadBalanceMessage(newLoadBalanceMessage);
					collector.emitDirect(newAuxiliaryExecutorTask,SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(newControl));
					
					
				}
				//collector.emit("ReplyWhenReceived", new Values(0));
			} else if (LoadBalanceMessage.ReplyWhenReceived.equals(balanceMessage.getLoadBalanceMessageType())) {
				//send Reply stream, update myPartition with the new one, reset statistics
				myPartition = (Partition) tuple.getValue(0);
				System.out.println("............................... " + myId + " : recived ReplyWhenReceived Partition info. of index = " + myPartition.index);
				
				
				
				Control newControl = new Control();
				newControl.setControlMessageType(Control.LOAD_BALANCE);
				LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
				newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.Reply);
				newControl.setLeadBalanceMessage(newLoadBalanceMessage);
				collector.emitDirect(tuple.getSourceTask(),SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(newControl));
				
				statData = 0;
				statQuery = 0;
			} else if (LoadBalanceMessage.Reply.equals(balanceMessage.getLoadBalanceMessageType())) {
				//Count Reply, until reach numberParallelismExecutors   
				System.out.println("............................... " + myId + " : recived Reply from task: " + tuple.getSourceTask());
				numberOfReceivedReply++;

				if (numberOfReceivedReply > numberParallelismExecutors) {
					try {
						System.out.println("BRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRB " + myId + " : START Removing Barrier ");
						Thread.sleep(500); //Note: mostly not necessarily
						ZK.removeBarrier();
						numberOfReceivedReply = 0;
						System.out.println("BRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRB " + myId + " : Barrier Removed ");
					} catch (Exception e) {
						System.err.println(myId + " could not remove Barrier " + e);
					}
				}
			}

			else {
				System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX " + myId + " : recived a tuple from unknown stream = " + tuple.getSourceStreamId());
			}
		}
	}

	//choosing the best splitting point ..IMPORT and adjust to proper cost
	private PointOnAxis bestSplitPosition() {
		PointOnAxis splitPoint = new PointOnAxis(0, 0);
		if (myPartition.getDimensions()[0] >= myPartition.getDimensions()[1])
			splitPoint.axis = 0; //0 is x-coord 1 is y-coord
		else
			splitPoint.axis = 1; //0 is x-coord 1 is y-coord

		if (splitPoint.axis == 0)
			splitPoint.coord = (int) (((myPartition.getDimensions()[0]) / 2) + myPartition.getCoords()[0]);
		else
			splitPoint.coord = (int) (((myPartition.getDimensions()[1]) / 2) + myPartition.getCoords()[1]);
		return splitPoint;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		try {
			//These are the streams for interaction with other bolts 
			declarer.declareStream(id + SpatioTextualConstants.Bolt_Bolt_STreamIDExtension_Query, new Fields(SpatioTextualConstants.query));
			declarer.declareStream(id + SpatioTextualConstants.Bolt_Bolt_STreamIDExtension_Data, new Fields(SpatioTextualConstants.data));
			declarer.declareStream(SpatioTextualConstants.getBoltBoltControlStreamId(id), new Fields(SpatioTextualConstants.control));
			declarer.declareStream(id + SpatioTextualConstants.Bolt_Index_STreamIDExtension_Query, new Fields(SpatioTextualConstants.query));
			declarer.declareStream(id + SpatioTextualConstants.Bolt_Index_STreamIDExtension_Data, new Fields(SpatioTextualConstants.data));
			declarer.declareStream(id + SpatioTextualConstants.Bolt_Index_STreamIDExtension_Control, new Fields(SpatioTextualConstants.control));
			//This is the final output 
			declarer.declareStream(SpatioTextualConstants.Bolt_Output_STreamIDExtension, new Fields(SpatioTextualConstants.output));
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private class PointOnAxis {
		int axis; //0 is x-coord 1 is y-coord
		int coord;

		public PointOnAxis(int axis, int coord) {
			this.axis = axis;
			this.coord = coord;
		}
	}

}
