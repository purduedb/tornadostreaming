package edu.purdue.cs.tornado.index.local;

import java.util.ArrayList;
import java.util.HashMap;

import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

/**
 * This class just puts everything in a single cell that is there is not local
 * index at all
 * 
 * @author ahmed
 *
 */
public class NoLocalIndex extends LocalHybridIndex {
	IndexCell singleIndexCell;

	private Rectangle selfBounds;
	DataSourceInformation dataSourcesInformation;
	private Integer allDataCount;
	private ArrayList<Query> globalKNNQueries;
	public NoLocalIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation) {
		super();
		this.allDataCount = 0;
		this.dataSourcesInformation = dataSourcesInformation;
		this.selfBounds = selfBounds;
	
		singleIndexCell = new IndexCell(selfBounds, false, 0);
		globalKNNQueries = new ArrayList<Query>();

	}

	@Override
	public Boolean addContinousQuery(Query query) {
		singleIndexCell.addQuery(query);
		return true;
	}

	@Override
	public IndexCell addDataObject(DataObject dataObject) {
		singleIndexCell.addDataObject(dataObject);
		return singleIndexCell;
	}

	@Override
	public Boolean dropContinousQuery(Query query) {
		singleIndexCell.dropQuery(query);
		return true;
	}

	@Override
	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords) {
		return singleIndexCell.getDataObjectCount();
	}

	@Override
	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords, Rectangle rect) {
		return singleIndexCell.getDataObjectCount();
	}

	@Override
	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords) {
		return singleIndexCell.getDataObjectCount();
	}

	@Override
	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords, Rectangle rect) {
		return singleIndexCell.getDataObjectCount();
	}

	@Override
	public Integer getCountPerRec(Rectangle rec) {
		return singleIndexCell.getDataObjectCount();
	}

	@Override
	public IndexCell getOverlappingIndexCells(Point point) {
		return singleIndexCell;
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCells(Rectangle rectangle) {
		ArrayList<IndexCell> indexCells = new ArrayList<IndexCell>();
		indexCells.add(singleIndexCell);
		return indexCells;
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle) {
		ArrayList<IndexCell> indexCells = new ArrayList<IndexCell>();
		indexCells.add(singleIndexCell);
		return indexCells;
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle, ArrayList<String> keywords) {
		ArrayList<IndexCell> indexCells = new ArrayList<IndexCell>();
		indexCells.add(singleIndexCell);
		return indexCells;
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(ArrayList<String> keywords) {
		ArrayList<IndexCell> indexCells = new ArrayList<IndexCell>();
		indexCells.add(singleIndexCell);
		return indexCells;
	}

	@Override
	public HashMap<String, Query> getReleventQueries(DataObject dataObject, Boolean fromNeighbour) {

		//this hashmap is based on query source id , query id itself
		HashMap<String, Query> queriesMap = new HashMap<String, Query>();

		//first consider all global KNN queries 
		for (Query q : globalKNNQueries) {
			String unqQueryId = q.getUniqueIDFromQuerySourceAndQueryId();
			if (!queriesMap.containsKey(unqQueryId))
				queriesMap.put(unqQueryId, q);
		}
		ArrayList<IndexCell> relevantIndexCells;
		if (fromNeighbour) {
			relevantIndexCells = getOverlappingIndexCells(dataObject.getRelevantArea());
			for (IndexCell indexCell : relevantIndexCells) {
				ArrayList<Query> queries = indexCell.getQueries();
				for (Query q : queries) {
					String unqQueryId = q.getUniqueIDFromQuerySourceAndQueryId();
					if (!queriesMap.containsKey(unqQueryId))
						queriesMap.put(unqQueryId, q);
				}
			}
		} else {
			IndexCell indexCell = getOverlappingIndexCells(dataObject.getLocation());
			ArrayList<Query> queries = indexCell.getQueries();
			for (Query q : queries) {
				String unqQueryId = q.getUniqueIDFromQuerySourceAndQueryId();
				if (!queriesMap.containsKey(unqQueryId))
					queriesMap.put(unqQueryId, q);
			}
		}

		return queriesMap;
	}

	@Override
	public LocalIndexKNNIterator KNNIterator(Point focalPoint, Double distance) {

		return null;
	}

	@Override
	public LocalIndexKNNIterator LocalKNNIterator(Point focalPoint) {

		return null;
	}

	@Override
	public IndexCell mapDataObjectToIndexCell(DataObject dataObject) {
		return singleIndexCell;
	}

	@Override
	public Boolean updateContinousQuery(Query oldQuery, Query query) {
		return true;
	}

}
