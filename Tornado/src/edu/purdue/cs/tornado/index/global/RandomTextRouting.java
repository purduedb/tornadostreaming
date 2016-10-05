package edu.purdue.cs.tornado.index.global;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.purdue.cs.tornado.helper.Point;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.helper.SpatioTextualConstants;
import edu.purdue.cs.tornado.helper.TextualPredicate;
import edu.purdue.cs.tornado.loadbalance.Cell;
import edu.purdue.cs.tornado.messages.DataObject;
import edu.purdue.cs.tornado.messages.Query;

public class RandomTextRouting extends GlobalIndex {

	public RandomTextRouting(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks);

	}

	public RandomTextRouting(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, ArrayList<Cell> partitions, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks);

	}

	public ArrayList<Cell> getInitialPartitions() {
		return null;
	}

	public void initStructures(ArrayList<Cell> partitions) {

	}

	@Override
	public HashSet<String> addTextToTaskID(ArrayList<Integer> tasks, ArrayList<String> text, boolean all, boolean forward) {
		return null;
	}

	@Override
	public void dropTextFromTaskID(ArrayList<Integer> tasks, ArrayList<String> text) {

	}

	@Override
	public Rectangle getBoundsForTaskId(Integer taskId) {
		Integer taskIndex = taskIdToIndex.get(taskId);
		return getBoundsForTaskIndex(taskIndex);
	}

	@Override
	public Rectangle getBoundsForTaskIndex(Integer taskIndex) {
		Rectangle rect;
		rect = new Rectangle(new Point(0.0, 0.0), new Point(SpatioTextualConstants.xMaxRange, SpatioTextualConstants.yMaxRange));
		return rect;
	}

	@Override
	public Integer getTaskIDsContainingPoint(Point point) throws Exception {
		return null;
	}

	@Override
	public ArrayList<Integer> getTaskIDsOverlappingRecangle(Rectangle rectangle) {
		return null;
	}

	@Override
	public GlobalIndexIterator globalKNNIterator(Point p) {
		return null;
	}

	@Override
	public Boolean verifyTextOverlap(Integer task, ArrayList<String> text) {
		return true;
	}

	@Override
	public Boolean isTextAware() {
		return false;
	}

	@Override
	public Boolean isTextOnlyIndex() {
		return true;
	}

	@Override
	public ArrayList<Integer> getTaskIDsContainingKeywordData(DataObject object) {
		return mapTextToInteger(object.getObjectText());
	}

	@Override
	public ArrayList<Integer> getTaskIDsContainingKeywordQuery(Query query) {
		ArrayList<Integer> tasks = null;
		if (query.getTextualPredicate().equals(TextualPredicate.OVERlAPS)) {
			tasks = mapTextToInteger(query.getQueryText());
		} else if (query.getTextualPredicate().equals(TextualPredicate.CONTAINS)) {
			ArrayList<String> singleKeyword = new ArrayList<String>();
			singleKeyword.add(query.getQueryText().get(0));
			tasks = mapTextToInteger(singleKeyword);
		} else if (query.getTextualPredicate().equals(TextualPredicate.BOOLEAN_EXPR)) {
			ArrayList<String> multipleKeywords = new ArrayList<String>();
			for (ArrayList<String> conjunction : query.getComplexQueryText())
				multipleKeywords.add(conjunction.get(0));
			tasks = mapTextToInteger(multipleKeywords);
		}
		return tasks;
	}

	protected ArrayList<Integer> mapTextToInteger(Collection<String> keywords) {
		HashSet<Integer> taskIndexes = new HashSet<Integer>();
		ArrayList<Integer> taskIds = new ArrayList<Integer>();
		for (String keyword : keywords) {
			Integer hashValue = (Math.abs(keyword.hashCode())) % numberOfEvaluatorTasks;
			taskIndexes.add(hashValue);
		}
		for (Integer taskIndex : taskIndexes) {
			Integer taskId = evaluatorBoltTasks.get(taskIndex);
			taskIds.add(taskId);
		}
		return taskIds;
	}
}
