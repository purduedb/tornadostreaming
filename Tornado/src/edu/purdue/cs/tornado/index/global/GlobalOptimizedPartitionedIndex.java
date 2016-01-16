package edu.purdue.cs.tornado.index.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.loadbalance.Partition;

public class GlobalOptimizedPartitionedIndex extends GlobalIndex {
	public Double xStep;
	public Double yStep;
	public Integer xCellsNum;
	public Integer yCellsNum;
	public ArrayList<ArrayList<RoutingGridCell>> routingIndex;
	ArrayList<Partition> partitions ;
	HashMap<Integer, Partition> taskIndexToPartition;
	public GlobalOptimizedPartitionedIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks);
		partitions = PartitionsHelper.getGridBasedParitions(numberOfEvaluatorTasks);
		taskIndexToPartition = new HashMap<Integer, Partition>();
		addPartitionsToGrid(partitions);

	}

	public GlobalOptimizedPartitionedIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, ArrayList<Partition> partitions) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks);
		taskIndexToPartition = new HashMap<Integer, Partition>();
		addPartitionsToGrid(partitions);

	}

	public void addPartitionsToGrid(ArrayList<Partition> partitions) {
		xCellsNum = SpatioTextualConstants.fineGridGranularityX;
		yCellsNum = SpatioTextualConstants.fineGridGranularityY;
		xStep = SpatioTextualConstants.fineGridGranularityXstep;
		yStep = SpatioTextualConstants.fineGridGranularityXstep;
		routingIndex = new ArrayList<ArrayList<RoutingGridCell>>();
		for (int i = 0; i < xCellsNum; i++) {
			ArrayList<RoutingGridCell> ycellList = new ArrayList<RoutingGridCell>();
			for (int j = 0; j < yCellsNum; j++) {
				ycellList.add(new RoutingGridCell(i, j));
			}
			routingIndex.add(ycellList);
		}
		for (Partition p : partitions) {
			taskIndexToPartition.put(p.index, p);
			for (int i =  p.getLeft(); i <p.getRight(); i++) {
				for (int j = p.getBottom(); j < p.getTop(); j++) {
					if (routingIndex.get(i).get(j).taskIdIndex == -1) {//not assigned
						routingIndex.get(i).get(j).taskIdIndex = p.index;
					} else {
						System.err.println("Error assigning a partition to a preassigned partition");
					}
				}
			}
		}
		//finding the rightneighours
		for (int j = 0; j < yCellsNum; j++) {
			Integer currentIndex = routingIndex.get(xCellsNum - 1).get(j).taskIdIndex;
			RoutingGridCell rightCell = null;
			routingIndex.get(xCellsNum - 1).get(j).rightCell = null;
			rightCell = routingIndex.get(xCellsNum - 1).get(j);
			for (int i = xCellsNum - 2; i >= 0; i--) {
				if (routingIndex.get(i).get(j).taskIdIndex != currentIndex) {
					currentIndex = routingIndex.get(i).get(j).taskIdIndex;
					rightCell = routingIndex.get(i + 1).get(j);
				}
				routingIndex.get(i).get(j).rightCell = rightCell;

			}
		}
		//finding the top neighours
		for (int i = 0; i < xCellsNum; i++) {
			Integer currentIndex = routingIndex.get(i).get(yCellsNum - 1).taskIdIndex;
			RoutingGridCell upperCell = null;
			routingIndex.get(i).get(yCellsNum - 1).upperCell = null;
			upperCell = routingIndex.get(i).get(yCellsNum - 1);
			for (int j = yCellsNum - 2; j >= 0; j--) {
				if (routingIndex.get(i).get(j).taskIdIndex != currentIndex) {
					currentIndex = routingIndex.get(i).get(j).taskIdIndex;
					upperCell = routingIndex.get(i).get(j + 1);
				}
				routingIndex.get(i).get(j).upperCell = upperCell;

			}
		}
	}

	@Override
	public ArrayList<Integer> getTaskIDsOverlappingRecangle(Rectangle rectangle) {
		HashSet<Integer> uniqueParitions = new HashSet<Integer>();
		Queue<RoutingGridCell> queue = new LinkedList<RoutingGridCell>();
		ArrayList<Integer> partitions = new ArrayList<Integer>();
		int xMinCell = (int) (rectangle.getMin().getX() / xStep);
		int yMinCell = (int) (rectangle.getMin().getY() / yStep);
		int xMaxCell = (int) (rectangle.getMax().getX() / xStep);
		int yMaxCell = (int) (rectangle.getMax().getY() / yStep);
		//to handle the case where data is outside the range of the bolts 
		if (xMaxCell >= SpatioTextualConstants.xMaxRange / xStep)
			xMaxCell = (int) ((SpatioTextualConstants.xMaxRange / xStep) - 1);
		if (yMaxCell >= SpatioTextualConstants.yMaxRange / xStep)
			yMaxCell = (int) ((SpatioTextualConstants.yMaxRange / yStep) - 1);
		if (xMinCell >= SpatioTextualConstants.xMaxRange / xStep)
			xMinCell = (int) ((SpatioTextualConstants.xMaxRange / xStep) - 1);
		else if (xMinCell < 0)
				xMinCell = 0;
		if (yMinCell >= SpatioTextualConstants.yMaxRange / xStep)
			yMinCell = (int) ((SpatioTextualConstants.yMaxRange / yStep) - 1);
		else if (yMinCell < 0)
			yMinCell = 0;

		RoutingGridCell cell = routingIndex.get(xMinCell).get(yMinCell);
		queue.add(cell);
		while (!queue.isEmpty()) {
			cell = queue.remove();
			if (!uniqueParitions.contains(cell.taskIdIndex)) {
				uniqueParitions.add(cell.taskIdIndex);
				partitions.add(evaluatorBoltTasks.get(cell.taskIdIndex));
			}
			RoutingGridCell rightCell = cell.rightCell;
			RoutingGridCell upperCell = cell.upperCell;
			if (rightCell != null && rightCell.xCoordinate <= xMaxCell && rightCell.yCoordinate <= yMaxCell)
				queue.add(rightCell);
			if (upperCell != null && upperCell.xCoordinate <= xMaxCell && upperCell.yCoordinate <= yMaxCell)
				queue.add(upperCell);
		}

		return partitions;
	}

	@Override
	public Integer getTaskIDsContainingPoint(Point point) throws Exception {
		return mapDataPointToEvaluatorTask(point.getX(), point.getY());
	}

	private Integer mapDataPointToEvaluatorTask(Double x, Double y) {

		Integer xCell = (int) (x / xStep);
		Integer yCell = (int) (y / yStep);
		if (xCell >= SpatioTextualConstants.xMaxRange / xStep)
			xCell = (int) ((SpatioTextualConstants.xMaxRange / xStep) - 1);
		if (yCell >= SpatioTextualConstants.yMaxRange / xStep)
			yCell = (int) ((SpatioTextualConstants.yMaxRange / yStep) - 1);
		if (xCell < 0)
			xCell = 0;
		if (yCell < 0)
			yCell = 0;

		Integer partitionNum = routingIndex.get(xCell).get(yCell).taskIdIndex;
		if (partitionNum == -1) {
			System.err.println("error in data " + x + " , " + y + "  index is not set");
			return null;

		} else {
			// System.out.println("Point "+x+" , "+y+" is mapped to xcell:"+xCell+"ycell:"+yCell+" index is "+			 partitionNum+" to partitions "+evaluatorBoltTasks.get(partitionNum));
			return evaluatorBoltTasks.get(partitionNum);
		}

	}

	@Override
	public Rectangle getBoundsForTaskIndex(Integer taskIndex) {
		
		Partition p = taskIndexToPartition.get(taskIndex);
		Rectangle rect = new Rectangle(
				new Point(p.getLeft()*xStep,p.getBottom()*yStep), 
				new Point(p.getRight()*xStep,p.getTop()*yStep));
		return rect;
	}

	@Override
	public Rectangle getBoundsForTaskId(Integer taskId) {
		Integer taskIndex = taskIdToIndex.get(taskId);
		return getBoundsForTaskIndex(taskIndex);
	}

	@Override
	public GlobalIndexIterator globalKNNIterator(Point p) {
		// TODO Auto-generated method stub
		return null;
	}

	public class RoutingGridCell {
		public Integer taskIdIndex;
		public Integer xCoordinate;
		public Integer yCoordinate;
		public RoutingGridCell rightCell;
		public RoutingGridCell upperCell;

		public RoutingGridCell() {
			taskIdIndex = -1;
			rightCell = null;
			upperCell = null;
			xCoordinate = null;
			yCoordinate = null;
		}

		public RoutingGridCell(Integer x, Integer y) {
			taskIdIndex = -1;
			rightCell = null;
			upperCell = null;
			xCoordinate = x;
			yCoordinate = y;
		}
	}

}
