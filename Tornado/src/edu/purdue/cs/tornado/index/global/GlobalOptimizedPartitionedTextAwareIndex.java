package edu.purdue.cs.tornado.index.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import edu.purdue.cs.tornado.helper.TextHelpers;
import edu.purdue.cs.tornado.loadbalance.Cell;

public class GlobalOptimizedPartitionedTextAwareIndex extends GlobalOptimizedPartitionedIndex {

	public HashMap<Integer, HashMap<String, Long>> taskIdTextualSummery;//this give information about query keywords in every task
	public HashMap<Integer, Boolean> noVerifyOnTextOverlap; // true this means i need to verify the text false means i do not verify and send directly

	public GlobalOptimizedPartitionedTextAwareIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks, fineGridGran);
		taskIdTextualSummery = new HashMap<Integer, HashMap<String, Long>>();
		noVerifyOnTextOverlap = new HashMap<Integer, Boolean>();
		for (Integer taskId : evaluatorBoltTasks) {
			taskIdTextualSummery.put(taskId, null);
			noVerifyOnTextOverlap.put(taskId, false);
		}
	}

	public GlobalOptimizedPartitionedTextAwareIndex(Integer numberOfEvaluatorTasks, List<Integer> evaluatorBoltTasks, ArrayList<Cell> partitions, Integer fineGridGran) {
		super(numberOfEvaluatorTasks, evaluatorBoltTasks, partitions, fineGridGran);
		taskIdTextualSummery = new HashMap<Integer, HashMap<String, Long>>();
		noVerifyOnTextOverlap = new HashMap<Integer, Boolean>();
		for (Integer taskId : evaluatorBoltTasks) {
			taskIdTextualSummery.put(taskId, null);
			noVerifyOnTextOverlap.put(taskId, false);
		}

	}

	@Override
	public ArrayList<String> addTextToTaskID(ArrayList<Integer> tasks, ArrayList<String> text, boolean all, boolean forward) {
		ArrayList<String> toForward = null;
		Long time = (new java.util.Date()).getTime();
		for (Integer task : tasks) {

			HashMap<String, Long> textSummery = taskIdTextualSummery.get(task);
			if (textSummery == null) {
				textSummery = new HashMap<String, Long>();
				taskIdTextualSummery.put(task, textSummery);
			}
			if (all) {
				//if (forward)
				for (String keyword : text) {
					textSummery.put(keyword, time);
				}
			} else {//one keyword suffies 
				Boolean found = false;
				for (String keyword : text) {
					if (textSummery.containsKey(keyword)) {
						found = true;
						break;
					}
				}
				if (!found && text.size() > 0) {
					textSummery.put(text.get(0), time);
				}
			}
		}
		return toForward;
	}

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

	public Boolean verifyTextOverlap(Integer task, ArrayList<String> text) {
		if (noVerifyOnTextOverlap.get(task))
			return true;// false means i need to check text 
		else if (taskIdTextualSummery.get(task) == null)
			return false; //means no queries with text on this bolt do not send 
		else
			return TextHelpers.overlapsTextuallyWithtime(taskIdTextualSummery.get(task), text);
	}

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

	public void clearTextSummery(Integer taskIndex) {
		Integer taskId = evaluatorBoltTasks.get(taskIndex);
		taskIdTextualSummery.remove(taskId);

	}

	@Override
	public Boolean isTextAware() {
		return true;
	}
}
