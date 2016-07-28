package edu.purdue.cs.tornado.messages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

import edu.purdue.cs.tornado.helper.Command;
import edu.purdue.cs.tornado.helper.DataObjectKNNComparator;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.QueryType;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.index.global.GlobalIndexIterator;
import edu.purdue.cs.tornado.index.local.LocalIndexKNNIterator;

public class JoinQuery extends Query {
	private  String srcId;
	private  Integer queryId;
	public Boolean added;
	public Integer visitied;
		
	private  Point focalPoint;
	private  Integer  k;
	private  ArrayList<String>  queryText;
	private  ArrayList<String>  queryText2;
	private  Long    timeStamp;
	private  Rectangle spatialRange;
	private  String dataSrc;
	private  String dataSrc2; 
	private  Long removeTime;  
	
	private  Command command; //add , Update , Drop 
	private  TextualPredicate textualPredicate;
	private  TextualPredicate textualPredicate2;
	private  TextualPredicate joinTextualPredicate;
		
	private  Double distance ;
	private  Boolean continousQuery;
		
	private PriorityQueue<DataObject> topKQueue;  // Priority queue (max-heap).
	private HashMap<Integer, Integer> currentRanks;  // Records the current rank of each object in the top-k list.
	private LocalIndexKNNIterator localTopKIterator;
	private GlobalIndexIterator globalTopKIterator;
	private ArrayList<Integer> pendingTopKTaskIds ;
	private Double farthestDistance;
	private HashMap<Integer, DataObject> currentObjects;
	
	
	
	public Long getRemoveTime() {
		return removeTime;
	}

	public void setRemoveTime(Long removeTime) {
		this.removeTime = removeTime;
	}
	private static Double maxFarthestDistance=Math.sqrt(SpatioTextualConstants.xMaxRange*SpatioTextualConstants.xMaxRange+
			SpatioTextualConstants.yMaxRange*SpatioTextualConstants.yMaxRange);//this is the maximum possible space between any two points indexed
	
	public void setFarthestDistance(Double farthestDistance) {
		this.farthestDistance = farthestDistance;
	}
	
	public TextualPredicate getJoinTextualPredicate() {
		return joinTextualPredicate;
	}

	public void setJoinTextualPredicate(TextualPredicate joinTextualPredicate) {
		this.joinTextualPredicate = joinTextualPredicate;
	}

	public TextualPredicate getTextualPredicate() {
		return textualPredicate;
	}
	
	public TextualPredicate getTextualPredicate2() {
		return textualPredicate2;
	}
	public void setTextualPredicate2(TextualPredicate textualPredicate2) {
		this.textualPredicate2 = textualPredicate2;
	}
	public void setTextualPredicate(TextualPredicate textualPredicate) {
		this.textualPredicate = textualPredicate;
	}
	public Boolean getContinousQuery() {
		return continousQuery;
	}
	public void setContinousQuery(Boolean continousQuery) {
		this.continousQuery = continousQuery;
	}
	public GlobalIndexIterator getGlobalKNNIterator() {
		return globalTopKIterator;
	}
	public void setGlobalKNNIterator(GlobalIndexIterator globalKNNIterator) {
		this.globalTopKIterator = globalKNNIterator;
	}
	public LocalIndexKNNIterator getLocalKnnIterator() {
		return localTopKIterator;
	}
	public void setLocalKnnIterator(LocalIndexKNNIterator localKNNIterator ) {
		this.localTopKIterator = localKNNIterator;
	}
	public JoinQuery (){
		added=false;
		visitied = 0;
		focalPoint = new Point();
		queryText = new ArrayList<String>();
		spatialRange = new Rectangle(new Point(), new Point());
		this.farthestDistance = maxFarthestDistance;
		removeTime = null;
		resetKNNStructures();
	}
	public JoinQuery (JoinQuery q){
		added=false;
		visitied = 0;
		this.farthestDistance = maxFarthestDistance;
		this.queryId=q.queryId;
		this.setCommand(q.command);
		this.srcId=q.srcId;
		this.setContinousQuery(q.continousQuery);
		this.setDataSrc(q.dataSrc);
		this.setDistance(q.distance);
		this.setQueryType(q.queryType);
		this.setTimeStamp(q.timeStamp);
		this.setSpatialRange(new Rectangle(q.spatialRange.getMin(),q.spatialRange.getMax()));
		this.setTextualPredicate(q.textualPredicate);
		this.setQueryText(q.queryText);
				
		if (queryType.equals(QueryType.queryTextualKNN)) {
			q.setFocalPoint(q.focalPoint);
			q.setK(k);
		} 
		
	}
	public void resetKNNStructures() {
		Comparator<DataObject> dataObjectKNNComparator = new DataObjectKNNComparator(this.focalPoint);
		this.topKQueue = new PriorityQueue<DataObject>(50, dataObjectKNNComparator);
		this.currentRanks = new HashMap<Integer, Integer>();
		this.currentObjects = new HashMap<Integer, DataObject>();
		
	}
	
	public Double getDistance() {
		return distance;
	}


	public void setDistance(Double distance) {
		this.distance = distance;
	}


	public ArrayList<String> getQueryText2() {
		return queryText2;
	}


	public void setQueryText2(ArrayList<String> queryText2) {
		this.queryText2 = queryText2;
	}


	
	public Integer getQueryId() {
		return queryId;
	}
	public void setQueryId(Integer queryId) {
		this.queryId = queryId;
	}
	
	
	public Integer getK() {
		return k;
	}
	public void setK(Integer k) {
		this.k = k;
	}
	public ArrayList<String>  getQueryText() {
		return queryText;
	}
	public void setQueryText(ArrayList<String>  queryText) {
		this.queryText = queryText;
	}
	public String getSrcId() {
		return srcId;
	}
	public void setSrcId(String srcId) {
		this.srcId = srcId;
	}
	public Long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public Point getFocalPoint() {
		return focalPoint;
	}
	public void setFocalPoint(Point focalPoint) {
		this.focalPoint = focalPoint;
	}
	public Rectangle getSpatialRange() {
		return spatialRange;
	}
	public void setSpatialRange(Rectangle spatialRange) {
		this.spatialRange = spatialRange;
	}


	public String getDataSrc() {
		return dataSrc;
	}


	public void setDataSrc(String dataSrc) {
		this.dataSrc = dataSrc;
	}


	public String getDataSrc2() {
		return dataSrc2;
	}


	public void setDataSrc2(String dataSrc2) {
		this.dataSrc2 = dataSrc2;
	}


	public Command getCommand() {
		return command;
	}


	public void setCommand(Command command) {
		this.command = command;
	}
	
	@Override
	public String toString(){
		String output = "Query[: "+(getQueryId()==null?"":getQueryId())
				+" , "+ "Source: "+(getSrcId()==null?"":getSrcId())
				+" , "+ "Type: "+(getQueryType()==null?"":getQueryType())
				+" , "+ "Text: "+(getQueryText()==null?"":getQueryText().toString())
				+" , "+ "focal: "+(getFocalPoint()==null?"":getFocalPoint().toString())
				+" , "+ "range: "+(getSpatialRange()==null?"":getSpatialRange().toString())
				+" , "+ "distance: "+(getDistance()==null?"":getDistance())
				+" , "+ "K: "+(getK()==null?"":getK())				
				+"]";
		return output;
	}
	
	
	
	public static String getUniqueIDFromQuerySourceAndQueryId(String querySourceId, Integer queryId)
	{
		return querySourceId+SpatioTextualConstants.queryIdDelimiter+queryId;
	}
	public String getUniqueIDFromQuerySourceAndQueryId(){
		return getUniqueIDFromQuerySourceAndQueryId(this.getSrcId(), this.getQueryId());
		
	}
	public static String getSrcIdFromUniqueQuerySrcQueryId(String src_query_id)
	{
		return src_query_id.split(SpatioTextualConstants.queryIdDelimiter)[0];
	}
	public static String getQueryIdFromUniqueQuerySrcQueryId(String src_query_id)
	{
		return src_query_id.split(SpatioTextualConstants.queryIdDelimiter)[1];
	}
	// Returns a representation of the changes in the top-k list (if any).
	//the incoming object must satisfy the KNN query textual predicate criteria
	//TODO address the nature of volatile, current object, 
	//TODO this code needs a lot of refactoring
	
	public synchronized ArrayList<ResultSetChange> processDataObject(DataObject dataObject) {
		ArrayList<ResultSetChange> changes = new ArrayList<>();
		boolean textualPredicateMatched = TextHelpers.evaluateTextualPredicate(dataObject.getObjectText(),queryText, textualPredicate);
		boolean topkMayHaveChanged = false;
		// If the new location update corresponds to an object that is already in the top-k list.
		if (this.currentRanks.containsKey(dataObject.getObjectId())) {
			topkMayHaveChanged = true;
			DataObject toBeUpdatedInHeap = null;
			// Locate that object in the priority queue.
			//Only one instance of the same object can exist at a time 
			Iterator<DataObject> it = topKQueue.iterator();
			while (it.hasNext()) {
				DataObject o = it.next();
				if (o.getObjectId().equals(dataObject.getObjectId())) {
					toBeUpdatedInHeap = o;
				}
			}
		
			// Heapify.
			
			ResultSetChange resultSetChange=null;
			if(Command.dropCommand.equals( dataObject.getCommand())||Command.updateDropCommand.equals( dataObject.getCommand())){
				//we need to check that the object to be removed from the heap is due to an authentic object in the heap 
				//of a time stamp equal to the objec to be removed
				//or if we are make a remove of an object at a later time stamp
				if((toBeUpdatedInHeap.getLocation().equals(dataObject.getLocation())&&dataObject.getTimeStamp().equals(toBeUpdatedInHeap.getTimeStamp()))||
						dataObject.getTimeStamp()>=toBeUpdatedInHeap.getTimeStamp()	){//most likely this condition should not happen
					topKQueue.remove(toBeUpdatedInHeap);
				}
			}
			//what about other objects that may be inside the query area this means the area of stored objects need to extended to include the
			else if((Command.updateCommand.equals( dataObject.getCommand()))&&textualPredicateMatched){
				if(	dataObject.getTimeStamp()>=toBeUpdatedInHeap.getTimeStamp()	){
					topKQueue.remove(toBeUpdatedInHeap);
					topKQueue.add(dataObject);
			  }
				
			}
		} else if (textualPredicateMatched&&(dataObject.getCommand().equals(Command.updateCommand)||dataObject.getCommand().equals(Command.addCommand))){
			// If the current list is small, i.e., has less than k objects, take that object anyway and add it to the topk list.
			
			if (currentRanks.size() < this.k) {
				topkMayHaveChanged = true;
				this.topKQueue.add(dataObject);			
			} else {
				// Calculate the distance corresponding to new location.
				double distanceOfObject =   SpatialHelper.getDistanceInBetween(dataObject.getLocation(), this.focalPoint)  ;
						  								   
				// New location is closer than the current farthest.
				if (this.getFarthestDistance() > distanceOfObject) {
					topkMayHaveChanged = true;
					// Remove the farthest.
					DataObject toRemove= this.topKQueue.remove();
					this.topKQueue.add(dataObject);
					
				}
			}
		}
		if(topkMayHaveChanged)
			changes=getTopkRanks();
		return changes;
	}
	//TODO the farthest distance may need to be extended to support the updates of object going out of 
	public Double getFarthestDistance() {
		return this.farthestDistance;
				
	}
	public Integer getKNNlistSize() {
		return topKQueue.size();
				
	}
	private ArrayList<ResultSetChange>  getTopkRanks() {
		// Calculate the new rank of each object in the top-k list.
		ArrayList<ResultSetChange> changes = new ArrayList<ResultSetChange>();
		HashMap<Integer, Integer> newRanks = new HashMap<Integer, Integer>();
		Comparator<DataObject> maxHeap = new DataObjectKNNComparator(this.focalPoint);
		PriorityQueue<DataObject> temp = new PriorityQueue<DataObject>(50, maxHeap);
		HashMap<Integer, DataObject> newCurrenObjects = new HashMap<Integer, DataObject>();
		int rank = 1;	
		while (!this.topKQueue.isEmpty()) {
			DataObject l = this.topKQueue.remove();
			temp.add(l);
			newRanks.put(l.getObjectId(), rank);
			rank++;
			newCurrenObjects.put(l.getObjectId(),new DataObject( l));
		}
		for (Integer objectId : newRanks.keySet()) {
			if (!this.currentRanks.containsKey(objectId)) {
				changes.add(new ResultSetChange(newCurrenObjects.get(objectId), Command.addCommand, this));
			} else if (!this.currentObjects.get(objectId).equalsLocation( newCurrenObjects.get(objectId))) {
				
				changes.add(new ResultSetChange(newCurrenObjects.get(objectId),Command.updateCommand, this));
			}
		}
		for (Integer objectId : this.currentRanks.keySet()) {
			if (!newRanks.containsKey(objectId)) {
				changes.add(new ResultSetChange(currentObjects.get(objectId),Command.dropCommand, this));
			}
		}
		this.topKQueue = temp;
		// Finally, update the current ranks to reflect the new ranks.
		this.currentRanks = newRanks;
		this.currentObjects = newCurrenObjects;
		calcFarthestDisatance();
		return changes;
	}
	private void calcFarthestDisatance(){
		DataObject farthest = topKQueue.peek();
		if(farthest!=null)
			this.farthestDistance= SpatialHelper.getDistanceInBetween(farthest.getLocation(), this.focalPoint);
		else 
			this.farthestDistance= maxFarthestDistance;
	}
	public ArrayList<DataObject> getKNNList(){
		ArrayList<DataObject> KNNlist = new ArrayList<>();
		Iterator <DataObject> it = topKQueue.iterator();
		while(it.hasNext()){
			KNNlist.add(it.next());
		}
		return KNNlist;
	}
	public ArrayList<Integer> getPendingKNNTaskIds() {
		return pendingTopKTaskIds;
	}
	public void setPendingKNNTaskIds(ArrayList<Integer> pendingKNNTaskIds) {
		this.pendingTopKTaskIds = pendingKNNTaskIds;
	}
	
	
}

