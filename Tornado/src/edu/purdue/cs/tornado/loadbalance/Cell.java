package edu.purdue.cs.tornado.loadbalance;

public class Cell extends Partition {
	public Cell(){
		super();
	}
	public Cell(int bottom, int top, int left, int right) {
		super(bottom, top, left, right);
		this.children = new Cell[2];
		this.children[0]=null;
		this.children[1]=null;
		this.parent = null;
	}				
	public Cell(Partition p){
		super(p.getBottom(), p.getTop(), p.getLeft(), p.getRight());
		this.children = new Cell[4];
		this.children[0]=null;
		this.children[1]=null;
		this.children[2]=null;
		this.children[3]=null;
		this.parent = null;
		this.index = p.index;
		this.score=0;
		
	}
	public Cell(Cell p){
		super(p.getBottom(), p.getTop(), p.getLeft(), p.getRight());
		this.children = new Cell[4];
		this.children[0]=null;
		this.children[1]=null;
		this.children[2]=null;
		this.children[3]=null;
		this.parent = null;
		this.index = p.index;
		this.score=0;
	}
	public Cell parent;
	public Cell[] children;
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
	@Override
 public String toString (){
		return "Index: "+this.index+", score "+score+", bottom "+getBottom()+", top "+getTop()+", left "+getLeft()+", right "+getRight();
	}
	private double score;
}