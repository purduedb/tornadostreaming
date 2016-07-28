package edu.purdue.cs.tornado.atlas.SGB.group;

import java.util.ArrayList;
import java.util.HashMap;

import edu.purdue.cs.tornado.atlas.SGB.global.AttrType;
import edu.purdue.cs.tornado.atlas.SGB.global.Distance;
import edu.purdue.cs.tornado.atlas.SGB.global.L2distance;
import edu.purdue.cs.tornado.atlas.SGB.global.LinfinityDistance;
import edu.purdue.cs.tornado.atlas.SGB.global.Tuple;

/**
 * we use the arraylist to store the generated groups and tuples
 * @author tang49
 *
 */
public class ArrayListGroups implements Groups {

	ArrayList <Group>groupsStorage;
	
	/**
	 * this is used to store the overlap tuples which are removed from eliminate operation
	 */
	private HashMap<Integer, Tuple> tmpFile;
	
	private static float EPS;
	private int count;
	private int DistanceMetric;
	
	
	
	public ArrayListGroups() {
		// TODO Auto-generated constructor stub
		this.groupsStorage=new ArrayList<Group>();
		count=0;
	}

	
	/**
	 * @return
	 * find those groups which have intersection with Tuple T. 
	 */
	public ArrayList intersection(Tuple t, int []Fileds)
	{
		int metric=this.getDistanceMetric();
		ArrayList <Group>tmp= new ArrayList<Group>();
		
		Distance d=null;
		
		if(metric==AttrType.L2)
		{
			d=new L2distance();
		}else
		{
			d=new LinfinityDistance();
		}
		
		for(Group g:this.groupsStorage)
		{
			Tuple center=g.getCenter();
			double distance=d.getDistance(metric, center, t, Fileds);
			
			//this is very importance point here
			if(distance<=2*this.EPS)
			{
				tmp.add(g);
			}
		}
		
		return tmp;
	
	}
	
	/**
	 * @param g
	 */
	public void addgroup(Group g)
	{
		this.groupsStorage.add(g);
		this.incrementCount();
	}
	
	/**
	 * @param g
	 */
	public void deletegroup(Group g)
	{
		this.groupsStorage.remove(g);
		
	}
	
	/**
	 * 
	 * @param gindex
	 * @param g
	 * @param t
	 */
	public void deletefromgroup(int gindex, Group g, Tuple t)
	{
	}
	
	/**
	 * @
	 */
	public void addTupleTogroup(int gindex, Group g, Tuple t)
	{
		
	}

	@Override
	public void outputgroups() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEPS(float eps) {
		// TODO Auto-generated method stub
		this.EPS=eps;
		
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

	@Override
	public Object getStorage() {
		// TODO Auto-generated method stub
		return this.groupsStorage;
	}
	
	/**
	 * reset the group list into a null, and build new one
	 */
	public void resetStorage()
	{
		this.groupsStorage=null;
		this.groupsStorage=new ArrayList<Group>();
		count=0;
		
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
	public Object getSIndex() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void markCandidateGroup(int groupId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public ArrayList<Group> getCandidateGroups() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
