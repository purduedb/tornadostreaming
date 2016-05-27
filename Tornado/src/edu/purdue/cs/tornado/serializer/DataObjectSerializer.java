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
package edu.purdue.cs.tornado.serializer;

import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.messages.DataObject;
//TODO change to support all null values
public class DataObjectSerializer extends  com.esotericsoftware.kryo.Serializer<DataObject>{



	@Override
	public DataObject read(Kryo kryo, Input input, Class<DataObject> dataObjectClass) {
		DataObject dataObject = new DataObject();
		dataObject.setSrcId(kryo.readObjectOrNull(input, String.class));
		dataObject.setObjectId(kryo.readObjectOrNull(input, Integer.class));
		dataObject.setLocation(kryo.readObjectOrNull(input, Point.class));
		dataObject.setObjectText(kryo.readObjectOrNull(input, ArrayList.class));
		dataObject.setOriginalText(kryo.readObjectOrNull(input, String.class));
		dataObject.setTimeStamp(kryo.readObjectOrNull(input, Long.class));
		dataObject.setRelevantArea(kryo.readObjectOrNull(input, Rectangle.class));
		dataObject.setCommand(kryo.readObjectOrNull(input, Command.class));
		
		return dataObject;
	}

	@Override
	public void write(Kryo kryo, Output output, DataObject dataObject) {
		kryo.writeObjectOrNull(output,dataObject.getSrcId(),String.class);
		kryo.writeObjectOrNull(output,dataObject.getObjectId(),Integer.class);
		kryo.writeObjectOrNull(output,dataObject.getLocation(),Point.class);
		kryo.writeObjectOrNull(output,dataObject.getObjectText(),ArrayList.class);
		kryo.writeObjectOrNull(output,dataObject.getOriginalText(),String.class);
		kryo.writeObjectOrNull(output,dataObject.getTimeStamp(),Long.class);
		kryo.writeObjectOrNull(output,dataObject.getRelevantArea(),Rectangle.class);
		kryo.writeObjectOrNull(output,dataObject.getCommand(),Command.class);

		
	}

}
