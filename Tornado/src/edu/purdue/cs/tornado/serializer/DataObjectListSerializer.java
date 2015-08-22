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

import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.DataObjectList;
//TODO change to support all null values
public class DataObjectListSerializer extends com.esotericsoftware.kryo.Serializer<DataObjectList>{

	@Override
	public DataObjectList read(Kryo kryo, Input input, Class<DataObjectList> dataObjectListClass) {
		DataObjectList dataObjectList= new DataObjectList();
		ArrayList<DataObject> dataObjects = new ArrayList<DataObject>();
		int size = input.readInt();
		for (int i =0 ;i< size;i++){
			DataObject dataObject = new DataObject();
			dataObject=kryo.readObject(input, DataObject.class);
			dataObjects.add(dataObject);
		}
			
		dataObjectList.setDataObjects(dataObjects);
		return dataObjectList;
	}

	@Override
	public void write(Kryo kryo, Output output, DataObjectList dataObjectList) {
		int size =0;
		if (dataObjectList.getDataObjects()!=null)
			size =dataObjectList.getDataObjects().size();
		output.writeInt(size);
		for(int i =0;i<size;i++)
			kryo.writeObject(output, dataObjectList.getDataObjects().get(i));
		
	}

}
