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

import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.purdue.cs.tornado.loadbalance.Partition;
import edu.purdue.cs.tornado.messages.Query;

public class SpatialHelper {
	private final static double QUARTERPI = Math.PI / 4.0;

	//	public static Boolean overlapsSpatially(Point point, Rectangle rectangle) {
	//		if (		(point.getX() >= rectangle.getMin().getX()||Math.abs(point.getX() - rectangle.getMin().getX())<.000001)
	//				&& (point.getX() <= rectangle.getMax().getX()||Math.abs(point.getX() - rectangle.getMax().getX())<.000001 )
	//				&& (point.getY() >= rectangle.getMin().getY()|| Math.abs(point.getY() -rectangle.getMin().getY())<.000001 )
	//				&& (point.getY() <= rectangle.getMax().getY()|| Math.abs(point.getY() - rectangle.getMax().getY())<.000001)
	//				)
	//			return true;
	//		return false;
	//	}
	public static Boolean overlapsSpatially(Point point, Rectangle rectangle) {
		if (Double.compare(point.getX(), rectangle.getMin().getX()) < 0 
				|| Double.compare(point.getX(), rectangle.getMax().getX()) > 0 
				|| Double.compare(point.getY(), rectangle.getMin().getY()) < 0
				|| Double.compare(point.getY(), rectangle.getMax().getY()) > 0)
			return false;
		return true;
	}

	public static Boolean overlapsSpatially(Rectangle rectangle1, Rectangle rectangle2) {
		if ((rectangle1.getMin().getX() <= rectangle2.getMax().getX() || Math.abs(rectangle1.getMin().getX() - rectangle2.getMax().getX()) < .000001)
				&& (rectangle1.getMax().getX() >= rectangle2.getMin().getX() || Math.abs(rectangle1.getMax().getX() - rectangle2.getMin().getX()) < .000001)
				&& (rectangle1.getMin().getY() <= rectangle2.getMax().getY() || Math.abs(rectangle1.getMin().getY() - rectangle2.getMax().getY()) < .000001)
				&& (rectangle1.getMax().getY() >= rectangle2.getMin().getY() || Math.abs(rectangle1.getMax().getY() - rectangle2.getMin().getY()) < .000001))
			return true;
		return false;
	}

	public static Double getDistanceInBetween(Point p1, Point p2) {
		Double dist = 0.0;
		dist = Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
		return dist;

	}

	public static Double getMaxDistanceBetween(Point point, Rectangle recangle) {
		Point p1 = recangle.getMin();
		Point p2 = recangle.getMax();
		Point p3 = new Point(p1.getX(), p2.getY());
		Point p4 = new Point(p1.getY(), p2.getX());
		Double dist1 = getDistanceInBetween(point, p1);
		Double dist2 = getDistanceInBetween(point, p2);
		Double dist3 = getDistanceInBetween(point, p3);
		Double dist4 = getDistanceInBetween(point, p4);
		return Math.max(Math.max(dist1, dist2), Math.max(dist3, dist4));
	}

	public static Double getMinDistanceBetween(Point point, Rectangle recangle) {
		Double dx = Math.max(0.0, Math.max(recangle.getMin().getX() - point.getX(), point.getX() - recangle.getMax().getX()));
		Double dy = Math.max(0.0, Math.max(recangle.getMin().getY() - point.getY(), point.getY() - recangle.getMax().getY()));
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Basic LInear Conversion
	 * 
	 * @param lonlat
	 * @return
	 */
	//	public static Point convertFromLatLonToXYPoint(LatLong lonlat){
	//		
	//		
	//
	//         Point xy = new Point();
	//         xy.setX( (lonlat.getLatitude()+90)/180 *SpatioTextualConstants.xMaxRange);
	//         xy.setY((lonlat.getLongitude()+180)/360*SpatioTextualConstants.yMaxRange);
	//		
	//         return xy;
	//	}
	public static Point convertFromLatLonToXYPoint(LatLong lonlat) {

		Point xy = new Point();
		xy.setY((lonlat.getLatitude() - SpatioTextualConstants.minLat) / (SpatioTextualConstants.maxLat - SpatioTextualConstants.minLat) * SpatioTextualConstants.yMaxRange);
		xy.setX((lonlat.getLongitude() - SpatioTextualConstants.minLong) / (SpatioTextualConstants.maxLong - SpatioTextualConstants.minLong) * SpatioTextualConstants.xMaxRange);

		return xy;
	}

	/**
	 * Basic Linear Conversion
	 * 
	 * @param lonlat
	 * @return
	 */
	//	public static LatLong convertFromXYToLatLonTo(Point xy){
	//		
	//		 LatLong latlong = new LatLong();
	//		 latlong.setLatitude((xy.getX()/SpatioTextualConstants.xMaxRange*180)-90);
	//		 latlong.setLongitude((xy.getY()/SpatioTextualConstants.yMaxRange*360)-180);
	//		
	//         return latlong;
	//	}
	public static LatLong convertFromXYToLatLonTo(Point xy) {

		LatLong latlong = new LatLong();
		latlong.setLatitude((xy.getY() / SpatioTextualConstants.yMaxRange * (SpatioTextualConstants.maxLat - SpatioTextualConstants.minLat)) + SpatioTextualConstants.minLat);
		latlong.setLongitude((xy.getX() / SpatioTextualConstants.xMaxRange * (SpatioTextualConstants.maxLong - SpatioTextualConstants.minLong)) + SpatioTextualConstants.minLong);

		return latlong;
	}

//	public static LatLong convertFromXYToLatLonTo(Point xy, Double latMin, Double lonMin, Double latMax, Double lonMax) {
//
//		LatLong latlong = new LatLong();
//		latlong.setLatitude((xy.getX() / SpatioTextualConstants.xMaxRange * (latMax - latMin)) + latMin);
//		latlong.setLongitude((xy.getY() / SpatioTextualConstants.yMaxRange * (lonMax - lonMin)) + lonMin);
//
//		return latlong;
//	}

	/**
	 * This function checks if a KNN query is fully satisfied internal to the
	 * current evaluator bolt
	 * 
	 * @param query
	 * @return
	 */
	public static Boolean checkKNNQueryDoneWithinLocalBounds(Query query, Rectangle bounds) {
		if (query.getKNNlistSize() == query.getK()) {
			//we got the K items 
			Double farthestDistance = query.getFarthestDistance();
			Point focal = query.getFocalPoint();
			Point minBounds = new Point(Math.max(focal.getX() - farthestDistance, 0), Math.max(focal.getY() - farthestDistance, 0));
			Point maxBounds = new Point(Math.min(focal.getX() + farthestDistance, SpatioTextualConstants.xMaxRange), Math.min(focal.getY() + farthestDistance, SpatioTextualConstants.xMaxRange));
			Rectangle KNNBounds = new Rectangle(minBounds, maxBounds);
			if (insideSpatially(bounds, KNNBounds))
				return true;
		}

		return false;
	}

	public static Boolean insideSpatially(Rectangle bigRectangle, Rectangle smallRectangle) {
		if ((smallRectangle.getMin().getX() >= bigRectangle.getMin().getX() || Double.compare(smallRectangle.getMin().getX(), bigRectangle.getMax().getX()) == 0)
				&& (smallRectangle.getMax().getX() <= bigRectangle.getMax().getX() || Double.compare(smallRectangle.getMax().getX(), bigRectangle.getMin().getX()) == 0)
				&& (smallRectangle.getMin().getY() >= bigRectangle.getMin().getY() || Double.compare(smallRectangle.getMin().getY(), bigRectangle.getMax().getY()) == 0)
				&& (smallRectangle.getMax().getY() <= bigRectangle.getMax().getY() || Double.compare(smallRectangle.getMax().getY(), bigRectangle.getMin().getY()) == 0))
			return true;
		return false;
	}

	/**
	 * Splits a partition into two partitions.
	 * 
	 * @author Anas Dagistani
	 * @param parent
	 *            The partition to be split.
	 * @param splitPosition
	 *            The position at which the split is to be done.
	 * @param isHorizontal
	 *            Whether the split is horizontal or vertical.
	 * @param costEstimator
	 *            This parameter is used when estimating the size and cost of
	 *            the resulting partitions.
	 * @return
	 */
	public static Partition[] split(Partition parent, int splitPosition, boolean isHorizontal) {

		Partition[] splits = new Partition[2];

		// Cell A will be split[0]
		int aBottom, aTop, aLeft, aRight;
		aBottom = parent.getBottom();
		aLeft = parent.getLeft();

		if (isHorizontal) {
			aTop = splitPosition;
			aRight = parent.getRight();
		} else {
			aTop = parent.getTop();
			aRight = splitPosition;
		}
		splits[0] = new Partition(aBottom, aTop, aLeft, aRight);
		//splits[0].setSizeInBytes(costEstimator.getSize(splits[0]));
		//splits[0].setCost(splits[0].getSizeInBytes() * costEstimator.getNumOverlappingQueries(splits[0]));

		// Cell B will be split[1]
		int BBottom, BTop, BLeft, BRight;
		BTop = parent.getTop();
		BRight = parent.getRight();

		if (isHorizontal) {
			BBottom = splitPosition;
			BLeft = parent.getLeft();
		} else {
			BLeft = splitPosition;
			BBottom = parent.getBottom();
		}
		splits[1] = new Partition(BBottom, BTop, BLeft, BRight);
		//splits[1].setSizeInBytes(costEstimator.getSize(splits[1]));
		//splits[1].setCost(splits[1].getSizeInBytes() * costEstimator.getNumOverlappingQueries(splits[1]));

		return splits;
	}

	/**
	 * Splits a partition into two partitions.
	 * 
	 * @param parent
	 *            The partition to be split.
	 * @param splitPosition
	 *            The position at which the split is to be done.
	 * @param isHorizontal
	 *            Whether the split is horizontal or vertical.
	 * @param costEstimator
	 *            This parameter is used when estimating the size and cost of
	 *            the resulting partitions.
	 * @return
	 */
	public static Partition[] split(Partition parent, int splitPosition, boolean isHorizontal, CostEstimator costEstimator) {

		Partition[] splits = new Partition[2];

		// Cell A will be split[0]
		int aBottom, aTop, aLeft, aRight;
		aBottom = parent.getBottom();
		aLeft = parent.getLeft();

		if (isHorizontal) {
			aTop = splitPosition;
			aRight = parent.getRight();
		} else {
			aTop = parent.getTop();
			aRight = splitPosition;
		}
		splits[0] = new Partition(aBottom, aTop, aLeft, aRight);
		splits[0].setCost(costEstimator.getNumberPoints(splits[0]));

		// Cell B will be split[1]
		int BBottom, BTop, BLeft, BRight;
		BTop = parent.getTop();
		BRight = parent.getRight();

		if (isHorizontal) {
			BBottom = splitPosition;
			BLeft = parent.getLeft();
		} else {
			BLeft = splitPosition;
			BBottom = parent.getBottom();
		}
		splits[1] = new Partition(BBottom, BTop, BLeft, BRight);
		splits[1].setCost(costEstimator.getNumberPoints(splits[1]));

		return splits;
	}

	public static Point mapDataPointToIndexCellCoordinates(Point point, int xCellCount, int yCellCount, double xMaxRange, double yMaxRange) {

		Double xStep = xMaxRange / xCellCount;
		Double yStep = yMaxRange / yCellCount;
		Integer xCell = (int) (point.getX() / xStep);
		Integer yCell = (int) (point.getY() / yStep);
		if (xCell >= xCellCount)
			xCell = (int) ((xCellCount) - 1);
		if (yCell >= yCellCount)
			yCell = (int) ((yCellCount) - 1);
		if (xCell < 0)
			xCell = 0;
		if (yCell < 0)
			yCell = 0;
		return new Point((double) (xCell), (double) (yCell));
	}

}
