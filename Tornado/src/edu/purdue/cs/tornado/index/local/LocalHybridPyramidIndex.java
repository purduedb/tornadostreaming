package edu.purdue.cs.tornado.index.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.index.DataSourceInformation;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class LocalHybridPyramidIndex extends LocalHybridIndex{

	private Integer localXcellCount;
	private Integer localYcellCount;
	private Double localXstep;
	private Double localYstep;
	private Rectangle selfBounds;
	DataSourceInformation dataSourcesInformation;
	private ArrayList<Query> globalKNNQueries;
	private Integer xGridGranularity;
	private Integer yGridGranularity;
	private Boolean spatialOnlyFlag;
	private Integer cellThreshold=200;
	ArrayList<ArrayList<IndexCell>> index;
	private Integer allDataCount;
	Integer level;
	
	public LocalHybridPyramidIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation) {
		this(selfBounds, dataSourcesInformation, SpatioTextualConstants.fineGridGranularityX, SpatioTextualConstants.fineGridGranularityY, false);
	}

	public LocalHybridPyramidIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer xGridGranularity, Integer yGridGranularity) {
		this(selfBounds, dataSourcesInformation, xGridGranularity, yGridGranularity, false);
	}

	public LocalHybridPyramidIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer xGridGranularity, Integer yGridGranularity, Boolean spatialOnlyFlag) {
		super();
		this.spatialOnlyFlag = spatialOnlyFlag;
		this.dataSourcesInformation = dataSourcesInformation;
		this.selfBounds = selfBounds;
		Double globalXrange = SpatioTextualConstants.xMaxRange;
		Double globalYrange = SpatioTextualConstants.yMaxRange;

		this.xGridGranularity = xGridGranularity;
		this.yGridGranularity = yGridGranularity;

		localXstep = (globalXrange / this.xGridGranularity);
		localYstep = (globalYrange / this.yGridGranularity);
		localXcellCount = (int) ((selfBounds.getMax().getX() - selfBounds.getMin().getX()) / localXstep);
		localYcellCount = (int) ((selfBounds.getMax().getY() - selfBounds.getMin().getY()) / localYstep);


		int xgranularity = xGridGranularity;
		int ygranularity = yGridGranularity;
		globalKNNQueries = new ArrayList<Query>();
		for (int i = 0; i < localXcellCount; i++) {
			ArrayList<IndexCell> ycellsList = new ArrayList<IndexCell>();
			for (int j = 0; j < localYcellCount; j++) {
				Rectangle bounds = new Rectangle(new Point(i * localXstep + selfBounds.getMin().getX(), j * localYstep + selfBounds.getMin().getY()),
						new Point((i + 1) * localXstep + selfBounds.getMin().getX(), (j + 1) * localYstep + selfBounds.getMin().getY()));
				ycellsList.add(new IndexCell(bounds, spatialOnlyFlag, 0));
			}
			index.add(ycellsList);
		}
	}

	public Boolean addContinousQuery(Query query) {
		if (query.getQueryType().equals(SpatioTextualConstants.queryTextualKNN)) {
			// initially assume that the incomming KKN query for a
			// volatile data object will go to global data entry for this bolt
			if (dataSourcesInformation.isVolatile()) {
				query.resetKNNStructures();
				globalKNNQueries.add(query);
			}
		}

		ArrayList<IndexCellCoordinates> indexCells = mapQueryToPartitions(query);
		for (IndexCellCoordinates indexCell : indexCells) {
			index.get(indexCell.getX()).get(indexCell.getY()).addQuery(query);

		}
		return true;
	}

	public IndexCell addDataObject(DataObject dataObject) {
		IndexCellCoordinates cellCoordinates = mapDataPointToPartition(dataObject.getLocation());
		IndexCell indexCell = index.get(cellCoordinates.getX()).get(cellCoordinates.getY());
		allDataCount++;
		return indexCell.addDataObjectRecusrive(dataObject);
	}

	
	public Boolean dropContinousQuery(Query query) {
		//first check inside the globalNKK list and if found return 
		if (query.getQueryType().equals(SpatioTextualConstants.queryTextualKNN)) {
			int i = 0;
			boolean found = false;
			for (i = 0; i < globalKNNQueries.size(); i++) {
				if (query.getSrcId().equals(globalKNNQueries.get(i).getSrcId()) && query.getQueryId().equals(globalKNNQueries.get(i).getQueryId())) {
					found = true;
					break;
				}
			}
			if (found) {
				globalKNNQueries.remove(i);
				return true;
			}
		}
		ArrayList<IndexCellCoordinates> indexCells = mapQueryToPartitions(query);
		for (IndexCellCoordinates indexCell : indexCells) {
			index.get(indexCell.getX()).get(indexCell.getY()).dropQuery(query);

		}
		return true;
	}

	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords) {
		if (spatialOnlyFlag)
			return allDataCount;
		Integer sum = 0;
		ArrayList<IndexCellCoordinates> indexCells = mapRecToIndexCells(selfBounds);
		for (IndexCellCoordinates indexCoordinate : indexCells) {

			sum += getIndexCellFromCoordinates(indexCoordinate).estimateDataObjectCountAll(keywords);

		}
		return sum;
	}

	@Override
	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords, Rectangle rect) {
		Integer sum = 0;
		ArrayList<IndexCellCoordinates> indexCells = mapRecToIndexCells(rect);
		for (IndexCellCoordinates indexCoordinate : indexCells)
			sum += getIndexCellFromCoordinates(indexCoordinate).estimateDataObjectCountAll(keywords);
		return sum;
	}

	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords) {
		if (spatialOnlyFlag)
			return allDataCount;
		Integer sum = 0;
		ArrayList<IndexCellCoordinates> indexCells = mapRecToIndexCells(selfBounds);
		for (IndexCellCoordinates indexCoordinate : indexCells) {

			sum += getIndexCellFromCoordinates(indexCoordinate).estimateDataObjectCountAny(keywords);

		}
		return sum;
	}

	@Override
	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords, Rectangle rect) {
		Integer sum = 0;
		ArrayList<IndexCellCoordinates> indexCells = mapRecToIndexCells(rect);
		for (IndexCellCoordinates indexCoordinate : indexCells)
			sum += getIndexCellFromCoordinates(indexCoordinate).estimateDataObjectCountAny(keywords);
		return sum;
	}

	public Integer getCountPerRec(Rectangle rec) {
		Integer sum = 0;
		ArrayList<IndexCellCoordinates> indexCells = mapRecToIndexCells(rec);
		for (IndexCellCoordinates indexCoordinate : indexCells)
			sum += getIndexCellFromCoordinates(indexCoordinate).getDataObjectCount();
		return sum;

	}

	public ArrayList<ArrayList<IndexCell>> getIndex() {
		return index;
	}

	private IndexCell getIndexCellFromCoordinates(IndexCellCoordinates indexCell) {
		return index.get(indexCell.getX()).get(indexCell.getY());
	}

	public Integer getLocalXcellCount() {
		return localXcellCount;
	}

	public Double getLocalXstep() {
		return localXstep;
	}

	public Integer getLocalYcellCount() {
		return localYcellCount;
	}

	public Double getLocalYstep() {
		return localYstep;
	}

	public IndexCell getOverlappingIndexCells(Point point) {
		IndexCellCoordinates relevantIndexCellCorredinates = mapDataPointToPartition(point);
		return getIndexCellFromCoordinates(relevantIndexCellCorredinates);

	}

	public ArrayList<IndexCell> getOverlappingIndexCells(Rectangle rectangle) {
		ArrayList<IndexCellCoordinates> relevantIndexCellCorredinates = mapRecToIndexCells(rectangle);
		ArrayList<IndexCell> relevantIndexCells = new ArrayList<IndexCell>();
		for (IndexCellCoordinates indexCellCoordinate : relevantIndexCellCorredinates)
			relevantIndexCells.add(index.get(indexCellCoordinate.getX()).get(indexCellCoordinate.getY()));
		return relevantIndexCells;

	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(ArrayList<String> keywords) {
		return getOverlappingIndexCellWithData(selfBounds, keywords);
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle) {
		ArrayList<IndexCellCoordinates> relevantIndexCellCorredinates = mapRecToIndexCells(rectangle);
		ArrayList<IndexCell> relevantIndexCells = new ArrayList<IndexCell>();
		for (IndexCellCoordinates indexCellCoordinate : relevantIndexCellCorredinates) {
			IndexCell indexCell = getIndexCellFromCoordinates(indexCellCoordinate);
			if (indexCell.getDataObjectCount() > 0)
				relevantIndexCells.add(indexCell);
		}
		return relevantIndexCells;
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle, ArrayList<String> keywords) {
		if (spatialOnlyFlag)
			return getOverlappingIndexCellWithData(rectangle);
		else {
			ArrayList<IndexCellCoordinates> relevantIndexCellCorredinates = mapRecToIndexCells(rectangle);
			ArrayList<IndexCell> relevantIndexCells = new ArrayList<IndexCell>();
			for (IndexCellCoordinates indexCellCoordinate : relevantIndexCellCorredinates) {
				IndexCell indexCell = getIndexCellFromCoordinates(indexCellCoordinate);
				if (indexCell.getDataObjectCount() > 0 && TextHelpers.overlapsTextually(indexCell.getAllDataTextInCell(), keywords))
					relevantIndexCells.add(indexCell);
			}
			return relevantIndexCells;
		}

	}

	/**
	 * This function retrtives all relevant queries that maybe affected by the
	 * addition of a new
	 * 
	 * @param dataObject
	 * @param fromNeighbour
	 * @return
	 */
	public HashMap<String, Query> getReleventQueries(DataObject dataObject, Boolean fromNeighbour) {

		//this hashmap is based on query source id , query id itself
				HashMap<String, Query> queriesMap = new HashMap<String, Query>();
				
		//first consider all global KNN queries 
				for (Query q : globalKNNQueries) {
					String unqQueryId = q.getUniqueIDFromQuerySourceAndQueryId();
					if (!queriesMap.containsKey(unqQueryId))
						queriesMap.put(unqQueryId, q);
				}
		ArrayList<IndexCell> relevantIndexCells ;
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

	public Rectangle getSelfBounds() {
		return selfBounds;
	}

	public LocalKNNGridIndexIterator KNNIterator(Point focalPoint, Double distance) {
		return null;//new LocalKNNGridIndexIterator(this, focalPoint, distance);
	}

	public LocalKNNGridIndexIterator LocalKNNIterator(Point focalPoint) {
		return null;// new LocalKNNGridIndexIterator(this, focalPoint);
	}

	public IndexCell mapDataObjectToIndexCell(DataObject dataObject) {
		IndexCellCoordinates cellCoordinates = mapDataPointToPartition(dataObject.getLocation());
		IndexCell indexCell = index.get(cellCoordinates.getX()).get(cellCoordinates.getY());
		return indexCell;
	}

	/**
	 * This function maps incoming data point to a grid cell inside the current
	 * evaluator bolt
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public IndexCellCoordinates mapDataPointToPartition(Point point) {
		Double x = point.getX();
		Double y = point.getY();

		//		if ((x < selfBounds.getMin().getX() && !(Math.abs(x - selfBounds.getMin().getX()) < .000001)) || (y < selfBounds.getMin().getY() && !(Math.abs(y - selfBounds.getMin().getY()) < .000001))
		//				|| (x > selfBounds.getMax().getX() && !(Math.abs(x - selfBounds.getMax().getX()) < .000001)) || (y > selfBounds.getMax().getY() && !(Math.abs(y - selfBounds.getMax().getY()) < .000001))) {
		//			System.err.println("Error Point: " + x + " , " + y + " is outside the range of this bolt ");
		//		} else {
		Integer xCell = (int) ((x - selfBounds.getMin().getX()) / localXstep);
		Integer yCell = (int) ((y - selfBounds.getMin().getY()) / localYstep);
		if (xCell >= localXcellCount)
			xCell = localXcellCount - 1;
		if (yCell >= localYcellCount)
			yCell = localYcellCount - 1;

		IndexCellCoordinates indexCellCoordinate = new IndexCellCoordinates(xCell, yCell);

		//		}
		return indexCellCoordinate;
	}

	/**
	 * This function maps a query to a set of index cells that overlap the
	 * query's range check if the query does not overlap with the bolts
	 * rectangular bounds maps the query to the bounds of the current bolt if it
	 * exceeds it
	 * 
	 * @param query
	 * @return
	 */
	private ArrayList<IndexCellCoordinates> mapQueryToPartitions(Query query) {
		ArrayList<IndexCellCoordinates> partitions = new ArrayList<IndexCellCoordinates>();
		double xmin, ymin, xmax, ymax;
		if (SpatioTextualConstants.queryTextualRange.equals(query.getQueryType()) || SpatioTextualConstants.queryTextualSpatialJoin.equals(query.getQueryType())) {
			if (query.getSpatialRange().getMin().getX() > selfBounds.getMax().getX() || query.getSpatialRange().getMin().getY() > selfBounds.getMax().getY() || query.getSpatialRange().getMax().getX() < selfBounds.getMin().getX()
					|| query.getSpatialRange().getMax().getY() < selfBounds.getMin().getY()) {
				System.err.println("Error query:" + query.getSrcId() + "  is outside the range of this bolt ");
			} else {
				partitions = mapRecToIndexCells(query.getSpatialRange());
			}
		} else if (SpatioTextualConstants.queryTextualKNN.equals(query.getQueryType())) {
			partitions.add(mapDataPointToPartition(query.getFocalPoint()));
		}
		return partitions;
	}

	private ArrayList<IndexCellCoordinates> mapRecToIndexCells(Rectangle rectangle) {
		ArrayList<IndexCellCoordinates> partitions = new ArrayList<IndexCellCoordinates>();
		double xmin, ymin, xmax, ymax;
		
		if (rectangle == null || selfBounds == null)
			return partitions;
		
		if (rectangle.getMin().getX() < selfBounds.getMin().getX())
			xmin = selfBounds.getMin().getX();
		else
			xmin = rectangle.getMin().getX();
		
		if (rectangle.getMin().getY() < selfBounds.getMin().getY())
			ymin = selfBounds.getMin().getY();
		else
			ymin = rectangle.getMin().getY();
		
		if (rectangle.getMax().getX() > selfBounds.getMax().getX())
			xmax = selfBounds.getMax().getX();//to prevent exceeding index range
		else
			xmax = rectangle.getMax().getX();
		
		if (rectangle.getMax().getY() > selfBounds.getMax().getY())
			ymax = selfBounds.getMax().getY();//to prevent exceeding index range
		else
			ymax = rectangle.getMax().getY();
		
		if (xmax == selfBounds.getMax().getX())
			xmax = selfBounds.getMax().getX() - 1;
		if (ymax == selfBounds.getMax().getY())
			ymax = selfBounds.getMax().getY() - 1;

		xmin -= selfBounds.getMin().getX();
		ymin -= selfBounds.getMin().getY();
		xmax -= selfBounds.getMin().getX();
		ymax -= selfBounds.getMin().getY();

		int xMinCell = (int) (xmin / localXstep);
		int yMinCell = (int) (ymin / localYstep);
		int xMaxCell = (int) (xmax / localXstep);
		int yMaxCell = (int) (ymax / localYstep);

		for (Integer xCell = xMinCell; xCell <= xMaxCell; xCell++)
			for (Integer yCell = yMinCell; yCell <= yMaxCell; yCell++) {
				IndexCellCoordinates indexCell = new IndexCellCoordinates(xCell, yCell);
				partitions.add(indexCell);
			}

		return partitions;
	}

	public void setIndex(ArrayList<ArrayList<IndexCell>> index) {
		this.index = index;
	}

	public void setLocalXcellCount(Integer localXcellCount) {
		this.localXcellCount = localXcellCount;
	}

	public void setLocalXstep(Double localXstep) {
		this.localXstep = localXstep;
	}

	public void setLocalYcellCount(Integer localYcellCount) {
		this.localYcellCount = localYcellCount;
	}

	public void setLocalYstep(Double localYstep) {
		this.localYstep = localYstep;
	}

	public void setSelfBounds(Rectangle selfBounds) {
		this.selfBounds = selfBounds;
	}

	//TODO we need to find a better way to update a query 
	public Boolean updateContinousQuery(Query oldQuery, Query query) {
		dropContinousQuery(oldQuery);
		addContinousQuery(query);
		return true;
	}

}
