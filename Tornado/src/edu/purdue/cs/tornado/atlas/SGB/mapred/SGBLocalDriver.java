package edu.purdue.cs.tornado.atlas.SGB.mapred;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

public class SGBLocalDriver 
{
	public SGBLocalDriver(String inputPath, String outputPath) throws URISyntaxException
	{
		run(inputPath, outputPath);
	}
	
	public void run(String inputPath, String outputPath) throws URISyntaxException
	{
		JobClient client = new JobClient();
		// Configurations for Job set in this variable
		JobConf conf = new JobConf();
		
		// Name of the Job
		conf.setJobName("LocalSGB");
		
		// Data type of Output Key and Value
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		
		
		
		conf.setJarByClass(edu.purdue.cs.tornado.atlas.SGB.mapred.SGBLocalDriver.class);
		System.out.println("JarClass : " + conf.getJar());
		
		
		
		// Setting the Mapper, Partitioner and Reducer Class
		conf.setMapperClass(edu.purdue.cs.tornado.atlas.SGB.mapred.SGBLocalMapper.class);
		conf.setPartitionerClass(edu.purdue.cs.tornado.atlas.SGB.mapred.SGBLocalPartitioner.class);
		conf.setReducerClass(edu.purdue.cs.tornado.atlas.SGB.mapred.SGBLocalReducer.class);
		
		
		// Set distributed cache to get partition values
		DistributedCache.addCacheFile(new URI(Variables.pathToTempDir+Variables.tempFile), conf);
		
		
		// Set map output key/value type
		conf.setMapOutputKeyClass(IntWritable.class);
		conf.setMapOutputValueClass(Text.class);
		
		// set the number of reducer
		conf.setNumReduceTasks(Variables.numReducer);

		// Formats of the Data Type of Input and output
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		// Specify input and output DIRECTORIES (not files)
		
		//FileInputFormat.setInputPaths(conf, new Path(Variables.pathToTempDir));
		FileInputFormat.setInputPaths(conf, new Path(inputPath));
		//FileOutputFormat.setOutputPath(conf, new Path(Variables.pathToTempOutDir));
		FileOutputFormat.setOutputPath(conf, new Path(outputPath));
		
		client.setConf(conf);
		
		try {
			// Running the job with Configurations set in the conf.
			JobClient.runJob(conf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
