package edu.purdue.cs.tornado.index.local.interfaces;

import java.util.List;

import edu.purdue.cs.tornado.messages.Query;

public interface QueriesCell {
	public List<Query> getQueries();

	public Long getMinExpireTime();

	public void setMinExpireTime(Long minExpireTime);

}
