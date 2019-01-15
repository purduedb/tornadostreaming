package edu.purdue.cs.tornado.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import edu.purdue.cs.tornado.index.local.fast.LocalFASTIndex;
import edu.purdue.cs.tornado.messages.MinimalRangeQuery;
import edu.purdue.cs.tornado.messages.Query;



public class KeywordTrieCell {
	public HashMap<String, Object> trieCells;
	public ArrayList<Query> queries;
	public ArrayList<Query> finalQueries;
	//public boolean extended ;

	public KeywordTrieCell() {
		queries = null;//
		finalQueries = null;
		trieCells = null;
		//	extended = false;
	}

	public void find(ArrayList<String> keywords, int start, List<Query> result, int level, Point location) {
		LocalFASTIndex.objectSearchTrieNodeCounter++;
		if (finalQueries != null)
			for (Query q : finalQueries) {
				LocalFASTIndex.objectSearchTrieFinalNodeCounter++; //added MRQ->Q and spatialRange->getSpatialRange
				if (SpatialHelper.overlapsSpatially(location, q.getSpatialRange())) {
					result.add(q);
				}
			}
		if (queries != null)
			for (Query q : queries) {
				LocalFASTIndex.objectSearchTrieNodeCounter++;
				if (SpatialHelper.overlapsSpatially(location, q.getSpatialRange())) {
					result.add(q);
				}
			}
		int i = start;
		HashMap<String, Object> currentCells = trieCells;
		if (currentCells != null)
			for (; i < keywords.size(); i++) {
				String keyword = keywords.get(i);
				Object cell = currentCells.get(keyword);

				if (cell == null)
					continue;
				LocalFASTIndex.objectSearchTrieHashAccess++;
				if (cell instanceof Query) {
					LocalFASTIndex.objectSearchTrieNodeCounter++;
					if (SpatialHelper.overlapsSpatially(location, ((Query) cell).getSpatialRange()) && TextHelpers.containsTextually(keywords, ((Query) cell).getQueryText()))
						result.add(((Query) cell));

				} else if (cell instanceof ArrayList) {
					for (Query q : ((ArrayList<Query>) cell)) {
						LocalFASTIndex.objectSearchTrieNodeCounter++;
						if (SpatialHelper.overlapsSpatially(location, q.getSpatialRange()) && TextHelpers.containsTextually(keywords, q.getQueryText()))
							result.add(q);
					}

				} else if (cell instanceof KeywordTrieCell) {
					((KeywordTrieCell) cell).find(keywords, i + 1, result, level + 1, location);
				}

			}
	}

	public void findTextualOnly(ArrayList<String> keywords, int start, List<Query> result, int level) {

		if (queries != null)
			result.addAll(queries);

		int i = start;
		HashMap<String, Object> currentCells = trieCells;
		if (currentCells != null)
			for (; i < keywords.size(); i++) {
				String keyword = keywords.get(i);
				Object cell = currentCells.get(keyword);

				if (cell == null)
					continue;
				if (cell instanceof Query) {
					if (TextHelpers.containsTextually(keywords, ((Query) cell).getQueryText(), i, level))
						result.add(((Query) cell));
					

				} else if (cell instanceof ArrayList) {
					for (Query q : ((ArrayList<Query>) cell)) {
						if (TextHelpers.containsTextually(keywords, q.getQueryText(), i, level))
							result.add(q);
						
					}

				} else if (cell instanceof KeywordTrieCell) {
					((KeywordTrieCell) cell).findTextualOnly(keywords, i + 1, result, level + 1);
				}

			}

	}

	public int clean(ArrayList<Query> combinedQueries) {
		int operations = 0;
		if (queries != null) {
			Iterator<Query> queriesItr = queries.iterator();
			while (queriesItr.hasNext()) {
				Query query = queriesItr.next();
				if (query.expireTime < LocalFASTIndex.queryTimeStampCounter)
					queriesItr.remove();
				else {
					combinedQueries.add(query);
				}
				operations++;
			}
			if (queries.size() == 0)
				queries = null;
		}
		if (trieCells != null) {
			Iterator<Entry<String, Object>> trieCellsItr = trieCells.entrySet().iterator();
			while (trieCellsItr.hasNext()) {
				Entry<String, Object> trieCellEntry = trieCellsItr.next();
				Object cell = trieCellEntry.getValue();
				if (cell instanceof Query) {
					if (((Query) cell).expireTime < LocalFASTIndex.queryTimeStampCounter)
						trieCellsItr.remove();
					else {
						combinedQueries.add((Query) cell);
					}
					operations++;
				} else if (cell instanceof ArrayList) {
					Iterator<Query> queriesInternalItr = ((ArrayList<Query>) cell).iterator();
					while (queriesInternalItr.hasNext()) {
						Query query = queriesInternalItr.next();
						if (query.expireTime < LocalFASTIndex.queryTimeStampCounter)
							queriesInternalItr.remove();
						else {
							combinedQueries.add(query);
						}
						operations++;
					}
					if (((ArrayList<Query>) cell).size() == 0)
						trieCellsItr.remove();
				} else if (cell instanceof KeywordTrieCell) {
					operations += ((KeywordTrieCell) cell).clean(combinedQueries);
					if (((KeywordTrieCell) cell).queries == null && ((KeywordTrieCell) cell).trieCells == null)
						trieCellsItr.remove();
				}
			}
			if (trieCells.size() == 0)
				trieCells = null;
		}

		return operations;
	}

}
