package edu.purdue.cs.tornado.index.global;

import java.io.Serializable;

public class RoutingGridCell implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2664508488552532404L;
	public Integer taskIdIndex;
	public Integer xCoordinate;
	public Integer yCoordinate;
	public RoutingGridCell rightCell;
	public RoutingGridCell upperCell;
	public Integer fineGridGran;
	public Double step;

	public RoutingGridCell() {
		taskIdIndex = -1;
		rightCell = null;
		upperCell = null;
		xCoordinate = null;
		yCoordinate = null;
	}

	public RoutingGridCell(Integer x, Integer y) {
		taskIdIndex = -1;
		rightCell = null;
		upperCell = null;
		xCoordinate = x;
		yCoordinate = y;
	}
	public void resetCell(Integer x, Integer y){
		taskIdIndex = -1;
		rightCell = null;
		upperCell = null;
		xCoordinate = x;
		yCoordinate = y;
	}

}
