package edu.purdue.cs.tornado.index.global;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class GlobalPartitionedGridBasedIndex extends GlobalIndex {
	public Double xStep;
	public Double yStep;
	public Integer xCellsNum;
	public Integer yCellsNum;
	public int[][] routingIndex;
	public ArrayList<Cell> partitions;
	public HashMap<Integer, Cell> taskIndexToPartition;
	public Integer fineGridGran;

	public void printRoutingIndex(RoutingGridCell[][] routingGridCells) {
		for (int i = 0; i < fineGridGran; i++) {
			for (int j = 0; j < fineGridGran; j++) {
				System.out.print(padRight(("" + routingGridCells[i][j].taskIdIndex), 2));
			}
			System.out.println();
		}
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public void copyTextSummery(Integer taskIndexFrom, Integer taskIndexTo) {
		//DO NOTHING
	}

	public GlobalPartitionedGridBasedIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks);
		this.fineGridGran = fineGridGran;
		xCellsNum = fineGridGran;
		yCellsNum = fineGridGran;
		yStep = xStep = SpatioTextualConstants.xMaxRange / xCellsNum;
		routingIndex = new int[xCellsNum][yCellsNum];
		partitions = getInitialPartitions();
		taskIndexToPartition = new HashMap<Integer, Cell>();
		initStructures(partitions);

	}

	public GlobalPartitionedGridBasedIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, ArrayList<Cell> partitions, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks);
		this.fineGridGran = fineGridGran;
		xCellsNum = fineGridGran;
		yCellsNum = fineGridGran;
		yStep = xStep = SpatioTextualConstants.xMaxRange / fineGridGran;
		routingIndex = new int[xCellsNum][yCellsNum];
		taskIndexToPartition = new HashMap<Integer, Cell>();
		if (partitions == null)
			partitions = getInitialPartitions();
		this.partitions = partitions;
		initStructures(this.partitions);

	}

	public ArrayList<Cell> getInitialPartitions() {
		return PartitionsHelper.getGridBasedParitions(numberOfEvaluatorTasks, fineGridGran, fineGridGran);
	}

	public void initStructures(ArrayList<Cell> partitions) {
		for (int i = 0; i < xCellsNum; i++)
			for (int j = 0; j < yCellsNum; j++)
				routingIndex[i][j] = -1;
		addPartitionsToGrid(routingIndex, partitions);
	}

	public void addPartitionsToGrid(int[][] routingIndex, ArrayList<Cell> partitions) {

		for (int i = 0; i < xCellsNum; i++) {
			//ArrayList<RoutingGridCell> ycellList = new ArrayList<RoutingGridCell>();
			for (int j = 0; j < yCellsNum; j++) {
				//	ycellList.add(new RoutingGridCell(i, j));

				routingIndex[i][j] = -1;

			}
			//	routingIndex.add(ycellList);
		}
		//NOT necessary in our case this time 
		for (Cell p : partitions) {
			taskIndexToPartition.put(p.index, p);
			for (int i = p.getLeft(); i < p.getRight(); i++) {
				for (int j = p.getBottom(); j < p.getTop(); j++) {
					if (routingIndex[i][j] == -1) {//not assigned
						routingIndex[i][j] = p.index;
					} else {
						System.err.println("Error assigning a partition to a preassigned partition");
					}
				}
			}
		}
		
	}

	@Override
	public HashSet<String> addTextToTaskID(ArrayList<Integer> tasks, ArrayList<String> text, boolean all, boolean forward) {
		return null;
	}

	@Override
	public void dropTextFromTaskID(ArrayList<Integer> tasks, ArrayList<String> text) {

	}

	@Override
	public Rectangle getBoundsForTaskId(Integer taskId) {
		Integer taskIndex = taskIdToIndex.get(taskId);
		return getBoundsForTaskIndex(taskIndex);
	}

	@Override
	public Rectangle getBoundsForTaskIndex(Integer taskIndex) {

		Partition p = taskIndexToPartition.get(taskIndex);

		Rectangle rect;
		if (p != null) {
			rect = new Rectangle(new Point(p.getLeft() * xStep, p.getBottom() * yStep), new Point(p.getRight() * xStep, p.getTop() * yStep));
		} else {
			rect = new Rectangle(new Point(0.0, 0.0), new Point(0.0, 0.0));
		}
		return rect;
	}

	@Override
	public Integer getTaskIDsContainingPoint(Point point) throws Exception {
		return mapDataPointToEvaluatorTask(point.getX(), point.getY());
	}

	//plain get taksIdOverlapping a rectrangle from grid cells simple
	@Override
	public ArrayList<Integer> getTaskIDsOverlappingRecangle(Rectangle rectangle) {
		HashSet<Integer> uniqueParitions = new HashSet<Integer>();
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

		for (int i = xMinCell; i <= xMaxCell; i++) {
			for (int j = yMinCell; j <= yMaxCell; j++) {
				Integer cell = routingIndex[i][j];
				if (!uniqueParitions.contains(cell)) {
					uniqueParitions.add(cell);
					partitions.add(evaluatorBoltTasks.get(cell));
				}
			}
		}

		return partitions;
	}

	@Override
	public GlobalIndexIterator globalKNNIterator(Point p) {
		// TODO Auto-generated method stub
		return null;
	}

	protected Integer mapDataPointToEvaluatorTask(Double x, Double y) {

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

		Integer partitionNum = routingIndex[xCell][yCell];
		if (partitionNum == -1) {
			System.err.println("error in data " + x + " , " + y + "  index is not set");
			return null;

		} else {
			// System.out.println("Point "+x+" , "+y+" is mapped to xcell:"+xCell+"ycell:"+yCell+" index is "+			 partitionNum+" to partitions "+evaluatorBoltTasks.get(partitionNum));
			return evaluatorBoltTasks.get(partitionNum);
		}

	}

	@Override
	public Boolean verifyTextOverlap(Integer task, ArrayList<String> text) {
		return true;
	}

	@Override
	public Boolean isTextAware() {
		return false;
	}

	@Override
	public Boolean isTextOnlyIndex() {
		return false;
	}

	@Override
	public ArrayList<Integer> getTaskIDsContainingKeywordData(DataObject object) {
		return null;
	}

	@Override
	public ArrayList<Integer> getTaskIDsContainingKeywordQuery(Query query) {
		return null;
	}

}
