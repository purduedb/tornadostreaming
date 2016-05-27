package edu.purdue.cs.tornado.atlas.SGB.mapred;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;

public class SGBLocalPartitioner implements Partitioner<IntWritable, Text>
{
	// Reducer Numbers
	private final static IntWritable R1 = new IntWritable(1);
	private final static IntWritable R2 = new IntWritable(2);
	private final static IntWritable R3 = new IntWritable(3);
	private final static IntWritable R4 = new IntWritable(4);


	@Override
	public int getPartition(IntWritable key, Text value, int numReduce) 
	{
	
        //this is done to avoid performing mod with 0
        if(numReduce == 0)
            return 0;

        // tuple goes to matched reducer
        if(key == R1)
	        return 0;
        else if(key == R2)
	        return 1 % numReduce;
        else if(key == R3)
	        return 2 % numReduce;
        else if(key == R4)
	        return 3 % numReduce;
        else
        	return 0;
        
	}


	@Override
	public void configure(JobConf arg0) {
		// TODO Auto-generated method stub
		
	}
	

}
