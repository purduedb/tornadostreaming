package edu.purdue.cs.tornado.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class TestJavaHDFS {
	public static void main(String[] args) throws InterruptedException {
		try {
			 Configuration conf = new Configuration();
			 conf.set("fs.hdfs.impl", 
				        org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()
				    );
			 conf.set("fs.file.impl",
				        org.apache.hadoop.fs.LocalFileSystem.class.getName()
				    );
			  conf.addResource(new Path("datasources/core-site.xml"));
		      Path pt=new Path("hdfs://172.18.11.143:8020/twitterdataset/tweet_us_2015_1_3.csv");
		      FileSystem fs = FileSystem.get(conf);
		      BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(pt)));
		      String line = br.readLine();
		      while (line != null){
		         System.out.println(line);
		         line=br.readLine();
		         // emit the line which was read from the HDFS file
		         // _collector is a private member variable of type SpoutOutputCollector set in the open method;
		        
		      }
		   } catch (Exception e) {
		     e.printStackTrace();
		   }
	}
}
