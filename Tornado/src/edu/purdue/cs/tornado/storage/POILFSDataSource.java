/**
 * Copyright Jul 13, 2015
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
package edu.purdue.cs.tornado.storage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import edu.purdue.cs.tornado.helper.LatLong;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.messages.DataObject;

public class POILFSDataSource extends AbstractStaticDataSource {
	public static final String POI_FOLDER_PATH = "POI_FOLDER_PATH";

	ArrayList<BufferedReader> brArr;
	ArrayList<FileInputStream> fsArr;
	ArrayList<String> pathArr;
	String str;
	String corePath;
	String folderPath;
	Integer currentOpenedFile;
	Double xrange;
	Double yrange;
	Integer xCellsNum;
	Integer yCellsNum;
	Double xStep;
	Double yStep;
	String currentLine = null;
	Integer countId=0;

	public POILFSDataSource(Rectangle bounds, Map<String, String> config, String sourceId, Integer selfTaskId, Integer selfTaskIdIndex) {
		super(bounds, config, sourceId, selfTaskId, selfTaskIdIndex);
		prepareData();
	}

	public void prepareData() {
		countId=0;
		xrange = SpatioTextualConstants.xMaxRange;
		yrange = SpatioTextualConstants.yMaxRange;
		xCellsNum = SpatioTextualConstants.defaultFineGridGranularityX;
		yCellsNum = SpatioTextualConstants.defaultFineGridGranularityY;
		xStep = xrange / xCellsNum;
		yStep = yrange / yCellsNum;
		folderPath = config.get(POI_FOLDER_PATH);
		brArr = new ArrayList<BufferedReader>();
		fsArr = new ArrayList<FileInputStream>();
		pathArr = new ArrayList<String>();
		currentOpenedFile = 0;
		int xMinCell = (int) (bounds.getMin().getX() / xStep);
		int yMinCell = (int) (bounds.getMin().getY() / yStep);
		int xMaxCell = (int) (bounds.getMax().getX() / xStep);
		int yMaxCell = (int) (bounds.getMax().getY() / yStep);
		//to handle the case where data is outside the range of the bolts 
		if (xMaxCell >= SpatioTextualConstants.xMaxRange / xStep)
			xMaxCell = (int) ((SpatioTextualConstants.xMaxRange / xStep) );
		if (yMaxCell >= SpatioTextualConstants.yMaxRange / xStep)
			yMaxCell = (int) ((SpatioTextualConstants.yMaxRange / yStep) );
		if (xMinCell < 0)
			xMinCell = 0;
		if (yMinCell < 0)
			yMinCell = 0;


		for (int i = xMinCell; i < xMaxCell; i++) {
			for (int j = yMinCell; j < yMaxCell; j++) {
				String filePath = folderPath + j + "_" + i + ".csv";
				pathArr.add(filePath);

			}
		}
		try {
			FileInputStream fstream = new FileInputStream(pathArr.get(currentOpenedFile));
			fsArr.add(fstream);
			brArr.add(new BufferedReader(new InputStreamReader(fstream)));
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	@Override
	public Boolean hasNext() {
		while (currentOpenedFile < pathArr.size()) {
			try {
				if ((currentLine = brArr.get(currentOpenedFile).readLine()) != null)
					return true;
				else {
					fsArr.get(currentOpenedFile).close();
					brArr.get(currentOpenedFile).close();
					currentOpenedFile++;
					if (currentOpenedFile < pathArr.size()) {
						FileInputStream fstream = new FileInputStream(pathArr.get(currentOpenedFile));
						fsArr.add(fstream);
						brArr.add(new BufferedReader(new InputStreamReader(fstream)));
					}
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		return false;

	}

	@Override
	public DataObject getNext() {
		if (currentLine != null) {
			DataObject obj = mapLineToDataObj(currentLine);
			currentLine = null;
			return obj;
		} else {
			if (hasNext()) {
				DataObject obj = mapLineToDataObj(currentLine);
				currentLine = null;
				return obj;
			}

		}
		return null;
	}

	private DataObject mapLineToDataObj(String line) {
		StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
		String id = "", text = "";
		if(countId>=Integer.MAX_VALUE)countId=0;
		Double lat, lon;
		if (stringTokenizer.hasMoreTokens()) {
			id = stringTokenizer.nextToken();
		} else
			return null;
		if (stringTokenizer.hasMoreTokens()) {
			lat = Double.parseDouble(stringTokenizer.nextToken());
		} else
			return null;
		if (stringTokenizer.hasMoreTokens()) {
			lon = Double.parseDouble(stringTokenizer.nextToken());
		} else
			return null;
		if (stringTokenizer.hasMoreTokens()) {
			text = stringTokenizer.nextToken();
		} else
			text = "";
		Point point = SpatialHelper.convertFromLatLonToXYPoint(new LatLong(lat, lon));
		DataObject dataObject = new DataObject();
		Date date = new Date();
		// for now just building random data
		dataObject.setOriginalText(text);
		ArrayList<String> textContent = TextHelpers.transformIntoSortedArrayListOfString(text);
		dataObject.setLocation(point);
		dataObject.setObjectId(countId++);
		dataObject.setObjectText(textContent);
		dataObject.setSrcId(sourceId);
		dataObject.setTimeStamp(date.getTime());
		return dataObject;

	}

	@Override
	public void close() {
		for (FileInputStream fsInputStream : fsArr) {
			try {
				fsInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (BufferedReader bufferedReader : brArr) {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
