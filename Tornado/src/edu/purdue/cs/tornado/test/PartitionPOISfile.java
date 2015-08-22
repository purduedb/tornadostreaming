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
package edu.purdue.cs.tornado.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;

public class PartitionPOISfile {

	public static void main(String[] args) throws IOException {
		System.out.println("begin");
		String mainFile = "/home/ahmed/Downloads/output.txt";
		FileInputStream mainFileStream = new FileInputStream(mainFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(mainFileStream));

		ArrayList<FileWriter> fwList = new ArrayList<FileWriter>();
		for (int i = 0; i < SpatioTextualConstants.globalGridGranularity*SpatioTextualConstants.globalGridGranularity; i++) {
			fwList.add(new FileWriter("/home/ahmed/Downloads/partitionedPOIsusa2/" + i + ".csv"));
		}
		String poiline = "";
		try {

			// Read File Line By Line
			while((poiline = br.readLine()) != null) {
				
				StringTokenizer stringTokenizer = new StringTokenizer(poiline, ",");
				String id = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
				Double lat = 0.0;
				Double lon = 0.0;
				try {
					lat = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;

					lon = stringTokenizer.hasMoreTokens() ? Double.parseDouble(stringTokenizer.nextToken()) : 0.0;
				} catch (Exception e) {
					e.printStackTrace();
						}
				String textContent = "";
				while (stringTokenizer.hasMoreTokens())
					textContent = textContent + stringTokenizer.nextToken() + " ";
				Point xy = SpatialHelper.convertFromLatLonToXYPoint(new LatLong(lat, lon));
				Integer indexCellIndex = mapDataPointToEvaluatorTask(xy.getX(), xy.getY());
				fwList.get(indexCellIndex).write(id+","+lat+","+lon+","+textContent+"\n");
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);

		}

		for (int i = 0; i < SpatioTextualConstants.globalGridGranularity*SpatioTextualConstants.globalGridGranularity; i++)
			fwList.get(i).close();
		br.close();
		System.out.println("done");
	}
	private static Integer mapDataPointToEvaluatorTask(Double x, Double y) {
		Double xStep = SpatioTextualConstants.xMaxRange/SpatioTextualConstants.globalGridGranularity;
		Double yStep = SpatioTextualConstants.yMaxRange/SpatioTextualConstants.globalGridGranularity;
	
		
		Integer xCell = (int) (x / xStep);
		Integer yCell = (int) (y / yStep);
		if(xCell>=SpatioTextualConstants.xMaxRange/xStep)
			xCell=(int) ((SpatioTextualConstants.xMaxRange/xStep)-1);
		if(yCell>=SpatioTextualConstants.yMaxRange/xStep)
			yCell=(int) ((SpatioTextualConstants.yMaxRange/yStep)-1);
		if(xCell<0)
			xCell=0;
		if(yCell<0)
			yCell=0;
		
		Integer partitionNum = xCell *SpatioTextualConstants.globalGridGranularity + yCell;
		return partitionNum;
	}

}
