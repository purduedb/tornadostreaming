package edu.purdue.cs.tornado.index.local.hybridpyramid;

import edu.purdue.cs.tornado.helper.IndexCellCoordinates;

public class PyramidIndexCellCoordinate extends IndexCellCoordinates {
	public int level;
	public PyramidIndexCellCoordinate(int x, int y) {
		super(x, y);
		level =0;
	}
	public PyramidIndexCellCoordinate(int x, int y,int level) {
		super(x, y);
		this.level = level;
	}

	
}
