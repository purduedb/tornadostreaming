
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
//This code is adoped from AQWA Aly, Ahmed M., et al. "A demonstration of AQWA: adaptive query-workload-aware partitioning of big spatial data." Proceedings of the VLDB Endowment 8.12 (2015): 1968-1971.
package edu.purdue.cs.tornado.helper;




public class CountersGrid {
	public class neighborInfo {
		double notInBottom; double archivedNotInBottom;
		double notInLeft; double archivedNotInLeft;
		double neitherInSouthNorLeft; double archivedNeitherInSouthNorLeft;
		double allOverlapping; double archivedAllOverlapping;
		public neighborInfo(){
			 notInBottom=0.0;  archivedNotInBottom=0.0;
			 notInLeft=0.0;  archivedNotInLeft=0.0;
			 neitherInSouthNorLeft=0.0;  archivedNeitherInSouthNorLeft=0.0;
			 allOverlapping=0.0;  archivedAllOverlapping=0.0;
		}
	}

	
	public double[][] pointData;
	public neighborInfo[][] regionData; //this can be used later if we consider range queries

	
	private int numRows, numColumns;

	public CountersGrid(int numRows, int numColumns) {
	
		this.pointData = new double[numRows][numColumns];
		this.regionData = new neighborInfo[numRows][numColumns];
		for (int row = 0; row < numRows; row++)
			for (int column = 0; column < numColumns; column++){
				this.regionData[row][column] = new neighborInfo();
				this.pointData[row][column]=0;
			}

		this.numRows = numRows;
		this.numColumns = numColumns;
	}

	public void insertPoint(int row, int column, double value) {
		this.pointData[row][column]+=value;
	}

	public void insertRegion(int bottom, int top, int left, int right) {
		regionData[bottom][left].neitherInSouthNorLeft ++;

		for (int row = bottom; row < top; row++) {
			for (int column = left; column < right; column++) {
				regionData[row][column].allOverlapping++;					
			}
		}

		for (int row = bottom; row < top; row++)
			regionData[row][left].notInLeft++;

		for (int column = left; column < right; column++)
			regionData[bottom][column].notInBottom++;
	}

	public void insertNewRegion(int bottom, int top, int left, int right) {
		//insertRegion(bottom, top, left, right);
		
		for (int row = bottom; row < top; row++) {
			for (int column = left; column < right; column++) {
				regionData[row][column].allOverlapping++;					
			}
		}

		// Column-wise for notInBottom
		for (int row = bottom; row < numRows; row++) {
			for (int column = left; column < right; column++)
				this.regionData[row][column].notInBottom ++; 
		}

		// Row-wise for notInLeft
		for (int row = bottom; row < top; row++) {
			for (int column = left; column < numColumns; column++) {
				this.regionData[row][column].notInLeft ++; 
			}
		}

		//		// Not in bottom
		//		for (int column = left; column < numColumns; column++)
		//			this.regionData[bottom][column].notInBottom ++;
		//
		//		// Not in left
		//		for (int row = bottom; row < numRows; row++)
		//			regionData[row][left].notInLeft++;

		// Neither in left nor in bottom
		for (int row = bottom; row < numRows; row++) {
			for (int column = left; column < numColumns; column++) {
				regionData[row][column].neitherInSouthNorLeft++;					
			}
		}
	}

	public void preAggregatePoints() {
		for (int row = 0; row < numRows; row++) {
			for (int column = 1; column < numColumns; column++) {
				this.pointData[row][column] += this.pointData[row][column - 1]; 
			}
		}

		for (int row = 1; row < numRows; row++) {
			for (int column = 0; column < numColumns; column++) {
				this.pointData[row][column] += this.pointData[row - 1][column]; 
			}
		}
	}

	public void preAggregateRegions() {

		// Column-wise for notInBottom
		for (int row = 1; row < numRows; row++) {
			for (int column = 0; column < numColumns; column++)
				this.regionData[row][column].notInBottom += this.regionData[row - 1][column].notInBottom; 
		}

		// Row-wise for notInLeft
		for (int row = 0; row < numRows; row++) {
			for (int column = 1; column < numColumns; column++) {
				this.regionData[row][column].notInLeft += this.regionData[row][column - 1].notInLeft; 
			}
		}

		// Row&Colums-wise for neitherInSouthNorLeft
		for (int row = 0; row < numRows; row++) {
			for (int column = 1; column < numColumns; column++) {
				this.regionData[row][column].neitherInSouthNorLeft += this.regionData[row][column - 1].neitherInSouthNorLeft; 
			}
		}
		for (int row = 1; row < numRows; row++) {
			for (int column = 0; column < numColumns; column++) {
				this.regionData[row][column].neitherInSouthNorLeft += this.regionData[row - 1][column].neitherInSouthNorLeft; 
			}
		}
	}

	
	public double getNumRegions(int bottom, int top, int left, int right) {

		double sum = this.regionData[bottom][left].allOverlapping;
		sum += this.regionData[bottom][left].archivedAllOverlapping;

		if ((top -  bottom) > 1) {
			sum += (this.regionData[top][left].notInBottom - this.regionData[bottom][left].notInBottom);
			sum += (this.regionData[top][left].archivedNotInBottom - this.regionData[bottom][left].archivedNotInBottom);
		}

		if ((right -  left) > 1) {
			sum += (this.regionData[bottom][right].notInLeft - this.regionData[bottom][left].notInLeft);
			sum += (this.regionData[bottom][right].archivedNotInLeft - this.regionData[bottom][left].archivedNotInLeft);
		}

		if ((right -  left) > 1 && (top -  bottom) > 1) {
			sum += (this.regionData[top][right].neitherInSouthNorLeft + this.regionData[bottom][left].neitherInSouthNorLeft);
			sum += (this.regionData[top][right].archivedNeitherInSouthNorLeft + this.regionData[bottom][left].archivedNeitherInSouthNorLeft);
			sum -= this.regionData[bottom][right].neitherInSouthNorLeft;
			sum -= this.regionData[bottom][right].archivedNeitherInSouthNorLeft;
			sum -= this.regionData[top][left].neitherInSouthNorLeft;
			sum -= this.regionData[top][left].archivedNeitherInSouthNorLeft;
		}

		return sum;
	}
	
	public void archive() {

		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				this.regionData[i][j].archivedAllOverlapping /= 2; this.regionData[i][j].archivedAllOverlapping += regionData[i][j].allOverlapping; regionData[i][j].allOverlapping = 0;
				this.regionData[i][j].archivedNeitherInSouthNorLeft /= 2; this.regionData[i][j].archivedNeitherInSouthNorLeft += regionData[i][j].neitherInSouthNorLeft; regionData[i][j].neitherInSouthNorLeft = 0;
				this.regionData[i][j].archivedNotInBottom /= 2; this.regionData[i][j].archivedNotInBottom += regionData[i][j].notInBottom; regionData[i][j].notInBottom = 0;
				this.regionData[i][j].archivedNotInLeft /= 2; this.regionData[i][j].archivedNotInLeft += regionData[i][j].notInLeft; regionData[i][j].notInLeft = 0;
			}
		}
	}

	public double getNumPoints (int bottom, int top, int left, int right) {

//		double sum = this.pointData[top - 1][right - 1];
//
//		if (bottom != 0 && left != 0)
//			sum += this.pointData[bottom - 1][left - 1];
//
//		if (bottom != 0)
//			sum -= this.pointData[bottom - 1][right - 1];
//
//		if (left != 0)
//			sum -= this.pointData[top - 1][left - 1];
//
//		return sum;
		double pcost=0;
		for(int k=left;k<right;k++){
			for(int j=bottom;j<top;j++)
				pcost+=this.pointData[k][j];
		}
		return pcost;
	}

	
	public int getWidth() {
		return this.numColumns;
	}

	public int getHeight() {
		return this.numRows;
	}
}
