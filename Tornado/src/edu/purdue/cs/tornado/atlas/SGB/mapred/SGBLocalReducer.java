package edu.purdue.cs.tornado.atlas.SGB.mapred;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import edu.purdue.cs.tornado.atlas.SGB.global.AttrType;
import edu.purdue.cs.tornado.atlas.SGB.global.Schema;
import edu.purdue.cs.tornado.atlas.SGB.global.Tuple;
import edu.purdue.cs.tornado.atlas.SGB.opr.SGBAllIndex;

public class SGBLocalReducer extends MapReduceBase implements
		Reducer<IntWritable, Text, Text, Text>

{
	private static Schema s_drivers;

	@Override
	public void reduce(IntWritable key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
	{
		s_drivers = new Schema(3);
		s_drivers.initField(Variables.positionKey, AttrType.INTEGER, 4, "PKey");
		s_drivers.initField(Variables.positionX, AttrType.INTEGER, 4, "x");
		s_drivers.initField(Variables.positionY, AttrType.INTEGER, 4, "y");

		System.out.println("Running on Reducer " + key);

		SGBAllIndex iter = new SGBAllIndex();

		int overlap = AttrType.OANY;
		String indexongroups = "rtree";
		float eps = Variables.eps;
		int[] Fileds = {1,2};
		int metric = AttrType.L2;

		iter.iniEnv(metric, overlap, indexongroups, eps, Fileds);

		int number = 0;
		while (values.hasNext()) 
		{
			Tuple tuple = new Tuple(s_drivers);
			String items[] = values.next().toString().split(Variables.splitter);

			tuple.setFiled(Variables.positionKey, Integer.valueOf(items[0]));
			tuple.setFiled(Variables.positionX, Double.valueOf(items[1]));
			tuple.setFiled(Variables.positionY, Double.valueOf(items[2]));

			iter.OprCurrent(tuple);
			
			System.out.print(items[0] +" ");
			number++;
		}
		System.out.println("");
			
		iter.PrintOutputs();
		
		iter.getGroupsInOverlapRegion();

	}

}
