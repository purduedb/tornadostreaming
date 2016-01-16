package edu.purdue.cs.tornado.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.google.common.base.Stopwatch;

public class TestHDFSVsLocalFSReadTime {
	public static void main(String[] args) throws InterruptedException {
		try {
			readHDFSfile();
			readLFS();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Long readHDFSfile() throws IOException {
		Long duration=new Long(0);
		Stopwatch stopwatch = Stopwatch.createStarted();
		Configuration conf = new Configuration();
		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
		conf.addResource(new Path("datasources/core-site.xml"));
		Path pt = new Path("hdfs://172.18.11.143:8020/partitionedPOIsusa/58_7.csv");
		FileSystem fs = FileSystem.get(conf);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(pt)));
		String line;
		int i=0;
		while ((line = br.readLine()) != null){
			i++;

		}
		stopwatch.stop();
		duration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		System.out.println("HDFS Time to read "+i+" line is "+duration/i);
		return duration;
	}

	public static Long readLFS() throws IOException {
		Long duration=new Long(0);
		Stopwatch stopwatch = Stopwatch.createStarted();
		String filePath = "/home/ahmed/Downloads/partitionedPOIsusa/58_7.csv";

		FileInputStream fstream;
		BufferedReader br;
		String line = "";

		fstream = new FileInputStream(filePath);
		br = new BufferedReader(new InputStreamReader(fstream));
	
		int i = 0;
		while ((line = br.readLine()) != null) {
			i++;

		}
		stopwatch.stop();
		duration=stopwatch.elapsed(TimeUnit.NANOSECONDS);
		
		System.out.println("LFS Time to read "+i+" line is "+duration/i);
		br.close();
		return duration;

	}
}
