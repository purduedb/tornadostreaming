package edu.purdue.cs.tornado.index.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

//import ch.qos.logback.classic.Level;
import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class LocalQuadTree extends LocalHybridIndex {

	IndexCell root;
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
		root = new IndexCell(selfBounds, spatialOnlyFlag, 0);
		this.spatialOnlyFlag = spatialOnlyFlag;
		this.globalKNNQueries = new ArrayList<Query>();

	}

	@Override
	public Boolean addContinousQuery(Query query) {
		if (query.getQueryType().equals(SpatioTextualConstants.queryTextualKNN)) {

			// initially assume that the incomming KKN query for a
			// volatile data object will go to global data entry for this bolt
			if (dataSourcesInformation.isVolatile()) {
				query.resetKNNStructures();
				globalKNNQueries.add(query);
			}
		}

		ArrayList<IndexCell> relevantCells = mapQueryToPartitions(query);
		for (IndexCell indexCell : relevantCells) {

			indexCell.addQuery(query);
			if (SpatioTextualConstants.queryTextualRange.equals(query.getQueryType()) || SpatioTextualConstants.queryTextualSpatialJoin.equals(query.getQueryType())) {
				if (SpatialHelper.insideSpatially(query.getSpatialRange(), indexCell.bounds))
					indexCell.coverQueryCount++;
			}
			if (indexCell.storedQueries.size() >= splitThreshold && indexCell.storedQueries.size() > indexCell.coverQueryCount*2 && indexCell.level < this.maxLevel) {
				splitCell(indexCell);
			}
		}
		return true;
	}

	private ArrayList<IndexCell> mapQueryToPartitions(Query query) {
		ArrayList<IndexCell> relevantCells = new ArrayList<IndexCell>();
		double xmin, ymin, xmax, ymax;
		if (SpatioTextualConstants.queryTextualRange.equals(query.getQueryType()) || SpatioTextualConstants.queryTextualSpatialJoin.equals(query.getQueryType())) {
			if (query.getSpatialRange().getMin().getX() > selfBounds.getMax().getX() || query.getSpatialRange().getMin().getY() > selfBounds.getMax().getY() || query.getSpatialRange().getMax().getX() < selfBounds.getMin().getX()
					|| query.getSpatialRange().getMax().getY() < selfBounds.getMin().getY()) {
				System.err.println("Error query:" + query.getSrcId() + "  is outside the range of this bolt ");
			} else {
				relevantCells = getOverlappingIndexCells(query.getSpatialRange());
			}
		} else if (SpatioTextualConstants.queryTextualKNN.equals(query.getQueryType())) {

			relevantCells.add(getOverlappingIndexCells(query.getFocalPoint()));
		}
		return relevantCells;
	}

	@Override
	public IndexCell addDataObject(DataObject dataObject) {
		IndexCell currentIndexCell = root;
		IndexCell retunIndexCell = null;
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

	private void splitCell(IndexCell currentIndexCell) {
		currentIndexCell.children = new IndexCell[4];
		double xrange = currentIndexCell.getBounds().getMax().getX() - currentIndexCell.getBounds().getMin().getX();
		double yrange = currentIndexCell.getBounds().getMax().getY() - currentIndexCell.getBounds().getMin().getY();
		double xmin = currentIndexCell.getBounds().getMin().getX();
		double ymin = currentIndexCell.getBounds().getMin().getY();
		currentIndexCell.spatialOnlyFlag = true;
		Rectangle bounds1 = new Rectangle(currentIndexCell.getBounds().getMin(), new Point(xmin + xrange / 2, ymin + yrange / 2));
		Rectangle bounds2 = new Rectangle(new Point(xmin + xrange / 2, ymin), new Point(xmin + xrange, ymin + yrange / 2));
		Rectangle bounds3 = new Rectangle(new Point(xmin, ymin + yrange / 2), new Point(xmin + xrange / 2, ymin + yrange));
		Rectangle bounds4 = new Rectangle(new Point(xmin + xrange / 2, ymin + yrange / 2), new Point(xmin + xrange, ymin + yrange));
		currentIndexCell.children[0] = new IndexCell(bounds1, spatialOnlyFlag, currentIndexCell.level + 1);
		currentIndexCell.children[1] = new IndexCell(bounds2, spatialOnlyFlag, currentIndexCell.level + 1);
		currentIndexCell.children[2] = new IndexCell(bounds3, spatialOnlyFlag, currentIndexCell.level + 1);
		currentIndexCell.children[3] = new IndexCell(bounds4, spatialOnlyFlag, currentIndexCell.level + 1);
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

				if (SpatioTextualConstants.queryTextualSpatialJoin.equals(query.getQueryType()) || SpatioTextualConstants.queryTextualRange.equals(query.getQueryType())) {
					//					if (SpatialHelper.insideSpatially(query.getSpatialRange(), currentIndexCell.boundsNoBoundaries))
					//						currentIndexCell.addQuery(query);
					//					else
					

					for (int i = 0; i < currentIndexCell.numberOfChildren; i++) {
						if (SpatialHelper.overlapsSpatially(query.getSpatialRange(), currentIndexCell.children[i].bounds))
							currentIndexCell.children[i].addQuery(query);
						if (SpatialHelper.insideSpatially(query.getSpatialRange(), currentIndexCell.children[i].bounds))
							currentIndexCell.children[i].coverQueryCount++;
					}
				} else if (SpatioTextualConstants.queryTextualKNN.equals(query.getQueryType())) {
					for (int i = 0; i < currentIndexCell.numberOfChildren; i++) {
						if (SpatialHelper.overlapsSpatially(query.getFocalPoint(), currentIndexCell.children[i].bounds))
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

	@Override
	public Boolean dropContinousQuery(Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords) {
		return getCountPerKeywrodsAll(keywords, selfBounds);
	}

	@Override
	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords, Rectangle rect) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords, Rectangle rect) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getCountPerRec(Rectangle rec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IndexCell getOverlappingIndexCells(Point point) {
		IndexCell currentIndexCell = root;
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

	public ArrayList<IndexCell> getOverlappingIndexCellsIncludingNonLeaf(Point point) {
		ArrayList<IndexCell> relevantCells = new ArrayList<IndexCell>();
		IndexCell currentIndexCell = root;
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

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCells(Rectangle rectangle) {
		ArrayList<IndexCell> relevantCells = new ArrayList<IndexCell>();
		IndexCell currentIndexCell = root;
		Queue<IndexCell> queue = new LinkedList<IndexCell>();
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

	public ArrayList<IndexCell> getOverlappingIndexCellsIncludingNonleaf(Rectangle rectangle) {
		ArrayList<IndexCell> relevantCells = new ArrayList<IndexCell>();
		IndexCell currentIndexCell = root;
		Queue<IndexCell> queue = new LinkedList<IndexCell>();
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

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(ArrayList<String> keywords) {
		return getOverlappingIndexCellWithData(selfBounds, keywords);
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle) {
		ArrayList<IndexCell> relevantCells = new ArrayList<IndexCell>();
		IndexCell currentIndexCell = root;
		Queue<IndexCell> queue = new LinkedList<IndexCell>();
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

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle, ArrayList<String> keywords) {
		ArrayList<IndexCell> relevantCells = new ArrayList<IndexCell>();
		IndexCell currentIndexCell = root;
		Queue<IndexCell> queue = new LinkedList<IndexCell>();
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
				if (indexCell == null)
					continue;
				List<Query> queries = indexCell.getQueries();
				for (Query q : queries) {
					String unqQueryId = q.getUniqueIDFromQuerySourceAndQueryId();
					if (!queriesMap.containsKey(unqQueryId))
						queriesMap.put(unqQueryId, q);
				}
			}
		} else {
			IndexCell indexCell = getOverlappingIndexCells(dataObject.getLocation());
			//for (IndexCell indexCell : relevantIndexCells) {
			if (indexCell != null && indexCell.getQueries() != null) {

				List<Query> queries = indexCell.getQueries();
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

	@Override
	public LocalIndexKNNIterator KNNIterator(Point focalPoint, Double distance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalIndexKNNIterator LocalKNNIterator(Point focalPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IndexCell mapDataObjectToIndexCell(DataObject dataObject) {
		return getOverlappingIndexCells(dataObject.getLocation());
	}

	@Override
	public Boolean updateContinousQuery(Query oldQuery, Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList< List<Query>> getReleventSpatialKeywordRangeQueries(DataObject dataObject, Boolean fromNeighbour) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getUpdatedTextSummery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

}
