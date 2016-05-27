package edu.purdue.cs.tornado.atlas.SGB.group;

import java.util.ArrayList;
import java.util.HashMap;

import edu.purdue.cs.tornado.atlas.SGB.global.Tuple;
import edu.purdue.cs.tornado.atlas.SGB.index.Rectangle;
import edu.purdue.cs.tornado.atlas.SGB.index.rtree.RTree;
import edu.purdue.cs.tornado.atlas.SGB.index.SpatialIndex;



public class RtreeGroups implements Groups {

	ArrayList <Group>groupsStorage;
	SpatialIndex GIndex;
	
	/**
	 * this is used to store the overlap tuples which are removed from eliminate operation
	 */
	private HashMap<Integer, Tuple> tmpFile;
	
	public double eps;
	private int count;
	private int DistanceMetric;
	
	public RtreeGroups()
	{
		this.count=0;
		GIndex = new RTree();
		GIndex.init(null);
		this.groupsStorage=new ArrayList<Group>();
	}

	@Override
	public void addgroup(Group g) {
		// TODO Auto-generated method stub
		this.groupsStorage.add(g);
		Rectangle groupdRect= g.getRectangle();
		GIndex.add(groupdRect, g.getGroupid());
		this.incrementCount();
	}

	@Override
	public void deletegroup(Group g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deletefromgroup(int gindex, Group g, Tuple t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTupleTogroup(int gindex, Group g, Tuple t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void outputgroups() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEPS(float eps) {
		// TODO Auto-generated method stub
		this.eps=eps;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.count;
	}

	@Override
	public void incrementCount() {
		// TODO Auto-generated method stub
		this.count++;
		
	}

	@Override
	public void setDistanceMetric(int s) {
		// TODO Auto-generated method stub
		this.DistanceMetric=s;
		
	}

	@Override
	public int getDistanceMetric() {
		// TODO Auto-generated method stub
		return this.DistanceMetric;
	}

	@Override
	public Object getStorage() {
		// TODO Auto-generated method stub
		return this.groupsStorage;
	}

	public HashMap<Integer, Tuple> getTmpFile() {
		
		if(tmpFile==null||tmpFile.isEmpty())
			tmpFile=new HashMap<Integer, Tuple>();
		
		return tmpFile;
	}


	public void setTmpFile(HashMap<Integer, Tuple> tmpFile) {
		this.tmpFile = tmpFile;
	}

	@Override
	public void resetStorage() {
		// TODO Auto-generated method stub
		this.groupsStorage=null;
		this.groupsStorage=new ArrayList<Group>();
		count=0;
		
		this.GIndex=null;
		this.GIndex=new RTree();
		GIndex.init(null);
	}

	public SpatialIndex getIndex()
	{
		return this.GIndex;
	}

	@Override
	public Object getSIndex() {
		// TODO Auto-generated method stub
		return this.GIndex;
	}
	
	public String toString()
	{
		StringBuffer s= new  StringBuffer();
		
		for(Group g:this.groupsStorage)
		{
			String tmp=g.toString();
			if(tmp!=null)
				s.append(tmp+"\n");
		}
		
		return s.toString();
	}
	
	
	
}
