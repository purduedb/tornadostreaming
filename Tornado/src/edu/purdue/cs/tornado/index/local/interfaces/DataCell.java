package edu.purdue.cs.tornado.index.local.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import com.sun.tools.javac.util.List;

import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.messages.DataObject;

public interface DataCell {
	public List<DataObject> getStoredObjects();
	public List<DataObject> getStoredObjects(Rectangle range, ArrayList<String >keywords,TextualPredicate predicate);
	public  void setStoredObjects(List<DataObject> objects);
	public Integer getDataObjectCount();
	public DataObject dropDataObject(Integer id);
	public HashMap<String,Integer> getAllDataTextInCell();
	public DataObject getDataObject(Integer id);
	public void addDataObject(DataObject id);
	public int estimateDataObjectCountAll(ArrayList<String>keywords);
}
