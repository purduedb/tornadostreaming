package edu.purdue.cs.tornado.helper;

import java.util.ArrayList;

import edu.purdue.cs.tornado.messages.MinimalRangeQuery;

public class CenterRightUpperCells {
	public ArrayList<MinimalRangeQuery> center[] ;
	public CenterRightUpperCells(){
		center = new ArrayList [3];
		for(int i=0;i<3;i++){
			center[i]=null;
		}
	}

}
