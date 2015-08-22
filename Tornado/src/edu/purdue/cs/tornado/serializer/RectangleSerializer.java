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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;

public class RectangleSerializer extends  com.esotericsoftware.kryo.Serializer<Rectangle> {
	//TODO change to support all null values
	@Override
	public Rectangle read(Kryo kryo, Input input, Class<Rectangle> rectangleClass) {
		Point min =kryo.readObject(input, Point.class);
		Point max =kryo.readObject(input, Point.class);
		Rectangle rectangle = new Rectangle(min, max);
		return rectangle;
	}

	@Override
	public void write(Kryo kryo, Output output, Rectangle rectangle) {
		
		kryo.writeObject(output, rectangle.getMin());
		kryo.writeObject(output, rectangle.getMax());
	}

}
