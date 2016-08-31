package edu.purdue.cs.tornado.index.local;

import java.util.ArrayList;
import  java.util.List;
import java.util.Map;
import java.util.Set;

import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public abstract class LocalHybridIndex {
	public Long beginCleanUpTime;
	public LocalHybridIndex(){}
	public abstract Boolean addContinousQuery(Query query) ;
	public abstract IndexCell addDataObject(DataObject dataObject) ;
	public abstract Boolean dropContinousQuery(Query query) ;
	public abstract Integer getCountPerKeywrodsAll(ArrayList<String> keywords);
	public abstract Integer getCountPerKeywrodsAll(ArrayList<String> keywords, Rectangle rect) ;
	public abstract Integer getCountPerKeywrodsAny(ArrayList<String> keywords);
	public abstract Integer getCountPerKeywrodsAny(ArrayList<String> keywords, Rectangle rect) ;
	public abstract Integer getCountPerRec(Rectangle rec) ;
	public abstract IndexCell getOverlappingIndexCells(Point point);
	public abstract ArrayList<IndexCell> getOverlappingIndexCells(Rectangle rectangle) ;
	public abstract ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle) ;
	public abstract ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle,ArrayList<String> keywords) ;
	public abstract ArrayList<IndexCell> getOverlappingIndexCellWithData(ArrayList<String> keywords) ;
	public abstract Map<String, Query> getReleventQueries(DataObject dataObject, Boolean fromNeighbour);
	public abstract ArrayList<List<Query>> getReleventSpatialKeywordRangeQueries(DataObject dataObject, Boolean fromNeighbour);
	public abstract LocalIndexKNNIterator KNNIterator(Point focalPoint, Double distance) ;
	public abstract LocalIndexKNNIterator LocalKNNIterator(Point focalPoint) ;
	public abstract IndexCell mapDataObjectToIndexCell(DataObject dataObject) ;
	public abstract ArrayList<IndexCell> getIndexCellsFromPartition(Cell partition) ;
	public abstract Boolean updateContinousQuery(Query oldQuery, Query query) ;
	public abstract Set<String> getUpdatedTextSummery() ;
	public abstract void cleanUp() ;
	public abstract IndexCellCoordinates mapDataPointToPartition(Point point) ;
	public abstract IndexCell getIndexCellFromCoordinates(IndexCellCoordinates indexCell);

}
