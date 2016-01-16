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
import edu.purdue.cs.tornado.messages.CombinedTuple;
import edu.purdue.cs.tornado.messages.Query;

public class CombinedTupleSerializer  extends  com.esotericsoftware.kryo.Serializer<CombinedTuple>{

	@Override
	public CombinedTuple read(Kryo kryo, Input input, Class<CombinedTuple> outputTupleCLass) {
		CombinedTuple outputTuple = new CombinedTuple();
		outputTuple.setDataObject(kryo.readObjectOrNull(input,DataObject.class));
		outputTuple.setDataObject2(kryo.readObjectOrNull(input,DataObject.class));
		outputTuple.setDataObject2List(kryo.readObjectOrNull(input,ArrayList.class));
		outputTuple.setDataObjectCommand(kryo.readObjectOrNull(input,String.class));
		outputTuple.setDataObject2Command(kryo.readObjectOrNull(input,String.class));
		outputTuple.setQuery(kryo.readObjectOrNull(input,Query.class));
		return outputTuple;
	}

	@Override
	public void write(Kryo kryo, Output output, CombinedTuple outputTuple) {
		kryo.writeObjectOrNull(output,outputTuple.getDataObject(),DataObject.class);
		kryo.writeObjectOrNull(output,outputTuple.getDataObject2(),DataObject.class);
		kryo.writeObjectOrNull(output,outputTuple.getDataObject2List(),ArrayList.class);
		kryo.writeObjectOrNull(output,outputTuple.getDataObjectCommand(),String.class);
		kryo.writeObjectOrNull(output,outputTuple.getDataObject2Command(),String.class);
		kryo.writeObjectOrNull(output,outputTuple.getQuery(),Query.class);
		
	}

}
