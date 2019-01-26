package edu.purdue.cs.tornado.test;

import java.util.ArrayList;
import java.util.HashMap;

import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.messages.Query;

public class BEIndex {
	public class ORNODE {
		boolean visited;
		int numOfChildren;
		ArrayList<Query> queriesList;

		public ORNODE(int numOfChildren) {
			visited = false;
			this.numOfChildren = numOfChildren;
			queriesList = new ArrayList<Query>();
		}

		ArrayList<ORNODE> parentORNodes;
		ArrayList<ANDNODE> parentANDNodes;
	}

	public class ANDNODE {
		int childrenVisited;
		int numOfChildren;
		ArrayList<Query> queriesList;

		public ANDNODE(int numOfChildren) {
			childrenVisited = 0;
			this.numOfChildren = numOfChildren;
			queriesList = new ArrayList<Query>();
		}

		ArrayList<ANDNODE> parentANDNodes;
	}

	HashMap<String, ArrayList<ORNODE>> indexOR;
	HashMap<String, ArrayList<ANDNODE>> indexAND;

	public void addQuery(Query q) {
		if (TextualPredicate.OVERlAPS.equals(q.getTextualPredicate())) {

		} else if (TextualPredicate.CONTAINS.equals(q.getTextualPredicate())) {

		}

	}
}
