package edu.purdue.cs.tornado.atlas.SGB.global;

import edu.purdue.cs.tornado.atlas.SGB.mapred.Variables;
import edu.purdue.cs.tornado.helper.SpatialHelper;
import edu.purdue.cs.tornado.messages.DataObject;

/**
 * convexhull for 2D data
 * 
 * @author tang49
 *
 */
public class Convexhull2D {

	//int numberConvexPoint;

	ConvexTuple ConvexHead;
	int[] fileds;
	int numofconvex = 0;

	public Convexhull2D() {
		//this.ConvexPoint=new LinkedList<Tuple>();
	}

	double getDegree(Tuple o, Tuple prevous, Tuple current) {

		int index1 = fileds[0];
		int index2 = fileds[1];

		double ma_x = (Float) (prevous.getFiled(index1)) - (Float) (o.getFiled(index1));
		double ma_y = (Float) (prevous.getFiled(index2)) - (Float) (o.getFiled(index2));
		double mb_x = (Float) (current.getFiled(index1)) - (Float) (o.getFiled(index1));
		double mb_y = (Float) (current.getFiled(index2)) - (Float) (o.getFiled(index2));

		double v1 = (ma_x * mb_x) + (ma_y * mb_y);
		double ma_val = Math.sqrt(ma_x * ma_x + ma_y * ma_y);
		double mb_val = Math.sqrt(mb_x * mb_x + mb_y * mb_y);

		if (ma_val * mb_val == 0) {
			return 180;
		}
		double tmpmultiple = ma_val * mb_val;
		double cosM = v1 / tmpmultiple;

		double angleAMB = Math.acos(cosM) * 180 / Math.PI;

		return angleAMB;
	}

	/**
	 * compute the crossproduct for three vector
	 * 
	 * @param o
	 * @param b
	 * @param a
	 * @return
	 */
	float crossproduct(Tuple o, Tuple b, Tuple a) {

		if (fileds.length < o.schema.getLength()) {
			double results;

			int index1 = fileds[0];
			int index2 = fileds[1];

			return ((Float) (a.getFiled(index1)) - (Float) (o.getFiled(index1))) * ((Float) b.getFiled(index2) - (Float) (o.getFiled(index2)))
					- ((Float) (a.getFiled(index2)) - (Float) (o.getFiled(index2))) * ((Float) (b.getFiled(index1)) - (Float) (o.getFiled(index1)));

		}

		return 0;

	}

	/**
	 * judge this t is a tangent point or not where o is the new point
	 */
	boolean isTangentPoint(Tuple o, Tuple prevous, Tuple current, Tuple nextone) {

		int product1 = (int) crossproduct(o, prevous, current);
		int product2 = (int) crossproduct(o, nextone, current);

		int multiple = 1;

		if ((product2 > 0 && product1 > 0) || (product2 < 0 && product1 < 0)) {
			multiple = 1;
		} else {
			multiple = -1;
		}

		if (product1 == 0 || product2 == 0) {
			if (product1 == 0 && product2 == 0) {
				return false;
			} else {
				return true;
			}

		} else if (multiple > 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * judge whether the current point is convex point
	 * 
	 * @param o
	 * @param prevous
	 * @param current
	 * @param nextone
	 * @return
	 */
	boolean isConvexPoint(Tuple o, Tuple prevous, Tuple current, Tuple nextone) {
		double ocn = getDegree(current, o, nextone);
		double pco = getDegree(current, prevous, o);
		double pcn = getDegree(current, prevous, nextone);

		if (ocn == 0) {
			int pco_ = (int) pco;
			int pcn_ = (int) pcn;

			if ((ocn + pco_) > pcn_) {
				// this is a convex point
				return true;

			} else {
				//this is not convex point
				return false;
			}
		}

		if ((ocn + pco) > pcn) {
			// this is a convex point
			return true;

		} else {
			//this is not convex point
			return false;
		}

	}

	/**
	 * judge whether this new point inside the convex or not. use the o(n)
	 * search method if return ==0 this tuple inside the convex and this tupe
	 * can join this convex if return ==1 this tuple outside of convex and it
	 * also can join this convex if return ==2 this tuple outside of convex and
	 * it can not join this convex because the longest if return ==3 this tuple
	 * outside of convex and it can not join this convex but it has overlap
	 * distance is bigger than the eps
	 */
	public int insideConvexAndInsideBound(Tuple t, double eps) {
		Distance d = null;
		d = new L2distance();
		if (this.numofconvex == 0) {
			return 1;
		} else if (this.numofconvex == 1) {

			Tuple t1 = this.ConvexHead.getT();
			double distance = d.getDistance(AttrType.L2, t, t1, fileds);
			if (distance<1) 
				return 0;
			if (distance <= eps) {
				return 1;
			} else {
				return 2;
			}

		}

		int count1 = 0;
		int count2 = 0;
		double longest = 0;

		int i = 0;
		boolean overlap = false;

		ConvexTuple current = null;

		if (this.ConvexHead != null)
			current = this.ConvexHead.getNext();

		//Iterator iterator = this.ConvexPoint.iterator(); 

		while (current != null && i < this.numofconvex) {
			//Tuple current=(Tuple) iterator.next();

			//Tuple next=(Tuple) iterator.next();
			ConvexTuple next = current.getNext();

			double distance = d.getDistance(AttrType.L2, t, current.getT(), fileds);

			if (Double.compare(distance , 0.0)==0) {
				return 0;
			}

			if (distance > longest) {

				if (longest <= eps) {
					overlap = true;
				}

				longest = distance;

				if (longest > eps && overlap == true) {
					return 3;
				}
			}

			int value = (int) crossproduct(t, current.getT(), next.getT());

			
			if (value > 0) {
				count1++;
			} else if (value < 0) {
				count2++;
			}

			current = current.getNext();
			i++;

		}

		if (0 == count1 || 0 == count2) {

			if (this.numofconvex == 2)
				return 1;
			else
				return 0;

		} else //this tuple is in the outside area
		{
			if (longest < eps)
				return 1;
			else {

				if (overlap == true) {
					return 3;
				} else {
					return 2;
				}
			}
		}

	}

	/**
	 * remove the no convex points when the new point is comming
	 */
	public void unionConvex(Tuple t, double eps) {
		ConvexTuple newpoint = new ConvexTuple();
		newpoint.setT(t);
		newpoint.setNext(null);
		if (this.numofconvex == 0) {

			this.ConvexHead = newpoint;
			this.numofconvex++;
			return;

		} else if (this.numofconvex == 1) {
			//this.ConvexPoint.add(t);

			this.ConvexHead.setNext(newpoint);
			newpoint.setNext(this.ConvexHead);
			this.numofconvex++;
			return;

		} else if (this.numofconvex == 2) {
			//this.ConvexPoint.add(t);
			ConvexTuple tmp = this.ConvexHead.getNext();
			tmp.setNext(newpoint);
			newpoint.setNext(this.ConvexHead);
			this.numofconvex++;
			return;

		} else {

			//int i = 0;

			ConvexTuple header = this.ConvexHead;
			ConvexTuple current = header.getNext();
			ConvexTuple previous = header;
			ConvexTuple beginpoint = current;
			ConvexTuple tangleOne = null;
			ConvexTuple tangleTwo = null;
			boolean firstone = false;
			boolean secondone = false;
			boolean shouldchangeTangle = false;

			int i = 0;
			while (i <= this.numofconvex) {
				//Tuple nextone=this.ConvexPoint.get(i+1);
				ConvexTuple nextone = current.getNext();

				if (isTangentPoint(t, previous.getT(), current.getT(), nextone.getT())) {
					if (firstone == false) {
						tangleOne = current;
						firstone = true;
					} else {
						if (!current.equals(tangleOne)) {
							tangleTwo = current;
							secondone = true;
						}

					}
				} else if (firstone == true && secondone == false) {
					if (isConvexPoint(t, previous.getT(), current.getT(), nextone.getT())) {
						shouldchangeTangle = true;
					}
				}

				previous = current;
				current = nextone;
				i++;
			}

			int lostnumber = 0;
			if (tangleTwo != null && tangleOne != null) {
				//int value=crossproduct(newpoint, tangleOne, tangleTwo);
				//printf("inner product value %d \n", value);
				if (shouldchangeTangle) {
					ConvexTuple tmp = tangleTwo;
					tangleTwo = tangleOne;
					tangleOne = tmp;
				}

				lostnumber = delete_from_to_connect(tangleOne, tangleTwo, newpoint);

				this.numofconvex = this.numofconvex - lostnumber + 1;

			} else {
				//printf("problem \n");
			}

		}
	}

	private int delete_from_to_connect(ConvexTuple begin, ConvexTuple end, ConvexTuple t) {
		// TODO Auto-generated method stub

		ConvexTuple current, next;

		current = begin.getNext();

		int i = 0;
		while (!current.equals(end)) {
			next = current.getNext();

			if (current.equals(this.ConvexHead)) {
				this.ConvexHead = begin;
			}

			//free(current);
			current = null;
			current = next;
			i++;
		}

		begin.setNext(t);
		t.setNext(end);

		return i;

	}

	private class ConvexTuple {
		private Tuple t;
		private ConvexTuple next;

		public Tuple getT() {
			return t;
		}

		public void setT(Tuple t) {
			this.t = t;
		}

		public ConvexTuple getNext() {
			return next;
		}

		public void setNext(ConvexTuple next) {
			this.next = next;
		}

	}

	@Override
	public String toString() {
		StringBuffer tmp = new StringBuffer();
		int number = this.numofconvex;
		ConvexTuple current, next;

		current = this.ConvexHead;

		int i = 0;
		while (current != null && i < number) {
			next = current.getNext();
			tmp.append(current.getT().toString() + " ");
			current = next;
			i++;
		}

		return tmp.toString();

	}

	/**
	 * @return double[]
	 * 
	 *         return convexhull's values [minX, maxX, minY, maxY]
	 */
	public double[] getXYBoundsInConvexPoints() {
		ConvexTuple current;
		int number = this.numofconvex;
		current = this.ConvexHead;

		int index1 = fileds[0];
		int index2 = fileds[1];

		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		double[] returnValues = new double[4];

		//System.out.print("Convex Points : ");

		int i = 0;
		while (current != null && i < number) {
			//System.out.print("("+current.getT().getFiled(index1).toString() + "," +current.getT().getFiled(index2).toString() + ") ");

			if ((Float) (current.getT().getFiled(index1)) < minX) {
				minX = (Float) (current.getT().getFiled(index1));
			}

			if ((Float) (current.getT().getFiled(index1)) > maxX) {
				maxX = (Float) (current.getT().getFiled(index1));
			}
			if ((Float) (current.getT().getFiled(index2)) < minY) {
				minY = (Float) (current.getT().getFiled(index2));
			}
			if ((Float) (current.getT().getFiled(index2)) > maxY) {
				maxY = (Float) (current.getT().getFiled(index2));
			}

			current = current.getNext();
			i++;
		}

		System.out.println("");
		returnValues[0] = minX;
		returnValues[1] = maxX;
		returnValues[2] = minY;
		returnValues[3] = maxY;

		return returnValues;

	}

	public float getDiameter() {

		// single point
		if (numofconvex <= 1)
			return 0;

		// number of points on the hull
		int M = numofconvex;
		float bestDistanceSquared = 0;
		ConvexTuple best1, best2;

		// the hull, in counterclockwise order
		ConvexTuple[] hull = new ConvexTuple[M + 1];
		int m = M;
		ConvexTuple currentTuple = ConvexHead;
		while (m >0) {
			hull[m--] = currentTuple;
			currentTuple = currentTuple.next;
			;
		}

		
		// points are collinear
		if (M == 2) {
			best1 = hull[1];
			best2 = hull[2];
			bestDistanceSquared = SpatialHelper.getDistanceInBetween(((DataObject) best1.t.getFiled(3)).getLocation(), ((DataObject) best2.t.getFiled(3)).getLocation()).floatValue();
			return bestDistanceSquared;
		}

		// k = farthest vertex from edge from hull[1] to hull[M]
		int k = 2;
		while (k<M&&SpatialHelper.getAreaInBetween(((DataObject) hull[M].t.getFiled(3)).getLocation(), ((DataObject) hull[k + 1].t.getFiled(3)).getLocation(), ((DataObject) hull[1].t.getFiled(3)).getLocation()) > SpatialHelper
				.getAreaInBetween(((DataObject) hull[M].t.getFiled(3)).getLocation(), ((DataObject) hull[k].t.getFiled(3)).getLocation(), ((DataObject) hull[1].t.getFiled(3)).getLocation())) {
			k++;
		}

		int j = k;
		for (int i = 1; i <= k; i++) {
			// StdOut.println("hull[i] + " and " + hull[j] + " are antipodal");
			float dist = SpatialHelper.getDistanceInBetween(((DataObject) hull[i].t.getFiled(3)).getLocation(),
					((DataObject) hull[j].t.getFiled(3)).getLocation()).floatValue();
			if (dist > bestDistanceSquared) {
				best1 = hull[i];
				best2 = hull[j];
				bestDistanceSquared = dist;
			}
			while ((j < M) && SpatialHelper.getAreaInBetween(((DataObject) hull[i].t.getFiled(3)).getLocation(), ((DataObject) hull[j + 1].t.getFiled(3)).getLocation(), ((DataObject) hull[i + 1].t.getFiled(3)).getLocation()) > SpatialHelper
					.getAreaInBetween(((DataObject) hull[i].t.getFiled(3)).getLocation(), ((DataObject) hull[j].t.getFiled(3)).getLocation(), ((DataObject) hull[i + 1].t.getFiled(3)).getLocation())) {
				j++;
				// StdOut.println(hull[i] + " and " + hull[j] + " are antipodal");
				dist = SpatialHelper.getDistanceInBetween(((DataObject) hull[i].t.getFiled(3)).getLocation(), ((DataObject) hull[j].t.getFiled(3)).getLocation()).floatValue();
				if (dist > bestDistanceSquared) {
					best1 = hull[i];
					best2 = hull[j];
					bestDistanceSquared = dist;
				}
			}
		}
		return bestDistanceSquared;
	}

	public int[] getFileds() {
		return fileds;
	}

	public void setFileds(int[] fileds) {
		this.fileds = fileds;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Schema s_drivers = new Schema(3);
		s_drivers.initField(0, AttrType.INTEGER, 4, "PKey");
		s_drivers.initField(1, AttrType.INTEGER, 4, "x");
		s_drivers.initField(2, AttrType.INTEGER, 4, "y");

		Convexhull2D test = new Convexhull2D();

		int judgeresult;
		int[] Fileds = { 1, 2 };
		test.fileds = Fileds;

		Tuple tuple = new Tuple(s_drivers);

		tuple.setFiled(0, Integer.valueOf(0));
		tuple.setFiled(1, Double.valueOf(13));
		tuple.setFiled(2, Double.valueOf(1));
		double eps = Variables.eps;
		judgeresult = test.insideConvexAndInsideBound(tuple, eps);

		if (judgeresult == 1) {
			test.unionConvex(tuple, eps);
		}

		tuple = new Tuple(s_drivers);

		tuple.setFiled(0, new Integer(1));
		tuple.setFiled(1, new Float(11));
		tuple.setFiled(2, new Float(3));

		judgeresult = test.insideConvexAndInsideBound(tuple, eps);

		if (judgeresult == 1) {
			test.unionConvex(tuple, eps);
		}

		tuple = new Tuple(s_drivers);

		tuple.setFiled(0, new Integer(2));
		tuple.setFiled(1, new Float(16));
		tuple.setFiled(2, new Float(5));

		judgeresult = test.insideConvexAndInsideBound(tuple, eps);

		if (judgeresult == 1) {
			test.unionConvex(tuple, eps);
		}

		tuple = new Tuple(s_drivers);

		tuple.setFiled(0, Integer.valueOf(3));
		tuple.setFiled(1, Double.valueOf(16));
		tuple.setFiled(2, Double.valueOf(0));

		judgeresult = test.insideConvexAndInsideBound(tuple, eps);

		if (judgeresult == 1) {
			test.unionConvex(tuple, eps);
		}

		tuple = new Tuple(s_drivers);

		tuple.setFiled(0, new Integer(4));
		tuple.setFiled(1, new Float(13));
		tuple.setFiled(2, new Float(1));

		judgeresult = test.insideConvexAndInsideBound(tuple, eps);

		if (judgeresult == 1) {
			test.unionConvex(tuple, eps);
		}

		System.out.println(test.toString());

		tuple = new Tuple(s_drivers);

		tuple.setFiled(0, Integer.valueOf(5));
		tuple.setFiled(1, Double.valueOf(12));
		tuple.setFiled(2, Double.valueOf(1));

		judgeresult = test.insideConvexAndInsideBound(tuple, eps);

		if (judgeresult == 1) {
			test.unionConvex(tuple, eps);
		}

		System.out.println(test.toString());

		tuple = new Tuple(s_drivers);

		tuple.setFiled(0, Integer.valueOf(6));
		tuple.setFiled(1, Double.valueOf(13));
		tuple.setFiled(2, Double.valueOf(4));

		judgeresult = test.insideConvexAndInsideBound(tuple, eps);

		if (judgeresult == 1) {
			test.unionConvex(tuple, eps);
		}
		System.out.println(judgeresult);
		System.out.println(test.toString());
	}

}
