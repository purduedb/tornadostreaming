package edu.purdue.cs.tornado.index.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.omg.CORBA.Bounds;

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
import edu.stanford.nlp.util.Index;

/**
 * This index is implemented as a multi level grid similar to a quad tree the
 * height of the tree is the log4 the height leaf level has all objects leaf
 * level ->height 0; root level -> height ;
 * 
 * @author ahmed
 *
 */
public class LocalHybridMultiGridIndex extends LocalHybridIndex {
	private Integer localXcellCount;
	private Integer localYcellCount;
	private Double localXstep;
	private Double localYstep;
	private Integer height;
	//private LocalHybridGridIndex[] index;
	private LocalHybridGridIndex[] index;
	private Rectangle selfBounds;
	DataSourceInformation dataSourcesInformation;
	private ArrayList<Query> globalKNNQueries;
	private Integer xGridGranularity;
	private Integer yGridGranularity;
	private Boolean spatialOnlyFlag;

	public LocalHybridMultiGridIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation) {
		this(selfBounds, dataSourcesInformation, SpatioTextualConstants.fineGridGranularityX, SpatioTextualConstants.fineGridGranularityY, false);
	}

	public LocalHybridMultiGridIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer xGridGranularity, Integer yGridGranularity) {
		this(selfBounds, dataSourcesInformation, xGridGranularity, yGridGranularity, false);
	}

	public LocalHybridMultiGridIndex(Rectangle selfBounds, DataSourceInformation dataSourcesInformation, Integer xGridGranularity, Integer yGridGranularity, Boolean spatialOnlyFlag) {
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

		height = (int) Math.ceil(Math.log(localXcellCount * localYcellCount) / Math.log(4));

		int xgranularity = xGridGranularity;
		int ygranularity = yGridGranularity;
		index = new LocalHybridGridIndex[2 * height];
		globalKNNQueries = new ArrayList<Query>();
		for (int k = 0; k <= height; k++) {
			LocalHybridGridIndex levelGridIndex;
			if (k == 0)
				levelGridIndex = new LocalHybridGridIndex(selfBounds, dataSourcesInformation, xgranularity, ygranularity, spatialOnlyFlag, k);
			else{
				levelGridIndex = new LocalHybridGridIndex(selfBounds, dataSourcesInformation, xgranularity, ygranularity, true, k);}
//				ArrayList<IndexCell> parentCells =levelGridIndex .getOverlappingIndexCells(selfBounds);
//				for(IndexCell parentCell:parentCells){
//					parentCell.children= new IndexCell[4];
//					ArrayList<IndexCell> childCells = index[k-1].getOverlappingIndexCells(parentCell.boundsNoBoundaries);
//					int j=0;
//					for(IndexCell childCell:childCells){
//						parentCell.children[j++]=childCell;
//						childCell.parent=parentCell;
//						
//					}
//				}
//			}
			xgranularity = xgranularity / 2;
			ygranularity = ygranularity / 2;
			index[k] = levelGridIndex;

		}

	}

	public Boolean addContinousQuery(Query query) {

		return index[0].addContinousQuery(query);
	}

	public IndexCell addDataObject(DataObject dataObject) {
		IndexCell indexCell= index[0].addDataObject(dataObject);
		if(height!=0)
		while(indexCell.parent==null&&indexCell.level<height){
			IndexCell parentCell = index[indexCell.level+1].getIndexCellCreateIfNull(dataObject.getLocation(),true);
			parentCell.children[parentCell.numberOfChildren++]=indexCell;
			indexCell.parent=parentCell;
			parentCell.dataObjectCount++;
			indexCell=parentCell;
		}
		while(indexCell.level!=height&&indexCell.parent.dataObjectCount==0){
			indexCell=indexCell.parent;
			indexCell.dataObjectCount++;
		}
			
		return indexCell;
	}

	public Boolean dropContinousQuery(Query query) {
		return index[0].dropContinousQuery(query);
	}

	@Override
	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords) {
		return getCountPerKeywrodsAll(keywords, selfBounds);
	}

	@Override
	public Integer getCountPerKeywrodsAll(ArrayList<String> keywords, Rectangle rect) {
		Integer count = 0;
		for (int k = height; k >= 0; k++) {
			count = index[k].getCountPerKeywrodsAll(keywords, rect);
			if (count == 0)
				return count;
		}
		return count;
	}

	@Override
	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords) {
		return getCountPerKeywrodsAny(keywords, selfBounds);
	}

	@Override
	public Integer getCountPerKeywrodsAny(ArrayList<String> keywords, Rectangle rect) {
		Integer count = 0;
		for (int k = height; k >= 0; k++) {
			count = index[k].getCountPerKeywrodsAny(keywords, rect);
			if (count == 0)
				return count;
		}
		return count;
	}

	public Integer getCountPerRec(Rectangle rec) {
		Integer count = 0;
		for (int k = height; k >= 0; k++) {
			count = index[k].getCountPerRec(rec);
			if (count == 0)
				return count;
		}
		return count;

	}

	public LocalHybridGridIndex[] getIndex() {
		return index;
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
		return index[0].getOverlappingIndexCells(point);
	}

	public ArrayList<IndexCell> getOverlappingIndexCells(Rectangle rectangle) {
		return index[0].getOverlappingIndexCells(rectangle);

	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(ArrayList<String> keywords) {
		return getOverlappingIndexCellWithData(selfBounds, keywords);
	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle) {
		Queue<IndexCell> queue = new LinkedList<IndexCell>();
		ArrayList<IndexCell> relevantIndexCells = new ArrayList<>();
		Integer level = getStartingLevel(rectangle);
		ArrayList<IndexCell> indexCells = index[level].getOverlappingIndexCellWithData(selfBounds);
		for(IndexCell indexCell:indexCells){
			if(indexCell!=null&&indexCell.dataObjectCount>0)
			queue.add(indexCell);
		}
		while (!queue.isEmpty()) {
			IndexCell indexCell = queue.remove();
			if (indexCell.getLevel() > 1) {
				for(int i=0;i<indexCell.numberOfChildren;i++){
					if(indexCell.children[i]!=null&&indexCell.children[i].dataObjectCount>0&&SpatialHelper.overlapsSpatially(indexCell.children[i].boundsNoBoundaries, rectangle))
						queue.add(indexCell.children[i]);
				}
				//queue.addAll(index[indexCell.getLevel() - 1].getOverlappingIndexCellWithData(SpatialHelper.shrink(indexCell.boundsNoBoundaries, rectangle)));
			} else if (indexCell.getLevel() == 1) {
				
				//relevantIndexCells.addAll(index[indexCell.getLevel() - 1].getOverlappingIndexCellWithData(SpatialHelper.shrink(indexCell.boundsNoBoundaries, rectangle)));
			} else {
				relevantIndexCells.add(indexCell);
			}

		}
		return relevantIndexCells;

	}

	@Override
	public ArrayList<IndexCell> getOverlappingIndexCellWithData(Rectangle rectangle, ArrayList<String> keywords) {
		if (spatialOnlyFlag)
			return getOverlappingIndexCellWithData(rectangle);
		Queue<IndexCell> queue = new LinkedList<IndexCell>();
		ArrayList<IndexCell> relevantIndexCells = new ArrayList<>();
		Integer level = getStartingLevel(rectangle);
		ArrayList<IndexCell> indexCells = index[level].getOverlappingIndexCellWithData(rectangle, keywords);
		
		for(IndexCell indexCell:indexCells){
			if(indexCell!=null&&indexCell.dataObjectCount>0)
			queue.add(indexCell);
		}
		while (!queue.isEmpty()) {
			IndexCell indexCell = queue.remove();
			if (indexCell.getLevel() > 1) {
				for(int i=0;i<indexCell.numberOfChildren;i++){
					if(indexCell.children[i]!=null&&indexCell.children[i].dataObjectCount>0&&SpatialHelper.overlapsSpatially(indexCell.children[i].boundsNoBoundaries, rectangle))
						queue.add(indexCell.children[i]);
				}
				//queue.addAll(index[indexCell.getLevel() - 1].getOverlappingIndexCellWithData(SpatialHelper.shrink(indexCell.boundsNoBoundaries, rectangle), keywords));
			} else if (indexCell.getLevel() == 1) {
				for(int i=0;i<indexCell.numberOfChildren;i++){
				if(indexCell.children[i].dataObjectCount>0&&SpatialHelper.overlapsSpatially(indexCell.children[i].boundsNoBoundaries, rectangle)&&TextHelpers.overlapsTextually(indexCell.children[i].allDataTextInCell, keywords))
					relevantIndexCells.add(indexCell.children[i]);
				}
				//relevantIndexCells.addAll(index[indexCell.getLevel() - 1].getOverlappingIndexCellWithData(SpatialHelper.shrink(indexCell.boundsNoBoundaries, rectangle), keywords));
			} else {
				relevantIndexCells.add(indexCell);
			}

		}
		return relevantIndexCells ;
	}

	public HashMap<String, Query> getReleventQueries(DataObject dataObject, Boolean fromNeighbour) {

		return index[0].getReleventQueries(dataObject, fromNeighbour);
	}

	public Rectangle getSelfBounds() {
		return selfBounds;
	}

	private Integer getStartingLevel(Rectangle rec) {
		Double maxSideLength = Math.max(rec.getMax().getX() - rec.getMin().getX(), rec.getMax().getY() - rec.getMin().getY());
		//Double cellSideLength = SpatioTextualConstants.xMaxRange / this.xGridGranularity;
		Integer numberOfCells =  (int) Math.ceil((double)maxSideLength / localXstep);
		Integer level = (int) Math.ceil(Math.log(numberOfCells) / Math.log(2));
		return level;

	}

	@Override
	public LocalIndexKNNIterator KNNIterator(Point focalPoint, Double distance) {
		return null;
	}

	@Override
	public LocalIndexKNNIterator LocalKNNIterator(Point focalPoint) {
		return null;
	}

	public IndexCell mapDataObjectToIndexCell(DataObject dataObject) {
		return index[0].mapDataObjectToIndexCell(dataObject);
	}

	private IndexCellCoordinates mapDataPointToPartition(Point point) {
		return index[0].mapDataPointToPartition(point);
	}

	public void setIndex(LocalHybridGridIndex[] index) {
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
