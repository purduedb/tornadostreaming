package edu.purdue.cs.tornado.loadbalance;

import java.util.ArrayList;
import java.util.List;

public class LoadBalanceMessage {
	//****************************************************************************
	//***********************Load Balance constansts
	public static final String PARTITION = "Partition";
	public static final String MERGE = "Merge";
	public static final String SPLIT = "Split";
	public static final String ENTER_BARRIER = "EnterBarrier";
	public static final String GOIING_TO_BARRIER = "GoingToBarrier";
	public static final String SEND_TO = "Send_To";
	public static final String NEW_CELL = "newCell";
	public static final String AUX_CELL = "auxCell";
	public static final String NEW_EXEC_TASKS = "newExecutorsTasks";
	public static final String NEW_AUX_EXEC_TASKS = "newAuxiliaryExecutorTask";
	public static final String NEW_PARTITIONS = "newPartitions";
	public static final String FINISHED = "Finished";
	public static final String ReplyWhenReceived = "ReplyWhenReceived";
	public static final String Reply="Reply";
	public static final String MERGE_DATA = "MergeData";
	public static final String SPLIT_DATA = "SplitData";

	

	
	Partition parition;
	Integer sendTo;
	Partition newCell, auxCell;
	List<Integer> newExecuterTasks;
	Integer newAuxiliaryExecutorTask;
	ArrayList<Cell> newPartitions;
	String loadBalanceMessageType;
	String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLoadBalanceMessageType() {
		return loadBalanceMessageType;
	}

	public void setLoadBalanceMessageType(String loadBalanceMessageType) {
		this.loadBalanceMessageType = loadBalanceMessageType;
	}

	public LoadBalanceMessage() {
		parition = null;
		sendTo = null;
		newCell = null;
		auxCell = null;
		newExecuterTasks = null;
		newAuxiliaryExecutorTask = null;
		newPartitions = null;
	}

	public Partition getParition() {
		return parition;
	}

	public void setParition(Partition parition) {
		this.parition = parition;
	}

	public Integer getSendTo() {
		return sendTo;
	}

	public void setSendTo(Integer sendTo) {
		this.sendTo = sendTo;
	}

	public Partition getNewCell() {
		return newCell;
	}

	public void setNewCell(Partition newCell) {
		this.newCell = newCell;
	}

	public Partition getAuxCell() {
		return auxCell;
	}

	public void setAuxCell(Partition auxCell) {
		this.auxCell = auxCell;
	}

	public List<Integer> getNewExecuterTasks() {
		return newExecuterTasks;
	}

	public void setNewExecuterTasks(List<Integer> newExecuterTasks) {
		this.newExecuterTasks = newExecuterTasks;
	}

	public Integer getNewAuxiliaryExecutorTask() {
		return newAuxiliaryExecutorTask;
	}

	public void setNewAuxiliaryExecutorTask(Integer newAuxiliaryExecutorTask) {
		this.newAuxiliaryExecutorTask = newAuxiliaryExecutorTask;
	}

	public ArrayList<Cell> getNewPartitions() {
		return newPartitions;
	}

	public void setNewPartitions(ArrayList<Cell> newPartitions) {
		this.newPartitions = newPartitions;
	}

}
