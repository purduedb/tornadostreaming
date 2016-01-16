package edu.purdue.cs.tornado.test;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import edu.purdue.cs.tornado.helper.PartitionsHelper;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.index.global.GlobalOptimizedPartitionedIndex;
import edu.purdue.cs.tornado.index.global.GlobalStaticPartitionedIndex;
import edu.purdue.cs.tornado.loadbalance.Partition;

public class TestOptimizedPartitions {

	@Test
	public void testMatchedPointRoutingBetweenOptimzedAndRTreeBasedRouting() throws Exception {
		ArrayList<Point> pointArray= new ArrayList<Point>();
		pointArray.add(new Point(0.0,0.0));
		pointArray.add(new Point(SpatioTextualConstants.xMaxRange,SpatioTextualConstants.yMaxRange));
		pointArray.add(new Point(1.0,1.0));
		pointArray.add(new Point(10.0,20.0));
		pointArray.add(new Point(100.0,300.0));
		pointArray.add(new Point(250.0,70.0));
		pointArray.add(new Point(750.0,730.0));
		pointArray.add(new Point(750.0,730.0));
		pointArray.add(new Point(750.0,730.0));
		pointArray.add(new Point(750.0,730.0));
		pointArray.add(new Point(3333.0,6333.0));
		pointArray.add(new Point(4333.0,2333.0));
		pointArray.add(new Point(4333.0,4333.0));
		pointArray.add(new Point(2333.0,2333.0));
		
		
		ArrayList<Partition> partitions = PartitionsHelper.readSerializedPartitions("resources/partitions64.ser");
		ArrayList<Integer> evaluators = new ArrayList<Integer>();
		for(int i =0;i<64;i++){
			evaluators.add(i);
		}
		GlobalStaticPartitionedIndex staticPartitionedIndex = new GlobalStaticPartitionedIndex(64, evaluators, partitions);
		
		GlobalOptimizedPartitionedIndex optimizedPartitionedIndex = new GlobalOptimizedPartitionedIndex(64, evaluators, partitions);
		
		for(Point p:pointArray){
			Integer routingList1 =staticPartitionedIndex.getTaskIDsContainingPoint(p);
			Integer routingList2 =optimizedPartitionedIndex.getTaskIDsContainingPoint(p);
			if(!routingList1.equals(routingList2))
				fail("Missmatch between points");
			
		}
	}

	@Test
	public void testMatchedRecatngleRoutingBetweenOptimzedAndRTreeBasedRouting() throws Exception {
		ArrayList<Rectangle> rectArray= new ArrayList<Rectangle>();
		rectArray.add(new Rectangle(new Point(0.0,0.0),new Point(0.0,0.0)));
		rectArray.add(new Rectangle(new Point(SpatioTextualConstants.xMaxRange,SpatioTextualConstants.yMaxRange),new Point(SpatioTextualConstants.xMaxRange,SpatioTextualConstants.yMaxRange)));
		rectArray.add(new Rectangle(new Point(0.0,0.0),new Point(SpatioTextualConstants.xMaxRange,SpatioTextualConstants.yMaxRange)));
		rectArray.add(new Rectangle(new Point(1.0,1.0),new Point(10.0,10.0)));
		rectArray.add(new Rectangle(new Point(10.0,20.0),new Point(20.0,40.0)));
		rectArray.add(new Rectangle(new Point(100.0,300.0),new Point(1050.0,3004.0)));
		rectArray.add(new Rectangle(new Point(250.0,70.0),new Point(2500.0,7080.0)));
		rectArray.add(new Rectangle(new Point(20.0,200.0),new Point(7003.0,2580.0)));;
		rectArray.add(new Rectangle(new Point(750.0,730.0),new Point(950.0,830.0)));
		rectArray.add(new Rectangle(new Point(750.0,730.0),new Point(750.0,730.0)));
		rectArray.add(new Rectangle(new Point(750.0,730.0),new Point(SpatioTextualConstants.xMaxRange,SpatioTextualConstants.yMaxRange)));
		rectArray.add(new Rectangle(new Point(3333.0,6333.0),new Point(4433.0,8823.0)));
		rectArray.add(new Rectangle(new Point(4333.0,2333.0),new Point(6323.0,3255.0)));
		rectArray.add(new Rectangle(new Point(4333.0,4333.0),new Point(7333.0,5333.0)));
		rectArray.add(new Rectangle(new Point(2333.0,2333.0),new Point(7533.0,3333.0)));
		
		
		ArrayList<Partition> partitions = PartitionsHelper.readSerializedPartitions("resources/partitions64.ser");
		ArrayList<Integer> evaluators = new ArrayList<Integer>();
		for(int i =0;i<64;i++){
			evaluators.add(i);
		}
		GlobalStaticPartitionedIndex staticPartitionedIndex = new GlobalStaticPartitionedIndex(64, evaluators, partitions);
		
		GlobalOptimizedPartitionedIndex optimizedPartitionedIndex = new GlobalOptimizedPartitionedIndex(64, evaluators, partitions);
		
		for(Rectangle r:rectArray){
			ArrayList<Integer> routingList1 =staticPartitionedIndex.getTaskIDsOverlappingRecangle(r);
			ArrayList<Integer> routingList2 =optimizedPartitionedIndex.getTaskIDsOverlappingRecangle(r);
			if(routingList1.size()==routingList2.size()){
				for(Integer i :routingList1){
					if(!routingList2.contains(i))
						fail("Missmatch between results");
				}
					
				
			}else{
				
				fail("Missmatch between results");
			}
			
		}
	}
	
}
