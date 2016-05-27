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
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.messages.Query;

public class QuerySerializer extends  com.esotericsoftware.kryo.Serializer<Query> {

	@Override
	public Query read(Kryo kryo, Input input, Class<Query> queryClass) {
		Query query = new Query();
		query.setSrcId(kryo.readObjectOrNull(input,String.class));
		query.setQueryType(kryo.readObjectOrNull(input,String.class));
		query.setQueryId(kryo.readObjectOrNull(input,Integer.class));
		query.setFocalPoint(kryo.readObjectOrNull(input, Point.class));
		query.setK(kryo.readObjectOrNull(input,Integer.class));
		query.setQueryText(kryo.readObjectOrNull(input, ArrayList.class));
		query.setQueryText2(kryo.readObjectOrNull(input, ArrayList.class));
		query.setTimeStamp(kryo.readObjectOrNull(input,Long.class));
		query.setSpatialRange(kryo.readObjectOrNull(input, Rectangle.class));
		query.setDistance(kryo.readObjectOrNull(input, Double.class));
		query.setDataSrc(kryo.readObjectOrNull(input, String.class));
		query.setDataSrc2(kryo.readObjectOrNull(input, String.class));
		query.setCommand(kryo.readObjectOrNull(input, Command.class));
		query.setContinousQuery(kryo.readObjectOrNull(input, Boolean.class));
		query.setTextualPredicate(kryo.readObjectOrNull(input, TextualPredicate.class));
		query.setTextualPredicate2(kryo.readObjectOrNull(input, TextualPredicate.class));
		query.setJoinTextualPredicate(kryo.readObjectOrNull(input, TextualPredicate.class));
		query.setFarthestDistance(kryo.readObjectOrNull(input, Double.class));
		query.setRemoveTime(kryo.readObjectOrNull(input, Long.class));
		return query;
	}

	@Override
	public void write(Kryo kryo, Output output, Query query) {
		
		kryo.writeObjectOrNull(output,query.getSrcId(),String.class);
		kryo.writeObjectOrNull(output,query.getQueryType(),String.class);
		kryo.writeObjectOrNull(output,query.getQueryId(),Integer.class);
		kryo.writeObjectOrNull(output, query.getFocalPoint(),Point.class);
		kryo.writeObjectOrNull(output, query.getK(),Integer.class);
		kryo.writeObjectOrNull(output,query.getQueryText(),ArrayList.class);
		kryo.writeObjectOrNull(output,query.getQueryText2(),ArrayList.class);
		kryo.writeObjectOrNull(output,query.getTimeStamp(),Long.class);
		kryo.writeObjectOrNull(output, query.getSpatialRange(),Rectangle.class);
		kryo.writeObjectOrNull(output, query.getDistance(),Double.class);
		kryo.writeObjectOrNull(output, query.getDataSrc(),String.class);
		kryo.writeObjectOrNull(output, query.getDataSrc2(),String.class);
		kryo.writeObjectOrNull(output, query.getCommand(),Command.class);
		kryo.writeObjectOrNull(output, query.getContinousQuery(),Boolean.class);
		kryo.writeObjectOrNull(output, query.getTextualPredicate(),TextualPredicate.class);
		kryo.writeObjectOrNull(output, query.getTextualPredicate2(),TextualPredicate.class);
		kryo.writeObjectOrNull(output, query.getJoinTextualPredicate(),TextualPredicate.class);
		kryo.writeObjectOrNull(output, query.getFarthestDistance(),Double.class);
		kryo.writeObjectOrNull(output, query.getRemoveTime(),Long.class);
	}

}
