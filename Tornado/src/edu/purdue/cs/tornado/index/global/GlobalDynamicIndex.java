package edu.purdue.cs.tornado.index.global;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.RTree;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.loadbalance.SplitMergeInfo;

public class GlobalDynamicIndex extends GlobalIndex{//responsible of load balance;
	
	public RTree<Cell> partitionsRTree;
	public RTree<Cell> newPartitionsRTree;
	

	private Integer numberParallelismExecutors; // no auxiliary evaluator 

	
	private PriorityQueue<Cell> minHeap;
	private PriorityQueue<Cell> maxHeap;
	
	
	public ArrayList<Cell> currentPartitions;//Note: changed from partition to Cell
	public ArrayList<Cell> newPartitions;//Note: changed from partition to Cell
	public Integer getNumberOfEvaluatorTasks() {
		return numberOfEvaluatorTasks;
	}
	public Integer getNumberParallelismExecutors() {
		return numberParallelismExecutors;
	}
	public void setNumberParallelismExecutors(Integer numberParallelismExecutors) {
		this.numberParallelismExecutors = numberParallelismExecutors;
	}
	public void setNumberOfEvaluatorTasks(Integer numberOfEvaluatorTasks) {
		this.numberOfEvaluatorTasks = numberOfEvaluatorTasks;
	}
	public List<Integer> getEvaluatorBoltTasks() {
		return evaluatorBoltTasks;
	}
	public void setEvaluatorBoltTasks(List<Integer> evaluatorBoltTasks) {
		this.evaluatorBoltTasks = evaluatorBoltTasks;
	}
	public HashMap<Integer, Integer> getTaskIdToIndex() {
		return taskIdToIndex;
	}
	public void setTaskIdToIndex(HashMap<Integer, Integer> taskIdToIndex) {
		this.taskIdToIndex = taskIdToIndex;
	}
	public GlobalDynamicIndex(Integer numberOfEvaluatorTasks,List<Integer> evaluatorBoltTasks){
		super(numberOfEvaluatorTasks, evaluatorBoltTasks);
		this.numberParallelismExecutors = numberOfEvaluatorTasks-1;
		this.numberOfEvaluatorTasks = numberOfEvaluatorTasks;
		this.evaluatorBoltTasks = evaluatorBoltTasks;
		taskIdToIndex = new HashMap<Integer, Integer>();
		for(Integer i =0;i<numberOfEvaluatorTasks;i++){
			taskIdToIndex.put(evaluatorBoltTasks.get(i), i);
		}
		
		
		xrange = SpatioTextualConstants.xMaxRange;
		yrange = SpatioTextualConstants.yMaxRange;
		
		
		initialPartitions(SpatioTextualConstants.fineGridGranularityX, SpatioTextualConstants.fineGridGranularityY);
		System.out.println("______________________________________________ number of partitions: "+this.currentPartitions.size());
		
	}
	public SplitMergeInfo newSplitMerge(int[][] stat) {

		printHeap("#1");

		SplitMergeInfo splitMergeInfo = new SplitMergeInfo();

		List<Cell> toAdd = new ArrayList<Cell>();
		while (!maxHeap.isEmpty()) {
			Cell c = maxHeap.remove();
			c.setScore ( stat[c.index][0]*stat[c.index][1]); //max heap cost function calculation .... IMPORTANT
			toAdd.add(c);
		}
		maxHeap.addAll(toAdd);
		printHeap("#2");

		toAdd.clear();
		while (!minHeap.isEmpty()) {
			Cell c = minHeap.remove();//MIN Heap cost function
			c.setScore ( (stat[c.getChildren()[0].index][0]*stat[c.getChildren()[0].index][1])+(stat[c.getChildren()[1].index][0]*stat[c.getChildren()[1].index][1]));
			toAdd.add(c);
		}
		minHeap.addAll(toAdd);
		printHeap("#3");
		printAllminHeap("Before finding new plan");

		//find out if the one having the highest load can be spited 
		List<Cell> addToMaxHeapBeforeReturn = new ArrayList<Cell>();
		while(!maxHeap.isEmpty()){
			Cell toBeSplitTest = maxHeap.peek(); 	
			Partition[] bestSplitsTest = SpatialHelper.split(toBeSplitTest, stat[toBeSplitTest.index][2], stat[toBeSplitTest.index][3]==1);
			if(((toBeSplitTest.getBottom()==bestSplitsTest[0].getBottom())&&(toBeSplitTest.getTop()==bestSplitsTest[0].getTop())
					&&(toBeSplitTest.getLeft()==bestSplitsTest[0].getLeft())&&(toBeSplitTest.getRight()==bestSplitsTest[0].getRight()))
					||(bestSplitsTest[0].getDimensions()[0]==0)||(bestSplitsTest[0].getDimensions()[1]==0)
					||(bestSplitsTest[1].getDimensions()[0]==0)||(bestSplitsTest[1].getDimensions()[1]==0)){
				System.out.println("CAN NOT SPLIT");
				System.err.println("CAN NOT SPLIT::toBeSplit: L:"+toBeSplitTest.getLeft()+" R:"+toBeSplitTest.getRight()+" T:"+toBeSplitTest.getTop()+" B:"+toBeSplitTest.getBottom()+"//  B_x = "+toBeSplitTest.getCoords()[0]+" B_y = "+toBeSplitTest.getCoords()[1]+" B_x_d = "+toBeSplitTest.getDimensions()[0]+" B_y_d = "+toBeSplitTest.getDimensions()[1]);
				System.out.println("CAN NOT SPLIT::toBeSplit.getChildren()[0]: L:"+bestSplitsTest[0].getLeft()+" R:"+bestSplitsTest[0].getRight()+" T:"+bestSplitsTest[0].getTop()+" B:"+bestSplitsTest[0].getBottom()+"//  B_x = "+bestSplitsTest[0].getCoords()[0]+" B_y = "+bestSplitsTest[0].getCoords()[1]+" B_x_d = "+bestSplitsTest[0].getDimensions()[0]+" B_y_d = "+bestSplitsTest[0].getDimensions()[1]);
				System.out.println("CAN NOT SPLIT::toBeSplit.getChildren()[1]: L:"+bestSplitsTest[1].getLeft()+" R:"+bestSplitsTest[1].getRight()+" T:"+bestSplitsTest[1].getTop()+" B:"+bestSplitsTest[1].getBottom()+"//  B_x = "+bestSplitsTest[1].getCoords()[0]+" B_y = "+bestSplitsTest[1].getCoords()[1]+" B_x_d = "+bestSplitsTest[1].getDimensions()[0]+" B_y_d = "+bestSplitsTest[1].getDimensions()[1]);
				addToMaxHeapBeforeReturn.add(maxHeap.remove());
			}
			else{
				break;
			}
		}		
		if(maxHeap.isEmpty()){
			maxHeap.addAll(addToMaxHeapBeforeReturn);
			return null;
		}

		//Test pass, the first partition in maxHeap can be split, start working 
		if (minHeap.peek().getScore() < maxHeap.peek().getScore()) { 

			System.out.println("++++++---------------++++++++++++++----------- (minHeap.peek().score < maxHeap.peek().score)");

			// Removing
			Cell toBeMerged = minHeap.remove();

			int emptyIndex;
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& scores:: child1 = "+toBeMerged.getChildren()[0].getScore()+" child2 = "+toBeMerged.getChildren()[1].getScore());
			if(toBeMerged.getChildren()[0].getScore() > toBeMerged.getChildren()[1].getScore()){
				toBeMerged.index = toBeMerged.getChildren()[0].index;
				splitMergeInfo.newAuxiliaryIndex = toBeMerged.getChildren()[1].index;
				emptyIndex = toBeMerged.getChildren()[1].index;
			}
			else{
				toBeMerged.index = toBeMerged.getChildren()[1].index;
				splitMergeInfo.newAuxiliaryIndex = toBeMerged.getChildren()[0].index;
				emptyIndex = toBeMerged.getChildren()[0].index;
			}

			toBeMerged.setScore ( 0);

			splitMergeInfo.mergeParent = toBeMerged;
			splitMergeInfo.mergeChild0 = toBeMerged.getChildren()[0];
			splitMergeInfo.mergeChild1 = toBeMerged.getChildren()[1];

			newPartitions.add(toBeMerged);	
			System.out.println("toBeMerged.getParent().getChildren()[0]"+toBeMerged.getParent().getChildren()[0].index+" toBeMerged.getParent().getChildren()[1]"+toBeMerged.getParent().getChildren()[1].index);
			if (newPartitions.contains(toBeMerged.getParent().getChildren()[0]) && newPartitions.contains(toBeMerged.getParent().getChildren()[1]))
				minHeap.add(toBeMerged.getParent());

			maxHeap.remove(toBeMerged.getChildren()[0]);
			maxHeap.remove(toBeMerged.getChildren()[1]);				

			newPartitions.remove(toBeMerged.getChildren()[0]);
			newPartitions.remove(toBeMerged.getChildren()[1]);

			double[] coords = new double[2];

			coords[0] = (double)(toBeMerged.getChildren()[0].getLeft() + toBeMerged.getChildren()[0].getRight())/2;
			coords[1] = (double)(toBeMerged.getChildren()[0].getBottom() + toBeMerged.getChildren()[0].getTop())/2;
			newPartitionsRTree.delete(coords, toBeMerged.getChildren()[0]);


			coords[0] = (double)(toBeMerged.getChildren()[1].getLeft() + toBeMerged.getChildren()[1].getRight())/2;
			coords[1] = (double)(toBeMerged.getChildren()[1].getBottom() + toBeMerged.getChildren()[1].getTop())/2;
			newPartitionsRTree.delete(coords, toBeMerged.getChildren()[1]);			

			//chooseBestCostSplit(toBeMerged);
			maxHeap.add(toBeMerged);
			newPartitionsRTree.insert(toBeMerged.getCoords(), toBeMerged.getDimensions(), toBeMerged);



			// Adding
			Cell toBeSplit = maxHeap.remove();	

			Partition[] bestSplits = SpatialHelper.split(toBeSplit, stat[toBeSplit.index][2], stat[toBeSplit.index][3]==1);

			if (bestSplits != null) {
				toBeSplit.getChildren()[0] = new Cell(bestSplits[0].getBottom(), bestSplits[0].getTop(), bestSplits[0].getLeft(), bestSplits[0].getRight());

				toBeSplit.getChildren()[0].setCost(bestSplits[0].getCost()); //not used now
				//toBeSplit.getChildren()[0].setSizeInBytes(bestSplits[0].getSizeInBytes()); //not used now
				//getParent().getChildren()[0].costReductionDueToSplit = bestSplits[0].getSizeInBytes(); //not used now

				toBeSplit.getChildren()[1] = new Cell(bestSplits[1].getBottom(), bestSplits[1].getTop(), bestSplits[1].getLeft(), bestSplits[1].getRight());

				toBeSplit.getChildren()[1].setCost(bestSplits[1].getCost()); //not used now
				//toBeSplit.getChildren()[1].setSizeInBytes(bestSplits[1].getSizeInBytes()); //not used now
				//getParent().getChildren()[1].costReductionDueToSplit = bestSplits[1].getSizeInBytes(); //not used now

				toBeSplit.getChildren()[0].setParent (toBeSplit);
				toBeSplit.getChildren()[1].setParent (toBeSplit);
			}

			System.out.println("toBeSplit: L:"+toBeSplit.getLeft()+" R:"+toBeSplit.getRight()+" T:"+toBeSplit.getTop()+" B:"+toBeSplit.getBottom()+"//  B_x = "+toBeSplit.getCoords()[0]+" B_y = "+toBeSplit.getCoords()[1]+" B_x_d = "+toBeSplit.getDimensions()[0]+" B_y_d = "+toBeSplit.getDimensions()[1]);
			System.out.println("toBeSplit.getChildren()[0]: L:"+toBeSplit.getChildren()[0].getLeft()+" R:"+toBeSplit.getChildren()[0].getRight()+" T:"+toBeSplit.getChildren()[0].getTop()+" B:"+toBeSplit.getChildren()[0].getBottom()+"//  B_x = "+toBeSplit.getChildren()[0].getCoords()[0]+" B_y = "+toBeSplit.getChildren()[0].getCoords()[1]+" B_x_d = "+toBeSplit.getChildren()[0].getDimensions()[0]+" B_y_d = "+toBeSplit.getChildren()[0].getDimensions()[1]);
			System.out.println("toBeSplit.getChildren()[1]: L:"+toBeSplit.getChildren()[1].getLeft()+" R:"+toBeSplit.getChildren()[1].getRight()+" T:"+toBeSplit.getChildren()[1].getTop()+" B:"+toBeSplit.getChildren()[1].getBottom()+"//  B_x = "+toBeSplit.getChildren()[1].getCoords()[0]+" B_y = "+toBeSplit.getChildren()[1].getCoords()[1]+" B_x_d = "+toBeSplit.getChildren()[1].getDimensions()[0]+" B_y_d = "+toBeSplit.getChildren()[1].getDimensions()[1]);


			toBeSplit.getChildren()[0].index = toBeSplit.index; 
			toBeSplit.getChildren()[1].index = emptyIndex; 


			splitMergeInfo.splitParent = toBeSplit;
			splitMergeInfo.splitChild0 = toBeSplit.getChildren()[0];
			splitMergeInfo.splitChild1 = toBeSplit.getChildren()[1];

			minHeap.remove(toBeSplit.getParent());
			coords[0] = (double)(toBeSplit.getLeft() + toBeSplit.getRight())/2;
			coords[1] = (double)(toBeSplit.getBottom() + toBeSplit.getTop())/2;
			//List<Cell> pppp = newPartitionsRTree.search(coords, new double[] {0.0,0.0});//test
			//System.err.println("pppppppppppppppppp = "+pppp);//test
			newPartitionsRTree.delete(coords, toBeSplit);
			newPartitions.remove(toBeSplit);

			//chooseBestCostSplit(toBeSplit.getChildren()[0]);
			//chooseBestCostSplit(toBeSplit.getChildren()[1]);
			maxHeap.add(toBeSplit.getChildren()[0]);
			maxHeap.add(toBeSplit.getChildren()[1]);
			newPartitions.add(toBeSplit.getChildren()[0]);
			newPartitions.add(toBeSplit.getChildren()[1]);

			newPartitionsRTree.insert(toBeSplit.getChildren()[0].getCoords(), toBeSplit.getChildren()[0].getDimensions(), toBeSplit.getChildren()[0]);
			newPartitionsRTree.insert(toBeSplit.getChildren()[1].getCoords(), toBeSplit.getChildren()[1].getDimensions(), toBeSplit.getChildren()[1]);			

			//setCostIncreaseDueToMerge(toBeSplit);
			//toBeSplit.costIncreaseDueToMerge = 2 * costEstimator.getSize(toBeSplit) + costEstimator.getCost(toBeSplit) - (costEstimator.getCost(toBeSplit.getChildren()[0]) + costEstimator.getCost(toBeSplit.getChildren()[1]));

			minHeap.add(toBeSplit);
		}
		else{
			maxHeap.addAll(addToMaxHeapBeforeReturn);
			return null;
		}
		printHeap("#4");
		printAllminHeap("After finding new plan");

		maxHeap.addAll(addToMaxHeapBeforeReturn);
		return splitMergeInfo;
	}
	public void printAllminHeap(String addedString){
		List<Cell> toAdd = new ArrayList<Cell>();
		int i=0;
		while (!minHeap.isEmpty()) {
			Cell c = minHeap.remove();
			System.out.print(addedString+": "+i+"->("+c.getChildren()[0].index+", "+c.getChildren()[1].index+"), ");
			toAdd.add(c);
			i++;
		}
		System.out.println();
		minHeap.addAll(toAdd);
	}

	public void switchToNewPartitions() {
		try {
			partitionsRTree.clear();
			currentPartitions.clear();
			partitionsRTree = (RTree<Cell>)deepCopy(newPartitionsRTree);
			currentPartitions = (ArrayList<Cell>)deepCopy(newPartitions);
		} catch (Exception e) {
			System.out.println("Could not deepCopy in LoadBalancing " +  e);
		}
	}

	public void printHeap(String s){
		System.out.print("################################################ "+s+"maxHeap head = "+maxHeap.peek().index);
		System.out.println(" minHeap head child1 = "+minHeap.peek().getChildren()[0].index+" child2 = "+minHeap.peek().getChildren()[1].index);
	}

	private void halfSplit(Cell parent, boolean isHorizontal) {
		int halfPosition;

		if(isHorizontal){
			halfPosition = (parent.getTop()+parent.getBottom())/2;
		}
		else{
			halfPosition = (parent.getLeft()+parent.getRight())/2;
		}
		Partition[] splits = SpatialHelper.split(parent, halfPosition, isHorizontal); 

		if (splits != null) {
			parent.getChildren()[0] = new Cell(splits[0].getBottom(), splits[0].getTop(), splits[0].getLeft(), splits[0].getRight());

			parent.getChildren()[0].setCost(splits[0].getCost()); //not used now
			//parent.getChildren()[0].costReductionDueToSplit = bestSplits[0].getSizeInBytes(); //not used now

			parent.getChildren()[1] = new Cell(splits[1].getBottom(), splits[1].getTop(), splits[1].getLeft(), splits[1].getRight());
			parent.getChildren()[1].setCost(splits[1].getCost()); //not used now
			
			//parent.getChildren()[1].costReductionDueToSplit = bestSplits[1].getSizeInBytes(); //not used now

			parent.getChildren()[0].setParent (parent);
			parent.getChildren()[1].setParent (parent);
		}
	}

	public ArrayList<Cell> initialPartitions(int numRows, int numColumns) { //Note: changed from partition to Cell
		Queue<Cell> queue = new LinkedList<Cell>();
		int numberCuts = 1;
		int remainingCuts = 1;
		boolean isHorizontal = false;

		// Start with the whole space as one cell (root)
		Cell wholeSpace = new Cell(0, numRows, 0, numColumns);
		wholeSpace.setCost(0); //not used now
		halfSplit(wholeSpace,isHorizontal);
		queue.add(wholeSpace);
		remainingCuts--;

		int remainingPartitions = numberParallelismExecutors - 1; // because we start with one cell, so it's a partition by itself
		ArrayList<Cell> partitions = new ArrayList<Cell>(); //Note: changed from partition to Cell 

		// Continue splitting until:
		while (remainingPartitions > 0) {
			if(remainingCuts == 0){
				numberCuts = numberCuts * 2;
				remainingCuts = numberCuts;
				if(isHorizontal)
					isHorizontal = false;
				else
					isHorizontal = true;
			}
			Cell nextInHeap = queue.remove();

			if (nextInHeap.getChildren()[0] == null) { 
				//pQueue.add(nextInHeap);
				//System.out.println("Warning k could not be reached; only " + (k-remainingPartitions) + "done");
				//break;
				partitions.add(nextInHeap);
				//remainingPartitions--;
				continue;
			}

			// Compute the best position to split each child, without actually adding the corresponding partitions now to the output
			halfSplit(nextInHeap.getChildren()[0],isHorizontal);
			halfSplit(nextInHeap.getChildren()[1],isHorizontal);

			// Insert the splits into the heap
			queue.add(nextInHeap.getChildren()[0]);
			queue.add(nextInHeap.getChildren()[1]);
			remainingCuts = remainingCuts - 2;
			remainingPartitions--;
		}

		// The queue will contain the remaining partitions		
		while (!queue.isEmpty())			
			partitions.add(queue.remove());

		for(int i=0; i<partitions.size(); i++)
			partitions.get(i).index = i;

		initStructures(partitions);

		return partitions;
	}
	private void initStructures(ArrayList<Cell> partitions) {//Note: changed from partition to Cell
		Comparator<Cell> maxComparator = new MaxHeapComparator();
		Comparator<Cell> minComparator = new MinHeapComparator();
		minHeap = new PriorityQueue<Cell>(numberParallelismExecutors, minComparator);
		maxHeap = new PriorityQueue<Cell>(numberParallelismExecutors, maxComparator);

		newPartitionsRTree = new RTree<Cell>(10, 5, 2);
		for (Cell p : partitions) {//Note: changed from partition to Cell
			//((Cell)p).costReductionDueToSplit = -2 * costEstimator.getSize(p); // 0 if we ignore the cost of repartitioning
			(p).setScore(0)  ;
			maxHeap.add(p);

			if (!minHeap.contains((p).getParent())) {
				if (partitions.contains((p).getParent().getChildren()[0]) &&
						partitions.contains((p).getParent().getChildren()[1])) {						
					//((Cell)p).parent.costIncreaseDueToMerge = 2 * costEstimator.getSize(p); // 0 if we ignore the cost of repartitioning
					(p).getParent().setScore ( 0);
					minHeap.add((p).getParent());	
				}
			}
			newPartitionsRTree.insert(p.getCoords(), p.getDimensions(), p);
		}
		this.newPartitions = partitions; 

		try {
			partitionsRTree = (RTree<Cell>)deepCopy(newPartitionsRTree);
			currentPartitions = (ArrayList<Cell>)deepCopy(newPartitions);
		} catch (Exception e) {
			System.out.println("Could not deepCopy in LoadBalancing " +  e);
		}
	}

	static public Object deepCopy(Object oldObj) throws Exception
	{
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try
		{
			ByteArrayOutputStream bos = 
					new ByteArrayOutputStream(); 
			oos = new ObjectOutputStream(bos); 
			// serialize and pass the object
			oos.writeObject(oldObj);   
			oos.flush();               
			ByteArrayInputStream bin = 
					new ByteArrayInputStream(bos.toByteArray());
			ois = new ObjectInputStream(bin);
			// return the new object
			return ois.readObject();
		}
		catch(Exception e)
		{
			System.out.println("Exception in ObjectCloner = " + e);
			throw(e);
		}
		finally
		{
			oos.close();
			ois.close();
		}
	}
	//*******************************************************************
	public ArrayList<Integer> getTaskIDsOverlappingRecangle(Rectangle rectangle){
		ArrayList<Integer> taskIndex = new ArrayList<Integer>();
		double [] coords = new double[2];
		double [] dimensions = new  double[2];
		coords[0]=rectangle.getMin().getX();
		coords[1]=rectangle.getMin().getY();
		dimensions[0]=rectangle.getMax().getX()-coords[0];
		dimensions[1]=rectangle.getMax().getY()-coords[1];
		List<Cell> searchResult = partitionsRTree.search(coords, dimensions);
		for(Cell cell:searchResult)
			taskIndex.add(cell.index);
		return taskIndex;
	
	}
	/**
	 * A point will only be send to a single bolt , no replication 
	 */
	public Integer getTaskIDsContainingPoint(Point point){
		return getTaskIDsOverlappingRecangle(new Rectangle(point, point)).get(0);
	}
	//*******************************************************************
	
	private class MaxHeapComparator implements Comparator<Cell>, Serializable{

		private static final long serialVersionUID = 1L;

		@Override
		public int compare(Cell x, Cell y) {
			if (x.getScore()  < y.getScore() )
				return 1;

			if (x.getScore()  > y.getScore() )			
				return -1;

			return 0;
		}
	}

	private class MinHeapComparator implements Comparator<Cell>, Serializable{

		private static final long serialVersionUID = 1L;

		@Override
		public int compare(Cell x, Cell y) {
			if (x.getScore() > y.getScore() )
				return 1;

			if (x.getScore() < y.getScore() )			
				return -1;

			return 0;
		}
	}

	@Override
	public Rectangle getBoundsForTaskIndex(Integer taskIndex) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Rectangle getBoundsForTaskId(Integer taskId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public GlobalIndexIterator globalKNNIterator(Point p) {
		// TODO Auto-generated method stub
		return null;
	}

}
