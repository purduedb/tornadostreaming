//   Point.java
//   Java Spatial Index Library
//   Copyright (C) 2002-2005 Infomatiq Limited.
//  
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//  
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//  
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA

package edu.purdue.cs.tornado.atlas.SGB.index;

/**
 * Currently hardcoded to 2 dimensions, but could be extended.
 */
public class Point {
  /**
   * The (x, y) coordinates of the point.
   */
  public float x, y;
  
  public float []coordinates;
  /**
   * Constructor.
   * 
   * @param x The x coordinate of the point
   * @param y The y coordinate of the point
   */
  public Point(float x, float y) {
    this.x = x; 
    this.y = y;
  }
  

  /**
   * more general case for the point from one dimension to higher dimension
   * @param coordinates
   */
  public Point(float ...coordinates) {
   
   this.coordinates=new float[coordinates.length];
   
   int i=0;
   for(float f:coordinates)
   {
	   this.coordinates[i]=f;
   }
   
  }
  
  
  /**
   * Copy from another point into this one
   */
  public void set(Point other) {
    x = other.x;
    y = other.y;
    
    this.coordinates=other.coordinates.clone();
    
  }
 
  
  
  /**
   * Print as a string in format "(x, y)"
   */
  public String toString() {
    //return "(" + x + ", " + y + ")";
    
	  StringBuffer tmp=new StringBuffer();
	  tmp.append("( ");
	 for(float f:this.coordinates)
	 {
		 tmp.append(f+" , ");
	 }
	 tmp.append(") ");
	 
	 return tmp.toString();
  }
  
  /**
   * @return X coordinate rounded to an int
   */
  public int xInt() {
    return (int) Math.round(x);
  }
  
  /**
   * @return Y coordinate rounded to an int
   */
  public int yInt() {
    return (int) Math.round(y);
  }
}
