package edu.purdue.cs.tornado.index.local;

import java.util.ArrayList;
import java.util.HashMap;

import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class LocalSpatialGridIndex extends LocalSpatialIndex {

	@Override
	public IndexCell addDataObject(DataObject dataObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IndexCell mapDataObjectToIndexCell(DataObject dataObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean addContinousQuery(Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean updateContinousQuery(Query oldQuery, Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Query> getReleventQueries(DataObject dataObject, Boolean fromNeighbour) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCells(Rectangle rectangle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCells(Point point) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean dropContinousQuery(Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getCountPerRec(Rectangle rec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalIndexKNNIterator LocalKNNIterator(Point focalPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalIndexKNNIterator KNNIterator(Point focalPoint, Double distance) {
		// TODO Auto-generated method stub
		return null;
	}

}
