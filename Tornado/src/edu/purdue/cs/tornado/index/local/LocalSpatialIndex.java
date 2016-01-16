package edu.purdue.cs.tornado.index.local;

import java.util.ArrayList;
import java.util.HashMap;

import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public abstract class LocalSpatialIndex {
	
	
	public abstract IndexCell addDataObject(DataObject dataObject) ;
	public abstract IndexCell mapDataObjectToIndexCell(DataObject dataObject) ;
	public abstract Boolean addContinousQuery(Query query) ;
	public abstract Boolean updateContinousQuery(Query oldQuery, Query query) ;
	public abstract HashMap<String, Query> getReleventQueries(DataObject dataObject, Boolean fromNeighbour);
	public abstract ArrayList<IndexCell> getOverlappingIndexCells(Rectangle rectangle) ;
	public abstract ArrayList<IndexCell> getOverlappingIndexCells(Point point);
	public abstract Boolean dropContinousQuery(Query query) ;
	public abstract Integer getCountPerRec(Rectangle rec) ;
	public abstract LocalIndexKNNIterator LocalKNNIterator(Point focalPoint) ;
	public abstract LocalIndexKNNIterator KNNIterator(Point focalPoint, Double distance) ;

}
