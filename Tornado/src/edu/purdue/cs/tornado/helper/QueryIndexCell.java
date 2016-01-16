package edu.purdue.cs.tornado.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class QueryIndexCell {
	public enum IndexCellType {
		Grid, Multi_Level_Grid, Pyramid
	}

	public ArrayList<Query> storedQueries;
	public ArrayList<Query> outerEvaluatorQueries;

	public Rectangle bounds;
	public Integer queryCount;

	//this is for testing spatial objects only
	public Boolean spatialOnlyFlag;
	IndexCellType indexCellType;

	//used by multilevel index
	public Rectangle boundsNoBoundaries;
	public Integer level;

	//used by pyramid index
	public IndexCell[] children;
	public IndexCell parent;
	public Integer numberOfChildren;
	public Integer coverQueryCount;

	public QueryIndexCell(Rectangle bounds, Boolean spatialOnlyFlag, Integer level) {
		
		storedQueries = null;
		outerEvaluatorQueries = null;

		this.bounds = bounds;
		this.queryCount = 0;

		this.boundsNoBoundaries = new Rectangle(new Point(bounds.getMin().getX() + .0001, bounds.getMin().getY() + .0001), new Point(bounds.getMax().getX() - .0001, bounds.getMax().getY() - .0001));
		this.spatialOnlyFlag = spatialOnlyFlag;

		this.level = level;
		//used by multilevel index
		children = null;
		parent = null;
		numberOfChildren = 0;
		this.coverQueryCount = 0;

	}

	
	public void addQuery(Query query) {
		if (storedQueries == null)
			storedQueries = new ArrayList<Query>();
		if (!storedQueries.contains(query)) {
			queryCount++;
			storedQueries.add(query);
		}
	}

	public boolean cellOverlapsSpatiall(Rectangle rectangle) {
		return SpatialHelper.overlapsSpatially(bounds, rectangle);
	}

	public boolean cellQueriesOverlapsTextually(ArrayList<String> textList) {
		return true;
	}

	
	public void dropQuery(Query query) {
		if (storedQueries == null)
			return;
		int i = 0;
		boolean found = false;
		for (i = 0; i < storedQueries.size(); i++) {
			if (query.getSrcId().equals(storedQueries.get(i).getSrcId()) && query.getQueryId().equals(storedQueries.get(i).getQueryId())) {
				found = true;
				break;
			}
		}
		if (found) {
			storedQueries.remove(i);
			queryCount--;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof IndexCell))
			return false;
		IndexCell c = (IndexCell) o;
		return (c.getBounds().equals(this.getBounds()));
	}

	



	public Rectangle getBounds() {
		return bounds;
	}

	
	public Integer getLevel() {
		return level;
	}

	public ArrayList<Query> getOuterEvaluatorQueries() {
		return outerEvaluatorQueries;
	}

	public ArrayList<Query> getQueries() {
		return storedQueries;
	}

	public Boolean getSpatialOnlyFlag() {
		return spatialOnlyFlag;
	}

	

	public ArrayList<Query> getStoredQueries() {
		return storedQueries;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}


	public void setLevel(Integer level) {
		this.level = level;
	}

	public void setOuterEvaluatorQueries(ArrayList<Query> outerEvaluatorQueries) {
		this.outerEvaluatorQueries = outerEvaluatorQueries;
	}

	public void setSpatialOnlyFlag(Boolean spatialOnlyFlag) {
		this.spatialOnlyFlag = spatialOnlyFlag;
	}

	public void setStoredQueries(ArrayList<Query> storedQueries) {
		this.storedQueries = storedQueries;
	}

}
