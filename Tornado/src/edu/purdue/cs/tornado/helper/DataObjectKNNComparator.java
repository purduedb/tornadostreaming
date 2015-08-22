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
package edu.purdue.cs.tornado.helper;

import java.util.Comparator;

import edu.purdue.cs.tornado.messages.DataObject;

public class DataObjectKNNComparator implements Comparator<DataObject>{

	Point focal = new Point();
	public DataObjectKNNComparator (Point  focal){
		this.focal = focal;
	}
	@Override
	public int compare(DataObject o1, DataObject o2) {
		Double o1Dist = SpatialHelper.getDistanceInBetween(o1.getLocation(), focal);
		Double o2Dist = SpatialHelper.getDistanceInBetween(o2.getLocation(), focal);
		
		if (o1Dist < o2Dist)
			return 1;
		if (o1Dist > o2Dist)
			return -1;
		return 0;
	}

}
