/**
 * Copyright Jul 5, 2015
 * Author : Ahmed Mahmood
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.purdue.cs.tornado.index.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.RTree;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.loadbalance.Partition;

public class GlobalStaticPartitionedIndex extends GlobalIndex {

	RTree<Partition> partitionTree;
	Integer fineGridGran;
	Double step;
	public HashMap<Integer, Cell> taskIndexToPartition;

	public GlobalStaticPartitionedIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, Integer fineGridGran) {

		super(numberOfEvaluatorTasks, evaluatorBoltTasks);
		this.fineGridGran = fineGridGran;
		this.step = SpatioTextualConstants.xMaxRange / fineGridGran;
		taskIndexToPartition = new HashMap<Integer, Cell>();

		partitionTree = new RTree<Partition>(10, 5, 2);
		ArrayList<Cell> partitions = PartitionsHelper.getGridBasedParitions(numberOfEvaluatorTasks, fineGridGran, fineGridGran);
		initStructures(partitions);
		for (Partition p : partitions) {
			double[] expandedCoordnates = p.getCoords().clone();
			double[] expandedDimensions = p.getDimensions().clone();
			expandedCoordnates[0] *= step;
			expandedCoordnates[1] *= step;

			expandedDimensions[0] *= step;
			expandedDimensions[1] *= step;
			partitionTree.insert(expandedCoordnates, expandedDimensions, p);
		}
	}

	public GlobalStaticPartitionedIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, ArrayList<Cell> partitions, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks);
		this.fineGridGran = fineGridGran;
		this.step = SpatioTextualConstants.xMaxRange / fineGridGran;
		taskIndexToPartition = new HashMap<Integer, Cell>();
		partitionTree = new RTree<Partition>(10, 5, 2);
		if (partitions == null)
			partitions = getInitialPartitions();
		initStructures(partitions);
		for (Partition p : partitions) {
			double[] expandedCoordnates = p.getCoords().clone();
			double[] expandedDimensions = p.getDimensions().clone();
			expandedCoordnates[0] *= step;
			expandedCoordnates[1] *= step;

			expandedDimensions[0] *= step;
			expandedDimensions[1] *= step;
			partitionTree.insert(expandedCoordnates, expandedDimensions, p);
		}

	}

	public void initStructures(ArrayList<Cell> partitions) {
		for (Cell p : partitions)
			taskIndexToPartition.put(p.index, p);
	}

	public ArrayList<Cell> getInitialPartitions() {
		return PartitionsHelper.getGridBasedParitions(numberOfEvaluatorTasks, fineGridGran, fineGridGran);
	}

	@Override
	public ArrayList<Integer> getTaskIDsOverlappingRecangle(Rectangle rectangle) {
		double[] coords = new double[2];
		double[] dimensions = new double[2];
		ArrayList<Integer> tasks = new ArrayList<Integer>();
		coords[0] = rectangle.getMin().getX();
		coords[1] = rectangle.getMin().getY();

		dimensions[0] = rectangle.getMax().getX() - rectangle.getMin().getX();
		dimensions[1] = rectangle.getMax().getY() - rectangle.getMin().getY();

		List<Partition> partitions = partitionTree.search(coords, dimensions);
		for (Partition p : partitions)
			tasks.add(evaluatorBoltTasks.get(p.index));
		return tasks;
	}

	@Override
	public Integer getTaskIDsContainingPoint(Point point) throws Exception {
		double[] coords = new double[2];
		double[] dimensions = new double[2];
		ArrayList<Integer> tasks = new ArrayList<Integer>();
		coords[0] = point.getX();
		coords[1] = point.getY();

		dimensions[0] = 0;
		dimensions[1] = 0;

		List<Partition> partitions = partitionTree.search(coords, dimensions);
		if (partitions == null || partitions.size() == 0) {
			System.err.println("Point " + point.toString() + " not foind");
			throw new Exception("Point not found");
		}
		Integer taskIndex = partitions.get(0).index;//this returns the the first task id for the partition
		//in the case of boundary overlap, choose the task id with the least index
		if (partitions.size() > 1)
			for (Partition p : partitions) {
				if (p.index < taskIndex)
					taskIndex = p.index;
			}

		return evaluatorBoltTasks.get(taskIndex);
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
			rect = new Rectangle(new Point(p.getLeft() * step, p.getBottom() * step), new Point(p.getRight() * step, p.getTop() * step));
		} else {
			rect = new Rectangle(new Point(0.0, 0.0), new Point(0.0, 0.0));
		}
		return rect;
	}


	@Override
	public GlobalIndexIterator globalKNNIterator(Point p) {
		return null;
	}

	@Override
	public ArrayList<String>  addTextToTaskID(ArrayList<Integer> tasks, ArrayList<String> text,boolean all,boolean forward) {
		return null;
	}

	@Override
	public void dropTextFromTaskID(ArrayList<Integer> tasks, ArrayList<String> text) {
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
