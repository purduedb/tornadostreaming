package edu.purdue.cs.tornado.index.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.Partition;

public class GlobalOptimizedPartitionedIndexLowerSpace extends GlobalIndex {
	public Double xStep;
	public Double yStep;
	public Integer xCellsNum;
	public Integer yCellsNum;
	public int[][] routingIndex;
	public HashMap<Integer,IndexCellCoordinates> rightRoutingSummary;
	public HashMap<Integer,  IndexCellCoordinates> upperRoutingSummary;

	public ArrayList<Cell> partitions;
	public ArrayList<Cell> taskIndexToPartition;
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
		//DO NOTHING this is spatial only for now 
	}

	public GlobalOptimizedPartitionedIndexLowerSpace(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks);
		this.fineGridGran = fineGridGran;
		xCellsNum = fineGridGran;
		yCellsNum = fineGridGran;
		yStep = xStep = SpatioTextualConstants.xMaxRange / xCellsNum;
		routingIndex = new int[xCellsNum][yCellsNum];
		partitions = getInitialPartitions();
		taskIndexToPartition = new ArrayList<Cell>();
		rightRoutingSummary = new HashMap<Integer ,IndexCellCoordinates>();
		upperRoutingSummary = new HashMap<Integer,IndexCellCoordinates>();
		initStructures(partitions);

	}

	public GlobalOptimizedPartitionedIndexLowerSpace(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, ArrayList<Cell> partitions, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks);
		this.fineGridGran = fineGridGran;
		xCellsNum = fineGridGran;
		yCellsNum = fineGridGran;
		yStep = xStep = SpatioTextualConstants.xMaxRange / fineGridGran;
		routingIndex = new int[xCellsNum][yCellsNum];
		taskIndexToPartition = new ArrayList<Cell>();
		rightRoutingSummary = new HashMap<Integer, IndexCellCoordinates>();
		upperRoutingSummary = new HashMap<Integer,  IndexCellCoordinates>();
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
		taskIdToIndex.clear();
		for (int i = 0; i < partitions.size(); i++)
			taskIndexToPartition.add(null);
		for (Cell p : partitions) {
			taskIndexToPartition.set(p.index, p);
			for (int i = p.getLeft(); i < p.getRight(); i++) {
				for (int j = p.getBottom(); j < p.getTop(); j++) {
					if ((routingIndex[i][j]) == -1) {//not assigned
						(routingIndex[i][j]) = p.index;
						if (i == p.getLeft()) {
//							if (!rightRoutingSummary.containsKey(i)) {
//								rightRoutingSummary.put(i, new HashMap<Integer, IndexCellCoordinates>());
//							}
							rightRoutingSummary.put(getRowMajorValue(i, j), new IndexCellCoordinates(-1, -1));
						}
						if (j == p.getBottom()) {
//							if (!upperRoutingSummary.containsKey(i)) {
//								upperRoutingSummary.put(i, new HashMap<Integer, IndexCellCoordinates>());
//							}
							upperRoutingSummary.put(getRowMajorValue(i, j),new IndexCellCoordinates(-1, -1));
						}
					} else {
						System.err.println("Error assigning a partition to a preassigned partition");
					}
				}
			}
		}
		//finding the rightneighours
		for (int j = 0; j < yCellsNum; j++) {
			int currentIndex = routingIndex[xCellsNum - 1][j];
			IndexCellCoordinates rightCell = null;
			if (rightRoutingSummary.containsKey(getRowMajorValue(xCellsNum - 1,j))) {
				rightRoutingSummary.put(getRowMajorValue(xCellsNum - 1,j), null);
			}
			
			for (int i = xCellsNum - 2; i >= 0; i--) {
				if (routingIndex[i][j] != currentIndex) {
					currentIndex = routingIndex[i][j];
					rightCell = new IndexCellCoordinates(i + 1, j);

				}
				if (rightRoutingSummary.containsKey(getRowMajorValue(i,j))) {
					rightRoutingSummary.put(getRowMajorValue(i,j), rightCell);
				}

			}
		}
		//finding the top neighours
		for (int i = 0; i < xCellsNum; i++) {
			int currentIndex = routingIndex[i][yCellsNum - 1];
			IndexCellCoordinates upperCell = null;
			if (upperRoutingSummary.containsKey(getRowMajorValue(i,yCellsNum - 1)) ){
				upperRoutingSummary.put(getRowMajorValue(i,yCellsNum - 1), null);
			}
			upperCell = null;
			for (int j = yCellsNum - 2; j >= 0; j--) {
				if (routingIndex[i][j] != currentIndex) {
					currentIndex = routingIndex[i][j];
					upperCell = new IndexCellCoordinates(i, j + 1);
				}
				if (upperRoutingSummary.containsKey(getRowMajorValue(i,j)) ){
					upperRoutingSummary.put(getRowMajorValue(i,j), upperCell);
				}

			}
		}
	}
	public Integer getRowMajorValue(int x,int y){
		return x+fineGridGran*y;
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

	@Override
	public ArrayList<Integer> getTaskIDsOverlappingRecangle(Rectangle rectangle) {
		HashSet<Integer> uniqueParitions = new HashSet<Integer>();
		Queue<IndexCellCoordinates> queue = new LinkedList<IndexCellCoordinates>();
		//Stack<IndexCellCoordinates> stack = new Stack<IndexCellCoordinates>();
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
		if ((xMaxCell - xMinCell) > 3 && (yMaxCell - yMinCell) > 3) {
			IndexCellCoordinates cell = new IndexCellCoordinates(xMinCell, yMinCell); //routingIndex[xMinCell][yMinCell];
			partitions.add(evaluatorBoltTasks.get(routingIndex[cell.x][cell.y]));
			uniqueParitions.add(routingIndex[cell.x][cell.y]);
			queue.add(cell);
			while (!queue.isEmpty()) {
				cell = queue.remove();
				IndexCellCoordinates rightCell = getRightCell(cell);
				if (rightCell != null && rightCell.x <= xMaxCell && !(uniqueParitions.contains(routingIndex[rightCell.x][rightCell.y]))) {
					int partitionIndex= routingIndex[rightCell.x][rightCell.y];
					uniqueParitions.add(partitionIndex);
					partitions.add(evaluatorBoltTasks.get(partitionIndex));
					rightCell = getDomininateCellRight(xMinCell, yMinCell, xMaxCell, yMaxCell, rightCell);
					queue.add(rightCell);
				}
				IndexCellCoordinates upperCell = getUpperCell(cell);
				if (upperCell != null && upperCell.y <= yMaxCell && !(uniqueParitions.contains(routingIndex[upperCell.x][upperCell.y]))) {
					int partitionIndex= routingIndex[upperCell.x][upperCell.y];
					uniqueParitions.add(partitionIndex);
					partitions.add(evaluatorBoltTasks.get(partitionIndex));
					upperCell = getDomininateCellUpper(xMinCell, yMinCell, xMaxCell, yMaxCell, upperCell);
					queue.add(upperCell);
				}
			}
		} else {
			for (int i = xMinCell; i <= xMaxCell; i++) {
				for (int j = yMinCell; j <= yMaxCell; j++) {
					Integer cell = routingIndex[i][j];
					if (!uniqueParitions.contains(cell)) {
						uniqueParitions.add(cell);
						partitions.add(evaluatorBoltTasks.get(cell));
					}
				}
			}
		}
		return partitions;

	}

	IndexCellCoordinates getRightCell(IndexCellCoordinates cell) {
		int x = cell.x;
		int y = cell.y;
		if (rightRoutingSummary.containsKey(getRowMajorValue(x, y))) {
			return rightRoutingSummary.get(getRowMajorValue(x, y));
		} else {
			Partition p = taskIndexToPartition.get(routingIndex[cell.x][cell.y]);
			x = p.getLeft();
			y = cell.getY();
			return rightRoutingSummary.get(getRowMajorValue(x, y));
		}
		
	}

	IndexCellCoordinates getUpperCell(IndexCellCoordinates cell) {
		int x = cell.x;
		int y = cell.y;
		if (upperRoutingSummary.containsKey(getRowMajorValue(x, y))) {
			return upperRoutingSummary.get(getRowMajorValue(x, y));
		} else {
			Partition p = taskIndexToPartition.get(routingIndex[cell.x][cell.y]);
			x = cell.getX();
			y = p.getBottom();
			return upperRoutingSummary.get(getRowMajorValue(x, y));
		}
	}

	IndexCellCoordinates getDomininateCellRight(Integer xMin, Integer yMin, Integer xMax, Integer yMax, IndexCellCoordinates cell) {
		Cell partition = taskIndexToPartition.get(routingIndex[cell.x][cell.y]);
		int y = partition.getBottom();
		if (y < yMin)
			cell.y = yMin;

		return cell;
	}

	IndexCellCoordinates getDomininateCellUpper(Integer xMin, Integer yMin, Integer xMax, Integer yMax, IndexCellCoordinates cell) {
		Cell partition = taskIndexToPartition.get(routingIndex[cell.x][cell.y]);
		int x = partition.getLeft();
		if (x < xMin)
			cell.x = xMin;
		return cell;
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

}
