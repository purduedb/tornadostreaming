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

import edu.purdue.cs.tornado.messages.Query;

public class SpatialHelper {
	private final static double QUARTERPI = Math.PI / 4.0;
	public static Boolean overlapsSpatially(Point point, Rectangle rectangle) {
		if (		(point.getX() >= rectangle.getMin().getX()||Math.abs(point.getX() - rectangle.getMin().getX())<.000001)
				&& (point.getX() <= rectangle.getMax().getX()||Math.abs(point.getX() - rectangle.getMax().getX())<.000001 )
				&& (point.getY() >= rectangle.getMin().getY()|| Math.abs(point.getY() -rectangle.getMin().getY())<.000001 )
				&& (point.getY() <= rectangle.getMax().getY()|| Math.abs(point.getY() - rectangle.getMax().getY())<.000001)
				)
			return true;
		return false;
	}
	public static Boolean overlapsSpatially(Rectangle rectangle1, Rectangle rectangle2) {
		if (		(rectangle1.getMin().getX() <= rectangle2.getMax().getX()||Math.abs(rectangle1.getMin().getX() - rectangle2.getMax().getX())<.000001)
				&& (rectangle1.getMax().getX() >= rectangle2.getMin().getX()||Math.abs(rectangle1.getMax().getX() -  rectangle2.getMin().getX())<.000001)
				&& (rectangle1.getMin().getY() <= rectangle2.getMax().getY()||Math.abs(rectangle1.getMin().getY() - rectangle2.getMax().getY())<.000001)
				&& (rectangle1.getMax().getY() >= rectangle2.getMin().getY()||Math.abs(rectangle1.getMax().getY() - rectangle2.getMin().getY())<.000001)	
			)
			return true;
			return false;
	}
	public static Double getDistanceInBetween(Point p1,Point p2){
		Double dist = 0.0;
		dist = Math.sqrt(Math.pow(p1.getX()-p2.getX(), 2)+Math.pow(p1.getY()-p2.getY(), 2));
		return dist;
		
	}
	public static Double getMaxDistanceBetween(Point point,Rectangle recangle){
		Point p1=recangle.getMin();
		Point p2=recangle.getMax();
		Point p3 = new Point(p1.getX(), p2.getY());
		Point p4 = new Point(p1.getY(), p2.getX());
		Double dist1= getDistanceInBetween(point, p1);
		Double dist2= getDistanceInBetween(point, p2);
		Double dist3= getDistanceInBetween(point, p3);
		Double dist4= getDistanceInBetween(point, p4);
		return Math.max(Math.max(dist1, dist2),Math.max(dist3, dist4));
	}
	public static Double getMinDistanceBetween(Point point,Rectangle recangle){
		 Double dx = Math.max(0.0, Math.max(recangle.getMin().getX() - point.getX(),  point.getX() - recangle.getMax().getX()));
		 Double dy =  Math.max(0.0, Math.max(recangle.getMin().getY() - point.getY(),  point.getY() - recangle.getMax().getY()));
		 return Math.sqrt(dx*dx + dy*dy);
	}
	/**
	 * Basic LInear Conversion
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
	public static Point convertFromLatLonToXYPoint(LatLong lonlat){
		
		

        Point xy = new Point();
        xy.setX( (lonlat.getLatitude()-SpatioTextualConstants.minLat)/(SpatioTextualConstants.maxLat-SpatioTextualConstants.minLat) *SpatioTextualConstants.xMaxRange);
        xy.setY((lonlat.getLongitude()-SpatioTextualConstants.minLong)/(SpatioTextualConstants.maxLong-SpatioTextualConstants.minLong) *SpatioTextualConstants.yMaxRange);
		
        return xy;
	}
	/**
	 * Basic Linear Conversion
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
	public static LatLong convertFromXYToLatLonTo(Point xy){
		
		 LatLong latlong = new LatLong();
		 latlong.setLatitude((xy.getX()/SpatioTextualConstants.xMaxRange*(SpatioTextualConstants.maxLat-SpatioTextualConstants.minLat))+SpatioTextualConstants.minLat);
		 latlong.setLongitude((xy.getY()/SpatioTextualConstants.yMaxRange*(SpatioTextualConstants.maxLong-SpatioTextualConstants.minLong))+SpatioTextualConstants.minLong);
		
        return latlong;
	}
	/**
	 * This function checks if a KNN query is fully satisfied internal to the current evaluator bolt
	 * @param query
	 * @return
	 */
	public static Boolean checkKNNQueryDoneWithinLocalBounds(Query query, Rectangle bounds){
		if(query.getKNNlistSize()==query.getK()){
			//we got the K items 
			Double farthestDistance = query.getFarthestDistance();
			Point focal = query.getFocalPoint();
			Point minBounds = new Point (Math.max(focal.getX()-farthestDistance,0),Math.max( focal.getY()-farthestDistance,0));
			Point maxBounds = new Point (Math.min(focal.getX()+farthestDistance,SpatioTextualConstants.xMaxRange), Math.min(focal.getY()+farthestDistance,SpatioTextualConstants.xMaxRange));
			Rectangle KNNBounds = new Rectangle(minBounds, maxBounds);
			if (insideSpatially(bounds,KNNBounds))
				return true;
		}
			
		return false;
	}
	public static Boolean insideSpatially(Rectangle bigRectangle, Rectangle smallRectangle) {
		if (	   (smallRectangle.getMin().getX() >= bigRectangle.getMin().getX()||Double.compare(smallRectangle.getMin().getX() , bigRectangle.getMax().getX())==0)
				&& (smallRectangle.getMax().getX() <= bigRectangle.getMax().getX()||Double.compare(smallRectangle.getMax().getX() , bigRectangle.getMin().getX())==0)
				&& (smallRectangle.getMin().getY() >= bigRectangle.getMin().getY()||Double.compare(smallRectangle.getMin().getY() , bigRectangle.getMax().getY())==0)
				&& (smallRectangle.getMax().getY() <= bigRectangle.getMax().getY()||Double.compare(smallRectangle.getMax().getY() , bigRectangle.getMin().getY())==0)	
			)
			return true;
			return false;
	}
}
