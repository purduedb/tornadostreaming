package edu.purdue.cs.tornado.atlas.SGB.opr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.purdue.cs.tornado.atlas.SGB.global.AttrType;
import edu.purdue.cs.tornado.atlas.SGB.global.Tuple;
import edu.purdue.cs.tornado.atlas.SGB.group.ArrayListGroups;
import edu.purdue.cs.tornado.atlas.SGB.group.GridGroups;
import edu.purdue.cs.tornado.atlas.SGB.group.Group;
import edu.purdue.cs.tornado.atlas.SGB.group.Groups;
import edu.purdue.cs.tornado.atlas.SGB.group.RtreeGroups;
import edu.purdue.cs.tornado.atlas.SGB.index.Rectangle;
import edu.purdue.cs.tornado.atlas.SGB.index.SpatialIndex;
import edu.purdue.cs.tornado.atlas.SGB.opr.SGBAllIndex.SaveToListProcedure;
import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.messages.DataObject;
import gnu.trove.TIntProcedure;

public class SGBAllIndexKewords extends SGBOpr {

	public String GroupsStorageName;
	Groups Gstorage;
	ArrayList<String> keywords;
	Point focalPoint;

	public SGBAllIndexKewords(ArrayList<String> keywords, Point focalPoint) {
		this.focalPoint = focalPoint;
		this.keywords = keywords;
	}

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
	public void iniEnv(int DistanceMetric, int overlap, String indexongroups, float eps, int[] Fileds) {
		// set up the eps
		this.setEps(eps);
		this.setDistanceMetric(DistanceMetric);
		// set up the distance metric

		this.setOverlap(overlap);

		// set up the index for the groups
		this.setGstorage(indexongroups);

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

		} else if (gstorname.equals("grid")) {
			this.Gstorage = new GridGroups(this.Eps);

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
		} else if (this.overlap == AttrType.ODUPLICATIED) {
			oprTupleOverlapDuplicate(T);
		} else if (this.overlap == AttrType.WITHIN_DIST_VALIDATE_ON_FLY) {
			oprTupleOverlapDuplicateValidateOnFly(T);
		}

	}

	/**
	 * handle the eliminate overlap option
	 * 
	 * @param T
	 */
	private void oprTupleOverlapEliminate(Tuple T) {

		ArrayList<Group> groups = (ArrayList<Group>) this.getGstorage().getStorage();

		int numberofcontain = 0;

		Group tmp = null;

		Rectangle pointRect = T.getRectangleForPoint();

		SpatialIndex GIndex = (SpatialIndex) this.Gstorage.getSIndex();

		boolean joinsucc = false;

		if (GIndex != null) {
			SaveToListProcedure myProc = new SaveToListProcedure();

			//query the index and find the groups that contain point T
			GIndex.containPoint(pointRect, myProc);

			List<Integer> ids = myProc.getIds();

			for (Integer id : ids) {

				Group g = groups.get(id - 1);

				boolean inside = g.judgeJoinGroup(T, this.getFileds());

				// if new tuple can join in one exist group
				if (inside) {

					g.updateGroupRectangleBound(T, this.getFileds());
					g.updateGroupMaxiMinBound(T, this.getFileds());
					// g.add(id, t);
					tmp = g;
					numberofcontain++;
					//break;
				}

				if (numberofcontain >= 2) {
					break;
				}

			}

		}

		if (numberofcontain == 1 && tmp != null) {
			tmp.addExecuteAgg(T);
			// update the group bound
			tmp.updateGroupRectangleBound(T, this.getFileds());
			tmp.updateGroupMaxiMinBound(T, this.getFileds());
			if (tmp.getDistanceMetric() == AttrType.L2) {
				tmp.updateGroupConvexHullBound(T, this.getFileds());
			}

		} else if (numberofcontain == 0) {
			int id = this.Gstorage.getCount() + 1;

			// init the group information
			Group g = new Group(id, this.getDistanceMetric(), this.getFileds(), this.Eps);

			// update the group bound
			g.updateGroupRectangleBound(T, this.getFileds());
			g.updateGroupMaxiMinBound(T, this.getFileds());
			if (g.getDistanceMetric() == AttrType.L2) {
				g.updateGroupConvexHullBound(T, this.getFileds());
			}
			//add this tuple into group
			g.addExecuteAgg(T);
			// we need to add the duplicated points into the new groups
			this.Gstorage.addgroup(g);
		} else {
			//move on, and disregard this tuple
		}

	}

	private void oprTupleOverlapDuplicate(Tuple T) {

		// check the intersection of the tuple with groups
		ArrayList<Group> groups = (ArrayList<Group>) this.getGstorage().getStorage();

		Rectangle pointRect = T.getRectangleForPoint();

		SpatialIndex GIndex = (SpatialIndex) this.Gstorage.getSIndex();

		boolean joinsucc = false;

		if (GIndex != null) {
			SaveToListProcedure myProc = new SaveToListProcedure();

			//query the index and find the groups that contain point T
			ArrayList<Integer> ids = GIndex.containPoint(pointRect, myProc);

			//List<Integer> ids = myProc.getIds();

			if (ids.size() >= 1) {
				for (Integer id : ids) {
					Group g = groups.get(id - 1);
					boolean inside = g.judgeJoinGroup(T, this.getFileds());
					if (inside) {
						Rectangle oldRect = g.getRectangle();
						g.updateGroupRectangleBound(T, this.getFileds());
						g.updateGroupMaxiMinBound(T, this.getFileds());
						Rectangle newRect = g.getRectangle();
						GIndex.update(oldRect, newRect, id);
						g.addExecuteAgg(T);
						if (g.getDistanceMetric() == AttrType.L2) {
							g.updateGroupConvexHullBound(T, this.getFileds());
						}
						joinsucc = true;
					}
				}
			} else {
				joinsucc = false;
			}

		}

		// if there is not intersection or can not join in any groups
		// build one group and add this tuple into this group,
		// add group into the gstorage
		if (joinsucc == false || groups.size() <= 0) {

			ArrayList<Group> newGroups = new ArrayList<Group>();

			int id = this.Gstorage.getCount() + 1;
			Group g = new Group(id, this.getDistanceMetric(), this.getFileds(), this.Eps);
			addTupleTOGroup(g, T,false);
			newGroups.add(g);
			this.Gstorage.addgroup(g);
			// we need to add the duplicated points into the new groups
			ArrayList<Tuple> tuples = GIndex.getSpatialOverlapTuples(g.getRectangle());
			for (Tuple tuple : tuples) {
				boolean tupleAdded = false;
				for (Group group : newGroups) {
					boolean inside = group.judgeJoinGroup(tuple, this.getFileds());
					// if new tuple can join in one exist group
					if (inside) {
						tupleAdded = true;
						addTupleTOGroup(group, tuple,false);
					}
				}
				if(! tupleAdded){
					id = this.Gstorage.getCount() + 1;
					Group g1 = new Group(id, this.getDistanceMetric(), this.getFileds(), this.Eps);
					addTupleTOGroup(g1, T,false);
					boolean inside = g1.judgeJoinGroup(tuple, this.getFileds());
					if (inside) {
						tupleAdded = true;
						addTupleTOGroup(g1, tuple,false);
						newGroups.add(g1);
						this.Gstorage.addgroup(g1);
					}
				}
			}
			GIndex.addTuple(T);
		} // if
	}
	private void addTupleTOGroup(Group g, Tuple T,boolean validateCandiaite) {
		g.updateGroupRectangleBound(T, this.getFileds());
		g.updateGroupMaxiMinBound(T, this.getFileds());
		if (g.getDistanceMetric() == AttrType.L2) {
			g.updateGroupConvexHullBound(T, this.getFileds());
		}
		//add this tuple into group
		g.addExecuteAgg(T);
		if(validateCandiaite)
		if (!g.candidate) {
			g.updateRemainingKeywords(((DataObject) T.getFiled(3)).hashedText);
			if (g.candidate)
				this.Gstorage.markCandidateGroup(g.getGroupid());
		}
	}
	private void oprTupleOverlapDuplicateValidateOnFly(Tuple T) {

		// check the intersection of the tuple with groups
		ArrayList<Group> groups = (ArrayList<Group>) this.getGstorage().getStorage();

		Rectangle pointRect = T.getRectangleForPoint();

		SpatialIndex GIndex = (SpatialIndex) this.Gstorage.getSIndex();

		boolean joinsucc = false;

		if (GIndex != null) {
			SaveToListProcedure myProc = new SaveToListProcedure();
			//query the index and find the groups that contain point T
			ArrayList<Integer> ids = GIndex.containPoint(pointRect, myProc);
			if (ids.size() >= 1) {
				for (Integer id : ids) {
					Group g = groups.get(id - 1);
					boolean inside = g.judgeJoinGroup(T, this.getFileds());
					// if new tuple can join in one exist group
					if (inside) {
						Rectangle oldRect = g.getRectangle();
						g.updateGroupRectangleBound(T, this.getFileds());
						g.updateGroupMaxiMinBound(T, this.getFileds());
						Rectangle newRect = g.getRectangle();
						GIndex.update(oldRect, newRect, id);
						if (!g.candidate) {
							g.updateRemainingKeywords(((DataObject) T.getFiled(3)).hashedText);
							if (g.candidate)
								this.Gstorage.markCandidateGroup(g.getGroupid());
						}
						g.addExecuteAgg(T);
						if (g.getDistanceMetric() == AttrType.L2) {
							g.updateGroupConvexHullBound(T, this.getFileds());
						}
						joinsucc = true;
					}
				}
			} else {
				joinsucc = false;
			}

		}

		// if there is not intersection or can not join in any groups
		// build one group and add this tuple into this group,
		// add group into the gstorage
		if (joinsucc == false || groups.size() <= 0) {
			ArrayList<Group> newGroups = new ArrayList<Group>();
			int id = this.Gstorage.getCount() + 1;
			Group g = new Group(id, this.getDistanceMetric(), this.getFileds(), this.Eps);
			g.addRemainingKeywords(keywords);
			addTupleTOGroup(g, T,true);
			newGroups.add(g);
			this.Gstorage.addgroup(g);
			// we need to add the duplicated points into the new groups
			ArrayList<Tuple> tuples = GIndex.getSpatialOverlapTuples(g.getRectangle());
			for (Tuple tuple : tuples) {
				boolean tupleAdded = false;
				for (Group group : newGroups) {
					boolean inside = group.judgeJoinGroup(tuple, this.getFileds());
					// if new tuple can join in one exist group
					if (inside) {
						tupleAdded = true;
						addTupleTOGroup(group, tuple,true);
						
					}
				}
				if(! tupleAdded){
					id = this.Gstorage.getCount() + 1;
					Group g1 = new Group(id, this.getDistanceMetric(), this.getFileds(), this.Eps);
					g1.addRemainingKeywords(keywords);
					addTupleTOGroup(g1, T,true);
					boolean inside = g1.judgeJoinGroup(tuple, this.getFileds());
					if (inside) {
						tupleAdded = true;
						addTupleTOGroup(g1, tuple,true);
						newGroups.add(g1);
						this.Gstorage.addgroup(g1);
					}
				}
			}
			GIndex.addTuple(T);

		} // if
	}

	private void OprTupleOverlapANY(Tuple T) {
		// check the intersection of the tuple with groups
		ArrayList<Group> groups = (ArrayList<Group>) this.getGstorage().getStorage();

		Rectangle pointRect = T.getRectangleForPoint();

		SpatialIndex GIndex = (SpatialIndex) this.Gstorage.getSIndex();

		boolean joinsucc = false;

		if (GIndex != null) {
			SaveToListProcedure myProc = new SaveToListProcedure();

			//query the index and find the groups that contain point T
			ArrayList<Integer> ids =GIndex.containPoint(pointRect, myProc);

			// myProc.getIds();

			if (ids.size() >= 1) {
				for (Integer id : ids) {

					Group g = groups.get(id - 1);

					boolean inside = g.judgeJoinGroup(T, this.getFileds());

					// if new tuple can join in one exist group
					if (inside) {
						Rectangle oldRect = g.getRectangle();

						g.updateGroupRectangleBound(T, this.getFileds());
						g.updateGroupMaxiMinBound(T, this.getFileds());
						Rectangle newRect = g.getRectangle();
						GIndex.update(oldRect, newRect, id);
						// add this one to that group
						g.addExecuteAgg(T);
						// update the group bound
						if (g.getDistanceMetric() == AttrType.L2) {
							//System.out.println("Update call");
							g.updateGroupConvexHullBound(T, this.getFileds());
						}
						// g.add(id, t);
						joinsucc = true;

						break;
					}
				}
			} else {
				joinsucc = false;
			}

		}

		// if there is not intersection or can not join in any groups
		// build one group and add this tuple into this group,
		// add group into the gstorage
		if (joinsucc == false || groups.size() <= 0) {

			int id = this.Gstorage.getCount() + 1;

			// init the group information
			Group g = new Group(id, this.getDistanceMetric(), this.getFileds(), this.Eps);

			// update the group bound
			g.updateGroupRectangleBound(T, this.getFileds());
			g.updateGroupMaxiMinBound(T, this.getFileds());
			if (g.getDistanceMetric() == AttrType.L2) {
				g.updateGroupConvexHullBound(T, this.getFileds());
			}
			//add this tuple into group
			g.addExecuteAgg(T);
			// we need to add the duplicated points into the new groups

			this.Gstorage.addgroup(g);

		} // if

	}

	/**
	 * handle the form-New overlap option
	 * 
	 * @param T
	 */
	private void oprTupleOverlapFormNew(Tuple T) {

		ArrayList<Group> groups = (ArrayList<Group>) this.getGstorage().getStorage();

		int numberofcontain = 0;
		int numberofoverlap = 0;

		Group tmp = null;
		int tmpGid = 0;

		TreeMap<Integer, Tuple> tuplesinsidebound = new TreeMap<Integer, Tuple>();

		Rectangle pointRect = T.getRectangleForPoint();

		SpatialIndex GIndex = (SpatialIndex) this.Gstorage.getSIndex();

		List<Integer> containids = null;
		List<Integer> deleteids = new ArrayList<Integer>();

		if (GIndex != null) {
			SaveToListProcedure myProc = new SaveToListProcedure();

			//query the index and find the groups that contain point T
			GIndex.containPoint(pointRect, myProc);

			containids = myProc.getIds();

			if (containids.size() >= 1) {
				for (Integer id : containids) {

					Group g = groups.get(id - 1);

					boolean inside = g.judgeJoinGroup(T, this.getFileds());

					// if new tuple can join in one exist group
					if (inside) {
						Rectangle updateone = g.getRectangle();

						if (GIndex.delete(updateone, id)) {
							//System.out.println("fail to delete rectangle");
							deleteids.add(id);
						}

						g.updateGroupRectangleBound(T, this.getFileds());
						g.updateGroupMaxiMinBound(T, this.getFileds());
						// g.add(id, t);
						tmp = g;
						tmpGid = g.getGroupid();
						numberofcontain++;

					}

					if (numberofcontain == 2) {
						this.getGstorage().getTmpFile().put((Integer) T.getFiled(0), T);
					}
				}
			}
		}

		if (GIndex != null) {
			SaveToListProcedure myProc = new SaveToListProcedure();
			//query the index and find the groups that contain point T

			pointRect = T.getRectangleForRange((float) this.getEps());
			GIndex.intersects(pointRect, myProc);

			List<Integer> ids = myProc.getIds();

			if (ids.size() >= 1) {
				for (Integer id : ids) {

					Group g = groups.get(id - 1);

					boolean overlap = g.judgeOverlap(T, this.getFileds());

					if (overlap) {
						boolean realoverlap = true;

						if (numberofoverlap == 0) {
							realoverlap = g.findIntersection(T, tuplesinsidebound, this.getGstorage().getTmpFile());
						} else {
							realoverlap = g.findIntersection(T, this.getGstorage().getTmpFile());
						}

						//one bug here
						//need to check the result is true or not
						if (realoverlap)
							numberofoverlap++;
					}

				}
			}

		}

		if (numberofcontain == 0 && numberofoverlap == 0) {
			int id = this.Gstorage.getCount() + 1;

			// init the group information
			Group g = new Group(id, this.getDistanceMetric(), this.getFileds(), this.Eps);

			// update the group bound
			g.updateGroupRectangleBound(T, this.getFileds());
			g.updateGroupMaxiMinBound(T, this.getFileds());

			if (g.getDistanceMetric() == AttrType.L2) {
				g.updateGroupConvexHullBound(T, this.getFileds());
			}
			//add this tuple into group
			g.addExecuteAgg(T);
			// we need to add the duplicated points into the new groups
			this.Gstorage.addgroup(g);

		} else if (numberofcontain == 1 && numberofoverlap == 0) {
			if (tmp != null) {

				tmp.addExecuteAgg(T);
				// update the group bound
				/*
				 * Rectangle updateone=tmp.getRectangle();
				 * if(GIndex.delete(updateone, tmpGid)) {
				 * tmp.updateGroupRectangleBound(T, this.getFileds());
				 * updateone=tmp.getRectangle(); GIndex.add(updateone, tmpGid);
				 * }else { System.out.println("fail to delete one rectangle"); }
				 */

				tmp.updateGroupRectangleBound(T, this.getFileds());
				tmp.updateGroupMaxiMinBound(T, this.getFileds());

				if (tmp.getDistanceMetric() == AttrType.L2) {
					tmp.updateGroupConvexHullBound(T, this.getFileds());
				}

			}

		} else if (numberofoverlap == 1 && numberofcontain == 0) {

			int id = this.Gstorage.getCount() + 1;

			// init the group information
			Group g = new Group(id, this.getDistanceMetric(), this.getFileds(), this.Eps);

			// update the group bound
			g.updateGroupRectangleBound(T, this.getFileds());
			g.updateGroupMaxiMinBound(T, this.getFileds());

			if (g.getDistanceMetric() == AttrType.L2) {
				g.updateGroupConvexHullBound(T, this.getFileds());
			}
			//add this tuple into group
			g.addExecuteAgg(T);

			Iterator<Entry<Integer, Tuple>> entries = tuplesinsidebound.entrySet().iterator();

			while (entries.hasNext()) {
				Map.Entry<Integer, Tuple> entry = entries.next();
				//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
				Tuple t2 = entry.getValue();

				//this tuple belong to multiple group, we can not add this tuple into this group
				//g.addExecuteAgg(t2);

				g.updateGroupRectangleBound(t2, this.getFileds());
				g.updateGroupMaxiMinBound(T, this.getFileds());

				if (g.getDistanceMetric() == AttrType.L2) {
					g.updateGroupConvexHullBound(t2, this.getFileds());
				}
			}

			// we need to add the duplicated points into the new groups
			this.Gstorage.addgroup(g);
		}

		if (deleteids != null && deleteids.size() > 0) {
			//System.out.println(deleteids.toString());

			for (Integer id : deleteids) {

				Group g = groups.get(id - 1);

				Rectangle updateone = g.getRectangle();
				GIndex.add(updateone, id);

			}
		}

	}

	class SaveToListProcedure implements TIntProcedure {
		private List<Integer> ids = new ArrayList<Integer>();

		@Override
		public boolean execute(int id) {
			ids.add(id);
			return true;
		};

		private List<Integer> getIds() {
			return ids;
		}
	};

	/**
	 * next iteration for the form new group
	 */
	public void FormNewHelper(int numberofiteration) {
		int i = 0;
		while (i < numberofiteration) {

			this.PrintOutputs();
			this.getGstorage().resetStorage();

			if (this.getGstorage().getTmpFile().size() == 0)
				break;

			if (overlap == AttrType.OFORMNEW) {
				java.util.Iterator<Entry<Integer, Tuple>> entries = this.getGstorage().getTmpFile().entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<Integer, Tuple> entry = entries.next();
					Tuple t2 = entry.getValue();
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

	public void getGroupsInOverlapRegion() {
		ArrayList<Group> groups = (ArrayList<Group>) this.getGstorage().getStorage();
		double[] bounds;
		float[][] bounds2;

		for (Group gr : groups) {
			System.out.println("All Convex points in GID:" + gr.getGroupid() + " : " + gr.getGroupConvexhullbound().toString());

			System.out.println(
					"Rectangle Points in GID:" + gr.getGroupid() + " : " + gr.getGroupRectanglebound()[0][0] + " " + gr.getGroupRectanglebound()[1][0] + " " + gr.getGroupRectanglebound()[0][1] + " " + gr.getGroupRectanglebound()[1][1]);

			System.out.print("MaxMin Points in GID:" + gr.getGroupid() + " : " + gr.getGroupMaxMinbound()[0][0] + " " + gr.getGroupMaxMinbound()[1][0] + " " + gr.getGroupMaxMinbound()[0][1] + " " + gr.getGroupMaxMinbound()[1][1]);

			bounds = gr.getGroupConvexhullbound().getXYBoundsInConvexPoints();

			System.out.print("Min X : " + bounds[0] + "   ");
			System.out.print("Max X : " + bounds[1] + "   ");
			System.out.print("Min Y : " + bounds[2] + "   ");
			System.out.println("Max Y : " + bounds[3]);
			System.out.println("");

		}

	}

}
