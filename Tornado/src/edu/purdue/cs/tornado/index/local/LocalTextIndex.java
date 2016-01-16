package edu.purdue.cs.tornado.index.local;

import java.util.ArrayList;
import java.util.Iterator;

import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public abstract class LocalTextIndex {
	public abstract Boolean dropDataObject(DataObject dataObject);
	public abstract void addDataObject(DataObject obj);
	public abstract void addDataObject(ArrayList<String> keywords, DataObject obj);
	public abstract void addContinousQuery(ArrayList<String> toListen, Query query);
	public abstract Integer estimateDataObjectCountAll(ArrayList<String> keywords);
	public abstract Integer estimateDataObjectCountAny(ArrayList<String> keywords);
	public abstract Iterator<DataObject> getDataObjectsContainAny(ArrayList<String> keywords);
	public abstract Iterator<DataObject> getDataObjectsContainAll(ArrayList<String> keywords);
}
