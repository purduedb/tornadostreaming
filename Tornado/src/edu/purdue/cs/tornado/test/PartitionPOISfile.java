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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
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

		ArrayList<ArrayList<FileWriter>> fwList = new ArrayList<ArrayList<FileWriter>>();
		for (int i = 0; i < SpatioTextualConstants.defaultFineGridGranularityX; i++){
			ArrayList<FileWriter> jfwList = new ArrayList<FileWriter>();
			for(int j = 0; j < SpatioTextualConstants.defaultFineGridGranularityX; j++) {
				jfwList.add(new FileWriter("/home/ahmed/Downloads/partitionedPOIsusa/" + i +"_"+j+ ".csv"));
			}
			fwList.add(jfwList);
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
					if(lat<SpatioTextualConstants.minLat||lat>SpatioTextualConstants.maxLat||lon<SpatioTextualConstants.minLong||lon>SpatioTextualConstants.maxLong)
						continue ;
				} catch (Exception e) {
					e.printStackTrace();
						}
				String textContent = "";
				while (stringTokenizer.hasMoreTokens())
					textContent = textContent + stringTokenizer.nextToken() + " ";
				Point xy = SpatialHelper.convertFromLatLonToXYPoint(new LatLong(lat, lon));
				IndexCellCoordinates indexCellIndex = mapDataPointToEvaluatorTask(xy.getX(), xy.getY());
				fwList.get(indexCellIndex.getX()).get(indexCellIndex.getY()).write(id+","+lat+","+lon+","+textContent+"\n");
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);

		}

		for (int i = 0; i < SpatioTextualConstants.defaultFineGridGranularityX;i++)
			for(int j=0;j<SpatioTextualConstants.defaultFineGridGranularityY; j++)
			fwList.get(i).get(j).close();
		br.close();
		System.out.println("done");
	}
	private static IndexCellCoordinates mapDataPointToEvaluatorTask(Double x, Double y) {
		Double xStep = SpatioTextualConstants.xMaxRange/SpatioTextualConstants.defaultFineGridGranularityX;
		Double yStep = SpatioTextualConstants.yMaxRange/SpatioTextualConstants.defaultFineGridGranularityY;
	
		
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
		IndexCellCoordinates indexCellCoordinates = new IndexCellCoordinates(xCell, yCell);
	//	Integer partitionNum = xCell *SpatioTextualConstants.fineGridGranularityY + yCell;
		return indexCellCoordinates;
	}

}
