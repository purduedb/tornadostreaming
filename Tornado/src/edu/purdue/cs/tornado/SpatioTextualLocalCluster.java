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

import java.util.Map;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.generated.SubmitOptions;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.messages.Control;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.DataObjectList;
import edu.purdue.cs.tornado.messages.OutputTuple;
import edu.purdue.cs.tornado.messages.Query;
import edu.purdue.cs.tornado.messages.ResultSetChange;
import edu.purdue.cs.tornado.serializer.ControlSerializer;
import edu.purdue.cs.tornado.serializer.DataObjectListSerializer;
import edu.purdue.cs.tornado.serializer.DataObjectSerializer;
import edu.purdue.cs.tornado.serializer.OutputTupleSerializer;
import edu.purdue.cs.tornado.serializer.PointSerializer;
import edu.purdue.cs.tornado.serializer.QuerySerializer;
import edu.purdue.cs.tornado.serializer.RectangleSerializer;
import edu.purdue.cs.tornado.serializer.ResultSetChangeSerializer;
/**
 * This class is a wrapper around the localcluster to add configuration and functions specific to spatio-textual tornado indexing
 * This allows the use whatever fucntionality existing in the 
 * @author Ahmed Mahmood
 *
 */
public class SpatioTextualLocalCluster extends LocalCluster{
	
	@Override
	public void submitTopology(String topologyName, Map conf,
			StormTopology topology) {
		
		((Config)conf).registerSerialization(Query.class, QuerySerializer.class);
		((Config)conf).registerSerialization(DataObject.class, DataObjectSerializer.class);
		((Config)conf).registerSerialization(DataObjectList.class, DataObjectListSerializer.class);
		((Config)conf).registerSerialization(Control.class, ControlSerializer.class);
		((Config)conf).registerSerialization(Point.class, PointSerializer.class);
		((Config)conf).registerSerialization(Rectangle.class, RectangleSerializer.class);
		((Config)conf).registerSerialization(OutputTuple.class, OutputTupleSerializer.class);
		((Config)conf).registerSerialization(ResultSetChange.class, ResultSetChangeSerializer.class);
		super.submitTopology(topologyName, conf, topology);
		
	}

	@Override
	public void submitTopologyWithOpts(String topologyName, Map conf,
			StormTopology topology, SubmitOptions submitOpts) {
		((Config)conf).registerSerialization(Query.class, QuerySerializer.class);
		((Config)conf).registerSerialization(DataObject.class, DataObjectSerializer.class);
		((Config)conf).registerSerialization(DataObjectList.class, DataObjectListSerializer.class);
		((Config)conf).registerSerialization(Control.class, ControlSerializer.class);
		((Config)conf).registerSerialization(Point.class, PointSerializer.class);
		((Config)conf).registerSerialization(Rectangle.class, RectangleSerializer.class);
		((Config)conf).registerSerialization(OutputTuple.class, OutputTupleSerializer.class);
		((Config)conf).registerSerialization(ResultSetChange.class, ResultSetChangeSerializer.class);
		super.submitTopologyWithOpts(topologyName, conf, topology,submitOpts);
		
	}

	

	
}
