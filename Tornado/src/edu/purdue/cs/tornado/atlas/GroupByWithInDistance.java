package edu.purdue.cs.tornado.atlas;

import edu.purdue.cs.tornado.atlas.SGB.global.AttrType;
import edu.purdue.cs.tornado.atlas.SGB.global.Schema;
import edu.purdue.cs.tornado.atlas.SGB.global.Tuple;
import edu.purdue.cs.tornado.atlas.SGB.mapred.Variables;
import edu.purdue.cs.tornado.atlas.SGB.opr.SGBAll;

public class GroupByWithInDistance {
	public static void main(String[] args) {
		String indexongroups = "arraylist";
		//String indexongroups="rtree";
		float eps = Variables.eps;
		int overlap = AttrType.OANY;
		Schema s_drivers = new Schema(3);
		s_drivers.initField(0, AttrType.INTEGER, 4, "PKey");
		s_drivers.initField(1, AttrType.INTEGER, 4, "x");
		s_drivers.initField(2, AttrType.INTEGER, 4, "y");
		int[] Fileds = { 1, 2 };
		int metric = AttrType.L2;
		SGBAll sgbAll = new SGBAll();
		sgbAll.iniEnv(metric, overlap, indexongroups, eps, Fileds);
		Tuple tuple1 = new Tuple(s_drivers);
		Tuple tuple2 = new Tuple(s_drivers);
		Tuple tuple3 = new Tuple(s_drivers);
	
		tuple1.setFiled(0, 5);
		tuple1.setFiled(1, 6);
		tuple1.setFiled(2, 7);
		tuple2.setFiled(0, 15);
		tuple2.setFiled(1, 6.5);
		tuple2.setFiled(2, 7.5);
		tuple3.setFiled(0, 16);
		tuple3.setFiled(1, 8);
		tuple3.setFiled(2, 11);
		
		sgbAll.OprCurrent(tuple1);
		sgbAll.OprCurrent(tuple2);
		sgbAll.OprCurrent(tuple3);
		sgbAll.PrintOutputs();
	}
}
