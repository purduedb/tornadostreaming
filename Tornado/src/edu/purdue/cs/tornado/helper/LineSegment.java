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
package edu.purdue.cs.tornado.helper;

public class LineSegment 
{
	public Point Start;
	public Point End;
	public int SegmentID;
	
	public LineSegment(Point start, Point end)
	{
		this.Start = start;
		this.End = end;
	}
	
	public LineSegment(Double xStart, Double yStart, Double xEnd, Double yEnd)
	{
		this.SegmentID = 0;
		this.Start = new Point(xStart, yStart);
		this.End = new Point(xEnd, yEnd);
	}
	
	public LineSegment(Double xStart, Double yStart, Double xEnd, Double yEnd, int segmentID)
	{
		this.SegmentID = segmentID;
		this.Start = new Point(xStart, yStart);
		this.End = new Point(xEnd, yEnd);
	}
	
	public LineSegment()
	{
		this.Start = null;
		this.End = null;
	}
	
	public boolean contains(Point point)
	{
		boolean pointOnLine = false;
		if(hasSlope())
		{
			//line equation is y = a * x + b
			Double a = getSlope();
			Double b = Start.Y - a * Start.X;
			Double y = a * point.X + b;
			if (Math.abs(point.Y - y) < Epsilon)
			{
				pointOnLine = true; // the line is approximately on the line
			}
		}
		else
		{
			//here x1 = x2
			if(point.X == this.Start.X && 
					((point.Y >= this.Start.Y && point.Y <= this.End.Y) || (point.Y <= this.Start.Y && point.Y >= this.End.Y)))
			{
				pointOnLine = true;
			}
		}
		return pointOnLine;
	}
	
	public boolean hasSlope()
	{
		boolean hasSloop = true;
		if(this.Start.X == this.End.X)
		{
			hasSloop = false;
		}
		return hasSloop;
	}
	
	public Double getSlope()
	{
		return (End.Y - Start.Y) / (End.X - Start.X);
	}
	
	public static Double Epsilon = 0.05;
}
