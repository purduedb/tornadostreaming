package edu.purdue.cs.tornado.index.local.quadtree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

//import ch.qos.logback.classic.Level;
//import edu.purdue.cs.tornado.helper.QuadTreeIndexCell;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.QueryType;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.index.local.LocalIndexKNNIterator;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.KNNQuery;
import edu.purdue.cs.tornado.messages.Query;

public class LocalQuadTree {

	QuadTreeIndexCell root;
	public Rectangle selfBounds;
	DataSourceInformation dataSourcesInformation;
	public ArrayList<Query> globalKNNQueries;

	public Boolean spatialOnlyFlag;
	public Integer allDataCount;
	public Integer maxLevel;
	public Integer splitThreshold;

	public LocalQuadTree(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Boolean spatialOnlyFlag, Integer maxlevel, Integer splitThreshold) {
		super();
		this.selfBounds = selfBounds;
		this.maxLevel = maxlevel;
		this.dataSourcesInformation = dataSourcesInformation;
		this.splitThreshold = splitThreshold;
		root = new QuadTreeIndexCell(selfBounds, spatialOnlyFlag, 0);
		this.spatialOnlyFlag = spatialOnlyFlag;
		this.globalKNNQueries = new ArrayList<Query>();

	}

	
	public Boolean addContinousQuery(Query query) {
		if (query.getQueryType().equals(QueryType.queryTextualKNN)) {

			// initially assume that the incomming KKN query for a
			// volatile data object will go to global data entry for this bolt
			if (dataSourcesInformation.isVolatile()) {
				((KNNQuery)query).resetKNNStructures();
				globalKNNQueries.add(query);
			}
		}

		ArrayList<QuadTreeIndexCell> relevantCells = mapQueryToPartitions(query);
		for (QuadTreeIndexCell QuadTreeIndexCell : relevantCells) {

			QuadTreeIndexCell.addQuery(query);
			if (QueryType.queryTextualRange.equals(query.getQueryType()) || QueryType.queryTextualSpatialJoin.equals(query.getQueryType())) {
				if (SpatialHelper.insideSpatially(query.getSpatialRange(), QuadTreeIndexCell.bounds))
					QuadTreeIndexCell.coverQueryCount++;
			}
			if (QuadTreeIndexCell.storedQueries.size() >= splitThreshold && QuadTreeIndexCell.storedQueries.size() > QuadTreeIndexCell.coverQueryCount*2 && QuadTreeIndexCell.level < this.maxLevel) {
				splitCell(QuadTreeIndexCell);
			}
		}
		return true;
	}

	private ArrayList<QuadTreeIndexCell> mapQueryToPartitions(Query query) {
		ArrayList<QuadTreeIndexCell> relevantCells = new ArrayList<QuadTreeIndexCell>();
		double xmin, ymin, xmax, ymax;
		if (QueryType.queryTextualRange.equals(query.getQueryType()) || QueryType.queryTextualSpatialJoin.equals(query.getQueryType())) {
			if (query.getSpatialRange().getMin().getX() > selfBounds.getMax().getX() || query.getSpatialRange().getMin().getY() > selfBounds.getMax().getY() || query.getSpatialRange().getMax().getX() < selfBounds.getMin().getX()
					|| query.getSpatialRange().getMax().getY() < selfBounds.getMin().getY()) {
				System.err.println("Error query:" + query.getSrcId() + "  is outside the range of this bolt ");
			} else {
				relevantCells = getOverlappingIndexCells(query.getSpatialRange());
			}
		} else if (QueryType.queryTextualKNN.equals(query.getQueryType())) {

			relevantCells.add(getOverlappingIndexCells(((KNNQuery)query).getFocalPoint()));
		}
		return relevantCells;
	}

	
	public QuadTreeIndexCell addDataObject(DataObject dataObject) {
		QuadTreeIndexCell currentIndexCell = root;
		QuadTreeIndexCell retunIndexCell = null;
		while (currentIndexCell.children != null) {
			currentIndexCell.dataObjectCount++;
			for (int i = 0; i < currentIndexCell.numberOfChildren; i++) {
				if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), currentIndexCell.children[i].bounds)) {
					currentIndexCell = currentIndexCell.children[i];
					break;
				}
			}
		}
		if (currentIndexCell.storedObjects!=null&&currentIndexCell.storedObjects.size() > splitThreshold && currentIndexCell.level < maxLevel) {
			splitCell(currentIndexCell);
			for (int i = 0; i < currentIndexCell.numberOfChildren; i++) {
				if (SpatialHelper.overlapsSpatially(dataObject.getLocation(), currentIndexCell.children[i].bounds)) {
					retunIndexCell = currentIndexCell.children[i];
					currentIndexCell.children[i].addDataObject(dataObject);
					break;
				}

			}
			return retunIndexCell;
		} else {
			currentIndexCell.addDataObject(dataObject);
			return currentIndexCell;
		}

	}

	private void splitCell(QuadTreeIndexCell currentIndexCell) {
		currentIndexCell.children = new QuadTreeIndexCell[4];
		double xrange = currentIndexCell.getBounds().getMax().getX() - currentIndexCell.getBounds().getMin().getX();
		double yrange = currentIndexCell.getBounds().getMax().getY() - currentIndexCell.getBounds().getMin().getY();
		double xmin = currentIndexCell.getBounds().getMin().getX();
		double ymin = currentIndexCell.getBounds().getMin().getY();
		currentIndexCell.spatialOnlyFlag = true;
		Rectangle bounds1 = new Rectangle(currentIndexCell.getBounds().getMin(), new Point(xmin + xrange / 2, ymin + yrange / 2));
		Rectangle bounds2 = new Rectangle(new Point(xmin + xrange / 2, ymin), new Point(xmin + xrange, ymin + yrange / 2));
		Rectangle bounds3 = new Rectangle(new Point(xmin, ymin + yrange / 2), new Point(xmin + xrange / 2, ymin + yrange));
		Rectangle bounds4 = new Rectangle(new Point(xmin + xrange / 2, ymin + yrange / 2), new Point(xmin + xrange, ymin + yrange));
		currentIndexCell.children[0] = new QuadTreeIndexCell(bounds1, spatialOnlyFlag, currentIndexCell.level + 1);
		currentIndexCell.children[1] = new QuadTreeIndexCell(bounds2, spatialOnlyFlag, currentIndexCell.level + 1);
		currentIndexCell.children[2] = new QuadTreeIndexCell(bounds3, spatialOnlyFlag, currentIndexCell.level + 1);
		currentIndexCell.children[3] = new QuadTreeIndexCell(bounds4, spatialOnlyFlag, currentIndexCell.level + 1);
		currentIndexCell.numberOfChildren = 4;
		if (currentIndexCell.getStoredObjects() != null) {
			Iterator<DataObject> itr = currentIndexCell.getStoredObjects().values().iterator();
			while (itr.hasNext()) {
				DataObject obj = itr.next();
				for (int i = 0; i < currentIndexCell.numberOfChildren; i++) {
					if (SpatialHelper.overlapsSpatially(obj.getLocation(), currentIndexCell.children[i].bounds)) {
						currentIndexCell.children[i].addDataObject(obj);
						break;
					}

				}
			}
		}

		List<Query> queries = currentIndexCell.getQueries();

		if (queries != null)
			for (Query query : queries) {

				if (QueryType.queryTextualSpatialJoin.equals(query.getQueryType()) || QueryType.queryTextualRange.equals(query.getQueryType())) {
					//					if (SpatialHelper.insideSpatially(query.getSpatialRange(), currentIndexCell.boundsNoBoundaries))
					//						currentIndexCell.addQuery(query);
					//					else
					

					for (int i = 0; i < currentIndexCell.numberOfChildren; i++) {
						if (SpatialHelper.overlapsSpatially(query.getSpatialRange(), currentIndexCell.children[i].bounds))
							currentIndexCell.children[i].addQuery(query);
						if (SpatialHelper.insideSpatially(query.getSpatialRange(), currentIndexCell.children[i].bounds))
							currentIndexCell.children[i].coverQueryCount++;
					}
				} else if (QueryType.queryTextualKNN.equals(query.getQueryType())) {
					for (int i = 0; i < currentIndexCell.numberOfChildren; i++) {
						if (SpatialHelper.overlapsSpatially(((KNNQuery)query).getFocalPoint(), currentIndexCell.children[i].bounds))
							currentIndexCell.children[i].addQuery(query);
					}
				}

			}
		currentIndexCell.allDataTextInCell = null;
		currentIndexCell.allQueryTextInCell = null;
		currentIndexCell.storedObjects = null;
		currentIndexCell.storedQueries = null;
		currentIndexCell.coverQueryCount = 0;
		currentIndexCell.queryCount = 0;
	}

	
	public Boolean dropContinousQuery(Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords) {
		return getCountPerKeywrodsAll(keywords, selfBounds);
	}

	
	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords, Rectangle rect) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords, Rectangle rect) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Integer getCountPerRec(Rectangle rec) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public QuadTreeIndexCell getOverlappingIndexCells(Point point) {
		QuadTreeIndexCell currentIndexCell = root;
		while (currentIndexCell.children != null) {
			for (int i = 0; i < currentIndexCell.numberOfChildren; i++) {
				if (SpatialHelper.overlapsSpatially(point, currentIndexCell.children[i].bounds)) {
					currentIndexCell = currentIndexCell.children[i];
					break;
				}
			}
		}
		return currentIndexCell;
	}

	public ArrayList<QuadTreeIndexCell> getOverlappingIndexCellsIncludingNonLeaf(Point point) {
		ArrayList<QuadTreeIndexCell> relevantCells = new ArrayList<QuadTreeIndexCell>();
		QuadTreeIndexCell currentIndexCell = root;
		relevantCells.add(currentIndexCell);
		while (currentIndexCell.children != null) {

			for (int i = 0; i < currentIndexCell.numberOfChildren; i++) {
				if (SpatialHelper.overlapsSpatially(point, currentIndexCell.children[i].bounds)) {
					currentIndexCell = currentIndexCell.children[i];
					relevantCells.add(currentIndexCell);
					break;
				}
			}
		}
		return relevantCells;
	}

	
	public ArrayList<QuadTreeIndexCell> getOverlappingIndexCells(Rectangle rectangle) {
		ArrayList<QuadTreeIndexCell> relevantCells = new ArrayList<QuadTreeIndexCell>();
		QuadTreeIndexCell currentIndexCell = root;
		Queue<QuadTreeIndexCell> queue = new LinkedList<QuadTreeIndexCell>();
		if (SpatialHelper.overlapsSpatially(currentIndexCell.boundsNoBoundaries, rectangle))
			queue.add(currentIndexCell);
		while (!queue.isEmpty()) {
			currentIndexCell = queue.remove();
			if (currentIndexCell.children != null)
				for (int i = 0; i < currentIndexCell.numberOfChildren; i++) {
					if (SpatialHelper.overlapsSpatially(currentIndexCell.children[i].boundsNoBoundaries, rectangle)) {
						queue.add(currentIndexCell.children[i]);
					}
				}
			else
				relevantCells.add(currentIndexCell);
		}
		return relevantCells;
	}

	public ArrayList<QuadTreeIndexCell> getOverlappingIndexCellsIncludingNonleaf(Rectangle rectangle) {
		ArrayList<QuadTreeIndexCell> relevantCells = new ArrayList<QuadTreeIndexCell>();
		QuadTreeIndexCell currentIndexCell = root;
		Queue<QuadTreeIndexCell> queue = new LinkedList<QuadTreeIndexCell>();
		if (SpatialHelper.overlapsSpatially(currentIndexCell.boundsNoBoundaries, rectangle)) {
			queue.add(currentIndexCell);
		}
		while (!queue.isEmpty()) {
			currentIndexCell = queue.remove();
			relevantCells.add(currentIndexCell);
			if (currentIndexCell.children != null)
				for (int i = 0; i < currentIndexCell.numberOfChildren; i++) {
					if (SpatialHelper.overlapsSpatially(currentIndexCell.children[i].boundsNoBoundaries, rectangle)) {
						queue.add(currentIndexCell.children[i]);
					}
				}

		}
		return relevantCells;
	}

	
	public ArrayList<QuadTreeIndexCell> getOverlappingIndexCellWithData(ArrayList<String> keywords) {
		return getOverlappingIndexCellWithData(selfBounds, keywords);
	}

	
	public ArrayList<QuadTreeIndexCell> getOverlappingIndexCellWithData(Rectangle rectangle) {
		ArrayList<QuadTreeIndexCell> relevantCells = new ArrayList<QuadTreeIndexCell>();
		QuadTreeIndexCell currentIndexCell = root;
		Queue<QuadTreeIndexCell> queue = new LinkedList<QuadTreeIndexCell>();
		if (SpatialHelper.overlapsSpatially(currentIndexCell.boundsNoBoundaries, rectangle))
			queue.add(currentIndexCell);
		while (!queue.isEmpty()) {
			currentIndexCell = queue.remove();
			if (currentIndexCell.children != null)
				for (int i = 0; i < currentIndexCell.numberOfChildren; i++) {
					if (SpatialHelper.overlapsSpatially(currentIndexCell.children[i].boundsNoBoundaries, rectangle)) {
						queue.add(currentIndexCell.children[i]);
					}
				}
			else if (currentIndexCell.dataObjectCount > 0)
				relevantCells.add(currentIndexCell);
		}
		return relevantCells;

	}

	
	public ArrayList<QuadTreeIndexCell> getOverlappingIndexCellWithData(Rectangle rectangle, ArrayList<String> keywords) {
		ArrayList<QuadTreeIndexCell> relevantCells = new ArrayList<QuadTreeIndexCell>();
		QuadTreeIndexCell currentIndexCell = root;
		Queue<QuadTreeIndexCell> queue = new LinkedList<QuadTreeIndexCell>();
		if (SpatialHelper.overlapsSpatially(currentIndexCell.boundsNoBoundaries, rectangle))
			queue.add(currentIndexCell);
		while (!queue.isEmpty()) {
			currentIndexCell = queue.remove();
			if (currentIndexCell.children != null)
				for (int i = 0; i < currentIndexCell.numberOfChildren; i++) {
					if (SpatialHelper.overlapsSpatially(currentIndexCell.children[i].boundsNoBoundaries, rectangle)) {
						queue.add(currentIndexCell.children[i]);
					}
				}
			else if (currentIndexCell.dataObjectCount > 0 && TextHelpers.overlapsTextually(currentIndexCell.getAllDataTextInCell(), keywords))
				relevantCells.add(currentIndexCell);
		}
		return relevantCells;
	}

	
	public HashMap<String, Query> getReleventQueries(DataObject dataObject, Boolean fromNeighbour) {
		//this hashmap is based on query source id , query id itself
		HashMap<String, Query> queriesMap = new HashMap<String, Query>();

		//first consider all global KNN queries 
		for (Query q : globalKNNQueries) {
			String unqQueryId = q.getUniqueIDFromQuerySourceAndQueryId();
			if (!queriesMap.containsKey(unqQueryId))
				queriesMap.put(unqQueryId, q);
		}
		ArrayList<QuadTreeIndexCell> relevantIndexCells;
		if (fromNeighbour) {
			relevantIndexCells = getOverlappingIndexCells(dataObject.getRelevantArea());
			for (QuadTreeIndexCell QuadTreeIndexCell : relevantIndexCells) {
				if (QuadTreeIndexCell == null)
					continue;
				List<Query> queries = QuadTreeIndexCell.getQueries();
				for (Query q : queries) {
					String unqQueryId = q.getUniqueIDFromQuerySourceAndQueryId();
					if (!queriesMap.containsKey(unqQueryId))
						queriesMap.put(unqQueryId, q);
				}
			}
		} else {
			QuadTreeIndexCell QuadTreeIndexCell = getOverlappingIndexCells(dataObject.getLocation());
			//for (QuadTreeIndexCell QuadTreeIndexCell : relevantIndexCells) {
			if (QuadTreeIndexCell != null && QuadTreeIndexCell.getQueries() != null) {

				List<Query> queries = QuadTreeIndexCell.getQueries();
				for (Query q : queries) {
					String unqQueryId = q.getUniqueIDFromQuerySourceAndQueryId();
					if (!queriesMap.containsKey(unqQueryId))
						queriesMap.put(unqQueryId, q);
				}
			}
			//}
		}

		return queriesMap;
	}

	
	public LocalIndexKNNIterator KNNIterator(Point focalPoint, Double distance) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public LocalIndexKNNIterator LocalKNNIterator(Point focalPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public QuadTreeIndexCell mapDataObjectToIndexCell(DataObject dataObject) {
		return getOverlappingIndexCells(dataObject.getLocation());
	}

	
	public Boolean updateContinousQuery(Query oldQuery, Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public ArrayList< List<Query>> getReleventSpatialKeywordRangeQueries(DataObject dataObject, Boolean fromNeighbour) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Set<String> getUpdatedTextSummery() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

}
