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

public final class GoogleMapsProjection2 
{
    private final int TILE_SIZE = 10000;
    private Point _pixelOrigin;
    private double _pixelsPerLonDegree;
    private double _pixelsPerLonRadian;

    public GoogleMapsProjection2()
    {
        this._pixelOrigin = new Point(TILE_SIZE / 2.0,TILE_SIZE / 2.0);
        this._pixelsPerLonDegree = TILE_SIZE / 360.0;
        this._pixelsPerLonRadian = TILE_SIZE / (2 * Math.PI);
    }

    double bound(double val, double valMin, double valMax)
    {
        double res;
        res = Math.max(val, valMin);
        res = Math.min(val, valMax);
        return res;
    }

    double degreesToRadians(double deg) 
    {
        return deg * (Math.PI / 180);
    }

    double radiansToDegrees(double rad) 
    {
        return rad / (Math.PI / 180);
    }

    Point fromLatLngToPoint(double lat, double lng, int zoom)
    {
        Point point = new Point(0.0, 0.0);

        point.X = _pixelOrigin.X + lng * _pixelsPerLonDegree;       

        // Truncating to 0.9999 effectively limits latitude to 89.189. This is
        // about a third of a tile past the edge of the world tile.
        double siny = bound(Math.sin(degreesToRadians(lat)), -0.9999,0.9999);
        point.Y = _pixelOrigin.Y + 0.5 * Math.log((1 + siny) / (1 - siny)) *- _pixelsPerLonRadian;

        int numTiles = 1 << zoom;
        point.X = point.X * numTiles;
        point.Y = point.Y * numTiles;
        return point;
     }

    Point fromPointToLatLng(Point point, int zoom)
    {
        int numTiles = 1 << zoom;
        point.X = point.X / numTiles;
        point.Y = point.Y / numTiles;       

        double lng = (point.X - _pixelOrigin.X) / _pixelsPerLonDegree;
        double latRadians = (point.Y - _pixelOrigin.Y) / - _pixelsPerLonRadian;
        double lat = radiansToDegrees(2 * Math.atan(Math.exp(latRadians)) - Math.PI / 2);
        return new Point(lat, lng);
    }

    public static void main(String []args) 
    {
        GoogleMapsProjection2 gmap2 = new GoogleMapsProjection2();

        Point point1 = gmap2.fromLatLngToPoint(-88.0, -179, 0);
        System.out.println(point1.X+"   "+point1.Y);
        Point point2 = gmap2.fromPointToLatLng(point1,1);
        System.out.println(point2.X+"   "+point2.Y);
    }
}

