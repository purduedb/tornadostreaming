package edu.purdue.cs.tornado.atlas.SGB.opr;

import edu.purdue.cs.tornado.atlas.SGB.global.Tuple;

/**
 * the class to implemment the iterator interface
 * @author tang49
 *
 */
public class SGBOpr implements iterator {

	static String  Duplicated;
	static float Eps;
	static int distanceMetric; 
	private int []Fileds;
	int overlap;
	
	
	public int[] getFileds() {
		return Fileds;
	}

	public void setFileds(int[] fileds) {
		Fileds = fileds;
	}
	
	public static double getEps() {
		return Eps;
	}

	public static void setEps(float eps) {
		Eps = eps;
	}

	public int getOverlap() {
		return this.overlap;
	}

	public void setOverlap(int overlap) {
		this.overlap = overlap;
	}

	public int getDistanceMetric() {
		return distanceMetric;
	}

	public void setDistanceMetric(int distanceMetric) {
		this.distanceMetric = distanceMetric;
	}

	
	public void OprCurrent(Tuple t)
	{
		
	}
	
	public void PrintOutputs()
	{
		
		
	}

	public void iniEnv(int DistanceMetric, int overlap, String indexongroups,
			float eps, int[] Fileds) {
		// TODO Auto-generated method stub
		
	}

	

}
