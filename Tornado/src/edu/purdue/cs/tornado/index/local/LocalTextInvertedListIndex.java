package edu.purdue.cs.tornado.index.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class LocalTextInvertedListIndex extends LocalTextIndex{

	HashMap<String, ArrayList<DataObject>> dataObjectsIndex;
	HashMap<String, ArrayList<Query>> queriesIndex;

	public LocalTextInvertedListIndex() {
		dataObjectsIndex = new HashMap<String, ArrayList<DataObject>>();
		queriesIndex = new HashMap<String, ArrayList<Query>>();
	}
	public Boolean dropDataObject(DataObject dataObject){
		Boolean removed = false;
		for (String text : dataObject.getObjectText()) {
			ArrayList<DataObject> invertedObjectList = dataObjectsIndex.get(text);
			Integer i = 0;
			Boolean found = false;
			for (DataObject obj : invertedObjectList) {
				if (obj.equals(dataObject)) {
					removed=true;
					found = true;
					break;
				}
				i++;
			}
			if (found)
				invertedObjectList.remove(i);
		}
		return removed;
	}
	public void addDataObject(DataObject obj) {
		for (String keyword : obj.getObjectText()) {
			if (!dataObjectsIndex.containsKey(keyword))
				dataObjectsIndex.put(keyword, new ArrayList<DataObject>());
			dataObjectsIndex.get(keyword).add(obj);
		}
	}
	public void addDataObject(ArrayList<String> keywords,DataObject obj) {
		for (String keyword : keywords) {
			if (!dataObjectsIndex.containsKey(keyword))
				dataObjectsIndex.put(keyword, new ArrayList<DataObject>());
			dataObjectsIndex.get(keyword).add(obj);
		}
	}

	public void addContinousQuery(ArrayList<String> toListen, Query query) {
		for (String keyword : toListen) {
			if (!queriesIndex.containsKey(keyword))
				queriesIndex.put(keyword, new ArrayList<Query>());
			queriesIndex.get(keyword).add(query);
		}
	}

	/**
	 * 
	 * Estimate how many data objects contain ALL of the input keywords this is
	 * estimated as the minimum number of objects per keyword.
	 * 
	 * @param keywords
	 */
	public Integer estimateDataObjectCountAll(ArrayList<String> keywords) {
		Integer min = Integer.MAX_VALUE;
		for (String keyword : keywords) {
			if (!dataObjectsIndex.containsKey(keyword)) {
				min = 0;
				break;
			} else if (dataObjectsIndex.get(keyword).size() < min)
				min = dataObjectsIndex.get(keyword).size();
		}
		return min;
	}

	/**
	 * 
	 * Estimate how many data objects contain Any of the input keywords this is
	 * estimated as the sum of objects per keyword.
	 * 
	 * @param keywords
	 */
	public Integer estimateDataObjectCountAny(ArrayList<String> keywords) {

		Integer sum = 0;
		for (String keyword : keywords) {
			if (!dataObjectsIndex.containsKey(keyword)) {
				continue;
			}
			sum += dataObjectsIndex.get(keyword).size();
		}
		return sum;
	}

	public Iterator<DataObject> getDataObjectsContainAny(ArrayList<String> keywords) {
		HashMap<Integer, DataObject> result = new HashMap<Integer, DataObject>();
		Integer sum = 0;
		for (String keyword : keywords) {
			if (!dataObjectsIndex.containsKey(keyword)) {
				continue;
			}
			for (DataObject obj : dataObjectsIndex.get(keyword))
				if (!result.containsKey(obj.getObjectId()))
					result.put(obj.getObjectId(), obj);
		}
		return result.values().iterator();
	}

	public Iterator<DataObject> getDataObjectsContainAll(ArrayList<String> keywords) {
		HashMap<Integer, DataObject> result = new HashMap<Integer, DataObject>();
		Integer sum = 0;
		for (String keyword : keywords) {
			if (!dataObjectsIndex.containsKey(keyword)) {
				continue;
			}
			for (DataObject obj : dataObjectsIndex.get(keyword)) {
				if (TextHelpers.containsTextually(obj.getObjectText(), keywords) && !result.containsKey(obj.getObjectId()))
					result.put(obj.getObjectId(), obj);
			}
		}
		return result.values().iterator();
	}
}
