package edu.purdue.cs.tornado.helper;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.Partition;

public class PartitionsHelper {
	public static ArrayList<Partition> getGridBasedParitions(Integer numberOfEvaluatorTasks) {
		ArrayList<Partition> partitions = new ArrayList<>();

		Double d = new Double(Math.sqrt(numberOfEvaluatorTasks));
		Integer xCellCount = d.intValue(), yCellCount = d.intValue();
		for (int i = 0; i < xCellCount; i++) {
			for (int j = 0; j < yCellCount; j++) {
				//Rectangle bounds = new Rectangle(new Point(), new Point((i + 1) * xStep, (j + 1) * yStep));
				Partition p = new Partition(j, (j + 1), i, (i + 1)); //new Partition(i * xStep, , (i + 1) * xStep, );
				partitions.add(p);
			}
		}

		return partitions;
	}

	public static ArrayList<Partition> getKDBasedParitionsFromPoints(Integer numberOfEvaluatorTasks, ArrayList<Point> points) {

		Queue<Partition> queue = new LinkedList<Partition>();
		CostEstimator costEstimator = new CostEstimator(points, SpatioTextualConstants.fineGridGranularityX, SpatioTextualConstants.fineGridGranularityX);

		// Start with the whole space as one cell (root)
		Cell wholeSpace = new Cell(0, SpatioTextualConstants.fineGridGranularityY, 0, SpatioTextualConstants.fineGridGranularityX);
		double totalCost = costEstimator.getCost(wholeSpace);
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
			} else{
				queue.add(currentParition);
				continue;
			}
			remainingPartitions--;
		}

		// The priority queue will contain the remaining partitions	
		int i=0;
		double totalCost2=0;
		while (!queue.isEmpty()){
			Partition p=queue.remove();
			p.index=i++;
			totalCost2+=p.getCost();
			partitions.add(p);
			double pcost=0;
			for(int k=p.getLeft();k<p.getRight();k++){
				for(int j=p.getBottom();j<p.getTop();j++)
					pcost+=costEstimator.grid2.pointData[k][j];
			}
			LatLong min = SpatialHelper.convertFromXYToLatLonTo(new Point (p.getCoords()[0]*SpatioTextualConstants.fineGridGranularityXstep,p.getCoords()[1]*SpatioTextualConstants.fineGridGranularityYstep));
			LatLong max = SpatialHelper.convertFromXYToLatLonTo(new Point ((p.getCoords()[0]+p.getDimensions()[0])*SpatioTextualConstants.fineGridGranularityXstep,(p.getCoords()[1]+p.getDimensions()[1])*SpatioTextualConstants.fineGridGranularityYstep));
			System.out.println("Partition "+p.index+",cost="+(int)p.getCost()+"," +pcost+",xmin="+p.getCoords()[0]+",ymin="+p.getCoords()[1] +",xmax="+(p.getDimensions()[0]+p.getCoords()[0])+",ymax="+(p.getCoords()[0]+p.getDimensions()[1])+","+min.toString()+","+max.toString());
		   
		}

		return partitions;

	}

	private static Partition[] chooseBestBalancedSplit(Partition parent, CostEstimator costEstimator) {

		Partition[] bestSplits = null;

		double currentCost = costEstimator.getCost(parent);

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

	public static ArrayList<Partition> readSerializedPartitions(String filePath) {
		ArrayList<Partition> partitions =null;
		try {
			// read object from file
			FileInputStream fis = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			partitions = (ArrayList<Partition>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return partitions;
	}
}
