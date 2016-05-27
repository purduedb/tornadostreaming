package edu.purdue.cs.tornado.atlas.SGB.mapred;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Driver {
	public static void main(String[] args) throws IOException, URISyntaxException 
	{
		// This is for Eclipse running 
		args = new String[3];
		args[0] = Variables.pathToDataDir;
		//args[1] = Variables.pathToTempDir;
		args[1] = Variables.pathToTempOutDir;
		
		
		
//		if(args.length != 3)
//			System.out.println("input parameter should be 3: [inputPath] [tempPath] [outputPath]");
		
		
		//File tempDir = new File(Variables.pathToTempDir);
		//tempDir.mkdir();

		Preprocess PD = new Preprocess(args[0]);
		SGBLocalDriver SGBLD = new SGBLocalDriver(args[0], args[1]);
		
		//delete(tempDir);

	}

	public static void delete(File file) throws IOException {

		if (file.isDirectory()) 
		{
			if (file.list().length == 0) 
			{
				file.delete();
			} 
			else 
			{
				String files[] = file.list();

				for (String temp : files) 
				{
					File fileDelete = new File(file, temp);
					delete(fileDelete);
				}

				if (file.list().length == 0) 
				{
					file.delete();
				}
			}

		} 
		else 
		{
			// if file, then delete it
			file.delete();
		}
	}

}
