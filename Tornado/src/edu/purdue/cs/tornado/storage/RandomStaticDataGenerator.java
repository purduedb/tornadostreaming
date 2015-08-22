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
package edu.purdue.cs.tornado.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.RandomGenerator;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.StringHelpers;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.spouts.SampleTextualContent;

public class RandomStaticDataGenerator extends AbstractStaticDataSource{
	

	private Integer count;
	private Integer maxCount;


	public RandomStaticDataGenerator(Rectangle bounds,
			Map<String, String> config, String sourceId,Integer selfTaskId,Integer selfTaskIdIndex) {
		super(bounds, config, sourceId, selfTaskIdIndex, selfTaskIdIndex);
		
	}

	public void  prepareData() {
		maxCount = SpatioTextualConstants.maxStaticDataEntriesCount;
		count = 0;
	}

	public Boolean hasNext() {
		if (count <= maxCount)
			return true;
		else
			return false;
	}

	public DataObject getNext() {
		if (count <= maxCount) {
			Date date = new Date();
			// for now just building random data
			RandomGenerator randomGenerator = new RandomGenerator(
					SpatioTextualConstants.generatorSeed);
			Double xCoord = randomGenerator.nextDouble(bounds.getMin().getX(),
					bounds.getMax().getX());
			Double yCoord = randomGenerator.nextDouble(bounds.getMin().getY(),
					bounds.getMax().getY());
			ArrayList<String> textContent = new ArrayList<String>();
			for (int j = 0; j < SpatioTextualConstants.queryTextualContentLength; j++)
				textContent.add(SampleTextualContent.TextArr[randomGenerator
						.nextInt(SampleTextualContent.TextArr.length - 1)]);

			textContent = StringHelpers.sortTextArrayList(textContent);

			DataObject dataObject = new DataObject();
			dataObject.setLocation(new Point(xCoord, yCoord));
			dataObject.setObjectId(""+count);
			dataObject.setObjectText(textContent);
			dataObject.setSrcId(sourceId);
			dataObject.setTimeStamp(date.getTime());
			count++;
			return dataObject;
		} else
			return null;
	}

}
