package edu.purdue.cs.tornado.atlas.SGB.mapred;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import edu.purdue.cs.tornado.atlas.SGB.global.AttrType;
import edu.purdue.cs.tornado.atlas.SGB.global.Schema;
import edu.purdue.cs.tornado.atlas.SGB.global.Tuple;

public class Preprocess 
{
	/** Drivers table schema. */
	private static Schema s_drivers;
	ArrayList<Tuple> tupleList;
	
	int partitionX;
	int partitionY;
	
	double partitionMaxX = Integer.MIN_VALUE;
	double partitionMinX = Integer.MAX_VALUE;
	
	double partitionMaxY = Integer.MIN_VALUE;
	double partitionMinY = Integer.MAX_VALUE;
	
	float eps;
	
	
	public Preprocess(String inputPath) throws IOException
	{
		this.eps = Variables.eps;
		
		this.tupleList = new ArrayList<Tuple>();
		
		s_drivers = new Schema(3);
		s_drivers.initField(Variables.positionKey, AttrType.INTEGER, 4, "PKey");
		s_drivers.initField(Variables.positionX, AttrType.INTEGER, 4, "x");
		s_drivers.initField(Variables.positionY, AttrType.INTEGER, 4, "y");
		

		readFile(inputPath);
		getPartition();
		writeFile(Variables.pathToTempDir);
	}
	
	private void writeFile(String tempOutPath) throws IOException
	{
		
		
//		FileWriter fw = new FileWriter(tempOutPath+Variables.tempFile);
//		BufferedWriter bw = new BufferedWriter(fw);
//		PrintWriter out = new PrintWriter(bw);
		
		
		//This is for HDFS
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		Path outFile = new Path(tempOutPath+Variables.tempFile);
		FSDataOutputStream out = fs.create(outFile);
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
		//PrintWriter out = new PrintWriter(outs);
		
		
		String line = "";
		line += this.eps;
		bw.write(line);
		bw.newLine();

		line = "";
		line += Math.floor(partitionMinX) + eps * (partitionX + 1);
		bw.write(line);
		bw.newLine();
		
		line = "";
		line += Math.floor(partitionMinY) + eps * (partitionY + 1);
		bw.write(line);
		bw.newLine();		
		
//		line = "";
//		line += this.partitionX;
//		bw.write(line);
//		bw.newLine();
//		
//		line = "";
//		line += this.partitionY;
//		bw.write(line);
//		bw.newLine();
		
//		
//		for(Tuple t : tupleList)
//		{
//			line = "";
//			
//			Object[] os = t.getAllFields();
//			
//			// Write appropriate partition sign for X, Y
//			if(Double.valueOf(t.getFiled(1).toString()) < Math.floor(partitionMinX) + eps * partitionX - eps)
//				line += Variables.minus + "\t";
//			else if(Double.valueOf(t.getFiled(1).toString()) > Math.floor(partitionMinX) + eps * partitionX + eps)
//				line += Variables.plus + "\t";
//			else
//				line += Variables.both + "\t";
//				
//			if(Double.valueOf(t.getFiled(2).toString()) < Math.floor(partitionMinY) + eps * partitionY - eps)
//				line += Variables.minus;
//			else if(Double.valueOf(t.getFiled(2).toString()) > Math.floor(partitionMinY) + eps * partitionY + eps)
//				line += Variables.plus;
//			else
//				line += Variables.both;
//			
//			line += "\t";
//			
//			for(Object o : os)
//			{	
//				line += o.toString() + "\t";
//			}
//			bw.write(line);
//			bw.newLine();
//		}
		bw.close();
		out.close();
//		bw.close();
//		fw.close();
		
		
	}  
	
	private void readFile(String inputPath) throws IOException 
	{

		double x,y;
		
		// This is for normal FS
//		FileInputStream fstream = new FileInputStream(Variables.pathToDataDir+Variables.inputFile);
//		DataInputStream in = new DataInputStream(fstream);
		
		
		//This is for HDFS
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path inFile = new Path(inputPath+Variables.inputFile);
		FSDataInputStream in = fs.open(inFile);
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		
		while((line = br.readLine()) != null)
		{
			Tuple tuple = new Tuple(s_drivers);
			String[] items = line.split(Variables.splitter);

			tuple.setFiled(Variables.positionKey, Integer.valueOf(items[Variables.positionKey]));
			x = Double.valueOf(items[Variables.positionX]);
			y = Double.valueOf(items[Variables.positionY]);
			
			
			// get max and min value of the whole points to get partition border
			if(x > this.partitionMaxX)
				this.partitionMaxX = x;
			if(x < this.partitionMinX)
				this.partitionMinX = x;
			
			tuple.setFiled(Variables.positionX, x);
			
			if(y > this.partitionMaxY)
				this.partitionMaxY = y;
			if(y < this.partitionMinY)
				this.partitionMinY = y;
			
			tuple.setFiled(Variables.positionY, y);
			this.tupleList.add(tuple);
		}
		br.close();
		in.close();
		//fstream.close();
		
	}
	
	private void getPartition()
	{
		double x;
		double y;
		
		int bucketXLength = (int)Math.ceil((Math.floor(partitionMaxX) 
								- Math.ceil(partitionMinX)) 
								/ eps);
		int bucketYLength = (int)Math.ceil((Math.floor(partitionMaxY) 
								- Math.ceil(partitionMinY))
								/ eps);
				
		int[] bucketX = new int[bucketXLength + 1];
		int[] bucketY = new int[bucketYLength + 1];

		for(Tuple t : tupleList)
		{
			x = Double.valueOf(t.getFiled(Variables.positionX).toString());
		
			// count bucket
			bucketX[(int)Math.floor((x - Math.floor(partitionMinX)) / eps)]++;
			
			// count bucket twice for the points who lie on the bucket's border
			if(x  == (int)x
					&& (int)(x - Math.floor(partitionMinX)) % eps == 0
					&& x != Math.floor(partitionMinX)
					&& x != Math.ceil(partitionMaxX))	
				bucketX[(int)Math.floor((x - Math.floor(partitionMinX)) / eps) - 1]++;

			
			y = Double.valueOf(t.getFiled(Variables.positionY).toString());
			
			bucketY[(int)Math.floor((y - Math.floor(partitionMinY)) / eps)]++;

			if(y  == (int)y
					&& (int)(y - Math.floor(partitionMinY)) % eps == 0
					&& y != Math.floor(partitionMinY)
					&& y != Math.ceil(partitionMaxY))	
				bucketY[(int)Math.floor((y - Math.floor(partitionMinY)) / eps) - 1]++;	
		}
		
		// last integer point goes to independent bucket.
		bucketX[bucketXLength - 1] += bucketX[bucketXLength];
		bucketY[bucketYLength - 1] += bucketY[bucketYLength];
		bucketX[bucketXLength] = 0;
		bucketY[bucketYLength] = 0;
		
		
		// get balanced partition
		int partitionX = -1;
		int partitionY = -1;
		int minDifferenceX = Integer.MAX_VALUE;
		int differenceX = 0;
		
		for(int i = 0; i < bucketXLength; i++)
		{
			differenceX = Math.abs(getLeftSum(bucketX, i) - getRightSum(bucketX, i));
			if(differenceX < minDifferenceX)
			{
				partitionX = i;
				minDifferenceX = differenceX;
			}
		}
		
		int minDifferenceY = Integer.MAX_VALUE;
		int differenceY = 0;
		for(int i = 0; i < bucketYLength; i++)
		{
			differenceY = Math.abs(getLeftSum(bucketY, i) - getRightSum(bucketY, i));
			if(differenceY < minDifferenceY)
			{
				partitionY = i;
				minDifferenceY = differenceY;
			}
		}
		
		
		
		this.partitionX = partitionX;
		this.partitionY = partitionY;
		
		
		
		
		for( int xx: bucketX)
		{
			System.out.print(xx + " ");
		}
		System.out.println("");
		
		for( int xx: bucketY)
		{
			System.out.print(xx + " ");
		}
		System.out.println("");
		
		System.out.println(partitionX + " " + partitionY);
		System.out.println(minDifferenceX + " " + minDifferenceY );
	}
	
	private int getLeftSum(int[] bucket, int partition)
	{
		int sum = 0;
		for(int i = 0; i <= partition; i++)
		{
			sum += bucket[i];
		}
		
		return sum;
	}
	
	private int getRightSum(int[] bucket, int partition)
	{
		int sum = 0;
		for(int i = partition; i < bucket.length; i++)
		{
			sum += bucket[i];
		}
		
		return sum;
	}
	


}
