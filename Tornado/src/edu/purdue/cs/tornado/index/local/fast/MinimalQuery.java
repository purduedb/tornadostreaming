package edu.purdue.cs.tornado.index.local.fast;

import java.util.ArrayList;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.messages.Query;

public class MinimalQuery {
	public  int queryId;
	public boolean added;
	public boolean deleted;
	
	public ArrayList<String> queryText;
	public Rectangle spatialRange;
	public long removeTime;
	
	public MinimalQuery (Query q) {
		this.queryId = q.getQueryId();
		this.removeTime = q.getRemoveTime();
		this.queryText = new ArrayList<String>();
		for(String s : q.getQueryText()) {
			this.queryText.add(new String(s));
		}
		this.added = false;
		this.deleted = false;
		this.spatialRange = new Rectangle(new Point(q.getSpatialRange().getMin()), new Point(q.getSpatialRange().getMax()));
	}
	
	public long getRemoveTime() {
		return removeTime;
	}
	public void setRemoveTime(long removeTime) {
		this.removeTime = removeTime;
	}
	public int getQueryId() {
		return queryId;
	}
	public void setQueryId(int queryId) {
		this.queryId = queryId;
	}

	public boolean isAdded() {
		return added;
	}
	public void setAdded(boolean added) {
		this.added = added;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public ArrayList<String> getQueryText() {
		return queryText;
	}
	public void setQueryText(ArrayList<String> queryText) {
		this.queryText = queryText;
	}
	public Rectangle getSpatialRange() {
		return spatialRange;
	}
	public void setSpatialRange(Rectangle spatialRange) {
		this.spatialRange = spatialRange;
	}
//	public TextualPredicate getTextualPredicate() {
//		return textualPredicate;
//	}
//	public void setTextualPredicate(TextualPredicate textualPredicate) {
//		this.textualPredicate = textualPredicate;
//	}
	public String toString(){
		String output = "Query[: " +  getQueryId() + " , "  + "Text: "
				+ (getQueryText() == null ? "" : getQueryText().toString()) + " Location:"+spatialRange.toString()+"]";
		return output;
	}
}
