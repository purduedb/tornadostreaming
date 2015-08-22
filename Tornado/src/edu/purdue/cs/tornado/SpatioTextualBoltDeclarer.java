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
package edu.purdue.cs.tornado;

import java.util.HashMap;


public interface SpatioTextualBoltDeclarer{
	public SpatioTextualBoltDeclarer addPersistentSpatioTextualInput(String componentId,String streamID);
	public SpatioTextualBoltDeclarer addPersistentSpatioTextualInput(String componentId);
	public SpatioTextualBoltDeclarer addVolatileSpatioTextualInput(String componentId,String streamID);
	public SpatioTextualBoltDeclarer addCurrentSpatioTextualInput(String componentId);
	public SpatioTextualBoltDeclarer addCurrentSpatioTextualInput(String componentId,String streamID);
	public SpatioTextualBoltDeclarer addVolatileSpatioTextualInput(String componentId);
	public SpatioTextualBoltDeclarer addContinuousQuerySource(String componentId,String streamID);
	public SpatioTextualBoltDeclarer addContinuousQuerySource(String componentId);
	public SpatioTextualBoltDeclarer addSnapShotQuerySource(String componentId,String streamID);
	public SpatioTextualBoltDeclarer addSnapShotQuerySource(String componentId);
	public SpatioTextualBoltDeclarer addStaticDataSource(String componentId, String sourceClassName,HashMap<String, String> conf);
}
