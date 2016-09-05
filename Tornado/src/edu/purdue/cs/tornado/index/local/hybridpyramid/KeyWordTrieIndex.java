package edu.purdue.cs.tornado.index.local.hybridpyramid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import edu.purdue.cs.tornado.messages.Query;

public class KeyWordTrieIndex {
	public KeywordTrieCell root;
	public static int SPLIT_THRESHOLD = 5;
	public static int SPLIT_THRESHOLD_FREQ = 5;
	int size = 0;

	public KeyWordTrieIndex() {
		root = new KeywordTrieCell();
		root.trieCells = new HashMap<String, KeywordTrieCell>();
		index = new HashMap<String, HashMap<String, ArrayList<Query>>>();
	}

	HashMap<String, HashMap<String, ArrayList<Query>>> index;

	public void insert(ArrayList<String> keywordList, Query q) {
		KeywordTrieCell currentCell = root;
		size++;
		int i = 0;
		for (String keyword : keywordList) {

			if (currentCell.trieCells == null)
				currentCell.trieCells = new HashMap<String, KeywordTrieCell>();
			KeywordTrieCell cell = currentCell.trieCells.get(keyword);
			if (cell == null) {
				cell = new KeywordTrieCell();
				currentCell.trieCells.put(keyword, cell);

			}
			currentCell = cell;
			if (currentCell.trieCells == null && i<keywordList.size()-1&&(currentCell.verifiableQueries == null || currentCell.verifiableQueries.size() <= SPLIT_THRESHOLD)) {
				if (currentCell.verifiableQueries == null)
					currentCell.verifiableQueries = new LinkedList<Query>();
				currentCell.verifiableQueries.add(q);
				return;
			} else if (currentCell.trieCells == null &&currentCell.verifiableQueries!=null&&currentCell.verifiableQueries.size() > SPLIT_THRESHOLD) {
				currentCell.trieCells = new HashMap<String, KeywordTrieCell>();
				//expand one more keyword
				for (Query otherQuery : currentCell.verifiableQueries) {
					KeywordTrieCell otherCell = currentCell.trieCells.get(otherQuery.getQueryText().get(i + 1));
					if (otherCell == null) {
						otherCell = new KeywordTrieCell();
						currentCell.trieCells.put(otherQuery.getQueryText().get(i + 1), otherCell);
					}
					if (i + 1 == otherQuery.getQueryText().size() - 1) {//last keyword
						if (otherCell.storedQueries == null)
							otherCell.storedQueries = new LinkedList<Query>();
						otherCell.storedQueries.add(otherQuery);
					} else {
						if (otherCell.verifiableQueries == null)
							otherCell.verifiableQueries = new LinkedList<Query>();
						otherCell.verifiableQueries.add(otherQuery);
					}
					currentCell.trieCells.put(keyword, otherCell);
				}
				currentCell.verifiableQueries = null;
			}
			i++;
			//				
		}
		if (currentCell.storedQueries == null)
			currentCell.storedQueries = new LinkedList<Query>();
		currentCell.storedQueries.add(q);

	}

	public void insertTwoLevelsOnly(ArrayList<String> keywordList, Query q) {
		String k1 = keywordList.get(0);
		String k2 = keywordList.get(1);
		HashMap<String, ArrayList<Query>> innerMap = index.get(k1);
		if (innerMap == null) {
			innerMap = new HashMap<String, ArrayList<Query>>();
			index.put(k1, innerMap);
		}
		ArrayList<Query> queries = innerMap.get(k2);
		if (queries == null) {
			queries = new ArrayList<Query>();
			innerMap.put(k2, queries);
		}
		queries.add(q);
	}

	public void insertOld1(ArrayList<String> keywordList, Query q) {
		KeywordTrieCell currentCell = root;

		for (String keyword : keywordList) {

			if (currentCell.trieCells == null)
				currentCell.trieCells = new HashMap<String, KeywordTrieCell>();
			KeywordTrieCell cell = currentCell.trieCells.get(keyword);
			if (cell == null) {
				cell = new KeywordTrieCell();
				currentCell.trieCells.put(keyword, cell);

			}
			currentCell = cell;
		}
		if (currentCell.storedQueries == null)
			currentCell.storedQueries = new LinkedList<Query>();
		currentCell.storedQueries.add(q);
		size++;
	}

	public void insertOld2(String keyword, Query q) {
		KeywordTrieCell currentCell = root;
		int level = 0;
		size++;
		//for (String keyword : keywordList) {
		if (currentCell.trieCells == null) {
			currentCell.storedQueries.add(q);
			if (currentCell.storedQueries.size() > SPLIT_THRESHOLD) {
				currentCell.trieCells = new HashMap<String, KeywordTrieCell>();
				LinkedList<Query> remainingQueries = new LinkedList<Query>();
				for (Query query : currentCell.storedQueries) {
					if (query.getQueryText().size() > level) {
						if (!currentCell.trieCells.containsKey(query.getQueryText().get(level))) {

							currentCell.trieCells.put(query.getQueryText().get(level), new KeywordTrieCell());
						}
						currentCell.trieCells.get(query.getQueryText().get(level)).storedQueries.add(query);
					} else {
						remainingQueries.add(query);
					}
				}
				currentCell.storedQueries = remainingQueries;

			}
			return;
		} else {
			if (currentCell.trieCells.containsKey(keyword)) {

				currentCell = currentCell.trieCells.get(keyword);
				level++;
			} else {
				KeywordTrieCell cell = new KeywordTrieCell();
				cell.storedQueries.add(q);
				currentCell.trieCells.put(keyword, cell);
				return;
			}
		}
		//	}

	}

	public ArrayList<Query> findOLd(ArrayList<String> keywordList) {
		ArrayList<Query> candidate = new ArrayList<Query>();
		for (int i = 0; i < keywordList.size(); i++) {
			if (index.containsKey(keywordList.get(i))) {
				HashMap<String, ArrayList<Query>> innerMap = index.get(keywordList.get(i));
				for (int j = i + 1; j < keywordList.size(); j++) {
					if (innerMap.containsKey(keywordList.get(j))) {
						candidate.addAll(innerMap.get(keywordList.get(j)));
					}
				}
			}
		}
		return candidate;
	}

	public LinkedList<Query> find(ArrayList<String> keywordList) {
		LinkedList<Query> result = new LinkedList<Query>();
		root.find(keywordList, 0, result, 0);
		return result;
	}

	public int size() {
		return size;
	}
}
