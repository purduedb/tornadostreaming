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

import java.util.Map;

import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.messages.DataObject;

public abstract class  AbstractStaticDataSource {
	 protected Rectangle bounds;
	 protected Map<String, String> config;
	 protected String sourceId;
	 protected Integer selfTaskId;
	 protected Integer selfTaskIdIndex;
	 
	
	public  AbstractStaticDataSource(Rectangle bounds,
			Map<String, String> config, String sourceId,Integer selfTaskId,Integer selfTaskIdIndex){
		this.bounds = bounds;
		this.config = config;
		this.sourceId = sourceId;
		this.selfTaskId = selfTaskId;
		this.selfTaskIdIndex = selfTaskIdIndex;
		prepareData();
	}
	abstract public void prepareData();
	abstract  public Boolean hasNext();
	abstract   public DataObject getNext() ;
	abstract   public void close() ;
}
