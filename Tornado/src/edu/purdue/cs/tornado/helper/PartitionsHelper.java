package edu.purdue.cs.tornado.helper;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.messages.Query;

public class PartitionsHelper {
	private static class MaxParitionComp implements Comparator<Partition>, Serializable {
		private static final long serialVersionUID = 1L;
		@Override
		public int compare(Partition x, Partition y) {
			if (x.getCost() < y.getCost())
				return 1;

			if (x.getCost() > y.getCost())
				return -1;

			return 0;
		}
	}
	public static ArrayList<Cell> getGridBasedParitions(Integer numberOfEvaluatorTasks, Integer numRowsFineGranularity, Integer numColumnsFineGranularity) {
		//		ArrayList<Cell> partitions = new ArrayList<>();
		//
		//		Double d = new Double(Math.sqrt(numberOfEvaluatorTasks));
		//		Integer xCellCount = d.intValue(), yCellCount = d.intValue();
		//		for (int i = 0; i < xCellCount; i++) {
		//			for (int j = 0; j < yCellCount; j++) {
		//				//Rectangle bounds = new Rectangle(new Point(), new Point((i + 1) * xStep, (j + 1) * yStep));
		//				Cell p = new Cell(j, (j + 1), i, (i + 1)); //new Partition(i * xStep, , (i + 1) * xStep, );
		//				partitions.add(p);
		//			}
		//		}
		//
		//		return partitions;
		Queue<Cell> queue = new LinkedList<Cell>();
		int numberCuts = 1;
		int remainingCuts = 1;
		boolean isHorizontal = false;

		// Start with the whole space as one cell (root)
		Cell wholeSpace = new Cell(0, numRowsFineGranularity, 0, numColumnsFineGranularity);
		wholeSpace.setCost(0); //not used now
		halfSplit(wholeSpace, isHorizontal);
		queue.add(wholeSpace);
		remainingCuts--;

		int remainingPartitions = numberOfEvaluatorTasks - 1; // because we start with one cell, so it's a partition by itself
		ArrayList<Cell> partitions = new ArrayList<Cell>(); //Note: changed from partition to Cell 

		// Continue splitting until:
		while (remainingPartitions > 0) {
			if (remainingCuts == 0) {
				numberCuts = numberCuts * 2;
				remainingCuts = numberCuts;
				if (isHorizontal)
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
			halfSplit(nextInHeap.getChildren()[0], isHorizontal);
			halfSplit(nextInHeap.getChildren()[1], isHorizontal);

			// Insert the splits into the heap
			queue.add(nextInHeap.getChildren()[0]);
			queue.add(nextInHeap.getChildren()[1]);
			remainingCuts = remainingCuts - 2;
			remainingPartitions--;
		}

		// The queue will contain the remaining partitions		
		while (!queue.isEmpty())
			partitions.add(queue.remove());

		for (int i = 0; i < partitions.size(); i++)
			partitions.get(i).index = i;
		return partitions;
	}

	private static void halfSplit(Cell parent, boolean isHorizontal) {
		int halfPosition;

		if (isHorizontal) {
			halfPosition = (parent.getTop() + parent.getBottom()) / 2;
		} else {
			halfPosition = (parent.getLeft() + parent.getRight()) / 2;
		}
		Partition[] splits = SpatialHelper.split(parent, halfPosition, isHorizontal);

		if (splits != null) {
			parent.getChildren()[0] = new Cell(splits[0].getBottom(), splits[0].getTop(), splits[0].getLeft(), splits[0].getRight());

			parent.getChildren()[0].setCost(splits[0].getCost()); //not used now
			//parent.getChildren()[0].costReductionDueToSplit = bestSplits[0].getSizeInBytes(); //not used now

			parent.getChildren()[1] = new Cell(splits[1].getBottom(), splits[1].getTop(), splits[1].getLeft(), splits[1].getRight());
			parent.getChildren()[1].setCost(splits[1].getCost()); //not used now

			//parent.getChildren()[1].costReductionDueToSplit = bestSplits[1].getSizeInBytes(); //not used now

			parent.getChildren()[0].setParent(parent);
			parent.getChildren()[1].setParent(parent);
		}
	}

	public static ArrayList<Partition> getKDBasedParitionsFromPoints(Integer numberOfEvaluatorTasks, ArrayList<Point> points, Integer fineGridGran) {

		Queue<Partition> queue = new LinkedList<Partition>();
		CostEstimator costEstimator = new CostEstimator(points, fineGridGran, fineGridGran);
		Double step = SpatioTextualConstants.xMaxRange / fineGridGran;
		// Start with the whole space as one cell (root)
		Cell wholeSpace = new Cell(0, fineGridGran, 0, fineGridGran);
		double totalCost = costEstimator.getCostPointOnly(wholeSpace);
		wholeSpace.setCost(totalCost);

		queue.add(wholeSpace);

		int remainingPartitions = numberOfEvaluatorTasks - 1; // because we start with one cell, so it's a partition by itself
		ArrayList<Partition> partitions = new ArrayList<Partition>();

		// Continue splitting until:
		while (remainingPartitions > 0) {

			//choose the next partition to split
			Partition currentParition = queue.remove();

			// Compute the best position to split each child, without actually adding the corresponding partitions now to the output
			Partition[] splits = chooseBestBalancedSplit(currentParition, costEstimator);
			if (splits != null && splits[0] != null && splits[1] != null) {
				queue.add(splits[0]);
				queue.add(splits[1]);
			} else {
				queue.add(currentParition);
				continue;
			}
			remainingPartitions--;
		}

		// The priority queue will contain the remaining partitions	
		int i = 0;
		double totalCost2 = 0;
		while (!queue.isEmpty()) {
			Partition p = queue.remove();
			p.index = i++;
			totalCost2 += p.getCost();
			partitions.add(p);
			double pcost = 0;
			for (int k = p.getLeft(); k < p.getRight(); k++) {
				for (int j = p.getBottom(); j < p.getTop(); j++)
					pcost += costEstimator.grid2.pointData[k][j];
			}
			LatLong min = SpatialHelper.convertFromXYToLatLonTo(new Point(p.getCoords()[0] * step, p.getCoords()[1] * step));
			LatLong max = SpatialHelper.convertFromXYToLatLonTo(new Point((p.getCoords()[0] + p.getDimensions()[0]) * step, (p.getCoords()[1] + p.getDimensions()[1]) * step));
			System.out.println("Partition " + p.index + ",cost=" + (int) p.getCost() + "," + pcost + ",xmin=" + p.getCoords()[0] + ",ymin=" + p.getCoords()[1] + ",xmax=" + (p.getDimensions()[0] + p.getCoords()[0]) + ",ymax="
					+ (p.getCoords()[0] + p.getDimensions()[1]) + "," + min.toString() + "," + max.toString());

		}

		return partitions;

	}

	public static ArrayList<Partition> getKDBasedParitionsFromPointsAndQueries(Integer numberOfEvaluatorTasks, ArrayList<Point> points, ArrayList<Query> queries, Integer fineGridGran) {

		Queue<Partition> queue = new LinkedList<Partition>();
		CostEstimator costEstimator = new CostEstimator(points, fineGridGran, fineGridGran, queries);
		Double step = SpatioTextualConstants.xMaxRange/fineGridGran;
		// Start with the whole space as one cell (root)
		Cell wholeSpace = new Cell(0, fineGridGran, 0, fineGridGran);
		double totalCost = costEstimator.getCostPointPointsAndQueries(wholeSpace);
		wholeSpace.setCost(totalCost);

		queue.add(wholeSpace);

		int remainingPartitions = numberOfEvaluatorTasks - 1; // because we start with one cell, so it's a partition by itself
		ArrayList<Partition> partitions = new ArrayList<Partition>();

		// Continue splitting until:
		while (remainingPartitions > 0) {

			//choose the next partition to split
			Partition currentParition = queue.remove();

			// Compute the best position to split each child, without actually adding the corresponding partitions now to the output
			Partition[] splits = chooseBestBalancedSplitFromQueriesAndData(currentParition, costEstimator);
			if (splits != null && splits[0] != null && splits[1] != null) {
				queue.add(splits[0]);
				queue.add(splits[1]);
			} else {
				queue.add(currentParition);
				continue;
			}
			remainingPartitions--;
		}

		// The priority queue will contain the remaining partitions	
		int i = 0;
		double totalCost2 = 0;
		while (!queue.isEmpty()) {
			Partition p = queue.remove();
			p.index = i++;
			totalCost2 += p.getCost();
			partitions.add(p);
			double pcost = 0;
			pcost = costEstimator.getCostPointPointsAndQueries(p);
			//			for(int k=p.getLeft();k<p.getRight();k++){
			//				for(int j=p.getBottom();j<p.getTop();j++)
			//					pcost+=costEstimator.grid2.pointData[k][j];
			//			}
			LatLong min = SpatialHelper.convertFromXYToLatLonTo(new Point(p.getCoords()[0] * step, p.getCoords()[1] *step));
			LatLong max = SpatialHelper.convertFromXYToLatLonTo(
					new Point((p.getCoords()[0] + p.getDimensions()[0]) * step, (p.getCoords()[1] + p.getDimensions()[1]) * step));
			System.out.println("Partition " + p.index + ",cost=" + (int) p.getCost() + "," + pcost + ",xmin=" + p.getCoords()[0] + ",ymin=" + p.getCoords()[1] + ",xmax=" + (p.getDimensions()[0] + p.getCoords()[0]) + ",ymax="
					+ (p.getCoords()[0] + p.getDimensions()[1]) + "," + min.toString() + "," + max.toString());

		}

		return partitions;

	}
	
	public static ArrayList<Partition> getKDBasedParitionsFromPointsAndQueriesPriority(Integer numberOfEvaluatorTasks, ArrayList<Point> points, ArrayList<Query> queries, Integer fineGridGran) {

		Comparator<Partition> maxComparator = new MaxParitionComp();
		PriorityQueue<Partition> queue = new PriorityQueue<Partition>( maxComparator);
		CostEstimator costEstimator = new CostEstimator(points, fineGridGran, fineGridGran, queries);
		Double step = SpatioTextualConstants.xMaxRange/fineGridGran;
		// Start with the whole space as one cell (root)
		Cell wholeSpace = new Cell(0, fineGridGran, 0, fineGridGran);
		double totalCost = costEstimator.getCostPointPointsAndQueries(wholeSpace);
		wholeSpace.setCost(totalCost);

		queue.add(wholeSpace);

		int remainingPartitions = numberOfEvaluatorTasks - 1; // because we start with one cell, so it's a partition by itself
		
		ArrayList<Partition> partitions = new ArrayList<Partition>();

		// Continue splitting until:
		while (remainingPartitions > 0) {

			//choose the next partition to split
			Partition currentParition = queue.remove();

			// Compute the best position to split each child, without actually adding the corresponding partitions now to the output
			Partition[] splits = chooseBestBalancedSplitFromQueriesAndData(currentParition, costEstimator);
			if (splits != null && splits[0] != null && splits[1] != null) {
				queue.add(splits[0]);
				queue.add(splits[1]);
			} else {
				partitions.add(currentParition);
				continue;
			}
			remainingPartitions--;
		}

		// The priority queue will contain the remaining partitions	
		int i = 0;
		double totalCost2 = 0;
		for(Partition p: partitions){
			p.index=i++;
			totalCost2 += p.getCost();
			double pcost = 0;
			pcost = costEstimator.getCostPointPointsAndQueries(p);
			//			for(int k=p.getLeft();k<p.getRight();k++){
			//				for(int j=p.getBottom();j<p.getTop();j++)
			//					pcost+=costEstimator.grid2.pointData[k][j];
			//			}
			LatLong min = SpatialHelper.convertFromXYToLatLonTo(new Point(p.getCoords()[0] * step, p.getCoords()[1] *step));
			LatLong max = SpatialHelper.convertFromXYToLatLonTo(
					new Point((p.getCoords()[0] + p.getDimensions()[0]) * step, (p.getCoords()[1] + p.getDimensions()[1]) * step));
			System.out.println("Partition " + p.index + ",cost=" + (int) p.getCost() + "," + pcost + ",xmin=" + p.getCoords()[0] + ",ymin=" + p.getCoords()[1] + ",xmax=" + (p.getDimensions()[0] + p.getCoords()[0]) + ",ymax="
					+ (p.getCoords()[0] + p.getDimensions()[1]) + "," + min.toString() + "," + max.toString());
		}
		while (!queue.isEmpty()) {
			Partition p = queue.remove();
			p.index = i++;
			totalCost2 += p.getCost();
			partitions.add(p);
			double pcost = 0;
			pcost = costEstimator.getCostPointPointsAndQueries(p);
			//			for(int k=p.getLeft();k<p.getRight();k++){
			//				for(int j=p.getBottom();j<p.getTop();j++)
			//					pcost+=costEstimator.grid2.pointData[k][j];
			//			}
			LatLong min = SpatialHelper.convertFromXYToLatLonTo(new Point(p.getCoords()[0] * step, p.getCoords()[1] *step));
			LatLong max = SpatialHelper.convertFromXYToLatLonTo(
					new Point((p.getCoords()[0] + p.getDimensions()[0]) * step, (p.getCoords()[1] + p.getDimensions()[1]) * step));
			System.out.println("Partition " + p.index + ",cost=" + (int) p.getCost() + "," + pcost + ",xmin=" + p.getCoords()[0] + ",ymin=" + p.getCoords()[1] + ",xmax=" + (p.getDimensions()[0] + p.getCoords()[0]) + ",ymax="
					+ (p.getCoords()[0] + p.getDimensions()[1]) + "," + min.toString() + "," + max.toString());

		}

		return partitions;

	}
	private static Partition[] chooseBestBalancedSplit(Partition parent, CostEstimator costEstimator) {

		Partition[] bestSplits = null;

		double currentCost = costEstimator.getCostPointOnly(parent);

		// Try all horizontal splits
		// If the split is based only on the number of points the split should be on partition to make it  
		for (int i = parent.getBottom() + 1; i < parent.getTop(); i++) {
			Partition[] splits = SpatialHelper.split(parent, i, true, costEstimator);

			double costdiff = Math.abs(splits[0].getCost() - splits[1].getCost());

			if (costdiff <= currentCost) {
				currentCost = costdiff;
				bestSplits = splits;
			}

		}

		// Try all vertical splits
		for (int i = parent.getLeft() + 1; i < parent.getRight(); i++) {
			Partition[] splits = SpatialHelper.split(parent, i, false, costEstimator);

			double costdiff = Math.abs(splits[0].getCost() - splits[1].getCost());

			if (costdiff <= currentCost) {
				currentCost = costdiff;
				bestSplits = splits;
			}
		}
		return bestSplits;

	}

	private static Partition[] chooseBestBalancedSplitFromQueriesAndData(Partition parent, CostEstimator costEstimator) {

		Partition[] bestSplits = null;

		double currentCost = costEstimator.getCostPointPointsAndQueries(parent);

		// Try all horizontal splits
		// If the split is based only on the number of points the split should be on partition to make it  
		for (int i = parent.getBottom() + 1; i < parent.getTop(); i++) {
			Partition[] splits = SpatialHelper.splitWithQueriesCost(parent, i, true, costEstimator);

			double getCostAtBounds = costEstimator.getBoundaryCostForHorizontalSplit(parent, i);
			double costdiff = Math.abs(splits[0].getCost() - splits[1].getCost()) + getCostAtBounds;

			if (costdiff <= currentCost) {
				currentCost = costdiff;
				bestSplits = splits;
			}

		}

		// Try all vertical splits
		for (int i = parent.getLeft() + 1; i < parent.getRight(); i++) {
			Partition[] splits = SpatialHelper.splitWithQueriesCost(parent, i, false, costEstimator);
			double getCostAtBounds = costEstimator.getBoundaryCostForVerticalSplit(parent, i);
			double costdiff = Math.abs(splits[0].getCost() - splits[1].getCost()) + getCostAtBounds;

			if (costdiff <= currentCost) {
				currentCost = costdiff;
				bestSplits = splits;
			}
		}
		return bestSplits;

	}

	public static ArrayList<Cell> readSerializedPartitions(String filePath) {
		ArrayList<Cell> partitions = new ArrayList<Cell>();
		try {
			// read object from file
			FileInputStream fis = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			ArrayList<Partition> serializedPartitions = (ArrayList<Partition>) ois.readObject();
			for (Partition p : serializedPartitions) {
				Cell c = new Cell();
				c.coords = p.coords;
				c.dimensions = p.dimensions;
				c.cost = p.cost;
				c.index = p.index;
				partitions.add(c);
			}
			ois.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return partitions;
	}
}
