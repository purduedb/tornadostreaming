/**
 *  
 * @author Anas Daghistani <anas@purdue.edu>
 *
 */
package edu.purdue.cs.tornado.evaluator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.DataSourceType;
import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.index.global.DynamicGlobalAQWAIndex;
import edu.purdue.cs.tornado.index.global.DynamicGlobalOptimizedIndex;
import edu.purdue.cs.tornado.index.global.GlobalIndexType;
import edu.purdue.cs.tornado.index.local.LocalHybridGridIndex;
import edu.purdue.cs.tornado.index.local.LocalIndexType;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.LoadBalanceMessage;
import edu.purdue.cs.tornado.loadbalance.LocalBestSplitInfo;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.loadbalance.TornadoZooKeeperConnection;
import edu.purdue.cs.tornado.messages.Control;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class DynamicEvalautorBolt extends SpatioTextualEvaluatorBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7243129323936186328L;
	String myId;
	protected PointOnAxis mySplitPoint;
	protected boolean isMerge = false;
	protected boolean isSplit = false;
	protected boolean isReceivedMerge = false;
	protected boolean isReceivedSplit = false;
	protected TornadoZooKeeperConnection ZK;
	protected int numberParallelismExecutors;
	protected int numberParallelismIndexBolts;
	protected int numberOfReceivedReply;
	protected int numberOfGoingToBarrier;
	protected int numberOfTickTuples;
	private List<Integer> indexTasks; //without Zookeeper Barrier
	private int lastPlanNumber;

	//For adaptivity and statstics monitoring this assumes an overlay on a fine grid granualrity  
	Integer totalCostUnMatched;
	Integer[] xColumnCostUnMatched;
	Integer[] yRowCostUnMatched;
	Integer totalCost;
	Integer[] xColumnCost;
	Integer[] yRowCost;

	Integer[] xColumnCostReported;
	Integer[] yRowCostReported;
	Cell toClearCells;
	Integer receipinetTaskId;

	//boolean[][] incommingCells;

	public DynamicEvalautorBolt(String id, LocalIndexType localIndexType, GlobalIndexType globalIndexType, ArrayList<Cell> partitions, Integer fineGridGran) {
		super(id, localIndexType, globalIndexType, partitions, fineGridGran);
//		this.totalCost=0;
//		this.toClearCells = null;
//		this.xColumnCost = new Integer[fineGridGran];
//		this.yRowCost = new Integer[fineGridGran];
//		this.xColumnCostUnMatched = new Integer[fineGridGran];
//		this.yRowCostUnMatched = new Integer[fineGridGran];
//		this.xColumnCostReported = new Integer[fineGridGran];
//		this.yRowCostReported = new Integer[fineGridGran];
//		totalCost = 0;
//		totalCostUnMatched = 0;
//		for (int i = 0; i < fineGridGran; i++) {
//			this.xColumnCost[i] = 0;
//			this.xColumnCostUnMatched[i] = 0;
//			this.xColumnCostReported[i] = 0;
//		}
//		for (int i = 0; i < fineGridGran; i++) {
//			this.yRowCost[i] = 0;
//			this.yRowCostUnMatched[i] = 0;
//			this.yRowCostReported[i] = 0;
//		}
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		super.prepare(stormConf, context, collector);
		this.myId = context.getThisComponentId() + "_" + context.getThisTaskId();
		this.ZK = new TornadoZooKeeperConnection(stormConf, this.myId, true);
		this.numberOfReceivedReply = 0;
		this.numberOfGoingToBarrier = 0;
		this.numberOfTickTuples = 0;
		this.indexTasks = context.getComponentTasks(id + SpatioTextualConstants.IndexIDExtension);

		this.toClearCells = null;
		this.xColumnCost = new Integer[fineGridGran];
		this.yRowCost = new Integer[fineGridGran];
		this.xColumnCostUnMatched = new Integer[fineGridGran];
		this.yRowCostUnMatched = new Integer[fineGridGran];
		this.xColumnCostReported = new Integer[fineGridGran];
		this.yRowCostReported = new Integer[fineGridGran];
		totalCost = 0;
		totalCostUnMatched = 0;
		for (int i = 0; i < fineGridGran; i++) {
			this.xColumnCost[i] = 0;
			this.xColumnCostUnMatched[i] = 0;
			this.xColumnCostReported[i] = 0;
		}
		for (int i = 0; i < fineGridGran; i++) {
			this.yRowCost[i] = 0;
			this.yRowCostUnMatched[i] = 0;
			this.yRowCostReported[i] = 0;
		}
		this.lastPlanNumber =-1;
		//		incommingCells = new boolean[fineGridGran][fineGridGran];
		//		for (int i = 0; i < fineGridGran; i++) {
		//			for (int j = 0; j < fineGridGran; j++) {
		//				incommingCells[i][j] = false;
		//			}
		//		}
		updateLocalIndexForNewParition();
	}

	/**
	 * This function initialized the global and local indexes information
	 */
	@Override
	public void prepareLocalAndGlobalIndexes() {
		numberOfEvaluatorTasks = this.evaluatorBoltTasks.size();
		selfComponentId = context.getThisComponentId();
		selfTaskId = context.getThisTaskId();
		selfTaskIndex = context.getThisTaskIndex();
		numberParallelismExecutors = context.getComponentTasks(id).size() - 1;
		numberParallelismIndexBolts = context.getComponentTasks(id + SpatioTextualConstants.IndexIDExtension).size();
		numberOfEvaluatorTasks = this.evaluatorBoltTasks.size();
		if (partitions == null) {
			this.partitions = getInitialPartitions();
		}
		this.myPartition = new Cell(0, 0, 0, 0);
		this.myPartition.index = selfTaskIndex;
		for (Cell c : partitions)
			if (c.index == selfTaskIndex)
				this.myPartition = c;

		selfComponentId = context.getThisComponentId();
		selfTaskId = context.getThisTaskId();
		selfTaskIndex = context.getThisTaskIndex();
		indexTasks = context.getComponentTasks(id + SpatioTextualConstants.IndexIDExtension);
		if (this.globalIndexType.equals(GlobalIndexType.DYNAMIC_AQWA)) {
			globalIndex = new DynamicGlobalAQWAIndex(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions, fineGridGran);
		} else if (this.globalIndexType.equals(GlobalIndexType.DYNAMIC_OPTIMIZED)) {
			globalIndex = new DynamicGlobalOptimizedIndex(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions, fineGridGran);
		}
		selfBounds = globalIndex.getBoundsForTaskIndex(selfTaskIndex);
	}

	@Override
	public void handleTickTuple(Tuple tuple) {
		//super.handleTickTuple(tuple);
		
		numberOfTickTuples++;
		if (numberOfTickTuples == 4) {
			
			numberOfTickTuples = -1; //skip one so that it will not update while IndexBolts get stat
			if (myPartition != null && !(myPartition.getBottom() == 0 && myPartition.getTop() == 0 && myPartition.getLeft() == 0 && myPartition.getRight() == 0)) {
				//				PointOnAxis myBestSplitPoint = getBestSplit();
				//				ZK.writeData(totalCost+totalCostUnMatched, 1, myBestSplitPoint.coord, myBestSplitPoint.axis);
				LocalBestSplitInfo myBestSplitPointVertical = getBestSplitPositionX();
				LocalBestSplitInfo myBestSplitPointHorizonal = getBestSplitPositionY();
//				ArrayList<Integer> xColumnStats = new ArrayList<Integer>();
//				ArrayList<Integer> yRawStats = new ArrayList<Integer>();
				for (int i = myPartition.getLeft(); i < myPartition.getRight(); i++) {
					xColumnCostReported[i]=((xColumnCost[i] + xColumnCostUnMatched[i]));
				}
			//	ArrayList<Integer> xRowStats = new ArrayList<Integer>();
				for (int i = myPartition.getBottom(); i < myPartition.getTop(); i++) {
					yRowCostReported[i]=((yRowCost[i] + yRowCostUnMatched[i]));
				}
				//				System.out.println("Writing to zookeeper: " + (totalCost + totalCostUnMatched) + "," + myPartition.toString() + ", vertical split" + myBestSplitPointVertical.splitPosition + ", horizontal split"
				//						+ myBestSplitPointHorizonal.splitPosition);
				ZK.writeData(totalCost + totalCostUnMatched, 1, myBestSplitPointVertical.splitPosition, 0, myBestSplitPointVertical.afterSplitCost, 1, myBestSplitPointHorizonal.splitPosition, 1, myBestSplitPointHorizonal.afterSplitCost);
				if (totalCost + totalCostUnMatched < 0)
					System.err.println("Error negative cost:" + myPartition.toString());
				
				//		ZK.writeData(totalCost, 1, myBestSplitPointVertical.splitPosition, 0,myBestSplitPointVertical.afterSplitCost,1,myBestSplitPointHorizonal.splitPosition,1,myBestSplitPointHorizonal.afterSplitCost);
			} else {
				;
				//	System.out.println("Null parition:" + this.selfTaskIndex);
			}
			resetAllCounters();
		}
	}

	public void resetAllCounters() {
		totalCostUnMatched = 0;
		totalCost /= 2;
		for (int i = 0; i < fineGridGran; i++) {
			this.xColumnCostUnMatched[i] = 0;
			this.xColumnCost[i] /= 2;
		}
		for (int i = 0; i < fineGridGran; i++) {
			this.yRowCostUnMatched[i] = 0;
			this.yRowCost[i] /= 2;
		}
		//Ahmed Mahmood, this method sends the data to another task for load balance 
		//In this case we will send data and queries of entire index cells, we need not index them again
		Iterator<Entry<String, DataSourceInformation>> itr = sourcesInformations.entrySet().iterator();
		while (itr.hasNext()) {
			DataSourceInformation sourceInfo = itr.next().getValue();
			if (DataSourceType.DATA_SOURCE.equals(sourceInfo.dataSourceType)) {
				String sourceId = sourceInfo.dataSourceId;
				ArrayList<IndexCell> indexCells = ((LocalHybridGridIndex) sourceInfo.localHybridIndex).getIndexCellsFromPartition(myPartition);
				for (IndexCell indexCell : indexCells) {
					indexCell.indexCellCost /= 2;
				}
			}
		}

	}

	protected LocalBestSplitInfo getBestSplitPositionX() {
		LocalBestSplitInfo localBestSplitX = new LocalBestSplitInfo();

		Integer bestSplitLoc = this.myPartition.getLeft();
		Integer beforeSplitCost = 0;
		Integer bestSplitCost = totalCost + totalCostUnMatched + 1;
		Integer bestBeforeSplitCost = 0;
		for (int i = this.myPartition.getLeft(); i < this.myPartition.getRight() - 1; i++) {
			beforeSplitCost += this.xColumnCost[i] + this.xColumnCostUnMatched[i];
			Integer splitCost = Math.abs((totalCost + totalCostUnMatched - beforeSplitCost) - beforeSplitCost); //This is the difference between two splits //TODO add cost for KNN queries 
			if (splitCost <= bestSplitCost) {
				bestSplitCost = splitCost;
				bestSplitLoc = i + 1;
				bestBeforeSplitCost = beforeSplitCost;
			} else
				break;
		}
//		if(bestSplitLoc==myPartition.getLeft()||bestSplitLoc==myPartition.getRight()){
//			System.err.println("Error in finding best split loc");
//			
//		}
		localBestSplitX.coordinate = 0;
		localBestSplitX.beforeSplitCost = bestBeforeSplitCost;
		localBestSplitX.afterSplitCost = totalCost + totalCostUnMatched - bestBeforeSplitCost;
		localBestSplitX.splitPosition = bestSplitLoc;
		localBestSplitX.bestSplitCost = bestSplitCost;
		
		return localBestSplitX;
	}

	protected LocalBestSplitInfo getBestSplitPositionY() {
		LocalBestSplitInfo localBestSplitY = new LocalBestSplitInfo();

		Integer bestSplitLoc = this.myPartition.getBottom();
		Integer beforeSplitCost = 0;
		Integer bestSplitCost = totalCost + totalCostUnMatched + 1;
		Integer bestBeforeSplitCost = 0;
		for (int i = this.myPartition.getBottom(); i < this.myPartition.getTop() - 1; i++) {
			beforeSplitCost += this.yRowCost[i] + this.yRowCostUnMatched[i];
			Integer splitCost = Math.abs((totalCost + totalCostUnMatched - beforeSplitCost) - beforeSplitCost); //This is the difference between two splits //TODO add cost for KNN queries 
			if (splitCost <= bestSplitCost) {
				bestSplitCost = splitCost;
				bestSplitLoc = i + 1;
				bestBeforeSplitCost = beforeSplitCost;
			} else
				break;
		}
//		if(bestSplitLoc==myPartition.getBottom()||bestSplitLoc==myPartition.getTop())
//			System.err.println("Error in finding best split loc");
		localBestSplitY.coordinate = 1;
		localBestSplitY.beforeSplitCost = bestBeforeSplitCost;
		localBestSplitY.afterSplitCost = totalCost + totalCostUnMatched - bestBeforeSplitCost;
		localBestSplitY.splitPosition = bestSplitLoc;
		localBestSplitY.bestSplitCost = bestSplitCost;
		
		return localBestSplitY;
	}

	@Override
	public void handleVolatileDataObject(DataObject dataObject) {
		boolean fromNeighbour = false;
		//		if (!SpatialHelper.overlapsSpatially(dataObject.getLocation(), selfBounds)) {
		//			fromNeighbour = true;
		//		} else {
		//			fromNeighbour = false;
		//		}
		//		if (!fromNeighbour) {
		LocalHybridGridIndex localHybridGridIndex = ((LocalHybridGridIndex) sourcesInformations.get(dataObject.getSrcId()).getLocalHybridIndex());
		IndexCellCoordinates cellCoordinates = localHybridGridIndex.mapDataPointToPartition(dataObject.getLocation());
		IndexCell indexCell = localHybridGridIndex.getIndexCellFromCoordinates(cellCoordinates);

		if (indexCell == null) {
			this.totalCostUnMatched++;
			this.xColumnCostUnMatched[cellCoordinates.getX()]++;
			this.yRowCostUnMatched[cellCoordinates.getY()]++;
			//			if (incommingCells[cellCoordinates.getX()][cellCoordinates.getY()] == true)
			//				System.out.println("Cannot find cell at" + cellCoordinates.getX() + "," + cellCoordinates.getY() + "my partition" + myPartition.toString());
			return;
		} else if (indexCell.transmitted == true) {
			forwardDataObjectTorecpientTask(dataObject);
			return;
		}

		Integer keywordCount = dataObject.getObjectText().size();
		this.totalCost += keywordCount;
		this.xColumnCost[cellCoordinates.getX()] += keywordCount;
		this.yRowCost[cellCoordinates.getY()] += keywordCount;
		indexCell.indexCellCost += keywordCount;
		ArrayList<List<Query>> queries = localHybridGridIndex.getReleventSpatialKeywordRangeQueries(dataObject, fromNeighbour);

		if (queries == null) {
			//System.out.println("Cannot find queries at" + cellCoordinates.getX() + "," + cellCoordinates.getY() + "my partition" + myPartition.toString());
			return;
		}

		

		//for every source 
		for (List<Query> srcQueryList : queries) {
			if(srcQueryList.size()==0||srcQueryList==null) continue;
			this.totalCost += srcQueryList.size();
			this.xColumnCost[cellCoordinates.getX()] += srcQueryList.size();
			this.yRowCost[cellCoordinates.getY()] += srcQueryList.size();
			indexCell.indexCellCost += srcQueryList.size();
			ArrayList<Integer> qualifiedQueriesIds = new ArrayList<Integer>();
			String queryScrId = null;
			if (srcQueryList.size() > 0) {
				queryScrId = srcQueryList.get(0).getSrcId();
				for (Query q : srcQueryList) {
					if (TextualPredicate.OVERlAPS.equals(q.getTextualPredicate()) || TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(), q.getQueryText(), q.getTextualPredicate())) {
						qualifiedQueriesIds.add(q.getQueryId());
					}
				}
				if (qualifiedQueriesIds.size() > 0)
					generateOutput(qualifiedQueriesIds, queryScrId, dataObject, Command.addCommand);
			}
		}
		//	}
	}

	protected PointOnAxis getBestSplit() {
		LocalBestSplitInfo localBestSplitInfoX = getBestSplitPositionX();
		LocalBestSplitInfo localBestSplitInfoY = getBestSplitPositionY();
		PointOnAxis bestSplit;
		if (localBestSplitInfoX.bestSplitCost < localBestSplitInfoY.bestSplitCost)
			bestSplit = new PointOnAxis(0, localBestSplitInfoX.splitPosition);
		else
			bestSplit = new PointOnAxis(1, localBestSplitInfoY.splitPosition);
		return bestSplit;
	}

	@Override
	boolean handleDataObject(Tuple tuple) throws Exception {

		return super.handleDataObject(tuple);
	}

	@Override
	public void handleQuery(Tuple tuple) {

		super.handleQuery(tuple);
	}

	@Override
	public void handleControlMessage(Tuple tuple) {
		Control controlMessage = (Control) tuple.getValueByField(SpatioTextualConstants.control);
		if (Control.LOAD_BALANCE.equals(controlMessage.getControlMessageType())) {
			LoadBalanceMessage balanceMessage = controlMessage.getLeadBalanceMessage();
			if (LoadBalanceMessage.MERGE.equals(balanceMessage.getLoadBalanceMessageType())) {
				//	System.out.println("///////////////////////////////////////// " + myId + " : recived Merge with TaskId: " + balanceMessage.getSendTo());
				isMerge = true;
				Integer receipinetTaskId = balanceMessage.getSendTo();
				Integer sourceIndexingBoltId = tuple.getSourceTask();

				//send all the data to the ExecutorBolt in the tuple
				//send data objects and queries of the 1st half
				sendIndexCellsInBlocks(receipinetTaskId, this.myPartition, LoadBalanceMessage.MERGE_DATA);
				toClearCells = this.myPartition;
				this.myPartition = new Cell(0, 0, 0, 0);
				this.myPartition.index = selfTaskIndex;
				//updateLocalIndexForNewParition();

				//finish sending data ...... notify IndexBolts
				//				for(int i =0;i<2;i++){
				Control newControl = new Control();
				newControl.setControlMessageType(Control.LOAD_BALANCE);
				LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
				newLoadBalanceMessage.setType(LoadBalanceMessage.MERGE);
				newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.FINISHED);
				newControl.setLeadBalanceMessage(newLoadBalanceMessage);
				collector.emitDirect(sourceIndexingBoltId, SpatioTextualConstants.getBoltIndexControlStreamId(id), new Values(newControl));
				//				}
			} else if (LoadBalanceMessage.SPLIT.equals(balanceMessage.getLoadBalanceMessageType())) {
				//System.out.println("///////////////////////////////////////// " + myId + " : recived Split to AuxTaskId: " + balanceMessage.getSendTo());
				//send the data out of the newCell in the tuple to the ExecutorBolt in the tuple (auxiliaryExecutor)
				isSplit = true;
				//Cell newCell = ( Cell) tuple.getValue(1);//new self boundaries 
				Partition newCell = balanceMessage.getNewCell();//new self boundaries 
				this.myPartition = new Cell(newCell);
				this.myPartition.index = selfTaskIndex;
				//updateLocalIndexForNewParition();
				//Cell auxCell = (Cell) tuple.getValue(2);// boundaries of data and queries to send 
				Partition auxCell = balanceMessage.getAuxCell();// boundaries of data and queries to send 
				toClearCells = new Cell(auxCell);
				Integer receipinetTaskId = balanceMessage.getSendTo();
				Integer sourceIndexingBoltId = tuple.getSourceTask();
				//send all the data to the ExecutorBolt in the tuple
				sendIndexCellsInBlocks(receipinetTaskId, new Cell(auxCell), LoadBalanceMessage.SPLIT_DATA);

				//finish sending data ...... notify IndexBolt
				//for (int i = 0; i < 2; i++) {
				Control newControl = new Control();
				newControl.setControlMessageType(Control.LOAD_BALANCE);
				LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
				newLoadBalanceMessage.setType(LoadBalanceMessage.SPLIT);
				newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.FINISHED);
				newControl.setLeadBalanceMessage(newLoadBalanceMessage);
				collector.emitDirect(sourceIndexingBoltId, SpatioTextualConstants.getBoltIndexControlStreamId(id), new Values(newControl));
				//}
			} else if (LoadBalanceMessage.SHIFT.equals(balanceMessage.getLoadBalanceMessageType())) {
				//System.out.println("///////////////////////////////////////// " + myId + " : recived Split to AuxTaskId: " + balanceMessage.getSendTo());
				//send the data out of the newCell in the tuple to the ExecutorBolt in the tuple (auxiliaryExecutor)
				isSplit = true;
				//Cell newCell = ( Cell) tuple.getValue(1);//new self boundaries 
				Partition newCell = balanceMessage.getNewCell();//new self boundaries 
				this.myPartition = new Cell(newCell);
				this.myPartition.index = selfTaskIndex;
				//updateLocalIndexForNewParition();
				//Cell auxCell = (Cell) tuple.getValue(2);// boundaries of data and queries to send 
				Partition auxCell = balanceMessage.getAuxCell();// boundaries of data and queries to send 
				toClearCells = new Cell(auxCell);
				Integer receipinetTaskId = balanceMessage.getSendTo();
				Integer sourceIndexingBoltId = tuple.getSourceTask();
				//send all the data to the ExecutorBolt in the tuple
				sendIndexCellsInBlocks(receipinetTaskId, new Cell(auxCell), LoadBalanceMessage.SPLIT_DATA);

				//finish sending data ...... notify IndexBolt
				//for (int i = 0; i < 2; i++) {
				Control newControl = new Control();
				newControl.setControlMessageType(Control.LOAD_BALANCE);
				LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
				newLoadBalanceMessage.setType(LoadBalanceMessage.SPLIT);
				newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.FINISHED);
				newControl.setLeadBalanceMessage(newLoadBalanceMessage);
				collector.emitDirect(sourceIndexingBoltId, SpatioTextualConstants.getBoltIndexControlStreamId(id), new Values(newControl));
				//}
			} 
			else if (LoadBalanceMessage.MERGE_DATA.equals(balanceMessage.getLoadBalanceMessageType())) {
				isReceivedMerge = true;
				//store data
				if (controlMessage.indexCell != null)
					handleIncommingIndexCellsFromMergeSplit(controlMessage.srcId, controlMessage.indexCell);
				if (controlMessage.indexCells != null)
					handleIncommingIndexCellsFromMergeSplit(controlMessage.srcId, controlMessage.indexCells);
				//	System.out.println("............................... " + myId + " : recived MergeData ");
			} else if (LoadBalanceMessage.SPLIT_DATA.equals(balanceMessage.getLoadBalanceMessageType())) {
				isReceivedSplit = true;
				//store data 
				if (controlMessage.indexCell != null)
					handleIncommingIndexCellsFromMergeSplit(controlMessage.srcId, controlMessage.indexCell);
				if (controlMessage.indexCells != null)
					handleIncommingIndexCellsFromMergeSplit(controlMessage.srcId, controlMessage.indexCells);
				//	System.out.println("............................... " + myId + " : recived SplitData ");
			} else if (LoadBalanceMessage.GOIING_TO_BARRIER.equals(balanceMessage.getLoadBalanceMessageType())) {
				//Send to all executorBolts and wait their responses
				//	System.out.println("............................... " + myId + " : recived GoingToBarrier from task: " + tuple.getSourceTask());
				numberOfGoingToBarrier++;
				if (numberOfGoingToBarrier >= numberParallelismIndexBolts&&balanceMessage.getPlanNumber()>lastPlanNumber) {
					lastPlanNumber=balanceMessage.getPlanNumber();
					System.out.println("............................... " + myId + " : All indexBolts entered barrier, send new partations to ExecutorBols ");
					numberOfGoingToBarrier = 0;
					Integer newAuxiliaryExecutorTask = balanceMessage.getNewAuxiliaryExecutorTask();
					ArrayList<Cell> newPartitions = (ArrayList<Cell>) balanceMessage.getNewPartitions();
					this.selfBounds = new Rectangle(new Point(0.0, 0.0), new Point(0.0, 0.0));
					this.myPartition = new Cell(0, 0, 0, 0);
					this.myPartition.index = this.selfTaskIndex;
					for (Cell p : newPartitions) {
						if (this.selfTaskIndex == p.index) {
							this.selfBounds = new Rectangle(new Point(p.getLeft() * step, p.getBottom() * step), new Point(p.getRight() * step, p.getTop() * step));
							this.myPartition = p;

						}

					}
					//updateLocalIndexForNewParition();
					Control newControl;
					for (Partition c : newPartitions) {
						newControl = new Control();
						newControl.setControlMessageType(Control.LOAD_BALANCE);
						LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
						newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.ReplyWhenReceived);
						newLoadBalanceMessage.setParition(c);
						newControl.setLeadBalanceMessage(newLoadBalanceMessage);
						collector.emitDirect(evaluatorBoltTasks.get(c.index), SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(newControl));

					}
					Partition auxPartition = new Partition(0, 0, 0, 0);
					if(newAuxiliaryExecutorTask==null||globalIndex==null||globalIndex.getTaskIdToIndex()==null)
						System.err.println("Error in newAuxiliaryExecutorTask");
					try{
					auxPartition.index = globalIndex.getTaskIdToIndex().get(newAuxiliaryExecutorTask);
					}catch(Exception e ){
						System.err.println("Error in newAuxiliaryExecutorTask");
					}
					newControl = new Control();
					newControl.setControlMessageType(Control.LOAD_BALANCE);
					LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
					newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.ReplyWhenReceived);
					newLoadBalanceMessage.setParition(auxPartition);
					newControl.setLeadBalanceMessage(newLoadBalanceMessage);
					collector.emitDirect(newAuxiliaryExecutorTask, SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(newControl));
				}
			} else if (LoadBalanceMessage.ReplyWhenReceived.equals(balanceMessage.getLoadBalanceMessageType())) {
				//send remaining data and queries that need to be sent, send Reply stream, update myPartition with the new one, the two half, and mySplitPoint, reset statistics
				myPartition = new Cell(balanceMessage.getParition());
				myPartition.index = selfTaskIndex;
				updateLocalIndexForNewParition();
				//mySplitPoint = getBestSplit();
				totalCostUnMatched = 0;
				for (int i = 0; i < fineGridGran; i++) {
					this.xColumnCostUnMatched[i] = 0;
				}
				for (int i = 0; i < fineGridGran; i++) {
					this.yRowCostUnMatched[i] = 0;
				}
			//	_inputDataCountMetric.getValueAndReset();
				//	System.out.println("............................... " + myId + " : recived ReplyWhenReceived Partition info. of index = " + myPartition.index);

				if (isSplit) {
					//clear whatever structures send
					if (toClearCells != null) {
						clearAllIndexCellInPartition(toClearCells);
						toClearCells = null;
						mergeCosts(false);
					}
					//	System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS " + myId + " isSplit Partition info. of index = " + myPartition.index);
				} else if (isMerge) {
					//clear all your sturctures
					if (toClearCells != null) {
						clearAllIndexCellInPartition(toClearCells);
						toClearCells = null;
						mergeCosts(false);
					}
					//	System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM " + myId + " isMerge Partition info. of index = " + myPartition.index);
				} else if (isReceivedSplit) {
					//Process incommming data 
					//Currently do nothing as all data is being directly indexed
					mergeCosts(true);
					//	System.out.println("SSSSSSSSSSSSSRRRRRRRRRRRRRRRRRRR " + myId + " isReceivedSplit Partition info. of index = " + myPartition.index);
				} else if (isReceivedMerge) {
					mergeCosts(true);
					//	System.out.println("MMMMMMMMMMMMMRRRRRRRRRRRRRRRRRRR " + myId + " isReceivedMerge Partition info. of index = " + myPartition.index);
				}
				Control newControl = new Control();
				newControl.setControlMessageType(Control.LOAD_BALANCE);
				LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
				newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.Reply);
				newControl.setLeadBalanceMessage(newLoadBalanceMessage);
				collector.emitDirect(tuple.getSourceTask(), SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(newControl));

				//update cost for all cells after split and merge 
				isSplit = false;
				isMerge = false;
				isReceivedMerge = false;
				isReceivedSplit = false;
			} else if (LoadBalanceMessage.Reply.equals(balanceMessage.getLoadBalanceMessageType())) {
				//Count Reply, until reach numberParallelismExecutors   
				//	System.out.println("............................... " + myId + " : recived Reply from task: " + tuple.getSourceTask());
				numberOfReceivedReply++;
				if (numberOfReceivedReply > numberParallelismExecutors) { //without Zookeeper Barrier
					//	System.out.println("BRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRB " + myId + " : START Removing Barrier ");
					//for (int i = 0; i < 2; i++) {
					Control newControl = new Control();
					newControl.setControlMessageType(Control.LOAD_BALANCE);
					LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
					newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessage.FINISHED);
					newControl.setLeadBalanceMessage(newLoadBalanceMessage);
					for (int IndexingBoltId : indexTasks) {
						collector.emitDirect(IndexingBoltId, SpatioTextualConstants.getBoltIndexControlStreamId(id), new Values(newControl));
					}
					//	}
					numberOfReceivedReply = 0;
					//	System.out.println("BRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRB " + myId + " : Barrier Removed ");
				}
			} else {
				System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX " + myId + " : recived unknown control massege from = " + tuple.getSourceGlobalStreamid());
			}
		} else if (Control.CORRECTNESS.equals(controlMessage.getControlMessageType())) {
			handleCorrecntnessMessageOfTask(controlMessage);
		}
	}

	void updateLocalIndexForNewParition() {
		//System.out.println("Updating self partition");
		this.selfBounds = new Rectangle(new Point(myPartition.getLeft() * step, myPartition.getBottom() * step), new Point(myPartition.getRight() * step, myPartition.getTop() * step));
		Iterator<Entry<String, DataSourceInformation>> itr = sourcesInformations.entrySet().iterator();
		while (itr.hasNext()) {
			DataSourceInformation sourceInfo = itr.next().getValue();
			if (DataSourceType.DATA_SOURCE.equals(sourceInfo.dataSourceType)) {
				((LocalHybridGridIndex) sourceInfo.localHybridIndex).myPartition = myPartition;
				((LocalHybridGridIndex) sourceInfo.localHybridIndex).selfBounds = this.selfBounds;
				sourceInfo.selfBounds = this.selfBounds;

			}
		}
	}

	void handleIncommingIndexCellsFromMergeSplit(String srcId, ArrayList<IndexCell> indexCells) {
		LocalHybridGridIndex localIdnex = ((LocalHybridGridIndex) sourcesInformations.get(srcId).getLocalHybridIndex());
		for (IndexCell indexCell : indexCells) {
			this.totalCost += indexCell.indexCellCost;
			this.xColumnCost[indexCell.globalCoordinates.getX()] += indexCell.indexCellCost;
			this.yRowCost[indexCell.globalCoordinates.getY()] += indexCell.indexCellCost;
			indexCell.transmitted = false;
			localIdnex.addIndexCellsFromPartition(indexCell,globalIndex.isTextAware());
		}
	}

	void handleIncommingIndexCellsFromMergeSplit(String srcId, IndexCell indexCell) {
		this.totalCost += indexCell.indexCellCost;
		this.xColumnCost[indexCell.globalCoordinates.getX()] += indexCell.indexCellCost;
		this.yRowCost[indexCell.globalCoordinates.getY()] += indexCell.indexCellCost;
		
		((LocalHybridGridIndex) sourcesInformations.get(srcId).getLocalHybridIndex()).addIndexCellsFromPartition(indexCell,globalIndex.isTextAware());
		indexCell.transmitted = false;
		//System.out.println("Recieved index cell " + indexCell.globalCoordinates.getX() + "," + indexCell.globalCoordinates.getY() );
		//	incommingCells[indexCell.globalCoordinates.getX()][indexCell.globalCoordinates.getY()]=true;
		//for debugging

		//	System.out.println(" queries size="+indexCell.queriesInvertedList.size() +indexCell.queriesInvertedList.entrySet().toString());

	}

	/**
	 * This method is corretness of execution at the adapvtiviy
	 */
	void forwardQueryTorecpientTask(Query q) {
		Control newControl = new Control();
		newControl.setControlMessageType(Control.CORRECTNESS);
		newControl.setSingleQuery(q);
		collector.emitDirect(receipinetTaskId, SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(newControl));
	}

	/**
	 * This method is corretness of execution at the adapvtiviy
	 */
	void forwardDataObjectTorecpientTask(DataObject object) {
		Control newControl = new Control();
		newControl.setControlMessageType(Control.CORRECTNESS);
		newControl.setSinglDataObject(object);
		newControl.setQueriesList(null);
		collector.emitDirect(receipinetTaskId, SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(newControl));
	}

	/**
	 * This method is corretness of execution at the adapvtiviy
	 */
	void handleCorrecntnessMessageOfTask(Control control) {

		if (control.getSinglDataObject() != null) {
			
				handleVolatileDataObject(control.getSinglDataObject() );
			
		}
		if (control.getSingleQuery() != null) {
				handleContinousQuery(control.getSingleQuery());
		}
	}

	void mergeCosts(Boolean add) {
//		if (add) {
//			totalCost += totalCostOther;
//			for (int i = 0; i < fineGridGran; i++) {
//				this.xColumnCost[i] += this.xColumnCostOther[i];
//				this.xColumnCostOther[i] = 0;
//			}
//			for (int i = 0; i < fineGridGran; i++) {
//				this.yRowCost[i] += this.yRowCostOther[i];
//				this.yRowCostOther[i] = 0;
//			}
//		} else {
//			if (totalCost < totalCostOther)
//				System.err.println("Error negative cost:" + myPartition.toString());
//			totalCost -= totalCostOther;
//			for (int i = 0; i < fineGridGran; i++) {
//				this.xColumnCost[i] -= this.xColumnCostOther[i];
//				this.xColumnCostOther[i] = 0;
//			}
//			for (int i = 0; i < fineGridGran; i++) {
//				this.yRowCost[i] -= this.yRowCostOther[i];
//				this.yRowCostOther[i] = 0;
//			}
//
//		}
//		this.totalCostOther = 0;
	}

	void sendIndexCellsInBlocks(int receipinetTaskId, Cell partitionToSend, String LoadBalanceMessageType) {
		this.receipinetTaskId = receipinetTaskId;
		//Ahmed Mahmood, this method sends the data to another task for load balance 
		//In this case we will send data and queries of entire index cells, we need not index them again
		//System.out.println("]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]] " + myId + " : start sending " + LoadBalanceMessageType + " data of size = " + 10 + " and qureies of size = ");
		Iterator<Entry<String, DataSourceInformation>> itr = sourcesInformations.entrySet().iterator();
		while (itr.hasNext()) {
			DataSourceInformation sourceInfo = itr.next().getValue();
			if (DataSourceType.DATA_SOURCE.equals(sourceInfo.dataSourceType)) {
				String sourceId = sourceInfo.dataSourceId;
				
				ArrayList<IndexCell> indexCells = ((LocalHybridGridIndex) sourceInfo.localHybridIndex).getIndexCellsFromPartition(partitionToSend);
				for (IndexCell indexCell : indexCells) {
					Control newControl = new Control();
					newControl.setControlMessageType(Control.LOAD_BALANCE);
					newControl.indexCell = indexCell;
					newControl.srcId = sourceId;
					indexCell.transmitted = true;
					//this.totalCostOther += indexCell.indexCellCost;
					this.totalCost -= indexCell.indexCellCost;
					
					
//					if (this.totalCostOther > this.totalCost)
//						System.err.println("Error in the cost of data to be send ");
//					this.xColumnCostOther[indexCell.globalCoordinates.getX()] += indexCell.indexCellCost;
//					this.yRowCostOther[indexCell.globalCoordinates.getY()] += indexCell.indexCellCost;
					this.xColumnCost[indexCell.globalCoordinates.getX()] -= indexCell.indexCellCost;
					this.yRowCost[indexCell.globalCoordinates.getY()] -= indexCell.indexCellCost;
					LoadBalanceMessage newLoadBalanceMessage = new LoadBalanceMessage();
					newLoadBalanceMessage.setLoadBalanceMessageType(LoadBalanceMessageType);
					newControl.setLeadBalanceMessage(newLoadBalanceMessage);
					collector.emitDirect(receipinetTaskId, SpatioTextualConstants.getBoltBoltControlStreamId(id), new Values(newControl));

				}
				//System.out.println("]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]] " + myId + " : Done sending data to another evalautor " + LoadBalanceMessageType + " data and qureies from ");
			}
		}

	}

	void clearAllIndexCellInPartition(Cell partition) {
		Iterator<Entry<String, DataSourceInformation>> itr = sourcesInformations.entrySet().iterator();
		while (itr.hasNext()) {
			DataSourceInformation sourceInfo = itr.next().getValue();
			if (DataSourceType.DATA_SOURCE.equals(sourceInfo.dataSourceType)) {
				((LocalHybridGridIndex) sourceInfo.localHybridIndex).removeIndexCellsFromPartition(partition,globalIndex.isTextAware());
			}
		}

		//		for(int i =partition.getLeft();i<partition.getRight();i++)
		//			xColumnCost[i]=0;
		//		
		//		for(int i =partition.getBottom();i<partition.getTop();i++)
		//			yRowCost[i]=0;

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Config conf = new Config();
		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 2); //OLD:2
		return conf;
	}

	protected class PointOnAxis {
		int axis; //0 is x-coord 1 is y-coord
		int coord;

		public PointOnAxis(int axis, int coord) {
			this.axis = axis;
			this.coord = coord;
		}
	}

	/**
	 * This function processePs a persistent query input
	 * 
	 * @param input
	 */
	@Override
	public void handleContinousQuery(Query query) {

		if (query.getCommand().equals(Command.addCommand)) {
			if (!sourcesInformations.containsKey(query.getDataSrc())) {
				System.err.println("Data Source not found: " + query.getDataSrc());
				return;

			}
			queryInformationHashMap.get(query.getSrcId()).put(query.getQueryId(), query);
			if (!sourcesInformations.get(query.getDataSrc()).isVolatile() || (query.getDataSrc2() != null && !sourcesInformations.get(query.getDataSrc2()).isVolatile())) {
				//this means that this query works on existing data and hence needs to first perorm a snapshop query 
				//then register itself as a continous query and hence update its result
				handleSnapShotQuery(query);
			}
			//TODO CHeck if some more results are needed from neighbour evaluators
			Boolean completed = sourcesInformations.get(query.getDataSrc()).getLocalHybridIndex().addContinousQuery(query);
			if (query.getDataSrc2() != null)
				sourcesInformations.get(query.getDataSrc2()).getLocalHybridIndex().addContinousQuery(query);
			if (!completed)
				forwardQueryTorecpientTask(query);
		} else if (query.getCommand().equals(Command.updateCommand)) {
			if (!sourcesInformations.containsKey(query.getDataSrc())) {
				System.err.println("Data Source not found: " + query.getDataSrc());
				return;

			}
			//delete then update
			Query queryInfo = queryInformationHashMap.get(query.getSrcId()).get(query.getQueryId());
			sourcesInformations.get(query.getDataSrc()).getLocalHybridIndex().updateContinousQuery(queryInfo, query);
			if (query.getDataSrc2() != null)
				sourcesInformations.get(query.getDataSrc2()).getLocalHybridIndex().updateContinousQuery(queryInfo, query);
		} else if (query.getCommand().equals(Command.dropCommand)) {
			//only getting information from oldStored Query as the new query may not have all information and it may contain source and query ids
			Query oldQuery = queryInformationHashMap.get(query.getSrcId()).get(query.getQueryId());
			sourcesInformations.get(oldQuery.getDataSrc()).getLocalHybridIndex().dropContinousQuery(oldQuery);
			if (oldQuery.getDataSrc2() != null)
				sourcesInformations.get(oldQuery.getDataSrc2()).getLocalHybridIndex().dropContinousQuery(oldQuery);
			queryInformationHashMap.get(oldQuery.getSrcId()).remove(oldQuery.getQueryId());
		}

	}

}
