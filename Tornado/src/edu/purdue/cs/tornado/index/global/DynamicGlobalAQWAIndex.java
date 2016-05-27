/**
 *  
 * @author Anas Daghistani <anas@purdue.edu>
 *
 */
package edu.purdue.cs.tornado.index.global;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.loadbalance.SplitMergeInfo;

public class DynamicGlobalAQWAIndex extends GlobalOptimizedPartitionedIndex {//GlobalOptimizedPartitionedTextAwareIndex {//responsible of load balance;
	public RoutingGridCell[][] newRoutingIndex;
	public Integer numberParallelismExecutors; // no auxiliary evaluator 
	public PriorityQueue<Cell> minHeap;
	public PriorityQueue<Cell> maxHeap;
	public ArrayList<Cell> newPartitions;//Note: changed from partition to Cell
	public Integer auxilaryIndex;

	public DynamicGlobalAQWAIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks, null, fineGridGran);
		System.out.println("______________________________________________ number of partitions: " + this.partitions.size());

	}

	public DynamicGlobalAQWAIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, ArrayList<Cell> partitions, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions, fineGridGran);
		System.out.println("______________________________________________ number of partitions: " + this.partitions.size());
	}

	@Override
	public ArrayList<Cell> getInitialPartitions() {
		return PartitionsHelper.getGridBasedParitions(numberOfEvaluatorTasks - 1, fineGridGran, fineGridGran);
	}

	/**
	 * A point will only be send to a single bolt , no replication
	 */
	public Integer getTaskIDsContainingPoint(Point point) {
		return getTaskIDsOverlappingRecangle(new Rectangle(point, point)).get(0);
	}

	//*******************************************************************
	@Override
	public void initStructures(ArrayList<Cell> partitions) {//Note: changed from partition to Cell
		this.numberParallelismExecutors = numberOfEvaluatorTasks - 1;
		Comparator<Cell> maxComparator = new MaxHeapComparator();
		Comparator<Cell> minComparator = new MinHeapComparator();
		minHeap = new PriorityQueue<Cell>(numberParallelismExecutors, minComparator);
		maxHeap = new PriorityQueue<Cell>(numberParallelismExecutors, maxComparator);
		newRoutingIndex = new RoutingGridCell[fineGridGran][fineGridGran];
		addPartitionsToGrid(newRoutingIndex, partitions);
		addPartitionsToGrid(routingIndex, partitions);
		this.newPartitions = partitions;
		for (Cell p : partitions) {//Note: changed from partition to Cell
			(p).setScore(0);
			maxHeap.add(p);
			if (!minHeap.contains((p).getParent())) {
				if (partitions.contains((p).getParent().getChildren()[0]) && partitions.contains((p).getParent().getChildren()[1])) {
					minHeap.add((p).getParent());
				}
			}

		}
		try {
			this.partitions = (ArrayList<Cell>) deepCopy(newPartitions);
		} catch (Exception e) {
			System.out.println("Could not deepCopy in LoadBalancing " + e);
		}
	}

	public SplitMergeInfo newSplitMerge(int[][] stat) {

		printHeap("#1");

		SplitMergeInfo splitMergeInfo = new SplitMergeInfo();

		List<Cell> toAdd = new ArrayList<Cell>();
		while (!maxHeap.isEmpty()) {
			Cell c = maxHeap.remove();
			if (stat[c.index] != null) {
				c.setScore(stat[c.index][0] * stat[c.index][1]); //max heap cost function calculation .... IMPORTANT

			} else {
				System.err.println("Missing stats:" + c.index);
			}
			toAdd.add(c);
		}
		maxHeap.addAll(toAdd);
		printHeap("#2");

		toAdd.clear();
		while (!minHeap.isEmpty()) {

			Cell c = minHeap.remove();//MIN Heap cost function
			try {
				if (c.getChildren() != null && c.getChildren()[0] != null && c.getChildren()[1] != null && stat[c.getChildren()[0].index] != null && stat[c.getChildren()[1].index] != null) {
					c.setScore((stat[c.getChildren()[0].index][0] * stat[c.getChildren()[0].index][1]) + (stat[c.getChildren()[1].index][0] * stat[c.getChildren()[1].index][1]));
					toAdd.add(c);
				} else {
					System.err.println("Missing stats: " + c.getChildren()[0].index + ": " + c.getChildren()[1].index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		minHeap.addAll(toAdd);
		printHeap("#3");
		printAllminHeap("Before finding new plan");

		//find out if the one having the highest load can be spited 
		List<Cell> addToMaxHeapBeforeReturn = new ArrayList<Cell>();
		while (!maxHeap.isEmpty()) {
			Cell toBeSplitTest = maxHeap.peek();
			Partition[] bestSplitsTest = SpatialHelper.split(toBeSplitTest, stat[toBeSplitTest.index][2], stat[toBeSplitTest.index][3] == 1);
			if (((toBeSplitTest.getBottom() == bestSplitsTest[0].getBottom()) && (toBeSplitTest.getTop() == bestSplitsTest[0].getTop()) && (toBeSplitTest.getLeft() == bestSplitsTest[0].getLeft())
					&& (toBeSplitTest.getRight() == bestSplitsTest[0].getRight())) || (bestSplitsTest[0].getDimensions()[0] == 0) || (bestSplitsTest[0].getDimensions()[1] == 0) || (bestSplitsTest[1].getDimensions()[0] == 0)
					|| (bestSplitsTest[1].getDimensions()[1] == 0)) {
				System.out.println("CAN NOT SPLIT");
				//				System.out.println("CAN NOT SPLIT::toBeSplit: L:" + toBeSplitTest.getLeft() + " R:" + toBeSplitTest.getRight() + " T:" + toBeSplitTest.getTop() + " B:" + toBeSplitTest.getBottom() + "//  B_x = "
				//						+ toBeSplitTest.getCoords()[0] + " B_y = " + toBeSplitTest.getCoords()[1] + " B_x_d = " + toBeSplitTest.getDimensions()[0] + " B_y_d = " + toBeSplitTest.getDimensions()[1]);
				//				System.out.println("CAN NOT SPLIT::toBeSplit.getChildren()[0]: L:" + bestSplitsTest[0].getLeft() + " R:" + bestSplitsTest[0].getRight() + " T:" + bestSplitsTest[0].getTop() + " B:" + bestSplitsTest[0].getBottom()
				//						+ "//  B_x = " + bestSplitsTest[0].getCoords()[0] + " B_y = " + bestSplitsTest[0].getCoords()[1] + " B_x_d = " + bestSplitsTest[0].getDimensions()[0] + " B_y_d = " + bestSplitsTest[0].getDimensions()[1]);
				//				System.out.println("CAN NOT SPLIT::toBeSplit.getChildren()[1]: L:" + bestSplitsTest[1].getLeft() + " R:" + bestSplitsTest[1].getRight() + " T:" + bestSplitsTest[1].getTop() + " B:" + bestSplitsTest[1].getBottom()
				//						+ "//  B_x = " + bestSplitsTest[1].getCoords()[0] + " B_y = " + bestSplitsTest[1].getCoords()[1] + " B_x_d = " + bestSplitsTest[1].getDimensions()[0] + " B_y_d = " + bestSplitsTest[1].getDimensions()[1]);
				addToMaxHeapBeforeReturn.add(maxHeap.remove());
			} else {
				break;
			}
		}
		if (maxHeap.isEmpty()) {
			maxHeap.addAll(addToMaxHeapBeforeReturn);
			return null;
		}

		//Test pass, the first partition in maxHeap can be split, start working 
		if (minHeap.peek().getScore() < maxHeap.peek().getScore()) {
			//System.out.println("++++++---------------++++++++++++++----------- (minHeap.peek().score < maxHeap.peek().score)");

			// Removing
			Cell toBeMerged = minHeap.remove();

			int emptyIndex;
			//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& scores:: child1 = " + toBeMerged.getChildren()[0].getScore() + " child2 = " + toBeMerged.getChildren()[1].getScore());
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

			toBeMerged.setScore(0);
			splitMergeInfo.mergeParent = toBeMerged;
			splitMergeInfo.mergeChild0 = toBeMerged.getChildren()[0];
			splitMergeInfo.mergeChild1 = toBeMerged.getChildren()[1];

			newPartitions.add(toBeMerged);
			//System.out.println("toBeMerged.getParent().getChildren()[0]" + toBeMerged.getParent().getChildren()[0].index + " toBeMerged.getParent().getChildren()[1]" + toBeMerged.getParent().getChildren()[1].index);
			if (newPartitions.contains(toBeMerged.getParent().getChildren()[0]) && newPartitions.contains(toBeMerged.getParent().getChildren()[1]))
				minHeap.add(toBeMerged.getParent());

			maxHeap.remove(toBeMerged.getChildren()[0]);
			maxHeap.remove(toBeMerged.getChildren()[1]);

			newPartitions.remove(toBeMerged.getChildren()[0]);
			newPartitions.remove(toBeMerged.getChildren()[1]);

			maxHeap.add(toBeMerged);

			Cell toBeSplit = maxHeap.remove();

			Partition[] bestSplits = SpatialHelper.split(toBeSplit, stat[toBeSplit.index][2], stat[toBeSplit.index][3] == 1);

			if (bestSplits != null) {

				toBeSplit.getChildren()[0] = new Cell(bestSplits[0].getBottom(), bestSplits[0].getTop(), bestSplits[0].getLeft(), bestSplits[0].getRight());

				toBeSplit.getChildren()[0].setCost(bestSplits[0].getCost()); //not used now
				toBeSplit.getChildren()[1] = new Cell(bestSplits[1].getBottom(), bestSplits[1].getTop(), bestSplits[1].getLeft(), bestSplits[1].getRight());

				toBeSplit.getChildren()[1].setCost(bestSplits[1].getCost()); //not used now

				toBeSplit.getChildren()[0].setParent(toBeSplit);
				toBeSplit.getChildren()[1].setParent(toBeSplit);

				//				System.out.println("toBeSplit: L:" + toBeSplit.getLeft() + " R:" + toBeSplit.getRight() + " T:" + toBeSplit.getTop() + " B:" + toBeSplit.getBottom() + "//  B_x = " + toBeSplit.getCoords()[0] + " B_y = "
				//						+ toBeSplit.getCoords()[1] + " B_x_d = " + toBeSplit.getDimensions()[0] + " B_y_d = " + toBeSplit.getDimensions()[1]);
				//				System.out.println("toBeSplit.getChildren()[0]: L:" + toBeSplit.getChildren()[0].getLeft() + " R:" + toBeSplit.getChildren()[0].getRight() + " T:" + toBeSplit.getChildren()[0].getTop() + " B:"
				//						+ toBeSplit.getChildren()[0].getBottom() + "//  B_x = " + toBeSplit.getChildren()[0].getCoords()[0] + " B_y = " + toBeSplit.getChildren()[0].getCoords()[1] + " B_x_d = "
				//						+ toBeSplit.getChildren()[0].getDimensions()[0] + " B_y_d = " + toBeSplit.getChildren()[0].getDimensions()[1]);
				//				System.out.println("toBeSplit.getChildren()[1]: L:" + toBeSplit.getChildren()[1].getLeft() + " R:" + toBeSplit.getChildren()[1].getRight() + " T:" + toBeSplit.getChildren()[1].getTop() + " B:"
				//						+ toBeSplit.getChildren()[1].getBottom() + "//  B_x = " + toBeSplit.getChildren()[1].getCoords()[0] + " B_y = " + toBeSplit.getChildren()[1].getCoords()[1] + " B_x_d = "
				//						+ toBeSplit.getChildren()[1].getDimensions()[0] + " B_y_d = " + toBeSplit.getChildren()[1].getDimensions()[1]);

				toBeSplit.getChildren()[0].index = toBeSplit.index;
				toBeSplit.getChildren()[1].index = auxilaryIndex;

				copyTextSummery(toBeSplit.getChildren()[0].index, toBeSplit.getChildren()[1].index);

				splitMergeInfo.splitParent = toBeSplit;
				splitMergeInfo.splitChild0 = toBeSplit.getChildren()[0];
				splitMergeInfo.splitChild1 = toBeSplit.getChildren()[1];

				minHeap.remove(toBeSplit.getParent());

				newPartitions.remove(toBeSplit);

				maxHeap.add(toBeSplit.getChildren()[0]);
				maxHeap.add(toBeSplit.getChildren()[1]);

				newPartitions.add(toBeSplit.getChildren()[0]);
				newPartitions.add(toBeSplit.getChildren()[1]);

				minHeap.add(toBeSplit);
				toAdd.clear();
				while (!minHeap.isEmpty()) {

					Cell c = minHeap.remove();//MIN Heap cost function
					try {
						if (c.getChildren() != null && c.getChildren()[0] != null && c.getChildren()[1] != null) {
							if (c.getChildren()[0].index == emptyIndex)
								c.getChildren()[0].index = auxilaryIndex;
							if (c.getChildren()[1].index == emptyIndex)
								c.getChildren()[1].index = auxilaryIndex;
							toAdd.add(c);
						} else {
							System.err.println("Minheap has entries with not children");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				minHeap.addAll(toAdd);
				this.auxilaryIndex = emptyIndex;
				addPartitionsToGrid(newRoutingIndex, newPartitions);
			}

		} else {
			maxHeap.addAll(addToMaxHeapBeforeReturn);
			return null;
		}
		printHeap("#4");
		printAllminHeap("After finding new plan");

		maxHeap.addAll(addToMaxHeapBeforeReturn);

		if (maxHeap.size() != numberParallelismExecutors)
			System.out.println("Error in heapsizes ");
		return splitMergeInfo;
	}

	public void setEvaluatorBoltTasks(List<Integer> evaluatorBoltTasks) {
		this.evaluatorBoltTasks = evaluatorBoltTasks;
	}

	public void setNumberOfEvaluatorTasks(Integer numberOfEvaluatorTasks) {
		this.numberOfEvaluatorTasks = numberOfEvaluatorTasks;
	}

	public void setNumberParallelismExecutors(Integer numberParallelismExecutors) {
		this.numberParallelismExecutors = numberParallelismExecutors;
	}

	public void setTaskIdToIndex(HashMap<Integer, Integer> taskIdToIndex) {
		this.taskIdToIndex = taskIdToIndex;
	}

	public void switchToNewPartitions() {
		try {

			RoutingGridCell[][] temp1 = routingIndex;
			RoutingGridCell[][] temp2 = newRoutingIndex;
			routingIndex = temp2;
			newRoutingIndex = temp1;
			newPartitions = (ArrayList<Cell>) deepCopy(partitions);
		} catch (Exception e) {
			System.out.println("Could not copy in LoadBalancing " + e);
		}
	}

	public class MaxHeapComparator implements Comparator<Cell>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public int compare(Cell x, Cell y) {
			if (x.getScore() < y.getScore())
				return 1;

			if (x.getScore() > y.getScore())
				return -1;

			return 0;
		}
	}

	public class MinHeapComparator implements Comparator<Cell>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public int compare(Cell x, Cell y) {
			if (x.getScore() > y.getScore())
				return 1;

			if (x.getScore() < y.getScore())
				return -1;

			return 0;
		}
	}

	static public ArrayList<Cell> deepCopy(ArrayList<Cell> partitions) {
		ArrayList<Cell> copy = new ArrayList<Cell>();
		// read object from file
		for (Cell p : partitions) {
			Cell c = new Cell();
			c.coords = p.coords;
			c.dimensions = p.dimensions;
			c.cost = p.cost;
			c.index = p.index;
			copy.add(p);
		}
		return copy;
	}

	public void printAllminHeap(String addedString) {
		//		List<Cell> toAdd = new ArrayList<Cell>();
		//		int i = 0;
		//		while (!minHeap.isEmpty()) {
		//			Cell c = minHeap.remove();
		//			System.out.print(addedString + ": " + i + "->(" + c.getChildren()[0].index + ", " + c.getChildren()[1].index + "), ");
		//			toAdd.add(c);
		//			i++;
		//		}
		//		System.out.println();
		//		minHeap.addAll(toAdd);
	}

	public void printHeap(String s) {
		//		if (maxHeap.peek() != null)
		//			System.out.print("################################################ " + s + "maxHeap head = " + maxHeap.peek().index);
		//		if (minHeap.peek() != null && minHeap.peek().getChildren() != null && minHeap.peek().getChildren()[1] != null)
		//			System.out.println(" minHeap head child1 = " + minHeap.peek().getChildren()[0].index + " child2 = " + minHeap.peek().getChildren()[1].index);
	}

}
