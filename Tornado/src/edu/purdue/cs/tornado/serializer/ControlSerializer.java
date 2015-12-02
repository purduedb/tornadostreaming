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

import edu.purdue.cs.tornado.loadbalance.LoadBalanceMessage;
import edu.purdue.cs.tornado.messages.Control;
//TODO change to support all null values
public class ControlSerializer extends  com.esotericsoftware.kryo.Serializer<Control> {

	@Override
	public Control read(Kryo kryo, Input input, Class<Control> controlClass ) {
		Control control = new Control();
		control.setControlMessageType(kryo.readObjectOrNull(input,String.class));
		control.setDataObjects(kryo.readObjectOrNull(input, ArrayList.class));
		control.setQueriesList(kryo.readObjectOrNull(input, ArrayList.class));
		control.setResultSetChanges(kryo.readObjectOrNull(input, ArrayList.class));
		control.setLeadBalanceMessage(kryo.readObjectOrNull(input, LoadBalanceMessage.class));//kryo.readObjectOrNull(input, LoadBalanceMessage.class));
		return control;
	}

	@Override
	public void write(Kryo kryo, Output output, Control control) {
		kryo.writeObjectOrNull(output,control.getControlMessageType(),String.class);
		kryo.writeObjectOrNull(output,control.getDataObjects(),ArrayList.class);
		kryo.writeObjectOrNull(output,control.getQueriesList(),ArrayList.class);
		kryo.writeObjectOrNull(output,control.getResultSetChanges(),ArrayList.class);
		kryo.writeObjectOrNull(output,control.getLeadBalanceMessage(),LoadBalanceMessage.class);
	}

	

}
