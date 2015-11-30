package edu.purdue.cs.tornado.loadbalance;


public class SplitMergeInfo {

	public Partition splitParent;
	public Partition splitChild1;
	public Partition splitChild0;
	
	public Partition mergeParent;
	public Partition mergeChild1;
	public Partition mergeChild0;
	
	public int newAuxiliaryIndex;


}
