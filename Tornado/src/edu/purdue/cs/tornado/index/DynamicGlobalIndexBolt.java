/**
 *  
 * @author Anas Daghistani <anas@purdue.edu>
 * @author Ahmed Mahmood <amahmoo@purdue.edu>
 */
package edu.purdue.cs.tornado.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.index.global.DynamicGlobalAQWAIndex;
import edu.purdue.cs.tornado.index.global.DynamicGlobalOptimizedIndex;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.LoadBalanceMessage;
import edu.purdue.cs.tornado.loadbalance.SplitMergeInfo;
import edu.purdue.cs.tornado.loadbalance.TornadoZooKeeperConnection;
import edu.purdue.cs.tornado.messages.Control;

public class DynamicGlobalIndexBolt extends GlobalIndexBolt {

	private static final long serialVersionUID = -1879981954445827627L;
	private TornadoZooKeeperConnection ZK;
	private String myId;
	private List<Integer> indexTasks;
	private Integer auxiliaryExecutorTask;
	private Integer auxiliaryExecutorTaskIndex;
	private Integer numberParallelismIndexBolts;
	private SplitMergeInfo splitMergeInfo;
	boolean splitFinished = false;
	boolean mergeFinished = false;
	private int indexBoltTurn = 0;
	private Integer newAuxiliaryExecutorTask = null;
	private boolean shouldEnterBarrier = false;
	private boolean StillApplyingNewPlan = false; //NEW ADDITION
	private boolean inBarrier = false; //without Zookeeper Barrier 
	private boolean beginAdaptive = false;//for experimenal purposes only 
	private int counterAfterPlan;
	private int numberOfTickToWait = 2;
	private int planNumber;

	/**
	 * Construcor
	 * 
	 * @param id
	 */
	public DynamicGlobalIndexBolt(String id, ArrayList<Cell> partitions, GlobalIndexType globalIndexType, Integer fineGridGran) {
		super(id, partitions, globalIndexType, fineGridGran);

	}

	// collecting the topology information
	// prepare the grid index cells
	// set the evaluator bolt task ids
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		super.prepare(stormConf, context, collector);
		this.myId = context.getThisComponentId() + "_" + context.getThisTaskId();
		this.numberParallelismIndexBolts = this.indexTasks.size();

		for (int i = 0; i < this.evaluatorBoltTasks.size(); i++)
			System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII" + myId + " : executorsTasks " + i + " = " + this.evaluatorBoltTasks.get(i));
		System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII" + myId + " : auxiliaryExecutorTask " + this.auxiliaryExecutorTask);
		if (this.selfTaskIndex == 0)
			this.ZK = new TornadoZooKeeperConnection(stormConf, myId, false);
		this.counterAfterPlan = 0;
		this.planNumber=0;
	}

	@Override
	protected void buildGlobalIndex() {
		this.indexTasks = context.getComponentTasks(id + SpatioTextualConstants.IndexIDExtension);
		this.auxiliaryExecutorTask = evaluatorBoltTasks.get(this.evaluatorBoltTasks.size() - 1);
		this.auxiliaryExecutorTaskIndex = this.evaluatorBoltTasks.size() - 1;
		if (this.globalIndexType.equals(GlobalIndexType.DYNAMIC_AQWA)) {
			globalIndex = new DynamicGlobalAQWAIndex(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions, fineGridGran);
			((DynamicGlobalAQWAIndex) globalIndex).auxilaryIndex = auxiliaryExecutorTaskIndex;
		} else if (this.globalIndexType.equals(GlobalIndexType.DYNAMIC_OPTIMIZED)) {
			globalIndex = new DynamicGlobalOptimizedIndex(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions, fineGridGran);
			((DynamicGlobalOptimizedIndex) globalIndex).auxilaryIndex = auxiliaryExecutorTaskIndex;
		}

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Config conf = new Config();
		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 10);//in seconds //OLD:10
		return conf;
	}

	@Override
	protected boolean handleData(Tuple input, String source) throws Exception {
		beginAdaptive = true;

		return super.handleData(input, source);
	}

	@Override
	protected void handleQuery(Tuple input, String source) throws Exception {

		super.handleQuery(input, source);

	}

	@Override
	protected void handleControl(Tuple tuple) {
		super.handleControl(tuple);
		Control controlMessage = (Control) tuple.getValueByField(SpatioTextualConstants.control);
		LoadBalanceMessage loadBalanceMessage = null;
		if (Control.LOAD_BALANCE.equals(controlMessage.getControlMessageType())) {
			loadBalanceMessage = controlMessage.getLeadBalanceMessage();
			if (inBarrier) { //without Zookeeper Barrier
				if (LoadBalanceMessage.FINISHED.equals(loadBalanceMessage.getLoadBalanceMessageType())) {
					System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXX " + selfTaskIndex + " : switching to new partitions XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
					((DynamicGlobalAQWAIndex) globalIndex).switchToNewPartitions();
					this.auxiliaryExecutorTaskIndex = ((DynamicGlobalAQWAIndex) globalIndex).auxilaryIndex;
					this.auxiliaryExecutorTask = evaluatorBoltTasks.get(this.auxiliaryExecutorTaskIndex);

					splitMergeInfo = null;
					splitFinished = false;
					mergeFinished = false;
					StillApplyingNewPlan = false; //NEW ADDITION
					inBarrier = false;
					//					if(selfTaskIndex==0)
					//					if (indexBoltTurn < numberParallelismIndexBolts - 1)
					//						indexBoltTurn++;
					//					else
					//						indexBoltTurn = 0;
				}
			} else {

				if (LoadBalanceMessage.FINISHED.equals(loadBalanceMessage.getLoadBalanceMessageType())) {
					if (LoadBalanceMessage.SPLIT.equals(loadBalanceMessage.getType())) {
						splitFinished = true;
						//System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSS " + myId + " reseived finished Split message from SourceTask: " + tuple.getSourceTask());
					} else if (LoadBalanceMessage.MERGE.equals(loadBalanceMessage.getType())) {
						mergeFinished = true;
						//System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMM " + myId + " reseived finished Merge message from SourceTask: " + tuple.getSourceTask());
					} else {
						System.err.println("Unknown Finished Stream");
					}
					if (splitFinished && mergeFinished) {
						//	System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFF " + myId + " finished Split & Merge, ready to switch to the new plan");
						try {
							//							splitFinished = false;
							//							mergeFinished = false;
							Control newControl = new Control();
							newControl.setControlMessageType(Control.LOAD_BALANCE);
							LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
							newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.ENTER_BARRIER);
							newLoadBalanceMessage.setPlanNumber(planNumber);
							newLoadBalanceMessage.setNewAuxiliaryExecutorTask(evaluatorBoltTasks.get(splitMergeInfo.newAuxiliaryIndex));
							newLoadBalanceMessage.setNewPartitions(((DynamicGlobalAQWAIndex) globalIndex).newPartitions);
							newControl.setLeadBalanceMessage(newLoadBalanceMessage);
							collector.emit(SpatioTextualConstants.getIndexIndexControlStreamId(id), new Values(newControl));
							enterBarrier();
						} catch (Exception e) {
							System.err.println(myId + " could not set barrier " + e);
						}
					}
				} else if (LoadBalanceMessage.ENTER_BARRIER.equals(loadBalanceMessage.getLoadBalanceMessageType())) {
						System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA " + selfTaskIndex + " Received enterBarrier Stream");
					if (splitMergeInfo == null&&this.selfTaskIndex!=0) {
						shouldEnterBarrier = true;

						newAuxiliaryExecutorTask = loadBalanceMessage.getNewAuxiliaryExecutorTask();
						ArrayList<Cell> newParitions = loadBalanceMessage.getNewPartitions();
						((DynamicGlobalAQWAIndex) globalIndex).newPartitions = newParitions;
						((DynamicGlobalAQWAIndex) globalIndex).addPartitionsToGrid(((DynamicGlobalAQWAIndex) globalIndex).newRoutingIndex, newParitions);
						if (globalIndex.getTaskIdToIndex().get(newAuxiliaryExecutorTask) == null)
							System.out.println("Error in setting newAuxiliaryExecutorTask");
						LoadBalanceMessage balanceMessage = new LoadBalanceMessage();
						balanceMessage.setNewAuxiliaryExecutorTask(this.newAuxiliaryExecutorTask);
						balanceMessage.setNewPartitions(newParitions);
						balanceMessage.setPlanNumber(loadBalanceMessage.getPlanNumber());
						balanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.GOIING_TO_BARRIER);
						Control contorlMesage = new Control();
						contorlMesage.setControlMessageType(Control.LOAD_BALANCE);
						contorlMesage.setLeadBalanceMessage(balanceMessage);
						collector.emitDirect(auxiliaryExecutorTask, SpatioTextualConstants.getIndexBoltControlStreamId(id), new Values(contorlMesage));
						inBarrier = true;
					} 
//					else {
//						enterBarrier();
//					}
				}

			}
		}

	}

	@Override
	protected void handleTickTuple(Tuple tuple) {
		if (!beginAdaptive)
			return;
		if (0 == selfTaskIndex) {
			if (!StillApplyingNewPlan) {//NEW ADDITION
				this.counterAfterPlan++;
				if(true){//if (this.counterAfterPlan % this.numberOfTickToWait == 0) {
					this.counterAfterPlan = 0;
					String bolt = ""; //for debugging
					int[][] result = new int[numberOfEvaluatorTasks][4];
					for (int i = 0; i < numberOfEvaluatorTasks; i++) {
						if (i != auxiliaryExecutorTaskIndex) {
							bolt = id + "_" + evaluatorBoltTasks.get(i);
							result[i] = ZK.readDataFrom(bolt);
							if (result[i] != null) {
								;
								System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR" + myId + " : Read data #" + i + " from " + bolt + " statData= " + result[i][0] + " statQuery= " + result[i][1] + " coord= " + result[i][2]
										+ " axis= " + result[i][3]);
							} else {
								System.err.println("Unable to read any data from zookeeper for task: " + i);
							}
						} else {
							result[i] = null;
						}
					}

					splitMergeInfo = ((DynamicGlobalAQWAIndex) globalIndex).newSplitMerge(result);

					if (splitMergeInfo != null) {
						StillApplyingNewPlan = true;//NEW ADDITION
						planNumber++;
						Control newControl;
						LoadBalanceMessage newLoadBalanceMessage;

						//((DynamicGlobalOptimizedIndex)globalIndex).printRoutingIndex(((DynamicGlobalOptimizedIndex)globalIndex).newRoutingIndex);
						if (splitMergeInfo.mergeChild0 != null) {
							System.out.println("NEW_PLAN::split merge " + myId + " sending Merge and Split commands......split " + splitMergeInfo.splitChild0.index + " to " + splitMergeInfo.splitChild1.index + " ,merge "
									+ splitMergeInfo.mergeChild0.index + "," + splitMergeInfo.mergeChild1.index);
							//merge messages
							newControl = new Control();
							newControl.setControlMessageType(Control.LOAD_BALANCE);
							newLoadBalanceMessage = new LoadBalanceMessage();
							newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.MERGE);
							newLoadBalanceMessage.setSendTo(evaluatorBoltTasks.get(splitMergeInfo.mergeParent.index));
							newControl.setLeadBalanceMessage(newLoadBalanceMessage);

							if (splitMergeInfo.mergeParent.index != splitMergeInfo.mergeChild0.index) {
								collector.emitDirect((evaluatorBoltTasks.get(splitMergeInfo.mergeChild0.index)), SpatioTextualConstants.getIndexBoltControlStreamId(id), new Values(newControl));
								//	System.out.println("NEW_PLAN::NEW_PLAN:: from: " + myId + " sent Merge message to TaskID: " + evaluatorBoltTasks.get(splitMergeInfo.mergeChild0.index) + " with index: " + splitMergeInfo.mergeChild0.index);
							} else {
								collector.emitDirect((evaluatorBoltTasks.get(splitMergeInfo.mergeChild1.index)), SpatioTextualConstants.getIndexBoltControlStreamId(id), new Values(newControl));
								//	System.out.println("NEW_PLAN::NEW_PLAN:: from: " + myId + " sent Merge message to TaskID: " + evaluatorBoltTasks.get(splitMergeInfo.mergeChild1.index) + " with index: " + splitMergeInfo.mergeChild1.index);
							}
							newControl = new Control();
							newControl.setControlMessageType(Control.LOAD_BALANCE);
							newLoadBalanceMessage = new LoadBalanceMessage();
							newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.SPLIT);
							newLoadBalanceMessage.setSendTo(auxiliaryExecutorTask);
							newLoadBalanceMessage.setNewCell(splitMergeInfo.splitChild0);
							newLoadBalanceMessage.setAuxCell(splitMergeInfo.splitChild1);
							newControl.setLeadBalanceMessage(newLoadBalanceMessage);
							//collector.emitDirect(executorsTasks.get(splitMergeInfo.splitParent.index), SpatioTextualConstants.getIndexBoltControlStreamId(id), new Values(newControl));
							collector.emitDirect(evaluatorBoltTasks.get(splitMergeInfo.splitParent.index), SpatioTextualConstants.getIndexBoltControlStreamId(id), new Values(newControl));
							//	System.out.println("NEW_PLAN::NEW_PLAN:: from: " + myId + " sent Split message to TaskID: " + evaluatorBoltTasks.get(splitMergeInfo.splitParent.index) + " with index: " + splitMergeInfo.splitParent.index);
							
						} else {
							System.out.println("NEW_PLAN::shifting " + myId + " sending shifting commands...from " + splitMergeInfo.splitChild0.index + " to " + splitMergeInfo.splitChild1.index);
							mergeFinished = true;
							newControl = new Control();
							newControl.setControlMessageType(Control.LOAD_BALANCE);
							newLoadBalanceMessage = new LoadBalanceMessage();
							newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.SHIFT);
							newLoadBalanceMessage.setSendTo(evaluatorBoltTasks.get(splitMergeInfo.splitChild1.index));
							newLoadBalanceMessage.setNewCell(splitMergeInfo.splitChild0);
							newLoadBalanceMessage.setAuxCell(splitMergeInfo.splitChild1);
							newControl.setLeadBalanceMessage(newLoadBalanceMessage);
							//collector.emitDirect(executorsTasks.get(splitMergeInfo.splitParent.index), SpatioTextualConstants.getIndexBoltControlStreamId(id), new Values(newControl));
							collector.emitDirect(evaluatorBoltTasks.get(splitMergeInfo.splitParent.index), SpatioTextualConstants.getIndexBoltControlStreamId(id), new Values(newControl));
							//	System.out.println("NEW_PLAN::NEW_PLAN:: from: " + myId + " sent Split message to TaskID: " + evaluatorBoltTasks.get(splitMergeInfo.splitParent.index) + " with index: " + splitMergeInfo.splitParent.index);
						}
						//split messages

						if (shouldEnterBarrier) {
							enterBarrier();
							shouldEnterBarrier = false;
						}
					} else {
						//System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ " + myId + " No need for changing the plan");
					}
				}
			} else if (splitFinished && mergeFinished) {
				System.out.println("Tick in barrier or still appllying plan");
				enterBarrier();
			}
		}
		collector.ack(tuple);
	}

	private void enterBarrier() {
		//System.out.println(myId + ": splitMergeInfo.newAuxiliaryIndex = " + splitMergeInfo.newAuxiliaryIndex);
		//System.out.println(myId + ": evaluatorBoltTasks.get(splitMergeInfo.newAuxiliaryIndex) = " + evaluatorBoltTasks.get(splitMergeInfo.newAuxiliaryIndex));
		newAuxiliaryExecutorTask = evaluatorBoltTasks.get(splitMergeInfo.newAuxiliaryIndex);
		if (globalIndex.getTaskIdToIndex().get(newAuxiliaryExecutorTask) == null)
			System.out.println("Error in setting newAuxiliaryExecutorTask");
		LoadBalanceMessage balanceMessage = new LoadBalanceMessage();
		balanceMessage.setNewAuxiliaryExecutorTask(newAuxiliaryExecutorTask);
		balanceMessage.setNewPartitions(((DynamicGlobalAQWAIndex) globalIndex).newPartitions);
		balanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.GOIING_TO_BARRIER);
		balanceMessage.setPlanNumber(planNumber);
		Control contorlMesage = new Control();
		contorlMesage.setControlMessageType(Control.LOAD_BALANCE);
		contorlMesage.setLeadBalanceMessage(balanceMessage);
		collector.emitDirect(auxiliaryExecutorTask, SpatioTextualConstants.getIndexBoltControlStreamId(id), new Values(contorlMesage));
		inBarrier = true;

		//System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXX " + myId + " : entering the barrier XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

	}
}
