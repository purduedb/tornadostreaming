package edu.purdue.cs.tornado.index.local.fast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.messages.Query;

public class KeywordTrieCellMinimalExperiment {
	public HashMap<String, Object> trieCells;
	public ArrayList<Query> queries;
	public ArrayList<Query> finalQueries;
	//public boolean extended ;

	public KeywordTrieCellMinimalExperiment() {
		queries = null;//
		finalQueries = null;
		trieCells = null;
		//	extended = false;
	}

	public void find(ArrayList<String> keywords, int start, List<Query> result, int level, Point location) {
		if (finalQueries != null)
			for (Query q : finalQueries) {
				if (SpatialHelper.overlapsSpatially(location, q.getSpatialRange())) {
					result.add(q);
				}
			}
		if (queries != null)
			for (Query q : queries) {
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
				if (cell instanceof Query) {
					if (SpatialHelper.overlapsSpatially(location, ((Query) cell).getSpatialRange()) && TextHelpers.containsTextually(keywords, ((Query) cell).getQueryText()))
						result.add(((Query) cell));

				} else if (cell instanceof ArrayList) {
					for (Query q : ((ArrayList<Query>) cell)) {
						if (SpatialHelper.overlapsSpatially(location, q.getSpatialRange()) && TextHelpers.containsTextually(keywords, q.getQueryText()))
							result.add(q);
					}

				} else if (cell instanceof KeywordTrieCellMinimalExperiment) {
					((KeywordTrieCellMinimalExperiment) cell).find(keywords, i + 1, result, level + 1, location);
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

				} else if (cell instanceof KeywordTrieCellMinimalExperiment) {
					((KeywordTrieCellMinimalExperiment) cell).findTextualOnly(keywords, i + 1, result, level + 1);
				}

			}

	}

	public int clean(ArrayList<Query> combinedQueries) {
		int operations = 0;
		if (queries != null) {
			Iterator<Query> queriesItr = queries.iterator();
			while (queriesItr.hasNext()) {
				Query query = queriesItr.next();
				if (query.getRemoveTime() < FAST.queryTimeStampCounter)
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
					if (((Query) cell).getRemoveTime() < FAST.queryTimeStampCounter)
						trieCellsItr.remove();
					else {
						combinedQueries.add((Query) cell);
					}
					operations++;
				} else if (cell instanceof ArrayList) {
					Iterator<Query> queriesInternalItr = ((ArrayList<Query>) cell).iterator();
					while (queriesInternalItr.hasNext()) {
						Query query = queriesInternalItr.next();
						if (query.getRemoveTime() < FAST.queryTimeStampCounter)
							queriesInternalItr.remove();
						else {
							combinedQueries.add(query);
						}
						operations++;
					}
					if (((ArrayList<Query>) cell).size() == 0)
						trieCellsItr.remove();
				} else if (cell instanceof KeywordTrieCellMinimalExperiment) {
					operations += ((KeywordTrieCellMinimalExperiment) cell).clean(combinedQueries);
					if (((KeywordTrieCellMinimalExperiment) cell).queries == null && ((KeywordTrieCellMinimalExperiment) cell).trieCells == null)
						trieCellsItr.remove();
				}
			}
			if (trieCells.size() == 0)
				trieCells = null;
		}

		return operations;
	}

}
