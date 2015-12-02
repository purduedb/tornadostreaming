package edu.purdue.cs.tornado.loadbalance;

public class Cell extends Partition {
	public Cell(){
		super();
	}
	public Cell(int bottom, int top, int left, int right) {
		super(bottom, top, left, right);
		this.children = new Cell[2];
		this.parent = null;
	}				

	private Cell parent;
	private Cell[] children;
	public Cell getParent() {
		return parent;
	}
	public void setParent(Cell parent) {
		this.parent = parent;
	}
	public Cell[] getChildren() {
		return children;
	}
	public void setChildren(Cell[] children) {
		this.children = children;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

	private double score;
}