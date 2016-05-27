package edu.purdue.cs.tornado.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.storm.generated.GlobalStreamId;

public class BoltInfoAggrecate {
	String id;
	HashMap<String, Long> transferred;
	HashMap<String, Long> emitted;

	HashMap<GlobalStreamId, Long> failed;

	HashMap<GlobalStreamId, Long> acked;
	HashMap<GlobalStreamId, Long> executed;
	HashMap<GlobalStreamId, ArrayList<Long>> executedAll;
	HashMap<GlobalStreamId, Long> maxExecuted;
	HashMap<GlobalStreamId, Long> minExecuted;
	HashMap<GlobalStreamId, Double> executeLatency;
	HashMap<GlobalStreamId, ArrayList<Double>> executeLatencyAll;
	HashMap<GlobalStreamId, Double> minExecuteLatency;
	HashMap<GlobalStreamId, Double> maxExecuteLatency;

	int numOfExecuters;
	Integer minUpTime;
	Integer maxUpTime;
	String toplogyId;

	public BoltInfoAggrecate(String id, String toplogyId) {
		this.id = id;
		this.toplogyId = toplogyId;
		transferred = new HashMap<String, Long>();
		emitted = new HashMap<String, Long>();
		failed = new HashMap<GlobalStreamId, Long>();
		executed = new HashMap<GlobalStreamId, Long>();
		executedAll = new HashMap<GlobalStreamId, ArrayList<Long>>();
		executeLatency = new HashMap<GlobalStreamId, Double>();
		executeLatencyAll = new HashMap<GlobalStreamId, ArrayList<Double>>();
		numOfExecuters = 0;
		minExecuted = new HashMap<GlobalStreamId, Long>();
		maxExecuted = new HashMap<GlobalStreamId, Long>();
		minExecuteLatency = new HashMap<GlobalStreamId, Double>();
		maxExecuteLatency = new HashMap<GlobalStreamId, Double>();
		acked = new HashMap<GlobalStreamId, Long>();
		minUpTime = Integer.MAX_VALUE;
		maxUpTime = 0;
	}

	public void addBoltStats(Map<String, Long> transferred, Map<String, Long> emitted, Map<GlobalStreamId, Long> acked, Map<GlobalStreamId, Long> failed, Map<GlobalStreamId, Long> executed, Map<GlobalStreamId, Double> executeLatency,
			int upTime) {
		this.numOfExecuters++;

		incrementMapsStringLong(this.transferred, transferred);
		incrementMapsStringLong(this.emitted, emitted);
		incrementMapsStreamIdLong(this.failed, failed);
		incrementMapsStreamIdLong(this.executed, executed);
		AddaValueMapsStreamIdLong(this.executedAll, executed);
		incrementMapsStreamIdDouble(this.executeLatency, executeLatency);
		AddaValueMapsStreamIdDouble(this.executeLatencyAll, executeLatency);
		if (upTime < minUpTime)
			minUpTime = upTime;
		if (upTime > maxUpTime)
			maxUpTime = upTime;

	}

	void incrementMapsStringLong(HashMap<String, Long> current, Map<String, Long> other) {
		if (other == null)
			return;
		Iterator<Entry<String, Long>> itr = other.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, Long> entry = itr.next();
			String key = entry.getKey();
			if (!current.containsKey(key))
				current.put(key, entry.getValue());
			else {
				current.put(key, current.get(key) + entry.getValue());
			}
		}
	}

	void incrementMapsStreamIdLong(HashMap<GlobalStreamId, Long> current, Map<GlobalStreamId, Long> other) {
		if (other == null)
			return;
		Iterator<Entry<GlobalStreamId, Long>> itr = other.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<GlobalStreamId, Long> entry = itr.next();
			GlobalStreamId key = entry.getKey();
			if (!current.containsKey(key))
				current.put(key, entry.getValue());
			else {
				current.put(key, current.get(key) + entry.getValue());
			}
		}
	}

	void AddaValueMapsStreamIdLong(HashMap<GlobalStreamId, ArrayList<Long>> current, Map<GlobalStreamId, Long> other) {
		if (other == null)
			return;
		Iterator<Entry<GlobalStreamId, Long>> itr = other.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<GlobalStreamId, Long> entry = itr.next();
			GlobalStreamId key = entry.getKey();
			if (!current.containsKey(key)) {
				ArrayList<Long> values = new ArrayList<Long>();
				values.add(entry.getValue());
				current.put(key, values);
			} else {
				current.get(key).add(entry.getValue());
			}
		}
	}

	void AddaValueMapsStreamIdDouble(HashMap<GlobalStreamId, ArrayList<Double>> current, Map<GlobalStreamId, Double> other) {
		if (other == null)
			return;
		Iterator<Entry<GlobalStreamId, Double>> itr = other.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<GlobalStreamId, Double> entry = itr.next();
			GlobalStreamId key = entry.getKey();
			if (!current.containsKey(key)) {
				ArrayList<Double> values = new ArrayList<Double>();
				values.add(entry.getValue());
				current.put(key, values);
			} else {
				current.get(key).add(entry.getValue());
			}
		}
	}

	void incrementMapsStreamIdDouble(HashMap<GlobalStreamId, Double> current, Map<GlobalStreamId, Double> other) {
		if (other == null)
			return;
		Iterator<Entry<GlobalStreamId, Double>> itr = other.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<GlobalStreamId, Double> entry = itr.next();
			GlobalStreamId key = entry.getKey();
			if (!current.containsKey(key))
				current.put(key, entry.getValue());
			else {
				current.put(key, current.get(key) + entry.getValue());
			}
		}
	}

	@Override
	public String toString() {
		if (id.toLowerCase().contains(toplogyId.toLowerCase())) {
			String Id = "Bolt id," + id;
			String emittedString = "Emitted," + getKeyValueString(emitted);
			String executedString = "Eexecuted," + getKeyValueGlobalStreamIdLong(executed)+",Executedstd"+getKeyValueSTDGlobalStreamIdLong(executedAll,1);
			String executeLatencyString = "Eexecute_Latency," + getKeyValueGlobalStreamIdDouble(executeLatency, numOfExecuters);
			String toReturn = Id + "," + emittedString + "," + executedString + "," + executeLatencyString + ",minup time," + minUpTime + ",maxup time," + maxUpTime + ",";
			return toReturn;
		} else
			return "";
	}

	public String toString(Integer divValue) {
		if (id.toLowerCase().contains  (toplogyId.toLowerCase())) {
			String Id = "Bolt id," + id;
			String emittedString = "Emitted," + getKeyValueString(emitted, divValue);
			String executedString = "Eexecuted," + getKeyValueGlobalStreamIdLong(executed, divValue) +",Executedstd"+getKeyValueSTDGlobalStreamIdLong(executedAll,divValue);
			String executeLatencyString = "Eexecute_Latency," + getKeyValueGlobalStreamIdDouble(executeLatency, numOfExecuters, 1);
			String toReturn = Id + "," + emittedString + "," + executedString + "," + executeLatencyString + ",minup time," + minUpTime + ",maxup time," + maxUpTime + ",";
			return toReturn;
		} else
			return "";
	}

	double getMean(ArrayList<Long> data) {
		double sum = 0.0;
		for (double a : data)
			sum += a;
		return sum / data.size();
	}

	double getVariance(ArrayList<Long> data) {
		double mean = getMean(data);
		double temp = 0;
		for (double a : data)
			temp += (mean - a) * (mean - a);
		return temp / data.size();
	}

	double getStdDev(ArrayList<Long> data) {
		return Math.sqrt(getVariance(data));
	}

	String getKeyValueString(HashMap<String, Long> current) {
		if (current == null)
			return "";
		String val = "";
		Iterator<Entry<String, Long>> itr = current.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, Long> entry = itr.next();
			if (!entry.getKey().contains("metrics") && !entry.getKey().contains("ack") && !entry.getKey().contains("system")) {
				val += entry.getKey() + "," + entry.getValue() + ",";
			}
		}
		return val;
	}

	String getKeyValueString(HashMap<String, Long> current, Integer divValue) {
		if (current == null)
			return "";
		String val = "";
		Iterator<Entry<String, Long>> itr = current.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, Long> entry = itr.next();
			if (!entry.getKey().contains("metrics") && !entry.getKey().contains("ack") && !entry.getKey().contains("system")) {
				val += entry.getKey() + "," + (entry.getValue() / divValue) + ",";
			}
		}
		return val;
	}

	String getKeyValueGlobalStreamIdLong(HashMap<GlobalStreamId, Long> current) {
		if (current == null)
			return "";
		String val = "";
		Iterator<Entry<GlobalStreamId, Long>> itr = current.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<GlobalStreamId, Long> entry = itr.next();
			val += entry.getKey().toString().replace("GlobalStreamId(componentId:", "").replace(")", "").replace(", streamId:", "_") + "," + entry.getValue() + ",";
		}
		return val;
	}

	String getKeyValueGlobalStreamIdLong(HashMap<GlobalStreamId, Long> current, Integer divValue) {
		if (current == null)
			return "";
		String val = "";
		Iterator<Entry<GlobalStreamId, Long>> itr = current.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<GlobalStreamId, Long> entry = itr.next();
			val += entry.getKey().toString().replace("GlobalStreamId(componentId:", "").replace(")", "").replace(", streamId:", "_") + "," + (entry.getValue() / divValue) + ",";
		}
		return val;
	}
	String getKeyValueSTDGlobalStreamIdLong(HashMap<GlobalStreamId, ArrayList<Long>> current, Integer divValue) {
		if (current == null)
			return "";
		String val = "";
		Iterator<Entry<GlobalStreamId,  ArrayList<Long>>> itr = current.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<GlobalStreamId, ArrayList<Long>> entry = itr.next();
			val += entry.getKey().toString().replace("GlobalStreamId(componentId:", "").replace(")", "").replace(", streamId:", "_") + "," + (getStdDev(entry.getValue() )/ divValue) + ",";
		}
		return val;
	}

	String getKeyValueGlobalStreamIdDouble(HashMap<GlobalStreamId, Double> current, Integer numberOdExecuters) {
		if (current == null)
			return "";
		String val = "";
		Iterator<Entry<GlobalStreamId, Double>> itr = current.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<GlobalStreamId, Double> entry = itr.next();
			val += entry.getKey().toString().replace("GlobalStreamId(componentId:", "").replace(")", "").replace(", streamId:", "_") + "," + entry.getValue() / numberOdExecuters + ",";
		}
		return val;
	}

	String getKeyValueGlobalStreamIdDouble(HashMap<GlobalStreamId, Double> current, Integer numberOdExecuters, Integer divValue) {
		if (current == null)
			return "";
		String val = "";
		Iterator<Entry<GlobalStreamId, Double>> itr = current.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<GlobalStreamId, Double> entry = itr.next();
			val += entry.getKey().toString().replace("GlobalStreamId(componentId:", "").replace(")", "").replace(", streamId:", "_") + "," + (entry.getValue() / numberOdExecuters / divValue) + ",";
		}
		return val;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNumOfExecuters() {
		return numOfExecuters;
	}

	public void setNumOfExecuters(int numOfExecuters) {
		this.numOfExecuters = numOfExecuters;
	}

}
