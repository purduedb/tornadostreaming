package edu.purdue.cs.tornado.evaluator;

public class OperatorCost {
	public OperatorCost(Double cost, OperatorEvaluationTechnique evaluationApproach) {
		super();
		this.cost = cost;
		this.evaluationApproach = evaluationApproach;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	public OperatorEvaluationTechnique getEvaluationApproach() {
		return evaluationApproach;
	}
	public void setEvaluationApproach(OperatorEvaluationTechnique evaluationApproach) {
		this.evaluationApproach = evaluationApproach;
	}
	public Double cost;
	public OperatorEvaluationTechnique evaluationApproach; //how to evaluate this operator
}
