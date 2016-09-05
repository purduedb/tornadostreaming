package edu.purdue.cs.tornado.index.local.hybridpyramid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.messages.Query;

public class KeywordTrieCell {
	public HashMap<String, KeywordTrieCell> trieCells;
	public LinkedList<Query> storedQueries;
	public LinkedList<Query> verifiableQueries;

	public KeywordTrieCell() {
		storedQueries = null;// new ArrayList<Query>();// new ArrayList<Query>();
		verifiableQueries = null;//
		trieCells = null;
	}

	public void find(ArrayList<String> keywords, int start, LinkedList<Query> result, int level) {
		if (storedQueries != null)
			result.addAll(storedQueries);
		if (verifiableQueries != null)
			for (Query q : verifiableQueries) {
				if (TextHelpers.containsTextually(keywords, q.getQueryText()))
					result.add(q);
			}
		int i = start;
		if (trieCells != null)
			for (; i < keywords.size(); i++) {
				String keyword = keywords.get(i);
				KeywordTrieCell cell = trieCells.get(keyword);
				if (cell != null)
					cell.find(keywords, i + 1, result, level + 1);
			}
	}

	public void findOld(ArrayList<String> keywords, int start, ArrayList<Query> result) {
		if (storedQueries != null)
			result.addAll(storedQueries);

		int i = start;
		if (trieCells != null)
			for (; i < keywords.size(); i++) {
				if (trieCells.containsKey(keywords.get(i)))
					trieCells.get(keywords.get(i)).findOld(keywords, i + 1, result);
			}
	}

}
