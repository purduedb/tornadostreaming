package edu.purdue.cs.tornado.index.local.fast;

import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.messages.Query;

public class ReinsertEntry {
	public Rectangle range;
	public Query query;
	public ReinsertEntry(Rectangle range,Query query) {
		super();
		this.range = range;
		this.query = query;
	}
	

}
