package edu.purdue.cs.tornado.loadbalance;

public class LocalBestSplitInfo {
	public Integer coordinate ;// 0 for x , 1 for y 
	public Integer splitPosition ;// 0 for x , 1 for y I
	public Integer beforeSplitCost;
	public Integer afterSplitCost;
	public Integer bestSplitCost;
	public LocalBestSplitInfo(){
		this.coordinate=0;
		this.splitPosition=0;
		this.beforeSplitCost=0;
		this.afterSplitCost =0;
		this.bestSplitCost =0;
	}
}
