package edu.purdue.cs.tornado.index.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.loadbalance.Cell;

public class GlobalOptimizedPartitionedTextAwareIndex extends GlobalOptimizedPartitionedIndexLowerSpace {

	public HashMap<Integer, HashMap<String, Long>> taskIdTextualSummery;//this give information about query keywords in every task
	public HashMap<Integer, Boolean> noVerifyOnTextOverlap; // true this means i need to verify the text false means i do not verify and send directly
/**
 * Constructor initializing local variables and setting number Of Evaluator Tasks, List of evaluatorBoltTasks and Grid Granularity
 */
	public GlobalOptimizedPartitionedTextAwareIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks, fineGridGran);
		taskIdTextualSummery = new HashMap<Integer, HashMap<String, Long>>();
		noVerifyOnTextOverlap = new HashMap<Integer, Boolean>();
		for (Integer taskId : evaluatorBoltTasks) {
			taskIdTextualSummery.put(taskId, null);
			noVerifyOnTextOverlap.put(taskId, false);
		}
	}
/**
 * Constructor initializing local variables and setting number Of Evaluator Tasks, List of evaluatorBoltTasks and Grid Granularity and a List of partitions
 */
	public GlobalOptimizedPartitionedTextAwareIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, ArrayList<Cell> partitions, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions, fineGridGran);
		taskIdTextualSummery = new HashMap<Integer, HashMap<String, Long>>();
		noVerifyOnTextOverlap = new HashMap<Integer, Boolean>();
		for (Integer taskId : evaluatorBoltTasks) {
			taskIdTextualSummery.put(taskId, null);
			noVerifyOnTextOverlap.put(taskId, false);
		}

	}
/**
 * Given an Integer List of Taskids and a List of Texts, return a set of all keywords in the text. 
 */
	@Override
	public HashSet<String> addTextToTaskID(ArrayList<Integer> tasks, ArrayList<String> text, boolean all, boolean forward) {
		HashSet<String> toForward = null;
		Long time = (new java.util.Date()).getTime();
		for (Integer task : tasks) {

			HashMap<String, Long> textSummery = taskIdTextualSummery.get(task);
			if (textSummery == null) {
				textSummery = new HashMap<String, Long>();
				taskIdTextualSummery.put(task, textSummery);
			}
			if (all) {
				if (forward) {
					if(toForward==null)toForward = new HashSet<String>();
					for (String keyword : text) {
						if (!textSummery.containsKey(keyword)) {
							textSummery.put(keyword, time);
							toForward.add(keyword);
						}

					}
				} else {
					for (String keyword : text) {
						textSummery.put(keyword, time);
					}
				}
			} else {//one keyword suffices 
				Boolean found = false;
				for (String keyword : text) {
					if (textSummery.containsKey(keyword)) {
						found = true;
						break;
					}
				}
				if (!found && text.size() > 0) {
					if (forward) {
						if(toForward==null)toForward = new HashSet<String>();
						toForward.add(text.get(0));
					}
					textSummery.put(text.get(0), time);
				}
			}
		}
		return toForward;
	}
/**
 * Given a list of tasks and a Set of texts, removes from text summary, if it does not match the text requirements
 */
	public void dropTextFromTaskID(ArrayList<Integer> tasks, Set<String> text, Long time) {
		for (Integer task : tasks) {
			HashMap<String, Long> textSummery = taskIdTextualSummery.get(task);
			if (taskIdTextualSummery != null && textSummery != null) {
				Iterator<Entry<String, Long>> itr = (Iterator<Entry<String, Long>>) textSummery.entrySet().iterator();
				while (itr.hasNext()) {
					Entry<String, Long> e = itr.next();
					if (!text.contains(e.getKey()) && time > e.getValue())
						itr.remove();
				}
			}
		}
	}
/**
 * Verifies if the text of the given taskId and given text List overlap
 */
	public Boolean verifyTextOverlap(Integer task, ArrayList<String> text) {
		if (noVerifyOnTextOverlap.get(task))
			return true;// false means i need to check text 
		else if (taskIdTextualSummery.get(task) == null)
			return false; //means no queries with text on this bolt do not send 
		else
			return TextHelpers.overlapsTextuallyWithtime(taskIdTextualSummery.get(task), text);
	}
/**
 * Given a taskIndex to copy from and a taskIndex to copy to, copies the textSummary of from fromtaskId to totaskId
 */
	public void copyTextSummery(Integer taskIndexFrom, Integer taskIndexTo) {
		Integer taskIdFrom = evaluatorBoltTasks.get(taskIndexFrom);
		Integer taskIdTo = evaluatorBoltTasks.get(taskIndexTo);
		HashMap<String, Long> textSummeryFrom = taskIdTextualSummery.get(taskIdFrom);
		HashMap<String, Long> textSummeryTo = taskIdTextualSummery.get(taskIdTo);
		if (textSummeryFrom != null) {
			if (textSummeryTo == null) {
				textSummeryTo = new HashMap<String, Long>();
				taskIdTextualSummery.put(taskIdTo, textSummeryTo);
			}
			Iterator<Entry<String, Long>> itr = textSummeryFrom.entrySet().iterator();
			while (itr.hasNext()) {
				Entry<String, Long> entry = itr.next();
				String keyword = entry.getKey();
				Long time = entry.getValue();
				textSummeryTo.put(keyword, time);
			}
		}
	}
/**
 * clears the textSummery entry of the given taskIndex
 */
	public void clearTextSummery(Integer taskIndex) {
		Integer taskId = evaluatorBoltTasks.get(taskIndex);
		taskIdTextualSummery.remove(taskId);

	}

	@Override
	public Boolean isTextAware() {
		return true;
	}
}
