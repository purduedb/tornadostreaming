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
import java.util.List;

import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.RTree;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.loadbalance.Partition;

public class GlobalStaticPartitionedIndex extends GlobalIndex {

	RTree<Partition> partitionTree ;



	public GlobalStaticPartitionedIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks) {
		
		super(numberOfEvaluatorTasks, evaluatorBoltTasks);
		partitionTree =new RTree<Partition>(10, 5, 2);
		ArrayList<Partition>partitions = PartitionsHelper.getGridBasedParitions(numberOfEvaluatorTasks);
		for(Partition p : partitions)
			partitionTree.insert(p.getCoords(), p.getDimensions(), p);
			

	}
   public GlobalStaticPartitionedIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks,ArrayList<Partition>partitions) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks);
		
		partitionTree =new RTree<Partition>(10, 5, 2);
		for(Partition p : partitions){
			double[] expandedCoordnates=p.getCoords().clone();
			double[] expandedDimensions=p.getDimensions().clone();
			expandedCoordnates[0]*=SpatioTextualConstants.fineGridGranularityXstep;
			expandedCoordnates[1]*=SpatioTextualConstants.fineGridGranularityYstep;
			
			expandedDimensions[0]*=SpatioTextualConstants.fineGridGranularityXstep;
			expandedDimensions[1]*=SpatioTextualConstants.fineGridGranularityYstep;
			partitionTree.insert(expandedCoordnates, expandedDimensions, p);
		}
			

	}

	@Override
	public ArrayList<Integer> getTaskIDsOverlappingRecangle(Rectangle rectangle) {
		double [] coords= new double[2];
		double [] dimensions= new double[2];
		ArrayList<Integer> tasks= new ArrayList<Integer>();
		coords[0]=rectangle.getMin().getX();
		coords[1]=rectangle.getMin().getY();
		
		dimensions[0]=rectangle.getMax().getX()-rectangle.getMin().getX();
		dimensions[1]=rectangle.getMax().getY()-rectangle.getMin().getY();
		
		List<Partition> partitions =partitionTree.search(coords, dimensions);
		for (Partition p:partitions)
			tasks.add(evaluatorBoltTasks.get(p.index));
		return tasks;
	}

	@Override
	public Integer getTaskIDsContainingPoint(Point point) throws Exception {
		double [] coords= new double[2];
		double [] dimensions= new double[2];
		ArrayList<Integer> tasks= new ArrayList<Integer>();
		coords[0]=point.getX();
		coords[1]=point.getY();
		
		dimensions[0]=0;
		dimensions[1]=0;
		
		List<Partition> partitions =partitionTree.search(coords, dimensions);
		if(partitions==null||partitions.size()==0){
			System.err.println("Point "+point.toString()+" not foind");
			throw new Exception ("Point not found");
		}
		Integer taskIndex=partitions.get(0).index;//this returns the the first task id for the partition
		//in the case of boundary overlap, choose the task id with the least index
		if(partitions.size()>1)
		for (Partition p:partitions){
			if(p.index<taskIndex)
				taskIndex=p.index;
		}
		
		return evaluatorBoltTasks.get(taskIndex);
	}

	@Override
	public Rectangle getBoundsForTaskIndex(Integer taskIndex) {
		return null;
	}

	@Override
	public Rectangle getBoundsForTaskId(Integer taskId) {
		return null;
	}
	@Override
	public GlobalIndexIterator globalKNNIterator(Point p) {
		return null;
	}

}
