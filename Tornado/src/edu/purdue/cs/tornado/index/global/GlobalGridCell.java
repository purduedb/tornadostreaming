package edu.purdue.cs.tornado.index.global;

import java.io.Serializable;

public class GlobalGridCell implements Serializable {
	/**
	 * 
	 */
	public int taskIdIndex;
//	public int x;
//	public int y;


	public GlobalGridCell() {
		taskIdIndex = -1;

//		x=-1;
//		y=-1;
	}

	public GlobalGridCell(Integer x, Integer y) {
		taskIdIndex = -1;
//		this.x=x;
//		this.y=y;

	}
	public void resetCell(Integer x, Integer y){
		taskIdIndex = -1;
//		this.x=x;
//		this.y=y;
	}

}
