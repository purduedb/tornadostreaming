package edu.purdue.cs.tornado.atlas.SGB.mapred;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;


public class SGBLocalMapper extends MapReduceBase implements 
			Mapper<LongWritable, Text, IntWritable, Text> 
{
	// Reducer Numbers
	private final static IntWritable R1 = new IntWritable(1);
	private final static IntWritable R2 = new IntWritable(2);
	private final static IntWritable R3 = new IntWritable(3);
	private final static IntWritable R4 = new IntWritable(4);

	//private Path[] localArchives;
    private Path[] localFiles;
    
    public void configure(JobConf job) 
    {
		// Get the cached files
		try 
		{
			//localArchives = DistributedCache.getLocalCacheArchives(job);
			localFiles = DistributedCache.getLocalCacheFiles(job);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}

	
	@Override
	public void map(LongWritable key, Text value, OutputCollector<IntWritable, Text> output, 
					Reporter reporter) throws IOException 
	{
		
		String outValue = "";
		String tempString = value.toString();
		String[] items = tempString.split(" ");
		
		// Get cached partition value
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		FSDataInputStream in = fs.open(this.localFiles[0]);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		float eps = Float.valueOf(br.readLine());
		double partitionX = Double.valueOf(br.readLine());
		double partitionY = Double.valueOf(br.readLine());
		//int partitionX = Integer.valueOf(br.readLine());
		//int partitionY = Integer.valueOf(br.readLine());
		
		br.close();
		in.close();
		
		// make outValue
		for(int i = 0; i < items.length; i++)
		{
			outValue += items[i];
			
			if(i != items.length - 1)
				outValue += Variables.splitter;
		}
		
		System.out.println(value.toString());

		// WITH OUT DUPLICATION
		// Reducer Number decided by tuple's partition
		if(Double.valueOf(items[Variables.positionX]) < partitionX)
		{
			if(Double.valueOf(items[Variables.positionY]) < partitionY)
			{
				output.collect(R1, new Text(outValue));
			}
			else
			{
				output.collect(R3, new Text(outValue));
			}
		}
		else 
		{
			if(Double.valueOf(items[Variables.positionY]) < partitionY)
			{
				output.collect(R2, new Text(outValue));
			}
			else
			{
				output.collect(R4, new Text(outValue));
			}
			
		}
		
		// Reducer Number decided by tuple's partition
//		if(Double.valueOf(items[Variables.positionX]) < partitionX - eps)
//		{
//			if(Double.valueOf(items[Variables.positionY]) < partitionY - eps)
//			{
//				output.collect(R1, new Text(outValue));
//			}
//			else if(Double.valueOf(items[Variables.positionY]) > partitionY + eps)
//			{
//				output.collect(R3, new Text(outValue));
//			}
//			else
//			{
//				output.collect(R1, new Text(outValue));
//				output.collect(R3, new Text(outValue));
//			}
//			
//		}
//		else if(Double.valueOf(items[Variables.positionX]) > partitionX + eps)
//		{
//			if(Double.valueOf(items[Variables.positionY]) < partitionY - eps)
//			{
//				output.collect(R2, new Text(outValue));
//			}
//			else if(Double.valueOf(items[Variables.positionY]) > partitionY + eps)
//			{
//				output.collect(R4, new Text(outValue));
//			}
//			else
//			{
//				output.collect(R2, new Text(outValue));
//				output.collect(R4, new Text(outValue));
//			}
//		}
//		else
//		{
//			if(Double.valueOf(items[Variables.positionY]) < partitionY - eps)
//			{
//				output.collect(R1, new Text(outValue));
//				output.collect(R2, new Text(outValue));
//			}
//			else if(Double.valueOf(items[Variables.positionY]) > partitionY + eps)
//			{
//				output.collect(R3, new Text(outValue));
//				output.collect(R4, new Text(outValue));
//			}
//			else
//			{
//				output.collect(R1, new Text(outValue));
//				output.collect(R2, new Text(outValue));
//				output.collect(R3, new Text(outValue));
//				output.collect(R4, new Text(outValue));
//			}
//		}
		
//		
//		if(items[0].equals(Variables.minus))
//		{
//			if(items[1].equals(Variables.minus))
//			{
//				output.collect(R1, new Text(outValue));
//			}
//			else if(items[1].equals(Variables.plus))
//			{
//				output.collect(R3, new Text(outValue));
//			}
//			else if(items[1].equals(Variables.both))
//			{
//				output.collect(R1, new Text(outValue));
//				output.collect(R3, new Text(outValue));
//			}
//			else
//			{
//				System.out.println("Unknown Partition");
//			}
//				
//		}
//		else if(items[0].equals(Variables.plus))
//		{
//			if(items[1].equals(Variables.minus))
//			{
//				output.collect(R2, new Text(outValue));
//			}
//			else if(items[1].equals(Variables.plus))
//			{
//				output.collect(R4, new Text(outValue));
//			}
//			else if(items[1].equals(Variables.both))
//			{
//				output.collect(R2, new Text(outValue));
//				output.collect(R4, new Text(outValue));
//			}
//			else
//			{
//				System.out.println("Unknown Partition");
//			}
//		}
//		else if(items[0].equals(Variables.both))
//		{
//			if(items[1].equals(Variables.minus))
//			{
//				output.collect(R1, new Text(outValue));
//				output.collect(R2, new Text(outValue));
//			}
//			else if(items[1].equals(Variables.plus))
//			{
//				output.collect(R3, new Text(outValue));
//				output.collect(R4, new Text(outValue));
//			}
//			else if(items[1].equals(Variables.both))
//			{
//				output.collect(R1, new Text(outValue));
//				output.collect(R2, new Text(outValue));
//				output.collect(R3, new Text(outValue));
//				output.collect(R4, new Text(outValue));
//			}
//			else
//			{
//				System.out.println("Unknown Partition");
//			}
//		}
//		else
//		{
//			System.out.println("Unknown Partition");
//		}
	}
	
}
