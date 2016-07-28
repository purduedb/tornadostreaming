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
import java.util.HashSet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.loadbalance.LoadBalanceMessage;
import edu.purdue.cs.tornado.messages.Control;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;
//TODO change to support all null values
public class ControlSerializer extends  com.esotericsoftware.kryo.Serializer<Control> {

	@Override
	public Control read(Kryo kryo, Input input, Class<Control> controlClass ) {
		Control control = new Control();
		control.setControlMessageType(kryo.readObjectOrNull(input,String.class));
		control.setDataObjects(kryo.readObjectOrNull(input, ArrayList.class));
		control.setQueriesList(kryo.readObjectOrNull(input, ArrayList.class));
		control.setSinglDataObject(kryo.readObjectOrNull(input, DataObject.class));
		control.setSingleQuery(kryo.readObjectOrNull(input, Query.class));
		control.setResultSetChanges(kryo.readObjectOrNull(input, ArrayList.class));
		control.setLeadBalanceMessage(kryo.readObjectOrNull(input, LoadBalanceMessage.class));//kryo.readObjectOrNull(input, LoadBalanceMessage.class));
		control.setIndexCells(kryo.readObjectOrNull(input, ArrayList.class));
		control.setIndexCell(kryo.readObjectOrNull(input, IndexCell.class));
		control.setSrcId(kryo.readObjectOrNull(input,String.class));
		control.setTextSummery( kryo.readObjectOrNull(input,HashSet.class));
		control.setTextSummeryTaskIdList(kryo.readObjectOrNull(input,ArrayList.class));
		control.setTextSummaryTimeStamp(kryo.readObjectOrNull(input,Long.class));
		control.setTextSummaryTimeStamp(kryo.readObjectOrNull(input,Long.class));
		control.setForwardTextIndexTaskIndex(kryo.readObjectOrNull(input,Integer.class));
		control.setForwardTextSummeryFromGlobalIndex(kryo.readObjectOrNull(input, ArrayList.class));
		return control;
	}

	@Override
	public void write(Kryo kryo, Output output, Control control) {
		kryo.writeObjectOrNull(output,control.getControlMessageType(),String.class);
		kryo.writeObjectOrNull(output,control.getDataObjects(),ArrayList.class);
		kryo.writeObjectOrNull(output,control.getQueriesList(),ArrayList.class);
		kryo.writeObjectOrNull(output,control.getSinglDataObject(),DataObject.class);
		kryo.writeObjectOrNull(output,control.getSingleQuery(),Query.class);
		kryo.writeObjectOrNull(output,control.getResultSetChanges(),ArrayList.class);
		kryo.writeObjectOrNull(output,control.getLeadBalanceMessage(),LoadBalanceMessage.class);
		kryo.writeObjectOrNull(output,control.getIndexCells(),ArrayList.class);
		kryo.writeObjectOrNull(output,control.getIndexCell(),IndexCell.class);
		kryo.writeObjectOrNull(output,control.getSrcId(),String.class);
		kryo.writeObjectOrNull(output,control.getTextSummery(),HashSet.class);
		kryo.writeObjectOrNull(output,control.getTextSummeryTaskIdList(),ArrayList.class);
		kryo.writeObjectOrNull(output,control.getTextSummaryTimeStamp(),Long.class);
		kryo.writeObjectOrNull(output,control.getForwardTextIndexTaskIndex(),Integer.class);
		kryo.writeObjectOrNull(output,control.getForwardTextSummeryFromGlobalIndex(),ArrayList.class);
	}

	

}
