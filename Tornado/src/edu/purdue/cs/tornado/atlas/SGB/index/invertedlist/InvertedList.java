package edu.purdue.cs.tornado.atlas.SGB.index.invertedlist;

import java.util.ArrayList;
import java.util.HashMap;

import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;
import edu.stanford.nlp.patterns.Data;

public class InvertedList {
	public HashMap<String, ArrayList<DataObject>> invertedList;

	public InvertedList() {
		invertedList = new HashMap<String, ArrayList<DataObject>>();
	}

	public void addItem(DataObject obj) {
		for (String keyword : obj.getObjectText()) {
			if (!invertedList.containsKey(keyword))
				invertedList.put(keyword, new ArrayList<DataObject>());
			invertedList.get(keyword).add(obj);
		}
	}

	public ArrayList<DataObject> getItems(ArrayList<String> keywords) {
		ArrayList<DataObject> result = new ArrayList<DataObject>();
		for (String keyword : keywords) {
			ArrayList<DataObject> dataObjects = invertedList.get(keyword);
			if (dataObjects != null)
				for (DataObject obj : dataObjects)
					if(obj.added==false){
						obj.added=true;
						result.add(obj);
					}
					
		}

		for(DataObject obj:result)
			obj.added = false;
		return result;
	}

	public void dropItem(DataObject obj) {

		//TODO implement this 
	}
}
