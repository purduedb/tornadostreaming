package edu.purdue.cs.tornado.index.local.fast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.messages.MinimalRangeQuery;
import edu.purdue.cs.tornado.messages.Query;
import edu.purdue.cs.tornado.helper.KeywordTrieCell;
import edu.purdue.cs.tornado.helper.ReinsertEntry;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.helper.IndexCell;




public class FASTIndexCell {
	public ConcurrentHashMap<String, Object> ptp;//queries that fall only within the cells range
	Iterator<Entry<String, Object>> cleaningIterator;
	Rectangle bounds;
	int coordinate;
	boolean test;
	Query testObject ;
	int level;
	int debugQID=-1;
	
	public FASTIndexCell(Rectangle bounds, Integer globalCoordinates, int level) {
		ptp = null;
		this.bounds = bounds;
		this.bounds.getMax().X-=.001;
		this.bounds.getMax().Y-=.001;
		this.coordinate = globalCoordinates;
		test = false;
		this.level=level;
	}
	
	public void addInternalQuery(String keyword, Query query, ArrayList<Query> sharedQueries, ArrayList<ReinsertEntry>insertNextLevelQueries, boolean force) {
		if (ptp == null) {
			ptp = new ConcurrentHashMap<String, Object>();
		}
		if (!ptp.containsKey(keyword) && sharedQueries != null) {
			LocalFASTIndex.numberOfHashEntries++;
			ptp.put(keyword, sharedQueries);
		} else {
			Object keywordIndex = ptp.get(keyword);
			if (keywordIndex instanceof Query) {
				Query exitingQuery = (Query) keywordIndex;
				if (exitingQuery.expireTime > LocalFASTIndex.queryTimeStampCounter) { //checking for the support of the query
					if (sharedQueries.contains(exitingQuery)) {
						ptp.put(keyword, sharedQueries);
					} else if (sharedQueries.size() < LocalFASTIndex.Trie_SPLIT_THRESHOLD) {
						LocalFASTIndex.queryInsertInvListNodeCounter++;
						sharedQueries.add(exitingQuery);
						ptp.put(keyword, sharedQueries);
					} else
						addInternalQueryNoShare(keyword, query, sharedQueries, insertNextLevelQueries, force);
				} else {
					deleteQueryFromStats(query);
					ptp.put(keyword, sharedQueries);
				}
			} else if (keywordIndex instanceof ArrayList && keywordIndex == sharedQueries) {
				//already inserted do nothing;

			} else if (keywordIndex instanceof ArrayList && keywordIndex != sharedQueries) {
				//already inserted do nothing;
				ArrayList<Query> nonSharedQueries = new ArrayList<Query>();
				for (Query q : (ArrayList<Query>) keywordIndex) {
					if (!sharedQueries.contains(q))
						nonSharedQueries.add(q);
				}
				if (nonSharedQueries.size() > 0 && nonSharedQueries.size() + sharedQueries.size() <=   LocalFASTIndex.Trie_SPLIT_THRESHOLD) {
					sharedQueries.addAll(nonSharedQueries);
					ptp.put(keyword, sharedQueries);
				} else {
					addInternalQueryNoShare(keyword, query, sharedQueries, insertNextLevelQueries, force);
				}
			} else {

				addInternalQueryNoShare(keyword, query, sharedQueries, insertNextLevelQueries, force);
			}
		}
	}

	public void deleteQueryFromStats(Query query) {
		if (query.deleted == false) {
			query.deleted = true;
//			for (String keyword : query.getQueryText()) {
//				LocalFASTIndex.overallQueryTextSummery.get(keyword).queryCount--;
//			}
			//TODO
		}
	}

	public Object addInternalQueryNoShare(String keyword, Query query, ArrayList<Query> sharedQueries, ArrayList<ReinsertEntry> insertNextLevelQueries, boolean force) {
		if (ptp == null) {
			ptp = new ConcurrentHashMap<String, Object>();
		}
		Queue<Query> queue = new LinkedList<Query>();
		boolean inserted = false;
		//step 1 find the best keyword to insert in:
		if(query.getQueryId()==debugQID ){
			System.out.println("Inserting: "+query+coordinate);
		}
		inserted = insertAtKeyWord(keyword, query, sharedQueries);
		if (inserted)
			if (ptp.get(keyword) instanceof ArrayList)
				return ptp.get(keyword);
			else
				return null;
		else {
			queue.add(query);

		}

		while (!queue.isEmpty()) {
			query = queue.remove();
			if(query.getQueryId()==debugQID ){
				System.out.println("Inserting: "+query+coordinate);
			}
			inserted = false;

			String otherKeyword = getOtherKeywordToInser(query);
			if (otherKeyword != null) {
				inserted = insertAtKeyWord(otherKeyword, query, null);
			}
			if (inserted)
				continue;
			else {
				for (String term : query.getQueryText()) {
					//mark all these keywords as tries
					if (ptp.get(term) instanceof ArrayList) {
						for(Query q:(ArrayList<Query>) ptp.get(term)){
							if(q.getQueryId() == debugQID)
								System.out.println("Adding query to queue for reinsert at a different list: "+query+coordinate +"keyword "+term);
						}
						queue.addAll((ArrayList<Query>) ptp.get(term));
						ptp.put(term, new KeywordTrieCell());
						LocalFASTIndex.numberOfTrieNodes++;
					}
				}
			}
			;
			KeywordTrieCell currentCell = (KeywordTrieCell) ptp.get(query.getQueryText().get(0));

			for (int j = 1; j < query.getQueryText().size() & !inserted; j++) {
				keyword = query.getQueryText().get(j);
				if (currentCell.trieCells == null)
					currentCell.trieCells = new HashMap<String, Object>();

				Object cell = currentCell.trieCells.get(keyword);
				if (cell == null) {
					currentCell.trieCells.put(keyword, query);
					inserted = true;
				} else if (cell instanceof Query) {
					if (((Query) cell).expireTime > LocalFASTIndex.queryTimeStampCounter) {
						ArrayList<Query> queries = new ArrayList<Query>();
						queries.add((Query) cell);
						queries.add((Query) query);
						currentCell.trieCells.put(keyword, queries);
						inserted = true;
					} else {
						deleteQueryFromStats((Query) cell);
						currentCell.trieCells.put(keyword, query);
						inserted = true;
					}
				} else if (cell instanceof ArrayList && ((ArrayList<Query>) cell).size() <= LocalFASTIndex.Trie_SPLIT_THRESHOLD) {
					((ArrayList<Query>) cell).add(query);
					inserted = true;
				} else if (cell instanceof ArrayList && ((ArrayList<Query>) cell).size() > LocalFASTIndex.Trie_SPLIT_THRESHOLD) {
					KeywordTrieCell newCell = new KeywordTrieCell();
					LocalFASTIndex.numberOfTrieNodes++;
					((ArrayList<Query>) cell).add(query);
					newCell.trieCells = new HashMap<String, Object>();
					newCell.queries = new ArrayList<Query>();
					currentCell.trieCells.put(keyword, newCell);
					for (Query otherQuery : ((ArrayList<Query>) cell)) {
						if (otherQuery.expireTime > LocalFASTIndex.queryTimeStampCounter) {
							if (otherQuery.getQueryText().size() > (j + 1)) {

								Object otherCell = newCell.trieCells.get(otherQuery.getQueryText().get(j + 1));
								if (otherCell == null) {
									otherCell = new ArrayList<Query>();
								}
								((ArrayList<Query>) otherCell).add(otherQuery);
								newCell.trieCells.put(otherQuery.getQueryText().get(j + 1), otherCell);
							} else {
								newCell.queries.add(otherQuery);
							}
						} else {
							deleteQueryFromStats(otherQuery);
						}
					}
					if (newCell.queries != null && newCell.queries.size() == 0)
						newCell.queries = null;
					inserted = true;
				} else if (cell instanceof KeywordTrieCell) {
					if (j < (query.getQueryText().size() - 1)) {
						currentCell = (KeywordTrieCell) cell;
					} else {
						if (level==0||checkSpanForForceInsertFinal(query)) {
							if (((KeywordTrieCell) cell).finalQueries == null)
								((KeywordTrieCell) cell).finalQueries = new ArrayList<Query>();
							((KeywordTrieCell) cell).finalQueries.add(query);
							if(query.getQueryId()==debugQID){
								System.out.println("final"+query+coordinate);
								test = true;
								testObject = query;
							}
						} else {

							if (((KeywordTrieCell) cell).queries == null)
								((KeywordTrieCell) cell).queries = new ArrayList<Query>();
							((KeywordTrieCell) cell).queries.add(query);
							if(query.getQueryId()==debugQID)
								System.out.println("Not final"+query+coordinate);
							if (((KeywordTrieCell) cell).queries.size() >  LocalFASTIndex.Degredation_Ratio) {
								findQueriesToReinsert((KeywordTrieCell) cell, insertNextLevelQueries);
							}
						}
						inserted = true;
					}

				}

			}
			if (!inserted) {
				if ((level==0||checkSpanForForceInsertFinal(query)) ) {
					if (currentCell.finalQueries == null)
						currentCell.finalQueries = new ArrayList<Query>();
					currentCell.finalQueries.add(query);
					if(query.getQueryId()==debugQID)
						System.out.println("final"+query+coordinate);

				} else {
					if (currentCell.queries == null)
						currentCell.queries = new ArrayList<Query>();
					currentCell.queries.add(query);
					if(query.getQueryId()==debugQID)
						System.out.println("Not final"+query+coordinate);
					if (currentCell.queries.size() > LocalFASTIndex.Degredation_Ratio)
						findQueriesToReinsert(currentCell, insertNextLevelQueries);
				}
			}

		}

		if(test ==true &&coordinate==16777766){
			;;
			KeywordTrieCell index1 = ((KeywordTrieCell)ptp.get("casino"));
			KeywordTrieCell index2 = ((KeywordTrieCell)index1.trieCells.get("hotel"));
			KeywordTrieCell index3 =  ((KeywordTrieCell)index2.trieCells.get("las"));
			
			if(!index3.finalQueries.contains(testObject))
				System.out.println("There is an error here");
		}
		return null;
	}
	public boolean checkSpanForForceInsertFinal(Query query){
		double queryRange = query.getSpatialRange().getMax().X-query.getSpatialRange().getMin().X;
		double cellRange = bounds.getMax().X-bounds.getMin().X;
		if(queryRange>(cellRange/2) )
			return true;
		return false;
		
	}
	public boolean insertAtKeyWord(String keyword, Query query, ArrayList<Query> sharedQueries) {
		boolean inserted = false;
		//System.out.println("Line 276 here");
		//if(keyword == null){
			//return null;
		//}
		if (!ptp.containsKey(keyword)) {
			//System.out.println("Line 278 here");
			LocalFASTIndex.numberOfHashEntries++;
			LocalFASTIndex.queryInsertInvListNodeCounter++;
			inserted = true;
			ptp.put(keyword, query);
			return inserted;
		} else { //this keyword already exists in the index
			//System.out.println("Line 285 here");
			Object keywordIndex = ptp.get(keyword);
			if (keywordIndex == null) {
				LocalFASTIndex.queryInsertInvListNodeCounter++;
				ptp.put(keyword, query);
				inserted = true;
				return inserted;
			}
			if (keywordIndex instanceof Query) { //single query 
				Query exitingQuery = (Query) keywordIndex;
				if (exitingQuery.expireTime > LocalFASTIndex.queryTimeStampCounter) { //checking for the support of the query
					ArrayList<Query> rareQueries = new ArrayList<Query>();
					LocalFASTIndex.queryInsertInvListNodeCounter++;
					rareQueries.add(exitingQuery);
					rareQueries.add(query);
					inserted = true;
					ptp.put(keyword, rareQueries);
					return inserted;
				} else {
					inserted = true;
					deleteQueryFromStats(exitingQuery);
					ptp.put(keyword, query);
					return inserted;
				}

			} else if ((keywordIndex instanceof ArrayList) && ((ArrayList<Query>) keywordIndex).size() < LocalFASTIndex.Trie_SPLIT_THRESHOLD) { // this keyword is rare
				if (((ArrayList<Query>) keywordIndex) != sharedQueries)
					if (!((ArrayList<Query>) keywordIndex).contains( query)) {
						((ArrayList<Query>) keywordIndex).add(query);
						LocalFASTIndex.queryInsertInvListNodeCounter++;
					}
				inserted = true;
				return inserted;
			} else if ((keywordIndex instanceof ArrayList) && ((ArrayList<Query>) keywordIndex).size() >= LocalFASTIndex.Trie_SPLIT_THRESHOLD) { // this keyword is rare
				if (((ArrayList<Query>) keywordIndex) != sharedQueries) {
					if (!((ArrayList<Query>) keywordIndex).contains( query)) {
						inserted = false;
						return inserted;
					} else {
						inserted = true;
						return inserted;
					}
				} else {
					inserted = true;
					return inserted;
				}
				//then trie insert for all frequent keywords 
			} else if (keywordIndex instanceof KeywordTrieCell) {
				inserted = false;
				return inserted;
			} else {
				System.err.println("This is an error you should never be here");
			}
		}
		return false;
	}

	public String getOtherKeywordToInser(Query query) {
		int minSize = Integer.MAX_VALUE;
		String minKeyword = null;
		for (String term : query.getQueryText()) {
			if (!ptp.containsKey(term)) {
				int size = 0;
				if (size < minSize) {
					minSize = size;
					minKeyword = term;
				}

			} else if (ptp.containsKey(term) && ptp.get(term) instanceof Query) {
				int size = 1;
				if (size < minSize) {
					minSize = size;
					minKeyword = term;
				}

			} else if (ptp.containsKey(term) && ptp.get(term) instanceof ArrayList) {
				int size = ((ArrayList<Query>) ptp.get(term)).size();
				if (size < minSize) {
					minSize = size;
					minKeyword = term;
				}
			}

		}
		return minKeyword;
	}

	public void findQueriesToReinsert(KeywordTrieCell cell, ArrayList<ReinsertEntry> insertNextLevelQueries) {
		SpatialOverlapCompartor spatialOverlapCompartor = new SpatialOverlapCompartor(bounds);
		Collections.sort(cell.queries, spatialOverlapCompartor);
		int queriesSize =  cell.queries.size() ;
		for (int i = queriesSize - 1; i > queriesSize/2; i--) {
		//for (int i = queriesSize - 1; i >=0; i--) {
			Query query = cell.queries.remove(i);
			insertNextLevelQueries.add(new ReinsertEntry(SpatialHelper.spatialIntersect(bounds, query.getSpatialRange()), query));
			if(query.getQueryId()==debugQID)
				System.out.println("reinsert"+query+coordinate);
		}
	}

	//removal of expired entries;
	public boolean clean() {
		if (cleaningIterator == null || !cleaningIterator.hasNext())
			cleaningIterator = ptp.entrySet().iterator();
		Integer numberOfVisitedEntries = 0;
		while (cleaningIterator.hasNext() && numberOfVisitedEntries < LocalFASTIndex.MAX_ENTRIES_PER_CLEANING_INTERVAL) {
			Entry<String, Object> keywordIndexEntry = cleaningIterator.next();
			Object keywordIndex = keywordIndexEntry.getValue();
			String keyword = keywordIndexEntry.getKey();
			if (keywordIndex instanceof Query) {
				numberOfVisitedEntries++;
				if (((Query) keywordIndex).expireTime < LocalFASTIndex.queryTimeStampCounter)
					keywordIndex = null;
			} else if (keywordIndex instanceof ArrayList) {
				Iterator<Query> queriesItrator = ((ArrayList<Query>) keywordIndex).iterator();
				while (queriesItrator.hasNext()) {
					Query query = queriesItrator.next();
					if (query.expireTime < LocalFASTIndex.queryTimeStampCounter)
						queriesItrator.remove();
					numberOfVisitedEntries++;
				}
				if (((ArrayList<Query>) keywordIndex).size() == 0)
					keywordIndex = null;
				else if (((ArrayList<Query>) keywordIndex).size() == 1) {
					Query singleQuery = ((ArrayList<Query>) keywordIndex).get(0);
					ptp.put(keyword, singleQuery);

				}
			} else if (keywordIndex instanceof KeywordTrieCell) {

				ArrayList<Query> combinedQueries = new ArrayList<Query>();
				numberOfVisitedEntries += ((KeywordTrieCell) keywordIndex).clean(combinedQueries);
				if (((KeywordTrieCell) keywordIndex).queries == null && ((KeywordTrieCell) keywordIndex).trieCells == null
						&& LocalFASTIndex.overallQueryTextSummery.get(keyword).queryCount <= LocalFASTIndex.Trie_OVERLALL_MERGE_THRESHOLD)
					keywordIndex = null;
				else if (combinedQueries.size() < LocalFASTIndex.Trie_OVERLALL_MERGE_THRESHOLD
						&& LocalFASTIndex.overallQueryTextSummery.get(keyword).queryCount <= LocalFASTIndex.Trie_OVERLALL_MERGE_THRESHOLD)
					ptp.put(keyword, combinedQueries);
			}
			if (keywordIndex == null)
				cleaningIterator.remove();

		}
		if (cleaningIterator.hasNext())
			return false;
		else
			return true;

	}

	public ArrayList<String> getInternalSpatiotTextualOverlappingQueries(Point p, ArrayList<String> keywords, List<Query> finalQueries) {
		ArrayList<String> remainingKewords = new ArrayList<String>();
		for (int i = 0; i < keywords.size(); i++) {
			String keyword = keywords.get(i);
			Object keyWordIndex = ptp.get(keyword);
			LocalFASTIndex.objectSearchInvListHashAccess++;
			if (keyWordIndex != null) {
				if (keyWordIndex instanceof Query) {
					Query query = ((Query) keyWordIndex);
					if (keywords.size() >= query.getQueryText().size() && SpatialHelper.overlapsSpatially(p, query.getSpatialRange()) && TextHelpers.containsTextually(keywords, query.getQueryText())) {
						finalQueries.add(query);
					}
				} else if (keyWordIndex instanceof ArrayList) {
					ArrayList<Query> rareQueries = (ArrayList<Query>) keyWordIndex;
					for (Query q : rareQueries) {
						LocalFASTIndex.objectSearchInvListNodeCounter++;
						if (keywords.size() >= q.getQueryText().size() && SpatialHelper.overlapsSpatially(p, q.getSpatialRange()) && TextHelpers.containsTextually(keywords, q.getQueryText())) {
							finalQueries.add(q);
						}
					}
				} else if (keyWordIndex instanceof KeywordTrieCell) {
					remainingKewords.add(keyword);
				}
			}

		}
		for (int i = 0; i < remainingKewords.size(); i++) {
			String keyword = remainingKewords.get(i);
			Object keyWordIndex = ptp.get(keyword);
			LocalFASTIndex.totalTrieAccess++;
			((KeywordTrieCell) keyWordIndex).find(remainingKewords, i + 1, finalQueries, 0, p);
		}

		return remainingKewords;
	}

}

class SpatialOverlapCompartor implements Comparator<Query> {
	Rectangle bounds;

	public SpatialOverlapCompartor(Rectangle bounds) {
		this.bounds = bounds;
	}

	@Override
	public int compare(Query e1, Query e2) {
		double val1 = 0, val2 = 0;

		val1 = SpatialHelper.getArea(SpatialHelper.spatialIntersect(bounds, e1.getSpatialRange()));

		val2 = SpatialHelper.getArea(SpatialHelper.spatialIntersect(bounds, e2.getSpatialRange()));
		if (val1 < val2) {
			return 1;
		} else if (val1 == val2)
			return 0;
		else {
			return -1;
		}
	}
	
}
