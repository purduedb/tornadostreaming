package edu.purdue.cs.tornado.index.global;

import java.io.Serializable;

public class RoutingGridCell implements Serializable {
	/**
	 * 
	 */
	public int taskIdIndex;
	public RoutingGridCell rightCell;
	public RoutingGridCell upperCell;
//	public int x;
//	public int y;


	public RoutingGridCell() {
		taskIdIndex = -1;
		rightCell = null;
		upperCell = null;
//		x=-1;
//		y=-1;
	}

	public RoutingGridCell(Integer x, Integer y) {
		taskIdIndex = -1;
		rightCell = null;
		upperCell = null;
//		this.x=x;
//		this.y=y;

	}
	public void resetCell(Integer x, Integer y){
		taskIdIndex = -1;
		rightCell = null;
		upperCell = null;
//		this.x=x;
//		this.y=y;
	}

}
