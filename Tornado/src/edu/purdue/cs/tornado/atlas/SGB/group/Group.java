package edu.purdue.cs.tornado.atlas.SGB.group;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.purdue.cs.tornado.atlas.SGB.global.AttrType;
import edu.purdue.cs.tornado.atlas.SGB.global.Convexhull2D;
import edu.purdue.cs.tornado.atlas.SGB.global.Distance;
import edu.purdue.cs.tornado.atlas.SGB.global.L2distance;
import edu.purdue.cs.tornado.atlas.SGB.global.LinfinityDistance;
import edu.purdue.cs.tornado.atlas.SGB.global.Tuple;
import edu.purdue.cs.tornado.atlas.SGB.index.Rectangle;

public class Group {

	/*
	 * use to store the tuples inside this group
	 */
	private HashMap<Integer, Tuple> g;
	
	private int groupid;
	
	/**the center point of one group*/
	Tuple center;
	
	/**the filed that is used to judge distance*/
	private int []FildeList;
	
	private float [][] groupRectanglebound;
	
	private float [][] groupMaxMinbound;
	
	
	public int getGroupid() {
		return groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	private Convexhull2D groupConvexhullbound;
	/**distance method to compute the tuple difference*/
	private int distanceMetric;
	
	/**parameter for the distance*/
	private float EPS;
	
	private int convexhullJudgeresult;
	/**update center label, if true, once add new tuples, we need to update the center*/
	//boolean update_center=true;
	
	/**update the group rectanlge bounds when adding new points*/
	//boolean update_bound=true;
	
	public Group(int id) {
		// TODO Auto-generated constructor stub
		this.g=new HashMap<Integer, Tuple>();
		this.groupid=id;
	}
	
	/**
	 * construction function for the group
	 * @param id
	 * @param update_center
	 * @param update_bound
	 * @param distanceMetric
	 * @param filedlist
	 */
	public Group(int id, 
		    int distanceMetric,
			int []filedlist,
			float eps
			)
	{
		this.g=new HashMap<Integer, Tuple>();
		this.groupid=id;
		this.setDistanceMetric(distanceMetric);
		//this.setUpdateCenter(update_center)	
		this.setFildeList(filedlist);
		float [][]groupsbounds=new float[filedlist.length][2];
		float [][]maxmin=new float[filedlist.length][2];
		this.setGroupRectanglebound(groupsbounds);
		this.setGroupMaxMinbound(maxmin);
		this.setEPS(eps);
		
		if(this.getDistanceMetric()==AttrType.L2)
		{
			this.groupConvexhullbound= new Convexhull2D();
			this.groupConvexhullbound.setFileds(filedlist);
		}
		
		convexhullJudgeresult=-1;
	}
	
	
	public Tuple getCenter() {
		return center;
	}

	public void setCenter(Tuple center) {
		this.center = center;
	}
	
	/**
	 * @update the center point of bounds
	 */
	public void updateCenter(Tuple T)
	{
		if(this.getCenter()==null)
		{
			this.center=new Tuple(T.getSchema());
			
			for(int i:this.getFildeList())
			{
				float f1=Float.valueOf(T.getFiled(i).toString());
				this.center.setFiled(i, f1);
			}
			return;
		}
		
		for(int i:this.getFildeList())
		{
			float f1=Float.valueOf(T.getFiled(i).toString());
			float f2=Float.valueOf(this.center.getFiled(i).toString());
			
			/**get the sum of previous tuples in one dimension*/
			f2=f2*this.g.size();
			
			/**update the sum*/
			f2=f1+f2;
			
			/**get average*/
			
			f2=f2/(this.g.size()+1);
			
			/**update the group center*/
			this.center.setFiled(i, f2);
			
		}
	}
	/***************************************************/
	
	/***************************************************/
	public int[] getFildeList() {
		return FildeList;
	}

	public void setFildeList(int[] fildeList) {
		FildeList = fildeList;
	}

	public int getDistanceMetric() {
		return distanceMetric;
	}

	public void setDistanceMetric(int distanceMetric) {
		this.distanceMetric = distanceMetric;
	}


	/***************************************************/
	/**
	 * delete one tuple from the tuple list
	 * @param t
	 */
	public void delete(Tuple t)
	{
		this.g.remove(t);
	}
	
	/**
	 * add a tuple into the group
	 * @param t
	 */
	public void addExecuteAgg(Tuple t)
	{
		
		int id=(Integer) t.getFiled(0);
		
		if(!g.containsKey(id))
		{
			/**update the group when add new points*/
			//this.updateGroup(t);
			g.put(id, t);
		
		}
		
		//execute the agg function
		
	}
	
	
	/*****************************************************/
	/**
	 * @param t
	 * @return
	 * if the tuple can join one group, return true, or else return false
	 * this function works for linfinity and l2 distance metric
	 */
	public boolean judgeJoinGroup(
			Tuple t, 
			int[] fileds)
	{
		boolean inside=false;
		
		/*
		if(this.g.size()==0)
		{
			//check the rectangle bound is null or not. 
			int i=0;
			for(int index:fileds)
			{
				//float f1=Float.valueOf(t.getFiled(index).toString());
				float left=this.getGroupRectanglebound()[i][0];
				float right=this.getGroupRectanglebound()[i][1];
				
				if(right!=0||left!=0)
				{
					return false;
				}
				
				i++;
			}
			return true;
		}*/
		
		//method 2: check the rectangle bounds to join this group
		if(this.distanceMetric==AttrType.Linfinity)
		{
			inside= checkRectangleboundJoin(t,fileds);
			
		}else if(this.distanceMetric==AttrType.L2)
		{
			inside= checkRectangleboundJoin(t,fileds);
			
			if(inside)
			{
				inside=convexthullJudgeJoin(t,fileds);
			}
		}
		
		return inside;
		
	}
	
	/**
	 * judge whether it is possible that new tuple can have overlap with current group
	 * @param t
	 * @param fileds
	 * @return
	 */
	public boolean judgeOverlap(
			Tuple t, 
			int[] fileds)
	{
		
		boolean overlap=false;
		
		if(this.g.size()==0)
		{
			return true;
		}
		
		//method 2: check the rectangle bounds to join this group
		if(this.distanceMetric==AttrType.Linfinity)
		{
			overlap= checkRectanglebound_Overlap(t,fileds);
			
		}else if(this.distanceMetric==AttrType.L2)
		{
			overlap= checkRectanglebound_Overlap(t,fileds);
			
			if(overlap)
			{
				overlap=checkConvexthull_Overlap(t,fileds);
			}
		}
		
		return overlap;
		
		
		//return false;
	}
	
	/**
	 * use the convexhull to judge whether there is overlap
	 * @param t
	 * @param fileds
	 * @return
	 */
	private boolean checkConvexthull_Overlap(Tuple t, int[] fileds) {
		// TODO Auto-generated method stub
		
		boolean overlap=false;
		int judgeresult=-1;
		
		if(this.convexhullJudgeresult==-1)
		{
			judgeresult=this.groupConvexhullbound.insideConvexAndInsideBound(t, this.getEPS());
		}
		
		if(this.convexhullJudgeresult==3||judgeresult==3)
		{
			overlap= true;
			
		}else
		{
			overlap= false;
		}
		
		//this.convexhullJudgeresult=-1;
		
		return overlap;
	}

	/**
	 * use the rectangle to judge whether there is overlap with the input tuple T
	 * @param t
	 * @param fileds
	 * @return
	 */
	private boolean checkRectanglebound_Overlap(Tuple t, int[] fileds) {
		// TODO Auto-generated method stub
		int i=0;
		for(int index:fileds)
		{
			float f1=Float.valueOf(t.getFiled(index).toString());
			float left=this.getGroupRectanglebound()[i][0];
			float right=this.getGroupRectanglebound()[i][1];
			
			if(f1<=right||f1>=left)
			{
				return true;
			}
			
			i++;
		}
		
		return false;
	}

	/**
	 * update the rectangle bounds when new points were added 
	 * @param T
	 */
	public void updateGroupRectangleBound(Tuple t,  int[] fileds)
	{
		
		//update the group bounds 
		if(this.g.size()==0)
		{
			int i=0;
			for(int index:fileds)
			{
				float f1=Float.valueOf(t.getFiled(index).toString());
				this.getGroupRectanglebound()[i][0]=f1-this.EPS;
				this.getGroupRectanglebound()[i][1]=f1+this.EPS;
				i++;
			}

		}else
		{
			int i=0;
			for(int index:fileds)
			{
				float f1=Float.valueOf(t.getFiled(index).toString());
				float left=this.getGroupRectanglebound()[i][0];
				float right=this.getGroupRectanglebound()[i][1];
				
				if(Math.abs(left-right)<=this.EPS)
				{
					return;
				}
				
				//update the left pivot
				if(f1-this.getEPS()>left)
				{
					this.getGroupRectanglebound()[i][0]=f1-this.EPS;
				}
				
				//update the right pivot
				if(f1+this.getEPS()<right)
				{
					this.getGroupRectanglebound()[i][1]=f1+this.EPS;
				}
				i++;
			}
		}
		
	}
	
	/**
	 * update the min max bounds when new points were added 
	 * @param T
	 */
	public void updateGroupMaxiMinBound(Tuple t,  int[] fileds)
	{
		
		//update the group bounds 
		if(this.g.size()==0)
		{
			int i=0;
			for(int index:fileds)
			{
				float f1=Float.valueOf(t.getFiled(index).toString());
				this.getGroupMaxMinbound()[i][0]=f1;
				this.getGroupMaxMinbound()[i][1]=f1;
				i++;
			}

		}else
		{
			int i=0;
			for(int index:fileds)
			{
				float f1=Float.valueOf(t.getFiled(index).toString());
				float left=this.getGroupMaxMinbound()[i][0];
				float right=this.getGroupMaxMinbound()[i][1];
				
				/*if(Math.abs(left-right)<=this.EPS)
				{
					return;
				}*/
				
				//update the left pivot
				if(f1<left)
				{
					this.getGroupMaxMinbound()[i][0]=f1;
				}else if(f1>right)
				{
					this.getGroupMaxMinbound()[i][1]=f1;
				}
				i++;
			}
		}
		
	}
	/**
	 * update the group convexhull group bound
	 * @param t
	 * @param fileds
	 */
	public void updateGroupConvexHullBound(Tuple t,  int[] fileds)
	{
		//System.out.println(this.convexhullJudgeresult);
		
		if(this.convexhullJudgeresult==-1)
		{
			this.convexhullJudgeresult=this.groupConvexhullbound.insideConvexAndInsideBound(t, this.getEPS());
		}
		
		if(this.convexhullJudgeresult==1)
			this.groupConvexhullbound.unionConvex(t, this.getEPS());
		
		this.convexhullJudgeresult=-1;
	}

	/**
	 * 
	 * @param t
	 * @param fileds
	 * @return
	 * checking every points in the group to meet the requirement
	 */
	private boolean convexthullJudgeJoin(Tuple t, int[] fileds) {
		// TODO Auto-generated method stub
		
		int judgeresult=this.groupConvexhullbound.insideConvexAndInsideBound(t, this.getEPS());
		
		convexhullJudgeresult=judgeresult;
		
		if(judgeresult<=1)
		{
			return true;
		}else 
		{
			return false;
		}
		
		//return false;
	}

	/**
	 * 
	 * @param t
	 * @param fileds
	 * @return
	 * method 2:
	 * check the rectangle bounds 
	 * and check the four points 
	 */
	private boolean checkRectangleboundJoin(Tuple t, int[] fileds) {
		// TODO Auto-generated method stub
		
		int i=0;
		for(int index:fileds)
		{
			float f1=Float.valueOf(t.getFiled(index).toString());
			float left=this.getGroupRectanglebound()[i][0];
			float right=this.getGroupRectanglebound()[i][1];
			
			if(f1>right||f1<left)
			{
				return false;
			}
			
			i++;
		}
		
		return true;
	}

	/**
	 * 
	 * @param t
	 * @param fileds
	 * @return
	 * method 3
	 * brute force to check one tuple can join one group methods 
	 */
	private boolean bfJudge(Tuple t, int[] fileds, TreeMap<Integer, Tuple> insidebound) {
		// TODO Auto-generated method stub
		
		int metric=this.getDistanceMetric();
		
		Distance d=null;
		
		if(metric==AttrType.L2)
		{
			d=new L2distance();
		}else
		{
			d=new LinfinityDistance();
		}
		
		Iterator<Entry<Integer, Tuple>> entries = this.g.entrySet().iterator();
		
		boolean door=true;
		
		while (entries.hasNext()) {
		    Map.Entry<Integer, Tuple> entry = entries.next();
		    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		    
		    Tuple t2=entry.getValue();
		    
		    double distance= d.getDistance(this.getDistanceMetric(), t, t2, fileds);
		    
		    if(distance<=this.getEPS())
		    {
		    	//door=true;
		    	//insidebound.add(t2);
		    	insidebound.put((Integer)t2.getFiled(0), t2);
		    	
		    }else
		    {
		    	door=false;
		    }
		}
		
		return door;
	}
	
	/**
	 * find the intersection with this current tuples T for those tuples in the current Group G
	 * we put the intersection tuples into the return list insidebound
	 * @param t
	 * @param insidebound
	 */
	public boolean findIntersection(Tuple t, TreeMap<Integer, Tuple> insidebound, HashMap<Integer, Tuple> globaltmpFile)
	{
		boolean overlap=false;
		
		int metric=this.getDistanceMetric();
		
		Distance d=null;
		
		if(metric==AttrType.L2)
		{
			d=new L2distance();
		}else
		{
			d=new LinfinityDistance();
		}
		
		Iterator<Entry<Integer, Tuple>> entries = this.g.entrySet().iterator();
		
		while (entries.hasNext()) {
		    Map.Entry<Integer, Tuple> entry = entries.next();
		    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		    
		    Tuple t2=entry.getValue();
		    
		    double distance= d.getDistance(this.getDistanceMetric(), t, t2, this.getFildeList());
		    
		    if(distance<=this.getEPS())
		    {
		    	insidebound.put((Integer)t2.getFiled(0), t2);
		    	globaltmpFile.put((Integer)t2.getFiled(0), t2);
		    	overlap=true;
		    	entries.remove();
		    }
		}
		
		return overlap;
	}
	
	
	/**
	 * find the intersection with this current tuples T for those tuples in the current Group G
	 *,
	 *we do not store the intersection into the return list
	 * @param t
	 * @param insidebound
	 */
	public boolean findIntersection(Tuple t, HashMap<Integer, Tuple> globaltmpFile)
	{
		boolean overlap=false;
		
		int metric=this.getDistanceMetric();
		
		Distance d=null;
		
		if(metric==AttrType.L2)
		{
			d=new L2distance();
		}else
		{
			d=new LinfinityDistance();
		}
		
		Iterator<Entry<Integer, Tuple>> entries = this.g.entrySet().iterator();
		
		while (entries.hasNext()) {
		    Map.Entry<Integer, Tuple> entry = entries.next();
		    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		    
		    Tuple t2=entry.getValue();
		    
		    double distance= d.getDistance(this.getDistanceMetric(), t, t2, this.getFildeList());
		    
		    if(distance<=this.getEPS())
		    {
		    	//insidebound.put((Integer)t2.getFiled(0), t2);
		    	globaltmpFile.put((Integer)t2.getFiled(0), t2);
		    	entries.remove();
		    	overlap=true;
		    }
		}
		
		return overlap;
	}
	
	/*****************************************************/
	
	public String toString()
	{
		StringBuffer s= new  StringBuffer();
		
		if(this.g.entrySet().size()==0)
		{
			return null;
		}
			
		s.append("GID:"+this.groupid+"\n");
		
		Iterator<Entry<Integer, Tuple>> entries = this.g.entrySet().iterator();
		while (entries.hasNext()) {
			  Map.Entry<Integer, Tuple> entry = entries.next();
			 Tuple t2=entry.getValue();
			 s.append(t2.toString()+"\t");
		}
	    // System.out.println(this.getGroupMaxMinbound()[0][0]+","+this.getGroupMaxMinbound()[0][1]);
	     //System.out.println(this.getGroupMaxMinbound()[1][0]+","+this.getGroupMaxMinbound()[1][1]);
		return s.toString();
		
	}
	
	public String toTupleString()
	{
		StringBuffer s= new  StringBuffer();
		
		Iterator<Entry<Integer, Tuple>> entries = this.g.entrySet().iterator();
		while (entries.hasNext()) {
			  Map.Entry<Integer, Tuple> entry = entries.next();
			 Tuple t2=entry.getValue();
			 s.append(String.valueOf(t2.getFiled(0))+",");
			 //s.append(t2.toString()+",");
		}
		return s.toString();
		
	}

	public float [][] getGroupRectanglebound() {
		
		return groupRectanglebound;
	}
	
	/**
	 * @return the groupMaxMinbound
	 */
	public float [][] getGroupMaxMinbound() {
		return groupMaxMinbound;
	}

	/**
	 * @param groupMaxMinbound the groupMaxMinbound to set
	 */
	public void setGroupMaxMinbound(float [][] groupMaxMinbound) {
		this.groupMaxMinbound = groupMaxMinbound;
	}

	public  Rectangle getRectangle()
	{
		Rectangle GrecT= new Rectangle(
				this.getGroupRectanglebound()[0][0],
				this.getGroupRectanglebound()[1][0],
				this.getGroupRectanglebound()[0][1], 
				this.getGroupRectanglebound()[1][1]);
		
		return GrecT;
	}

	public void setGroupRectanglebound(float [][] groupRectanglebound) {
		
		this.groupRectanglebound = groupRectanglebound;
	}
	
	/***************************************************/
	public double getEPS() {
		return EPS;
	}


	public void setEPS(float ePS) {
		EPS = ePS;
	}

	public HashMap<Integer, Tuple> getG() {
		return g;
	}

	public void setG(HashMap<Integer, Tuple> g) {
		this.g = g;
	}
	
	public Convexhull2D getGroupConvexhullbound()
	{
		return this.groupConvexhullbound;
	}


}
