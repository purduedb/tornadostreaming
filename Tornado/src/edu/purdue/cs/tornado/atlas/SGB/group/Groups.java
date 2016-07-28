package edu.purdue.cs.tornado.atlas.SGB.group;

import java.util.ArrayList;
import java.util.HashMap;

import edu.purdue.cs.tornado.atlas.SGB.global.Tuple;


/**
 * this is the in memory storage to store the exist groups
 * @author tang49
 *
 */
public interface Groups {

	
	/**
	 * @param t
	 * @param Fileds
	 * @return find the intersection between the tuple with the possible groups
	 */
	//public ArrayList intersection(Tuple t, int []Fileds, int overlapType);
	
	
	public Object getStorage();
	
	public void resetStorage();
	/**
	 * @param g
	 * add one group into the index for the groups 
	 */
	public void addgroup(Group g);
	
	/**
	 * @param g
	 * delete one group from the index
	 */
	public void deletegroup(Group g);
	
	/**
	 * 
	 * @param gindex
	 * @param g
	 * @param t
	 */
	public void deletefromgroup(int gindex, Group g, Tuple t);
	public void addTupleTogroup(int gindex, Group g, Tuple t);
	
	/**
	 * @output the groups
	 */
	public void outputgroups();
	
	public void setEPS(float eps);
	
	public int getCount();
	
	public void incrementCount();
	
	public void setDistanceMetric(int s);
	
	public int getDistanceMetric();
	
	public HashMap<Integer, Tuple> getTmpFile();
	
	public void setTmpFile(HashMap<Integer, Tuple> tmpFile);
	
	public Object getSIndex();
	public void markCandidateGroup(int groupId);
	public ArrayList<Group> getCandidateGroups();
	
}
