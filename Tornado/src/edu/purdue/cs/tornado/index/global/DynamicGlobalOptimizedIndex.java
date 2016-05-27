/**
 *  
 * @author Ahmed Mahmood <amahmoo@purdue.edu>
 */
package edu.purdue.cs.tornado.index.global;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.loadbalance.SplitMergeInfo;

public class DynamicGlobalOptimizedIndex extends DynamicGlobalAQWAIndex {
	static double cellTransferCost = 1;
	public PriorityQueue<Cell> shiftHeap;

	public DynamicGlobalOptimizedIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks, fineGridGran);
	}

	public DynamicGlobalOptimizedIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, ArrayList<Cell> partitions, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions, fineGridGran);
	}

	public void buildTaskIndexToParition(HashMap<Integer, Cell> taskIndexToPartition, ArrayList<Cell> partitions) {
		taskIndexToPartition.clear();
		for (Cell c : partitions) {
			taskIndexToPartition.put(c.index, c);
		}
	}

	public boolean checkMergableRight(Cell main, Cell right) {
		if (main.dimensions[1] == right.dimensions[1] && main.getRight() == right.getLeft() && main.getBottom() == right.getBottom() && main.getTop() == right.getTop())
			return true;
		else
			return false;
	}

	public boolean checkShiftableLowHighRight(Cell main, Cell right) {
		if (main.dimensions[0] > 1 && main.dimensions[1] < right.dimensions[1] && main.getRight() == right.getLeft() && (main.getBottom() == right.getBottom() || main.getTop() == right.getTop()))
			return true;
		else
			return false;
	}

	public boolean checkShiftableHighLowRight(Cell main, Cell right) {
		if (right.dimensions[1] > 1 && main.dimensions[1] > right.dimensions[1] && main.getRight() == right.getLeft() && (main.getBottom() == right.getBottom() || main.getTop() == right.getTop()))
			return true;
		else
			return false;
	}

	public boolean checkMergableUpper(Cell main, Cell upper) {
		if (main.dimensions[0] == upper.dimensions[0] && main.getTop() == upper.getBottom() && main.getLeft() == upper.getLeft() && main.getRight() == upper.getRight())
			return true;
		else
			return false;

	}

	public boolean checkShiftableLowHighUpper(Cell main, Cell upper) {
		if (main.dimensions[0] > 1 && main.dimensions[0] < upper.dimensions[0] && main.getTop() == upper.getBottom() && (main.getLeft() == upper.getLeft() || main.getRight() == upper.getRight()))
			return true;
		else
			return false;

	}

	public boolean checkShiftableHighLowUpper(Cell main, Cell upper) {
		if (upper.dimensions[1] > 1 && main.dimensions[0] > upper.dimensions[0] && main.getTop() == upper.getBottom() && (main.getLeft() == upper.getLeft() || main.getRight() == upper.getRight()))
			return true;
		else
			return false;

	}

	@Override
	public void initStructures(ArrayList<Cell> partitions) {//Note: changed from partition to Cell
		this.numberParallelismExecutors = numberOfEvaluatorTasks - 1;
		Comparator<Cell> maxComparator = new MaxHeapComparator();
		Comparator<Cell> minComparator = new MinHeapComparator();
		for (int i = 0; i < xCellsNum; i++)
			for (int j = 0; j < yCellsNum; j++)
				routingIndex[i][j] = null;
		addPartitionsToGrid(routingIndex, partitions);
		taskIndexToPartition = new HashMap<Integer, Cell>();
		buildTaskIndexToParition(taskIndexToPartition, partitions);
		minHeap = new PriorityQueue<Cell>(numberParallelismExecutors, minComparator);
		maxHeap = new PriorityQueue<Cell>(numberParallelismExecutors, maxComparator);
		shiftHeap = new PriorityQueue<Cell>(numberParallelismExecutors, maxComparator);
		buildMinMaxHeaps(partitions, routingIndex);
		try {
			this.newPartitions = (ArrayList<Cell>) deepCopy(partitions);
		} catch (Exception e) {
			System.out.println("Could not deepCopy in LoadBalancing " + e);
		}
		newRoutingIndex = new RoutingGridCell[fineGridGran][fineGridGran];
		for (int i = 0; i < xCellsNum; i++)
			for (int j = 0; j < yCellsNum; j++)
				newRoutingIndex[i][j] = null;

		addPartitionsToGrid(newRoutingIndex, newPartitions);

		//	printRoutingIndex(newRoutingIndex);
	}

	public void buildMinMaxHeaps(ArrayList<Cell> partitions, RoutingGridCell[][] routingIndex) {
		HashMap<Integer, Cell> taskIndexToPartition = new HashMap<Integer, Cell>();
		buildTaskIndexToParition(taskIndexToPartition, partitions);
		maxHeap.clear();
		minHeap.clear();
		shiftHeap.clear();

		for (Cell p : partitions) {
			(p).setScore(0);
			maxHeap.add(p);

			if (p.children == null)
				p.children = new Cell[2];
			p.children[0] = null;
			p.children[1] = null;
			//get upper partition
			//get right partition
			//check if they are mergable
			//if mergable add to the minheap 
			Integer upperPatitionIndex = null, rightPartitionIndex = null;

			if (routingIndex[p.getLeft()][p.getBottom()].rightCell != null && routingIndex[p.getLeft()][p.getBottom()].rightCell.taskIdIndex != null) {

				rightPartitionIndex = routingIndex[p.getLeft()][p.getBottom()].rightCell.taskIdIndex;
				Cell rightPartition = taskIndexToPartition.get(rightPartitionIndex);
				if (rightPartition != null) {
					Cell minHeapCell = new Cell(p.getBottom(), p.getTop(), p.getLeft(), rightPartition.getRight());
					minHeapCell.index = p.index;
					if (checkMergableRight(p, rightPartition)) {
						minHeapCell.children[0] = new Cell(p);
						minHeapCell.children[1] = new Cell(rightPartition);
						minHeapCell.setScore(0);
						minHeap.add(minHeapCell);
						Cell shiftHeapCell = new Cell(p);
						shiftHeapCell.children[0] = new Cell(p);
						shiftHeapCell.children[1] = new Cell(rightPartition);
						shiftHeapCell.setScore(0);
						shiftHeap.add(shiftHeapCell);

					} else if (checkShiftableLowHighRight(p, rightPartition) || checkShiftableHighLowRight(p, rightPartition)) {
						Cell shiftHeapCell = new Cell(p);
						shiftHeapCell.children[0] = new Cell(p);
						shiftHeapCell.children[1] = new Cell(rightPartition);
						shiftHeapCell.setScore(0);
						shiftHeap.add(shiftHeapCell);
					}
					if (routingIndex[p.getLeft()][p.getTop() - 1].rightCell != null && routingIndex[p.getLeft()][p.getTop() - 1].rightCell.taskIdIndex != null) {
						rightPartitionIndex = routingIndex[p.getLeft()][p.getTop() - 1].rightCell.taskIdIndex;
						Cell rightPartition2 = taskIndexToPartition.get(rightPartitionIndex);
						if (rightPartition2 != null && rightPartition2 != rightPartition) {
							if (checkShiftableHighLowRight(p, rightPartition2)) {
								Cell shiftHeapCell = new Cell(p);
								shiftHeapCell.children[0] = new Cell(p);
								shiftHeapCell.children[1] = new Cell(rightPartition2);
								shiftHeapCell.setScore(0);
								shiftHeap.add(shiftHeapCell);
							}
						}
					}

				} else {
					System.err.println("Error in finding neighoubrs right parition index =" + rightPartitionIndex);
				}
			}

			if (routingIndex[p.getLeft()][p.getBottom()].upperCell != null && routingIndex[p.getLeft()][p.getBottom()].upperCell.taskIdIndex != null) {
				upperPatitionIndex = routingIndex[p.getLeft()][p.getBottom()].upperCell.taskIdIndex;
				Cell upperPartition = taskIndexToPartition.get(upperPatitionIndex);
				if (upperPartition != null) {
					Cell minHeapCell = new Cell(p.getBottom(), upperPartition.getTop(), p.getLeft(), p.getRight());
					minHeapCell.index = p.index;
					if (checkMergableUpper(p, upperPartition)) {
						minHeapCell.children[0] = new Cell(p);
						minHeapCell.children[1] = new Cell(upperPartition);
						minHeapCell.setScore(0);
						minHeap.add(minHeapCell);
						Cell shiftHeapCell = new Cell(p);
						shiftHeapCell.children[0] = new Cell(p);
						shiftHeapCell.children[1] = new Cell(upperPartition);
						shiftHeapCell.setScore(0);
						shiftHeap.add(shiftHeapCell);

					} else if (checkShiftableLowHighUpper(p, upperPartition) || checkShiftableHighLowUpper(p, upperPartition)) {
						Cell shiftHeapCell = new Cell(p);
						shiftHeapCell.children[0] = new Cell(p);
						shiftHeapCell.children[1] = new Cell(upperPartition);
						shiftHeapCell.setScore(0);
						shiftHeap.add(shiftHeapCell);
					}
					if (routingIndex[p.getRight() - 1][p.getBottom()].upperCell != null && routingIndex[p.getRight() - 1][p.getBottom()].upperCell.taskIdIndex != null) {
						rightPartitionIndex = routingIndex[p.getRight() - 1][p.getBottom()].upperCell.taskIdIndex;
						Cell upperPartition2 = taskIndexToPartition.get(rightPartitionIndex);
						if (upperPartition2 != null && upperPartition2 != upperPartition) {
							if (checkShiftableHighLowRight(p, upperPartition2)) {
								Cell shiftHeapCell = new Cell(p);
								shiftHeapCell.children[0] = new Cell(p);
								shiftHeapCell.children[1] = new Cell(upperPartition2);
								shiftHeapCell.setScore(0);
								shiftHeap.add(shiftHeapCell);
							}
						}
					}
				} else {
					System.err.println("Error in finding neighoubrs upper parition index =" + upperPatitionIndex);
				}
			}

		}

	}

	public SplitMergeInfo newSplitMerge(int[][] stat) {
		//	printHeap("#1");
		SplitMergeInfo splitMergeInfo = null;

		//	printHeap("#2");
		updateMaxHeapScores(stat);
		updateMinheapScores(stat);
		updateShifHeapScores(stat);
		//	printHeap("#3");
		this.minHeap = this.minHeap;
		this.shiftHeap = this.shiftHeap;
		//	printAllminHeap("Before finding new plan");
		//find out if the one having the highest load can be splitted and accordingly shifted
		List<Cell> addToMaxHeapBeforeReturn = new ArrayList<Cell>();
		Boolean splittable = false;
		while (!maxHeap.isEmpty() && !splittable) {
			Cell toBeSplitTest = maxHeap.peek();
			//			Partition[] bestSplitsTest = SpatialHelper.split(toBeSplitTest, stat[toBeSplitTest.index][2], stat[toBeSplitTest.index][3] == 1);
			//			if (bestSplitsTest == null)
			//				return null;
			if (!checkSplittable(toBeSplitTest, true, stat) && !checkSplittable(toBeSplitTest, false, stat)) {

				//				System.out.println("CAN NOT SPLIT");
				//				System.out.println("CAN NOT SPLIT::toBeSplit: L:" + toBeSplitTest.getLeft() + " R:" + toBeSplitTest.getRight() + " T:" + toBeSplitTest.getTop() + " B:" + toBeSplitTest.getBottom() + "//  B_x = "
				//						+ toBeSplitTest.getCoords()[0] + " B_y = " + toBeSplitTest.getCoords()[1] + " B_x_d = " + toBeSplitTest.getDimensions()[0] + " B_y_d = " + toBeSplitTest.getDimensions()[1]);
				//				System.out.println("CAN NOT SPLIT::toBeSplit.getChildren()[0]: L:" + bestSplitsTest[0].getLeft() + " R:" + bestSplitsTest[0].getRight() + " T:" + bestSplitsTest[0].getTop() + " B:" + bestSplitsTest[0].getBottom()
				//						+ "//  B_x = " + bestSplitsTest[0].getCoords()[0] + " B_y = " + bestSplitsTest[0].getCoords()[1] + " B_x_d = " + bestSplitsTest[0].getDimensions()[0] + " B_y_d = " + bestSplitsTest[0].getDimensions()[1]);
				//				System.out.println("CAN NOT SPLIT::toBeSplit.getChildren()[1]: L:" + bestSplitsTest[1].getLeft() + " R:" + bestSplitsTest[1].getRight() + " T:" + bestSplitsTest[1].getTop() + " B:" + bestSplitsTest[1].getBottom()
				//						+ "//  B_x = " + bestSplitsTest[1].getCoords()[0] + " B_y = " + bestSplitsTest[1].getCoords()[1] + " B_x_d = " + bestSplitsTest[1].getDimensions()[0] + " B_y_d = " + bestSplitsTest[1].getDimensions()[1]);

				addToMaxHeapBeforeReturn.add(maxHeap.remove());
			} else {
				splittable = true;
			}
		}

		if (splittable) {
			Double shiftGain = getBestShiftGain(stat);
			Double splitMergeGain = getBestSplitMergeGain(stat);
			if (((splitMergeGain == null && shiftGain != null) || (splitMergeGain != null && shiftGain != null && shiftGain > splitMergeGain)) && shiftGain > 0) {
				//if(shiftGain != null &&shiftGain>0){
				splitMergeInfo = shiftCells(stat);
				addPartitionsToGrid(newRoutingIndex, newPartitions);
				System.gc();
				buildMinMaxHeaps(newPartitions, newRoutingIndex);
			} else if (splitMergeGain != null && splitMergeGain > 0) {
				splitMergeInfo = splitMerge(stat);
				addPartitionsToGrid(newRoutingIndex, newPartitions);
				System.gc();
				buildMinMaxHeaps(newPartitions, newRoutingIndex);
			}

			//printRoutingIndex(newRoutingIndex);

		} else {
			//			System.out.println("******************************************************************");
			//			System.out.println("CAN NOT SPLIT ANY THING ");
			//			System.out.println("******************************************************************");
			maxHeap.addAll(addToMaxHeapBeforeReturn);
			splitMergeInfo = null;
		}
		//printHeap("#4");
		//printAllminHeap("After finding new plan");
		//maxHeap.addAll(addToMaxHeapBeforeReturn);
		//addToMaxHeapBeforeReturn.clear();
		if (maxHeap.size() != numberParallelismExecutors)
			System.out.println("Error in heapsizes ");
		return splitMergeInfo;
	}

	public Double getBestShiftGain(int[][] stat) {
		if (!shiftHeap.isEmpty()) {
			if (shiftHeap.peek().getScore() != -1)
				return shiftHeap.peek().getScore();
		}
		return null;
	}

	public void updateMinheapScores(int[][] stat) {
		List<Cell> toAdd = new ArrayList<Cell>();
		while (!minHeap.isEmpty()) {

			Cell c = minHeap.remove();
			try {
				if (c.getChildren() != null && c.getChildren()[0] != null && c.getChildren()[1] != null && stat[c.getChildren()[0].index] != null && stat[c.getChildren()[1].index] != null) {
					c.setScore(stat[c.getChildren()[0].index][0] + stat[c.getChildren()[1].index][0]);
					c.getChildren()[0].setScore(stat[c.getChildren()[0].index][0]);
					c.getChildren()[1].setScore(stat[c.getChildren()[1].index][0]);
				} else {
					System.err.println("Missing stats: " + c.getChildren()[0].index + ": " + c.getChildren()[1].index);
				}
				toAdd.add(c);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		minHeap.addAll(toAdd);
	}

	public void updateMaxHeapScores(int[][] stat) {
		List<Cell> toAdd = new ArrayList<Cell>();
		while (!maxHeap.isEmpty()) {
			Cell c = maxHeap.remove();
			if (stat[c.index] != null) {
				c.setScore(stat[c.index][0]); //max heap cost function calculation .... IMPORTANT 

			} else {
				System.err.println("Missing stats:" + c.index);
			}
			toAdd.add(c);
		}
		maxHeap.addAll(toAdd);
	}

	public void updateShifHeapScores(int[][] stat) {
		ArrayList<Cell> toAdd = new ArrayList<Cell>();
		while (!shiftHeap.isEmpty()) {

			Cell c = shiftHeap.remove();

			if (c.getChildren() != null && c.getChildren()[0] != null && c.getChildren()[1] != null && stat[c.getChildren()[0].index] != null && stat[c.getChildren()[1].index] != null) {

				c.getChildren()[0].setScore(stat[c.getChildren()[0].index][0]);
				c.getChildren()[1].setScore(stat[c.getChildren()[1].index][0]);
				ArrayList<Integer> xColumnsCost0 = new ArrayList<Integer>();
				ArrayList<Integer> xColumnsCost1 = new ArrayList<Integer>();
				ArrayList<Integer> yRowCost0 = new ArrayList<Integer>();
				ArrayList<Integer> yRowCostt0 = new ArrayList<Integer>();
				double transferCost = 0;

				if (checkShiftableLowHighUpper(c.getChildren()[0], c.getChildren()[1])) {
					if (c.getChildren()[0].getScore() < c.getChildren()[1].getScore()) {
						double ratio = c.getChildren()[0].dimensions[0] / c.getChildren()[1].dimensions[0]; //this should be less than 1
						double scoreEstimate = ratio * c.getChildren()[1].getScore() + c.getChildren()[0].getScore();
						transferCost = ratio * c.getChildren()[1].dimensions[0] * c.getChildren()[1].dimensions[1] * cellTransferCost;
						if (scoreEstimate + transferCost < c.getChildren()[1].getScore())
							c.setScore(c.getChildren()[1].getScore() - Math.max(scoreEstimate, c.getChildren()[1].getScore() - scoreEstimate) - transferCost);
						else
							c.setScore(-1);
					} else
						c.setScore(-1);
				} else if (checkShiftableLowHighRight(c.getChildren()[0], c.getChildren()[1])) {
					if (c.getChildren()[0].getScore() < c.getChildren()[1].getScore()) {
						double ratio = c.getChildren()[0].dimensions[0] / c.getChildren()[1].dimensions[0]; //this should be less than 1
						double scoreEstimate = ratio * c.getChildren()[1].getScore() + c.getChildren()[0].getScore();
						transferCost = ratio * c.getChildren()[1].dimensions[0] * c.getChildren()[1].dimensions[1] * cellTransferCost;
						if (scoreEstimate + transferCost < c.getChildren()[1].getScore())
							//c.setScore(scoreEstimate);
							c.setScore(c.getChildren()[1].getScore() - Math.max(scoreEstimate, c.getChildren()[1].getScore() - scoreEstimate) - transferCost);
						else
							c.setScore(-1);
					} else
						c.setScore(-1);
				} else if (checkShiftableHighLowUpper(c.getChildren()[0], c.getChildren()[1])) {
					if (c.getChildren()[0].getScore() > c.getChildren()[1].getScore()) {
						double ratio = c.getChildren()[1].dimensions[0] / c.getChildren()[0].dimensions[0]; //this should be less than 1
						double scoreEstimate = ratio * c.getChildren()[0].getScore() + c.getChildren()[1].getScore();
						transferCost = ratio * c.getChildren()[0].dimensions[0] * c.getChildren()[0].dimensions[1] * cellTransferCost;
						if (scoreEstimate + transferCost < c.getChildren()[0].getScore())
							//c.setScore(scoreEstimate);
							c.setScore(c.getChildren()[0].getScore() - Math.max(scoreEstimate, c.getChildren()[0].getScore() - scoreEstimate) - transferCost);
						else
							c.setScore(-1);
					} else
						c.setScore(-1);
				} else if (checkShiftableHighLowRight(c.getChildren()[0], c.getChildren()[1])) {
					if (c.getChildren()[0].getScore() > c.getChildren()[1].getScore()) {
						double ratio = c.getChildren()[1].dimensions[0] / c.getChildren()[0].dimensions[0]; //this should be less than 1
						double scoreEstimate = ratio * c.getChildren()[0].getScore() + c.getChildren()[1].getScore();
						transferCost = ratio * c.getChildren()[0].dimensions[0] * c.getChildren()[0].dimensions[1] * cellTransferCost;
						if (scoreEstimate + transferCost < c.getChildren()[0].getScore())
							//c.setScore(scoreEstimate);
							c.setScore(c.getChildren()[0].getScore() - Math.max(scoreEstimate, c.getChildren()[0].getScore() - scoreEstimate) - transferCost);
						else
							c.setScore(-1);
					} else
						c.setScore(-1);
				} else {

					Boolean isHorizontal = checkMergableUpper(c.getChildren()[0], c.getChildren()[1]);
					if (isHorizontal) {
						if (c.getChildren()[0].getScore() > c.getChildren()[1].getScore()) {
							if (checkSplittable(c.getChildren()[0], true, stat)) {
								transferCost = cellTransferCost * c.getChildren()[0].dimensions[0] * (c.getChildren()[0].getTop() - stat[c.getChildren()[0].index][6]);
								double MaxScore = Math.max(stat[c.getChildren()[0].index][8] + stat[c.getChildren()[1].index][0], stat[c.getChildren()[0].index][0] - stat[c.getChildren()[0].index][8]);
								c.setScore(c.getChildren()[0].getScore() - MaxScore - transferCost);
							} else
								c.setScore(-1);
						} else {
							if (checkSplittable(c.getChildren()[1], true, stat)) {
								transferCost = cellTransferCost * c.getChildren()[1].dimensions[0] * (stat[c.getChildren()[1].index][6] - c.getChildren()[1].getBottom());
								double MaxScore = Math.max(stat[c.getChildren()[1].index][0] - stat[c.getChildren()[1].index][8] + stat[c.getChildren()[0].index][0], stat[c.getChildren()[1].index][8]);
								c.setScore(c.getChildren()[1].getScore() - MaxScore - transferCost);
							} else
								c.setScore(-1);
						}
					} else {
						if (c.getChildren()[0].getScore() > c.getChildren()[1].getScore()) {
							if (checkSplittable(c.getChildren()[0], false, stat)) {
								transferCost = cellTransferCost * c.getChildren()[0].dimensions[1] * (c.getChildren()[0].getRight() - stat[c.getChildren()[0].index][2]);
								double MaxScore = Math.max(stat[c.getChildren()[0].index][4] + stat[c.getChildren()[1].index][0], stat[c.getChildren()[0].index][0] - stat[c.getChildren()[0].index][4]);
								c.setScore(c.getChildren()[0].getScore() - MaxScore - transferCost);
							} else
								c.setScore(-1);
						} else {
							if (checkSplittable(c.getChildren()[1], false, stat)) {
								transferCost = cellTransferCost * c.getChildren()[1].dimensions[1] * (stat[c.getChildren()[1].index][2] - c.getChildren()[1].getLeft());
								double MaxScore = Math.max(stat[c.getChildren()[1].index][0] - stat[c.getChildren()[1].index][4] + stat[c.getChildren()[0].index][0], stat[c.getChildren()[1].index][4]);
								c.setScore(c.getChildren()[1].getScore() - MaxScore - transferCost);
							} else {
								c.setScore(-1);
							}
						}
					}
				}
			} else {
				System.err.println("Missing stats: " + c.getChildren()[0].index + ": " + c.getChildren()[1].index);
			}
			toAdd.add(c);

		}
		shiftHeap.addAll(toAdd);
	}

	public Boolean checkSplittable(Cell c, boolean isHorizontalSplit, int[][] stat) {
		if (isHorizontalSplit) {
			Partition[] bestSplitsTest = SpatialHelper.split(c, stat[c.index][6], true);
			if (bestSplitsTest == null || ((c.getBottom() == bestSplitsTest[0].getBottom()) && (c.getTop() == bestSplitsTest[0].getTop()) && (c.getLeft() == bestSplitsTest[0].getLeft()) && (c.getRight() == bestSplitsTest[0].getRight()))
					|| (bestSplitsTest[0].getDimensions()[0] == 0) || (bestSplitsTest[0].getDimensions()[1] == 0) || (bestSplitsTest[1].getDimensions()[0] == 0) || (bestSplitsTest[1].getDimensions()[1] == 0))
				return false;

		} else {
			Partition[] bestSplitsTest = SpatialHelper.split(c, stat[c.index][2], false);
			if (bestSplitsTest == null || ((c.getBottom() == bestSplitsTest[0].getBottom()) && (c.getTop() == bestSplitsTest[0].getTop()) && (c.getLeft() == bestSplitsTest[0].getLeft()) && (c.getRight() == bestSplitsTest[0].getRight()))
					|| (bestSplitsTest[0].getDimensions()[0] == 0) || (bestSplitsTest[0].getDimensions()[1] == 0) || (bestSplitsTest[1].getDimensions()[0] == 0) || (bestSplitsTest[1].getDimensions()[1] == 0))
				return false;
		}
		return true;
	}

	public Double getBestSplitMergeGain(int[][] stat) {
		if (minHeap.peek() != null && maxHeap.peek() != null) {
			double mergeTransrferCost = Math.min(minHeap.peek().children[0].dimensions[0] * minHeap.peek().children[0].dimensions[1], minHeap.peek().children[1].dimensions[0] * minHeap.peek().children[1].dimensions[1]) * cellTransferCost;
			Double mergeScore = minHeap.peek().getScore();
			int maxHeapCellIndex = maxHeap.peek().index;
			double horizontalSplitTransferCost = cellTransferCost * maxHeap.peek().dimensions[0] * (stat[maxHeapCellIndex][6] - maxHeap.peek().getBottom());
			double verticalSplitTransferCost = cellTransferCost * maxHeap.peek().dimensions[1] * (stat[maxHeapCellIndex][2] - maxHeap.peek().getLeft());
			Double maxHorizonalSplit = (double) Math.max(stat[maxHeapCellIndex][0] - stat[maxHeapCellIndex][8], stat[maxHeapCellIndex][8]);
			Double maxVerticalSplit = (double) Math.max(stat[maxHeapCellIndex][0] - stat[maxHeapCellIndex][4], stat[maxHeapCellIndex][4]);

			double splitMax = maxHorizonalSplit;
			double transferCost = horizontalSplitTransferCost;
			if (maxVerticalSplit < maxHorizonalSplit) {
				splitMax = maxVerticalSplit;
				transferCost = verticalSplitTransferCost;
			}
			//return stat[maxHeapCellIndex][0] - (Math.max(Math.min(maxHorizonalSplit, maxVerticalSplit), mergeScore));
			return stat[maxHeapCellIndex][0] - (Math.max(splitMax, mergeScore)) - transferCost - mergeTransrferCost;
		}
		return null;
	}
	public Boolean isBestSplitHorizontal(int[][] stat) {
		if (minHeap.peek() != null && maxHeap.peek() != null) {
			double mergeTransrferCost = Math.min(minHeap.peek().children[0].dimensions[0] * minHeap.peek().children[0].dimensions[1], minHeap.peek().children[1].dimensions[0] * minHeap.peek().children[1].dimensions[1]) * cellTransferCost;
			Double mergeScore = minHeap.peek().getScore();
			int maxHeapCellIndex = maxHeap.peek().index;
			double horizontalSplitTransferCost = cellTransferCost * maxHeap.peek().dimensions[0] * (stat[maxHeapCellIndex][6] - maxHeap.peek().getBottom());
			double verticalSplitTransferCost = cellTransferCost * maxHeap.peek().dimensions[1] * (stat[maxHeapCellIndex][2] - maxHeap.peek().getLeft());
			Double maxHorizonalSplit = (double) Math.max(stat[maxHeapCellIndex][0] - stat[maxHeapCellIndex][8], stat[maxHeapCellIndex][8]);
			Double maxVerticalSplit = (double) Math.max(stat[maxHeapCellIndex][0] - stat[maxHeapCellIndex][4], stat[maxHeapCellIndex][4]);

			double splitMax = maxHorizonalSplit;
			double transferCost = horizontalSplitTransferCost;
			if (maxVerticalSplit < maxHorizonalSplit) {
				return false;
			}
			//return stat[maxHeapCellIndex][0] - (Math.max(Math.min(maxHorizonalSplit, maxVerticalSplit), mergeScore));
			return true;
		}
		return null;
	}


	public SplitMergeInfo shiftCells(int[][] stat) {
		SplitMergeInfo splitMergeInfo = new SplitMergeInfo();
		splitMergeInfo.mergeChild0 = null;
		splitMergeInfo.mergeChild1 = null;
		splitMergeInfo.mergeParent = null;
		splitMergeInfo.delegateFindingBestSplit = false;

		Cell toBeSplit = shiftHeap.peek();
		Cell shrinkedParition = null;
		Cell mergedParition = null;
		Partition[] bestSplits = null;
		//System.out.println("SHIFTING SHIFTING SHIFTING SHIFTING SHIFTING SHIFTING SHIFTING SHIFTING ");

		if (checkShiftableLowHighUpper(toBeSplit.getChildren()[0], toBeSplit.getChildren()[1])) {

			copyTextSummery(toBeSplit.getChildren()[1].index, toBeSplit.getChildren()[0].index);
			if (toBeSplit.getChildren()[0].getLeft() == toBeSplit.getChildren()[1].getLeft()) {
				bestSplits = SpatialHelper.split(toBeSplit.getChildren()[1], toBeSplit.getChildren()[0].getRight(), false);
				splitMergeInfo.splitParent = toBeSplit.getChildren()[1];
				splitMergeInfo.splitChild0 = bestSplits[1];
				splitMergeInfo.splitChild0.index = toBeSplit.getChildren()[1].index;
				splitMergeInfo.splitChild1 = bestSplits[0];
				splitMergeInfo.splitChild1.index = toBeSplit.getChildren()[0].index;
				shrinkedParition = new Cell(bestSplits[1]);
				mergedParition = new Cell(toBeSplit.getChildren()[0].getBottom(), toBeSplit.getChildren()[1].getTop(), toBeSplit.getChildren()[0].getLeft(), toBeSplit.getChildren()[0].getRight());
				mergedParition.index = toBeSplit.getChildren()[0].index;
				shrinkedParition.index = toBeSplit.getChildren()[1].index;
			} else if (toBeSplit.getChildren()[0].getRight() == toBeSplit.getChildren()[1].getRight()) {
				bestSplits = SpatialHelper.split(toBeSplit.getChildren()[1], toBeSplit.getChildren()[0].getLeft(), false);
				splitMergeInfo.splitParent = toBeSplit.getChildren()[1];
				splitMergeInfo.splitChild0 = bestSplits[0];
				splitMergeInfo.splitChild0.index = toBeSplit.getChildren()[1].index;
				splitMergeInfo.splitChild1 = bestSplits[1];
				splitMergeInfo.splitChild1.index = toBeSplit.getChildren()[0].index;
				shrinkedParition = new Cell(bestSplits[0]);
				mergedParition = new Cell(toBeSplit.getChildren()[0].getBottom(), toBeSplit.getChildren()[1].getTop(), toBeSplit.getChildren()[0].getLeft(), toBeSplit.getChildren()[0].getRight());
				mergedParition.index = toBeSplit.getChildren()[0].index;
				shrinkedParition.index = toBeSplit.getChildren()[1].index;
			} else {
				System.err.println("Error in Low high shift");
			}

		} else if (checkShiftableHighLowUpper(toBeSplit.getChildren()[0], toBeSplit.getChildren()[1])) {
			copyTextSummery(toBeSplit.getChildren()[0].index, toBeSplit.getChildren()[1].index);
			if (toBeSplit.getChildren()[0].getLeft() == toBeSplit.getChildren()[1].getLeft()) {
				bestSplits = SpatialHelper.split(toBeSplit.getChildren()[0], toBeSplit.getChildren()[1].getRight(), false);
				splitMergeInfo.splitParent = toBeSplit.getChildren()[0];
				splitMergeInfo.splitChild0 = bestSplits[1];
				splitMergeInfo.splitChild0.index = toBeSplit.getChildren()[0].index;
				splitMergeInfo.splitChild1 = bestSplits[0];
				splitMergeInfo.splitChild1.index = toBeSplit.getChildren()[1].index;
				shrinkedParition = new Cell(bestSplits[1]);
				mergedParition = new Cell(toBeSplit.getChildren()[0].getBottom(), toBeSplit.getChildren()[1].getTop(), toBeSplit.getChildren()[0].getLeft(), toBeSplit.getChildren()[1].getRight());
				mergedParition.index = toBeSplit.getChildren()[1].index;
				shrinkedParition.index = toBeSplit.getChildren()[0].index;
			} else if (toBeSplit.getChildren()[0].getRight() == toBeSplit.getChildren()[1].getRight()) {
				bestSplits = SpatialHelper.split(toBeSplit.getChildren()[0], toBeSplit.getChildren()[1].getLeft(), false);
				splitMergeInfo.splitParent = toBeSplit.getChildren()[0];
				splitMergeInfo.splitChild0 = bestSplits[0];
				splitMergeInfo.splitChild0.index = toBeSplit.getChildren()[0].index;
				splitMergeInfo.splitChild1 = bestSplits[1];
				splitMergeInfo.splitChild1.index = toBeSplit.getChildren()[1].index;
				shrinkedParition = new Cell(bestSplits[0]);
				mergedParition = new Cell(toBeSplit.getChildren()[0].getBottom(), toBeSplit.getChildren()[1].getTop(), toBeSplit.getChildren()[1].getLeft(), toBeSplit.getChildren()[0].getRight());
				mergedParition.index = toBeSplit.getChildren()[1].index;
				shrinkedParition.index = toBeSplit.getChildren()[0].index;
			} else {
				System.err.println("Error in Low high shift");
			}
		} else if (checkShiftableLowHighRight(toBeSplit.getChildren()[0], toBeSplit.getChildren()[1])) {
			copyTextSummery(toBeSplit.getChildren()[1].index, toBeSplit.getChildren()[0].index);
			if (toBeSplit.getChildren()[0].getBottom() == toBeSplit.getChildren()[1].getBottom()) {
				bestSplits = SpatialHelper.split(toBeSplit.getChildren()[1], toBeSplit.getChildren()[0].getTop(), true);
				splitMergeInfo.splitParent = toBeSplit.getChildren()[1];
				splitMergeInfo.splitChild0 = bestSplits[1];
				splitMergeInfo.splitChild0.index = toBeSplit.getChildren()[1].index;
				splitMergeInfo.splitChild1 = bestSplits[0];
				splitMergeInfo.splitChild1.index = toBeSplit.getChildren()[0].index;
				shrinkedParition = new Cell(bestSplits[1]);
				mergedParition = new Cell(toBeSplit.getChildren()[0].getBottom(), toBeSplit.getChildren()[0].getTop(), toBeSplit.getChildren()[0].getLeft(), toBeSplit.getChildren()[1].getRight());
				mergedParition.index = toBeSplit.getChildren()[0].index;
				shrinkedParition.index = toBeSplit.getChildren()[1].index;
			} else if (toBeSplit.getChildren()[0].getTop() == toBeSplit.getChildren()[1].getTop()) {
				bestSplits = SpatialHelper.split(toBeSplit.getChildren()[1], toBeSplit.getChildren()[0].getBottom(), true);
				splitMergeInfo.splitParent = toBeSplit.getChildren()[1];
				splitMergeInfo.splitChild0 = bestSplits[0];
				splitMergeInfo.splitChild0.index = toBeSplit.getChildren()[1].index;
				splitMergeInfo.splitChild1 = bestSplits[1];
				splitMergeInfo.splitChild1.index = toBeSplit.getChildren()[0].index;
				shrinkedParition = new Cell(bestSplits[0]);
				mergedParition = new Cell(toBeSplit.getChildren()[0].getBottom(), toBeSplit.getChildren()[0].getTop(), toBeSplit.getChildren()[0].getLeft(), toBeSplit.getChildren()[1].getRight());
				mergedParition.index = toBeSplit.getChildren()[0].index;
				shrinkedParition.index = toBeSplit.getChildren()[1].index;
			} else {
				System.err.println("Error in Low high shift");
			}
		} else if (checkShiftableHighLowRight(toBeSplit.getChildren()[0], toBeSplit.getChildren()[1])) {
			copyTextSummery(toBeSplit.getChildren()[0].index, toBeSplit.getChildren()[1].index);
			if (toBeSplit.getChildren()[0].getBottom() == toBeSplit.getChildren()[1].getBottom()) {
				bestSplits = SpatialHelper.split(toBeSplit.getChildren()[0], toBeSplit.getChildren()[1].getTop(), true);
				splitMergeInfo.splitParent = toBeSplit.getChildren()[0];
				splitMergeInfo.splitChild0 = bestSplits[1];
				splitMergeInfo.splitChild0.index = toBeSplit.getChildren()[0].index;
				splitMergeInfo.splitChild1 = bestSplits[0];
				splitMergeInfo.splitChild1.index = toBeSplit.getChildren()[1].index;
				shrinkedParition = new Cell(bestSplits[1]);
				mergedParition = new Cell(toBeSplit.getChildren()[0].getBottom(), toBeSplit.getChildren()[1].getTop(), toBeSplit.getChildren()[0].getLeft(), toBeSplit.getChildren()[1].getRight());
				mergedParition.index = toBeSplit.getChildren()[1].index;
				shrinkedParition.index = toBeSplit.getChildren()[0].index;
			} else if (toBeSplit.getChildren()[0].getTop() == toBeSplit.getChildren()[1].getTop()) {
				bestSplits = SpatialHelper.split(toBeSplit.getChildren()[0], toBeSplit.getChildren()[1].getBottom(), true);
				splitMergeInfo.splitParent = toBeSplit.getChildren()[0];
				splitMergeInfo.splitChild0 = bestSplits[0];
				splitMergeInfo.splitChild0.index = toBeSplit.getChildren()[0].index;
				splitMergeInfo.splitChild1 = bestSplits[1];
				splitMergeInfo.splitChild1.index = toBeSplit.getChildren()[1].index;
				shrinkedParition = new Cell(bestSplits[0]);
				mergedParition = new Cell(toBeSplit.getChildren()[1].getBottom(), toBeSplit.getChildren()[1].getTop(), toBeSplit.getChildren()[0].getLeft(), toBeSplit.getChildren()[1].getRight());
				mergedParition.index = toBeSplit.getChildren()[1].index;
				shrinkedParition.index = toBeSplit.getChildren()[0].index;
			} else {
				System.err.println("Error in Low high shift");
			}
		}

		else {

			System.out.println("Shift requireing delegation");
			Boolean isHorizontal = checkMergableUpper(toBeSplit.getChildren()[0], toBeSplit.getChildren()[1]);

			if (isHorizontal) {
				if (toBeSplit.getChildren()[0].getScore() > toBeSplit.getChildren()[1].getScore()) {
					bestSplits = SpatialHelper.split(toBeSplit.getChildren()[0], stat[toBeSplit.getChildren()[0].index][6], true);
					splitMergeInfo.splitParent = toBeSplit.getChildren()[0];
					splitMergeInfo.splitChild0 = bestSplits[0];
					splitMergeInfo.splitChild0.index = toBeSplit.getChildren()[0].index;
					splitMergeInfo.splitChild1 = bestSplits[1];
					splitMergeInfo.splitChild1.index = toBeSplit.getChildren()[1].index;
					copyTextSummery(toBeSplit.getChildren()[0].index, toBeSplit.getChildren()[1].index);
					shrinkedParition = new Cell(bestSplits[0]);
					shrinkedParition.index = toBeSplit.getChildren()[0].index;
					mergedParition = new Cell(bestSplits[1].getBottom(), toBeSplit.getChildren()[1].getTop(), toBeSplit.getChildren()[1].getLeft(), toBeSplit.getChildren()[1].getRight());
					mergedParition.index = splitMergeInfo.splitChild1.index;
					if ((toBeSplit.getChildren()[0].dimensions[0] + toBeSplit.getChildren()[1].dimensions[0]) != (mergedParition.dimensions[0] + shrinkedParition.dimensions[0])
							|| (toBeSplit.getChildren()[0].dimensions[1] + toBeSplit.getChildren()[1].dimensions[1] != (mergedParition.dimensions[1] + shrinkedParition.dimensions[1])))
						System.err.println("Error in splits for shifts");

				} else {
					bestSplits = SpatialHelper.split(toBeSplit.getChildren()[1], stat[toBeSplit.getChildren()[1].index][6], true);
					splitMergeInfo.splitParent = toBeSplit.getChildren()[1];
					splitMergeInfo.splitChild0 = bestSplits[1];
					splitMergeInfo.splitChild0.index = toBeSplit.getChildren()[1].index;
					splitMergeInfo.splitChild1 = bestSplits[0];
					splitMergeInfo.splitChild1.index = toBeSplit.getChildren()[0].index;
					copyTextSummery(toBeSplit.getChildren()[1].index, toBeSplit.getChildren()[0].index);
					shrinkedParition = new Cell(bestSplits[1]);
					shrinkedParition.index = toBeSplit.getChildren()[1].index;
					mergedParition = new Cell(toBeSplit.getChildren()[0].getBottom(), bestSplits[0].getTop(), toBeSplit.getChildren()[1].getLeft(), toBeSplit.getChildren()[1].getRight());
					mergedParition.index = splitMergeInfo.splitChild1.index;
					if ((toBeSplit.getChildren()[0].dimensions[0] + toBeSplit.getChildren()[1].dimensions[0]) != (mergedParition.dimensions[0] + shrinkedParition.dimensions[0])
							|| (toBeSplit.getChildren()[0].dimensions[1] + toBeSplit.getChildren()[1].dimensions[1] != (mergedParition.dimensions[1] + shrinkedParition.dimensions[1])))
						System.err.println("Error in splits for shifts");

				}

			} else {
				if (toBeSplit.getChildren()[0].getScore() > toBeSplit.getChildren()[1].getScore()) {
					bestSplits = SpatialHelper.split(toBeSplit.getChildren()[0], stat[toBeSplit.getChildren()[0].index][2], false);
					splitMergeInfo.splitParent = toBeSplit.getChildren()[0];
					splitMergeInfo.splitChild0 = bestSplits[0];
					splitMergeInfo.splitChild0.index = toBeSplit.getChildren()[0].index;
					splitMergeInfo.splitChild1 = bestSplits[1];
					splitMergeInfo.splitChild1.index = toBeSplit.getChildren()[1].index;
					copyTextSummery(toBeSplit.getChildren()[0].index, toBeSplit.getChildren()[1].index);
					shrinkedParition = new Cell(bestSplits[0]);
					shrinkedParition.index = toBeSplit.getChildren()[0].index;
					mergedParition = new Cell(toBeSplit.getChildren()[1].getBottom(), toBeSplit.getChildren()[1].getTop(), bestSplits[1].getLeft(), toBeSplit.getChildren()[1].getRight());
					mergedParition.index = splitMergeInfo.splitChild1.index;
					if ((toBeSplit.getChildren()[0].dimensions[0] + toBeSplit.getChildren()[1].dimensions[0]) != (mergedParition.dimensions[0] + shrinkedParition.dimensions[0])
							|| (toBeSplit.getChildren()[0].dimensions[1] + toBeSplit.getChildren()[1].dimensions[1] != (mergedParition.dimensions[1] + shrinkedParition.dimensions[1])))
						System.err.println("Error in splits for shifts");

				} else {
					bestSplits = SpatialHelper.split(toBeSplit.getChildren()[1], stat[toBeSplit.getChildren()[1].index][2], false);
					splitMergeInfo.splitParent = toBeSplit.getChildren()[1];
					splitMergeInfo.splitChild0 = bestSplits[1];
					splitMergeInfo.splitChild0.index = toBeSplit.getChildren()[1].index;
					splitMergeInfo.splitChild1 = bestSplits[0];
					splitMergeInfo.splitChild1.index = toBeSplit.getChildren()[0].index;
					copyTextSummery(toBeSplit.getChildren()[1].index, toBeSplit.getChildren()[0].index);
					shrinkedParition = new Cell(bestSplits[1]);
					shrinkedParition.index = toBeSplit.getChildren()[1].index;
					mergedParition = new Cell(toBeSplit.getChildren()[1].getBottom(), toBeSplit.getChildren()[1].getTop(), toBeSplit.getChildren()[0].getLeft(), bestSplits[0].getRight());
					mergedParition.index = splitMergeInfo.splitChild1.index;
					if ((toBeSplit.getChildren()[0].dimensions[0] + toBeSplit.getChildren()[1].dimensions[0]) != (mergedParition.dimensions[0] + shrinkedParition.dimensions[0])
							|| (toBeSplit.getChildren()[0].dimensions[1] + toBeSplit.getChildren()[1].dimensions[1] != (mergedParition.dimensions[1] + shrinkedParition.dimensions[1])))
						System.err.println("Error in splits for shifts");

				}
			}
		}
		removeParition(newPartitions, splitMergeInfo.splitChild0.index);
		removeParition(newPartitions, splitMergeInfo.splitChild1.index);
		newPartitions.add(shrinkedParition);
		newPartitions.add(mergedParition);
		splitMergeInfo.newAuxiliaryIndex = auxilaryIndex;
		return splitMergeInfo;

	}

	public SplitMergeInfo splitMerge(int[][] stat) {
		SplitMergeInfo splitMergeInfo = new SplitMergeInfo();
		splitMergeInfo.delegateFindingBestSplit = false;
		// Removing
		Cell toBeMerged = minHeap.remove();
		int emptyIndex;

		if (toBeMerged.getChildren()[0].getScore() > toBeMerged.getChildren()[1].getScore()) {
			toBeMerged.index = toBeMerged.getChildren()[0].index;
			splitMergeInfo.newAuxiliaryIndex = toBeMerged.getChildren()[1].index;
			emptyIndex = toBeMerged.getChildren()[1].index;

		} else {
			toBeMerged.index = toBeMerged.getChildren()[1].index;
			splitMergeInfo.newAuxiliaryIndex = toBeMerged.getChildren()[0].index;
			emptyIndex = toBeMerged.getChildren()[0].index;

		}
		copyTextSummery(toBeMerged.index, splitMergeInfo.newAuxiliaryIndex);
		splitMergeInfo.mergeParent = toBeMerged;
		splitMergeInfo.mergeChild0 = toBeMerged.getChildren()[0];
		splitMergeInfo.mergeChild1 = toBeMerged.getChildren()[1];
		removeParition(newPartitions, toBeMerged.index);
		removeParition(newPartitions, splitMergeInfo.newAuxiliaryIndex);
		newPartitions.add(toBeMerged);

		Boolean isBestSplitHorizontal = isBestSplitHorizontal(stat);
		Cell toBeSplit = maxHeap.remove();

		Partition[] bestSplitsHor = SpatialHelper.split(toBeSplit, stat[toBeSplit.index][6], true);
		if (bestSplitsHor == null
				|| ((toBeSplit.getBottom() == bestSplitsHor[0].getBottom()) && (toBeSplit.getTop() == bestSplitsHor[0].getTop()) && (toBeSplit.getLeft() == bestSplitsHor[0].getLeft()) && (toBeSplit.getRight() == bestSplitsHor[0].getRight()))
				|| (bestSplitsHor[0].getDimensions()[0] == 0) || (bestSplitsHor[0].getDimensions()[1] == 0) || (bestSplitsHor[1].getDimensions()[0] == 0) || (bestSplitsHor[1].getDimensions()[1] == 0))
			bestSplitsHor = null;
		
		
		Partition[]	bestSplitsVer = SpatialHelper.split(toBeSplit, stat[toBeSplit.index][2], false);
		if (bestSplitsVer == null
				|| ((toBeSplit.getBottom() == bestSplitsVer[0].getBottom()) && (toBeSplit.getTop() == bestSplitsVer[0].getTop()) && (toBeSplit.getLeft() == bestSplitsVer[0].getLeft()) && (toBeSplit.getRight() == bestSplitsVer[0].getRight()))
				|| (bestSplitsVer[0].getDimensions()[0] == 0) || (bestSplitsVer[0].getDimensions()[1] == 0) || (bestSplitsVer[1].getDimensions()[0] == 0) || (bestSplitsVer[1].getDimensions()[1] == 0))
			bestSplitsVer = null;
		//		Partition[] bestSplits = SpatialHelper.split(toBeSplit, stat[toBeSplit.index][2], );
		//		if(bestSplits==null)
		//			 SpatialHelper.split(toBeSplit, stat[toBeSplit.index][2], stat[toBeSplit.index][3] == 1);
		Partition[] bestSplits=bestSplitsVer;
		if(bestSplitsHor!=null&&isBestSplitHorizontal)
			bestSplits=bestSplitsHor;
		if (bestSplits != null) {
			toBeSplit.getChildren()[0] = new Cell(bestSplits[0].getBottom(), bestSplits[0].getTop(), bestSplits[0].getLeft(), bestSplits[0].getRight());
			toBeSplit.getChildren()[1] = new Cell(bestSplits[1].getBottom(), bestSplits[1].getTop(), bestSplits[1].getLeft(), bestSplits[1].getRight());
			toBeSplit.getChildren()[0].setParent(toBeSplit);
			toBeSplit.getChildren()[1].setParent(toBeSplit);
			//			System.out.println("toBeSplit: L:" + toBeSplit.getLeft() + " R:" + toBeSplit.getRight() + " T:" + toBeSplit.getTop() + " B:" + toBeSplit.getBottom() + "//  B_x = " + toBeSplit.getCoords()[0] + " B_y = "
			//					+ toBeSplit.getCoords()[1] + " B_x_d = " + toBeSplit.getDimensions()[0] + " B_y_d = " + toBeSplit.getDimensions()[1]);
			//			System.out.println("toBeSplit.getChildren()[0]: L:" + toBeSplit.getChildren()[0].getLeft() + " R:" + toBeSplit.getChildren()[0].getRight() + " T:" + toBeSplit.getChildren()[0].getTop() + " B:"
			//					+ toBeSplit.getChildren()[0].getBottom() + "//  B_x = " + toBeSplit.getChildren()[0].getCoords()[0] + " B_y = " + toBeSplit.getChildren()[0].getCoords()[1] + " B_x_d = " + toBeSplit.getChildren()[0].getDimensions()[0]
			//					+ " B_y_d = " + toBeSplit.getChildren()[0].getDimensions()[1]);
			//			System.out.println("toBeSplit.getChildren()[1]: L:" + toBeSplit.getChildren()[1].getLeft() + " R:" + toBeSplit.getChildren()[1].getRight() + " T:" + toBeSplit.getChildren()[1].getTop() + " B:"
			//					+ toBeSplit.getChildren()[1].getBottom() + "//  B_x = " + toBeSplit.getChildren()[1].getCoords()[0] + " B_y = " + toBeSplit.getChildren()[1].getCoords()[1] + " B_x_d = " + toBeSplit.getChildren()[1].getDimensions()[0]
			//					+ " B_y_d = " + toBeSplit.getChildren()[1].getDimensions()[1]);
			toBeSplit.getChildren()[0].index = toBeSplit.index;
			toBeSplit.getChildren()[1].index = auxilaryIndex;
			copyTextSummery(toBeSplit.getChildren()[0].index, toBeSplit.getChildren()[1].index);
			splitMergeInfo.splitParent = toBeSplit;
			splitMergeInfo.splitChild0 = toBeSplit.getChildren()[0];
			splitMergeInfo.splitChild1 = toBeSplit.getChildren()[1];

			removeParition(newPartitions, toBeSplit.index);
			newPartitions.add(toBeSplit.getChildren()[0]);
			newPartitions.add(toBeSplit.getChildren()[1]);

			this.auxilaryIndex = emptyIndex;
			return splitMergeInfo;
		} else
			return null;
	}

	public Boolean removeParition(ArrayList<Cell> partitions, Integer index) {
		Boolean found = false;
		for (int i = 0; i < partitions.size(); i++) {
			if (partitions.get(i).index == index) {
				partitions.remove(i);
				found = true;
				break;
			}
		}
		return found;
	}

	@Override
	public void switchToNewPartitions() {
		try {
			RoutingGridCell[][] temp1 = routingIndex;
			RoutingGridCell[][] temp2 = newRoutingIndex;
			routingIndex = temp2;
			newRoutingIndex = temp1;
			partitions = newPartitions;
			newPartitions = (ArrayList<Cell>) deepCopy(partitions);
			buildTaskIndexToParition(taskIndexToPartition, partitions);
		} catch (Exception e) {
			System.out.println("Could not copy in LoadBalancing " + e);
		}
	}
}
