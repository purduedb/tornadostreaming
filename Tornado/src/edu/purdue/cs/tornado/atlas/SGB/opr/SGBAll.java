package edu.purdue.cs.tornado.atlas.SGB.opr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.purdue.cs.tornado.atlas.SGB.global.AttrType;
import edu.purdue.cs.tornado.atlas.SGB.global.Tuple;
import edu.purdue.cs.tornado.atlas.SGB.group.ArrayListGroups;
import edu.purdue.cs.tornado.atlas.SGB.group.Group;
import edu.purdue.cs.tornado.atlas.SGB.group.Groups;
import edu.purdue.cs.tornado.atlas.SGB.group.RtreeGroups;

/**
 * the sgb-all operation that iterate the tuples sets
 * 
 * @author tang49
 * 
 */
public class SGBAll extends SGBOpr {

	public String GroupsStorageName;
	Groups Gstorage;

	public SGBAll()
	{
		
	};
	/**
	 * 
	 * @param DistanceMetric
	 * @param overlap
	 * @param indexongroups
	 * @param eps
	 * @param Fileds
	 *            initi the opertaor for the SGB-All operation
	 */
	@Override
	public void iniEnv(int DistanceMetric,
			int overlap, 
			String indexongroups,
			float eps, 
			int[] Fileds) {
		this.setDistanceMetric(DistanceMetric);
		// set up the distance metric

		this.setOverlap(overlap);

		// set up the index for the groups
		this.setGstorage(indexongroups);

		// set up the eps
		this.setEps(eps);
		this.Gstorage.setEPS(eps);
		this.setDistanceMetric(DistanceMetric);
		this.Gstorage.setDistanceMetric(DistanceMetric);

		this.setFileds(Fileds);
	}


	public Groups getGstorage() {
		return Gstorage;
	}

	public void setGstorage(String gstorname) {

		if (gstorname.equals("arraylist")) {
			this.Gstorage = new ArrayListGroups();

		} else if (gstorname.equals("rtree")) {
			this.Gstorage = new RtreeGroups();

		} else if (gstorname.equals("other")) {
			throw new IllegalStateException("invalid group index");
		} else {
			throw new IllegalStateException("invalid group index");
		}

	}

	/**
	 * 
	 */
	@Override
	public void OprCurrent(Tuple T) {
		if (this.overlap == AttrType.OANY) {
			OprTupleOverlapANY(T);

		} else if (this.overlap == AttrType.OFORMNEW) {
			oprTupleOverlapFormNew(T);

		} else if (this.overlap == AttrType.OELIMINATED) {
			oprTupleOverlapEliminate(T);
		}

	}

	private void OprTupleOverlapANY(Tuple T) {
		// check the intersection of the tuple with groups
		ArrayList<Group> groups = (ArrayList<Group>)this.getGstorage().getStorage();

		boolean joinsucc = false;

		//if (this.getGstorage()> 0) 
		{
			for (Group g :groups) {
				
				//TreeMap<Integer, Tuple> tuplesinsidebound = new TreeMap<Integer, Tuple>();

				boolean inside = g.judgeJoinGroup(T, this.getFileds());

				// if new tuple can join in one exist group
				if (inside) {
					// add this one to that group
					g.addExecuteAgg(T);
					// update the group bound
					g.updateGroupRectangleBound(T, this.getFileds());
					g.updateGroupMaxiMinBound(T, this.getFileds());
					
					if(g.getDistanceMetric()==AttrType.L2)
					{
					   g.updateGroupConvexHullBound(T, this.getFileds());
					}
					// g.add(id, t);
					joinsucc=true;
					break;
				}

			}

		}

		// if there is not intersection or can not join in any groups
		// build one group and add this tuple into this group,
		// add group into the gstorage
		if (joinsucc == false || groups.size() <= 0) {
			
			int id = this.Gstorage.getCount() + 1;
			
			// init the group information
			Group g = new Group(id, 
					this.getDistanceMetric(),
					this.getFileds(),
					this.Eps);

			// update the group bound
			g.updateGroupRectangleBound(T, this.getFileds());
			g.updateGroupMaxiMinBound(T, this.getFileds());
			
			if(g.getDistanceMetric()==AttrType.L2)
			{
			   g.updateGroupConvexHullBound(T, this.getFileds());
			}
			//add this tuple into group
			g.addExecuteAgg(T);
			// we need to add the duplicated points into the new groups
			this.Gstorage.addgroup(g);

		}// if

	}

	

	/**
	 * handle the form-New overlap option
	 * @param T
	 */
	private void oprTupleOverlapFormNew(Tuple T) {

		ArrayList<Group> groups = (ArrayList<Group>)this.getGstorage().getStorage();
		
		int numberofcontain=0;
		int numberofoverlap=0;
		
		Group tmp = null;
		
		TreeMap<Integer, Tuple> tuplesinsidebound = new TreeMap<Integer, Tuple>();
		
		for (Group g :groups) {
			
			boolean inside = g.judgeJoinGroup(T, this.getFileds());

			// if new tuple can join in one exist group
			if (inside) {
				g.updateGroupRectangleBound(T, this.getFileds());
				g.updateGroupMaxiMinBound(T, this.getFileds());
				// g.add(id, t);
				tmp=g;
				numberofcontain++;
				//break;
			}
			//if this tuple can not join in this group
			else
			{
				//judge whether there is overlap
				//by checking the group bounds 
				boolean overlap=g.judgeOverlap(T, this.getFileds());
				
				if(overlap)
				{
					boolean realoverlap=true;
					
					if(numberofoverlap==0)
					{
						realoverlap=g.findIntersection(T, tuplesinsidebound, this.getGstorage().getTmpFile());
					}else
					{
						realoverlap=g.findIntersection(T, this.getGstorage().getTmpFile());
					}
					
					//one bug here
					//need to check the result is true or not
					if(realoverlap)
						numberofoverlap++;
				}
				
			}
			
			if(numberofcontain==2)
			{
				 this.getGstorage().getTmpFile().put((Integer)T.getFiled(0), T);
			}
			
		}//
		
		if(numberofcontain==0&&numberofoverlap==0)
		{
			int id = this.Gstorage.getCount() + 1;
			
			// init the group information
			Group g = new Group(id, 
					this.getDistanceMetric(),
					this.getFileds(),
					this.Eps);

			// update the group bound
			g.updateGroupRectangleBound(T, this.getFileds());
			g.updateGroupMaxiMinBound(T, this.getFileds());
			
			if(g.getDistanceMetric()==AttrType.L2)
			{
			   g.updateGroupConvexHullBound(T, this.getFileds());
			}
			//add this tuple into group
			g.addExecuteAgg(T);
			// we need to add the duplicated points into the new groups
			this.Gstorage.addgroup(g);
			
		}else if(numberofcontain==1&&numberofoverlap==0)
		{
			if(tmp!=null)
			{
			
			tmp.addExecuteAgg(T);
			// update the group bound
			tmp.updateGroupRectangleBound(T, this.getFileds());
			tmp.updateGroupMaxiMinBound(T, this.getFileds());
			
			if(tmp.getDistanceMetric()==AttrType.L2)
			{
				tmp.updateGroupConvexHullBound(T, this.getFileds());
			}
			
			}
			
		}else if(numberofoverlap==1&&numberofcontain==0)
		{
			
			int id = this.Gstorage.getCount() + 1;
			
			// init the group information
			Group g = new Group(id, 
					this.getDistanceMetric(),
					this.getFileds(),
					this.Eps);

			// update the group bound
			g.updateGroupRectangleBound(T, this.getFileds());
			g.updateGroupMaxiMinBound(T, this.getFileds());
			if(g.getDistanceMetric()==AttrType.L2)
			{
			   g.updateGroupConvexHullBound(T, this.getFileds());
			}
			//add this tuple into group
			g.addExecuteAgg(T);
			
			Iterator<Entry<Integer, Tuple>> entries = tuplesinsidebound.entrySet().iterator();
			
			while (entries.hasNext()) {
			    Map.Entry<Integer, Tuple> entry = entries.next();
			    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			    Tuple t2=entry.getValue();
			    
			    //this tuple belong to multiple group, we can not add this tuple into this group
			    //g.addExecuteAgg(t2);
			    
			    g.updateGroupRectangleBound(t2, this.getFileds());
			    g.updateGroupMaxiMinBound(t2, this.getFileds());
				
				if(g.getDistanceMetric()==AttrType.L2)
				{
				   g.updateGroupConvexHullBound(t2, this.getFileds());
				}
			}
			
			// we need to add the duplicated points into the new groups
			this.Gstorage.addgroup(g);
		}
		
	}

	/**
	 * handle the eliminate overlap option
	 * @param T
	 */
	private void oprTupleOverlapEliminate(Tuple T) {
		
		ArrayList<Group> groups = (ArrayList<Group>)this.getGstorage().getStorage();
		
		int numberofcontain=0;
		
		Group tmp = null;
		for (Group g :groups) {
			
			//TreeMap<Integer, Tuple> tuplesinsidebound = new TreeMap<Integer, Tuple>();

			boolean inside = g.judgeJoinGroup(T, this.getFileds());

			// if new tuple can join in one exist group
			if (inside) {
				
				g.updateGroupRectangleBound(T, this.getFileds());
				g.updateGroupMaxiMinBound(T, this.getFileds());
				// g.add(id, t);
				tmp=g;
				numberofcontain++;
				//break;
			}
			
			if(numberofcontain>=2)
			{
				break;
			}
		}
		
		if(numberofcontain==1&&tmp!=null)
		{
			tmp.addExecuteAgg(T);
			// update the group bound
			tmp.updateGroupRectangleBound(T, this.getFileds());
			tmp.updateGroupMaxiMinBound(T, this.getFileds());
			if(tmp.getDistanceMetric()==AttrType.L2)
			{
				tmp.updateGroupConvexHullBound(T, this.getFileds());
			}
			
		}else if(numberofcontain==0)
		{
			int id = this.Gstorage.getCount() + 1;
			
			// init the group information
			Group g = new Group(id, 
					this.getDistanceMetric(),
					this.getFileds(),
					this.Eps);

			// update the group bound
			g.updateGroupRectangleBound(T, this.getFileds());
			g.updateGroupMaxiMinBound(T, this.getFileds());
			
			if(g.getDistanceMetric()==AttrType.L2)
			{
			   g.updateGroupConvexHullBound(T, this.getFileds());
			}
			//add this tuple into group
			g.addExecuteAgg(T);
			// we need to add the duplicated points into the new groups
			this.Gstorage.addgroup(g);
		}else
		{
			//move on, and disregard this tuple
		}
	

	}

	/**
	 * next iteration for the form new group
	 */
	public void FormNewHelper(int numberofiteration)
	{
		int i=0;
		while(i<numberofiteration)
		{
		
		this.PrintOutputs();
		this.getGstorage().resetStorage();
		
		if(this.getGstorage().getTmpFile().size()==0)
			break;
		
		 if(overlap==AttrType.OFORMNEW)
		 {
				java.util.Iterator<Entry<Integer, Tuple>> entries =  this.getGstorage().getTmpFile().entrySet().iterator();
				while (entries.hasNext()) {
				    Map.Entry<Integer, Tuple> entry = entries.next();				    
				    Tuple t2=entry.getValue();
				    this.OprCurrent(t2);
				}
				
				
		 }
		 
		 //output results
		
		 i++;
		}
		
	}
	
	public void PrintOutputs() {
		System.out.println(this.Gstorage.toString());

	}

}
